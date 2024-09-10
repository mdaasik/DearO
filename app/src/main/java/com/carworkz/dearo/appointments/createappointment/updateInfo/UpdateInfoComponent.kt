package com.carworkz.dearo.appointments.createappointment.updateInfo

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

/**
 * Created by Farhan on 8/1/18.
 */
@ActivityScoped
@Subcomponent(modules = [(UpdateInfoPresenterModule::class)])
interface UpdateInfoComponent {
    fun inject(UpdateInfoActivity: UpdateInfoActivity)
}