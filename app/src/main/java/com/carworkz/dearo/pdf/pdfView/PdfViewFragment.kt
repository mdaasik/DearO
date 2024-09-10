package com.carworkz.dearo.pdf.pdfView

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.viewpager.widget.ViewPager
import com.carworkz.dearo.LoggingFacade
import com.carworkz.dearo.R
import com.carworkz.dearo.base.BaseActivity
import com.carworkz.dearo.base.BaseFragment
import com.carworkz.dearo.databinding.FragmentDashboardBinding
import com.carworkz.dearo.databinding.LayoutPdfViewerBinding
import com.carworkz.dearo.utils.PermissionUtil
import com.carworkz.library.downloader.Error
import com.carworkz.library.downloader.OnDownloadListener
import com.carworkz.library.downloader.PRDownloader
import com.carworkz.library.downloader.request.DownloadRequest
/*import kotlinx.android.synthetic.main.base_layout.*
import kotlinx.android.synthetic.main.layout_pdf.**/
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference

private const val ARG_FILE_NAME = "file_name"
private const val ARG_FILE_URL = "file_url"
private const val ARG_DIR_PATH = "dir_path"

class PdfViewFragment : BaseFragment(), FileInteractionProvider {
    private lateinit var binding: LayoutPdfViewerBinding
    private var fileName: String? = null
    private var fileUrl: String? = null
    private var dirPath: String? = null
    private var file: File? = null

    private lateinit var fileDownloader: FileDownloader

    private var pageCount = 0
    private var pDialog: AlertDialog? = null

    private var isFileDownloading: Boolean = false

    var deleteFileOnDestory = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            fileName = it.getString(ARG_FILE_NAME)
            fileUrl = it.getString(ARG_FILE_URL)
            dirPath = it.getString(ARG_DIR_PATH)
        }
        file = File(dirPath, fileName)
        fileDownloader = FileDownloader(this, fileUrl!!, dirPath!!, fileName!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = LayoutPdfViewerBinding.inflate(inflater, container, false)
        return binding.root
        /*return inflater.inflate(R.layout.layout_pdf_viewer, container, false)*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pDialog = AlertDialog.Builder(activity)
                .setMessage("PDF Loading...")
                .setView(ProgressBar(activity))
                .setNegativeButton("Cancel") { v, _ -> v.dismiss() }
                .setCancelable(true)
                .create()
        // actionParentView.visibility = View.GONE
        progressBar = ProgressBar(requireContext())
        binding.pdfView.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                binding.tvPageNumber?.text = StringBuilder()
                        .append("Page ")
                        .append(position + 1)
                        .append(" of ")
                        .append(pageCount)
                // "Page ${position + 1} of $pageCount"
            }
        })

        checkOrAskPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("destroying pdf fragment")
        if (isFileDownloading) {
            fileDownloader.cancel()
            file?.delete()
        }
//        else{
//            if (deleteFileOnDestory){
//                file?.delete()
//            }
//        }
    }

    override fun getPdfFile(): File? = file

    override fun getPdfFileUrl(): String? =fileUrl

    override fun isFileDownloading(): Boolean = isFileDownloading

    private fun checkOrAskPermission() {

        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)) {
        val isPermissionGranted = PermissionUtil.checkPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (!isPermissionGranted) {
            PermissionUtil.requestPermissions(activity as BaseActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.string.permission_save_file) { _, _, grantResults ->
                if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                    renderOrDownloadPdf()
                } else {
                    activity?.finish()
                }
            }
        } else {
            renderOrDownloadPdf()
        }}
        else{
            renderOrDownloadPdf()
        }
    }

    private fun renderOrDownloadPdf() {
        if (file?.exists() == true) {
            Timber.d("pdf is cached, rendering...")
            renderPdf()
        } else {
            pDialog?.show()
            Timber.d("pdf is not cached, downloading...")
            isFileDownloading = true
            fileDownloader.download()
        }
    }

    private fun renderPdf() {

        val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        val pdfRenderer = PdfRenderer(fileDescriptor)
        pageCount = pdfRenderer.pageCount

        binding.tvPageNumber?.text = StringBuilder()
                .append("Page 1 of ")
                .append(pageCount)

        try {
            val pdfPage = ArrayList<Bitmap>()
            for (i in 0 until pageCount) {
                val rendererPage = pdfRenderer.openPage(i)
                val rendererPageWidth = rendererPage.width
                val rendererPageHeight = rendererPage.height
                var pdfBitmap = Bitmap.createBitmap(rendererPageWidth, rendererPageHeight, Bitmap.Config.ARGB_8888)
                pdfBitmap = scaleToFill(pdfBitmap, resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)
                rendererPage.render(pdfBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                pdfPage.add(pdfBitmap)
                rendererPage.close()
            }
            binding.pdfView.adapter = PDFPagerAdapter(pdfPage)
        } catch (e: IOException) {
            if (file?.length() == 0L) {
                LoggingFacade.log("pdf file error", "file size is 0,resulting in IOException")
            }
            LoggingFacade.log(e)
        } finally {
            pdfRenderer.close()
            fileDescriptor?.close()
        }
    }

    private fun scaleToFill(b: Bitmap, width: Int, height: Int): Bitmap {

        Timber.d("scale to fill bitmap height " + b.height)
        Timber.d("scale to fill bitmap width " + b.width)
        Timber.d("scale to fill width $width")
        Timber.d("scale to fill height $height")

        val factorH = height / b.width.toFloat()
        val factorW = width / b.width.toFloat()
        val factorToUse = if (factorH > factorW) factorW else factorH

        Timber.d("scale to fill factor height $factorH")
        Timber.d("scale to fill factor width $factorW")
        Timber.d("scale to fill factor to use $factorToUse")
        return Bitmap.createScaledBitmap(b, (b.width * factorToUse).toInt(),
                (b.height * factorToUse).toInt(), true)
    }

    companion object {

        @JvmStatic
        fun newInstance(fileName: String, fileUrl: String, dirPath: String) =
                PdfViewFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_FILE_NAME, fileName)
                        putString(ARG_FILE_URL, fileUrl)
                        putString(ARG_DIR_PATH, dirPath)
                    }
                }
    }

    class FileDownloader() {
        private lateinit var weakFragment: WeakReference<PdfViewFragment>
        private lateinit var fileUrl: String
        private lateinit var dirPath: String
        private lateinit var fileName: String
        private var downloadRequest: DownloadRequest? = null

        constructor(fragment: PdfViewFragment, fileUrl: String, dirPath: String, fileName: String) : this() {
            weakFragment = WeakReference(fragment)
            this.fileName = fileName
            this.fileUrl = fileUrl
            this.dirPath = dirPath
        }

        fun download() {
            downloadRequest = PRDownloader.download(fileUrl, dirPath, fileName).build()
            downloadRequest?.start(object : OnDownloadListener {
                override fun onError(error: Error?) {
                    if (weakFragment.get()?.isAdded == true && weakFragment.get()?.activity != null) {
                        weakFragment.get()?.pDialog?.dismiss()
                        weakFragment.get()?.isFileDownloading = false
                    }
                }

                override fun onDownloadComplete() {
                    if (weakFragment.get()?.isAdded == true && weakFragment.get()?.activity != null) {
                        weakFragment.get()?.renderPdf()
                        weakFragment.get()?.pDialog?.dismiss()
                        weakFragment.get()?.isFileDownloading = false
                    }
                }
            })
        }

        fun cancel() {
            downloadRequest?.cancel()
        }
    }
}
