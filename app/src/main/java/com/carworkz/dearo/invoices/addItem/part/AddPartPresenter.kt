package com.carworkz.dearo.invoices.addItem.part

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.injection.ActivityScoped
import com.carworkz.dearo.utils.Constants.ApiConstants
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kush on 13/9/17.
 */
@ActivityScoped
class AddPartPresenter @Inject constructor(var view: AddPartContract.View?, private val dearODataRepository: DearODataRepository) : AddPartContract.Presenter {

    override fun savePart(id: String, part: Part) {
        view?.showProgressIndicator()
        dearODataRepository.savePart(id, part, object : DataSource.OnResponseCallback<Part> {
            override fun onSuccess(obj: Part) {
                view?.dismissProgressIndicator()
                view?.onPartSavedSuccess(obj)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                if (error.errorMessages != null) {
                    error.errorMessages.forEach {
                        when (it.key) {
                            ApiConstants.KEY_PART_PARTNUMBER -> view?.partNumberError(it.value.joinToString(","))
                            ApiConstants.KEY_PART_BRANDNAME -> view?.brandError(it.value.joinToString(","))
                            else -> view?.showGenericError(error.errorMessage)
                        }
                    }
                } else {
                    view?.showGenericError(error.errorMessage)
                }
            }
        })
    }

    override fun deletePart(invoiceId: String, partId: String) {
        dearODataRepository.deletePart(invoiceId, partId, object : DataSource.OnResponseCallback<Invoice> {
            override fun onSuccess(obj: Invoice) {
                view?.onPartDeleted()
            }

            override fun onError(error: ErrorWrapper) {
            }
        })
    }

    override fun getHSN() {
        view?.showProgressIndicator()
        dearODataRepository.getHSN(object : DataSource.OnResponseCallback<List<HSN>> {
            override fun onSuccess(obj: List<HSN>) {
                view?.dismissProgressIndicator()
                view?.displayHsnList(obj.toMutableList())
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError(errorMsg = error.errorMessage)
            }
        })
    }

    override fun fetchBrandName(query: String, jobCardId: String, partNumber: String?, vehicleType: String?) {
        dearODataRepository.fetchBrandName(query, jobCardId, partNumber, vehicleType, object : DataSource.OnResponseCallback<List<BrandName>> {
            override fun onSuccess(obj: List<BrandName>) {
                view?.displayBrandNames(obj)
            }

            override fun onError(error: ErrorWrapper) {
                view?.brandError(error.errorMessage)
            }
        })
    }

    override fun fetchPartNumber(query: String, partId: String?, jobCardId: String, brandId: String?, vehicleType: String?) {
        Timber.d("Part Number Presenter$query")
        dearODataRepository.fetchPartNumber(query, partId, jobCardId, brandId, vehicleType, object : DataSource.OnResponseCallback<List<PartNumber>> {
            override fun onSuccess(obj: List<PartNumber>) {
                view?.displayPartNumbers(obj)
                Timber.d("Presenter PartNumber", obj.map { it.partNumber })
            }

            override fun onError(error: ErrorWrapper) {
                Timber.e(error.errorMessage)
                view?.partNumberError(error.errorMessage)
            }
        })
    }

    override fun start() {
    }

    override fun detachView() {
        view = null
    }
}