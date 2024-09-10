package com.carworkz.dearo.invoices.addItem.labour

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.Invoice
import com.carworkz.dearo.domain.entities.Labour
import com.carworkz.dearo.injection.ActivityScoped
import javax.inject.Inject

/**
 * Created by kush on 13/9/17.
 */
@ActivityScoped
class AddLabourPresenter @Inject constructor(var view: AddLabourContract.View?, private val dearODataRepository: DearODataRepository) : AddLabourContract.Presenter {

    override fun saveLabour(invoiceId: String, labour: Labour) {
        view?.showProgressIndicator()
        dearODataRepository.saveLabour(invoiceId, labour, object : DataSource.OnResponseCallback<Labour> {
            override fun onSuccess(obj: Labour) {
                view?.dismissProgressIndicator()
                view?.onLabourSavedSuccess(obj)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun deleteLabour(invoiceId: String, labourId: String) {
        view?.showProgressIndicator()
        dearODataRepository.deleteLabour(invoiceId, labourId, object : DataSource.OnResponseCallback<Invoice> {
            override fun onSuccess(obj: Invoice) {
                view?.dismissProgressIndicator()
                view?.onLabourDeleted()
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