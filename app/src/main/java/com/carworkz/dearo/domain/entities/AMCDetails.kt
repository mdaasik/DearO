package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

class AMCDetails() : Parcelable {

    @Json(name = "isAvailable")
    var isAvailable = false
    @Json(name = "number")
    var number: String? = null
    @Json(name = "expiryDate")
    var expiryDate: String? = null

    constructor(parcel: Parcel) : this() {
        isAvailable = parcel.readByte() != 0.toByte()
        number = parcel.readString()
        expiryDate = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (isAvailable) 1 else 0)
        parcel.writeString(number)
        parcel.writeString(expiryDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AMCDetails> {
        const val AMC_ACTIVE = "ACTIVE"
        const val AMC_EXPIRED = "EXPIRED"

        override fun createFromParcel(parcel: Parcel): AMCDetails {
            return AMCDetails(parcel)
        }

        override fun newArray(size: Int): Array<AMCDetails?> {
            return arrayOfNulls(size)
        }
    }
}