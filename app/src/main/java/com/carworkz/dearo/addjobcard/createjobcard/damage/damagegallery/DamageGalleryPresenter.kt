package com.carworkz.dearo.addjobcard.createjobcard.damage.damagegallery

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.data.Result
import com.carworkz.dearo.domain.entities.FileObject
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DamageGalleryPresenter @Inject constructor(private val dataRepo: DearODataRepository, private var view: DamageGalleryContract.View?) : DamageGalleryContract.Presenter,
    CoroutineScope {

    private val parentJob = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + parentJob

    override fun getGalleryImages(jobCardId: String, sort: String) {
        view?.showProgressIndicator()

        dataRepo.getDamages(jobCardId, sort, object : DataSource.OnResponseCallback<List<FileObject>> {
            override fun onSuccess(obj: List<FileObject>) {
                view?.displayGalleryImages(obj as ArrayList<FileObject>)
                view?.dismissProgressIndicator()
            }

            override fun onError(error: ErrorWrapper) {
            }
        })
    }

    override fun getPdcData(jobId: String) {
        Timber.d("damage presenter ")
        launch {
            view?.showProgressIndicator()
            withContext(Dispatchers.IO) {
                val result = dataRepo.getPdcImages(jobId, null)
                withContext(Dispatchers.Main) {
                    if (result is Result.Success) {
                        view?.displayGalleryImages(result.data as ArrayList<FileObject>)
                        view?.dismissProgressIndicator()
                    } else {
                        view?.dismissProgressIndicator()
                        view?.showGenericError((result as Result.Error).errorWrapper.errorMessage)
                    }
                }
            }
        }}

    override fun start() {
    }

    override fun detachView() {
        view = null
    }
}