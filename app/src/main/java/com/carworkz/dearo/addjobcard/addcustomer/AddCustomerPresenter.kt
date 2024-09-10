package com.carworkz.dearo.addjobcard.addcustomer

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.injection.ActivityScoped
import com.carworkz.dearo.utils.Constants.ApiConstants
import javax.inject.Inject

/**
 * Created by Farhan on 9/8/17.
 */
@ActivityScoped
class AddCustomerPresenter @Inject constructor(var view: AddCustomerContract.View?, private val dataRepository: DearODataRepository) : AddCustomerContract.Presenter {

    override fun start() {
    }

    override fun detachView() {
        view = null
    }

    override fun addNewCustomer(customer: Customer, address: Address) {
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
            customerGroupId=customer.customerGroupId
            ownerType="Customer"
        }

        dataRepository.addCustomer(custAndAddress, object : DataSource.OnResponseCallback<CustomerAndAddressResponse> {
            override fun onSuccess(obj: CustomerAndAddressResponse) {
                view?.moveToNextScreen()
                view?.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                error.errorMessages?.let {
                    it.forEach { entry ->
                        when (entry.key) {
                            ApiConstants.KEY_CUSTOMER_EMAIL -> view?.emailError(entry.value.joinToString(","))
                            else -> view?.displayError(error.errorMessage)
                        }
                    }
                } ?: run {
                    view?.displayError(error.errorMessage)
                }
            }
        })
    }

    override fun pinCodeCity(pinCode: Int) {
        dataRepository.pinCodeCity(pinCode, object : DataSource.OnResponseCallback<Pincode> {
            override fun onSuccess(obj: Pincode) {
                view?.setCity(obj.city)
            }
            override fun onError(error: ErrorWrapper) {
                view?.cityError()
            }
        })
    }

    override fun getCustomerGroup() {
        dataRepository.getCustomerGroup(object : DataSource.OnResponseCallback<List<Option> > {
            override fun onSuccess(obj: List<Option>) {
                view?.showCustomerGroup(obj)
            }

            override fun onError(error: ErrorWrapper) {
                view?.showError("Could not find customer group")
            }

        })
    }

    override fun updateCustomer(customerId: String, customer: Customer, address: Address) {
        view?.showProgressIndicator()
        dataRepository.updateCustomer(customerId, customer, address, object : DataSource.OnResponseCallback<Customer> {
            override fun onSuccess(obj: Customer) {
                view?.dismissProgressIndicator()
                view?.moveToNextScreen()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                error.errorMessages?.forEach {
                    when (it.key) {
                        ApiConstants.KEY_CUSTOMER_MOBILE -> view?.mobileError(it.value.joinToString(","))
                        else -> view?.displayError(error.errorMessage)
                    }
                }
                if (error.errorMessages == null) {
                    view?.displayError(error.errorMessage)
                }
            }
        })
    }

    override fun getCustomerById(Id: String) {
        view?.showProgressIndicator()
        dataRepository.getCustomerById(Id, object : DataSource.OnResponseCallback<Customer> {
            override fun onSuccess(obj: Customer) {
                view?.showCustomerDetails(obj)
                view?.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper) {
                view?.displayError(error.errorMessage)
            }
        })
    }
}