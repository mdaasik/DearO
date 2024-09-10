package com.carworkz.dearo.domain.entities

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AMCPackage(

    @Json(name = "includeTax")
    val includeTax: Boolean = false,
    @Json(name = "code")
    val code: String = "",
    @Json(name = "description")
    val description: String = "",
    @Json(name = "id")
    val id: String = "",
    @Json(name = "kms")
    val kms: Int = 0,
    @Json(name = "name")
    val name: String = "",
    @Json(name = "packageId")
    val packageId: String = "",
    @Json(name = "price")
    val price: Double = 0.0,
    @Json(name = "benefits")
    val benefits: List<AmcBenefit> = listOf(),
    @Json(name = "services")
    val services: List<SoldAMCDetails.AmcService> = listOf(),
    @Json(name = "terms")
    val terms: List<String> = listOf(),
    @Json(name = "validity")
    val validity: Int = 0
) : Parcelable
