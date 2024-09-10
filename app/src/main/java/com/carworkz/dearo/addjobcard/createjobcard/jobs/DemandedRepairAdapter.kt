package com.carworkz.dearo.addjobcard.createjobcard.jobs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R
import com.carworkz.dearo.domain.entities.Labour
import com.carworkz.dearo.domain.entities.Part

/**
 * Created by Farhan on 24/8/17.
 */
open class DemandedRepairAdapter(private var context: Context, private var list: List<Any>) : RecyclerView.Adapter<DemandedRepairAdapter.ViewHolder>() {

    var selectedPartsList = mutableListOf<Part>()
    var selectedLabourList = mutableListOf<Labour>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_demanded_repairs, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val obj = list[position]
        if (obj is Part) {
            holder.title.text = obj.text
            holder.checkbox.tag = obj
            holder.checkbox.isChecked = selectedPartsList.find { it.id == obj.id } != null
        }

        if (obj is Labour) {
            holder.title.text = obj.text
//            holder.checkbox.tag = obj
            holder.checkbox.isChecked = selectedLabourList.find { it.id == obj.id } != null
            holder.checkbox.isEnabled = false
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var checkbox = itemView.findViewById(R.id.cb_row_demanded_repairs) as CheckBox
        var title = itemView.findViewById(R.id.tv_demanded_repair) as TextView

        init {
            checkbox.setOnClickListener { v ->
                val obj = v.tag
                if (obj is Part) {
                    if (checkbox.isChecked) {
                        selectedPartsList.add(obj)
                    } else {
                        selectedPartsList.remove(obj)
                    }
                }
                //                if (obj is Labour) {
                //                    if (checkbox.isChecked) {
                //                        selectedLabourList.add(obj)
                //                    } else {
                //                        selectedLabourList.remove(obj)
                //                    }
                //                }
            }
        }
    }
}