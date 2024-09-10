package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

data class VehicleType(@Json(name = "enabled") val enabled: Boolean, @Json(name = "values") val values: List<String>) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readByte() != 0.toByte(),
        parcel.createStringArrayList()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (enabled) 1 else 0)
        parcel.writeStringList(values)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VehicleType> {

        const val VEHICLE_TYPE_CAR = "cars"
        const val VEHICLE_TYPE_BIKE = "bikes"

        override fun createFromParcel(parcel: Parcel): VehicleType {
            return VehicleType(parcel)
        }

        override fun newArray(size: Int): Array<VehicleType?> {
            return arrayOfNulls(size)
        }
    }
}