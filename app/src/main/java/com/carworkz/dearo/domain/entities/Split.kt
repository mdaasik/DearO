package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

class Split() : Parcelable {

    @Json(name = "mode")
    internal var mode: String = MODE_PERCENTAGE
    @Json(name = "cost")
    internal var cost: Double = 0.0
    @Json(name = "customerPay")
    internal var customerPay: CustomerPay = CustomerPay()
    @Json(name = "insurancePay")
    internal var insurancePay: InsurancePay = InsurancePay()

    constructor(parcel: Parcel) : this() {
        mode = parcel.readString().toString()
        cost = parcel.readDouble()
        customerPay = parcel.readParcelable(CustomerPay::class.java.classLoader)!!
        insurancePay = parcel.readParcelable(InsurancePay::class.java.classLoader)!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(mode)
        parcel.writeDouble(cost)
        parcel.writeParcelable(customerPay, flags)
        parcel.writeParcelable(insurancePay, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Split> {
        const val MODE_PERCENTAGE = "PERCENTAGE"
        const val MODE_PRICE = "PRICE"

        override fun createFromParcel(parcel: Parcel): Split {
            return Split(parcel)
        }

        override fun newArray(size: Int): Array<Split?> {
            return arrayOfNulls(size)
        }
    }
}
