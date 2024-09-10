package com.carworkz.dearo.addjobcard.addeditvehicle

/*import kotlinx.android.synthetic.main.activity_vehicle_details.*
import kotlinx.android.synthetic.main.activity_vehicle_details.view.*
import kotlinx.android.synthetic.main.base_layout.*
import kotlinx.android.synthetic.main.insurance_details.*
import kotlinx.android.synthetic.main.insurance_details.view.*
import kotlinx.android.synthetic.main.layout_amc_details.*
import kotlinx.android.synthetic.main.layout_amc_details.view.*
import kotlinx.android.synthetic.main.layout_vehicle_type.**/
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.autofill.AutofillManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.databinding.ActivityReadOtpBinding
import com.carworkz.dearo.databinding.ActivityVehicleDetailsBinding
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.interactionprovider.ToolBarInteractionProvider
import com.carworkz.dearo.screencontainer.SingleTextActionScreenContainer
import com.carworkz.dearo.utils.AnimationCallback
import com.carworkz.dearo.utils.Constants
import com.carworkz.dearo.utils.Utility
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Created by Farhan on 9/8/17.
 */

private const val TEMPLATE_DATE = "%d-%d-%d"

class VehicleDetailsActivity : ScreenContainerActivity(), ToolBarInteractionProvider,
    RadioGroup.OnCheckedChangeListener, VehicleDetailsContract.View,
    AdapterView.OnItemSelectedListener,
    View.OnClickListener {
    private lateinit var binding: ActivityVehicleDetailsBinding
    private lateinit var makeList: MutableList<Make>
    private lateinit var modelList: MutableList<Model>
    private lateinit var variantList: MutableList<Variant>
    private lateinit var filteredVariantList: MutableList<Variant>
    private lateinit var descList: MutableList<Variant>
    private lateinit var companyList: MutableList<InsuranceCompany>
    private var weight = 0.5f
    private var fuelTypes = ArrayList<String?>()
    private var transmissionTypes = ArrayList<String?>()
    private lateinit var registrationNumber: String
    private lateinit var customerId: String
    private lateinit var type: String
    private lateinit var vehicle: Vehicle
    private lateinit var variantAdapter: ArrayAdapter<String>
    private lateinit var variantDescAdapter: ArrayAdapter<String>
    private lateinit var modelAdapter: ArrayAdapter<String>
    private lateinit var makeAdapter: ArrayAdapter<String>
    private lateinit var insuranceCompanyAdapter: ArrayAdapter<String>
    private var calendar = Calendar.getInstance()
    private lateinit var insuranceDatePicker: DatePickerDialog
    private lateinit var amcDatePicker: DatePickerDialog
    private var defaultYear: Int = calendar.get(Calendar.YEAR)
    private var defaultMonth: Int = calendar.get(Calendar.MONTH)
    private var defaultDate: Int = calendar.get(Calendar.DAY_OF_MONTH)
    private var spinnerInit = 0
   /* private var insuranceBinding: InsuranceDetailsBinding? = null
    private var amcBinding: LayoutAmcDetailsBinding? = null*/
    private lateinit var datePickerDialog: DatePickerDialog
    private var registrationDate: Date? = null

    private var vehicleType: String? = null

    @Inject
    lateinit var presenter: VehicleDetailsPresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        getIntentData()
        super.onCreate(savedInstanceState)
        initComponent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val autoFillManager: AutofillManager = getSystemService(AutofillManager::class.java)
            autoFillManager.disableAutofillServices()
        }
        when (type) {
            ARG_ADD -> screenTracker.sendScreenEvent(
                this,
                ScreenTracker.SCREEN_ADD_VEHICLE,
                javaClass.name
            )

            ARG_EDIT -> screenTracker.sendScreenEvent(
                this,
                ScreenTracker.SCREEN_EDIT_VEHICLE,
                javaClass.name
            )

            ARG_VIEW -> screenTracker.sendScreenEvent(
                this,
                ScreenTracker.SCREEN_VIEW_VEHICLE,
                javaClass.name
            )
        }
        initLists()
        initializeViews()
        if (type != ARG_VIEW) {
            setListeners()
            if (checkIfNetworkAvailable()) {
                showProgressBar()
                presenter.getMake(vehicleType)
                presenter.getCompanyList()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.insuranceParentView -> {
                Utility.expandOrCollapseView(
                    binding.insuranceDetailView,
                    object : AnimationCallback {
                        override fun onAnimationEnd(endState: AnimationCallback.Toggle) {
                            if (endState == AnimationCallback.Toggle.UP) {
                                binding.toggleInsuranceView.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        this@VehicleDetailsActivity,
                                        R.drawable.ic_keyboard_arrow_up_black_24dp
                                    )
                                )
                            } else {
                                binding.toggleInsuranceView.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        this@VehicleDetailsActivity,
                                        R.drawable.ic_keyboard_arrow_down_black_24dp
                                    )
                                )
                            }
                        }
                    })
            }

            binding.amcParentView -> {
                Utility.expandOrCollapseView(binding.amcView, object : AnimationCallback {
                    override fun onAnimationEnd(endState: AnimationCallback.Toggle) {
                        if (endState == AnimationCallback.Toggle.UP) {
                            binding.toggleAmcView.setImageDrawable(
                                ContextCompat.getDrawable(
                                    this@VehicleDetailsActivity,
                                    R.drawable.ic_keyboard_arrow_up_black_24dp
                                )
                            )
                        } else {
                            binding.toggleAmcView.setImageDrawable(
                                ContextCompat.getDrawable(
                                    this@VehicleDetailsActivity,
                                    R.drawable.ic_keyboard_arrow_down_black_24dp
                                )
                            )
                        }
                    }
                })
            }

            binding.insuranceDetailView.insuranceDateView -> {
                insuranceDatePicker.show()
            }

            binding.amcView.amcDateView -> {
                amcDatePicker.show()
            }

            binding.amcView.isAvailableView -> {
                if (SharedPrefHelper.isAmcEnabled() && binding.amcView.isAvailableView.isChecked) {
                    binding.amcView.amcNumberView.visibility = View.VISIBLE
                    binding.amcView.dateParentView.visibility = View.VISIBLE
                } else {
                    binding.amcView.amcNumberView.visibility = View.GONE
                    binding.amcView.amcNumberView.text?.clear()
                    binding.amcView.dateParentView.visibility = View.GONE
                    binding.amcView.amcDateView.text?.clear()
                }
            }

            binding.registrationDateView -> {
                datePickerDialog.show()
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        p0?.setSelection(0)
    }

    override fun onItemSelected(spinner: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
        if (++spinnerInit > 4) {
            when (spinner) {
                binding.makeView -> {
                    if (pos == 0) {
                        vehicle.makeSlug = null
                        binding.modelView.setSelection(0)
                    } else {
                        binding.makeErrorView.error = null
                        vehicle.makeSlug = makeList[pos].slug
                        if (checkIfNetworkAvailable() && type != ARG_VIEW && vehicle.makeSlug != null) {
                            presenter.getModel(vehicle.makeSlug)
                        }
                    }
                    clearLists(
                        arrayListOf(
                            CLEAR_MODEL,
                            CLEAR_VARIANT,
                            CLEAR_FILTERED_VARIANT,
                            CLEAR_VARIANT_DESC
                        )
                    )
                }

                binding.modelView -> {
                    if (pos == 0) {
                        vehicle.modelSlug = null
                        binding.variantDescView.setSelection(0)
                        binding.variantView.setSelection(0)
                        binding.variantDescView.setSelection(0)
                        binding.fuelErrorView.visibility = View.GONE
                        binding.fuelView.clearCheck()
                    } else {
                        binding.modelErrorView.error = null
                        vehicle.modelSlug = modelList[pos].slug
                        if (checkIfNetworkAvailable() && type != ARG_VIEW) {
                            presenter.getVariant(null, vehicle.modelSlug)
                            vehicle.fuelType = null
                            vehicle.transmissionType = null
                            fuelTypes.clear()
                            binding.fuelView.removeAllViews()
                            binding.fuelView.clearCheck()
                            binding.transmissionView.clearCheck()
                            weight = 0.5f
                        }
                    }
                    clearLists(
                        arrayListOf(
                            CLEAR_VARIANT,
                            CLEAR_FILTERED_VARIANT,
                            CLEAR_VARIANT_DESC
                        )
                    )
                }

                binding.variantDescView -> {
                    binding.variantDescErrorView.error = null
                    if (pos != 0) {
                        vehicle.description = descList[pos].description
                        clearLists(arrayListOf(CLEAR_FILTERED_VARIANT))
                        filteredVariantList.addAll(variantList.filter { it.description == descList[pos].description && it.fuelType == descList[pos].fuelType }
                            .toMutableList())
                    } else {
                        vehicle.description = null
                        clearTransmission()
                        clearLists(arrayListOf(CLEAR_FILTERED_VARIANT))
                    }
                    variantAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        filteredVariantList.map { it.name })
                    binding.variantView.adapter = variantAdapter
                    if (filteredVariantList.size == 2) {
                        binding.variantView.setSelection(filteredVariantList.lastIndex)
                    }
                }

                binding.variantView -> {
                    if (pos != 0) {
                        val selected = filteredVariantList[pos]
                        Timber.d(selected.transmissionType)
                        vehicle.variantCode = selected.code
                        clearTransmission()
                        transmissionTypes.addAll(filteredVariantList.filter { it.transmissionType != null }
                            .map { it.transmissionType }.distinct())
                        if (transmissionTypes.isNotEmpty()) {
                            binding.transmissionContainer.visibility = View.VISIBLE
                            transmissionTypes.indices.forEach {
                                addRadioButton(transmissionTypes[it], it, TRANSMISSION)
                                if (selected.transmissionType == transmissionTypes[it]) {
                                    binding.transmissionView.check(it)
                                }
                            }
                        }
                    } else {
                        vehicle.variantCode = null
                        clearTransmission()
                    }
                    binding.variantErrorView.error = null
                }

                binding.insuranceDetailView.insuranceCompanyView -> {
                    if (pos != 0) {
                        vehicle.insurance.company = companyList[pos].name
                    } else {
                        vehicle.insurance.company = null
                    }
                }
            }
        }
    }

    override fun onCheckedChanged(radioGroup: RadioGroup?, itemId: Int) {
        if (itemId != -1) {
            when (radioGroup) {
                binding.fuelView -> {
                    binding.fuelErrorView.error = null
                    clearLists(arrayListOf(CLEAR_FILTERED_VARIANT, CLEAR_VARIANT_DESC))
                    vehicle.fuelType = fuelTypes[itemId]
                    descList.addAll(variantList.filter { !it.description.isNullOrBlank() && it.fuelType == vehicle.fuelType }
                        .distinctBy { it.description })
                    if (descList.size > 1) {
                        binding.variantDescErrorView.visibility = View.VISIBLE
                        variantDescAdapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_spinner_dropdown_item,
                            descList.map { it.description!! })
                        binding.variantDescView.adapter = variantDescAdapter
                        if (descList.size == 2) {
                            binding.variantDescView.setSelection(descList.lastIndex)
                        }
                    } else {
                        filteredVariantList.addAll(variantList.filter { it.fuelType == vehicle.fuelType })
                        binding.variantDescErrorView.visibility = View.GONE
                        variantAdapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_spinner_dropdown_item,
                            filteredVariantList.map { it.name })
                        binding.variantView.adapter = variantAdapter
                        if (filteredVariantList.size == 2) {
                            binding.variantView.setSelection(filteredVariantList.lastIndex)
                        }
                    }
                }

                binding.transmissionView -> {
                    vehicle.transmissionType = transmissionTypes[itemId]
                }
            }
        } else {
            when (radioGroup) {
                binding.fuelView -> vehicle.fuelType = null
                binding.transmissionView -> vehicle.transmissionType = null
            }
        }
    }

    override fun onActionBtnClick() {
        if (checkIfNetworkAvailable()) {
            val isValidRegistrationNumber = binding.registrationView.text.toString().isNotEmpty() &&
                    Utility.isRegistrationNumberValid(
                        binding.registrationView.text.toString().trim()
                    ) &&
                    Constants.BusinessConstants.STATE_LIST.find {
                        it == binding.registrationView.text.toString().substring(0..1)
                    } != null
            if (isValidRegistrationNumber &&
                vehicle.makeSlug != null &&
                vehicle.modelSlug != null &&
                ((descList.size > 1 && vehicle.description != null) || descList.size <= 1) && // >1 since 1st item in select description variant
                vehicle.variantCode != null &&
                vehicle.fuelType != null &&
                vehicle.transmissionType != null &&
                checkAmc() &&
                isInsuranceValidated()
            ) {
                vehicle.let {
                    it.customerId = customerId
                    it.registrationNumber =
                        Utility.getServerAcceptableRegistrationNumber(registrationNumber)
                    if (binding.registrationDateView.text.toString()
                            .isNotEmpty() && registrationDate != null
                    ) {
                        it.registrationDate =
                            Utility.formatToServerTime(registrationDate!!, Utility.DATE_FORMAT_5)
                    }
                    it.vinNumber = binding.chassisView.text.toString()
                    it.engineNumber = binding.engineView.text.toString()
                    it.color = binding.colourView.text.toString()
                    if (binding.insuranceDetailView.insuranceDateView.text?.isNotEmpty() == true) {

                        it.insurance.expiryDate = Utility.formatDate(
                            binding.insuranceDetailView.insuranceDateView.text.toString(),
                            Utility.DATE_FORMAT_7,
                            Utility.DATE_FORMAT_5,
                            Utility.TIMEZONE_UTC
                        )
                    }
                    it.insurance.policyNumber =
                        binding.insuranceDetailView.policyNumberView.text.toString().trimEnd()
                    it.amcDetails.number = binding.amcView.amcNumberView.text.toString().trimEnd()
                    it.amcDetails.isAvailable = binding.amcView.isAvailableView.isChecked
                }
                if (type == ARG_ADD) {
                    vehicle.vehicleType = vehicleType
                    presenter.addVehicle(vehicle)
                } else {
                    presenter.updateVehicle(vehicle)
                }
                Utility.hideSoftKeyboard(this)
            } else {
                if (binding.registrationView.text.isEmpty()) {
                    binding.registrationView.requestFocus()
                    binding.registrationView.error = "Cannot be empty"
                }
                if (!isValidRegistrationNumber || !Utility.isRegistrationNumberValid(
                        binding.registrationView.text.toString().trim()
                    )
                ) {
                    binding.registrationView.requestFocus()
                    binding.registrationView.error = "Invalid Registration Number"
                }
                if (vehicle.makeSlug == null) {
                    binding.makeErrorView.error = "Select Make"
                }
                if (vehicle.modelSlug == null) {
                    binding.modelErrorView.error = "Select Model"
                }
                if (vehicle.fuelType == null) {
                    binding.fuelErrorView.error = "Fuel Type Required"
                }
                if (descList.size > 1 && vehicle.description == null) {
                    binding.variantDescErrorView.error = "Variant Description Required"
                }
                if (vehicle.variantCode == null) {
                    binding.variantErrorView.error = "Select Variant"
                }
                if (!checkAmc()) {
                    when {
                        binding.amcView.amcDateView.text.toString()
                            .isEmpty() -> binding.amcView.amcDateView.error = "Date Required"

                        binding.amcView.amcNumberView.text.toString()
                            .isEmpty() -> binding.amcView.amcNumberView.error =
                            "AMC Number Required"
                    }
                }
            }
        }
    }

    private fun isInsuranceValidated(): Boolean {
        if (SharedPrefHelper.isInsuranceValidationEnabled()) {
            if (vehicle.insurance.company == null || vehicle.insurance.company?.length!! == 0) {
                toast("Insurance company can't be empty")
                return false
            }
            if (binding.insuranceDetailView.policyNumberView.text.toString()
                    .trimEnd().length == 0
            ) {
                binding.insuranceDetailView.policyNumberErrorView.error =
                    "policy number can't be empty"
                return false
            }
            if (binding.insuranceDetailView.insuranceDateView.text.toString().isEmpty()) {
                binding.insuranceDetailView.insuranceDateErrorView.error =
                    "expiry date can't be empty"
                binding.insuranceDetailView.policyNumberErrorView.error = null
                return false
            }
            binding.insuranceDetailView.policyNumberErrorView.error = null
            binding.insuranceDetailView.insuranceDateErrorView.error = null
            return true
        } else {
            return true
        }
    }

    override fun getActionBtnTitle(): String {
        return if (type != ARG_VIEW) "SAVE" else ""
    }

    override fun getToolBarTitle(): String {
        return when (type) {
            ARG_ADD -> "Add Vehicle"
            ARG_EDIT -> "Edit Vehicle"
            ARG_VIEW -> "Vehicle Details"
            else -> "Vehicle Details"
        }
    }

    override fun createScreenContainer(): ScreenContainer {
        return SingleTextActionScreenContainer(this)
    }

    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityVehicleDetailsBinding.inflate(layoutInflater)
        return binding
    }

    override fun getProgressView(): View = ProgressBar(this)


    override fun moveToNextScreen() {
        dismissProgressBar()
        finish()
    }

    override fun displayMake(makes: List<Make>) {
        clearLists(arrayListOf(CLEAR_MAKE, CLEAR_MODEL, CLEAR_VARIANT))
        makeList.addAll(makes)
        makeAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            makeList.map { it.name!! })
        binding.makeView.adapter = makeAdapter
    }

    override fun displayModel(models: List<Model>) {
        modelList.addAll(models as ArrayList<Model>)
        modelAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            modelList.map { it.name!! })
        binding.modelView.adapter = modelAdapter
    }

    override fun displayVariant(variants: List<Variant>) {
        variantList.addAll(variants as ArrayList)
        fuelTypes.addAll(variants.map { it.fuelType }.distinct())
        if (fuelTypes.isNotEmpty()) {
            binding.fuelErrorView.visibility = View.VISIBLE
            weight = 1f / fuelTypes.size
            Timber.d(fuelTypes.toString())
        } else {
            binding.fuelErrorView.visibility = View.GONE
        }
        fuelTypes.indices.forEach { addRadioButton(fuelTypes[it], it, FUEL) }
        if (fuelTypes.size == 1) {
            binding.fuelView.check(0)
        }
    }

    override fun displayCompanySuggestions(companyList: List<InsuranceCompany>) {
        this.companyList.addAll(companyList)
        insuranceCompanyAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            this.companyList.map { it.name })
        binding.insuranceDetailView.insuranceCompanyView.adapter = insuranceCompanyAdapter
    }

    override fun showProgressIndicator() {
        showProgressBar()
    }

    override fun dismissProgressIndicator() {
        dismissProgressBar()
    }

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg) { _, _ -> finish() }
    }

    private fun clearTransmission() {
        transmissionTypes.clear()
        binding.transmissionView.removeAllViews()
        binding.transmissionView.clearCheck()
        binding.transmissionContainer.visibility = View.GONE
    }

    private fun checkAmc(): Boolean {
        return if (SharedPrefHelper.isAmcEnabled() && binding.amcView.isAvailableView.isChecked) {
            when {
                binding.amcView.amcDateView.text.toString().isEmpty() -> false
                binding.amcView.amcNumberView.text.toString().isEmpty() -> false
                else -> true
            }
        } else {
            true
        }
    }

    private fun addRadioButton(name: String?, position: Int, type: String) {
        val radioButton = RadioButton(this)
        radioButton.layoutParams =
            RadioGroup.LayoutParams(0, RadioGroup.LayoutParams.WRAP_CONTENT, weight)
        radioButton.id = position
        radioButton.text = name
        radioButton.background = ContextCompat.getDrawable(this, R.drawable.custom_radio_selector)
        radioButton.buttonDrawable = null
        radioButton.setPadding(10)
        radioButton.setTextColor(ContextCompat.getColor(this, R.color.state_list))
        radioButton.setTextColor(ContextCompat.getColorStateList(this, R.color.state_list))
        radioButton.textAlignment = View.TEXT_ALIGNMENT_CENTER
        if (type == FUEL) {
            binding.fuelView.addView(radioButton)
        } else {
            radioButton.isEnabled = false
            binding.transmissionView.addView(radioButton)
        }
        Timber.d("Added $name RadioButton")
    }

    private fun clearLists(clearAll: ArrayList<String>) {
        clearAll.forEach { listName ->
            when (listName) {
                CLEAR_MAKE -> {
                    makeList = makeList.subList(0, 1)
                    makeAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        makeList.map { it.name!! })
                    binding.makeView.adapter = makeAdapter
                    Timber.d("Clearing Make")
                }

                CLEAR_MODEL -> {
                    modelList = modelList.subList(0, 1)
                    modelAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        modelList.map { it.name!! })
                    binding.modelView.adapter = modelAdapter
                    Timber.d("Clearing Model")
                }

                CLEAR_VARIANT_DESC -> {
                    descList = descList.subList(0, 1)
                    variantDescAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        descList.map { it.description!! })
                    binding.variantDescView.adapter = variantDescAdapter
                    Timber.d("Clearing Description")
                }

                CLEAR_FILTERED_VARIANT -> {
                    filteredVariantList = filteredVariantList.subList(0, 1)
                    variantAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        filteredVariantList.map { it.name })
                    binding.variantView.adapter = variantAdapter
                    Timber.d("Clearing Filtered Variant")
                }

                CLEAR_VARIANT -> {
                    variantList.clear()
                    Timber.d("Clearing Variant")
                }
            }
        }
    }

    private fun initLists() {
        makeList = ArrayList()
        modelList = ArrayList()
        descList = ArrayList()
        variantList = ArrayList()
        filteredVariantList = ArrayList()
        companyList = ArrayList()

        val selectMake = Make()
        selectMake.name = "Select Make"
        selectMake.slug = null
        makeList.add(0, selectMake)

        val selectModel = Model()
        selectModel.name = "Select Model"
        selectModel.slug = null
        modelList.add(0, selectModel)

        val selectDesc = Variant()
        selectDesc.name = "Select Variant"
        selectDesc.description = "Select Variant Description"
        descList.add(0, selectDesc)

        val selectVariant = Variant()
        selectVariant.name = "Select Variant"
        selectVariant.code = ""
        filteredVariantList.add(0, selectVariant)

        val selectCompany = InsuranceCompany("Select Insurance Provider", DUMMY_SLUG)
        companyList.add(selectCompany)

        insuranceCompanyAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            companyList.map { it.name })
        binding.insuranceDetailView.insuranceCompanyView.adapter = insuranceCompanyAdapter

        makeAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            makeList.map { it.name!! })
        binding.makeView.adapter = makeAdapter

        modelAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            modelList.map { it.name!! })
        binding.modelView.adapter = modelAdapter

        variantDescAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            descList.map { it.description!! })
        binding.variantDescView.adapter = variantDescAdapter

        variantAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            filteredVariantList.map { it.name })
        binding.variantView.adapter = variantAdapter
    }

    private fun initializeViews() {
         /*insuranceBinding = InsuranceDetailsBinding.bind(binding.insuranceDetailView)
         amcBinding = LayoutAmcDetailsBinding.bind(binding.amcView)*/
        binding.insuranceDetailView.cashlessParentView.visibility = View.GONE
        if (SharedPrefHelper.isAmcEnabled()) {
            binding.amcParentView.visibility = View.VISIBLE
            binding.amcView.root.visibility = View.VISIBLE
        } else {
            binding.amcParentView.visibility = View.GONE
            binding.amcView.root.visibility = View.GONE
        }
        binding.insuranceDetailView.claimNumberView.visibility = View.GONE
        when (type) {
            ARG_VIEW -> {
                /*Disable all views*/
                binding.makeView.isEnabled = false
                binding.modelView.isEnabled = false
                binding.variantDescView.isEnabled = false
                binding.variantView.isEnabled = false
                binding.colourView.isEnabled = false
                binding.engineView.isEnabled = false
                binding.chassisView.isEnabled = false
                binding.insuranceDetailView.insuranceDateView.isEnabled = false
                binding.amcView.amcDateView.isEnabled = false
                binding.insuranceDetailView.policyNumberView.isEnabled = false
                binding.amcView.amcNumberView.isEnabled = false
                binding.amcView.isAvailableView.isEnabled = false
                binding.insuranceDetailView.insuranceCompanyView.isEnabled = false
                binding.registrationDateView.isEnabled = false
                if (SharedPrefHelper.isAmcEnabled()) {
                    binding.amcView.root.visibility = View.VISIBLE
                }
                binding.insuranceDetailView.root.visibility = View.VISIBLE
                binding.fuelErrorView.visibility = View.VISIBLE
                weight = 0.5f
                addRadioButton(vehicle.fuelType, 0, FUEL)
                (binding.fuelView.getChildAt(0) as RadioButton).isEnabled = false
                (binding.fuelView.getChildAt(0) as RadioButton).isChecked = true
                (binding.fuelView.getChildAt(0) as RadioButton).setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )

                for (i in 0 until binding.transmissionView.childCount) {
                    (binding.transmissionView.getChildAt(i) as RadioButton).isEnabled = false
                    (binding.transmissionView.getChildAt(i) as RadioButton).setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.black
                        )
                    )
                }
                addRadioButton(vehicle.transmissionType, 0, TRANSMISSION)
                (binding.transmissionView.getChildAt(0) as RadioButton).isEnabled = false
                (binding.transmissionView.getChildAt(0) as RadioButton).isChecked = true
                (binding.transmissionView.getChildAt(0) as RadioButton).setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )

                /*Update all values to views*/
                binding.makeView.adapter = ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    arrayOf(vehicle.make.name)
                )
                modelAdapter = ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    arrayOf(vehicle.model.name)
                )
                binding.modelView.adapter = modelAdapter

                variantAdapter = if (vehicle.variant != null) {
                    ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        arrayOf(vehicle.variant.name)
                    )
                } else {
                    ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        arrayOf("N/A")
                    )
                }
                variantDescAdapter = if (vehicle.description != null) {
                    ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        arrayOf(vehicle.description)
                    )
                } else {
                    ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        arrayOf("N/A")
                    )
                }
                binding.variantView.adapter = variantAdapter
                binding.variantDescView.adapter = variantDescAdapter

                binding.colourView.setText(vehicle.color ?: "")

                binding.registrationView.setText(vehicle.registrationNumber)
                binding.registrationDateView.setText(
                    Utility.formatDate(
                        vehicle.registrationDate,
                        Utility.TIMESTAMP,
                        Utility.DATE_FORMAT_7,
                        Utility.TIMEZONE_UTC
                    )
                )

                companyList.add(InsuranceCompany(vehicle.insurance.company ?: "N/A", DUMMY_SLUG))
                insuranceCompanyAdapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    companyList.map { it.name })

                binding.insuranceDetailView.insuranceCompanyView.adapter = insuranceCompanyAdapter
                binding.insuranceDetailView.insuranceCompanyView.setSelection(companyList.lastIndex)

                if (!vehicle.vinNumber.isNullOrBlank()) {
                    binding.chassisView.setText(vehicle.vinNumber)
                } else {
                    binding.chassisView.setText("N/A")
                }

                if (!vehicle.engineNumber.isNullOrEmpty()) {
                    binding.engineView.setText(vehicle.engineNumber)
                } else {
                    binding.engineView.setText("N/A")
                }
                binding.amcView.details = vehicle.amcDetails.apply {
                    this?.expiryDate = Utility.formatDate(
                        this?.expiryDate,
                        Utility.TIMESTAMP,
                        Utility.DATE_FORMAT_7,
                        Utility.TIMEZONE_UTC
                    )
                }
                binding.amcView.executePendingBindings()
                binding.insuranceDetailView.details = vehicle.insurance.apply {
                    this?.expiryDate = Utility.formatDate(
                        this?.expiryDate,
                        Utility.TIMESTAMP,
                        Utility.DATE_FORMAT_7,
                        Utility.TIMEZONE_UTC
                    )
                }
                binding.insuranceDetailView.executePendingBindings()
                binding.addVehicleParentView.requestFocus()
            }

            ARG_ADD -> {
                if (SharedPrefHelper.getWorkshopVehicleType().size > 1) {
                    binding.vehicleType.parentVehicleTypeView.visibility = View.VISIBLE
                    setVehicleTypeToggle()
                } else {
                    binding.vehicleType.parentVehicleTypeView.visibility = View.GONE
                }
                binding.amcView.root.visibility = View.GONE
                binding.registrationView.setText(registrationNumber)
                binding.amcView.isAvailableView.setOnClickListener(this)
                binding.registrationDateView.setOnClickListener(this)
                val today = Calendar.getInstance()
                datePickerDialog = DatePickerDialog(
                    this,
                    { _, year, month, day ->
                        registrationDate = Calendar.getInstance().apply {
                            set(year, month, day)
                        }.time
                        binding.registrationDateView.setText(
                            String.format(
                                Locale.getDefault(),
                                TEMPLATE_DATE,
                                day,
                                month + 1,
                                year
                            )
                        )
                    },
                    today.get(Calendar.YEAR),
                    today.get(Calendar.MONTH),
                    today.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
                insuranceDatePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)
                    binding.insuranceDetailView.insuranceDateView.setText(
                        String.format(
                            Locale.getDefault(),
                            TEMPLATE_DATE,
                            dayOfMonth,
                            month + 1,
                            year
                        )
                    )
                    binding.insuranceDetailView.insuranceDateErrorView.error = null
                }, defaultYear, defaultMonth, defaultDate)
                insuranceDatePicker.datePicker.minDate = System.currentTimeMillis()

                amcDatePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)
                    binding.amcView.amcDateView.setText(
                        String.format(
                            Locale.getDefault(),
                            TEMPLATE_DATE,
                            dayOfMonth,
                            month + 1,
                            year
                        )
                    )
                    vehicle.amcDetails.expiryDate = "$year-${month + 1}-$dayOfMonth"
                }, defaultYear, defaultMonth, defaultDate)
                amcDatePicker.datePicker.minDate = System.currentTimeMillis()
            }

            else -> {
                binding.registrationView.isEnabled = true
            }
        }
    }

    private fun getIntentData() {
        if (intent.extras != null) {
            type = intent.extras!!.getString(ARG_TYPE).toString()
            when (type) {
                ARG_ADD -> {
                    registrationNumber =
                        intent.extras!!.getString(ARG_REGISTRATION_NUMBER).toString()
                    customerId = intent.extras!!.getString(ARG_CUSTOMER_ID).toString()
                    vehicle = Vehicle()
                }

                ARG_VIEW -> {
                    vehicle = intent.extras!!.get(VEHICLE)!! as Vehicle
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
                }
            }
        }
    }

    private fun setListeners() {
        binding.fuelView.setOnCheckedChangeListener(this)
        binding.transmissionView.setOnCheckedChangeListener(this)
        binding.makeView.onItemSelectedListener = this
        binding.modelView.onItemSelectedListener = this
        binding.variantDescView.onItemSelectedListener = this
        binding.variantView.onItemSelectedListener = this
        binding.insuranceParentView.setOnClickListener(this)
        binding.amcParentView.setOnClickListener(this)
        binding.insuranceDetailView.insuranceDateView.setOnClickListener(this)
        binding.amcView.amcDateView.setOnClickListener(this)
        binding.insuranceDetailView.insuranceCompanyView.onItemSelectedListener = this
    }

    private fun initComponent() {
        (application as DearOApplication)
            .repositoryComponent
            .COMPONENT(VehicleDetailsPresenterModule(this))
            .inject(this)
    }

    private fun setVehicleTypeToggle() {
        vehicleType = VehicleType.VEHICLE_TYPE_CAR

        binding.vehicleType.carVehicleView.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.colorPrimary
            )
        )
        binding.vehicleType.bikeVehicleView.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.white
            )
        )
        binding.vehicleType.carIconView.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_directions_car_white_24dp
            )
        )
        binding.vehicleType.bikeIconView.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_motorcycle_black_24dp
            )
        )
        binding.vehicleType.carVehicleView.setOnClickListener {
            vehicleType = VehicleType.VEHICLE_TYPE_CAR
            binding.vehicleType.carVehicleView.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimary
                )
            )
            binding.vehicleType.bikeVehicleView.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            binding.vehicleType.carIconView.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_directions_car_white_24dp
                )
            )
            binding.vehicleType.bikeIconView.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_motorcycle_black_24dp
                )
            )
            presenter.getMake(vehicleType)
        }

        binding.vehicleType.bikeVehicleView.setOnClickListener {
            vehicleType = VehicleType.VEHICLE_TYPE_BIKE
            binding.vehicleType.carVehicleView.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            binding.vehicleType.bikeVehicleView.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimary
                )
            )
            binding.vehicleType.carIconView.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_directions_car_black_24dp
                )
            )
            binding.vehicleType.bikeIconView.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_motorcycle_white_24dp
                )
            )
            presenter.getMake(vehicleType)
        }
    }

    companion object {
        const val CLEAR_MAKE = "clear make"
        const val CLEAR_MODEL = "clear model"
        const val CLEAR_FILTERED_VARIANT = "clear_filtered_variant"
        const val CLEAR_VARIANT = "clear_variant"
        const val CLEAR_VARIANT_DESC = "clear_variant_desc"
        const val OTHERS = "Others"
        const val ARG_TYPE = "ARG_TYPE"
        const val ARG_VIEW = "ARG_VIEW"
        const val ARG_EDIT = "ARG_EDIT"
        const val ARG_ADD = "ARG_ADD"
        const val ARG_REGISTRATION_NUMBER = "ARG_REGISTRATION_NUMBER"
        const val VEHICLE = "VEHICLE"
        const val ARG_CUSTOMER_ID = "ARG_CUSTOMER_ID"
        const val FUEL = "fuel"
        const val TRANSMISSION = "transmission"

        fun getAddVehicleIntent(
            context: Context,
            registrationNumber: String,
            customerId: String
        ): Intent {
            return Intent(context, VehicleDetailsActivity::class.java).apply {
                putExtra(ARG_TYPE, ARG_ADD)
                putExtra(ARG_REGISTRATION_NUMBER, registrationNumber)
                putExtra(ARG_CUSTOMER_ID, customerId)
            }
        }

        fun getViewVehicleIntent(context: Context, vehicle: Vehicle): Intent {
            return Intent(context, VehicleDetailsActivity::class.java).apply {
                putExtra(ARG_TYPE, ARG_VIEW)
                putExtra(VEHICLE, vehicle)
            }
        }
    }
}
