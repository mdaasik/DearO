package com.carworkz.dearo.addjobcard.createjobcard.customercarsearch

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.addcustomer.AddCustomerActivity
import com.carworkz.dearo.addjobcard.addeditvehicle.VehicleDetailsActivity
import com.carworkz.dearo.addjobcard.addeditvehicle.addmissinginfo.AddMissingVehicleInfoActivity
import com.carworkz.dearo.addjobcard.createjobcard.CreateJobCardActivity
import com.carworkz.dearo.addjobcard.createjobcard.damage.capturedamage.UploadService
import com.carworkz.dearo.addjobcard.quickjobcard.QuickJobCardActivity
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.appointments.createappointment.updateInfo.UpdateInfoActivity
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.cardslisting.adapters.AMCSelectionAdapter
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.interactionprovider.ToolBarInteractionProvider
import com.carworkz.dearo.amc.amcsolddetails.HistoryAdapter
import com.carworkz.dearo.amc.amcsolddetails.AmcDetailsInteraction
import com.carworkz.dearo.databinding.ActivityCustomerCarSearchBinding
import com.carworkz.dearo.databinding.ActivityInsuranceAddressSelectionBinding
import com.carworkz.dearo.mycustomers.cutomervehiclehistory.CustomerVehicleHistoryActivity
import com.carworkz.dearo.othersyshistory.OtherSysHistoryActivity
import com.carworkz.dearo.pdf.Source
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.screencontainer.SingleTextActionScreenContainer
import com.carworkz.dearo.utils.Constants
import com.carworkz.dearo.utils.PermissionUtil
import com.carworkz.dearo.utils.Utility
import com.shankasur.view.ToasterMessage
/*import kotlinx.android.synthetic.main.activity_customer_car_search.*
import kotlinx.android.synthetic.main.activity_new_estimtor.*
import kotlinx.android.synthetic.main.base_layout.**/
import timber.log.Timber
import javax.inject.Inject

class CustomerCarSearchActivity : ScreenContainerActivity(), ToolBarInteractionProvider, CustomerCarSearchContract.View, View.OnClickListener, AmcDetailsInteraction, AMCSelectionAdapter.AmcSelectListener
{
    private lateinit var binding: ActivityCustomerCarSearchBinding
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
    private var amcAdapter: AMCSelectionAdapter? = null

    private var selectedAmc: AMC? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        initialize()
        createComponent()
        screenTracker.sendScreenEvent(this, ScreenTracker.SCREEN_CUSTOMER_CAR_SEARCH, javaClass.name)
        intent.extras?.let {
            appointmentId = it.getString(APPOINTMENT_ID)
            binding.registerNumberView.setText(it.getString(REGISTRATION_NUMBER))
            binding.numberView.setText(it.getString(CUSTOMER_MOBILE_NUMBER))
        }
    }

    override fun onResume()
    {
        super.onResume()
        if (isSearchValid())

           searchCarAndCustomerMethod()

            //performSearch()
    }

    override fun onDestroy()
    {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode)
        {
            REQUEST_CODE                ->
            {
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    binding.numberView.setText(data.extras?.getString(CHANGED_MOBILE_NO))
                }
            }
            REQUEST_CODE_PICK_CONTACT   ->
            {
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    val contactUri = data.data
                    contactUri?.let { resolveContactData(it) }
                    resetConditionalStateForFreshSearch()
                    Timber.d("contact uri $contactUri")
                }
            }

            REQUEST_CODE_UPDATE_VEHICLE ->
            {
                if (resultCode == Activity.RESULT_OK)
                    resetConditionalStateForFreshSearch()
                binding.searchView.performClick()
            }
        }
    }

    override fun onClick(view: View?)
    {
        Timber.e(view?.hashCode().toString())
        Timber.e(binding.updateNumberView.hashCode().toString())
        when (view)
        {
            binding.initJobcardView     ->
            {
                if (jcType != null)
                {
                    if (jcType.equals(JobCard.TYPE_ACCIDENTAL) && selectedAmc != null)
                    {
                        //show dialog
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("AMC alert !!!")
                        builder.setMessage("AMC will not be applicable in case of Accidental Jobcard")
                        builder.setPositiveButton("Continue") { _, _ ->
                            //If “Continue” is clicked deselect AMC radio button and make it disable so that AMC details will not pass to JC and it will be considered without AMC
                            presenter.initiateJobCard(customer!!.id, vehicle!!.id, appointmentId, null, jcType!!)
                            Utility.hideSoftKeyboard(this)
                        }
                        builder.setNegativeButton(android.R.string.cancel) { _, _ ->
                            //On clicking “Cancel” button popup will be closed and Jobcard type will select “Periodic” by default.
                            jcType = JobCard.TYPE_PERIODIC
                            toggleRadioGroup(minor = false, major = false, periodic = true, accidental = false, amcsmc = false)
                        }
                        builder.setCancelable(false)
                        builder.show()
                        return
                    }


                    presenter.initiateJobCard(customer!!.id, vehicle!!.id, appointmentId, selectedAmc?.id, jcType!!)
                    Utility.hideSoftKeyboard(this)
                }
                else
                {
                    toast("Select JobCard Type")
                }
            }
            binding.searchView          ->
            {
                if (isSearchValid())
                {
                    performSearch()
                }
                else
                {
                    displayValidationError()
                }
                Utility.hideSoftKeyboard(this)
                resetConditionalStateForFreshSearch()
            }
            binding.createCustomerView  ->
            {
                if (customer != null && editable)
                {
                    startActivity(AddCustomerActivity.getIntent(this, AddCustomerActivity.ARG_EDIT, customer!!.id, binding.numberView.text.toString(), null, null))
                    Utility.hideSoftKeyboard(this)
                }
                else
                {
                    startActivity(AddCustomerActivity.getIntent(this, AddCustomerActivity.ARG_ADD, null, binding.numberView.text.toString(), contactName, contactEmail))
                    Utility.hideSoftKeyboard(this)
                    editable = true
                }
            }
            binding.addVehicleView      ->
            {
                if (vehicle == null)
                {
                    startActivity(VehicleDetailsActivity.getAddVehicleIntent(this, binding.registerNumberView.text.toString(), customer!!.id))
                    Utility.hideSoftKeyboard(this)
                }
                else
                {
                    startActivityForResult(AddMissingVehicleInfoActivity.getIntent(this, vehicle!!), REQUEST_CODE_UPDATE_VEHICLE)
                    Utility.hideSoftKeyboard(this)
                }
            }
            binding.updateNumberView    ->
            {
                if (checkIfNetworkAvailable())
                {
                    val isValidRegistrationNumber = binding.registerNumberView.text.toString().isNotEmpty() &&
                            Utility.isRegistrationNumberValid(binding.registerNumberView.text.toString().trim()) /*&&
                            Constants.BusinessConstants.STATE_LIST.find { it == registerNumberView.text.toString().substring(0..1) } != null*/

                 //   val isValidRegistrationNumber = registerNumberView.text.toString().isNotEmpty()


                    if (binding.numberView.text.toString().isNotEmpty() &&
                            Utility.isMobileNumberValid(binding.numberView.text.toString()) &&
                            isValidRegistrationNumber)
                    {
                        presenter.updateNumber(Utility.getServerAcceptableContactNumber(binding.numberView.text.toString()), customer!!.id)
                    }
                    else
                    {
                        if (isValidRegistrationNumber.not())
                        {
                            binding.registerNumberView.requestFocus()
                            binding.registerNumberView.error = "Invalid Registration"
                        }
                        else if (!Utility.isMobileNumberValid(binding.numberView.text.toString()))
                        {
                            binding.numberView.requestFocus()
                            binding.numberView.error = "Invalid Phone Number"
                        }
                    }
                }
            }
            binding.alternateNumberView ->
            {
                if (checkIfNetworkAvailable())
                {
                    val isValidRegistrationNumber = binding.registerNumberView.text.toString().isNotEmpty() &&
                            Utility.isRegistrationNumberValid(binding.registerNumberView.text.toString().trim()) /*&&
                            Constants.BusinessConstants.STATE_LIST.find { it == registerNumberView.text.toString().substring(0..1) } != null*/

                    if (binding.numberView.text.toString().isNotEmpty() &&
                            Utility.isMobileNumberValid(binding.numberView.text.toString()) &&
                            isValidRegistrationNumber)
                    {

                        presenter.addAlternateNumber(Utility.getServerAcceptableContactNumber(binding.numberView.text.toString()), customer!!.id)
                    }
                    else
                    {
                        if (!isValidRegistrationNumber)
                        {
                            binding.registerNumberView.requestFocus()
                            binding.registerNumberView.error = "Invalid Registration"
                        }
                        else if (!Utility.isMobileNumberValid(binding.numberView.text.toString()))
                        {
                            binding.numberView.requestFocus()
                            binding.numberView.error = "Invalid Phone Number"
                        }
                    }
                }
            }
            binding.majorView           ->
            {
                jcType = JobCard.TYPE_MAJOR
                toggleRadioGroup(minor = false, major = true, periodic = false, accidental = false, amcsmc = false)
            }
            binding.minorView           ->
            {
                jcType = JobCard.TYPE_MINOR
                toggleRadioGroup(minor = true, major = false, periodic = false, accidental = false, amcsmc = false)
            }
            binding.accidentalView      ->
            {
                jcType = JobCard.TYPE_ACCIDENTAL
                toggleRadioGroup(minor = false, major = false, periodic = false, accidental = true, amcsmc = false)
            }
            binding.periodicView        ->
            {
                jcType = JobCard.TYPE_PERIODIC
                toggleRadioGroup(minor = false, major = false, periodic = true, accidental = false, amcsmc = false)
            }
            binding.amcsmcView          ->
            {
                jcType = JobCard.TYPE_AMC_SMC
                toggleRadioGroup(minor = false, major = false, periodic = false, accidental = false, amcsmc = true)
            }
            binding.amcsmcView          ->
            {
                jcType = JobCard.TYPE_AMC_SMC
                toggleRadioGroup(minor = false, major = false, periodic = false, accidental = false, amcsmc = true)
            }
            binding.moreHistory         ->
            {
                //set flag
                presenter.getJobCardData(customerVehicleSearch.customerVehicleId, true)
            }
            binding.otherHistory        ->
            {
                //set flag
                presenter.getJobCardData(customerVehicleSearch.customerVehicleId, false)
            }
        }
    }

    override fun createScreenContainer(): ScreenContainer
    {
        return SingleTextActionScreenContainer(this)
    }

    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityCustomerCarSearchBinding.inflate(layoutInflater)
       return binding
    }


    override fun getProgressView(): View? = binding.baseLayout.pbMain


    override fun onActionBtnClick()
    {
    }

    override fun getActionBtnTitle(): String
    {
        return ""
    }

    override fun getToolBarTitle(): String
    {
        return getString(R.string.init_job_card)
    }

    override fun updateCustomerViews(obj: Customer)
    {
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
        if (obj.address != null && obj.address.isNotEmpty())
        {
            var address = ""
            if (obj.address[0].location != null) address += "${obj.address[0].location},"
            if (obj.address[0].pincode != null) address += " ${obj.address[0].pincode}"
            binding.addressView.text = address
        }
        else
        {
            binding.addressView.text = ""
        }
//        updateNumberView.visibility = View.GONE
// //        misMatchView.visibility = View.GONE
// //        correctView.visibility = View.GONE
//        alternateNumberView.visibility = View.GONE
        // createCustomerView.visibility = View.GONE
        binding.addVehicleView.isEnabled = true
//        addVehicleView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
//        addVehicleView.setTextColor(ContextCompat.getColor(this, R.color.white))
        binding.initJobcardView.visibility = View.VISIBLE
        if (editable)
        {
            binding.createCustomerView.text = getString(R.string.customer_car_search_edit_customer)
            binding.createCustomerView.visibility = View.VISIBLE
        }
        else
        {
            binding.createCustomerView.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    override fun updateVehicleViews(obj: Vehicle)
    {
        vehicle = obj
        binding.vehicleDetailsView.visibility = View.VISIBLE
        binding.vehicleDetailsView.setOnClickListener(this)
        binding.vehicleDetailsView.isClickable = true
        binding.vehicleNameView.visibility = View.VISIBLE
        binding.vehicleErrorView.visibility = View.GONE
        binding.vehicleNameView.text = "${obj.make.name} ${obj.model.name}"
        binding.addVehicleView.visibility = View.GONE
    }

    override fun updateAmcViews(amcList: List<AMC>)
    {
        if (amcList.isNotEmpty())
        {
            binding.amcDetailsView.visibility = View.VISIBLE
            binding.amcSelectorRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            amcList[0].selectedPosition = 0
            amcList[0].isSelected = true
            selectedAmc = amcList[0]
            amcAdapter = AMCSelectionAdapter(this, amcList as ArrayList<AMC>, this)
            if (selectedAmc != null)
            {
                amcAdapter!!.selectedAmc = selectedAmc as AMC
            }
            binding.amcSelectorRecyclerView.adapter = amcAdapter
        }
        else
        {
            binding.amcsmcView.isChecked = false
            binding.amcsmcView.visibility = View.GONE
        }
    }

    override fun toggleJobCardType(visibility: Boolean)
    {
        if (visibility)
        {
            binding.jcTypeView.visibility = View.VISIBLE
            when (jcType)
            {
                JobCard.TYPE_PERIODIC   -> toggleRadioGroup(minor = false, major = false, periodic = true, accidental = false, amcsmc = false)
                JobCard.TYPE_ACCIDENTAL -> toggleRadioGroup(minor = false, major = false, periodic = false, accidental = true, amcsmc = false)
                JobCard.TYPE_MINOR      -> toggleRadioGroup(minor = true, major = false, periodic = false, accidental = false, amcsmc = false)
                JobCard.TYPE_MAJOR      -> toggleRadioGroup(minor = false, major = true, periodic = false, accidental = false, amcsmc = false)
                JobCard.TYPE_AMC_SMC    -> toggleRadioGroup(minor = false, major = false, periodic = false, accidental = false, amcsmc = true)
                else                    ->
                {
                    toggleRadioGroup(minor = false, major = false, periodic = true, accidental = false, amcsmc = false)
                    jcType = JobCard.TYPE_PERIODIC
                }
            }
        }
        else
        {
            binding.jcTypeView.visibility = View.GONE
            jcType = null
        }
    }

    @SuppressLint("SetTextI18n")
    override fun updateCustomerNumberViews(obj: CustomerVehicleSearch, showUpdate: Boolean)
    {
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
        if (showUpdate)
        {
            binding.updateNumberView.visibility = View.VISIBLE
        }
        else
        {
            binding.updateNumberView.visibility = View.GONE
        }
        binding.alternateNumberView.visibility = View.VISIBLE
        binding.createCustomerView.visibility = View.GONE
        binding.customerErrorView.visibility = View.GONE
        // searchView.visibility = View.GONE
    }

    override fun addCustomer()
    {
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

    override fun onUseAlternateNumberSuccess(message: String)
    {
        toast("Alternate Number Updated")
        binding.numberMisMatchParentView.visibility = View.GONE
    }

    override fun addVehicle()
    {
        dismissProgressBar()
        binding.vehicleNotFoundView.visibility = View.VISIBLE
        binding.vehicleErrorView.visibility = View.VISIBLE
        binding.addVehicleView.visibility = View.VISIBLE
        binding.vehicleDetailsView.visibility = View.GONE
        binding.vehicleErrorView.text = getString(R.string.customer_car_search_label_add_vehicle_details)
        binding.addVehicleView.text = getString(R.string.customer_car_search_label_add_vehicle_details)
    }

    override fun updateVehicle()
    {
        binding.vehicleNotFoundView.visibility = View.VISIBLE
        binding.vehicleErrorView.visibility = View.VISIBLE
        binding.addVehicleView.visibility = View.VISIBLE
        binding.vehicleDetailsView.visibility = View.VISIBLE
        binding.vehicleErrorView.text = getString(R.string.customer_car_search_error_variant_info)
        binding.addVehicleView.text = getString(R.string.customer_car_search_label_update_vehicle_details)
    }

    override fun toggleJobCardBtn(enabled: Boolean)
    {
        if (enabled)
        {
            binding.initJobcardView.visibility = View.VISIBLE
            binding.initJobcardView.isEnabled = true
            binding.initJobcardView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
            binding.initJobcardView.setTextColor(ContextCompat.getColor(this, R.color.white))
        }
        else
        {
            binding.initJobcardView.visibility = View.GONE
            binding.initJobcardView.isEnabled = false
            binding.initJobcardView.setBackgroundColor(ContextCompat.getColor(this, R.color.light_grey))
            binding.initJobcardView.setTextColor(ContextCompat.getColor(this, R.color.black))
        }
    }

    override fun moveToNextScreen(obj: JobCard)
    {
        obj.customer = customer
        obj.vehicle = vehicle
        if (SharedPrefHelper.isQuickFlow())
        {
            startActivity(QuickJobCardActivity.getIntent(this, false, false, false, binding.registerNumberView.text.toString().toUpperCase(), obj))
            setResult(Activity.RESULT_OK)
            finish()
        }
        else
        {
            val intent = Intent(this, CreateJobCardActivity::class.java)
            intent.putExtra(CreateJobCardActivity.ARG_JOB_CARD, obj)
            intent.putExtra(CreateJobCardActivity.VEHICLE_AMC_ID, selectedAmc?.id)
            intent.putExtra(CreateJobCardActivity.ARG_IS_VIEW_ONLY, false)
            intent.putExtra(CreateJobCardActivity.ARG_VEHICLE_ID,binding.registerNumberView.text.toString().toUpperCase())
            if (obj.type == JobCard.TYPE_ACCIDENTAL)
            {
                intent.putExtra(CreateJobCardActivity.ARG_INIT, true)
            }
            // Notify to refresh CardList
            setResult(Activity.RESULT_OK)
            startActivity(intent)
            finish()
        }


    }

    override fun viewJC(obj: JobCard)
    {
        setResult(Activity.RESULT_OK)
        val intent = Intent(this, CreateJobCardActivity::class.java)
        intent.putExtra(CreateJobCardActivity.ARG_JOB_CARD, obj)
        intent.putExtra(CreateJobCardActivity.ARG_IS_VIEW_ONLY, true)
        intent.putExtra(CreateJobCardActivity.ARG_VEHICLE_ID, binding.registerNumberView.text.toString().toUpperCase())
        if (obj.type == JobCard.TYPE_ACCIDENTAL)
        {
            intent.putExtra(CreateJobCardActivity.ARG_INIT, true)
        }
        startActivity(intent)
//        intent=QuickJobCardActivity.getIntent(this, true, false, false, registerNumberView.text.toString().toUpperCase(), obj)
//        startActivity(intent)
    }

    override fun updateIncompleteInfo()
    {
        startActivity(Intent(this, UpdateInfoActivity::class.java)
                .apply {
                    putExtra(UpdateInfoActivity.CUSTOMER, customer)
                    putExtra(VEHICLE, vehicle)
                })
    }

    override fun resetCustomer()
    {
        customer = null
    }

    override fun showProgressIndicator()
    {
        showProgressBar()
    }

    override fun dismissProgressIndicator()
    {
        dismissProgressBar()
    }

    override fun showGenericError(errorMsg: String)
    {
        displayError(errorMsg)
    }

    override fun errorToast(error: String)
    {
        toast(error)
    }

    override fun onUseAlternateNumberError(error: String)
    {
        toast(error)
    }

    private fun initialize()
    {
        binding.searchView.setOnClickListener(this)
        binding.createCustomerView.setOnClickListener(this)
        binding.addVehicleView.setOnClickListener(this)
        binding.updateNumberView.setOnClickListener(this)
        binding.alternateNumberView.setOnClickListener(this)
        binding.initJobcardView.setOnClickListener(this)
        binding.periodicView.setOnClickListener(this)
        binding.minorView.setOnClickListener(this)
        binding.majorView.setOnClickListener(this)
        binding.amcsmcView.setOnClickListener(this)
        binding.moreHistory.setOnClickListener(this)
        binding.otherHistory.setOnClickListener(this)
        binding.numberView.setOnTouchListener(object : View.OnTouchListener
        {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean
            {
                val drawableRight = 2
                if (event?.action == MotionEvent.ACTION_UP)
                {
                    if (event.rawX >= (binding.numberView.right - binding.numberView.compoundDrawables[drawableRight].bounds.width()))
                    {

                        if (PermissionUtil.checkPermission(this@CustomerCarSearchActivity, android.Manifest.permission.READ_CONTACTS))
                        {
                            try
                            {
                                val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                                startActivityForResult(intent, REQUEST_CODE_PICK_CONTACT)
                            }
                            catch (e: ActivityNotFoundException)
                            {
                                Timber.d("contact activity not found")
                                toast("No App found for contacts")
                            }
                        }
                        else
                        {
                            PermissionUtil.requestPermissions(this@CustomerCarSearchActivity, android.Manifest.permission.READ_CONTACTS, R.string.permission_read_contacts) { _, _, grantResults ->
                                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                                {
                                    try
                                    {
                                        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                                        startActivityForResult(intent, REQUEST_CODE_PICK_CONTACT)
                                    }
                                    catch (e: ActivityNotFoundException)
                                    {
                                        Timber.d("contact activity not found")
                                        toast("No App found for contacts")
                                    }
                                }
                            }
                        }

                        return true
                    }
                }
                else
                {
                    v?.performClick()
                }
                return false
            }
        })

        if (SharedPrefHelper.isAccidentalEnabled())
        {
            binding.accidentalView.setOnClickListener(this)
        }
        else
        {
            binding.accidentalView.visibility = View.GONE
        }
    }

    private fun createComponent()
    {
        (application as DearOApplication)
                .repositoryComponent
                .COMPONENT(CustomerCarSearchPresenterModule(this))
                .inject(this)
    }

    private fun toggleRadioGroup(minor: Boolean, major: Boolean, periodic: Boolean, accidental: Boolean, amcsmc: Boolean)
    {
        binding.minorView.isChecked = minor
        binding.majorView.isChecked = major
        binding.accidentalView.isChecked = accidental
        binding.periodicView.isChecked = periodic
        binding.amcsmcView.isChecked = amcsmc
        if (amcsmc)
        {
            //show amc list
            binding.amcDetailsView.visibility = View.VISIBLE
            selectedAmc = amcAdapter?.amcList?.get(0)
            amcAdapter?.amcList?.get(0)?.selectedPosition = 0
            amcAdapter?.amcList?.get(0)?.isSelected = true
        }
        else
        {
            //hide amc list and amc=null
            if (selectedAmc != null)
            {
                binding.amcDetailsView.visibility = View.GONE
                amcAdapter?.amcList?.get(selectedAmc?.selectedPosition!!)?.isSelected = false
                amcAdapter?.amcList?.get(selectedAmc?.selectedPosition!!)?.selectedPosition = 0
                selectedAmc = null
                amcAdapter?.notifyDataSetChanged()
            }
        }
    }

    private fun performSearch()
    {
        if (checkIfNetworkAvailable())
        {
            //call new customer car search api
            presenter.newCarSearchApiCall(Utility.getServerAcceptableContactNumber(binding.numberView.text.toString()), Utility.getServerAcceptableRegistrationNumber(binding.registerNumberView.text.toString().toUpperCase()))
        }
    }

    override fun newSearchApiUpdate(enabled: Boolean,errorMessage : String) {
        if (enabled)
            searchCarAndCustomerMethod()
        else toast(errorMessage)
    }

    private fun searchCarAndCustomerMethod() {
        Utility.setVisibility(false, binding.historyViewLL)
        presenter.searchCarAndCustomer(Utility.getServerAcceptableContactNumber(binding.numberView.text.toString()), Utility.getServerAcceptableRegistrationNumber(binding.registerNumberView.text.toString().toUpperCase()))
        binding.searchView.visibility = View.GONE
    }

    private fun disableCreateJob()
    {
        binding.initJobcardView.isEnabled = false
        binding.initJobcardView.setBackgroundColor(ContextCompat.getColor(this, R.color.light_grey))
        binding.initJobcardView.setTextColor(ContextCompat.getColor(this, R.color.black))
    }

    @SuppressLint("Range")
    private fun resolveContactData(contactUri: Uri)
    {
        val cursor = contentResolver.query(contactUri, null, null, null, null)
        cursor.use {
            if (cursor!!.moveToFirst())
            {
                val contactId = it!!.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
                val hasPhoneNumber = it.getInt(it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) == 1
                if (!hasPhoneNumber)
                {
                    displayError("Contact details not available")
                    return
                }
                contactName = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                retrieveMobileNumberByContactId(contactId)
                retrieveEmailByContactId(contactId)
            }
        }
    }

    @SuppressLint("Range")
    private fun retrieveMobileNumberByContactId(contactId: String)
    {
        val selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"
        val cursorPhone = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                selection,
                arrayOf(contactId),
                null)
        cursorPhone?.use {
            val numbers = arrayListOf<String>()
            while (it.moveToNext())
            {
                it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).apply {
                    val number = PhoneNumberUtils.stripSeparators(this)
                    Timber.d("cleaned number ${PhoneNumberUtils.stripSeparators(number)}")
                    Timber.d("cleaned number $number")
                    if (Utility.isMobileNumberValid(number))
                    {
                        val serverAcceptableNumber = Utility.getServerAcceptableContactNumber(number)
                        if (Utility.isMobileNumber(serverAcceptableNumber))
                        {
                            numbers.add(this)
                        }
                    }
                }
            }

            if (numbers.isEmpty())
            {
                toast("No Mobile Number found in selected contact")
                return@use
            }

            if (numbers.size > 1)
            {
                //1. Creating the AlertDialog
                val alertBuilder = AlertDialog.Builder(this)

                //2. Setting the title
                alertBuilder.setTitle("Select")

                //3. Setting click handlers for each item of the list
                alertBuilder.setItems(Array(numbers.size) { itemIndex -> numbers[itemIndex].toString() }) { _, position ->
                    contactNumber = PhoneNumberUtils.stripSeparators(numbers[position])
                    setContactNumberForSearch(contactNumber!!)
                }.show()
            }
            else
            {
                contactNumber = PhoneNumberUtils.stripSeparators(numbers[0])
                setContactNumberForSearch(contactNumber!!)
            }
        }
    }

    @SuppressLint("Range")
    private fun retrieveEmailByContactId(contactId: String)
    {
        val cursorEmail = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                arrayOf(contactId),
                null)
        cursorEmail?.use {
            if (it.moveToFirst())
            {
                val email = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                contactEmail = if (Utility.isEmailValid(email)) email else null
            }
        }
    }

    private fun setContactNumberForSearch(mobileNumber: String)
    {
        binding.numberView.setText(mobileNumber)
    }

    private fun resetConditionalStateForFreshSearch()
    {
        editable = false
        editableVehicle = false
        vehicle = null
        if (contactNumber != binding.numberView.text.toString())
        {
            contactEmail = null
            contactName = null
        }
    }

    private fun displayValidationError()
    {
        if (binding.numberView.text.isNullOrEmpty())
        {
            binding.numberView.requestFocus()
            binding.numberView.error = "Phone Number is required"
        }
        else if (!Utility.isMobileNumberValid(binding.numberView.text.toString()))
        {
            binding.numberView.requestFocus()
            binding.numberView.error = "Invalid Phone Number"
        }
        if (binding.registerNumberView.text.isNullOrEmpty())
        {
            binding.registerNumberView.requestFocus()
            binding.registerNumberView.error = "Registration Number is required"
        }
        else if (!Utility.isRegistrationNumberValid(binding.registerNumberView.text.toString().trim()) ||
                Constants.BusinessConstants.STATE_LIST.find { it == binding.registerNumberView.text.toString().substring(0..1) } == null)
        {
            binding.registerNumberView.requestFocus()
            binding.registerNumberView.error = "Invalid Registration Number"
        }
    }

    private fun isSearchValid(): Boolean
    {
        return (binding.numberView.text.toString().isNotEmpty() &&
                binding.registerNumberView.text.toString().isNotEmpty() &&
                Utility.isMobileNumberValid(binding.numberView.text.toString()) &&
                Utility.isRegistrationNumberValid(binding.registerNumberView.text.toString().trim()) /*&&
                Constants.BusinessConstants.STATE_LIST.find { it == registerNumberView.text.toString().substring(0..1) } != null*/
                )
    }

    override fun displayHistory(searchObject: CustomerVehicleSearch)
    {
        customerVehicleSearch = searchObject
        if (searchObject.historyCount > 2 || searchObject.otherServiceHistoryCount > 0)
        {
            Utility.setVisibility(true, binding.historyButtonLL)
            Utility.setVisibility(true, binding.historyViewLL)
            if (searchObject.historyCount > 2)
            {
                Utility.setVisibility(true, binding.moreHistory)
            }
            if (searchObject.otherServiceHistoryCount > 0)
            {
                Utility.setVisibility(true, binding.otherHistory)
            }
        }

        if (searchObject.history != null && searchObject.history.size > 0)
        {
            Utility.setVisibility(true, binding.historyViewLL)
            binding.historyView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.historyView.setItemViewCacheSize(20)
            binding.historyView.isDrawingCacheEnabled = true
            binding.historyView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
            ViewCompat.setNestedScrollingEnabled(binding.historyView, false);
            binding.historyView.adapter = HistoryAdapter(searchObject.history, searchObject.vehicle!!.registrationNumber, this)
        }
        else
        {
            Utility.setVisibility(false, binding.historyViewLL)
        }
    }

    override fun showHistory(details: CustomerVehicleDetails, isDearoHistory: Boolean)
    {
        if (isDearoHistory)
        {
            startActivityForResult(CustomerVehicleHistoryActivity.getIntent(this, details), 2)
        }
        else
        {
            //show other history here
            val intent: Intent = OtherSysHistoryActivity.getIntent(this, details)
            startActivity(intent)
        }
    }


    companion object
    {
        const val REQUEST_CODE = 100
        const val REQUEST_CODE_PICK_CONTACT = 101
        const val REQUEST_CODE_UPDATE_VEHICLE = 102
        const val VEHICLE = "VEHICLE"
        const val REGISTRATION_NUMBER = "reg_no"
        const val CUSTOMER_MOBILE_NUMBER = "mobile_number"
        const val APPOINTMENT_ID = "appointment_id"
        const val CHANGED_MOBILE_NO = "changedNo"

        fun getIntent(context: Context, registrationNumber: String?, phoneNumber: String?, appointmentId: String?): Intent
        {
            return Intent(context, CustomerCarSearchActivity::class.java).apply {
                putExtra(REGISTRATION_NUMBER, registrationNumber)
                putExtra(CUSTOMER_MOBILE_NUMBER, phoneNumber)
                putExtra(APPOINTMENT_ID, appointmentId)
            }
        }
    }

    override fun getJobCardById(id: String)
    {
        presenter.getJobCard(id)
    }

    override fun startInvoicePreview(invoice: Invoice, jobCardId: String, source: Source)
    {
        pdfMediator.startInvoicePreviewPdf(this, invoice, jobCardId, source)
    }

    override fun onAmcSelected(amc: AMC)
    {
        selectedAmc = amc
    }
}
