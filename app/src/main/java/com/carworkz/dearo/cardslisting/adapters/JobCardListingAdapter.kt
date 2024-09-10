package com.carworkz.dearo.cardslisting.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.addcustomer.AddCustomerActivity
import com.carworkz.dearo.addjobcard.addeditvehicle.VehicleDetailsActivity
import com.carworkz.dearo.addjobcard.createjobcard.CreateJobCardActivity
import com.carworkz.dearo.addjobcard.quickjobcard.QuickJobCardActivity
import com.carworkz.dearo.base.DialogFactory
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.cardslisting.CardListingBaseAdapter
import com.carworkz.dearo.cardslisting.CardListingBaseViewHolder
import com.carworkz.dearo.cardslisting.CardListingFragment
import com.carworkz.dearo.cardslisting.CardListingInteractionProvider
import com.carworkz.dearo.carpm.CarPmAuthActivity
import com.carworkz.dearo.carpm.ClearCodeActivity
import com.carworkz.dearo.customerfeedback.NewCustomerFeedBackActivity
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.domain.entities.Address
import com.carworkz.dearo.domain.entities.Invoice
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.domain.entities.Role
import com.carworkz.dearo.events.CancelEvent
import com.carworkz.dearo.extensions.image
import com.carworkz.dearo.extensions.textColor
import com.carworkz.dearo.morecta.MoreCtaListDialogFragment
import com.carworkz.dearo.pdf.Source
import com.carworkz.dearo.utils.Utility
import java.util.Locale

/**
 * Created by farhan on 03/01/18.
 */
class JobCardListingAdapter(
    private val context: Context,
    private val jobCardList: MutableList<JobCard>,
    private val interaction: CardListingInteractionProvider,
) : CardListingBaseAdapter<JobCardListingAdapter.JobCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobCardViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.row_new_job_card_listing, parent, false
        )
        return JobCardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return jobCardList.size
    }

    fun updateList(updateList: List<JobCard>) {
        val oldSize = itemCount/*updateList.forEach { jobCardList.add(it)
        notifyItemInserted(oldSize+1)}*/
        jobCardList.addAll(updateList)
        notifyItemRangeInserted(oldSize, itemCount)
    }

    override fun onBindViewHolder(holder: JobCardViewHolder, position: Int) {
        val jobCard = jobCardList[position]


        when (jobCard.status) {
            JobCard.STATUS_INITIATED -> holder.initInitiatedView()

            JobCard.STATUS_IN_PROGRESS -> {
                holder.initInProgressView()
                if (jobCard.isCarpmIssueFixed) {
                    holder.isCarpmScanned.visibility = View.VISIBLE
                } else {
                    holder.isCarpmScanned.visibility = View.GONE
                }
            }

            JobCard.STATUS_COMPLETED -> {
                holder.initCompletedView()
                if (jobCard.isCarpmIssueFixed) {
                    holder.isCarpmScanned.visibility = View.VISIBLE
                } else {
                    holder.isCarpmScanned.visibility = View.GONE
                }
            }

            JobCard.STATUS_CLOSED -> holder.initClosedView()
            JobCard.STATUS_CANCELLED -> holder.initCancelledView()
        }


        holder.callView.tag = jobCard.customer?.mobile
        val chipText = " <b>${jobCard.type}</b> : ${jobCard.stepLabel} "
        holder.chipView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(chipText, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(chipText)
        }
        when (jobCard.type) {
            JobCard.TYPE_ACCIDENTAL -> {
                (holder.chipView.background as? GradientDrawable)?.setColor(
                    ContextCompat.getColor(
                        context, R.color.colorAccidental
                    )
                )
                holder.chipView.textColor = ContextCompat.getColor(
                    context, R.color.colorAccidentalText
                )
            }

            JobCard.TYPE_MAJOR -> {
                (holder.chipView.background as? GradientDrawable)?.setColor(
                    ContextCompat.getColor(
                        context, R.color.colorMajor
                    )
                )
                holder.chipView.textColor = ContextCompat.getColor(context, R.color.colorMajorText)
            }

            JobCard.TYPE_MINOR -> {
                (holder.chipView.background as? GradientDrawable)?.setColor(
                    ContextCompat.getColor(
                        context, R.color.colorMinor
                    )
                )
                holder.chipView.textColor = ContextCompat.getColor(context, R.color.colorMinorText)
            }

            JobCard.TYPE_PERIODIC -> {
                (holder.chipView.background as? GradientDrawable)?.setColor(
                    ContextCompat.getColor(
                        context, R.color.colorPeriodic
                    )
                )
                holder.chipView.textColor = ContextCompat.getColor(
                    context, R.color.colorPeriodicText
                )
            }

            else -> {
                (holder.chipView.background as? GradientDrawable)?.setColor(
                    ContextCompat.getColor(
                        context, R.color.light_grey
                    )
                )
                holder.chipView.textColor = ContextCompat.getColor(
                    context, R.color.textColorPrimary
                )
            }
        }
        holder.parentView.tag = jobCard
        holder.secondaryCtaParent.tag = jobCard
        holder.primaryCtaParent.tag = jobCard
        holder.idView.text = StringBuilder().apply {
            append("\u2022 ")
            append(jobCard.jobCardId)
        }

        holder.titleView.text = jobCard.vehicle.registrationNumber

        holder.makeModelView.text = StringBuilder().apply {
            // Used StringBuilder to avoid lint warning
            append(jobCard.vehicle.make.name)
            append("-")
            append(jobCard.vehicle.model.name)
            append("-")
            append(jobCard.vehicle.fuelType)
        }

        holder.nameView.text = jobCard.customer?.name?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }

        if (jobCard.status == JobCard.STATUS_IN_PROGRESS || jobCard.status == JobCard.STATUS_CLOSED && jobCard.estimate != null) {
            holder.timeRemainingView.text =
                Utility.newTimeRemaining(jobCard.estimate.deliveryDateTime)
            when {
                Utility.newTimeRemaining(jobCard.estimate.deliveryDateTime)
                    .contains("remaining") -> holder.timeRemainingView.setTextColor(
                    ContextCompat.getColor(
                        context, R.color.forest_green
                    )
                )

                Utility.newTimeRemaining(jobCard.estimate.deliveryDateTime)
                    .contains("ago") -> holder.timeRemainingView.setTextColor(
                    ContextCompat.getColor(
                        context, R.color.persion_red
                    )
                )

                Utility.newTimeRemaining(jobCard.estimate.deliveryDateTime).contains("Now") -> {
                    holder.timeRemainingView.setTextColor(
                        ContextCompat.getColor(
                            context, R.color.colorPrimary
                        )
                    )
                    holder.timeRemainingView.text =
                        context.getString(R.string.card_listing_time_remaining_now)
                }

                else -> holder.timeRemainingView.setTextColor(
                    ContextCompat.getColor(
                        context, R.color.persion_red
                    )
                )
            }

            if (jobCard.estimate.maxCost != 0) holder.moneyEstimateView.text =
                StringBuilder().apply {
                    append(Utility.convertToCurrency(jobCard.estimate.minCost.toDouble()))
                    append(" - ")
                    append(Utility.convertToCurrency(jobCard.estimate.maxCost.toDouble()))
                }
            else {
                holder.moneyEstimateView.text =
                    Utility.convertToCurrency(jobCard.estimate.minCost.toDouble())
            }
        }

        if (jobCard.status == JobCard.STATUS_COMPLETED && jobCard.estimate != null) {
            holder.timeRemainingView.text = Utility.timeRemaining(jobCard.completionDate)
            when {
                Utility.timeRemaining(jobCard.completionDate)
                    .contains("remaining") -> holder.timeRemainingView.setTextColor(
                    ContextCompat.getColor(
                        context, R.color.forest_green
                    )
                )

                Utility.timeRemaining(jobCard.completionDate)
                    .contains("ago") -> holder.timeRemainingView.setTextColor(
                    ContextCompat.getColor(
                        context, R.color.persion_red
                    )
                )

                Utility.timeRemaining(jobCard.completionDate).contains("Now") -> {
                    holder.timeRemainingView.setTextColor(
                        ContextCompat.getColor(
                            context, R.color.colorPrimary
                        )
                    )
                    holder.timeRemainingView.text =
                        context.getString(R.string.card_listing_time_remaining_now)
                }

                else -> holder.timeRemainingView.setTextColor(
                    ContextCompat.getColor(
                        context, R.color.persion_red
                    )
                )
            }
            if (jobCard.invoice.summary?.totalAmountAfterTax != null && jobCard.invoice.summary?.totalAmountAfterTax.toString() != "0.0") {
                holder.moneyImageView.visibility = View.VISIBLE

                holder.moneyEstimateView.text =
                    Utility.convertToCurrency(jobCard.invoice.summary?.totalAmountAfterTax)
            } else {
                holder.moneyEstimateView.visibility = View.GONE
                holder.moneyImageView.visibility = View.GONE
            }
        }
    }

    override fun removeItemAndRefresh(id: String) {
        val index = jobCardList.indexOf(jobCardList.find { it.id == id })
        if (index != -1) {
            jobCardList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    inner class JobCardViewHolder(view: View) : CardListingBaseViewHolder(view) {

        private val context = view.context

        init {
            parentView.setOnClickListener { v ->
                startCreateJobCardActivity(v, false)
            }
            moreActionView.visibility = View.VISIBLE
        }

        internal fun initInitiatedView() {
            primaryCtaImageView.image = AppCompatResources.getDrawable(
                context, R.drawable.ic_continue_jobcard
            )
            secondaryCtaImageView.image = AppCompatResources.getDrawable(
                context, R.drawable.ic_cancel_jobcard
            )

            secondaryCtaParent.visibility =
                if (SharedPrefHelper.getApproval()) View.GONE else if (SharedPrefHelper.getUserRole()
                        .equals(
                            Role.USER_ROLE_ADMIN, true
                        )
                ) View.VISIBLE else View.GONE
            primaryCtaParent.visibility = View.VISIBLE

            timeImageView.visibility = View.GONE
            timeRemainingView.visibility = View.GONE
            moneyImageView.visibility = View.GONE
            moneyEstimateView.visibility = View.GONE

            primaryCtaView.text = context.getString(R.string.continue_job)
            secondaryCtaView.text = context.getString(R.string.cancel_job)
            secondaryCtaParent.setOnClickListener {
                if (adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener

                EventsManager.post(
                    CancelEvent(
                        CancelEvent.JOBCARD, jobCardList[adapterPosition].id
                    )
                )
                // Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show()
            }
            primaryCtaParent.setOnClickListener { v ->
                startCreateJobCardActivity(v, false)
            }
            moreActionView.setOnClickListener {
                showCtaSheet()
            }
        }

        internal fun initInProgressView() {
            secondaryCtaParent.visibility = View.VISIBLE

            timeRemainingView.visibility = View.VISIBLE
            timeImageView.visibility = View.VISIBLE
            moneyEstimateView.visibility = View.VISIBLE
            moneyImageView.visibility = View.VISIBLE
            primaryCtaParent.visibility = View.VISIBLE

            primaryCtaImageView.setImageResource(R.drawable.ic_job_complete)

            // primaryCtaImageView.image = VectorDrawableCompat.create(context.resources, R.drawable.ic_job_complete, null)
            secondaryCtaImageView.image = AppCompatResources.getDrawable(
                context, R.drawable.ic_job_add
            )

            primaryCtaView.text = context.getString(R.string.job_complete)
            secondaryCtaView.text = context.getString(R.string.add_job)

            primaryCtaParent.setOnClickListener {
                if (SharedPrefHelper.isPreDeliveryCheckEnabled().not()) {
                    interaction.callCompleteJobCard(jobCardList[adapterPosition])
                } else if (SharedPrefHelper.isPreDeliveryCheckEnabled() and jobCardList[adapterPosition].isPdcCompleted) {
                    interaction.callCompleteJobCard(jobCardList[adapterPosition])
                } else {
                    DialogFactory.createGenericErrorDialog(
                        context, "Pre-Delivery check is not completed"
                    ).show()
                }
            }
            secondaryCtaParent.setOnClickListener { v ->
                startCreateJobCardActivity(v, true)
            }
            moreActionView.setOnClickListener {
                showCtaSheet()
            }
        }

        internal fun initCompletedView() {
            timeRemainingView.visibility = View.VISIBLE
            timeImageView.visibility = View.VISIBLE
            moneyEstimateView.visibility = View.VISIBLE
            moneyImageView.visibility = View.VISIBLE
            primaryCtaParent.visibility = View.VISIBLE
            moreActionView.setOnClickListener {
                showCtaSheet()
            }
            if (Invoice.STATUS_PROFORMA == jobCardList[adapterPosition].invoice.status) {
                secondaryCtaParent.visibility = View.GONE
                primaryCtaImageView.image = AppCompatResources.getDrawable(
                    context, R.drawable.ic_invoice_edit
                )
                primaryCtaView.text = context.getString(R.string.edit_parts_and_labor)
                primaryCtaParent.setOnClickListener {
                    val invoice = jobCardList[adapterPosition].invoice
                    interaction.startEditProformaActivity(
                        invoice,
                        jobCardList[adapterPosition].id,
                        jobCardList[adapterPosition].jobCardId,
                        invoice.id,
                        invoice.invoiceId,
                        invoice.splitInvoice,
                        invoice.vehicleType,
                        jobCardList[adapterPosition].type,
                        CardListingFragment.NEW_ESTIMATOR_JC_REQUEST_CODE
                    )
                }
            } else {
                //   secondaryCtaParent.visibility = View.VISIBLE
                primaryCtaImageView.image = AppCompatResources.getDrawable(
                    context, R.drawable.ic_invoice_view
                )
                secondaryCtaImageView.image = AppCompatResources.getDrawable(
                    context, R.drawable.ic_payment_black_24dp
                )
                primaryCtaView.text = context.getString(R.string.view_invoice)
                secondaryCtaView.text = context.getString(R.string.update_payment)
                primaryCtaParent.setOnClickListener {
                    if (adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener

                    interaction.startInvoicePreview(
                        jobCardList[adapterPosition].invoice,
                        jobCardList[adapterPosition].id,
                        Source.COMPLETED
                    )

//                    startInvoicePreviewPDFActivity(jobCardList[adapterPosition].id, jobCardList[adapterPosition].invoice.id
//                            ?: ""
//                            , jobCardList[adapterPosition].invoice.invoiceId ?: "",
//                            jobCardList[adapterPosition].invoice.splitInvoice && jobCardList[adapterPosition].invoice.summary?.split?.insurancePay?.totalAmountAfterTax!! > 0)
                }
                secondaryCtaParent.setOnClickListener {
                    if (adapterPosition != -1) {
                        interaction.callUpdatePayment(
                            jobCardList[adapterPosition].invoice.id!!,
                            jobCardList[adapterPosition].invoice.invoiceId!!,
                            jobCardList[adapterPosition].id
                        )
                    }
                }
            }
        }

        internal fun initClosedView() {
            timeRemainingView.visibility = View.GONE
            timeImageView.visibility = View.GONE
            moneyEstimateView.visibility = View.GONE
            moneyImageView.visibility = View.GONE
            primaryCtaParent.visibility = View.VISIBLE
//            ImageViewCompat.setImageTintList(primaryCtaImageView, null)

            if (Invoice.STATUS_DUE == jobCardList[adapterPosition].invoice.status) {
                // secondaryCtaParent.visibility = View.VISIBLE
                secondaryCtaView.text = context.getString(R.string.update_payment)
                secondaryCtaParent.setOnClickListener {
                    Toast.makeText(context, "Update Payment", Toast.LENGTH_SHORT).show()
                }
            } else {
                secondaryCtaParent.visibility = View.GONE
            }

            when {
                jobCardList[adapterPosition].invoice.status == Invoice.STATUS_INVOICED -> {
                    // primaryCtaParent.visibility = View.VISIBLE
                    primaryCtaView.text = context.getString(R.string.view_invoice)
                    primaryCtaParent.setOnClickListener {
                        interaction.startInvoicePreview(
                            jobCardList[adapterPosition].invoice,
                            jobCardList[adapterPosition].id,
                            Source.DEFAULT
                        )
//                        startPdfActivity(jobCardList[adapterPosition].id, jobCardList[adapterPosition].invoice.id
//                                ?: "", jobCardList[adapterPosition].invoice.invoiceId ?: "")
                    }
                }

                jobCardList[adapterPosition].status == JobCard.STATUS_CLOSED -> {
                    if (jobCardList[adapterPosition].feedback != null) {
                        primaryCtaView.text =
                            context.getString(R.string.card_listing_btn_view_feedback)
                        primaryCtaImageView.image = AppCompatResources.getDrawable(
                            context, R.drawable.ic_feedback_black_24dp
                        )
                        primaryCtaParent.setOnClickListener {
                            context.startActivity(
                                NewCustomerFeedBackActivity.getIntent(
                                    context,
                                    jobCardList[adapterPosition].feedback,
                                    jobCardList[adapterPosition].id
                                )
                            )
                        }
                    } else {
                        Utility.setVisibility(false, ctaParentView)
                    }

                }
            }

            moreActionView.setOnClickListener {
                showCtaSheet()
            }
        }

        internal fun initCancelledView() {
            primaryCtaParent.visibility = View.GONE
            secondaryCtaParent.visibility = View.GONE
            moreActionView.setOnClickListener {
                showCtaSheet()
            }
        }

        private fun startCreateJobCardActivity(v: View, isAddJob: Boolean) {
            val jobCard = v.tag as JobCard
            val startCreateJobCardIntent: Intent

            if (SharedPrefHelper.isQuickFlow()) {
                startCreateJobCardIntent = QuickJobCardActivity.getIntent(
                    context,
                    jobCardList[adapterPosition].status != JobCard.STATUS_INITIATED,
                    isAddJob,
                    false,
                    jobCard.vehicle.registrationNumber,
                    jobCard
                )
            } else {
                Log.v("chekkkkkk", jobCard.toString())
                startCreateJobCardIntent =
                    Intent(context, CreateJobCardActivity::class.java).apply {
                        putExtra(CreateJobCardActivity.ARG_JOB_CARD, jobCard)
                        putExtra(
                            CreateJobCardActivity.ARG_VEHICLE_ID, jobCard.vehicle.registrationNumber
                        )
                        putExtra(CreateJobCardActivity.ARG_IS_ADD_JOB, isAddJob)
                        putExtra(CreateJobCardActivity.VEHICLE_AMC_ID, interaction.getAmcId())
                        putExtra(
                            CreateJobCardActivity.ARG_IS_VIEW_ONLY,
                            jobCardList[adapterPosition].status != JobCard.STATUS_INITIATED
                        )
                    }

                /* startCreateJobCardIntent = Intent(context, CreateJobCardActivity::class.java)
                 startCreateJobCardIntent.putExtra(CreateJobCardActivity.ARG_JOB_CARD, jobCard)
                 startCreateJobCardIntent.putExtra(
                     CreateJobCardActivity.ARG_VEHICLE_ID, jobCard.vehicle.registrationNumber
                 )
                 startCreateJobCardIntent.putExtra(CreateJobCardActivity.ARG_IS_ADD_JOB, isAddJob)
                 startCreateJobCardIntent.putExtra(
                     CreateJobCardActivity.VEHICLE_AMC_ID, interaction.getAmcId()
                 )
                 if (jobCardList[adapterPosition].status == JobCard.STATUS_INITIATED) startCreateJobCardIntent.putExtra(
                     CreateJobCardActivity.ARG_IS_VIEW_ONLY, false
                 )
                 else {
                     startCreateJobCardIntent.putExtra(CreateJobCardActivity.ARG_IS_VIEW_ONLY, true)
                 }*/
            }
            (context as? Activity)?.startActivityForResult(
                startCreateJobCardIntent, CardListingFragment.REQUEST_CODE
            )
        }

        private fun showCtaSheet() {
            val bundle = Bundle()
            val jobCard = jobCardList[adapterPosition]
            jobCard.customer.address = ArrayList<Address>()
            jobCard.customer.address.add(jobCard.address)
            when (jobCard.status) {
                JobCard.STATUS_INITIATED -> {
                    bundle.putString(AddCustomerActivity.ARG_CUSTOMER_ID, jobCard.customer.id)
                    bundle.putString(AddCustomerActivity.ARG_TYPE, AddCustomerActivity.ARG_VIEW)
                    bundle.putString(
                        VehicleDetailsActivity.ARG_TYPE, VehicleDetailsActivity.ARG_VIEW
                    )
                    bundle.putParcelable(VehicleDetailsActivity.VEHICLE, jobCard.vehicle)
                }

                JobCard.STATUS_IN_PROGRESS -> {
                    Log.d(
                        "Cheeeeeekkkkk",
                        jobCard.id + "----" + jobCard.vehicle.registrationNumber + "----" + jobCard.vehicle.makeName + "---" + jobCard.vehicle.makeSlug + "----" + jobCard.isCarpmIssueFixed + "----" + jobCard.carpmScanId
                    )
                    bundle.putString(MoreCtaListDialogFragment.ARG_JOB_CARD_ID, jobCard.id)
                    if (jobCard.carpmScanId == null || jobCard.carpmScanId.equals("null") || jobCard.carpmScanId.isEmpty() || jobCard.carpmScanId.equals(
                            ""
                        )
                    ) {
                        bundle.putString(MoreCtaListDialogFragment.ARG_CARPM_SCAN_ID, "")
                        bundle.putString(MoreCtaListDialogFragment.ARG_CARPM_SCAN_ID, "")
                        bundle.putString(CarPmAuthActivity.ARG_SCAN_ID, "")
                    } else {
                        bundle.putString(
                            MoreCtaListDialogFragment.ARG_CARPM_SCAN_ID, jobCard.carpmScanId
                        )
                        bundle.putString(
                            MoreCtaListDialogFragment.ARG_CARPM_SCAN_ID, jobCard.carpmScanId
                        )
                        bundle.putString(CarPmAuthActivity.ARG_SCAN_ID, jobCard.carpmScanId)
                    }


                    bundle.putBoolean(
                        MoreCtaListDialogFragment.ARG_IS_CARPM_SCANNED, jobCard.isCarpmIssueFixed
                    )
                    bundle.putString(
                        MoreCtaListDialogFragment.ARG_DISPLAY_ID,
                        jobCard.jobCardId + "-" + jobCard.vehicle.registrationNumber
                    )
                    bundle.putString(AddCustomerActivity.ARG_CUSTOMER_ID, jobCard.customer.id)
                    bundle.putString(AddCustomerActivity.ARG_TYPE, AddCustomerActivity.ARG_VIEW)
                    bundle.putString(
                        VehicleDetailsActivity.ARG_TYPE, VehicleDetailsActivity.ARG_VIEW
                    )
                    bundle.putByte(
                        MoreCtaListDialogFragment.ARG_IS_PDC_COMPLETED,
                        (if (jobCard.isPdcCompleted) 1 else 0).toByte()
                    )
                    bundle.putParcelable(VehicleDetailsActivity.VEHICLE, jobCard.vehicle)
                    bundle.putString(CarPmAuthActivity.ARG_JOB_ID, jobCard.id)
                    bundle.putString(
                        CarPmAuthActivity.ARG_REG_NO, jobCard.vehicle.registrationNumber
                    )
                    bundle.putString(CarPmAuthActivity.ARG_MAKE_NAME, jobCard.vehicle.makeName)
                    bundle.putBoolean(
                        CarPmAuthActivity.ARG_IS_CARPM_SCANNED, jobCard.isCarpmIssueFixed
                    )
                }

                JobCard.STATUS_COMPLETED -> {
                    bundle.putString(MoreCtaListDialogFragment.ARG_JOB_CARD_ID, jobCard.id)
                    if (jobCard.carpmScanId == null || jobCard.carpmScanId.equals("null") || jobCard.carpmScanId.isEmpty() || jobCard.carpmScanId.equals(
                            ""
                        )
                    ) {
                        bundle.putString(MoreCtaListDialogFragment.ARG_CARPM_SCAN_ID, "")
                    } else {
                        bundle.putString(
                            MoreCtaListDialogFragment.ARG_CARPM_SCAN_ID, jobCard.carpmScanId
                        )
                    }

                    bundle.putBoolean(
                        MoreCtaListDialogFragment.ARG_IS_CARPM_SCANNED, jobCard.isCarpmIssueFixed
                    )
                    bundle.putString(ClearCodeActivity.ARG_JOB_ID, jobCard.id)
                    if (jobCard.invoice.status == Invoice.STATUS_PROFORMA) {
                        val dummyJobCard = JobCard()
                        dummyJobCard.status = JobCard.STATUS_CLOSED
                        bundle.putParcelable(MoreCtaListDialogFragment.ARG_JOB_CARD, dummyJobCard)
                    }
//                    bundle.putStringArray(MoreCtaListDialogFragment.ARG_INVOICE_REMARKS, jobCard.remarks)
                    bundle.putString(MoreCtaListDialogFragment.ARG_DISPLAY_ID, jobCard.jobCardId)
                    bundle.putString(AddCustomerActivity.ARG_CUSTOMER_ID, jobCard.customer.id)
                    bundle.putString(AddCustomerActivity.ARG_TYPE, AddCustomerActivity.ARG_VIEW)
                    bundle.putString(
                        VehicleDetailsActivity.ARG_TYPE, VehicleDetailsActivity.ARG_VIEW
                    )
                    bundle.putString(
                        MoreCtaListDialogFragment.ARG_INVOICE_STATUS, jobCard.invoice.status
                    )
                    bundle.putParcelable(VehicleDetailsActivity.VEHICLE, jobCard.vehicle)

                    bundle.putByte(
                        MoreCtaListDialogFragment.ARG_IS_PDC_COMPLETED,
                        (if (jobCard.isPdcCompleted) 1 else 0).toByte()
                    )
                }

                JobCard.STATUS_CLOSED -> {
                    bundle.putString(AddCustomerActivity.ARG_CUSTOMER_ID, jobCard.customer.id)
                    bundle.putString(AddCustomerActivity.ARG_TYPE, AddCustomerActivity.ARG_VIEW)
                    bundle.putString(
                        VehicleDetailsActivity.ARG_TYPE, VehicleDetailsActivity.ARG_VIEW
                    )
                    bundle.putParcelable(VehicleDetailsActivity.VEHICLE, jobCard.vehicle)
                    bundle.putString(MoreCtaListDialogFragment.ARG_JOB_CARD_ID, jobCard.id)
                    bundle.putString(MoreCtaListDialogFragment.ARG_DISPLAY_ID, jobCard.jobCardId)
                }

                JobCard.STATUS_CANCELLED -> {
                    bundle.putString(AddCustomerActivity.ARG_CUSTOMER_ID, jobCard.customer.id)
                    bundle.putString(AddCustomerActivity.ARG_TYPE, AddCustomerActivity.ARG_VIEW)
                    bundle.putString(
                        VehicleDetailsActivity.ARG_TYPE, VehicleDetailsActivity.ARG_VIEW
                    )
                    bundle.putParcelable(VehicleDetailsActivity.VEHICLE, jobCard.vehicle)
                }
            }
            val modalBottom = MoreCtaListDialogFragment.newInstance(
                jobCard.status, bundle, jobCard.vehicleType
            )
            val fragmentManager = (context as FragmentActivity).supportFragmentManager
            modalBottom.show(fragmentManager, "some")
        }
    }
}