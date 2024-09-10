package com.carworkz.dearo.domain.entities


import android.annotation.SuppressLint
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class SoldAMCDetails(
        @Json(name = "amcCode")
        val amcCode: String = "",
        @Json(name = "amcId")
        val amcId: String = "",
        @Json(name = "amcName")
        val amcName: String = "",
        @Json(name = "amcNumber")
        val amcNumber: String = "",
        @Json(name = "amount")
        val amount: Double = 0.0,
        @Json(name = "benefits")
        val benefits: List<AmcBenefit> = listOf(),
        @Json(name = "createdOn")
        val createdOn: String = "",
        @Json(name = "customerAddressId")
        val customerAddressId: String = "",
        @Json(name = "customerId")
        val customerId: String = "",
        @Json(name = "expiryDate")
        val expiryDate: String = "",
        @Json(name = "id")
        val id: String = "",
        @Json(name = "invoiceAmount")
        val invoiceAmount: String = "",
        @Json(name = "invoiceDate")
        val invoiceDate: String = "",
        @Json(name = "invoiceId")
        val invoiceId: String = "",
        @Json(name = "packageId")
        val packageId: String = "",
        @Json(name = "rates")
        val rates: List<Rate> = listOf(),
        @Json(name = "services")
        val services: List<AmcService> = listOf(),
        @Json(name = "status")
        val status: String = "",
        @Json(name = "tax")
        val tax: Tax = Tax(),
        @Json(name = "terms")
        val terms: List<String> = listOf(),
        @Json(name = "updatedOn")
        val updatedOn: String = "",
        @Json(name = "vehicleId")
        val vehicleId: String = "",
        @Json(name = "workshopId")
        val workshopId: String = "",
        @Json(name = "jobcards")
        val jobcards: MutableList<AMCJobCard> = mutableListOf()
) : Parcelable {
    @SuppressLint("ParcelCreator")
    @Parcelize
    data class Rate(
            @Json(name = "amcId")
            val amcId: String = "",
            @Json(name = "category")
            val category: String = "",
            @Json(name = "cityType")
            val cityType: String = "",
            @Json(name = "createdOn")
            val createdOn: String = "",
            @Json(name = "fuelType")
            val fuelType: String = "",
            @Json(name = "id")
            val id: String = "",
            @Json(name = "makeSlug")
            val makeSlug: String = "",
            @Json(name = "modelSlug")
            val modelSlug: String = "",
            @Json(name = "price")
            val price: Int = 0,
            @Json(name = "updatedOn")
            val updatedOn: String = ""
    ) : Parcelable

    @Parcelize
    data class AmcService(@Json(name = "id")
                              val id: String = "",
                          @Json(name = "text")
                              val text: String? = "",
                          @Json(name = "type")
                              val type: String = "",
                          @Json(name = "typeId")
                              val typeId: String = "",
                          @Json(name = "availedQuantity")
                              val availedQuantity: Int,
                          @Json(name = "quantity")
                              val quantity: Int) : Parcelable
}