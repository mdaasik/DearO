package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

class WorkshopResource {

    @Json(name = "workshopAdviser")
    internal var workshopAdviser: List<WorkshopAdviser> = ArrayList()
    @Json(name = "workshopTechnician")
    internal var workshopTechnician: List<WorkshopTechnician> = ArrayList()
    @Json(name = "workshopBay")
    internal var workshopBay: List<WorkshopBay> = ArrayList()
    @Json(name = "vehicleFlow")
    var vehicleFlow: VehicleFlow? = null
    @Json(name = "pending")
    var pending: Int? = null
    @Json(name = "ready")
    var ready: Int? = null
    @Json(name = "delivered")
    var delivered: Int? = null
    @Json(name = "invoice")
    var invoice: InvoiceDashboard? = null
    @Json(name = "summary")
    var summary: SummaryDashBoard? = null
}