package com.carworkz.dearo.otc

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
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
import com.carworkz.dearo.databinding.NewEstimatorTitleBinding
import com.carworkz.dearo.databinding.NewProformaSummaryBinding
import com.carworkz.dearo.domain.entities.Discount
import com.carworkz.dearo.domain.entities.HSN
import com.carworkz.dearo.domain.entities.Part
import com.carworkz.dearo.extensions.*
import com.carworkz.dearo.outwarding.SectionViewHolder
import com.carworkz.dearo.outwarding.SummaryViewHolder
import com.carworkz.dearo.outwarding.helper.OutwardSection
import com.carworkz.dearo.outwarding.helper.OutwardSummary
import com.carworkz.dearo.searchabledialog.SearchableDialog
import com.carworkz.dearo.utils.Constants
import com.carworkz.dearo.utils.Utility
import timber.log.Timber
import java.util.*

class OtcProformaAdapter(val context: Context, val items: MutableList<Any>, private val hsnList: MutableList<HSN>?, val priceChangeListener: PriceChangeListener) : DelegateSwipeAdapter<Any>(items) {

    override fun onItemDeleted(position: Int, item: Any)
    {
        Handler(Looper.getMainLooper()).postDelayed({ (context as? OtcProformaActivity)?.refreshSummary() }, 200)
    }


    override fun onItemDeletedUndo(position: Int) {
        Handler(Looper.getMainLooper()).postDelayed({ (context as? OtcProformaActivity)?.refreshSummary() }, 200)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PART -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_new_estimator, parent, false))
            VIEW_TYPE_SECTION -> SectionViewHolder(NewEstimatorTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            VIEW_TYPE_SUMMARY ->SummaryViewHolder(NewProformaSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_new_estimator, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Part -> VIEW_TYPE_PART
            is OutwardSection -> VIEW_TYPE_SECTION
            is OutwardSummary -> VIEW_TYPE_SUMMARY
            else -> VIEW_TYPE_PART
        }
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SummaryViewHolder -> {
                val item = items[position] as OutwardSummary
                holder.summarySubtotal.text = if (item.subTotalAmount != null) Utility.round(item.subTotalAmount!!, 1).toString() else "-"
                holder.summaryTotalTax.text = if (item.taxAmount != null) Utility.round(item.taxAmount!!, 1).toString() else "-"
                holder.summaryTotalDiscount.text = if (item.discountAmount != null) Utility.round(item.discountAmount!!, 1).toString() else "-"
                holder.summaryTotal.text = if (item.totalAmount != null) Utility.round(item.totalAmount!!, 1).toString() else "-"
            }
            is SectionViewHolder -> {
                val section = items[position] as OutwardSection
                holder.sectionTitleView.text = section.title
                holder.sectionTotalView.visibility = View.GONE
            }
            is ViewHolder -> {
                val item = items[position] as Part
                if (itemsPendingRemoval.contains(item)) {
                    holder.deleteParentView.visibility = View.VISIBLE
                    holder.itemParentView.visibility = View.GONE
                    holder.undoView.setOnClickListener {
                        undoDelete(item)
                    }
                } else {
                    holder.deleteParentView.visibility = View.GONE
                    holder.itemParentView.visibility = View.VISIBLE
                    holder.itemTitleView.text = item.text
                    holder.itemRateView.isEnabled = SharedPrefHelper.isPartPriceEditable() || item.price <= 0.0
                    if (SharedPrefHelper.isGstEnabled()) {
                        holder.hsnLayout.visibility = View.VISIBLE
                        if (item.tax.hsn != null) {
                            holder.hsnView.text = if (item.tax.hsn.equals(HSN.SELECT)) {
                                HSN.HSN_CODE
                            } else {
                                item.tax.hsn
                            }
                        } else {
                            holder.hsnView.text = HSN.HSN_CODE
                        }
                        if (item.tax.hsn != null && !item.tax.hsn.equals(HSN.SELECT)) {
                            holder.hsnLayout.isEnabled = SharedPrefHelper.isHsnPartEnabled()
                        }
                    } else {
                        holder.hsnLayout.visibility = View.GONE
                    }
                    if (item.partNumber.isNullOrEmpty()) {
                        holder.itemPartNumberView.visibility = View.GONE
                    } else {
                        holder.itemPartNumberView.visibility = View.VISIBLE

                        @SuppressLint("SetTextI18n")
                        holder.itemPartNumberView.text = "${item.brand.name} - ${item.partNumber}"
                        if (SharedPrefHelper.isInventoryEnabled()) {
                            holder.itemPartNumberView.textColor = if (item.stock!! > 0) ContextCompat.getColor(holder.itemPartNumberView.context, R.color.forest_green)
                            else ContextCompat.getColor(holder.itemDiscTaxView.context, R.color.red)
                        } else {
                            holder.itemPartNumberView.textColor = ContextCompat.getColor(holder.itemPartNumberView.context, R.color.black)
                        }
                    }
                    holder.itemDiscTaxView.visibility = View.VISIBLE
                    holder.itemDiscTaxView.textColor = R.color.black
                    holder.itemDiscTaxView.text = String.format(if (item.discount.mode == Discount.MODE_PRICE) DISC_TAX_RS else DISC_TAX_PERCENT, item.discount.amount, if (item.tax.hsn != null && SharedPrefHelper.isGstEnabled()) item.tax.sgst + item.tax.cgst else 0.0)
                    holder.quantityParentView.visibility = View.VISIBLE
                    holder.itemQuantityView.setText(item.quantity.toString())
                    holder.itemQuantityUnitView.adapter = ArrayAdapter(holder.itemQuantityUnitView.context, R.layout.simple_units_picker_item, if ((items[position] as Part).units != null) (items[position] as Part).units!! else Constants.BusinessConstants.UNITS_LIST)
                    (holder.itemQuantityUnitView.adapter as ArrayAdapter<*>).setDropDownViewResource(R.layout.simple_hsn_spinner_item1)
                    holder.itemQuantityUnitView.prompt = "Select Units"
                    val index: Int? = if (item.units != null) {
                        item.units?.indexOf(item.units?.find { it == item.unit })
                    } else {
                        Constants.BusinessConstants.UNITS_LIST.indexOf(Constants.BusinessConstants.UNITS_LIST.find { it == item.unit })
                    }
                    holder.itemQuantityUnitView.setSelection(index ?: 0)
                }
                holder.itemTotalView.text = if (item.amount >= 0.0) String.format("%.1f", Utility.round(item.amount, 1)) else "-"
                holder.itemRateView.setText(if (item.price > 0.0) {
                    item.price.toString()
                } else "")
                val rateText = holder.itemRateView.text
                val decimalIndex = rateText.indexOf(".")
                holder.itemRateView.setSelection(if (decimalIndex != -1) decimalIndex else rateText.length)
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val deleteParentView: View = view.find(R.id.cl_parent_delete)
        val itemParentView: View = view.find(R.id.estimateRowParentView)
        val quantityParentView: View = view.find(R.id.ll_quantity)
        val itemTitleView: TextView = view.find(R.id.tv_item_name)
        val itemPartNumberView: TextView = view.find(R.id.tv_part_number)
        val itemDiscTaxView: TextView = view.find(R.id.tv_disc_tax)
        val itemQuantityUnitView: Spinner = view.find(R.id.spinner_quantity_units)
        val itemQuantityView: EditText = view.find(R.id.et_quantity)
        val itemTotalView: TextView = view.find(R.id.tv_item_total)
        val itemRateView: EditText = view.find(R.id.salvageView)
        val undoView: TextView = view.find(R.id.tv_delete_undo)
        val hsnView: TextView = view.find(R.id.tv_hsn)
        val hsnLayout: LinearLayout = view.find(R.id.ll_hsn)
        private var isRateViewInitiated = false
        private var isQuantityViewInitiated = false
        val handler = Handler(Looper.getMainLooper())
        var rateRunnable: Runnable? = null
        var quantityRunnable: Runnable? = null

        init {
            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                itemQuantityView.keyListener = DigitsKeyListener.getInstance(Locale.ENGLISH, true, true)
                itemRateView.keyListener = DigitsKeyListener.getInstance(Locale.ENGLISH, true, true)
            } else {
                itemQuantityView.keyListener = DigitsKeyListener.getInstance(true, true)
                itemRateView.keyListener = DigitsKeyListener.getInstance(true, true)
            }
            itemRateView.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (isRateViewInitiated) {
                        if(rateRunnable!=null) {
                            handler.removeCallbacks(rateRunnable!!)
                        }
                        rateRunnable = Runnable {
                            if (adapterPosition != -1) {
                                val item = items[adapterPosition] as Part
                                if (s?.isNotEmpty() == true && Utility.isValidDecimal(s.toString())) {
                                    if (item.price != s.toString().toDouble()) {
                                        if (item.discount.mode == Discount.MODE_PERCENTAGE && item.discount.amount > 100 || s.toString().toFloat() < item.discount.amount) {
                                            itemRateView.error = itemRateView.context.getString(R.string.discount_gt_price)
                                        } else {
                                            itemRateView.error = null
                                            item.price = Utility.round(s.toString().toDouble(), 1)
                                            priceChangeListener.onPriceUpdate("", adapterPosition)
                                        }
                                    }
                                } else {
                                    if (s?.isNotEmpty() == true && !Utility.isValidDecimal(s.toString())) {
                                        itemRateView.error = "Invalid Price"
                                    } else if (s?.isEmpty() == true && item.price != 0.0) {
                                        item.price = 0.0
                                        item.amount = 0.0
                                        itemTotalView.text = 0.0.toString()
                                        priceChangeListener.onPriceUpdate("", adapterPosition)
                                    }
                                }
                            }
                        }
                        handler.postDelayed(rateRunnable!!, PRICE_CHANGE_INTERVAL)
                    } else {
                        isRateViewInitiated = true
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
            itemQuantityView.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (isQuantityViewInitiated) {
                        if(quantityRunnable!=null) {
                            handler.removeCallbacks(quantityRunnable!!)
                        }
                        quantityRunnable = Runnable {
                            if (adapterPosition != -1) {
                                val item = items[adapterPosition] as Part
                                if (s?.isNotEmpty() == true && Utility.isValidDecimal(s.toString()) && s.toString().toFloat() > 0.0f && item.quantity != s.toString().toFloat()) {
                                    item.quantity = s.toString().toFloat()
                                    priceChangeListener.onPriceUpdate("", adapterPosition)
                                } else {
                                    if (s?.isNotEmpty() == true && !Utility.isValidDecimal(s.toString())) {
                                        itemQuantityView.error = "Invalid Quantity"
                                    } else if (s?.isEmpty() == true || s.toString().toFloat() <= 0.0f) {
                                        item.quantity = 1.0f
                                        itemQuantityView.setText("1.0")
                                        priceChangeListener.onPriceUpdate("", adapterPosition)
                                        itemQuantityView.context.toast("Quantity cannot be 0")
                                    }
                                }
                            }
                        }
                        handler.postDelayed(quantityRunnable!!, PRICE_CHANGE_INTERVAL)
//                             else {
//                                itemQuantityView.setText("1")
//                            }
                    } else {
                        isQuantityViewInitiated = true
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
            hsnLayout.setOnClickListener {
                if (items[adapterPosition] is Part) {
                    val part = items[adapterPosition] as Part
                    if (part.tax.hsn == null || part.tax.hsn.equals(HSN.SELECT) || SharedPrefHelper.isHsnPartEnabled()) {
                        SearchableDialog.Builder(hsnLayout.context, hsnList!!).apply {
                            setTitle("HSN CODE")
                            setSelectedItem(hsnList.find { it.hsn == part.tax.hsn })
                            setItemSelectedListener(object : SearchableDialog.OnSearchItemSelectedListener<HSN> {
                                override fun onItemSelectedItem(item: HSN?) {
                                    Timber.d("hsn ${item?.hsn}")
                                    part.tax.apply {
                                        hsn = item?.hsn
                                        cgst = item?.cgst ?: 0.0
                                        sgst = item?.sgst ?: 0.0
                                    }
                                    notifyItemChanged(adapterPosition)
                                    priceChangeListener.onPriceUpdate("", adapterPosition)
                                }

                                override fun onCancel() {
                                }
                            })
                        }.show()
                    }
                }
            }
            itemQuantityUnitView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    (items[adapterPosition] as Part).unit = (items[adapterPosition] as Part).units?.get(p2) ?: Constants.BusinessConstants.UNITS_LIST[p2]
                }
            }
        }
    }

    companion object {
        const val VIEW_TYPE_PART = 0
        const val VIEW_TYPE_SECTION = 1
        const val VIEW_TYPE_SUMMARY = 2
        const val PRICE_CHANGE_INTERVAL = 1500L // 1.5 sec

        const val DISC_TAX_RS = "Disc : \u20B9 %.1f Tax: %.1f%%"
        const val DISC_TAX_PERCENT = "Disc :  %.1f%% Tax: %.1f%%"
    }
}