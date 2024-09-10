package com.carworkz.dearo.domain.entities

import com.carworkz.dearo.searchabledialog.Searchable
import com.squareup.moshi.Json
import io.realm.RealmObject

open class State : RealmObject(), Searchable {
    override fun getText(): String {
        return state!!
    }

    @Json(name = "selected")
    var selected: Boolean = false
    @Json(name = "state")
    var state: String? = null
    @Json(name = "stateCode")
    var stateCode: Int = -1
}