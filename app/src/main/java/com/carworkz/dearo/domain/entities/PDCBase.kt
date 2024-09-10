package com.carworkz.dearo.domain.entities
import com.squareup.moshi.Json


data class PdcBase (

	@field:Json(name="group") val group : String,
	@field:Json(name="ownerId") val ownerId : Int,
	@field:Json(name="ownerType") val ownerType : String,
	@field:Json(name="checklist") val checklist : List<Checklist>
)