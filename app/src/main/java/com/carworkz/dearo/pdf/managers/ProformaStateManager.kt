package com.carworkz.dearo.pdf.managers

import android.os.Parcelable
import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.domain.entities.Invoice
import com.carworkz.dearo.domain.entities.PDF
import com.carworkz.dearo.pdf.Source
import kotlinx.parcelize.Parcelize

/**
 * Manages proforma states & actions in case of both jobcard proforma & OTC proforma.
 *
 * Also check [StateManager] documentation to understand design.
* */

@Parcelize
class ProformaStateManager(val invoice: Invoice, val jobCardId: String?, val source: Source) : StateManager(), Parcelable {

    init {
        if (source != Source.OTC && jobCardId == null) {
            throw IllegalStateException("jobCard id is mandatory when source is $source ")
        }
    }

    /**
     *Actions for this state differ based on the [Source]
     *
     * [ProformaStateManager] supports 2 actions i.e. Raise Invoice(OTC Proforma) & start Service remainder activity(which eventually raises invoice in this case)
    * */
    override fun executeAction() {
        if (source == Source.COMPLETED || source == Source.PROFORMA)
            stateManagerActionInteractionProvider?.startServiceRemainder(jobCardId!!, invoice.id!!)

        if (source == Source.OTC) {
            stateManagerActionInteractionProvider?.displayRaiseInvoiceDialog()
        }
    }

    override fun getActionText(): String? = "Raise Invoice"

    override fun getMainTitle(selectedPdfPos: Int): String {
        return "Proforma"
//        val currentPdf = pdfs.getOrNull(selectedPdfPos)
//        return currentPdf?.invoiceId ?: invoice.invoiceId!!
    }

    override fun <T> react(reactionValue: ReactionValue<T>?) {
        when (val value = reactionValue?.obj) {
            is String -> {
                invoice.invoiceId = value
                stateManagerActionInteractionProvider?.setActivityResultOk()
                goNext()
            }
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
        val invoiceStateManager = InvoiceStateManager(invoice, jobCardId, source)
        stateManagerActionInteractionProvider?.invalidate()
        stateManagerActionInteractionProvider?.setNextStateManager(invoiceStateManager)
        stateManagerActionInteractionProvider?.restart()
    }

    override fun start() {
        requireNotNull(stateManagerActionInteractionProvider) { "Action provider cannot be null" }
        getProformaPdfs()
    }

    private fun getProformaPdfs() {
        stateManagerActionInteractionProvider?.showProgressIndicator()
        repository.getProformaPDF(invoice.id!!, object : DataSource.OnResponseCallback<List<PDF>> {
            override fun onSuccess(obj: List<PDF>) {
                pdfs.addAll(obj)
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