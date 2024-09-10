package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

 class RoadTest {
     @field:Json(name = "datetime")
     lateinit var datetime: String

     @field:Json(name = "endKms")
      var endKms: Int=0

     @field:Json(name = "startKms")
        var startKms: Int=0
 }