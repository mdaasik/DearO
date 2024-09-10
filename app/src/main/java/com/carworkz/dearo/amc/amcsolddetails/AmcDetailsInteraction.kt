package com.carworkz.dearo.amc.amcsolddetails

import com.carworkz.dearo.domain.entities.Invoice
import com.carworkz.dearo.pdf.Source

interface AmcDetailsInteraction {
    fun getJobCardById(id: String)
    // fun startInvoicePreview(invoiceId: String, jobCardId: String, displayId: String, isSplit: Boolean)
    fun startInvoicePreview(invoice: Invoice, jobCardId: String, source: Source)

}