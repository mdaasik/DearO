package com.carworkz.dearo.addjobcard.createjobcard.accidental.selectionaddress

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.InsuranceCompany
import com.carworkz.dearo.domain.entities.InsuranceCompanyDetails
import com.carworkz.dearo.domain.entities.State
import com.carworkz.dearo.injection.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class InsuranceAddressSelectionPresenter @Inject constructor(private var view: InsuranceAddressSelectionContract.View?, private val repository: DearODataRepository) : InsuranceAddressSelectionContract.Presenter {
    override fun getInsuranceAddress(company: InsuranceCompany) {
        view?.showProgressIndicator()
        repository.getInsuranceCompanyAddresses(company, object : DataSource.OnResponseCallback<List<InsuranceCompanyDetails>> {
            override fun onSuccess(obj: List<InsuranceCompanyDetails>) {
                view?.dismissProgressIndicator()
                view?.displayAddress(obj)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun start() {
    }

    override fun getInsuranceStates() {
        view?.showProgressIndicator()
        repository.getStates(object : DataSource.OnResponseCallback<List<State>> {
            override fun onSuccess(obj: List<State>) {
                view?.dismissProgressIndicator()
                view?.onStatesFetched(obj)
            }

            override fun onError(error: ErrorWrapper) {
                view?.showGenericError(error.errorMessage)
                view?.dismissProgressIndicator()
            }
        })
    }

    override fun detachView() {
        view = null
    }
}