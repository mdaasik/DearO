package com.carworkz.dearo.amc

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.addcustomer.AddCustomerActivity
import com.carworkz.dearo.addjobcard.addeditvehicle.VehicleDetailsActivity
import com.carworkz.dearo.addjobcard.addeditvehicle.addmissinginfo.AddMissingVehicleInfoActivity
import com.carworkz.dearo.addjobcard.createjobcard.CreateJobCardActivity
import com.carworkz.dearo.addjobcard.createjobcard.customercarsearch.CustomerCarSearchContract
import com.carworkz.dearo.addjobcard.createjobcard.customercarsearch.CustomerCarSearchPresenter
import com.carworkz.dearo.addjobcard.createjobcard.customercarsearch.CustomerCarSearchPresenterModule
import com.carworkz.dearo.addjobcard.quickjobcard.QuickJobCardActivity
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.appointments.createappointment.updateInfo.UpdateInfoActivity
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.cardslisting.adapters.AMCSelectionAdapter
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.interactionprovider.ToolBarInteractionProvider
import com.carworkz.dearo.amc.amcsolddetails.AmcDetailsInteraction
import com.carworkz.dearo.databinding.ActivityAmcSearchBinding
import com.carworkz.dearo.databinding.ActivityMainBinding
import com.carworkz.dearo.extensions.selector
import com.carworkz.dearo.pdf.Source
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.screencontainer.DefaultScreenContainer
import com.carworkz.dearo.utils.Constants
import com.carworkz.dearo.utils.PermissionUtil
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.activity_amc_search.*
import kotlinx.android.synthetic.main.base_layout.**/
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

class AMCSearchActivity : ScreenContainerActivity(), ToolBarInteractionProvider,
    CustomerCarSearchContract.View, View.OnClickListener, AmcDetailsInteraction {
    private lateinit var binding: ActivityAmcSearchBinding

    @Inject
    lateinit var presenter: CustomerCarSearchPresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    @Inject
    lateinit var pdfMediator: PdfMediator

    private var customer: Customer? = null
    private var vehicle: Vehicle? = null

    private var editable = false
    private var editableVehicle = false

    private var jcType: String? = null
    private var appointmentId: String? = null

    /*Fetched from contacts intent*/
    private var contactEmail: String? = null
    private var contactName: String? = null
    private var contactNumber: String? = null

    private var customerVehicleSearch = CustomerVehicleSearch()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        createComponent()
        screenTracker.sendScreenEvent(
            this,
            ScreenTracker.SCREEN_CUSTOMER_CAR_SEARCH,
            javaClass.name
        )
        intent.extras?.let {
            appointmentId = it.getString(APPOINTMENT_ID)
            binding.registerNumberView.setText(it.getString(REGISTRATION_NUMBER))
            binding.numberView.setText(it.getString(CUSTOMER_MOBILE_NUMBER))
        }
    }

    override fun onResume() {
        super.onResume()
        if (isSearchValid()) performSearch()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    binding.numberView.setText(data.extras?.getString(CHANGED_MOBILE_NO))
                }
            }

            REQUEST_CODE_PICK_CONTACT -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val contactUri = data.data
                    resolveContactData(contactUri!!)
                    resetConditionalStateForFreshSearch()
                    Timber.d("contact uri $contactUri")
                }
            }

            REQUEST_CODE_UPDATE_VEHICLE -> {
                if (resultCode == Activity.RESULT_OK)
                    resetConditionalStateForFreshSearch()
                binding.searchView.performClick()
            }
        }
    }

    override fun onClick(view: View?) {
        Timber.e(view?.hashCode().toString())
        Timber.e(binding.updateNumberView.hashCode().toString())
        when (view) {
            binding.buyAMC -> {
                //show amc package listing
                if (customer?.address == null) {
                    showGenericError("Update Address first!\nFor more details contact support team")
                } else {
                    if (!vehicle?.make?.name.isNullOrEmpty() && !vehicle?.model?.name.isNullOrEmpty() && !vehicle?.variant?.fuelType.isNullOrEmpty()) {
                        startActivity(AmcListingActivity.getIntent(this, vehicle!!, customer!!))
                        finish()
                    } else {
                        displayInfo("AMC not available for this make/model")
                    }
                }
            }

            binding.searchView -> {
                if (isSearchValid()) {
                    performSearch()
                } else {
                    displayValidationError()
                }
                Utility.hideSoftKeyboard(this)
                resetConditionalStateForFreshSearch()
            }

            binding.createCustomerView -> {
                if (customer != null && editable) {
                    startActivity(
                        AddCustomerActivity.getIntent(
                            this,
                            AddCustomerActivity.ARG_EDIT,
                            customer!!.id,
                            binding.numberView.text.toString(),
                            null,
                            null
                        )
                    )
                    Utility.hideSoftKeyboard(this)
                } else {
                    startActivity(
                        AddCustomerActivity.getIntent(
                            this,
                            AddCustomerActivity.ARG_ADD,
                            null,
                            binding.numberView.text.toString(),
                            contactName,
                            contactEmail
                        )
                    )
                    Utility.hideSoftKeyboard(this)
                    editable = true
                }
            }

            binding.addVehicleView -> {
                if (vehicle == null) {
                    startActivity(
                        VehicleDetailsActivity.getAddVehicleIntent(
                            this,
                            binding.registerNumberView.text.toString(),
                            customer!!.id
                        )
                    )
                    Utility.hideSoftKeyboard(this)
                } else {
                    startActivityForResult(
                        AddMissingVehicleInfoActivity.getIntent(this, vehicle!!),
                        REQUEST_CODE_UPDATE_VEHICLE
                    )
                    Utility.hideSoftKeyboard(this)
                }
            }

            binding.updateNumberView -> {
                if (checkIfNetworkAvailable()) {
                    val isValidRegistrationNumber =
                        binding.registerNumberView.text.toString().isNotEmpty() &&
                                Utility.isRegistrationNumberValid(
                                    binding.registerNumberView.text.toString().trim()
                                ) /*&&
                            Constants.BusinessConstants.STATE_LIST.find { it == registerNumberView.text.toString().substring(0..1) } != null
*/
                    if (binding.numberView.text.toString().isNotEmpty() &&
                        Utility.isMobileNumberValid(binding.numberView.text.toString()) &&
                        isValidRegistrationNumber
                    ) {
                        presenter.updateNumber(
                            Utility.getServerAcceptableContactNumber(binding.numberView.text.toString()),
                            customer!!.id
                        )
                    } else {
                        if (isValidRegistrationNumber.not()) {
                            binding.registerNumberView.requestFocus()
                            binding.registerNumberView.error = "Invalid Registration"
                        } else if (!Utility.isMobileNumberValid(binding.numberView.text.toString())) {
                            binding.numberView.requestFocus()
                            binding.numberView.error = "Invalid Phone Number"
                        }
                    }
                }
            }

            binding.alternateNumberView -> {
                if (checkIfNetworkAvailable()) {
                    val isValidRegistrationNumber =
                        binding.registerNumberView.text.toString().isNotEmpty() &&
                                Utility.isRegistrationNumberValid(
                                    binding.registerNumberView.text.toString().trim()
                                ) /*&&
                            Constants.BusinessConstants.STATE_LIST.find { it == registerNumberView.text.toString().substring(0..1) } != null*/

                    if (binding.numberView.text.toString().isNotEmpty() &&
                        Utility.isMobileNumberValid(binding.numberView.text.toString()) &&
                        isValidRegistrationNumber
                    ) {

                        presenter.addAlternateNumber(
                            Utility.getServerAcceptableContactNumber(
                                binding.numberView.text.toString()
                            ), customer!!.id
                        )
                    } else {
                        if (!isValidRegistrationNumber) {
                            binding.registerNumberView.requestFocus()
                            binding.registerNumberView.error = "Invalid Registration"
                        } else if (!Utility.isMobileNumberValid(binding.numberView.text.toString())) {
                            binding.numberView.requestFocus()
                            binding.numberView.error = "Invalid Phone Number"
                        }
                    }
                }
            }
        }
    }

    override fun createScreenContainer(): ScreenContainer {
        return DefaultScreenContainer(this)
    }

    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityAmcSearchBinding.inflate(layoutInflater)
        return binding
    }


    override fun getProgressView(): View = binding.baseLayout.pbMain

    override fun onActionBtnClick() {
    }

    override fun getActionBtnTitle(): String {
        return ""
    }

    override fun getToolBarTitle(): String {
        return getString(R.string.customer_details)
    }

    override fun updateCustomerViews(obj: Customer) {
        // dismissProgressBar()
        customer = obj
        binding.customerParentView.visibility = View.VISIBLE
        binding.customerDetailsView.visibility = View.VISIBLE
        binding.customerAddressParentView.visibility = View.VISIBLE
        binding.nameView.visibility = View.VISIBLE
        binding.numberMisMatchParentView.visibility = View.GONE
        binding.customerErrorView.visibility = View.GONE
        binding.customerDetailsView.setOnClickListener(this)
        binding.customerAddressParentView.setOnClickListener(this)
        binding.nameView.text = obj.name
        if (obj.address != null && obj.address.isNotEmpty()) {
            var address = ""
            if (obj.address[0].location != null) address += "${obj.address[0].location},"
            if (obj.address[0].pincode != null) address += " ${obj.address[0].pincode}"
            binding.addressView.text = address
        } else {
            binding.addressView.text = ""
        }
        binding.addVehicleView.isEnabled = true
        if (editable) {
            binding.createCustomerView.text = getString(R.string.customer_car_search_edit_customer)
            binding.createCustomerView.visibility = View.VISIBLE
        } else {
            binding.createCustomerView.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    override fun updateVehicleViews(obj: Vehicle) {
        vehicle = obj
        binding.vehicleDetailsView.visibility = View.VISIBLE
        binding.vehicleDetailsView.setOnClickListener(this)
        binding.vehicleDetailsView.isClickable = true
        binding.vehicleNameView.visibility = View.VISIBLE
        binding.vehicleErrorView.visibility = View.GONE
        binding.vehicleNameView.text = "${obj.make.name} ${obj.model.name}"
        binding.vehicleEngineTextView.visibility = View.VISIBLE
        binding.vehicleEngineTextView.text = obj.engineNumber
        binding.addVehicleView.visibility = View.GONE
        binding.buyAMC.visibility = View.VISIBLE
    }

    override fun updateAmcViews(amcList: List<AMC>) {
        if (amcList.isNotEmpty()) {
            binding.amcDetailsView.visibility = View.VISIBLE
            binding.amcSelectorRecyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.amcSelectorRecyclerView.adapter =
                AMCSelectionAdapter(this, amcList as ArrayList<AMC>, null)
        }
    }


    @SuppressLint("SetTextI18n")
    override fun updateCustomerNumberViews(obj: CustomerVehicleSearch, showUpdate: Boolean) {
        // dismissProgressBar()
        customer = obj.customer
        binding.customerDetailsView.visibility = View.VISIBLE
        binding.numberMisMatchParentView.visibility = View.VISIBLE
        binding.customerDetailsView.setOnClickListener(this)
        binding.nameView.text = obj.customer.name
        binding.nameView.visibility = View.VISIBLE
        binding.mobileView.text = "XXXXXX${obj.customer.mobile.substring(6)}"
//        customerAddressParentView.visibility = View.VISIBLE
//        correctView.visibility = View.VISIBLE
//        misMatchView.text = "Customer Details do not match"
//        misMatchView.visibility = View.VISIBLE
        if (showUpdate) {
            binding.updateNumberView.visibility = View.VISIBLE
        } else {
            binding.updateNumberView.visibility = View.GONE
        }
        binding.alternateNumberView.visibility = View.VISIBLE
        binding.createCustomerView.visibility = View.GONE
        binding.customerErrorView.visibility = View.GONE
        // searchView.visibility = View.GONE
    }

    override fun addCustomer() {
        dismissProgressBar()
        binding.customerParentView.visibility = View.VISIBLE
        binding.customerDetailsView.visibility = View.GONE
        binding.createCustomerView.visibility = View.VISIBLE
        binding.createCustomerView.text = getString(R.string.customer_car_search_create_customer)
        // nameView.visibility = View.GONE
        binding.customerAddressParentView.visibility = View.GONE
        binding.numberMisMatchParentView.visibility = View.GONE
        // editDetailsView.visibility = View.GONE
        binding.createCustomerView.visibility = View.VISIBLE
        binding.customerErrorView.visibility = View.VISIBLE
        disableCreateJob()
        binding.addVehicleView.isEnabled = false
        binding.customerDetailsView.setOnClickListener(null)
    }

    override fun onUseAlternateNumberSuccess(message: String) {
        toast("Alternate Number Updated")
        binding.numberMisMatchParentView.visibility = View.GONE
    }

    override fun addVehicle() {
        dismissProgressBar()
        binding.vehicleNotFoundView.visibility = View.VISIBLE
        binding.vehicleErrorView.visibility = View.VISIBLE
        binding.addVehicleView.visibility = View.VISIBLE
        binding.vehicleDetailsView.visibility = View.GONE
        binding.vehicleErrorView.text =
            getString(R.string.customer_car_search_label_add_vehicle_details)
        binding.addVehicleView.text =
            getString(R.string.customer_car_search_label_add_vehicle_details)
    }

    override fun updateVehicle() {
        binding.vehicleNotFoundView.visibility = View.VISIBLE
        binding.vehicleErrorView.visibility = View.VISIBLE
        binding.addVehicleView.visibility = View.VISIBLE
        binding.vehicleDetailsView.visibility = View.VISIBLE
        binding.vehicleErrorView.text = getString(R.string.customer_car_search_error_variant_info)
        binding.addVehicleView.text =
            getString(R.string.customer_car_search_label_update_vehicle_details)
    }


    override fun moveToNextScreen(obj: JobCard) {
        obj.customer = customer
        obj.vehicle = vehicle
        if (SharedPrefHelper.isQuickFlow()) {
            startActivity(
                QuickJobCardActivity.getIntent(
                    this,
                    false,
                    false,
                    false,
                    binding.registerNumberView.text.toString().uppercase(Locale.getDefault()),
                    obj
                )
            )
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            val intent = Intent(this, CreateJobCardActivity::class.java)
            intent.putExtra(CreateJobCardActivity.ARG_JOB_CARD, obj)
            intent.putExtra(CreateJobCardActivity.ARG_IS_VIEW_ONLY, false)
            intent.putExtra(
                CreateJobCardActivity.ARG_VEHICLE_ID,
                binding.registerNumberView.text.toString().uppercase(Locale.getDefault())
            )
            if (obj.type == JobCard.TYPE_ACCIDENTAL) {
                intent.putExtra(CreateJobCardActivity.ARG_INIT, true)
            }
            // Notify to refresh CardList
            setResult(Activity.RESULT_OK)
            startActivity(intent)
            finish()
        }
    }

    override fun viewJC(obj: JobCard) {
        setResult(Activity.RESULT_OK)
        val intent = Intent(this, CreateJobCardActivity::class.java)
        intent.putExtra(CreateJobCardActivity.ARG_JOB_CARD, obj)
        intent.putExtra(CreateJobCardActivity.ARG_IS_VIEW_ONLY, true)
        intent.putExtra(
            CreateJobCardActivity.ARG_VEHICLE_ID,
            binding.registerNumberView.text.toString().uppercase(Locale.getDefault())
        )
        if (obj.type == JobCard.TYPE_ACCIDENTAL) {
            intent.putExtra(CreateJobCardActivity.ARG_INIT, true)
        }
        startActivity(intent)
//        intent=QuickJobCardActivity.getIntent(this, true, false, false, registerNumberView.text.toString().toUpperCase(), obj)
//        startActivity(intent)
    }

    override fun updateIncompleteInfo() {
        startActivity(Intent(this, UpdateInfoActivity::class.java)
            .apply {
                putExtra(UpdateInfoActivity.CUSTOMER, customer)
                putExtra(VEHICLE, vehicle)
            })
    }

    override fun toggleJobCardType(visibility: Boolean) {

    }

    override fun toggleJobCardBtn(enabled: Boolean) {
    }

    override fun resetCustomer() {
        customer = null
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

    override fun errorToast(error: String) {
        toast(error)
    }

    override fun onUseAlternateNumberError(error: String) {
        toast(error)
    }

    private fun initialize() {
        binding.searchView.setOnClickListener(this)
        binding.buyAMC.setOnClickListener(this)
        binding.createCustomerView.setOnClickListener(this)
        binding.addVehicleView.setOnClickListener(this)
        binding.updateNumberView.setOnClickListener(this)
        binding.alternateNumberView.setOnClickListener(this)
        binding.numberView.setOnTouchListener(@SuppressLint("ClickableViewAccessibility")
        object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val drawableRight = 2
                if (event?.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (binding.numberView.right - binding.numberView.compoundDrawables[drawableRight].bounds.width())) {

                        if (PermissionUtil.checkPermission(
                                this@AMCSearchActivity,
                                android.Manifest.permission.READ_CONTACTS
                            )
                        ) {
                            try {
                                val intent = Intent(
                                    Intent.ACTION_PICK,
                                    ContactsContract.Contacts.CONTENT_URI
                                )
                                startActivityForResult(intent, REQUEST_CODE_PICK_CONTACT)
                            } catch (e: ActivityNotFoundException) {
                                Timber.d("contact activity not found")
                                toast("No App found for contacts")
                            }
                        } else {
                            PermissionUtil.requestPermissions(
                                this@AMCSearchActivity,
                                android.Manifest.permission.READ_CONTACTS,
                                R.string.permission_read_contacts
                            ) { _, _, grantResults ->
                                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                                    try {
                                        val intent = Intent(
                                            Intent.ACTION_PICK,
                                            ContactsContract.Contacts.CONTENT_URI
                                        )
                                        startActivityForResult(
                                            intent,
                                            REQUEST_CODE_PICK_CONTACT
                                        )
                                    } catch (e: ActivityNotFoundException) {
                                        Timber.d("contact activity not found")
                                        toast("No App found for contacts")
                                    }
                                }
                            }
                        }

                        return true
                    }
                } else {
                    v?.performClick()
                }
                return false
            }
        })
    }

    private fun createComponent() {
        (application as DearOApplication)
            .repositoryComponent
            .COMPONENT(CustomerCarSearchPresenterModule(this))
            .inject(this)
    }


    private fun performSearch() {
        if (checkIfNetworkAvailable()) {
            binding.vehicleEngineTextView.visibility = View.GONE
            presenter.searchCarAndCustomer(
                Utility.getServerAcceptableContactNumber(binding.numberView.text.toString()),
                Utility.getServerAcceptableRegistrationNumber(
                    binding.registerNumberView.text.toString().uppercase(Locale.getDefault())
                )
            )
        }
    }

    private fun disableCreateJob() {
    }

    @SuppressLint("Range")
    private fun resolveContactData(contactUri: Uri) {
        val cursor = contentResolver.query(contactUri, null, null, null, null)
        cursor.use {
            if (cursor?.moveToFirst()!!) {
                val contactId = it?.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
                val hasPhoneNumber =
                    it?.getInt(it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) == 1
                if (!hasPhoneNumber) {
                    displayError("Contact details not available")
                    return
                }

                contactName =
                    it?.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                retrieveMobileNumberByContactId(contactId!!)
                retrieveEmailByContactId(contactId)
            }
        }
    }

    @SuppressLint("Range")
    private fun retrieveMobileNumberByContactId(contactId: String) {
        val selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"
        val cursorPhone = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            selection,
            arrayOf(contactId),
            null
        )
        cursorPhone?.use {
            val numbers = arrayListOf<String>()
            while (it.moveToNext()) {
                it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    .apply {
                        val number = PhoneNumberUtils.stripSeparators(this)
                        Timber.d("cleaned number ${PhoneNumberUtils.stripSeparators(number)}")
                        Timber.d("cleaned number $number")
                        if (Utility.isMobileNumberValid(number)) {
                            val serverAcceptableNumber =
                                Utility.getServerAcceptableContactNumber(number)
                            if (Utility.isMobileNumber(serverAcceptableNumber)) {
                                numbers.add(this)
                            }
                        }
                    }
            }

            if (numbers.isEmpty()) {
                toast("No Mobile Number found in selected contact")
                return@use
            }

            if (numbers.size > 1) {
                selector("Select", numbers) { _, i ->
                    contactNumber = PhoneNumberUtils.stripSeparators(numbers[i])
                    setContactNumberForSearch(contactNumber!!)
                }
            } else {
                contactNumber = PhoneNumberUtils.stripSeparators(numbers[0])
                setContactNumberForSearch(contactNumber!!)
            }
        }
    }

    @SuppressLint("Range")
    private fun retrieveEmailByContactId(contactId: String) {
        val cursorEmail = contentResolver.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            null,
            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
            arrayOf(contactId),
            null
        )
        cursorEmail?.use {
            if (it.moveToFirst()) {
                val email =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                contactEmail = if (Utility.isEmailValid(email)) email else null
            }
        }
    }

    private fun setContactNumberForSearch(mobileNumber: String) {
        binding.numberView.setText(mobileNumber)
    }

    private fun resetConditionalStateForFreshSearch() {
        editable = false
        editableVehicle = false
        vehicle = null
        if (contactNumber != binding.numberView.text.toString()) {
            contactEmail = null
            contactName = null
        }
    }

    private fun displayValidationError() {
        if (binding.numberView.text.isNullOrEmpty()) {
            binding.numberView.requestFocus()
            binding.numberView.error = "Phone Number is required"
        } else if (!Utility.isMobileNumberValid(binding.numberView.text.toString())) {
            binding.numberView.requestFocus()
            binding.numberView.error = "Invalid Phone Number"
        }
        if (binding.registerNumberView.text.isNullOrEmpty()) {
            binding.registerNumberView.requestFocus()
            binding.registerNumberView.error = "Registration Number is required"
        } else if (!Utility.isRegistrationNumberValid(
                binding.registerNumberView.text.toString().trim()
            ) ||
            Constants.BusinessConstants.STATE_LIST.find {
                it == binding.registerNumberView.text.toString().substring(0..1)
            } == null
        ) {
            binding.registerNumberView.requestFocus()
            binding.registerNumberView.error = "Invalid Registration Number"
        }
    }

    private fun isSearchValid(): Boolean {
        return (binding.numberView.text.toString().isNotEmpty() &&
                binding.registerNumberView.text.toString().isNotEmpty() &&
                Utility.isMobileNumberValid(binding.numberView.text.toString()) &&
                Utility.isRegistrationNumberValid(binding.registerNumberView.text.toString().trim())/* &&
                Constants.BusinessConstants.STATE_LIST.find { it == registerNumberView.text.toString().substring(0..1) } != null*/
                )
    }

    override fun displayHistory(searchObject: CustomerVehicleSearch) {

    }

    override fun showHistory(details: CustomerVehicleDetails, isDearoHistory: Boolean) {

    }

    override fun newSearchApiUpdate(enabled: Boolean, error: String) {
    }


    companion object {
        const val REQUEST_CODE = 100
        const val REQUEST_CODE_PICK_CONTACT = 101
        const val REQUEST_CODE_UPDATE_VEHICLE = 102
        const val VEHICLE = "VEHICLE"
        const val REGISTRATION_NUMBER = "reg_no"
        const val CUSTOMER_MOBILE_NUMBER = "mobile_number"
        const val APPOINTMENT_ID = "appointment_id"
        const val CHANGED_MOBILE_NO = "changedNo"

        fun getIntent(
            context: Context,
            registrationNumber: String?,
            phoneNumber: String?,
            appointmentId: String?
        ): Intent {
            return Intent(context, AMCSearchActivity::class.java).apply {
                putExtra(REGISTRATION_NUMBER, registrationNumber)
                putExtra(CUSTOMER_MOBILE_NUMBER, phoneNumber)
                putExtra(APPOINTMENT_ID, appointmentId)
            }
        }
    }

    override fun getJobCardById(id: String) {
    }

    override fun startInvoicePreview(invoice: Invoice, jobCardId: String, source: Source) {
    }


}
