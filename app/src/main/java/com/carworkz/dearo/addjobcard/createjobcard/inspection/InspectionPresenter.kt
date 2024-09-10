package com.carworkz.dearo.addjobcard.createjobcard.inspection

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.injection.FragmentScoped
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Farhan on 23/8/17.
 */
@FragmentScoped
class InspectionPresenter @Inject constructor(private var view: InspectionContract.View?, private val dataRepository: DearODataRepository, private val isViewOnly: Boolean) : InspectionContract.Presenter {

    override fun saveInspection(jobCardId: String, obj: InspectionPostPOJO) {
        dataRepository.saveInspection(jobCardId, obj, object : DataSource.OnResponseCallback<NetworkPostResponse> {
            override fun onSuccess(obj: NetworkPostResponse) {
                view?.moveToNextScreen()
                Timber.d("presenter success", "" + obj.message)
            }

            override fun onError(error: ErrorWrapper) {
                view?.showGenericError(error.errorMessage)
                Timber.d("presenter success", "" + error.errorMessage)
            }
        })
    }

    override fun start() {
    }

    override fun detachView() {
        view = null
    }

    override fun getInspectionGroups(vehicleType: String?) {
        view?.showProgressIndicator()
        dataRepository.getInspectionGroups(vehicleType, object : DataSource.OnResponseCallback<List<InspectionGroup>> {
            override fun onSuccess(obj: List<InspectionGroup>) {
                view?.dismissProgressIndicator()
                view?.displayInspectionGroups(obj)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(errorMsg = error.errorMessage)
            }
        })
    }

    override fun getInspectionItemByGroup(groupSlug: String) {
        view?.showProgressIndicator()
        dataRepository.getInspectionItemsByGroup(groupSlug, object : DataSource.OnResponseCallback<List<InspectionItem>> {
            override fun onSuccess(obj: List<InspectionItem>) {
                Timber.d("inspection", "" + obj.size)
                view?.displayInspectionItems(obj)
                view?.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(errorMsg = error.errorMessage)
            }
        })
    }

    override fun getSavedInspection(jobCardId: String) {
        view?.showProgressIndicator()
        dataRepository.getJobCardById(jobCardId, null, object : DataSource.OnResponseCallback<JobCard> {

            override fun onSuccess(obj: JobCard) {
                view?.dismissProgressIndicator()
                if (!isViewOnly)
                    view?.displaySelectedInspectionItems(obj.inspectionGroup, obj.inspection)
                else {
                    view?.displayViewOnly(obj.inspectionGroup, obj.inspection)
                }
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(errorMsg = error.errorMessage)
            }
        })
    }
}