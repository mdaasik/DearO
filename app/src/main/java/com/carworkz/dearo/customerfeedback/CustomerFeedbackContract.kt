package com.carworkz.dearo.customerfeedback

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView

interface CustomerFeedbackContract {

    interface View : BaseView<Presenter> {

        fun onFeedBackSavedSuccess()
    }

    interface Presenter : BasePresenter {

        fun saveFeedback(
            jobcardId: String,
            recommendedScore: Int,
            serviceQuality: Float,
            billingTransparency: Float,
            timelyDelivery: Float,
            comments: String?
        )
    }
}