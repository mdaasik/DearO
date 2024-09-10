package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

class WorkOrderConfig {
    @Json(name = "enabled")
    val enabled: Boolean = false
}