package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

/**
 * Created by Kush Singh Chibber on 8/22/2017.
 */

open class FileObject() : RealmObject(), Parcelable {

    @Required
    @PrimaryKey
    internal var pid: String = UUID.randomUUID().toString()

    var jobCardID: String? = null

    var uri: String? = null

    var isUploaded: Boolean = false

    var isDeleted: Boolean = false

    @Json(name = "id")
    var id: String? = null
    @Json(name = "name")
    var name: String? = null
    @Json(name = "title")
    var caption: String? = null
    var title = caption
    @Json(name = "originalName")
    var originalName: String = ""
    @Json(name = "mime")
    var mime: String? = null
    @Json(name = "url")
    var url: String? = null
    @Json(name = "meta")
    var meta: Meta? = null
    @Json(name = "type")
    var type: String? = null
    @Json(name = "path")
    internal var path: String? = null
    @Json(name = "uploadableId")
    var uploadableId: String? = null
    @Json(name = "uploadableType")
    internal var uploadableType: String? = null
    @Json(name = "createdOn")
    internal var createdOn: String? = null
    @Json(name = "updatedOn")
    internal var updatedOn: String? = null
    @Json(name = "customScope")
    internal var customScope: String? = null

    constructor(parcel: Parcel) : this() {
        pid = parcel.readString().toString()
        jobCardID = parcel.readString()
        uri = parcel.readString()
        isUploaded = parcel.readByte() != 0.toByte()
        isDeleted = parcel.readByte() != 0.toByte()
        id = parcel.readString()
        name = parcel.readString()
        caption = parcel.readString()
        title = parcel.readString()
        originalName = parcel.readString().toString()
        mime = parcel.readString()
        url = parcel.readString()
        meta = parcel.readParcelable(Meta::class.java.classLoader)
        type = parcel.readString()
        path = parcel.readString()
        uploadableId = parcel.readString()
        uploadableType = parcel.readString()
        createdOn = parcel.readString()
        updatedOn = parcel.readString()
        customScope = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(pid)
        parcel.writeString(jobCardID)
        parcel.writeString(uri)
        parcel.writeByte(if (isUploaded) 1 else 0)
        parcel.writeByte(if (isDeleted) 1 else 0)
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(caption)
        parcel.writeString(title)
        parcel.writeString(originalName)
        parcel.writeString(mime)
        parcel.writeString(url)
        parcel.writeParcelable(meta, flags)
        parcel.writeString(type)
        parcel.writeString(path)
        parcel.writeString(uploadableId)
        parcel.writeString(uploadableType)
        parcel.writeString(createdOn)
        parcel.writeString(updatedOn)
        parcel.writeString(customScope)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "FileObject(pid='$pid', jobCardID=$jobCardID, uri=$uri, isUploaded=$isUploaded, isDeleted=$isDeleted, id=$id, name=$name, caption=$caption, title=$title, originalName='$originalName', mime=$mime, url=$url, meta=$meta, type=$type, path=$path, uploadableId=$uploadableId, uploadableType=$uploadableType, createdOn=$createdOn, updatedOn=$updatedOn,customScope=$customScope)"
    }

    companion object CREATOR : Parcelable.Creator<FileObject> {
//        const val PDC_AND_DAMAGES = "PDC"
        const val PDC_AND_DAMAGES = "Pre-Delivery Check"
        const val INSPECTION_AND_DAMAGES = "Inspection And Damages"
        const val WORK_IN_PROGESS = "Work In Progress"
        const val FILE_TYPE_DAMAGE = "DAMAGE"
        const val FILE_TYPE_ACCIDENTAL = "ACCIDENTAL"
        const val FILE_TYPE_PDC = "pdc"

        override fun createFromParcel(parcel: Parcel): FileObject {
            return FileObject(parcel)
        }

        override fun newArray(size: Int): Array<FileObject?> {
            return arrayOfNulls(size)
        }
    }
}
