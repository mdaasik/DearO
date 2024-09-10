package com.carworkz.dearo.login.readotp

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView

/**
 * Created by Farhan on 2/8/17.
 */
interface ReadOtpContract {

    interface View : BaseView<Presenter> {

        fun onOtpMatchSuccess()

        fun showOtpSentMessage()

        fun showOtpDidNotMatchError(message: String)
    }

    interface Presenter : BasePresenter {
        fun verifyOtp(otp: Int, mobileNo: String)

        fun resendOtp(mobileNo: String)

        fun checkForceUpdate(appName: String, platform: String, versionCode: Int)

        fun getInitData()
    }
}