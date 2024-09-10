package com.carworkz.dearo.cronjobs.imageupload

import dagger.Subcomponent

@Subcomponent(modules = [(ImageUploadPresenterModule::class)])
interface ImageUploadComponent {
    fun inject(job: ImageUploadJob)
}