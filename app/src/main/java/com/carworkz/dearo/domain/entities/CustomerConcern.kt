package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

data class CustomerConcern(@Json(name = "id") val id: String, @Json(name = "name") val name: String)