package com.carworkz.dearo.cronjobs.imageupload

import android.app.job.JobParameters
import android.app.job.JobService
import com.carworkz.dearo.DearOApplication
import javax.inject.Inject

class ImageUploadJob : JobService(), ImageUploadContract.View {

    @Inject
    lateinit var presenter: ImageUploadPresenter

    private var params: JobParameters? = null

    override fun onStopJob(params: JobParameters?): Boolean = true // true to reschedule on system failure.

    override fun onStartJob(params: JobParameters?): Boolean {
        initComponent()
        this.params = params
        presenter.syncImages()
        return true // indicates background processing is done on different thread.
    }

    override fun onUploadingSuccessfully() {
        params?.let {
            jobFinished(it, false)
        }
    }

    override fun onUploadingFailure() {
        params?.let {
            jobFinished(it, true)
        }
    }

    override fun showProgressIndicator() = Unit

    override fun dismissProgressIndicator() = Unit

    override fun showGenericError(errorMsg: String) = Unit

    private fun initComponent() {
        (application as DearOApplication)
                .repositoryComponent
                .COMPONENT(ImageUploadPresenterModule(this))
                .inject(this)
    }
}