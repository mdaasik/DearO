package com.carworkz.dearo.domain.entities


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomerSource(
    @Json(name = "createdBy")
    var createdBy: String?, // 1630323391543442
    @Json(name = "createdOn")
    var createdOn: String?, // 2022-07-23T08:53:28.307Z
    @Json(name = "id")
    var id: String?, // 1658566408307345
    @Json(name = "source")
    var source: String?, // PAMPHLET DISTRIBUTION
    @Json(name = "sourceTypeId")
    var sourceTypeId: String?, // 1658565699899624
    @Json(name = "updatedBy")
    var updatedBy: String?, // 1630323391543442
    @Json(name = "updatedOn")
    var updatedOn: String? // 2022-07-23T08:53:28.307Z
) : Parcelable{
    override fun toString(): String {
        return source!!
    }
}