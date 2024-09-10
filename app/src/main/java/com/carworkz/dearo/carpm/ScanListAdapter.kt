package com.carworkz.dearo.carpm

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.databinding.AdapterCodeListBinding
import com.carworkz.dearo.domain.entities.CodeDetail

class ScanListAdapter(
    private val context: Context, private var codeDetails: List<CodeDetail>
) : RecyclerView.Adapter<ScanListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterCodeListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = codeDetails[position]
        holder.binding.apply {
            tvDtc.text = buildString {
                append("Fault Code : ")
                append(data.dtc)
            }
            tvModule.text = data.module
            tvStatus.text = data.status
            tvMeaning.text = buildString {
                append("Meaning : ")
                append(data.meaning)
            }
            tvSystem.text = data.system

            if (data.descriptions.size > 0) {
                tvDescription.visibility = View.VISIBLE
            } else {
                tvDescription.visibility = View.GONE
            }

            if (data.causes.size > 0) {
                tvCauses.visibility = View.VISIBLE
            } else {
                tvCauses.visibility = View.GONE
            }

            if (data.solutions.size > 0) {
                tvSolution.visibility = View.VISIBLE
            } else {
                tvSolution.visibility = View.GONE
            }

            if (data.symptoms.size > 0) {
                tvSymptoms.visibility = View.VISIBLE
            } else {
                tvSymptoms.visibility = View.GONE
            }

            tvShowMore.setOnClickListener {
                if (constraintShowMore.visibility == View.VISIBLE) {
                    constraintShowMore.visibility = View.GONE
                    tvShowMore.text = "ShowMore"
                } else {
                    constraintShowMore.visibility = View.VISIBLE
                    tvShowMore.text = "ShowLess"
                }
            }

            tvDescription.setOnClickListener {
                toggleListView(lvDescription, data.descriptions)
            }

            tvCauses.setOnClickListener {
                toggleListView(lvCauses, data.causes)
            }

            tvSolution.setOnClickListener {
                toggleListView(lvSolution, data.solutions)
            }

            tvSymptoms.setOnClickListener {
                toggleListView(lvSymptoms, data.symptoms)
            }
        }
    }

    override fun getItemCount(): Int = codeDetails.size

    private fun toggleListView(listView: ListView, data: List<String>) {
        if (listView.adapter == null) {
            // Add dot symbol to each item in the list
            val dataWithDots = data.map { "• $it" }

            // Create and set adapter with data that includes dots
            val adapter = ArrayAdapter(context, R.layout.simple_list_item_1, dataWithDots)
            listView.adapter = adapter
        }

        // Toggle visibility
        listView.visibility = if (listView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    inner class ViewHolder(val binding: AdapterCodeListBinding) :
        RecyclerView.ViewHolder(binding.root)
}