package com.carworkz.library.filepicker.models

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable


open class BaseFile(open var id: Long = 0,
                    open var name: String,
                    open var path: Uri
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readParcelable(Uri::class.java.classLoader)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeParcelable(path, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BaseFile> {
        override fun createFromParcel(parcel: Parcel): BaseFile {
            return BaseFile(parcel)
        }

        override fun newArray(size: Int): Array<BaseFile?> {
            return arrayOfNulls(size)
        }
    }
}