package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

class InvoiceRemarks {

    @Json(name = "remarks")
    internal var remarks: List<Remark> = ArrayList()

    @Json(name = "jobs")
    internal var jobs: List<RecommendedJob> = ArrayList()

    internal lateinit var jobIds: List<String>
}