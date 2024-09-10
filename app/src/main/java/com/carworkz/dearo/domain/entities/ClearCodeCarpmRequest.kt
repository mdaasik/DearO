package com.carworkz.dearo.domain.entities

import com.google.gson.annotations.SerializedName

data class ClearCodeCarpmRequest (
    @SerializedName("remarks") val remark: String,
    @SerializedName("jobCardId") val jobCardId: String
)