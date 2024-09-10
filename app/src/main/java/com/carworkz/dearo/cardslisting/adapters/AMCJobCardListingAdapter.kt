package com.carworkz.dearo.cardslisting.adapters

import android.annotation.SuppressLint
import android.content.Context
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
import com.carworkz.dearo.cardslisting.CardListingInteractionProvider
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.extensions.find
import com.carworkz.dearo.extensions.textColor
import com.carworkz.dearo.extensions.toast
import com.carworkz.dearo.pdf.Source
import com.carworkz.dearo.utils.Utility


class AMCJobCardListingAdapter( val context: Context, val jobCardList: MutableList<AMCJobCard>, val interaction: CardListingInteractionProvider) : RecyclerView.Adapter<AMCJobCardListingAdapter.ViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(context).inflate(R.layout.row_vehicle_history_jc, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return jobCardList.size
    }

    fun updateList(updateList: List<AMCJobCard>)
    {
        val oldSize = itemCount
        /*updateList.forEach { jobCardList.add(it)
        notifyItemInserted(oldSize+1)}*/
        jobCardList.addAll(updateList)
        notifyItemRangeInserted(oldSize, itemCount)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val jobCard = jobCardList[position]

        holder.statusView.text = jobCardList[position].stepLabel
        @SuppressLint("SetTextI18n")
        holder.idView.text = "\u2022 ${jobCardList[position].jobCardId}"
        holder.dateView.text = Utility.formatDate(jobCardList[position].createdOn, Utility.TIMESTAMP, Utility.DATE_FORMAT_4, Utility.TIMEZONE_UTC)
        if (jobCardList[position].jobCardId == null)
        {
            holder.primaryCtaView.visibility = View.GONE
        }

        if (jobCardList[position].invoice == null || jobCardList[position].invoice?.status == Invoice.STATUS_PROFORMA || jobCardList[position].invoice?.status == Invoice.STATUS_CLOSED)
        {
            holder.moneyView.visibility = View.GONE
            holder.secondaryCtaView.visibility = View.GONE
        }
        else
        {
            holder.moneyView.text = "${
                Utility.round(jobCardList[position].invoice?.summary?.totalAmountAfterTax
                ?: 0.0, 1)}"
        }

        if (jobCardList[position].status == JobCard.STATUS_INITIATED || jobCardList[position].status == JobCard.STATUS_IN_PROGRESS)
        {
            holder.approvedJobContainer.visibility = View.GONE
            holder.unapprovedJobContainer.visibility = View.GONE
        }
        else
        {
            if (jobCardList[position].invoice != null)
            {
                holder.approvedJobsView.text = getRedemptionsFormatted(jobCardList[position].redemptions)
            }
            else
            {
                holder.approvedJobContainer.visibility = View.GONE
            }
            if (jobCardList[position].remarks?.isNotEmpty() == true)
            {
                val redRemarkList = jobCardList[position].remarks?.filter { it.type == "red" }?.map(Remark::remark)
                val yellowRemarkList = jobCardList[position].remarks?.filter { it.type == "yellow" }?.map(
                    Remark::remark)

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

    private fun getRedemptionsFormatted(redemptions: List<Redemption>?): String
    {
        var formattedString = ""

            redemptions?.forEach {
                formattedString += it.name + ", "
            }
            if (formattedString.contains(','))
            {
                formattedString = formattedString.substring(0, formattedString.lastIndex - 1)
            }
        return formattedString
    }

/*    override fun removeItemAndRefresh(id: String)
    {
        val index = jobCardList.indexOf(jobCardList.find { it.id == id })
        if (index != -1)
        {
            jobCardList.removeAt(index)
            notifyItemRemoved(index)
        }
    }*/

    inner class ViewHolder(view: View) :  RecyclerView.ViewHolder(view)
    {

        private val context = view.context
        private val jobsDone: TextView = itemView.find(R.id.jobsDone)
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
            jobsDone.text=context.getText(R.string.amc_benefits_availed)
            primaryCtaView.setOnClickListener { interaction.getJobCardById(jobCardList[adapterPosition].id!!) }

            secondaryCtaView.setOnClickListener {
                val invoice = jobCardList[adapterPosition].invoice
                if (invoice?.id != null)
                {
                    // interaction.startInvoicePreview(list[adapterPosition].invoice?.id!!, list[adapterPosition].jobCardId!!, list[adapterPosition].invoice?.invoiceId!!, invoice.splitInvoice)
                    interaction.startInvoicePreview(jobCardList[adapterPosition].invoice!!, jobCardList[adapterPosition].jobCardId!!, Source.DEFAULT)
                }
                else
                {
                    secondaryCtaView.context.toast("Invoice Not Found")
                }
            }
        }
    }
}