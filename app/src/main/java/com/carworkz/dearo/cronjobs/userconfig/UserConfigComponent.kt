package com.carworkz.dearo.cronjobs.userconfig

import dagger.Subcomponent

/**
 * Created by Farhan on 18/10/17.
 */
@Subcomponent(modules = [(UserConfigPresenterModule::class)])
interface UserConfigComponent {

    fun inject(jobService: UserConfigService)
}