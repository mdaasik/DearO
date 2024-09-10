package com.carworkz.dearo.othersyshistory

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.CustomerVehicleDetails
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.domain.entities.OtherSysHistory
import com.carworkz.dearo.domain.entities.ServiceDate
import timber.log.Timber
import javax.inject.Inject

class OtherHistoryPresenter @Inject constructor(private var view: OtherHistoryContract.View, private val dataRepo: DearODataRepository) : OtherHistoryContract.Presenter {



    override fun getOtherSysHistory(registrationNumber: String,mobileNumber:String) {
        view.showProgressIndicator()
        dataRepo.getOtherHistory(registrationNumber,mobileNumber, object : DataSource.OnResponseCallback<CustomerVehicleDetails> {
            override fun onSuccess(obj: CustomerVehicleDetails) {
                view.displayHistory(obj)
                view.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper) {
                view.dismissProgressIndicator()
                view.showGenericError(error.errorMessage)
            }
        })
    }


    override fun start() {
    }

    override fun detachView() {
    }
}