package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

class WorkshopTechnician : SpinnerEntity {

    @Json(name = "id")
    internal var id: String? = null
    @Json(name = "workshopId")
    internal var workshopId: String? = null
    @Json(name = "name")
    internal var name: String? = null
    @Json(name = "status")
    internal var status: String? = null

    override fun name(): String = name!!
}
