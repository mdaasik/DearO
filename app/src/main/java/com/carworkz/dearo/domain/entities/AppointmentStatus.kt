package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

class AppointmentStatus() : Parcelable {
    @Json(name = "id")
    internal var id: String? = null

    @Json(name = "status")
    internal var status: String? = null

    @Json(name = "remarks")
    internal var remarks: String? = null

    @Json(name = "date")
    internal var date: String? = null

    @Json(name = "appointmentStatus")
    internal var appointmentStatus: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        status = parcel.readString()
        remarks = parcel.readString()
        date = parcel.readString()
        appointmentStatus = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(status)
        parcel.writeString(remarks)
        parcel.writeString(date)
        parcel.writeString(appointmentStatus)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AppointmentStatus> {
        val STATUS_LIST = arrayListOf(
            "Appointment Rescheduled",
            "Not Contactable",
            "Call Back/Under Follow Up",
            "Hung Up/Refuse to Speak",
            "Service Done from Outside",
            "Service Not required",
            "Vehicle Sold",
            "Wrong Number",
            "Appointment Cancelled",
            "Others"
        )
        override fun createFromParcel(parcel: Parcel): AppointmentStatus {
            return AppointmentStatus(parcel)
        }

        override fun newArray(size: Int): Array<AppointmentStatus?> {
            return arrayOfNulls(size)
        }
    }
}