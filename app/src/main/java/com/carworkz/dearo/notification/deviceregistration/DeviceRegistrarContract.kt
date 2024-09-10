package com.carworkz.dearo.notification.deviceregistration

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView

interface DeviceRegistrarContract {

    interface View : BaseView<Presenter> {
        fun onRegistrationSuccess()

        fun onRegistrationFailed()
    }

    interface Presenter : BasePresenter {
        fun registerDevice(
            workshopId: String,
            userId: String,
            accessToken: String,
            deviceId: String,
            deviceName: String,
            platform: String,
            version: String,
            fcmToken: String
        )
    }
}