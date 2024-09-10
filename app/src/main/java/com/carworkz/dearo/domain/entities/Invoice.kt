package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.carworkz.dearo.extensions.readBoolean
import com.carworkz.dearo.extensions.writeBoolean
import com.squareup.moshi.Json

/**
 * Created by ambab on 15/9/17.
 */
class Invoice() : Parcelable {

    @Json(name = "id")
    internal var id: String? = null
    @Json(name = "customerId")
    internal var customerId: String? = null
    @Json(name = "workshopId")
    internal var workshopId: String? = null
    @Json(name = "invoiceId")
    internal var invoiceId: String? = null
    @Json(name = "igstInvoiceId")
    internal var igstInvoiceId: String? = null
    @Json(name = "insuranceInvoiceId")
    internal var insuranceInvoiceId: String? = null
    @Json(name = "jobCardId")
    internal var jobCardId: String? = null
    @Json(name = "customer")
    internal var customer: Customer? = null
    @Json(name = "proformaDate")
    internal var proformaDate: String? = null
    @Json(name = "date")
    internal var date: String? = null
    @Json(name = "dueDate")
    internal var dueDate: String? = null
    @Json(name = "status")
    internal var status: String? = null
    @Json(name = "stepLabel")
    internal var stepLabel: String? = null
    @Json(name = "createdOn")
    internal var createdOn: String? = null
    @Json(name = "updatedOn")
    internal var updatedOn: String? = null
    @Json(name = "jobCard")
    internal var jobCard: JobCard? = null
    @Json(name = "summary")
    internal var summary: Summary? = null
    @Json(name = "pdf")
    internal var pdf: PDF? = null
    @Json(name = "insurancePdf")
    internal var insurancePdf: PDF? = null
    @Json(name = "igstCustomerPdf")
    internal var igstCustomerPdf: PDF? = null
    @Json(name = "splitInvoice")
    internal var splitInvoice: Boolean = false
    @Json(name = "customerIgst")
    internal var customerIgst: Boolean = false
    @Json(name = "parts")
    internal var parts: MutableList<Part>? = null
    @Json(name = "labours")
    internal var labours: MutableList<Labour>? = null
    @Json(name = "packages")
    internal var packages: MutableList<ServicePackage>? = null
    internal var packageIds: MutableList<String>? = null
    @Json(name = "vehicleType")
    internal var vehicleType: String? = null
    @Json(name = "payments")
    internal var payments: MutableList<Payment>? = null
    @Json(name = "totalPaidAmount")
    internal var totalPaidAmount: Double? = null
    @Json(name = "outStandingAmount")
    internal var outStandingAmount: Double? = null
    @Json(name = "salvageValue")
    internal var salvageValue: Double? = null
    @Json(name = "excessClauseValue")
    internal var excessClauseValue: Double? = null
    @Json(name = "isThirdParty")
    internal var isThirdParty: Boolean = false
    @Json(name = "thirdPartyDetails")
    internal var thirdPartyDetails: ThirdParty? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        customerId = parcel.readString()
        workshopId = parcel.readString()
        invoiceId = parcel.readString()
        insuranceInvoiceId = parcel.readString()
        jobCardId = parcel.readString()
        customer = parcel.readParcelable(Customer::class.java.classLoader)
        proformaDate = parcel.readString()
        date = parcel.readString()
        dueDate = parcel.readString()
        status = parcel.readString()
        stepLabel = parcel.readString()
        createdOn = parcel.readString()
        updatedOn = parcel.readString()
        jobCard = parcel.readParcelable(JobCard::class.java.classLoader)
        summary = parcel.readParcelable(Summary::class.java.classLoader)
        pdf = parcel.readParcelable(PDF::class.java.classLoader)
        insurancePdf = parcel.readParcelable(PDF::class.java.classLoader)
        igstCustomerPdf = parcel.readParcelable(PDF::class.java.classLoader)
        thirdPartyDetails = parcel.readParcelable(ThirdParty::class.java.classLoader)
        splitInvoice = parcel.readInt()==1
        customerIgst = parcel.readInt()==1
        parts = parcel.createTypedArrayList(Part.CREATOR)
        labours = parcel.createTypedArrayList(Labour.CREATOR)
        packages = parcel.createTypedArrayList(ServicePackage.CREATOR)
        payments = parcel.createTypedArrayList(Payment.CREATOR)
        packageIds = parcel.createStringArrayList()
        vehicleType = parcel.readString()
        totalPaidAmount = parcel.readDouble()
        outStandingAmount = parcel.readDouble()
        salvageValue = parcel.readDouble()
        excessClauseValue = parcel.readDouble()
        isThirdParty =parcel.readInt()==1
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(customerId)
        parcel.writeString(workshopId)
        parcel.writeString(invoiceId)
        parcel.writeString(insuranceInvoiceId)
        parcel.writeString(jobCardId)
        parcel.writeParcelable(customer, flags)
        parcel.writeString(proformaDate)
        parcel.writeString(date)
        parcel.writeString(dueDate)
        parcel.writeString(status)
        parcel.writeString(stepLabel)
        parcel.writeString(createdOn)
        parcel.writeString(updatedOn)
        parcel.writeParcelable(jobCard, flags)
        parcel.writeParcelable(summary, flags)
        parcel.writeParcelable(pdf, flags)
        parcel.writeParcelable(insurancePdf, flags)
        parcel.writeParcelable(igstCustomerPdf, flags)
        parcel.writeParcelable(thirdPartyDetails, flags)
        parcel.writeInt(if(splitInvoice)  1 else 0)
        parcel.writeInt(if(customerIgst)  1 else 0)
        parcel.writeTypedList(parts)
        parcel.writeTypedList(labours)
        parcel.writeTypedList(packages)
        parcel.writeTypedList(payments)
        parcel.writeStringList(packageIds)
        parcel.writeString(vehicleType)
        parcel.writeDouble(totalPaidAmount ?: 0.0)
        parcel.writeDouble(outStandingAmount ?: 0.0)
        parcel.writeDouble(salvageValue ?: 0.0)
        parcel.writeDouble(excessClauseValue ?: 0.0)
        parcel.writeInt(if(isThirdParty)  1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Invoice> {

        const val STATUS_PROFORMA = "PROFORMA"
        const val STATUS_INVOICED = "INVOICED"
        const val STATUS_PAID = "PAID"
        const val STATUS_PAID_PARTIAL = "PAID_PARTIAL"
        const val STATUS_DUE = "DUE"
        const val STATUS_CLOSED = "CLOSED"
        const val STATUS_CANCEL = "CANCEL"

        override fun createFromParcel(parcel: Parcel): Invoice {
            return Invoice(parcel)
        }

        override fun newArray(size: Int): Array<Invoice?> {
            return arrayOfNulls(size)
        }
    }
}
