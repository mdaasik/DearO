package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

data class CustomerPay(
    @Json(name = "amount")
    internal var amount: Double = 0.0,
    @Json(name = "discountAmount")
    internal var discountAmount: Double = 0.0,
    @Json(name = "sgstAmount")
    internal var sgstAmount: Double = 0.0,
    @Json(name = "cgstAmount")
    internal var cgstAmount: Double = 0.0,
    @Json(name = "taxableAmount")
    internal var taxableAmount: Double = 0.0,
    @Json(name = "rate")
    internal var rate: Double = 0.0
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readDouble())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(amount)
        parcel.writeDouble(discountAmount)
        parcel.writeDouble(sgstAmount)
        parcel.writeDouble(cgstAmount)
        parcel.writeDouble(taxableAmount)
        parcel.writeDouble(rate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CustomerPay> {
        override fun createFromParcel(parcel: Parcel): CustomerPay {
            return CustomerPay(parcel)
        }

        override fun newArray(size: Int): Array<CustomerPay?> {
            return arrayOfNulls(size)
        }
    }
}