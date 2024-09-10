package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

class Reschedule {
    @Json(name = "reason")
    internal lateinit var reason: String
    @Json(name = "date")
    internal lateinit var date: String
    @Json(name = "notifyCustomer")
    internal var notifyCustomer: Boolean = false
}