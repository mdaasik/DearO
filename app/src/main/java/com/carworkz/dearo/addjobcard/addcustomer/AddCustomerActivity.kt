package com.carworkz.dearo.addjobcard.addcustomer

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.autofill.AutofillManager
import android.widget.AdapterView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.customercarsearch.CustomerCarSearchActivity
import com.carworkz.dearo.addjobcard.createjobcard.voice.CustomSpinnerAdapter
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.databinding.ActivityCustomerDetailsBinding
import com.carworkz.dearo.databinding.ActivityMainBinding
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.extensions.*
import com.carworkz.dearo.interactionprovider.ToolBarImgInteractionProvider
import com.carworkz.dearo.screencontainer.ActionImgScreenContainer
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.activity_customer_details.*
import kotlinx.android.synthetic.main.base_layout.*
import kotlinx.android.synthetic.main.fragment_new_voice.**/
import javax.inject.Inject

/**
 * Created by Kush on 8/8/17.
 */
class AddCustomerActivity : ScreenContainerActivity(),
    AddCustomerContract.View,
    ToolBarImgInteractionProvider, RadioGroup.OnCheckedChangeListener,
    AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityCustomerDetailsBinding
    private var customer = Customer()
    private lateinit var id: String
    private lateinit var address: Address
    private lateinit var type: String
    private lateinit var screenContainer: ActionImgScreenContainer
    private lateinit var customerGroupAdapter: CustomSpinnerAdapter
    private var customerGroupId: String? = null
    private var customerGroupList = mutableListOf<Option>()


    @Inject
    lateinit var presenter: AddCustomerPresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        type = intent.extras!!.getString(ARG_TYPE)!!
        super.onCreate(savedInstanceState)
        (application as DearOApplication)
            .repositoryComponent
            .COMPONENT(AddCustomerPresenterModule(this))
            .inject(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val autoFillManager: AutofillManager = getSystemService(AutofillManager::class.java)
            autoFillManager.disableAutofillServices()
        }


        if (type != ARG_ADD && checkIfNetworkAvailable()) {
            id = intent?.extras?.getString(ARG_CUSTOMER_ID)!!
            presenter.getCustomerById(id)
        } else {
            if (type == ARG_ADD) {
                binding.mobileView.setText(intent?.extras?.getString(ARG_MOBILE))
                binding.mobileView.isEnabled = false
                binding.emailView.setText(intent?.extras?.getString(ARG_CUSTOMER_EMAIL))
                binding.nameView.setText(intent?.extras?.getString(ARG_CUSTOMER_NAME))
            }
        }
        binding.registerNumberView.setOnCheckedChangeListener(this)
        if (type != ARG_VIEW) {
            binding.pinCodeView.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.isNotEmpty() == true && Utility.isPinCodeValid(s.toString())) {
                        presenter.pinCodeCity(s.toString().toInt())
                    } else {
                        binding.cityView.setText("")
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
        }
        binding.customerGroupSpinner.onItemSelectedListener = this
        if(SharedPrefHelper.getCustomerGroup()) {
            presenter.getCustomerGroup()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun createScreenContainer(): ScreenContainer {
        screenContainer = ActionImgScreenContainer(this)
        return screenContainer
    }

    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityCustomerDetailsBinding.inflate(layoutInflater)
        return binding
    }

    override fun getProgressView(): View {
        return binding.pbMain
    }

    override fun getNavigationImage(): Int = when (type) {
        ARG_VIEW, ARG_ADD -> R.drawable.ic_clear_white_24dp
        ARG_EDIT -> R.drawable.ic_arrow_back_white_24dp
        else -> R.drawable.ic_clear_white_24dp
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            when (type) {
                ARG_EDIT -> {
                    finish()
                }
                ARG_ADD, ARG_VIEW -> finish()
            }
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        when (type) {
            ARG_EDIT -> {
                finish()
            }
            ARG_ADD, ARG_VIEW -> finish()
        }
    }

    override fun onSecondaryActionBtnClick() {
        when (type) {
            ARG_ADD -> addNewCustomer()
            ARG_EDIT -> updateExisting()
            ARG_VIEW -> {
                if (customer.isEditable) {
                    type = ARG_EDIT
                    enableAllViews()
                    screenContainer.refreshToolBar()
                    /*oldCustomer = customer.copy()*/
                }
            }
        }
    }

    override fun getActionBarImage(): Int = 0

    override fun getSecondaryActionBarImage(): Int {
        return when (type) {
            ARG_VIEW -> {
                if (customer.isEditable) {
                    R.drawable.ic_edit_white_24dp
                } else {
                    0
                }
            }
            ARG_EDIT -> R.drawable.ic_save_white_24dp
            ARG_ADD -> R.drawable.ic_save_white_24dp
            else -> 0
        }
    }

    override fun showProgressIndicator() {
        showProgressBar()
    }

    override fun dismissProgressIndicator() {
        dismissProgressBar()
    }

    override fun onActionBtnClick() {
    }

    override fun onCheckedChanged(p0: RadioGroup?, id: Int) {
        when (find(id) as View) {
            binding.yesView -> {
                customer.registeredDealer = true
                binding.gstView.visibility = View.VISIBLE
                //visible customer group view
                if (SharedPrefHelper.getCustomerGroup()) {
                    binding.customerGroupLayout.visibility = View.VISIBLE
                }
            }

            binding.noView -> {
                customer.registeredDealer = false
                binding.gstView.visibility = View.GONE
                //hide customer group view
                binding.customerGroupLayout.visibility = View.GONE
            }
        }
    }

    override fun mobileError(error: String) {
        binding.mobileView.error = error
    }

    override fun showError(error: String) {
        if (error.isNotEmpty()) {
            val builder: AlertDialog.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
            } else {
                AlertDialog.Builder(this)
            }
            builder.setTitle(error)
                .setMessage("Retry")
                .setPositiveButton(R.string.yes) { _, _ -> finish() }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
    }

    override fun getToolBarTitle(): String {
        return when (type) {
            ARG_ADD -> "Add Customer"
            ARG_EDIT -> "Edit Customer"
            else -> "Customer Details"
        }
    }

    override fun moveToNextScreen() {
        val intent = Intent()
        intent.putExtra(CustomerCarSearchActivity.CHANGED_MOBILE_NO, binding.mobileView.text.toString())
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun displayError(Error: String) {
        displayError(Error) { _, _ -> }
    }

    override fun setCity(city: City?) {
        binding.cityView.setText(city?.city)
        binding.stateView.setText(city?.state)
        binding.cityView.error = null
    }

    override fun cityError() {
        binding.pinCodeView.error = "Invalid Pincode"
        binding.cityView.text?.clear()
    }

    override fun emailError(error: String) {
        binding.emailView.error = error
    }

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg) { _, _ -> finish() }
    }

    override fun showCustomerDetails(customer: Customer) {
        this.customer = customer
        screenContainer.refreshToolBar()
        binding.mobileView.setText(customer.mobile)
        binding.nameView.setText(customer.name)
        binding.emailView.setText(customer.email)

        if (customer.address.isNullOrEmpty().not()) {
            binding.localityView.setText(customer.address.first().location)
            binding.streetView.setText(customer.address.first().street)
            binding.pinCodeView.setText(customer.address.first().pincode.toString())
            binding.cityView.setText(customer.address.first().city)
            binding.stateView.setText(customer.address.first().state)
        } else {
            /*address = oldAddress*/
            binding.localityView.setText(address.location)
            binding.streetView.setText(address.street)
            binding.pinCodeView.setText(address.pincode.toString())
            binding.cityView.setText(address.city)
            binding.stateView.setText(address.state)
        }

        id = customer.id
        if (customer.registeredDealer) {
            binding.yesView.isChecked = true
            binding.gstView.setText(customer.gst)
            binding.customerGroupLayout.visibility=View.VISIBLE
        } else {
            binding.noView.isChecked = true
        }
        when (type) {
            ARG_EDIT, ARG_ADD -> {
                enableAllViews()
            }
            else -> {
                disableAllViews()

            }
        }
        binding.addCustomerParentView.requestFocus()
    }

    override fun showCustomerGroup(customerGroupList: List<Option>) {
        this.customerGroupList= customerGroupList as MutableList<Option>
        customerGroupAdapter=CustomSpinnerAdapter(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            customerGroupList
        )
        binding.customerGroupSpinner.adapter = customerGroupAdapter
    }

    private fun addNewCustomer() {
        if (checkIfNetworkAvailable()) {
            if (binding.nameView.text!!.trimEnd().isNotEmpty() &&
                binding.pinCodeView.text!!.isNotEmpty() &&
                Utility.isPinCodeValid(binding.pinCodeView.text.toString()) &&
                validateGST() &&
                validateEmailField(binding.emailView.text.toString()) &&
                Utility.isMobileNumberValid(binding.mobileView.text!!.toString()) &&
                binding.cityView.text!!.isNotEmpty() &&
                binding.stateView.text!!.isNotEmpty()) {

                customer.let {
                    it.name = binding.nameView.text.toString()
                    it.mobile = Utility.getServerAcceptableContactNumber(binding.mobileView.text.toString())
                    it.email = binding.emailView.text.toString()
                    if (it.registeredDealer) {
                        it.gst = binding.gstView.text.toString()
                    } else {
                        it.gst = null
                    }
                    it.customerGroupId=customerGroupId
                }
                address = Address().apply {
                    street = binding.streetView.text.toString()
                    location = binding.localityView.text.toString()
                    pincode = binding.pinCodeView.text.toString().toInt()
                    city=binding.cityView.text.toString()
                    state=binding.stateView.text.toString()
                }

                presenter.addNewCustomer(customer, address)
            } else {
                if (binding.nameView.text!!.isEmpty()) {
                    binding.nameView.error = "Name is required"
                }

                if (Utility.isMobileNumberValid(binding.mobileView.text.toString()).not()) {
                    binding.mobileView.error = "Mobile Number not Valid"
                }
                if (!validateEmailField(binding.emailView.text.toString())) {
                    binding.emailView.error = "Email Not valid"
                }
                if (!Utility.isPinCodeValid(binding.pinCodeView.text.toString())) {
                    binding.pinCodeView.error = "Invalid pinCode"
                }
                if (binding.pinCodeView.text!!.isEmpty()) {
                    binding.pinCodeView.error = "PinCode is required"
                    binding.cityView.requestFocus()
                }
                if (binding.cityView.text!!.isEmpty()) {
                    binding.cityView.error = "City is required"
                    binding.cityView.requestFocus()
                }
                if (!validateGST()) {
                    if (binding.gstView.text.toString().isEmpty()) {
                        binding.gstView.error = "Cannot be Empty"
                    } else {
                        binding.gstView.error = "Invalid Gst Number"
                    }
                }
            }
        } else {
            dismissProgressIndicator()
            toast("No Internet Connection")
        }
    }

    private fun validateGST(): Boolean {
        return if (customer.registeredDealer) {
            binding.gstView.text.toString().isNotEmpty() && Utility.isValidGst(binding.gstView.text.toString())
        } else {
            true
        }
    }

    private fun validateEmailField(email: String?): Boolean {
        return if (email.isNullOrEmpty()) {
            true
        } else (email.isNotEmpty() && Utility.isEmailValid(email))
    }

    private fun updateExisting() {
        if (checkIfNetworkAvailable()) {
            if (binding.nameView.text!!.trimEnd().isNotEmpty() &&
                validateGST() &&
                binding.pinCodeView.text!!.isNotEmpty() &&
                Utility.isMobileNumberValid(binding.mobileView.text.toString()) &&
                Utility.isPinCodeValid(binding.pinCodeView.text.toString()) &&
                id.isNotEmpty()) {
                customer.let {
                    it.name = binding.nameView.text.toString()
                    it.email = binding.emailView.text.toString()
                    it.mobile = Utility.getServerAcceptableContactNumber(binding.mobileView.text.toString())
                    if (it.registeredDealer) {
                        it.gst = binding.gstView.text.toString()
                    } else {
                        it.gst = null
                    }
                    it.customerGroupId=customerGroupId
                }
                if (!customer.address.isNullOrEmpty()) {
                    /*oldAddress = customer.address[0].copy()*/
                    address = customer.address.first()
                    customer.address = null
                }
                address.let {
                    it.street = binding.streetView.text.toString()
                    it.location = binding.localityView.text.toString()
                    it.pincode = binding.pinCodeView.text.toString().toInt()
                }

                presenter.updateCustomer(id, customer, address)
            } else {
                if (binding.nameView.text!!.isEmpty()) {
                    binding.nameView.error = "Name is required"
                }
                if (binding.emailView.text!!.isEmpty() && Utility.isEmailValid(binding.emailView.text.toString())) {
                    binding.emailView.error = "Email Not valid"
                }
                if (binding.mobileView.text!!.isEmpty() || Utility.isMobileNumberValid(binding.mobileView.text.toString())) {
                    binding.mobileView.error = "Mobile Number Not valid"
                }
                if (!Utility.isPinCodeValid(binding.pinCodeView.text.toString())) {
                    binding.pinCodeView.error = "Invalid pinCode"
                }
                if (binding.pinCodeView.text!!.isEmpty()) {
                    binding.pinCodeView.error = "PinCode Cannot be Blank"
                }

                if (!validateGST()) {
                    if (binding.gstView.text.toString().isEmpty()) {
                        binding.gstView.error = "Cannot be Empty"
                    } else {
                        binding.gstView.error = "Invalid Gst Number"
                    }
                }
                if (id.isEmpty()) {
                    alert("Retry?", "Some Thing went wrong!") {
                        ContextCompat.getDrawable(
                            applicationContext,
                            android.R.drawable.ic_dialog_alert
                        )?.let {
                            icon = it
                        }
                        okButton { finish() }
                        cancelButton { }
                    }.show()
                }
            }
        } else {
            dismissProgressIndicator()
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun disableAllViews() {
        false.apply {
            binding.nameView.isEnabled = this
            binding.mobileView.isEnabled = this
            binding.emailView.isEnabled = this
            binding.streetView.isEnabled = this
            binding.localityView.isEnabled = this
            binding.pinCodeView.isEnabled = this
            binding.registerNumberView.isEnabled = this
            binding.yesView.isEnabled = this
            binding.noView.isEnabled = this
            binding.gstView.isEnabled = this

            if(customer.customerGroupId.isNullOrEmpty().not()) {
                //hide spinner and show textview and set customerGroupud on it
                binding.customerGroupSpinner.visibility = View.GONE
                binding.customerGroupTextView.text=customer.customerGroupId
                binding.customerGroupTextView.visibility = View.VISIBLE
            }
            else
            {
                binding.customerGroupLayout.visibility = View.GONE
            }
        }
    }

    private fun enableAllViews() {
        true.apply {
            binding.nameView.isEnabled = this
            binding.mobileView.isEnabled = this
            binding.emailView.isEnabled = this
            binding.streetView.isEnabled = this
            binding.localityView.isEnabled = this
            binding.pinCodeView.isEnabled = this
            binding.registerNumberView.isEnabled = this
            binding.noView.isEnabled = this
            binding.yesView.isEnabled = this
            binding.gstView.isEnabled = this

            //check if cust group id is not null
            //if id is present then show selected item
            //else populate spinner

            if(customer.customerGroupId.isNullOrEmpty().not() and SharedPrefHelper.getCustomerGroup())
            {
                //visible customer group
                binding.customerGroupLayout.visibility = View.VISIBLE
                binding.customerGroupSpinner.visibility = View.VISIBLE
                binding.customerGroupTextView.visibility = View.VISIBLE

                //show selectedItem
                val option= customerGroupList.find { it.id.equals( customer.customerGroupId,true) }
                binding.customerGroupSpinner.setSelection(customerGroupList.indexOf(option))

                //option for remove
                binding.customerGroupTextView.text=""
                binding.customerGroupTextView.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_close_delete_black_24dp,0)
                binding.customerGroupTextView.setOnClickListener {
                    customerGroupId=""
                    binding.customerGroupSpinner.adapter= customerGroupAdapter
                    binding.customerGroupTextView.visibility=View.GONE
                }
            }
            else
            {
                customerGroupId=""
                binding.customerGroupLayout.visibility = View.GONE
            }
        }
    }

    companion object {
        const val ARG_TYPE = "ARG_TYPE"
        const val ARG_VIEW = "ARG_VIEW"
        const val ARG_EDIT = "ARG_EDIT"
        const val ARG_ADD = "ARG_ADD"
        const val ARG_MOBILE = "ARG_MOBILE"
        private const val ARG_CUSTOMER_NAME = "ARG_CUSTOMER_NAME"
        private const val ARG_CUSTOMER_EMAIL = "ARG_CUSTOMER_EMAIL"
        const val ARG_CUSTOMER_ID = "ARG_CUSTOMER_ID"

        fun getIntent(
            context: Context,
            type: String,
            Id: String?,
            mobile: String?,
            name: String?,
            email: String?
        ): Intent {
            return Intent(context, AddCustomerActivity::class.java)
                .putExtra(ARG_CUSTOMER_ID, Id)
                .putExtra(ARG_TYPE, type)
                .putExtra(ARG_MOBILE, mobile)
                .putExtra(ARG_CUSTOMER_NAME, name)
                .putExtra(ARG_CUSTOMER_EMAIL, email)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //
        customerGroupId = customerGroupList[position].id
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}
