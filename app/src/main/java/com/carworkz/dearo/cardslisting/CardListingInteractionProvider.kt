package com.carworkz.dearo.cardslisting

import com.carworkz.dearo.domain.entities.AMC
import com.carworkz.dearo.domain.entities.Appointment
import com.carworkz.dearo.domain.entities.Invoice
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.pdf.Source

/**
 * Created by farhan on 03/01/18.
 */
interface CardListingInteractionProvider {

    fun acceptAppointment(appointment: Appointment)

    fun updateLeadStatus(appointment: Appointment)

    fun rescheduleAppointment(appointmentId: String)

    fun callCancelJobCard(jobCardId: String)

    fun callCompleteJobCard(jobCard: JobCard)

    fun callCloseJobCard(jobCardId: String)

    fun callCreateJobCard(appointment: Appointment)

    fun callUpdatePayment(invoiceId: String, displayInvoiceId: String, jobCardId: String)

    fun getCardType(): String

    fun startEditProformaActivity(invoice: Invoice, jobCardId: String?, displayJcId: String?, invoiceId: String?, displayInvoiceId: String?, splitInvoice: Boolean, vehicleType: String?,jcType:String, requestCode: Int)

    fun startOtcProformaActivity(invoiceId: String, displayInvoiceId: String, vehicleType: String?)

    fun startInvoicePreview(invoice: Invoice, jobCardId: String, source: Source)

    fun startProformaPdf(invoice: Invoice, jobCardId: String, source: Source)

    fun startOtcInvoicePreview(invoice: Invoice)

    fun startJobCardDetailsPreview(jobCardId: String, displayId: String, source: Source)

    fun getAmcId():String?

    fun getJobCardById(id: String)
}