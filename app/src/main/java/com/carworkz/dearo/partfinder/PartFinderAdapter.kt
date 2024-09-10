package com.carworkz.dearo.partfinder

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.PriceChangeListener
import com.carworkz.dearo.R
import com.carworkz.dearo.base.DelegateSwipeAdapter
import com.carworkz.dearo.domain.entities.PartNumber
import com.carworkz.dearo.utils.Utility

class PartFinderAdapter(private var context: Context, var parts: MutableList<PartNumber>, private val priceChangeListener: PriceChangeListener?) : DelegateSwipeAdapter<PartNumber>(parts) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartViewHolder {
        return PartViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_swipe_to_delete, parent, false))
    }

    override fun getItemCount(): Int = parts.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = parts[position]
        when (holder) {
            is PartViewHolder -> {
                if (isPendingRemoval(item)) {
                    holder.deleteParentView.visibility = View.VISIBLE
                    holder.partParentView.visibility = View.GONE
                    holder.undoView.setOnClickListener {
                        undoDelete(item)
                    }
                } else {
                    holder.deleteParentView.visibility = View.GONE
                    holder.partParentView.visibility = View.VISIBLE
                    holder.partNameView.text = item.partName
                    holder.partDescriptionView.text = item.description
                    holder.partNumberView.text = item.brandName + " - " + item.partNumber
                    holder.partRateView.text = if (item.sellingPrice > 0.0f) Utility.convertToCurrency(item.sellingPrice.toDouble()) else "\u20B9 -"
                    if (item.remark != null) {
                        holder.remarkView.text = item.remark.toString()
                        holder.remarkView.visibility = View.VISIBLE
                    } else {
                        holder.remarkView.visibility = View.GONE
                    }
                }
            }
            is CustomPartViewHolder -> {
            }
        }
    }

    override fun onItemDeletedUndo(position: Int) {
        /*empty value to indicate calculate everything again*/
        priceChangeListener?.onPriceUpdate("", -1)
    }

    override fun onItemDeleted(position: Int, item: PartNumber)
    {
        /*empty value to indicate calculate everything again*/
        priceChangeListener?.onPriceUpdate("", -1)
    }


    inner class PartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val partParentView = view.findViewById(R.id.ll_part_finder_parent) as LinearLayout
        val partNameView = view.findViewById(R.id.tv_part_title) as TextView
        val partDescriptionView = view.findViewById(R.id.tv_part_description) as TextView
        val partNumberView = view.findViewById(R.id.tv_part_finder_part_number) as TextView
        val partInStockView = view.findViewById(R.id.tv_part_finder_instock) as TextView
        val partRateView = view.findViewById(R.id.tv_part_finder_part_rate) as TextView
        val deleteParentView: View = view.findViewById(R.id.cl_parent_delete)
        val undoView: TextView = view.findViewById(R.id.tv_delete_undo) as TextView
        val remarkView: TextView = view.findViewById(R.id.remarkView) as TextView
    }

    class CustomPartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val partNameView = view.findViewById(R.id.tv_part_title) as TextView
    }
}