package com.carworkz.dearo.pdf.managers

import android.os.Parcelable
import com.carworkz.dearo.domain.entities.PDF
import kotlinx.parcelize.Parcelize

@Parcelize
class AMCInvoiceStateManager(var amcPdf: PDF) : StateManager(), Parcelable {

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
        pdfs.add(amcPdf)
        stateManagerActionInteractionProvider?.create()
        stateManagerActionInteractionProvider?.dismissProgressIndicator()
    }
}
