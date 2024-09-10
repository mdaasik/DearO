package com.carworkz.dearo.addjobcard.quickjobcard

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.addcustomer.AddCustomerActivity
import com.carworkz.dearo.addjobcard.addeditvehicle.VehicleDetailsActivity
import com.carworkz.dearo.addjobcard.createjobcard.jobs.viewjc.ViewJCActivity
import com.carworkz.dearo.addjobcard.quickjobcard.quickconcerns.QuickConcernsFragment
import com.carworkz.dearo.addjobcard.quickjobcard.quickestimate.QuickEstimateFragment
import com.carworkz.dearo.addjobcard.quickjobcard.quickjobs.QuickJobFragment
import com.carworkz.dearo.addjobcard.quickjobcard.quicktopnav.QuickJcSectionActivity
import com.carworkz.dearo.addjobcard.quickjobcard.quicktopnav.QuickTopNavFragment
import com.carworkz.dearo.addjobcard.quickjobcard.quickvehiclereading.QuickVehicleReadingFragment
import com.carworkz.dearo.addjobcard.quickjobcard.quickviewjc.QuickViewJcFragment
import com.carworkz.dearo.base.DialogFactory
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.databinding.ActivityCustomerDetailsBinding
import com.carworkz.dearo.databinding.ActivityQuickJobCardBinding
import com.carworkz.dearo.domain.entities.Invoice
import com.carworkz.dearo.domain.entities.JobAndVerbatim
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.events.CardStatusChangeEvent
import com.carworkz.dearo.events.ChangeCardStatusEvent
import com.carworkz.dearo.exceptions.ValidationInputException
import com.carworkz.dearo.interactionprovider.SubtitleImgInteractionProvider
import com.carworkz.dearo.morecta.MoreCtaListDialogFragment
import com.carworkz.dearo.outwarding.OutwardingProcessActivity
import com.carworkz.dearo.outwarding.OutwardingProcessActivity.Companion.SCREEN_TYPE_PROFORMA
import com.carworkz.dearo.outwarding.helper.OutwardSection
import com.carworkz.dearo.outwarding.helper.OutwardStepMapper
import com.carworkz.dearo.pdf.Source
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.screencontainer.SubtitleImgScreenContainer
import com.carworkz.dearo.serviceremainder.NewServiceReminderActivity
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.activity_quick_job_card.*
import kotlinx.android.synthetic.main.base_layout.*
import kotlinx.android.synthetic.main.dialog_vertical_btn.view.**/
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class QuickJobCardActivity : ScreenContainerActivity(), QuickJobCardContract.View,
    SubtitleImgInteractionProvider, View.OnClickListener, EventsManager.EventSubscriber,
    NavigationInteraction, PmsInteractionProvider {
    private lateinit var binding: ActivityQuickJobCardBinding
    private lateinit var jobCard: JobCard
    private lateinit var vehicleRegNo: String
    private var isAddJob = false
    private var isViewOnly = false
    private var isHistory = false

    private var fragmentList: MutableList<Fragment> = mutableListOf()

    private var jobAndVerbatim: JobAndVerbatim? = null

    private var alertDialog: AlertDialog? = null
    private var calendar = Calendar.getInstance()

    @Inject
    lateinit var presenter: QuickJobCardPresenter

    @Inject
    lateinit var pdfMediator: PdfMediator
    private var vehicleAmcId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        getIntentData()
        super.onCreate(savedInstanceState)
        initComponent()
        // initBottomActions()
        if (checkIfNetworkAvailable()) {
            presenter.getData(jobCard.id)
        }
//        else {
//            initFragments()
//        }
    }

    override fun onResume() {
        super.onResume()
        EventsManager.register(this)
    }

    override fun onStop() {
        super.onStop()
        EventsManager.unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_SECTION -> {
                if (resultCode == Activity.RESULT_OK) {
                    when (jobCard.status) {
                        JobCard.STATUS_INITIATED -> {
                            presenter.getData(jobCard.id)
                        }

                        JobCard.STATUS_IN_PROGRESS -> {
                            presenter.getJobCardById(jobCard.id)
                        }
                    }
                }
            }

            REQUEST_CODE_CLOSE_JOBCARD -> {
                if (resultCode == Activity.RESULT_OK) {
                    finish()
                }
            }

            REQUEST_CODE_ESTIMATE_PROFORMA -> {
                presenter.getJobCardById(jobCard.id)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (jobCard.status == JobCard.STATUS_INITIATED || jobCard.status == JobCard.STATUS_IN_PROGRESS) {
                val listener = object : DialogFactory.NotifyButtonListener {
                    override fun positiveButton(notify: Boolean?) {
                        saveJobCard(NavigationInteraction.SCREEN_EXIT)
                    }

                    override fun neutralButton() {
                    }
                }
                val alert = DialogFactory.notifyAlertDialog(
                    this,
                    "Are you sure?",
                    "You have unsaved changes. Your changes will be lost if you exit without saving",
                    false,
                    false,
                    false,
                    listener
                )
                alert.setButton(
                    AlertDialog.BUTTON_POSITIVE,
                    "SAVE AND EXIT"
                ) { dialogInterface, i ->
                    listener.positiveButton(false)
                }
                alert.setButton(AlertDialog.BUTTON_NEGATIVE, "EXIT") { dialogInterface, i ->
                    finish()
                    dialogInterface.cancel()
                }
                alert.show()
            } else {
                finish()
            }
        }
        return true
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.bottomActionButton1 -> {
                if (jobCard.status == JobCard.STATUS_INITIATED || jobCard.status == JobCard.STATUS_IN_PROGRESS) {
                    saveJobCard(NavigationInteraction.SCREEN_ESTIMATE)
                } else {

                    saveJobCard(NavigationInteraction.SCREEN_PROFORMA)
                }
            }

            binding.bottomActionButton2 -> {
                when (jobCard.status) {
                    JobCard.STATUS_INITIATED -> {
                        initJobCard()
                    }

                    JobCard.STATUS_IN_PROGRESS -> {
                        completeJobCard()
                    }

                    JobCard.STATUS_COMPLETED -> {
                        // val isSplit = jobCard.invoice?.splitInvoice == true && jobCard.invoice?.summary?.split?.insurancePay?.totalAmountAfterTax!! > 0
                        pdfMediator.startInvoicePreviewPdf(
                            this,
                            jobCard.invoice,
                            jobCard.id,
                            Source.COMPLETED
                        )
                        // pdfMediator.startInvoicePreviewPdf(this, jobCard.invoice?.id!!, jobCard.id, jobCard.invoice?.invoiceId!!, isSplit, PdfMediator.OriginalState.COMPLETED)
//                        if (jobCard.invoice?.splitInvoice == true && jobCard.invoice?.summary?.split?.insurancePay?.totalAmountAfterTax!! > 0) {
//                            startActivity(SplitInvoicePdfActivity.getPdfIntent(this, SplitInvoicePdfActivity.COMPLETED, jobCard.invoice?.invoiceId!!, jobCard.invoice?.insuranceInvoiceId
//                                    ?: "", jobCard.invoice?.id!!, jobCard.id!!))
//                        } else {
//                            startActivity(PDFActivity.getPdfIntent(this, PDFActivity.COMPLETED, jobCard.invoice?.invoiceId!!, jobCard.invoice?.id!!, jobCard.id!!))
//                        }
                    }

                    JobCard.STATUS_CLOSED, JobCard.STATUS_CANCELLED -> {
                        // val isSplit = jobCard.invoice?.splitInvoice == true && jobCard.invoice?.summary?.split?.insurancePay?.totalAmountAfterTax!! > 0
                        pdfMediator.startInvoicePreviewPdf(
                            this,
                            jobCard.invoice,
                            jobCard.id,
                            Source.DEFAULT
                        )

                        // pdfMediator.startInvoicePreviewPdf(this, jobCard.invoice?.id!!, jobCard.id, jobCard.invoice?.invoiceId!!, isSplit, PdfMediator.OriginalState.DEFAULT)
//                        if (jobCard.invoice?.splitInvoice == true && jobCard.invoice?.summary?.split?.insurancePay?.totalAmountAfterTax!! > 0) {
//                            startActivity(SplitInvoicePdfActivity.getPdfIntent(this, SplitInvoicePdfActivity.PAID, jobCard.invoice?.invoiceId!!, jobCard.invoice?.insuranceInvoiceId
//                                    ?: "", jobCard.invoice?.id!!, jobCard.id!!))
//                        } else {
//                            startActivity(PDFActivity.getPdfIntent(this, PDFActivity.PAID, jobCard.invoice?.invoiceId!!, jobCard.invoice?.id!!, jobCard.id!!))
//                        }
                    }
                }
            }
        }
    }

    override fun createScreenContainer(): ScreenContainer = SubtitleImgScreenContainer(this)
    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityQuickJobCardBinding.inflate(layoutInflater)
       return binding
    }


    override fun getProgressView(): View = binding.baseLayout.pbMain

    override fun onStoryClick(screenToStart: String?) {
        when (screenToStart) {
            NavigationInteraction.SCREEN_VOICE -> {
                saveJobCard(screenToStart)
            }

            NavigationInteraction.SCREEN_INVENTORY -> {
                saveJobCard(screenToStart)
            }

            NavigationInteraction.SCREEN_INSPECTION -> {
                startScreen(screenToStart, jobCard)
            }

            NavigationInteraction.SCREEN_DAMAGES -> {
                startScreen(screenToStart, jobCard)
            }

            NavigationInteraction.SCREEN_INSURANCE -> {
                startScreen(screenToStart, jobCard)
            }
        }
    }

    override fun startScreen(screenToStart: String, jobCard: JobCard) {
        when (screenToStart) {
            NavigationInteraction.SCREEN_VOICE -> {
                startActivityForResult(
                    QuickJcSectionActivity.getIntent(
                        this,
                        isViewOnly,
                        QuickJcSectionActivity.SCREEN_SHOPFLOOR,
                        jobCard
                    ), REQUEST_CODE_SECTION
                )
            }

            NavigationInteraction.SCREEN_INVENTORY -> {
                this.startActivityForResult(
                    QuickJcSectionActivity.getIntent(
                        this,
                        isViewOnly,
                        QuickJcSectionActivity.SCREEN_INVENTORY,
                        jobCard
                    ), REQUEST_CODE_SECTION
                )
            }

            NavigationInteraction.SCREEN_INSPECTION -> {
                startActivityForResult(
                    QuickJcSectionActivity.getIntent(
                        this,
                        isViewOnly,
                        QuickJcSectionActivity.SCREEN_INSPECTION,
                        jobCard
                    ), REQUEST_CODE_SECTION
                )
            }

            NavigationInteraction.SCREEN_DAMAGES -> {
                this.startActivityForResult(
                    QuickJcSectionActivity.getIntent(
                        this,
                        isViewOnly,
                        QuickJcSectionActivity.SCREEN_DAMAGES,
                        jobCard
                    ), REQUEST_CODE_SECTION
                )
            }

            NavigationInteraction.SCREEN_INSURANCE -> {
                this.startActivityForResult(
                    QuickJcSectionActivity.getIntent(
                        this,
                        !(jobCard.status == JobCard.STATUS_IN_PROGRESS || jobCard.status == JobCard.STATUS_INITIATED),
                        QuickJcSectionActivity.SCREEN_INSURANCE,
                        jobCard
                    ), REQUEST_CODE_SECTION
                )
            }

            NavigationInteraction.SCREEN_ESTIMATE -> {
                startActivityForResult(
                    OutwardingProcessActivity.getEstimatorIntent(
                        this, jobCard.jobCardId, jobCard.id,
                        isViewOnly && jobCard.status == JobCard.STATUS_COMPLETED || jobCard.status == JobCard.STATUS_CLOSED || jobCard.status == JobCard.STATUS_CANCELLED,
                        jobCard.vehicleType
                    ), REQUEST_CODE_ESTIMATE_PROFORMA
                )
            }

            NavigationInteraction.SCREEN_PROFORMA -> {
                startActivityForResult(
                    OutwardingProcessActivity.getProformaIntent(
                        this,
                        SCREEN_TYPE_PROFORMA,
                        jobCard.jobCardId,
                        jobCard.invoice?.invoiceId,
                        jobCard.id,
                        jobCard.invoice?.id,
                        jobCard.invoice?.splitInvoice
                            ?: false,
                        jobCard.vehicleType,
                        jobCard.invoice,
                        jobCard.type
                    ), REQUEST_CODE_ESTIMATE_PROFORMA
                )
            }

            NavigationInteraction.SCREEN_PDF -> {
                pdfMediator.startJobCardDetailsPdf(
                    this,
                    jobCard.id,
                    jobCard.jobCardId + "-" + this.jobCard.vehicle.registrationNumber,
                    Source.IN_PROGRESS
                )
            }

            NavigationInteraction.SCREEN_VIEW_JC -> {
                startActivityForResult(
                    ViewJCActivity.getViewJcIntent(
                        this@QuickJobCardActivity,
                        jobCard.id,
                        jobCard.jobCardId,
                        false,
                        false,
                        jobCard.vehicleType
                    ), REQUEST_CODE_SECTION
                )
            }

            NavigationInteraction.SCREEN_NONE -> {
                // done to keep current screen
            }

            NavigationInteraction.SCREEN_EXIT -> {
                finish()
            }
        }
    }

    override fun displayJobCard(jobCard: JobCard) {
//        jobCard.customer = this.jobCard.customer
//        jobCard.vehicle = this.jobCard.vehicle
        this.jobCard = jobCard
        initFragments()
        initBottomActions()
    }

    override fun displayWithJobAndVerbatim(jobAndVerbatim: JobAndVerbatim) {
        this.jobAndVerbatim = jobAndVerbatim
        initFragments()
    }

    override fun displayData(jobCard: JobCard, jobAndVerbatim: JobAndVerbatim) {
//        jobCard.customer = this.jobCard.customer
//        jobCard.vehicle = this.jobCard.vehicle
        this.jobCard = jobCard
        this.jobAndVerbatim = jobAndVerbatim
        initFragments()
        initBottomActions()
//        Handler().postDelayed({
//            onKmsChanged(jobCard.kmsReading ?: 0)//for initial setup of regular service.
//        }, 500)
    }

    override fun onKmsChanged(kms: Int) {
        if (jobCard.status == JobCard.STATUS_INITIATED && fragmentList.isNotEmpty()) {
            (fragmentList[3] as? QuickJobFragment)?.updatePms(kms)
            Timber.d("farhan on kms changed $kms")
        }
    }

    override fun showAccidentalError(error: String?) {
        val dialog = DialogFactory.createGenericErrorDialog(
            this,
            "Following details are missing",
            error
        ) { _, _ ->
            onStoryClick(NavigationInteraction.SCREEN_INSURANCE)
        }
        dialog.show()
    }

    override fun launchWhatsapp(contactNumbe: String, message: String) {
        Utility.sendWhatsApp(this, contactNumbe, message)
    }

    override fun moveToNextScreen(statusChange: Boolean) {
        if (statusChange) {
            if (jobCard.status == JobCard.STATUS_IN_PROGRESS) {
                EventsManager.post(
                    CardStatusChangeEvent(
                        CardStatusChangeEvent.CARD_TYPE_JOB_CARD,
                        CardStatusChangeEvent.CARD_STATUS_JOB_CARD_COMPLETED
                    )
                )
            } else {
                EventsManager.post(
                    CardStatusChangeEvent(
                        CardStatusChangeEvent.CARD_TYPE_JOB_CARD,
                        CardStatusChangeEvent.CARD_STATUS_JOB_CARD_IN_PROGRESS
                    )
                )
            }
        }
        finish()
    }

    override fun showProgressIndicator() {
        showProgressBar()
    }

    override fun dismissProgressIndicator() {
        dismissProgressBar()
    }

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg)
    }

    override fun onActionBtnClick() {
        if (jobCard.status == JobCard.STATUS_IN_PROGRESS) {
            saveJobCard(NavigationInteraction.SCREEN_PDF)
        }
    }

    override fun onSecondaryActionBtnClick() {
        try {
            if (jobCard.status == JobCard.STATUS_IN_PROGRESS || jobCard.status == JobCard.STATUS_INITIATED) {
                val postJobCard = JobCard()
                fragmentList.forEach {
                    when (it) {
                        is QuickViewJcFragment -> {
                            postJobCard.jobs = it.getJobs()
                        }

                        is QuickJobFragment -> {
                            postJobCard.jobs = it.getJobs()
                        }

                        is QuickVehicleReadingFragment -> {
                            postJobCard.fuelReading = it.getFuelReading()
                            postJobCard.kmsReading = it.getKilometerReading()
                        }

                        is QuickEstimateFragment -> {
                            postJobCard.estimate = it.getEstimate(false)
                        }

                        is QuickConcernsFragment -> {
                            postJobCard.verbatim = it.getConcerns()
                        }
                    }
                }
                presenter.saveJobCard(jobCard.id, postJobCard, NavigationInteraction.SCREEN_NONE)
                Timber.d("still making api call")
            }
        } catch (e: ValidationInputException) {
            Timber.d("Validation exception")
        }
    }

    override fun onTertiaryActionBtnClick() {
        showMoreOptionsSheet()
    }

    override fun getActionBarImage(): Int =
        if (jobCard.status == JobCard.STATUS_IN_PROGRESS) R.drawable.ic_picture_as_pdf_black_24dp else 0

    override fun getSecondaryActionBarImage(): Int =
        if (jobCard.status == JobCard.STATUS_IN_PROGRESS || jobCard.status == JobCard.STATUS_INITIATED) R.drawable.ic_save_white_24dp else 0

    override fun getTertiaryActionBarImage(): Int = R.drawable.ic_more_vert_grey_24dp

    override fun getNavigationImage(): Int = R.drawable.ic_arrow_back_white_24dp

    override fun getToolBarTitle(): String = jobCard.jobCardId + "-" + vehicleRegNo

    override fun getToolbarSubTitle(): String = getPresentableSubtitle()

    @Subscribe
    fun onJobCardCloseEvent(event: ChangeCardStatusEvent) {
        if (event.cardStatus == JobCard.STATUS_CLOSED)
            startActivityForResult(
                NewServiceReminderActivity.getIntent(
                    this,
                    event.cardId,
                    null,
                    NewServiceReminderActivity.ACTION_CLOSE
                ), REQUEST_CODE_CLOSE_JOBCARD
            )
    }

    private fun getIntentData() {
        vehicleAmcId = intent.extras!!.getString(ARG_VEHICLE_AMC_ID, "")
        jobCard = intent.getParcelableExtra(ARG_JOB_CARD)!!
        vehicleRegNo = intent.getStringExtra(ARG_VEHICLE_REG_NO).toString()
        isViewOnly = intent.getBooleanExtra(ARG_IS_VIEW_ONLY, false)
        isAddJob = intent.getBooleanExtra(ARG_IS_ADD_JOB, false)
        isHistory = intent.getBooleanExtra(ARG_IS_HISTORY, false)
    }

    private fun initComponent() {
        (application as DearOApplication)
            .repositoryComponent
            .COMPONENT(QuickJobCardPresenterModule(this))
            .inject(this)
    }

    private fun getPresentableSubtitle(): String {
        return when (jobCard.status) {
            JobCard.STATUS_INITIATED -> "INITIATED"
            JobCard.STATUS_IN_PROGRESS -> "IN PROGRESS"
            JobCard.STATUS_COMPLETED -> "COMPLETED"
            JobCard.STATUS_CLOSED -> "CLOSED"
            JobCard.STATUS_CANCELLED -> "CANCELLED"
            else -> ""
        }
    }

    private fun showMoreOptionsSheet() {
        val bundle = Bundle()
        bundle.putString(AddCustomerActivity.ARG_CUSTOMER_ID, jobCard.customer.id)
        bundle.putString(AddCustomerActivity.ARG_TYPE, AddCustomerActivity.ARG_VIEW)
        bundle.putString(VehicleDetailsActivity.ARG_TYPE, VehicleDetailsActivity.ARG_VIEW)
        bundle.putString(MoreCtaListDialogFragment.ARG_JOB_CARD_ID, jobCard.id)
        bundle.putParcelable(VehicleDetailsActivity.VEHICLE, jobCard.vehicle)
        if (jobCard.status == JobCard.STATUS_COMPLETED || jobCard.status == JobCard.STATUS_CLOSED) {
//            bundle.putStringArray(MoreCtaListDialogFragment.ARG_INVOICE_REMARKS, jobCard.remarks)
            bundle.putString(MoreCtaListDialogFragment.ARG_DISPLAY_ID, jobCard.jobCardId)
            bundle.putString(MoreCtaListDialogFragment.ARG_INVOICE_STATUS, jobCard.invoice?.status)
        }
        val modalBottom =
            MoreCtaListDialogFragment.newInstance(true, jobCard.status, bundle, jobCard.vehicleType)
        val fragmentManager = supportFragmentManager
        modalBottom.show(fragmentManager, "some")
    }

    private fun initBottomActions() {
        binding.bottomActionButton1.setOnClickListener(this)
        binding.bottomActionButton2.setOnClickListener(this)
        when (jobCard.status) {
            JobCard.STATUS_INITIATED -> {
                binding.bottomActionButton1TextView.text =
                    getString(R.string.quick_jc_btn_go_to_estimate)
                binding.bottomActionButton2TextView.text =
                    getString(R.string.quick_jc_btn_create_jc)
                binding.bottomActionButton2.visibility = View.VISIBLE
            }

            JobCard.STATUS_IN_PROGRESS -> {
                binding.bottomActionButton1TextView.text =
                    getString(R.string.quick_jc_btn_parts_n_labour)
                binding.bottomActionButton2TextView.text =
                    getString(R.string.quick_jc_btn_job_complete)
                binding.bottomActionButton2.visibility = View.VISIBLE
            }

            JobCard.STATUS_COMPLETED -> {
                if (jobCard.invoice.status != Invoice.STATUS_PROFORMA) {
                    binding.bottomActionButton2TextView.text =
                        getString(R.string.quick_jc_btn_go_to_invoice)
                    binding.bottomActionButton2.visibility = View.VISIBLE
                    binding.bottomActionButton1.visibility = View.GONE
                } else {
                    if (jobCard.invoice.status == Invoice.STATUS_PROFORMA) {
                        binding.bottomActionButton1TextView.text =
                            getString(R.string.quick_jc_btn_parts_n_labour)
                        binding.bottomActionButton1.visibility = View.VISIBLE
                    } else {
                        binding.bottomActionButton1.visibility = View.GONE
                    }
                    binding.bottomActionButton2.visibility = View.GONE
                }
            }

            JobCard.STATUS_CLOSED -> {
                if (jobCard.invoice?.status == Invoice.STATUS_PAID) {
                    binding.bottomActionParentView.visibility = View.VISIBLE
                    binding.bottomActionButton2TextView.text =
                        getString(R.string.quick_jc_btn_go_to_invoice)
                    binding.bottomActionButton2.visibility = View.VISIBLE
                    binding.bottomActionButton1.visibility = View.GONE
                } else {
                    binding.bottomActionParentView.visibility = View.GONE
                }
            }

            JobCard.STATUS_CANCELLED -> {
                binding.bottomActionParentView.visibility = View.GONE
                binding.quickScrollView.setPadding(0, 0, 0, 10)
            }
        }

        if (jobCard.costEstimate != null) {
            val estimatorList = OutwardStepMapper.costEstimateToOutwardStep(
                jobCard.costEstimate,
                jobCard.packages,
                false
            )
            var total = 0.0
            estimatorList.asSequence().filter { it is OutwardSection }.map { it as OutwardSection }
                .toList().forEach {
                    total += it.total ?: 0.0
                }
            if (total > 0) {
                binding.estimateTotalView.visibility = View.VISIBLE
                binding.estimateTotalView.text = Utility.convertToCurrency(total).toString()
            } else {
                binding.estimateTotalView.visibility = View.GONE
            }
        }
    }

    private fun initFragments() {
        fragmentList.clear()
        fragmentList.add(
            QuickTopNavFragment.newInstance(
                isViewOnly,
                jobCard.id,
                jobCard,
                jobCard.status
            )
        )
        when (jobCard.status) {
            JobCard.STATUS_INITIATED -> {
                fragmentList.add(QuickConcernsFragment.newInstance(isViewOnly, jobCard.verbatim))
                fragmentList.add(
                    QuickVehicleReadingFragment.newInstance(
                        isViewOnly, jobCard.kmsReading
                            ?: 0, jobCard.fuelReading ?: 0
                    )
                )
                fragmentList.add(
                    QuickJobFragment.newInstance(
                        jobCard.jobs,
                        jobAndVerbatim!!,
                        jobCard.vehicleType
                    )
                )
                fragmentList.add(
                    QuickEstimateFragment.newInstance(
                        isViewOnly && jobCard.status == JobCard.STATUS_COMPLETED || jobCard.status == JobCard.STATUS_CLOSED || jobCard.status == JobCard.STATUS_CANCELLED,
                        jobCard
                    )
                )
            }

            JobCard.STATUS_IN_PROGRESS -> {
                fragmentList.add(
                    QuickViewJcFragment.newInstance(
                        jobCard.status != JobCard.STATUS_IN_PROGRESS,
                        jobCard.jobs,
                        jobCard.vehicleType,
                        isAddJob
                    )
                )
                fragmentList.add(QuickConcernsFragment.newInstance(isViewOnly, jobCard.verbatim))
                fragmentList.add(
                    QuickVehicleReadingFragment.newInstance(
                        isViewOnly, jobCard.kmsReading
                            ?: 0, jobCard.fuelReading ?: 0
                    )
                )
                fragmentList.add(
                    QuickEstimateFragment.newInstance(
                        isViewOnly && jobCard.status == JobCard.STATUS_COMPLETED || jobCard.status == JobCard.STATUS_CLOSED || jobCard.status == JobCard.STATUS_CANCELLED,
                        jobCard
                    )
                )
            }

            JobCard.STATUS_COMPLETED, JobCard.STATUS_CLOSED, JobCard.STATUS_CANCELLED -> {
                fragmentList.add(QuickConcernsFragment.newInstance(isViewOnly, jobCard.verbatim))
                fragmentList.add(
                    QuickVehicleReadingFragment.newInstance(
                        isViewOnly, jobCard.kmsReading
                            ?: 0, jobCard.fuelReading ?: 0
                    )
                )
                fragmentList.add(
                    QuickViewJcFragment.newInstance(
                        jobCard.status != JobCard.STATUS_IN_PROGRESS,
                        jobCard.jobs,
                        jobCard.vehicleType,
                        isAddJob
                    )
                )
                fragmentList.add(
                    QuickEstimateFragment.newInstance(
                        isViewOnly && jobCard.status == JobCard.STATUS_COMPLETED || jobCard.status == JobCard.STATUS_CLOSED || jobCard.status == JobCard.STATUS_CANCELLED,
                        jobCard
                    )
                )
            }
        }
        isAddJob = false // reset so that add jobs screen is not open when activity is resumed.
        supportFragmentManager.beginTransaction().replace(R.id.sectionOne, fragmentList[0])
            .commitAllowingStateLoss()
        supportFragmentManager.beginTransaction().replace(R.id.sectionTwo, fragmentList[1])
            .commitAllowingStateLoss()
        supportFragmentManager.beginTransaction().replace(R.id.sectionThree, fragmentList[2])
            .commitAllowingStateLoss()
        supportFragmentManager.beginTransaction().replace(R.id.sectionFour, fragmentList[3])
            .commitAllowingStateLoss()
        supportFragmentManager.beginTransaction().replace(R.id.sectionFive, fragmentList[4])
            .commitAllowingStateLoss()
    }

    private fun initJobCard() {
        if (checkIfNetworkAvailable()) {
            try {
                val postJobCard = JobCard()
                fragmentList.forEach {
                    when (it) {
                        is QuickViewJcFragment -> {
                            postJobCard.jobs = it.getJobs()
                        }

                        is QuickJobFragment -> {
                            postJobCard.jobs = it.getJobs()
                        }

                        is QuickVehicleReadingFragment -> {
                            postJobCard.fuelReading = it.getFuelReading()
                            postJobCard.kmsReading = it.getKilometerReading()
                        }

                        is QuickEstimateFragment -> {
                            postJobCard.estimate = it.getEstimate(true)
                        }

                        is QuickConcernsFragment -> {
                            postJobCard.verbatim = it.getConcerns()
                        }
                    }
                }
                postJobCard.status = JobCard.STATUS_IN_PROGRESS
                if (jobCard.type != JobCard.TYPE_ACCIDENTAL) {
                    DialogFactory.notifyAlertDialog(
                        this,
                        getString(R.string.create_job_card),
                        if (SharedPrefHelper.isNotifyOnCreateJC()) getString(R.string.notify_string) else getString(
                            R.string.are_u_sure
                        ),
                        SharedPrefHelper.isNotifyOnCreateJC() && SharedPrefHelper.isNotifyEnabled(),
                        SharedPrefHelper.getDefaultOptionCreateJC(),
                        false,
                        object : DialogFactory.NotifyButtonListener {
                            override fun positiveButton(notify: Boolean?) {
                                run {
                                    postJobCard.estimate.notifyCustomer = notify ?: false
                                    presenter.saveJobCard(jobCard.id, postJobCard, null)
                                }
                            }

                            override fun neutralButton() {
                            }
                        }).show()
                } else {
                    if (jobCard.accidental?.insurance?.claim == true) {
                        if (jobCard.accidental?.insurance?.claimNumber != null &&
                            jobCard.accidental?.insurance?.policyNumber != null &&
                            jobCard.accidental?.insurance?.expiryDate != null &&
                            jobCard.accidental?.company?.name != null &&
                            jobCard.accidental?.company?.address?.city != null &&
                            jobCard.accidental?.company?.address?.state != null &&
                            jobCard.accidental?.company?.address?.street != null &&
                            Utility.isPinCodeValid(jobCard.accidental?.company?.address?.pincode?.toString()) &&
                            jobCard.accidental?.company?.gstNumber != null

                        ) {
                            DialogFactory.notifyAlertDialog(
                                this,
                                getString(R.string.create_job_card),
                                getString(R.string.notify_string),
                                SharedPrefHelper.isNotifyOnCreateJC(),
                                SharedPrefHelper.getDefaultOptionCreateJC(),
                                false,
                                object : DialogFactory.NotifyButtonListener {
                                    override fun positiveButton(notify: Boolean?) {
                                        postJobCard.estimate.notifyCustomer = notify ?: false
                                        presenter.saveJobCard(jobCard.id, postJobCard, null)
                                    }

                                    override fun neutralButton() {
                                    }
                                }).show()
                        } else {

                            val errorStringBuilder = StringBuilder()
                            if (jobCard.accidental.insurance?.claimNumber == null) {
                                errorStringBuilder.appendLine("\u2022 Claim Number")
                            }
                            if (jobCard.accidental.insurance?.policyNumber == null) {
                                errorStringBuilder.appendLine("\u2022 Policy Number")
                            }
                            if (jobCard.accidental.insurance?.expiryDate == null) {
                                errorStringBuilder.appendLine("\u2022 Policy Expiry date")
                            }
                            if (jobCard.accidental.company?.name == null) {
                                errorStringBuilder.appendLine("\u2022 Insurance Company Name")
                            }

                            if (jobCard.accidental.company?.address?.city == null) {
                                errorStringBuilder.appendLine("\u2022 Insurance Company City")
                            }

                            if (jobCard.accidental.company?.address?.state == null) {
                                errorStringBuilder.appendLine("\u2022 Insurance Company State")
                            }

                            if (jobCard.accidental.company?.address?.street == null) {
                                errorStringBuilder.appendLine("\u2022 Insurance Company Street")
                            }

                            if (jobCard.accidental.company?.address?.pincode == null) {
                                errorStringBuilder.appendLine("\u2022 Insurance Company PinCode")
                            }

                            if (jobCard.accidental.company?.address?.pincode != null && Utility.isPinCodeValid(
                                    jobCard.accidental?.company?.address?.pincode?.toString()
                                ).not()
                            ) {
                                errorStringBuilder.appendLine("\u2022 PinCode is not valid")
                            }

                            if (jobCard.accidental.company?.gstNumber == null) {
                                errorStringBuilder.appendLine("\u2022 GST Number")
                            }

                            showAccidentalError(errorStringBuilder.toString())
                        }
                    } else {
                        DialogFactory.notifyAlertDialog(
                            this,
                            getString(R.string.create_job_card),
                            getString(R.string.notify_string),
                            SharedPrefHelper.isNotifyOnCreateJC() && SharedPrefHelper.isNotifyEnabled(),
                            SharedPrefHelper.getDefaultOptionCreateJC(),
                            false,
                            object : DialogFactory.NotifyButtonListener {
                                override fun positiveButton(notify: Boolean?) {
                                    postJobCard.estimate.notifyCustomer = notify ?: false
                                    presenter.saveJobCard(jobCard.id, postJobCard, null)
                                }

                                override fun neutralButton() {
                                }
                            }).show()
                    }
                }
            } catch (e: ValidationInputException) {
            }
        }
    }

    private fun completeJobCard() {
        if (checkIfNetworkAvailable()) {
            try {
                val postJobCard = JobCard()
                fragmentList.forEach {
                    when (it) {
                        is QuickViewJcFragment -> {
                            postJobCard.jobs = it.getJobs()
                        }

                        is QuickJobFragment -> {
                            postJobCard.jobs = it.getJobs()
                        }

                        is QuickVehicleReadingFragment -> {
                            postJobCard.fuelReading = it.getFuelReading()
                            postJobCard.kmsReading = it.getKilometerReading()
                        }

                        is QuickEstimateFragment -> {
                            postJobCard.estimate = it.getEstimate(true)
                        }

                        is QuickConcernsFragment -> {
                            postJobCard.verbatim = it.getConcerns()
                        }
                    }
                }
                if (jobCard.type != JobCard.TYPE_ACCIDENTAL) {
                    DialogFactory.notifyAlertDialog(this, getString(R.string.job_complete),
                        getString(R.string.job_complete_message),
                        SharedPrefHelper.isNotifyOnCompleteJC() && SharedPrefHelper.isNotifyEnabled(),
                        SharedPrefHelper.getDefaultOptionCompleteJC(),
                        true,
                        object : DialogFactory.NotifyButtonListener {
                            override fun positiveButton(notify: Boolean?) {
                                presenter.completeJobCard(
                                    jobCard.id, postJobCard, notify
                                        ?: false
                                )
                            }

                            override fun neutralButton() {
                                saveJobCard(NavigationInteraction.SCREEN_VIEW_JC)
                            }
                        }).show()
                } else {
                    if (jobCard.accidental?.insurance?.claim == true) {
                        if (jobCard.accidental?.insurance?.claimNumber != null &&
                            jobCard.accidental?.insurance?.policyNumber != null &&
                            jobCard.accidental?.insurance?.expiryDate != null &&
                            jobCard.accidental?.company?.name != null &&
                            jobCard.accidental?.company?.address?.city != null &&
                            jobCard.accidental?.company?.address?.state != null &&
                            jobCard.accidental?.company?.address?.street != null &&
                            jobCard.accidental?.company?.address?.street != null &&
                            Utility.isPinCodeValid(jobCard.accidental?.company?.address?.pincode?.toString()) &&
                            jobCard.accidental?.company?.gstNumber != null

                        ) {
                            DialogFactory.notifyAlertDialog(
                                this,
                                getString(R.string.complete_job_card),
                                getString(R.string.job_complete_message),
                                SharedPrefHelper.isNotifyOnCompleteJC() && SharedPrefHelper.isNotifyEnabled(),
                                SharedPrefHelper.getDefaultOptionCompleteJC(),
                                true,
                                object : DialogFactory.NotifyButtonListener {
                                    override fun positiveButton(notify: Boolean?) {
                                        presenter.completeJobCard(
                                            jobCard.id, postJobCard, notify
                                                ?: false
                                        )
                                    }

                                    override fun neutralButton() {
                                        saveJobCard(NavigationInteraction.SCREEN_VIEW_JC)
                                    }
                                }).show()
                        } else {
                            val errorStringBuilder = StringBuilder()
                            if (jobCard.accidental.insurance?.claimNumber == null) {
                                errorStringBuilder.appendLine("\u2022 Claim Number")
                            }

                            if (jobCard.accidental.insurance?.policyNumber == null) {
                                errorStringBuilder.appendLine("\u2022 Policy Number")
                            }
                            if (jobCard.accidental.insurance?.expiryDate == null) {
                                errorStringBuilder.appendLine("\u2022 Policy Expiry date")
                            }
                            if (jobCard.accidental.company?.name == null) {
                                errorStringBuilder.appendLine("\u2022 Insurance Company Name")
                            }

                            if (jobCard.accidental.company?.address?.city == null) {
                                errorStringBuilder.appendLine("\u2022 Insurance Company City")
                            }

                            if (jobCard.accidental.company?.address?.state == null) {
                                errorStringBuilder.appendLine("\u2022 Insurance Company State")
                            }

                            if (jobCard.accidental.company?.address?.street == null) {
                                errorStringBuilder.appendLine("\u2022 Insurance Company Street")
                            }

                            if (jobCard.accidental.company?.address?.pincode == null) {
                                errorStringBuilder.appendLine("\u2022 Insurance Company PinCode")
                            }

                            if (jobCard.accidental.company?.address?.pincode != null && Utility.isPinCodeValid(
                                    jobCard.accidental?.company?.address?.pincode?.toString()
                                ).not()
                            ) {
                                errorStringBuilder.appendLine("\u2022 PinCode Not Valid")
                            }

                            if (jobCard.accidental.company?.gstNumber == null) {
                                errorStringBuilder.appendLine("\u2022 GST Number")
                            }

                            showAccidentalError(errorStringBuilder.toString())
                        }
                    } else {
                        var dialog: AlertDialog? = null
                        val dialogView = View.inflate(this, R.layout.dialog_vertical_btn, null)
                        val dialogBuilder = AlertDialog.Builder(this)
                            .setView(dialogView)
                        dialogBuilder.setTitle("Create Split Invoice?")
                        dialogBuilder.setMessage("This will let you split the invoice between Customer and Insurance Company")
                        val postiveButton: Button = dialogView.findViewById(R.id.positiveBtn)
                        val negativeButtin: Button = dialogView.findViewById(R.id.negativeBtn)
                        postiveButton.setOnClickListener {
                            onStoryClick(NavigationInteraction.SCREEN_INSURANCE)
                            dialog?.dismiss()
                        }
                        negativeButtin.setOnClickListener {
                            DialogFactory.notifyAlertDialog(
                                this,
                                getString(R.string.complete_job_card),
                                getString(R.string.job_complete_message),
                                SharedPrefHelper.isNotifyOnCompleteJC() && SharedPrefHelper.isNotifyEnabled(),
                                SharedPrefHelper.getDefaultOptionCompleteJC(),
                                true,
                                object : DialogFactory.NotifyButtonListener {
                                    override fun positiveButton(notify: Boolean?) {
                                        presenter.completeJobCard(
                                            jobCard.id, postJobCard, notify
                                                ?: false
                                        )
                                    }

                                    override fun neutralButton() {
                                        saveJobCard(NavigationInteraction.SCREEN_VIEW_JC)
                                    }
                                }).show()
                            dialog?.dismiss()
                        }
                        dialog = dialogBuilder.show()
                    }
                }
            } catch (e: ValidationInputException) {
            }
        }
    }

    private fun saveJobCard(screenToStart: String?) {
        try {
            if (jobCard.status == JobCard.STATUS_IN_PROGRESS || jobCard.status == JobCard.STATUS_INITIATED) {
                val postJobCard = JobCard()
                fragmentList.forEach {
                    when (it) {
                        is QuickViewJcFragment -> {
                            postJobCard.jobs = it.getJobs()
                        }

                        is QuickJobFragment -> {
                            postJobCard.jobs = it.getJobs()
                        }

                        is QuickVehicleReadingFragment -> {
                            postJobCard.fuelReading = it.getFuelReading()
                            postJobCard.kmsReading = it.getKilometerReading()
                        }

                        is QuickEstimateFragment -> {
                            postJobCard.estimate = it.getEstimate(false)
                        }

                        is QuickConcernsFragment -> {
                            postJobCard.verbatim = it.getConcerns()
                        }
                    }
                }
                presenter.saveJobCard(jobCard.id, postJobCard, screenToStart)
                Timber.d("still making api call")
            } else {
                startScreen(screenToStart!!, jobCard)
            }
        } catch (e: ValidationInputException) {
            Timber.d("Validation exception")
        }
    }

    companion object {
        const val ARG_VEHICLE_AMC_ID = "vehicle-amc-id"
        const val ARG_JOB_CARD = "job_card"
        const val ARG_IS_ADD_JOB = "is_add_job"
        const val ARG_VEHICLE_REG_NO = "vehicle_reg_no"
        const val ARG_IS_HISTORY = "is_history"
        const val ARG_IS_VIEW_ONLY = "is_view_only"
        const val REQUEST_CODE_SECTION = 1009
        const val REQUEST_CODE_ESTIMATE_PROFORMA = 1010
        const val REQUEST_CODE_CLOSE_JOBCARD = 1001
        const val REQUEST_CODE_ACCIDENTAL_INFO = 1002

        fun getIntent(
            context: Context,
            isViewOnly: Boolean,
            isAddJob: Boolean,
            isHistory: Boolean,
            vehicleRegNo: String,
            jobCard: JobCard
        ): Intent {
            return Intent(context, QuickJobCardActivity::class.java).apply {
                putExtra(ARG_JOB_CARD, jobCard)
                putExtra(ARG_IS_ADD_JOB, isAddJob)
                putExtra(ARG_IS_HISTORY, isHistory)
                putExtra(ARG_IS_VIEW_ONLY, isViewOnly)
                putExtra(ARG_VEHICLE_REG_NO, vehicleRegNo)
            }
        }
    }
}
