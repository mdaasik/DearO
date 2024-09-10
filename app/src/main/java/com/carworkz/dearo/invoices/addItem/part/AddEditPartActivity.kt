package com.carworkz.dearo.invoices.addItem.part

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.estimate.partnumberselection.PartNumberSelectionAdapter
import com.carworkz.dearo.addjobcard.createjobcard.estimate.partnumberselection.searchpartnumber.SearchPartNumberActivity
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.base.BaseActivity
import com.carworkz.dearo.customviews.toggle.interfaces.OnToggledListener
import com.carworkz.dearo.customviews.toggle.model.ToggleableView
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.databinding.ActivityAddItemBinding
import com.carworkz.dearo.databinding.ActivityNewAddPartBinding
import com.carworkz.dearo.dirtydetector.DirtyDetector
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.extensions.*
import com.carworkz.dearo.searchabledialog.SearchableDialog
import com.carworkz.dearo.utils.Constants
import com.carworkz.dearo.utils.Utility
import com.google.android.material.appbar.AppBarLayout
/*import kotlinx.android.synthetic.main.activity_add_item.*
import kotlinx.android.synthetic.main.activity_new_add_part.*
import kotlinx.android.synthetic.main.activity_new_add_part.totalView
import kotlinx.android.synthetic.main.layout_add_part_new.*
import kotlinx.android.synthetic.main.layout_add_part_new.brandView
import kotlinx.android.synthetic.main.layout_add_part_new.focSwitch
import kotlinx.android.synthetic.main.layout_add_part_new.hsnParentView
import kotlinx.android.synthetic.main.layout_add_part_new.partNumberView
import kotlinx.android.synthetic.main.layout_add_part_new.quantityView
import kotlinx.android.synthetic.main.layout_extended_fab.**/
import timber.log.Timber
import javax.inject.Inject

class AddEditPartActivity : BaseActivity(), AddPartContract.View,
    AppBarLayout.OnOffsetChangedListener, TextWatcher, View.OnFocusChangeListener,
    AdapterView.OnItemSelectedListener,
    AdapterView.OnItemClickListener,
    RadioGroup.OnCheckedChangeListener, View.OnClickListener, OnToggledListener {
    private lateinit var binding: ActivityNewAddPartBinding
    @Inject
    lateinit var presenter: AddPartPresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    private lateinit var part: Part
    private lateinit var invoiceId: String
    private lateinit var jobCardId: String
    private var taskHandler = Handler()
    private lateinit var amtRunnable: Runnable
    private lateinit var priceRunnable: Runnable
    private lateinit var quantityRunnable: Runnable
    private lateinit var sgstRunnable: Runnable
    private lateinit var cgstRunnable: Runnable
    private var canReduceDiscount = false
    private var discAmt: Double = 0.0
    private var partNumberList = ArrayList<PartNumber>()
    private var brandList = ArrayList<BrandName>()
    private var brandId: String? = null
    private var isTheTitleVisible = false
    private var isTheTitleContainerVisible = true
    private var canDelete = false
    private var hsnList: List<HSN> = ArrayList()
    private var init = false
    private var enableDiscount = false
    private val discountTypes = arrayListOf("in (%)", "in (\u20B9)")
    private var vehicleType: String? = null
    private val dirtyDetector = DirtyDetector.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntentData()
        binding = ActivityNewAddPartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        createComponent()
        createRunnables()
        init()
        startAlphaAnimation(binding.partTitleView, 0, View.INVISIBLE)
        screenTracker.sendScreenEvent(
            this,
            if (canDelete) ScreenTracker.SCREEN_EDIT_PART else ScreenTracker.SCREEN_ADD_PART,
            this.javaClass.name
        )
        if (SharedPrefHelper.isGstEnabled()) {
            presenter.getHSN()
        }
        dirtyDetector.observe(part)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
        taskHandler.removeCallbacks(amtRunnable)
        taskHandler.removeCallbacks(priceRunnable)
        taskHandler.removeCallbacks(quantityRunnable)
        taskHandler.removeCallbacks(sgstRunnable)
        taskHandler.removeCallbacks(cgstRunnable)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_SEARCH_PART_NUMBER -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val partNumber =
                        data.getParcelableExtra<PartNumber>(PartNumberSelectionAdapter.ARG_PART_NUMBER)
                    Timber.d(" selected parnumber $partNumber")
                    partNumber?.let {
                        part.partNumber = partNumber.partNumber
                        part.description = partNumber.description
                        part.brand.name = partNumber.brandName
                        part.brand.id = partNumber.brandId
                        if (partNumber.unit != part.unit) {
                            part.unit = partNumber.unit
                            part.quantity = 1f
                        }
                        if (partNumber.parts.isNotEmpty()) {
                            part.tax.cgst = partNumber.parts.first().tax.cgst
                            part.tax.sgst = partNumber.parts.first().tax.sgst
                            part.tax.hsn = partNumber.parts.first().tax.hsn
                        }

                        partNumber.tax?.let {
                            if (it.hsn.isNullOrEmpty().not()) {
                                part.tax = it
                            }
                        }

                        part.price = partNumber.unitPrice.toDouble()
                    }
                    updatePart()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.deleteView -> {
                if (checkIfNetworkAvailable()) {
                    alert("Are you sure you want to delete this item?", "Delete Confirm") {
                        yesButton {
                            presenter.deletePart(invoiceId, part.uid!!)
                        }
                        noButton {
                        }
                    }.show()
                }
            }
            binding.layoutExtendedFab.actionParentView -> {
                if (validate()) {
                    savePart()
                } else {
                    setErrors()
                }
            }
            binding.searchFab -> {
                startActivityForResult(
                    SearchPartNumberActivity.getIntent(
                        this, part.text
                            ?: "", jobCardId, null, vehicleType
                    ), REQUEST_CODE_SEARCH_PART_NUMBER
                )
            }
        }
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, offset: Int) {
        val maxScroll = appBarLayout.totalScrollRange
        val percentage = Math.abs(offset).toFloat() / maxScroll.toFloat()

        handleAlphaOnTitle(percentage)
        handleToolbarTitleVisibility(percentage)
    }

    override fun afterTextChanged(s: Editable?) {
        if (init)
            when (s?.hashCode()) {
                binding.layoutAddPartNew.discountView.text.hashCode() -> {
                    if (enableDiscount) {
                        taskHandler.removeCallbacksAndMessages(amtRunnable)
                        taskHandler.postDelayed(amtRunnable, DELAY)
                    }
                }
                binding.layoutAddPartNew.mrpView.text.hashCode() -> {
                    taskHandler.removeCallbacksAndMessages(priceRunnable)
                    taskHandler.postDelayed(priceRunnable, DELAY)
                }
                binding.layoutAddPartNew.sgstView.text.hashCode() -> {
                    taskHandler.removeCallbacks(sgstRunnable)
                    taskHandler.postDelayed(sgstRunnable, DELAY)
                }
                binding.layoutAddPartNew.cgstView.text.hashCode() -> {
                }
                binding.layoutAddPartNew.quantityView.text.hashCode() -> {
                    taskHandler.removeCallbacksAndMessages(quantityRunnable)
                    taskHandler.postDelayed(quantityRunnable, DELAY)
                }
                binding.layoutAddPartNew.partDescView.text.hashCode() -> {
                    if (s.toString().isNotEmpty()) {
                        part.description = s.toString()
                    }
                }
                binding.layoutAddPartNew.brandView.text.hashCode() -> {
                    if (binding.layoutAddPartNew.brandView.text.toString().isNotEmpty() && brandList.map { it.name }
                            .find { it == s.toString() } == null) {
                        presenter.fetchBrandName(
                            binding.layoutAddPartNew.brandView.text.toString(),
                            jobCardId,
                            part.partNumber,
                            vehicleType
                        )
                    }
                    part.brand.name = s.toString()
                    brandId = s.toString()
                }
                binding.layoutAddPartNew.partNumberView.text.hashCode() -> {
                    if (SharedPrefHelper.isPartFinderEnabled() && binding.layoutAddPartNew.partNumberView.text.toString()
                            .isNotEmpty() && partNumberList.map { it.partNumber }
                            .find { it == s.toString() } == null
                    ) {
                        Handler().postDelayed({
                            presenter.fetchPartNumber(
                                s.toString(),
                                part.id,
                                jobCardId,
                                brandId,
                                vehicleType
                            )
                        }, 1000)
                    } else {
                        binding.layoutAddPartNew.mrpView.isEnabled = SharedPrefHelper.isPartPriceEditable()
                    }
                }
            }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v) {
            binding.layoutAddPartNew.discountView -> {
                if (hasFocus) {
                    if (binding.layoutAddPartNew.discountView.text.toString()
                            .isNotEmpty() && binding.layoutAddPartNew.discountView.text.toString() == "0.0"
                    )
                        binding.layoutAddPartNew.discountView.text.clear()
                }
            }
            binding.layoutAddPartNew.mrpView -> {
                if (hasFocus) {
                    if (binding.layoutAddPartNew.mrpView.text.toString().isNotEmpty() && binding.layoutAddPartNew.mrpView.text.toString() == "0.0")
                        binding.layoutAddPartNew.mrpView.text.clear()
                }
            }

            binding.layoutAddPartNew.brandView -> {
                if (hasFocus) {
                    presenter.fetchBrandName(
                        binding.layoutAddPartNew.brandView.text.toString(),
                        jobCardId,
                        part.partNumber,
                        vehicleType
                    )
                }
            }
            binding.layoutAddPartNew.partNumberView -> {
                if (hasFocus && SharedPrefHelper.isPartFinderEnabled()) {
                    presenter.fetchPartNumber(
                        binding.layoutAddPartNew.partNumberView.text.toString(),
                        part.id,
                        jobCardId,
                        brandId,
                        vehicleType
                    )
                }
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (init) {
            when (parent) {
                binding.layoutAddPartNew.unitsListView -> {
                    part.unit = if (part.units != null) {
                        part.units!![position]
                    } else {
                        Constants.BusinessConstants.UNITS_LIST[position]
                    }
                }
                binding.layoutAddPartNew.discountListView -> {
                    if (position == 0) {
                        part.discount.mode = Discount.MODE_PERCENTAGE
                    } else {
                        part.discount.mode = Discount.MODE_PRICE
                    }
                    binding.layoutAddPartNew.discountView.setText(0.0.toString())
                    setTotal()
                }
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (view) {
            binding.layoutAddPartNew.brandView -> {
                part.brand.name = brandList[position].name
                brandId = brandList[position].id
            }
            binding.layoutAddPartNew.partNumberView -> {
                val selectedPartNumber = partNumberList[position]
                part.partNumber = selectedPartNumber.partNumber
                part.tax = selectedPartNumber.tax
                binding.layoutAddPartNew.mrpView.setText(
                    Utility.round(partNumberList[position].unitPrice.toDouble(), 1).toString()
                )
                Timber.d("Unit Price", partNumberList[position].unitPrice?.toString())
                binding.layoutAddPartNew.partDescView.setText(partNumberList[position].description)
            }
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
    }

    override fun getProgressView(): View = binding.progressBarView

    override fun displayHsnList(hsnList: MutableList<HSN>) {
        val dummyHsn = HSN()
        dummyHsn.cgst = 0.0
        dummyHsn.sgst = 0.0
        hsnList.add(0, dummyHsn)
        this.hsnList = hsnList
        if (hsnList.isNotEmpty()) {
            val selectedHsn = hsnList.find { it.hsn == part.tax.hsn } ?: hsnList.first()
            binding.layoutAddPartNew.cgstView.setText(selectedHsn.cgst.toString())
            binding.layoutAddPartNew.sgstView.setText(selectedHsn.sgst.toString())
            binding.layoutAddPartNew.hsnView.text = selectedHsn.displayText
            part.tax.hsn = selectedHsn.hsn
            part.tax.sgst = selectedHsn.sgst
            part.tax.cgst = selectedHsn.cgst
            if (isHsnEditable()) {
                binding.layoutAddPartNew.hsnParentView.setOnClickListener {
                    SearchableDialog.Builder(this, hsnList)
                        .setTitle("HSN CODE")
                        .setSelectedItem(selectedHsn)
                        .setItemSelectedListener(object :
                            SearchableDialog.OnSearchItemSelectedListener<HSN> {
                            override fun onItemSelectedItem(item: HSN?) {
                                item?.let {
                                    binding.layoutAddPartNew.cgstView.setText(it.cgst.toString())
                                    binding.layoutAddPartNew.sgstView.setText(it.sgst.toString())
                                    binding.layoutAddPartNew.hsnView.text = it.displayText
                                    part.tax.hsn = it.hsn
                                    part.tax.sgst = it.sgst
                                    part.tax.cgst = it.cgst
                                }
                            }

                            override fun onCancel() {
                            }
                        }).show()
                }
            }
        }
    }

    override fun displayBrandNames(brandNameList: List<BrandName>) {
        brandList = brandNameList as ArrayList<BrandName>
        binding.layoutAddPartNew.brandView.setAdapter(
            ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                brandNameList.map { it.name })
        )
        // when he lands on page if value is found in list DD isn't shown
        if (brandNameList.find { it.name == binding.layoutAddPartNew.brandView.text.toString() } == null && brandNameList.isNotEmpty()) {
            binding.layoutAddPartNew.brandView.showDropDown()
            binding.layoutAddPartNew.mrpView.isEnabled = true
        } else {
            binding.layoutAddPartNew.mrpView.isEnabled = SharedPrefHelper.isPartPriceEditable()
        }
        Timber.d("Brand", brandNameList.joinToString(",") { it.name })
    }

    override fun displayPartNumbers(partNumberList: List<PartNumber>) {
        this.partNumberList = partNumberList as ArrayList<PartNumber>
        binding.layoutAddPartNew.partNumberView.setAdapter(
            ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                partNumberList.map { it.partNumber })
        )
        // when he lands on page if value is found in adapterList DD isn't shown
        if (this.partNumberList.find { it.partNumber == binding.layoutAddPartNew.partNumberView.text.toString() } == null && binding.layoutAddPartNew.partNumberView.hasFocus() && partNumberList.isNotEmpty()) {
            binding.layoutAddPartNew.partNumberView.showDropDown()
            binding.layoutAddPartNew.mrpView.isEnabled = true
        } else {
            binding.layoutAddPartNew.mrpView.isEnabled = SharedPrefHelper.isPartPriceEditable()
        }

        Timber.d(partNumberList.joinToString(",") { it.partNumber })
    }

    override fun brandError(error: String) {
        binding.layoutAddPartNew.brandView.error = error
    }

    override fun partNumberError(error: String) {
        binding.layoutAddPartNew.partNumberView.error = error
    }

    override fun moveToNextScreen() {
        finish()
    }

    override fun onPartSavedSuccess(part: Part) {
        setResult(Activity.RESULT_OK, Intent().putExtra(Part.TAG, part))
        finish()
    }

    override fun onPartDeleted() {
        setResult(Activity.RESULT_CANCELED, Intent().putExtra(Part.TAG, part))
        finish()
    }

    override fun showProgressIndicator() {
        super.showProgressBar()
    }

    override fun dismissProgressIndicator() {
        super.dismissProgressBar()
    }

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg) { _, _ -> }
    }

    private fun initDiscount() {
        if (enableDiscount) {
            binding.layoutAddPartNew.discountListView.adapter =
                ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, discountTypes)
            if (part.discount.mode == MODE_PERCENTAGE) {
                binding.layoutAddPartNew.discountListView.setSelection(0, false)
                binding.layoutAddPartNew.discountView.setText(part.discount.amount.toString())
            }
            if (part.discount.mode == MODE_PRICE) {
                binding.layoutAddPartNew.discountListView.setSelection(1, false)
                binding.layoutAddPartNew.discountView.setText(part.discount.amount.toString())
                val length = 7
                binding.layoutAddPartNew.discountView.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(length))
            }
        } else {
            binding.layoutAddPartNew.discountParentView.visibility = View.GONE
        }
    }

    private fun savePart() {
       /* if (canReduceDiscount) {
            if (discAmt < part.discount.amount) {
                toast("Discount cannot be less than ${part.discount.amount}%")
                return
            }
            part.discount.amount = discAmt
        } else {
            part.discount.amount = discAmt
        }*/
        part.discount.amount = discAmt
        part.text = binding.partNameView.text.toString()
        part.partNumber = binding.layoutAddPartNew.partNumberView.text.toString()
        if (checkIfNetworkAvailable()) {
            presenter.savePart(invoiceId, part)
        }
    }

    private fun initToolbar() {
        setSupportActionBar(binding.addPartToolbarView)
        val actionBar = supportActionBar
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setDisplayShowTitleEnabled(true)
        binding.addPartToolbarView.setNavigationIcon(R.drawable.ic_clear_white_24dp)
    }
    private fun init() {
        binding.layoutExtendedFab.actionParentView.visibility = View.VISIBLE
        binding.layoutExtendedFab.actionTitleView.text = getString(R.string.add_edit_btn_save_changes)
        binding.layoutExtendedFab.actionIconView.setImageDrawable(resources.getDrawable(R.drawable.ic_save_white_24dp, theme))
        if (canDelete) {
            binding.deleteView.visibility = View.VISIBLE
            binding.deleteView.setOnClickListener(this)
        } else {
            binding.deleteView.visibility = View.GONE
        }
        binding.layoutAddPartNew.brandView.setText(part.brand.name)
        binding.layoutAddPartNew.partNumberView.setText(part.partNumber)
        binding.partNameView.text = part.text
        binding.partTitleView.text = part.text
        binding.layoutAddPartNew.partDescView.setText(part.description)
        binding.layoutAddPartNew.mrpView.setText(Utility.round(part.price, 1).toString())
        binding.layoutAddPartNew.quantityView.setText(part.quantity.toString())
        binding.layoutAddPartNew.mrpView.isEnabled = SharedPrefHelper.isPartPriceEditable()
        if (SharedPrefHelper.isGstEnabled()) {
            /*if gst is disabled will there be tax node from api?*/
            binding.layoutAddPartNew.sgstView.setText(part.tax.sgst.toString())
            binding.layoutAddPartNew.cgstView.setText(part.tax.cgst.toString())
        } else {
            binding.layoutAddPartNew.sgstView.setText("0")
            binding.layoutAddPartNew.cgstView.setText("0")
            part.tax.sgst = 0.0
            part.tax.cgst = 0.0
//            hsnListView.adapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, arrayOf("N/A"))
//            hsnParentView.isEnabled = false
        }

        if (SharedPrefHelper.isPartFinderEnabled().not()) {
            Utility.setVisibility(false, binding.searchFab)
        }

        when {
            part.units != null && part.unit != null -> {
                binding.layoutAddPartNew.unitsListView.adapter =
                    ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, part.units!!)
                binding.layoutAddPartNew.unitsListView.setSelection(
                    part.units?.indexOf(part.units?.find { it == part.unit })
                        ?: 0
                )
            }
            part.units == null && part.unit != null -> {

                binding.layoutAddPartNew.unitsListView.adapter = ArrayAdapter(
                    this,
                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                    Constants.BusinessConstants.UNITS_LIST
                )
                Timber.d(
                    "position",
                    Constants.BusinessConstants.UNITS_LIST.indexOf(Constants.BusinessConstants.UNITS_LIST.find { it == part.unit })
                        .toString()
                )
                binding.layoutAddPartNew.unitsListView.setSelection(Constants.BusinessConstants.UNITS_LIST.indexOf(Constants.BusinessConstants.UNITS_LIST.find { it == part.unit }))
            }
            part.units != null && part.unit == null -> {
                binding.layoutAddPartNew.unitsListView.adapter =
                    ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, part.units!!)
            }
            else -> {
                binding.layoutAddPartNew.unitsListView.adapter = ArrayAdapter(
                    this,
                    R.layout.simple_hsn_spinner_item,
                    Constants.BusinessConstants.UNITS_LIST
                )
            }
        }
        (binding.layoutAddPartNew.unitsListView.adapter as ArrayAdapter<*>).setDropDownViewResource(R.layout.simple_hsn_spinner_item1)
        binding.layoutAddPartNew.unitsListView.prompt = "Select Units"
        initDiscount()
        if (canReduceDiscount) {
            binding.layoutAddPartNew.discountListView.isEnabled = false
            binding.layoutAddPartNew.discountListView.isClickable = false
        }
        init = true
        initListeners()
        setTotal()
        if(SharedPrefHelper.getFOC())
        {
            binding.layoutAddPartNew.focSwitch.setOnToggledListener(this)
            binding.layoutAddPartNew.focSwitch.isEnabled = true
            binding.layoutAddPartNew.focSwitch.visibility=View.VISIBLE
        }
        binding.layoutAddPartNew.focSwitch.isOn=part.isFOC
    }

    private fun initListeners() {
        binding.layoutAddPartNew.quantityView.addTextChangedListener(this)
        binding.layoutAddPartNew.partDescView.addTextChangedListener(this)
        binding.layoutAddPartNew.brandView.addTextChangedListener(this)
        binding.layoutAddPartNew.partNumberView.addTextChangedListener(this)
        binding.layoutAddPartNew.mrpView.addTextChangedListener(this)
        binding.layoutAddPartNew.sgstView.addTextChangedListener(this)
        binding.layoutAddPartNew.cgstView.addTextChangedListener(this)
        binding.layoutAddPartNew.discountListView.onItemSelectedListener = this
        binding.layoutAddPartNew.discountView.onFocusChangeListener = this
        binding.layoutAddPartNew.discountView.addTextChangedListener(this)
        binding.layoutAddPartNew.brandView.onFocusChangeListener = this
        binding.layoutAddPartNew.partNumberView.onFocusChangeListener = this
        binding.layoutAddPartNew.mrpView.onFocusChangeListener = this
        binding.layoutAddPartNew.brandView.onItemClickListener = this
        binding.layoutAddPartNew.partNumberView.onItemClickListener = this
        binding.layoutAddPartNew.unitsListView.onItemSelectedListener = this
        // hsnListView.onItemSelectedListener = this
        binding.searchFab.setOnClickListener(this)
        binding.layoutExtendedFab.actionParentView.setOnClickListener(this)
        binding.appBarView.addOnOffsetChangedListener(this)
    }

    private fun getIntentData() {
        canReduceDiscount = intent.extras!!.getBoolean(ARG_CAN_REDUCE_DISCOUNT, false)
        canDelete = intent.extras!!.getBoolean(ARG_IS_DELETEABLE, false)
        part = intent.extras!!.getParcelable(ARG_PART)!!
        invoiceId = intent.extras!!.getString(ARG_INVOICE_ID).toString()
        jobCardId = intent.extras!!.getString(ARG_JOBCARD_ID).toString()
        vehicleType = intent.extras!!.getString(ARG_VEHICLE_TYPE)
        enableDiscount = intent.extras!!.getBoolean(ARG_ENABLE_DISCOUNT, false)
        discAmt = part.discount.amount
    }

    private fun createComponent() {
        (application as DearOApplication)
            .repositoryComponent
            .COMPONENT(AddPartPresenterModule(this))
            .inject(this)
    }

    private fun updatePart() {
        binding.layoutAddPartNew.brandView.setText(part.brand.name)
        binding.layoutAddPartNew.partNumberView.setText(part.partNumber)
        binding.layoutAddPartNew.partDescView.setText(part.description)
        binding.layoutAddPartNew.mrpView.setText(Utility.round(part.price, 1).toString())
        binding.layoutAddPartNew.sgstView.setText(part.tax.sgst.toString())
        binding.layoutAddPartNew.cgstView.setText(part.tax.cgst.toString())
        binding.layoutAddPartNew.quantityView.setText(part.quantity.toString())
        // find hsn from list to display hsn with description else fallback
        binding.layoutAddPartNew.hsnView.text = hsnList.find { it.hsn == part.tax.hsn }?.displayText ?: part.tax.hsn
        binding.addEditPartParentView.requestFocus()
    }

    private fun createRunnables() {
        amtRunnable = Runnable {
            when {
                part.discount.mode == MODE_PERCENTAGE && Utility.isValidDecimal(binding.layoutAddPartNew.discountView.text.toString()) -> {
                    if (binding.layoutAddPartNew.discountView.text.toString().toFloat() <= 100f) {
                        /*if (canReduceDiscount) {
                            if (discountView.text.toString().toDouble() < discAmt) {
                                if (discountView.text.toString()
                                        .toDouble() < part.discount.amount
                                ) {
                                    toast("Discount cannot be less than ${discAmt}%")
                                } else {
                                    discAmt = discountView.text.toString().toDouble()
                                    setTotal()
                                }
                            } else {
                                discAmt = discountView.text.toString().toDouble()
                                setTotal()
                            }
                        } else {
                            discAmt = discountView.text.toString().toDouble()
                            setTotal()
                        }*/
                        discAmt = binding.layoutAddPartNew.discountView.text.toString().toDouble()
                        setTotal()
                    } else {
                        binding.layoutAddPartNew.discountView.text.clear()
                        toast("Discount cannot be more than 100%")
                    }
                }

                part.discount.mode == MODE_PRICE && Utility.isValidDecimal(binding.layoutAddPartNew.discountView.text.toString()) -> {
                    if (binding.layoutAddPartNew.discountView.text.toString().toFloat() <= part.price * part.quantity) {
                        /*if (canReduceDiscount) {
                            if (discountView.text.toString().toDouble() < discAmt) {
                                if (discountView.text.toString()
                                        .toDouble() < part.discount.amount
                                ) {
                                    toast("Discount cannot be less than ${discAmt}")
                                } else {
                                    discAmt = discountView.text.toString().toDouble()
                                    setTotal()
                                }
                            } else {
                                discAmt = discountView.text.toString().toDouble()
                                setTotal()
                            }
                        } else {
                            discAmt = discountView.text.toString().toDouble()
                            setTotal()
                        }*/
                        discAmt = binding.layoutAddPartNew.discountView.text.toString().toDouble()
                        setTotal()
                    } else {
                        binding.layoutAddPartNew.discountView.text.clear()
                        toast("Discount cannot be more than the total amount")
                    }
                }

                binding.layoutAddPartNew.discountView.text.toString().isEmpty() -> {
                    part.discount.amount = 0.0
                    setTotal()
                }

                else -> {
                    toast("Incorrect Value")
                    part.discount.amount = 0.0
                    setTotal()
                }
            }
        }
        priceRunnable = Runnable {
            if (Utility.isValidDecimal(binding.layoutAddPartNew.mrpView.text.toString())) {
                part.price = Utility.round(binding.layoutAddPartNew.mrpView.text.toString().toBigDecimal().toDouble(), 1)
                setTotal()
                if (part.price == 0.0) {
                    binding.layoutAddPartNew.mrpView.isEnabled = true
                } else {
                    if (binding.layoutAddPartNew.mrpView.text.toString().isEmpty())
                        toast("Invalid Price Input")
                }
            }
        }
        quantityRunnable = Runnable {
            if (Utility.isValidDecimal(binding.layoutAddPartNew.quantityView.text.toString())) {
                part.quantity = binding.layoutAddPartNew.quantityView.text.toString().toFloat()
                part.quantity = binding.layoutAddPartNew.quantityView.text.toString().toFloat()
                setTotal()
            } else {
                if (binding.layoutAddPartNew.quantityView.text.toString().isNotEmpty()) {
                    toast("Invalid Quantity")
                }
            }
        }
        sgstRunnable = Runnable {
            if (binding.layoutAddPartNew.sgstView.text.toString().isNotEmpty() && binding.layoutAddPartNew.cgstView.text.isNotEmpty()) {
                part.tax.sgst = binding.layoutAddPartNew.sgstView.text.toString().toDouble()
                part.tax.cgst = binding.layoutAddPartNew.cgstView.text.toString().toDouble()
                setTotal()
            } else {
                part.tax.sgst = 0.0
            }
        }
        cgstRunnable = Runnable {
            if (binding.layoutAddPartNew.cgstView.text.isNotEmpty() && binding.layoutAddPartNew.sgstView.text.isNotEmpty()) {
                part.tax.cgst = binding.layoutAddPartNew.cgstView.text.toString().toDouble()
                part.tax.sgst = binding.layoutAddPartNew.sgstView.text.toString().toDouble()
                setTotal()
            } else {
                part.tax.cgst = 0.0
            }
        }
    }

    private fun setTotal() {
        val tax: Float =
            if (SharedPrefHelper.isGstEnabled()) (((part.tax.cgst + part.tax.sgst).toFloat() / 100)) else 0.0f
        // part.price = Utility.round(part.rate * (1 + tax), 2)

        part.rate = part.price / (1 + tax)
        val itemSubTotal = part.rate * part.quantity
        val itemDiscountAmount = if (discAmt == 0.0) 0.0 else (if (part.discount.mode == Discount.MODE_PRICE) discAmt else (itemSubTotal * (discAmt / 100)))
        val itemAmountAfterDiscount = itemSubTotal - itemDiscountAmount
        val itemTaxAmount = if (tax > 0.0f) (itemAmountAfterDiscount * tax) else 0.0
        val itemAmount = (itemAmountAfterDiscount + itemTaxAmount) // itemAmountAfterDiscount//
        part.amount = itemAmount
        binding.totalView.text = Utility.convertToCurrency(part.amount)
    }

    private fun setErrors() {
        if (binding.layoutAddPartNew.brandView.text.toString().isEmpty() && SharedPrefHelper.isInventoryEnabled()) {
            binding.layoutAddPartNew.brandView.error = "Brand cannot be Empty"
        }

        if (binding.layoutAddPartNew.partNumberView.text.toString().isEmpty() && SharedPrefHelper.isInventoryEnabled()) {
            binding.layoutAddPartNew.partNumberView.error = "Part Number Cannot be Empty"
        }

        if (part.unit.isNullOrEmpty()) {
            binding.layoutAddPartNew.unitsListView.prompt = "Cannot be blank"
            toast("Units not selected")
            binding.layoutAddPartNew.unitsListView.performClick()
        }
        if (binding.layoutAddPartNew.mrpView.text.isNullOrEmpty() &&binding.layoutAddPartNew. mrpView.text.toString().isEmpty()) {
            binding.layoutAddPartNew.mrpView.error = "Unit Price cannot be Blank"
        }
        if (!Utility.isValidDecimal(binding.layoutAddPartNew.mrpView.text.toString())) {
            binding.layoutAddPartNew.mrpView.error = "Invalid Price"
        }
        if (binding.layoutAddPartNew.discountView.text.toString().isNotEmpty() && binding.layoutAddPartNew.mrpView.text.toString().isNotEmpty()) {
            if ((part.discount.mode == MODE_PRICE && part.discount.amount > (part.rate * part.quantity))) {
                toast("Total cannot be less than 0")
            }
        }
        if (binding.layoutAddPartNew.quantityView.text.toString().isEmpty()) {
            binding.layoutAddPartNew.quantityView.error = "quantity cannot be empty"
        }
        if (!Utility.isValidDecimal(binding.layoutAddPartNew.quantityView.text.toString())) {
            binding.layoutAddPartNew.quantityView.error = "Invalid Quantity"
        }
        if (binding.layoutAddPartNew.quantityView.text.toString()
                .isNotEmpty() && Utility.isValidDecimal(binding.layoutAddPartNew.quantityView.text.toString()) && binding.layoutAddPartNew.quantityView.text.toString()
                .toFloat() <= 0f
        ) {
            binding.layoutAddPartNew.quantityView.error = "Quantity cannot be 0"
        }
        if (!Utility.isValidDecimal(binding.layoutAddPartNew.discountView.text.toString())) {
            binding.layoutAddPartNew.discountView.error = "Invalid Discount"
        }
    }
    private fun validate(): Boolean {
        return when {
            part.unit == null -> false
            !Utility.isValidDecimal(binding.layoutAddPartNew.mrpView.text.toString()) -> false
            !Utility.isValidDecimal(binding.layoutAddPartNew.quantityView.text.toString()) -> false
            binding.layoutAddPartNew.quantityView.text.toString().toFloat() <= 0f -> false
            !isInfoValidBasedOnInventory() -> false
            enableDiscount -> isDiscountValid()
            else -> true
        }
    }

    private fun isInfoValidBasedOnInventory(): Boolean {
        return if (SharedPrefHelper.isInventoryEnabled()) {
            !(binding.layoutAddPartNew.brandView.text.toString().isEmpty() || binding.layoutAddPartNew.partNumberView.text.toString().isEmpty())
        } else {
            true
        }
    }

    private fun isDiscountValid(): Boolean {
        return Utility.isValidDecimal(binding.layoutAddPartNew.discountView.text.toString()) &&
                ((part.discount.mode == MODE_PRICE && part.discount.amount <= (part.rate * part.quantity)) || (part.discount.mode == MODE_PERCENTAGE && part.discount.amount <= 100))
    }

    private fun isHsnEditable(): Boolean {
        return SharedPrefHelper.isGstEnabled() && SharedPrefHelper.isHsnEnabled() && SharedPrefHelper.isHsnPartEnabled() || part.tax.hsn.isNullOrEmpty()
    }

    private fun handleToolbarTitleVisibility(percentage: Float) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if (!isTheTitleVisible) {
                startAlphaAnimation(
                    binding.partTitleView!!,
                    ALPHA_ANIMATIONS_DURATION.toLong(),
                    View.VISIBLE
                )
                isTheTitleVisible = true
            }
        } else {
            if (isTheTitleVisible) {
                startAlphaAnimation(
                    binding.partTitleView!!,
                    ALPHA_ANIMATIONS_DURATION.toLong(),
                    View.INVISIBLE
                )
                isTheTitleVisible = false
            }
        }
    }

    private fun handleAlphaOnTitle(percentage: Float) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (isTheTitleContainerVisible) {
                startAlphaAnimation(
                    binding.partNameTitleView!!,
                    ALPHA_ANIMATIONS_DURATION.toLong(),
                    View.INVISIBLE
                )
                isTheTitleContainerVisible = false
            }
        } else {
            if (!isTheTitleContainerVisible) {
                startAlphaAnimation(
                    binding.partNameTitleView!!,
                    ALPHA_ANIMATIONS_DURATION.toLong(),
                    View.VISIBLE
                )
                isTheTitleContainerVisible = true
            }
        }
    }

    private fun startAlphaAnimation(v: View, duration: Long, visibility: Int) {
        val alphaAnimation = if (visibility == View.VISIBLE)
            AlphaAnimation(0f, 1f)
        else
            AlphaAnimation(1f, 0f)

        alphaAnimation.duration = duration
        alphaAnimation.fillAfter = true
        v.startAnimation(alphaAnimation)
    }

    companion object {
        const val REQUEST_CODE_SEARCH_PART_NUMBER = 100
        const val ARG_CAN_REDUCE_DISCOUNT = "ARG_CAN_REDUCE_DISCOUNT"
        const val ARG_IS_DELETEABLE = "IS_DELETEABLE"
        const val ARG_ENABLE_DISCOUNT = "ENABLE_DISCOUNT"
        const val ARG_PART = "PART"
        const val MODE_PERCENTAGE = "PERCENTAGE"
        const val MODE_PRICE = "PRICE"
        const val ARG_INVOICE_ID = "INVOICE_ID"
        const val ARG_JOBCARD_ID = "JOBCARD_ID"
        const val ARG_VEHICLE_TYPE = "vehicle_type"
        const val DELAY = 1000L
        private const val PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f
        private const val PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f
        private const val ALPHA_ANIMATIONS_DURATION = 200

        fun getIntent(
            context: Context,
            canDelete: Boolean,
            part: Part,
            jobCardId: String,
            invoiceId: String?,
            discountEnabled: Boolean,
            vehicleType: String?,
            isDescountReducable: Boolean
        ): Intent {
            return Intent(context, AddEditPartActivity::class.java).apply {
                putExtra(ARG_CAN_REDUCE_DISCOUNT, isDescountReducable)
                putExtra(ARG_IS_DELETEABLE, canDelete)
                putExtra(ARG_PART, part)
                putExtra(ARG_ENABLE_DISCOUNT, discountEnabled)
                putExtra(ARG_JOBCARD_ID, jobCardId)
                putExtra(ARG_INVOICE_ID, invoiceId)
                putExtra(ARG_VEHICLE_TYPE, vehicleType)
            }
        }
    }

    override fun onSwitched(toggleableView: ToggleableView?, isOn: Boolean) {
        part.isFOC=isOn
    }
}
