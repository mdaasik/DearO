package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

data class DoorStep(@Json(name = "enabled") var enabled: Boolean = false)