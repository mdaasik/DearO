package com.carworkz.dearo.pdf.managers

import com.carworkz.dearo.data.Result
import com.carworkz.dearo.domain.entities.Invoice
import com.carworkz.dearo.pdf.Source
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Manages Invoice state for both OTC & Job Cards.
 *
 * @param jobCardId mandatory in case of jobcard invoice for updating payments.
 *
* */

@Parcelize
class InvoiceStateManager(var invoice: Invoice, val jobCardId: String?, val source: Source) : StateManager(), Parcelable {

    init {
        if (source != Source.OTC && jobCardId == null) {
            throw IllegalStateException("jobcard id is mandatory when source is $source ")
        }
    }

    override fun executeAction() {
        if (source == Source.PROFORMA || source == Source.COMPLETED || source == Source.INVOICED) {
            stateManagerActionInteractionProvider?.startPartPayment(invoice.id!!, jobCardId!!, invoice.invoiceId!!)
        }
    }

    override fun getActionText(): String? {
        Timber.d("")
        return if (source == Source.PROFORMA || source == Source.COMPLETED || source == Source.INVOICED)
        null
        else
            null
    }

    override fun <T> react(reactionValue: ReactionValue<T>?) {
        if (source == Source.PROFORMA || source == Source.COMPLETED || source == Source.INVOICED) {
            stateManagerActionInteractionProvider?.hideActionButton()
            stateManagerActionInteractionProvider?.setActivityResultOk()
        }
    }

    override fun goNext() = Unit

    override fun start() {
        requireNotNull(stateManagerActionInteractionProvider) { "Action provider cannot be null" }
        getInvoicePdf()
    }

    override fun getMainTitle(selectedPdfPos: Int): String {
        return "Invoice"
    }

    private fun getInvoicePdf() {
        stateManagerActionInteractionProvider?.showProgressIndicator()
        launch {
            val job = async {
                repository.getInvoicePdfs(invoice.id!!)
            }
            when (val result = job.await()) {
                is Result.Success -> {
                    pdfs.addAll(result.data)
                    stateManagerActionInteractionProvider?.create()
                    stateManagerActionInteractionProvider?.dismissProgressIndicator()
                }

                is Result.Error -> {
                    stateManagerActionInteractionProvider?.dismissProgressIndicator()
                    stateManagerActionInteractionProvider?.showErrorMessage(result.errorWrapper.errorMessage)
                }
            }
        }
    }
}