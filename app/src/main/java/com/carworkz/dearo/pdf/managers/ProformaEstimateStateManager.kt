package com.carworkz.dearo.pdf.managers

import android.os.Parcelable
import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.domain.entities.PDF
import kotlinx.parcelize.Parcelize


@Parcelize
class ProformaEstimateStateManager(val invoiceId: String, val displayId: String) : StateManager(), Parcelable {

    override fun executeAction() = Unit

    override fun getActionText(): String? = null

    override fun <T> react(reactionValue: ReactionValue<T>?) = Unit

    override fun goNext() = Unit

    override fun start() {
        requireNotNull(stateManagerActionInteractionProvider) { " action provider cannot be null" }
        repository.getProformaEstimatePdf(invoiceId, object : DataSource.OnResponseCallback<PDF> {
            override fun onSuccess(obj: PDF) {
                pdfs.add(obj)
                stateManagerActionInteractionProvider?.dismissProgressIndicator()
                stateManagerActionInteractionProvider?.create()
            }

            override fun onError(error: ErrorWrapper) {
                stateManagerActionInteractionProvider?.dismissProgressIndicator()
                stateManagerActionInteractionProvider?.showErrorMessage(error.errorMessage)
            }
        })
    }

    override fun getMainTitle(selectedPdfPos: Int): String = displayId
}