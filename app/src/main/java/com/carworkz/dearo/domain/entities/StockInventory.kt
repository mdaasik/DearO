package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

class StockInventory() : Parcelable {

    @Json(name = "part")
    internal var part: Part? = null
    @Json(name = "brand")
    internal var brand: Brand? = null
    @Json(name = "partNumber")
    internal var partNumber: String? = null
    @Json(name = "quantity")
    internal var quantity: Float = 0f
    @Json(name = "uom")
    internal var uom: String? = null
    @Json(name = "stock")
    internal var stock: Float = 0f

    constructor(parcel: Parcel) : this() {
        part = parcel.readParcelable(Part::class.java.classLoader)
        brand = parcel.readParcelable(Brand::class.java.classLoader)
        partNumber = parcel.readString()
        quantity = parcel.readFloat()
        uom = parcel.readString()
        stock = parcel.readFloat()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(part, flags)
        parcel.writeParcelable(brand, flags)
        parcel.writeString(partNumber)
        parcel.writeFloat(quantity)
        parcel.writeString(uom)
        parcel.writeFloat(stock)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StockInventory> {
        override fun createFromParcel(parcel: Parcel): StockInventory {
            return StockInventory(parcel)
        }

        override fun newArray(size: Int): Array<StockInventory?> {
            return arrayOfNulls(size)
        }
    }
}