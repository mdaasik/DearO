package com.carworkz.dearo.othersyshistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R
import com.carworkz.dearo.domain.entities.History
import com.carworkz.dearo.domain.entities.Invoice
import com.carworkz.dearo.extensions.find
import com.carworkz.dearo.utils.Utility

class OtherHistoryAdapter(val list: List<History>, val registrationNumber: String, val interaction: OtherHistoryInteraction) : RecyclerView.Adapter<OtherHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_vehicle_history_jc, parent, false))

    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val obj = list[position]
        holder.statusView.visibility = View.GONE
        holder.unapprovedJobContainer.visibility = View.GONE
        holder.secondaryCtaView.visibility = View.GONE
        holder.moneyView.visibility = View.GONE
        holder.idView.text = obj.invoice?.invoiceId
        holder.approvedJobsView.text = getPartsAndLabourFormatted(obj.invoice)
        holder.primaryCtaView.text = "View Details"
        holder.primaryCtaView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_details, 0, 0, 0)
        val timeRemaining = Utility.formatDate(obj.invoice?.date, Utility.TIMESTAMP, Utility.DATE_FORMAT_6, Utility.TIMEZONE_UTC)
        holder.dateView.text = timeRemaining
    }

    private fun getPartsAndLabourFormatted(otherSysInvoice: Invoice?): String {
        var formattedString=""
        if(!otherSysInvoice?.parts.isNullOrEmpty()) {
             formattedString = "Parts : \n"
            otherSysInvoice?.parts?.forEach {
                formattedString += it.text + ", "
            }
            if(formattedString.contains(','))
            {
                formattedString = formattedString.substring(0,formattedString.lastIndex-1)
            }
        }
        if(!otherSysInvoice?.labours.isNullOrEmpty())
        {
            formattedString+= if(!formattedString.isEmpty()) "\nLabours : \n" else "Labours : \n"
            otherSysInvoice?.labours?.forEach {
                formattedString+=it.text +", "
            }
            if(formattedString.contains(','))
            {
                formattedString = formattedString.substring(0,formattedString.lastIndex-1)
            }
        }
        return formattedString
    }

    override fun getItemViewType(position: Int): Int
    {
        if(list.get(position).step.equals("HAS_MORE"))
        {
            return 1
        }
        else
        {
            return 2
        }
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        internal val statusView: TextView = itemView.find(R.id.tv_job_listing_status)
        internal val idView: TextView = itemView.find(R.id.tv_job_listing_jcno)
        internal val approvedJobContainer: LinearLayout = itemView.find(R.id.ll_job_approvedContainer)
        internal val unapprovedJobContainer: LinearLayout = itemView.find(R.id.ll_job_unApprovedContainer)
        internal val approvedJobsView: TextView = itemView.find(R.id.jobs_approved)
//        internal val unApprovedJobsView: TextView = itemView.find(R.id.jobs_unapproved)
        internal val primaryCtaView: TextView = itemView.find(R.id.primary_cta_one)
        internal val secondaryCtaView: TextView = itemView.find(R.id.primary_cta_two)
        internal val dateView: TextView = itemView.find(R.id.tv_date)
        internal val moneyView: TextView = itemView.find(R.id.tv_money)

        init {
            primaryCtaView.setOnClickListener {
                interaction.showDetails(list[adapterPosition])
            }
        }
    }
}