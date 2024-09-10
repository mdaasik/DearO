package com.carworkz.dearo.addjobcard.createjobcard.estimate.digitalsignature

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import java.io.File

interface DigitalSignatureContract {
    interface View : BaseView<Presenter> {
        fun moveToNextScreen()
    }

    interface Presenter : BasePresenter {
        fun saveSignature(jobCardId: String, file: File)
    }
}