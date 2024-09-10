package com.carworkz.dearo.addjobcard.addcustomer

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.Address
import com.carworkz.dearo.domain.entities.City
import com.carworkz.dearo.domain.entities.Customer
import com.carworkz.dearo.domain.entities.Option

/**
 * Created by Farhan on 8/8/17.
 */
interface AddCustomerContract {

    interface View : BaseView<Presenter> {

        fun moveToNextScreen()

        fun displayError(Error: String)

        fun showError(error: String)

        fun cityError()

        fun emailError(error: String)

        fun setCity(city: City?)

        fun showCustomerDetails(customer: Customer)

        fun showCustomerGroup(customerGroupList: List<Option>)

        fun mobileError(error: String)
    }

    interface Presenter : BasePresenter {

        fun addNewCustomer(customer: Customer, address: Address)

        fun updateCustomer(customerId: String, customer: Customer, address: Address)

        fun getCustomerById(Id: String)

        fun pinCodeCity(pinCode: Int)

        fun getCustomerGroup()
    }
}