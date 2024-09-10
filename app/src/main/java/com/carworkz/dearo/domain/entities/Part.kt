package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.carworkz.dearo.extensions.readBoolean
import com.carworkz.dearo.extensions.writeBoolean
import com.squareup.moshi.Json

class Part() : Parcelable {

    @Json(name = "id")
    internal var id: String? = null
    @Json(name = "uid")
    var uid: String? = null
    @Json(name = "source")
    var source: String? = null
    @Json(name = "text")
    internal var text: String? = null
    @Json(name = "type")
    internal var type: String? = null
    @Json(name = "partName")
    internal var partName: String? = null
    @Json(name = "hsn")
    internal var hsn: String? = null
    @Json(name = "taxPercent")
    internal var taxPercent: Double= 0.0
    @Json(name = "taxAmount")
    internal var taxAmount: String? = null
    @Json(name = "price")
    internal var price: Double = 0.0
    @Json(name = "description")
    internal var description: String? = null
    @Json(name = "partNumber")
    internal var partNumber: String? = null
    @Json(name = "quantity")
    internal var quantity: Float = 1.0f
    @Json(name = "rate")
    internal var rate: Double = 0.0
    @Json(name = "units")
    internal var units: List<String>? = null
    @Json(name = "unit")
    internal var unit: String? = null
    @Json(name = "brand")
    internal var brand = Brand("", "")
    @Json(name = "discount")
    internal var discount = Discount()
    @Json(name = "discountAmount")
    internal var discountAmount: Double? = null
    @Json(name = "tax")
    internal var tax = Tax()
    @Json(name = "taxableAmount")
    internal var taxableAmount = 0.0
    @Json(name = "amount")
    internal var amount = 0.0
    @Json(name = "stock")
    internal var stock:Float? = 0f
    @Json(name = "split")
    internal var split: Split? = null
    @Json(name = "isComplete")
    internal var isComplete: Boolean? = null
    @Json(name = "ftd")
    var ftd: Ftd? = null
    @Json(name = "mtd")
    var mtd: Mtd? = null
    @Json(name = "total")
    var total: Total? = null
    @Json(name = "vehicleType")
    var vehicleType: String? = null
    @Json(name = "isIssued")
    var isIssued: Boolean = false
    @Json(name = "finalRate")
    internal var finalRate: Double= 0.0
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
        partName = parcel.readString()
        hsn = parcel.readString()
        taxPercent = parcel.readDouble()
        taxAmount = parcel.readString()
        price = parcel.readDouble()
        description = parcel.readString()
        partNumber = parcel.readString()
        quantity = parcel.readFloat()
        rate = parcel.readDouble()
        units = parcel.createStringArrayList()
        unit = parcel.readString()
        brand = parcel.readParcelable(Brand::class.java.classLoader)!!
        discount = parcel.readParcelable(Discount::class.java.classLoader)!!
        discountAmount = parcel.readValue(Double::class.java.classLoader) as? Double
        tax = parcel.readParcelable(Tax::class.java.classLoader)!!
        taxableAmount = parcel.readDouble()
        amount = parcel.readDouble()
        stock = parcel.readFloat()
        split = parcel.readParcelable(Split::class.java.classLoader)
        isComplete = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        vehicleType = parcel.readString()
        isIssued = parcel.readByte() != 0.toByte()
        finalRate = parcel.readDouble()
        isApproved = parcel.readInt().let {
            if(it < 0)
                null
            else (it != 0.toInt()) }
        isFOC=parcel.readByte() != 0.toByte()
    }
    override fun toString(): String {
        return "PartId : $id, PartName : $text, PartNumber : $partNumber, Price : $price, Description : $description, Quantity : $quantity, Rate : $rate , Units : [$units], Unit : $unit," +
                " Brand: $brand, Tax : $tax, Discount : $discount"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Part

        if (id != other.id) return false
        if (uid != other.uid) return false
        if (text != other.text) return false
        if (partNumber != other.partNumber) return false
        if (quantity != other.quantity) return false
        if (tax != other.tax) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (uid?.hashCode() ?: 0)
        result = 31 * result + (text?.hashCode() ?: 0)
        result = 31 * result + (partNumber?.hashCode() ?: 0)
        result = 31 * result + quantity.hashCode()
        result = 31 * result + tax.hashCode()
        return result
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(uid)
        parcel.writeString(source)
        parcel.writeString(text)
        parcel.writeString(type)
        parcel.writeString(partName)
        parcel.writeString(hsn)
        parcel.writeDouble(taxPercent)
        parcel.writeString(taxAmount)
        parcel.writeDouble(price)
        parcel.writeString(description)
        parcel.writeString(partNumber)
        parcel.writeFloat(quantity)
        parcel.writeDouble(rate)
        parcel.writeStringList(units)
        parcel.writeString(unit)
        parcel.writeParcelable(brand, flags)
        parcel.writeParcelable(discount, flags)
        parcel.writeValue(discountAmount)
        parcel.writeParcelable(tax, flags)
        parcel.writeDouble(taxableAmount)
        parcel.writeDouble(amount)
        parcel.writeFloat((if(stock==null) 0f else stock)!!)
        parcel.writeParcelable(split, flags)
        parcel.writeValue(isComplete)
        parcel.writeString(vehicleType)
        parcel.writeByte(if (isIssued) 1 else 0)
        parcel.writeDouble(finalRate)
        parcel.writeInt(if (isApproved==null) -1 else if (isApproved ==true) 1 else 0)
        parcel.writeByte(if(isFOC) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Part> {
        const val TAG = "part"
        override fun createFromParcel(parcel: Parcel): Part {
            return Part(parcel)
        }

        override fun newArray(size: Int): Array<Part?> {
            return arrayOfNulls(size)
        }
    }
}
