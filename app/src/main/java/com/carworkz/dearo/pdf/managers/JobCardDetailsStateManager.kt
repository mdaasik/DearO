package com.carworkz.dearo.pdf.managers


import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.domain.entities.PDF
import com.carworkz.dearo.domain.entities.Signature
import com.carworkz.dearo.pdf.Source
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
class JobCardDetailsStateManager(val jobcardId: String, val displayId: String, private var signature: Signature? = null, val source: Source) : StateManager(), Parcelable {

    override fun executeAction() {
        stateManagerActionInteractionProvider?.startDigitalSignatureActivity(jobcardId)
    }

    override fun getActionText(): String? {
        if (source != Source.IN_PROGRESS)
            return null

        return if (signature == null) {
            "Add Sign."
        } else {
            "Update Sign."
        }
    }

    override fun <T> react(reactionValue: ReactionValue<T>?) {
        stateManagerActionInteractionProvider?.invalidate()
        pdfs.clear()
        start()
    }

    override fun goNext() = Unit

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun start() {
        requireNotNull(stateManagerActionInteractionProvider) { "Action provider cannot be null" }
        repository.getJobCardPreview(jobcardId, object : DataSource.OnResponseCallback<PDF> {
            override fun onSuccess(pdf: PDF) {
                repository.getJobCardDetails(jobcardId, arrayOf("customerSignature"), object : DataSource.OnResponseCallback<JobCard> {
                    override fun onSuccess(jobCard: JobCard) {
                        pdfs.add(pdf)
                        signature = jobCard.customerSignature
                        stateManagerActionInteractionProvider?.dismissProgressIndicator()
                        stateManagerActionInteractionProvider?.create()
                    }

                    override fun onError(error: ErrorWrapper) {
                        stateManagerActionInteractionProvider?.dismissProgressIndicator()
                        stateManagerActionInteractionProvider?.showErrorMessage(error.errorMessage)
                    }
                })
            }

            override fun onError(error: ErrorWrapper) {
                stateManagerActionInteractionProvider?.dismissProgressIndicator()
                stateManagerActionInteractionProvider?.showErrorMessage(error.errorMessage)
            }
        })
    }

    override fun getMainTitle(selectedPdfPos: Int): String = displayId
}