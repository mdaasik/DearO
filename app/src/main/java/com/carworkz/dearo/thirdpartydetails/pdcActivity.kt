package com.carworkz.dearo.thirdpartydetails

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.base.DialogFactory
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityMainBinding
import com.carworkz.dearo.databinding.ActivityThirdPartyDetailsBinding
import com.carworkz.dearo.domain.entities.Address
import com.carworkz.dearo.domain.entities.City
import com.carworkz.dearo.domain.entities.ThirdParty
import com.carworkz.dearo.interactionprovider.DefaultInteractionProvider
import com.carworkz.dearo.screencontainer.DefaultScreenContainer
/*import kotlinx.android.synthetic.main.activity_third_party_details.*
import kotlinx.android.synthetic.main.base_layout.**/
import javax.inject.Inject

const val ARG_INVOICE_ID = "arg_invoice_id"
const val ARG_THIRD_PARTY_DETAILS = "arg_third_party_details"

class ThirdPartyDetailsActivity : ScreenContainerActivity(), DefaultInteractionProvider, ThirdPartyDetailsContract.View {
    private lateinit var binding: ActivityThirdPartyDetailsBinding
    @Inject
    lateinit var presenter: ThirdPartyDetailsPresenter

    private lateinit var invoiceId: String

    private var thirdParty: ThirdParty? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        initComponent()
        super.onCreate(savedInstanceState)
        getExtras()
        setData()
        binding.submitBtn.setOnClickListener {
            val name = binding.nameThirdView.text.toString()
            val gstNumber = binding.gstNumberThirdView.text.toString()
            val mobileNumber = binding.mobileNumberThirdView.text.toString().trim()
            val email = binding.emailThirdView.text.toString()
            val street = binding.streetThirdView.text.toString()
            val locality = binding.localityThirdView.text.toString()
            val pinCode: String = binding.pincodeThirdView.text.toString()
            val city = binding.cityThirdView.text.toString()

            thirdParty?.let {
                it.isThirdParty = true
                it.name = name
                it.gstNumber = gstNumber
                if (mobileNumber.isNotEmpty()) {
                    it.mobile = mobileNumber
                } else {
                    it.mobile = null
                }
                if (email.isNotEmpty()) {
                    it.email = email
                }
                it.address.apply {
                    setStreet(street)
                    setCity(city)
                    if (pinCode.isNotEmpty())
                        pincode = pinCode.toInt()
                    location = locality
                }
            } ?: run {
                thirdParty = ThirdParty(isThirdParty = true, name = name, mobile = if (mobileNumber.isNotEmpty()) mobileNumber else null, email = if (email.isNotEmpty()) email else null, gstNumber = gstNumber, address = Address().apply {
                    setStreet(street)
                    setCity(city)
                    if (pinCode.isNotEmpty())
                        pincode = pinCode.toInt()

                    location = locality
                })
            }
            resetErrors()
            if (presenter.validate(thirdParty!!)) {
                DialogFactory.createGenericErrorDialog(this, "Alert!", "Please ensure that Customer & 3rd Party are not the same.") { _, _ -> presenter.save(invoiceId, thirdParty!!) }.show()
            }
        }
        binding.pincodeThirdView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 6) {
                    presenter.getCityByPinCode(s.toString().toInt())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        })
    }

    override fun onSaveSuccess(thirdParty: ThirdParty) {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onCityFetched(city: City?) {
        binding.cityThirdView.setText(city?.city)
        binding.cityThirdInputLayout.error = null
    }

    override fun invalidName() {
        binding.nameThirdInputLayout.error = "Name Required"
    }

    override fun invalidEmail() {
        binding.emailThirdInputLayout.error = "Invalid Email"
    }

    override fun invalidMobileNumber() {
        binding.mobileNumberThirdInputLayout.error = "Invalid Mobile Number"
    }

    override fun invalidGstNumber() {
        binding.gstNumberThirdInputLayout.error = "Invalid Gst Number"
    }

    override fun emptyGstNumber() {
        binding.gstNumberThirdInputLayout.error = "GST Number Required"
    }

    override fun invalidAddress() {
        binding.streetThirdInputLayout.error = "Building & Street Required"
    }

    override fun invalidPincode() {
        binding.pincodeThirdInputLayout.error = "PinCode Required"
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

    override fun createScreenContainer(): ScreenContainer = DefaultScreenContainer(this)
    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityThirdPartyDetailsBinding.inflate(layoutInflater)
        return binding
    }

    override fun getProgressView(): View = ProgressBar(this)

    override fun getToolBarTitle(): String = getString(R.string.third_party_label_toolbar_title)

    private fun initComponent() {
        (application as DearOApplication)
                .repositoryComponent
                .COMPONENT(ThirdPartyDetailsPresenterModule(this))
                .inject(this)
    }

    private fun getExtras() {
        invoiceId = intent.extras!!.getString(ARG_INVOICE_ID)!!
        thirdParty = intent.extras!!.getParcelable(ARG_THIRD_PARTY_DETAILS)
    }

    private fun resetErrors() {
        binding.nameThirdInputLayout.error = null
        binding.emailThirdInputLayout.error = null
        binding.mobileNumberThirdInputLayout.error = null
        binding.gstNumberThirdInputLayout.error = null
        binding.streetThirdInputLayout.error = null
        binding.pincodeThirdInputLayout.error = null
    }

    private fun setData() {
        thirdParty?.run {
            binding.nameThirdView.setText(name)
            binding.gstNumberThirdView.setText(gstNumber)
            binding.mobileNumberThirdView.setText(mobile)
            binding.emailThirdView.setText(email)
            binding.streetThirdView.setText(address.street)
            binding.localityThirdView.setText(address.location)
            binding.pincodeThirdView.setText(address.pincode.toString())
            binding.cityThirdView.setText(address.city)
        }
    }

    companion object {

        fun getIntent(context: Context, invoiceId: String, thirdParty: ThirdParty?): Intent {
            return Intent(context, ThirdPartyDetailsActivity::class.java).apply {
                putExtra(ARG_THIRD_PARTY_DETAILS, thirdParty)
                putExtra(ARG_INVOICE_ID, invoiceId)
            }
        }
    }
}
