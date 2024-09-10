package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

/**
 * Created by kush on 18/8/17.
 */

class Make() : Parcelable {

    @Json(name = "name")
    var name: String? = null
    @Json(name = "slug")
    var slug: String? = null
    @Json(name = "where")
    var status: String? = null

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        slug = parcel.readString()
        status = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(slug)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Make> {
        override fun createFromParcel(parcel: Parcel): Make {
            return Make(parcel)
        }

        override fun newArray(size: Int): Array<Make?> {
            return arrayOfNulls(size)
        }
    }
}
