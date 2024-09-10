package com.carworkz.dearo.othersyshistory

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import com.carworkz.dearo.domain.entities.Labour
import com.carworkz.dearo.domain.entities.Part
import com.carworkz.dearo.domain.entities.Summary
import com.squareup.moshi.Json

class OtherSysInvoice(): Parcelable
{
    @Json(name = "invoiceDate")
    internal var invoiceDate: String? = null

    @Json(name = "insuranceInvoiceNo")
    internal var insuranceInvoiceNo: String? = null

    @Json(name = "invoiceNo")
    internal var invoiceNo: String? = null

    @Json(name = "jobCardNo")
    internal var jobCardNo: String? = null

    @Json(name = "source")
    internal var source: String? = null

    @Json(name = "parts")
    internal var parts: List<Part>? = null

    @Json(name = "labours")
    internal var labours: List<Labour>? = null

    constructor(parcel: Parcel) : this() {
        invoiceDate = parcel.readString()
        insuranceInvoiceNo = parcel.readString()
        invoiceNo = parcel.readString()
        jobCardNo = parcel.readString()
        source = parcel.readString()
        parts = parcel.createTypedArrayList(Part)
        labours = parcel.createTypedArrayList(Labour)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(invoiceDate)
        parcel.writeString(insuranceInvoiceNo)
        parcel.writeString(invoiceNo)
        parcel.writeString(jobCardNo)
        parcel.writeString(source)
        parcel.writeTypedList(parts)
        parcel.writeTypedList(labours)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OtherSysInvoice> {
        override fun createFromParcel(parcel: Parcel): OtherSysInvoice {
            return OtherSysInvoice(parcel)
        }

        override fun newArray(size: Int): Array<OtherSysInvoice?> {
            return arrayOfNulls(size)
        }
    }
}