package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable

data class FilterMap(var key: String, var value: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(key)
        parcel.writeString(value)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FilterMap> {
        override fun createFromParcel(parcel: Parcel): FilterMap {
            return FilterMap(parcel)
        }

        override fun newArray(size: Int): Array<FilterMap?> {
            return arrayOfNulls(size)
        }
    }
}