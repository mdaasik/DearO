package com.carworkz.dearo.appointments.createappointment.appointmentDetails

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.ActionButtonClickEvent
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.appointments.createappointment.CreateAppointmentActivity
import com.carworkz.dearo.appointments.createappointment.ICreateAppointmentInteraction
import com.carworkz.dearo.base.BaseActivity
import com.carworkz.dearo.base.BaseFragment
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.databinding.FragmentAppointmentDetailsBinding
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.extensions.*
import com.carworkz.dearo.utils.Constants
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.fragment_appointment_details.*
import kotlinx.android.synthetic.main.layout_vehicle_type.**/
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

/**
 * Created by kush on 24/11/17.
 */
class AppointmentVehicleDetailsFragment : BaseFragment(),
    AppointmentDetailsContract.View,
    RadioGroup.OnCheckedChangeListener,
    EventsManager.EventSubscriber, TextWatcher,
    AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentAppointmentDetailsBinding
    private var interaction: ICreateAppointmentInteraction? = null
    private var mInteraction: ICreateAppointmentInteraction? = null
    private lateinit var makeList: MutableList<Make>
    private lateinit var modelList: MutableList<Model>
    private lateinit var variantList: MutableList<Variant>
    private lateinit var descList: MutableList<Variant>
    private lateinit var filteredVariantList: MutableList<Variant>
    private lateinit var makeAdapter: ArrayAdapter<String>
    private lateinit var modelAdapter: ArrayAdapter<String>
    private lateinit var variantAdapter: ArrayAdapter<String>
    private lateinit var variantDescAdapter: ArrayAdapter<String>
    private lateinit var activity: CreateAppointmentActivity
    private var appointment = Appointment()
    private var handler = Handler()
    private lateinit var registerRunnable: Runnable
    private lateinit var numberRunnable: Runnable
    private lateinit var pincodeRunnable: Runnable
    private var weight = 0.5f
    private var fuelTypes = ArrayList<String?>()
    private var init = false

    private var vehicleType: String? = null

    private var states = arrayOf(
        intArrayOf(android.R.attr.state_checked),
        intArrayOf(-android.R.attr.state_checked),
        intArrayOf(android.R.attr.state_pressed)
    )
    private var colors = intArrayOf(Color.WHITE, Color.BLACK, R.color.old_lavender)

    @Inject
    lateinit var presenter: AppointmentDetailsPresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ((activity.application) as DearOApplication)
            .repositoryComponent
            .COMPONENT(AppointmentDetailsPresenterModule(this))
            .inject(this)
        screenTracker.sendScreenEvent(
            activity,
            ScreenTracker.SCREEN_ADD_APPMNT_DETAILS,
            javaClass.name
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppointmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
        /*        return inflater.inflate(R.layout.fragment_appointment_details, container, false)*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("on view created $vehicleType")
        initLists()
        if (SharedPrefHelper.getWorkshopVehicleType().size > 1) {
            binding.layoutVehicleType.parentVehicleTypeView.visibility = View.VISIBLE
            setVehicleTypeToggle()
        } else {
            binding.layoutVehicleType.parentVehicleTypeView.visibility = View.GONE
        }
        binding.makeView.onItemSelectedListener = this
        binding.modelView.onItemSelectedListener = this
        binding.variantView.onItemSelectedListener = this
        init = true
        createRunnable()
        binding.mobileView.addTextChangedListener(this)
        binding.fuelView.setOnCheckedChangeListener(this)
        binding.oilView.setOnCheckedChangeListener(this)
        binding.registrationView.addTextChangedListener(this)
        binding.pincodeView.addTextChangedListener(this)
        setIntentData()
    }

    override fun onResume() {
        super.onResume()
        EventsManager.register(this)
        appointment = activity.appointment.deepCopy()
        Timber.d("On Resume " + (context as CreateAppointmentActivity).appointment.vehicle.toString())
        if (checkIfNetworkAvailable()) {
            presenter.getMake(vehicleType)
        }
    }

    override fun onPause() {
        super.onPause()
        EventsManager.unregister(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CreateAppointmentActivity) {
            activity = context
        } else {
            IllegalStateException("No Parent Activity Found")
        }
        if (context is ICreateAppointmentInteraction) {
            interaction = context
        } else {
            throw IllegalStateException("Activity must implement ICreateJobCardInteraction interface ")
        }

        if (context is ICreateAppointmentInteraction) {
            mInteraction = context
        } else {
            throw IllegalStateException("Activity must implement ICreateJobCardInteraction interface ")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //  handler.removeCallbacks(numberRunnable)
        handler.removeCallbacks(registerRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        interaction = null
        mInteraction = null

        // handler.removeCallbacks()
        presenter.detachView()
    }

    override fun displayMake(make: List<Make>) {
        clearLists(arrayListOf(CLEAR_MAKE, CLEAR_MODEL, CLEAR_VARIANT))
        makeList.addAll(make)
        binding.makeView.adapter =
            ArrayAdapter<String>(activity, R.layout.picker_item, makeList.map { it.name })
        Timber.d(appointment.vehicle.makeSlug + " & " + activity.appointment.vehicle.makeSlug)
        val index =
            makeList.indexOf(makeList.find { it.slug == appointment.vehicle.makeSlug ?: activity.appointment.vehicle.makeSlug })
        binding.makeView.setSelection(index, false)
        if (appointment.vehicle.engineOilType != null)
            when (appointment.vehicle.engineOilType) {
                REGULAR -> binding.oilView.check(R.id.rb_regular)
                SEMI_SYNTHETIC -> binding.oilView.check(R.id.rb_semi_synthetic)
                SYNTHETIC -> binding.oilView.check(R.id.rb_synthetic)
            }
        else {
            binding.oilView.check(R.id.rb_regular)
            appointment.vehicle.engineOilType = REGULAR
        }
    }

    override fun displayModel(model: List<Model>) {
        modelList.addAll(model as ArrayList)
        modelAdapter =
            ArrayAdapter<String>(activity, R.layout.picker_item, modelList.map { it.name })
        binding.modelView.adapter = modelAdapter
        val index =
            modelList.indexOf(modelList.find { it.slug == appointment.vehicle.modelSlug ?: activity.appointment.vehicle.modelSlug })
        binding.modelView.setSelection(index, false)
    }

    override fun displayVariant(variant: List<Variant>) {
        clearLists(arrayListOf(CLEAR_VARIANT))
        variantList.addAll(variant as ArrayList)
        fuelTypes.addAll(variantList.map { it.fuelType }.distinct())
        if (fuelTypes.isNotEmpty()) {
            binding.fuelErrorView.visibility = View.VISIBLE
            weight = 1f / fuelTypes.size
        } else {
            binding.fuelErrorView.visibility = View.GONE
        }
        fuelTypes.indices.forEach { addFuelRadioButton(fuelTypes[it], it) }
        if (fuelTypes.size == 1) {
            binding.fuelView.check(0)
        }
        if (appointment.vehicle.fuelType != null && fuelTypes.isNotEmpty()) {
            val index = fuelTypes.indexOf(fuelTypes.find { it == appointment.vehicle.fuelType })
            binding.fuelView.check(index)
        } else {
            val fuel = (context as CreateAppointmentActivity).appointment.vehicle.fuelType
            if (fuelTypes.isNotEmpty() && fuel != null) {
                binding.fuelErrorView.visibility = View.VISIBLE
                val index = fuelTypes.indexOf(fuelTypes.find { it == fuel })
                binding.fuelView.check(index)
            }
        }
    }

    override fun displayVehicleData(vehicle: Vehicle) {
        binding.makeView.visibility = View.GONE
        binding.modelView.visibility = View.GONE
        binding.makeView.setSelection(0)
        binding.modelView.setSelection(0)
        binding.makeErrorView.error = null
        binding.modelErrorView.error = null
        binding.makeFoundView.visibility = View.VISIBLE
        binding.modelFoundView.visibility = View.VISIBLE
        if (vehicle.variant != null) {
            binding.variantView.visibility = View.GONE
            binding.variantView.setSelection(0)
            binding.variantFoundView.visibility = View.VISIBLE
            binding.variantFoundView.text = vehicle.variant.name
            fuelTypes.clear()
            fuelTypes.add(vehicle.fuelType)
            binding.fuelView.removeAllViews()
            binding.fuelView.clearCheck()
            addFuelRadioButton(vehicle.fuelType, 0)
            binding.fuelView.check(0)
            binding.fuelErrorView.visibility = View.VISIBLE
        } else {
            presenter.getVariant(vehicle.modelSlug)
            binding.variantView.visibility = View.VISIBLE
            binding.variantFoundView.visibility = View.GONE
        }
        binding.makeFoundView.text = vehicle.make.name
        binding.modelFoundView.text = vehicle.model.name
        binding.colourView.setText(vehicle.color)
        appointment.vehicle.let {
            it.makeSlug = vehicle.makeSlug
            it.makeName = vehicle.make.name
            it.modelSlug = vehicle.modelSlug
            it.modelName = vehicle.model.name
            it.fuelType = vehicle.fuelType
            it.color = vehicle.color
            it.variantCode = vehicle.variantCode
            if (vehicle.variant != null) {
                it.variantName = vehicle.variant.name
            }

            if (vehicleType != vehicle.vehicleType) {
                it.vehicleType = vehicle.vehicleType
                vehicleType = vehicle.vehicleType
                presenter.getMake(vehicleType)
            }

            /*it.engineOilType = vehicle.engineOilType*/
        }
        if (vehicle.engineOilType != null)
            when (vehicle.engineOilType) {
                REGULAR -> binding.oilView.check(R.id.rb_regular)
                SEMI_SYNTHETIC -> binding.oilView.check(R.id.rb_semi_synthetic)
                SYNTHETIC -> binding.oilView.check(R.id.rb_synthetic)
            }
        else {
            binding.oilView.check(R.id.rb_regular)
            vehicle.engineOilType = REGULAR
        }
        disableVehicleTypes()
    }

    override fun resetVehicleData() {
        binding.makeView.visibility = View.VISIBLE
        binding.modelView.visibility = View.VISIBLE
        binding.makeFoundView.visibility = View.GONE
        binding.modelFoundView.visibility = View.GONE
        binding.variantView.visibility = View.VISIBLE
        binding.variantFoundView.visibility = View.GONE
        appointment.vehicle.make = null
        appointment.vehicle.makeSlug = null
        appointment.vehicle.model = null
        appointment.vehicle.modelSlug = null
        fuelTypes.clear()
        clearLists(arrayListOf(CLEAR_MODEL, CLEAR_VARIANT))
        binding.makeView.setSelection(0)
        binding.fuelView.removeAllViews()
        binding.fuelView.clearCheck()
        setVehicleTypeToggle()
        binding.oilView.check(R.id.rb_regular)
        appointment.vehicle.engineOilType = REGULAR
    }

    override fun displayCustomerData(customer: Customer) {
        binding.nameView.setText(customer.name ?: "")
        binding.emailView.setText(customer.email ?: "")
        appointment.customer.let {
            it.name = customer.name
            it.email = customer.email
            it.address = customer.address
            if (it.address.isNullOrEmpty().not()) {
                val address = it.address[0]
                binding.localityView.setText(address.location)
                binding.streetView.setText(address.street)
                binding.cityView.setText(address.city)
                binding.stateView.setText(address.state)
                binding.pincodeView.setText(address.pincode.toString())
            }
        }
    }

    override fun resetCustomer() {
        binding.emailView.text.clear()
        binding.nameView.text.clear()
        binding.stateView.text?.clear()
        binding.cityView.text?.clear()
        binding.pincodeView.text?.clear()
        binding.localityView.text?.clear()
        binding.streetView.text?.clear()
        appointment.customer.let {
            it.name = null
            it.email = null
            it.address = null
        }
    }

    override fun displayPincodeData(pincode: Pincode) {
        binding.appointmentPincodeParentView.error = null
        binding.cityView.setText(pincode.city?.city)
        binding.stateView.setText(pincode.city?.state)
    }

    override fun showProgressIndicator() {
        (activity as BaseActivity).showProgressBar()
    }

    override fun dismissProgressIndicator() {
        (activity as BaseActivity).dismissProgressBar()
    }

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg)
    }

    override fun moveToNextScreen(appointment: Appointment) {
        activity.appointment.run {
            id = appointment.id
            vehicle = appointment.vehicle
            Timber.d(" Move to Next Screen " + appointment.vehicle.toString())
            customer = appointment.customer
            if (SharedPrefHelper.isAppointmentServicePackageActive() && appointment.suggestedPackages?.list?.isNotEmpty() == true) {
                suggestedPackages = appointment.suggestedPackages
                skipPage = false
            } else {
                timeSlots = appointment.timeSlots
                skipPage = true
            }
            workshopId = appointment.workshopId
        }
        Timber.d("Activity Saved Appointment " + activity.appointment.vehicle.toString())
        Utility.hideSoftKeyboard(activity)
        interaction?.onSuccess()
    }


    override fun showEmailError(message: String) {
        binding.emailView.error = message
    }

    override fun showMobileError(message: String) {
        binding.mobileView.error = message
    }

    override fun showError(message: String) {
        displayError(message)
    }

    override fun afterTextChanged(s: Editable?) {
        when {
            s?.hashCode() == binding.registrationView.text.hashCode() && binding.registrationView.hasFocus() -> {
                Timber.d("Registration ATC")
                handler.removeCallbacks(registerRunnable)
                handler.postDelayed(registerRunnable, DELAY)
            }

            s?.hashCode() == binding.mobileView.text.hashCode() && binding.mobileView.hasFocus() -> {
                handler.removeCallbacks(numberRunnable)
                handler.postDelayed(numberRunnable, 200L)
            }

            s?.hashCode() == binding.pincodeView.text.hashCode() && binding.pincodeView.hasFocus() -> {
                handler.removeCallbacks(pincodeRunnable)
                handler.postDelayed(pincodeRunnable, 200L)
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onCheckedChanged(radioGroup: RadioGroup?, itemId: Int) {
        if (radioGroup == binding.fuelView && itemId != -1) {
            binding.fuelErrorView.error = null
            clearLists(arrayListOf(CLEAR_FILTERED_VARIANT, CLEAR_VARIANT_DESC))
            appointment.vehicle.fuelType = fuelTypes[itemId]
            filteredVariantList.addAll(variantList.filter { it.fuelType == appointment.vehicle.fuelType })
            Variant().let {
                it.name = "Others"
                it.code = "others"
                filteredVariantList.add(it)
            }
            binding.variantDescErrorView.visibility = View.GONE
            variantAdapter =
                ArrayAdapter(activity, R.layout.picker_item, filteredVariantList.map { it.name })
            binding.variantView.adapter = variantAdapter
            Timber.d(appointment.vehicle.variantCode + " & " + activity.appointment.vehicle.variantCode)
            val index =
                filteredVariantList.indexOf(filteredVariantList.find { it.code == appointment.vehicle.variantCode ?: activity.appointment.vehicle.variantCode })
            if (index != -1) {
                binding.variantView.setSelection(index)
            } else {
                if (filteredVariantList.size == 2) {
                    binding.variantView.setSelection(filteredVariantList.lastIndex)
                }
            }
        } else if (radioGroup == binding.oilView && itemId != -1) {
            // Oil Type
            when (itemId) {
                R.id.rb_regular -> {
                    appointment.vehicle.engineOilType = REGULAR
                    binding.oilErrorView.error = null
                }

                R.id.rb_synthetic -> {
                    appointment.vehicle.engineOilType = SYNTHETIC
                    binding.oilErrorView.error = null
                }

                R.id.rb_semi_synthetic -> {
                    appointment.vehicle.engineOilType = SEMI_SYNTHETIC
                    binding.oilErrorView.error = null
                }
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(spinner: AdapterView<*>?, view: View?, pos: Int, p3: Long) {
        // Utility.hideSoftKeyboard(activity)
        if (init) {
            when (spinner) {
                binding.makeView -> {
                    if (pos == 0) {
                        appointment.vehicle.makeSlug = null
                        appointment.vehicle.makeName = null
                        binding.modelView.setSelection(0)
                    } else {
                        appointment.vehicle.makeSlug = makeList[pos].slug
                        appointment.vehicle.makeName = makeList[pos].name
                        binding.makeErrorView.error = null
                        if (checkIfNetworkAvailable()) {
                            presenter.getModel(
                                appointment.vehicle.makeSlug
                                    ?: (context as CreateAppointmentActivity).appointment.vehicle.makeSlug
                            )
                        }
                    }
                    clearLists(
                        arrayListOf(
                            CLEAR_MODEL,
                            CLEAR_VARIANT,
                            CLEAR_VARIANT_DESC,
                            CLEAR_FILTERED_VARIANT
                        )
                    )
                }

                binding.modelView -> {
                    if (pos == 0) {
                        appointment.vehicle.modelSlug = null
                        appointment.vehicle.modelName = null
                        binding.variantDescView.setSelection(0)
                        binding.variantView.setSelection(0)
                        binding.fuelErrorView.visibility = View.GONE
                        binding.fuelView.clearCheck()
                        appointment.vehicle.fuelType = null
                    } else {
                        appointment.vehicle.modelSlug = modelList[pos].slug
                        appointment.vehicle.modelName = modelList[pos].name
                        binding.modelErrorView.error = null
                        fuelTypes.clear()
                        binding.fuelView.removeAllViews()
                        binding.fuelView.clearCheck()
                        binding.fuelErrorView.visibility = View.GONE
                        if (checkIfNetworkAvailable()) {
                            presenter.getVariant(
                                appointment.vehicle.modelSlug
                                    ?: activity.appointment.vehicle.modelSlug
                            )
                        }
                    }
                    clearLists(
                        arrayListOf(
                            CLEAR_VARIANT,
                            CLEAR_VARIANT_DESC,
                            CLEAR_FILTERED_VARIANT
                        )
                    )
                }

                binding.variantView -> {
                    if (pos != 0) {
                        appointment.vehicle.variantCode = filteredVariantList[pos].code
                        appointment.vehicle.variantName = filteredVariantList[pos].name
                    } else {
                        appointment.vehicle.variantCode = null
                        appointment.vehicle.variantName = null
                    }
                }
            }
        }
    }

    @Subscribe
    fun onNextBtnEvent(btn: ActionButtonClickEvent) {
        Timber.v("ActionButton Click Event")
        //have to do

        //  if (!registrationView.isEnabled && !mobileView.isEnabled){
        createAppointmentNextBtnCall()
        //}else{
        // presenter.newNextApiCall(mobileView.text.toString(),registrationView.text.toString())
        // }

    }


    override fun newNextApiUpdate(enabled: Boolean, error: String) {

        if (enabled) {
            createAppointmentNextBtnCall()

        } else {
            displayError(error)
        }


        //  mInteraction?.onSuccess()
    }

    fun createAppointmentNextBtnCall() {
        if (checkIfNetworkAvailable()) {
            appointment.run {

                val isValidRegistrationNumber =
                    binding.registrationView.text.toString().isNotEmpty() &&
                            Utility.isRegistrationNumberValid(
                                binding.registrationView.text.toString().trim()
                            ) &&
                            Constants.BusinessConstants.STATE_LIST.find {
                                it == binding.registrationView.text.toString().substring(0..1)
                            } != null
                //for now pincode validation is only for new appointment not for lead : 07SEP2021
                var validate = true
                val isPincodeValid = binding.pincodeView.text.toString()
                    .isNotEmpty() && binding.cityView.text.toString()
                    .isNotEmpty() && binding.stateView.text.toString().isNotEmpty()
                val shouldPincodeValidated = appointment.id.isNullOrBlank()
                if (shouldPincodeValidated) {
                    validate = isPincodeValid
                }

                if (isValidRegistrationNumber &&
                    binding.mobileView.text.toString().isNotEmpty() &&
                    Utility.isMobileNumberValid(binding.mobileView.text.toString()) &&
                    validateEmailField(binding.emailView.text.toString()) &&
                    binding.oilView.checkedRadioButtonId != -1 &&
                    validate &&
                    (vehicle.makeSlug != null && vehicle.makeSlug.isNotEmpty()) &&
                    (vehicle.modelSlug != null && vehicle.modelSlug.isNotEmpty()) &&
                    vehicle.fuelType != null
                ) {

                    appointment.let {
                        it.vehicle.registrationNumber =
                            Utility.getServerAcceptableRegistrationNumber(
                                binding.registrationView.text.toString()
                                    .uppercase(Locale.getDefault())
                                    .trim()
                            )
                        it.vehicle.color = binding.colourView.text.toString()
                        it.customer.mobile =
                            Utility.getServerAcceptableContactNumber(binding.mobileView.text.toString())
                        if (binding.nameView.text.toString().isNotBlank())
                            it.customer.name = binding.nameView.text.toString()

                        if (binding.emailView.text.toString().isNotBlank() && validateEmailField(
                                binding.emailView.text.toString()
                            )
                        )
                            it.customer.email = binding.emailView.text.toString()

                        if (appointment.address == null) appointment.address = Address()

                        appointment.address?.let { address ->
                            address.location = binding.localityView.text.toString()
                            address.street = binding.streetView.text.toString()
                            val pincode = binding.pincodeView.text.toString()
                            if (pincode.isNotEmpty()) {
                                address.pincode = pincode.toIntOrNull()
                                address.city = binding.cityView.text.toString()
                                address.state = binding.stateView.text.toString()
                            }
                        }
                    }
                    presenter.saveDetails(appointment)
                } else {
                    if (binding.oilView.checkedRadioButtonId == -1) {
                        binding.oilErrorView.error = "Oil Type Required"
                        binding.oilView.requestFocus()
                    }
                    if (vehicle.fuelType == null) {
                        binding.fuelErrorView.error = "Fuel Type Required"
                        binding.fuelView.requestFocus()
                    }
                    if (binding.mobileView.text.toString().isEmpty()) {
                        binding.mobileView.error = "Mobile Number Required"
                    }
                    if (binding.mobileView.text.toString()
                            .isNotEmpty() && !Utility.isMobileNumberValid(binding.mobileView.text.toString())
                    ) {
                        binding.mobileView.error = "Invalid Mobile Number"
                    }
                    if (binding.registrationView.text.toString()
                            .isEmpty() || binding.registrationView.text.toString().isBlank()
                    ) {
                        binding.registrationView.error = "Registration Number Required"
                        binding.registrationView.requestFocus()
                    }
                    if (binding.registrationView.text.toString()
                            .isNotEmpty() && !isValidRegistrationNumber
                    ) {
                        binding.registrationView.error = "Invalid Registration Number"
                        binding.registrationView.requestFocus()
                    }
                    if (!validateEmailField(binding.emailView.text.toString())) {
                        binding.emailView.error = "Invalid Email"
                    }
                    if (vehicle.makeSlug == null || vehicle.makeSlug.isEmpty()) {
                        binding.makeErrorView.error = "Make Required"
                    }
                    if (vehicle.modelSlug == null || vehicle.modelSlug.isEmpty()) {
                        binding.modelErrorView.error = "Model Required"
                    }
                    //for now pincode validation disabling as business requirement : 07SEP2021
                    if (binding.pincodeView.text.toString()
                            .isNotEmpty() && (binding.cityView.text.toString()
                            .isEmpty() || binding.stateView.text.toString()
                            .isEmpty()) && appointment.appointmentId.isNullOrBlank()
                    ) {
                        binding.pincodeView.error = "Pincode might be incorrect"
                    } else if (binding.pincodeView.text.toString()
                            .isEmpty() && appointment.appointmentId.isNullOrBlank()
                    ) {
                        binding.pincodeView.error = "Pincode Required"
                    }
                }
            }
        }
    }


    private fun addFuelRadioButton(name: String?, position: Int) {
        val fuelButton = RadioButton(activity)
        fuelButton.layoutParams =
            RadioGroup.LayoutParams(0, RadioGroup.LayoutParams.WRAP_CONTENT, weight)
        fuelButton.id = position
        fuelButton.text = name
        fuelButton.background =
            ContextCompat.getDrawable(activity, R.drawable.custom_radio_selector)
        fuelButton.buttonDrawable = null
        fuelButton.padding = 10
        fuelButton.textColor = R.color.state_list
        fuelButton.setTextColor(ColorStateList(states, colors))
        fuelButton.textAlignment = View.TEXT_ALIGNMENT_CENTER
        binding.fuelView.addView(fuelButton)
        Timber.d("Added $name RadioButton")
    }

    private fun validateEmailField(email: String): Boolean {
        return when {
            email.isEmpty() -> true
            email.isNotEmpty() && Utility.isEmailValid(email) -> true
            else -> false
        }
    }

    private fun initLists() {
        makeList = ArrayList()
        modelList = ArrayList()
        descList = ArrayList()
        variantList = ArrayList()
        filteredVariantList = ArrayList()

        val selectMake = Make()
        selectMake.name = "Select Make"
        selectMake.slug = "Select Make"
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
        selectVariant.code = null
        filteredVariantList.add(0, selectVariant)

        makeAdapter = ArrayAdapter(activity, R.layout.picker_item, makeList.map { it.name!! })
        binding.makeView.adapter = makeAdapter
        modelAdapter =
            ArrayAdapter<String>(activity, R.layout.picker_item, modelList.map { it.name })
        binding.modelView.adapter = modelAdapter
        variantDescAdapter =
            ArrayAdapter(activity, R.layout.picker_item, descList.map { it.description!! })
        binding.variantDescView.adapter = variantDescAdapter
        variantAdapter =
            ArrayAdapter(activity, R.layout.picker_item, filteredVariantList.map { it.name })
        binding.variantView.adapter = variantAdapter
    }

    private fun clearLists(listsToClear: ArrayList<String>) {
        listsToClear.forEach { listToClear ->
            when (listToClear) {
                CLEAR_MAKE -> {
                    makeList = makeList.subList(0, 1)
                    makeAdapter =
                        ArrayAdapter(activity, R.layout.picker_item, makeList.map { it.name!! })
                    binding.makeView.adapter = makeAdapter
                }

                CLEAR_MODEL -> {
                    modelList = modelList.subList(0, 1)
                    modelAdapter =
                        ArrayAdapter(activity, R.layout.picker_item, modelList.map { it.name!! })
                    binding.modelView.adapter = modelAdapter
                }

                CLEAR_VARIANT_DESC -> {
                    descList = descList.subList(0, 1)
                    variantDescAdapter = ArrayAdapter(
                        activity,
                        R.layout.picker_item,
                        descList.map { it.description!! })
                    binding.variantDescView.adapter = variantDescAdapter
                }

                CLEAR_FILTERED_VARIANT -> {
                    filteredVariantList = filteredVariantList.subList(0, 1)
                    variantAdapter = ArrayAdapter(
                        activity,
                        R.layout.picker_item,
                        filteredVariantList.map { it.name })
                    binding.variantView.adapter = variantAdapter
                }

                CLEAR_VARIANT -> {
                    variantList.clear()
                }
            }
        }
    }

    private fun createRunnable() {
        Timber.d("Runnable Created")
        registerRunnable = Runnable {
            if (binding.registrationView.text.toString().isNotEmpty() &&
                Utility.isRegistrationNumberValid(binding.registrationView.text.toString()) &&
                Constants.BusinessConstants.STATE_LIST.find {
                    it == binding.registrationView.text.toString().substring(0..1)
                } != null
            ) {
                binding.colourView.text.clear()
                presenter.getVehicleDetails(Utility.getServerAcceptableRegistrationNumber(binding.registrationView.text.toString()))
                Timber.d("Registration Number if")
            } else {
                if (binding.registrationView.text.toString().isNotEmpty() &&
                    (!Utility.isRegistrationNumberValid(binding.registrationView.text.toString()) ||
                            Constants.BusinessConstants.STATE_LIST.find {
                                it == binding.registrationView.text.toString().substring(0..1)
                            } == null)
                ) {
                    Timber.d("Registration Number else")
                    binding.registrationView.requestFocus()
                    binding.registrationView.error = "Invalid Registration Number"
                    binding.colourView.text.clear()
                }
                resetVehicleData()
            }
        }
        numberRunnable = Runnable {
            if (binding.mobileView.text.toString().isNotEmpty() && Utility.isMobileNumberValid(
                    binding.mobileView.text.toString()
                )
            ) {
                presenter.getCustomerDetails(Utility.getServerAcceptableContactNumber(binding.mobileView.text.toString()))
            } else {
                resetCustomer()
            }
        }
        pincodeRunnable = Runnable {
            if (binding.pincodeView.text.toString()
                    .isNotEmpty() && Utility.isPinCodeValid(binding.pincodeView.text.toString())
            ) {
                presenter.getCityByPincode(binding.pincodeView.text!!.trim().toString().toInt())
            } else {
                binding.appointmentPincodeParentView.error = "Pincode not valid"
            }
        }
    }

    private fun setVehicleTypeToggle() {
        vehicleType = vehicleType ?: VehicleType.VEHICLE_TYPE_CAR
        setVehicleType()
        binding.layoutVehicleType.carVehicleView.setOnClickListener {
            vehicleType = VehicleType.VEHICLE_TYPE_CAR
            setVehicleType()
            presenter.getMake(vehicleType)
        }
        binding.layoutVehicleType.bikeVehicleView.setOnClickListener {
            vehicleType = VehicleType.VEHICLE_TYPE_BIKE
            setVehicleType()
            presenter.getMake(vehicleType)
        }
    }

    private fun disableVehicleTypes() {
        binding.layoutVehicleType.carVehicleView.setOnClickListener(null)
        binding.layoutVehicleType.bikeVehicleView.setOnClickListener(null)
        setVehicleType()
    }

    private fun setVehicleType() {
        when (vehicleType) {
            VehicleType.VEHICLE_TYPE_CAR -> {
                binding.layoutVehicleType.carVehicleView.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorPrimary
                    )
                )
                binding.layoutVehicleType.bikeVehicleView.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                binding.layoutVehicleType.carIconView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_directions_car_white_24dp
                    )
                )
                binding.layoutVehicleType.bikeIconView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_motorcycle_black_24dp
                    )
                )
            }

            VehicleType.VEHICLE_TYPE_BIKE -> {
                binding.layoutVehicleType.carVehicleView.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                binding.layoutVehicleType.bikeVehicleView.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorPrimary
                    )
                )
                binding.layoutVehicleType.carIconView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_directions_car_black_24dp
                    )
                )
                binding.layoutVehicleType.bikeIconView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_motorcycle_white_24dp
                    )
                )
            }
        }
    }

    private fun setIntentData() {
        arguments?.run {
            val registrationNumber = getString(ARG_REGISTRATION_NUMBER)
            val mobileNumber = getString(ARG_MOBILE_NUMBER)



            if (registrationNumber.toString().isNotEmpty()) {
                //   println("------reg-----"+registrationNumber)
                // registrationView.isEnabled = false
                binding.registrationView.isEnabled = true
            }
            if (registrationNumber.equals("null")) {

                //  println("------reg--null---"+registrationNumber)
                binding.registrationView.isEnabled = true
            }


            registrationNumber?.let {
                binding.registrationView.requestFocus()
                binding.registrationView.setText(if (it.equals("null")) "" else it)
            }
            mobileNumber?.let {
                binding.mobileView.requestFocus()
                binding.mobileView.setText(it)
                binding.mobileView.isEnabled = false

            }
            appointment.id = getString(ARG_APPOINTMENT_ID)
            if (appointment.id.isNullOrBlank()) {
                binding.appointmentPincodeParentView.setHint(R.string.pincode)
            }
        }
    }

    companion object {
        fun newInstance(
            registrationNumber: String?,
            mobileNumber: String?,
            appointmentId: String?
        ): AppointmentVehicleDetailsFragment {
            val fragment = AppointmentVehicleDetailsFragment()
            val args = Bundle()
            args.putString(ARG_REGISTRATION_NUMBER, registrationNumber)
            args.putString(ARG_MOBILE_NUMBER, mobileNumber)
            args.putString(ARG_APPOINTMENT_ID, appointmentId)
            fragment.arguments = args
            return fragment
        }

        const val CLEAR_MAKE = "clear make"
        const val CLEAR_MODEL = "clear model"
        const val CLEAR_FILTERED_VARIANT = "clear_filtered_variant"
        const val CLEAR_VARIANT = "clear_variant"
        const val CLEAR_VARIANT_DESC = "clear_variant_desc"
        const val REGULAR = "Regular"
        const val SYNTHETIC = "Synthetic"
        const val SEMI_SYNTHETIC = "SEMI_Synthetic"
        const val ARG_REGISTRATION_NUMBER = "arg_reg_number"
        const val ARG_MOBILE_NUMBER = "arg_mobile_number"
        const val ARG_APPOINTMENT_ID = "arg_appointment_id"
        const val DELAY = 800L
    }
}
