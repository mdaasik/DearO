package com.carworkz.dearo.cronjobs.imageupload

import dagger.Module
import dagger.Provides

@Module
class ImageUploadPresenterModule(private val view: ImageUploadContract.View) {

    @Provides
    fun providesView(): ImageUploadContract.View {
        return view
    }
}