package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

data class Payment(
    @Json(name = "id")
    val id: String?,
    @Json(name = "type")
    val type: String,
    @Json(name = "method")
    val method: String,
    @Json(name = "amount")
    val amount: Double,
    @Json(name = "invoiceId")
    val invoiceId: String,
    @Json(name = "transactionNumber")
    val transactionNumber: String?,
    @Json(name = "transactionDetails")
    val transactionDetails: String?,
    @Json(name = "bankName")
    val bankName: String?,
    @Json(name = "cardNumber")
    val cardNumber: String?,
    @Json(name = "drawnOnDate")
    val drawnOnDate: String?,
    @Json(name = "chequeDate")
    val chequeDate: String?,
    @Json(name = "chequeNumber")
    val chequeNumber: String?,
    @Json(name = "remarks")
    val remarks: String?,
    @Json(name = "createdOn")
    val createdOn: String?,
    @Json(name = "receiptNumber")
    val receiptNumber: String?,
    @Json(name = "notifyCustomer")
    val notifyCustomer: Boolean?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readDouble(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readByte().equals(1)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(type)
        parcel.writeString(method)
        parcel.writeDouble(amount)
        parcel.writeString(invoiceId)
        parcel.writeString(transactionNumber)
        parcel.writeString(transactionDetails)
        parcel.writeString(bankName)
        parcel.writeString(cardNumber)
        parcel.writeString(drawnOnDate)
        parcel.writeString(chequeDate)
        parcel.writeString(chequeNumber)
        parcel.writeString(remarks)
        parcel.writeString(createdOn)
        parcel.writeString(receiptNumber)
        parcel.writeByte(if (notifyCustomer == true) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Payment> {
        override fun createFromParcel(parcel: Parcel): Payment {
            return Payment(parcel)
        }

        override fun newArray(size: Int): Array<Payment?> {
            return arrayOfNulls(size)
        }
    }

}