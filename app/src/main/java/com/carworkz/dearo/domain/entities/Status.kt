package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

/**
 * Created by farhan on 06/01/18.
 */
class Status() : Parcelable {

    @Json(name = "status")
    internal var status: String? = null
    @Json(name = "date")
    private var dateTime: String? = null

    constructor(parcel: Parcel) : this() {
        status = parcel.readString()
        dateTime = parcel.readString()
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String) {
        this.status = status
    }

    fun getDateTime(): String? {
        return dateTime
    }

    fun setDateTime(dateTime: String) {
        this.dateTime = dateTime
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(status)
        parcel.writeString(dateTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Status> {
        override fun createFromParcel(parcel: Parcel): Status {
            return Status(parcel)
        }

        override fun newArray(size: Int): Array<Status?> {
            return arrayOfNulls(size)
        }
    }
}
