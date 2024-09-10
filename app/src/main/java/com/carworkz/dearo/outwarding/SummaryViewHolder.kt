package com.carworkz.dearo.outwarding

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.databinding.NewProformaSummaryBinding

class SummaryViewHolder(val binding: NewProformaSummaryBinding) : RecyclerView.ViewHolder(binding.root) {
    val summarySubtotal: TextView = binding.newProformaSummarySubtotal
    val summaryTotalTax: TextView = binding.newProformaSummaryTax
    val summaryTotalDiscount: TextView = binding.newProformaSummaryDiscount
    val summaryTotal: TextView = binding.newProformaSummaryTotal
}
