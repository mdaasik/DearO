package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json
import org.jetbrains.annotations.Nullable

/**
 * Created by Farhan on 19/9/17.
 */
data class Brand(
    @Nullable
    @Json(name = "name")
    internal var name: String,
    @Nullable
    @Json(name = "id")
    internal var id: String?
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
            parcel.readString())

    override fun toString() = "{ Name : $name }"

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Brand> {
        override fun createFromParcel(parcel: Parcel): Brand {
            return Brand(parcel)
        }

        override fun newArray(size: Int): Array<Brand?> {
            return arrayOfNulls(size)
        }
    }
}