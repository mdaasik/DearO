package com.carworkz.dearo.domain.entities

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AmcBenefit(
        @Json(name = "text")
        val text: String = ""
): Parcelable