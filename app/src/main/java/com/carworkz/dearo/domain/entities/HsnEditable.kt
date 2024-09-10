package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

class HsnEditable {

    @Json(name = "enabled")
    val enabled: Boolean? = null
    @Json(name = "part")
    val part: Boolean? = null
    @Json(name = "labour")
    val labour: Boolean? = null
}
