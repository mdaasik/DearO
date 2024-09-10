package com.carworkz.dearo.addjobcard.createjobcard.inspection

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R
import com.carworkz.dearo.domain.entities.InspectionItem

/**
 * Created by Farhan on 11/9/17.
 */
class InspectionViewOnlyAdapter(private val context: Context?, private val list: List<InspectionItem>?) : RecyclerView.Adapter<InspectionViewOnlyAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_inspection_subitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val obj = list?.get(position)
        holder.subItem.text = obj?.text
//        info { "key is ${obj?.text} condition is ${obj?.condition}" }
        when (obj?.condition) {
            InspectionAdapter.POOR -> {
//                holder?.poorBox?.isEnabled = false
                holder.poorBox.isChecked = true
                holder.avgBox.isChecked = false
                holder.goodBox.isChecked = false
            }

            InspectionAdapter.AVERAGE -> {
//                holder.avgBox.isEnabled = false
                holder.poorBox.isChecked = false
                holder.avgBox.isChecked = true
                holder.goodBox.isChecked = false
            }

            InspectionAdapter.GOOD -> {
//                holder.goodBox.isEnabled = false
                holder.poorBox.isChecked = false
                holder.avgBox.isChecked = false
                holder.goodBox.isChecked = true
            }
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var subItem: TextView = itemView.findViewById(R.id.tv_title_inspection_subitem) as TextView
        val poorBox: CheckBox = itemView.findViewById(R.id.cb_poor) as CheckBox
        val avgBox: CheckBox = itemView.findViewById(R.id.cb_average) as CheckBox
        val goodBox: CheckBox = itemView.findViewById(R.id.cb_good) as CheckBox

        init {
            poorBox.isEnabled = false
            avgBox.isEnabled = false
            goodBox.isEnabled = false
        }
    }
}