package com.carworkz.dearo.addjobcard.createjobcard.accidental

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.injection.FragmentScoped
import com.carworkz.dearo.utils.Constants
import javax.inject.Inject

@FragmentScoped
class AccidentalPresenter @Inject constructor(var view: AccidentalContract.View?, private val repository: DearODataRepository) : AccidentalContract.Presenter {

    override fun getJobCardById(jobCardId: String) {
        view?.showProgressIndicator()
        val includeList = ArrayList<String>()
        includeList.add("vehicle")
        repository.getJobCardById(jobCardId, includeList, object : DataSource.OnResponseCallback<JobCard> {
            override fun onSuccess(obj: JobCard) {
                getCompanyList(obj)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        }
        )
    }

    override fun saveData(jobCardId: String, accidental: Accidental) {
        view?.showProgressIndicator()
        repository.saveAccidentalData(jobCardId, accidental, object : DataSource.OnResponseCallback<JobCard> {
            override fun onSuccess(obj: JobCard) {
                view?.dismissProgressIndicator()
                view?.moveToNextScreen()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                if (error.errorMessages != null && error.errorMessages.isNotEmpty()) {
                    error.errorMessages.forEach {
                        when (it.key) {
                            Constants.ApiConstants.KEY_INSURANCE_ERROR -> {
                                if (it.value.joinToString(",").contains(Constants.ApiConstants.KEY_EXPIRY_ERROR)) {
                                    view?.insuranceError(it.value.joinToString(","))
                                } else {
                                    view?.showGenericError(it.value.joinToString(","))
                                }
                            }
                        }
                    }
                } else {
                    view?.showGenericError(error.errorMessage)
                }
            }
        })
    }

    override fun getCityByPincode(pincode: Int) {
        repository.pinCodeCity(pincode, object : DataSource.OnResponseCallback<Pincode> {
            override fun onSuccess(obj: Pincode) {
                view?.displayCity(obj.city?.city ?: "")
            }

            override fun onError(error: ErrorWrapper) {
                view?.showPinCodeError("Invalid Pincode")
            }
        })
    }

    override fun getCompanyList(jobCard: JobCard) {
        repository.getCompanyNames(object : DataSource.OnResponseCallback<List<InsuranceCompany>> {
            override fun onSuccess(obj: List<InsuranceCompany>) {
                view?.dismissProgressIndicator()
                view?.displayJobCardData(jobCard, obj)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun getDocuments(jobCardID: String) {
        repository.getDocuments(jobCardID, object : DataSource.OnResponseCallback<List<FileObject>> {
            override fun onSuccess(obj: List<FileObject>) {
                view?.displayDocuments(obj)
            }

            override fun onError(error: ErrorWrapper) {
                if (error.errorMessages != null || error.errorMessages.isNotEmpty()) {
                    error.errorMessages.forEach {
                        when (it.key) {
                            Constants.ApiConstants.KEY_INSURANCE_ERROR -> {
                                it.value.forEach { value ->
                                    if (value.contains(Constants.ApiConstants.KEY_INSURANCE_EXPIRY)) {
                                        view?.insuranceError(value)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    view?.showGenericError(error.errorMessage)
                }
            }
        })
    }

    override fun deleteDocument(fileObject: FileObject) {
        // val jobCardID = fileObject.jobCardID
        repository.deleteDocument(fileObject, object : DataSource.OnResponseCallback<FileObject> {
            override fun onSuccess(obj: FileObject) {
                view?.updateDocView()
            }

            override fun onError(error: ErrorWrapper) {
            }
        })
    }

    override fun clearCache() {
        repository.clearAccidentalCache()
    }

    override fun start() {
    }

    override fun detachView() {
        view = null
    }
}