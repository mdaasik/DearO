package com.carworkz.dearo.cardslisting.adapters

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.addcustomer.AddCustomerActivity
import com.carworkz.dearo.addjobcard.addeditvehicle.VehicleDetailsActivity
import com.carworkz.dearo.addjobcard.createjobcard.jobs.viewjc.ViewJCActivity
import com.carworkz.dearo.cardslisting.CardListingBaseAdapter
import com.carworkz.dearo.cardslisting.CardListingBaseViewHolder
import com.carworkz.dearo.cardslisting.CardListingFragment
import com.carworkz.dearo.cardslisting.CardListingInteractionProvider
import com.carworkz.dearo.domain.entities.Address
import com.carworkz.dearo.domain.entities.Invoice
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.extensions.*
import com.carworkz.dearo.morecta.MoreCtaListDialogFragment
import com.carworkz.dearo.pdf.Source
import com.carworkz.dearo.utils.Utility

class InvoiceListingAdapter(private val invoiceList: MutableList<Invoice>, private val interactionProvider: CardListingInteractionProvider) : CardListingBaseAdapter<InvoiceListingAdapter.InvoiceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_new_job_card_listing, parent, false)
        return InvoiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        val invoice = invoiceList[position]
        val jobCard = invoice.jobCard
        when (invoice.status) {
            Invoice.STATUS_PROFORMA -> holder.initProformaView()
            Invoice.STATUS_INVOICED, Invoice.STATUS_PAID_PARTIAL -> holder.initInvoiceView()
            Invoice.STATUS_PAID -> holder.initPaidView()
            Invoice.STATUS_CANCEL -> holder.initCancelledView()
        }

        holder.callView.tag = jobCard?.customer?.mobile
        holder.parentView.tag = invoice

        val chipText = " <b>${jobCard?.type}</b> : ${invoice.stepLabel} "
        holder.chipView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(chipText, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(chipText)
        }

        when (jobCard?.type) {
            JobCard.TYPE_ACCIDENTAL -> {
                (holder.chipView.background as? GradientDrawable)?.setColor(ContextCompat.getColor(holder.chipView.context, R.color.colorAccidental))
                holder.chipView.textColor = ContextCompat.getColor(holder.chipView.context, R.color.colorAccidentalText)
            }

            JobCard.TYPE_MAJOR -> {
                (holder.chipView.background as? GradientDrawable)?.setColor(ContextCompat.getColor(holder.chipView.context, R.color.colorMajor))
                holder.chipView.textColor = ContextCompat.getColor(holder.chipView.context, R.color.colorMajorText)
            }

            JobCard.TYPE_MINOR -> {
                (holder.chipView.background as? GradientDrawable)?.setColor(ContextCompat.getColor(holder.chipView.context, R.color.colorMinor))
                holder.chipView.textColor = ContextCompat.getColor(holder.chipView.context, R.color.colorMinorText)
            }

            JobCard.TYPE_PERIODIC -> {
                (holder.chipView.background as? GradientDrawable)?.setColor(ContextCompat.getColor(holder.chipView.context, R.color.colorPeriodic))
                holder.chipView.textColor = ContextCompat.getColor(holder.chipView.context, R.color.colorPeriodicText)
            }

            else -> {
                (holder.chipView.background as? GradientDrawable)?.setColor(ContextCompat.getColor(holder.chipView.context, R.color.light_grey))
                holder.chipView.textColor = ContextCompat.getColor(holder.chipView.context, R.color.textColorPrimary)
            }
        }

        @SuppressLint("SetTextI18n")
        holder.idView.text = "\u2022 ${invoice.invoiceId}"

        holder.titleView.text = jobCard?.vehicle?.registrationNumber
        holder.moneyEstimateView.text = Utility.convertToCurrency(invoice.summary?.totalAmountAfterTax)

        @SuppressLint("SetTextI18n")
        holder.makeModelView.text = "${jobCard?.vehicle?.make?.name} - ${jobCard?.vehicle?.model?.name} - ${jobCard?.vehicle?.fuelType}"

        holder.nameView.text = jobCard?.customer?.name

        if (invoiceList[position].status == Invoice.STATUS_PROFORMA) {
            val timeRemaining = Utility.timeRemaining(invoice.proformaDate)
            holder.timeImageView.visibility = View.VISIBLE
            holder.timeRemainingView.visibility = View.VISIBLE
            holder.timeRemainingView.text = timeRemaining
            if (!timeRemaining.contains("ago"))
                holder.timeRemainingView.setTextColor(ContextCompat.getColor(holder.timeRemainingView.context, R.color.forest_green))
            else {
                holder.timeRemainingView.setTextColor(ContextCompat.getColor(holder.timeRemainingView.context, R.color.persion_red))
            }
        }

        if (invoiceList[position].summary?.totalAmountAfterTax != null && invoiceList[position].summary?.totalAmountAfterTax!! > 0.0) {
            holder.moneyEstimateView.visibility = View.VISIBLE
            holder.moneyImageView.visibility = View.VISIBLE
        } else {
            holder.moneyEstimateView.visibility = View.GONE
            holder.moneyImageView.visibility = View.GONE
        }

        if (invoiceList[position].status == Invoice.STATUS_INVOICED || invoiceList[position].status == Invoice.STATUS_PAID_PARTIAL) {
            val timeRemaining = Utility.formatDate(invoice.date, Utility.TIMESTAMP, Utility.DATE_FORMAT_2, Utility.TIMEZONE_UTC)
            holder.timeImageView.visibility = View.VISIBLE
            holder.timeRemainingView.visibility = View.VISIBLE
            holder.timeRemainingView.text = timeRemaining
            holder.timeRemainingView.textColor = ContextCompat.getColor(holder.timeRemainingView.context, R.color.old_lavender)
        }
    }

    override fun getItemCount(): Int {
        return invoiceList.size
    }

    override fun removeItemAndRefresh(id: String) {
        val index = invoiceList.indexOf(invoiceList.find { it.id == id })
        if (index != -1) {
            invoiceList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    inner class InvoiceViewHolder(private val view: View) : CardListingBaseViewHolder(view) {

        init {
            moreActionView.setOnClickListener { showCtaSheet() }
            moneyEstimateView.visibility = View.VISIBLE
            moneyImageView.visibility = View.VISIBLE
            moreActionView.visibility = View.VISIBLE
            parentView.setOnClickListener {
                when (invoiceList[adapterPosition].status) {
                    Invoice.STATUS_INVOICED, Invoice.STATUS_PAID_PARTIAL -> {
                        interactionProvider.startInvoicePreview(invoiceList[adapterPosition], invoiceList[adapterPosition].jobCard?.id!!, Source.INVOICED)
                    }

                    Invoice.STATUS_PAID, Invoice.STATUS_CANCEL -> {
                        interactionProvider.startInvoicePreview(invoiceList[adapterPosition], invoiceList[adapterPosition].jobCard?.id!!, Source.DEFAULT)
                    }

                    Invoice.STATUS_PROFORMA -> {
                        val invoice = invoiceList[adapterPosition]
                        interactionProvider.startEditProformaActivity(invoice, invoice.jobCard!!.id, invoice.jobCard!!.jobCardId, invoice.id, invoice.invoiceId, invoice.splitInvoice, invoice.vehicleType,invoice.jobCard!!.type, CardListingFragment.NEW_ESTIMATOR_INVOICE_REQUEST_CODE)
                    }
                }
            }
        }

        internal fun initPaidView() {
            if (adapterPosition == -1) {
                return
            }

            secondaryCtaParent.visibility = View.VISIBLE
            secondaryCtaImageView.visibility = View.VISIBLE
            secondaryCtaView.visibility = View.VISIBLE
            secondaryCtaImageView.image = AppCompatResources.getDrawable(view.context, R.drawable.ic_payment_details_24dp)
            secondaryCtaView.text = view.context.getString(R.string.card_listing_btn_payment_details)
            secondaryCtaParent.setOnClickListener {
                if (adapterPosition != -1) {
                    interactionProvider.callUpdatePayment(invoiceList[adapterPosition].id!!, invoiceList[adapterPosition].invoiceId!!, invoiceList[adapterPosition].jobCard?.id!!)
                }
            }

            primaryCtaView.text = view.context.getString(R.string.card_listing_btn_view_jobcard)
            primaryCtaImageView.image = AppCompatResources.getDrawable(view.context, R.drawable.ic_jobcard_pdf)
            primaryCtaParent.setOnClickListener {
                interactionProvider.startJobCardDetailsPreview(invoiceList[adapterPosition].jobCard!!.id, invoiceList[adapterPosition].jobCard?.jobCardId + " - " + invoiceList[adapterPosition].jobCard?.vehicle?.registrationNumber, Source.PAID)
            }
            timeRemainingView.visibility = View.VISIBLE
            timeImageView.visibility = View.VISIBLE
            timeRemainingView.text = Utility.formatDate(invoiceList[adapterPosition].date, Utility.TIMESTAMP, Utility.DATE_FORMAT_2, Utility.TIMEZONE_UTC)
        }

        internal fun initCancelledView() {
            secondaryCtaView.visibility = View.GONE
            primaryCtaView.text = view.context.getString(R.string.card_listing_btn_view_jobcard)
            primaryCtaImageView.image = AppCompatResources.getDrawable(view.context, R.drawable.ic_jobcard_pdf)
            primaryCtaParent.setOnClickListener {
                interactionProvider.startJobCardDetailsPreview(invoiceList[adapterPosition].jobCard!!.id, invoiceList[adapterPosition].jobCard?.jobCardId + " - " + invoiceList[adapterPosition].jobCard?.vehicle?.registrationNumber, Source.CANCELLED)
            }
            timeRemainingView.visibility = View.VISIBLE
            timeImageView.visibility = View.VISIBLE
            timeRemainingView.text = Utility.formatDate(invoiceList[adapterPosition].date, Utility.TIMESTAMP, Utility.DATE_FORMAT_2, Utility.TIMEZONE_UTC)
        }

        internal fun initInvoiceView() {
            primaryCtaImageView.image = AppCompatResources.getDrawable(view.context, R.drawable.ic_jobcard_pdf)
            secondaryCtaImageView.image = AppCompatResources.getDrawable(view.context, R.drawable.ic_payment_black_24dp)
           // secondaryCtaParent.visibility = View.VISIBLE
            secondaryCtaImageView.visibility = View.VISIBLE
            secondaryCtaView.visibility = View.VISIBLE
            secondaryCtaView.text = view.context.getString(R.string.card_listing_btn_update_payment)
            primaryCtaView.text = view.context.getString(R.string.card_listing_btn_view_jobcard)
            primaryCtaParent.setOnClickListener {
                interactionProvider.startJobCardDetailsPreview(invoiceList[adapterPosition].jobCard!!.id, invoiceList[adapterPosition].jobCard?.jobCardId + " - " + invoiceList[adapterPosition].jobCard?.vehicle?.registrationNumber, Source.INVOICED)

//                startJCPdfActivity(invoiceList[adapterPosition].jobCardId
//                        ?: "", invoiceList[adapterPosition].id!!, invoiceList[adapterPosition].jobCard?.jobCardId + " - " + invoiceList[adapterPosition].jobCard?.vehicle?.registrationNumber)
            }
            secondaryCtaParent.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    interactionProvider.callUpdatePayment(invoiceList[adapterPosition].id!!, invoiceList[adapterPosition].invoiceId!!, invoiceList[adapterPosition].jobCard?.id!!)
                }
            }
        }

        internal fun initProformaView() {
            primaryCtaImageView.image = AppCompatResources.getDrawable(view.context, R.drawable.ic_invoice_preview)
            secondaryCtaImageView.image =AppCompatResources.getDrawable(view.context, R.drawable.ic_job_view)
            secondaryCtaParent.visibility = View.VISIBLE
            secondaryCtaImageView.visibility = View.VISIBLE
            secondaryCtaView.visibility = View.VISIBLE
            secondaryCtaView.text = view.context.getString(R.string.card_listing_btn_view_jobs)
            primaryCtaView.text = view.context.getString(R.string.card_listing_btn_preview_proforma)
            moreActionView.visibility = View.VISIBLE
            secondaryCtaParent.setOnClickListener {
                startViewJcActivity()
            }

            primaryCtaParent.setOnClickListener {
                if (invoiceList[adapterPosition].parts?.isNotEmpty() == true || invoiceList[adapterPosition].labours?.isNotEmpty() == true || invoiceList[adapterPosition].packages?.isNotEmpty() == true) {
                    interactionProvider.startProformaPdf(invoiceList[adapterPosition], invoiceList[adapterPosition].jobCard!!.id, Source.PROFORMA)
                } else {
                    view.context.toast("Add Parts or Labour")
                }
            }
        }

        private fun showCtaSheet() {
            val bundle = Bundle()
            invoiceList[adapterPosition].jobCard?.customer?.address = ArrayList<Address>()
            invoiceList[adapterPosition].jobCard?.customer?.address?.add(invoiceList[adapterPosition].jobCard?.address)
            when (invoiceList[adapterPosition].status) {
                Invoice.STATUS_PROFORMA -> {
                    bundle.putString(MoreCtaListDialogFragment.ARG_JOB_CARD_ID, invoiceList[adapterPosition].jobCard?.id)
                    bundle.putString(MoreCtaListDialogFragment.ARG_INVOICE_ID, invoiceList[adapterPosition].id)
                    bundle.putString(MoreCtaListDialogFragment.ARG_DISPLAY_ID, invoiceList[adapterPosition].jobCard?.jobCardId)
                    bundle.putString(AddCustomerActivity.ARG_CUSTOMER_ID, invoiceList[adapterPosition].jobCard?.customer?.id)
                    bundle.putString(AddCustomerActivity.ARG_TYPE, AddCustomerActivity.ARG_VIEW)
                    bundle.putString(VehicleDetailsActivity.ARG_TYPE, VehicleDetailsActivity.ARG_VIEW)
                    bundle.putParcelable(VehicleDetailsActivity.VEHICLE, invoiceList[adapterPosition].jobCard?.vehicle)
                }
                Invoice.STATUS_INVOICED, Invoice.STATUS_PAID_PARTIAL -> {
                    bundle.putString(MoreCtaListDialogFragment.ARG_INVOICE_ID, invoiceList[adapterPosition].id)
                    bundle.putString(MoreCtaListDialogFragment.ARG_JOB_CARD_ID, invoiceList[adapterPosition].jobCard?.id)
                    bundle.putString(MoreCtaListDialogFragment.ARG_DISPLAY_ID, invoiceList[adapterPosition].jobCard?.jobCardId)
                    bundle.putString(AddCustomerActivity.ARG_CUSTOMER_ID, invoiceList[adapterPosition].jobCard?.customer?.id)
                    bundle.putString(AddCustomerActivity.ARG_TYPE, AddCustomerActivity.ARG_VIEW)
                    bundle.putString(VehicleDetailsActivity.ARG_TYPE, VehicleDetailsActivity.ARG_VIEW)
                    bundle.putParcelable(VehicleDetailsActivity.VEHICLE, invoiceList[adapterPosition].jobCard?.vehicle)
                    bundle.putParcelable(MoreCtaListDialogFragment.ARG_FEEDBACK, invoiceList[adapterPosition].jobCard?.feedback)
                }
                Invoice.STATUS_PAID, Invoice.STATUS_CANCEL -> {
                    bundle.putString(AddCustomerActivity.ARG_CUSTOMER_ID, invoiceList[adapterPosition].jobCard?.customer?.id)
                    bundle.putString(AddCustomerActivity.ARG_TYPE, AddCustomerActivity.ARG_VIEW)
                    bundle.putString(VehicleDetailsActivity.ARG_TYPE, VehicleDetailsActivity.ARG_VIEW)
                    bundle.putParcelable(VehicleDetailsActivity.VEHICLE, invoiceList[adapterPosition].jobCard?.vehicle)
                    bundle.putParcelable(MoreCtaListDialogFragment.ARG_FEEDBACK, invoiceList[adapterPosition].jobCard?.feedback)
                    bundle.putString(MoreCtaListDialogFragment.ARG_JOB_CARD_ID, invoiceList[adapterPosition].jobCard?.id)
                    bundle.putString(MoreCtaListDialogFragment.ARG_DISPLAY_ID, invoiceList[adapterPosition].jobCard?.jobCardId)
                }
            }
            bundle.putString(MoreCtaListDialogFragment.ARG_TYPE, type)
            val modalBottom = MoreCtaListDialogFragment.newInstance(invoiceList[adapterPosition].status
                    ?: "", bundle, null)
            val fragmentManager = (view.context as FragmentActivity).supportFragmentManager
            modalBottom.show(fragmentManager, "some")
        }

        private fun startViewJcActivity() {
            view.context.startActivity(ViewJCActivity.getViewJcIntent(view.context, invoiceList[adapterPosition].jobCard?.id!!, invoiceList[adapterPosition].jobCard?.jobCardId!!, true, false, invoiceList[adapterPosition].jobCard?.vehicleType))
        }
    }

    fun updateList(updateList: List<Invoice>) {
        val oldSize = itemCount
        invoiceList.addAll(updateList)
        notifyItemRangeInserted(oldSize, itemCount)
    }

    companion object {
        const val type = "INVOICE"
    }
}