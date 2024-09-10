package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

data class CostEstimate(
        @Json(name = "labours")
        internal var labours: MutableList<Labour>? = null,
        @Json(name = "parts")
        internal var parts: MutableList<Part>? = null,
        internal var packageIds: MutableList<String>? = null,
        @Json(name = "packages")
        internal var packages: MutableList<ServicePackage>? = null
) : Parcelable
{
    constructor(parcel: Parcel) : this(
            parcel.createTypedArrayList(Labour.CREATOR),
            parcel.createTypedArrayList(Part.CREATOR),
            parcel.createStringArrayList(),
            parcel.createTypedArrayList(ServicePackage.CREATOR)
    )

    override fun writeToParcel(dest: Parcel, flags: Int)
    {
        dest?.writeTypedList(labours)
        dest?.writeTypedList(parts)
        dest?.writeStringList(packageIds)
        dest?.writeTypedList(packages)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<CostEstimate>
    {
        override fun createFromParcel(parcel: Parcel): CostEstimate
        {
            return CostEstimate(parcel)
        }

        override fun newArray(size: Int): Array<CostEstimate?>
        {
            return arrayOfNulls(size)
        }
    }
}
