package com.carworkz.dearo.amc.amcdetails

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R
import com.carworkz.dearo.extensions.find

class AmcTermsAdapter(val list: List<String>) : RecyclerView.Adapter<AmcTermsAdapter.BenefitHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): BenefitHolder {
        return BenefitHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_icon_and_text, parent, false))
    }

    override fun getItemCount(): Int = list.size


    override fun onBindViewHolder(holder: BenefitHolder, position: Int) {
        val text="\u25cf "+ list[position]
        holder.benefitName.text =text
//        holder.icon.apply { image = holder.itemView.context.getDrawable(R.drawable.shape_circle) }
//        holder.icon.setBackgroundColor(Color.BwLACK)
        holder.icon.visibility = View.GONE
    }

    class BenefitHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val benefitName: TextView = itemView.find(R.id.text)
        internal val icon: ImageView = itemView.find(R.id.icon)
    }
}