package com.carworkz.dearo.cronjobs.imageupload

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.data.Result
import com.carworkz.dearo.domain.entities.FileObject
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ImageUploadPresenter @Inject constructor(var view: ImageUploadContract.View?, val dataRepository: DearODataRepository) : ImageUploadContract.Presenter, CoroutineScope {

    private val parentJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main

    override fun syncImages() {
        dataRepository.fetchUnSavedImages(object : DataSource.OnResponseCallback<List<FileObject>> {
            override fun onSuccess(obj: List<FileObject>) {
                launch {
                    val job = async {
                        var allUploadedSuccessfully = true
                        for (image in obj) {
                            val result = if (image.type == FileObject.FILE_TYPE_DAMAGE)
                                dataRepository.saveDamageImage(image.jobCardID!!, image)
                            else
                                dataRepository.uploadDocument(image)
                            if (result is Result.Error) {
                                allUploadedSuccessfully = false
                                break
                            }
                        }
                        allUploadedSuccessfully
                    }
                    if (job.await()) {
                        view?.onUploadingSuccessfully()
                    } else {
                        view?.onUploadingFailure()
                    }
                }
            }

            override fun onError(error: ErrorWrapper) {
                view?.onUploadingFailure()
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