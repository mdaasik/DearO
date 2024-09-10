package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

class History() : Parcelable {

    @Json(name = "id")
    internal var id: String? = null
    @Json(name = "vehicleId")
    internal var vehicleId: String? = null
    @Json(name = "jobCardId")
    internal var jobCardId: String? = null
    @Json(name = "workshopId")
    internal var workshopId: String? = null
    @Json(name = "customerId")
    internal var customerId: String? = null
    @Json(name = "status")
    internal var status: String? = null
    @Json(name = "step")
    internal var step: String? = null
    @Json(name = "stepLabel")
    internal var stepLabel: String? = null
    @Json(name = "jobs")
    internal var jobs: List<String>? = null
    @Json(name = "remarks")
    internal var remarks: List<Remark>? = null
    @Json(name = "inProgressDate")
    internal var inProgressDate: String? = null
    @Json(name = "completionDate")
    internal var completionDate: String? = null
    @Json(name = "estimate")
    internal var estimate: Estimate? = null
    @Json(name = "invoice")
    internal var invoice: Invoice? = null
    @Json(name = "createdOn")
    internal var createdOn: String? = null
    @Json(name = "updatedOn")
    internal var updatedOn: String? = null
    @Json(name = "unapprovedJobs")
    internal var unapprovedJobs: List<String>? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        vehicleId = parcel.readString()
        jobCardId = parcel.readString()
        workshopId = parcel.readString()
        customerId = parcel.readString()
        status = parcel.readString()
        step = parcel.readString()
        stepLabel = parcel.readString()
        jobs = parcel.createStringArrayList()
        remarks = parcel.createTypedArrayList(Remark.CREATOR);
        inProgressDate = parcel.readString()
        completionDate = parcel.readString()
        estimate = parcel.readParcelable(Estimate::class.java.classLoader)
        invoice = parcel.readParcelable(Invoice::class.java.classLoader)
        createdOn = parcel.readString()
        updatedOn = parcel.readString()
        unapprovedJobs = parcel.createStringArrayList()
    }



    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(vehicleId)
        parcel.writeString(jobCardId)
        parcel.writeString(workshopId)
        parcel.writeString(customerId)
        parcel.writeString(status)
        parcel.writeString(step)
        parcel.writeString(stepLabel)
        parcel.writeStringList(jobs)
        parcel.writeTypedList(remarks)
        parcel.writeString(inProgressDate)
        parcel.writeString(completionDate)
        parcel.writeParcelable(estimate, flags)
        parcel.writeParcelable(invoice, flags)
        parcel.writeString(createdOn)
        parcel.writeString(updatedOn)
        parcel.writeStringList(unapprovedJobs)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<History> {
        override fun createFromParcel(parcel: Parcel): History {
            return History(parcel)
        }

        override fun newArray(size: Int): Array<History?> {
            return arrayOfNulls(size)
        }
    }


}