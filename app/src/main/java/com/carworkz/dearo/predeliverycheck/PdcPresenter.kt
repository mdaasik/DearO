package com.carworkz.dearo.predeliverycheck

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.data.Result
import com.carworkz.dearo.domain.entities.*
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class PdcPresenter @Inject constructor(private var view: PdcContract.View?, private val repository: DearODataRepository) : PdcContract.Presenter, CoroutineScope {

    private val parentJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main

    override fun getEstimation(jobCardId: String)
    {
        view?.showProgressIndicator()
        repository.getJobCardDetails(jobCardId, arrayOf("vehicle"), object : DataSource.OnResponseCallback<JobCard>
        {
            override fun onSuccess(obj: JobCard)
            {
                view?.dismissProgressIndicator()
                Timber.d("cost estimation " + obj.costEstimate)
                view?.onFetchJobCard(obj)
            }

            override fun onError(error: ErrorWrapper)
            {
                view?.dismissProgressIndicator()
            }
        })
    }

    override fun getChecklist(jobCardId: String) {
        view?.showProgressIndicator()
        repository.getPdcChecklist(jobCardId,object : DataSource.OnResponseCallback<List<PdcBase>>
        {
            override fun onSuccess(obj: List<PdcBase>) {
                view?.dismissProgressIndicator()
                view?.onFetchChecklist(obj)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError("Cannot Fetch checklist,Please try again!")
            }

        })
    }

    override fun saveChecklist(jobCardId: String, pdcEntity: PdcEntity, print: Boolean) {
        view?.showProgressIndicator()
        repository.postPdcChecklist(jobCardId,pdcEntity,object : DataSource.OnResponseCallback<NetworkPostResponse>
        {
            override fun onSuccess(obj: NetworkPostResponse) {
                view?.dismissProgressIndicator()
                view?.onSaveSuccess(print)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                view?.showGenericError("Cannot save checklist,Please try again!")
            }

        })

    }

//        override fun getGalleryData(jobId: String) {
//            Timber.d("damage presenter ")
//            launch {
//                view?.showProgressIndicator()
//                withContext(Dispatchers.IO) {
//                    val result = repository.getDamages(jobId, null)
//                    withContext(Dispatchers.Main) {
//                        if (result is Result.Success) {
//                            view?.displayPdcImages(result.data)
//                            view?.dismissProgressIndicator()
//                        } else {
//                            view?.dismissProgressIndicator()
//                            view?.showGenericError((result as Result.Error).errorWrapper.errorMessage)
//                        }
//                    }
//                }
//            }
//        }

    override fun getPdcData(jobId: String) {
        Timber.d("damage presenter ")
        launch {
            view?.showProgressIndicator()
            withContext(Dispatchers.IO) {
                val result = repository.getPdcImages(jobId, null)
                withContext(Dispatchers.Main) {
                    if (result is Result.Success) {
                        view?.displayPdcImages(result.data)
                        view?.dismissProgressIndicator()
                    } else {
                        view?.dismissProgressIndicator()
                        view?.showGenericError((result as Result.Error).errorWrapper.errorMessage)
                    }
                }
            }
        }
    }


    override fun deleteDamage(fileObject: FileObject) {
        view?.showProgressIndicator()
        repository.deleteDamageImage(fileObject, object : DataSource.OnResponseCallback<FileObject?> {
            override fun onSuccess(obj: FileObject?) {
                Timber.d("Deleted obj $obj")
                view?.dismissProgressIndicator()
                view?.updateAlert()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
            }
        })
    }

    override fun editCaption(Image_id: String, caption: String, jobId: String) {
        view?.showProgressIndicator()
        repository.updateCaption(caption, jobId, Image_id, object : DataSource.OnResponseCallback<FileObject> {
            override fun onSuccess(obj: FileObject) {
                view?.updateAlert()
                view?.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
            }
        })
    }

    override fun start() {
    }

    override fun detachView() {
        view = null
        parentJob.cancel()
    }
}