package com.carworkz.dearo.login.lead

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.LeadForm
import javax.inject.Inject

class LeadPresenter @Inject constructor(var view: LeadContract.View?, val dataRepo: DearODataRepository) : LeadContract.Presenter {

    override fun saveLeadInfo(leadId: String, mobile: String, name: String, city: String) {
        view?.showProgressIndicator()
        dataRepo.saveLead(leadId, LeadForm(mobile, name, city), object : DataSource.OnResponseCallback<LeadForm> {
            override fun onSuccess(obj: LeadForm) {
                view?.dismissProgressIndicator()
                view?.moveToNextScreen()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
            }
        })
    }

    override fun start() {
    }

    override fun detachView() {
        view = null
    }
}