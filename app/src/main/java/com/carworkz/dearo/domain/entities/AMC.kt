package com.carworkz.dearo.domain.entities


import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AMC(
    @property:Json(name = "customer")
    internal var customer: Customer,
    @property:Json(name = "vehicle")
    internal var vehicle: Vehicle,
    @Json(name = "amcCode")
    val amcCode: String = "",
    @Json(name = "amcName")
    val amcName: String = "",
    @Json(name = "amcId")
    val amcId: String = "",
    @Json(name = "amcNumber")
    val amcNumber: String = "",
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
    @Json(name = "packageId")
    val packageId: String = "",
    @Json(name = "benefits")
    val benefits: List<AmcBenefit> = listOf(),
    @Json(name = "services")
    val services: List<SoldAMCDetails.AmcService> = listOf(),
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
    @Json(name = "amount")
    val amount: Double = 0.0,
    @Json(name = "invoice")
    internal var invoice: Invoice? = null,
    @Json(name = "invoiceId")
    internal var invoiceId: String = "",
    @Json(name = "invoiceDate")
    internal var invoiceDate: String = "",
    @Json(name = "invoiceAmount")
    internal var invoiceAmount: Double = 0.0,
    @Json(name = "unitRate")
    internal var unitRate: Double = 0.0,
    @Json(name = "pdf")
    internal var pdf: PDF? = null,
    @Json(name = "cancelledDate")
    val cancelledDate: String? = "",
    @Json(name = "ftd")
    var ftd: Double? = null,
    @Json(name = "mtd")
    var mtd: Double? = null
) : Parcelable {
    @IgnoredOnParcel
    var isSelected: Boolean = false

    @IgnoredOnParcel
    var selectedPosition: Int = 0

    @Parcelize
    data class Tax(
        @Json(name = "cgst")
        val cgst: Int = 0,
        @Json(name = "hsn")
        val hsn: String = "",
        @Json(name = "igst")
        val igst: Int = 0,
        @Json(name = "sgst")
        val sgst: Int = 0
    ) : Parcelable

    companion object {
        const val AMC_EXPIRED = "EXPIRED"
        const val AMC_ACTIVE = "ACTIVE"
        const val AMC_CANCELLED = "CANCELLED"
    }
}
