package com.carworkz.dearo.mycustomers.cutomervehiclehistory

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
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.CreateJobCardActivity
import com.carworkz.dearo.addjobcard.createjobcard.customercarsearch.CustomerCarSearchActivity
import com.carworkz.dearo.addjobcard.quickjobcard.QuickJobCardActivity
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.appointments.createappointment.CreateAppointmentActivity
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.databinding.ActivityCustomerVehicleHistoryBinding
import com.carworkz.dearo.databinding.ActivityMrnBinding
import com.carworkz.dearo.domain.entities.CustomerVehicleDetails
import com.carworkz.dearo.domain.entities.History
import com.carworkz.dearo.domain.entities.Invoice
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.extensions.find
import com.carworkz.dearo.interactionprovider.ToolBarInteractionProvider
import com.carworkz.dearo.pdf.Source
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.screencontainer.SingleTextActionScreenContainer
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.activity_customer_vehicle_history.*
import kotlinx.android.synthetic.main.row_my_customer.**/
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class CustomerVehicleHistoryActivity : ScreenContainerActivity(),
        ToolBarInteractionProvider,
        CustomerVehicleHistoryContract.View,
        MyCustomerVehicleInteraction,
        View.OnClickListener
{
    private lateinit var binding: ActivityCustomerVehicleHistoryBinding
    @Inject
    lateinit var presenter: CustomerVehicleHistoryPresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    @Inject
    lateinit var pdfMediator: PdfMediator

    private val calender = Calendar.getInstance()
    private val currentYear = calender.get(Calendar.YEAR)
    private val currentMonth = calender.get(Calendar.MONTH)
    private val currentDay = calender.get(Calendar.DAY_OF_MONTH)
    private var dateChanged = false
    private lateinit var customerVehicleDetails: CustomerVehicleDetails
    private var delay = 0L

    private lateinit var datePicker: DatePickerDialog

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        customerVehicleDetails = intent.extras?.getParcelable(ARG_CARD)!!
        initComponent()
        super.onCreate(savedInstanceState)
        screenTracker.sendScreenEvent(this, ScreenTracker.SCREEN_CUSTOMER_CARD_DETAILS, this.javaClass.name)
        val includeView = find<View>(R.id.include)
        val makeModelVariantView = includeView.find<TextView>(R.id.tv_make_model_variant)
        val regNameView = includeView.find<TextView>(R.id.tv_reg_name)
        val recentDateView = includeView.find<TextView>(R.id.tv_recent_date)

        @SuppressLint("SetTextI18n")
        makeModelVariantView.text = "${customerVehicleDetails.vehicle.makeName} - ${customerVehicleDetails.vehicle.modelName} - ${customerVehicleDetails.vehicle.fuelType}"
        @SuppressLint("SetTextI18n")
        regNameView.text = "${customerVehicleDetails.vehicle.registrationNumber}, ${customerVehicleDetails.customer.name}"

        if (customerVehicleDetails.vehicle.serviceDate != null)
        {
            binding.dateView.text = Utility.formatDate(customerVehicleDetails.vehicle.serviceDate, Utility.TIMESTAMP, Utility.DATE_FORMAT_4, Utility.TIMEZONE_UTC)
            binding.setReminderView.text = getString(R.string.reminder_reschedule)
            val selectedDate = Utility.dateToCalender(customerVehicleDetails.vehicle.serviceDate!!, Utility.TIMESTAMP)
            datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                binding.dateView.text = Utility.formatDate("$dayOfMonth-${month + 1}-$year", Utility.DATE_FORMAT_7, Utility.DATE_FORMAT_4, Utility.TIMEZONE_UTC)
                dateChanged = true
                presenter.saveServiceReminderDate(customerVehicleDetails.vehicle.vehicleId!!, "$year-${month + 1}-$dayOfMonth")
            }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH))
        }
        else
        {
            binding.dateView.text = getString(R.string.reminder_text_not_updated)
            datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                binding.dateView.text = Utility.formatDate("$dayOfMonth-${month + 1}-$year", Utility.DATE_FORMAT_7, Utility.DATE_FORMAT_4, Utility.TIMEZONE_UTC)
                dateChanged = true
                binding.setReminderView.text = getString(R.string.reminder_reschedule)
                presenter.saveServiceReminderDate(customerVehicleDetails.vehicle.vehicleId!!, "$year-${month + 1}-$dayOfMonth")
            }, currentYear, currentMonth, currentDay)
        }
        datePicker.datePicker.minDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
        }.timeInMillis

        recentDateView.visibility = View.GONE
        binding.historyView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.historyView.setItemViewCacheSize(20)
        binding.historyView.isDrawingCacheEnabled = true
        binding.historyView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        setActions()
    }

    override fun onResume()
    {
        super.onResume()
        Handler().postDelayed({ presenter.getJobCardData(customerVehicleDetails.id) }, delay)
    }

    override fun onDestroy()
    {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == CreateJobCardActivity.RESULT_CODE && requestCode == REQUEST_CODE)
        {
            delay = 1000L
        }
    }

    override fun onClick(v: View?)
    {
        when (v)
        {
            binding.include.createJobCardView   ->
            {
                startActivity(CustomerCarSearchActivity.getIntent(this, customerVehicleDetails.vehicle.registrationNumber, customerVehicleDetails.customer.mobile, null))
            }
            binding.include.bookAppointmentView ->
            {
                startActivity(CreateAppointmentActivity.getIntent(this, customerVehicleDetails.vehicle.registrationNumber, customerVehicleDetails.customer.mobile))
            }
            binding.include.btnCall            ->
            {
                Utility.makeCall(this, customerVehicleDetails.customer.mobile.toString())
            }
            binding.setReminderView     ->
            {
                datePicker.show()
            }
        }
    }

    override fun createScreenContainer(): ScreenContainer
    {
        return SingleTextActionScreenContainer(this)
    }

    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityCustomerVehicleHistoryBinding.inflate(layoutInflater)
        return binding
    }

    override fun getToolBarTitle(): String = "Vehicle History for ${customerVehicleDetails.customer.name}"

    override fun getActionBtnTitle(): String = ""

    override fun onActionBtnClick() = Unit

    override fun getProgressView(): View = ProgressBar(this)

    override fun showProgressIndicator() = super.showProgressBar()

    override fun dismissProgressIndicator() = super.dismissProgressBar()

    override fun showGenericError(errorMsg: String)
    {
        displayError(errorMsg)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when (item.itemId)
        {
            android.R.id.home ->
            {
                if (dateChanged)
                {
                    setResult(Activity.RESULT_OK)
                    finish()
                    true
                }
                else
                {
                    super.onOptionsItemSelected(item)
                }
            }
            else              -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed()
    {
        if (dateChanged)
        {
            setResult(Activity.RESULT_OK)
        }
        finish()
    }

    override fun serviceDateError()
    {
        displayError("Sorry", "Unable To save Service Reminder Date") { _, _ ->
            finish()
        }
    }

    override fun displayJCList(list: List<History>?)
    {
        if (list != null)
        {
            Utility.setVisibility(false,  binding.noHistoryView)
            Utility.setVisibility(true,  binding.historyView)
            binding.historyView.adapter = HistoryAdapter(list, customerVehicleDetails.vehicle.registrationNumber, this)
        }
        else
        {
            Utility.setVisibility(true,  binding.noHistoryView)
            Utility.setVisibility(false,  binding.historyView)
        }
    }

    override fun getJobCardById(id: String)
    {
        presenter.getJobCard(id)
    }

    override fun startInvoicePreview(invoice: Invoice, jobCardId: String, source: Source)
    {
        pdfMediator.startInvoicePreviewPdf(this, invoice, jobCardId, source)
    }

    override fun moveToNextScreen(obj: JobCard)
    {
        Timber.d("Found JobCard : ${obj.jobCardId}")
        obj.vehicle = customerVehicleDetails.vehicle

        if (SharedPrefHelper.isQuickFlow())
        {
            startActivityForResult(QuickJobCardActivity.getIntent(this, obj.status != JobCard.STATUS_INITIATED, false, true, obj.vehicle.registrationNumber, obj), REQUEST_CODE)
        }
        else
        {
            val startCreateJobCardIntent = Intent(this, CreateJobCardActivity::class.java)
            startCreateJobCardIntent.putExtra(CreateJobCardActivity.ARG_JOB_CARD, obj)
            startCreateJobCardIntent.putExtra(CreateJobCardActivity.ARG_VEHICLE_ID, obj.vehicle.registrationNumber)
            startCreateJobCardIntent.putExtra(CreateJobCardActivity.ARG_ISHISTORY, ARG_HISTORY)
            if (obj.status == JobCard.STATUS_INITIATED)
                startCreateJobCardIntent.putExtra(CreateJobCardActivity.ARG_IS_VIEW_ONLY, false)
            else
            {
                startCreateJobCardIntent.putExtra(CreateJobCardActivity.ARG_IS_VIEW_ONLY, true)
            }
            startActivityForResult(startCreateJobCardIntent, REQUEST_CODE)
        }
    }

    private fun setActions()
    {
        binding.include.parentCustomerAction.visibility = View.VISIBLE
        binding.include.separatorView.visibility = View.VISIBLE
        binding.include.createJobCardView.setOnClickListener(this)
        binding.include.bookAppointmentView.setOnClickListener(this)
        binding.include.btnCall.setOnClickListener(this)
        binding.setReminderView.setOnClickListener(this)
    }

    private fun initComponent()
    {
        (application as DearOApplication)
                .repositoryComponent
                .COMPONENT(CustomerVehicleHistoryPresenterModule(this))
                .inject(this)
    }

    companion object
    {
        const val ARG_CARD = "CARD"
        const val ARG_HISTORY = true
        const val REQUEST_CODE = 1000

        fun getIntent(context: Context, card: CustomerVehicleDetails): Intent
        {
            return Intent(context, CustomerVehicleHistoryActivity::class.java).apply {
                putExtra(ARG_CARD, card)
            }
        }
    }
}
