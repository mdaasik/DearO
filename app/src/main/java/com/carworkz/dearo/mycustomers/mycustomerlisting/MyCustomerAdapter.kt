package com.carworkz.dearo.mycustomers.mycustomerlisting

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R
import com.carworkz.dearo.domain.entities.CustomerVehicleDetails
import com.carworkz.dearo.extensions.find
import com.carworkz.dearo.utils.Utility

class MyCustomerAdapter(val adapterList: MutableList<CustomerVehicleDetails>, val interaction: Interaction?) : RecyclerView.Adapter<MyCustomerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_my_customer, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = adapterList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.makeModelVariantView.text = "${adapterList[position].vehicle.makeName} - ${adapterList[position].vehicle.modelName} - ${adapterList[position].vehicle.fuelType} "
        holder.registrationNameView.text = "${adapterList[position].vehicle.registrationNumber}, ${adapterList[position].customer.name}"
        holder.recentDateView.text = Utility.formatDate(adapterList[position].lastJobCardDate, Utility.TIMESTAMP, Utility.DATE_FORMAT_4, Utility.TIMEZONE_UTC)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val makeModelVariantView = itemView.find<TextView>(R.id.tv_make_model_variant)
        internal val registrationNameView = itemView.find<TextView>(R.id.tv_reg_name)
        internal val recentDateView = itemView.find<TextView>(R.id.tv_recent_date)
        private val callBtn = itemView.find<ImageView>(R.id.btn_call)

        init {
            itemView.setOnClickListener { interaction?.startActivity(adapterList[adapterPosition]) }
            callBtn.setOnClickListener { Utility.makeCall(it.context, adapterList[adapterPosition].customer.mobile) }
        }
    }

    interface Interaction {
        fun startActivity(card: CustomerVehicleDetails)
    }
}