package com.carworkz.dearo.amc.amcsolddetails

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.CreateJobCardActivity
import com.carworkz.dearo.addjobcard.createjobcard.customercarsearch.CustomerCarSearchActivity
import com.carworkz.dearo.addjobcard.quickjobcard.QuickJobCardActivity
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.cardslisting.CardListingInteractionProvider
import com.carworkz.dearo.cardslisting.adapters.AMCJobCardListingAdapter
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.databinding.ActivityAmcDetailsBinding
import com.carworkz.dearo.databinding.ActivitySoldAmcDetailsBinding
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.extensions.toast
import com.carworkz.dearo.interactionprovider.ToolBarInteractionProvider
import com.carworkz.dearo.mycustomers.cutomervehiclehistory.CustomerVehicleHistoryActivity
import com.carworkz.dearo.pdf.Source
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.screencontainer.SingleTextActionScreenContainer
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.activity_sold_amc_details.**/
import java.util.*
import javax.inject.Inject

class AmcSoldDetailsActivity : ScreenContainerActivity(),
    ToolBarInteractionProvider,
    AmcDetailsContract.View,
    View.OnClickListener, CardListingInteractionProvider {
    private lateinit var binding: ActivitySoldAmcDetailsBinding
    @Inject
    lateinit var presenter: AmcDetailsPresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    @Inject
    lateinit var pdfMediator: PdfMediator

    private lateinit var amc: AMC
    private var delay = 0L
    private var isExpiredOrCancelled=false
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        amc = intent.extras?.get(ARG_AMC) as AMC
        initComponent()
        super.onCreate(savedInstanceState)
        screenTracker.sendScreenEvent(
            this,
            ScreenTracker.SCREEN_CUSTOMER_CARD_DETAILS,
            this.javaClass.name
        )
        binding.tvJobListingMakeModel.text =
            "${amc.vehicle.make.name} - ${amc.vehicle.model.name} - ${amc.vehicle.fuelType}"
        binding.tvRegNoCustName.text = "${amc.vehicle.registrationNumber}, ${amc.customer.name}"
        binding.amcName.text = amc.amcName
        binding.amcExpiry.text = Utility.formatDate(
            amc.expiryDate,
            Utility.TIMESTAMP,
            Utility.DATE_FORMAT_4,
            Utility.TIMEZONE_UTC
        )
        binding.amcAmount.text = Utility.convertToCurrency(amc.amount)
        setActions()

        isExpiredOrCancelled = (Utility.dateToCalender(
            amc.expiryDate,
            Utility.TIMESTAMP
        ).time < Calendar.getInstance().time) or  (amc.status==AMC.AMC_CANCELLED)

        if(isExpiredOrCancelled)
        {
            binding.createJobCard.text="Renew AMC"
        }

        presenter.getRedemptionDetails(amc.id)
    }


    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == CreateJobCardActivity.RESULT_CODE && requestCode == REQUEST_CODE) {
            delay = 1000L
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.createJobCard -> {
                if (isExpiredOrCancelled) {
                    // TODO: 28/1/21 renew AMC
                    toast("Coming soon!")
                }
                else {
                    startActivity(
                        CustomerCarSearchActivity.getIntent(
                            this,
                            amc.vehicle.registrationNumber,
                            amc.customer.mobile,
                            null
                        )
                    )
                }
            }

            binding.viewInvoice -> {
//                pdfMediator.startJobCardEstimatePdf(this, amc.!!, displayJobCardId!!)
                pdfMediator.startAmcInvoicePdf(this, amc.invoice?.pdf)
            }
        }
    }

    override fun createScreenContainer(): ScreenContainer {
        return SingleTextActionScreenContainer(this)
    }

    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivitySoldAmcDetailsBinding.inflate(layoutInflater)
        return binding
    }

    override fun getToolBarTitle(): String = amc.amcNumber

    override fun getActionBtnTitle(): String = ""

    override fun onActionBtnClick() = Unit

    override fun getProgressView(): View = ProgressBar(this)

    override fun showProgressIndicator() = super.showProgressBar()

    override fun dismissProgressIndicator() = super.dismissProgressBar()

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun showDetails(amcDetails: SoldAMCDetails) {
        if (amcDetails.services.isNotEmpty()) {
            binding.benefitsRecyclerView.visibility = View.VISIBLE
            binding.benefitsRecyclerView.adapter = AmcServiceRedemptionAdapter(amcDetails.services)
        }
        if (amcDetails.jobcards.isNotEmpty()) {
            binding.jobCardRecyclerView.visibility = View.VISIBLE
            binding.jobCardRecyclerView.adapter = AMCJobCardListingAdapter(this, amcDetails.jobcards, this)
        }
    }

    override fun moveToNextScreen(obj: JobCard) {
        obj.vehicle = amc.vehicle

        if (SharedPrefHelper.isQuickFlow()) {
            startActivityForResult(
                QuickJobCardActivity.getIntent(
                    this,
                    obj.status != JobCard.STATUS_INITIATED,
                    false,
                    true,
                    obj.vehicle.registrationNumber,
                    obj
                ),
                CustomerVehicleHistoryActivity.REQUEST_CODE
            )
        } else {
            val startCreateJobCardIntent = Intent(this, CreateJobCardActivity::class.java)
            startCreateJobCardIntent.putExtra(CreateJobCardActivity.ARG_JOB_CARD, obj)
            startCreateJobCardIntent.putExtra(
                CreateJobCardActivity.ARG_VEHICLE_ID,
                obj.vehicle.registrationNumber
            )
            startCreateJobCardIntent.putExtra(
                CreateJobCardActivity.ARG_ISHISTORY,
                CustomerVehicleHistoryActivity.ARG_HISTORY
            )
            if (obj.status == JobCard.STATUS_INITIATED)
                startCreateJobCardIntent.putExtra(CreateJobCardActivity.ARG_IS_VIEW_ONLY, false)
            else {
                startCreateJobCardIntent.putExtra(CreateJobCardActivity.ARG_IS_VIEW_ONLY, true)
            }
            startActivityForResult(
                startCreateJobCardIntent,
                CustomerVehicleHistoryActivity.REQUEST_CODE
            )
        }
    }

    override fun serviceDateError() {

    }

    override fun showSuccess(obj: AMC) {
    }

    private fun setActions() {

        binding.viewInvoice.setOnClickListener(this)
        binding.createJobCard.setOnClickListener(this)
    }

    private fun initComponent() {
        (application as DearOApplication)
            .repositoryComponent
            .COMPONENT(AmcDetailsPresenterModule(this))
            .inject(this)
    }

    companion object {
        const val ARG_AMC = "AMC"
        const val ARG_HISTORY = true
        const val REQUEST_CODE = 1000

        fun getIntent(context: Context, amc: AMC): Intent {
            return Intent(context, AmcSoldDetailsActivity::class.java).apply {
                putExtra(ARG_AMC, amc)
            }
        }
    }


    override fun acceptAppointment(appointment: Appointment) {

    }

    override fun updateLeadStatus(appointment: Appointment) {

    }

    override fun rescheduleAppointment(appointmentId: String) {

    }

    override fun callCancelJobCard(jobCardId: String) {

    }

    override fun callCompleteJobCard(jobCard: JobCard) {

    }

    override fun callCloseJobCard(jobCardId: String) {

    }

    override fun callCreateJobCard(appointment: Appointment) {

    }

    override fun callUpdatePayment(invoiceId: String, displayInvoiceId: String, jobCardId: String) {

    }

    override fun getCardType(): String {
        return ""
    }

    override fun startEditProformaActivity(
        invoice: Invoice,
        jobCardId: String?,
        displayJcId: String?,
        invoiceId: String?,
        displayInvoiceId: String?,
        splitInvoice: Boolean,
        vehicleType: String?,
        jcType: String,
        requestCode: Int
    ) {
    }

    override fun startOtcProformaActivity(
        invoiceId: String,
        displayInvoiceId: String,
        vehicleType: String?
    ) {
    }

    override fun getJobCardById(id: String) {
        presenter.getJobCard(id)
    }

    override fun startInvoicePreview(invoice: Invoice, jobCardId: String, source: Source) {
        pdfMediator.startInvoicePreviewPdf(this, invoice, jobCardId, source)
    }

    override fun startProformaPdf(invoice: Invoice, jobCardId: String, source: Source) {

    }

    override fun startOtcInvoicePreview(invoice: Invoice) {
    }

    override fun startJobCardDetailsPreview(jobCardId: String, displayId: String, source: Source) {
    }

    override fun getAmcId(): String? {
        return amc.id
    }
}
