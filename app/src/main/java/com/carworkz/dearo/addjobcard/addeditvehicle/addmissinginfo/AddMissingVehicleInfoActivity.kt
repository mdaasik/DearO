package com.carworkz.dearo.addjobcard.addeditvehicle.addmissinginfo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityAddMissingVehicleInfoBinding
import com.carworkz.dearo.databinding.ActivityVehicleDetailsBinding
import com.carworkz.dearo.domain.entities.Variant
import com.carworkz.dearo.domain.entities.Vehicle
import com.carworkz.dearo.domain.entities.VehicleType
import com.carworkz.dearo.domain.entities.VehicleVariantBody
import com.carworkz.dearo.extensions.checkedIndex
import com.carworkz.dearo.extensions.*
import com.carworkz.dearo.interactionprovider.ToolBarImgInteractionProvider
import com.carworkz.dearo.screencontainer.ActionImgScreenContainer
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.activity_add_missing_vehicle_info.*
import kotlinx.android.synthetic.main.base_layout.**/
import timber.log.Timber
import javax.inject.Inject

class AddMissingVehicleInfoActivity : ScreenContainerActivity(), AddMissingVehicleInfoContract.View, RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener, ToolBarImgInteractionProvider {
    private lateinit var binding: ActivityAddMissingVehicleInfoBinding
    private var variantDescAdapter: ArrayAdapter<String>? = null
    private var variantAdapter: ArrayAdapter<String>? = null
    private var transmissionAdapter: ArrayAdapter<String>? = null

    @Inject
    lateinit var presenter: AddMissingVehicleInfoPresenter

    private lateinit var vehicle: Vehicle

    private lateinit var fuelTypes: List<String>
    private var descriptions: MutableList<String> = mutableListOf()
    private var variants: MutableList<Variant> = mutableListOf()
    private var transmissions: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        getExtras()
        super.onCreate(savedInstanceState)
        initComponent()
        setVehicleType()
        initViews()
        presenter.getFuelTypes(vehicle.model.slug!!)
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (group) {
            binding.fuelGroupView -> {
                val index = binding.fuelGroupView.checkedIndex()
                if (index < 0) {
                    return
                }
                resetOnFuelSelection()
                val fuelType = fuelTypes[index]
                vehicle.fuelType = fuelType
                presenter.getDescriptions(fuelType)
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) = Unit

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (position < 0) {
            return
        }

        when (parent) {
            binding.descriptionSpinnerView -> {
                resetOnDescriptionSelection()
                if (position != 0) {
                    vehicle.description = descriptions[position]
                    presenter.getVariants(vehicle.description, vehicle.fuelType)
                } else {
                    vehicle.description = null
                }
            }

            binding.variantSpinnerView -> {
                resetOnVariantSelection()
                if (position != 0) {
                    val variantSelected = variants[position]
                    vehicle.variantCode = variantSelected.code
                    presenter.getTransmissions(vehicle.description, vehicle.fuelType)
                } else {
                    vehicle.variantCode = null
                }
            }

            binding.transmissionSpinnerView -> {
                if (position != 0) {
                    vehicle.transmissionType = transmissions[position]
                } else {
                    vehicle.transmissionType = null
                }
            }
        }
    }

    override fun onUpdateSuccess() {
        finish()
    }

    override fun displayFuelTypes(fuelTypes: List<String>) {
        this.fuelTypes = fuelTypes
        val weight = 1f / fuelTypes.size
        fuelTypes.forEachIndexed { pos, fuelType ->
            addRadioButton(fuelType, pos, weight)
        }

        binding.fuelGroupView.check(Utility.findIndexFromList(vehicle.fuelType, fuelTypes))

        if (fuelTypes.size == 1) {
            binding.fuelGroupView.check(0)
        }
    }

    override fun displayDescriptions(descriptions: MutableList<String>) {
        if (descriptions.isEmpty()) {
            Utility.setVisibility(false,   binding.descriptionGroup)
            presenter.getVariants(null, vehicle.fuelType)
        } else {
            Utility.setVisibility(true,   binding.descriptionGroup)
            this.descriptions.addAll(descriptions)
            variantDescAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, this.descriptions)
            binding.descriptionSpinnerView.adapter = variantDescAdapter
            if (this.descriptions.size == 2) {
                binding.descriptionSpinnerView.setSelection(this.descriptions.lastIndex)
            }
        }
    }

    override fun displayVariants(variants: MutableList<Variant>) {
        this.variants.addAll(variants)
        variantAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, this.variants.map { it.name })
        binding.variantSpinnerView.adapter = variantAdapter
        if (this.variants.size == 2) {
            binding.variantSpinnerView.setSelection(this.variants.lastIndex)
        }
    }

    override fun displayTransmission(transmissions: MutableList<String>) {
        this.transmissions.addAll(transmissions)
        transmissionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, this.transmissions)
        binding.transmissionSpinnerView.adapter = transmissionAdapter
        if (this.transmissions.size == 2) {
            binding.transmissionSpinnerView.setSelection(this.transmissions.lastIndex)
        }
    }

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg)
    }

    override fun createScreenContainer(): ScreenContainer = ActionImgScreenContainer(this)
    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityAddMissingVehicleInfoBinding.inflate(layoutInflater)
       return binding
    }


    override fun getProgressView(): View =   binding.baseLayouyt.pbMain

    override fun getNavigationImage(): Int = R.drawable.ic_clear_white_24dp

    override fun onActionBtnClick() = Unit

    override fun onSecondaryActionBtnClick() {
        if (vehicle.variantCode != null && vehicle.transmissionType != null && vehicle.fuelType != null && ((vehicle.description != null && descriptions.size > 1) || descriptions.size == 1)) {
            resetErrors()
            presenter.saveVariantDetails(vehicle.id, VehicleVariantBody(vehicle.variantCode, vehicle.transmissionType, vehicle.fuelType, vehicle.description))
        } else {
            if (vehicle.variantCode == null) {
                (binding.variantSpinnerView?.selectedView as? TextView)?.error = "Please Select Variant"
            }

            if (vehicle.transmissionType == null) {
                (binding.transmissionSpinnerView?.selectedView as? TextView)?.error = "Please Select Transmission"
            }

            if (vehicle.fuelType == null) {
                Utility.setVisibility(true, binding.fuelErrorView)
            }

            if (vehicle.description == null && descriptions.size > 1) {
                (binding.descriptionSpinnerView?.selectedView as? TextView)?.error = "Please Select Description"
            }
        }
    }

    override fun getActionBarImage(): Int = 0

    override fun getSecondaryActionBarImage(): Int = R.drawable.ic_save_white_24dp

    override fun getToolBarTitle(): String = "Update Details"

    override fun showProgressIndicator() {
        showProgressBar()
    }

    override fun dismissProgressIndicator() {
        dismissProgressBar()
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        binding.registrationNumberView.text = vehicle.registrationNumber
        binding.makeModelView.text = vehicle.make.name + " " + vehicle.model.name
        binding.fuelGroupView.setOnCheckedChangeListener(this)

        descriptions.add(0, "Select Description")
        variants.add(0, Variant().apply {
            name = "Select Variant"
        })
        transmissions.add(0, "Select Transmission")

        variantAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, variants.map { it.name })
        variantDescAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, descriptions)
        transmissionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, transmissions)

        binding.descriptionSpinnerView.adapter = variantDescAdapter
        binding.transmissionSpinnerView.adapter = transmissionAdapter
        binding.variantSpinnerView.adapter = variantAdapter

        binding.descriptionSpinnerView.onItemSelectedListener = this
        binding.variantSpinnerView.onItemSelectedListener = this
        binding.transmissionSpinnerView.onItemSelectedListener = this
    }

    private fun setVehicleType() {
        when (vehicle.vehicleType) {
            VehicleType.VEHICLE_TYPE_CAR -> {
                binding.vehicleTypeIconView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_directions_car_white_24dp))
            }

            VehicleType.VEHICLE_TYPE_BIKE -> {
                binding.vehicleTypeIconView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_bike_24dp))
            }
        }
    }

    private fun addRadioButton(name: String?, position: Int, weight: Float) {
        val radioButton = RadioButton(this)
        radioButton.layoutParams = RadioGroup.LayoutParams(0, RadioGroup.LayoutParams.WRAP_CONTENT, weight)
        radioButton.id = position
        radioButton.text = name
        radioButton.background = ContextCompat.getDrawable(this, R.drawable.custom_radio_selector)
        radioButton.buttonDrawable = null
        radioButton.padding = 10
        radioButton.textColor = R.color.state_list
        radioButton.setTextColor(ContextCompat.getColorStateList(this, R.color.state_list))
        radioButton.textAlignment = View.TEXT_ALIGNMENT_CENTER
        binding.fuelGroupView.addView(radioButton)
        Timber.d("Added $name RadioButton")
    }

    private fun initComponent() {
        (application as DearOApplication)
                .repositoryComponent
                .COMPONENT(AddMissingVehicleInfoPresenterModule(this))
                .inject(this)
    }

    private fun getExtras() {
        intent?.extras?.let {
            vehicle = it.getParcelable(ARG_VEHICLE)!!
        } ?: run {
            throw IllegalStateException("Vehicle cannot be null")
        }
    }

    private fun resetOnFuelSelection() {
        descriptions = descriptions.subList(0, 1)
        variantDescAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, this.descriptions)
        binding.descriptionSpinnerView.adapter = variantDescAdapter
        resetOnDescriptionSelection()
    }

    private fun resetOnDescriptionSelection() {
        variants = variants.subList(0, 1)
        variantAdapter?.clear()
        variantAdapter?.addAll(variants.map { it.name })
        resetOnVariantSelection()
    }

    private fun resetOnVariantSelection() {
        transmissions = transmissions.subList(0, 1)
        transmissionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, this.transmissions)
        binding.transmissionSpinnerView.adapter = transmissionAdapter

        // transmissionAdapter?.addAll(transmissions)
    }

    private fun resetErrors() {
        (binding.descriptionSpinnerView?.selectedView as? TextView)?.error = null
        (binding.transmissionSpinnerView?.selectedView as? TextView)?.error = null
        (binding.variantSpinnerView?.selectedView as? TextView)?.error = null
        Utility.setVisibility(false, binding.fuelErrorView)
    }

    companion object {
        const val ARG_VEHICLE = "arg_vehicle"

        fun getIntent(context: Context, vehicle: Vehicle): Intent {
            return Intent(context, AddMissingVehicleInfoActivity::class.java).apply {
                putExtra(ARG_VEHICLE, vehicle)
            }
        }
    }
}
