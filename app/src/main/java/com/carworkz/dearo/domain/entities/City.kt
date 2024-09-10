package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

/**
 * Created by kush on 18/10/17.
 */

class City {
    @Json(name = "city")
    var city: String? = null
    @Json(name = "citySlug")
    var citySlug: String? = null
    @Json(name = "state")
    var state: String? = null
    @Json(name = "stateSlug")
    var stateSlug: String? = null
    @Json(name = "stateCode")
    var stateCode: Int? = null
    @Json(name = "status")
    var status: Boolean? = null
    @Json(name = "id")
    var id: String? = null
}
