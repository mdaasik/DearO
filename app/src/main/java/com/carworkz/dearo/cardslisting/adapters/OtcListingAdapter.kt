package com.carworkz.dearo.cardslisting.adapters

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.addcustomer.AddCustomerActivity
import com.carworkz.dearo.cardslisting.CardListingBaseAdapter
import com.carworkz.dearo.cardslisting.CardListingBaseViewHolder
import com.carworkz.dearo.cardslisting.CardListingInteractionProvider
import com.carworkz.dearo.domain.entities.Address
import com.carworkz.dearo.domain.entities.Invoice
import com.carworkz.dearo.extensions.image
import com.carworkz.dearo.morecta.MoreCtaListDialogFragment
import com.carworkz.dearo.utils.Utility

class OtcListingAdapter(private val invoiceList: MutableList<Invoice>, private val interactionProvider: CardListingInteractionProvider) : CardListingBaseAdapter<OtcListingAdapter.OTCViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OTCViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_new_job_card_listing, parent, false)
        return OTCViewHolder(view)
    }

    override fun onBindViewHolder(holder: OTCViewHolder, position: Int) {
        val invoice = invoiceList[position]
        val jobCard = invoice.jobCard
        when (invoice.status) {
            Invoice.STATUS_PROFORMA -> holder.initProformaView()
            Invoice.STATUS_INVOICED -> holder.initInvoiceView()
        }
        holder.callView.tag = invoice.customer?.mobile
        holder.parentView.tag = invoice
        @SuppressLint("SetTextI18n")
        holder.idView.text = "\u2022 ${invoice.invoiceId}"
        holder.titleView.text = jobCard?.vehicle?.registrationNumber
        holder.makeModelView.text = "${invoice.customer?.name}"
        holder.nameView.text = "${invoice.customer?.mobile}"
        if (invoiceList[position].status == Invoice.STATUS_PROFORMA) {
            val timeRemaining = Utility.formatDate(invoice.proformaDate, Utility.TIMESTAMP, Utility.DATE_FORMAT_8, Utility.TIMEZONE_UTC)
            holder.timeImageView.visibility = View.VISIBLE
            holder.timeRemainingView.visibility = View.VISIBLE
            holder.timeRemainingView.text = timeRemaining
        }

        if (invoiceList[position].summary?.totalAmountAfterTax != null && invoiceList[position].summary?.totalAmountAfterTax!! > 0.0) {
            holder.moneyEstimateView.visibility = View.VISIBLE
            holder.moneyImageView.visibility = View.VISIBLE
            holder.moneyEstimateView.text = Utility.convertToCurrency(invoiceList[position].summary?.totalAmountAfterTax
                    ?: 0.0)
        } else {
            holder.moneyEstimateView.visibility = View.GONE
            holder.moneyImageView.visibility = View.GONE
        }

        if (invoiceList[position].status == Invoice.STATUS_INVOICED) {
            val timeRemaining = Utility.formatDate(invoice.date, Utility.TIMESTAMP, Utility.DATE_FORMAT_8, Utility.TIMEZONE_UTC)
            holder.timeImageView.visibility = View.VISIBLE
            holder.timeRemainingView.visibility = View.VISIBLE
            holder.timeRemainingView.text = timeRemaining
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

    inner class OTCViewHolder(private val view: View) : CardListingBaseViewHolder(view) {

        init {
            moreActionView.setOnClickListener { showCtaSheet() }
            moneyEstimateView.visibility = View.VISIBLE
            moneyImageView.visibility = View.VISIBLE
            moreActionView.visibility = View.VISIBLE
            ctaParentView.visibility = View.GONE
            dividerView.visibility = View.GONE
            chipView.visibility = View.GONE
            moneyEstimateView.visibility = View.GONE
            moneyImageView.visibility = View.GONE
            parentView.setOnClickListener {
                if (adapterPosition == RecyclerView.NO_POSITION)
                    return@setOnClickListener

                when (invoiceList[adapterPosition].status) {
                    Invoice.STATUS_INVOICED -> {
                        interactionProvider.startOtcInvoicePreview(invoiceList[adapterPosition])
                    }

                    Invoice.STATUS_PROFORMA -> {
                        val invoice = invoiceList[adapterPosition]
                        interactionProvider.startOtcProformaActivity(invoice.id!!, invoice.invoiceId!!, invoice.vehicleType)
                    }
                }
            }
        }

        internal fun initInvoiceView() {
            primaryCtaImageView.image = AppCompatResources.getDrawable(view.context, R.drawable.ic_jobcard_pdf)
            secondaryCtaImageView.image = AppCompatResources.getDrawable(view.context, R.drawable.ic_payment_black_24dp)
            secondaryCtaParent.visibility = View.GONE
            secondaryCtaImageView.visibility = View.GONE
            secondaryCtaView.visibility = View.GONE
            primaryCtaImageView.visibility = View.GONE
            primaryCtaParent.visibility = View.GONE
            titleView.visibility = View.GONE
            primaryCtaView.visibility = View.GONE
        }

        internal fun initProformaView() {
            primaryCtaImageView.image = AppCompatResources.getDrawable(view.context, R.drawable.ic_invoice_preview)
            secondaryCtaImageView.image = AppCompatResources.getDrawable(view.context, R.drawable.ic_job_view)
            secondaryCtaParent.visibility = View.GONE
            secondaryCtaImageView.visibility = View.GONE
            secondaryCtaView.visibility = View.GONE
            primaryCtaImageView.visibility = View.GONE
            primaryCtaParent.visibility = View.GONE
            primaryCtaView.visibility = View.GONE
            moreActionView.visibility = View.VISIBLE
            titleView.visibility = View.GONE
        }

        private fun showCtaSheet() {
            val bundle = Bundle()
            invoiceList[adapterPosition].jobCard?.customer?.address = ArrayList<Address>()
            invoiceList[adapterPosition].jobCard?.customer?.address?.add(invoiceList[adapterPosition].jobCard?.address)
            when (invoiceList[adapterPosition].status) {
                Invoice.STATUS_PROFORMA -> {
                    bundle.putString(AddCustomerActivity.ARG_CUSTOMER_ID, invoiceList[adapterPosition].customerId)
                    bundle.putString(AddCustomerActivity.ARG_TYPE, AddCustomerActivity.ARG_VIEW)
                }
                Invoice.STATUS_INVOICED -> {
                    bundle.putString(AddCustomerActivity.ARG_CUSTOMER_ID, invoiceList[adapterPosition].customerId)
                    bundle.putString(AddCustomerActivity.ARG_TYPE, AddCustomerActivity.ARG_VIEW)
                }
            }
            bundle.putString(MoreCtaListDialogFragment.ARG_TYPE, type)
            val modalBottom = MoreCtaListDialogFragment.newInstance(invoiceList[adapterPosition].status
                    ?: "", bundle, invoiceList[adapterPosition].vehicleType)
            val fragmentManager = (view.context as FragmentActivity).supportFragmentManager
            modalBottom.show(fragmentManager, "some")
        }
    }

    fun updateList(updateList: List<Invoice>) {
        val oldSize = itemCount
        invoiceList.addAll(updateList)
        notifyItemRangeInserted(oldSize, itemCount)
    }

    companion object {
        const val type = "OTC"
    }
}