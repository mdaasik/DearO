package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

class Total {
    @Json(name = "ftd")
    var ftd: Float? = null
    @Json(name = "mtd")
    var mtd: Float? = null
}
