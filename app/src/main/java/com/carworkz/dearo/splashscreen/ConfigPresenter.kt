package com.carworkz.dearo.splashscreen

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.AppUpdate
import com.carworkz.dearo.domain.entities.HSN
import com.carworkz.dearo.domain.entities.NetworkPostResponse
import com.carworkz.dearo.domain.entities.User
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Farhan on 26/10/17.
 */
class ConfigPresenter @Inject constructor(private var view: ConfigContract.View?, private val dataRepo: DearODataRepository) : ConfigContract.Presenter {

    override fun getHsn() {
        dataRepo.getHSN(object : DataSource.OnResponseCallback<List<HSN>> {
            override fun onSuccess(obj: List<HSN>) {
                Timber.d("Fetched HSN")
            }

            override fun onError(error: ErrorWrapper) {
            }
        })
    }

    override fun clearHsn() {
        dataRepo.deleteHSN(object : DataSource.OnResponseCallback<Boolean> {
            override fun onSuccess(obj: Boolean) {
                Timber.d("hsn deleted $obj")
            }

            override fun onError(error: ErrorWrapper) {
                Timber.d("error deleting hsn")
            }
        })
    }

    override fun clearCustomerGroup() {
        dataRepo.deleteCustomerGroup(object : DataSource.OnResponseCallback<Boolean>{
            override fun onSuccess(obj: Boolean) {
                Timber.d("customer group deleted $obj")
            }

            override fun onError(error: ErrorWrapper) {
                Timber.d("error deleting customer group")
            }


        })
    }

    override fun getGstStatus() {
        dataRepo.getUserConfig(object : DataSource.OnResponseCallback<User> {
            override fun onError(error: ErrorWrapper) {
                Timber.e("gst Status " + error.errorMessage)
            }

            override fun onSuccess(obj: User) {
                Timber.d("ReadOtp gst " + obj.status)
            }
        })
    }

    override fun logout() {
        view?.showProgressIndicator()
        dataRepo.logoutUser(object : DataSource.OnResponseCallback<NetworkPostResponse?> {
            override fun onSuccess(obj: NetworkPostResponse?) {
                view?.dismissProgressIndicator()
                view?.moveToNextScreen()
                Timber.d("logout success ")
            }

            override fun onError(error: ErrorWrapper) {
                Timber.d("logout fail ")
                view?.dismissProgressIndicator()
                view?.moveToNextScreen()
            }
        })
    }

    override fun checkForceUpdate(appName: String, platform: String, versionCode: Int) {
        dataRepo.checkForceUpdate(appName, platform, versionCode, object : DataSource.OnResponseCallback<AppUpdate> {
            override fun onSuccess(obj: AppUpdate) {
                view?.moveToNextScreen()
            }

            override fun onError(error: ErrorWrapper) {
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