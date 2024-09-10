package com.carworkz.dearo.addjobcard.createjobcard.damage.capturedamage

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

/**
 * Created by kush on 23/8/17.
 */
@ActivityScoped
@Subcomponent(modules = [(ClickPicturePresenterModule::class)])
interface CapturePictureComponent {
    fun inject(uploadService: UploadService)
}
