package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

class PdcEntity {
     @field:Json(name = "pdcCompleted")
     var pdcCompleted: Boolean = false
    @field:Json(name = "preDelivery")
     lateinit var preDelivery: PreDelivery
 }