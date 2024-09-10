package com.carworkz.dearo.addjobcard.createjobcard.estimate.partnumberselection

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.databinding.RowPartNumberFinderBinding
import com.carworkz.dearo.domain.entities.PartNumber
import com.carworkz.dearo.utils.Utility
import java.util.*

class PartNumberSelectionAdapter(
    private val partNumbers: List<PartNumber>,
    private val showStock: Boolean
) : RecyclerView.Adapter<PartNumberSelectionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowPartNumberFinderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = partNumbers.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val partNumber = partNumbers[position]
        with(holder.binding) {
            Utility.setVisibility(partNumber.partName != null, partNameView)
            partNameView.text = partNumber.partName

            Utility.setVisibility(partNumber.remark != null, remarkView)
            remarkView.text = partNumber.remark

            Utility.setVisibility(partNumber.category != null, tvOilType)
            tvOilType.text = partNumber.category

            descriptionView.text = partNumber.description
            partNumberView.text = "${partNumber.brandName} - ${partNumber.partNumber}"

            priceView.text = if (partNumbers[position].sellingPrice > 0.0) Utility.convertToCurrency(partNumbers[position].sellingPrice.toDouble()) else "\u20B9 -"

            if (SharedPrefHelper.isInventoryEnabled() && showStock) {
                Utility.setVisibility(true, stockView)
                if (partNumbers[position].stock > 0) {
                    stockView.text = String.format(Locale.getDefault(), root.context.getString(R.string.search_part_number_label_in_stock), partNumbers[position].stock)
                    stockView.setTextColor(ContextCompat.getColor(root.context, R.color.check_box_green))
                } else {
                    stockView.setText(R.string.search_part_number_label_out_stock)
                    stockView.setTextColor(ContextCompat.getColor(root.context, R.color.red))
                }
            } else {
                Utility.setVisibility(false, stockView)
            }
        }
    }

    inner class ViewHolder(val binding: RowPartNumberFinderBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.parentView.setOnClickListener {
                val intent = Intent()
                intent.putExtra(ARG_PART_NUMBER, partNumbers[adapterPosition])
                (binding.root.context as? Activity)?.setResult(Activity.RESULT_OK, intent)
                (binding.root.context as? Activity)?.finish()
            }
        }
    }

    companion object {
        const val ARG_PART_NUMBER = "part_number"
    }
}
