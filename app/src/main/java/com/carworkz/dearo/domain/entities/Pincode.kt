package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

/**
 * Created by kush on 18/10/17.
 */
class Pincode {
    @Json(name = "code")
    private var code: Int? = null
    @Json(name = "stateSlug")
    private var stateSlug: String? = null
    @Json(name = "citySlug")
    private var citySlug: String? = null
    @Json(name = "updatedOn")
    private var updatedOn: String? = null
    @Json(name = "city")
    var city: City? = null
}