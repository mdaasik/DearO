package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

/**
 * Created by Farhan on 19/9/17.
 */
data class TaxGst(@Json(name = "percentage") private val percentage: Int, @Json(name = "amount") private var amount: Double)