package com.carworkz.dearo.othersyshistory

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.CustomerVehicleDetails
import com.carworkz.dearo.domain.entities.History
import com.carworkz.dearo.domain.entities.JobCard

interface OtherHistoryContract {
    interface View : BaseView<Presenter> {
        fun displayHistory(obj: CustomerVehicleDetails?)
    }

    interface Presenter : BasePresenter {
        fun getOtherSysHistory(registrationNumber: String,mobileNumber:String)
    }
}