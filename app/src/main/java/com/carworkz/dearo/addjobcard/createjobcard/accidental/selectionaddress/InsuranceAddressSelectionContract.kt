package com.carworkz.dearo.addjobcard.createjobcard.accidental.selectionaddress

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.InsuranceCompany
import com.carworkz.dearo.domain.entities.InsuranceCompanyDetails
import com.carworkz.dearo.domain.entities.State

interface InsuranceAddressSelectionContract {

    interface View : BaseView<Presenter> {
        fun displayAddress(address: List<InsuranceCompanyDetails>)

        fun onStatesFetched(states: List<State>)
    }

    interface Presenter : BasePresenter {
        fun getInsuranceAddress(company: InsuranceCompany)

        fun getInsuranceStates()
    }
}