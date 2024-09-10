package com.carworkz.dearo.oslvendor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.NoDefaultSpinner
import com.carworkz.dearo.R
import com.carworkz.dearo.domain.entities.Vendor
import com.carworkz.dearo.extensions.find
import com.carworkz.dearo.outwarding.helper.OutwardItem

class OslLabourVendorSelectionAAdapter(val items: List<OutwardItem>, val vendorList: ArrayList<Vendor>) : RecyclerView.Adapter<OslLabourVendorSelectionAAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //inflate layout for vendor selection
        val view=LayoutInflater.from(parent.context).inflate(R.layout.row_osl_vendor_selection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //get item at position
        val item=items.get(position)
        //set labour name
        holder.labourText.text=item.text
        //set vendor list on spinner
        holder.vendorSpinner.adapter = ArrayAdapter(holder.itemView.context, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, vendorList)
//        (holder.vendorSpinner.adapter as ArrayAdapter<*>).setDropDownViewResource(R.layout.simple_hsn_spinner_item1)
        holder.vendorSpinner.prompt = "Select Vendor"
        //find existing selection of vendor and set it
        val existingVendorIndex= vendorList.indexOf(vendorList.find { it.id== item.vendor?.id})
        holder.vendorSpinner.setSelection(existingVendorIndex)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val labourText: TextView = itemView.find(R.id.labourText)
        internal val vendorSpinner: NoDefaultSpinner = itemView.find(R.id.vendorSpinner)

        init {
            vendorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    items[adapterPosition].vendor = vendorList[position]

                   /* val item = items[adapterPosition]
                    //CHECK FOR EXISTING SELECTION
                    if(items[adapterPosition].vendor?.id!=vendorList[position].id) {
                        items[adapterPosition].vendor = vendorList[position]
                        val vendorMargin = (item.rate * vendorList[position].vendorMargin) / 100
                        items[adapterPosition].finalRate = item.surcharge + item.rate + vendorMargin
                    }
                    else
                    {
                        items[adapterPosition].finalRate = item.finalRate
                    }*/
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
        }
    }

}