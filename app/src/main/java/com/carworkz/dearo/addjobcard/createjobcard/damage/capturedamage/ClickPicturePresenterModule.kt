package com.carworkz.dearo.addjobcard.createjobcard.damage.capturedamage

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Module
import dagger.Provides

/**
 * Created by kush on 23/8/17.
 */
@Module
class ClickPicturePresenterModule(private val view: CaptureImageContract.View) {

    @Provides
    @ActivityScoped
    fun providesView(): CaptureImageContract.View {
        return view
    }
}
