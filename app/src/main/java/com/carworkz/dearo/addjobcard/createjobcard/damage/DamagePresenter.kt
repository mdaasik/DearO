package com.carworkz.dearo.addjobcard.createjobcard.damage

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.data.Result
import com.carworkz.dearo.domain.entities.FileObject
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * Created by kush on 23/8/17.
 */

class DamagePresenter @Inject constructor(private val dataRepository: DearODataRepository, private var view: DamageContract.View?) : DamageContract.Presenter, CoroutineScope {

    private val parentJob = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + parentJob

    override fun getGalleryData(jobId: String) {
        Timber.d("damage presenter ")
        launch {
            view?.showProgressIndicator()
            withContext(Dispatchers.IO) {
                val result = dataRepository.getDamages(jobId, null)
                withContext(Dispatchers.Main) {
                    if (result is Result.Success) {
                        view?.displayDamages(result.data)
                        view?.dismissProgressIndicator()
                    } else {
                        view?.dismissProgressIndicator()
                        view?.showGenericError((result as Result.Error).errorWrapper.errorMessage)
                    }
                }
            }
        }

//        dataRepository.getDamages(jobId, null, object : DataSource.OnResponseCallback<List<FileObject>> {
//            override fun onSuccess(obj: List<FileObject>) {
//                view?.dismissProgressIndicator()
//                view?.displayDamages(obj)
//            }
//
//            override fun onError(error: ErrorWrapper) {
//                view?.dismissProgressIndicator()
//                view?.showGenericError(error.errorMessage)
//            }
//        })
    }

    override fun getPdcData(jobId: String) {
        Timber.d("damage presenter ")
        launch {
            view?.showProgressIndicator()
            withContext(Dispatchers.IO) {
                val result = dataRepository.getPdcImages(jobId, null)
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
    }}

    override fun deleteDamage(fileObject: FileObject) {
        view?.showProgressIndicator()
        dataRepository.deleteDamageImage(fileObject, object : DataSource.OnResponseCallback<FileObject?> {
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

    override fun start() {
    }

    override fun detachView() {
        dataRepository.clearDamageCache()
        view = null
        parentJob.cancel()
    }

    override fun editCaption(Image_id: String, caption: String, jobId: String) {
        view?.showProgressIndicator()
        dataRepository.updateCaption(caption, jobId, Image_id, object : DataSource.OnResponseCallback<FileObject> {
            override fun onSuccess(obj: FileObject) {
                view?.updateAlert()
                view?.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
            }
        })
    }
}
