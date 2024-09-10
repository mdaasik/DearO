package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

data class Surveyor(
    @Json(name = "name")
    var name: String?,
    @Json(name = "code")
    var code: String?,
    @Json(name = "mobile")
    var mobile: String?
) : Parcelable {
    constructor() : this(null, null, null)
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(code)
        parcel.writeString(mobile)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Surveyor> {
        override fun createFromParcel(parcel: Parcel): Surveyor {
            return Surveyor(parcel)
        }

        override fun newArray(size: Int): Array<Surveyor?> {
            return arrayOfNulls(size)
        }
    }
}
