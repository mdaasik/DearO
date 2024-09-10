package com.carworkz.dearo.addjobcard.createjobcard.damage.capturedamage

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.core.content.FileProvider
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.carworkz.dearo.BuildConfig
import com.carworkz.dearo.R
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityClickPictureBinding
import com.carworkz.dearo.databinding.ActivityCustomerCarSearchBinding
import com.carworkz.dearo.domain.entities.FileObject
import com.carworkz.dearo.domain.entities.Meta
import com.carworkz.dearo.interactionprovider.ToolBarInteractionProvider
import com.carworkz.dearo.screencontainer.SingleTextActionScreenContainer
import com.carworkz.dearo.utils.Constants
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.activity_click_picture.**/
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ClickPictureActivity : ScreenContainerActivity(), ToolBarInteractionProvider, View.OnClickListener {
    private lateinit var binding: ActivityClickPictureBinding
    private lateinit var progressView: ProgressBar
    private lateinit var capturedPicView: ImageView

    private lateinit var captionView: EditText
    private lateinit var jobCardID: String
    private var isViewOnly = true
    private var currentPicPath: String? = null
    private var photoFile: File? = null
    private var _category: String? = null
    private val categoryList = Constants.BusinessConstants.ACCIDENTAL_DOCUMENT_TYPE_LIST.toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isViewOnly = intent.extras!!.getBoolean(ARG_IS_VIEW_ONLY, false)
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            toast("Camera not available")
            finish()
            return
        }
        initViews()
        getIntentData()
        clickDamagePicture()
    }

    override fun onActionBtnClick() {
        if (checkIfNetworkAvailable()) {
            if (currentPicPath != null && photoFile?.length() != 0L) {
                startUploadService()
                Utility.hideSoftKeyboard(this)
            } else {
                toast("No Picture to save")
            }
        }
    }

    override fun getActionBtnTitle(): String {
        return "Submit"
    }

    override fun getToolBarTitle(): String {
        return ""
    }

    override fun createScreenContainer(): ScreenContainer {
        return SingleTextActionScreenContainer(this)
    }

    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityClickPictureBinding.inflate(layoutInflater)
       return binding
    }


    override fun getProgressView(): View {
        return progressView
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.image_frame -> clickDamagePicture()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (REQUEST_IMAGE_CODE == requestCode && resultCode == RESULT_OK) {
            capturedPicView.scaleType = ImageView.ScaleType.FIT_CENTER
            Glide.with(this)
                    .asDrawable()
                    .load(currentPicPath)
                    .into(capturedPicView)
        } else {
            finish()
        }
    }

    private fun initViews() {
        progressView = findViewById(R.id.pb_main)
        captionView = findViewById(R.id.et_caption)
        capturedPicView = findViewById(R.id.image_frame)
        capturedPicView.setOnClickListener(this)
        when (intent.action) {
            ACTION_DAMAGE -> {
                captionView.visibility = View.VISIBLE
                binding.categoryView.visibility = View.GONE
            }
            ACTION_ACCIDENTAL -> {
                captionView.visibility = View.GONE
                binding.categoryView.visibility = View.VISIBLE
                categoryList.add(0, "Select Category")
                binding.categoryView.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categoryList)
                binding.categoryView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        if (position != 0) {
                            _category = categoryList[position]
                        }
                    }
                }
            }

            ACTION_PDC -> {
                captionView.visibility = View.VISIBLE
                binding.categoryView.visibility = View.GONE
            }
        }
    }

    private fun getIntentData() {
        if (intent.extras != null) {
            jobCardID = intent.extras!!.getString(ARG_JOB_CARD_ID, null)
        }
    }

    private fun clickDamagePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            if (photoFile == null) {
                try {
                    photoFile = createNewImageFile()
                } catch (ex: IOException) {
                    Timber.d("creating ex" + ex.message)
                }
            }
            val photoURI =
                    FileProvider.getUriForFile(
                            this,
                            "${BuildConfig.APPLICATION_ID}.fileprovider",
                            photoFile!!)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CODE)
        }
    }

    @Throws(IOException::class)
    private fun createNewImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss.SSS", Locale.ENGLISH).format(Date())
        val imgFileName = "PHOTO_${timeStamp}_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imgfile = File.createTempFile(
                imgFileName,
                ".jpg",
                storageDir
        )
        currentPicPath = imgfile.absolutePath
        return imgfile
    }

    private fun startUploadService() {
        when (intent.action) {
            ACTION_DAMAGE -> {
                val intent = Intent(this, UploadService::class.java)
                intent.action = ACTION_DAMAGE
                intent.putExtra(UploadService.ARG_FILE, FileObject()
                        .apply {
                            this.jobCardID = this@ClickPictureActivity.jobCardID
                            this.caption = captionView.text.toString()
                            this.title = captionView.text.toString()
                            this.uri = currentPicPath
                            this.type = FileObject.FILE_TYPE_DAMAGE
                            this.mime = getMimeType(currentPicPath!!)
                            originalName = File(currentPicPath).name
                            meta = Meta().apply {
                                category = if (isViewOnly) {
                                    FileObject.WORK_IN_PROGESS
                                } else {
                                    FileObject.INSPECTION_AND_DAMAGES
                                }
                            }
                        })
                startService(intent)
                Utility.hideSoftKeyboard(this)
                showProgressBar()
                Handler(Looper.getMainLooper()).postDelayed({
                    dismissProgressBar()
                    finish()
                }, 500)
            }
            ACTION_ACCIDENTAL -> {
                if (_category != null) {
                    val intent = Intent(this, UploadService::class.java)
                    intent.action = ACTION_ACCIDENTAL
                    intent.putExtra(UploadService.ARG_FILE, FileObject()
                            .apply {
                                this.jobCardID = this@ClickPictureActivity.jobCardID
                                this.uri = currentPicPath
                                this.type = FileObject.FILE_TYPE_ACCIDENTAL
                                this.mime = getMimeType(currentPicPath!!)
                                originalName = File(currentPicPath).name
                                meta = Meta().apply {
                                    category = _category!!
                                }
                            })
                    startService(intent)
                    Utility.hideSoftKeyboard(this)
                    showProgressBar()
                    Handler(Looper.getMainLooper()).postDelayed({
                        dismissProgressBar()
                        finish()
                    }, 700)
                } else {
                    toast("Please Select Category")
                }
            }
            ACTION_PDC -> {
                val intent = Intent(this, UploadService::class.java)
                intent.action = ACTION_PDC
                intent.putExtra(UploadService.ARG_FILE, FileObject()
                    .apply {
                        this.jobCardID = this@ClickPictureActivity.jobCardID
                        this.caption = captionView.text.toString()
                        this.title = captionView.text.toString()
                        this.uri = currentPicPath
                        this.type = FileObject.FILE_TYPE_PDC
                        this.mime = getMimeType(currentPicPath!!)
                        originalName = File(currentPicPath).name
                        meta = Meta().apply {
                            category =  FileObject.WORK_IN_PROGESS
                        }
                    })
                startService(intent)
                Utility.hideSoftKeyboard(this)
                showProgressBar()
                Handler(Looper.getMainLooper()).postDelayed({
                    dismissProgressBar()
                    finish()
                }, 500)
            }
        }
    }

    private fun getMimeType(url: String): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    companion object {
        const val ARG_JOB_CARD_ID = "JobId"
        private const val REQUEST_IMAGE_CODE = 20
        const val ARG_IS_VIEW_ONLY = "is_view_only"
        const val ACTION_ACCIDENTAL = "accidental"
        const val ACTION_DAMAGE = "damage"
        const val ACTION_PDC = "pdc"

        fun getIntent(context: Context, jobCardId: String, isViewOnly: Boolean, action: String): Intent {
            val intent = Intent(context, ClickPictureActivity::class.java)
            intent.action = action
            intent.putExtra(ARG_JOB_CARD_ID, jobCardId)
            intent.putExtra(ARG_IS_VIEW_ONLY, isViewOnly)
            return intent
        }
    }
}
