package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

/**
 * Created by kush on 26/9/17.
 */
class PDF() : Parcelable {

    @Json(name = "url")
    internal var url: String? = null
    @Json(name = "name")
    internal var name: String? = null
    @Json(name = "invoiceId")
    internal var invoiceId: String? = null
    @Json(name = "customScope")
    internal var customScope: String? = null
    @Json(name = "invoiceLabel")
    internal var invoiceLabel: String? = null

    @Transient
    internal var title: String? = null

    constructor(parcel: Parcel) : this() {
        url = parcel.readString()
        name = parcel.readString()
        invoiceId = parcel.readString()
        customScope = parcel.readString()
        invoiceLabel = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
        parcel.writeString(name)
        parcel.writeString(invoiceId)
        parcel.writeString(customScope)
        parcel.writeString(invoiceLabel)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PDF> {
        override fun createFromParcel(parcel: Parcel): PDF {
            return PDF(parcel)
        }

        override fun newArray(size: Int): Array<PDF?> {
            return arrayOfNulls(size)
        }
    }
}