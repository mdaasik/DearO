package com.carworkz.dearo.addjobcard.createjobcard.damage.damagegallery

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Module
import dagger.Provides

@Module
class DamageGalleryPresenterModule(private val view: DamageGalleryContract.View) {
    @Provides
    @ActivityScoped
    fun providesView(): DamageGalleryContract.View {
        return view
    }
}