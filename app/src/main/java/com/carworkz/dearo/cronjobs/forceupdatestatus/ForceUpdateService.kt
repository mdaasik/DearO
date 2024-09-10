package com.carworkz.dearo.cronjobs.forceupdatestatus

import android.app.job.JobParameters
import android.app.job.JobService
import com.carworkz.dearo.BuildConfig
import com.carworkz.dearo.DearOApplication
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by ambab on 8/11/17.
 */
class ForceUpdateService : JobService(), ForceUpdateContract.View {

    @Inject
    lateinit var presenter: ForceUpdatePresenter

    private var jobParams: JobParameters? = null

    override fun onStopJob(p0: JobParameters?): Boolean = true // true if reschedule on system failure.

    override fun onStartJob(p0: JobParameters?): Boolean {
        (application as DearOApplication)
                .repositoryComponent
                .COMPONENT(ForceUpdatePresenterModule(this))
                .inject(this)

        jobParams = p0
        presenter.checkForceUpdate("dearoapp", "android", BuildConfig.VERSION_CODE)
        return true // indicates background processing is done on different thread.
    }

    override fun showProgressIndicator() {
    }

    override fun dismissProgressIndicator() {
    }

    override fun onJobDone() {
        jobParams?.let {
            jobFinished(it, false)
        }
    }

    override fun onJobFailure() {
        jobParams?.let {
            jobFinished(it, true)
        }
    }

    override fun showGenericError(errorMsg: String) {
        Timber.e(errorMsg)
    }
}