package com.carworkz.dearo.pdf.managers

import android.os.Parcelable
import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.domain.entities.Invoice
import com.carworkz.dearo.domain.entities.PDF
import com.carworkz.dearo.pdf.Source
import kotlinx.parcelize.Parcelize

/**
 * Manages Proforma state for OTC module
 *
 * Note: OTC states have only one pdf.
* */
@Suppress("DEPRECATED_ANNOTATION")
@Parcelize
class OtcProformaStateManager(val invoice: Invoice, val source: Source) : StateManager(), Parcelable {

    /**
     * Otc Proforma state only supports single action of "Raise Invoice'.
    * */
    override fun executeAction() {
        stateManagerActionInteractionProvider?.displayRaiseInvoiceDialog()
    }

    override fun getActionText(): String? = "Raise Invoice"

    override fun <T> react(reactionValue: ReactionValue<T>?) {
        when (val value = reactionValue?.obj) {
            is Boolean -> {
                stateManagerActionInteractionProvider?.showProgressIndicator()
                repository.createInvoice(invoice.id!!, value, null, object : DataSource.OnResponseCallback<List<PDF>> {
                    override fun onSuccess(obj: List<PDF>) {
                        val pdf = obj.first()
                        invoice.invoiceId = pdf.invoiceId
                        stateManagerActionInteractionProvider?.dismissProgressIndicator()
                        stateManagerActionInteractionProvider?.setActivityResultOk()
                        goNext()
                    }

                    override fun onError(error: ErrorWrapper) {
                        stateManagerActionInteractionProvider?.dismissProgressIndicator()
                        stateManagerActionInteractionProvider?.showErrorMessage(errorMessage = error.errorMessage)
                    }
                })
            }
        }
    }

    override fun goNext() {
        val invoiceStateManager = OtcInvoiceStateManager(invoice, source)
        stateManagerActionInteractionProvider?.invalidate()
        stateManagerActionInteractionProvider?.setNextStateManager(invoiceStateManager)
        stateManagerActionInteractionProvider?.restart()
    }

    override fun start() {
        requireNotNull(stateManagerActionInteractionProvider) { "Action provider cannot be null" }
        getProforma()
    }

    override fun getMainTitle(selectedPdfPos: Int): String {
        return "Proforma"
        // invoice.invoiceId!!
    }

    private fun getProforma() {
        stateManagerActionInteractionProvider?.showProgressIndicator()
        repository.getProformaPDF(invoice.id!!, object : DataSource.OnResponseCallback<List<PDF>> {
            override fun onSuccess(obj: List<PDF>) {
                val pdf = obj.first()
                pdf.title = invoice.invoiceId
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