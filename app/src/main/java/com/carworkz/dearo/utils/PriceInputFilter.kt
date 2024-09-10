package com.carworkz.dearo.utils

import android.text.InputFilter
import android.text.Spanned

class PriceInputFilter(private val min: String, private val max: String) : InputFilter {

    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence {
        try {
            val input = (dest.toString() + source.toString()).toDouble()
            return isInRange(min, max, input)
        } catch (nfe: NumberFormatException) {
        }

        return ""
    }

    private fun isInRange(min: String, max: String, input: Double): String {
        if (input < min.toDouble()) {
            return min
        }
        if (input > max.toDouble())
            return max

        return input.toString()
    }
}