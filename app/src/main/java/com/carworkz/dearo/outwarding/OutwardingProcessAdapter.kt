package com.carworkz.dearo.outwarding

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.PriceChangeListener
import com.carworkz.dearo.R
import com.carworkz.dearo.base.DelegateSwipeAdapter
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.databinding.LayoutEstimatorBottomBinding
import com.carworkz.dearo.databinding.NewEstimatorTitleBinding
import com.carworkz.dearo.databinding.NewProformaSummaryBinding
import com.carworkz.dearo.databinding.NewRowPkgPartBinding
import com.carworkz.dearo.databinding.NewRowProformaBinding
import com.carworkz.dearo.databinding.RowLabourProformaBinding
import com.carworkz.dearo.databinding.RowNewEstimatorBinding
import com.carworkz.dearo.databinding.RowPackagesProformaBinding
import com.carworkz.dearo.databinding.RowProformaThirdPartyAddBinding
import com.carworkz.dearo.databinding.RowProformaThirdPartyEditBinding
import com.carworkz.dearo.databinding.RowSplitInvoiceBinding
import com.carworkz.dearo.databinding.RowSplitOtherChargesBinding
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.extensions.*
import com.carworkz.dearo.invoices.addItem.labour.AddLabourActivity
import com.carworkz.dearo.invoices.addItem.part.AddEditPartActivity
import com.carworkz.dearo.outwarding.OutwardingProcessActivity.Companion.NO_ITEM_TYPE
import com.carworkz.dearo.outwarding.helper.*
import com.carworkz.dearo.searchabledialog.SearchableDialog
import com.carworkz.dearo.thirdpartydetails.ThirdPartyDetailsActivity
import com.carworkz.dearo.utils.Constants
import com.carworkz.dearo.utils.Utility/*import kotlinx.android.synthetic.main.row_labour_proforma.view.*
import kotlinx.android.synthetic.main.row_new_estimator_background.view.*
import kotlinx.android.synthetic.main.row_proforma_third_party_add.view.*
import kotlinx.android.synthetic.main.row_proforma_third_party_edit.view.**/
import timber.log.Timber
import java.util.*

/***
 *
 */
class OutwardingProcessAdapter(
    var items: MutableList<OutwardStep>,
    val viewOnly: Boolean,
    val invoiceId: String?,
    val jobCardId: String,
    val type: String,
    private val hsnList: MutableList<HSN>?,
    val priceChangeListener: PriceChangeListener,
    val vehicleType: String?,
    val isAmcApplied: Boolean,
    val jcType: String
) : DelegateSwipeAdapter<Any>(items) {

    private var interaction: OutwardProcessInteraction? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PART -> PartViewHolder(
                RowNewEstimatorBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            VIEW_TYPE_LABOUR -> LabourViewHolder(
                RowLabourProformaBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            VIEW_TYPE_SPLIT_INVOICE -> SplitInvoiceViewHolder(
                RowSplitInvoiceBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            VIEW_TYPE_SECTION -> SectionViewHolder(
                NewEstimatorTitleBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            VIEW_TYPE_PROFORMA_SUMMARY -> SummaryViewHolder(
                NewProformaSummaryBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            VIEW_TYPE_ESTIMATOR_SUMMARY -> EstimatorSummaryViewHolder(
                LayoutEstimatorBottomBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            VIEW_TYPE_SERVICE_PACKAGE -> PackageViewHolder(
                RowPackagesProformaBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            VIEW_TYPE_SPLIT_OTHER_CHARGES -> SplitInvoiceOtherChargesViewHolder(
                RowSplitOtherChargesBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            VIEW_TYPE_THIRD_PARTY_ADD -> ThirdPartyAddViewHolder(
                RowProformaThirdPartyAddBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            VIEW_TYPE_THIRD_PARTY_EDIT -> ThirdPartyEditViewHolder(
                RowProformaThirdPartyEditBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            VIEW_TYPE_SER_PKG_PART -> ServicePkgPartViewHolder(
                NewRowPkgPartBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            VIEW_TYPE_SER_PKG_LABOUR -> ServicePkgLabourViewHolder(
                NewRowPkgPartBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            else -> PartViewOneHolder(
                NewRowProformaBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = items[position]) {
            is OutwardItem -> {
                when (item.type) {
                    OutwardItem.TYPE_SERVICE_PKG -> VIEW_TYPE_SERVICE_PACKAGE
                    OutwardItem.TYPE_LABOUR -> VIEW_TYPE_LABOUR
                    OutwardItem.TYPE_PART -> VIEW_TYPE_PART
                    OutwardItem.TYPE_SPLIT_PART, OutwardItem.TYPE_SPLIT_LABOUR -> VIEW_TYPE_SPLIT_INVOICE
                    OutwardItem.TYPE_SPLIT_OTHER_CHARGES -> VIEW_TYPE_SPLIT_OTHER_CHARGES
                    OutwardItem.TYPE_SER_PKG_PART -> VIEW_TYPE_SER_PKG_PART
                    OutwardItem.TYPE_SER_PKG_LABOUR -> VIEW_TYPE_SER_PKG_LABOUR
                    else -> VIEW_TYPE_PART
                }
            }

            is OutwardSection -> VIEW_TYPE_SECTION
            is OutwardSummary -> {
                when (item.title) {
                    OutwardStepMapper.TITLE_ESTIMATOR_SUMMARY -> VIEW_TYPE_ESTIMATOR_SUMMARY
                    OutwardStepMapper.TITLE_PROFORMA_SUMMARY -> VIEW_TYPE_PROFORMA_SUMMARY
                    else -> VIEW_TYPE_PROFORMA_SUMMARY
                }
            }

            is OutwardActionItem -> {
                if (item.thirdParty == null || item.thirdParty.isThirdParty != true) {
                    VIEW_TYPE_THIRD_PARTY_ADD
                } else {
                    VIEW_TYPE_THIRD_PARTY_EDIT
                }
            }

            else -> VIEW_TYPE_PROFORMA_SUMMARY
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_PART -> {
                val binding = (holder as PartViewHolder).binding
                val item = items[position] as OutwardItem
                if (itemsPendingRemoval.contains(item)) {
                    binding.layOutEstimator.clParentDelete.visibility = View.VISIBLE
                    binding.layOutRowPro.estimateRowParentView.visibility = View.GONE
                    binding.layOutEstimator.tvDeleteUndo.setOnClickListener {
                        undoDelete(item)
                    }
                } else {
                    binding.layOutEstimator.clParentDelete.visibility = View.GONE
                    binding.layOutRowPro.estimateRowParentView.visibility = View.VISIBLE
                    bindPart(item, binding, position)
                }
            }

            VIEW_TYPE_LABOUR -> {
                val binding = (holder as LabourViewHolder).binding
                val item = items[position] as OutwardItem
                if (itemsPendingRemoval.contains(item)) {
                    binding.layOutNewEstimator.clParentDelete.visibility = View.VISIBLE
                    binding.estimateRowParentView.visibility = View.GONE
                    binding.layOutNewEstimator.tvDeleteUndo.setOnClickListener {
                        undoDelete(item)
                    }
                } else {
                    binding.layOutNewEstimator.clParentDelete.visibility = View.GONE
                    binding.estimateRowParentView.visibility = View.VISIBLE
                    bindLabour(item, binding)
                }
            }

            VIEW_TYPE_SECTION -> {
                val binding = (holder as SectionViewHolder).binding
                val section = items[position] as OutwardSection
                binding.tvSectionTitle.text = section.title
                if (section.title == OutwardStepMapper.TITLE_PROFORMA_SUMMARY || section.title == OutwardStepMapper.TITLE_ESTIMATOR_SUMMARY || section.title == OutwardStepMapper.TITLE_SPLIT_INVOICE_SUMMARY || section.title == OutwardStepMapper.TITLE_SPLIT_OTHER_CHARGES) {
                    binding.tvSectionTotal.visibility = View.GONE
                } else {
                    binding.tvSectionTotal.visibility = View.VISIBLE
                    binding.tvSectionTotal.text = if (section.total != null) {
                        "Total: \u20B9 ${Utility.round(section.total!!, 1)}"
                    } else "-"
                }
            }

            VIEW_TYPE_PROFORMA_SUMMARY -> {
                val binding = (holder as SummaryViewHolder).binding
                val item = items[position] as OutwardSummary
                binding.newProformaSummarySubtotal.text =
                    if (item.subTotalAmount != null) Utility.convertToCurrency(item.subTotalAmount!!)
                        .toString() else "-"
                binding.newProformaSummaryTax.text =
                    if (item.taxAmount != null) Utility.convertToCurrency(item.taxAmount!!)
                        .toString() else "-"
                binding.newProformaSummaryDiscount.text =
                    if (item.discountAmount != null) Utility.convertToCurrency(item.discountAmount!!)
                        .toString() else "-"
                binding.newProformaSummaryTotal.text =
                    if (item.totalAmount != null) Utility.convertToCurrency(item.totalAmount!!)
                        .toString() else "-"
            }

            VIEW_TYPE_ESTIMATOR_SUMMARY -> {
                val binding = (holder as EstimatorSummaryViewHolder).binding
                val item = items[position] as OutwardSummary
                binding.tvPartsTotal.text =
                    if (item.subTotalAmount != null) Utility.convertToCurrency(item.subTotalAmount!!)
                        .toString() else "-"
                binding.tvLabourTotal.text =
                    if (item.taxAmount != null) Utility.convertToCurrency(item.taxAmount!!)
                        .toString() else "-"
                binding.tvServicePkgTotal.visibility = View.VISIBLE
                binding.tvServicePackageTotal.visibility = View.VISIBLE
                binding.tvServicePackageTotal.text =
                    if (item.discountAmount != null) Utility.convertToCurrency(item.discountAmount!!)
                        .toString() else "-"
                binding.tvEstimatorGrandTotal.text =
                    if (item.totalAmount != null) Utility.convertToCurrency(item.totalAmount!!)
                        .toString() else "-"
            }


            VIEW_TYPE_SERVICE_PACKAGE -> {
                val binding = (holder as PackageViewHolder).binding
                val item = items[position] as OutwardItem
                if (itemsPendingRemoval.contains(item)) {
                    binding.layNewEstimator.clParentDelete.visibility = View.VISIBLE
                    binding.layPackageItem.llEditProformaParent.visibility = View.GONE
                    binding.layNewEstimator.tvDeleteUndo.setOnClickListener {
                        undoDelete(item)
                    }
                } else {
                    binding.layNewEstimator.clParentDelete.visibility = View.GONE
                    binding.layPackageItem.llEditProformaParent.visibility = View.VISIBLE
                    binding.layPackageItem.tvName.text = item.name
                    if (type == ARG_IS_FROM_ESTIMATE) {
                        binding.layPackageItem.tvPackageTax.visibility = View.GONE
                    } else {
                        binding.layPackageItem.tvPackageTax.visibility = View.VISIBLE
                        binding.layPackageItem.tvPackageTax.text =
                            "Tax: ${item.tax.cgst + item.tax.sgst}%"
                    }
                    var packageTotal = 0.0
                    if (item.rates?.isNotEmpty() == true) {
                        item.rates?.forEach { packageTotal += it.offerPrice.amount }
                    } else {
                        packageTotal = item.amount ?: 0.0
                    }
                    binding.layPackageItem.tvPackageTotal.text =
                        Utility.convertToCurrency(packageTotal)
                    Timber.d("rendering ${item.type}")

                    if (item.parts.isNotEmpty()) {
                        // Handle parts visibility and adapter setup
                    } else {
                        binding.layPackageItem.partParentView.visibility = View.GONE
                    }
                }
            }

            VIEW_TYPE_SPLIT_INVOICE -> {
                val binding = (holder as SplitInvoiceViewHolder).binding
                val splitItem = items[position] as OutwardItem
                if (splitItem.type != OutwardItem.TYPE_SERVICE_PKG) {
                    binding.tvItemName.text = splitItem.text
                    binding.tvSplitInvoiceTotal.text = Utility.convertToCurrency(splitItem.amount)
                    binding.etInsuranceAmt.setText("${splitItem.split?.cost ?: 0.0}")
                    binding.etCustomerAmt.setText(calculateCustomerAmount(splitItem))

                    splitItem.split?.let {
                        if (splitItem.split?.mode == Split.MODE_PERCENTAGE) {
                            binding.tvHsn.text =
                                binding.tvHsn.context.getString(R.string.split_by_percentage)
                        } else {
                            binding.tvHsn.text =
                                binding.tvHsn.context.getString(R.string.split_by_amount)
                        }
                    } ?: run {
                        if (splitItem.type == OutwardItem.TYPE_SPLIT_PART) {
                            binding.tvHsn.text =
                                binding.tvHsn.context.getString(R.string.split_by_percentage)
                        } else {
                            binding.tvHsn.text =
                                binding.tvHsn.context.getString(R.string.split_by_amount)
                            splitItem.split?.mode = Split.MODE_PRICE
                        }
                    }
                }
            }

            VIEW_TYPE_SPLIT_OTHER_CHARGES -> {
                val binding = (holder as SplitInvoiceOtherChargesViewHolder).binding
                val item = items[position] as OutwardItem
                binding.salvageView.setText(item.salvageValue.toString())
                binding.excessClauseView.setText(item.excessClauseValue.toString())
            }

            VIEW_TYPE_THIRD_PARTY_EDIT -> {
                val binding = (holder as ThirdPartyEditViewHolder).binding
                val thirdParty = (items[position] as OutwardActionItem).thirdParty
                binding.thirdPartyNameView.text = thirdParty?.name
                binding.thirdPartyGstView.text = thirdParty?.gstNumber
            }

            VIEW_TYPE_THIRD_PARTY_ADD -> {
            }

            VIEW_TYPE_SER_PKG_PART -> {
                val binding = (holder as ServicePkgPartViewHolder).binding
                val item = items[position] as OutwardItem
                bindPkgPart(item, binding)
            }

            VIEW_TYPE_SER_PKG_LABOUR -> {
                val binding = (holder as ServicePkgLabourViewHolder).binding
                val item = items[position] as OutwardItem
                bindPkgLabour(item, binding, position)
            }
        }
    }

    override fun onItemDeleted(position: Int, item: Any) {
        Handler().postDelayed(
            {
                if (item is OutwardItem && item.type == OutwardItem.TYPE_SERVICE_PKG) {
                    //write delete code for parts and labour
                    items.removeAll { it is OutwardItem && it.packageId == item.id }
                    Log.d("TAG", "onItemDeleted: ")

                }
                priceChangeListener.onPriceUpdate(NO_ITEM_TYPE, 1)
                notifyDataSetChanged()
            }, 500
        )
    }

    override fun onItemDeletedUndo(position: Int) {
        val item = items[position]
        if (item is OutwardItem) {
            priceChangeListener.onPriceUpdate(item.type, position)
        }
    }

    fun setInteraction(interaction: OutwardProcessInteraction) {
        this.interaction = interaction
    }

    inner class PartViewHolder(val binding: RowNewEstimatorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val deleteParentView: View = binding.layOutEstimator.clParentDelete
        val itemParentView: View = binding.layOutRowPro.estimateRowParentView
        val quantityParentView: View = binding.layOutRowPro.llQuantity
        val itemTitleView: TextView = binding.layOutRowPro.tvItemName
        val itemPartNumberView: TextView = binding.layOutRowPro.tvPartNumber
        val itemDiscTaxView: TextView = binding.layOutRowPro.tvDiscTax
        val itemQuantityUnitView: Spinner = binding.layOutRowPro.spinnerQuantityUnits
        val itemQuantityView: EditText = binding.layOutRowPro.etQuantity
        val itemTotalView: TextView = binding.layOutRowPro.tvItemTotal
        val itemRateView: EditText = binding.layOutRowPro.salvageView
        val undoView: TextView = binding.layOutEstimator.tvDeleteUndo
        val hsnView: TextView = binding.layOutRowPro.tvHsn
        val focView: TextView = binding.layOutRowPro.foc
        val middleView: LinearLayout = binding.layOutRowPro.llMiddleView
        val hsnLayout: LinearLayout = binding.layOutRowPro.llHsn
        private var isRateViewInitiated = false
        private var isQuantityViewInitiated = false
        val handler = Handler(Looper.getMainLooper())
        var rateRunnable: Runnable? = null
        var quantityRunnable: Runnable? = null

        init {
            @Suppress("DEPRECATION") if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                itemQuantityView.keyListener =
                    DigitsKeyListener.getInstance(Locale.ENGLISH, true, true)
                itemRateView.keyListener = DigitsKeyListener.getInstance(Locale.ENGLISH, true, true)
            } else {
                itemQuantityView.keyListener = DigitsKeyListener.getInstance(true, true)
                itemRateView.keyListener = DigitsKeyListener.getInstance(true, true)
            }

            itemRateView.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (isRateViewInitiated) {
                        if (rateRunnable != null) {
                            handler.removeCallbacks(rateRunnable!!)
                        }

                        rateRunnable = Runnable {
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                val item = items[adapterPosition] as OutwardItem
                                if (s?.isNotEmpty() == true && Utility.isValidDecimal(s.toString())) {
                                    if (item.price != s.toString().toDouble()) {
                                        if (item.discount.mode == Discount.MODE_PERCENTAGE && item.discount.amount > 100 || s.toString()
                                                .toFloat() < item.discount.amount
                                        ) {
                                            itemRateView.error =
                                                itemRateView.context.getString(R.string.discount_gt_price)
                                        } else {
                                            itemRateView.error = null
                                            item.price = Utility.round(s.toString().toDouble(), 1)
                                            priceChangeListener.onPriceUpdate(
                                                item.type, adapterPosition
                                            )
                                        }
                                    }
                                } else {
                                    if (s?.isNotEmpty() == true && !Utility.isValidDecimal(s.toString())) {
                                        itemRateView.error = "Invalid Price"
                                    } else if (s?.isEmpty() == true && item.price != 0.0) {
                                        item.price = 0.0
                                        item.amount = 0.0
                                        itemTotalView.text = 0.0.toString()
                                        priceChangeListener.onPriceUpdate(
                                            item.type, adapterPosition
                                        )
                                    }
                                }
                            }
                        }
                        handler.postDelayed(rateRunnable!!, PRICE_CHANGE_INTERVAL)
                    } else {
                        isRateViewInitiated = true
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })

            itemQuantityView.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (isQuantityViewInitiated) {
                        if (quantityRunnable != null) {
                            handler.removeCallbacks(quantityRunnable!!)
                        }

                        quantityRunnable = Runnable {
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                val item = items[adapterPosition] as OutwardItem
                                if (s?.isNotEmpty() == true && Utility.isValidDecimal(s.toString()) && s.toString()
                                        .toFloat() > 0.0f && item.quantity != s.toString().toFloat()
                                ) {
                                    item.quantity = s.toString().toFloat()
                                    priceChangeListener.onPriceUpdate(item.type, adapterPosition)
                                } else {
                                    if (s?.isNotEmpty() == true && !Utility.isValidDecimal(s.toString())) {
                                        itemQuantityView.error = "Invalid Quantity"
                                    } else if (s?.isEmpty() == true || s.toString()
                                            .toFloat() <= 0.0f
                                    ) {
                                        item.quantity = 1.0f
                                        itemQuantityView.setText("1.0")
                                        priceChangeListener.onPriceUpdate(
                                            item.type, adapterPosition
                                        )
                                        itemQuantityView.context.toast("Quantity cannot be 0")
                                    }
                                }
                            }
                        }
                        handler.postDelayed(quantityRunnable!!, PRICE_CHANGE_INTERVAL)
                    } else {
                        isQuantityViewInitiated = true
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })

            if (!viewOnly) {
                if (type == ARG_IS_FROM_PROFROMA) {
                    itemParentView.setOnClickListener {
                        if (adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener

                        startAddPartActivity(itemParentView.context, adapterPosition)
                    }
                } else {
                    itemParentView.setOnClickListener {
                        if (adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener

                        val item = items[adapterPosition] as OutwardItem
                        if (item.type == OutwardItem.TYPE_PART && SharedPrefHelper.isPartFinderEnabled()) {
                            (it.context as? OutwardingProcessActivity)?.getPartNumbers(
                                adapterPosition
                            )
                        }
                    }
                }
            }

            hsnLayout.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION && items[adapterPosition] is OutwardItem) {
                    val estimatorItem = items[adapterPosition] as OutwardItem
                    //If logged in workshop is composite enabled then show composite HSN list

                    if (estimatorItem.tax.hsn == null || estimatorItem.tax.hsn.equals(HSN.SELECT) || SharedPrefHelper.isHsnPartEnabled()) {

                        SearchableDialog.Builder(hsnLayout.context, hsnList!!).apply {
                            setTitle("HSN CODE")
                            setSelectedItem(hsnList.find { it.hsn == estimatorItem.tax.hsn })
                            setItemSelectedListener(object :
                                SearchableDialog.OnSearchItemSelectedListener<HSN> {
                                override fun onItemSelectedItem(item: HSN?) {
                                    Timber.d("hsn ${item?.hsn}")
                                    estimatorItem.tax.apply {
                                        hsn = item?.hsn
                                        cgst = item?.cgst ?: 0.0
                                        sgst = item?.sgst ?: 0.0
                                    }
                                    notifyItemChanged(adapterPosition)
                                    priceChangeListener.onPriceUpdate(
                                        estimatorItem.type, adapterPosition
                                    )
                                }

                                override fun onCancel() {
                                }
                            })
                        }.show()
                    }
                }
            }

            itemQuantityUnitView.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }

                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        (items[adapterPosition] as OutwardItem).unit =
                            (items[adapterPosition] as OutwardItem).units?.get(p2)
                                ?: Constants.BusinessConstants.UNITS_LIST[p2]
                    }
                }
        }
    }


    inner class PartViewOneHolder(val binding: NewRowProformaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val itemParentView: View = binding.estimateRowParentView
        val itemQuantityUnitView: Spinner = binding.spinnerQuantityUnits
        val itemQuantityView: EditText = binding.etQuantity
        val itemTotalView: TextView = binding.tvItemTotal
        val itemRateView: EditText = binding.salvageView
        val hsnLayout: LinearLayout = binding.llHsn
        private var isRateViewInitiated = false
        private var isQuantityViewInitiated = false
        val handler = Handler(Looper.getMainLooper())
        var rateRunnable: Runnable? = null
        var quantityRunnable: Runnable? = null

        init {
            @Suppress("DEPRECATION") if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                itemQuantityView.keyListener =
                    DigitsKeyListener.getInstance(Locale.ENGLISH, true, true)
                itemRateView.keyListener = DigitsKeyListener.getInstance(Locale.ENGLISH, true, true)
            } else {
                itemQuantityView.keyListener = DigitsKeyListener.getInstance(true, true)
                itemRateView.keyListener = DigitsKeyListener.getInstance(true, true)
            }

            itemRateView.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (isRateViewInitiated) {
                        if (rateRunnable != null) {
                            handler.removeCallbacks(rateRunnable!!)
                        }

                        rateRunnable = Runnable {
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                val item = items[adapterPosition] as OutwardItem
                                if (s?.isNotEmpty() == true && Utility.isValidDecimal(s.toString())) {
                                    if (item.price != s.toString().toDouble()) {
                                        if (item.discount.mode == Discount.MODE_PERCENTAGE && item.discount.amount > 100 || s.toString()
                                                .toFloat() < item.discount.amount
                                        ) {
                                            itemRateView.error =
                                                itemRateView.context.getString(R.string.discount_gt_price)
                                        } else {
                                            itemRateView.error = null
                                            item.price = Utility.round(s.toString().toDouble(), 1)
                                            priceChangeListener.onPriceUpdate(
                                                item.type, adapterPosition
                                            )
                                        }
                                    }
                                } else {
                                    if (s?.isNotEmpty() == true && !Utility.isValidDecimal(s.toString())) {
                                        itemRateView.error = "Invalid Price"
                                    } else if (s?.isEmpty() == true && item.price != 0.0) {
                                        item.price = 0.0
                                        item.amount = 0.0
                                        itemTotalView.text = 0.0.toString()
                                        priceChangeListener.onPriceUpdate(
                                            item.type, adapterPosition
                                        )
                                    }
                                }
                            }
                        }
                        handler.postDelayed(rateRunnable!!, PRICE_CHANGE_INTERVAL)
                    } else {
                        isRateViewInitiated = true
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })

            itemQuantityView.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (isQuantityViewInitiated) {
                        if (quantityRunnable != null) {
                            handler.removeCallbacks(quantityRunnable!!)
                        }

                        quantityRunnable = Runnable {
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                val item = items[adapterPosition] as OutwardItem
                                if (s?.isNotEmpty() == true && Utility.isValidDecimal(s.toString()) && s.toString()
                                        .toFloat() > 0.0f && item.quantity != s.toString().toFloat()
                                ) {
                                    item.quantity = s.toString().toFloat()
                                    priceChangeListener.onPriceUpdate(item.type, adapterPosition)
                                } else {
                                    if (s?.isNotEmpty() == true && !Utility.isValidDecimal(s.toString())) {
                                        itemQuantityView.error = "Invalid Quantity"
                                    } else if (s?.isEmpty() == true || s.toString()
                                            .toFloat() <= 0.0f
                                    ) {
                                        item.quantity = 1.0f
                                        itemQuantityView.setText("1.0")
                                        priceChangeListener.onPriceUpdate(
                                            item.type, adapterPosition
                                        )
                                        itemQuantityView.context.toast("Quantity cannot be 0")
                                    }
                                }
                            }
                        }
                        handler.postDelayed(quantityRunnable!!, PRICE_CHANGE_INTERVAL)
                    } else {
                        isQuantityViewInitiated = true
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })

            if (!viewOnly) {
                if (type == ARG_IS_FROM_PROFROMA) {
                    itemParentView.setOnClickListener {
                        if (adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener

                        startAddPartActivity(itemParentView.context, adapterPosition)
                    }
                } else {
                    itemParentView.setOnClickListener {
                        if (adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener

                        val item = items[adapterPosition] as OutwardItem
                        if (item.type == OutwardItem.TYPE_PART && SharedPrefHelper.isPartFinderEnabled()) {
                            (it.context as? OutwardingProcessActivity)?.getPartNumbers(
                                adapterPosition
                            )
                        }
                    }
                }
            }

            hsnLayout.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION && items[adapterPosition] is OutwardItem) {
                    val estimatorItem = items[adapterPosition] as OutwardItem
                    //If logged in workshop is composite enabled then show composite HSN list

                    if (estimatorItem.tax.hsn == null || estimatorItem.tax.hsn.equals(HSN.SELECT) || SharedPrefHelper.isHsnPartEnabled()) {

                        SearchableDialog.Builder(hsnLayout.context, hsnList!!).apply {
                            setTitle("HSN CODE")
                            setSelectedItem(hsnList.find { it.hsn == estimatorItem.tax.hsn })
                            setItemSelectedListener(object :
                                SearchableDialog.OnSearchItemSelectedListener<HSN> {
                                override fun onItemSelectedItem(item: HSN?) {
                                    Timber.d("hsn ${item?.hsn}")
                                    estimatorItem.tax.apply {
                                        hsn = item?.hsn
                                        cgst = item?.cgst ?: 0.0
                                        sgst = item?.sgst ?: 0.0
                                    }
                                    notifyItemChanged(adapterPosition)
                                    priceChangeListener.onPriceUpdate(
                                        estimatorItem.type, adapterPosition
                                    )
                                }

                                override fun onCancel() {
                                }
                            })
                        }.show()
                    }
                }
            }

            itemQuantityUnitView.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }

                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        (items[adapterPosition] as OutwardItem).unit =
                            (items[adapterPosition] as OutwardItem).units?.get(p2)
                                ?: Constants.BusinessConstants.UNITS_LIST[p2]
                    }
                }
        }
    }

    inner class ServicePkgLabourViewHolder(val binding: NewRowPkgPartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val itemTitleView: TextView = binding.tvItemName
        val itemPartNumberView: TextView = binding.tvPartNumber
        val priceView: TextView = binding.partNotAssignedTV

        init {
            // Set visibility of views
            itemPartNumberView.visibility = View.GONE
            priceView.visibility = View.GONE
        }
    }

    inner class LabourViewHolder(val binding: RowLabourProformaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var isRateViewInitiated: Boolean = false
        private val handler = Handler(Looper.getMainLooper())
        private var rateRunnable: Runnable? = null

        init {
            binding.vendorNameTV.setOnClickListener {
                //show vendor selection dialog for current item
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    items[adapterPosition].let {
                        it as OutwardItem
                        if (type == ARG_IS_FROM_ESTIMATE) priceChangeListener.onVendorSelectionClick(
                            it.type, adapterPosition
                        )
                    }
                }
            }
            binding.estimateRowParentView.setOnClickListener {
                if (adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener

                if (type == ARG_IS_FROM_PROFROMA) {
                    startAddLabourActivity(it.context, adapterPosition)
                }
            }

            binding.labourRateAmountView.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    //calculate the labour rate, amount,
                    if (isRateViewInitiated) {
                        if (rateRunnable != null) {
                            handler.removeCallbacks(rateRunnable!!)
                        }

                        rateRunnable = Runnable {
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                items[adapterPosition].let {
                                    it as OutwardItem
                                    if (s?.isNotEmpty() == true && Utility.isValidDecimal(s.toString())) {
                                        val finalRate = Utility.round(s.toString().toDouble(), 1)
                                        if (it.finalRate != finalRate) {
                                            if (it.discount.mode == Discount.MODE_PERCENTAGE && it.discount.amount > 100) {
                                                binding.labourRateAmountView.error =
                                                    binding.labourRateAmountView.context.getString(R.string.discount_gt_price)
                                            } else if (it.rate > 0 && finalRate < 1.0) {
                                                binding.labourRateAmountView.error =
                                                    "Final Rate can not be empty or 0"
                                            } else {
                                                if ((jcType != JobCard.TYPE_ACCIDENTAL)) {
                                                    //VALIDATION 1
                                                    //if all 3 config are false then final rate and rate should be same
                                                    if (SharedPrefHelper.getIsLabourRateEditable()
                                                            .not() && SharedPrefHelper.isLabourSurchargeEnabled()
                                                            .not() && SharedPrefHelper.isLabourReductionEnabled()
                                                            .not()
                                                    ) {
                                                        binding.labourRateAmountView.error = null
                                                        it.finalRate = finalRate
                                                        it.rate = finalRate + 0.0
                                                        priceChangeListener.onPriceUpdate(
                                                            it.type, adapterPosition
                                                        )
                                                    }

                                                    //VALIDATION 2
                                                    //if surcharge is true and rate is 0 then rate=final rate only once
                                                    //final rate can be changed multiple times
                                                    else if (SharedPrefHelper.getIsLabourRateEditable()
                                                            .not() && SharedPrefHelper.isLabourSurchargeEnabled() && SharedPrefHelper.isLabourReductionEnabled()
                                                            .not()
                                                    ) {
                                                        it.finalRate = finalRate
                                                        binding.labourRateAmountView.error = null
                                                        priceChangeListener.onPriceUpdate(
                                                            it.type, adapterPosition
                                                        )

                                                    }

                                                    //VALIDATION 3
                                                    //if labour rate editable is true then rate will not change but it should calculate properly there should not be any restriction in case of accidental JC
                                                    else if (SharedPrefHelper.getIsLabourRateEditable() && SharedPrefHelper.isLabourSurchargeEnabled()
                                                            .not() && SharedPrefHelper.isLabourReductionEnabled()
                                                            .not()
                                                    ) {
                                                        binding.labourRateAmountView.error = null
                                                        it.finalRate = finalRate
                                                        it.rate = finalRate + 0.0
                                                        priceChangeListener.onPriceUpdate(
                                                            it.type, adapterPosition
                                                        )
                                                    }

                                                    //VALIDATION 4
                                                    //if rate is 0 then update it to final rate and calculate surcharge and
                                                    else if (SharedPrefHelper.getIsLabourRateEditable()
                                                            .not() && SharedPrefHelper.isLabourSurchargeEnabled() && SharedPrefHelper.isLabourReductionEnabled()
                                                    ) {
                                                        binding.labourRateAmountView.error = null
                                                        it.finalRate = finalRate
                                                        if (it.rate == 0.0) {
                                                            it.rate = finalRate
                                                        }
                                                        priceChangeListener.onPriceUpdate(
                                                            it.type, adapterPosition
                                                        )
                                                    }

                                                    //VALIDATION 5
                                                    else if (SharedPrefHelper.getIsLabourRateEditable() && SharedPrefHelper.isLabourSurchargeEnabled() && SharedPrefHelper.isLabourReductionEnabled()
                                                            .not()
                                                    ) {
                                                        binding.labourRateAmountView.error = null
                                                        it.finalRate = finalRate
                                                        if (it.rate == 0.0) {
                                                            it.rate = finalRate
                                                        }
                                                        priceChangeListener.onPriceUpdate(
                                                            it.type, adapterPosition
                                                        )
                                                    }

                                                    //NO VALIDATION
                                                    else {
                                                        binding.labourRateAmountView.error = null
                                                        it.finalRate = finalRate

                                                        priceChangeListener.onPriceUpdate(
                                                            it.type, adapterPosition
                                                        )
                                                    }
                                                } else {
                                                    if (SharedPrefHelper.getIsLabourRateEditable() && SharedPrefHelper.isLabourSurchargeEnabled()
                                                            .not() && SharedPrefHelper.isLabourReductionEnabled()
                                                            .not()
                                                    ) {
                                                        binding.labourRateAmountView.error = null
                                                        it.finalRate = finalRate
                                                        it.rate = finalRate + 0.0
                                                        priceChangeListener.onPriceUpdate(
                                                            it.type, adapterPosition
                                                        )
                                                    } else {
                                                        binding.labourRateAmountView.error = null
                                                        it.finalRate = finalRate
                                                        priceChangeListener.onPriceUpdate(
                                                            it.type, adapterPosition
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        if (s?.isEmpty() == true && it.rate > 0) {
                                            binding.labourRateAmountView.error =
                                                "Final Rate can not be empty or 0"
                                        } else if (s?.isNotEmpty() == true && !Utility.isValidDecimal(
                                                s.toString()
                                            )
                                        ) {
                                            binding.labourRateAmountView.error = "Invalid Price"
                                        } else if (s?.isEmpty() == true && it.finalRate != 0.0) {
                                            it.amount = 0.0
                                            binding.labourTotalView.text = 0.0.toString()
                                            priceChangeListener.onPriceUpdate(
                                                it.type, adapterPosition
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        handler.postDelayed(rateRunnable!!, PRICE_CHANGE_INTERVAL)
                    } else {
                        isRateViewInitiated = true
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) = Unit

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) =
                    Unit
            })
        }
    }


    inner class EstimatorSummaryViewHolder(val binding: LayoutEstimatorBottomBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val partsTotalView: TextView = binding.tvPartsTotal
        val labourTotalView: TextView = binding.tvLabourTotal
        val servicePkgTotalView: TextView = binding.tvServicePkgTotal
        val servicePkgTitleView: TextView = binding.tvServicePackageTotal
        val totalView: TextView = binding.tvEstimatorGrandTotal
    }


    inner class PackageViewHolder(val binding: RowPackagesProformaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val partParentView: LinearLayout = binding.layPackageItem.partParentView
        val partsView: RecyclerView = binding.layPackageItem.llPackagesParts
        val packageNameView: TextView = binding.layPackageItem.tvName
        val packageRateView: TextView = binding.layPackageItem.tvPackageTotal
        val packageTaxView: TextView = binding.layPackageItem.tvPackageTax
        val deleteParentView: View = binding.layNewEstimator.clParentDelete
        val undoView: TextView = binding.layNewEstimator.tvDeleteUndo
        val itemParentView: View = binding.layPackageItem.llEditProformaParent
    }


    inner class SplitInvoiceViewHolder(val binding: RowSplitInvoiceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        internal val itemTitleView: TextView = binding.tvItemName
        internal val itemTotalView: TextView = binding.tvSplitInvoiceTotal
        internal val insuranceAmtView: EditText = binding.etInsuranceAmt
        internal val customerAmtView: EditText = binding.etCustomerAmt
        internal val hsnView: TextView = binding.tvHsn
        private val hsnParentView: LinearLayout = binding.llHsn
        private var isRateViewInitiated = false
        val handler = Handler(Looper.getMainLooper())
        var rateRunnable: Runnable? = null

        init {
            insuranceAmtView.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (isRateViewInitiated) {
                        if (rateRunnable != null) {
                            handler.removeCallbacks(rateRunnable!!)
                        }
                        rateRunnable = Runnable {
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                val item = items[adapterPosition] as OutwardItem
                                if (!s.isNullOrEmpty() && checkIfValid(s.toString(), item)) {
                                    item.split?.let {
                                        if (it.cost != s.toString().toDouble()) {
                                            it.cost = s.toString().toDouble()
                                            customerAmtView.setText(calculateCustomerAmount(item))
                                            priceChangeListener.onPriceUpdate(
                                                NO_ITEM_TYPE, adapterPosition
                                            )
                                        }
                                    } ?: run {
                                        item.split = Split()
                                        if (item.type == OutwardItem.TYPE_SPLIT_LABOUR) item.split?.mode =
                                            Split.MODE_PRICE
                                        item.split!!.cost = s.toString().toDouble()
                                        customerAmtView.setText(calculateCustomerAmount(item))
                                        priceChangeListener.onPriceUpdate(
                                            NO_ITEM_TYPE, adapterPosition
                                        )
                                    }
                                } else {
                                    if (s.isNullOrEmpty()) {
                                        item.split?.cost = 0.0
                                        customerAmtView.setText(calculateCustomerAmount(item))
                                        priceChangeListener.onPriceUpdate(
                                            item.type, adapterPosition
                                        )
                                    } else {
                                        insuranceAmtView.context.toast("Invalid value")
                                    }
                                }
                            }
                        }

                        handler.postDelayed(rateRunnable!!, PRICE_CHANGE_INTERVAL)
                    } else {
                        isRateViewInitiated = true
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
            hsnParentView.setOnClickListener {
                val context = hsnParentView.context
                context.selector(
                    null, listOf(
                        context.getString(R.string.split_by_percentage),
                        context.getString(R.string.split_by_amount)
                    )
                ) { _, i ->
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        val item = items[adapterPosition] as OutwardItem
                        if (i == 0) {
                            resetSplitItem(Split.MODE_PERCENTAGE, item)
                        } else {
                            resetSplitItem(Split.MODE_PRICE, item)
                        }
                        notifyItemChanged(adapterPosition)
                        priceChangeListener.onPriceUpdate(item.type, adapterPosition)
                    }
                }
            }
        }
    }

    fun calculateCustomerAmount(item: OutwardItem): String {
        val split = item.split
        split?.let {
            if (split.mode == Split.MODE_PERCENTAGE) {
                split.customerPay.rate = (100 - split.cost)
            } else {
                split.customerPay.rate = (item.amount!! - item.split?.cost!!)
            }
        }
        // split is null when isSplitInvoice but values are not filled yet.
        // In that case full amount is assumed to be paid by customer. hence returning item amount
        return Utility.round(
            split?.customerPay?.rate
                ?: if (item.type == OutwardItem.TYPE_SPLIT_LABOUR) item.amount!! else 100.0, 1
        ).toString()
    }

    private fun checkIfValid(price: String, item: OutwardItem): Boolean {
        item.split?.let {
            if (it.mode == Split.MODE_PERCENTAGE && Utility.isValidDecimal(price) && (price.toDouble() <= 100) && price.toDouble() >= 0) {
                return true
            }

            if (it.mode == Split.MODE_PRICE && Utility.isValidDecimal(price) && (price.toDouble() <= item.amount!!) && price.toDouble() >= 0) {
                return true
            }
        } ?: run {
            if (item.type == OutwardItem.TYPE_SPLIT_PART && Utility.isValidDecimal(price) && (price.toDouble() <= 100) && price.toDouble() >= 0) return true

            if (item.type == OutwardItem.TYPE_SPLIT_LABOUR && Utility.isValidDecimal(price) && (price.toDouble() <= item.amount!!) && price.toDouble() >= 0) return true
        }
        return false
    }

    private fun resetSplitItem(mode: String, item: OutwardItem) {
        if (item.split == null) {
            item.split = Split()
        }

        val split = item.split!!
        if (mode == Split.MODE_PERCENTAGE) {
            split.mode = Split.MODE_PERCENTAGE
            split.customerPay.rate = 100.0
            split.cost = 0.0
        } else {
            split.mode = Split.MODE_PRICE
            split.customerPay.rate = item.amount ?: 0.0
            split.cost = 0.0
        }
    }

    inner class SplitInvoiceOtherChargesViewHolder(val binding: RowSplitOtherChargesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val salvageView: EditText = binding.salvageView
        val excessClauseView: EditText = binding.excessClauseView
        private var salvageRunnableInitiated = false
        private var excessRunnableInitiated = false
        private var salvageRunnable: Runnable? = null
        private var excessSalvageRunnable: Runnable? = null
        private val handler = Handler(Looper.getMainLooper())

        init {
            salvageView.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (salvageRunnableInitiated) {
                        salvageRunnable?.let { handler.removeCallbacks(it) }

                        salvageRunnable = Runnable {
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                val item = items[adapterPosition] as OutwardItem
                                if (s?.isNotEmpty() == true && Utility.isValidDecimal(s.toString())) {
                                    item.salvageValue = s.toString().toDouble()
                                } else {
                                    if (s?.isNotEmpty() == true && !Utility.isValidDecimal(s.toString())) {
                                        salvageView.error = "Invalid amount"
                                    }
                                }
                            }
                        }

                        salvageRunnable?.let { handler.postDelayed(it, PRICE_CHANGE_INTERVAL) }
                    } else {
                        salvageRunnableInitiated = true
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) = Unit

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) =
                    Unit
            })

            excessClauseView.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (excessRunnableInitiated) {
                        excessSalvageRunnable?.let { handler.removeCallbacks(it) }

                        excessSalvageRunnable = Runnable {
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                val item = items[adapterPosition] as OutwardItem
                                if (s?.isNotEmpty() == true && Utility.isValidDecimal(s.toString())) {
                                    item.excessClauseValue = s.toString().toDouble()
                                } else {
                                    if (s?.isNotEmpty() == true && !Utility.isValidDecimal(s.toString())) {
                                        excessClauseView.error = "Invalid amount"
                                    }
                                }
                            }
                        }

                        excessSalvageRunnable?.let {
                            handler.postDelayed(
                                it, PRICE_CHANGE_INTERVAL
                            )
                        }
                    } else {
                        excessRunnableInitiated = true
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) = Unit

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) =
                    Unit
            })
        }
    }


    inner class ThirdPartyAddViewHolder(private val binding: RowProformaThirdPartyAddBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.addThirdPartyBtn.setOnClickListener {
                startThirdPartyActivity(it.context, adapterPosition)
            }
        }
    }


    inner class ThirdPartyEditViewHolder(val binding: RowProformaThirdPartyEditBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.thirdPartyEditDetailsBtn.setOnClickListener {
                startThirdPartyActivity(it.context, adapterPosition)
            }

            binding.thirdPartyRemoveBtn.setOnClickListener {
                val item = items[adapterPosition] as? OutwardActionItem
                item?.thirdParty?.let { thirdParty ->
                    interaction?.removeThirdParty(invoiceId!!, thirdParty)
                }
            }
        }
    }


    private fun startAddPartActivity(context: Context, position: Int) {
        Timber.d((context as OutwardingProcessActivity).isSplitInvoice.toString())
        (context as Activity).startActivityForResult(
            AddEditPartActivity.getIntent(
                context,
                (items[position] as OutwardItem).uid != null,
                OutwardStepMapper.outwardStepToPart(items[position] as OutwardItem),
                jobCardId,
                invoiceId,
                (context).isSplitInvoice.not(),
                vehicleType,
                isAmcApplied
            ), OutwardingProcessActivity.REQUEST_CODE_ADD_PART
        )
    }

    private fun startAddLabourActivity(context: Context, position: Int) {
        Timber.d((context as OutwardingProcessActivity).isSplitInvoice.toString())
        (context as Activity).startActivityForResult(
            AddLabourActivity.getIntent(
                context,
                (context).isSplitInvoice,
                (items[position] as OutwardItem).uid != null,
                invoiceId!!,
                OutwardStepMapper.outwardStepToLabour(items[position] as OutwardItem),
                isAmcApplied,
                jcType
            ), OutwardingProcessActivity.REQUEST_CODE_ADD_LABOUR
        )
    }

    private fun startThirdPartyActivity(context: Context, position: Int) {
        val thirdParty = (items[position] as OutwardActionItem).thirdParty
        (context as Activity).startActivityForResult(
            ThirdPartyDetailsActivity.getIntent(
                context, invoiceId!!, thirdParty
            ), OutwardingProcessActivity.REQUEST_CODE_THIRD_PARTY
        )
    }

    private fun getPresentableStockValue(stock: Double): String {
        if (stock == 0.0) return ""
        val stockInt = stock.toInt()
        val stockDec = stock - stockInt
        val finalValue = if (stockDec <= 0.0) {
            stockInt.toString()
        } else {
            Utility.round(stock, 1).toString()
        }
        return "- ($finalValue)"
    }

    private fun bindPart(item: OutwardItem, binding: RowNewEstimatorBinding, position: Int) {
        val deleteParentView: View = binding.layOutEstimator.clParentDelete
        val itemParentView: View = binding.layOutRowPro.estimateRowParentView
        val quantityParentView: View = binding.layOutRowPro.llQuantity
        val itemTitleView: TextView = binding.layOutRowPro.tvItemName
        val itemPartNumberView: TextView = binding.layOutRowPro.tvPartNumber
        val itemDiscTaxView: TextView = binding.layOutRowPro.tvDiscTax
        val itemQuantityUnitView: Spinner = binding.layOutRowPro.spinnerQuantityUnits
        val itemQuantityView: EditText = binding.layOutRowPro.etQuantity
        val itemTotalView: TextView = binding.layOutRowPro.tvItemTotal
        val itemRateView: EditText = binding.layOutRowPro.salvageView
        val undoView: TextView = binding.layOutEstimator.tvDeleteUndo
        val hsnView: TextView = binding.layOutRowPro.tvHsn
        val focView: TextView = binding.layOutRowPro.foc
        val middleView: LinearLayout = binding.layOutRowPro.llMiddleView
        val hsnLayout: LinearLayout = binding.layOutRowPro.llHsn


        Timber.d("rendering ${item.type}")
        itemRateView.isEnabled = !viewOnly
        itemQuantityView.isEnabled = !viewOnly
        itemTitleView.text = item.text
        when (type) {
            ARG_IS_FROM_ESTIMATE -> {
                hsnLayout.visibility = View.GONE
                itemDiscTaxView.visibility = View.GONE
                if (item.partNumber != null) {
                    middleView.visibility = View.VISIBLE
                    itemPartNumberView.visibility = View.VISIBLE
                    itemDiscTaxView.visibility = View.GONE
                    itemPartNumberView.text = "${item.brand.name} - ${item.partNumber}"
                } else {
                    middleView.visibility = View.GONE
                }
            }

            else -> {
                if (SharedPrefHelper.isGstEnabled()) {
                    hsnLayout.visibility = View.VISIBLE
                    if (item.tax.hsn != null && item.tax.hsn!!.isNotEmpty()) {
                        hsnView.text = if (item.tax.hsn.equals(HSN.SELECT)) {
                            HSN.HSN_CODE
                        } else {
                            item.tax.hsn
                        }
                    } else {
                        hsnView.text = HSN.HSN_CODE
                    }
                    if (item.tax.hsn != null && !item.tax.hsn.equals(HSN.SELECT)) {
                        hsnLayout.isEnabled = SharedPrefHelper.isHsnPartEnabled()
                    }
                } else {
                    hsnLayout.visibility = View.GONE
                }
                itemDiscTaxView.visibility = View.VISIBLE
                itemDiscTaxView.setTextColor(
                    ContextCompat.getColor(
                        itemDiscTaxView.context, R.color.black
                    )
                )
                itemDiscTaxView.text = String.format(
                    if (item.discount.mode == Discount.MODE_PRICE) DISC_TAX_RS else DISC_TAX_PERCENT,
                    item.discount.amount,
                    if (item.tax.hsn != null && SharedPrefHelper.isGstEnabled()) item.tax.sgst + item.tax.cgst else 0.0
                )

                if (item.isFOC) {
                    focView.visibility = View.VISIBLE
                    itemParentView.background = ContextCompat.getDrawable(
                        itemParentView.context, R.drawable.row_foc_border
                    )
                }
            }
        }
        if (item.partNumber.isNullOrEmpty()) {
            itemPartNumberView.visibility = View.GONE
        } else {
            itemPartNumberView.visibility = View.VISIBLE
            itemPartNumberView.text = "${item.brand.name} - ${item.partNumber} ${
                if (!viewOnly) getPresentableStockValue(item.stock) else ""
            }"
            if (SharedPrefHelper.isInventoryEnabled() && !viewOnly) {
                itemPartNumberView.setTextColor(
                    if (item.stock > 0) ContextCompat.getColor(
                        itemPartNumberView.context, R.color.forest_green
                    )
                    else ContextCompat.getColor(itemDiscTaxView.context, R.color.red)
                )
            } else {
                itemPartNumberView.setTextColor(
                    ContextCompat.getColor(
                        itemDiscTaxView.context, R.color.black
                    )
                )
            }
        }
        itemRateView.isEnabled =
            ((SharedPrefHelper.isPartPriceEditable().not() && item.price > 0.0) || viewOnly).not()
        quantityParentView.visibility = View.VISIBLE
        itemQuantityView.setText(item.quantity.toString())

        val unitsAdapter = ArrayAdapter(
            itemQuantityUnitView.context,
            R.layout.simple_units_picker_item,
            item.units ?: Constants.BusinessConstants.UNITS_LIST
        )
        unitsAdapter.setDropDownViewResource(R.layout.simple_hsn_spinner_item1)
        itemQuantityUnitView.adapter = unitsAdapter
        itemQuantityUnitView.prompt = "Select Units"
        val index: Int = item.units?.indexOf(item.unit)
            ?: Constants.BusinessConstants.UNITS_LIST.indexOf(item.unit)
        itemQuantityUnitView.setSelection(index)

        itemTotalView.text =
            if (item.amount != null) Utility.convertToCurrency(item.amount!!) else "-"
        itemRateView.setText(
            if (item.price > 0.0) Utility.round(item.price, 1).toString() else ""
        )
    }


    private fun bindLabour(item: OutwardItem, view: RowLabourProformaBinding) {
        /************************[ASSIGN PLAIN VALUES]*****************************
         *
         */
        if (SharedPrefHelper.isOSLWorkOrder() and item.labourType.equals(Labour.TYPE_OSL) and (item.vendor != null)) {
            view.vendorNameTV.visibility = View.VISIBLE
        }
        //--------------------------------------------------------------------------------

        /************************[DISCOUNT_AND_TAX_VISIBILITY]*****************************
         * If it is from proforma then only we have show discount and tax
         * By default the view is not visible
         * We have used regex pattern to show 2 values on single view */
        if (type == ARG_IS_FROM_PROFROMA) {
            view.labourdiscountTaxView.visibility = View.VISIBLE
            view.labourdiscountTaxView.text = String.format(
                if (item.discount.mode == Discount.MODE_PRICE) DISC_TAX_RS
                else DISC_TAX_PERCENT,
                item.discount.amount,
                if (item.tax.sac != null && SharedPrefHelper.isGstEnabled()) item.tax.sgst + item.tax.cgst else 0.0
            )

        } else {
            view.labourdiscountTaxView.visibility = View.GONE
            view.vendorNameTV.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0, 0, R.drawable.ic_edit_12, 0
            )
            view.vendorNameTV.text = item.vendor?.name
        }
        //--------------------------------------------------------------------------------

        view.labourItemName.text = item.text

        /*******************************[RATE_AMOUNT_EDIT]******************************
         * # Rate Amount change enabled
         * 1. check for labour rate editable
         * 2. OR check if it is misc labour and misc is enabled
         * 3. price should be greater than 0 for point number 1 and 2
         * 4. And this screen is not opened for view only */
        //Conditional visibility
        //For Periodic/Major/Minor/AMC-SMC JC do not show "Reduction" field on Estimate and Proforma screen
        if (jcType != JobCard.TYPE_ACCIDENTAL) {
            view.labourReductionAmountView.visibility = View.GONE
            view.labourReductionLabel.visibility = View.GONE
        }

        //if surcharge config is disabled hide the field
        if (SharedPrefHelper.isLabourSurchargeEnabled().not()) {
            view.labourSurchargeAmountView.visibility = View.GONE
            view.labourSurchargeLabel.visibility = View.GONE
        }
        //if reduction config is disabled hide the field
        if (SharedPrefHelper.isLabourReductionEnabled().not()) {
            view.labourReductionAmountView.visibility = View.GONE
            view.labourReductionLabel.visibility = View.GONE
        }

        val rate = "RATE :" + Utility.round(item.rate, 1).toString()
        view.rateTextView.text = rate

        view.labourRateAmountView.setText(
            if (item.finalRate > 0.0) {
                Utility.round(item.finalRate, 1).toString()
            } else 0.0.toString()
        )

        //--------------------------------------------------------------------------------
        if (item.finalRate > item.rate) {
            //surcharge
            item.surcharge = item.finalRate - item.rate
            item.reduction = 0.0
        } else {
            //reduction
            item.reduction = item.rate - item.finalRate
            item.surcharge = 0.0
        }

//        item.amount=item.price
        view.labourTotalView.text =
            if (item.amount != null) Utility.convertToCurrency(item.amount!!) else "-"
        view.labourSurchargeAmountView.text = item.surcharge.toString()
        view.labourReductionAmountView.text = item.reduction.toString()
        //final rate should not be editable if is disable from config
        view.labourRateAmountView.isEnabled = ((SharedPrefHelper.getIsLabourRateEditable()
            .not() && SharedPrefHelper.isLabourSurchargeEnabled()
            .not() && item.finalRate > 0.0) || viewOnly).not()
        if (item.isFOC) {
            view.foc.visibility = View.VISIBLE
            view.estimateRowParentView.background =
                ContextCompat.getDrawable(view.root.context, R.drawable.row_foc_border)
        }
    }

    inner class ServicePkgPartViewHolder(val binding: NewRowPkgPartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val itemTitleView: TextView = binding.tvItemName
        val itemParentView: View = binding.estimateRowParentView
        val itemPartNumberView: TextView = binding.tvPartNumber
        val itemPartNotAssigned: TextView = binding.partNotAssignedTV

        init {
            if (viewOnly.not()) {
                itemParentView.setOnClickListener {
                    val item = items[adapterPosition] as OutwardItem

                    if (item.partNumbers!!.isNotEmpty()) {
                        // Open part finder
                        (it.context as? OutwardingProcessActivity)?.getPartNumbers(adapterPosition)
                    } else {
                        // Open search part
                        interaction?.showPartSelector(adapterPosition, item)
                    }
                }
            }
        }
    }


    private fun bindPkgPart(item: OutwardItem, binding: NewRowPkgPartBinding) {
        val itemTitleView: TextView = binding.tvItemName
        val itemParentView: View = binding.estimateRowParentView
        val itemPartNumberView: TextView = binding.tvPartNumber
        val itemPartNotAssigned: TextView = binding.partNotAssignedTV

        Timber.d("rendering ${item.type}")
        itemTitleView.text = item.text
        if (item.partNumber.isNullOrEmpty().not()) {
            //set the part which is available
            val text = "${item.brand.name} - ${item.partNumber} ${
                if (viewOnly.not()) getPresentableStockValue(item.stock) else ""
            }"
            itemPartNumberView.text = text
            itemPartNotAssigned.visibility = View.GONE

            itemPartNumberView.textColor = if (item.stock > 0) ContextCompat.getColor(
                itemPartNumberView.context, R.color.forest_green
            )
            else ContextCompat.getColor(itemPartNumberView.context, R.color.red)
        } else if (item.partNumbers != null && item.partNumbers!!.isNotEmpty()) {
            //it means we have multiple part number for single part -> open part selector
            itemPartNumberView.text = "NOT ALLOTTED"
            itemPartNotAssigned.visibility = View.GONE
        } else {
            //no part has been selected yet
            //open part finder
            itemPartNumberView.text = "NOT ALLOTTED"
            itemPartNotAssigned.visibility = View.VISIBLE
        }
    }

    private fun bindPkgLabour(
        item: OutwardItem, binding: NewRowPkgPartBinding, position: Int
    ) {
        val itemTitleView: TextView = binding.tvItemName
        val itemPartNumberView: TextView = binding.tvPartNumber
        val priceView: TextView = binding.partNotAssignedTV

        Timber.d("rendering ${item.type}")
        itemTitleView.text = item.text
//        holder.priceView.visibility=View.VISIBLE
//        holder.priceView.setTextColor(Color.BLACK)
//        holder.priceView.text=if (item.amount != null) Utility.convertToCurrency(item.amount!!) else "-"
    }

    companion object {
        const val VIEW_TYPE_PART = 0
        const val VIEW_TYPE_LABOUR = 10
        const val VIEW_TYPE_SECTION = 1
        const val VIEW_TYPE_PROFORMA_SUMMARY = 2
        const val VIEW_TYPE_ESTIMATOR_SUMMARY = 3
        const val VIEW_TYPE_SERVICE_PACKAGE = 4
        const val VIEW_TYPE_SPLIT_INVOICE = 5
        const val VIEW_TYPE_SPLIT_OTHER_CHARGES = 7
        const val VIEW_TYPE_THIRD_PARTY_ADD = 8
        const val VIEW_TYPE_THIRD_PARTY_EDIT = 9
        const val PRICE_CHANGE_INTERVAL = 800L // 1.5 sec
        const val DISC_TAX_RS = "Disc : \u20B9 %.1f Tax: %.1f%%"
        const val DISC_TAX_PERCENT = "Disc :  %.1f%% Tax: %.1f%%"
        const val ARG_IS_FROM_ESTIMATE = "is_estimator"
        const val ARG_IS_FROM_PROFROMA = "is_proforma"
        const val VIEW_TYPE_SER_PKG_PART = 11
        const val VIEW_TYPE_SER_PKG_LABOUR = 12
    }
}