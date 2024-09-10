package com.carworkz.dearo.amc

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.*
import org.json.JSONObject


interface AMCContract {

    interface View : BaseView<Presenter> {
        fun updateAmcPackages(packages: List<AMCPackage>)

        fun errorToast(error: String)

        fun showSuccess(obj:AMC)

        fun moveToNextScreen(obj: JobCard)

    }

    interface Presenter : BasePresenter {

        fun suggestAmcPackages(vehicleId: String)
        
        fun purchaseAmc(amcDetails: AMCPurchase)

//        fun initiateJobCard(customerId: String, vehicleId: String, appointmentId: String?, type: String)

//        fun getJobCardData(id: String)

//        fun getJobCard(id: String)
    }
}
