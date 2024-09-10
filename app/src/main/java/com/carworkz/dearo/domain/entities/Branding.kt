package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

data class Branding(@Json(name = "enabled") val enabled: Boolean)