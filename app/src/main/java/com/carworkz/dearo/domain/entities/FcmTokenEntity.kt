package com.carworkz.dearo.domain.entities

data class FcmTokenEntity(
    val workshopId: String,
    val userId: String,
    val userAccessToken: String,
    val deviceId: String,
    val deviceName: String,
    val platform: String,
    val version: String,
    val fcmAccessToken: String
)