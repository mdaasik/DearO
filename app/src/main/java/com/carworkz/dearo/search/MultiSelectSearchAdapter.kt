package com.carworkz.dearo.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R
import com.carworkz.dearo.domain.entities.Labour
import com.carworkz.dearo.domain.entities.Part
import com.carworkz.dearo.domain.entities.RecommendedJob

class MultiSelectSearchAdapter<T> constructor(val context: Context, val vehicleType: String?, val items: MutableList<T>, val itemAddedListener: OnItemAddedListener) : RecyclerView.Adapter<MultiSelectSearchAdapter<T>.ViewHolder>() {

    val selectedItems = arrayListOf<T>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_multi_select_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        var isCustomItem = false
        when (item) {
            is Part -> {
                isCustomItem = item.id == null
                if (position == 0 &&
                        (context as SearchActivity).getQuery().isNotEmpty() &&
                        isCustomItem &&
                        selectedItems.find { it is Part && it.text == item.text } == null
                ) {
                    holder.itemTitleView.text = String.format(context.getString(R.string.search_screen_add_as_new_item, item.text))
                } else {
                    holder.itemTitleView.text = item.text
                }
            }
            is Labour -> {
                isCustomItem = item.id == null
                if (position == 0 &&
                        (context as SearchActivity).getQuery().isNotEmpty() &&
                        isCustomItem &&
                        selectedItems.find { it is Labour && it.text == item.text } == null
                ) {
                    holder.itemTitleView.text = String.format(context.getString(R.string.search_screen_add_as_new_item, item.text))
                } else {
                    holder.itemTitleView.text = item.text
                }
            }
            is RecommendedJob -> {
                isCustomItem = item.id == null
                if (position == 0 &&
                        (context as SearchActivity).getQuery().isNotEmpty() &&
                        isCustomItem &&
                        selectedItems.find { it is RecommendedJob && it.text == item.text } == null
                ) {
                    holder.itemTitleView.text = String.format(context.getString(R.string.search_screen_add_as_new_item, item.text))
                } else {
                    holder.itemTitleView.text = item.text
                }
            }
        }

        if (position == 0 &&
                (context as SearchActivity).getQuery().isNotEmpty() &&
                isCustomItem &&
                selectedItems.find { it == item } == null
        ) {
            holder.itemImageView.visibility = View.VISIBLE
            holder.itemCheckBox.visibility = View.INVISIBLE
            holder.itemImageView.setOnClickListener {
                if (selectedItems.find { it == item } == null) {
                    selectedItems.add(item)
                    notifyItemChanged(0)
                    itemAddedListener.onItemAdded(selectedItems.size)
                }
            }

            holder.itemParentView.setOnClickListener {
                if (selectedItems.find { it == item } == null) {
                    selectedItems.add(item)
                    notifyItemChanged(0)
                    itemAddedListener.onItemAdded(selectedItems.size)
                }
            }
        } else {
            holder.itemCheckBox.visibility = View.VISIBLE
            holder.itemImageView.visibility = View.INVISIBLE
            holder.itemImageView.setOnClickListener(null)
        }

        holder.itemCheckBox.isChecked = selectedItems.find { it == item } != null
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemImageView: ImageView = view.findViewById(R.id.itemIconView)
        val itemTitleView: TextView = view.findViewById(R.id.itemTitleView)
        val itemCheckBox: CheckBox = view.findViewById(R.id.itemSelectCheckBox)
        val itemParentView: View = view.findViewById(R.id.itemSelectParentView)

        init {
            itemCheckBox.setOnClickListener {
                if (adapterPosition == RecyclerView.NO_POSITION)
                    return@setOnClickListener

                if (itemCheckBox.isChecked) {
                    selectedItems.add(items[adapterPosition])
                } else {
                    selectedItems.remove(items[adapterPosition])
                }
                itemAddedListener.onItemAdded(selectedItems.size)
            }
            itemParentView.setOnClickListener {
                if (adapterPosition == RecyclerView.NO_POSITION)
                    return@setOnClickListener

                if (!itemCheckBox.isChecked) {
                    selectedItems.add(items[adapterPosition])
                    itemCheckBox.isChecked = true
                } else {
                    selectedItems.remove(items[adapterPosition])
                    itemCheckBox.isChecked = false
                }
                itemAddedListener.onItemAdded(selectedItems.size)
            }
        }
    }

    interface OnItemAddedListener {
        fun onItemAdded(itemSize: Int)
    }
}