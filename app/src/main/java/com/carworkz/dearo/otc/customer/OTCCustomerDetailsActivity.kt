package com.carworkz.dearo.otc.customer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.databinding.ActivityCustomerDetailsOtcBinding
import com.carworkz.dearo.databinding.ActivityOtcproformaBinding
import com.carworkz.dearo.domain.entities.Address
import com.carworkz.dearo.domain.entities.Customer
import com.carworkz.dearo.domain.entities.Invoice
import com.carworkz.dearo.domain.entities.VehicleType
import com.carworkz.dearo.interactionprovider.ToolBarInteractionProvider
import com.carworkz.dearo.otc.OtcProformaActivity
import com.carworkz.dearo.screencontainer.SingleTextActionScreenContainer
import com.carworkz.dearo.utils.Utility
import com.google.android.material.textfield.TextInputEditText
/*import kotlinx.android.synthetic.main.activity_customer_details_otc.*
import kotlinx.android.synthetic.main.base_layout.*
import kotlinx.android.synthetic.main.layout_vehicle_type.**/
import javax.inject.Inject

class OTCCustomerDetailsActivity : ScreenContainerActivity(), ToolBarInteractionProvider, CustomerDetailsContract.View, TextWatcher, CompoundButton.OnCheckedChangeListener {
    private lateinit var binding: ActivityCustomerDetailsOtcBinding
    @Inject
    lateinit var presenter: CustomerDetailsPresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    private val handler = Handler()
    private lateinit var mobileRunnable: Runnable
    private lateinit var pinCodeRunnable: Runnable
    private var customerDetailsBinding: ActivityCustomerDetailsOtcBinding? = null
    private var foundCustomer = false
    private var foundAddress = false
    private lateinit var customerId: String
    private lateinit var addressId: String

    private var vehicleType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as DearOApplication)
                .repositoryComponent
                .COMPONENT(CustomerDetailsPresenterModule(this))
                .inject(this)
        screenTracker.sendScreenEvent(this, ScreenTracker.SCREEN_OTC_CUSTOMER, this::class.java.simpleName)
        customerDetailsBinding = ActivityCustomerDetailsOtcBinding.bind(binding.parentView)
        binding.isRegisteredView.setOnCheckedChangeListener(this)
        binding.mobileView.addTextChangedListener(this)
        binding.pinCodeView.addTextChangedListener(this)
        mobileRunnable = Runnable {
            handler.removeCallbacks(mobileRunnable)
            foundCustomer = false
            foundAddress = false
            toggleView(true)
            if (Utility.isMobileNumberValid(binding.mobileView.text.toString())) {
                presenter.findCustomer(Utility.getServerAcceptableContactNumber(binding.mobileView.text.toString()))
            }
        }
        pinCodeRunnable = Runnable {
            handler.removeCallbacks(pinCodeRunnable)
            presenter.pinCodeCity(binding.pinCodeView.text.toString().toInt())
        }
        if (SharedPrefHelper.getWorkshopVehicleType().size > 1) {
            binding.layOutVehicleType.parentVehicleTypeView.visibility = View.VISIBLE
            setVehicleTypeToggle()
        } else {
            binding.layOutVehicleType.parentVehicleTypeView.visibility = View.GONE
        }
    }

    override fun createScreenContainer(): ScreenContainer = SingleTextActionScreenContainer(this)
    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityCustomerDetailsOtcBinding.inflate(layoutInflater)
        return binding
    }

    override fun getProgressView(): View = ProgressBar(this)

    override fun getToolBarTitle(): String = getString(R.string.customer_details_title)

    override fun getActionBtnTitle(): String = getString(R.string.next)

    override fun onActionBtnClick() {
        binding.mobileView.error = null
        binding.nameView.error = null
        binding.gstView.error = null
        binding.pinCodeView.error = null
        binding.emailView.error = null
        if (binding.mobileView.text!!.isNotEmpty() &&
                Utility.isMobileNumberValid(binding.mobileView.text.toString()) &&
            binding.nameView.text!!.isNotEmpty() &&
                checkGst(binding.gstView.text.toString()) &&
                checkEmail(binding.emailView.text.toString()) && checkPinCode()) {
            val customer = Customer().apply {
                mobile = Utility.getServerAcceptableContactNumber(binding.mobileView.text.toString())
                name = binding.nameView.text.toString()
                registeredDealer = binding.isRegisteredView.isChecked
                if (binding.isRegisteredView.isChecked) {
                    gst = binding.gstView.text.toString()
                }
            }
            val address = Address().apply {
                location = fetchTextOrNull(binding.localityView)
                street = fetchTextOrNull(binding.streetView)
                pincode = fetchTextOrNull(binding.pinCodeView)?.toInt()
            }
            if (!foundCustomer) {
                presenter.saveCustomerDetails(customer, address, vehicleType)
            } else {
                presenter.addOtcProforma(customerId, addressId, vehicleType)
            }
        } else {
            if (binding.mobileView.text!!.isEmpty()) {
                binding.mobileView.error = "Mobile Number is Required"
            }
            if (binding.mobileView.text!!.isNotEmpty() && !Utility.isMobileNumberValid(binding.mobileView.text.toString())) {
                binding.mobileView.error = "Mobile Number is Invalid"
            }
            if (binding.nameView.text!!.isEmpty()) {
                binding.nameView.error = "Name is Required"
            }
            if (!checkGst(binding.gstView.text.toString())) {
                binding.gstView.error = "GST Number is Invalid"
            }
            if (binding.isRegisteredView.isChecked && binding.gstView.text.toString().isEmpty()) {
                binding.gstView.error = "GST Number Cannot be Empty"
            }
            if (!checkEmail(binding.emailView.text.toString())) {
                binding.emailView.error = "Email is Invalid"
            }
            if (!checkPinCode()) {
                binding.pinCodeView.error = "Invalid Pincode"
            }
            if (binding.pinCodeView.text!!.isEmpty()) {
                binding.pinCodeView.error = "Cannot Be Blank"
            }
        }
    }

    private fun checkPinCode(): Boolean {
        return when {
            binding.pinCodeView.text!!.isEmpty() -> false
            binding.pinCodeView.text!!.isEmpty() && binding.cityView.text!!.isEmpty() -> false
            binding.pinCodeView.text!!.isNotEmpty() && binding.cityView.text!!.isNotEmpty() -> true
            else -> false
        }
    }

    private fun checkEmail(email: String): Boolean {
        return when {
            email.isEmpty() -> true
            email.isNotEmpty() && Utility.isEmailValid(email) -> true
            else -> false
        }
    }

    private fun checkGst(gst: String): Boolean {
        return when {
            binding.isRegisteredView.isChecked && gst.isEmpty() -> false
            binding.isRegisteredView.isChecked && gst.isNotEmpty() && !Utility.isValidGst(gst) -> false
            binding.isRegisteredView.isChecked && gst.isNotEmpty() && Utility.isValidGst(gst) -> true
            else -> true
        }
    }

    override fun moveToNextScreen(obj: Invoice) {
        startActivity(OtcProformaActivity.getIntent(this, obj.id!!, obj.invoiceId!!, vehicleType))
        finish()
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

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        when (s?.hashCode()) {
            binding.mobileView.text.hashCode() -> {
                handler.postDelayed(mobileRunnable, DELAY)
            }
            binding.pinCodeView.text.hashCode() -> {
                if (Utility.isPinCodeValid(s.toString())) {
                    handler.postDelayed(pinCodeRunnable, DELAY)
                }
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView) {
            binding.isRegisteredView -> {
                if (isChecked) {
                    binding.gstView.visibility = View.VISIBLE
                } else {
                    binding.gstView.visibility = View.GONE
                }
            }
        }
    }

    private fun fetchTextOrNull(view: View): String? {
        return if (view is TextInputEditText) {
            if (view.text.toString().isEmpty()) {
                null
            } else {
                view.text.toString()
            }
        } else if (view is EditText) {
            if (view.text.toString().isEmpty()) {
                null
            } else {
                view.text.toString()
            }
        } else {
            null
        }
    }

    override fun fillCustomerDetails(customer: Customer) {
        foundCustomer = true
        customerDetailsBinding?.customer = customer
        customerId = customer.id
        if (customer.address[0] != null) {
            foundAddress = true
            addressId = customer.address[0].id!!
        }
        customerDetailsBinding?.address = customer.address[0]
        customerDetailsBinding?.executePendingBindings()
        toggleView(false)
    }

    private fun toggleView(isEditable: Boolean) {
        binding.nameView.isEnabled = isEditable
        binding.emailView.isEnabled = isEditable
        binding.streetView.isEnabled = isEditable
        binding.gstView.isEnabled = isEditable
        binding.localityView.isEnabled = isEditable
        binding.pinCodeView.isEnabled = isEditable
        binding.cityView.isEnabled = isEditable
        binding.isRegisteredView.isEnabled = isEditable
        binding.mobileView.isEnabled = true
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    override fun setCity(s: String) {
        binding.cityView.error = null
        binding.cityView.setText(s)
    }

    override fun emailError(error: String) {
        binding.emailView.error = error
    }

    override fun cityError() {
        binding.cityView.error = "No City Found"
        binding.cityView.text?.clear()
    }

    private fun setVehicleTypeToggle() {
        vehicleType = VehicleType.VEHICLE_TYPE_CAR
        binding.layOutVehicleType.carVehicleView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        binding.layOutVehicleType.bikeVehicleView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        binding.layOutVehicleType.carIconView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_directions_car_white_24dp))
        binding.layOutVehicleType.bikeIconView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_motorcycle_black_24dp))
        binding.layOutVehicleType.carVehicleView.setOnClickListener {
            vehicleType = VehicleType.VEHICLE_TYPE_CAR
            binding.layOutVehicleType.carVehicleView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
            binding.layOutVehicleType.bikeVehicleView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            binding.layOutVehicleType.carIconView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_directions_car_white_24dp))
            binding.layOutVehicleType.bikeIconView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_motorcycle_black_24dp))
        }
        binding.layOutVehicleType.bikeVehicleView.setOnClickListener {
            vehicleType = VehicleType.VEHICLE_TYPE_BIKE
            binding.layOutVehicleType.carVehicleView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            binding.layOutVehicleType.bikeVehicleView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
            binding.layOutVehicleType.carIconView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_directions_car_black_24dp))
            binding.layOutVehicleType.bikeIconView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_motorcycle_white_24dp))
        }
    }

    companion object {
        private const val DELAY = 500L
        fun getIntent(context: Context): Intent {
            return Intent(context, OTCCustomerDetailsActivity::class.java)
        }
    }
}
