package com.carworkz.dearo.otc

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.HSN
import com.carworkz.dearo.domain.entities.Invoice
import javax.inject.Inject

class OtcProformaPresenter @Inject constructor(private var view: OtcProformaContract.View?, private val dataRepo: DearODataRepository) : OtcProformaContract.Presenter {

    override fun getProformaInvoice(invoiceId: String) {
        view?.showProgressIndicator()
        dataRepo.getInvoiceById(invoiceId, object : DataSource.OnResponseCallback<Invoice> {
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
            override fun onSuccess(invoiceObj: Invoice) {
                dataRepo.getHSN(object : DataSource.OnResponseCallback<List<HSN>> {
                    override fun onSuccess(obj: List<HSN>) {
                        view?.dismissProgressIndicator()
                        view?.displayProforma(invoiceObj, obj)
                    }

                    override fun onError(error: ErrorWrapper) = Unit
                })
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun updateProformaInvoice(invoiceId: String, invoice: Invoice, isPreview: Boolean, isSaveOnly: Boolean) {
        view?.showProgressIndicator()
        dataRepo.updateProforma(invoiceId, invoice, object : DataSource.OnResponseCallback<Invoice> {
            override fun onSuccess(invoice: Invoice) {
                view?.dismissProgressIndicator()
                if (isSaveOnly) {
                    dataRepo.getHSN(object : DataSource.OnResponseCallback<List<HSN>> {
                        override fun onSuccess(obj: List<HSN>) {
                            view?.dismissProgressIndicator()
                            view?.displayProforma(invoice, obj)
                        }

                        override fun onError(error: ErrorWrapper) = Unit
                    })
                } else {
                    view?.moveToNextScreen(isPreview)
                }
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