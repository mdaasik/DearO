package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

/**
 * Created by kush on 18/8/17.
 */

class Model() : Parcelable {
    @Json(name = "makeSlug")
    var makeSlug: String? = null
    @Json(name = "name")
    var name: String? = null
    @Json(name = "slug")
    var slug: String? = null
    @Json(name = "where")
    var status: String? = null

    constructor(parcel: Parcel) : this() {
        makeSlug = parcel.readString()
        name = parcel.readString()
        slug = parcel.readString()
        status = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(makeSlug)
        parcel.writeString(name)
        parcel.writeString(slug)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Model> {
        override fun createFromParcel(parcel: Parcel): Model {
            return Model(parcel)
        }

        override fun newArray(size: Int): Array<Model?> {
            return arrayOfNulls(size)
        }
    }
}
