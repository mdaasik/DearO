package com.carworkz.dearo.addjobcard.createjobcard.accidental.selectionaddress

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R
import com.carworkz.dearo.domain.entities.InsuranceCompanyDetails

class InsuranceAddressSelectionAdapter(val context: Context, val address: List<InsuranceCompanyDetails>) : RecyclerView.Adapter<InsuranceAddressSelectionAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_insurance_address_selection, parent, false))
    }

    override fun getItemCount(): Int = address.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val address = address[position]
        holder.let {
            it.detailAddressView.text = address.address
            it.areaAddressView.text = address.city
            it.insuranceAddressSelectionParentView.tag = address
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val detailAddressView: TextView = view.findViewById(R.id.insAddSelectDetailView)
        val areaAddressView: TextView = view.findViewById(R.id.insAddSelectAreaView)
        val insuranceAddressSelectionParentView: LinearLayout = view.findViewById(R.id.insuranceAddressSelectionParentView)

        init {
            insuranceAddressSelectionParentView.setOnClickListener {
                val address = it.tag as InsuranceCompanyDetails
                (context as InsuranceAddressSelectionActivity).setResult(address)
            }
        }
    }
}