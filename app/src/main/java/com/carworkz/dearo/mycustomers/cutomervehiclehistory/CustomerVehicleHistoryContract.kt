package com.carworkz.dearo.mycustomers.cutomervehiclehistory

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.History
import com.carworkz.dearo.domain.entities.JobCard

interface CustomerVehicleHistoryContract {
    interface View : BaseView<Presenter> {
        fun displayJCList(list: List<History>?)

        fun moveToNextScreen(obj: JobCard)

        fun serviceDateError()
    }

    interface Presenter : BasePresenter {
        fun getJobCardData(id: String)

        fun getJobCard(id: String)

        fun saveServiceReminderDate(vehicleId: String, date: String)
    }
}