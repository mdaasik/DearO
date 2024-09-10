package com.carworkz.dearo.pdf.managers

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.domain.entities.PDF
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import timber.log.Timber

@Parcelize
class JobCardEstimateStateManager(val jobCardId: String, val displayId: String) : StateManager(), Parcelable {

    override fun executeAction() = Unit

    override fun getActionText(): String? = null

    override fun <T> react(reactionValue: ReactionValue<T>?) = Unit

    override fun goNext() = Unit

    override fun start() {
        Timber.d("Jobcardestimate manager")
        requireNotNull(stateManagerActionInteractionProvider) { "remember to set action interaction provider" }
        stateManagerActionInteractionProvider?.showProgressIndicator()
        repository.getPrintEstimate(jobCardId, object : DataSource.OnResponseCallback<PDF> {
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