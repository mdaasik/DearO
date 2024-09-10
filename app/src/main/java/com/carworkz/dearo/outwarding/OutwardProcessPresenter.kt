package com.carworkz.dearo.outwarding

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.data.Result
import com.carworkz.dearo.domain.entities.*
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class OutwardProcessPresenter @Inject constructor(private var view: OutwardingProcessContract.View?, private val dataRepository: DearODataRepository) : OutwardingProcessContract.Presenter, CoroutineScope
{

    private val parentJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main

    override fun updateProformaInvoice(invoiceId: String, invoice: Invoice, isPreview: Boolean)
    {
        view?.showProgressIndicator()
        dataRepository.updateProforma(invoiceId, invoice, object : DataSource.OnResponseCallback<Invoice>
        {
            override fun onSuccess(obj: Invoice)
            {
                view?.dismissProgressIndicator()
                view?.onSaveSuccess(obj, false)
                if (isPreview)
                {
                    view?.moveToNextScreen(isPreview)
                }
            }

            override fun onError(error: ErrorWrapper)
            {
                view?.dismissProgressIndicator()
                error.errorMessages?.run {
                    val errorMessageBuilder = StringBuilder(values.size)
                    values.forEach { valuesArray ->
                        valuesArray.forEach {
                            errorMessageBuilder.appendln("\u2022 ".plus(it))
                        }
                    }
                    view?.showGenericError(errorMessageBuilder.toString())
                } ?: view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun getVehicleAmcById(id: String?)
    {
        view?.showProgressIndicator()
        dataRepository.getVehicleAMCById(id, object : DataSource.OnResponseCallback<List<AMC>>
        {
            override fun onSuccess(obj: List<AMC>)
            {
                view?.dismissProgressIndicator()
                view?.showAmcDetails(obj)
            }

            override fun onError(error: ErrorWrapper)
            {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }


        })
    }

    override fun getVendorList() {
        view?.showProgressIndicator()
        dataRepository.getVendorList( object : DataSource.OnResponseCallback<List<Vendor>>
        {
            override fun onSuccess(obj: List<Vendor>)
            {
                view?.dismissProgressIndicator()
                view?.onFetchVendors(obj)
            }

            override fun onError(error: ErrorWrapper)
            {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun getPartNumber(jobCardId: String, partId: String?, partName: String, brandId: String?, showStock: Boolean, vehicleType: String?, packageId: String?)
    {
        view?.showProgressIndicator()
        //changing VEHICLE_STRICT to VEHICLE_FLEXIBLE
        val filterMode= if(packageId.isNullOrEmpty()) VEHICLE_STRICT else VEHICLE_FLEXIBLE
        dataRepository.searchPartNumber(partName, partId, jobCardId, null, null, null, null, showStock, vehicleType,filterMode, packageId, object : DataSource.OnResponseCallback<List<PartNumber>>
        {
            override fun onSuccess(obj: List<PartNumber>)
            {
                view?.dismissProgressIndicator()
                view?.showPartSelection(jobCardId, partId, partName, brandId, obj)
            }

            override fun onError(error: ErrorWrapper)
            {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun getProformaInvoice(invoiceId: String)
    {
        view?.showProgressIndicator()
        dataRepository.getInvoiceDetailsById(invoiceId, object : DataSource.OnResponseCallback<Invoice>
        {
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
            override fun onSuccess(invoiceObj: Invoice)
            {
                dataRepository.getHSN(object : DataSource.OnResponseCallback<List<HSN>>
                {
                    override fun onSuccess(obj: List<HSN>)
                    {
                        view?.dismissProgressIndicator()
                        view?.displayProforma(invoiceObj, obj)
                    }

                    override fun onError(error: ErrorWrapper)
                    {
                        view?.showGenericError(errorMsg = error.errorMessage)
                    }
                })
            }

            override fun onError(error: ErrorWrapper)
            {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun getHsn()
    {
        dataRepository.getHSN(object : DataSource.OnResponseCallback<List<HSN>>
        {
            override fun onSuccess(obj: List<HSN>)
            {
            }

            override fun onError(error: ErrorWrapper)
            {
            }
        })
    }

    override fun getEstimation(jobCardId: String)
    {
        view?.showProgressIndicator()
        dataRepository.getJobCardDetails(jobCardId, arrayOf("vehicle"), object : DataSource.OnResponseCallback<JobCard>
        {
            override fun onSuccess(obj: JobCard)
            {
                view?.dismissProgressIndicator()
                Timber.d("cost estimation " + obj.costEstimate)
                view?.displayJobEstimation(obj)
            }

            override fun onError(error: ErrorWrapper)
            {
                view?.dismissProgressIndicator()
            }
        })
    }

    override fun saveEstimation(
        jobCardId: String,
        obj: CostEstimate,
        showPdf: Boolean,
        showCustomerApproval: Boolean
    )
    {
        view?.showProgressIndicator()
        dataRepository.saveCostEstimation(jobCardId, obj, object : DataSource.OnResponseCallback<JobCard>
        {
            override fun onSuccess(obj: JobCard)
            {
                view?.dismissProgressIndicator()
                if (showPdf)
                {
                    view?.showPrintEstimate()
                }
                else
                {
                    view?.onSaveSuccess(null,showCustomerApproval)
                }
            }

            override fun onError(error: ErrorWrapper)
            {
                view?.dismissProgressIndicator()
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun removeThirdParty(invoiceId: String, thirdParty: ThirdParty)
    {
        launch {
            view?.showProgressIndicator()
            val removeJob = async {
                dataRepository.saveThirdPartyDetails(invoiceId, thirdParty.apply {
                    isThirdParty = false
                })
            }
            val result = removeJob.await()
            view?.dismissProgressIndicator()
            when (result)
            {
                is Result.Success ->
                {
                    view?.onThirdPartyRemoveSuccess()
                }

                is Result.Error   ->
                {
                    view?.showGenericError(result.errorWrapper.errorMessage)
                }
            }
        }
    }

    override fun start()
    {
    }

    override fun detachView()
    {
        view = null
        parentJob.cancel()
    }
}