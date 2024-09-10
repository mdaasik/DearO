package com.carworkz.dearo.domain.entities

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomerVehicleDetails(
    @property:Json(name = "id") internal var id: String,
    @property:Json(name = "workshopId") internal var workshopId: String,
    @property:Json(name = "customer") internal var customer: Customer,
    @property:Json(name = "vehicle") internal var vehicle: Vehicle,
    @property:Json(name = "history") internal var history: List<History>?,
    @property:Json(name = "lastJobCardDate") internal val lastJobCardDate: String,
    @property:Json(name = "historyCount") internal val historyCount: Int
) : Parcelable
