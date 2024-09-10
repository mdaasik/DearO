package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

data class ServiceAdviserAccessConfig(@Json(name = "enabled") val enabled: Boolean, @Json(name = "serviceAdvisor") val serviceAdvisor: Boolean)