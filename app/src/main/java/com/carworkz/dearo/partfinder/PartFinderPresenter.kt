package com.carworkz.dearo.partfinder

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.PartNumber
import com.carworkz.dearo.domain.entities.VEHICLE_STRICT
import com.carworkz.dearo.injection.ActivityScoped
import timber.log.Timber
import javax.inject.Inject

@ActivityScoped
class PartFinderPresenter @Inject constructor(
    var view: PartFinderContract.View?,
    private val dearODataRepository: DearODataRepository
) : PartFinderContract.Presenter {

    override fun searchPart(query: String, make: String, model: String, variant: String, fuelType: String, vehicleType: String?) {
        dearODataRepository.searchPartNumber(query, null, null, make, model, variant, fuelType, false, vehicleType, VEHICLE_STRICT,null, object : DataSource.OnResponseCallback<List<PartNumber>> {
            override fun onSuccess(obj: List<PartNumber>) {
                Timber.d("inside part finder success $obj")
                view?.onSearchResultReceived(obj)
            }

            override fun onError(error: ErrorWrapper) {
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