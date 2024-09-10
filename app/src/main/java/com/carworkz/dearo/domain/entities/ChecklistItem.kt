package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json


class ChecklistItem() : Parcelable {
    @field:Json(name = "checked")
    var checked: Boolean = false
    @field:Json(name = "comments")
    var comments: String? = null
    @field:Json(name = "defect")
    var defect: Boolean = false
    @field:Json(name = "group")
    var group: String? = null
    @field:Json(name = "name")
    var name: String? = null

    constructor(parcel: Parcel) : this() {
        checked = parcel.readByte() != 0.toByte()
        comments = parcel.readString()
        defect = parcel.readByte() != 0.toByte()
        group = parcel.readString()
        name = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (checked) 1 else 0)
        parcel.writeString(comments)
        parcel.writeByte(if (defect) 1 else 0)
        parcel.writeString(group)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChecklistItem> {
        override fun createFromParcel(parcel: Parcel): ChecklistItem {
            return ChecklistItem(parcel)
        }

        override fun newArray(size: Int): Array<ChecklistItem?> {
            return arrayOfNulls(size)
        }
    }
}