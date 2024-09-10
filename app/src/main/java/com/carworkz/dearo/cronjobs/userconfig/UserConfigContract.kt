package com.carworkz.dearo.cronjobs.userconfig

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView

/**
 * Created by farhan on 17/10/17.
 */
interface UserConfigContract {

    interface View : BaseView<Presenter> {

        fun onJobDone()

        fun onJobFailure()
    }

    interface Presenter : BasePresenter {

        fun getGstStatus()
    }
}