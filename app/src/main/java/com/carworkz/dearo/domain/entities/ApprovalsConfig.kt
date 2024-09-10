package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

class ApprovalsConfig {
    @Json(name = "enabled")
    val enabled: Boolean = false
}