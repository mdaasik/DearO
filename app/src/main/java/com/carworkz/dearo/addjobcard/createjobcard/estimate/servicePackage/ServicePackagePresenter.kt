package com.carworkz.dearo.addjobcard.createjobcard.estimate.servicePackage

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.Packages
import com.carworkz.dearo.domain.entities.ServicePackage
import javax.inject.Inject

class ServicePackagePresenter @Inject constructor(var view: ServicePackageContract.View?, val dataRepo: DearODataRepository) : ServicePackageContract.Presenter {

    override fun filterPackages(filterType: String) {
        view?.displayFilteredPackages(filterType)
    }

    override fun getPackages(jobCardId: String) {
        view?.showProgressIndicator()
        dataRepo.getPackages(jobCardId, object : DataSource.OnResponseCallback<Packages> {
            override fun onSuccess(obj: Packages) {
                view?.dismissProgressIndicator()
                if (obj.list?.isNotEmpty() == true) {
                    view?.displayPackages(obj.list)
                } else {
                    view?.displayPackageError()
                }
                view?.displayFilter(obj.filter?.category)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun updatePackages(jobCardId: String, invoiceId: String, packageIds: List<String>, isInvoice: Boolean) {
        view?.showProgressIndicator()
        dataRepo.updatePackages(jobCardId, invoiceId, isInvoice, packageIds, object : DataSource.OnResponseCallback<List<ServicePackage>> {
            override fun onSuccess(obj: List<ServicePackage>) {
                view?.dismissProgressIndicator()
                view?.moveToNextScreen(obj)
            }
            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun start() {
    }

    override fun detachView() {
        view = null
    }
}