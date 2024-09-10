package com.carworkz.dearo.pdf.mediator

import android.app.Activity
import android.content.Context
import com.carworkz.dearo.domain.entities.AMC
import com.carworkz.dearo.domain.entities.Invoice
import com.carworkz.dearo.domain.entities.PDF
import com.carworkz.dearo.pdf.Source

interface PdfMediator {

    fun createInvoicePdf(context: Context, invoiceId: String, displayId: String)

    fun startProformaPdf(context: Context, invoice: Invoice, jobCardId: String, source: Source)

    fun startProformaPdfWithResult(activity: Activity, invoice: Invoice, jobCardId: String, source: Source, requestCode: Int)

    fun startOtcProformaPdfWithResult(activity: Activity, invoice: Invoice, requestCode: Int)

    fun startOtcInvoicePreview(context: Context, invoice: Invoice)

    fun startInvoicePreviewPdf(context: Context, invoice: Invoice, jobCardId: String, source: Source)

    fun startProformaEstimatePdf(context: Context, invoiceId: String, displayId: String) // done

    fun startJobCardEstimatePdf(context: Context, jobCardId: String, displayId: String)

    fun startJobCardDetailsPdf(context: Context, jobCardId: String, displayId: String, source: Source) // done

    fun startJobCardPdcPdf(context: Context, jobCardId: String, displayId: String) // done

    fun startGatePassPdf(context: Context, jobCardId: String, displayId: String) // done

    fun startAmcInvoicePdf(context: Context, amcPdf: PDF?) //

    fun startPaymentReceiptPdf(context: Context, receiptId: String, displayId: String) //
}