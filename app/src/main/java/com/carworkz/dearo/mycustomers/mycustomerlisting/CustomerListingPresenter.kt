package com.carworkz.dearo.mycustomers.mycustomerlisting

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.CustomerVehicleDetails
import com.carworkz.dearo.helpers.PaginationList
import javax.inject.Inject

class CustomerListingPresenter @Inject constructor(private var view: CustomerListingContract.View?, private val dataRepo: DearODataRepository) : CustomerListingContract.Presenter {
    override fun getListing(filterList: List<String>?, startDate: String?, endDate: String?, skip: Int, limit: Int) {
        view?.showProgressIndicator()
        dataRepo.getCustomerVehicleList(null, filterList, startDate, endDate, skip, limit, object : DataSource.OnResponseCallback<PaginationList<CustomerVehicleDetails>> {
            override fun onSuccess(obj: PaginationList<CustomerVehicleDetails>) {
                view?.displayCustomers(obj, obj.metaData?.totalItemsCount ?: 0)
                view?.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showError()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun start() {
    }

    override fun detachView() {
        view = null
    }
}