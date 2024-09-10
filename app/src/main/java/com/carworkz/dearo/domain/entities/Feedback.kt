package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

/**
 * Created by kush on 7/12/17.
 */

class Feedback() : Parcelable {

    @Json(name = "recommendedScore")
    internal var recommendedScore: Int? = null
    @Json(name = "serviceQuality")
    internal var serviceQuality: Float = 0.0f
    @Json(name = "billingTransparency")
    internal var billingTransparency: Float = 0.0f
    @Json(name = "timelyDelivery")
    internal var timelyDelivery: Float = 0.0f
    @Json(name = "comment")
    internal var comment: String? = null

    constructor(parcel: Parcel) : this() {
        recommendedScore = parcel.readValue(Int::class.java.classLoader) as? Int
        serviceQuality = parcel.readValue(Float::class.java.classLoader) as Float
        billingTransparency = parcel.readValue(Float::class.java.classLoader) as Float
        timelyDelivery = parcel.readValue(Float::class.java.classLoader) as Float
        comment = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(recommendedScore)
        parcel.writeValue(serviceQuality)
        parcel.writeValue(billingTransparency)
        parcel.writeValue(timelyDelivery)
        parcel.writeString(comment)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Feedback> {
        override fun createFromParcel(parcel: Parcel): Feedback {
            return Feedback(parcel)
        }

        override fun newArray(size: Int): Array<Feedback?> {
            return arrayOfNulls(size)
        }
    }
}