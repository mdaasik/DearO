package com.carworkz.dearo.cronjobs.imageupload

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView

interface ImageUploadContract {

    interface View : BaseView<Presenter> {

        fun onUploadingSuccessfully()
        fun onUploadingFailure()
    }

    interface Presenter : BasePresenter {
        // fun saveUnUploadedImages()

        fun syncImages()
    }
}