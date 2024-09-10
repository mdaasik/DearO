package com.carworkz.dearo.addjobcard.createjobcard.jobs

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.JobAndVerbatim
import com.carworkz.dearo.domain.entities.Jobs
import com.carworkz.dearo.domain.entities.Remark

/**
 * Created by Farhan on 24/8/17.
 */
interface JobContract {

    interface View : BaseView<Presenter> {

        fun displayJobsData(jobAndVerbatim: JobAndVerbatim)

        fun displaySavedJobs(jobs: Jobs)

        fun moveToNextScreen()

        fun showError(message: String?)

        fun displayRemarks(remarks: List<Remark>?)
    }

    interface Presenter : BasePresenter {

        fun getJobsAndVerbatim(jobCardId: String)

        fun getSavedJobs(jobCardId: String)

        fun saveJobsData(jobCardId: String, obj: Jobs)
    }
}