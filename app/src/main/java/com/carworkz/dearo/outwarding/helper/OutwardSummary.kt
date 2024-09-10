package com.carworkz.dearo.outwarding.helper

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OutwardSummary(
    val title: String,
    var subTotalAmount: Double?,
    var taxAmount: Double?,
    var discountAmount: Double?,
    var totalAmount: Double?
) : OutwardStep, Parcelable