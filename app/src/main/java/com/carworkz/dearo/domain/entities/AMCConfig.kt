package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

class AMCConfig {
    @Json(name = "enabled")
    var enabled: Boolean = true
}
