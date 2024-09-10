package com.carworkz.dearo.pdf

import com.carworkz.dearo.pdf.managers.StateManager

/**/
interface StateManagerActionContract {

    fun startServiceRemainder(jobCardId: String, invoiceId: String)

    fun startPartPayment(invoiceId: String, jobCardId: String, displayId: String)

    fun displayRaiseInvoiceDialog()

    fun hideActionButton()

    /**
     * Refresh current state.
    * */
    fun restart()

    /**
     * clear current view for fresh rendering.
    * */
    fun invalidate()

    /**
     *used to render pdfs & initialization when fetched from server
     *
    * */
    fun create()

    fun showProgressIndicator()

    fun dismissProgressIndicator()

    fun setNextStateManager(stateManager: StateManager)

    fun showErrorMessage(errorMessage: String)

    fun setActivityResultOk()

    fun startDigitalSignatureActivity(jobCardId: String)
}