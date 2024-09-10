package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

/**
 * Created by ambab on 8/1/18.
 */

class UpsertDetails {

    @Json(name = "variantCode")
    internal lateinit var variantCode: String
    @Json(name = "name")
    internal lateinit var name: String
    @Json(name = "pincode")
    internal var pincode: Int = 0
    @Json(name = "customer")
    internal lateinit var customer: Customer
    @Json(name = "address")
    internal lateinit var address: Address
    @Json(name = "vehicle")
    internal lateinit var vehicle: Vehicle
@Json(name = "id")
internal lateinit var id: String
}