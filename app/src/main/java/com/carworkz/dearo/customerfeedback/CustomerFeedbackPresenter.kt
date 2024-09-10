package com.carworkz.dearo.customerfeedback

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.NetworkPostResponse
import com.carworkz.dearo.injection.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class CustomerFeedbackPresenter @Inject constructor(private var view: CustomerFeedbackContract.View?, private val dataRepository: DearODataRepository) : CustomerFeedbackContract.Presenter {

    override fun saveFeedback(jobcardId: String, recommendedScore: Int, serviceQuality: Float, billingTransparency: Float, timelyDelivery: Float, comments: String?) {
        view?.showProgressIndicator()
        dataRepository.saveCustomerFeedback(jobcardId, recommendedScore, serviceQuality, billingTransparency, timelyDelivery, comments, object : DataSource.OnResponseCallback<NetworkPostResponse> {
            override fun onSuccess(obj: NetworkPostResponse) {
                view?.dismissProgressIndicator()
                view?.onFeedBackSavedSuccess()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun start() = Unit

    override fun detachView() {
        view = null
    }
}