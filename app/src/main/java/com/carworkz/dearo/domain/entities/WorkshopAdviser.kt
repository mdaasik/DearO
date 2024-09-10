package com.carworkz.dearo.domain.entities

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WorkshopAdviser(
    @Json(name = "id")
    internal var id: String? = null,
    @Json(name = "name")
    internal var name: String? = null,
    @Json(name = "mobile")
    internal var mobile: String? = null,
    @Json(name = "email")
    internal var email: String? = null,
    @Json(name = "defaultWorkshop")
    internal var defaultWorkshop: String? = null,
    @Json(name = "remarks")
    internal var remarks: String? = null,
    @Json(name = "status")
    internal var status: String? = null,
    @Json(name = "createdOn")
    internal var createdOn: String? = null,
    @Json(name = "updatedOn")
    internal var updatedOn: String? = null
) : SpinnerEntity, Parcelable {

    override fun name(): String = name!!
}
