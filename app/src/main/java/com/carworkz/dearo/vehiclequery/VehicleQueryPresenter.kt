package com.carworkz.dearo.vehiclequery

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.Make
import com.carworkz.dearo.domain.entities.Model
import com.carworkz.dearo.domain.entities.Variant
import javax.inject.Inject

class VehicleQueryPresenter @Inject constructor(val view: VehicleQueryContract.View?, val dataRepo: DearODataRepository) : VehicleQueryContract.Presenter {

    override fun getMake(vehicleType: String?) {
        view?.showProgressIndicator()
        dataRepo.getMake(vehicleType, object : DataSource.OnResponseCallback<List<Make>> {
            override fun onSuccess(obj: List<Make>) {
                view?.dismissProgressIndicator()
                view?.displayMake(obj)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun getModel(makeSlug: String) {

        dataRepo.getModel(makeSlug, object : DataSource.OnResponseCallback<List<Model>> {
            override fun onSuccess(obj: List<Model>) {
                view?.displayModel(obj)
                view?.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun getVariant(modelSlug: String) {

        dataRepo.getVariant(modelSlug, null, object : DataSource.OnResponseCallback<List<Variant>> {
            override fun onSuccess(obj: List<Variant>) {
                view?.displayVariant(obj)
                view?.dismissProgressIndicator()
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
    }
}