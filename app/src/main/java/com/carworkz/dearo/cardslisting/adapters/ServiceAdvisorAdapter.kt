package com.carworkz.dearo.cardslisting.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R
import com.carworkz.dearo.domain.entities.WorkshopAdviser
import com.carworkz.dearo.extensions.find

class ServiceAdvisorAdapter(private var context: Context, var selectedServiceAdvisor: String?, private val advisors: List<WorkshopAdviser>) : RecyclerView.Adapter<ServiceAdvisorAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.simple_hsn_spinner_item1, parent, false))
    }

    override fun getItemCount(): Int = advisors.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val advisor = advisors[position]
        holder.displayView.text = advisor.name
        holder.displayView.isChecked = advisor.id == selectedServiceAdvisor
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    {
        internal val displayView: CheckedTextView = view.find(R.id.textView)

        init {
            displayView.setOnClickListener {
                if (adapterPosition != -1) {
                    selectedServiceAdvisor = advisors[adapterPosition].id
                    notifyDataSetChanged()
                }
            }
        }
    }
}