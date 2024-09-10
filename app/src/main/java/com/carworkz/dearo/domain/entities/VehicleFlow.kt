package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

class VehicleFlow {

    @Json(name = "ftd")
    var ftd: Int? = null
    @Json(name = "mtd")
    var mtd: Int? = null
}