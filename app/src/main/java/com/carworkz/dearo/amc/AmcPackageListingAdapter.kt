package com.carworkz.dearo.amc

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R
import com.carworkz.dearo.domain.entities.AMCPackage
import com.carworkz.dearo.utils.Utility

class AmcPackageListingAdapter(private val context: Context, private val packageList: List<AMCPackage>,private val listener:AmcCallbackListener) : RecyclerView.Adapter<AmcPackageListingAdapter.AmcCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): AmcCardViewHolder {
        return AmcCardViewHolder(LayoutInflater.from(context).inflate(R.layout.item_amc_list, parent, false))

    }

    override fun getItemCount(): Int = packageList.size

    override fun onBindViewHolder(holder: AmcCardViewHolder, position: Int) {
        val obj = packageList[position]
        holder.packageName.text = obj.code
        holder.description.text = obj.name
        holder.benefit.text = obj.description
        holder.price.text = Utility.convertToCurrency(obj.price)
        holder.sellButton.setOnClickListener { v -> listener.onPackageSelected(packageList[position]) }
    }

    class AmcCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val packageName = view.findViewById(R.id.amcPackageName) as TextView
        val description = view.findViewById(R.id.description) as TextView
        val price = view.findViewById(R.id.price) as TextView
        val benefit = view.findViewById(R.id.benefit) as TextView
        val sellButton = view.findViewById(R.id.sellButton) as Button

    }

    interface AmcCallbackListener
    {
        fun onPackageSelected(pkg:AMCPackage)
    }
}