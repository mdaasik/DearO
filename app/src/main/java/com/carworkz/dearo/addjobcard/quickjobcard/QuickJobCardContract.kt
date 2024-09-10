package com.carworkz.dearo.addjobcard.quickjobcard

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.JobAndVerbatim
import com.carworkz.dearo.domain.entities.JobCard

interface QuickJobCardContract {

    interface View : BaseView<Presenter> {
        fun displayJobCard(jobCard: JobCard)

        fun displayWithJobAndVerbatim(jobAndVerbatim: JobAndVerbatim)

        fun displayData(jobCard: JobCard, jobAndVerbatim: JobAndVerbatim)

        fun showAccidentalError(error: String?)

        fun moveToNextScreen(statusChange: Boolean)

        fun startScreen(screenToStart: String, jobCard: JobCard)

        fun launchWhatsapp(contactNumbe: String, message: String)
    }

    interface Presenter : BasePresenter {
        fun getJobCardById(jobCardId: String)

        fun getJobs(jobCardId: String)

        fun getData(jobCardId: String)

        fun saveJobCard(jobCardId: String, jobCard: JobCard, screenToStart: String?)

        fun completeJobCard(jobCardId: String, jobCard: JobCard, notify: Boolean)
    }
}