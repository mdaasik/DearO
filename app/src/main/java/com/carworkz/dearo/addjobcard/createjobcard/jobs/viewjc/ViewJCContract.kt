package com.carworkz.dearo.addjobcard.createjobcard.jobs.viewjc

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.Jobs
import com.carworkz.dearo.domain.entities.ServicePackage

/**
 * Created by kush on 19/9/17.
 */
interface ViewJCContract {
    interface View : BaseView<Presenter> {
        fun displayJobsData(obj: Jobs)

        fun displayPackages(packages: List<ServicePackage>)

        fun moveToNextScreen()

        fun showError(message: String?)

        fun displayRemarks(remarks: Array<out String>?)
    }

    interface Presenter : BasePresenter {

        fun getJobsData(jobCardId: String)

        fun saveJobsData(jobCardId: String, obj: Jobs)
    }
}