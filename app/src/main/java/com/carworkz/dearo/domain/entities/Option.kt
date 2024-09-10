package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Option : RealmObject(), SpinnerEntity
 {
    @Json(name = "id")
    @PrimaryKey
    lateinit var  id: String
    @Json(name = "name")
    lateinit var name: String


     override fun name(): String=name

 }