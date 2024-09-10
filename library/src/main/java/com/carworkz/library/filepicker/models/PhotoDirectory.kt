package com.carworkz.library.filepicker.models

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


class PhotoDirectory(
                var id: Long = 0,
                var bucketId: String? = null,
                private var coverPath: Uri? = null,
                var name: String? = null,
                var dateAdded: Long = 0,
                val medias: MutableList<Media> = mutableListOf()
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readString(),
        parcel.readLong(),
        TODO("medias")
    )

    fun getCoverPath(): Uri? {
        return when {
            medias.size > 0 -> medias[0].path
            coverPath != null -> coverPath
            else -> null
        };
    }

    fun setCoverPath(coverPath: Uri?) {
        this.coverPath = coverPath
    }

    fun addPhoto(imageId: Long, fileName: String, path: Uri, mediaType: Int) {
        medias.add(Media(imageId, fileName, path, mediaType))
    }

    override fun equals(other: Any?): Boolean {
        return this.bucketId == (other as? PhotoDirectory)?.bucketId
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (bucketId?.hashCode() ?: 0)
        result = 31 * result + (coverPath?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + dateAdded.hashCode()
        result = 31 * result + medias.hashCode()
        return result
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(bucketId)
        parcel.writeParcelable(coverPath, flags)
        parcel.writeString(name)
        parcel.writeLong(dateAdded)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PhotoDirectory> {
        override fun createFromParcel(parcel: Parcel): PhotoDirectory {
            return PhotoDirectory(parcel)
        }

        override fun newArray(size: Int): Array<PhotoDirectory?> {
            return arrayOfNulls(size)
        }
    }
}