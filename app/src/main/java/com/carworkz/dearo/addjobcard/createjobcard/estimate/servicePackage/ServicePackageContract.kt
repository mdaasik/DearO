package com.carworkz.dearo.addjobcard.createjobcard.estimate.servicePackage

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.Category
import com.carworkz.dearo.domain.entities.ServicePackage

interface ServicePackageContract {
    interface View : BaseView<Presenter> {

        fun displayPackages(packages: List<ServicePackage>?)

        fun displayFilter(filter: Category?)

        fun displayPackageError()

        fun displayFilteredPackages(filterType: String)

        fun moveToNextScreen(packageList: List<ServicePackage>)
    }

    interface Presenter : BasePresenter {

        fun filterPackages(filterType: String)

        fun getPackages(jobCardId: String)

        fun updatePackages(jobCardId: String, invoiceId: String, packageIds: List<String>, isInvoice: Boolean)
    }
}