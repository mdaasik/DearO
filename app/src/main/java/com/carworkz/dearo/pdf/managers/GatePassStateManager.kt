package com.carworkz.dearo.pdf.managers

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.domain.entities.PDF
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import android.os.Parcelable

@Parcelize
class GatePassStateManager(private val jobCardId: String, val displayId: String) : StateManager(), Parcelable {

    override fun getActionText(): String? = null

    override fun executeAction() = Unit

    override fun <T> react(reactionValue: ReactionValue<T>?) = Unit

    override fun goNext() = Unit

    override fun getMainTitle(selectedPdfPos: Int): String {
        return displayId
    }

    override fun start() {
        requireNotNull(stateManagerActionInteractionProvider) { "Action provider cannot be null" }
        repository.getGatePassPreview(jobCardId, callback = object : DataSource.OnResponseCallback<PDF> {
            override fun onSuccess(obj: PDF) {
                pdfs.add(obj)
                stateManagerActionInteractionProvider?.create()
            }

            override fun onError(error: ErrorWrapper) {
                Timber.d("error ${error.errorMessage}")
                stateManagerActionInteractionProvider?.dismissProgressIndicator()
                stateManagerActionInteractionProvider?.showErrorMessage(error.errorMessage)
            }
        })
    }
}