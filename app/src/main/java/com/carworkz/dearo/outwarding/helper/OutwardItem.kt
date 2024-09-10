package com.carworkz.dearo.outwarding.helper

import android.os.Parcel
import android.os.Parcelable
import com.carworkz.dearo.domain.entities.*

/*This class has parcel implementation generated manually & intentionally to counter limitation of @Parcelize with properties*/
class OutwardItem() : OutwardStep {

    internal var id: String? = null

    internal var uid: String? = null

    internal var packageId: String? = null

    internal var text: String? = null

    internal var name: String? = null

    internal var rate: Double = 0.0

    internal var pkgTaxableAmount: Double = 0.0

    internal var source: String? = null

    internal var quantity: Float = 1f

    internal var units: ArrayList<String>? = null

    internal var unit: String? = null

    internal var hsn: String? = null

    internal var taxPercent: Double? = null

    internal var discountAmount: Double? = null

    internal var price: Double = 0.0

    internal var amount: Double? = null

    internal var stock: Double = 0.0

    internal var description: String? = null

    internal var partNumber: String? = null

    internal var tax = Tax()

    internal var discount = Discount()

    internal var brand = Brand("", "")

    internal var parts = ArrayList<ServicePackagesParts>()

    internal var rates: List<Rates>? = null

    internal lateinit var type: String

    internal var split: Split? = null

    var vehicleType: String? = null

    var salvageValue: Double = 0.0

    var excessClauseValue: Double = 0.0

    var surcharge: Double = 0.0

    var labourType: String? = null

    var reduction: Double = 0.0

    var finalRate: Double = 0.0

    var partNumbers: List<String>? = null

    var vendor: Vendor? = null

    var isApproved: Boolean? = null

    var isFOC: Boolean = false


    constructor(parcel: Parcel) : this()
    {
        id = parcel.readString()
        uid = parcel.readString()
        text = parcel.readString()
        name = parcel.readString()
        rate = parcel.readDouble()
        pkgTaxableAmount = parcel.readDouble()
        source = parcel.readString()
        quantity = parcel.readFloat()
        unit = parcel.readString()
        price = parcel.readDouble()
        amount = parcel.readValue(Double::class.java.classLoader) as? Double
        stock = parcel.readDouble()
        description = parcel.readString()
        partNumber = parcel.readString()
        tax = parcel.readParcelable(Tax::class.java.classLoader)!!
        discount = parcel.readParcelable(Discount::class.java.classLoader)!!
        brand = parcel.readParcelable(Brand::class.java.classLoader)!!
        rates = parcel.createTypedArrayList(Rates)
        type = parcel.readString()!!
        split = parcel.readParcelable(Split::class.java.classLoader)
        vehicleType = parcel.readString()
        salvageValue = parcel.readDouble()
        excessClauseValue = parcel.readDouble()
        surcharge = parcel.readDouble()
        labourType = parcel.readString()
        reduction = parcel.readDouble()
        partNumbers = parcel.createStringArrayList()
        vendor = parcel.readParcelable(Vendor::class.java.classLoader)
        isApproved = parcel.readInt().let {
            if(it < 0)
                null
            else (it != 0.toInt()) }
        isFOC=parcel.readByte() != 0.toByte()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as OutwardItem

        if (id != other.id) return false
        if (text != other.text) return false
        if (quantity != other.quantity) return false
        if (price != other.price) return false
        if (amount != other.amount) return false
        if (tax != other.tax) return false
        if (partNumber != other.partNumber) return false

        return true
    }

    fun compareEquals(obj: Any?): Boolean {
        obj?.let {
            when (it) {
                is Part -> {
                    if (id != it.id) return false
                    if (text != it.text) return false
                    if (quantity != it.quantity) return false
                    if (price != it.price) return false
                    if (amount != it.amount) return false
                    if (tax != it.tax) return false
                    if (type != it.type) return false
                    if (partNumber != it.partNumber) return false
                    if (discount != it.discount) return false
                    return true
                }

                is PartNumber -> {
                    if (it.partNumber != partNumber) return false
                    if (it.partName != text) return false
                    if (it.brandId != brand.id) return false
                    return true
                }

                else -> return false
            }
        }
        return false
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (uid?.hashCode() ?: 0)
        // result = 31 * result + (packageId?.hashCode() ?: 0)
        result = 31 * result + (text?.hashCode() ?: 0)
        result = 31 * result + (rate.hashCode())
        result = 31 * result + (source?.hashCode() ?: 0)
        result = 31 * result + quantity.hashCode()
        result = 31 * result + (unit?.hashCode() ?: 0)
        result = 31 * result + price.hashCode()
        result = 31 * result + (amount?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (partNumber?.hashCode() ?: 0)
        result = 31 * result + tax.hashCode()
        result = 31 * result + discount.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        // super.writeToParcel(parcel, flags)
        parcel.writeString(id)
        parcel.writeString(uid)
        parcel.writeString(text)
        parcel.writeString(name)
        parcel.writeDouble(rate)
        parcel.writeDouble(pkgTaxableAmount)
        parcel.writeString(source)
        parcel.writeFloat(quantity)
        parcel.writeString(unit)
        parcel.writeDouble(price)
        parcel.writeValue(amount)
        parcel.writeDouble(stock)
        parcel.writeString(description)
        parcel.writeString(partNumber)
        parcel.writeParcelable(tax, flags)
        parcel.writeParcelable(discount, flags)
        parcel.writeParcelable(brand, flags)
        parcel.writeTypedList(rates)
        parcel.writeString(type)
        parcel.writeParcelable(split, flags)
        parcel.writeString(vehicleType)
        parcel.writeDouble(salvageValue)
        parcel.writeDouble(excessClauseValue)
        parcel.writeDouble(surcharge)
        parcel.writeString(labourType)
        parcel.writeDouble(reduction)
        parcel.writeStringList(partNumbers)
        parcel.writeParcelable(vendor, flags)
        parcel.writeInt(if (isApproved==null) -1 else if (isApproved ==true) 1 else 0)
        parcel.writeByte(if(isFOC) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OutwardItem> {

        const val TYPE_LABOUR = "type_labour"
        const val TYPE_SER_PKG_LABOUR = "type_ser_pkg_labour"
        const val TYPE_PART = "type_part"
        const val TYPE_SER_PKG_PART = "type_ser_pkg_part"
        const val TYPE_SERVICE_PKG = "type_service_pkg"
        const val TYPE_SPLIT_LABOUR = "split_type_labour"
        const val TYPE_SPLIT_PART = "split_type_part"
        const val TYPE_SPLIT_OTHER_CHARGES = "split_type_other"

        override fun createFromParcel(parcel: Parcel): OutwardItem {
            return OutwardItem(parcel)
        }

        override fun newArray(size: Int): Array<OutwardItem?> {
            return arrayOfNulls(size)
        }
    }
}