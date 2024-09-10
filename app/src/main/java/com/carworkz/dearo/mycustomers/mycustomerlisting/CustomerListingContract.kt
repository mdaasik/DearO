package com.carworkz.dearo.mycustomers.mycustomerlisting

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.CustomerVehicleDetails

interface CustomerListingContract {
    interface View : BaseView<Presenter> {
        fun displayCustomers(list: List<CustomerVehicleDetails>, totalItemCount: Int)

        fun showError()
    }

    interface Presenter : BasePresenter {
        fun getListing(filterList: List<String>?, startDate: String?, endDate: String?, skip: Int, limit: Int)
    }
}