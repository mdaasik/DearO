package com.carworkz.dearo.pdf

import android.app.Activity
import android.app.DownloadManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.BuildConfig
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.estimate.digitalsignature.DigitalSignatureActivity
import com.carworkz.dearo.base.DialogFactory
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.databinding.ActivityMultiPdfBinding
import com.carworkz.dearo.domain.entities.PDF
import com.carworkz.dearo.interactionprovider.ToolBarImgInteractionProvider
import com.carworkz.dearo.partpayment.PartPaymentActivity
import com.carworkz.dearo.pdf.managers.StateManager
import com.carworkz.dearo.pdf.pdfView.FileInteractionProvider
import com.carworkz.dearo.pdf.pdfView.PdfViewFragment
import com.carworkz.dearo.screencontainer.ActionImgScreenContainer
import com.carworkz.dearo.serviceremainder.NewServiceReminderActivity
import com.carworkz.dearo.utils.DearOFileUtils
import com.google.android.material.tabs.TabLayout
import timber.log.Timber
import java.io.File
import java.util.Locale
/*import kotlinx.android.synthetic.main.activity_multi_pdf.*
import kotlinx.android.synthetic.main.base_layout.*
import kotlinx.android.synthetic.main.layout_extended_fab.**/


class MultiPdfActivity : ScreenContainerActivity(), ToolBarImgInteractionProvider,
    StateManagerActionContract, TabLayout.OnTabSelectedListener {
   private lateinit var binding: ActivityMultiPdfBinding

    private lateinit var stateManager: StateManager

    private val fragmentMap = mutableMapOf<TabLayout.Tab, PdfViewFragment>()

    private var isCreating = false

//    private val fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
//    private val fileDir = cacheDir.absolutePath

    lateinit var screenContainer: ActionImgScreenContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        stateManager = intent?.extras?.getParcelable(ARG_STATE_MANAGER)!!
        stateManager.setInteractionProvider(this)
        super.onCreate(savedInstanceState)
        stateManager.start()
        Timber.d("Starting Multi pdf")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SERVICE_REMAINDER -> {
                if (data != null) {
                    val pdfs =
                        data.getParcelableArrayListExtra<PDF>(NewServiceReminderActivity.ARG_PDF)
                    val reactionValue =
                        StateManager.ReactionValue(pdfs?.findLast { it.invoiceId != null }?.invoiceId)
                    stateManager.react(reactionValue)
                }
            }

            REQUEST_CODE_PART_PAYMENT -> {
                if (resultCode == Activity.RESULT_OK) {
                    stateManager.react<Any>(null)
                }
            }

            REQUEST_CODE_DIGITAL_SIGNATURE -> {
                if (resultCode == Activity.RESULT_OK) {
                    stateManager.react<Any>(null)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stateManager.detach()
    }

    override fun createScreenContainer(): ScreenContainer {
        screenContainer = ActionImgScreenContainer(this)
        return screenContainer
    }

    override fun getViewBinding(
        inflater: LayoutInflater?, container: ViewGroup?, attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityMultiPdfBinding.inflate(layoutInflater)
        return binding
    }





    // override fun getLayout(): Int = R.layout.activity_multi_pdf

    override fun getProgressView(): View = ProgressBar(this)

    override fun getToolBarTitle(): String = "Pdf File"

   /* override fun getToolBarTitle(): String = stateManager.getMainTitle(
        binding.tabLayout?.selectedTabPosition ?: -1
    )*/

    override fun onActionBtnClick() {
        //Share PDF
        val currentInvoiceFragment =
            fragmentMap[binding.tabLayout.getTabAt(binding.tabLayout.selectedTabPosition)]
        if ((currentInvoiceFragment as? FileInteractionProvider)?.isFileDownloading() == false) {
            currentInvoiceFragment.getPdfFile()?.run {

                val uri = FileProvider.getUriForFile(
                    this@MultiPdfActivity, "${BuildConfig.APPLICATION_ID}.fileprovider", this
                )
                if (uri != null) {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "application/pdf"
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    shareIntent.setDataAndType(uri, contentResolver.getType(uri))
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "PDF")

                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                    startActivity(
                        Intent.createChooser(
                            shareIntent, getString(R.string.share_pdf).uppercase(Locale.getDefault())
                        )
                    )
                }
            }
        } else {
            toast("Downloading file, please wait..")
        }
    }

    override fun onSecondaryActionBtnClick() {
        //Save PDF
        val currentInvoiceFragment =
            fragmentMap[binding.tabLayout.getTabAt(binding.tabLayout.selectedTabPosition)]

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //code for Q and above
            currentInvoiceFragment?.getPdfFile()?.let {

//            DearOFileUtils.saveInDownloads(this, it)


                val fileToExport = it
                currentInvoiceFragment.deleteFileOnDestory = false


                val contentDetails = ContentValues()
                contentDetails.put(MediaStore.MediaColumns.DISPLAY_NAME, it.name)
                contentDetails.put(
                    MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS
                )
                contentDetails.put(MediaStore.MediaColumns.IS_PENDING, 1)

                val contentUri = contentResolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentDetails
                )
                contentUri?.let { insertedContentUri ->
                    copyFileData(insertedContentUri, fileToExport)

                    contentDetails.clear()
                    contentDetails.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    contentResolver.update(insertedContentUri, contentDetails, null, null)

                }
                toast("PDF Downloaded")


                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notificationBuilder = NotificationCompat.Builder(
                    this, getString(R.string.default_notification_channel_id)
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    val channelId = getString(R.string.default_notification_channel_id)
                    val name = "Dearo"
                    // val Description = "This is my channel"
                    val importance = NotificationManager.IMPORTANCE_HIGH
                    val mChannel = NotificationChannel(channelId, name, importance)
                    // mChannel.description = Description;
                    mChannel.enableLights(true)
                    mChannel.lightColor = Color.RED
                    mChannel.enableVibration(true)
                    mChannel.setShowBadge(false)
                    notificationManager.createNotificationChannel(mChannel)
                }


                val type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(FILE_MIME_TYPE)

                val uri = FileProvider.getUriForFile(
                    this@MultiPdfActivity, "${BuildConfig.APPLICATION_ID}.fileprovider", it
                )

                val pdfOpenIntent = Intent(Intent.ACTION_VIEW)
                pdfOpenIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                pdfOpenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                pdfOpenIntent.setDataAndType(uri, type)
                notificationBuilder.setContentIntent(
                    PendingIntent.getActivity(
                        this, 0, pdfOpenIntent, FLAG_IMMUTABLE
                    )
                ).setSmallIcon(R.drawable.dearo_noti).setPriority(1)
                    .setDefaults(Notification.DEFAULT_LIGHTS and Notification.DEFAULT_SOUND and Notification.DEFAULT_VIBRATE)
                    .setContentText(it.nameWithoutExtension).setAutoCancel(true)

                notificationBuilder.setContentTitle("PDF Downloaded")
                notificationManager.notify(
                    it.nameWithoutExtension, it.hashCode(), notificationBuilder.build()
                )
            }
        } else {
            //code for below Q
            val uri = Uri.parse(currentInvoiceFragment?.getPdfFileUrl())
            val request = DownloadManager.Request(uri)
            val dir = File(Environment.DIRECTORY_DOWNLOADS, DearOFileUtils.FOLDER)
            val dirFlag = dir.mkdirs()
            request.setDestinationInExternalPublicDir(
                dir.absolutePath,
                currentInvoiceFragment?.getPdfFile()?.name
            )
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // to notify when download is complete
            val manager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
            toast("PDF Downloaded")
        }
    }

    private fun copyFileData(destinationContentURI: Uri, fileToExport: File) {
        contentResolver.openFileDescriptor(destinationContentURI, "w").use { parcelFileDescriptor ->
            ParcelFileDescriptor.AutoCloseOutputStream(parcelFileDescriptor)
                .write(fileToExport.readBytes())
        }
    }

    override fun getNavigationImage(): Int = R.drawable.ic_arrow_back_white_24dp

    override fun getActionBarImage(): Int = R.drawable.ic_share_white_24dp

    override fun getSecondaryActionBarImage(): Int = R.drawable.ic_file_download_white_24dp

    override fun startServiceRemainder(jobCardId: String, invoiceId: String) {
        startActivityForResult(
            NewServiceReminderActivity.getIntent(
                this, jobCardId, invoiceId, NewServiceReminderActivity.ACTION_INVOICE
            ), REQUEST_CODE_SERVICE_REMAINDER
        )
    }

    override fun startPartPayment(invoiceId: String, jobCardId: String, displayId: String) {
        startActivityForResult(
            PartPaymentActivity.getIntent(this, invoiceId, displayId, jobCardId),
            REQUEST_CODE_PART_PAYMENT
        )
    }

    override fun startDigitalSignatureActivity(jobCardId: String) {
        startActivityForResult(
            DigitalSignatureActivity.getIntent(this, jobCardId), REQUEST_CODE_DIGITAL_SIGNATURE
        )
    }

    override fun hideActionButton() {
        binding.layCommonPdf.layOutExtendedFab.actionParentView.visibility = View.GONE
    }

    override fun displayRaiseInvoiceDialog() {
        DialogFactory.notifyAlertDialog(this,
            "Raise Invoice",
            "This cannot be undone",
            SharedPrefHelper.isNotifyOnCreateInvoice() && SharedPrefHelper.isNotifyEnabled(),
            SharedPrefHelper.getDefaultOptionCreateInvoice(),
            false,
            object : DialogFactory.NotifyButtonListener {
                override fun positiveButton(notify: Boolean?) {
                    stateManager.react(StateManager.ReactionValue(notify ?: false))
                    // presenter.getInvoicePDF(id, notify ?: false)
                }

                override fun neutralButton() {
                }
            }).show()
    }

    override fun setActivityResultOk() {
        if (callingActivity != null) setResult(Activity.RESULT_OK)
    }

    override fun invalidate() {
        fragmentMap.clear()
        binding.tabLayout.removeAllTabs()
    }

    override fun create() {
        isCreating = true
        setUpPdf()
        setUpActions()
        isCreating = false
        openPdf()
        screenContainer.refreshToolBar()
    }

    override fun restart() {
        stateManager.setInteractionProvider(this)
        stateManager.start()
        screenContainer.refreshToolBar()
    }

    override fun setNextStateManager(stateManager: StateManager) {
        this.stateManager = stateManager
    }

    override fun showErrorMessage(errorMessage: String) {
        displayError(errorMessage) { _, _ ->
            finish()
        }
    }

    override fun showProgressIndicator() {
        showProgressBar()
    }

    override fun dismissProgressIndicator() {
        dismissProgressBar()
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (isCreating.not()) {
            openPdf()
            screenContainer.refreshToolBar()
        }
    }

    private fun setUpPdf() {
        if (stateManager.pdfs.size <= 1) {
            hideTabs()
        } else {
            showTabs()
        }

        stateManager.pdfs.forEach { pdf ->
            val newTab = getNewTab()
            val titleView = newTab.customView?.findViewById<TextView>(R.id.tabTitleView)
            titleView?.text = pdf.invoiceLabel
            val fragment = PdfViewFragment.newInstance(
                requireNotNull(pdf.name) { "pdf name is null" }, requireNotNull(
                    pdf.url
                ) { "pdf url is null" }, externalCacheDir?.absolutePath!!
            )
            binding.tabLayout.addTab(newTab)
            fragmentMap[newTab] = fragment
        }
        binding.tabLayout.addOnTabSelectedListener(this)
        // setFirstTab()
    }

    private fun getNewTab(): TabLayout.Tab {
        val view = View.inflate(this, R.layout.layout_multi_pdf_tab, null)
        val newTab = binding.tabLayout.newTab()
        newTab.customView = view
        return newTab
    }

    private fun hideTabs() {
        binding.tabLayout.visibility = View.GONE
    }

    private fun showTabs() {
        binding.tabLayout.visibility = View.VISIBLE
    }

    private fun openPdf() {
        val selectedTab = binding.tabLayout.getTabAt(binding.tabLayout.selectedTabPosition)
        val fragment = fragmentMap[selectedTab]
        if (fragment != null) {
            supportFragmentManager.beginTransaction().replace(R.id.pdfContainer, fragment)
                .commitAllowingStateLoss()
        }
    }

    private fun setUpActions() {
        val actionText = stateManager.getActionText()
        if (actionText == null) {
            binding.layCommonPdf.layOutExtendedFab.actionParentView.visibility = View.GONE
        } else {
            binding.layCommonPdf.layOutExtendedFab.actionParentView.visibility = View.VISIBLE
            binding.layCommonPdf.layOutExtendedFab.actionTitleView.text = actionText
            binding.layCommonPdf.layOutExtendedFab.actionParentView.setOnClickListener {
                stateManager.executeAction()
            }
        }
    }

    companion object {
        private const val ARG_STATE_MANAGER = "arg_state_manager"
        private const val REQUEST_CODE_SERVICE_REMAINDER = 1000
        private const val REQUEST_CODE_PART_PAYMENT = 1001
        private const val REQUEST_CODE_DIGITAL_SIGNATURE = 1002
        private const val FILE_MIME_TYPE = "application/pdf"

        fun getIntent(context: Context, stateManager: StateManager): Intent {
            return Intent(context, MultiPdfActivity::class.java).apply {
                putExtra(ARG_STATE_MANAGER, stateManager)
            }
        }
    }
}
