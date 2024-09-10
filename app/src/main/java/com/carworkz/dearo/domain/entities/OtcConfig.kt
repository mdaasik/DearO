package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

internal class OtcConfig {
    @Json(name = "enabled")
    var enabled: Boolean = false
}
