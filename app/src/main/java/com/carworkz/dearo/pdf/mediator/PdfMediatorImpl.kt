package com.carworkz.dearo.pdf.mediator

import android.app.Activity
import android.content.Context
import com.carworkz.dearo.domain.entities.Invoice
import com.carworkz.dearo.domain.entities.PDF
import com.carworkz.dearo.extensions.toast
import com.carworkz.dearo.pdf.MultiPdfActivity
import com.carworkz.dearo.pdf.Source
import com.carworkz.dearo.pdf.managers.*
import javax.inject.Inject

class PdfMediatorImpl @Inject constructor() : PdfMediator {

    override fun createInvoicePdf(context: Context, invoiceId: String, displayId: String) {

    }

    override fun startOtcProformaPdfWithResult(activity: Activity, invoice: Invoice, requestCode: Int) {
        val stateManager = ProformaStateManager(invoice, null, Source.OTC)
        activity.startActivityForResult(MultiPdfActivity.getIntent(activity, stateManager), requestCode)
    }

    override fun startOtcInvoicePreview(context: Context, invoice: Invoice) {
        val stateManager = InvoiceStateManager(invoice, null, Source.OTC)
        context.startActivity(MultiPdfActivity.getIntent(context, stateManager))
    }

    override fun startProformaPdf(context: Context, invoice: Invoice, jobCardId: String, source: Source) {
        val stateManager = ProformaStateManager(invoice, jobCardId, source)
        context.startActivity(MultiPdfActivity.getIntent(context, stateManager))
    }

    override fun startProformaPdfWithResult(activity: Activity, invoice: Invoice, jobCardId: String, source: Source, requestCode: Int) {
        val stateManager = ProformaStateManager(invoice, jobCardId, source)
        activity.startActivityForResult(MultiPdfActivity.getIntent(activity, stateManager), requestCode)
    }

    override fun startProformaEstimatePdf(context: Context, invoiceId: String, displayId: String) {
        val stateManager = ProformaEstimateStateManager(invoiceId, displayId)
        context.startActivity(MultiPdfActivity.getIntent(context, stateManager))
    }

    override fun startInvoicePreviewPdf(context: Context, invoice: Invoice, jobCardId: String, source: Source) {
        val stateManager = InvoiceStateManager(invoice, jobCardId, source)
        context.startActivity(MultiPdfActivity.getIntent(context, stateManager))
    }

    override fun startJobCardEstimatePdf(context: Context, jobCardId: String, displayId: String) {
        val stateManager = JobCardEstimateStateManager(jobCardId, displayId)
        context.startActivity(MultiPdfActivity.getIntent(context, stateManager))
    }

    override fun startJobCardDetailsPdf(context: Context, jobCardId: String, displayId: String, source: Source) {
        val stateManager = JobCardDetailsStateManager(jobCardId, displayId, null, source)
        context.startActivity(MultiPdfActivity.getIntent(context, stateManager))
        // context.startActivity(NewPdfActivity.getIntent(context, displayId, jobCardId, null, PdfMediator.Action.ACTION_JOB_CARD_DETAILS))
    }

    override fun startJobCardPdcPdf(
        context: Context,
        jobCardId: String,
        displayId: String
    ) {
        val stateManager = JobCardPdcStateManager(jobCardId, displayId)
        context.startActivity(MultiPdfActivity.getIntent(context, stateManager))
    }

    override fun startGatePassPdf(context: Context, jobCardId: String, displayId: String) {
        val stateManager = GatePassStateManager(jobCardId, displayId)
        context.startActivity(MultiPdfActivity.getIntent(context, stateManager))
    }

    override fun startAmcInvoicePdf(context: Context, amcPdf: PDF?)
    {
        if(amcPdf==null)
        {
            context.toast("File not available\nContact support team")
        }
        else
        {
            val stateManager= AMCInvoiceStateManager(amcPdf)
            context.startActivity(MultiPdfActivity.getIntent(context, stateManager))
        }
    }

    override fun startPaymentReceiptPdf(context: Context, receiptId: String, displayId: String) {
        val stateManager = PaymentReceiptStateManager(receiptId, displayId)
        context.startActivity(MultiPdfActivity.getIntent(context, stateManager))
    }
}