package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

class RecommendedJob(
        @property:Json(name = "id") var id: String? = null,
        @property:Json(name = "text") var text: String? = null,
        @property:Json(name = "source") var source: String? = null,
        @property:Json(name = "price") var price: Int = 0,
        @property:Json(name = "isRecommended") var isRecommended: Boolean = true,
        @property:Json(name = "isComplete") var isComplete: Boolean = false,
        @property:Json(name = "vehicleType") var vehicleType: String? = null) : Parcelable
{

    @IgnoredOnParcel
    var isSelected: Boolean = false

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readString())
    {
        isSelected = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int)
    {
        parcel.writeString(id)
        parcel.writeString(text)
        parcel.writeString(source)
        parcel.writeInt(price)
        parcel.writeByte(if (isRecommended) 1 else 0)
        parcel.writeByte(if (isComplete) 1 else 0)
        parcel.writeString(vehicleType)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int
    {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RecommendedJob>
    {
        var TAG = "RecommendedJob"
        override fun createFromParcel(parcel: Parcel): RecommendedJob
        {
            return RecommendedJob(parcel)
        }

        override fun newArray(size: Int): Array<RecommendedJob?>
        {
            return arrayOfNulls(size)
        }
    }


}
