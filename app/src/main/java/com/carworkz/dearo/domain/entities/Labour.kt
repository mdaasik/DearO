package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Nullable
import com.squareup.moshi.Json

class Labour() : Parcelable {

    @Json(name = "id")
    var id: String? = null
    @Json(name = "uid")
    var uid: String? = null
    @Json(name = "source")
    var source: String? = null
    @Json(name = "text")
    var text: String? = null
    @Json(name = "type")
    var type: String? = null
    @Json(name = "price")
    var price: Double = 0.0//this field is useless as per Rahul Gupta(@Deputy Manager) as on 12Sep2022
    @Json(name = "quantity")
    var quantity: Int = 1
    @Json(name = "rate")
    var rate: Double = 0.0
    @Json(name = "unit")
    var unit: String? = null
    @Json(name = "brand")
    var brand = Brand("", "")
    @Json(name = "discount")
    var discount = Discount()
    @Json(name = "tax")
    var tax = Tax()
    @Json(name = "amount")
    internal var amount: Double? = 0.0
    @Json(name = "split")
    internal var split: Split? = null
    @Json(name = "isComplete")
    internal var isComplete: Boolean? = null
    @Json(name = "ftd")
    var ftd: Float? = null
    @Json(name = "mtd")
    var mtd: Float? = null
    @Json(name = "vehicleType")
    var vehicleType: String? = null
    @Json(name = "surcharge")
    var surcharge: Double = 0.0
    @Json(name = "labourType")
    var labourType: String? = null
    @Json(name = "discountAmount")
    internal var discountAmount: Double? = 0.0
    @Json(name = "taxPercent")
    internal var taxPercent: Double= 0.0
    @Json(name = "reduction")
    internal var reduction: Double= 0.0
    @Json(name = "finalRate")
    internal var finalRate: Double= 0.0
    @Json(name = "vendor")
    internal var vendor : Vendor? = null
    @Json(name = "isApproved")
    internal var isApproved: Boolean? = null
    @Json(name = "isFOC")
    internal var isFOC: Boolean = false

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        uid = parcel.readString()
        source = parcel.readString()
        text = parcel.readString()
        type = parcel.readString()
        price = parcel.readDouble()
        quantity = parcel.readInt()
        rate = parcel.readDouble()
        unit = parcel.readString()
        brand = parcel.readParcelable(Brand::class.java.classLoader)!!
        discount = parcel.readParcelable(Discount::class.java.classLoader)!!
        tax = parcel.readParcelable(Tax::class.java.classLoader)!!
        amount = parcel.readDouble()
        split = parcel.readParcelable(Split::class.java.classLoader)
        isComplete = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        ftd = parcel.readValue(Float::class.java.classLoader) as? Float
        mtd = parcel.readValue(Float::class.java.classLoader) as? Float
        vehicleType = parcel.readString()
        surcharge = parcel.readDouble()
        labourType = parcel.readString()
        discountAmount = parcel.readValue(Double::class.java.classLoader) as? Double
        taxPercent = parcel.readDouble()
        reduction = parcel.readDouble()
        finalRate = parcel.readDouble()
        isApproved = parcel.readInt().let {
            if(it < 0)
                null
            else (it != 0.toInt()) }
        vendor = parcel.readParcelable(Vendor::class.java.classLoader)
        isFOC=parcel.readByte() != 0.toByte()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val labour = other as Labour?
        return if (id != null) {
            id == labour!!.id || text == labour.text
        } else {
            text == labour!!.text
        }
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (text?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Labour(id=$id, text=$text, type=$type, price=$price, quantity=$quantity, rate=$rate, unit=$unit, brand=$brand, discount=$discount, tax=$tax, amount=$amount)"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(uid)
        parcel.writeString(source)
        parcel.writeString(text)
        parcel.writeString(type)
        parcel.writeDouble(price)
        parcel.writeInt(quantity)
        parcel.writeDouble(rate)
        parcel.writeString(unit)
        parcel.writeParcelable(brand, flags)
        parcel.writeParcelable(discount, flags)
        parcel.writeParcelable(tax, flags)
        parcel.writeDouble(amount?:0.0)
        parcel.writeParcelable(split, flags)
        parcel.writeValue(isComplete)
        parcel.writeValue(ftd)
        parcel.writeValue(mtd)
        parcel.writeString(vehicleType)
        parcel.writeDouble(surcharge)
        parcel.writeString(labourType)
        parcel.writeValue(discountAmount)
        parcel.writeDouble(taxPercent)
        parcel.writeDouble(reduction)
        parcel.writeDouble(finalRate)
        parcel.writeInt(if (isApproved==null) -1 else if (isApproved ==true) 1 else 0)
        parcel.writeParcelable(vendor,flags)
        parcel.writeByte(if(isFOC) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Labour> {
        const val TAG = "labour"
        const val TYPE_OSL="OSL"

        override fun createFromParcel(parcel: Parcel): Labour {
            return Labour(parcel)
        }

        override fun newArray(size: Int): Array<Labour?> {
            return arrayOfNulls(size)
        }
    }

}
