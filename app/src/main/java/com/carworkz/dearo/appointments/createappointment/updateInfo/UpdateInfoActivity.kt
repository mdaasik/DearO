package com.carworkz.dearo.appointments.createappointment.updateInfo

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.addeditvehicle.VehicleDetailsActivity
import com.carworkz.dearo.addjobcard.createjobcard.customercarsearch.CustomerCarSearchActivity
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityAmcDetailsBinding
import com.carworkz.dearo.databinding.ActivityUpdateInfoBinding
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.extensions.*
import com.carworkz.dearo.interactionprovider.MultiTitleInteractionProvider
import com.carworkz.dearo.screencontainer.MultiTitleActionScreenContainer
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.activity_update_info.**/
import timber.log.Timber
import javax.inject.Inject

class UpdateInfoActivity : ScreenContainerActivity(), MultiTitleInteractionProvider,
        View.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher, UpdateInfoContract.View {
    private lateinit var binding: ActivityUpdateInfoBinding
    private var customer = Customer()
    private var vehicle = Vehicle()

   /* private lateinit var binding.variantSpin: Spinner*/
    private lateinit var variantAdapter: ArrayAdapter<String>
  /*  private lateinit var  binding.etName: TextView
    private lateinit var binding.etPincode: EditText
    private lateinit var cityView: EditText
    private lateinit var binding.tvMake: TextView
    private lateinit var binding.tvModel: TextView
    private lateinit var  binding.tvVariant: TextView
    private lateinit var initJCBtn: Button*/
    private lateinit var variant: MutableList<Variant>
    private lateinit var titleOne: String
    private lateinit var titleTwo: String

    private lateinit var appointmentId: String
    private var isPincodeRequired: Boolean = false
    private var isVariantRequired: Boolean = false
    private var isNameRequired: Boolean = false

    @Inject
    lateinit var presenter: UpdateInfoPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        intent?.extras?.let {
            titleOne= it.getString(DISPLAY_ID).toString()
            titleTwo = it.getString(DISPLAY_TIME).toString()
            customer = it.getParcelable(CUSTOMER)!!
            vehicle = it.getParcelable(VEHICLE)!!
            appointmentId = it.getString(APPOINTMENT_ID).toString()
            isPincodeRequired = it.getBoolean(PINCODE_REQUIRED)
            isVariantRequired =it.getBoolean(VARIANT_REQUIRED)
            isNameRequired = it.getBoolean(NAME_REQUIRED)
        }
        super.onCreate(savedInstanceState)
        (application as DearOApplication).repositoryComponent
                .COMPONENT(UpdateInfoPresenterModule(this))
                .inject(this)
      /*  binding.variantSpin = find(R.id.variantSpin)
         binding.etName = find(R.id.et_name)
        binding.etPincode = find(R.id.et_pincode)
        cityView = find(R.id.et_city)
        initJCBtn = find(R.id.initBtn)
        binding.tvMake = find(R.id.tv_make)
        binding.tvModel = find(R.id.tv_model)
         binding.tvVariant = find(R.id.tv_variant)*/
        initList()
        if (!isPincodeRequired) {
            find<LinearLayout>(R.id.ll_city_parent).visibility = View.GONE
        }
        binding.initBtn.setOnClickListener(this)
        binding.variantSpin.onItemSelectedListener = this
        binding.etPincode.addTextChangedListener(this)

        if (isVariantRequired) {
            presenter.getVariant(vehicle.fuelType, vehicle.modelSlug)
            binding.variantSpin.visibility = View.VISIBLE
            binding.tvVariant.visibility = View.GONE
        } else {
            binding.variantSpin.visibility = View.GONE
             binding.tvVariant.visibility = View.VISIBLE
             binding.tvVariant.text = vehicle.variantName
        }

        binding.fuelView.forEach {
            it.isEnabled = isVariantRequired
        }
        addRadioButton(vehicle.fuelType, 1)
        binding.tvMake.text = vehicle.makeName
        binding.tvModel.text = vehicle.modelName
        binding.etName.text = Editable.Factory.getInstance().newEditable(this.customer.name)
        binding.etName.isEnabled = isNameRequired
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    private fun addRadioButton(name: String?, position: Int) {
        val fuelButton = RadioButton(this, null, R.style.RadioStyle)
        fuelButton.layoutParams = RadioGroup.LayoutParams(0, RadioGroup.LayoutParams.WRAP_CONTENT, 1f)
        fuelButton.id = position
        fuelButton.text = name
        fuelButton.background = ContextCompat.getDrawable(this, R.drawable.custom_radio_selector)
        fuelButton.padding = 15
        fuelButton.setTextColor(ContextCompat.getColorStateList(this, R.color.state_list))
        fuelButton.isClickable = true
        fuelButton.textAlignment = View.TEXT_ALIGNMENT_CENTER
        binding.fuelView.addView(fuelButton)
        Timber.d("Added $name RadioButton")
        if (vehicle.fuelType == name) {
            binding.fuelView.check(position)
        }
    }

    override fun onActionBtnClick() {
    }

    override fun getActionBtnTitle(): String = ""

    override fun getToolBarTitleOne(): String? = titleOne

    override fun getToolBarTitleTwo(): String = Utility.formatDate(titleTwo, Utility.TIMESTAMP, Utility.DATE_FORMAT_4, Utility.TIMEZONE_UTC)

    override fun createScreenContainer(): ScreenContainer = MultiTitleActionScreenContainer(this)
    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityUpdateInfoBinding.inflate(layoutInflater)
        return binding
    }


    override fun getProgressView(): View = ProgressBar(this)

    override fun onClick(view: View?) {
        when (view) {
            binding.initBtn -> {
                removeAllErrors()
                updateInfo()
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun afterTextChanged(s: Editable?) {
        if (s?.hashCode() == binding.etPincode.text.hashCode()) {
            if (Utility.isPinCodeValid(s.toString()))
                presenter.getCity(s.toString().toInt())
        } else {
            binding.etPincode.error = "Invalid Pincode"
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        if (p0 == binding.variantSpin) {
            vehicle.variantCode = if (position != 0) variant[position].code else null
        }
    }

    override fun showProgressIndicator() {
        super.showProgressBar()
    }

    override fun dismissProgressIndicator() {
        super.dismissProgressBar()
    }

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg) { _, _ -> finish() }
    }

    override fun displayVariant(variantList: List<Variant>) {
        variant = variant.subList(0, 1)
        variant.addAll(variantList)
        val othersVariant = Variant()
        othersVariant.name = VehicleDetailsActivity.OTHERS
        othersVariant.code = "others"
        variant.add(othersVariant)
        variantAdapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, variant.map { it.name })
        binding.variantSpin.adapter = variantAdapter
    }

    override fun displayCity(city: String) {
        binding.etCity.error = null
        binding.etCity.setText(city)
        val address = Address()
        address.pincode = binding.etPincode.text.toString().toInt()
        customer.address = ArrayList()
        customer.address.add(address)
    }

    override fun cityError(error: String) {
        binding.etPincode.error = "Invalid Pincode"
    }

    private fun initList() {
        variant = ArrayList()
        val selectVariant = Variant()
        selectVariant.name = "Select Variant"
        variant.add(0, selectVariant)
        variantAdapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, variant.map { it.name })
        binding.variantSpin.adapter = variantAdapter
    }

    private fun updateInfo() {
        if (customer.name.isNullOrEmpty() &&  binding.etName.text.toString().isEmpty()) {
             binding.etName.error = "Customer name cannot be empty"
            return
        }

        if (vehicle.variantCode.isNullOrEmpty()) {
            (binding.variantSpin.selectedView as TextView).error = "Please select a variant"
            // toast("Please select a variant")
            return
        }

        if (isPincodeRequired && binding.etPincode.text.isEmpty()) {
            binding.etPincode.error = "Pincode cannot be empty"
            return
        }

        if (isPincodeRequired && !Utility.isPinCodeValid(binding.etPincode.text.toString())) {
            binding.etPincode.error = "Invalid Pincode"
            return
        }

        if (isPincodeRequired && binding.etCity.text.isEmpty()) {
            toast("Valid city Required")
            return
        }

        val details = UpsertDetails()
        details.let {
            it.variantCode = vehicle.variantCode
            it.name =  binding.etName.text.toString()
            if (isPincodeRequired)
                it.pincode = binding.etPincode.text.toString().toInt()
        }
        presenter.upsertData(appointmentId, details)
    }

    private fun removeAllErrors() {
        binding.etPincode.error = null
         binding.etName.error = null
        (binding.variantSpin.selectedView as? TextView)?.error = null
    }

    override fun moveToNextScreen(upsertDetails: UpsertDetails) {
        val startCustomerSearch = Intent(this, CustomerCarSearchActivity::class.java)
        startCustomerSearch.putExtra(CustomerCarSearchActivity.APPOINTMENT_ID, upsertDetails.id)
        startCustomerSearch.putExtra(CustomerCarSearchActivity.CUSTOMER_MOBILE_NUMBER, upsertDetails.customer.mobile)
        startCustomerSearch.putExtra(CustomerCarSearchActivity.REGISTRATION_NUMBER, upsertDetails.vehicle.registrationNumber)
        startActivity(startCustomerSearch)
        finish()
    }

    companion object {
        const val CUSTOMER = "customer"
        const val VEHICLE = "vehicle"
        const val APPOINTMENT_ID = "appointment_id"
        const val DISPLAY_ID = "display_id"
        const val DISPLAY_TIME = "display_time"
        const val PINCODE_REQUIRED = "pincode"
        const val VARIANT_REQUIRED = "variant"
        const val NAME_REQUIRED = "name"
    }
}
