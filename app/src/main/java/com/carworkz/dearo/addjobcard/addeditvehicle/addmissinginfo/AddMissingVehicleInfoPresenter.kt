package com.carworkz.dearo.addjobcard.addeditvehicle.addmissinginfo

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.data.Result
import com.carworkz.dearo.domain.entities.Variant
import com.carworkz.dearo.domain.entities.VehicleVariantBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AddMissingVehicleInfoPresenter @Inject constructor(private var view: AddMissingVehicleInfoContract.View?, private val repository: DearODataRepository) : AddMissingVehicleInfoContract.Presenter, CoroutineScope {

    private val parentJob = Job()

    private lateinit var variants: List<Variant>

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + parentJob

    override fun saveVariantDetails(vehicleId: String, variantBody: VehicleVariantBody) {
        launch {
            view?.showProgressIndicator()
            val result = repository.updateVehicleVariantInfo(vehicleId, variantBody)
            view?.dismissProgressIndicator()
            when (result) {
                is Result.Success -> {
                    view?.onUpdateSuccess()
                }

                is Result.Error -> {
                    view?.showGenericError(result.errorWrapper.errorMessage)
                }
            }
        }
    }

    override fun getFuelTypes(modelSlug: String) {
        view?.showProgressIndicator()
        repository.getVariant(modelSlug, null, object : DataSource.OnResponseCallback<List<Variant>> {
            override fun onSuccess(obj: List<Variant>) {
                variants = obj
                view?.dismissProgressIndicator()
                view?.displayFuelTypes(obj.mapNotNull { it.fuelType }.distinct())
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun getDescriptions(fuelType: String) {
        view?.displayDescriptions(variants.asSequence().filter { it.fuelType == fuelType && it.description.isNullOrEmpty().not() }.mapNotNull { it.description }.distinct().toMutableList())
    }

    override fun getVariants(description: String?, fuelType: String) {
        if (description != null)
            view?.displayVariants(variants.filter { it.description == description && it.fuelType == fuelType }.toMutableList())
        else {
            view?.displayVariants(variants.filter { it.fuelType == fuelType }.toMutableList())
        }
    }

    override fun getTransmissions(description: String?, fuelType: String) {
        if (description != null)
            view?.displayTransmission(variants.asSequence().filter { it.description == description && it.fuelType == fuelType && it.transmissionType != null }.map { it.transmissionType }.filterNotNull().distinct().toMutableList())
        else {
            view?.displayTransmission(variants.asSequence().filter { it.fuelType == fuelType && it.transmissionType != null }.map { it.transmissionType }.filterNotNull().distinct().toMutableList())
        }
    }

    override fun start() {
    }

    override fun detachView() {
        view = null
        parentJob.cancel()
    }
}