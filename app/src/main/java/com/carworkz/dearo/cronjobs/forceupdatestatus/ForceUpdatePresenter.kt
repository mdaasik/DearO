package com.carworkz.dearo.cronjobs.forceupdatestatus

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.AppUpdate
import javax.inject.Inject

/**
 * Created by Farhan on 8/11/17.
 */

class ForceUpdatePresenter @Inject constructor(var view: ForceUpdateContract.View?, val dataRepository: DearODataRepository) : ForceUpdateContract.Presenter {
    override fun start() {
    }

    override fun detachView() {
        view = null
    }

    override fun checkForceUpdate(appName: String, platform: String, versionCode: Int) {
        dataRepository.checkForceUpdate(appName, platform, versionCode, object : DataSource.OnResponseCallback<AppUpdate> {
            override fun onSuccess(obj: AppUpdate) {
                view?.onJobDone()
            }

            override fun onError(error: ErrorWrapper) {
                view?.onJobFailure()
            }
        })
    }
}