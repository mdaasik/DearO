package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

/**
 * Created by kush on 8/12/17.
 */

data class TimeSlot(
    @Json(name = "date")
    var date: String, // 2017-12-27
    @Json(name = "slots")
    var slots: List<Slot>
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.createTypedArrayList(Slot)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(date)
        parcel.writeTypedList(slots)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TimeSlot> {
        override fun createFromParcel(parcel: Parcel): TimeSlot {
            return TimeSlot(parcel)
        }

        override fun newArray(size: Int): Array<TimeSlot?> {
            return arrayOfNulls(size)
        }
    }
}

data class Slot(
    @Json(name = "time")
    var time: String, // 09:00 AM
    @Json(name = "displayTime")
    var displayTime: String,
    @Json(name = "available")
    var available: Boolean // true
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
            parcel.readByte() != 0.toByte())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(time)
        parcel.writeString(displayTime)
        parcel.writeByte(if (available) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Slot> {
        override fun createFromParcel(parcel: Parcel): Slot {
            return Slot(parcel)
        }

        override fun newArray(size: Int): Array<Slot?> {
            return arrayOfNulls(size)
        }
    }
}