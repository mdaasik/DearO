package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

data class PreDeliveryConfig(
        @Json(name = "enabled")
        val enabled: Boolean,
        @Json(name = "mode")
        val mode: String
)