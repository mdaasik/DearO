package com.carworkz.dearo.domain.entities


import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomerSourceType(
    @Json(name = "createdBy")
    var createdBy: String?, // 1630323391543442
    @Json(name = "createdOn")
    var createdOn: String?,
    @Json(name = "id")
    var id: String?,// 2022-07-23T08:41:39.899Z
    // 1658565699899624
    @Json(name = "sourceType")
    var sourceType: String?, // LEAFLET
    @Json(name = "updatedBy")
    var updatedBy: String?, // 1630323391543442
    @Json(name = "updatedOn")
    var updatedOn: String? // 2022-07-23T08:41:39.899Z
) : Parcelable{
    override fun toString(): String {
        return sourceType!!
    }
}