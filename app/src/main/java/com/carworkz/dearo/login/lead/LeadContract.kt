package com.carworkz.dearo.login.lead

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView

interface LeadContract {
    interface View : BaseView<Presenter> {

        fun moveToNextScreen()
    }

    interface Presenter : BasePresenter {
        fun saveLeadInfo(leadId: String, mobile: String, name: String, city: String)
    }
}