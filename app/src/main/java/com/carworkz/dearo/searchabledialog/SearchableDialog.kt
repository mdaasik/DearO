package com.carworkz.dearo.searchabledialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.SearchView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.carworkz.dearo.databinding.LayoutSearchableDialogBinding
import timber.log.Timber

class SearchableDialog<T : Searchable> private constructor(builder: Builder<T>) {

    /* Views */
    private val binding: LayoutSearchableDialogBinding
    private lateinit var dialogAdapter: SearchableAdapter<T>

    /* Properties */
    private val context: Context
    private val originalSearchList: List<T>
    private var selectedItem: T? = null
    private var title: String? = null
    private var itemSelectedListener: OnSearchItemSelectedListener<T>? = null

    init {
        context = builder.context
        originalSearchList = builder.originalSearchList
        selectedItem = builder.getSelectedItem()
        title = builder.getTitle()
        itemSelectedListener = builder.getItemSelectedListener()
        binding = LayoutSearchableDialogBinding.inflate(LayoutInflater.from(context))
        initViews()
    }

    fun show() {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setView(binding.root)
            setPositiveButton("SELECT") { _, _ ->
                Timber.d("is listener null $itemSelectedListener")
                itemSelectedListener?.onItemSelectedItem(dialogAdapter.selectedItem)
            }
            setNegativeButton("CANCEL", null)
            show()
        }
    }

    private fun initViews() {
        binding.searchListView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        dialogAdapter = SearchableAdapter(context, originalSearchList.toMutableList(), selectedItem)
        binding.searchListView.adapter = dialogAdapter

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    val filteredList = getFilteredList(it)
                    if (filteredList.isNullOrEmpty()) {
                        binding.noResultView.visibility = View.VISIBLE
                        binding.searchListView.visibility = View.GONE
                    } else {
                        binding.noResultView.visibility = View.GONE
                        binding.searchListView.visibility = View.VISIBLE
                    }
                    dialogAdapter.searchableList = filteredList
                    dialogAdapter.notifyDataSetChanged()
                }
                return true
            }
        })
    }

    private fun getFilteredList(query: String): List<T> {
        return originalSearchList.filter { it.getText().contains(query, true) }
    }

    class Builder<T : Searchable>(val context: Context, val originalSearchList: List<T>) {

        private var selectedItem: T? = null
        private var title: String? = null
        private var itemSelectedListener: OnSearchItemSelectedListener<T>? = null

        fun setSelectedItem(item: T?): Builder<T> {
            this.selectedItem = item
            return this
        }

        fun setTitle(title: String): Builder<T> {
            this.title = title
            return this
        }

        fun setTitle(@StringRes title: Int): Builder<T> {
            setTitle(context.getString(title))
            return this
        }

        fun setItemSelectedListener(listener: OnSearchItemSelectedListener<T>?): Builder<T> {
            itemSelectedListener = listener
            return this
        }

        fun getSelectedItem() = selectedItem

        fun getTitle() = title

        fun getItemSelectedListener() = itemSelectedListener

        fun create(): SearchableDialog<T> {
            return SearchableDialog(this)
        }

        fun show() {
            create().show()
        }
    }

    interface OnSearchItemSelectedListener<E> {
        fun onItemSelectedItem(item: E?)
        fun onCancel()
    }
}
