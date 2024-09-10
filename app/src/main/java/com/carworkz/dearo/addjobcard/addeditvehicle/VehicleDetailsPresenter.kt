package com.carworkz.dearo.addjobcard.addeditvehicle

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.*
import javax.inject.Inject

/**
 * Created by kush on 18/8/17.
 */

class VehicleDetailsPresenter @Inject constructor(private val dearODataRepository: DearODataRepository, var view: VehicleDetailsContract.View?) : VehicleDetailsContract.Presenter {

    override fun getMake(vehicleType: String?) {
        view?.showProgressIndicator()
        dearODataRepository.getMake(vehicleType, object : DataSource.OnResponseCallback<List<Make>> {
            override fun onSuccess(obj: List<Make>) {
                view?.displayMake(obj)
                view?.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun getModel(slug: String) {

        dearODataRepository.getModel(slug, object : DataSource.OnResponseCallback<List<Model>> {
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

    override fun getVariant(fuelType: String?, slug: String) {

        dearODataRepository.getVariant(slug, null, object : DataSource.OnResponseCallback<List<Variant>> {
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

    override fun addVehicle(vehicle: Vehicle) {
        view?.showProgressIndicator()

        dearODataRepository.addVehicle(vehicle, object : DataSource.OnResponseCallback<Vehicle> {
            override fun onSuccess(obj: Vehicle) {
                view?.dismissProgressIndicator()
                view?.moveToNextScreen()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        }
        )
    }

    override fun updateVehicle(vehicle: Vehicle) {
        view?.showProgressIndicator()
        dearODataRepository.updateVehicle(vehicle, object : DataSource.OnResponseCallback<Vehicle> {
                override fun onSuccess(obj: Vehicle) {
                    view?.dismissProgressIndicator()
                    view?.moveToNextScreen()
                }

                override fun onError(error: ErrorWrapper) {
                    view?.dismissProgressIndicator()
                }
            })
    }

    override fun getCompanyList() {
        dearODataRepository.getCompanyNames(object : DataSource.OnResponseCallback<List<InsuranceCompany>> {
            override fun onSuccess(obj: List<InsuranceCompany>) {
                view?.displayCompanySuggestions(obj)
            }

            override fun onError(error: ErrorWrapper) {
            }
        })
    }

    override fun start() {
    }

    override fun detachView() {
        view = null
    }
}