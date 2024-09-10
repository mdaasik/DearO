package com.carworkz.dearo.otc.customer

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.utils.Constants.ApiConstants
import timber.log.Timber
import javax.inject.Inject

class CustomerDetailsPresenter @Inject constructor(var view: CustomerDetailsContract.View?, val dataRepo: DearODataRepository) : CustomerDetailsContract.Presenter {

    override fun findCustomer(mobile: String) {
        view?.showProgressIndicator()
        dataRepo.getCustomerDetails(mobile, object : DataSource.OnResponseCallback<List<Customer>> {
            override fun onSuccess(obj: List<Customer>) {
                if (obj.isNotEmpty()) {
                    view?.fillCustomerDetails(obj[0])
                } else {
                    Timber.d("No Customer Details Found")
                }
                view?.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper) {
                view?.showGenericError(error.errorMessage)
                view?.dismissProgressIndicator()
            }
        })
    }

    override fun pinCodeCity(pinCode: Int) {
        dataRepo.pinCodeCity(pinCode, object : DataSource.OnResponseCallback<Pincode> {
            override fun onSuccess(obj: Pincode) {
                view?.setCity(obj.city?.city ?: "")
            }

            override fun onError(error: ErrorWrapper) {
                view?.cityError()
            }
        })
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun saveCustomerDetails(customer: Customer, address: Address, vehicleType: String?) {
        view?.showProgressIndicator()
        //here we need to update new API
        //create single obj for customer and address
        val custAndAddress= CustomerAndAddress().apply {
            mobile=customer.mobile
            name=customer.name
            email=customer.email
            gstRegistered= customer.gst!=null
            gstNumber=customer.gst
            isRegisteredDealer=customer.registeredDealer
            street=address.street
            location=address.location
            pincode= address.pincode!!
            city=address.city
            state=address.state

            ownerType="Customer"
        }
        dataRepo.addCustomer(custAndAddress, object : DataSource.OnResponseCallback<CustomerAndAddressResponse> {
            override fun onSuccess(response : CustomerAndAddressResponse)
            {
                addOtcProforma(response.customer?.id!!, response.address?.id!!, vehicleType)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                error.errorMessages?.forEach {
                    when (it.key) {
                        ApiConstants.KEY_CUSTOMER_EMAIL -> view?.emailError(it.value.joinToString(","))
                        else -> view?.showGenericError(error.errorMessage)
                    }
                } ?: run {
                    view?.showGenericError(error.errorMessage)
                }
            }
        })
    }

    override fun addOtcProforma(customerId: String, addressId: String, vehicleType: String?) {
        view?.showProgressIndicator()
        dataRepo.addOtcProforma(customerId, addressId, vehicleType, object : DataSource.OnResponseCallback<Invoice> {
            override fun onSuccess(obj: Invoice) {
                view?.moveToNextScreen(obj)
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
        view = null
    }
}