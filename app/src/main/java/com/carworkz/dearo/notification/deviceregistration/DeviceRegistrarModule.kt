package com.carworkz.dearo.notification.deviceregistration

import dagger.Module
import dagger.Provides

@Module
class DeviceRegistrarModule(private val view: DeviceRegistrarContract.View) {

    @Provides
    fun providesView(): DeviceRegistrarContract.View {
        return view
    }
}