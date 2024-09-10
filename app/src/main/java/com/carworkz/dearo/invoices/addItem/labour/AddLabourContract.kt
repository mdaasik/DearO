package com.carworkz.dearo.invoices.addItem.labour

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.Labour

/**
 * Created by kush on 13/9/17.
 */

interface AddLabourContract {

    interface Presenter : BasePresenter {
        fun saveLabour(invoiceId: String, labour: Labour)
        fun deleteLabour(invoiceId: String, labourId: String)
    }

    interface View : BaseView<Presenter> {
        fun moveToNextScreen(isDeleted: Boolean)

        fun onLabourSavedSuccess(labour: Labour)

        fun onLabourDeleted()

        fun setupTotal()
    }
}