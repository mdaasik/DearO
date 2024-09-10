package com.carworkz.dearo.serviceremainder

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.base.BaseActivity
import com.carworkz.dearo.base.DialogFactory
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.databinding.ActivityNewServiceRemainderBinding
import com.carworkz.dearo.databinding.RemarkEditLayoutBinding
import com.carworkz.dearo.domain.entities.InvoiceRemarks
import com.carworkz.dearo.domain.entities.PDF
import com.carworkz.dearo.domain.entities.RecommendedJob
import com.carworkz.dearo.domain.entities.Remark
import com.carworkz.dearo.utils.Utility
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject

class NewServiceReminderActivity : BaseActivity(), View.OnClickListener,
    ServiceReminderContract.View {
    private lateinit var binding: ActivityNewServiceRemainderBinding
    private var remarks = ArrayList<Remark>()
    private var unApprovedJobs = mutableListOf<RecommendedJob>()
    private var editTextListOne = mutableListOf<View>()
    private var editTextList = mutableListOf<RemarkEditLayoutBinding>()
    private var editJobList = mutableListOf<View>()

    private var selectedDate = Calendar.getInstance()
    private lateinit var datePicker: DatePickerDialog
    private val todayCalendar = Calendar.getInstance()

    private lateinit var jobCardId: String
    private var invoiceId: String? = null

    @Inject
    lateinit var presenter: ServiceReminderPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        jobCardId = intent.extras!!.getString(ARG_JOBCARD_ID)!! // "1539601062022137"
        invoiceId = intent.extras!!.getString(ARG_INVOICE_ID)
        super.onCreate(savedInstanceState)
        setUpComponent()
        binding = ActivityNewServiceRemainderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarServiceRemainder)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title =
            if (intent.action == ACTION_INVOICE) "Raise Invoice" else "Close JobCard"
        binding.submitView.setOnClickListener(this)
        binding.addItemView.setOnClickListener(this)
        binding.layOutServiceRemainder.customView.setOnClickListener(this)
        binding.layOutServiceRemainder.rgServiceRemainderView.setOnCheckedChangeListener { _, checkedId ->
            binding.layOutServiceRemainder.customView.isChecked = false
            binding.layOutServiceRemainder.customView.text =
                getString(R.string.service_remainder_custom)
            when (checkedId) {
                R.id.noneView -> {
                    monthsToDate(0)
                }

                R.id.threeView -> {
                    monthsToDate(3)
                }

                R.id.sixView -> {
                    Timber.d("six selected")
                    monthsToDate(6)
                }

                R.id.twelveView -> {
                    Timber.d("twelve selected")
                    monthsToDate(12)
                }

                R.id.eighteenView -> {
                    Timber.d("18 selected")
                    monthsToDate(18)
                }
            }
        }

        datePicker = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                binding.layOutServiceRemainder.rgServiceRemainderView.clearCheck()
                selectedDate.set(year, month, dayOfMonth)
                binding.layOutServiceRemainder.customView.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_mode_edit_black_24dp,
                    0
                )
                binding.layOutServiceRemainder.customView.text = Utility.formatDate(
                    Utility.DATE_FORMAT_6,
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH)
                )
                Handler().postDelayed({
                    binding.layOutServiceRemainder.customView.isChecked = true
                }, 200)
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.updateDate(
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH) + 6,
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )

        if (checkIfNetworkAvailable()) {
            presenter.getRemarks(jobCardId)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.addItemView -> {
                createNewRemarkText(Remark())
            }

            binding.layOutServiceRemainder.customView -> displayDatePicker()
            // Assuming you have initialized binding properly somewhere in your activity/fragment
// val binding = ActivityYourLayoutBinding.inflate(layoutInflater)


            binding.submitView -> {
                val invoiceRemarks = InvoiceRemarks()
                val remarkList = ArrayList<Remark>()

                for (binding in editTextList) {
                    val viewBinding = binding as? RemarkEditLayoutBinding
                    viewBinding?.let {
                        if (it.editView.text.isNotEmpty()) {
                            val remark = Remark().apply {
                                this.remark = it.editView.text.toString()
                                this.type = if (it.switchView.isOn) "red" else "yellow"
                            }
                            remarkList.add(remark)
                        }
                    }
                }

                invoiceRemarks.remarks = remarkList
                val deleteList = ArrayList<String>()
                unApprovedJobs.forEach { job ->
                    val jobIdsInList =
                        editJobList.mapNotNull { (it as? RemarkEditLayoutBinding)?.editView?.tag as? RecommendedJob }
                            .filter { it.id == job.id }

                    if (jobIdsInList.isEmpty()) {
                        job.id?.let { deleteList.add(it) }
                    }
                }

                invoiceRemarks.jobIds = deleteList
                //  presenter.saveInvoiceRemarks(jobCardId, invoiceRemarks)

                Timber.d(selectedDate.time.toString())
                Timber.d(Utility.dateToCalender(selectedDate.time, Utility.DATE_FORMAT_5))
                if (binding.layOutServiceRemainder.rgServiceRemainderView.checkedRadioButtonId != -1 || binding.layOutServiceRemainder.customView.isChecked) {
                    if (checkIfNetworkAvailable()) {
                        val serviceRemainderDate =
                            if (binding.layOutServiceRemainder.rgServiceRemainderView.checkedRadioButtonId == R.id.noneView) null else Utility.dateToCalender(
                                selectedDate.time,
                                Utility.DATE_FORMAT_5
                            )
                        Timber.d(
                            "service reminder date ${
                                Utility.formatDate(
                                    serviceRemainderDate,
                                    Utility.DATE_FORMAT_5,
                                    Utility.DATE_FORMAT_7,
                                    Utility.TIMEZONE_UTC
                                )
                            }"
                        )
                        DialogFactory.notifyAlertDialog(
                            this,
                            if (intent.action == ACTION_INVOICE) getString(R.string.invoice_title) else getString(
                                R.string.close_ques
                            ),
                            getString(R.string.undone_warning),
                            SharedPrefHelper.isNotifyOnCreateInvoice() && SharedPrefHelper.isNotifyEnabled(),
                            SharedPrefHelper.getDefaultOptionCreateInvoice(),
                            false,
                            object : DialogFactory.NotifyButtonListener {
                                override fun positiveButton(notify: Boolean?) {
                                    presenter.saveInformation(
                                        jobCardId,
                                        invoiceId,
                                        invoiceRemarks,
                                        serviceRemainderDate,
                                        notify ?: false,
                                        intent.action!!
                                    )
                                    // presenter.getInvoicePDF(jobCardId, notify, serviceRemainderDate)
                                }

                                override fun neutralButton() {
                                }
                            }).show()
                    }
                }
            }
        }
    }

    override fun getProgressView(): View = binding.progressBarView

    override fun moveToNextScreen(pdfs: List<PDF>?) {
        setResult(Activity.RESULT_OK, Intent().apply {
            if (pdfs != null) {
                putParcelableArrayListExtra(ARG_PDF, ArrayList(pdfs))
            }
        })
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

    override fun displayRemarksAndJobs(
        remarks: List<Remark>,
        unApprovedJobs: List<RecommendedJob>?
    ) {

        remarks.forEach {
            createNewRemarkText(it)
        }
        unApprovedJobs?.forEach {
            createNewJobText(it)
        }

        if (remarks.isEmpty() && unApprovedJobs?.isEmpty() == true) {
            createNewRemarkText(Remark())
        }
    }

    override fun launchWhatsapp(contactNumber: String, message: String) {
        Utility.sendWhatsApp(this, contactNumber, message)
    }

    private fun setUpComponent() {
        (application as DearOApplication).repositoryComponent.COMPONENT(
                ServiceReminderPresenterModule(this)
            ).inject(this)
    }


    private fun createNewRemarkText(remark: Remark) {
        remarks.add(remark)
        val bindingEdit = RemarkEditLayoutBinding.inflate(LayoutInflater.from(this))

        bindingEdit.clearView.setOnClickListener {
            editTextListOne.remove(bindingEdit.root)
            binding.addedItemView.removeView(bindingEdit.root)
        }

        bindingEdit.editView.setText(remark.remark)
        bindingEdit.switchView.isOn = remark.type == "red"

        editTextListOne.add(bindingEdit.root)
        binding.addedItemView.addView(bindingEdit.root)
        bindingEdit.editView.requestFocus()
    }


    @SuppressLint("InflateParams")
    private fun createNewJobText(s: RecommendedJob) {
        unApprovedJobs.add(s)
        val bindingEdit = RemarkEditLayoutBinding.inflate(LayoutInflater.from(this))

        bindingEdit.editView.tag = s
        bindingEdit.clearView.visibility = View.VISIBLE
        bindingEdit.clearView.setOnClickListener {
            editJobList.remove(bindingEdit.root)
            binding.addedItemView.removeView(bindingEdit.root)
        }
        bindingEdit.editView.setText(s.text)
        bindingEdit.editView.isEnabled = false
        bindingEdit.editView.isFocusable = false
        bindingEdit.switchView.visibility = View.INVISIBLE

        editJobList.add(bindingEdit.root)
        binding.addedItemView.addView(bindingEdit.root)
        bindingEdit.editView.requestFocus()
    }


    private fun monthsToDate(numOfMonths: Int) {
        selectedDate = Calendar.getInstance()
        selectedDate.add(Calendar.MONTH, numOfMonths)
        //  selectedDate.set(calender.get(Calendar.YEAR), calender.get(Calendar.MONTH) + numOfMonths, calender.get(Calendar.DAY_OF_MONTH))
    }

    private fun displayDatePicker() {
        todayCalendar.add(Calendar.DAY_OF_MONTH, 1)
        datePicker.datePicker.minDate = todayCalendar.timeInMillis
        if (!datePicker.isShowing) {
            binding.layOutServiceRemainder.rgServiceRemainderView.clearCheck()
            datePicker.show()
        }
    }

    companion object {
        const val ARG_JOBCARD_ID = "arg_jobcard_id"
        const val ARG_INVOICE_ID = "arg_invoice_id"
        const val ACTION_INVOICE = "arg_raise_invoice"
        const val ACTION_CLOSE = "arg_close"
        const val ARG_PDF = "arg_pdf"

        fun getIntent(
            context: Context,
            jobCardId: String,
            invoiceId: String?,
            action: String
        ): Intent {
            Timber.d("Jobcard id $jobCardId and invoiecId $invoiceId")
            return Intent(context, NewServiceReminderActivity::class.java).apply {
                putExtra(ARG_JOBCARD_ID, jobCardId)
                putExtra(ARG_INVOICE_ID, invoiceId)
                setAction(action)
            }
        }
    }
}

