package com.carworkz.dearo.outwarding

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.*
import com.carworkz.dearo.addjobcard.createjobcard.estimate.partnumberselection.PartNumberSelectionAdapter
import com.carworkz.dearo.addjobcard.createjobcard.estimate.partnumberselection.PartNumberSelectorActivity
import com.carworkz.dearo.addjobcard.createjobcard.estimate.partnumberselection.searchpartnumber.SearchPartNumberActivity
import com.carworkz.dearo.addjobcard.createjobcard.jobs.viewjc.ViewJCActivity
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.customerapproval.CustomerApprovalActivity
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.databinding.ActivityNewEstimtorBinding
import com.carworkz.dearo.databinding.ActivityOtherSysHistoryDetailsBinding
import com.carworkz.dearo.databinding.BottomNewEstimatorBinding
import com.carworkz.dearo.databinding.BottomSplitInvoiceBinding
import com.carworkz.dearo.databinding.DialogSplitGroupBinding
import com.carworkz.dearo.databinding.LayoutTopEstimatorBinding
import com.carworkz.dearo.databinding.LayoutTopProformaBinding
import com.carworkz.dearo.databinding.LayoutTopSplitInvoiceBinding
import com.carworkz.dearo.dirtydetector.DirtyDetector
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.events.ApproveEvent
import com.carworkz.dearo.extensions.alert
import com.carworkz.dearo.extensions.find
import com.carworkz.dearo.extensions.toast
import com.carworkz.dearo.interactionprovider.TertiaryImgInteractionProvider
import com.carworkz.dearo.morecta.MoreCtaListDialogFragment
import com.carworkz.dearo.oslvendor.OslLabourVendorSelectionAAdapter
import com.carworkz.dearo.outwarding.OutwardingProcessActivity.Companion.SCREEN_TYPE_ESTIMATE
import com.carworkz.dearo.outwarding.OutwardingProcessActivity.Companion.SCREEN_TYPE_PROFORMA
import com.carworkz.dearo.outwarding.helper.*
import com.carworkz.dearo.pdf.Source
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.screencontainer.ActionImgScreenContainer
import com.carworkz.dearo.utils.Constants
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.activity_new_estimtor.*
import kotlinx.android.synthetic.main.bottom_new_estimator.*
import kotlinx.android.synthetic.main.bottom_split_invoice.*
import kotlinx.android.synthetic.main.dialog_split_group.view.*
import kotlinx.android.synthetic.main.layout_top_estimator.*
import kotlinx.android.synthetic.main.layout_top_proforma.*
import kotlinx.android.synthetic.main.layout_top_split_invoice.*
import kotlinx.android.synthetic.main.row_labour_proforma.view.**/
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

/**
 *
 * [OutwardingProcessActivity] is used to manage 3 different types of screens
 *
 * i.e. JobCard Estimator[SCREEN_TYPE_ESTIMATE],Edit Proforma[SCREEN_TYPE_PROFORMA], Edit Split Invoice[SCREEN_TYPE_ESTIMATE]. [SCREEN_TYPE_ESTIMATE] can be view only[isViewOnly] when Job Card state is [JobCard.STATUS_COMPLETED] & beyond.
 *
 * There are 3 main entities displayed here i.e Parts,Labour & Service Package. All can be added using 'Add Item' action present in [SCREEN_TYPE_ESTIMATE] & [SCREEN_TYPE_PROFORMA]
 *
 *
 *
 *
 * */

class OutwardingProcessActivity : ScreenContainerActivity(), EventsManager.EventSubscriber,
    OutwardingProcessContract.View, PriceChangeListener, TertiaryImgInteractionProvider,
    OutwardProcessInteraction {
    private lateinit var binding: ActivityNewEstimtorBinding
    private lateinit var bindingBottom: BottomNewEstimatorBinding
    private lateinit var bindingBottomOne: BottomSplitInvoiceBinding
    private lateinit var bindingTop: LayoutTopEstimatorBinding
    private lateinit var bindingTopOne: LayoutTopProformaBinding
    private lateinit var bindingTopTwo: LayoutTopSplitInvoiceBinding
    private val TAG = "OutwardingProcessActivi"

    /*   private lateinit var progressBar: ProgressBar
       private lateinit var binding.estimatorRecyclerview: RecyclerView
       private lateinit var  binding.flNewEstimatorBottomContainer: FrameLayout
       private lateinit var binding.estimatorTopContainer: FrameLayout*/
    private lateinit var jobCard: JobCard
    private var totalAmountView: TextView? = null
    private var jobCardId: String? = null
    private var invoiceId: String? = null
    private var displayInvoiceId: String? = null
    private var displayJobCardId: String? = null
    private var isViewOnly: Boolean = false
    private lateinit var type: String
    private var currentSelectedPosition = 0
    internal var isSplitInvoice: Boolean = false
    private var estimatorList = arrayListOf<OutwardStep>()
    private var adapter: OutwardingProcessAdapter? = null
    private var vendorSelectionAdapter: OslLabourVendorSelectionAAdapter? = null
    private lateinit var invoice: Invoice
    private var vehicleType: String? = null
    private var vehicleAmcId: String? = null
    private var jobCardType: String? = null
    private var outwardItem: OutwardItem? = null
    private var vendorList = arrayListOf<Vendor>()
    internal var isEstimateAproveClicked: Boolean = false

    @Inject
    lateinit var presenter: OutwardProcessPresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    @Inject
    lateinit var pdfMediator: PdfMediator

    private val dirtyDetector = DirtyDetector.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (intent.extras != null) {
            getDataFromIntent()
        } else {
            if (BuildConfig.DEBUG) {
                throw IllegalStateException("job card id  cannot be null")
            } else {
                showGenericError("oops,something went wrong!")
            }
        }
        super.onCreate(savedInstanceState)
        bindingBottom = BottomNewEstimatorBinding.inflate(
            layoutInflater,
            binding.flNewEstimatorBottomContainer,
            true
        )
        bindingBottomOne = BottomSplitInvoiceBinding.inflate(
            layoutInflater,
            binding.flNewEstimatorBottomContainer,
            true
        )

        bindingTop =
            LayoutTopEstimatorBinding.inflate(layoutInflater, binding.estimatorTopContainer, true)
        bindingTopOne =
            LayoutTopProformaBinding.inflate(layoutInflater, binding.estimatorTopContainer, true)
        bindingTopTwo = LayoutTopSplitInvoiceBinding.inflate(
            layoutInflater,
            binding.estimatorTopContainer,
            true
        )
        initComponent()
        /* progressBar = findViewById(R.id.pb_main)
         binding.estimatorRecyclerview = findViewById(R.id.estimator_recyclerview)*/
        binding.estimatorRecyclerview.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        //binding.flNewEstimatorBottomContainer = findViewById(R.id.fl_new_estimator_bottom_container)
        // binding.estimatorTopContainer = findViewById(R.id. binding.estimatorTopContainer)
        setUpBottomActions()
        setUpTopActions()


        when (type) {
            SCREEN_TYPE_ESTIMATE -> {
                bindingBottom.llEstimatorViewDetails.setOnClickListener {
                    binding.estimatorRecyclerview.post {
                        adapter?.let {
                            binding.estimatorRecyclerview.smoothScrollToPosition(it.items.size - 1)
                            startSummaryAnimation()
                        }
                    }
                }
                bindingTop.ivMoreCta.setOnClickListener {
                    showMoreDialogSheetForApproval()
                }
                screenTracker.sendScreenEvent(
                    this,
                    ScreenTracker.SCREEN_ESTIMATOR,
                    this.javaClass.simpleName
                )
                presenter.getEstimation(jobCardId!!)
                setSwipeForRecyclerView()
            }

            SCREEN_TYPE_PROFORMA -> {
                screenTracker.sendScreenEvent(
                    this,
                    ScreenTracker.SCREEN_EDIT_PROFORMA,
                    this.javaClass.simpleName
                )
                presenter.getProformaInvoice(invoiceId!!)
                setSwipeForRecyclerView()
            }

            SCREEN_TYPE_SPLIT_INVOICE -> {
                screenTracker.sendScreenEvent(
                    this,
                    ScreenTracker.SCREEN_EDIT_SPLIT_INVOICE,
                    this.javaClass.simpleName
                )
                this.invoice = intent.extras?.getParcelable(ARG_INVOICE)!!
                displaySplitInvoice(this.invoice)
            }
        }
        if (SharedPrefHelper.isOSLWorkOrder()) {
            presenter.getVendorList()
        }
    }

    override fun onStart() {
        super.onStart()
        EventsManager.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
        EventsManager.unregister(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                MoreCtaListDialogFragment.REQUEST_CODE_PART -> {
                    val parts = data.getParcelableArrayListExtra<Part>(Part.TAG)
                    val partToEstimateList = arrayListOf<OutwardItem>()
                    parts?.forEach {
                        partToEstimateList.add(OutwardStepMapper.partToOutwardStep(it))
                    }
                    partToEstimateList.find { it.isApproved == null }?.isApproved = true
                    insertInBulk(OutwardItem.TYPE_PART, partToEstimateList)
                }

                MoreCtaListDialogFragment.REQUEST_CODE_LABOUR -> {
                    val labour = data.getParcelableArrayListExtra<Labour>(Labour.TAG)
                    val labourToEstimateItemList = arrayListOf<OutwardItem>()
                    labour?.forEach {
                        labourToEstimateItemList.add(OutwardStepMapper.labourToOutwardStep(it))
                    }

                    labourToEstimateItemList.find { it.isApproved == null }?.isApproved = true
                    //check for OSL labour
                    if (SharedPrefHelper.isOSLWorkOrder() and labourToEstimateItemList.any {
                            it.labourType.equals(
                                Labour.TYPE_OSL,
                                true
                            )
                        }) {
                        //show vendor selection dialog and then insert in bulk
                        showOslVendorDialog(labourToEstimateItemList, true, -1)
                    } else {
                        insertInBulk(OutwardItem.TYPE_LABOUR, labourToEstimateItemList)
                    }
                }

                REQUEST_CODE_ADD_PART -> {
                    val part = data.getParcelableExtra<Part>(Part.TAG)!!
                    part.isApproved = true
                    updateListItem(OutwardStepMapper.partToOutwardStep(part))
                }

                REQUEST_CODE_ADD_LABOUR -> {
                    val labour = data.getParcelableExtra<Labour>(Labour.TAG)!!
                    labour.isApproved = true
                    updateListItem(OutwardStepMapper.labourToOutwardStep(labour))
                }

                PART_NUMBER_REQUEST_CODE -> {
                    val partNumber =
                        data.getParcelableExtra<PartNumber>(PartNumberSelectionAdapter.ARG_PART_NUMBER)
                    val currentItem: OutwardItem? =
                        adapter?.items?.get(currentSelectedPosition) as? OutwardItem

                    if (currentItem?.partNumber == partNumber?.partNumber && currentItem?.brand?.id == partNumber?.brandId) {
                        return
                    }
                    adapter?.let { nonNullAdapter ->
                        if (nonNullAdapter.items.any {
                                it is OutwardItem && it.type == OutwardItem.TYPE_PART &&
                                        (it.compareEquals(partNumber))
                            }) {
                            toast("Part with same Part Number/Brand already added")
                            return
                        }

                        if ((nonNullAdapter.items[currentSelectedPosition] is OutwardItem) && ((nonNullAdapter.items[currentSelectedPosition] as OutwardItem).type == OutwardItem.TYPE_PART) || ((nonNullAdapter.items[currentSelectedPosition] as OutwardItem).type == OutwardItem.TYPE_SER_PKG_PART)) {
                            val item =
                                (nonNullAdapter.items[currentSelectedPosition] as OutwardItem)
                            item.partNumber = partNumber?.partNumber
                            item.description = partNumber?.description
                            item.brand.name = partNumber?.brandName.toString()
                            item.brand.id = partNumber?.brandId
                            item.price = partNumber?.unitPrice?.toDouble() ?: 0.0
                            item.stock = partNumber?.stock?.toDouble() ?: 0.0

                            showUnitQuantityDialog(item)
                        }

                    }
                }

                REQUEST_CODE_PACKAGES -> {
                    adapter?.let {
                        //here we have to show updated package details (labour and parts)
                        when (type) {
                            SCREEN_TYPE_ESTIMATE -> {
                                presenter.getEstimation(jobCardId!!)
                                setSwipeForRecyclerView()
                            }

                            SCREEN_TYPE_PROFORMA -> {
                                presenter.getProformaInvoice(invoiceId!!)
                                setSwipeForRecyclerView()
                            }
                        }
                    }
                }
            }
            return
        }
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_CHECK_INVOICE -> {
                    setResult(Activity.RESULT_OK)
                    finish()
                }

                REQUEST_CODE_SPLIT_INVOICE -> presenter.getProformaInvoice(invoiceId!!)
                REQUEST_CODE_THIRD_PARTY -> presenter.getProformaInvoice(invoiceId!!)
            }
        }
        if (resultCode == Activity.RESULT_CANCELED && data != null) {
            when (requestCode) {
                REQUEST_CODE_ADD_PART -> {
                    val part = data.getParcelableExtra<Part>(Part.TAG)!!
                    deleteFromList(OutwardStepMapper.partToOutwardStep(part))
                }

                REQUEST_CODE_ADD_LABOUR -> {
                    val labour = data.getParcelableExtra<Labour>(Labour.TAG)!!
                    deleteFromList(OutwardStepMapper.labourToOutwardStep(labour))
                }
            }
        }
    }

    /**
     * @param items
     * @param insert if it is true means insert else update
     * @param position it is to update the labour at position
     */
    private fun showOslVendorDialog(items: List<OutwardItem>, insert: Boolean, position: Int) {
        if (vendorList.isNullOrEmpty()) {
            toast("No Vendor Found")
        } else {
            //separate vendor selection list
            val labourForVendorSelectionList =
                items.filter { it.labourType.equals(Labour.TYPE_OSL, true) }
            //create alert dialog builder
            val alertDialogBuilder = AlertDialog.Builder(this)
            //add custom view with recyclerview
            val customView = View.inflate(this, R.layout.dialog_osl_vendor_selection, null)
            //find recyclerview
            val oslVendorSelectionRV: RecyclerView = customView.find(R.id.oslVendorSelectionRV)

            //create vendor selection Adapter
            vendorSelectionAdapter =
                OslLabourVendorSelectionAAdapter(labourForVendorSelectionList, vendorList)
            oslVendorSelectionRV.adapter = vendorSelectionAdapter

            alertDialogBuilder.setView(customView)
            alertDialogBuilder.setPositiveButton(getString(R.string.label_set)) { _, _ ->
                //validate if vendor is selected for all labours
                //todo
                //get all the labour with vendor selection
                val itemsToInsert = vendorSelectionAdapter?.items
                itemsToInsert?.forEach {
                    val vendorMargin: Double = if (it.vendor != null) {
                        (it.rate * it.vendor!!.vendorMargin) / 100
                    } else {
                        0.0
                    }
                    it.finalRate = it.rate + vendorMargin
//                    if(!insert)
                    it.surcharge = vendorMargin
                }
                //filter items without OSL
                val withoutOslItems =
                    items.filter { it.labourType.equals(Labour.TYPE_OSL, true).not() }
                //prepare list to insert in bulk
                val labourItemList = arrayListOf<OutwardItem>()
                if (itemsToInsert != null) {
                    labourItemList.addAll(itemsToInsert)
                }
                labourItemList.addAll(withoutOslItems)
                //insert in bulk
                if (insert) {
                    insertInBulk(OutwardItem.TYPE_LABOUR, labourItemList)
                } else {
                    updateLabour(position)
                }
            }

            alertDialogBuilder.setNegativeButton(getString(R.string.cancel)) { dialogInterface, _ ->
                val withoutOslItems =
                    items.filter { it.labourType.equals(Labour.TYPE_OSL, true).not() }
                insertInBulk(OutwardItem.TYPE_LABOUR, withoutOslItems)
                dialogInterface.cancel()
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.setTitle("Select OSL Vendor")
            alertDialog.setCancelable(false)
            alertDialog.show()
        }
    }

    @Subscribe
    fun approveEvent(approveEvent: ApproveEvent) {
        when (approveEvent.type) {
            ApproveEvent.ESTIMATE_CUSTOMER_APPROVAL -> {
                saveEstimate(false, true)
            }

            ApproveEvent.ESTIMATE_REFRESH -> {
                presenter.getEstimation(jobCardId!!)
            }
        }
    }

    private fun showUnitQuantityDialog(item: OutwardItem) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        val customView = View.inflate(this, R.layout.dialog_unit_quantity, null)

        val itemQuantityUnitView: Spinner = customView.find(R.id.spinner_quantity_units)
        val quantityEditText: EditText = customView.find(R.id.et_quantity)

        itemQuantityUnitView.adapter = ArrayAdapter(
            itemQuantityUnitView.context,
            R.layout.simple_units_picker_item,
            Constants.BusinessConstants.UNITS_LIST
        )
        (itemQuantityUnitView.adapter as ArrayAdapter<*>).setDropDownViewResource(R.layout.simple_hsn_spinner_item1)
        itemQuantityUnitView.prompt = "Select Units"
        val index: Int? = if (item.units != null) {
            item.units?.indexOf(item.units?.find { it == item.unit })
        } else {
            Constants.BusinessConstants.UNITS_LIST.indexOf(Constants.BusinessConstants.UNITS_LIST.find { it == item.unit })
        }
        itemQuantityUnitView.setSelection(index ?: 0)
        quantityEditText.setText(item.quantity.toString())
        alertDialogBuilder.setView(customView)
        alertDialogBuilder.setPositiveButton(getString(R.string.label_set)) { dialog, which ->
            if (Utility.isValidDecimal(quantityEditText.text.toString())) {
                item.unit = itemQuantityUnitView.selectedItem.toString()
                item.quantity = quantityEditText.text.toString().toFloat()
                adapter?.notifyItemChanged(currentSelectedPosition)
                onPriceUpdate(OutwardItem.TYPE_PART, currentSelectedPosition)
            } else {
                toast("Enter valid quantity")
            }
        }
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel)) { dialogInterface, _ ->
            dialogInterface.cancel()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.setTitle("Set Quantity and Unit")
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun onBackPressed() {
        when (type) {
            SCREEN_TYPE_PROFORMA, SCREEN_TYPE_ESTIMATE -> promptUserIfDirtyOrExit()
            else -> super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            when (type) {
                SCREEN_TYPE_PROFORMA, SCREEN_TYPE_ESTIMATE -> promptUserIfDirtyOrExit()
                else -> finish()
            }
        }
        return true
    }

    override fun showPartSelection(
        jobCardId: String,
        partId: String?,
        partName: String,
        brandId: String?,
        partNumbers: List<PartNumber>
    ) {
        if (partNumbers.isNotEmpty()) {
            startActivityForResult(
                PartNumberSelectorActivity.getIntent(
                    this,
                    jobCardId,
                    partId,
                    partName,
                    partNumbers as ArrayList<PartNumber>,
                    vehicleType,
                    outwardItem?.type == OutwardItem.TYPE_PART
                ), PART_NUMBER_REQUEST_CODE
            )
        } else {
            startActivityForResult(
                SearchPartNumberActivity.getIntent(
                    this,
                    partName,
                    jobCardId,
                    brandId,
                    vehicleType
                ), PART_NUMBER_REQUEST_CODE
            )
        }
    }

    override fun showError(error: String?) {
        toast(error.toString())
    }

    override fun onSaveSuccess(savedObj: Any?, showCustomerApproval: Boolean) {
        when (type) {
            SCREEN_TYPE_ESTIMATE -> {
                startDirtyDetector()
                toast("Estimate Saved")
                if (showCustomerApproval) {
                    if (savedObj is JobCard) {
                        jobCard = savedObj
                    }
                    startActivity(
                        CustomerApprovalActivity.getIntent(
                            this, jobCardId!!, jobCard.jobCardId,
                            isViewOnly
                        )
                    )
                }
            }

            SCREEN_TYPE_PROFORMA -> {
                if (savedObj is Invoice) {
                    invoice = savedObj
                    estimatorList.clear()
                    estimatorList.addAll(OutwardStepMapper.invoiceToOutwardStep(invoice))
                    adapter?.items = estimatorList
                    adapter?.notifyDataSetChanged()
                    startDirtyDetector()
                }

                toast("Proforma Saved")
            }

            SCREEN_TYPE_SPLIT_INVOICE -> {
                setResult(Activity.RESULT_OK)
                toast("Saved")
            }
        }
    }

    override fun moveToNextScreen(preview: Boolean) {
        if (preview) {
            startPdfActivity()
        } else {
            finish()
        }
    }

    override fun displayJobEstimation(obj: JobCard) {
        jobCard = obj
        //iv_more_cta
        if (SharedPrefHelper.getCustomerApproval() and (jobCard.status == JobCard.STATUS_IN_PROGRESS || jobCard.status == JobCard.STATUS_COMPLETED)) {
            bindingTop.ivMoreCta.visibility = View.VISIBLE
        }
        vehicleAmcId = obj.vehicleAmcId
        val estimate = obj.costEstimate
        val packages = estimate.packages
        val vehicle = obj.vehicle
        binding.estimatorTopContainer.visibility = View.VISIBLE
        if (SharedPrefHelper.isInventoryEnabled()) {
            bindingTop.parentPartsAvailability.visibility = View.VISIBLE
        } else {
            bindingTop.parentPartsAvailability.visibility = View.GONE
        }
        estimatorList = OutwardStepMapper.costEstimateToOutwardStep(estimate, packages, false)
        adapter = OutwardingProcessAdapter(
            estimatorList.toMutableList(),
            isViewOnly,
            invoiceId,
            jobCardId!!,
            type,
            null,
            this,
            vehicleType,
            !vehicleAmcId.isNullOrEmpty(),
            obj.type
        )
        bindingTop.tvVehicleDetails.text = StringBuilder().apply {
            append(vehicle.make.name)
            append("-")
            append(vehicle.model.name)
            append("-")
            append(if (vehicle.variant != null) vehicle.variant.name else "NA")
        }

        adapter?.setInteraction(this)
        binding.estimatorRecyclerview.adapter = adapter
        checkIfInStock()
        updateEstimateTotal()
        startDirtyDetector()
        if (!vehicleAmcId.isNullOrEmpty())
            presenter.getVehicleAmcById(vehicleAmcId)
    }

    override fun displayProforma(invoice: Invoice, hsnList: List<HSN>) {
        vehicleAmcId = invoice.jobCard?.vehicleAmcId
        binding.estimatorTopContainer.visibility = View.GONE
        this.invoice = invoice
        jobCard = invoice.jobCard!!
        estimatorList = OutwardStepMapper.invoiceToOutwardStep(invoice)
        invoiceId = invoice.id
        jobCardId = invoice.jobCardId
        val list = hsnList.toMutableList()
        val selectHsn = HSN()
        list.add(0, selectHsn)
        adapter = OutwardingProcessAdapter(
            estimatorList.toMutableList(),
            isViewOnly,
            invoiceId,
            jobCardId
                ?: invoiceId
                ?: "",
            type,
            list,
            this,
            vehicleType,
            !vehicleAmcId.isNullOrEmpty(),
            invoice.jobCard?.type!!
        )

        adapter?.setInteraction(this)
        binding.estimatorRecyclerview.adapter = adapter
        if (isSplitInvoice) {
            binding.estimatorTopContainer.visibility = View.VISIBLE
            val summary = calculateSummary(ARG_IS_SPLIT_INVOICE)
            updateProformaTopSummary(summary)
            bindingTopOne.detailView.setOnClickListener {
                if (checkIfPdfPreviewable()) {
                    adapter?.let { nonNullAdapter ->
                        val invoice2 = OutwardStepMapper.outwardStepToInvoice(
                            nonNullAdapter.items.asSequence()
                                .filter { !nonNullAdapter.isPendingRemoval(it) && (it is OutwardItem || it is OutwardActionItem) }
                                .toList()
                        )
                        invoice2.excessClauseValue = invoice.excessClauseValue
                        invoice2.salvageValue = invoice.salvageValue
                        startActivityForResult(
                            getProformaIntent(
                                this,
                                SCREEN_TYPE_SPLIT_INVOICE,
                                displayJobCardId,
                                displayInvoiceId,
                                jobCardId,
                                invoiceId,
                                invoice.splitInvoice,
                                null,
                                invoice2,
                                invoice.jobCard?.type
                            ), REQUEST_CODE_SPLIT_INVOICE
                        )
                    } ?: run {
                        LoggingFacade.log(KotlinNullPointerException("Split invoice is clicked before adapter is init"))
                    }
                } else {
                    val toast =
                        Toast.makeText(this, R.string.proforma_error_no_tax, Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.TOP, 0, 0)
                    toast.show()
                }
            }
        }
        //check if amc is not null and show the amc package
        if (!vehicleAmcId.isNullOrEmpty())
            presenter.getVehicleAmcById(vehicleAmcId)
        startDirtyDetector()
    }

    override fun displaySplitInvoice(invoice: Invoice) {
        binding.estimatorTopContainer.visibility = View.VISIBLE
        estimatorList = OutwardStepMapper.invoiceToSplitOutwardStep(invoice)
        val isAMCAplied = !vehicleAmcId.isNullOrEmpty()
        adapter = OutwardingProcessAdapter(
            estimatorList.toMutableList(),
            isViewOnly,
            invoiceId,
            jobCardId!!,
            type,
            null,
            this,
            vehicleType,
            isAMCAplied,
            jobCardType!!
        )
        binding.estimatorRecyclerview.adapter = adapter
        val summary = calculateSummary(type)
        updateSplitInvoiceSummary()
        updateSplitInvoiceSummary(summary.taxAmount ?: 0.0, summary.subTotalAmount ?: 0.0)
        bindingTopTwo.quickSplitView.setOnClickListener {
            quickSplitInvoice()
        }
    }

    override fun onThirdPartyRemoveSuccess() {
        presenter.getProformaInvoice(invoiceId!!)
    }

    override fun showAmcDetails(obj: List<AMC>) {
        if (obj.isNotEmpty() && type != SCREEN_TYPE_PROFORMA) {
            bindingTop.tvAmcApplicable.visibility = View.VISIBLE
            val amcCode = " " + obj[0].amcCode + " APPLICABLE"
            bindingTop.tvAmcApplicable.text = amcCode
        } else if (obj.isNotEmpty() && type == SCREEN_TYPE_PROFORMA) {
            binding.amcView.visibility = View.VISIBLE
            val amcCode = " " + obj[0].amcCode + " APPLICABLE"
            binding.amcPackageName.text = amcCode
        }
    }

    override fun displaySearchResults(partNumbers: List<PartNumber>) {
        //show this list in part selector
        if (outwardItem?.type == OutwardItem.TYPE_SER_PKG_PART && partNumbers.isEmpty()) {
            //open  part finder
            startActivityForResult(
                SearchPartNumberActivity.getIntent(
                    this,
                    outwardItem?.text!!,
                    jobCardId,
                    outwardItem?.brand?.id,
                    vehicleType
                ), PART_NUMBER_REQUEST_CODE
            )
        } else {
            startActivityForResult(
                PartNumberSelectorActivity.getIntent(
                    this,
                    jobCardId!!,
                    outwardItem?.id,
                    outwardItem?.text!!,
                    partNumbers as ArrayList<PartNumber>,
                    vehicleType,
                    true
                ), PART_NUMBER_REQUEST_CODE
            )
        }
    }

    override fun onFetchVendors(vendors: List<Vendor>) {
        this.vendorList =
            vendors.filter { it.vendorType == Vendor.TYPE_OSL_SUPPLIER } as ArrayList<Vendor>
    }

    override fun removeThirdParty(invoiceId: String, thirdParty: ThirdParty) {
        presenter.removeThirdParty(invoiceId, thirdParty)
    }

    override fun showPartSelector(position: Int, item: OutwardItem) {
        currentSelectedPosition = position
        this.outwardItem = item
//        presenter.searchInStockPartNumber(item.text!!, jobCardId, item.brand.id, vehicleType, item.id, item.packageId)
        startActivityForResult(
            SearchPartNumberActivity.getIntent(
                this,
                outwardItem?.text!!,
                jobCardId,
                outwardItem?.brand?.id,
                vehicleType
            ), PART_NUMBER_REQUEST_CODE
        )

    }

    override fun onPriceUpdate(itemType: String, position: Int) {
        if (position == -1) return
        /* @itemType will be empty to @NO_ITEM_TYPE parts/labour calculations*/
        when (itemType) {
            OutwardItem.TYPE_PART -> {
                updatePart(position)
            }

            OutwardItem.TYPE_LABOUR -> {
                updateLabour(position)
            }
        }

        val summary = calculateSummary(type)
        adapter?.let { nonNullAdapter ->
            if (isSplitInvoice && type == ARG_IS_SPLIT_INVOICE) {
                updateSplitInvoiceSummary(
                    summary.taxAmount ?: 0.0,
                    summary.subTotalAmount ?: 0.0
                )
            } else {
                val lastIndex =
                    if (type != SCREEN_TYPE_PROFORMA) nonNullAdapter.items.lastIndex else Utility.findIndexFromList(
                        nonNullAdapter.items.findLast { it is OutwardSection && it.title == OutwardStepMapper.TITLE_PROFORMA_SUMMARY },
                        nonNullAdapter.items.toList()
                    ) + 1
                nonNullAdapter.items[lastIndex] = summary
                nonNullAdapter.notifyItemChanged(lastIndex, null)
            }
        }

        if (type != SCREEN_TYPE_SPLIT_INVOICE) {
            calculateSections()
        }
        if (type == SCREEN_TYPE_ESTIMATE) {
            updateEstimateTotal()
            checkIfInStock()
        }
        if (type == SCREEN_TYPE_PROFORMA && isSplitInvoice) {
            updateProformaTopSummary(calculateSummary(SCREEN_TYPE_SPLIT_INVOICE)) // to get split invoice summary
        }
    }

    override fun onVendorSelectionClick(itemType: String, position: Int) {
        if (position == -1) return
        /* @itemType will be empty to @NO_ITEM_TYPE parts/labour calculations*/
        when (itemType) {
            OutwardItem.TYPE_LABOUR -> {
                adapter?.items?.getOrNull(position)?.let {
                    if (it !is OutwardItem)
                        return
                    it
                    val itemList = arrayListOf<OutwardItem>()
                    itemList.add(it)
                    showOslVendorDialog(itemList, false, position)
                }
            }
        }
    }

    override fun showProgressIndicator() {
        showProgressBar()
    }

    override fun dismissProgressIndicator() {
        dismissProgressBar()
    }

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg)
    }

    override fun showPrintEstimate() {
        pdfMediator.startJobCardEstimatePdf(this, jobCardId!!, displayJobCardId!!)
    }

    override fun onActionBtnClick() {
        when (type) {
            // SCREEN_TYPE_SPLIT_INVOICE -> 0
            SCREEN_TYPE_PROFORMA -> startActivity(
                ViewJCActivity.getViewJcIntent(
                    this,
                    jobCardId!!,
                    displayJobCardId!!,
                    true,
                    false,
                    vehicleType
                )
            )
            // SCREEN_TYPE_ESTIMATE -> saveEstimate(true)
            else -> IllegalStateException("class does not support type $type")
        }
    }

    override fun onSecondaryActionBtnClick() {
        when (type) {
            SCREEN_TYPE_SPLIT_INVOICE -> saveSplitInvoice()
            SCREEN_TYPE_PROFORMA -> {
                // start split invoice
//                adapter?.let { nonNullAdapter ->
//                    val invoice = EstimateMapper.estimatorItemToInvoice(nonNullAdapter.items.filter { it is OutwardItem && !nonNullAdapter.isPendingRemoval(it) }.map { it as OutwardItem })
//                    startActivityForResult(OutwardingProcessActivity.getStartIntent(this, displayJobCardId, displayInvoiceId, jobCardId, invoiceId, false, invoice.splitInvoice, OutwardingProcessActivity.SCREEN_TYPE_SPLIT_INVOICE, invoice), REQUEST_CODE_SPLIT_INVOICE)
//                } ?: kotlin.run {
//                    LoggingFacade.log(KotlinNullPointerException("Split invoice is clicked before adapter is init"))
//                }
            }

            SCREEN_TYPE_ESTIMATE -> {
                if (isViewOnly) {
                    showPrintEstimate()
                } else {
                    saveEstimate(true, false)
                }
            }
        }
    }

    override fun onTertiaryActionBtnClick() {
        when (type) {
            SCREEN_TYPE_SPLIT_INVOICE -> saveSplitInvoice()
            SCREEN_TYPE_PROFORMA -> saveProforma(false)
            SCREEN_TYPE_ESTIMATE -> saveEstimate(false, false)
        }
    }

    override fun getActionBarImage(): Int {
        if (isViewOnly) return 0
        return when (type) {
            // SCREEN_TYPE_SPLIT_INVOICE -> 0
            SCREEN_TYPE_PROFORMA -> R.drawable.save_proforma
            // SCREEN_TYPE_ESTIMATE -> R.drawable.ic_print_white_24dp
            else -> 0
        }
    }

    override fun getSecondaryActionBarImage(): Int {
//        if (isViewOnly) return 0
        return when (type) {
            // SCREEN_TYPE_SPLIT_INVOICE -> R.drawable.ic_save_white_24dp
            // SCREEN_TYPE_PROFORMA -> if (isSplitInvoice) R.drawable.ic_split_invoice else 0
            SCREEN_TYPE_ESTIMATE -> R.drawable.ic_print_white_24dp
            else -> 0
        }
    }

    override fun getTertiaryActionBarImage(): Int {
        if (isViewOnly)
            return 0
        return when (type) {
            SCREEN_TYPE_PROFORMA -> R.drawable.ic_save_white_24dp
            SCREEN_TYPE_SPLIT_INVOICE -> R.drawable.ic_save_white_24dp
            SCREEN_TYPE_ESTIMATE -> R.drawable.ic_save_white_24dp
            else -> 0
        }
    }

    override fun getToolBarTitle(): String {
        return when (type) {
            SCREEN_TYPE_SPLIT_INVOICE -> "Split Invoice"
            SCREEN_TYPE_PROFORMA -> "$displayInvoiceId"
            SCREEN_TYPE_ESTIMATE -> "Estimate $displayJobCardId"
            else -> ""
        }
    }

    override fun getNavigationImage(): Int {
        return R.drawable.ic_clear_white_24dp
    }

    override fun createScreenContainer(): ScreenContainer = ActionImgScreenContainer(this)
    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityNewEstimtorBinding.inflate(layoutInflater)
        return binding
    }

    override fun getProgressView(): View = ProgressBar(this)

    fun getPartNumbers(position: Int) {
        this.currentSelectedPosition = position
        adapter?.let { nonNullAdapter ->
            if (nonNullAdapter.items[position] is OutwardItem) {
                val item = nonNullAdapter.items[position] as OutwardItem
                outwardItem = item

                if (checkIfNetworkAvailable() && item.type == OutwardItem.TYPE_PART || checkIfNetworkAvailable() && item.type == OutwardItem.TYPE_SER_PKG_PART) {
                    presenter.getPartNumber(
                        jobCardId!!,
                        item.id,
                        item.text!!,
                        item.brand.id,
                        true,
                        vehicleType,
                        item.packageId
                    )
                }
            }
        } ?: run {
            LoggingFacade.log(NullPointerException("getpart numbers called on null adapter"))
        }
    }

    private fun updateSplitInvoiceSummary() {
        var total = 0.0
        adapter?.items?.asSequence()?.filter { it is OutwardSection }
            ?.map { it as OutwardSection }?.toList()?.forEach {
                total += it.total ?: 0.0
            }
        bindingBottomOne.splitInvoiceSummaryTotalView.text =
            Utility.convertToCurrency(total).toString()
    }

    private fun getDataFromIntent() {
//        vehicleAmcId = intent.extras!!.getString(ARG_VEHICLE_AMC_ID, "")
        jobCardId = intent.extras!!.getString(ARG_JOB_CARD_ID)
        invoiceId = intent.extras!!.getString(ARG_INVOICE_ID)
        isSplitInvoice = intent.extras!!.getBoolean(ARG_IS_SPLIT_INVOICE)
        isViewOnly = intent.extras!!.getBoolean(ARG_IS_VIEW_ONLY)
        displayInvoiceId = intent.extras!!.getString(ARG_DISPLAY_INVOICE_ID)
        displayJobCardId = intent.extras!!.getString(ARG_DISPLAY_JOBCARD_ID)
        vehicleType = intent.extras!!.getString(ARG_VEHICLE_TYPE)
//        type = intent.extras!!.getString(SCREEN_TYPE)
        type = intent.getStringExtra(SCREEN_TYPE).toString()
        jobCardType = intent.getStringExtra(ARG_JOB_CARD_TYPE)
        Log.d(TAG, "getDataFromIntent: " + type)
    }

    private fun initComponent() {
        (application as DearOApplication)
            .repositoryComponent
            .COMPONENT(OutwardingProcessPresenterModule(this))
            .inject(this)
    }

    private fun saveProforma(isPreview: Boolean) {
        adapter?.let { nonNullAdapter ->
            val invoiceCopy = OutwardStepMapper.outwardStepToInvoice(
                nonNullAdapter.items.asSequence()
                    .filter { !nonNullAdapter.isPendingRemoval(it) && (it is OutwardItem || it is OutwardActionItem) }
                    .toList()
            )
            invoiceCopy.excessClauseValue = invoice.excessClauseValue
            invoiceCopy.salvageValue = invoice.salvageValue
            val err = validatePrices()
            if (err.isEmpty()) {
                presenter.updateProformaInvoice(invoiceId!!, invoiceCopy, isPreview)
            } else {
                toast(err)
            }
        } ?: run {
            LoggingFacade.log(NullPointerException("saveProforma isPreview $isPreview adapter init"))
        }
    }

    private fun saveEstimate(showPdf: Boolean, showCustomerApproval: Boolean) {
        adapter?.let { nonNullAdapter ->
            val msg = isValidEstimateData(
                nonNullAdapter.items.asSequence()
                    .filter { it is OutwardItem && !nonNullAdapter.isPendingRemoval(it) }
                    .map { it as OutwardItem }.toList()
            )
            if (msg.isEmpty()) {
                val costEstimate = OutwardStepMapper.outwardStepToCostEstimate(
                    nonNullAdapter.items.asSequence()
                        .filter { it is OutwardItem && !nonNullAdapter.isPendingRemoval(it) }
                        .map { it as OutwardItem }.toList()
                )
                presenter.saveEstimation(
                    jobCardId!!,
                    costEstimate,
                    showPdf,
                    showCustomerApproval
                )
            } else {
                showGenericError(msg)
            }
        } ?: run {
            LoggingFacade.log(NullPointerException("Save estimate is clicked before adapter init"))
        }
    }

    private fun isValidEstimateData(list: List<OutwardItem>): String {
        val labours = list.filter { it.type == OutwardItem.TYPE_LABOUR }
        var msg: String = ""
        labours.forEach { item ->
            if (item.discount.mode == Discount.MODE_PERCENTAGE && item.discount.amount > 100) {
                msg = getString(R.string.discount_gt_rate)
            }
            /* else {
                 if ((adapter?.jcType != JobCard.TYPE_ACCIDENTAL) && item.finalRate < item.rate) {
                     msg = "Final rate should be greater than or equal to Rate"
                 } else if (item.rate > 0 && item.finalRate < 1.0)//we are here means it is accidental JC
                 {
                     msg = "Final Rate must be greater than or equal to Rate"
                 }
             }*/
        }

        return msg
    }

    private fun saveSplitInvoice() {
        adapter?.let { nonNullAdapter ->
            val newInvoice = OutwardStepMapper.outwardStepToInvoice(
                nonNullAdapter.items.asSequence()
                    .filter { it is OutwardItem || it is OutwardActionItem }.toList()
            )
            newInvoice.packageIds = invoice.packageIds
            presenter.updateProformaInvoice(invoiceId!!, newInvoice, false)
        } ?: run {
            LoggingFacade.log(NullPointerException("save split invoice called on null adapter"))
        }
    }

    private fun deleteFromList(estimationItem: OutwardItem) {
        when (estimationItem.type) {
            OutwardItem.TYPE_LABOUR -> {
                val index =
                    adapter?.items?.indexOf(adapter?.items?.find { it is OutwardItem && (it.id == estimationItem.id || it.uid == estimationItem.uid || it.text == estimationItem.text) })
                adapter?.items?.removeAt(index!!)
                adapter?.notifyItemRemoved(index!!)
                onPriceUpdate(NO_ITEM_TYPE, index ?: -1)
            }

            OutwardItem.TYPE_PART -> {
                val index =
                    adapter?.items?.indexOf(adapter?.items?.find { it is OutwardItem && (it.id == estimationItem.id || it.uid == estimationItem.uid || it.text == estimationItem.text) })
                adapter?.items?.removeAt(index!!)
                adapter?.notifyItemRemoved(index!!)
                onPriceUpdate(NO_ITEM_TYPE, index!!)
            }

            OutwardItem.TYPE_SERVICE_PKG -> {
                val index =
                    adapter?.items?.indexOf(adapter?.items?.find { it is OutwardItem && (it.id == estimationItem.id || it.uid == estimationItem.uid || it.text == estimationItem.text) })
                //now get the swiped item
                val packageItem = adapter?.items?.get(index!!) as OutwardItem
                //now find and delete parts and labour from list using package ID
                adapter?.items?.removeAll { it is OutwardItem && it.packageId == packageItem.id }
                adapter?.items?.removeAt(index!!)
                adapter?.notifyDataSetChanged()
                onPriceUpdate(NO_ITEM_TYPE, index ?: -1)
            }
        }
    }

    private fun deleteInBulk(type: String, list: List<OutwardItem>) {
        when (type) {
            OutwardItem.TYPE_SERVICE_PKG -> {
                adapter?.let { nonNullAdapter ->
                    val packageSection: OutwardSection = nonNullAdapter.items.find {
                        it is OutwardSection && it.title.contains(
                            OutwardStepMapper.TITLE_PACKAGES,
                            true
                        )
                    } as OutwardSection
                    val sectionIndex = nonNullAdapter.items.indexOf(packageSection)
                    nonNullAdapter.items.removeAll(list)
                    nonNullAdapter.notifyItemRangeRemoved(sectionIndex + 1, list.size)
                    packageSection.title =
                        "${OutwardStepMapper.TITLE_PACKAGES}(${nonNullAdapter.items.filter { it is OutwardItem && it.type == OutwardItem.TYPE_SERVICE_PKG }.size})"
                    nonNullAdapter.notifyItemChanged(sectionIndex, null)
                    onPriceUpdate(NO_ITEM_TYPE, sectionIndex + 1)
                    binding.estimatorRecyclerview.post {
                        binding.estimatorRecyclerview.smoothScrollToPosition(sectionIndex + 1)
                    }
                }
            }
        }
    }

    private fun insertToList(estimationItem: OutwardItem) {
        when (estimationItem.type) {
            OutwardItem.TYPE_LABOUR -> {
                adapter?.let { nonNullAdapter ->
                    if (nonNullAdapter.items.find { it is OutwardItem && (it.text == estimationItem.text || (it.uid != null && it.uid == estimationItem.uid)) } == null) {
                        val labourSection = nonNullAdapter.items.find {
                            it is OutwardSection && it.title.contains(
                                OutwardStepMapper.TITLE_LABOURS,
                                true
                            )
                        } as OutwardSection
                        val index = nonNullAdapter.items.indexOf(labourSection)
                        nonNullAdapter.items.add(index + 1, estimationItem)
                        nonNullAdapter.notifyItemInserted(index + 1)
                        labourSection.title =
                            "${OutwardStepMapper.TITLE_LABOURS}(${nonNullAdapter.items.filter { it is OutwardItem && it.type == OutwardItem.TYPE_LABOUR }.size})"
                        nonNullAdapter.notifyItemChanged(index, null)
                        onPriceUpdate(estimationItem.type, index + 1)
                        binding.estimatorRecyclerview.post {
                            binding.estimatorRecyclerview.smoothScrollToPosition(index + 1)
                        }
                    } else {
                        toast("${estimationItem.text} already added.")
                    }
                }
            }

            OutwardItem.TYPE_PART -> {
                adapter?.let { nonNullAdapter ->
                    if (nonNullAdapter.items.any {
                            it is OutwardItem && it.type == OutwardItem.TYPE_PART &&
                                    it == estimationItem
                        }) {
                        toast("Part with same Part Number/Brand already added")
                        return
                    }
                    val partSection = nonNullAdapter.items.find {
                        it is OutwardSection && it.title.contains(
                            OutwardStepMapper.TITLE_PARTS,
                            true
                        )
                    }?.let { it as OutwardSection }
                    val index = nonNullAdapter.items.indexOf(partSection!!)
                    nonNullAdapter.items.add(index + 1, estimationItem)
                    nonNullAdapter.notifyItemInserted(index + 1)
                    partSection.title =
                        "${OutwardStepMapper.TITLE_PARTS}(${nonNullAdapter.items.filter { it is OutwardItem && it.type == OutwardItem.TYPE_PART }.size})"
                    nonNullAdapter.notifyItemChanged(index, null)
                    onPriceUpdate(estimationItem.type, index + 1)
                    binding.estimatorRecyclerview.post {
                        binding.estimatorRecyclerview.smoothScrollToPosition(index + 1)
                    }
                }
            }

            OutwardItem.TYPE_SERVICE_PKG -> {
                adapter?.let { nonNullAdapter ->
                    val packageSection: OutwardSection = nonNullAdapter.items.find {
                        it is OutwardSection && it.title.contains(
                            OutwardStepMapper.TITLE_PACKAGES,
                            true
                        )
                    } as OutwardSection
                    val index = nonNullAdapter.items.indexOf(packageSection)
                    nonNullAdapter.items.add(index + 1, estimationItem)
                    nonNullAdapter.notifyItemInserted(index + 1)
                    packageSection.title =
                        "${OutwardStepMapper.TITLE_PACKAGES}(${nonNullAdapter.items.filter { it is OutwardItem && it.type == OutwardItem.TYPE_SERVICE_PKG }.size})"
                    nonNullAdapter.notifyItemChanged(index, null)
                    onPriceUpdate(estimationItem.type, index + 1)
                    binding.estimatorRecyclerview.post {
                        binding.estimatorRecyclerview.smoothScrollToPosition(index + 1)
                    }
                }
            }
        }
    }

    private fun insertInBulk(type: String, insertedItems: List<OutwardItem>) {
        when (type) {
            OutwardItem.TYPE_SERVICE_PKG -> {
                adapter?.let { nonNullAdapter ->
                    val packageSection: OutwardSection = nonNullAdapter.items.find {
                        it is OutwardSection && it.title.contains(
                            OutwardStepMapper.TITLE_PACKAGES,
                            true
                        )
                    } as OutwardSection
                    val sectionIndex = nonNullAdapter.items.indexOf(packageSection)
                    insertedItems.forEach {
                        nonNullAdapter.items.add(sectionIndex + 1, it)
                    }
                    nonNullAdapter.notifyItemRangeInserted(sectionIndex + 1, insertedItems.size)
                    packageSection.title =
                        "${OutwardStepMapper.TITLE_PACKAGES}(${nonNullAdapter.items.filter { it is OutwardItem && it.type == OutwardItem.TYPE_SERVICE_PKG }.size})"
                    nonNullAdapter.notifyItemChanged(sectionIndex, null)
                    onPriceUpdate(type, sectionIndex + 1)
                    binding.estimatorRecyclerview.post {
                        binding.estimatorRecyclerview.smoothScrollToPosition(sectionIndex + 1)
                    }
                }
            }

            OutwardItem.TYPE_PART -> {
                adapter?.let { nonNullAdapter ->
                    val partSection: OutwardSection = nonNullAdapter.items.find {
                        it is OutwardSection && it.title.contains(
                            OutwardStepMapper.TITLE_PARTS,
                            true
                        )
                    } as OutwardSection
                    val sectionIndex = nonNullAdapter.items.indexOf(partSection)
                    insertedItems.forEach {
                        nonNullAdapter.items.add(sectionIndex + 1, it)
                    }
                    nonNullAdapter.notifyItemRangeInserted(sectionIndex + 1, insertedItems.size)
                    partSection.title =
                        "${OutwardStepMapper.TITLE_PARTS}(${nonNullAdapter.items.filter { it is OutwardItem && it.type == OutwardItem.TYPE_PART }.size})"
                    nonNullAdapter.notifyItemChanged(sectionIndex, null)
                    onPriceUpdate(NO_ITEM_TYPE, sectionIndex + 1)
                    binding.estimatorRecyclerview.post {
                        binding.estimatorRecyclerview.smoothScrollToPosition(sectionIndex)
                    }
                }
            }

            OutwardItem.TYPE_LABOUR -> {
                adapter?.let { nonNullAdapter ->
                    val labourSection: OutwardSection = nonNullAdapter.items.find {
                        it is OutwardSection && it.title.contains(
                            OutwardStepMapper.TITLE_LABOURS,
                            true
                        )
                    } as OutwardSection
                    val sectionIndex = nonNullAdapter.items.indexOf(labourSection)
                    var itemAddedCount = 0

                    insertedItems.forEach { insertedItem ->
                        nonNullAdapter.items.add(sectionIndex + 1, insertedItem)
                        itemAddedCount++
                    }

                    // following method is used to update amount of all recently added labours, since it is not available from server which is not the case with parts.
                    insertLabours(sectionIndex + 1, itemAddedCount)
                    labourSection.title =
                        "${OutwardStepMapper.TITLE_LABOURS}(${nonNullAdapter.items.filter { it is OutwardItem && it.type == OutwardItem.TYPE_LABOUR }.size})"
                    nonNullAdapter.notifyItemChanged(sectionIndex, null)
                    onPriceUpdate(NO_ITEM_TYPE, sectionIndex + 1)
                    binding.estimatorRecyclerview.post {
                        binding.estimatorRecyclerview.smoothScrollToPosition(sectionIndex)
                    }
                }
            }
        }
    }

    private fun updateListItem(estimationItem: OutwardItem) {
        when (estimationItem.type) {
            OutwardItem.TYPE_LABOUR -> {
                val index =
                    adapter?.items?.indexOf(adapter?.items?.find { it is OutwardItem && (it.uid == estimationItem.uid || it.text == estimationItem.text) })
                adapter?.items?.set(index!!, estimationItem)
                adapter?.notifyItemChanged(index!!, null)
                onPriceUpdate(estimationItem.type, index ?: -1)
            }

            OutwardItem.TYPE_PART -> {
                val index = adapter?.items?.indexOf(adapter?.items?.find {
                    it is OutwardItem &&
                            (it.uid == estimationItem.uid ||
                                    (it.text == estimationItem.text))
                })
                adapter?.items?.set(index!!, estimationItem)
                adapter?.notifyItemChanged(index!!, null)
                onPriceUpdate(estimationItem.type, index ?: -1)
            }
        }
    }

    private fun checkIfInStock() {
        adapter?.let { nonNullAdapter ->
            val filteredList = nonNullAdapter.items.asSequence().filter {
                it is OutwardItem && ((it.type == OutwardItem.TYPE_PART) or (it.type == OutwardItem.TYPE_SER_PKG_PART))
            }.map { it as OutwardItem }.toList()

            var inStock = true

            if (filteredList.isNotEmpty() &&
                (filteredList.find { it.type == OutwardItem.TYPE_PART && it.type == OutwardItem.TYPE_SER_PKG_PART && it.partNumber == null } == null)
            ) {

                filteredList.forEach {
                    inStock = inStock && it.stock > 0
                }

                /*     filteredList.filter { it.type == OutwardItem.TYPE_SER_PKG_PART }.forEach { servicePkgPart ->
                servicePkgPart.parts.forEach {
                    inStock = inStock && it.stock > 0
                }
            }*/

                if (inStock) {
                    bindingTop.tvInStock.text = getString(R.string.estimator_label_part_in_stock)
                    bindingTop.tvInStock.setTextColor(
                        ContextCompat.getColor(
                            bindingTop.tvInStock.context,
                            R.color.forest_green
                        )
                    )
                } else {
                    bindingTop.tvInStock.text = getString(R.string.estimator_label_part_out_stock)
                    bindingTop.tvInStock.setTextColor(
                        ContextCompat.getColor(
                            bindingTop.tvInStock.context,
                            R.color.persion_red
                        )
                    )
                }
            } else {
                bindingTop.tvInStock.text = getString(R.string.NA)
                bindingTop.tvInStock.setTextColor(
                    ContextCompat.getColor(
                        bindingTop.tvInStock.context,
                        R.color.black
                    )
                )
            }

            /*   if (filteredList.none { it.type == OutwardItem.TYPE_PART })

        {
            var isServicePackagePartsEmpty = true
            filteredList.filter { it.type == OutwardItem.TYPE_SERVICE_PKG }.forEach { servicePkg ->
                if (servicePkg.parts.isNotEmpty())
                    isServicePackagePartsEmpty = false
            }

            if (isServicePackagePartsEmpty)
            {
                 bindingTop.tvInStock.text = getString(R.string.NA)
                 bindingTop.tvInStock.setTextColor(ContextCompat.getColor( bindingTop.tvInStock.context, R.color.black))
            }
        }*/
        }
    }

    private fun updateEstimateTotal() {
        var total = 0.0
        adapter?.items?.filter { it is OutwardSection }?.map { it as OutwardSection }?.forEach {
            total += it.total ?: 0.0
        }
        totalAmountView?.text = Utility.convertToCurrency(total).toString()
    }

    private fun setUpBottomActions() {
        when (type) {
            SCREEN_TYPE_ESTIMATE -> {
                LayoutInflater.from(this)
                    .inflate(R.layout.bottom_new_estimator, binding.flNewEstimatorBottomContainer)
                val addItemView = findViewById<TextView>(R.id.tv_add_item)
                if (!isViewOnly) {
                    addItemView.setOnClickListener {
                        showMoreDialogSheet()
                    }
                } else {
                    addItemView.setBackgroundColor(
                        ContextCompat.getColor(
                            this,
                            R.color.light_grey
                        )
                    )
                }
                totalAmountView = findViewById(R.id.tv_part_finder_total_amount)
            }

            SCREEN_TYPE_PROFORMA -> {
                LayoutInflater.from(this)
                    .inflate(
                        R.layout.bottom_new_proforma,
                        binding.flNewEstimatorBottomContainer,
                        true
                    )

                (find<TextView>(R.id.tv_add_item)).setOnClickListener {
                    showMoreDialogSheet()
                }
                find<TextView>(R.id.tv_preview).setOnClickListener {
                    saveProforma(true)
                }
                if (SharedPrefHelper.getApproval()) {
                    (find<TextView>(R.id.tv_add_item)).visibility = View.GONE
                    (find<View>(R.id.spaceView)).visibility = View.GONE
                }
            }

            ARG_IS_SPLIT_INVOICE -> {
                LayoutInflater.from(this)
                    .inflate(R.layout.bottom_split_invoice, binding.flNewEstimatorBottomContainer)
//                val addItemView = find<TextView>(R.id.tv_add_item)
//                addItemView.visibility = View.INVISIBLE
//                totalAmountView = find(R.id.tv_part_finder_total_amount)
            }
        }
    }

    private fun setUpTopActions() {
        when (type) {
            SCREEN_TYPE_ESTIMATE -> {
                LayoutInflater.from(this)
                    .inflate(R.layout.layout_top_estimator, binding.estimatorTopContainer)
            }

            SCREEN_TYPE_PROFORMA -> {
                if (isSplitInvoice) {
                    LayoutInflater.from(this)
                        .inflate(R.layout.layout_top_proforma, binding.estimatorTopContainer, true)
                    (find<TextView>(R.id.tv_add_item)).setOnClickListener {
                        showMoreDialogSheet()
                    }
                    find<TextView>(R.id.tv_preview).setOnClickListener {
                        saveProforma(true)
                    }

                    //Add customer discount

                }
            }

            ARG_IS_SPLIT_INVOICE -> {
                LayoutInflater.from(this)
                    .inflate(R.layout.layout_top_split_invoice, binding.estimatorTopContainer)
            }
        }
    }

    private fun startPdfActivity() {
        if (adapter?.items?.none { it is OutwardItem } == true) {
            toast("No Labour or Parts added")
            return
        }

        if (checkIfPdfPreviewable()) {
            pdfMediator.startProformaPdfWithResult(
                this,
                invoice,
                jobCardId!!,
                Source.PROFORMA,
                REQUEST_CODE_CHECK_INVOICE
            )
        } else {
            toast(R.string.proforma_error_no_tax)
        }
    }

    private fun checkIfPdfPreviewable(): Boolean {
        adapter?.let { nonNullAdapter ->
            return !SharedPrefHelper.isGstEnabled() || nonNullAdapter.items.find {
                it is OutwardItem && it.type == OutwardItem.TYPE_PART && (it.tax.hsn == null || it.tax.hsn.equals(
                    HSN.SELECT
                ))
            } == null &&
                    nonNullAdapter.items.find { it is OutwardItem && it.type == OutwardItem.TYPE_LABOUR && it.tax.sac == null } == null
        } ?: run {
            return false
        }
    }

    private fun showMoreDialogSheetForApproval() {
        val bundle = Bundle()
        bundle.putString(MoreCtaListDialogFragment.ARG_INVOICE_ID, invoiceId)
        bundle.putString(MoreCtaListDialogFragment.ARG_JOB_CARD_ID, jobCardId)
        bundle.putString(MoreCtaListDialogFragment.ARG_VEHICLE_AMC_ID, vehicleAmcId)
        bundle.putString(MoreCtaListDialogFragment.ARG_JOB_CARD_ID, jobCard.id)
        bundle.putString(MoreCtaListDialogFragment.ARG_DISPLAY_ID, jobCard.jobCardId)
        bundle.putString(MoreCtaListDialogFragment.ARG_DISPLAY_ID, jobCard.jobCardId)
        bundle.putByte(ARG_IS_VIEW_ONLY, if (isViewOnly) 1 else 0)

        val modalBottom = MoreCtaListDialogFragment.newInstance(
            MoreCtaListDialogFragment.ARG_CUSTOMER_APPROVAL,
            bundle,
            vehicleType
        )
        val fragmentManager = supportFragmentManager
        modalBottom.show(fragmentManager, "some")
    }

    private fun showMoreDialogSheet() {
        val bundle = Bundle()
        bundle.putString(MoreCtaListDialogFragment.ARG_INVOICE_ID, invoiceId)
        bundle.putString(MoreCtaListDialogFragment.ARG_JOB_CARD_ID, jobCardId)
        bundle.putString(MoreCtaListDialogFragment.ARG_VEHICLE_AMC_ID, vehicleAmcId)

        adapter?.let { nonNullAdapter ->
            if (SharedPrefHelper.isPackagesEnabled()) {
                val existingIds = nonNullAdapter.items.asSequence().filter { it is OutwardItem }
                    .filter {
                        (it as OutwardItem).type == OutwardItem.TYPE_SERVICE_PKG && nonNullAdapter.isPendingRemoval(
                            it
                        ).not()
                    }
                    .map { (it as OutwardItem).id }.toList()
                bundle.putStringArrayList(
                    MoreCtaListDialogFragment.ARG_EXISTING_ID,
                    ArrayList(existingIds)
                )
                if (type == SCREEN_TYPE_PROFORMA)
                    bundle.putBoolean(MoreCtaListDialogFragment.ARG_IS_FROM_PROFROMA, true)
                else
                    bundle.putBoolean(MoreCtaListDialogFragment.ARG_IS_FROM_PROFROMA, false)
            }
        }
        val modalBottom = MoreCtaListDialogFragment.newInstance("", bundle, vehicleType)
        val fragmentManager = supportFragmentManager
        modalBottom.show(fragmentManager, "some")
    }

    private fun setSwipeForRecyclerView() {

        val swipeHelper = object : SwipeCallback(0, ItemTouchHelper.LEFT, this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val swipedPosition = viewHolder.adapterPosition
                val adapter = binding.estimatorRecyclerview.adapter as OutwardingProcessAdapter
                adapter.pendingRemoval(swipedPosition)
            }

            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val position = viewHolder.adapterPosition
                val adapter = binding.estimatorRecyclerview.adapter as OutwardingProcessAdapter
                return if (isViewOnly || !(viewHolder is OutwardingProcessAdapter.LabourViewHolder || viewHolder is OutwardingProcessAdapter.PartViewHolder || viewHolder is OutwardingProcessAdapter.PackageViewHolder) || adapter.isPendingRemoval(
                        position
                    )
                ) {
                    0
                } else super.getSwipeDirs(recyclerView, viewHolder)
            }
        }

        val mItemTouchHelper = ItemTouchHelper(swipeHelper)
        mItemTouchHelper.attachToRecyclerView(binding.estimatorRecyclerview)

        // set swipe label
        swipeHelper.leftSwipeLabel = "Delete"
        // set swipe background-Color
        swipeHelper.leftColorCode = ContextCompat.getColor(this, R.color.red)
    }

    private fun calculateSummary(type: String): OutwardSummary {
        when (type) {
            SCREEN_TYPE_ESTIMATE -> {
                var partsTotal = 0.0
                var labourTotal = 0.0
                var servicePkgTotal = 0.0
                adapter?.items?.filter { it is OutwardItem && it.type == OutwardItem.TYPE_PART }
                    ?.map { it as OutwardItem }?.forEach {
                        partsTotal += it.amount!!
                    }

                adapter?.items?.filter { it is OutwardItem && it.type == OutwardItem.TYPE_LABOUR }
                    ?.map { it as OutwardItem }?.forEach {
                        labourTotal += it.amount!!
                    }
                adapter?.items?.filter { it is OutwardItem && it.type == OutwardItem.TYPE_SERVICE_PKG }
                    ?.map { it as OutwardItem }?.forEach {
                        it.rates?.map { rate -> servicePkgTotal += rate.offerPrice.amount }
                    }
                //commenting code due to duplication
//                adapter?.items?.filter { it is OutwardItem && it.type == OutwardItem.TYPE_LABOUR }?.map { it as OutwardItem }?.forEach { labourTotal += (it.amount!! + it.surcharge) * it.quantity }
                adapter?.items?.filter { it is OutwardItem && it.type == OutwardItem.TYPE_SERVICE_PKG }
                    ?.map { it as OutwardItem }?.forEach {
                        servicePkgTotal += it.amount ?: 0.0
                    }
                return OutwardSummary(
                    OutwardStepMapper.TITLE_ESTIMATOR_SUMMARY,
                    partsTotal,
                    labourTotal,
                    servicePkgTotal,
                    partsTotal + labourTotal + servicePkgTotal
                )
            }

            SCREEN_TYPE_PROFORMA -> {
                var totalTax = 0.0
                var totalDiscount = 0.0
                var subTotal = 0.0
                var totalAmount = 0.0
                adapter?.items?.filter { it is OutwardItem && it.type != OutwardItem.TYPE_SERVICE_PKG }
                    ?.map { it as OutwardItem }?.forEach {
                        val tax =
                            if (SharedPrefHelper.isGstEnabled()) (it.tax.cgst + it.tax.sgst).toFloat() / 100 else 0.0f
                        val rate =
                            if (it.type == OutwardItem.TYPE_PART) it.price / (1 + tax) else (it.finalRate)
                        val itemSubTotal = rate * it.quantity
                        val itemDiscountAmount =
                            if (it.discount.amount == 0.0) 0.0 else (if (it.discount.mode == Discount.MODE_PRICE) it.discount.amount else (itemSubTotal * (it.discount.amount / 100)))
                        val itemAmountAfterDiscount = itemSubTotal - itemDiscountAmount
                        val itemTaxAmount =
                            if (SharedPrefHelper.isGstEnabled() && tax > 0.0f) (itemAmountAfterDiscount * tax) else 0.0
                        subTotal += itemSubTotal
                        totalDiscount += itemDiscountAmount
                        totalTax += itemTaxAmount
                        totalAmount += (itemAmountAfterDiscount + itemTaxAmount)
                    }
                adapter?.items?.filter { it is OutwardItem && it.type == OutwardItem.TYPE_SERVICE_PKG }
                    ?.map { it as OutwardItem }?.forEach {
                        totalAmount += it.amount ?: 0.0
                        subTotal += it.pkgTaxableAmount
                        totalTax += it.tax.cgstAmount ?: 0.0.plus(it.tax.sgstAmount ?: 0.0)
                    }
                return OutwardSummary(
                    OutwardStepMapper.TITLE_PROFORMA_SUMMARY,
                    subTotal,
                    totalTax,
                    totalDiscount,
                    totalAmount
                )
            }

            else -> {
                if (!checkIfPdfPreviewable()) {
                    return OutwardSummary(
                        title = OutwardStepMapper.TITLE_SPLIT_INVOICE_SUMMARY,
                        subTotalAmount = null,
                        taxAmount = null,
                        discountAmount = null,
                        totalAmount = null
                    )
                }
                var customerAmount = 0.0
                var insuranceAmount = 0.0
                adapter?.items?.asSequence()?.filter { it is OutwardItem }
                    ?.map { it as OutwardItem }?.toList()?.forEach {
                        val insurance: Double = if (it.split?.mode == Split.MODE_PERCENTAGE) {
                            Utility.percentageToAmount(it.split!!.cost, it.amount!!)
                        } else {
                            it.split?.cost ?: 0.0
                        }
                        val customer = (it.amount ?: 0.0).minus(insurance)
                        customerAmount += customer
                        insuranceAmount += insurance
                    }
                return OutwardSummary(
                    title = OutwardStepMapper.TITLE_SPLIT_INVOICE_SUMMARY,
                    subTotalAmount = customerAmount,
                    taxAmount = insuranceAmount,
                    discountAmount = null,
                    totalAmount = null
                )
            }
        }
    }

    private fun calculateSections() {
        adapter?.let { nonNullAdapter ->
            /*start parts section calculation*/
            var partsTotal = 0.0
            val partList =
                nonNullAdapter.items.filter { it is OutwardItem && it.type == OutwardItem.TYPE_PART }
            run loop@{
                partList.map { it as OutwardItem }.forEach {
                    if (type == SCREEN_TYPE_PROFORMA && SharedPrefHelper.isGstEnabled() && (it.tax.hsn == null || it.tax.hsn == HSN.SELECT)) {
                        val section = nonNullAdapter.items.find { partSection ->
                            partSection is OutwardSection && partSection.title.contains(
                                OutwardStepMapper.TITLE_PARTS,
                                true
                            )
                        } as OutwardSection
                        section.total = null
                        val index =
                            Utility.findIndexFromList(section, nonNullAdapter.items.toList())
                        if (!binding.estimatorRecyclerview.isComputingLayout)
                            nonNullAdapter.notifyItemChanged(index, null)
                        return@loop
                    }
                    val tax: Float =
                        if (type == SCREEN_TYPE_PROFORMA && SharedPrefHelper.isGstEnabled()) (((it.tax.cgst + it.tax.sgst).toFloat() / 100)) else 0.0f
                    val rate = Utility.round(it.price / (1 + tax), 1)
//                    it.rate = rate
                    val itemSubTotal = Utility.round(rate * it.quantity, 1)
                    val itemDiscountAmount =
                        if (it.discount.amount == 0.0) 0.0 else (if (it.discount.mode == Discount.MODE_PRICE) it.discount.amount else Utility.round(
                            (itemSubTotal * (it.discount.amount / 100)),
                            1
                        ))
                    val itemAmountAfterDiscount =
                        Utility.round(itemSubTotal - itemDiscountAmount, 1)
                    val itemTaxAmount = if (tax > 0.0f) Utility.round(
                        (itemAmountAfterDiscount * tax),
                        1
                    ) else 0.0
                    partsTotal += (itemAmountAfterDiscount + itemTaxAmount)
                }
                val section = nonNullAdapter.items.find {
                    it is OutwardSection && it.title.contains(
                        OutwardStepMapper.TITLE_PARTS,
                        true
                    )
                }?.let { it as OutwardSection }
                val newTitle = OutwardStepMapper.TITLE_PARTS + "(${partList.size})"
                if (section?.total != partsTotal || section.title != newTitle) {
                    section?.total = partsTotal
                    section?.title = newTitle
                    val index =
                        Utility.findIndexFromList(section, nonNullAdapter.items.toList())
                    nonNullAdapter.notifyItemChanged(index, null)
                }
            }
            /*end parts section calculation*/

            /*start labour section calculation*/
            var laboursTotal = 0.0
            val labourList =
                nonNullAdapter.items.filter { it is OutwardItem && it.type == OutwardItem.TYPE_LABOUR }
            labourList.map { it as OutwardItem }.forEach {

                /**
                 * When GST is enabled for the workshop and `sac` is null then total is invalid, hence display "-" and return without any further calculations.
                 * **/
//                if (type == SCREEN_TYPE_PROFORMA && SharedPrefHelper.isGstEnabled() && it.tax.sac == null)
                if (SharedPrefHelper.isGstEnabled() && it.tax.sac == null) {
                    val section = nonNullAdapter.items.find { labourSection ->
                        labourSection is OutwardSection && labourSection.title.contains(
                            OutwardStepMapper.TITLE_LABOURS,
                            true
                        )
                    } as OutwardSection
                    section.total = null
                    val index =
                        Utility.findIndexFromList(section, nonNullAdapter.items.toList())
                    // Handler().post {
                    if (!binding.estimatorRecyclerview.isComputingLayout)
                        nonNullAdapter.notifyItemChanged(index, null)
                    // }
                    return
                }
                val tax: Float =
                    if (SharedPrefHelper.isGstEnabled()) ((it.tax.cgst + it.tax.sgst).toFloat() / 100.0f) else 0.0f // 0.18

                val itemSubTotal = if (it.reduction > 0.0) {
                    (it.rate - it.reduction) * it.quantity
                } else {
                    (it.rate + it.surcharge) * it.quantity
                }
                val itemDiscountAmount =
                    if (it.discount.amount == 0.0) 0.0 else (if (it.discount.mode == Discount.MODE_PRICE) it.discount.amount else (itemSubTotal * (it.discount.amount / 100)))
                val itemAmountAfterDiscount = itemSubTotal - itemDiscountAmount
                val itemTaxAmount =
                    if (SharedPrefHelper.isGstEnabled() && tax > 0.0) (itemAmountAfterDiscount * tax) else 0.0
                laboursTotal += itemAmountAfterDiscount + itemTaxAmount
            }

            val section = nonNullAdapter.items.find {
                it is OutwardSection && it.title.contains(
                    OutwardStepMapper.TITLE_LABOURS,
                    true
                )
            }?.let { it as OutwardSection }
            val newTitle = OutwardStepMapper.TITLE_LABOURS + "(${labourList.size})"
            if (section?.total != laboursTotal || section.title != newTitle) {
                section?.total = laboursTotal
                section?.title = newTitle
                val index = Utility.findIndexFromList(section, nonNullAdapter.items.toList())
                if (!binding.estimatorRecyclerview.isComputingLayout)
                    nonNullAdapter.notifyItemChanged(index, null)
            }
            /*end labour section calculation*/

            /*start service packages calculation*/
            var packageTotal = 0.0
            val packageList =
                nonNullAdapter.items.filter { it is OutwardItem && it.type == OutwardItem.TYPE_SERVICE_PKG }
                    .map { it as OutwardItem }
            packageList.forEach {
                packageTotal += it.amount ?: 0.0
                /* if (type == SCREEN_TYPE_ESTIMATE)
             {
                 it.rates?.forEach { rate ->
                     packageTotal += rate.offerPrice.amount
                 }
             }
             else
             {
                 packageTotal += it.amount ?: 0.0
             }*/
            }
            val pkgSection = nonNullAdapter.items.find {
                it is OutwardSection && it.title.contains(
                    OutwardStepMapper.TITLE_PACKAGES,
                    true
                )
            }?.let { it as OutwardSection }
            val pkgNewTitle = OutwardStepMapper.TITLE_PACKAGES + "(${packageList.size})"
            if (pkgSection?.total != packageTotal || pkgSection.title != pkgNewTitle) {
                pkgSection?.title = pkgNewTitle
                pkgSection?.total = packageTotal
                val index = Utility.findIndexFromList(pkgSection, nonNullAdapter.items.toList())
                if (!binding.estimatorRecyclerview.isComputingLayout)
                    nonNullAdapter.notifyItemChanged(index, null)
            }

            /*end service packages calculation*/
        }
    }

    private fun updatePart(position: Int) {
        //check if item at this position is an Outward item
        adapter?.items?.getOrNull(position)?.let {
            if (it !is OutwardItem)
                return
            //in case of proforma check if HSN is not null otherwise return
            if (type == SCREEN_TYPE_PROFORMA && SharedPrefHelper.isGstEnabled() && (it.tax.hsn == null || it.tax.hsn == HSN.SELECT)) {
                it.amount = null
                if (!binding.estimatorRecyclerview.isComputingLayout)
                    adapter?.notifyItemChanged(position, null)
                return
            }

            val tax: Float =
                if (SharedPrefHelper.isGstEnabled()) (((it.tax.cgst + it.tax.sgst).toFloat() / 100)) else 0.0f
            val rate = it.price / (1 + tax)
            it.rate = rate
            val itemSubTotal = it.rate * it.quantity // rate*quan //amount
            val itemDiscountAmount =
                if (it.discount.amount == 0.0) 0.0 else (if (it.discount.mode == Discount.MODE_PRICE) it.discount.amount else itemSubTotal * (it.discount.amount / 100)) // disamount
            val itemAmountAfterDiscount = itemSubTotal - itemDiscountAmount
            val itemTaxAmount = if (tax > 0.0f) (itemAmountAfterDiscount * tax) else 0.0
            val itemAmount = itemAmountAfterDiscount + itemTaxAmount
            if (it.amount != itemAmount) {
                it.amount = itemAmount
                if (!binding.estimatorRecyclerview.isComputingLayout)
                    adapter?.notifyItemChanged(position, null)
            }

            // done to remove split invoice calculations for the part as the price/discout might have changed. will be re-calculated.
            if (type == SCREEN_TYPE_PROFORMA && it.split != null) {
                it.split = null
            }
        }
    }

    private fun updateLabour(position: Int) {
        //check if item at this position is an Outward item
        adapter?.items?.getOrNull(position)?.let {
            if (it !is OutwardItem)
                return
            //in case of proforma check if sac tax is not null otherwise return
            if (type == SCREEN_TYPE_PROFORMA && SharedPrefHelper.isGstEnabled() && it.tax.sac == null) {
                it.amount = null
                if (!binding.estimatorRecyclerview.isComputingLayout)
                    adapter?.notifyItemChanged(position, null)
                return
            }
            val rate = it.finalRate

            if (it.rate == 0.0) {
                it.rate = 0.0 + rate
                it.price = 0.0 + rate
            }

            if (it.rate != 0.0 && it.rate != it.finalRate) {
                if (it.finalRate > it.rate) {
                    //surcharge
                    it.surcharge = it.finalRate - it.rate
                    it.reduction = 0.0
                } else {
                    //reduction
                    it.reduction = it.rate - it.finalRate
                    it.surcharge = 0.0
                }
            } else {
                it.reduction = 0.0
                it.surcharge = 0.0
            }

            //
            val itemSubTotal = if (it.reduction > 0.0) {
                (it.rate - it.reduction) * it.quantity
            } else {
                (it.rate + it.surcharge) * it.quantity
            }

            val tax =
                if (SharedPrefHelper.isGstEnabled()) ((it.tax.cgst + it.tax.sgst).toFloat() / 100) else 0.0f
            val itemDiscountAmount =
                if (it.discount.amount == 0.0) 0.0 else (if (it.discount.mode == Discount.MODE_PRICE) it.discount.amount else (itemSubTotal * (it.discount.amount / 100)))
            val itemAmountAfterDiscount = itemSubTotal - itemDiscountAmount
            val itemTaxAmount =
                if (SharedPrefHelper.isGstEnabled() && tax > 0.0f) (itemAmountAfterDiscount * tax) else 0.0
            it.amount = itemAmountAfterDiscount + itemTaxAmount
            if (!binding.estimatorRecyclerview.isComputingLayout)
                adapter?.notifyItemChanged(position, null)

            if (type == SCREEN_TYPE_PROFORMA && it.split != null) {
                it.split = null
            }
        }
    }

    private fun insertLabours(startPos: Int, itemsAddedCount: Int) {
        adapter?.items?.let { list ->
            // startPos is always 1 but the loop is programmed to cover edge cases i.e if startPos = 16 & itemCount= 7 then only labours with index 16 to 22 will be updated.
            for (position in startPos until startPos + itemsAddedCount) {
                list.getOrNull(position)?.let { labourItem ->
                    if (labourItem !is OutwardItem)
                        return
                    if (type == SCREEN_TYPE_PROFORMA && SharedPrefHelper.isGstEnabled() && labourItem.tax.sac == null) {
                        labourItem.amount = null
                        return
                    }
                    val tax =
                        if (SharedPrefHelper.isGstEnabled()) ((labourItem.tax.cgst + labourItem.tax.sgst).toFloat() / 100) else 0.0f
                    val rate = labourItem.price
                    labourItem.rate = rate
                    val itemSubTotal = (rate + labourItem.surcharge) * labourItem.quantity
                    val itemDiscountAmount =
                        if (labourItem.discount.amount == 0.0) 0.0 else (if (labourItem.discount.mode == Discount.MODE_PRICE) labourItem.discount.amount else (itemSubTotal * (labourItem.discount.amount / 100)))
                    val itemAmountAfterDiscount = itemSubTotal - itemDiscountAmount
                    val itemTaxAmount =
                        if (SharedPrefHelper.isGstEnabled() && tax > 0.0f) (itemAmountAfterDiscount * tax) else 0.0
                    labourItem.amount = itemAmountAfterDiscount + itemTaxAmount
                }
            }
            if (!binding.estimatorRecyclerview.isComputingLayout) {
                adapter?.notifyItemRangeInserted(startPos, itemsAddedCount)
            }
        }
    }

    private fun validatePrices(): String {
        if (adapter == null) return ""

        var err = ""
        adapter?.items?.forEachIndexed { index, item ->
            if (item is OutwardItem) {
                if ((item.discount.mode == Discount.MODE_PERCENTAGE && item.discount.amount > 100) || (item.discount.mode == Discount.MODE_PRICE && item.finalRate < item.discount.amount)) {
                    err = getString(R.string.discount_gt_price)
                }

                /*          if (item.type == OutwardItem.TYPE_LABOUR) {
                              if (item.rate > 0 && item.finalRate < 1.0) {
                                  err = "Final Rate must be greater than or equal to Rate"

                                  val viewHolder =
                                      binding.estimatorRecyclerview.findViewHolderForAdapterPosition(index) as OutwardingProcessAdapter.LabourViewHolder
                                  if (viewHolder != null) {
                                      viewHolder.itemView.labourRateAmountView.error =
                                          "Final Rate must be greater than or equal to Rate"
                                  }
                              }
                              if ((jobCardType != JobCard.TYPE_ACCIDENTAL) && item.finalRate < item.rate) {
                                  err = "Final Rate must be greater than or equal to Rate"
                                  val viewHolder =
                                      binding.estimatorRecyclerview.findViewHolderForAdapterPosition(index) as OutwardingProcessAdapter.LabourViewHolder
                                  if (viewHolder != null) {
                                      viewHolder.itemView.labourRateAmountView.error =
                                          "Final Rate must be greater than or equal to Rate"
                                  }
                              }
                          }*/
            }
        }
        return err
    }

    private fun startSummaryAnimation() {
        val childPos =
            (binding.estimatorRecyclerview.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        if (adapter?.items?.get(childPos) is OutwardSummary) {
            val view =
                (binding.estimatorRecyclerview.layoutManager as LinearLayoutManager).findViewByPosition(
                    childPos
                )
            val shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake)
            view?.startAnimation(shakeAnimation)
        }
    }

    private fun updateSplitInvoiceSummary(insuranceAmount: Double, customerAmount: Double) {
        bindingBottomOne.splitInvoiceSummaryInsuranceView.text =
            Utility.convertToCurrency(insuranceAmount)
        bindingBottomOne.splitInvoiceSummaryCustomerView.text =
            Utility.convertToCurrency(customerAmount)
    }

    private fun updateProformaTopSummary(summary: OutwardSummary) {
        bindingTopOne.customerAmountView.text =
            if (summary.subTotalAmount != null) Utility.convertToCurrency(summary.subTotalAmount) else "-"
        bindingTopOne.insuranceAmountView.text =
            if (summary.taxAmount != null) Utility.convertToCurrency(summary.taxAmount) else "-"
    }

    private fun quickSplitInvoice() {
        val quickSplitHandler = Handler()
        val customViewBinding = DialogSplitGroupBinding.inflate(LayoutInflater.from(this))

        customViewBinding.labourInsuranceView.setText(0.0.toString())
        customViewBinding.labourCustomerView.text = 100.0.toString()
        customViewBinding.partInsuranceView.setText(0.0.toString())
        customViewBinding.partCustomerView.text = 100.0.toString()
        customViewBinding.partInsuranceView.isEnabled = false
        customViewBinding.labourInsuranceView.isEnabled = false

        customViewBinding.allPartsView.setOnCheckedChangeListener { _, isChecked ->
            customViewBinding.partInsuranceView.isEnabled = isChecked
        }
        customViewBinding.allLabourView.setOnCheckedChangeListener { _, isChecked ->
            customViewBinding.labourInsuranceView.isEnabled = isChecked
        }

        val labourSplitRunnable = Runnable {
            if (customViewBinding.labourInsuranceView.text.isNotEmpty() && Utility.isValidDecimal(
                    customViewBinding.labourInsuranceView.text.toString()
                ) && customViewBinding.labourInsuranceView.text.toString().toDouble() <= 100.0
            ) {
                customViewBinding.labourCustomerView.text = Utility.round(
                    100.0.minus(
                        customViewBinding.labourInsuranceView.text.toString().toDouble()
                    ), 1
                ).toString()
            } else {
                if (customViewBinding.labourInsuranceView.text.isNotEmpty() && Utility.isValidDecimal(
                        customViewBinding.labourInsuranceView.text.toString()
                    )
                ) {
                    customViewBinding.labourInsuranceView.error = "Invalid Amount"
                } else if (customViewBinding.labourInsuranceView.text.isNotEmpty()) {
                    toast("Percentage cannot be greater than 100%")
                    customViewBinding.labourInsuranceView.setText(0.0.toString())
                    customViewBinding.labourCustomerView.text = 100.0.toString()
                }
            }
        }

        val partSplitRunnable = Runnable {
            if (customViewBinding.partInsuranceView.text.isNotEmpty() && Utility.isValidDecimal(
                    customViewBinding.partInsuranceView.text.toString()
                ) && customViewBinding.partInsuranceView.text.toString().toDouble() <= 100.0
            ) {
                customViewBinding.partCustomerView.text = Utility.round(
                    100.0.minus(
                        customViewBinding.partInsuranceView.text.toString().toDouble()
                    ), 1
                ).toString()
            } else {
                if (customViewBinding.partInsuranceView.text.isNotEmpty() && Utility.isValidDecimal(
                        customViewBinding.partInsuranceView.text.toString()
                    )
                ) {
                    customViewBinding.partInsuranceView.error = "Invalid Amount"
                } else if (customViewBinding.partInsuranceView.text.isNotEmpty()) {
                    toast("Percentage cannot be greater than 100%")
                    customViewBinding.partInsuranceView.setText(0.0.toString())
                    customViewBinding.partCustomerView.text = 100.0.toString()
                }
            }
        }

        customViewBinding.labourInsuranceView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quickSplitHandler.removeCallbacks(labourSplitRunnable)
                quickSplitHandler.postDelayed(
                    labourSplitRunnable,
                    OutwardingProcessAdapter.PRICE_CHANGE_INTERVAL
                )
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        customViewBinding.partInsuranceView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quickSplitHandler.removeCallbacks(partSplitRunnable)
                quickSplitHandler.postDelayed(
                    partSplitRunnable,
                    OutwardingProcessAdapter.PRICE_CHANGE_INTERVAL
                )
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setView(customViewBinding.root)
        alertDialogBuilder.setPositiveButton(
            getString(R.string.split_invoice_dialog_button_apply),
            null
        )
        alertDialogBuilder.setNegativeButton(getString(R.string.split_invoice_dialog_button_cancel)) { dialogInterface, _ -> dialogInterface.cancel() }
        val alertDialog = alertDialogBuilder.create()

        alertDialog.setOnShowListener {
            val positiveButton = (alertDialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                var isValidationPassed = true
                if (customViewBinding.allPartsView.isChecked) {
                    if (customViewBinding.partInsuranceView.text.isNotEmpty() && customViewBinding.partInsuranceView.text.toString()
                            .toDouble() <= 100.0
                    ) {
                        adapter?.let { nonNullAdapter ->
                            nonNullAdapter.items.forEach { estimatorItem ->
                                if (estimatorItem is OutwardItem && estimatorItem.type == OutwardItem.TYPE_SPLIT_PART) {
                                    if (estimatorItem.split == null) estimatorItem.split = Split()

                                    estimatorItem.split?.mode = Split.MODE_PERCENTAGE
                                    estimatorItem.split?.cost =
                                        customViewBinding.partInsuranceView.text.toString()
                                            .toDouble()
                                }
                            }
                        }
                    } else {
                        isValidationPassed = false
                        if (customViewBinding.partInsuranceView.text.isNotEmpty()) {
                            toast("Percentage cannot be greater than 100%")
                            customViewBinding.partInsuranceView.setText(0.0.toString())
                            customViewBinding.partCustomerView.text = 100.0.toString()
                        }
                    }
                }

                if (customViewBinding.allLabourView.isChecked) {
                    if (customViewBinding.labourInsuranceView.text.isNotEmpty() && customViewBinding.labourInsuranceView.text.toString()
                            .toDouble() <= 100.0
                    ) {
                        adapter?.let { nonNullAdapter ->
                            nonNullAdapter.items.forEach { estimatorItem ->
                                if (estimatorItem is OutwardItem && estimatorItem.type == OutwardItem.TYPE_SPLIT_LABOUR) {
                                    if (estimatorItem.split == null) estimatorItem.split = Split()

                                    estimatorItem.split?.mode = Split.MODE_PERCENTAGE
                                    estimatorItem.split?.cost =
                                        customViewBinding.labourInsuranceView.text.toString()
                                            .toDouble()
                                }
                            }
                        }
                    } else {
                        isValidationPassed = false
                        if (customViewBinding.labourInsuranceView.text.isNotEmpty()) {
                            toast("Percentage cannot be greater than 100%")
                            customViewBinding.labourInsuranceView.setText(0.0.toString())
                            customViewBinding.labourCustomerView.text = 100.0.toString()
                        }
                    }
                }

                if (isValidationPassed && (customViewBinding.allPartsView.isChecked || customViewBinding.allLabourView.isChecked)) {
                    adapter?.notifyDataSetChanged()
                    val summary = calculateSummary(SCREEN_TYPE_SPLIT_INVOICE)
                    updateSplitInvoiceSummary(
                        summary.taxAmount ?: 0.0,
                        summary.subTotalAmount ?: 0.0
                    )
                    alertDialog.dismiss()
                }
            }
        }

        alertDialog.show()
    }


    /*  private fun quickSplitInvoice() {
          val quickSplitHandler = Handler()
          val customView = View.inflate(this, R.layout.dialog_split_group, null)
          customView.labourInsuranceView.setText(0.0.toString())
          customView.labourCustomerView.text = 100.0.toString()
          customView.partInsuranceView.setText(0.0.toString())
          customView.partCustomerView.text = 100.0.toString()
          customView.partInsuranceView.isEnabled = false
          customView.labourInsuranceView.isEnabled = false
          customView.allPartsView.setOnCheckedChangeListener { _, isChecked ->
              customView.partInsuranceView.isEnabled = isChecked
          }
          customView.allLabourView.setOnCheckedChangeListener { _, isChecked ->
              customView.labourInsuranceView.isEnabled = isChecked
          }
          val labourSplitRunnable = Runnable {
              if (customView.labourInsuranceView.text.isNotEmpty() && Utility.isValidDecimal(
                      customView.labourInsuranceView.text.toString()
                  ) && customView.labourInsuranceView.text.toString().toDouble() <= 100.0
              ) {
                  customView.labourCustomerView.text = Utility.round(
                      100.0.minus(
                          customView.labourInsuranceView.text.toString().toDouble()
                      ), 1
                  ).toString()
              } else {
                  if (customView.labourInsuranceView.text.isNotEmpty() && Utility.isValidDecimal(
                          customView.labourInsuranceView.text.toString()
                      )
                  ) {
                      customView.labourInsuranceView.error = "Invalid Amount"
                  } else if (customView.labourInsuranceView.text.isNotEmpty()) {
                      toast("Percentage cannot be greater than 100%")
                      customView.labourInsuranceView.setText(0.0.toString())
                      customView.labourCustomerView.text = 100.0.toString()
                  }
              }
          }
          val partSplitRunnable = Runnable {
              if (customView.partInsuranceView.text.isNotEmpty() && Utility.isValidDecimal(
                      customView.partInsuranceView.text.toString()
                  ) && customView.partInsuranceView.text.toString().toDouble() <= 100.0
              ) {
                  customView.partCustomerView.text = Utility.round(
                      100.0.minus(
                          customView.partInsuranceView.text.toString().toDouble()
                      ), 1
                  ).toString()
              } else {
                  if (customView.partInsuranceView.text.isNotEmpty() && Utility.isValidDecimal(
                          customView.partInsuranceView.text.toString()
                      )
                  ) {
                      customView.partInsuranceView.error = "Invalid Amount"
                  } else if (customView.partInsuranceView.text.isNotEmpty()) {
                      toast("Percentage cannot be greater than 100%")
                      customView.partInsuranceView.setText(0.0.toString())
                      customView.partCustomerView.text = 100.0.toString()
                  }
              }
          }
          customView.labourInsuranceView.addTextChangedListener(object : TextWatcher {
              override fun afterTextChanged(s: Editable?) {
                  quickSplitHandler.removeCallbacks(labourSplitRunnable)
                  quickSplitHandler.postDelayed(
                      labourSplitRunnable,
                      OutwardingProcessAdapter.PRICE_CHANGE_INTERVAL
                  )
              }

              override fun beforeTextChanged(
                  s: CharSequence?,
                  start: Int,
                  count: Int,
                  after: Int
              ) {
              }

              override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
              }
          })
          customView.partInsuranceView.addTextChangedListener(object : TextWatcher {
              override fun afterTextChanged(s: Editable?) {
                  quickSplitHandler.removeCallbacks(partSplitRunnable)
                  quickSplitHandler.postDelayed(
                      partSplitRunnable,
                      OutwardingProcessAdapter.PRICE_CHANGE_INTERVAL
                  )
              }

              override fun beforeTextChanged(
                  s: CharSequence?,
                  start: Int,
                  count: Int,
                  after: Int
              ) {
              }

              override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
              }
          })
          val alertDialogBuilder = AlertDialog.Builder(this)
          alertDialogBuilder.setView(customView)
          alertDialogBuilder.setPositiveButton(
              getString(R.string.split_invoice_dialog_button_apply),
              null
          )
          alertDialogBuilder.setNegativeButton(getString(R.string.split_invoice_dialog_button_cancel)) { dialogInterface, _ -> dialogInterface.cancel() }
          val alertDialog = alertDialogBuilder.create()
          alertDialog.setOnShowListener {
              val positiveButton =
                  (alertDialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
              positiveButton.setOnClickListener {
                  var isValidationPassed = true
                  if (customView.allPartsView.isChecked) {
                      if (customView.partInsuranceView.text.isNotEmpty() && customView.partInsuranceView.text.toString()
                              .toDouble() <= 100.0
                      ) {
                          adapter?.let { nonNullAdapter ->
                              nonNullAdapter.items
                                  .forEach { estimatorItem ->
                                      if (estimatorItem is OutwardItem && estimatorItem.type == OutwardItem.TYPE_SPLIT_PART) {
                                          if (estimatorItem.split == null) estimatorItem.split =
                                              Split()

                                          estimatorItem.split?.mode = Split.MODE_PERCENTAGE
                                          estimatorItem.split?.cost =
                                              customView.partInsuranceView.text.toString()
                                                  .toDouble()
                                      }
                                  }
                          }
                      } else {
                          isValidationPassed = false
                          if (customView.partInsuranceView.text.isNotEmpty()) {
                              toast("Percentage cannot be greater than 100%")
                              customView.partInsuranceView.setText(0.0.toString())
                              customView.partCustomerView.text = 100.0.toString()
                          }
                      }
                  }
                  if (customView.allLabourView.isChecked) {
                      if (customView.labourInsuranceView.text.isNotEmpty() && customView.labourInsuranceView.text.toString()
                              .toDouble() <= 100.0
                      ) {
                          adapter?.let { nonNullAdapter ->
                              nonNullAdapter.items
                                  .forEach { estimatorItem ->
                                      if (estimatorItem is OutwardItem && estimatorItem.type == OutwardItem.TYPE_SPLIT_LABOUR) {
                                          if (estimatorItem.split == null) estimatorItem.split =
                                              Split()

                                          estimatorItem.split?.mode = Split.MODE_PERCENTAGE
                                          estimatorItem.split?.cost =
                                              customView.labourInsuranceView.text.toString()
                                                  .toDouble()
                                      }
                                  }
                          }
                      } else {
                          isValidationPassed = false
                          if (customView.labourInsuranceView.text.isNotEmpty()) {
                              toast("Percentage cannot be greater than 100%")
                              customView.labourInsuranceView.setText(0.0.toString())
                              customView.labourCustomerView.text = 100.0.toString()
                          }
                      }
                  }
                  if (isValidationPassed && (customView.allPartsView.isChecked || customView.allLabourView.isChecked)) {
                      adapter?.notifyDataSetChanged()
                      val summary = calculateSummary(SCREEN_TYPE_SPLIT_INVOICE)
                      updateSplitInvoiceSummary(
                          summary.taxAmount ?: 0.0, summary.subTotalAmount
                              ?: 0.0
                      ) // to get split invoice summary
                      alertDialog.dismiss()
                  }
              }
          }
          alertDialog.show()
      }*/

    private fun promptUserIfDirtyOrExit() {
        if (dirtyDetector.isObserving && dirtyDetector.isDirty) {
            alert(
                getString(R.string.alert_dirty_message),
                getString(R.string.alert_dirty_title)
            ) {
                positiveButton(getString(R.string.alert_dirty_btn_save)) {
                    when (type) {
                        SCREEN_TYPE_ESTIMATE -> {
                            saveEstimate(false, false)
                        }

                        SCREEN_TYPE_PROFORMA -> {
                            saveProforma(false)
                        }

                        SCREEN_TYPE_SPLIT_INVOICE -> {
                            saveSplitInvoice()
                        }
                    }
                }
                negativeButton(getString(R.string.alert_dirty_btn_exit)) {
                    finish()
                }
                neutralPressed(getString(R.string.alert_dirty_btn_cancel)) {
                    it.dismiss()
                }
            }.show()
        } else {
            finish()
        }
    }

    private fun startDirtyDetector() {
        dirtyDetector.observe(
            estimatorList,
            OutwardStep::class.java.classLoader!!
        ) { originalObject, currentObject ->
            // true if both objects are equal or check if removal is pending then false
            originalObject == currentObject && (adapter?.isPendingRemoval() == true).not()
        }
    }

    companion object {
        const val PART_NUMBER_REQUEST_CODE = 353

        //        const val ARG_VEHICLE_AMC_ID = "vehicle-amc-id"
        const val ARG_JOB_CARD_ID = "job_card_id"
        const val ARG_INVOICE_ID = "invoice_id"
        const val ARG_IS_VIEW_ONLY = "is_view_only"
        const val ARG_VEHICLE_TYPE = "arg_vehicle_type"
        const val ARG_DISPLAY_JOBCARD_ID = "display_jobcard_id"
        const val ARG_DISPLAY_INVOICE_ID = "id"
        const val ARG_IS_SPLIT_INVOICE = "is_split_invoice"
        const val ARG_INVOICE = "invoice"
        const val ARG_JOB_CARD_TYPE = "jobcard_type"

        const val SCREEN_TYPE = "is_from"
        const val SCREEN_TYPE_ESTIMATE = "is_estimator"
        const val SCREEN_TYPE_PROFORMA = "is_proforma"
        const val SCREEN_TYPE_SPLIT_INVOICE = "is_split_invoice"

        const val REQUEST_CODE_ADD_PART = 987
        const val REQUEST_CODE_ADD_LABOUR = 984
        const val REQUEST_CODE_THIRD_PARTY = 985
        private const val REQUEST_CODE_CHECK_INVOICE = 121
        private const val REQUEST_CODE_SPLIT_INVOICE = 122
        const val REQUEST_CODE_PACKAGES = 123
        const val NO_ITEM_TYPE = ""

        fun getEstimatorIntent(
            context: Context,
            displayJobCardId: String?,
            jobCardId: String,
            isViewOnly: Boolean,
            vehicleType: String?
        ): Intent {
            val startIntent = Intent(context, OutwardingProcessActivity::class.java)
//            startIntent.putExtra(ARG_VEHICLE_AMC_ID, vehicleAmcId)
            startIntent.putExtra(ARG_JOB_CARD_ID, jobCardId)
            startIntent.putExtra(ARG_DISPLAY_JOBCARD_ID, displayJobCardId)
            startIntent.putExtra(ARG_IS_VIEW_ONLY, isViewOnly)
            startIntent.putExtra(SCREEN_TYPE, SCREEN_TYPE_ESTIMATE)
            startIntent.putExtra(ARG_VEHICLE_TYPE, vehicleType)
            return startIntent
        }

        fun getProformaIntent(
            context: Context,
            screenType: String,
            displayJobCardId: String?,
            displayInvoiceId: String?,
            jobCardID: String?,
            invoiceId: String?,
            isSplitInvoice: Boolean,
            vehicleType: String?,
            invoice: Invoice?,
            jobCardType: String?
        ): Intent {
            val startIntent = Intent(context, OutwardingProcessActivity::class.java)
//            startIntent.putExtra(ARG_VEHICLE_AMC_ID, vehicleAmcId)
            startIntent.putExtra(ARG_JOB_CARD_ID, jobCardID)
            startIntent.putExtra(ARG_INVOICE_ID, invoiceId)
            startIntent.putExtra(ARG_DISPLAY_JOBCARD_ID, displayJobCardId)
            startIntent.putExtra(ARG_DISPLAY_INVOICE_ID, displayInvoiceId)
            startIntent.putExtra(SCREEN_TYPE, screenType)
            startIntent.putExtra(ARG_IS_SPLIT_INVOICE, isSplitInvoice)
            startIntent.putExtra(ARG_VEHICLE_TYPE, vehicleType)
            startIntent.putExtra(ARG_INVOICE, invoice)
            startIntent.putExtra(ARG_JOB_CARD_TYPE, jobCardType)
            return startIntent
        }
    }


}

interface OutwardProcessInteraction {
    fun removeThirdParty(invoiceId: String, thirdParty: ThirdParty)
    fun showPartSelector(position: Int, item: OutwardItem)
}