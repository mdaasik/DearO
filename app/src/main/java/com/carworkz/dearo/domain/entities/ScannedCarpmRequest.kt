package com.carworkz.dearo.domain.entities

import com.google.gson.annotations.SerializedName

data class ScannedCarpmRequest (
    @SerializedName("scanId") val scanId: String,
    @SerializedName("jobCardId") val jobCardId: String
)