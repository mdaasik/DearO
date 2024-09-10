package com.carworkz.dearo.mycustomers.cutomervehiclehistory

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.CustomerVehicleDetails
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.domain.entities.ServiceDate
import timber.log.Timber
import javax.inject.Inject

class CustomerVehicleHistoryPresenter @Inject constructor(private var view: CustomerVehicleHistoryContract.View, private val dataRepo: DearODataRepository) : CustomerVehicleHistoryContract.Presenter {

    override fun getJobCardData(id: String) {
        view.showProgressIndicator()
        dataRepo.getCustomerVehicleHistory(id, object : DataSource.OnResponseCallback<CustomerVehicleDetails> {
            override fun onSuccess(obj: CustomerVehicleDetails) {
                view.displayJCList(obj.history)
                view.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper) {
                view.dismissProgressIndicator()
                view.showGenericError(error.errorMessage)
            }
        })
    }

    override fun getJobCard(id: String) {
        view.showProgressIndicator()
        dataRepo.getJobCardById(id, null, object : DataSource.OnResponseCallback<JobCard> {
            override fun onSuccess(obj: JobCard) {
                view.moveToNextScreen(obj)
                view.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper) {
                view.dismissProgressIndicator()
                view.showGenericError(error.errorMessage)
            }
        })
    }

    override fun saveServiceReminderDate(vehicleId: String, date: String) {
        dataRepo.saveServiceReminder(vehicleId, date, object : DataSource.OnResponseCallback<ServiceDate> {
            override fun onSuccess(obj: ServiceDate) {
                Timber.d("Updated")
            }

            override fun onError(error: ErrorWrapper) {
            }
        })
    }


    override fun start() {
    }

    override fun detachView() {
    }
}