package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

class Company() : Parcelable {
    @Json(name = "name")
    var name: String? = null
    @Json(name = "address")
    var address: Address? = null
    @Json(name = "gstNumber")
    var gstNumber: String? = null

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        address = parcel.readParcelable(Address::class.java.classLoader)
        gstNumber = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeParcelable(address, flags)
        parcel.writeString(gstNumber)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Company> {
        override fun createFromParcel(parcel: Parcel): Company {
            return Company(parcel)
        }

        override fun newArray(size: Int): Array<Company?> {
            return arrayOfNulls(size)
        }
    }
}
