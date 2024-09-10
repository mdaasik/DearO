package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

data class CustomerGroup(
    @Json(name = "enabled")
    val enabled: Boolean,
    @Json(name = "options")
    val options: List<Option>
)