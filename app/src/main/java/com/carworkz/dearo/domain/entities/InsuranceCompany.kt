package com.carworkz.dearo.domain.entities

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

const val DUMMY_SLUG = "" // used purely for display purpose

@Parcelize
data class InsuranceCompany(
    @Json(name = "name") val name: String,
    @Json(name = "slug") val slug: String,
    @Json(name = "stateCode") var stateCode: Int? = null
) : Parcelable