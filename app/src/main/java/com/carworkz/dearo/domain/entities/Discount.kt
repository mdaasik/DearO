package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

/**
 * Created by Kush Singh Chibber on 9/13/2017.
 */

class Discount() : Parcelable {

    @Json(name = "mode")
    internal var mode = MODE_PERCENTAGE

    @Json(name = "amount")
    internal var amount: Double = 0.0

    constructor(parcel: Parcel) : this() {
        mode = parcel.readString().toString()
        amount = parcel.readDouble()
    }

    override fun toString(): String {
        return "{ MODE : $mode AMOUNT : $amount }"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(mode)
        parcel.writeDouble(amount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Discount> {
        const val MODE_PERCENTAGE = "PERCENTAGE"
        const val MODE_PRICE = "PRICE"

        override fun createFromParcel(parcel: Parcel): Discount {
            return Discount(parcel)
        }

        override fun newArray(size: Int): Array<Discount?> {
            return arrayOfNulls(size)
        }
    }
}
