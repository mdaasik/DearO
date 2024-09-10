package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json
import io.realm.RealmModel
import io.realm.annotations.RealmClass

@RealmClass
open class WhatsAppTemplate : RealmModel {

    @Json(name = "mobile")
    lateinit var mobile: String
    @Json(name = "text")
    lateinit var text: String
}