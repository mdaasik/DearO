package com.carworkz.dearo.cronjobs.userconfig

import android.app.job.JobParameters
import android.app.job.JobService
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.events.ToggleMenuItemsEvent
import timber.log.Timber
import javax.inject.Inject

class UserConfigService : JobService(), UserConfigContract.View {

    private var params: JobParameters? = null

    @Inject
    lateinit var presenter: UserConfigPresenter

    override fun onStopJob(job: JobParameters?): Boolean = true // true if reschedule on system failure.

    override fun onStartJob(job: JobParameters?): Boolean {
        (application as DearOApplication)
                .repositoryComponent
                .COMPONENT(UserConfigPresenterModule(this))
                .inject(this)
        params = job
        presenter.getGstStatus()
        return true // indicates background processing is done on different thread.
    }

    override fun showProgressIndicator() {
    }

    override fun dismissProgressIndicator() {
    }

    override fun onJobDone() {
        EventsManager.post(ToggleMenuItemsEvent())
        params?.let {
            jobFinished(it, false)
        }
    }

    override fun onJobFailure() {
        params?.let {
            jobFinished(it, true)
        }
    }

    override fun showGenericError(errorMsg: String) {
        Timber.e(errorMsg)
    }
}
