package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

class Mtd {

    @Json(name = "jobCard")
    var jobCard: Float? = null
    @Json(name = "otc")
    var otc: Float? = null
}