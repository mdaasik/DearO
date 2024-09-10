package com.carworkz.dearo.thirdpartydetails

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.City
import com.carworkz.dearo.domain.entities.ThirdParty

interface ThirdPartyDetailsContract {

    interface View : BaseView<Presenter> {

        fun onSaveSuccess(thirdParty: ThirdParty)

        fun onCityFetched(city: City?)

        fun invalidEmail()

        fun invalidMobileNumber()

        fun invalidGstNumber()

        fun emptyGstNumber()

        fun invalidAddress()

        fun invalidPincode()

        fun invalidName()
    }

    interface Presenter : BasePresenter {

        fun save(invoiceId: String, thirdParty: ThirdParty)

        fun validate(thirdParty: ThirdParty): Boolean

        fun getCityByPinCode(pincode: Int)
    }
}