package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json


data class Remark(
        @Json(name = "remark")
        internal var remark: String? = "",
        @Json(name = "type")
        internal var type: String? = "yellow"
): Parcelable
{
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString())
    {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int)
    {
        parcel.writeString(remark)
        parcel.writeString(type)
    }

    override fun describeContents(): Int
    {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Remark>
    {
        override fun createFromParcel(parcel: Parcel): Remark
        {
            return Remark(parcel)
        }

        override fun newArray(size: Int): Array<Remark?>
        {
            return arrayOfNulls(size)
        }
    }
}