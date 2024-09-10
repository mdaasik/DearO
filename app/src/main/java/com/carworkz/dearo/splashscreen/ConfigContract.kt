package com.carworkz.dearo.splashscreen

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView

/**
 * Created by Farhan on 26/10/17.
 */
interface ConfigContract {

    interface View : BaseView<Presenter> {
        fun moveToNextScreen()
    }

    interface Presenter : BasePresenter {

        fun getGstStatus()

        fun checkForceUpdate(appName: String, platform: String, versionCode: Int)

        fun getHsn()

        fun clearHsn()

        fun clearCustomerGroup()

        fun logout()
    }
}