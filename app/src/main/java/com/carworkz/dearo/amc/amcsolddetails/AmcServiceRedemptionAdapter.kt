package com.carworkz.dearo.amc.amcsolddetails

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R
import com.carworkz.dearo.domain.entities.SoldAMCDetails
import com.carworkz.dearo.extensions.find

class AmcServiceRedemptionAdapter(val list: List<SoldAMCDetails.AmcService>) : RecyclerView.Adapter<AmcServiceRedemptionAdapter.BenefitHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): BenefitHolder {
        return BenefitHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_amc_benefits, parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: BenefitHolder, position: Int) {
        val service = list[position]
        val availableQuantity=service.quantity-service.availedQuantity


        holder.benefitName.text = service.text
        holder.availableCount.text = "$availableQuantity"
        if (availableQuantity == 0)
        {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M)
                holder.availableCount.setTextColor(holder.itemView.resources.getColor(R.color.persion_red, null))
            else
                holder.availableCount.setTextColor(holder.itemView.resources.getColor(R.color.persion_red))
        }
        holder.usedCount.text = "${service.availedQuantity} Availed"
    }

    class BenefitHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val benefitName: TextView = itemView.find(R.id.benefitName)
        internal val availableCount: TextView = itemView.find(R.id.availableCount)
        internal val usedCount: TextView = itemView.find(R.id.usedCount)
    }
}