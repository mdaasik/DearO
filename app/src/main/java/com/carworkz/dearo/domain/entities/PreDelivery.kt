package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

class PreDelivery() : Parcelable {
    @field:Json(name = "checklist")
    lateinit var checklist: List<ChecklistItem>

    @field:Json(name = "inspectionDate")
    var inspectionDate: String = ""

    @field:Json(name = "inspectorName")
    var inspectorName: String = ""

    @field:Json(name = "remarks")
    var remarks: String = ""

    @field:Json(name = "roadTest")
    lateinit var roadTest: RoadTest

    constructor(parcel: Parcel) : this() {
        checklist = parcel.createTypedArrayList(ChecklistItem)!!
        inspectionDate = parcel.readString().toString()
        inspectorName = parcel.readString().toString()
        remarks = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(checklist)
        parcel.writeString(inspectionDate)
        parcel.writeString(inspectorName)
        parcel.writeString(remarks)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PreDelivery> {
        override fun createFromParcel(parcel: Parcel): PreDelivery {
            return PreDelivery(parcel)
        }

        override fun newArray(size: Int): Array<PreDelivery?> {
            return arrayOfNulls(size)
        }
    }
}
