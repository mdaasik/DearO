package com.carworkz.dearo.outwarding

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.databinding.NewEstimatorTitleBinding

class SectionViewHolder(val binding: NewEstimatorTitleBinding) : RecyclerView.ViewHolder(binding.root) {
    val sectionTitleView: TextView = binding.tvSectionTitle
    val sectionTotalView: TextView = binding.tvSectionTotal
}