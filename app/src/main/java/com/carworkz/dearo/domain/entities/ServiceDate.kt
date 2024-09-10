package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

class ServiceDate {
    @Json(name = "id")
    var id: String? = null
    @Json(name = "workshopId")
    var workshopId: String? = null
    @Json(name = "vehicleId")
    var vehicleId: String? = null
    @Json(name = "serviceDate")
    var serviceDate: String? = null
    @Json(name = "createdOn")
    var createdOn: String? = null
    @Json(name = "updatedOn")
    var updatedOn: String? = null
}