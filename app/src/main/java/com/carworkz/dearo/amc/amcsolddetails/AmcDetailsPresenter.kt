package com.carworkz.dearo.amc.amcsolddetails

import com.carworkz.dearo.amc.AMCPurchase
import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.AMC
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.domain.entities.SoldAMCDetails
import javax.inject.Inject

class AmcDetailsPresenter @Inject constructor(private var view: AmcDetailsContract.View, private val dataRepo: DearODataRepository) : AmcDetailsContract.Presenter
{

    override fun purchaseAmc(amcDetails: AMCPurchase)
    {
        view?.showProgressIndicator()
        dataRepo.purchaseAMC(amcDetails,object : DataSource.OnResponseCallback<AMC>{
            override fun onSuccess(obj: AMC) {
                view?.dismissProgressIndicator()
                view?.showSuccess(obj)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun getRedemptionDetails(vehicleAmcId: String)
    {
        view.showProgressIndicator()
        dataRepo.getRedemptionDetails(vehicleAmcId, object : DataSource.OnResponseCallback<SoldAMCDetails>
        {
            override fun onSuccess(obj: SoldAMCDetails)
            {
                view.showDetails(obj)
                view.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper)
            {
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

    override fun start()
    {
    }

    override fun detachView()
    {

    }
}