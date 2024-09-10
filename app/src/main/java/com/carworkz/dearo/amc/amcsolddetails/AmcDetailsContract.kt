package com.carworkz.dearo.amc.amcsolddetails

import com.carworkz.dearo.amc.AMCPurchase
import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.AMC
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.domain.entities.SoldAMCDetails

interface AmcDetailsContract
{
    interface View : BaseView<Presenter>
    {
        fun showDetails(amcDetails: SoldAMCDetails)

        fun moveToNextScreen(obj: JobCard)

        fun serviceDateError()

        fun showSuccess(obj: AMC)
    }

    interface Presenter : BasePresenter
    {
        fun purchaseAmc(amcDetails: AMCPurchase)
        fun getRedemptionDetails(vehicleAmcId: String)
        fun getJobCard(id: String)
    }
}