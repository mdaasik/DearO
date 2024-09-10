package com.carworkz.dearo.notification.deviceregistration

import android.app.job.JobParameters
import android.app.job.JobService
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.utils.Constants
import javax.inject.Inject

class DeviceRegistrarJob : JobService(), DeviceRegistrarContract.View {

    private var jobParams: JobParameters? = null

    @Inject
    lateinit var presenter: DeviceRegistrarPresenter

    override fun onRegistrationSuccess() {
        jobParams?.let {
            jobFinished(it, false)
        }
    }

    override fun onRegistrationFailed() {
        jobParams?.let {
            jobFinished(it, true)
        }
    }

    override fun showProgressIndicator() = Unit

    override fun dismissProgressIndicator() = Unit

    override fun showGenericError(errorMsg: String) = Unit

    override fun onStopJob(params: JobParameters?): Boolean = true // true to reschedule on system failure.

    override fun onStartJob(params: JobParameters?): Boolean {
        jobParams = params
        initComponent()
        presenter.registerDevice(SharedPrefHelper.getWorkshopId(), SharedPrefHelper.getUserId(),
                SharedPrefHelper.getUserAccessToken(), SharedPrefHelper.getDeviceId(),
                Constants.DEVICE_MODEL, Constants.PLATFORM,
                Constants.DEVICE_VERSION, SharedPrefHelper.getFcmToken())
        return true
    }

    private fun initComponent() {
        (application as DearOApplication)
                .repositoryComponent
                .COMPONENT(DeviceRegistrarModule(this))
                .inject(this)
    }
}