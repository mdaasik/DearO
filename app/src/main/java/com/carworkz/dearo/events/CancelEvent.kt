package com.carworkz.dearo.events

/**
*
 *
 *
 *
 *
 * Event fired when a job card/invoice/appointment is cancelled.
 *
 * @param type represents [JOBCARD]/[INVOICE]/[APPOINTMENT]
 * @param id represents to jobcardId/InvoiceId/AppointmentId
 *
 *  @author Farhan Patel.
* */
class CancelEvent(val type: String, val id: String) {
    companion object {
        const val JOBCARD = "jobcard"
        const val INVOICE = "invoice"
        const val APPOINTMENT = "appointment"
        const val AMC = "AMC"
        const val APPROVE = "approve"
    }
}