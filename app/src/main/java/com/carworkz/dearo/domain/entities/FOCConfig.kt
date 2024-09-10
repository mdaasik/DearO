package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

data class FOCConfig(
    @Json(name = "enabled")
    val enabled: Boolean
)
