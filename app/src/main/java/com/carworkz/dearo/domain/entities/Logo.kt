package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

class Logo() : Parcelable {

    @Json(name = "id")
    var id: String? = null
    @Json(name = "name")
    var name: String? = null
    @Json(name = "originalName")
    var originalName: String? = null
    @Json(name = "url")
    var url: String? = null
    @Json(name = "createdOn")
    var createdOn: String? = null
    @Json(name = "updatedOn")
    var updatedOn: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        name = parcel.readString()
        originalName = parcel.readString()
        url = parcel.readString()
        createdOn = parcel.readString()
        updatedOn = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(originalName)
        parcel.writeString(url)
        parcel.writeString(createdOn)
        parcel.writeString(updatedOn)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Logo> {
        override fun createFromParcel(parcel: Parcel): Logo {
            return Logo(parcel)
        }

        override fun newArray(size: Int): Array<Logo?> {
            return arrayOfNulls(size)
        }
    }
}