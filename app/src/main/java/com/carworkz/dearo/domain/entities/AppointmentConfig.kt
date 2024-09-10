package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

data class AppointmentConfig(
    @Json(name = "enabled")
    var enabled: Boolean,
    @Json(name = "packages")
    var packages: PackagesConfig,
    @Json(name = "bay")
    var bay: BayConfig,
    @Json(name = "notifyCustomer")
    var notifyCustomer: Boolean
)

data class PackagesConfig(
    @Json(name = "enabled")
    var enabled: Boolean,
    @Json(name = "mandatory")
    var mandatory: Boolean
)

data class BayConfig(
    @Json(name = "enabled")
    var enabled: Boolean
)
