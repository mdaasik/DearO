package com.carworkz.dearo.appointments.createappointment.appointmentDetails

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.injection.FragmentScoped
import okhttp3.ResponseBody
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kush on 24/11/17.
 */
@FragmentScoped
class AppointmentDetailsPresenter @Inject constructor(var view: AppointmentDetailsContract.View?, val dearODataRepository: DearODataRepository) : AppointmentDetailsContract.Presenter {

    override fun start() {
    }

    override fun detachView() {
        view = null
    }

    override fun getMake(vehicleType: String?) {
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

    override fun getVariant(slug: String) {
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

    override fun getCustomerDetails(mobileNumber: String) {
        view?.showProgressIndicator()
        dearODataRepository.getCustomerDetails(mobileNumber, object : DataSource.OnResponseCallback<List<Customer>> {
            override fun onSuccess(obj: List<Customer>) {
                if (obj.isNotEmpty()) {
                    view?.displayCustomerData(obj[0])
                } else {
                    Timber.d("No Matching Customer Found")
                    view?.resetCustomer()
                }
                view?.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper) {
                view?.showGenericError(error.errorMessage)
                view?.dismissProgressIndicator()
            }
        })
    }

    override fun getVehicleDetails(registrationNumber: String) {
        view?.showProgressIndicator()
        dearODataRepository.getVehicleDetails(registrationNumber, object : DataSource.OnResponseCallback<List<Vehicle>> {
            override fun onSuccess(obj: List<Vehicle>) {
                if (obj.isNotEmpty()) {
                    view?.displayVehicleData(obj[0])
                } else {
                    view?.resetVehicleData()
                    Timber.d("No Matching Vehicle Found")
                }
                view?.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper) {
                view?.resetVehicleData()
                view?.showGenericError(error.errorMessage)
                view?.dismissProgressIndicator()
            }
        })
    }

    override fun saveDetails(appointment: Appointment) {
        view?.showProgressIndicator()
        dearODataRepository.saveDetails(appointment, object : DataSource.OnResponseCallback<Appointment> {
            override fun onSuccess(obj: Appointment) {
                view?.dismissProgressIndicator()
                if (SharedPrefHelper.isAppointmentServicePackageActive() && SharedPrefHelper.isAppointmentServicePackageMandatory() && obj.suggestedPackages?.list?.isEmpty() == true) {
                    view?.showGenericError("Service Packages not Available for this Vehicle")
                } else {
                    view?.moveToNextScreen(obj)
                }
            }

            override fun onError(error: ErrorWrapper) {
//                error.errorMessages?.run {
//                    if (get("mobile") != null){
//                        view?.showMobileError(getValue("mobile")!![0])
//                    }
//                } ?:
                view?.showError(error.errorMessage)
                view?.dismissProgressIndicator()
            }
        })
    }


    override fun newNextApiCall(phone: String, registration: String) {
        view?.showProgressIndicator()

        dearODataRepository.newSearchApiCall(phone,registration, object : DataSource.OnResponseCallback<ResponseBody>{
            override fun onSuccess(obj: ResponseBody) {
                view?.newNextApiUpdate(true,"")
                view?.dismissProgressIndicator()

            }

            override fun onError(error: ErrorWrapper) {

                view?.newNextApiUpdate(false,error.errorMessage.toString())
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })

    }

    override fun getCityByPincode(pincode: Int) {
        view?.showProgressIndicator()
        dearODataRepository.pinCodeCity(pincode, object : DataSource.OnResponseCallback<Pincode> {
            override fun onSuccess(obj: Pincode) {
                view?.dismissProgressIndicator()
                view?.displayPincodeData(obj)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showError(error.errorMessage)
            }
        })
    }
}