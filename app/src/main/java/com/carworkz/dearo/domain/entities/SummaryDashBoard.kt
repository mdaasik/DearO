package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

class SummaryDashBoard {
    @Json(name = "totalFtd")
    var totalFtd: Float? = null
    @Json(name = "totalMtd")
    var totalMtd: Float? = null
}
