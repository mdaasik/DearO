package com.carworkz.dearo.addjobcard.createjobcard.estimate

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.completejobcard.CompleteJobCardActivity
import com.carworkz.dearo.addjobcard.createjobcard.ActionButtonClickEvent
import com.carworkz.dearo.addjobcard.createjobcard.ICreateJobCardInteraction
import com.carworkz.dearo.addjobcard.createjobcard.accidental.missinginfoaccidental.AccidentalMissingInfoActivity
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.base.BaseFragment
import com.carworkz.dearo.base.DialogFactory
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.databinding.FragmentDamagePictureBinding
import com.carworkz.dearo.databinding.FragmentEstimateBinding
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.events.CardStatusChangeEvent
import com.carworkz.dearo.extensions.toast
import com.carworkz.dearo.outwarding.OutwardingProcessActivity
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.dialog_vertical_btn.view.**/
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class EstimateFragment : BaseFragment(), EstimateContract.View, EventsManager.EventSubscriber {
    private lateinit var binding: FragmentEstimateBinding
    private var isViewOnly: Boolean = false
    private lateinit var jobCardID: String
    private lateinit var dateParentView: LinearLayout
    private lateinit var timeParentView: LinearLayout
    private lateinit var datePicker: DatePickerDialog
    private lateinit var timePicker: TimePickerDialog
    private lateinit var dateTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var btnCreateJobCard: Button

    private lateinit var minEstimateView: EditText
    private lateinit var maxEstimateView: EditText

    private lateinit var startEstimatorView: CardView
    private lateinit var interaction: ICreateJobCardInteraction

    private var deliveryDateCalendar = Calendar.getInstance()

    private var selectedYear: Int = deliveryDateCalendar.get(Calendar.YEAR)
    private var selectedMonth: Int = deliveryDateCalendar.get(Calendar.MONTH)
    private var selectedDay: Int = deliveryDateCalendar.get(Calendar.DAY_OF_MONTH)
    private var selectedHour: Int = deliveryDateCalendar.get(Calendar.HOUR_OF_DAY) + 1
    private var selectedMinutes: Int = deliveryDateCalendar.get(Calendar.MINUTE)
    private lateinit var _container: ViewGroup

    @Inject
    lateinit var presenter: EstimatePresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    private var jobCard: JobCard? = null

    private var vehicleAmcId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isViewOnly = it.getBoolean(ARG_IS_VIEW_ONLY)
            jobCardID = it.getString(ARG_JOB_CARD_ID)!!
            vehicleAmcId = it.getString(ARG_VEHICLE_AMC_ID)

        }

        (requireActivity().application as DearOApplication).repositoryComponent
            .COMPONENT(EstimatePresenterModule(this))
            .inject(this)

        screenTracker.sendScreenEvent(
            activity,
            if (isViewOnly) ScreenTracker.SCREEN_VIEW_ESTIMATE else ScreenTracker.SCREEN_ESTIMATE,
            this.javaClass.name
        )

        datePicker = DatePickerDialog(
            requireActivity(),
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                selectedYear = year
                selectedDay = dayOfMonth
                selectedMonth = monthOfYear

                dateTextView.text =
                    Utility.formatDate(Utility.DATE_FORMAT_3, year, monthOfYear, dayOfMonth)
            },
            selectedYear,
            selectedMonth,
            selectedDay
        )

        datePicker.datePicker.minDate = System.currentTimeMillis() - 1000

        timePicker = TimePickerDialog(
            activity,
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->

                Timber.d("Time", " $selectedHour hours & $selectedMinute minutes")
                this.selectedHour = selectedHour
                this.selectedMinutes = selectedMinute
                timeTextView.text = Utility.formatTime(selectedHour, selectedMinute)
            },
            selectedHour,
            selectedMinutes,
            false
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEstimateBinding.inflate(inflater, container, false)
        _container = container!!
        return binding.root
        // Inflate the layout for this fragment
        /* val view = inflater.inflate(R.layout.fragment_estimate, container, false)
         _container = container!!
         return view*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dateParentView = view.findViewById<LinearLayout>(R.id.ll_estimate_parent_date)!!
        timeParentView = view.findViewById<LinearLayout>(R.id.ll_estimate_parent_time)!!
        dateTextView = view.findViewById<TextView>(R.id.tv_estimate_date)!!
        timeTextView = view.findViewById<TextView>(R.id.tv_estimate_time)!!
        btnCreateJobCard = view.findViewById<Button>(R.id.btn_create_job_card)!!
        maxEstimateView = view.findViewById<EditText>(R.id.et_max_estimate)!!
        minEstimateView = view.findViewById<EditText>(R.id.et_min_estimate)!!
        startEstimatorView = view.findViewById<CardView>(R.id.cd_parent_estimator)!!
        dateTextView.text =
            Utility.formatDate(Utility.DATE_FORMAT_3, selectedYear, selectedMonth, selectedDay)
        timeTextView.text = Utility.formatTime(selectedHour, selectedMinutes)

        if (isViewOnly) {
            maxEstimateView.isEnabled = false
            minEstimateView.isEnabled = false
            btnCreateJobCard.visibility = View.GONE
        } else {
            dateParentView.setOnClickListener { datePicker.show() }
            timeParentView.setOnClickListener { timePicker.show() }
            btnCreateJobCard.setOnClickListener {
                initJobCard(true)
            }
        }

        startEstimatorView.setOnClickListener {
            activity?.startActivity(
                OutwardingProcessActivity.getEstimatorIntent(
                    requireActivity(),
                    jobCard?.jobCardId,
                    jobCardID,
                    isViewOnly,
                    jobCard?.vehicleType
                )
            )
        }

        if (checkIfNetworkAvailable())
            presenter.getEstimate(jobCardID)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_ACCIDENTAL_INFO -> {
                presenter.getEstimate(jobCardID)
            }
        }
    }

    override fun showAccidentalError(error: String?) {
        val dialog = DialogFactory.createGenericErrorDialog(
            context,
            "Following details are missing",
            error
        ) { _, _ ->
            startAccidentalMissingActivity()
        }
        dialog.show()
    }

    override fun launchWhatsapp(contactNumber: String?, message: String?) {
        Utility.sendWhatsApp(context, contactNumber, message)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ICreateJobCardInteraction) {
            interaction = context
        } else {
            throw IllegalArgumentException("Activity must implement ICreateJobCardInteraction")
        }
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

    override fun showDeliveryDateError(message: String) {
        toast(message)
    }

    override fun showMinCostError(message: String?) {
        minEstimateView.error = message
    }

    override fun showMaxCostError(message: String?) {
        maxEstimateView.error = message
    }

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg)
    }

    override fun setUpdatedJC(jobCard: JobCard?) {
        this.jobCard = jobCard
        completeJobCard()
    }

    override fun showProgressIndicator() {
        (activity as ScreenContainerActivity).showProgressBar()
    }

    override fun dismissProgressIndicator() {
        (activity as ScreenContainerActivity).dismissProgressBar()
    }

    override fun displayEstimate(jobCard: JobCard?) {
        this.jobCard = jobCard
        if (jobCard?.estimate == null)
            return

        if (jobCard.status == JobCard.STATUS_IN_PROGRESS) {
            btnCreateJobCard.text = getString(R.string.job_completed)
            btnCreateJobCard.setOnClickListener {
                presenter.getEstimateAndCompleteJC(jobCardID)
            }
        }
        jobCard.estimate.deliveryDateTime?.let { dateString ->
            deliveryDateCalendar.time = Utility.getDate(dateString)
            selectedDay = deliveryDateCalendar.get(Calendar.DAY_OF_MONTH)
            selectedMonth = deliveryDateCalendar.get(Calendar.MONTH)
            selectedYear = deliveryDateCalendar.get(Calendar.YEAR)
            selectedHour = deliveryDateCalendar.get(Calendar.HOUR_OF_DAY)
            selectedMinutes = deliveryDateCalendar.get(Calendar.MINUTE)
            dateTextView.text =
                Utility.formatDate(Utility.DATE_FORMAT_3, selectedYear, selectedMonth, selectedDay)
            timeTextView.text = Utility.formatTime(selectedHour, selectedMinutes)
        }
        maxEstimateView.setText(jobCard.estimate?.maxCost.toString())
        minEstimateView.setText(jobCard.estimate?.minCost.toString())
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
        activity?.finish()
    }

    @Subscribe
    fun onNextBtnClick(obj: ActionButtonClickEvent) {
        Timber.v("ActionButton Click Event" + obj.hashCode())
        if (jobCard?.status != JobCard.STATUS_INITIATED && jobCard?.status != JobCard.STATUS_IN_PROGRESS || isViewOnly) {
            interaction.onJobSuccess()
            return
        }
        initJobCard(false)
    }

    private fun initJobCard(createJobCard: Boolean) {
        /*
        * Here we have to validate the data only and pass it to @CompleteJobCardActivity
        *
        */

        deliveryDateCalendar.set(Calendar.YEAR, selectedYear)
        deliveryDateCalendar.set(Calendar.MONTH, selectedMonth)
        deliveryDateCalendar.set(Calendar.DAY_OF_MONTH, selectedDay)
        deliveryDateCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
        deliveryDateCalendar.set(Calendar.MINUTE, selectedMinutes)
        deliveryDateCalendar.set(Calendar.SECOND, 0)

        val tempCal = Calendar.getInstance()
        if (deliveryDateCalendar.time < tempCal.time) {
            toast("Delivery time cannot be less than current time")
            interaction.onJobFailure()
            return
        }
        val datetime = Utility.formatToServerTime(deliveryDateCalendar.time, Utility.DATE_FORMAT_1)
        val minEstimate = minEstimateView.text.toString() //min est cost
        val maxEstimate = maxEstimateView.text.toString() //max est cost
        val status = if (createJobCard) STATUS_IN_PROGRESS else null
        if (checkIfNetworkAvailable()) {
            if (minEstimate.isNotEmpty()) {
                if (createJobCard) {
                    if (jobCard?.type != JobCard.TYPE_ACCIDENTAL) {
                        DialogFactory.notifyAlertDialog(
                            activity,
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
                                        presenter.saveEstimate(
                                            jobCardID,
                                            datetime,
                                            minEstimate.toInt(),
                                            if (maxEstimate.isNotBlank()) maxEstimate.toInt() else 0,
                                            status,
                                            notify
                                                ?: false
                                        )
                                    }
                                }

                                override fun neutralButton() {
                                }
                            }).show()
                    } else {
                        if (jobCard!!.accidental?.insurance?.claim == true) {
                            if (jobCard!!.accidental?.insurance?.claimNumber != null &&
                                jobCard!!.accidental?.insurance?.policyNumber != null &&
                                jobCard!!.accidental?.insurance?.expiryDate != null &&
                                jobCard!!.accidental?.company?.name != null &&
                                jobCard!!.accidental?.company?.address?.city != null &&
                                jobCard!!.accidental?.company?.address?.state != null &&
                                jobCard!!.accidental?.company?.address?.street != null &&
                                Utility.isPinCodeValid(jobCard!!.accidental?.company?.address?.pincode?.toString()) &&
                                jobCard!!.accidental?.company?.gstNumber != null
                            ) {
                                DialogFactory.notifyAlertDialog(
                                    activity,
                                    getString(R.string.create_job_card),
                                    getString(R.string.notify_string),
                                    SharedPrefHelper.isNotifyOnCreateJC() && SharedPrefHelper.isNotifyEnabled(),
                                    SharedPrefHelper.getDefaultOptionCreateJC(),
                                    false,
                                    object : DialogFactory.NotifyButtonListener {
                                        override fun positiveButton(notify: Boolean?) {
                                            presenter.saveEstimate(
                                                jobCardID,
                                                datetime,
                                                minEstimate.toInt(),
                                                if (maxEstimate.isNotBlank()) maxEstimate.toInt() else 0,
                                                status,
                                                notify
                                                    ?: false
                                            )
                                        }

                                        override fun neutralButton() {
                                        }
                                    }).show()
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
                                    errorStringBuilder.appendLine("\u2022 PinCode is not valid")
                                }
                                if (jobCard!!.accidental.company?.gstNumber == null) {
                                    errorStringBuilder.appendLine("\u2022 GST Number")
                                }
                                showAccidentalError(errorStringBuilder.toString())
                            }
                        } else {
                            DialogFactory.notifyAlertDialog(
                                activity,
                                getString(R.string.create_job_card),
                                getString(R.string.notify_string),
                                SharedPrefHelper.isNotifyOnCreateJC() && SharedPrefHelper.isNotifyEnabled(),
                                SharedPrefHelper.getDefaultOptionCreateJC(),
                                false,
                                object : DialogFactory.NotifyButtonListener {
                                    override fun positiveButton(notify: Boolean?) {
                                        presenter.saveEstimate(
                                            jobCardID,
                                            datetime,
                                            minEstimate.toInt(),
                                            if (maxEstimate.isNotBlank()) maxEstimate.toInt() else 0,
                                            status,
                                            notify
                                                ?: false
                                        )
                                    }

                                    override fun neutralButton() {
                                    }
                                }).show()
                        }
                    }
                } else {
                    presenter.saveEstimate(
                        jobCardID,
                        datetime,
                        minEstimate.toInt(),
                        if (maxEstimate.isNotBlank()) maxEstimate.toInt() else 0,
                        status,
                        false
                    )
                }
            } else {
                dismissProgressIndicator()
                minEstimateView.error = "Please enter minimum estimate"
            }
        } else {
            dismissProgressIndicator()
        }
    }

    private fun completeJobCard() {
        deliveryDateCalendar.set(Calendar.YEAR, selectedYear)
        deliveryDateCalendar.set(Calendar.MONTH, selectedMonth)
        deliveryDateCalendar.set(Calendar.DAY_OF_MONTH, selectedDay)
        deliveryDateCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
        deliveryDateCalendar.set(Calendar.MINUTE, selectedMinutes)
        deliveryDateCalendar.set(Calendar.SECOND, 0)

        //if reasonForDelay is enabled then make sure that the delivery time can not be changed after limit
        if (SharedPrefHelper.isReasonForDelayAllowed() == false) {
            val tempCal = Calendar.getInstance()
            if (deliveryDateCalendar.time < tempCal.time) {
                toast("Delivery time cannot be less than current time")
                interaction.onJobFailure()
                return
            }
        }

        //we need to check if mrnEstimate is enable and part issue is completed
        if ((SharedPrefHelper.isMrnEnabled()) and (jobCard?.mrn?.isPartsIssued != true)) {
            showGenericError("Parts incharge not issued the requested parts")
            return
        }

        val deliveryDateTime =
            Utility.formatToServerTime(deliveryDateCalendar.time, Utility.DATE_FORMAT_1)
        val minEstimate = minEstimateView.text.toString()
        val maxEstimate = maxEstimateView.text.toString()



        if (checkIfNetworkAvailable()) {
            if (minEstimate.isNotEmpty()) {
                //pass all the data to complete job card activity

                if (jobCard?.type != JobCard.TYPE_ACCIDENTAL) {
                    startActivity(
                        CompleteJobCardActivity.getIntent(
                            requireContext(),
                            jobCardID,
                            deliveryDateCalendar.time.time.toString(),
                            minEstimate.toInt(),
                            if (maxEstimate.isNotBlank()) maxEstimate.toInt() else 0,
                            jobCard!!
                        )
                    )
                    activity?.finish()
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
                            startActivity(
                                CompleteJobCardActivity.getIntent(
                                    requireContext(),
                                    jobCardID,
                                    deliveryDateCalendar.time.time.toString(),
                                    minEstimate.toInt(),
                                    if (maxEstimate.isNotBlank()) maxEstimate.toInt() else 0,
                                    jobCard!!
                                )
                            )
                            activity?.finish()
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
                        var dialog: AlertDialog? = null
                        val dialogView = View.inflate(context, R.layout.dialog_vertical_btn, null)
                        val splitInvoiceDialogBuilder = AlertDialog.Builder(requireContext())
                            .setView(dialogView)
                            .setTitle("Create Split Invoice?")
                            .setMessage("This will let you split the invoice between Customer and Insurance Company")
                        val positiveButton: Button = dialogView.findViewById(R.id.positiveBtn)
                        val negativeButton: Button = dialogView.findViewById(R.id.negativeBtn)
                        positiveButton.setOnClickListener {
                            startAccidentalMissingActivity()
                            dialog?.dismiss()
                        }

                        negativeButton.setOnClickListener {
                            dialog?.dismiss()
                            startActivity(
                                CompleteJobCardActivity.getIntent(
                                    requireContext(),
                                    jobCardID,
                                    deliveryDateCalendar.time.time.toString(),
                                    minEstimate.toInt(),
                                    if (maxEstimate.isNotBlank()) maxEstimate.toInt() else 0,
                                    jobCard!!
                                )
                            )
                        }

                        dialog = splitInvoiceDialogBuilder.show()
                    }
                }
            } else {
                dismissProgressIndicator()
                minEstimateView.error = "Please enter minimum estimate"
            }
        }
    }

    private fun startAccidentalMissingActivity() {
        startActivityForResult(
            AccidentalMissingInfoActivity.getIntent(
                requireContext(),
                jobCardID,
                jobCard!!.status
            ), REQUEST_CODE_ACCIDENTAL_INFO
        )
    }

    companion object {
        const val ARG_VEHICLE_AMC_ID = "vehicle-amc-id"
        const val ARG_IS_VIEW_ONLY = "arg_is_view_only"
        const val ARG_JOB_CARD_ID = "arg_job_card_id"
        const val STATUS_IN_PROGRESS = "IN_PROGRESS"
        const val REQUEST_CODE_ACCIDENTAL_INFO = 1000

        @JvmStatic
        fun newInstance(isViewOnly: Boolean, jobCardId: String, amcId: String?): EstimateFragment {
            val fragment = EstimateFragment()
            val args = Bundle()
            args.putString(ARG_VEHICLE_AMC_ID, amcId)
            args.putBoolean(ARG_IS_VIEW_ONLY, isViewOnly)
            args.putString(ARG_JOB_CARD_ID, jobCardId)
            args.putString(ARG_JOB_CARD_ID, jobCardId)
            fragment.arguments = args
            return fragment
        }
    }
}
