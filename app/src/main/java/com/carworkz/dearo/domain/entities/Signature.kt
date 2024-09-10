package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

class Signature() : Parcelable {

    @Json(name = "id")
    var id: String? = null
    @Json(name = "name")
    var name: String? = null
    @Json(name = "originalName")
    var originalName: String? = null
    @Json(name = "url")
    var url: String? = null
    @Json(name = "meta")
    var meta: Meta? = null
    @Json(name = "createdOn")
    var createdOn: String? = null
    @Json(name = "updatedOn")
    var updatedOn: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        name = parcel.readString()
        originalName = parcel.readString()
        url = parcel.readString()
        meta = parcel.readParcelable(Meta::class.java.classLoader)
        createdOn = parcel.readString()
        updatedOn = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(originalName)
        parcel.writeString(url)
        parcel.writeParcelable(meta, flags)
        parcel.writeString(createdOn)
        parcel.writeString(updatedOn)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Signature> {
        override fun createFromParcel(parcel: Parcel): Signature {
            return Signature(parcel)
        }

        override fun newArray(size: Int): Array<Signature?> {
            return arrayOfNulls(size)
        }
    }
}