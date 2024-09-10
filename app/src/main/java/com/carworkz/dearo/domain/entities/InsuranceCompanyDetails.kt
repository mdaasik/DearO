package com.carworkz.dearo.domain.entities

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InsuranceCompanyDetails(

    @Json(name = "gstNumber")
    val gstNumber: String? = null,

    @Json(name = "pincode")
    val pincode: Int? = null,

    @Json(name = "address")
    val address: String? = null,

    @Json(name = "city")
    val city: String? = null,

    @Json(name = "street")
    val street: String? = null,

    @Json(name = "name")
    val name: String? = null,

    @Json(name = "mobile")
    val mobile: String? = null,

    @Json(name = "addressSlug")
    val addressSlug: String? = null,

    @Json(name = "state")
    val state: String? = null,

    @Json(name = "vehicleType")
    val vehicleType: List<String?>? = null,

    @Json(name = "slug")
    val slug: String? = null,

    @Json(name = "status")
    val status: String? = null
) : Parcelable