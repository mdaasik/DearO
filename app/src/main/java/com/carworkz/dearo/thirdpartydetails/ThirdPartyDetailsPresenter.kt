package com.carworkz.dearo.thirdpartydetails

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.data.Result
import com.carworkz.dearo.domain.entities.Pincode
import com.carworkz.dearo.domain.entities.ThirdParty
import com.carworkz.dearo.utils.Utility
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ThirdPartyDetailsPresenter @Inject constructor(private var view: ThirdPartyDetailsContract.View?, private val repository: DearODataRepository) : ThirdPartyDetailsContract.Presenter, CoroutineScope {

    private val parentJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main

    override fun save(invoiceId: String, thirdParty: ThirdParty) {
        launch {
            view?.showProgressIndicator()
            val job = async {
                repository.saveThirdPartyDetails(invoiceId, thirdParty)
            }
            val result = job.await()
            System.out.println("inside result $result")
            view?.dismissProgressIndicator()
            when (result) {
                is Result.Success -> {
                    System.out.println("inside success")
                    view?.onSaveSuccess(result.data)
                }
                is Result.Error -> {
                    System.out.println("inside Error")
                    view?.showGenericError(result.errorWrapper.errorMessage)
                }
            }
        }
    }

    override fun validate(thirdParty: ThirdParty): Boolean {

        var isValid = true

        if (thirdParty.name.isEmpty()) {
            isValid = false
            view?.invalidName()
        }

        if (thirdParty.mobile.isNullOrEmpty().not() && Utility.isMobileNumberValid(thirdParty.mobile).not()) {
            isValid = false
            view?.invalidMobileNumber()
        }

        if (thirdParty.email.isNullOrEmpty().not() && Utility.isEmailValid(thirdParty.email).not()) {
            isValid = false
            view?.invalidEmail()
        }

        if (Utility.isValidGst(thirdParty.gstNumber).not()) {
            isValid = false
            if (thirdParty.gstNumber.isEmpty())
                view?.emptyGstNumber()
            else
                view?.invalidGstNumber()
        }

        if (thirdParty.address.street.isNullOrEmpty()) {
            isValid = false
            view?.invalidAddress()
        }

        if (thirdParty.address.pincode == null || Utility.isPinCodeValid(thirdParty.address.pincode.toString()).not()) {
            isValid = false
            view?.invalidPincode()
        }

        return isValid
    }

    override fun getCityByPinCode(pincode: Int) {
        view?.showProgressIndicator()
        repository.pinCodeCity(pincode, object : DataSource.OnResponseCallback<Pincode> {
            override fun onSuccess(obj: Pincode) {
                view?.dismissProgressIndicator()
                view?.onCityFetched(obj.city)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError("Cannot Fetch City for the PinCode,Please try again!")
            }
        })
    }

    override fun start() {
    }

    override fun detachView() {
        view = null
        parentJob.cancel()
    }
}