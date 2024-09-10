package com.carworkz.dearo.addjobcard.quickjobcard.quickjobs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R
import com.carworkz.dearo.domain.entities.Labour
import com.carworkz.dearo.domain.entities.Part
import timber.log.Timber

class QuickRegServiceAdapter(var context: Context, var list: List<Any>) : RecyclerView.Adapter<QuickRegServiceAdapter.ViewHolder>() {

    var selectedPartsList = mutableListOf<Part>()
    var selectedLabourList = mutableListOf<Labour>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_short_recommended_job, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val obj = list[position]
        Timber.d("position")
        if (obj is Part) {
            holder.checkbox.text = obj.text
            holder.checkbox.tag = obj
            holder.checkbox.isChecked = selectedPartsList.find { it.id == obj.id } != null
        }

        if (obj is Labour) {
            holder.checkbox.text = obj.text
//            holder.checkbox.tag = obj
            holder.checkbox.isChecked = selectedLabourList.find { it.id == obj.id } != null
            holder.checkbox.isEnabled = false
        }
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var checkbox = itemView.findViewById(R.id.checkBox) as CheckBox

        init {
            checkbox.setOnClickListener { v ->
                val obj = v.tag
                if (obj is Part) {
                    if ((v as CheckBox).isChecked) {
                        selectedPartsList.add(obj)
                    } else {
                        selectedPartsList.remove(obj)
                    }
                }
            }
        }
    }
}