package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

data class LabourSurchargeConfig(@Json(name = "enabled") val enabled: Boolean, @Json(name = "maxMechanicalLimit") val maxMechanicalLimit: Double, @Json(name = "maxAccidentalLimit") val maxAccidentalLimit: Double)