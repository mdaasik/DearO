package com.carworkz.library.filepicker.models

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.android.parcel.Parcelize


class FileType constructor(
        var title: String,
        var extensions : Array<String>,
        @DrawableRes
        var drawable: Int
) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readString()!!,
                parcel.createStringArray()!!,
                parcel.readInt()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(title)
                parcel.writeStringArray(extensions)
                parcel.writeInt(drawable)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<FileType> {
                override fun createFromParcel(parcel: Parcel): FileType {
                        return FileType(parcel)
                }

                override fun newArray(size: Int): Array<FileType?> {
                        return arrayOfNulls(size)
                }
        }
}