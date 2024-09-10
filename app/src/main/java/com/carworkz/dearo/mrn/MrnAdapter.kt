package com.carworkz.dearo.mrn

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R
import com.carworkz.dearo.domain.entities.Part
import com.carworkz.dearo.utils.Utility

class MrnAdapter(val context: Context, val parts: List<Part>) : RecyclerView.Adapter<MrnAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_mrn, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = parts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val part = parts[position]
        holder.apply {
            partView.apply {
                setText(SpannableStringBuilder().apply {
                    val partNameSpan = TextAppearanceSpan(context, R.style.MrnPartName)
                    append(part.text, partNameSpan, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
//                    append(part.text)
                    if (part.description != null) {
                        append(" - ")
                        val partDescriptionSpan = TextAppearanceSpan(context, R.style.MrnPartDescription)
                        append(part.description, partDescriptionSpan, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                        // append(part.des)
                    }
                }, TextView.BufferType.SPANNABLE)
            }

            mrpView.setText(SpannableStringBuilder().apply {
                val mrpSpan = TextAppearanceSpan(context, R.style.MrnPartPriceTextStyle)
                append("MRP: ", mrpSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                val priceSpan = TextAppearanceSpan(context, R.style.MrnPartPriceValueStyle)
                append(Utility.convertToCurrency(part.price), priceSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }, TextView.BufferType.SPANNABLE)

            qtyView.setText(SpannableStringBuilder().apply {
                val qtySpan = TextAppearanceSpan(context, R.style.MrnPartPriceTextStyle)
                append("QTY: ", qtySpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                val quantitySpan = TextAppearanceSpan(context, R.style.MrnPartPriceValueStyle)
                val unitSpan = TextAppearanceSpan(context, R.style.MrnPartPriceValueStyle)
                append(part.quantity.toString(), quantitySpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                append(" ")
                append(part.unit, unitSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }, TextView.BufferType.SPANNABLE)

            totalView.text = Utility.convertToCurrency(part.price * part.quantity)
            brandPartNumberView.text = StringBuilder()
                    .append(part.brand.name)
                    .append(" - ")
                    .append(part.partNumber)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val brandPartNumberView = view.findViewById<TextView>(R.id.mrnBrandPartNumberView)!!
        val partView = view.findViewById<TextView>(R.id.mrnPartView)!!
        val totalView = view.findViewById<TextView>(R.id.mrnTotalView)!!
        val mrpView = view.findViewById<TextView>(R.id.mrnMrpView)!!
        val qtyView = view.findViewById<TextView>(R.id.mrnQtyView)!!
    }
}