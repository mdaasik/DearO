package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

/**
 * Created by farhan on 08/01/18.
 */

class AppointmentPost {

    @Json(name = "packageIds")
    var packageIds: List<String>? = null

    @Json(name = "date")
    var date: String? = null

    @Json(name = "force")
    var force: Boolean = false

    @Json(name = "remarks")
    var remarks: String? = null

    @Json(name = "serviceAdvisorId")
    var serviceAdvisorId: String? = null
}
