package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

data class Checklist (

	@field:Json(name="name") val name : String
)