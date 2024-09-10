package com.carworkz.dearo.domain.entities

import android.os.Parcelable

import com.carworkz.dearo.JsonStringToMap
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Notification(
    @field:Json(name = "screen") val screen: String?,
    @field:Json(name = "category") val category: String?,
    @field:Json(name = "title") val title: String?,
    @field:Json(name = "message") val message: String?,
    @field:Json(name = "version") val version: String,
    @field:Json(name = "task") val task: String,
    @JsonStringToMap
    @field:Json(name = "options") val options: Map<String, String>?
) : Parcelable {

    // creating unique id for each notification
    // will be used to update same notification if required.
    val signature: Int
        get() = (screen + category + title + message).hashCode()

    override fun toString(): String {
        return "screen $screen category $category title $title message $message notification versison $version options $options"
    }
}
