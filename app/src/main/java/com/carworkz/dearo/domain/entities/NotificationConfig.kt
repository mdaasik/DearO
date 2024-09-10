package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

class NotificationConfig {
    @Json(name = "enabled")
    var enabled: Boolean = false
    @Json(name = "sms")
    var sms: SmsConfig? = null
    @Json(name = "whatsapp")
    var whatsapp: WhatsAppConfig? = null
}