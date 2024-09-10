package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

class Accidental(
    @field:Json(name = "insurance") var insurance: Insurance? = null,
    @field:Json(name = "company") var company: Company? = null,
    @field:Json(name = "surveyor") var surveyor: Surveyor? = null,
    @field:Json(name = "fir") var fir: Fir? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readParcelable(Insurance::class.java.classLoader),
            parcel.readParcelable(Company::class.java.classLoader),
            parcel.readParcelable(Surveyor::class.java.classLoader),
            parcel.readParcelable(Fir::class.java.classLoader))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(insurance, flags)
        parcel.writeParcelable(company, flags)
        parcel.writeParcelable(surveyor, flags)
        parcel.writeParcelable(fir, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Accidental> {
        override fun createFromParcel(parcel: Parcel): Accidental {
            return Accidental(parcel)
        }

        override fun newArray(size: Int): Array<Accidental?> {
            return arrayOfNulls(size)
        }
    }
}
