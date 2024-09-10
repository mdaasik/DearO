package com.carworkz.dearo.domain.entities

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MrnEstimate(
    @Json(name = "parts")
    internal var parts: MutableList<Part>? = null,
    @Json(name = "isPartsIssued")
    internal var isPartsIssued: Boolean,
    @Json(name = "partsIssuedDate")
    internal var partsIssuedDate: String?
) : Parcelable