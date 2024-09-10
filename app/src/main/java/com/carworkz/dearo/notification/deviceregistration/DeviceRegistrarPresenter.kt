package com.carworkz.dearo.notification.deviceregistration

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.domain.entities.FcmTokenEntity
import com.carworkz.dearo.domain.entities.NetworkPostResponse
import javax.inject.Inject

class DeviceRegistrarPresenter @Inject constructor(private val dataRepository: DearODataRepository, private var view: DeviceRegistrarContract.View?) : DeviceRegistrarContract.Presenter {

    override fun registerDevice(
        workshopId: String,
        userId: String,
        accessToken: String,
        deviceId: String,
        deviceName: String,
        platform: String,
        version: String,
        fcmToken: String
    ) {
        dataRepository.registerDevice(deviceId, FcmTokenEntity(workshopId, userId, accessToken, deviceId, deviceName, platform, version, fcmToken), object : DataSource.OnResponseCallback<NetworkPostResponse> {
            override fun onSuccess(obj: NetworkPostResponse) {
                view?.onRegistrationSuccess()
                SharedPrefHelper.setFcmSynced()
            }

            override fun onError(error: ErrorWrapper) {
                view?.onRegistrationFailed()
            }
        })
    }

    override fun start() = Unit

    override fun detachView() {
        view = null
    }
}