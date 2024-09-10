package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

class CustomerAndAddress() : Parcelable{
    @Json(name = "mobile")
    var mobile: String?=null
    @Json(name = "name")
    var name: String?=null
    @Json(name = "email")
    var email: String?=null
    @Json(name = "gstRegistered")
    var gstRegistered: Boolean=false
    @Json(name = "gstNumber")
    var gstNumber: String?=null
    @Json(name = "isRegisteredDealer")
    var isRegisteredDealer: Boolean=false
    @Json(name = "street")
    var street: String?=null
    @Json(name = "location")
    var location: String?=null
    @Json(name = "pincode")
    var pincode: Int=0
    @Json(name = "city")
    var city: String?=null
    @Json(name = "state")
    var state: String?=null
    @Json(name = "stateCode")
    var stateCode: Int=0
    @Json(name = "ownerType")
    var ownerType: String?=null
    @Json(name = "customerGroupId")
     var customerGroupId: String? = null

    constructor(parcel: Parcel) : this() {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CustomerAndAddress> {
        override fun createFromParcel(parcel: Parcel): CustomerAndAddress {
            return CustomerAndAddress(parcel)
        }

        override fun newArray(size: Int): Array<CustomerAndAddress?> {
            return arrayOfNulls(size)
        }
    }
}