package com.carworkz.dearo.addjobcard.createjobcard.estimate.digitalsignature

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.base.BaseActivity
import com.carworkz.dearo.databinding.ActivityClickPictureBinding
import com.carworkz.dearo.databinding.ActivityDigitalSignatureBinding
import com.carworkz.dearo.interactionprovider.ToolBarInteractionProvider
/*import kotlinx.android.synthetic.main.activity_digital_signature.**/
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class DigitalSignatureActivity : BaseActivity(), DigitalSignatureContract.View, ToolBarInteractionProvider, View.OnClickListener {
    private lateinit var binding: ActivityDigitalSignatureBinding

    @Inject
    lateinit var presenter: DigitalSignaturePresenter

    private lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDigitalSignatureBinding.inflate(layoutInflater)
        setContentView(binding.root)
      /*  setContentView(R.layout.activity_digital_signature)*/
        (application as DearOApplication)
                .repositoryComponent
                .COMPONENT(DigitalSignaturePresenterModule(this))
                .inject(this)
        id = intent.extras?.getString(ARG_ID).toString()
        /*if (intent.extras.getString(ARG_URL) != null) {
            //downloadFile(intent.extras.getString(ARG_URL))
        }*/
        binding.cancelView.setOnClickListener(this)
        binding.clearView.setOnClickListener(this)
        binding.submitView.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.cancelView -> finish()
            binding.clearView -> binding.signatureView.clear()
            binding.submitView -> {
                if (binding.agreeView.isChecked && !binding.signatureView.isEmpty) {
                    val file = File(this.cacheDir, "${id}_signature.svg")
                    val os = FileOutputStream(file)
                    os.write(binding.signatureView.signatureSvg.toByteArray())
                    os.flush()
                    os.close()
                    presenter.saveSignature(id, file)
                } else {
                    if (!binding.agreeView.isChecked) {
                        toast("Please Agree to Terms and Conditions.")
                    } else {
                        toast("Please Sign in the given space.")
                    }
                }
            }
        }
    }

    /*  fun downloadFile(url: String) {
          PRDownloader.download(url, cacheDir.path, "signature.svg").build().start(
                  object : OnDownloadListener {
                      override fun onDownloadComplete() {
                          Glide.with(this@DigitalSignatureActivity)
                                  .asBitmap()
                                  .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA))
                                  .load(File(cacheDir.path,"signature"))
                                  .into(signatureView)
                      }

                      override fun onError(error: Error?) {
                      }
                  }
          )
      }*/

    override fun getProgressView(): View = binding.pbMain

    override fun getToolBarTitle(): String = getString(R.string.cutomer_signature)

    override fun getActionBtnTitle(): String = ""

    override fun onActionBtnClick() = Unit

    override fun moveToNextScreen() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun showProgressIndicator() {
        showProgressBar()
    }

    override fun dismissProgressIndicator() {
        dismissProgressBar()
    }

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg)
    }

    companion object {
        private const val ARG_ID = "id"

        fun getIntent(context: Context, id: String): Intent = Intent(context, DigitalSignatureActivity::class.java).apply {
            putExtra(ARG_ID, id)
        }
    }
}
