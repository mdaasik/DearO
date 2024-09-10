package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

/**
 * Created by kush on 13/9/17.
 */

class Tax() : Parcelable {

    @Json(name = "hsn")
    var hsn: String? = null
    @Json(name = "sac")
    var sac: String? = null
    @Json(name = "cgstTotal")
    var cgstSummary: List<TaxGst>? = null
    @Json(name = "sgstTotal")
    var sgstSummary: List<TaxGst>? = null
    @Json(name = "sgst")
    var sgst: Double = 0.0
    @Json(name = "cgst")
    var cgst: Double = 0.0
    @Json(name = "sgstAmount")
    var sgstAmount: Double? = null
    @Json(name = "cgstAmount")
    var cgstAmount: Double? = null
    @Json(name = "cess")
    private var cess: Double? = null
    @Json(name = "igst")
    private var igst: Double? = null

    constructor(parcel: Parcel) : this() {
        hsn = parcel.readString()
        sac = parcel.readString()
        sgst = parcel.readDouble()
        cgst = parcel.readDouble()
        sgstAmount = parcel.readValue(Double::class.java.classLoader) as? Double
        cgstAmount = parcel.readValue(Double::class.java.classLoader) as? Double
        cess = parcel.readValue(Double::class.java.classLoader) as? Double
        igst = parcel.readValue(Double::class.java.classLoader) as? Double
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tax

        if (hsn != other.hsn) return false
        if (sac != other.sac) return false
        if (cgstSummary != other.cgstSummary) return false
        if (sgstSummary != other.sgstSummary) return false
        if (sgst != other.sgst) return false
        if (cgst != other.cgst) return false
        if (sgstAmount != other.sgstAmount) return false
        if (cgstAmount != other.cgstAmount) return false
        if (cess != other.cess) return false
        if (igst != other.igst) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hsn?.hashCode() ?: 0
        result = 31 * result + (sac?.hashCode() ?: 0)
        result = 31 * result + (cgstSummary?.hashCode() ?: 0)
        result = 31 * result + (sgstSummary?.hashCode() ?: 0)
        result = 31 * result + sgst.hashCode()
        result = 31 * result + cgst.hashCode()
        result = 31 * result + (sgstAmount?.hashCode() ?: 0)
        result = 31 * result + (cgstAmount?.hashCode() ?: 0)
        result = 31 * result + (cess?.hashCode() ?: 0)
        result = 31 * result + (igst?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Tax(hsn=$hsn, sac=$sac, cgstSummary=$cgstSummary, sgstSummary=$sgstSummary, sgst=$sgst, cgst=$cgst, sgstAmount=$sgstAmount, cgstAmount=$cgstAmount, cess=$cess, igst=$igst)"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(hsn)
        parcel.writeString(sac)
        parcel.writeDouble(sgst)
        parcel.writeDouble(cgst)
        parcel.writeValue(sgstAmount)
        parcel.writeValue(cgstAmount)
        parcel.writeValue(cess)
        parcel.writeValue(igst)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Tax> {
        override fun createFromParcel(parcel: Parcel): Tax {
            return Tax(parcel)
        }

        override fun newArray(size: Int): Array<Tax?> {
            return arrayOfNulls(size)
        }
    }
}
