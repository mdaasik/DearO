package com.carworkz.dearo.pdf.managers

import android.os.Parcelable
import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.domain.entities.Invoice
import com.carworkz.dearo.pdf.Source
import kotlinx.parcelize.Parcelize

/**
 * Manages Invoice state for OTC module
 *
 * Note: OTC states have only one pdf.
 * */

@Parcelize
class OtcInvoiceStateManager(var invoice: Invoice, val source: Source) : StateManager(), Parcelable {

    override fun executeAction() = Unit

    override fun getActionText(): String? = null

    override fun <T> react(reactionValue: ReactionValue<T>?) = Unit

    override fun goNext() = Unit

    override fun start() {
        requireNotNull(stateManagerActionInteractionProvider) { "Action provider cannot be null" }
        getOnlyInvoice()
    }

    override fun getMainTitle(selectedPdfPos: Int): String {
        return "Invoice"
        // invoice.invoiceId!!
    }

    private fun getOnlyInvoice() {
        stateManagerActionInteractionProvider?.showProgressIndicator()
        repository.getInvoicePreview(invoice.id!!, object : DataSource.OnResponseCallback<Invoice> {
            override fun onSuccess(obj: Invoice) {
                invoice = obj
                val pdf = requireNotNull(obj.pdf) { "invoice preview pdf null" }
                pdf.title = obj.invoiceId
                pdfs.add(pdf)
                stateManagerActionInteractionProvider?.create()
                stateManagerActionInteractionProvider?.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper) {
                stateManagerActionInteractionProvider?.dismissProgressIndicator()
                stateManagerActionInteractionProvider?.showErrorMessage(error.errorMessage)
            }
        })
    }
}