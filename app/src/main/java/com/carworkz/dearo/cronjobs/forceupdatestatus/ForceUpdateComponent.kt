package com.carworkz.dearo.cronjobs.forceupdatestatus

import dagger.Subcomponent

/**
 * Created by ambab on 8/11/17.
 */
@Subcomponent(modules = [(ForceUpdatePresenterModule::class)])
interface ForceUpdateComponent {

    fun inject(service: ForceUpdateService)
}