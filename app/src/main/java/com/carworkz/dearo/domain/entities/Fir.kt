package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

class Fir() : Parcelable {
    @Json(name = "policeStation")
    var policeStation: String? = null
    @Json(name = "number")
    var number: String? = null
    @Json(name = "date")
    var date: String? = null

    constructor(parcel: Parcel) : this() {
        policeStation = parcel.readString()
        number = parcel.readString()
        date = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(policeStation)
        parcel.writeString(number)
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Fir> {
        override fun createFromParcel(parcel: Parcel): Fir {
            return Fir(parcel)
        }

        override fun newArray(size: Int): Array<Fir?> {
            return arrayOfNulls(size)
        }
    }
}
