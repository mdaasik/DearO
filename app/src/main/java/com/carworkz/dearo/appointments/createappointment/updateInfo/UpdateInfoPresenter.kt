package com.carworkz.dearo.appointments.createappointment.updateInfo

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.Pincode
import com.carworkz.dearo.domain.entities.UpsertDetails
import com.carworkz.dearo.domain.entities.Variant
import com.carworkz.dearo.injection.ActivityScoped
import javax.inject.Inject

/**
 * Created by Farhan on 8/1/18.
 */
@ActivityScoped
class UpdateInfoPresenter @Inject constructor(private var view: UpdateInfoContract.View?, private val dearODataRepository: DearODataRepository) : UpdateInfoContract.Presenter {

    override fun start() {
    }

    override fun detachView() {
        view = null
    }

    override fun getVariant(fuelType: String, slug: String) {
        dearODataRepository.getVariant(slug, fuelType, object : DataSource.OnResponseCallback<List<Variant>> {
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

    override fun getCity(pincode: Int) {
        dearODataRepository.pinCodeCity(pincode, object : DataSource.OnResponseCallback<Pincode> {
            override fun onSuccess(obj: Pincode) {
                view?.displayCity(obj.city?.city ?: "")
            }

            override fun onError(error: ErrorWrapper) {
                view?.cityError(error.errorMessage)
            }
        })
    }

    override fun upsertData(appointmentId: String, upsertDetails: UpsertDetails) {
        view?.showProgressIndicator()
        dearODataRepository.upsertData(appointmentId, upsertDetails, object : DataSource.OnResponseCallback<UpsertDetails> {
            override fun onSuccess(obj: UpsertDetails) {
                view?.dismissProgressIndicator()
                view?.moveToNextScreen(obj)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }
}