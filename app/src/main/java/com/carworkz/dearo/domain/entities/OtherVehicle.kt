package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

class OtherVehicle() : Parcelable {
    @Json(name = "registrationNumber")
     internal var registrationNumber:String?=null
    @Json(name = "make")
    internal var make:String?=null
    @Json(name = "model")
    internal var model:String?=null
    @Json(name = "year")
    internal var year:String?=null

    constructor(parcel: Parcel) : this() {
        registrationNumber = parcel.readString()
        make = parcel.readString()
        model = parcel.readString()
        year = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(registrationNumber)
        parcel.writeString(make)
        parcel.writeString(model)
        parcel.writeString(year)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OtherVehicle> {
        override fun createFromParcel(parcel: Parcel): OtherVehicle {
            return OtherVehicle(parcel)
        }

        override fun newArray(size: Int): Array<OtherVehicle?> {
            return arrayOfNulls(size)
        }
    }
}