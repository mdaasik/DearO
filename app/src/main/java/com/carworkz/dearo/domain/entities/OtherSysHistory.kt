package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.carworkz.dearo.othersyshistory.OtherSysCustomer
import com.carworkz.dearo.othersyshistory.OtherSysInvoice
import com.squareup.moshi.Json
public class OtherSysHistory() : Parcelable
{
    @Json(name = "invoices")
    internal var invoices: MutableList<OtherSysInvoice>? = null

    @Json(name = "customer")
    internal var customer:OtherSysCustomer? =null

    @Json(name = "vehicle")
    internal var vehicle:OtherVehicle? =null

    constructor(parcel: Parcel) : this() {
        vehicle = parcel.readParcelable(OtherVehicle::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(vehicle, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OtherSysHistory> {
        override fun createFromParcel(parcel: Parcel): OtherSysHistory {
            return OtherSysHistory(parcel)
        }

        override fun newArray(size: Int): Array<OtherSysHistory?> {
            return arrayOfNulls(size)
        }
    }
}