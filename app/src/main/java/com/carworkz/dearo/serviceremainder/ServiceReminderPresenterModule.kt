package com.carworkz.dearo.serviceremainder

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Module
import dagger.Provides

@Module
class ServiceReminderPresenterModule(val view: ServiceReminderContract.View) {

    @Provides
    @ActivityScoped
    fun providesView(): ServiceReminderContract.View {
        return view
    }
}