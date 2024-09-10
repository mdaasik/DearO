package com.carworkz.dearo.domain.entities


import com.squareup.moshi.Json

data class ValidationConfig(
        @Json(name = "enabled")
        val enabled: Boolean,
        @Json(name = "insurance")
        val insurance: Boolean,
        @Json(name = "vinNumber")
        val vinNumber: Boolean
)