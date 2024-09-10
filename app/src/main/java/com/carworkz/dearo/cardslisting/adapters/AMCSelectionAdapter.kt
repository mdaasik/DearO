package com.carworkz.dearo.cardslisting.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R
import com.carworkz.dearo.amc.amcdetails.AmcBenefitAdapter
import com.carworkz.dearo.domain.entities.AMC
import com.carworkz.dearo.utils.Utility

class AMCSelectionAdapter(private val context: Context, var amcList: ArrayList<AMC>, private val listener: AmcSelectListener?) : RecyclerView.Adapter<AMCSelectionAdapter.AMCSelectionViewHolder>()
{
    lateinit var selectedAmc: AMC
    var isInitialized = false

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): AMCSelectionViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_amc_selector_listing, parent, false)
        return AMCSelectionViewHolder(view)
    }


    override fun onBindViewHolder(holder: AMCSelectionViewHolder, position: Int)
    {
        val amcObj = amcList[position]
        holder.radioButton.tag = amcObj
        holder.radioButton.isChecked = amcObj.isSelected

        val chipText = " <b>${amcObj.amcCode}</b>"
        holder.chipView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            Html.fromHtml(chipText, Html.FROM_HTML_MODE_LEGACY)
        }
        else
        {
            Html.fromHtml(chipText)
        }

        (holder.chipView.background as? GradientDrawable)?.setColor(ContextCompat.getColor(context, android.R.color.holo_orange_dark))

        holder.chipView.setTextColor(  Color.WHITE)

        val timeRemaining = "Expires On: <b>${Utility.formatDate(amcObj.expiryDate, Utility.TIMESTAMP, Utility.DATE_FORMAT_4, Utility.TIMEZONE_UTC)}</b>"

        holder.amcExpiry.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            Html.fromHtml(timeRemaining, Html.FROM_HTML_MODE_LEGACY)
        }
        else
        {
            Html.fromHtml(timeRemaining)
        }

        //if amc has services then show it in recyclerview
        if (amcObj.services.isNotEmpty() && listener!=null)
        {
            holder.amcServicesLayout.visibility = View.VISIBLE
            holder.amcServicesRecyclerView.adapter = AmcBenefitAdapter(amcObj.services)
        }
    }

    override fun getItemCount(): Int = amcList.size

    inner class AMCSelectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var radioButton: RadioButton = itemView.findViewById(R.id.rb_row_amc)
        var chipView: TextView = itemView.findViewById(R.id.amcCode)
        var amcExpiry: TextView = itemView.findViewById(R.id.amcExpiry)
        var amcServicesLayout: LinearLayout = itemView.findViewById(R.id.amcServicesLayout)
        var amcServicesRecyclerView: RecyclerView = itemView.findViewById(R.id.amcServicesRecyclerView)

        init
        {
            if (listener == null)
            {
                radioButton.visibility = View.GONE
            }
            radioButton.setOnClickListener { v ->
                if (radioButton.isChecked)
                {
                    /* if(!isInitialized)
                     {
                         selectedAmc=v.tag as AMC
                         isInitialized=true
                         selectedAmc.selectedPosition=adapterPosition
                         selectedAmc.isSelected=true
                         amcList[selectedAmc.selectedPosition] = selectedAmc
                         listener?.OnAmcSelected(selectedAmc)
                     }
                     else
                     {*/
                    selectedAmc.isSelected = false
                    amcList[selectedAmc.selectedPosition] = selectedAmc
                    selectedAmc = v.tag as AMC
                    selectedAmc.selectedPosition = adapterPosition
                    selectedAmc.isSelected = true
                    listener?.onAmcSelected(selectedAmc)
                    /*  }*/
                }
                else
                {
                    selectedAmc = v.tag as AMC
                    selectedAmc.isSelected = false
                    selectedAmc.selectedPosition = adapterPosition
                    amcList[selectedAmc.selectedPosition] = selectedAmc
                }
                notifyDataSetChanged()
            }
        }
    }

    interface AmcSelectListener
    {
        fun onAmcSelected(amc: AMC)
    }
}