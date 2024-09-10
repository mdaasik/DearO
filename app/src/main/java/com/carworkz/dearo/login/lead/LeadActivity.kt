package com.carworkz.dearo.login.lead

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityInvoiceRemarksBinding
import com.carworkz.dearo.databinding.ActivityLeadBinding
import com.carworkz.dearo.databinding.AlertThankYouBinding
import com.carworkz.dearo.login.LoginScreenContainer
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.activity_lead.*
import kotlinx.android.synthetic.main.alert_thank_you.view.**/
import javax.inject.Inject

class LeadActivity : ScreenContainerActivity(), LeadContract.View {
    private lateinit var binding: ActivityLeadBinding

    @Inject
    lateinit var presenter: LeadPresenter

    private var leadId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as DearOApplication)
            .repositoryComponent
            .COMPONENT(LeadPresenterModule(this))
            .inject(this)
        binding.mobileView.setText(intent?.extras?.getString(ARG_MOBILE, ""))
        leadId = intent?.extras?.getString(ARG_LEAD_ID, "")
        binding.submitView.setOnClickListener {
            if (Utility.isMobileNumberValid(binding.mobileView.text.toString())) {
                presenter.saveLeadInfo(
                    leadId!!,
                    Utility.getServerAcceptableContactNumber(binding.mobileView.text.toString()),
                    binding.nameView.text.toString(),
                    binding.cityView.text.toString()
                )
            } else {
                binding.mobileView.error = "Invalid Mobile Number"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun createScreenContainer(): ScreenContainer {
        return LoginScreenContainer()
    }

    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityLeadBinding.inflate(layoutInflater)
        return binding
    }

    override fun getProgressView(): View = binding.progressBarView

    override fun showProgressIndicator() {
        super.showProgressBar()
    }

    override fun dismissProgressIndicator() {
        super.dismissProgressBar()
    }

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg)
    }

    @SuppressLint("InflateParams")
    override fun moveToNextScreen() {
        val alert = AlertDialog.Builder(this)

        // Inflate the layout using the binding class
        val bindingAlert = AlertThankYouBinding.inflate(LayoutInflater.from(this))

        // Set the click listener for the okView button
        bindingAlert.okView.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }

        // Set the view for the alert dialog using the binding root
        alert.setView(bindingAlert.root)
        alert.setCancelable(false)
        alert.show()
    }


    /* @SuppressLint("InflateParams")
     override fun moveToNextScreen() {
         val alert = AlertDialog.Builder(this)
         val view = LayoutInflater.from(this).inflate(R.layout.alert_thank_you, null)
         view.okView.setOnClickListener {
             setResult(Activity.RESULT_OK)
             finish()
         }
         alert.setView(view)
         alert.setCancelable(false)
         alert.show()
     }*/

    companion object {
        const val ARG_MOBILE = "mobile"
        const val ARG_LEAD_ID = "LeadID"

        @JvmStatic
        fun getIntent(context: Context, leadId: String, mobileNumber: String): Intent {
            return Intent(context, LeadActivity::class.java).apply {
                putExtra(ARG_LEAD_ID, leadId)
                putExtra(ARG_MOBILE, mobileNumber)
            }
        }
    }
}
