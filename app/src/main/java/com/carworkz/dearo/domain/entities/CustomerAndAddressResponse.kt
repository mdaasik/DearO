package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

class CustomerAndAddressResponse() : Parcelable
{
    @Json(name = "customer")
    var customer: Customer?=null
    @Json(name = "address")
    var address: Address?=null

    constructor(parcel: Parcel) : this() {
        customer = parcel.readParcelable(Customer::class.java.classLoader)
        address = parcel.readParcelable(Address::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(customer, flags)
        parcel.writeParcelable(address, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CustomerAndAddressResponse> {
        override fun createFromParcel(parcel: Parcel): CustomerAndAddressResponse {
            return CustomerAndAddressResponse(parcel)
        }

        override fun newArray(size: Int): Array<CustomerAndAddressResponse?> {
            return arrayOfNulls(size)
        }
    }
}