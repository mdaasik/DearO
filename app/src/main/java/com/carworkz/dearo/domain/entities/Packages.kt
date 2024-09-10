package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

/**
 * Created by Kush Singh Chibber on 11/25/2017.
 */
data class Packages(
    @Json(name = "list") var list: MutableList<ServicePackage>?,
    @Json(name = "filter") var filter: Filter?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(ServicePackage.CREATOR),
        parcel.readParcelable(Filter::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(filter, flags)
        parcel.writeTypedList(list)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Packages> {
        override fun createFromParcel(parcel: Parcel): Packages {
            return Packages(parcel)
        }

        override fun newArray(size: Int): Array<Packages?> {
            return arrayOfNulls(size)
        }
    }
}

class ServicePackage() : Parcelable {
    @Json(name = "id")
    var id: String? = null // 1513681565565496

    @Json(name = "packageId")
    var packageId: String? = null // 1513681565565496

    @Json(name = "code")
    var code: String? = null // PKG:001

    @Json(name = "category")
    var category: String? = null

    @Json(name = "ownerId")
    var ownerId: String? = null // 1507395745988664

    @Json(name = "ownerType")
    var ownerType: String? = null // WorkshopCluster

    @Json(name = "name")
    var name: String? = null // Express Service

    @Json(name = "description")
    var description: String? = null // Review your car now

    @Json(name = "attributes")
    var attributes: List<String>? = null

    @Json(name = "services")
    var services: List<Service>? = null

    @Json(name = "hsn")
    var hsn: String? = null // 2710

    @Json(name = "terms")
    var terms: List<String>? = null

    @Json(name = "makeSlug")
    var makeSlug: String? = null // honda

    @Json(name = "modelSlug")
    var modelSlug: String? = null // city

    @Json(name = "fuelType")
    var fuelType: String? = null // Petrol

    @Json(name = "engineOilType")
    var engineOilType: String? = null // Regular

    @Json(name = "offerPrice")
    var offerPrice: PackageOfferPrice? = null

    @Json(name = "actualPrice")
    var actualPrice: Int = 0 // 5000

    @Json(name = "amount")
    var amount: Double = 0.0

    @Json(name = "taxableAmount")
    var taxableAmount: Double = 0.0

    @Json(name = "tax")
    var tax: Tax? = null

    @Json(name = "time")
    var time: Time? = null

    @Json(name = "parts")
    var parts: List<ServicePackagesParts>? = null

    @Json(name = "rates")
    var rates: List<Rates>? = null
    var isSelected: Boolean = false

    @Json(name = "ftd")
    var ftd: Float? = null

    @Json(name = "mtd")
    var mtd: Float? = null

    @Json(name = "labours")
    var labours: List<Labour>? = null

    @Json(name = "labourIds")
    var labourIds: List<String>? = null

    @Json(name = "startDate")
    var startDate: String? = null

    @Json(name = "endDate")
    var endDate: String? = null

    @Json(name = "isApproved")
    internal var isApproved: Boolean = false

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        packageId = parcel.readString()
        code = parcel.readString()
        category = parcel.readString()
        ownerId = parcel.readString()
        ownerType = parcel.readString()
        name = parcel.readString()
        description = parcel.readString()
        attributes = parcel.createStringArrayList()
        services = parcel.createTypedArrayList(Service)
        hsn = parcel.readString()
        terms = parcel.createStringArrayList()
        makeSlug = parcel.readString()
        modelSlug = parcel.readString()
        fuelType = parcel.readString()
        engineOilType = parcel.readString()
        offerPrice = parcel.readParcelable(PackageOfferPrice::class.java.classLoader)
        actualPrice = parcel.readInt()
        amount = parcel.readDouble()
        taxableAmount = parcel.readDouble()
        tax = parcel.readParcelable(Tax::class.java.classLoader)
        time = parcel.readParcelable(Time::class.java.classLoader)
        parts = parcel.createTypedArrayList(ServicePackagesParts)
        rates = parcel.createTypedArrayList(Rates)
        isSelected = parcel.readByte() != 0.toByte()
        ftd = parcel.readValue(Float::class.java.classLoader) as? Float
        mtd = parcel.readValue(Float::class.java.classLoader) as? Float
        labours = parcel.createTypedArrayList(Labour)
        labourIds = parcel.createStringArrayList()
        startDate = parcel.readString()
        endDate = parcel.readString()
        isApproved = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(packageId)
        parcel.writeString(code)
        parcel.writeString(category)
        parcel.writeString(ownerId)
        parcel.writeString(ownerType)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeStringList(attributes)
        parcel.writeTypedList(services)
        parcel.writeString(hsn)
        parcel.writeStringList(terms)
        parcel.writeString(makeSlug)
        parcel.writeString(modelSlug)
        parcel.writeString(fuelType)
        parcel.writeString(engineOilType)
        parcel.writeParcelable(offerPrice, flags)
        parcel.writeInt(actualPrice)
        parcel.writeDouble(amount)
        parcel.writeDouble(taxableAmount)
        parcel.writeParcelable(tax, flags)
        parcel.writeParcelable(time, flags)
        parcel.writeTypedList(parts)
        parcel.writeTypedList(rates)
        parcel.writeByte(if (isSelected) 1 else 0)
        parcel.writeValue(ftd)
        parcel.writeValue(mtd)
        parcel.writeTypedList(labours)
        parcel.writeStringList(labourIds)
        parcel.writeString(startDate)
        parcel.writeString(endDate)
        parcel.writeByte(if (isApproved) 1 else 0)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ServicePackage> {
        override fun createFromParcel(parcel: Parcel): ServicePackage {
            return ServicePackage(parcel)
        }

        override fun newArray(size: Int): Array<ServicePackage?> {
            return arrayOfNulls(size)
        }
    }
}

data class UpdatePackage(
        internal var packageIds: List<String>
) : Parcelable
{
    constructor(parcel: Parcel) : this(parcel.createStringArrayList()!!)

    override fun writeToParcel(parcel: Parcel, flags: Int)
    {
        parcel.writeStringList(packageIds)
    }

    override fun describeContents(): Int
    {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UpdatePackage>
    {
        override fun createFromParcel(parcel: Parcel): UpdatePackage
        {
            return UpdatePackage(parcel)
        }

        override fun newArray(size: Int): Array<UpdatePackage?>
        {
            return arrayOfNulls(size)
        }
    }
}

data class Service(
    @Json(name = "text") var text: String // Replace Engine oil
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString().toString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(text)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Service> {
        override fun createFromParcel(parcel: Parcel): Service {
            return Service(parcel)
        }

        override fun newArray(size: Int): Array<Service?> {
            return arrayOfNulls(size)
        }
    }
}

data class Time(
    @Json(name = "total") var total: Int // 300
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(total)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Time> {
        override fun createFromParcel(parcel: Parcel): Time {
            return Time(parcel)
        }

        override fun newArray(size: Int): Array<Time?> {
            return arrayOfNulls(size)
        }
    }
}

data class PackageOfferPrice(
    @Json(name = "hsn") var hsn: String, // 2710
    @Json(name = "cgst") var cgst: Int, // 9
    @Json(name = "sgst") var sgst: Int, // 9
    @Json(name = "cgstTotal") var cgstTotal: Float, // 315.04
    @Json(name = "sgstTotal") var sgstTotal: Float, // 315.04
    @Json(name = "amount") var amount: Float // 3500.45
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(hsn)
        parcel.writeInt(cgst)
        parcel.writeInt(sgst)
        parcel.writeFloat(cgstTotal)
        parcel.writeFloat(sgstTotal)
        parcel.writeFloat(amount)
    }
    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PackageOfferPrice> {
        override fun createFromParcel(parcel: Parcel): PackageOfferPrice {
            return PackageOfferPrice(parcel)
        }

        override fun newArray(size: Int): Array<PackageOfferPrice?> {
            return arrayOfNulls(size)
        }
    }
}

data class ServicePackagesParts(
        @Json(name = "packageId")
        var packageId: String?,
        @Json(name = "partId")
        var partId: String?,
        @Json(name = "brandId")
        var brandId: String?,
        @Json(name = "partNumber")
        var partNumber: String?,
        @Json(name = "quantity")
        var quantity: Float,
        @Json(name = "uom")
        var uom: String,
        @Json(name = "part")
        var part: Part,
        @Json(name = "brand")
        var brand: Brand,
        @Json(name = "stock")
        var stock: Float,
        @Json(name = "makeSlug")
        var makeSlug: String,
        @Json(name = "modelSlug")
        var modelSlug: String,
        @Json(name = "partNumbers")
        var partNumbers: List<String>?,
        @Json(name = "description")
        var description: String?
) : Parcelable
{
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readFloat(),
            parcel.readString()!!,
            parcel.readParcelable(Part::class.java.classLoader)!!,
            parcel.readParcelable(Brand::class.java.classLoader)!!,
            parcel.readFloat(),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.createStringArrayList(),
            parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int)
    {
        parcel.writeString(packageId)
        parcel.writeString(partId)
        parcel.writeString(brandId)
        parcel.writeString(partNumber)
        parcel.writeFloat(quantity)
        parcel.writeString(uom)
        parcel.writeParcelable(part, flags)
        parcel.writeParcelable(brand, flags)
        parcel.writeFloat(stock)
        parcel.writeString(makeSlug)
        parcel.writeString(modelSlug)
        parcel.writeStringList(partNumbers)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ServicePackagesParts> {
        override fun createFromParcel(parcel: Parcel): ServicePackagesParts {
            return ServicePackagesParts(parcel)
        }

        override fun newArray(size: Int): Array<ServicePackagesParts?> {
            return arrayOfNulls(size)
        }
    }
}

data class Filter(
    @Json(name = "category") var category: Category
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readParcelable<Category>(Category::class.java.classLoader) as Category)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(category, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Filter> {
        override fun createFromParcel(parcel: Parcel): Filter {
            return Filter(parcel)
        }

        override fun newArray(size: Int): Array<Filter?> {
            return arrayOfNulls(size)
        }
    }
}

data class Category(
    @Json(name = "value") var value: List<String>,
    @Json(name = "multi") var multi: Boolean // false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createStringArrayList()!!,
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStringList(value)
        parcel.writeByte(if (multi) 1 else 0)
    }

    override fun describeContents(): Int
    {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Category>
    {
        override fun createFromParcel(parcel: Parcel): Category
        {
            return Category(parcel)
        }

        override fun newArray(size: Int): Array<Category?>
        {
            return arrayOfNulls(size)
        }
    }
}

data class Rates(
    @Json(name = "engineOilType") var engineOilType: String,
    @Json(name = "offerPrice") var offerPrice: PackageOfferPrice
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readParcelable<PackageOfferPrice>(PackageOfferPrice::class.java.classLoader) as PackageOfferPrice
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(engineOilType)
        parcel.writeParcelable(offerPrice, flags)
    }

    override fun describeContents(): Int
    {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Rates>
    {
        override fun createFromParcel(parcel: Parcel): Rates
        {
            return Rates(parcel)
        }

        override fun newArray(size: Int): Array<Rates?>
        {
            return arrayOfNulls(size)
        }
    }
}