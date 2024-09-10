package com.carworkz.dearo.addjobcard.createjobcard.jobs.viewjc

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.domain.entities.Jobs
import com.carworkz.dearo.domain.entities.NetworkPostResponse
import javax.inject.Inject

/**
 * Created by kush on 19/9/17.
 */
class ViewJCPresenter @Inject constructor(var view: ViewJCContract.View?, val dataRepository: DearODataRepository) : ViewJCContract.Presenter {
    override fun saveJobsData(jobCardId: String, obj: Jobs) {
        dataRepository.saveJobsData(jobCardId, obj, object : DataSource.OnResponseCallback<NetworkPostResponse> {
            override fun onSuccess(obj: NetworkPostResponse) {
                view?.dismissProgressIndicator()
                view?.moveToNextScreen()
            }

            override fun onError(error: ErrorWrapper) {
                view?.showGenericError(error.errorMessage)
                view?.dismissProgressIndicator()
            }
        })
    }

    override fun getJobsData(jobCardId: String) {
        view?.showProgressIndicator()
        dataRepository.getJobCardDetails(jobCardId, arrayOf("vehicle"), object : DataSource.OnResponseCallback<JobCard> {
            override fun onSuccess(obj: JobCard) {
                view?.displayJobsData(obj.jobs)
                view?.displayPackages(obj.packages)
                view?.dismissProgressIndicator()
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