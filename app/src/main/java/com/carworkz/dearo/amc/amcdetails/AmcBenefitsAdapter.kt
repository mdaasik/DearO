package com.carworkz.dearo.amc.amcdetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R
import com.carworkz.dearo.domain.entities.AmcBenefit
import com.carworkz.dearo.extensions.find

class AmcBenefitsAdapter(val list: List<AmcBenefit>) : RecyclerView.Adapter<AmcBenefitsAdapter.BenefitHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): BenefitHolder {
        return BenefitHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_icon_and_text, parent, false))
    }

    override fun getItemCount(): Int = list.size


    override fun onBindViewHolder(holder: BenefitHolder, position: Int) {
        val benefit = list[position]
        holder.benefitName.text = benefit.text
    }

    class BenefitHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val benefitName: TextView = itemView.find(R.id.text)

    }
}
