package com.carworkz.dearo.addjobcard.createjobcard.damage.capturedamage

import android.annotation.TargetApi
import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.domain.entities.FileObject
import timber.log.Timber
import javax.inject.Inject

class UploadService : IntentService("UploadService"), CaptureImageContract.View {

    @Inject
    lateinit var presenter: CaptureImagePresenter
    private lateinit var file: FileObject

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            super.startForeground(1, getNotification())
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            super.startForeground(1, getNotification())
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onHandleIntent(intent: Intent?) {
        (applicationContext as DearOApplication)
                .repositoryComponent
                .COMPONENT(ClickPicturePresenterModule(this))
                .inject(this)

        if (intent?.extras != null) {
            when (intent.action) {
                ACTION_DAMAGE -> {
                    file = intent.extras?.get(ARG_FILE) as FileObject
                    presenter.saveDamageImage(file)
                }
                ACTION_ACCIDENTAL -> {
                    file = intent.extras!!.get(ARG_FILE) as FileObject
                    presenter.saveDocument(file)
                } ACTION_PDC -> {
                    file = intent.extras!!.get(ARG_FILE) as FileObject
                    presenter.savePDCImage(file)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun showProgressIndicator() {
    }

    override fun dismissProgressIndicator() {
    }

    override fun showGenericError(errorMsg: String) {
        Timber.e(errorMsg)
    }

    override fun onDamageUploadFinish() {
        stopSelf()
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun getNotification(): Notification {

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = getString(R.string.default_notification_channel_id)
        val channel = NotificationChannel(channelId,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        // Build your notification
        val mBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.dearo_noti)
                .setContentTitle("Uploading Image")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Set the Channel ID for Android O. DON'T FORGET
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder.setChannelId(channelId)
        }
        return mBuilder.build()
    }

    companion object {
        const val ARG_FILE = "file"
        const val ACTION_DAMAGE = "damage"
        const val ACTION_ACCIDENTAL = "accidental"
        const val ACTION_PDC = "pdc"
    }
}
