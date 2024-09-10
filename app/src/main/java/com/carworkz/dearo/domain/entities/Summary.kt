package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

/**
 * Created by ambab on 19/9/17.
 */
class Summary(
    @field:Json(name = "totalAmountBeforeTax") internal val totalAmountBeforeTax: Double? = null,
    @field:Json(name = "totalAmountAfterTax") internal val totalAmountAfterTax: Double? = null,
    @field:Json(name = "totalDiscount") internal val totalDiscount: Double? = null,
    @field:Json(name = "totalTaxAmount") internal val totalTaxAmount: Double? = null,
    @field:Json(name = "split") internal val split: Split? = null,
    @field:Json(name = "tax") internal val tax: Tax? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readParcelable(Split::class.java.classLoader),
            parcel.readParcelable(Tax::class.java.classLoader))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(totalAmountBeforeTax)
        parcel.writeValue(totalAmountAfterTax)
        parcel.writeValue(totalDiscount)
        parcel.writeValue(totalTaxAmount)
        parcel.writeParcelable(split, flags)
        parcel.writeParcelable(tax, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Summary> {
        override fun createFromParcel(parcel: Parcel): Summary {
            return Summary(parcel)
        }

        override fun newArray(size: Int): Array<Summary?> {
            return arrayOfNulls(size)
        }
    }
}