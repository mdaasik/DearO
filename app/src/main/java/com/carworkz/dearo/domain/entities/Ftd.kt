package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

class Ftd {

    @Json(name = "jobCard")
    var jobCard: Float? = null
    @Json(name = "otc")
    var otc: Float? = null
}
