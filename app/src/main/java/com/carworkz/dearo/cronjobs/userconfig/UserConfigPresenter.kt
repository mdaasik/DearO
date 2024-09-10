package com.carworkz.dearo.cronjobs.userconfig

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.User
import javax.inject.Inject

/**
 * Created by farhan on 17/10/17.
 */
class UserConfigPresenter @Inject constructor(private var view: UserConfigContract.View?, private val dearODataRepository: DearODataRepository) : UserConfigContract.Presenter {

    override fun getGstStatus() {
        dearODataRepository.getUserConfig(object : DataSource.OnResponseCallback<User> {
            override fun onSuccess(obj: User) {
                view?.onJobDone()
            }

            override fun onError(error: ErrorWrapper) {
                view?.onJobFailure()
            }
        })
    }

    override fun start() {
    }

    override fun detachView() {
        view = null
    }
}