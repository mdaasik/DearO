package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

/**
 * Created by Kush Singh Chibber on 1/6/2018.
 */

data class AppointmentHistory(
    @Json(name = "status")
    var status: List<Status>? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.createTypedArrayList(Status))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AppointmentHistory> {
        override fun createFromParcel(parcel: Parcel): AppointmentHistory {
            return AppointmentHistory(parcel)
        }

        override fun newArray(size: Int): Array<AppointmentHistory?> {
            return arrayOfNulls(size)
        }
    }
}
