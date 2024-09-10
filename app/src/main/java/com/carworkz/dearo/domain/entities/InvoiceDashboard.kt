package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

class InvoiceDashboard {
    @Json(name = "labour")
    var labour: Labour? = null
    @Json(name = "servicePackage")
    var servicePackage: ServicePackage? = null
    @Json(name = "part")
    var part: Part? = null
    @Json(name = "amc")
    var amc: AMC? = null
}