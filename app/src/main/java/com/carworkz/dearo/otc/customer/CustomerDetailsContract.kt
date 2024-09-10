package com.carworkz.dearo.otc.customer

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.Address
import com.carworkz.dearo.domain.entities.Customer
import com.carworkz.dearo.domain.entities.Invoice

interface CustomerDetailsContract {
    interface View : BaseView<Presenter> {
        fun moveToNextScreen(obj: Invoice)

        fun fillCustomerDetails(customer: Customer)

        fun setCity(s: String)

        fun cityError()

        fun emailError(error: String)
    }

    interface Presenter : BasePresenter {
        fun saveCustomerDetails(customer: Customer, address: Address, vehicleType: String?)

        fun findCustomer(mobile: String)

        fun pinCodeCity(pinCode: Int)

        fun addOtcProforma(customerId: String, addressId: String, vehicleType: String?)
    }
}