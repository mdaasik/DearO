package com.carworkz.dearo.addjobcard.createjobcard.damage.damagegallery

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [(DamageGalleryPresenterModule::class)])
interface DamageGalleryComponent {
    fun inject(activity: DamageGalleryActivity)
}