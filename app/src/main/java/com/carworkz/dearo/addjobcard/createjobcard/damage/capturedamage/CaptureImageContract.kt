package com.carworkz.dearo.addjobcard.createjobcard.damage.capturedamage

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.FileObject

/**
 * Created by kush on 23/8/17.
 */

interface CaptureImageContract {
    interface View : BaseView<Presenter> {
        fun onDamageUploadFinish()
    }

    interface Presenter : BasePresenter {
        fun saveDamageImage(image: FileObject)

        fun savePDCImage(image: FileObject)

        fun saveDocument(file: FileObject)
    }
}
