package com.carworkz.dearo.othersyshistory

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

data class OtherSysCustomer(
        @Json(name = "corporateGSTIN")
        val corporateGSTIN: String,
        @Json(name = "corporateName")
        val corporateName: String,
        @Json(name = "customerName")
        val customerName: String,
        @Json(name = "customerType")
        val customerType: String,
        @Json(name = "email")
        val email: String,
        @Json(name = "mobile")
        val mobile: String
) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(corporateGSTIN)
                parcel.writeString(corporateName)
                parcel.writeString(customerName)
                parcel.writeString(customerType)
                parcel.writeString(email)
                parcel.writeString(mobile)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<OtherSysCustomer> {
                override fun createFromParcel(parcel: Parcel): OtherSysCustomer {
                        return OtherSysCustomer(parcel)
                }

                override fun newArray(size: Int): Array<OtherSysCustomer?> {
                        return arrayOfNulls(size)
                }
        }
}
