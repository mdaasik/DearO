package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json
import io.realm.RealmObject

open class Meta() : RealmObject(), Parcelable {
    @Json(name = "category")
    lateinit var category: String

    constructor(parcel: Parcel) : this() {
        category = parcel.readString().toString()
    }

    override fun toString(): String {
        return "Meta(category='$category')"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(category)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Meta> {
        override fun createFromParcel(parcel: Parcel): Meta {
            return Meta(parcel)
        }

        override fun newArray(size: Int): Array<Meta?> {
            return arrayOfNulls(size)
        }
    }
}