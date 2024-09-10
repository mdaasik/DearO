package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

internal class AccidentalConfig {

    @Json(name = "enabled")
    var enabled: Boolean = false
}
