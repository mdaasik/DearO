package com.carworkz.dearo.amc


import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.carworkz.dearo.domain.entities.Discount

@Parcelize
data class AMCPurchase(
        @Json(name = "amcId")
        var amcId: String = "",
        @Json(name = "customerAddressId")
        var customerAddressId: String = "",
        @Json(name = "customerId")
        var customerId: String = "",
        @Json(name = "packageId")
        var packageId: String = "",
        @Json(name = "discount")
        val discount: Discount?=Discount(),
        @Json(name = "vehicleId")
        var vehicleId: String = ""
) : Parcelable
