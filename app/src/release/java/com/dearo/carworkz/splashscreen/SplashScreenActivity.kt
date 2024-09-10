package com.carworkz.dearo.splashscreen

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import com.carworkz.dearo.base.BaseActivity
import com.carworkz.dearo.BuildConfig
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.MainActivity
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.login.addmobilenumber.LoginActivity
import com.carworkz.dearo.notification.NotificationNavigator
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import timber.log.Timber
import java.util.*
import com.carworkz.dearo.extensions.*
import javax.inject.Inject

class SplashScreenActivity : BaseActivity(), ConfigContract.View {

    @Inject
    lateinit var presenter: ConfigPresenter

    @Inject
    lateinit var navigator: NotificationNavigator

    private var alert: DialogInterface? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_splashscreen)
        (application as DearOApplication).repositoryComponent
            .COMPONENT(ConfigPresenterModule(this))
            .inject(this)
        if (SharedPrefHelper.getForceUpdate() && SharedPrefHelper.getMinimumVersion() > BuildConfig.VERSION_CODE) {
            Timber.d("inside force update " + SharedPrefHelper.getForceUpdate())
            displayForceUpdateDialog()
            return
        } else {
            val check = Random().nextBoolean()
            if (SharedPrefHelper.getOptionalUpdate() && BuildConfig.VERSION_CODE < SharedPrefHelper.getVersionCode() && check) {
                displayOptionUpdate()
                return
            }
        }
        getConfigAndContinue()
    }

    override fun onDestroy() {
        super.onDestroy()
        alert?.dismiss()
        presenter.detachView()
    }

    override fun getProgressView(): View {
        return View(this)
    }

    override fun showProgressIndicator() {
    }

    override fun dismissProgressIndicator() {
    }

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg, { _, _ -> finish() })
    }

    override fun moveToNextScreen() {
        if (SharedPrefHelper.getForceUpdate() && SharedPrefHelper.getMinimumVersion() > BuildConfig.VERSION_CODE) {
            Timber.d("inside force update " + SharedPrefHelper.getForceUpdate())
            displayForceUpdateDialog()
            return
        } else {
            val check = Random().nextBoolean()
            if (SharedPrefHelper.getOptionalUpdate() && BuildConfig.VERSION_CODE < SharedPrefHelper.getVersionCode() && check) {
                displayOptionUpdate()
                return
            } else {
                // pass extras to check if its a notification.
                if (intent.extras != null && intent.extras?.get(NotificationNavigator.ARG_TASK) != null) {
                    navigator.navigate(intent.extras)
                } else {
                    if (SharedPrefHelper.getUserAccessToken().isEmpty()) {
                        startActivity<LoginActivity>()
                    } else {
                        presenter.getHsn()
                        presenter.getGstStatus()
                        startActivity<MainActivity>()
                    }
                }
                finish()
            }
        }
    }



    private fun getConfigAndContinue() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)
        when {
            resultCode == ConnectionResult.SUCCESS -> {
                presenter.checkForceUpdate("dearoapp", "android", BuildConfig.VERSION_CODE)
            }
            googleApiAvailability.isUserResolvableError(resultCode) -> googleApiAvailability.getErrorDialog(
                this,
                resultCode,
                REQUEST_CODE_PLAY_SERVICES
            )?.show()
            else -> {
                showPlayServicesNotAvailable()
                Timber.e("This device doesn't support Play Services")
            }
        }
        presenter.getHsn()
        presenter.getGstStatus()
    }

    private fun displayOptionUpdate() {
        alert("There is a Update Available", "Update") {
            positiveButton("Update") {
                val appPackageName = packageName // getPackageName() from Context or Activity object
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + appPackageName)
                        )
                    )
                } catch (anfe: android.content.ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)
                        )
                    )
                }
            }
            negativeButton("Skip") {
                getConfigAndContinue()
                it.dismiss()
            }
        }.show()
    }

    private fun displayForceUpdateDialog() {
        alert = alert(SharedPrefHelper.getForceUpdateText(), "Force Update") {
            yesButton {
                val appPackageName =
                    "com.carworkz.dearo" // getPackageName() from Context or Activity object
                try {
                    if (SharedPrefHelper.getForceUpdateLink().isEmpty()) {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=$appPackageName")
                            )
                        )
                    } else {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(SharedPrefHelper.getForceUpdateLink())
                            )
                        )
                    }
                } catch (anfe: android.content.ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                        )
                    )
                }
            }
            noButton {
                it.dismiss()
                finish()
            }
        }.show()
    }

    private fun showPlayServicesNotAvailable() {
        toast("no play services")
        finish()
    }

    companion object {

        const val NETWORK = "network"
        const val LOCAL = "local"
        const val MY_REQUEST_CODE = 1001
        private const val REQUEST_CODE_PLAY_SERVICES = 200
    }
}
