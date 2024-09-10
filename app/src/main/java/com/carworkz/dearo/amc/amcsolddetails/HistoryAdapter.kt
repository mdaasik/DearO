package com.carworkz.dearo.amc.amcsolddetails

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R
import com.carworkz.dearo.domain.entities.History
import com.carworkz.dearo.domain.entities.Invoice
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.domain.entities.Remark
import com.carworkz.dearo.extensions.find
import com.carworkz.dearo.extensions.textColor
import com.carworkz.dearo.extensions.toast
import com.carworkz.dearo.pdf.Source
import com.carworkz.dearo.utils.Utility

class HistoryAdapter(val list: List<History>, val registrationNumber: String, val interaction: AmcDetailsInteraction) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {

        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_vehicle_history_jc, parent, false))

    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int
    {
        if (list.get(position).step.equals("HAS_MORE"))
        {
            return 1
        }
        else
        {
            return 2
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {


        holder.statusView.text = list[position].stepLabel
        @SuppressLint("SetTextI18n")
        holder.idView.text = "\u2022 ${list[position].jobCardId}"
        holder.dateView.text = Utility.formatDate(list[position].createdOn, Utility.TIMESTAMP, Utility.DATE_FORMAT_4, Utility.TIMEZONE_UTC)
        if (list[position].jobCardId == null)
        {
            holder.primaryCtaView.visibility = View.GONE
        }

        if (list[position].invoice == null || list[position].invoice?.status == Invoice.STATUS_PROFORMA || list[position].invoice?.status == Invoice.STATUS_CLOSED)
        {
            holder.moneyView.visibility = View.GONE
            holder.secondaryCtaView.visibility = View.GONE
        }
        else
        {
            holder.moneyView.text = "${Utility.round(list[position].invoice?.summary?.totalAmountAfterTax
                    ?: 0.0, 1)}"
        }

        if (list[position].status == JobCard.STATUS_INITIATED || list[position].status == JobCard.STATUS_IN_PROGRESS)
        {
            holder.approvedJobContainer.visibility = View.GONE
            holder.unapprovedJobContainer.visibility = View.GONE
        }
        else
        {
            if (list[position].invoice != null)
            {
                holder.approvedJobsView.text = getPartsAndLabourFormatted(list[position].invoice)
            }
            else
            {
                holder.approvedJobContainer.visibility = View.GONE
            }
            if (list[position].remarks?.isNotEmpty() == true)
            {
                val redRemarkList = list[position].remarks?.filter { it.type == "red" }?.map(Remark::remark)
                val yellowRemarkList = list[position].remarks?.filter { it.type == "yellow" }?.map(Remark::remark)

                val remarkLinearLayout = LinearLayout(holder.itemView.context)
                remarkLinearLayout.orientation = LinearLayout.VERTICAL

                val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
                //Red Remark
                if (redRemarkList!=null && redRemarkList.isNotEmpty())
                {
                    val redRemarkLinearLayout = LinearLayout(holder.itemView.context)
                    redRemarkLinearLayout.orientation = LinearLayout.HORIZONTAL
                    val redCircle = TextView(holder.itemView.context)
                    redCircle.text = "\u2B24"
                    redCircle.setTextColor(holder.itemView.context.resources.getColor(R.color.switch_red))
                    redRemarkLinearLayout.addView(redCircle)
                    val redRemarkTextView = TextView(holder.itemView.context)
                    val redRemarkText = "<b>RED REMARKS: </b>" + redRemarkList?.joinToString(",")
                    redRemarkTextView.text = Html.fromHtml(redRemarkText)
                    params.setMargins(20, 0, 0, 0)
                    redRemarkTextView.layoutParams = params
                    redRemarkLinearLayout.addView(redRemarkTextView)
                    remarkLinearLayout.addView(redRemarkLinearLayout)
                }

                //Yellow Remark
                if(yellowRemarkList!=null && yellowRemarkList.isNotEmpty())
                {
                    val yellowRemarkLinearLayout = LinearLayout(holder.itemView.context)
                    yellowRemarkLinearLayout.orientation = LinearLayout.HORIZONTAL
                    val yellowCircle = TextView(holder.itemView.context)
                    yellowCircle.text = "\u2B24"
                    yellowCircle.setTextColor(holder.itemView.context.resources.getColor(R.color.switch_yellow))
                    yellowRemarkLinearLayout.addView(yellowCircle)
                    val yellowRemarkTextView = TextView(holder.itemView.context)
                    val yellowRemarkText = "<b>YELLOW REMARKS: </b>" + yellowRemarkList?.joinToString(",")
                    yellowRemarkTextView.text = Html.fromHtml(yellowRemarkText)
                    yellowRemarkTextView.layoutParams = params
                    yellowRemarkLinearLayout.addView(yellowRemarkTextView)
                    remarkLinearLayout.addView(yellowRemarkLinearLayout)
                }


                holder.unapprovedJobContainer.addView(remarkLinearLayout)
            }
            else
            {
                holder.unapprovedJobContainer.visibility = View.GONE
            }
        }
        (holder.statusView.background as? GradientDrawable)?.setColor(ContextCompat.getColor(holder.statusView.context, R.color.light_grey))
        holder.statusView.textColor = ContextCompat.getColor(holder.statusView.context, R.color.textColorPrimary)

    }

    private fun getPartsAndLabourFormatted(invoice: Invoice?): String
    {
        var formattedString = ""
        if (!invoice?.parts.isNullOrEmpty())
        {
            formattedString = "Parts : \n"
            invoice?.parts?.forEach {
                formattedString += it.text + ", "
            }
            if (formattedString.contains(','))
            {
                formattedString = formattedString.substring(0, formattedString.lastIndex - 1)
            }
        }
        if (!invoice?.labours.isNullOrEmpty())
        {
            formattedString += if (!formattedString.isEmpty()) "\nLabours : \n" else "Labours : \n"
            invoice?.labours?.forEach {
                formattedString += it.text + ", "
            }
            if (formattedString.contains(','))
            {
                formattedString = formattedString.substring(0, formattedString.lastIndex - 1)
            }
        }
        if (!invoice?.packages.isNullOrEmpty())
        {
            formattedString += if (!formattedString.isEmpty()) "\nService Package : \n" else "Service Package : \n"
            invoice?.packages?.forEach {
                formattedString += it.name + ", "
            }
            if (formattedString.contains(','))
            {
                formattedString = formattedString.substring(0, formattedString.lastIndex - 1)
            }
        }
        return formattedString
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

        init
        {
            primaryCtaView.setOnClickListener { interaction.getJobCardById(list[adapterPosition].id!!) }
            secondaryCtaView.setOnClickListener {
                val invoice = list[adapterPosition].invoice
                if (invoice?.id != null)
                {
                    // interaction.startInvoicePreview(list[adapterPosition].invoice?.id!!, list[adapterPosition].jobCardId!!, list[adapterPosition].invoice?.invoiceId!!, invoice.splitInvoice)
                    interaction.startInvoicePreview(list[adapterPosition].invoice!!, list[adapterPosition].jobCardId!!, Source.DEFAULT)
                }
                else
                {
                    secondaryCtaView.context.toast("Invoice Not Found")
                }
            }
        }
    }
}