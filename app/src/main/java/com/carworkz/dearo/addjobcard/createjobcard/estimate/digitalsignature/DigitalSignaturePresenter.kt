package com.carworkz.dearo.addjobcard.createjobcard.estimate.digitalsignature

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.Signature
import java.io.File
import javax.inject.Inject

class DigitalSignaturePresenter @Inject constructor(private var view: DigitalSignatureContract.View?, private val dataRepo: DearODataRepository) : DigitalSignatureContract.Presenter {

    override fun saveSignature(jobCardId: String, file: File) {
        view?.showProgressIndicator()
        dataRepo.saveSignature(jobCardId, file, object : DataSource.OnResponseCallback<Signature> {
            override fun onSuccess(obj: Signature) {
                view?.dismissProgressIndicator()
                view?.moveToNextScreen()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun start() {
    }

    override fun detachView() {
        view = null
    }
}