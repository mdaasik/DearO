package com.carworkz.dearo.login.addmobilenumber

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView

/**
 * Created by Farhan on 31/7/17.
 */
interface LoginContract {

    interface Presenter : BasePresenter {
        fun initOtp(mobileNo: String)

        fun logout()
    }

    interface View : BaseView<Presenter> {

        fun toggleLoginButton(isEnabled: Boolean)

        fun moveToNextScreen()

        fun moveToContactUsScreen(leadId: String)

        fun showPhoneNumberNotValidError()
    }
}