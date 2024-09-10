package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

class CancelInvoiceConfig {
    @Json(name = "enabled")
    var enabled: Boolean? = false
    @Json(name = "template")
    var template: String? = null
    @Json(name = "checked")
    var checked: Boolean? = false
}
