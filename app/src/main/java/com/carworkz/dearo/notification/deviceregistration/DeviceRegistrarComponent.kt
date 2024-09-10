package com.carworkz.dearo.notification.deviceregistration

import dagger.Subcomponent

@Subcomponent(modules = [DeviceRegistrarModule::class])
interface DeviceRegistrarComponent {
    fun inject(service: DeviceRegistrarJob)
}