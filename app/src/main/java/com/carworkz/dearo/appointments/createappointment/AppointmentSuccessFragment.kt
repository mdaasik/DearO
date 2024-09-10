package com.carworkz.dearo.appointments.createappointment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.carworkz.dearo.LoggingFacade
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.ActionButtonClickEvent
import com.carworkz.dearo.base.BaseFragment
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.domain.entities.Appointment
import com.carworkz.dearo.domain.entities.ServicePackage
import com.carworkz.dearo.extensions.*
import com.carworkz.dearo.utils.Utility
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import java.util.*

class AppointmentSuccessFragment : BaseFragment(), EventsManager.EventSubscriber {

    private var interaction: ICreateAppointmentInteraction? = null
    private var appointment = Appointment()
    private lateinit var idView: TextView
    private lateinit var dateTimeView: TextView
    private lateinit var nameView: TextView
    private lateinit var numberView: TextView
    private lateinit var emailView: TextView
    private lateinit var modelView: TextView
    private lateinit var registrationView: TextView
    private lateinit var packageLayout: LinearLayout
    private lateinit var itemCountView: TextView
    private lateinit var totalRateView: TextView
    private lateinit var vehicleImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appointment = (context as CreateAppointmentActivity).appointment
        Timber.d("success fragment created")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_appointment_success, container, false)
        idView = view.find(R.id.tv_id)
        dateTimeView = view.find(R.id.tv_time)
        nameView = view.find(R.id.tv_name)
        numberView = view.find(R.id.tv_mobile)
        emailView = view.find(R.id.tv_email)
        modelView = view.find(R.id.tv_model)
        registrationView = view.find(R.id.tv_registration)
        packageLayout = view.find(R.id.ll_package_container)
        itemCountView = view.find(R.id.tv_item_count)
        totalRateView = view.find(R.id.tv_total_rate)
        vehicleImageView = view.find(R.id.iv_car_image)
        Timber.d("success fragment created view")
        setData()
        return view
    }

    override fun onResume() {
        super.onResume()
        EventsManager.register(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ICreateAppointmentInteraction) {
            interaction = context
        } else {
            throw IllegalStateException("Activity must implement ICreateJobCardInteraction interface ")
        }
    }

    override fun onPause() {
        super.onPause()
        EventsManager.unregister(this)
    }

    @Subscribe
    fun onNextBtnEvent(btn: ActionButtonClickEvent) {
        interaction?.onSuccess()
    }

    private fun setData() {
        idView.text = appointment.appointmentId
        dateTimeView.text = Utility.formatDate(appointment.date, Utility.TIMESTAMP, Utility.DATE_FORMAT_2, Utility.TIMEZONE_UTC)
        nameView.text = if (appointment.customer.name != null) appointment.customer.name else ""
        numberView.text = appointment.customer.mobile
        emailView.text = appointment.customer.email
        modelView.text = appointment.vehicle.let {
            if (it.variant != null) {
                "${it.makeName} ${it.modelName} ${it.variantName}"
            } else {
                "${it.makeName} ${it.modelName}"
            }
        }
        LoggingFacade.log("appointment debug", "is appointment null $appointment")
        LoggingFacade.log("appointment debug", "is vehicle  null ${appointment.vehicle}")
        registrationView.text = appointment.vehicle.registrationNumber.toUpperCase()
        var totalPackagesRate = 0f
        appointment.packages?.forEach { servicePkg ->
            inflatePackageLayout(servicePkg)
            servicePkg.rates?.forEach { totalPackagesRate += it.offerPrice.amount }
        }
        if (packageLayout.childCount == 0) {
            packageLayout.visibility = View.GONE
            itemCountView.visibility = View.GONE
            totalRateView.visibility = View.GONE
        } else {
            itemCountView.text = String.format(Locale.getDefault(), TEMPLATE_ITEMS, appointment.packages?.size
                    ?: 0)
            totalRateView.text = String.format(Locale.getDefault(), TEMPLATE_TOTAL, Utility.convertToCurrency(totalPackagesRate.toDouble()))
        }
    }

    private fun inflatePackageLayout(servicePackage: ServicePackage) {
        val view = LayoutInflater.from(activity).inflate(R.layout.row_package_detail, packageLayout, false)
        val packageNameView = view.find<TextView>(R.id.tv_package_name)
        val descView = view.find<TextView>(R.id.tv_package_type)
        val rateView = view.find<TextView>(R.id.tv_package_rate)
        val oilType = view.find<TextView>(R.id.tv_oil_type)
        packageNameView.text = servicePackage.name
        descView.text = servicePackage.description
        var total = 0f
        servicePackage.rates?.forEach { total += it.offerPrice.amount }
        rateView.text = Utility.convertToCurrency(total.toDouble())
        oilType.text = appointment.vehicle.engineOilType
        view.padding = 4
        packageLayout.addView(view)
        View(activity).let {
            it.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 8)
            it.backgroundColor = ContextCompat.getColor(requireContext(), R.color.light_grey)
            packageLayout.addView(it)
        }
    }

    companion object {
        fun newInstance(): AppointmentSuccessFragment {
            return AppointmentSuccessFragment()
        }

        const val TEMPLATE_ITEMS = "ITEMS (%d)"
        const val TEMPLATE_TOTAL = "TOTAL : %s"
    }
}
