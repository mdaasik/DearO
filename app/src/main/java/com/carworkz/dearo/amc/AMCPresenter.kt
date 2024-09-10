package com.carworkz.dearo.amc

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.AMC
import com.carworkz.dearo.domain.entities.AMCPackage
import com.carworkz.dearo.domain.entities.Packages
import org.json.JSONObject
import javax.inject.Inject

class AMCPresenter @Inject constructor(private var dearODataRepository: DearODataRepository, var view: AMCContract.View?) : AMCContract.Presenter {

    override fun suggestAmcPackages(vehicleId: String)
    {
        view?.showProgressIndicator()
        dearODataRepository.suggestAmcPackages(vehicleId,object : DataSource.OnResponseCallback<List<AMCPackage>>{
            override fun onSuccess(obj: List<AMCPackage>) {
                view?.dismissProgressIndicator()
                view?.updateAmcPackages(obj)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.errorToast(error.errorMessage)
            }
        })
    }

    override fun purchaseAmc(amcDetails: AMCPurchase)
    {
        view?.showProgressIndicator()
        dearODataRepository.purchaseAMC(amcDetails,object : DataSource.OnResponseCallback<AMC>{
            override fun onSuccess(obj: AMC) {
                view?.dismissProgressIndicator()
                view?.showSuccess(obj)
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
