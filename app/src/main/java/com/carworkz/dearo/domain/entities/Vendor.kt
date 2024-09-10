package com.carworkz.dearo.domain.entities


import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

/**
 * @author Mehul KAdam
 *
 */

class Vendor() : Parcelable {
    @Json(name = "code")
    var code: String="" // string
    @Json(name = "id")
    var id: String=""// string
    @Json(name = "name")
    var name: String="" // string
    @Json(name = "vendorMargin")
    var vendorMargin: Int = 0 // 0
    @Json(name = "vendorType")
    var vendorType: String="" // string

    constructor(parcel: Parcel) : this() {
        code = parcel.readString().toString()
        id = parcel.readString().toString()
        name = parcel.readString().toString()
        vendorMargin = parcel.readInt()
        vendorType = parcel.readString().toString()
    }


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest?.writeString(code)
        dest?.writeString(id)
        dest?.writeString(name)
        dest?.writeInt(vendorMargin)
        dest?.writeString(vendorType)
    }

    companion object CREATOR : Parcelable.Creator<Vendor> {
        const val TYPE_OSL_SUPPLIER="OSL Supplier"
        const val TYPE_PART_SUPPLIER="Part Supplier"
        override fun createFromParcel(parcel: Parcel): Vendor {
            return Vendor(parcel)
        }

        override fun newArray(size: Int): Array<Vendor?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return name
    }
}