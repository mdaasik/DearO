package com.carworkz.dearo.appointments.status

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityAppointmentStatusBinding
import com.carworkz.dearo.databinding.ActivityPdcBinding
import com.carworkz.dearo.databinding.ActivityUpdateInfoBinding
import com.carworkz.dearo.domain.entities.Appointment
import com.carworkz.dearo.domain.entities.AppointmentStatus
import com.carworkz.dearo.interactionprovider.ToolBarImgInteractionProvider
import com.carworkz.dearo.screencontainer.ActionImgScreenContainer
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.activity_appointment_status.*
import kotlinx.android.synthetic.main.activity_appointment_status.remarkEt
import kotlinx.android.synthetic.main.activity_pdc.*
import kotlinx.android.synthetic.main.base_layout.**/
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class AppointmentStatusChangeActivity : ScreenContainerActivity(), ToolBarImgInteractionProvider,
    AppointmentStatusContract.View {
    private lateinit var binding: ActivityAppointmentStatusBinding
    @Inject
    lateinit var presenter: AppointmentStatusPresenter

    lateinit var appointment: Appointment
    val details = AppointmentStatus()
    lateinit var id: String
    private lateinit var datePicker: DatePickerDialog
    private var deliveryDateCalendar = Calendar.getInstance()
    private var selectedYear: Int = deliveryDateCalendar.get(Calendar.YEAR)
    private var selectedMonth: Int = deliveryDateCalendar.get(Calendar.MONTH)
    private var selectedDay: Int = deliveryDateCalendar.get(Calendar.DAY_OF_MONTH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = intent.getStringExtra(ID).toString()

        (application as DearOApplication)
            .repositoryComponent
            .COMPONENT(AppointmentStatusPresenterModule(this))
            .inject(this)

        if (checkIfNetworkAvailable()) {
            presenter.getAppointmentById(id)
        }
        deliveryDateCalendar.add(Calendar.DAY_OF_MONTH,1)
        selectedYear = deliveryDateCalendar.get(Calendar.YEAR)
        selectedMonth = deliveryDateCalendar.get(Calendar.MONTH)
        selectedDay = deliveryDateCalendar.get(Calendar.DAY_OF_MONTH)

        datePicker = DatePickerDialog(
            this, { _, year, monthOfYear, dayOfMonth ->
                selectedYear = year
                selectedDay = dayOfMonth
                selectedMonth = monthOfYear

                binding.dateTV.text =
                    Utility.formatDate(Utility.DATE_FORMAT_3, year, monthOfYear, dayOfMonth)
            },
            selectedYear, selectedMonth, selectedDay
        )

        datePicker.datePicker.minDate = deliveryDateCalendar.timeInMillis

        binding.statusSpinner.adapter = ArrayAdapter(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            AppointmentStatus.STATUS_LIST
        )
        binding.statusSpinner.prompt = "Select Status"
        binding.statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                details.status = AppointmentStatus.STATUS_LIST[position]

                if (arrayOf(
                        "Appointment Rescheduled",
                        "Not Contactable",
                        "Call Back/Under Follow Up",
                        "Hung Up/Refuse to Speak",
                        "Others"
                    ).contains(details.status)
                ) {
                    //visible date picker
                    binding.dateLL.visibility=View.VISIBLE
                    selectedYear = deliveryDateCalendar.get(Calendar.YEAR)
                    selectedMonth = deliveryDateCalendar.get(Calendar.MONTH)
                    selectedDay = deliveryDateCalendar.get(Calendar.DAY_OF_MONTH)
                    binding.dateTV.text = Utility.formatDate(Utility.DATE_FORMAT_3, selectedYear, selectedMonth, selectedDay)
                }
                else
                {
                    binding.dateLL.visibility=View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        binding.remarkEt.doOnTextChanged { charSequence: CharSequence?, _: Int, _: Int, _: Int ->
            details.remarks = charSequence.toString()
        }

        binding.dateTV.setOnClickListener { datePicker.show() }
        binding.save.setOnClickListener { validateAndSave() }
    }//onCreate


    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }


    override fun createScreenContainer(): ScreenContainer = ActionImgScreenContainer(this)
    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityAppointmentStatusBinding.inflate(layoutInflater)
        return binding
    }

    override fun getProgressView(): View = binding.baseLayout.pbMain

    override fun getToolBarTitle(): String = "Update Lead Status"

    override fun getNavigationImage(): Int = R.drawable.ic_arrow_back_white_24dp

    override fun onSecondaryActionBtnClick() = Timber.d("No Action")

    override fun getActionBarImage(): Int = 0// R.drawable.ic_save_white_24dp

    override fun getSecondaryActionBarImage(): Int = 0

    override fun onActionBtnClick() {
    }

    override fun onFetchAppointment(appointment: Appointment) {
        this.appointment = appointment
    }

    override fun onSaveSuccess() {
        toast("Saved Successfully")
        finish()
    }

    fun validateAndSave() {
        //prepare and validate data
        details.appointmentStatus = Appointment.STATUS_IN_PROGRESS
        details.id = appointment.id

        if (details.status.isNullOrBlank()) {
            showGenericError("Please select status")
            return
        }
        if (details.remarks.isNullOrBlank()) {
            showGenericError("Please enter remarks")
            return
        }
        if (arrayOf(
                "Appointment Rescheduled",
                "Not Contactable",
                "Call Back/Under Follow Up",
                "Hung Up/Refuse to Speak",
                "Others"
            ).contains(details.status).not()
        ) {
            details.appointmentStatus = Appointment.STATUS_CANCELLED
        }

        deliveryDateCalendar.set(Calendar.YEAR, selectedYear)
        deliveryDateCalendar.set(Calendar.MONTH, selectedMonth)
        deliveryDateCalendar.set(Calendar.DAY_OF_MONTH, selectedDay)
        deliveryDateCalendar.set(Calendar.SECOND, 0)

        details.date = Utility.formatToServerTime(deliveryDateCalendar.time, Utility.DATE_FORMAT_1)

        //now save the the data
        presenter.saveDetails(details)
    }


    override fun showProgressIndicator() {
        super.showProgressBar()
    }

    override fun dismissProgressIndicator() {
        super.dismissProgressBar()
    }

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg)
    }

    companion object {
        const val ID = "id"
        fun getIntent(context: Context, id: String): Intent {
            return Intent(context, AppointmentStatusChangeActivity::class.java).putExtra(ID, id)
        }
    }
}
