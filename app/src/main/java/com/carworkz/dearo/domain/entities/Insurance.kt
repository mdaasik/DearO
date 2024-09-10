package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

class Insurance() : Parcelable {
    @Json(name = "claim")
    var claim: Boolean? = null
    @Json(name = "cashless")
    var cashless: Boolean? = null
    @Json(name = "claimNumber")
    var claimNumber: String? = null
    @Json(name = "name")
    var name: String? = null
    @Json(name = "policyNumber")
    var policyNumber: String? = null
    @Json(name = "expiryDate")
    var expiryDate: String? = null
    @Json(name = "company")
    var company: String? = null

    constructor(parcel: Parcel) : this() {
        claim = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        cashless = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        claimNumber = parcel.readString()
        name = parcel.readString()
        policyNumber = parcel.readString()
        expiryDate = parcel.readString()
        company = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(claim)
        parcel.writeValue(cashless)
        parcel.writeString(claimNumber)
        parcel.writeString(name)
        parcel.writeString(policyNumber)
        parcel.writeString(expiryDate)
        parcel.writeString(company)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Insurance> {
        override fun createFromParcel(parcel: Parcel): Insurance {
            return Insurance(parcel)
        }

        override fun newArray(size: Int): Array<Insurance?> {
            return arrayOfNulls(size)
        }
    }
}
