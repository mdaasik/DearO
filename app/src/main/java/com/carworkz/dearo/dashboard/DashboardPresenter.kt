package com.carworkz.dearo.dashboard

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.WorkshopResource
import com.carworkz.dearo.injection.FragmentScoped

import javax.inject.Inject

@FragmentScoped
class DashboardPresenter @Inject constructor(private var view: DashboardContract.View?, private val dataRepo: DearODataRepository) : DashboardContract.Presenter {

    override fun start() {
    }

    override fun detachView() {
        view = null
    }

    override fun getDashBoardDetails() {
        view?.showProgressIndicator()
        dataRepo.getDashBoardDetails(object : DataSource.OnResponseCallback<WorkshopResource> {
            override fun onSuccess(obj: WorkshopResource) {
                view?.showDashBoardData(obj)
                view?.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }
}
