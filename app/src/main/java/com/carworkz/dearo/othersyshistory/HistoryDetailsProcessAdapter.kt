package com.carworkz.dearo.othersyshistory

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R
import com.carworkz.dearo.databinding.*
import com.carworkz.dearo.extensions.textColor
import com.carworkz.dearo.outwarding.SectionViewHolder
import com.carworkz.dearo.outwarding.SummaryViewHolder
import com.carworkz.dearo.outwarding.helper.*
import com.carworkz.dearo.utils.Utility
import timber.log.Timber

class HistoryDetailsProcessAdapter(var items: MutableList<OutwardStep>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PART -> PartViewHolder(RowNewEstimatorBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            VIEW_TYPE_LABOUR -> LabourViewHolder(RowLabourProformaBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            VIEW_TYPE_SECTION -> SectionViewHolder(NewEstimatorTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            VIEW_TYPE_PROFORMA_SUMMARY -> SummaryViewHolder(NewProformaSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> SectionViewHolder(NewEstimatorTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = items[position]) {
            is OutwardItem -> {
                when (item.type) {
                    OutwardItem.TYPE_LABOUR -> VIEW_TYPE_LABOUR
                    else -> VIEW_TYPE_PART
                }
            }
            is OutwardSection -> VIEW_TYPE_SECTION
            else -> VIEW_TYPE_PROFORMA_SUMMARY
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_PART -> {
                holder as PartViewHolder
                val item = items[position] as OutwardItem

                holder.binding.layOutEstimator.clParentDelete.visibility = View.GONE
                holder.binding.layOutRowPro.estimateRowParentView.visibility = View.VISIBLE
                bindPart(item, holder, position)
            }
            VIEW_TYPE_LABOUR -> {
                holder as LabourViewHolder
                val item = items[position] as OutwardItem

                holder.binding.layOutNewEstimator.clParentDelete.visibility = View.GONE
                holder.binding.estimateRowParentView.visibility = View.VISIBLE
                bindLabour(item, holder)
            }
            VIEW_TYPE_SECTION -> {
                holder as SectionViewHolder
                val section = items[position] as OutwardSection
                holder.sectionTitleView.text = section.title

                if (section.title == OutwardStepMapper.TITLE_PROFORMA_SUMMARY || section.title == OutwardStepMapper.TITLE_ESTIMATOR_SUMMARY ||
                    section.title == OutwardStepMapper.TITLE_SPLIT_INVOICE_SUMMARY || section.title == OutwardStepMapper.TITLE_SPLIT_OTHER_CHARGES) {
                    holder.sectionTotalView.visibility = View.GONE
                } else {
                    holder.sectionTotalView.visibility = View.VISIBLE
                    holder.sectionTotalView.text = if (section.total != null) "Total: \u20B9 ${Utility.round(section.total!!, 1)}" else "-"
                }
            }
            VIEW_TYPE_PROFORMA_SUMMARY -> {
                holder as SummaryViewHolder
                val item = items[position] as OutwardSummary
                holder.summarySubtotal.text = if (item.subTotalAmount != null) Utility.convertToCurrency(item.subTotalAmount!!).toString() else "-"
                holder.summaryTotalTax.text = if (item.taxAmount != null) Utility.convertToCurrency(item.taxAmount!!).toString() else "-"
                holder.summaryTotalDiscount.text = if (item.discountAmount != null) Utility.convertToCurrency(item.discountAmount!!).toString() else "-"
                val totalAmt = if (item.totalAmount != null) Utility.convertToCurrency(item.totalAmount!!).toString() else "-"
                holder.summaryTotal.text = totalAmt
            }
        }
    }

    inner class PartViewHolder(val binding: RowNewEstimatorBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    inner class LabourViewHolder(val binding: RowLabourProformaBinding) : RecyclerView.ViewHolder(binding.root) {
        private var isRateViewInitiated: Boolean = false
        private var isSurchargeViewInitiated: Boolean = false
        val handler = Handler(Looper.getMainLooper())
        var rateRunnable: Runnable? = null
        var surchargeRunnable: Runnable? = null
    }

    private fun bindPart(item: OutwardItem, holder: PartViewHolder, position: Int) {
        Timber.d("rendering ${item.type}")
        val binding = holder.binding

        // Set visibility
        binding.layOutRowPro.llMiddleView.visibility = View.VISIBLE
        binding.layOutRowPro.tvPartNumber.visibility = View.VISIBLE
        binding.layOutRowPro.tvDiscTax.visibility = View.VISIBLE
        binding.layOutRowPro.llQuantity.visibility = View.VISIBLE

        // Disable views
        Utility.setVisibility(false, binding.layOutEstimator.clParentDelete)
        binding.layOutRowPro.tvItemName.isFocusable = false
        binding.layOutRowPro.tvPartNumber.isFocusable = false
        binding.layOutRowPro.tvDiscTax.isFocusable = false
        binding.layOutRowPro.spinnerQuantityUnits.isFocusable = false
        binding.layOutRowPro.etQuantity.isFocusable = false
        binding.layOutRowPro.tvItemTotal.isFocusable = false
        binding.layOutRowPro.salvageView.isFocusable = false
        binding.layOutRowPro.tvHsn.isFocusable = false

        binding.layOutRowPro.tvItemName.text = item.text
        binding.layOutRowPro.tvHsn.text = item.hsn
        val brandPart = "${item.brand.name ?: ""} ${if (item.partNumber.isNullOrEmpty()) "" else "-"} ${item.partNumber ?: ""}"
        binding.layOutRowPro.tvPartNumber.text = brandPart
        binding.layOutRowPro.etQuantity.setText(item.quantity.toString())
        binding.layOutRowPro.tvItemTotal.text = if (item.amount != null) Utility.convertToCurrency(item.amount!!) else "-"
        binding.layOutRowPro.salvageView.setText(if (item.price > 0.0) {
            Utility.round(item.price, 1).toString()
        } else "")

        binding.layOutRowPro.tvDiscTax.visibility = View.VISIBLE
        binding.layOutRowPro.tvDiscTax.textColor = R.color.black
        binding.layOutRowPro.tvDiscTax.text = String.format(DISC_TAX_RS, item.discountAmount, item.taxPercent)
        binding.layOutRowPro.tvDiscTax.textColor = R.color.black
    }

    private fun bindLabour(item: OutwardItem, holder: LabourViewHolder) {
        val binding = holder.binding

        binding.labourdiscountTaxView.isFocusable = false
        binding.labourdiscountTaxView.visibility = View.VISIBLE
        binding.labourdiscountTaxView.text = String.format(DISC_TAX_RS, item.discountAmount, item.taxPercent)
        binding.labourItemName.text = item.text
        binding.labourTotalView.text = if (item.amount != null) Utility.convertToCurrency(item.amount!!) else "-"
        binding.labourRateAmountView.isEnabled = false
        binding.labourRateAmountView.setText(if (item.price > 0.0) {
            Utility.round(item.price, 1).toString()
        } else "")
        binding.labourSurchargeAmountView.isEnabled = false
        binding.labourSurchargeAmountView.setText(item.surcharge.toString())
    }

    companion object {
        const val VIEW_TYPE_PART = 0
        const val VIEW_TYPE_LABOUR = 10
        const val VIEW_TYPE_SECTION = 1
        const val VIEW_TYPE_PROFORMA_SUMMARY = 2
        const val DISC_TAX_RS = "Disc : \u20B9 %.1f Tax: %.1f%%"
        const val DISC_TAX_PERCENT = "Disc :  %.1f%% Tax: %.1f%%"
        const val ARG_IS_FROM_ESTIMATE = "is_estimator"
        const val ARG_IS_FROM_PROFROMA = "is_proforma"
    }
}
