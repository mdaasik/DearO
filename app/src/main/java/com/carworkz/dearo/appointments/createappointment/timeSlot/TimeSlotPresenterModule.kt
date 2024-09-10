package com.carworkz.dearo.appointments.createappointment.timeSlot

import com.carworkz.dearo.injection.FragmentScoped
import dagger.Module
import dagger.Provides

/**
 * Created by kush on 8/12/17.
 */
@Module
class TimeSlotPresenterModule(val view: TimeSlotContract.View) {
    @Provides
    @FragmentScoped
    fun providesView(): TimeSlotContract.View {
        return view
    }
}