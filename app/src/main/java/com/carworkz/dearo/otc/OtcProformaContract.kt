package com.carworkz.dearo.otc

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.HSN
import com.carworkz.dearo.domain.entities.Invoice

interface OtcProformaContract {

    interface View : BaseView<Presenter> {

        fun displayProforma(invoice: Invoice, hsnList: List<HSN>)

        fun showError(error: String?)

        fun moveToNextScreen(preview: Boolean)
    }

    interface Presenter : BasePresenter {

        fun getProformaInvoice(invoiceId: String)

        fun updateProformaInvoice(invoiceId: String, invoice: Invoice, isPreview: Boolean, isSaveOnly: Boolean)
    }
}