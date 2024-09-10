package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

data class InvoiceConfig(
        @Json(name = "compositeScheme")
        val compositeScheme: CompositeScheme,
        @Json(name = "enabled")
        val enabled: Boolean,
        @Json(name = "format")
        val format: String,
        @Json(name = "partNumberVisiblity")
        val partNumberVisiblity: Boolean
)

data class CompositeScheme(
        @Json(name = "cgst")
        val cgst: Int,
        @Json(name = "enabled")
        val enabled: Boolean,
        @Json(name = "igst")
        val igst: Int,
        @Json(name = "sgst")
        val sgst: Int
)