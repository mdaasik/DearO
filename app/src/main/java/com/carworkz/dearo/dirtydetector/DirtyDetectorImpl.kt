package com.carworkz.dearo.dirtydetector

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

internal class DirtyDetectorImpl : DirtyDetector() {

    override fun observe(parcelable: Parcelable) {
        var parcel: Parcel? = null
        try {
            parcel = Parcel.obtain()
            parcel!!.writeParcelable(parcelable, 0)
            parcel.setDataPosition(0)
            originalObject = parcelable
            clonedCopy = parcel.readParcelable(parcelable.javaClass.classLoader)
        } finally {
            parcel?.recycle()
        }
    }

    override fun observe(serializable: Serializable) {
        var parcel: Parcel? = null
        try {
            parcel = Parcel.obtain()
            parcel!!.writeSerializable(serializable)
            parcel.setDataPosition(0)
            originalObject = serializable
            clonedCopy = parcel.readSerializable()
        } finally {
            parcel?.recycle()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <E : Parcelable> observe(list: ArrayList<E>, elementClassLoader: ClassLoader) {
        var parcel: Parcel? = null
        try {
            parcel = Parcel.obtain()
            parcel.writeList(list)
            parcel.setDataPosition(0)
            originalObject = list
            clonedCopy = arrayListOf<E>()
            parcel.readList(clonedCopy as ArrayList<E>, elementClassLoader)
        } finally {
            parcel?.recycle()
        }
    }
}
