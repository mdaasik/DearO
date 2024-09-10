package com.carworkz.dearo.addjobcard.createjobcard.estimate.partnumberselection.searchpartnumber

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.PartNumber
import com.carworkz.dearo.domain.entities.VEHICLE_FLEXIBLE
import javax.inject.Inject

class SearchPartNumberPresenter @Inject constructor(private var view: SearchPartNumberContract.View?, private val repository: DearODataRepository) : SearchPartNumberContract.Presenter {
    override fun searchPartNumber(query: String, partId: String?, jobCardId: String?, vehicleType: String?,packageId:String?) {
        view?.showProgressIndicator()
        repository.searchPartNumber(query, partId, jobCardId, null, null, null, null, true, vehicleType, VEHICLE_FLEXIBLE,packageId, object : DataSource.OnResponseCallback<List<PartNumber>> {
            override fun onSuccess(obj: List<PartNumber>) {
                view?.displaySearchResults(obj)
                view?.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(errorMsg = error.errorMessage)
            }
        })
    }

    override fun searchInStockPartNumber(query: String, jobCardId: String?, brandId: String?, vehicleType: String?) {
        view?.showProgressIndicator()
        repository.searchInStockPartNumbers(query, jobCardId, null, brandId, vehicleType, "",object : DataSource.OnResponseCallback<List<PartNumber>> {
            override fun onSuccess(obj: List<PartNumber>) {
                view?.dismissProgressIndicator()
                view?.displaySearchResults(obj)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
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