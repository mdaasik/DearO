package com.carworkz.dearo.domain.entities

const val VEHICLE_STRICT = "strict"
const val VEHICLE_FLEXIBLE = "flexible"

data class PartNumberSearchRequest(val q: String?, val partId: String?, val jobCardId: String?, val brandId: String?, val showStock: Boolean, val vehicleType: String?, val vehicle: String = VEHICLE_STRICT) {
    private lateinit var makeSlug: String
    private lateinit var modelSlug: String
    private lateinit var variantCode: String
    private lateinit var fuelType: String
    private lateinit var packageId: String

    constructor(q: String, makeSlug: String, modelSlug: String, variantCode: String, fuelType: String, showStock: Boolean, vehicleType: String?, vehicle: String = VEHICLE_STRICT) : this(q, null, null, null, showStock, vehicleType, vehicle)
    {
        this.makeSlug = makeSlug
        this.modelSlug = modelSlug
        this.variantCode = variantCode
        this.fuelType = fuelType
    }

    constructor(query: String?, jobCardId: String?, partId: String?, showStock: Boolean, vehicleType: String?, vehicle: String = VEHICLE_STRICT) : this(query, partId, jobCardId, null, showStock, vehicleType, vehicle)

    constructor(query: String?,jobCardId: String?, showStock: Boolean, vehicleType: String?, vehicle: String = VEHICLE_STRICT, packageId: String?, partId: String?) : this(query, partId, jobCardId, null, showStock, vehicleType, vehicle)
    {
        if (packageId != null)
        {
            this.packageId = packageId
        }
    }
}
