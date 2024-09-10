package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

/**
 * Created by kush on 18/8/17.
 */

class Variant() : Parcelable {

    @Json(name = "code")
    internal var code: String? = null
    @Json(name = "name")
    internal lateinit var name: String
    @Json(name = "makeSlug")
    internal var makeSlug: String? = null
    @Json(name = "modelSlug")
    internal var modelSlug: String? = null
    @Json(name = "fuelType")
    internal var fuelType: String? = null
    @Json(name = "description")
    internal var description: String? = null
    @Json(name = "defaultForPms")
    internal var defaultForPms: Boolean? = null
    @Json(name = "defaultKms")
    internal var defaultKms: Int? = null
    @Json(name = "startingYear")
    internal var startingYear: Int? = null
    @Json(name = "engineSize")
    internal var engineSize: String? = null
    @Json(name = "transmissionType")
    internal var transmissionType: String? = null
    @Json(name = "carCategory")
    internal var carCategory: String? = null
    @Json(name = "carSize")
    internal var carSize: String? = null
    @Json(name = "status")
    internal var status: String? = null
    @Json(name = "updatedOn")
    internal var updatedOn: String? = null
    @Json(name = "carBodyCategory")
    internal var carBodyCategory: String? = null
    @Json(name = "carCostCategory")
    internal var carCostCategory: String? = null

    constructor(parcel: Parcel) : this() {
        code = parcel.readString()
        name = parcel.readString().toString()
        makeSlug = parcel.readString()
        modelSlug = parcel.readString()
        fuelType = parcel.readString()
        description = parcel.readString()
        defaultForPms = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        defaultKms = parcel.readValue(Int::class.java.classLoader) as? Int
        startingYear = parcel.readValue(Int::class.java.classLoader) as? Int
        engineSize = parcel.readString()
        transmissionType = parcel.readString()
        carCategory = parcel.readString()
        carSize = parcel.readString()
        status = parcel.readString()
        updatedOn = parcel.readString()
        carBodyCategory = parcel.readString()
        carCostCategory = parcel.readString()
    }

    override fun toString(): String {
        return "Variant(code=$code, name='$name', makeSlug=$makeSlug, modelSlug=$modelSlug, fuelType=$fuelType, description=$description, defaultForPms=$defaultForPms, defaultKms=$defaultKms, startingYear=$startingYear, engineSize=$engineSize, transmissionType=$transmissionType, carCategory=$carCategory, carSize=$carSize, status=$status, updatedOn=$updatedOn, carBodyCategory=$carBodyCategory, carCostCategory=$carCostCategory)"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(code)
        parcel.writeString(name)
        parcel.writeString(makeSlug)
        parcel.writeString(modelSlug)
        parcel.writeString(fuelType)
        parcel.writeString(description)
        parcel.writeValue(defaultForPms)
        parcel.writeValue(defaultKms)
        parcel.writeValue(startingYear)
        parcel.writeString(engineSize)
        parcel.writeString(transmissionType)
        parcel.writeString(carCategory)
        parcel.writeString(carSize)
        parcel.writeString(status)
        parcel.writeString(updatedOn)
        parcel.writeString(carBodyCategory)
        parcel.writeString(carCostCategory)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Variant> {
        override fun createFromParcel(parcel: Parcel): Variant {
            return Variant(parcel)
        }

        override fun newArray(size: Int): Array<Variant?> {
            return arrayOfNulls(size)
        }
    }
}
