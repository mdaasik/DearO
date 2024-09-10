package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

class WorkshopBay : SpinnerEntity {
    @Json(name = "id")
    internal var id: String? = null
    @Json(name = "workshopId")
    internal var workshopId: String? = null
    @Json(name = "name")
    internal var name: String? = null
    @Json(name = "type")
    internal var type: String? = null
    @Json(name = "createdOn")
    internal var createdOn: String? = null
    @Json(name = "updatedOn")
    internal var updatedOn: String? = null

    override fun name(): String = name!!
}
