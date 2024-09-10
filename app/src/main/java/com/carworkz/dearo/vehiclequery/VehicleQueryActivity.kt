package com.carworkz.dearo.vehiclequery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.carworkz.dearo.databinding.ActivityMainBinding
import com.carworkz.dearo.databinding.ActivityVehicleQueryBinding
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.extensions.startActivityForResult
import com.carworkz.dearo.interactionprovider.ToolBarInteractionProvider
import com.carworkz.dearo.partfinder.PartFinderActivity
import com.carworkz.dearo.screencontainer.SingleTextActionScreenContainer/*import kotlinx.android.synthetic.main.activity_vehicle_query.*
import kotlinx.android.synthetic.main.base_layout.*
import kotlinx.android.synthetic.main.layout_vehicle_type.**/
import timber.log.Timber
import javax.inject.Inject

class VehicleQueryActivity : ScreenContainerActivity(), ToolBarInteractionProvider,
    VehicleQueryContract.View, RadioGroup.OnCheckedChangeListener,
    AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityVehicleQueryBinding

    @Inject
    lateinit var presenter: VehicleQueryPresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    private lateinit var makeList: MutableList<Make>
    private lateinit var modelList: MutableList<Model>
    private lateinit var descList: MutableList<Variant>
    private lateinit var variantList: MutableList<Variant>
    private lateinit var filteredVariantList: MutableList<Variant>

    private lateinit var makeAdapter: ArrayAdapter<String>
    private lateinit var modelAdapter: ArrayAdapter<String>
    private lateinit var variantDescAdapter: ArrayAdapter<String>
    private lateinit var variantAdapter: ArrayAdapter<String>
    private var vehicle = Vehicle()
    private var init = false
    private var weight = 0.5f
    private var fuelTypes = ArrayList<String?>()
    private var transmissionTypes = ArrayList<String?>()

    private var vehicleType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as DearOApplication).repositoryComponent.COMPONENT(
                VehicleQueryPresenterModule(
                    this
                )
            ).inject(this)
        screenTracker.sendScreenEvent(
            this, ScreenTracker.SCREEN_PART_FINDER_VEHICLE_DETAILS, this.javaClass.simpleName
        )
        initLists()
        binding.makeView.onItemSelectedListener = this
        binding.modelView.onItemSelectedListener = this
        binding.variantDescView.onItemSelectedListener = this
        binding.variantView.onItemSelectedListener = this
        init = true
        binding.fuelView.setOnCheckedChangeListener(this)
        binding.transmissionView.setOnCheckedChangeListener(this)
        if (SharedPrefHelper.getWorkshopVehicleType().size > 1) {
            binding.layOutVehicleType.parentVehicleTypeView.visibility = View.VISIBLE
            setVehicleTypeToggle()
        } else {
            binding.layOutVehicleType.parentVehicleTypeView.visibility = View.GONE
        }
        presenter.getMake(vehicleType)
    }

    override fun onPause() {
        init = false
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({ init = true }, 500)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_PART_FINDER -> {
                if (resultCode == Activity.RESULT_OK && data?.extras != null) vehicle =
                    data.extras?.getParcelable(PartFinderActivity.VEHICLE)!!
            }
        }
    }

    override fun onItemSelected(spinner: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
        if (init) {
            when (spinner) {
                binding.makeView -> {
                    if (pos == 0) {
                        vehicle.makeSlug = null
                        vehicle.makeName = null
                        binding.modelView.setSelection(0)
                    } else {
                        binding.makeErrorView.error = null
                        vehicle.makeSlug = makeList[pos].slug
                        vehicle.makeName = makeList[pos].name
                        presenter.getModel(vehicle.makeSlug)
                    }
                    clearLists(
                        arrayListOf(
                            CLEAR_MODEL, CLEAR_VARIANT, CLEAR_FILTERED_VARIANT, CLEAR_VARIANT_DESC
                        )
                    )
                }

                binding.modelView -> {
                    if (pos == 0) {
                        vehicle.modelSlug = null
                        vehicle.modelName = null
                        binding.variantDescView.setSelection(0)
                        binding.variantView.setSelection(0)
                        binding.variantDescView.setSelection(0)
                        binding.fuelErrorView.visibility = View.GONE
                        binding.fuelView.clearCheck()
                    } else {
                        binding.modelErrorView.error = null
                        vehicle.modelSlug = modelList[pos].slug
                        vehicle.modelName = modelList[pos].name
                        presenter.getVariant(vehicle.modelSlug)
                        vehicle.fuelType = null
                        vehicle.transmissionType = null
                        fuelTypes.clear()
                        binding.variantDescView.setSelection(0)
                        binding.variantView.setSelection(0)
                        binding.fuelView.removeAllViews()
                        binding.fuelView.clearCheck()
                        weight = 0.5f
                    }
                    clearLists(
                        arrayListOf(
                            CLEAR_VARIANT, CLEAR_FILTERED_VARIANT, CLEAR_VARIANT_DESC
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
                        clearLists(arrayListOf(CLEAR_FILTERED_VARIANT))
                        clearTransmission()
                    }
                    variantAdapter = ArrayAdapter(this,
                        R.layout.picker_item,
                        filteredVariantList.map { it.name })
                    binding.variantView.adapter = variantAdapter
                    val index =
                        filteredVariantList.indexOf(filteredVariantList.find { it.code == vehicle.variantCode })
                    if (index != -1) {
                        binding.variantView.setSelection(index)
                    } else {
                        binding.variantView.setSelection(if (filteredVariantList.size == 2) filteredVariantList.lastIndex else 0)
                    }
                }

                binding.variantView -> {
                    if (pos != 0) {
                        val selected = filteredVariantList[pos]
                        Timber.d(selected.transmissionType)
                        vehicle.variantCode = selected.code
                        vehicle.variantName = selected.name
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
                        vehicle.variantName = null
                        binding.transmissionView.clearCheck()
                        clearTransmission()
                    }
                    binding.variantErrorView.error = null
                }
            }
        }
    }

    private fun clearTransmission() {
        transmissionTypes.clear()
        binding.transmissionView.removeAllViews()
        binding.transmissionView.clearCheck()
        binding.transmissionContainer.visibility = View.GONE
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun displayMake(obj: List<Make>) {
        clearLists(arrayListOf(CLEAR_MAKE, CLEAR_MODEL, CLEAR_VARIANT))
        makeList.addAll(obj)
        makeAdapter = ArrayAdapter(this, R.layout.spinner_item, makeList.map { it.name!! })
        binding.makeView.adapter = makeAdapter
        binding.makeView.setSelection(makeList.indexOf(makeList.find { it.slug == vehicle.makeSlug }
            ?: 0))
    }

    override fun displayModel(model: List<Model>) {
        modelList.addAll(model as ArrayList<Model>)
        modelAdapter = ArrayAdapter<String>(this, R.layout.picker_item, modelList.map { it.name })
        binding.modelView.adapter = modelAdapter
        binding.modelView.setSelection(modelList.indexOf(modelList.find { it.slug == vehicle.modelSlug }
            ?: 0))
    }

    override fun displayVariant(variant: List<Variant>) {
        variantList.addAll(variant as ArrayList)
        fuelTypes.addAll(variant.map { it.fuelType }.distinct())
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
                        variantDescAdapter = ArrayAdapter(this,
                            R.layout.picker_item,
                            descList.map { it.description!! })
                        binding.variantDescView.adapter = variantDescAdapter
                        val index =
                            descList.indexOf(descList.find { it.description == vehicle.description })
                        if (index != -1) {
                            binding.variantDescView.setSelection(index)
                        } else {
                            binding.variantDescView.setSelection(if (descList.size == 2) descList.lastIndex else 0)
                        }
                    } else {
                        filteredVariantList.addAll(variantList.filter { it.fuelType == vehicle.fuelType })
                        binding.variantDescErrorView.visibility = View.GONE
                        variantAdapter = ArrayAdapter(this,
                            R.layout.picker_item,
                            filteredVariantList.map { it.name })
                        binding.variantView.adapter = variantAdapter
                        val index =
                            variantList.indexOf(variantList.find { it.code == vehicle.variantCode })
                        if (index != -1) {
                            binding.variantView.setSelection(index)
                        } else {
                            binding.variantView.setSelection(if (filteredVariantList.size == 2) filteredVariantList.lastIndex else 0)
                        }
                    }
                }

                binding.transmissionView -> vehicle.transmissionType = transmissionTypes[itemId]
            }
        } else {
            when (radioGroup) {
                binding.fuelView -> vehicle.fuelType = null
                binding.transmissionView -> vehicle.transmissionType = null
            }
        }
    }

    override fun createScreenContainer(): ScreenContainer = SingleTextActionScreenContainer(this)
    override fun getViewBinding(
        inflater: LayoutInflater?, container: ViewGroup?, attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityVehicleQueryBinding.inflate(inflater!!, container, attachToParent)
        return binding
    }

    override fun getProgressView(): View = ProgressBar(this)

    override fun getToolBarTitle(): String = "Vehicle Details"

    override fun getActionBtnTitle(): String = "NEXT"

    override fun onActionBtnClick() {
        if (vehicle.makeSlug != null && vehicle.modelSlug != null && vehicle.fuelType != null && vehicle.transmissionType != null && ((descList.size > 1 && vehicle.description != null) || (descList.size <= 1 && vehicle.description == null)) && vehicle.variantCode != null) {
            startActivityForResult<PartFinderActivity>(
                REQUEST_CODE_PART_FINDER, PartFinderActivity.VEHICLE to vehicle
            )
//            val intent =Intent(this,PartFinderActivity.javaClass)
//            intent.putExtra(PartFinderActivity.VEHICLE,vehicle)
//            startActivityForResult(intent,REQUEST_CODE_PART_FINDER)
            vehicle.vehicleType = vehicleType
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left)
        } else {
            if (vehicle.makeSlug == null) {
                binding.makeErrorView.error = "Make Required"
            }
            if (vehicle.modelSlug == null) {
                binding.modelErrorView.error = "Model Required"
            }
            if (vehicle.fuelType == null) {
                binding.fuelErrorView.error = "Fuel Type Required"
            }
            if (descList.size > 1 && vehicle.description == null) {
                binding.variantDescErrorView.error = "Description Required"
            }
            if (vehicle.variantCode == null) {
                binding.variantErrorView.error = "Variant Required"
            }
        }
    }

    override fun dismissProgressIndicator() {
        super.dismissProgressBar()
    }

    override fun showProgressIndicator() {
        super.showProgressBar()
    }

    override fun showGenericError(errorMsg: String) {
    }

    private fun initLists() {
        makeList = ArrayList()
        modelList = ArrayList()
        descList = ArrayList()
        variantList = ArrayList()
        filteredVariantList = ArrayList()

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

        makeAdapter = ArrayAdapter<String>(this, R.layout.picker_item, makeList.map { it.name })
        binding.makeView.adapter = makeAdapter
        modelAdapter = ArrayAdapter<String>(this, R.layout.picker_item, modelList.map { it.name })
        binding.modelView.adapter = modelAdapter
        variantDescAdapter =
            ArrayAdapter(this, R.layout.picker_item, descList.map { it.description!! })
        binding.variantDescView.adapter = variantDescAdapter
        variantAdapter =
            ArrayAdapter(this, R.layout.picker_item, filteredVariantList.map { it.name })
        binding.variantView.adapter = variantAdapter
    }


    private fun addRadioButton(name: String?, position: Int, type: String) {
        val radioButton = RadioButton(this)
        radioButton.layoutParams = RadioGroup.LayoutParams(
            0, RadioGroup.LayoutParams.WRAP_CONTENT, weight
        )
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
    }

    private fun clearLists(listsToClear: ArrayList<String>) {
        listsToClear.forEach { listToClear ->
            when (listToClear) {
                CLEAR_MAKE -> {
                    makeList = makeList.subList(0, 1)
                    makeAdapter =
                        ArrayAdapter(this, R.layout.picker_item, makeList.map { it.name!! })
                    binding.makeView.adapter = makeAdapter
                }

                CLEAR_MODEL -> {
                    modelList = modelList.subList(0, 1)
                    modelAdapter =
                        ArrayAdapter(this, R.layout.picker_item, modelList.map { it.name!! })
                    binding.modelView.adapter = modelAdapter
                }

                CLEAR_VARIANT_DESC -> {
                    descList = descList.subList(0, 1)
                    variantDescAdapter =
                        ArrayAdapter(this, R.layout.picker_item, descList.map { it.description!! })
                    binding.variantDescView.adapter = variantDescAdapter
                }

                CLEAR_FILTERED_VARIANT -> {
                    filteredVariantList = filteredVariantList.subList(0, 1)
                    variantAdapter = ArrayAdapter(this,
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

    private fun setVehicleTypeToggle() {
        vehicleType = VehicleType.VEHICLE_TYPE_CAR
        toggleVehicle()
        binding.layOutVehicleType.carVehicleView.setOnClickListener {
            vehicle.makeSlug = null
            vehicle.modelSlug = null
            vehicleType = VehicleType.VEHICLE_TYPE_CAR
            toggleVehicle()
            presenter.getMake(vehicleType)
        }

        binding.layOutVehicleType.bikeVehicleView.setOnClickListener {
            vehicle.makeSlug = null
            vehicle.modelSlug = null
            vehicleType = VehicleType.VEHICLE_TYPE_BIKE
            toggleVehicle()
            presenter.getMake(vehicleType)
        }
    }

    private fun toggleVehicle() {
        when (vehicleType) {
            VehicleType.VEHICLE_TYPE_CAR -> {
                binding.layOutVehicleType.carVehicleView.setBackgroundColor(
                    ContextCompat.getColor(
                        this, R.color.colorPrimary
                    )
                )
                binding.layOutVehicleType.bikeVehicleView.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.white
                    )
                )
                binding.layOutVehicleType.carIconView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this, R.drawable.ic_directions_car_white_24dp
                    )
                )
                binding.layOutVehicleType.bikeIconView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this, R.drawable.ic_motorcycle_black_24dp
                    )
                )
            }

            VehicleType.VEHICLE_TYPE_BIKE -> {
                binding.layOutVehicleType.carVehicleView.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.white
                    )
                )
                binding.layOutVehicleType.bikeVehicleView.setBackgroundColor(
                    ContextCompat.getColor(
                        this, R.color.colorPrimary
                    )
                )
                binding.layOutVehicleType.carIconView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this, R.drawable.ic_directions_car_black_24dp
                    )
                )
                binding.layOutVehicleType.bikeIconView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this, R.drawable.ic_motorcycle_white_24dp
                    )
                )
            }
        }
    }

    companion object {
        const val REQUEST_CODE_PART_FINDER = 100
        const val CLEAR_MAKE = "clear make"
        const val CLEAR_MODEL = "clear model"
        const val CLEAR_FILTERED_VARIANT = "clear_filtered_variant"
        const val CLEAR_VARIANT = "clear_variant"
        const val CLEAR_VARIANT_DESC = "clear_variant_desc"
        const val TRANSMISSION = "Transmission"
        const val FUEL = "Fuel"
    }
}