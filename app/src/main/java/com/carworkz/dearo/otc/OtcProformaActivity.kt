package com.carworkz.dearo.otc

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.PriceChangeListener
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.estimate.partnumberselection.PartNumberSelectionAdapter
import com.carworkz.dearo.addjobcard.createjobcard.estimate.partnumberselection.searchpartnumber.SearchPartNumberActivity
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.databinding.ActivityCustomerVehicleHistoryBinding
import com.carworkz.dearo.databinding.ActivityOtcproformaBinding
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.events.CardStatusChangeEvent
import com.carworkz.dearo.interactionprovider.ToolBarImgInteractionProvider
import com.carworkz.dearo.outwarding.SwipeCallback
import com.carworkz.dearo.outwarding.helper.OutwardSection
import com.carworkz.dearo.outwarding.helper.OutwardSummary
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.screencontainer.ActionImgScreenContainer
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.base_layout.*
import kotlinx.android.synthetic.main.bottom_new_proforma.*
import kotlinx.android.synthetic.main.layout_empty_cart.*
import kotlinx.android.synthetic.main.layout_otc_proforma.**/
import timber.log.Timber
import javax.inject.Inject

class OtcProformaActivity : ScreenContainerActivity(), ToolBarImgInteractionProvider,
    OtcProformaContract.View, View.OnClickListener, PriceChangeListener {
    private lateinit var binding: ActivityOtcproformaBinding
    private lateinit var actionImgScreenContainer: ActionImgScreenContainer

    private var partsList = mutableListOf<Any>()
    private var hsnList = mutableListOf<HSN>()

    @Inject
    lateinit var presenter: OtcProformaPresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    @Inject
    lateinit var pdfMediator: PdfMediator

    private lateinit var invoiceId: String
    private lateinit var displayId: String

    private lateinit var invoice: Invoice

    private var adapter: OtcProformaAdapter? = null

    private var vehicleType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        getIntentData()
        super.onCreate(savedInstanceState)
        setUpComponent()
        setSwipeForRecyclerView()
        screenTracker.sendScreenEvent(this, ScreenTracker.SCREEN_OTC_PROFORMA, this.javaClass.name)
        presenter.getProformaInvoice(invoiceId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_PART -> {
                    if (data == null)
                        return
                    val partNumber =
                        data.getParcelableExtra(PartNumberSelectionAdapter.ARG_PART_NUMBER) as? PartNumber
                    val part = mapToPart(partNumber!!)
                    insertPart(part)
                    actionImgScreenContainer.refreshToolBar()
                }

                REQUEST_CODE_PDF -> {
                    EventsManager.post(
                        CardStatusChangeEvent(
                            CardStatusChangeEvent.CARD_TYPE_OTC,
                            CardStatusChangeEvent.CARD_STATUS_OTC_INVOICED
                        )
                    )
                    finish()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.idlayOTC.layBottomNewPro.tvAddItem, binding.idlayEmpty.btnAddPart -> {
                startActivityForResult(
                    SearchPartNumberActivity.getIntent(
                        this,
                        "",
                        null,
                        null,
                        vehicleType
                    ), REQUEST_CODE_PART
                )
            }

            binding.idlayOTC.layBottomNewPro.tvPreview -> {
                saveProforma(isPreview = true, isSaveOnly = false)
            }
        }
    }

    private fun startProformaPdf() {
        pdfMediator.startOtcProformaPdfWithResult(this, invoice, REQUEST_CODE_PDF)
    }

    override fun getNavigationImage(): Int = R.drawable.ic_arrow_back_white_24dp

    override fun onActionBtnClick() = saveProforma(isPreview = false, isSaveOnly = false)

    override fun onSecondaryActionBtnClick() = Unit

    override fun getActionBarImage(): Int =
        if (partsList.isNotEmpty()) R.drawable.ic_save_white_24dp else 0

    override fun getSecondaryActionBarImage(): Int = 0

    override fun getToolBarTitle(): String = displayId

    override fun createScreenContainer(): ScreenContainer {
        actionImgScreenContainer = ActionImgScreenContainer(this)
        return actionImgScreenContainer
    }

    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityOtcproformaBinding.inflate(layoutInflater)
        return binding
    }


    override fun getProgressView(): View = ProgressBar(this)

    override fun displayProforma(invoice: Invoice, hsnList: List<HSN>) {
        this.invoice = invoice
        this.hsnList = hsnList.toMutableList()
        this.partsList = map(invoice.parts!!)
        setUpList()
        actionImgScreenContainer.refreshToolBar()
    }

    override fun showError(error: String?) {
    }

    override fun moveToNextScreen(preview: Boolean) {
        if (preview) {
            if (adapter?.items?.none { it is Part } == true) {
                toast("Please add parts to preview")
                return
            }
            startProformaPdf()
            return
        }
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

    override fun onPriceUpdate(itemType: String, position: Int) {
        adapter?.items?.getOrNull(position)?.let {
            if (it is Part) {
                if (SharedPrefHelper.isGstEnabled() && (it.tax.hsn == null || it.tax.hsn == HSN.SELECT)) {
                    it.amount = -1.0 // to display error
                    if (!binding.idlayOTC.rvOtc.isComputingLayout)
                        adapter?.notifyItemChanged(position)
                    return
                }
                val tax: Float = (((it.tax.cgst + it.tax.sgst).toFloat() / 100))
                val rate = Utility.round(it.price / (1 + tax), 1)
                it.rate = rate
                val itemSubTotal = Utility.round(rate * it.quantity, 1)
                val itemDiscountAmount =
                    if (it.discount.amount == 0.0) 0.0 else (if (it.discount.mode == Discount.MODE_PRICE) it.discount.amount else Math.round(
                        (itemSubTotal * (it.discount.amount / 100))
                    ).toDouble())
                val itemAmountAfterDiscount = Utility.round(itemSubTotal - itemDiscountAmount, 1)
                val itemTaxAmount = if (tax > 0.0f) (itemAmountAfterDiscount * tax) else 0.0
                val itemAmount = Utility.round((itemAmountAfterDiscount + itemTaxAmount), 1)
                if (it.amount != itemAmount) {
                    it.amount = Utility.round(itemAmount, 1)
                    if (!binding.idlayOTC.rvOtc.isComputingLayout)
                        adapter?.notifyItemChanged(position, null)
                }
            }
        }
        adapter?.notifyItemChanged(position)
        refreshSummary()
    }

    override fun onVendorSelectionClick(itemType: String, position: Int) {
        TODO("Not yet implemented")
    }

    private fun saveProforma(isPreview: Boolean, isSaveOnly: Boolean) {
        adapter?.let { nonNullAdapter ->
            val invoice = Invoice()
            invoice.id = invoiceId
            invoice.parts =
                nonNullAdapter.items.filter { it is Part && !nonNullAdapter.isPendingRemoval(it) }
                    .map { it as Part }.toMutableList()
            presenter.updateProformaInvoice(invoiceId, invoice, isPreview, isSaveOnly)
        }
    }

    private fun getIntentData() {
        invoiceId = intent.extras?.getString(ARG_INVOICE_ID)!!
        displayId = intent.extras?.getString(ARG_DISPLAY_ID)!!
        vehicleType = intent.extras?.getString(ARG_VEHICLE_TYPE)
    }

    private fun setUpComponent() {
        (application as DearOApplication)
            .repositoryComponent
            .COMPONENT(OtcProfromaPresenterModule(this))
            .inject(this)
    }

    private fun setSwipeForRecyclerView() {

        val swipeHelper = object : SwipeCallback(0, ItemTouchHelper.LEFT, this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val swipedPosition = viewHolder.adapterPosition
                val adapter = binding.idlayOTC.rvOtc.adapter as OtcProformaAdapter
                adapter.pendingRemoval(swipedPosition)
            }

            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val position = viewHolder.adapterPosition
                val adapter = binding.idlayOTC.rvOtc.adapter as OtcProformaAdapter
                return if ((viewHolder !is OtcProformaAdapter.ViewHolder) || adapter.isPendingRemoval(
                        position
                    )
                ) {
                    0
                } else super.getSwipeDirs(recyclerView, viewHolder)
            }
        }

        val mItemTouchHelper = ItemTouchHelper(swipeHelper)
        mItemTouchHelper.attachToRecyclerView(binding.idlayOTC.rvOtc)

        // set swipe label
        swipeHelper.leftSwipeLabel = "Delete"
        // set swipe background-Color
        swipeHelper.leftColorCode = ContextCompat.getColor(this, R.color.red)
    }

    private fun setUpList() {
        if (partsList.isNotEmpty()) {
            binding.idlayOTC.parentOtcView.visibility = View.VISIBLE
            binding.idlayEmpty.emptyCartParentView.visibility = View.GONE
            binding.idlayOTC.layBottomNewPro.tvAddItem.setOnClickListener(this)
            binding.idlayOTC.layBottomNewPro.tvAddItem.text = getString(R.string.add_parts_w_plus)
            binding.idlayOTC.layBottomNewPro.tvPreview.setOnClickListener(this)
            adapter = OtcProformaAdapter(this, partsList, this.hsnList, this)
            binding.idlayOTC.rvOtc.addItemDecoration(
                (DividerItemDecoration(
                    this,
                    LinearLayout.VERTICAL
                ))
            )
            binding.idlayOTC.rvOtc.adapter = adapter
        } else {
            Timber.d("No Parts Found")
            partsList.clear() // incase api is called again to bring page to fresh state.
            binding.idlayOTC.parentOtcView.visibility = View.GONE
            binding.idlayEmpty.emptyCartParentView.visibility = View.VISIBLE
            binding.idlayEmpty.btnAddPart.setOnClickListener(this)
        }
    }

    fun refreshSummary() {
        adapter?.let { nonNullAdapter ->
            val lastIndex = nonNullAdapter.items.lastIndex
            nonNullAdapter.items[lastIndex] =
                calculateSummary(nonNullAdapter.items.filter { it is Part }.map { it as Part })
            nonNullAdapter.notifyItemChanged(lastIndex)
            val partSection =
                nonNullAdapter.items.find { it is OutwardSection && it.title.contains(TITLE_PART) }
            Timber.d("is part sectyion null $partSection")
            (partSection as? OutwardSection)?.title =
                "$TITLE_PART(${nonNullAdapter.items.filter { it is Part }.size})"
            nonNullAdapter.notifyItemChanged(nonNullAdapter.items.indexOf(partSection))
        }
    }

    private fun insertPart(part: Part) {
        if (partsList.isEmpty()) {
            binding.idlayOTC.parentOtcView.visibility = View.VISIBLE
            binding.idlayEmpty.emptyCartParentView.visibility = View.GONE
            this.partsList = map(listOf(part))
            setUpList()
        } else {
            val currentList = adapter?.items
            if (currentList?.find { it is Part && it.partNumber == part.partNumber } != null) {
                toast("Already added")
                return
            }
            currentList?.add(1, part)
            adapter?.notifyItemInserted(1)
            refreshSummary()
        }
        saveProforma(isPreview = false, isSaveOnly = true)
    }

    private fun mapToPart(partNumber: PartNumber): Part {
        val part = Part()
        part.text =
            if (partNumber.parts.isNotEmpty()) partNumber.parts[0].partName else partNumber.description
        part.partNumber = partNumber.partNumber
        part.description = partNumber.description
        part.brand.name = partNumber.brandName
        part.brand.id = partNumber.brandId
        part.stock = partNumber.stock
        if (partNumber.parts?.isNotEmpty() == true)
            part.tax = partNumber.parts[0].tax

        partNumber.tax?.let {
            if (it.hsn.isNullOrEmpty().not()) {
                part.tax = it
            }
        }
        if (partNumber.unit != part.unit) {
            part.price = partNumber.unitPrice.toDouble()
            part.unit = partNumber.unit
            part.quantity = 1f
        } else {
            part.price = partNumber.unitPrice.toDouble()
        }
        Timber.d("part nmber $partNumber")
        Timber.d("part $part")
        return part
    }

    private fun map(parts: List<Part>): MutableList<Any> {
        val list = mutableListOf<Any>()
        if (parts.isNotEmpty()) {
            val partSectionTitle = "$TITLE_PART(${parts.size})"
            val partSection = OutwardSection(partSectionTitle, null)
            val summarySection = OutwardSection(TITLE_SUMMARY, null)
            val summary = calculateSummary(parts)
            list.add(partSection)
            list.addAll(parts)
            list.add(summarySection)
            list.add(summary)
        }
        return list
    }

    private fun calculateSummary(parts: List<Part>): OutwardSummary {
        var totalTax = 0.0
        var totalDiscount = 0.0
        var subTotal = 0.0
        var totalAmount = 0.0
        parts.forEach {
            val tax =
                if (SharedPrefHelper.isGstEnabled()) (it.tax.cgst + it.tax.sgst).toFloat() / 100 else 0.0f
            val rate = it.price / (1 + tax)
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
        return OutwardSummary(TITLE_SUMMARY, subTotal, totalTax, totalDiscount, totalAmount)
    }

    companion object {
        const val ARG_INVOICE_ID = "arg_invoice_id"
        const val ARG_DISPLAY_ID = "arg_display_id"
        const val ARG_VEHICLE_TYPE = "arg_display_idvehicle_type"

        // const val ARG_PART = "arg_part"
        const val TITLE_PART = "Parts"
        const val TITLE_SUMMARY = "Summary"
        const val REQUEST_CODE_PART = 100
        const val REQUEST_CODE_PDF = 101

        fun getIntent(
            context: android.content.Context,
            invoiceId: String,
            displayId: String,
            vehicleType: String?
        ): Intent {
            return Intent(context, OtcProformaActivity::class.java).apply {
                putExtra(ARG_INVOICE_ID, invoiceId)
                putExtra(ARG_DISPLAY_ID, displayId)
                putExtra(ARG_VEHICLE_TYPE, vehicleType)
            }
        }
    }
}
