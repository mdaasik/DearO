package com.carworkz.dearo.appointments.createappointment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.ActionButtonClickEvent
import com.carworkz.dearo.appointments.createappointment.appointmentDetails.AppointmentVehicleDetailsFragment
import com.carworkz.dearo.appointments.createappointment.servicePackages.ServicePackageFragment
import com.carworkz.dearo.appointments.createappointment.timeSlot.TimeSlotFragment
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.databinding.ActivityCreateAppointmentBinding
import com.carworkz.dearo.domain.entities.Appointment
import com.carworkz.dearo.extensions.alert
import com.carworkz.dearo.extensions.cancelButton
import com.carworkz.dearo.extensions.yesButton
import com.carworkz.dearo.interactionprovider.ToolBarInteractionProvider
import com.carworkz.dearo.screencontainer.SingleTextActionScreenContainer

class CreateAppointmentActivity : ScreenContainerActivity(), ToolBarInteractionProvider,
    ICreateAppointmentInteraction {
    private lateinit var binding: ActivityCreateAppointmentBinding

    //  private lateinit var progressBar: ProgressBar
//    private lateinit var container: FrameLayout
    private lateinit var screenContainer: SingleTextActionScreenContainer
    private var fragmentList = ArrayList<Fragment>()
    private var currentFragmentIndex = 0
    private var appointmentDetails = 0
    private var servicePackage = 1
    private var timeSlot = 2
    private var successPage = 3
    internal var appointment = Appointment()
    private lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //progressBar = find(R.id.pb_main)
//        container = find(R.id.fl_create_appointment_container)
        fragmentManager = supportFragmentManager
        intent?.extras?.let {
            appointment.vehicle.registrationNumber =
                it.getString(APPOINTMENT_REGISTRATION_NUMBER).toString()
            appointment.customer.mobile = it.getString(APPOINTMENT_MOBILE_NUMBER)
            appointment.id = it.getString(APPOINTMENT_ID)
        }

        fragmentList.add(
            AppointmentVehicleDetailsFragment.newInstance(
                appointment.vehicle.registrationNumber,
                appointment.customer.mobile,
                appointment.id
            )
        )
        if (SharedPrefHelper.isAppointmentServicePackageActive()) {
            fragmentList.add(ServicePackageFragment.newInstance())
        } else {
            servicePackage = -1
            timeSlot = 1
            successPage = 2
        }
        fragmentList.add(TimeSlotFragment.newInstance())
        fragmentList.add(AppointmentSuccessFragment.newInstance())
        startDefaultFragment()
    }

    override fun getProgressView(): View = ProgressBar(this)

    override fun createScreenContainer(): ScreenContainer {
        screenContainer = SingleTextActionScreenContainer(this)
        return screenContainer
    }

    override fun getViewBinding(
        inflater: LayoutInflater?, container: ViewGroup?, attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityCreateAppointmentBinding.inflate(layoutInflater)
        return binding
    }


    override fun getToolBarTitle(): String {
        return when (currentFragmentIndex) {
            appointmentDetails -> {
                APPOINTMENT_DETAILS_TITLE
            }

            servicePackage -> {
                SERVICE_PACKAGES_TITLE
            }

            timeSlot -> {
                TIME_SLOT_TITLE
            }

            else -> {
                ""
            }
        }
    }

    override fun getActionBtnTitle(): String {
        return if (currentFragmentIndex == successPage) {
            ""
        } else {
            "Next"
        }
    }

    override fun onActionBtnClick() {
        if (checkIfNetworkAvailable()) {
            EventsManager.post(ActionButtonClickEvent())
        }
    }

    override fun onSuccess() {
        when (currentFragmentIndex) {
            appointmentDetails -> {
                currentFragmentIndex =
                    if (SharedPrefHelper.isAppointmentServicePackageActive() && !appointment.skipPage) {
                        servicePackage
                    } else {
                        timeSlot
                    }
                fragmentManager.beginTransaction().replace(
                    R.id.fl_create_appointment_container,
                    fragmentList[currentFragmentIndex]
                ).addToBackStack(APPOINTMENT_DETAILS_TITLE).commit()
            }

            servicePackage -> {
                currentFragmentIndex = timeSlot
                fragmentManager.beginTransaction().replace(
                    R.id.fl_create_appointment_container,
                    fragmentList[currentFragmentIndex]
                ).addToBackStack(SERVICE_PACKAGES_TITLE).commit()
            }

            timeSlot -> {
                currentFragmentIndex = successPage
                fragmentManager.beginTransaction().replace(
                    R.id.fl_create_appointment_container,
                    fragmentList[currentFragmentIndex]
                ).addToBackStack(TIME_SLOT_TITLE).commit()
            }

            successPage -> {
                finish()
                // fragmentManager.beginTransaction().replace(R.id.fl_create_appointment_container, fragmentList[currentFragmentIndex]).addToBackStack(null).commit()
            }

            else -> {
                finish()
            }
        }
        screenContainer.refreshToolBar()
    }

    override fun onBackPressed() {
        when (currentFragmentIndex) {
            appointmentDetails -> {
                showExitDialog()
            }

            servicePackage -> {
                currentFragmentIndex = appointmentDetails
                fragmentManager.popBackStackImmediate()
            }

            timeSlot -> {
                currentFragmentIndex =
                    if (SharedPrefHelper.isAppointmentServicePackageActive() && !appointment.skipPage) {
                        servicePackage
                    } else {
                        appointmentDetails
                    }
                fragmentManager.popBackStackImmediate()
            }

            successPage -> {
                finish()
            }

            else -> {
                finish()
            }
        }
        screenContainer.refreshToolBar()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) if (currentFragmentIndex == successPage) {
            finish()
        } else {
            showExitDialog()
        }
        return true
    }

    override fun getSelectedServicePackages(): List<String>? {
        return appointment.packageIds
    }

    override fun onFailure() {
        toast("Something went wrong!")
    }

    private fun startDefaultFragment() {
        screenContainer.toolbar.title = APPOINTMENT_DETAILS_TITLE
        fragmentManager.beginTransaction()
            .add(R.id.fl_create_appointment_container, fragmentList[currentFragmentIndex])
            .addToBackStack(APPOINTMENT_DETAILS_TITLE).commit()
    }

    private fun showExitDialog() {
        alert("Discard Appointment?") {
            yesButton {
                finish()
            }
            cancelButton { it.dismiss() }
        }.show()
    }

    companion object {
        const val APPOINTMENT_DETAILS_TITLE = "Appointment Details"
        const val SERVICE_PACKAGES_TITLE = "Service Packages"
        const val TIME_SLOT_TITLE = "Time Slot"
        const val APPOINTMENT_REGISTRATION_NUMBER = "ARG_REG_NUMBER"
        const val APPOINTMENT_MOBILE_NUMBER = "ARG_MOBILE_NUMBER"
        const val APPOINTMENT_ID = "ARG_APPOINTMENT_ID"

        fun getIntent(
            context: Context,
            registrationNumber: String? = null,
            mobileNumber: String? = null
        ): Intent {
            return Intent(context, CreateAppointmentActivity::class.java).apply {
                putExtra(APPOINTMENT_REGISTRATION_NUMBER, registrationNumber)
                putExtra(APPOINTMENT_MOBILE_NUMBER, mobileNumber)
            }
        }

        fun getIntent(
            context: Context,
            registrationNumber: String? = null,
            mobileNumber: String? = null,
            appointmentId: String? = null
        ): Intent {
            return Intent(context, CreateAppointmentActivity::class.java).apply {
                putExtra(APPOINTMENT_REGISTRATION_NUMBER, registrationNumber)
                putExtra(APPOINTMENT_MOBILE_NUMBER, mobileNumber)
                putExtra(APPOINTMENT_ID, appointmentId)
            }
        }
    }
}
