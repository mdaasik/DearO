package com.carworkz.dearo.domain.entities

data class Redemption(
    val invoiceId: String,
    val name: String,
    val type: String,
    val mode: String,
    val cost: Double
)
