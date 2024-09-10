package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

class WhatsAppConfig {

    @Json(name = "enabled")
    var enabled: Boolean = false
    @Json(name = "createJobCard")
    var createJobCard: CreateJobCardConfig? = null
    @Json(name = "completeJobCard")
    var completeJobCard: CompleteJobCardConfig? = null
    @Json(name = "closeJobCard")
    var closeJobCard: CloseJobCardConfig? = null
    @Json(name = "createInvoice")
    var createInvoice: CreateInvoiceConfig? = null
    @Json(name = "cancelInvoice")
    var cancelInvoice: CancelInvoiceConfig? = null
}