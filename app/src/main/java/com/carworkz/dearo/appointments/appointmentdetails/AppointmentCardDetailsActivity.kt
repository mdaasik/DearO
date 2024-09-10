package com.carworkz.dearo.appointments.appointmentdetails

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.BuildConfig
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.appointments.createappointment.servicePackages.PackageDetailActivity
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityAppointmentDetailsBinding
import com.carworkz.dearo.databinding.ActivitySoldAmcDetailsBinding
import com.carworkz.dearo.domain.entities.Appointment
import com.carworkz.dearo.domain.entities.ServicePackage
import com.carworkz.dearo.extensions.*
import com.carworkz.dearo.interactionprovider.MultiTitleInteractionProvider
import com.carworkz.dearo.screencontainer.MultiTitleActionScreenContainer
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.activity_appointment_details.**/
import timber.log.Timber
import java.util.*
import javax.inject.Inject

const val TEMPLATE_ITEMS = "ITEMS (%d)"
class AppointmentCardDetailsActivity : ScreenContainerActivity(),
    AppointmentCardDetailsContract.View, MultiTitleInteractionProvider {
    private lateinit var binding: ActivityAppointmentDetailsBinding
    @Inject
    lateinit var presenter: AppointmentCardDetailsPresenter

    private lateinit var nameView: TextView
    private lateinit var numberView: TextView
    private lateinit var emailView: TextView
    private lateinit var modelView: TextView
    private lateinit var registrationView: TextView
    private lateinit var packageLayout: LinearLayout
    private lateinit var itemCountView: TextView
    private lateinit var itemTotalView: TextView
    private var appointment = Appointment()

    override fun getToolBarTitleOne() = appointment.appointmentId

    override fun getToolBarTitleTwo() = Utility.formatDate(
        appointment.date,
        Utility.TIMESTAMP,
        Utility.DATE_FORMAT_2,
        Utility.TIMEZONE_UTC
    )!!

    override fun getActionBtnTitle() = ""

    override fun onActionBtnClick() {
        return finish()
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

    override fun displayDetails(appointment: Appointment) {
        var itemCount = 0
        appointment.packages?.forEach {
            inflatePackageLayout(it)
            itemCountView.text = String.format(Locale.getDefault(), TEMPLATE_ITEMS, ++itemCount)
        }
        var totalOfPackage = 0f
        appointment.packages?.forEach { servicePackage ->
            servicePackage.rates?.forEach { totalOfPackage += it.offerPrice.amount }
        }
        if (appointment.address?.pincode != null) {
            val addressStringBuilder = StringBuilder()
            if (appointment.address!!.location.isNullOrEmpty().not()) addressStringBuilder.append(
                appointment.address!!.location
            ).append(", ")
            if (appointment.address!!.street.isNullOrEmpty().not()) addressStringBuilder.append(
                appointment.address!!.street
            ).append(",")
            if (appointment.address!!.city.isNullOrEmpty().not()) {
                addressStringBuilder.append("\n").append(appointment.address!!.city)
                addressStringBuilder.append(", ").append(appointment.address!!.state).append(",")
                addressStringBuilder.append("\n").append(appointment.address!!.pincode)
            }

            binding.addressView.text = addressStringBuilder
            binding.addressParentView.visibility = View.VISIBLE
            binding.navigateView.setOnClickListener {
                try {
                    val map = "http://maps.google.co.in/maps?q=$addressStringBuilder"
                    val i = Intent(Intent.ACTION_VIEW, Uri.parse(map))
                    startActivity(i)
                } catch (e: ActivityNotFoundException) {
                    toast("Google Maps not available")
                    Timber.d("nothing here")
                }
            }
        } else {
            binding.addressParentView.visibility = View.GONE
        }

        if (appointment.serviceAdvisor != null) {
            binding.serviceAdvisorParentView.visibility = View.VISIBLE
            binding.serviceAdvisorView.text = appointment.serviceAdvisor!!.name
        } else {
            binding.serviceAdvisorParentView.visibility = View.GONE
        }

        appointment.remarks?.let { remarks ->
            binding.commentsParentView.visibility = View.VISIBLE
            binding.commentsView.text = remarks
        } ?: run {
            binding.commentsParentView.visibility = View.GONE
        }

        itemTotalView.text = getString(R.string.package_total, totalOfPackage.toString()) // Dummy
    }

    private fun inflatePackageLayout(servicePackage: ServicePackage) {
        val view = layoutInflater.inflate(R.layout.row_package_detail, packageLayout, false)
        val packageNameView = view.find<TextView>(R.id.tv_package_name)
        val descView = view.find<TextView>(R.id.tv_package_type)
        val rateView = view.find<TextView>(R.id.tv_package_rate)
        val oilType = view.find<TextView>(R.id.tv_oil_type)
        packageNameView.text = servicePackage.name
        descView.text = servicePackage.description
        var total = 0f
        servicePackage.rates?.forEach { total += it.offerPrice.amount }
        rateView.text = Utility.convertToCurrency(total.toDouble())
        oilType.visibility = View.GONE
        view.setOnClickListener { startActivity<PackageDetailActivity>(PackageDetailActivity.PACKAGE to servicePackage) }
        packageLayout.addView(view)
        View(this).let {
            it.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 8)
            it.backgroundColor = ContextCompat.getColor(this, R.color.light_grey)
            packageLayout.addView(it)
        }
    }

    override fun createScreenContainer() = MultiTitleActionScreenContainer(this)
    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityAppointmentDetailsBinding.inflate(layoutInflater)
        return binding
    }


    override fun getProgressView() = ProgressBar(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        if (intent.extras != null) {
            appointment = intent.extras?.getParcelable(APPOINTMENT)!!

        }
        super.onCreate(savedInstanceState)
        (application as DearOApplication).repositoryComponent
            .COMPONENT(AppointmentCardDetailsPresenterModule(this))
            .inject(this)
        init()
        presenter.getDetails(appointment.id ?: "")
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    private fun init() {
        if (BuildConfig.FLAVOR.contains("bike")) {
            binding.ivCarImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_bike_grey_24dp
                )
            )
        } else {
            binding.ivCarImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_car))
        }
        nameView = find(R.id.tv_name)
        numberView = find(R.id.tv_mobile)
        emailView = find(R.id.tv_email)
        modelView = find(R.id.tv_model)
        registrationView = find(R.id.tv_registration)
        itemCountView = find(R.id.tv_item_count)
        itemTotalView = find(R.id.tv_item_total)
        packageLayout = find(R.id.ll_package_container)
        nameView.text = appointment.customer.name
        numberView.text = appointment.customer.mobile
        emailView.text = appointment.customer.email
        val variant =
            if (appointment.vehicle.variantName != null) appointment.vehicle.variantName else ""

        @SuppressLint("SetTextI18n")
        modelView.text = "${appointment.vehicle.makeName} ${appointment.vehicle.modelName} $variant"

        registrationView.text = appointment.vehicle.registrationNumber.toUpperCase()
    }

    companion object {
        const val APPOINTMENT = "APPOINTMENT"
    }
}
