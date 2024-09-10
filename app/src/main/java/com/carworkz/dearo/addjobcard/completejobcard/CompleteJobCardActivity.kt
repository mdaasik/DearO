package com.carworkz.dearo.addjobcard.completejobcard

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.ICreateJobCardInteraction
import com.carworkz.dearo.addjobcard.createjobcard.accidental.missinginfoaccidental.AccidentalMissingInfoActivity
import com.carworkz.dearo.addjobcard.createjobcard.estimate.EstimateFragment
import com.carworkz.dearo.addjobcard.createjobcard.jobs.viewjc.ViewJCActivity
import com.carworkz.dearo.base.DialogFactory
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.databinding.ActivityAddMissingVehicleInfoBinding
import com.carworkz.dearo.databinding.ActivityCompleteJobcardBinding
import com.carworkz.dearo.databinding.RemarkEditLayoutBinding
import com.carworkz.dearo.domain.entities.InvoiceRemarks
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.domain.entities.RecommendedJob
import com.carworkz.dearo.domain.entities.Remark
import com.carworkz.dearo.events.CardStatusChangeEvent
import com.carworkz.dearo.interactionprovider.ToolBarImgInteractionProvider
import com.carworkz.dearo.screencontainer.ActionImgScreenContainer
import com.carworkz.dearo.utils.Constants
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.activity_complete_jobcard.*
import kotlinx.android.synthetic.main.base_layout.*
import kotlinx.android.synthetic.main.dialog_vertical_btn.view.*
import kotlinx.android.synthetic.main.remark_edit_layout.view.**/
import java.util.*
import javax.inject.Inject


class CompleteJobCardActivity : ScreenContainerActivity(), ToolBarImgInteractionProvider,
    ICreateJobCardInteraction, View.OnClickListener, CompleteJobCardContract.View {
    private lateinit var binding: ActivityCompleteJobcardBinding
    private var jobCard: JobCard? = null
    private lateinit var jobCardID: String
    private lateinit var deliveryDateTime: String
    private var minEstTime: Int = 0
    private var maxEstTime: Int = 0
    private var notify = false
    private var deliveryDateCalendar = Calendar.getInstance()
    lateinit var screenContainer: ActionImgScreenContainer
    private var selectedReason: String = ""
    private var editTextList = mutableListOf<View>()
    private var remarks = ArrayList<Remark>()

    @Inject
    lateinit var presenter: CompleteJobCardPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getIntentData()
        super.onCreate(savedInstanceState)
        initialize()
        (application as DearOApplication)
            .repositoryComponent
            .COMPONENT(CompleteJobCardPresenterModule(this))
            .inject(this)
        if (checkIfNetworkAvailable()) {
            presenter.getRemarks(jobCardID)
        }
    }

    private fun getIntentData() {
        jobCard = intent.getParcelableExtra(ARG_JOB_CARD)
        jobCardID = intent.getStringExtra(ARG_JOB_CARD_ID).toString()
        deliveryDateTime = intent.getStringExtra(ARG_DELIVERY_DATE_TIME).toString()
        minEstTime = intent.getIntExtra(ARG_MIN_EST_TIME, 0)
        maxEstTime = intent.getIntExtra(ARG_MAX_EST_TIME, 0)
        deliveryDateCalendar.timeInMillis = deliveryDateTime.toLong()
        Log.d(Companion.TAG, "intent time is : " + deliveryDateCalendar.time.toString())
    }

    override fun createScreenContainer(): ScreenContainer {
        screenContainer = ActionImgScreenContainer(this)
        return screenContainer
    }

    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityCompleteJobcardBinding.inflate(layoutInflater)
        return binding
    }


    override fun getProgressView(): View {
        return binding.baseLayouyt.pbMain
    }

    override fun getNavigationImage(): Int = R.drawable.ic_arrow_back_white_24dp

    override fun onActionBtnClick() {
    }

    override fun onSecondaryActionBtnClick() {
    }

    override fun getActionBarImage(): Int = 0

    override fun getSecondaryActionBarImage(): Int = 0

    override fun getToolBarTitle(): String = "Complete Jobcard"

    private fun initialize() {
        if (SharedPrefHelper.isNotifyOnCompleteJC() && SharedPrefHelper.isNotifyEnabled()) {
            //enable notify checkbox
            notify = true
            binding.notifyCustomer.isEnabled = true
            binding.tvRegularService.isEnabled = true
        }
        if (SharedPrefHelper.getDefaultOptionCompleteJC()) {
            binding.notifyCustomer.isChecked = true
        }

        binding.notifyCustomer.setOnCheckedChangeListener { _, isChecked ->
            notify = isChecked
        }

        binding.verifyJobsButton.setOnClickListener {
            verifyJob()
        }

        binding.proceed.setOnClickListener(this)

        //init reasons for delay
        //
        val tempCal = Calendar.getInstance()
        if (SharedPrefHelper.isReasonForDelayAllowed() and (deliveryDateCalendar.time < tempCal.time)) {
            binding.reasonForDelayLL.visibility = View.VISIBLE

            val spinnerArrayAdapter: ArrayAdapter<String> = object : ArrayAdapter<String>(
                this,
                R.layout.spinner_item,
                Constants.BusinessConstants.REASONS_FOR_DELAY_LIST
            ) {
                override fun isEnabled(position: Int): Boolean {
                    return true// Disable the first item from Spinner
                    // First item will be use for hint
                    position != 0
                }

                override fun getDropDownView(
                    position: Int, convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view = super.getDropDownView(position, convertView, parent)
                    val tv = view as TextView
                    if (position == 0) {
                        // Set the hint text color gray
                        tv.setTextColor(Color.GRAY)
                    } else {
                        tv.setTextColor(Color.BLACK)
                    }
                    return view
                }

                override fun setDropDownViewResource(resource: Int) {
                    R.layout.spinner_item
                }
            }
//            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item)
            binding.reasonSpinner.adapter = spinnerArrayAdapter
//            reasonSpinner.prompt = "Select reason for delay"

            binding.reasonSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        selectedReason =
                            Constants.BusinessConstants.REASONS_FOR_DELAY_LIST[position]
                        if (selectedReason == Constants.BusinessConstants.REASONS_FOR_DELAY_LIST.last()) {
                            //it means user has selected other now enable free text field to enter reason
                            binding.detailsLayout.visibility = View.VISIBLE
                        } else {
                            binding.detailsLayout.visibility = View.GONE
                        }
                    }

                }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    private fun verifyJob() {
        startActivity(
            ViewJCActivity.getViewJcIntent(
                this,
                jobCard?.id!!,
                jobCard?.jobCardId!!,
                isViewOnly = false,
                isAddJob = false,
                vehicleType = jobCard?.vehicleType
            )
        )
    }

    private fun completeJobCard() {
        /*
        * Validate the data and complete jobcard
        * */
        deliveryDateTime = Utility.formatToServerTime(
            deliveryDateCalendar.time,
            Utility.DATE_FORMAT_1
        )

        if (jobCard?.type != JobCard.TYPE_ACCIDENTAL) {
            presenter.completeJobCard(
                jobCardID,
                deliveryDateTime,
                minEstTime,
                maxEstTime,
                notify,
                selectedReason
            )
        } else {
            //this is for accidental

            if (jobCard!!.accidental?.insurance?.claim == true) {
                if (jobCard!!.accidental?.insurance?.claimNumber != null &&
                    jobCard!!.accidental?.insurance?.policyNumber != null &&
                    jobCard!!.accidental?.insurance?.expiryDate != null &&
                    jobCard!!.accidental?.company?.name != null &&
                    jobCard!!.accidental?.company?.address?.city != null &&
                    jobCard!!.accidental?.company?.address?.state != null &&
                    jobCard!!.accidental?.company?.address?.street != null &&
                    jobCard!!.accidental?.company?.address?.street != null &&
                    Utility.isPinCodeValid(jobCard!!.accidental?.company?.address?.pincode?.toString()) &&
                    jobCard!!.accidental?.company?.gstNumber != null

                ) {
                    presenter.completeJobCard(
                        jobCardID,
                        deliveryDateTime,
                        minEstTime,
                        maxEstTime,
                        notify,
                        selectedReason
                    )
                } else {
                    val errorStringBuilder = StringBuilder()
                    if (jobCard!!.accidental.insurance?.claimNumber == null) {
                        errorStringBuilder.appendLine("\u2022 Claim Number")
                    }
                    if (jobCard!!.accidental.insurance?.policyNumber == null) {
                        errorStringBuilder.appendLine("\u2022 Policy Number")
                    }
                    if (jobCard!!.accidental.insurance?.expiryDate == null) {
                        errorStringBuilder.appendLine("\u2022 Policy Expiry date")
                    }
                    if (jobCard!!.accidental.company?.name == null) {
                        errorStringBuilder.appendLine("\u2022 Insurance Company Name")
                    }

                    if (jobCard!!.accidental.company?.address?.city == null) {
                        errorStringBuilder.appendLine("\u2022 Insurance Company City")
                    }

                    if (jobCard!!.accidental.company?.address?.state == null) {
                        errorStringBuilder.appendLine("\u2022 Insurance Company State")
                    }

                    if (jobCard!!.accidental.company?.address?.street == null) {
                        errorStringBuilder.appendLine("\u2022 Insurance Company Street")
                    }

                    if (jobCard!!.accidental.company?.address?.pincode == null) {
                        errorStringBuilder.appendLine("\u2022 Insurance Company PinCode")
                    }

                    if (jobCard!!.accidental.company?.address?.pincode != null && Utility.isPinCodeValid(
                            jobCard!!.accidental?.company?.address?.pincode?.toString()
                        ).not()
                    ) {
                        errorStringBuilder.appendLine("\u2022 PinCode not valid")
                    }

                    if (jobCard!!.accidental.company?.gstNumber == null) {
                        errorStringBuilder.appendLine("\u2022 GST Number")
                    }

                    showAccidentalError(errorStringBuilder.toString())
                }
            } else {
                presenter.completeJobCard(
                    jobCardID,
                    deliveryDateTime,
                    minEstTime,
                    maxEstTime,
                    notify,
                    selectedReason
                )
            }
        }
    }

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg)
    }

    override fun dismissProgressIndicator() {
        super.dismissProgressBar()
    }

    override fun showProgressIndicator() {
        super.showProgressBar()
    }

    override fun showMinCostError(message: String?) {
        displayError(message)
    }

    override fun showAccidentalError(error: String?) {
        val dialog = DialogFactory.createGenericErrorDialog(
            this,
            "Following details are missing",
            error
        ) { _, _ ->
            startAccidentalMissingActivity()
        }
        dialog.show()
    }

    override fun displayEstimate(jobCard: JobCard?) {
        this.jobCard = jobCard
        if (jobCard?.estimate == null)
            return

        jobCard.estimate.deliveryDateTime?.let { dateString ->
            deliveryDateCalendar.time = Utility.getDate(dateString)
        }
    }

    override fun showDeliveryDateError(message: String?) {
        if (message != null) showGenericError(message)
    }

    override fun showMaxCostError(message: String?) {
        if (message != null) showGenericError(message)
    }

    override fun launchWhatsapp(contactNumber: String?, message: String?) {
        Utility.sendWhatsApp(this, contactNumber, message)
    }

    override fun moveToNextScreen() {
        if (jobCard?.status == JobCard.STATUS_IN_PROGRESS) {
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
        finish()
    }

    override fun onRedYellowInvoiceRemarkSuccess() {
        //this method will get call when RED YELLOW remarks get saved
        completeJobCard()
    }

    override fun displayRemarksAndJobs(
        remarks: List<Remark>,
        unApprovedJobs: List<RecommendedJob>?
    ) {
        remarks.forEach {
            createNewRemarkText(it)
        }
        /*unApprovedJobs?.forEach {
            createNewJobText(it)
        }*/

        if (remarks.isEmpty() && unApprovedJobs?.isEmpty() == true) {
            createNewRemarkText(Remark())
        }
    }

    private fun startAccidentalMissingActivity() {
        startActivityForResult(
            AccidentalMissingInfoActivity.getIntent(
                this,
                jobCardID,
                jobCard!!.status
            ), EstimateFragment.REQUEST_CODE_ACCIDENTAL_INFO
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            EstimateFragment.REQUEST_CODE_ACCIDENTAL_INFO -> {
                presenter.getEstimate(jobCardID)
            }
        }
    }


    override fun onJobSuccess() {

    }

    override fun onJobVerify() {

    }

    override fun onJobFailure() {

    }

    override fun onClick(v: View?) {
        when (v) {
            binding.addItemView -> {
                //add remarks
                createNewRemarkText(Remark())

            }

            binding.proceed -> {
                val err = validateData()
                if (err.isEmpty()) {
                    //firstly we will save Red Yellow Remarks
                    //in the success of above we will complete the job card
                    saveRedYellowRemark()
                } else {
                    showGenericError(err)
                }
            }
        }
    }

    private fun createNewRemarkText(remark: Remark) {
        // Add the remark to the list
        remarks.add(remark)

        // Inflate the layout and bind it
        val viewBinding = RemarkEditLayoutBinding.inflate(LayoutInflater.from(this), null, false)

        // Set up the clear button's click listener
        viewBinding.clearView.setOnClickListener {
            editTextList.remove(viewBinding.root)
            binding.addedItemView.removeView(viewBinding.root)
        }

        // Set the text and switch state based on the remark
        viewBinding.editView.setText(remark.remark)
        viewBinding.switchView.isOn = remark.type == "red"

        // Add the view to the lists and parent view
        editTextList.add(viewBinding.root)
        binding.addedItemView.addView(viewBinding.root)

        // Request focus for the EditText
        viewBinding.editView.requestFocus()
    }


    private fun validateData(): String {
        var err = ""
        //if reason is allowed then only validate it
        if (binding.reasonForDelayLL.visibility == View.VISIBLE) {
            //check if selected reason has other
            if (selectedReason.isEmpty() || selectedReason == Constants.BusinessConstants.REASONS_FOR_DELAY_LIST.first()) {
                err = "Please select reason for delay"
            } else if (selectedReason == Constants.BusinessConstants.REASONS_FOR_DELAY_LIST.last()) {
                //it means other
                if (binding.otherReason.text.isEmpty()) {
                    err = "Please type reason for Others"
                }
            }
        }
        return err
    }

    private fun saveRedYellowRemark() {
        val data = InvoiceRemarks()

        val remarkList = ArrayList<Remark>()
        for (view in editTextList) {
            // Cast the view to the binding class
            val binding = RemarkEditLayoutBinding.bind(view)

            // Check if the EditText is not empty
            if (binding.editView.text.isNotEmpty()) {
                val remark = Remark()
                remark.remark = binding.editView.text.toString()
                remark.type = if (binding.switchView.isOn) "red" else "yellow"
                remarkList.add(remark)
            }
        }
        data.remarks = remarkList

        // Prepare the delete list
        val deleteList = ArrayList<String>()
        data.jobIds = deleteList

        // Save the invoice remarks using the presenter
        presenter.saveInvoiceRemarks(jobCardID, data)
    }

    companion object {
        const val ARG_JOB_CARD = "arg_job_card"
        const val ARG_JOB_CARD_ID = "arg_job_card_id"
        const val ARG_DELIVERY_DATE_TIME = "arg_delivery_datetime"
        const val ARG_MIN_EST_TIME = "arg_min_est_time"
        const val ARG_MAX_EST_TIME = "arg_max_est_time"
        const val STATUS_IN_PROGRESS = "IN_PROGRESS"

        fun getIntent(
            context: Context,
            jobCardID: String,
            deliveryDateTime: String,
            minEstimate: Int,
            maxEstimate: Int,
            jobCard: JobCard
        ): Intent {
            val intent = Intent(context, CompleteJobCardActivity::class.java)
            intent.putExtra(ARG_JOB_CARD_ID, jobCardID)
            intent.putExtra(ARG_DELIVERY_DATE_TIME, deliveryDateTime)
            intent.putExtra(ARG_MIN_EST_TIME, minEstimate)
            intent.putExtra(ARG_MAX_EST_TIME, maxEstimate)
            intent.putExtra(ARG_JOB_CARD, jobCard)
            return intent
        }

        private const val TAG = "CompleteJobCardActivity"
    }
}