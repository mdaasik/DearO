package com.carworkz.dearo.appointments.createappointment.servicePackages

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.Appointment
import com.carworkz.dearo.domain.entities.AppointmentPost
import com.carworkz.dearo.injection.FragmentScoped
import com.carworkz.dearo.utils.Constants.ApiConstants
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Kush Singh Chibber on 11/25/2017.
 */
@FragmentScoped
class ServicePackagePresenter @Inject constructor(private var view: ServicePackageContract.View?, private val dearODataRepository: DearODataRepository) : ServicePackageContract.Presenter {

    override fun start() {
    }

    override fun detachView() {
        view = null
    }

    override fun getPackages(appointmentId: String) {
        view?.showProgressIndicator()
        view?.dismissProgressIndicator()
    }

    override fun filterPackages(filterType: String) {
        Timber.d("Applying Filter")
        view?.displayFilteredPackages(filterType)
    }

    override fun savePackages(appointmentId: String, appointmentPost: AppointmentPost) {
        view?.showProgressIndicator()
        dearODataRepository.savePackages(appointmentId, appointmentPost, object : DataSource.OnResponseCallback<Appointment> {
            override fun onSuccess(obj: Appointment) {
                view?.dismissProgressIndicator()
                view?.moveToNextScreen(obj)
            }

            override fun onError(error: ErrorWrapper) {
                if (error.errorMessages != null) {
                    error.errorMessages.forEach {
                        when (it.key) {
                            ApiConstants.KEY_PACKAGE_ID -> view?.displayPackageError(it.value.joinToString(","))
                            else -> view?.showGenericError(error.errorMessage)
                        }
                    }
                } else {
                    view?.displayPackageError(error.errorMessage)
                }
                view?.dismissProgressIndicator()
            }
        })
    }
}