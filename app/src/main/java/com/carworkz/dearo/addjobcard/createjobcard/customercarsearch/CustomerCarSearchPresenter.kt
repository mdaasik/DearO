package com.carworkz.dearo.addjobcard.createjobcard.customercarsearch

import com.carworkz.dearo.domain.entities.CustomerVehicleSearch
import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.Customer
import com.carworkz.dearo.domain.entities.CustomerVehicleDetails
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.injection.ActivityScoped
import com.carworkz.dearo.utils.Constants
import okhttp3.ResponseBody
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kush on 17/8/17.
 */
@ActivityScoped
class CustomerCarSearchPresenter @Inject constructor(private var dearODataRepository: DearODataRepository, var view: CustomerCarSearchContract.View?) : CustomerCarSearchContract.Presenter
{

    override fun initiateJobCard(customerId: String, vehicleId: String, appointmentId: String?, vehicleAmcId: String?, type: String)
    {
        view?.showProgressIndicator()
        dearODataRepository.initiateJobCard(customerId, vehicleId, appointmentId, vehicleAmcId, type, object : DataSource.OnResponseCallback<JobCard>
        {
            override fun onSuccess(obj: JobCard)
            {
                view?.dismissProgressIndicator()
                view?.moveToNextScreen(obj)
            }

            override fun onError(error: ErrorWrapper)
            {
                view?.dismissProgressIndicator()
                view?.errorToast(error.errorMessage)
            }
        })
    }


    //new search api call.
    override fun newCarSearchApiCall(phone: String, registration: String) {
        view?.showProgressIndicator()

        dearODataRepository.newSearchApiCall(phone,registration, object : DataSource.OnResponseCallback<ResponseBody>{
            override fun onSuccess(obj: ResponseBody) {
                view?.newSearchApiUpdate(true,"")
            }

            override fun onError(error: ErrorWrapper) {
                view?.newSearchApiUpdate(false,error.errorMessage.toString())

                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })

    }

    override fun searchCarAndCustomer(phone: String, registration: String)
    {
        view?.showProgressIndicator()
        dearODataRepository.searchCustomerAndCarDetails(phone, registration, object : DataSource.OnResponseCallback<CustomerVehicleSearch>
        {
            override fun onSuccess(obj: CustomerVehicleSearch)
            {
                Timber.d("match", obj.decision.customer.toString() + " " + obj.decision.vehicle.toString())
                view?.dismissProgressIndicator()
                if (Constants.ApiConstants.FOUND == obj.decision.customer && Constants.ApiConstants.FOUND == obj.decision.vehicle)
                {
                    view?.updateCustomerViews(obj.customer)
                    view?.updateVehicleViews(obj.vehicle)
                    view?.displayHistory(obj)
                    if (obj.amc != null)
                        view?.updateAmcViews(obj.amc)
                    view?.toggleJobCardType(obj.vehicle.variant != null)
                    view?.toggleJobCardBtn(obj.vehicle.variant != null)
                    if (obj.vehicle.variant == null)
                    {
                        view?.updateVehicle()
                    }
                }
                else if (Constants.ApiConstants.MISMATCH == obj.decision.customer && Constants.ApiConstants.FOUND == obj.decision.vehicle)
                {
                    view?.updateCustomerViews(obj.customer)
                    view?.updateCustomerNumberViews(obj, false)
                    view?.updateVehicleViews(obj.vehicle)
                    view?.toggleJobCardBtn(false)
                    view?.toggleJobCardType(false)
                    if (obj.vehicle.variant == null)
                    {
                        view?.updateVehicle()
                    }
                }
                else if (Constants.ApiConstants.NOT_FOUND == obj.decision.customer && Constants.ApiConstants.FOUND == obj.decision.vehicle)
                {
                    view?.updateCustomerNumberViews(obj, true)
                    view?.updateVehicleViews(obj.vehicle)
                    view?.toggleJobCardBtn(false)
                    view?.toggleJobCardType(false)
                    if (obj.vehicle.variant == null)
                    {
                        view?.updateVehicle()
                    }
                }
                else if (Constants.ApiConstants.FOUND == obj.decision.customer && Constants.ApiConstants.NOT_FOUND == obj.decision.vehicle)
                {
                    view?.updateCustomerViews(obj.customer)
                    view?.addVehicle()
                    view?.toggleJobCardType(false)
                    view?.toggleJobCardBtn(false)
                }
                else if (Constants.ApiConstants.NOT_FOUND == obj.decision.customer && Constants.ApiConstants.NOT_FOUND == obj.decision.vehicle)
                {
                    view?.toggleJobCardBtn(false)
                    view?.toggleJobCardType(false)
                    view?.addCustomer()
                    view?.addVehicle()
                    view?.resetCustomer()
                }
            }

            override fun onError(error: ErrorWrapper)
            {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun addAlternateNumber(phone: String, customerId: String)
    {
        view?.showProgressIndicator()
        dearODataRepository.addAlternate(phone, customerId, object : DataSource.OnResponseCallback<Customer>
        {
            override fun onSuccess(obj: Customer)
            {
                view?.onUseAlternateNumberSuccess("Alternate Number Updated")
                view?.toggleJobCardBtn(true)
                view?.dismissProgressIndicator()
                view?.toggleJobCardType(true)
            }

            override fun onError(error: ErrorWrapper)
            {
                view?.dismissProgressIndicator()
                error.errorMessages?.let { messages ->
                    messages.forEach {
                        when (it.key)
                        {
                            Constants.ApiConstants.KEY_CCSEARCH_MOBILE -> view?.onUseAlternateNumberError(it.value.joinToString(","))
                            else                                       -> view?.showGenericError(error.errorMessage)
                        }
                    }
                } ?: view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun updateNumber(phone: String, customerId: String)
    {
        view?.showProgressIndicator()
        dearODataRepository.updateNumber(phone, customerId, object : DataSource.OnResponseCallback<Customer>
        {
            override fun onSuccess(obj: Customer)
            {
                view?.updateCustomerViews(obj)
                view?.toggleJobCardType(true)
                view?.toggleJobCardBtn(true)
                view?.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper)
            {
                view?.dismissProgressIndicator()
                error.errorMessages?.let { messages ->
                    messages.forEach {
                        when (it.key)
                        {
                            Constants.ApiConstants.KEY_CCSEARCH_MOBILE -> view?.onUseAlternateNumberError(it.value.joinToString(","))
                            else                                       -> view?.showGenericError(error.errorMessage)
                        }
                    }
                } ?: view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun getJobCardData(id: String, isDearoHistory: Boolean)
    {
        view?.showProgressIndicator()
        dearODataRepository.getCustomerVehicleHistory(id, object : DataSource.OnResponseCallback<CustomerVehicleDetails>
        {
            override fun onSuccess(obj: CustomerVehicleDetails)
            {
                view?.showHistory(obj, isDearoHistory)
                view?.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper)
            {
                view?.dismissProgressIndicator()
            }
        })
    }

    override fun getJobCard(id: String)
    {
        view?.showProgressIndicator()
        dearODataRepository.getJobCardById(id, null, object : DataSource.OnResponseCallback<JobCard>
        {
            override fun onSuccess(obj: JobCard)
            {
                view?.viewJC(obj)
                view?.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper)
            {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun start()
    {
    }

    override fun detachView()
    {
        view = null
    }
}
