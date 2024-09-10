package com.carworkz.dearo.searchabledialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R

internal class SearchableAdapter<T : Searchable>(private val context: Context, var searchableList: List<T>, var selectedItem: T?) : RecyclerView.Adapter<SearchableAdapter<T>.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_searchable_dialog, parent, false))
    }

    override fun getItemCount(): Int = searchableList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = searchableList[position]
        holder.itemNameView.text = item.getText()
        holder.itemRadioBtn.isChecked = item == selectedItem
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val itemParentView = view.findViewById<LinearLayout>(R.id.searchableItemParentView)!!
        val itemNameView = view.findViewById<TextView>(R.id.itemNameView)!!
        val itemRadioBtn = view.findViewById<RadioButton>(R.id.itemRadioBtn)!!

        init {
            itemParentView.setOnClickListener {
                updateSelectedItem(adapterPosition)
            }
            itemRadioBtn.setOnClickListener {
                updateSelectedItem(adapterPosition)
            }
        }

        private fun updateSelectedItem(position: Int) {
            if (position != RecyclerView.NO_POSITION) {
                val previouslySelectedItemPos = searchableList.indexOf(selectedItem)
                val item = searchableList[position]
                selectedItem = item
                notifyItemChanged(previouslySelectedItemPos)
                notifyItemChanged(adapterPosition)
            }
        }
    }
}