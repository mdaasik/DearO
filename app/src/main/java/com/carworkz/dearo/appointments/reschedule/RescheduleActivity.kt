package com.carworkz.dearo.appointments.reschedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.appointments.createappointment.timeSlot.TimeSlotAdapter
import com.carworkz.dearo.base.DialogFactory
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.databinding.ActivityRescheduleBinding
import com.carworkz.dearo.domain.entities.Reschedule
import com.carworkz.dearo.domain.entities.Slot
import com.carworkz.dearo.events.CardStatusChangeEvent
import com.carworkz.dearo.extensions.*
import com.carworkz.dearo.interactionprovider.ToolBarInteractionProvider
import com.carworkz.dearo.screencontainer.SingleTextActionScreenContainer
import com.carworkz.dearo.utils.Utility
import java.util.*
import javax.inject.Inject

class RescheduleActivity : ScreenContainerActivity(), ToolBarInteractionProvider,
    View.OnClickListener, RadioGroup.OnCheckedChangeListener, CalendarView.OnDateChangeListener,
    RescheduleContract.View {
    private lateinit var binding: ActivityRescheduleBinding
    @Inject
    lateinit var presenter: ReschedulePresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    private lateinit var pBar: ProgressBar
    private lateinit var calendarView: CalendarView
    private lateinit var rvTimeSlot: RecyclerView
    private lateinit var rgReason: RadioGroup
    private lateinit var etReason: EditText
    private lateinit var rescheduleBtn: Button
    private lateinit var timeSlotAdapter: TimeSlotAdapter
    private var reschedule = Reschedule()
    private lateinit var id: String
    private val today = Calendar.getInstance()
    private lateinit var selectedDate: String
    private lateinit var type: String
    private lateinit var notWorkingView: TextView
    private lateinit var reasonParentView: LinearLayout

    override fun createScreenContainer(): ScreenContainer = SingleTextActionScreenContainer(this)
    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityRescheduleBinding.inflate(layoutInflater)
        return binding
    }


    override fun getProgressView(): View = pBar

    override fun onCreate(savedInstanceState: Bundle?) {
        type = intent.extras?.getString(ARG_TYPE, ARG_IS_RESCHEDULE).toString()
        super.onCreate(savedInstanceState)
        (application as DearOApplication).repositoryComponent
            .COMPONENT(ReschedulePresenterModule(this))
            .inject(this)
        id = intent.extras?.getString(ARG_ID).toString()
        pBar = ProgressBar(this)
        calendarView = find(R.id.calendarView)
        rvTimeSlot = find(R.id.rv_timeSlot)
        notWorkingView = find(R.id.tv_notWorking)
        reasonParentView = find(R.id.ll_reason_parent)
        val reasonView = find<View>(R.id.reasonView)
        rgReason = reasonView.find(R.id.rg_reason)
        etReason = reasonView.find(R.id.et_reason)
        rescheduleBtn = reasonView.find(R.id.rescheduleBtn)
        rescheduleBtn.setOnClickListener(this)
        rgReason.setOnCheckedChangeListener(this)
        when (type) {
            ARG_IS_RESCHEDULE -> {
                screenTracker.sendScreenEvent(
                    this,
                    ScreenTracker.SCREEN_RESCHEDULE,
                    this.javaClass.simpleName
                )
                rescheduleBtn.text = getString(R.string.appointment_reschedule_btn_reschedule)
                calendarView.minDate = today.timeInMillis
                val futureCalender = Calendar.getInstance()
                futureCalender.set(
                    today.get(Calendar.YEAR),
                    today.get(Calendar.MONTH),
                    today.get(Calendar.DAY_OF_MONTH) + 15
                )
                calendarView.maxDate = futureCalender.timeInMillis
                selectedDate = Utility.formatToServerTime(today.time, Utility.DATE_FORMAT_5)
                calendarView.setOnDateChangeListener(this)
                rvTimeSlot.layoutManager = GridLayoutManager(this, 4)
                presenter.getTimeSlot(
                    id,
                    Utility.formatToServerTime(today.time, Utility.DATE_FORMAT_5)
                )
            }

            ARG_IS_REJECT_APPOINTMENT -> {
                screenTracker.sendScreenEvent(
                    this,
                    ScreenTracker.SCREEN_REJECT,
                    this.javaClass.simpleName
                )
                find<RadioButton>(R.id.rb_not_interested).visibility = View.VISIBLE
                find<RadioButton>(R.id.rb_no_driver).text =
                    getString(R.string.appointment_reschedule_label_option_pick_not_available)
                rescheduleBtn.text = getString(R.string.appointment_reschedule_btn_decline)
                calendarView.visibility = View.GONE
                rvTimeSlot.visibility = View.GONE
            }

            ARG_IS_CANCEL_INVOICE -> {
                screenTracker.sendScreenEvent(
                    this,
                    ScreenTracker.SCREEN_CANCEL_INVOICE,
                    this.javaClass.simpleName
                )
                find<RadioButton>(R.id.rb_ws_closed).text =
                    getString(R.string.appointment_reschedule_label_option_wrongly_invoice)
                find<RadioButton>(R.id.rb_load_full).text =
                    getString(R.string.appointment_reschedule_label_option_incorrect_info)
                find<RadioButton>(R.id.rb_no_driver).visibility = View.GONE
                rescheduleBtn.text =
                    getString(R.string.appointment_reschedule_btn_submit_and_cancel)
                calendarView.visibility = View.GONE
                rvTimeSlot.visibility = View.GONE
            }

            ARG_IS_CANCEL_AMC -> {
                screenTracker.sendScreenEvent(
                    this,
                    ScreenTracker.SCREEN_CANCEL_INVOICE,
                    this.javaClass.simpleName
                )
                find<RadioButton>(R.id.rb_ws_closed).text =
                    getString(R.string.appointment_reschedule_label_option_wrongly_invoice)
                find<RadioButton>(R.id.rb_load_full).text =
                    getString(R.string.appointment_reschedule_label_option_incorrect_info)
                find<RadioButton>(R.id.rb_no_driver).visibility = View.GONE
                rescheduleBtn.text =
                    getString(R.string.appointment_reschedule_btn_submit_and_cancel)
                calendarView.visibility = View.GONE
                rvTimeSlot.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun getToolBarTitle(): String = when (type) {
        ARG_IS_CANCEL_INVOICE -> "Cancel Invoice"
        ARG_IS_REJECT_APPOINTMENT -> "Decline Appointment"
        else -> "Reschedule Appointment"
    }

    override fun launchWhatsapp(contactNumber: String, message: String) {
        Utility.sendWhatsApp(this, contactNumber, message)
    }

    override fun getActionBtnTitle(): String = ""

    override fun onActionBtnClick() = Unit

    override fun onClick(view: View?) {
        when (view) {
            rescheduleBtn -> {

                when (type) {
                    ARG_IS_RESCHEDULE -> {
                        if (validate() && timeSlotAdapter.getTimeSlot() != null) {
                            reschedule.date =
                                "$selectedDate ${timeSlotAdapter.getTimeSlot()!!.time}"
                            presenter.rescheduleAppointment(id, reschedule)
                        } else {
                            if (!validate()) {
                                toast("Please Give a Reason")
                            } else {
                                toast("Please Select time Slot")
                            }
                        }
                    }
                    ARG_IS_REJECT_APPOINTMENT -> {
                        if (validate()) {
                            //                        reschedule.reason using the same POJO for reason
                            presenter.rejectAppointment(id, reschedule)
                        } else {
                            toast("Please Give a Reason")
                        }
                    }
                    ARG_IS_CANCEL_INVOICE -> {
                        if (SharedPrefHelper.isNotifyOnCancelInvoice() && SharedPrefHelper.isNotifyEnabled()) {
                            DialogFactory.notifyAlertDialog(
                                this,
                                "Inform Customer",
                                "An Sms will be sent to customer informing about cancelled invoice",
                                true,
                                SharedPrefHelper.getDefaultOptionCancelInvoice(),
                                false,
                                object : DialogFactory.NotifyButtonListener {
                                    override fun positiveButton(notify: Boolean?) {
                                        reschedule.notifyCustomer = notify ?: false
                                        if (validate()) {
                                            presenter.cancelInvoice(id, reschedule)
                                        } else {
                                            toast("Please Give a Reason")
                                        }
                                    }

                                    override fun neutralButton() {
                                    }
                                }).show()
                        } else {
                            if (validate()) {
                                presenter.cancelInvoice(id, reschedule)
                            } else {
                                toast("Please Give a Reason")
                            }
                        }
                    }

                    ARG_IS_CANCEL_AMC -> {
                        if (validate()) {
                            presenter.cancelAMC(id, reschedule.reason)
                        } else {
                            toast("Please Give a Reason")
                        }
                    }
                }
            }
        }
    }

    private fun validate(): Boolean {
        return if (rgReason.checkedRadioButtonId != R.id.rb_other) {
            rgReason.checkedRadioButtonId != -1
        } else {
            reschedule.reason = etReason.text.toString()
            etReason.text.toString().isNotEmpty()
        }
    }

    override fun onCheckedChanged(view: RadioGroup?, button: Int) {
        when (button) {
            R.id.rb_ws_closed -> {
                reschedule.reason =
                    if (type == ARG_IS_CANCEL_INVOICE || type == ARG_IS_CANCEL_AMC) {
                        getString(R.string.appointment_reschedule_label_option_wrongly_invoice)
                    } else {
                        getString(R.string.appointment_reschedule_reason_workshop_closed)
                    }
                etReason.visibility = View.GONE
            }

            R.id.rb_load_full -> {
                reschedule.reason = if (type == ARG_IS_CANCEL_INVOICE || type == ARG_IS_CANCEL_AMC) {
                    getString(R.string.appointment_reschedule_label_option_incorrect_info)
                } else {
                    getString(R.string.appointment_reschedule_reason_service_load_full)
                }
                etReason.visibility = View.GONE
            }

            R.id.rb_no_driver -> {
                reschedule.reason =
                    getString(R.string.appointment_reschedule_label_driver_not_available_for_pickup)
                etReason.visibility = View.GONE
            }

            R.id.rb_not_interested -> {
                reschedule.reason =
                    getString(R.string.appointment_reschedule_reason_not_interested_in_make_model)
                etReason.visibility = View.GONE
            }

            R.id.rb_other -> {
                etReason.visibility = View.VISIBLE
            }
        }
    }

    override fun onSelectedDayChange(view: CalendarView, year: Int, month: Int, dayOfMonth: Int) {
        selectedDate = "$year-${month + 1}-$dayOfMonth"
        presenter.getTimeSlot(id, selectedDate)
    }

    override fun moveToNextScreen() {
        when (type) {
            ARG_IS_CANCEL_INVOICE -> EventsManager.post(
                CardStatusChangeEvent(
                    CardStatusChangeEvent.CARD_TYPE_INVOICE,
                    CardStatusChangeEvent.CARD_STATUS_PROFORMA
                )
            )
        }
        finish()
    }

    override fun dateError(errorMsg: String) {
        toast(errorMsg)
    }

    override fun slotError(errorMsg: String) {
        toast(errorMsg)
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

    override fun displayTimeSlot(timeSlot: List<Slot>) {
        rvTimeSlot.visibility = View.VISIBLE
        notWorkingView.visibility = View.GONE
        reasonParentView.visibility = View.VISIBLE
        rescheduleBtn.visibility = View.VISIBLE
        timeSlotAdapter = TimeSlotAdapter(timeSlot)
        rvTimeSlot.adapter = timeSlotAdapter
    }

    override fun showClosedWorkshopView() {
        rvTimeSlot.visibility = View.GONE
        notWorkingView.visibility = View.VISIBLE
        reasonParentView.visibility = View.GONE
        rescheduleBtn.visibility = View.GONE
    }

    companion object {
        const val ARG_ID = "ID"
        const val ARG_TYPE = "type"
        const val ARG_IS_RESCHEDULE = "IS_RESCHEDULE"
        const val ARG_IS_REJECT_APPOINTMENT = "IS_REJECT"
        const val ARG_IS_CANCEL_INVOICE = "IS_CANCEL_INVOICE"
        const val ARG_IS_CANCEL_AMC = "IS_CANCEL_AMC"
    }
}
