package com.carworkz.dearo.addjobcard.createjobcard.accidental

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.forEach
import androidx.core.widget.NestedScrollView
import com.carworkz.dearo.BuildConfig
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.LoggingFacade
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.ActionButtonClickEvent
import com.carworkz.dearo.addjobcard.createjobcard.ICreateJobCardInteraction
import com.carworkz.dearo.addjobcard.createjobcard.accidental.selectionaddress.InsuranceAddressSelectionActivity
import com.carworkz.dearo.addjobcard.createjobcard.damage.capturedamage.ClickPictureActivity
import com.carworkz.dearo.addjobcard.createjobcard.damage.capturedamage.UploadService
import com.carworkz.dearo.base.BaseActivity
import com.carworkz.dearo.base.BaseFragment
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.FragmentAccidentalBinding
import com.carworkz.dearo.databinding.InsuranceCompanyDetailsBinding
import com.carworkz.dearo.databinding.InsuranceDetailsBinding
import com.carworkz.dearo.databinding.LayoutFirDetailsBinding
import com.carworkz.dearo.databinding.LayoutSurveyDetailBinding
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.utils.AnimationCallback
import com.carworkz.dearo.utils.Constants
import com.carworkz.dearo.utils.PermissionUtil
import com.carworkz.dearo.utils.Utility
import com.carworkz.library.filepicker.FilePickerBuilder
import com.carworkz.library.filepicker.FilePickerConst
/*import kotlinx.android.synthetic.main.fragment_accidental.*
import kotlinx.android.synthetic.main.fragment_accidental.view.*
import kotlinx.android.synthetic.main.insurance_company_details.*
import kotlinx.android.synthetic.main.insurance_details.*
import kotlinx.android.synthetic.main.insurance_details.view.*
import kotlinx.android.synthetic.main.layout_fir_details.*
import kotlinx.android.synthetic.main.layout_survey_detail.**/
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import java.io.File
import java.net.URI
import java.util.*
import javax.inject.Inject

private const val TEMPLATE_DISPLAY_DATE = "%d-%d-%d"

class AccidentalFragment : BaseFragment(), View.OnClickListener,
    DocumentAdapter.DocumentInteraction, AccidentalContract.View, EventsManager.EventSubscriber,
    TextWatcher {
    private lateinit var binding: FragmentAccidentalBinding
    private var status: String? = null
    private var jobCardID: String? = null
    private lateinit var insuranceDatePicker: DatePickerDialog
    private lateinit var firDatePicker: DatePickerDialog
    private val calendar = Calendar.getInstance()
    private var selectedYear: Int = calendar.get(Calendar.YEAR)
    private var selectedMonth: Int = calendar.get(Calendar.MONTH)
    private var selectedDay: Int = calendar.get(Calendar.DAY_OF_MONTH)
    private var interaction: ICreateJobCardInteraction? = null

    /* private var insuranceDetailsBinding: InsuranceDetailsBinding? = null
     private var companyDetailsBinding: InsuranceCompanyDetailsBinding? = null
     private var surveyDetailBinding: LayoutSurveyDetailBinding? = null
     private var firDetailBinding: LayoutFirDetailsBinding? = null*/
    private var companyList = mutableListOf<InsuranceCompany>()
    private var selectedCompany: InsuranceCompany? = null
    private var isViewOnly = false

    @Inject
    lateinit var presenter: AccidentalPresenter

    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        initComponent()
        super.onCreate(savedInstanceState)
        arguments?.let {
            status = it.getString(STATUS)
            jobCardID = it.getString(ID)
            isViewOnly = it.getBoolean(ARG_VIEW_ONLY)
        } ?: run {
            LoggingFacade.log("Accidental fragment ", " arguments are null")
        }
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = it.data?.data

                    Timber.d("File Uri  $uri")
                    val categoryList = Constants.BusinessConstants.ACCIDENTAL_DOCUMENT_TYPE_LIST

                    val file = File(uri.toString())
                    val fileProvider = FileProvider.getUriForFile(
                        requireContext(),
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        file
                    )
                    Timber.d("file uri ${Uri.fromFile(file)} & file is $file")

                    //1. Creating the AlertDialog
                    val alertBuilder = AlertDialog.Builder(requireContext())

                    //2. Setting the title
                    alertBuilder.setTitle("Select Category")

                    //3. Setting click handlers for each item of the list
                    alertBuilder.setItems(Array(categoryList.size) { itemIndex -> categoryList[itemIndex].toString() }) { _, position ->
                        val intent = Intent(
                            this@AccidentalFragment.requireContext(),
                            UploadService::class.java
                        )
                        intent.action = UploadService.ACTION_ACCIDENTAL
                        val fileObject = FileObject()
                        fileObject.jobCardID = this@AccidentalFragment.jobCardID
                        fileObject.type = FileObject.FILE_TYPE_ACCIDENTAL
                        fileObject.uri = uri.toString()
                        fileObject.mime =
                            getMime(this@AccidentalFragment.requireContext(), fileProvider)
                        fileObject.originalName = file.name
                        val meta = Meta()
                        meta.category = categoryList[position]
                        fileObject.meta = meta
                        intent.putExtra(UploadService.ARG_FILE, fileObject)
                        this@AccidentalFragment.requireContext().startService(intent)
                    }.show()


                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccidentalBinding.inflate(inflater, container, false)
        return binding.root
        // return inflater.inflate(R.layout.fragment_accidental, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (status == JobCard.STATUS_IN_PROGRESS || status == JobCard.STATUS_INITIATED) {
            binding.insuranceParentView.setOnClickListener(this)
            binding.companyParentView.setOnClickListener(this)
            binding.surveyorParentView.setOnClickListener(this)
            binding.firParentView.setOnClickListener(this)
            binding.uploadView.setOnClickListener(this)
            binding.documentParentView.setOnClickListener(this)
            binding.scanView.setOnClickListener(this)
            binding.insuranceCompanyDetailView.pinCodeView.addTextChangedListener(this)
            binding.insuranceDetailView.insuranceDateView.setOnClickListener(this)
            binding.firDetailsView.firDateView.setOnClickListener(this)
            binding.insuranceCompanyDetailView.insuranceCompanyAssistView.setOnClickListener(this)
            binding.insuranceCompanyDetailView.insuranceCompanyAssistView.visibility = View.VISIBLE
            insuranceDatePicker = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    binding.insuranceDetailView.dateErrorView.text = ""
                    binding.insuranceDetailView.dateErrorView.visibility = View.GONE
                    binding.insuranceDetailView.insuranceDateView.error = null
                    binding.insuranceDetailView.insuranceDateView.setText(
                        String.format(
                            Locale.getDefault(),
                            TEMPLATE_DISPLAY_DATE,
                            dayOfMonth,
                            month + 1,
                            year
                        )
                    )
                },
                selectedYear,
                selectedMonth,
                selectedDay
            )
            insuranceDatePicker.datePicker.minDate = System.currentTimeMillis() - 1000
            firDatePicker = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    binding.firDetailsView.firDateView.setText(
                        String.format(
                            Locale.getDefault(),
                            TEMPLATE_DISPLAY_DATE,
                            dayOfMonth,
                            month + 1,
                            year
                        )
                    )
                },
                selectedYear,
                selectedMonth,
                selectedDay
            )
            val minDate = Calendar.getInstance()
            minDate.add(Calendar.MONTH, -1)
            firDatePicker.datePicker.minDate = minDate.timeInMillis
            firDatePicker.datePicker.maxDate = System.currentTimeMillis()
            binding.parentScrollView.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
                if (scrollY > oldScrollY) {
                    binding.floatingActionMenu.visibility = View.GONE
                }
                if (scrollY < oldScrollY) {
                    binding.floatingActionMenu.visibility = View.VISIBLE
                }
            }
        } else {
            View.VISIBLE.let {
                binding.insuranceDetailView.root.visibility = it
                binding.insuranceCompanyDetailView.root.visibility = it
                binding.surveyorDetailView.root.visibility = it
                binding.firDetailsView.root.visibility = it
                binding.documentView.visibility = it
            }
            binding.floatingActionMenu.visibility = View.GONE
            false.apply {
                (binding.insuranceDetailView as LinearLayout).forEach {
                    it.isEnabled = this
                    it.isFocusable = this
                }
                (binding.insuranceCompanyDetailView as LinearLayout).forEach {
                    it.isEnabled = this
                    it.isFocusable = this
                }
                (binding.surveyorDetailView as LinearLayout).forEach {
                    it.isEnabled = this
                    it.isFocusable = this
                }
                (binding.firDetailsView as LinearLayout).forEach {
                    it.isEnabled = this
                    it.isFocusable = this
                }
                binding.claimView.isEnabled = this
                binding.insuranceDetailView.cashlessView.isEnabled = this
                binding.insuranceCompanyDetailView.pinCodeView.isEnabled = this
                binding.insuranceCompanyDetailView.pinCodeView.isFocusable = this
            }
            Utility.disableStartKeyboard(activity)
        }
        /* insuranceDetailsBinding = InsuranceDetailsBinding.bind(binding.insuranceDetailView)
         companyDetailsBinding = InsuranceCompanyDetailsBinding.bind(binding.insuranceCompanyDetailView)
         surveyDetailBinding = LayoutSurveyDetailBinding.bind(binding.surveyorDetailView)
         firDetailBinding = LayoutFirDetailsBinding.bind(binding.firDetailsView)*/
        companyList.add(InsuranceCompany("Select Insurance Company", DUMMY_SLUG))
        binding.insuranceDetailView.insuranceCompanyView.adapter = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            companyList.map { it.name })
        presenter.getJobCardById(jobCardID!!)
    }

    override fun onResume() {
        super.onResume()
        EventsManager.register(this)
        Handler(Looper.getMainLooper()).postDelayed({
            presenter.getDocuments(jobCardID!!)
        }, 500)
    }

    override fun onStop() {
        super.onStop()
        presenter.clearCache()
        EventsManager.unregister(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ICreateJobCardInteraction) {
            interaction = context
        } else
            throw IllegalStateException("Activity must implement ICreateJobCardInteraction interface ")
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
        interaction = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_COMPANY_ADDRESS -> {
                data?.let {
                    val companyDetails = it.getParcelableExtra<InsuranceCompanyDetails>(
                        InsuranceAddressSelectionActivity.TAG
                    )
                    displayCompanyAddress(companyDetails!!)
                }
            }

            FilePickerConst.REQUEST_CODE_DOC -> {
                Timber.d("request code $requestCode result code $resultCode")
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val docPaths = mutableListOf<String>()
                    data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS)?.let {
                        docPaths.addAll(
                            it
                        )
                    }

                    Timber.d("selected paths $docPaths")
                    val categoryList = Constants.BusinessConstants.ACCIDENTAL_DOCUMENT_TYPE_LIST
                    docPaths.firstOrNull()?.let {
                        val file = File(it)
                        val fileProvider = FileProvider.getUriForFile(
                            requireContext(),
                            BuildConfig.APPLICATION_ID + ".fileprovider",
                            file
                        )
                        Timber.d("file uri ${Uri.fromFile(file)} & file is $file")

                        //1. Creating the AlertDialog
                        val alertBuilder = AlertDialog.Builder(requireContext())

                        //2. Setting the title
                        alertBuilder.setTitle("Select Category")

                        //3. Setting click handlers for each item of the list
                        alertBuilder.setItems(Array(categoryList.size) { itemIndex -> categoryList[itemIndex].toString() }) { _, position ->
                            val intent = Intent(
                                this@AccidentalFragment.requireContext(),
                                UploadService::class.java
                            )
                            intent.action = UploadService.ACTION_ACCIDENTAL
                            val fileObject = FileObject()
                            fileObject.jobCardID = this@AccidentalFragment.jobCardID
                            fileObject.type = FileObject.FILE_TYPE_ACCIDENTAL
                            fileObject.uri = it
                            fileObject.mime =
                                getMime(this@AccidentalFragment.requireContext(), fileProvider)
                            fileObject.originalName = file.name
                            val meta = Meta()
                            meta.category = categoryList[position]
                            fileObject.meta = meta
                            intent.putExtra(UploadService.ARG_FILE, fileObject)
                            this@AccidentalFragment.requireContext().startService(intent)
                        }.show()
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun displayCompanyList(companyList: List<InsuranceCompany>) {
        this.companyList.addAll(companyList)
        binding.insuranceDetailView.insuranceCompanyView.adapter = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            this.companyList.map { it.name })
        binding.insuranceDetailView.insuranceCompanyView.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (selectedCompany == this@AccidentalFragment.companyList[position]) return

                    selectedCompany = if (position != 0) {
                        this@AccidentalFragment.companyList[position]
                    } else {
                        null
                    }
                    clearCompanyAddress()
                }
            }
    }

    override fun moveToNextScreen() {
        interaction?.onJobSuccess()
    }

    override fun deleteDocument(fileObject: FileObject, position: Int) {
        presenter.deleteDocument(fileObject)
        (binding.documentRecycleView.adapter as DocumentAdapter).deleteItem(position)
    }

    override fun displayDocuments(obj: List<FileObject>) {
        if (obj.isNotEmpty()) {
            binding.noDocumentView.visibility = View.GONE
            binding.documentRecycleView.visibility = View.VISIBLE
            binding.documentRecycleView.adapter =
                DocumentAdapter(obj.toMutableList(), this, status!!)
        } else {
            binding.noDocumentView.visibility = View.VISIBLE
            binding.documentRecycleView.visibility = View.GONE
        }
    }

    override fun updateDocView() {
        if ((binding.documentRecycleView.adapter as DocumentAdapter).list.size < 0) {
            binding.noDocumentView.visibility = View.VISIBLE
            binding.documentRecycleView.visibility = View.GONE
        } else {
            binding.noDocumentView.visibility = View.GONE
            binding.documentRecycleView.visibility = View.VISIBLE
        }
    }

    override fun insuranceError(error: String) {
        binding.insuranceDetailView.dateErrorView.text = error
        binding.insuranceDetailView.dateErrorView.visibility = View.VISIBLE
        Utility.expandOrCollapseView(binding.insuranceDetailView, object : AnimationCallback {
            override fun onAnimationEnd(endState: AnimationCallback.Toggle) {
                if (endState == AnimationCallback.Toggle.UP) {
                    binding.toggleInsuranceView.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_keyboard_arrow_up_black_24dp
                        )
                    )
                } else {
                    binding.toggleInsuranceView.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_keyboard_arrow_down_black_24dp
                        )
                    )
                }
            }
        })
    }

    override fun showPinCodeError(error: String) {
        binding.insuranceCompanyDetailView.pinCodeView.error = error
        binding.insuranceCompanyDetailView.cityView.text?.clear()
    }

    override fun displayJobCardData(jobCard: JobCard, obj: List<InsuranceCompany>) {
        displayCompanyList(obj)
        if (jobCard.accidental != null) {
            if (jobCard.accidental.insurance != null) {
                binding.claimView.isChecked = jobCard.accidental.insurance!!.claim ?: false
                binding.insuranceDetailView.let {
                    if (jobCard.accidental.insurance?.expiryDate != null) {
                        val calendar = Calendar.getInstance()
                        calendar.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, 0, 0, 0)
                        if (Utility.getDate(jobCard.accidental.insurance?.expiryDate)
                                .before(calendar.time) && Calendar.getInstance().time != Utility.getDate(
                                jobCard.accidental.insurance?.expiryDate
                            )
                        ) {
                            if (status == JobCard.STATUS_IN_PROGRESS || status == JobCard.STATUS_INITIATED) {
                                binding.insuranceDetailView.dateErrorView.text =
                                    getString(R.string.accidental_label_insurance_expired)
                                binding.insuranceDetailView.dateErrorView.visibility = View.VISIBLE
                            }
                            if (jobCard.status != JobCard.STATUS_COMPLETED && jobCard.status != JobCard.STATUS_CLOSED) {
                                binding.insuranceDetailView.insuranceDateView.isEnabled = true
                            }
                        }
                    }
                    it.details = jobCard.accidental.insurance.apply {
                        this?.expiryDate = Utility.formatDate(
                            this?.expiryDate,
                            Utility.TIMESTAMP,
                            Utility.DATE_FORMAT_7,
                            Utility.TIMEZONE_UTC
                        )
                    }
                    it.executePendingBindings()
                }
            }
            if (jobCard.accidental.company != null) {
                binding.insuranceCompanyDetailView.let {
                    it.details = jobCard.accidental.company
                    it.address = jobCard.accidental.company?.address
                    it.executePendingBindings()
                }
                var selectedIndex =
                    companyList.indexOf(companyList.find { it.name == jobCard.accidental.company!!.name })
                selectedIndex = if (selectedIndex == -1) 0 else selectedIndex
                selectedCompany = if (selectedIndex == 0) null else companyList[selectedIndex]
                binding.insuranceDetailView.insuranceCompanyView.setSelection(selectedIndex)
            }
            if (jobCard.accidental.surveyor != null) {
                binding.surveyorDetailView.let {
                    it.details = jobCard.accidental.surveyor
                    it.executePendingBindings()
                }
            }
            if (jobCard.accidental.fir != null) {
                binding.firDetailsView.let {
                    it.details = jobCard.accidental.fir.apply {
                        this?.date = Utility.formatDate(
                            this?.date,
                            Utility.TIMESTAMP,
                            Utility.DATE_FORMAT_7,
                            Utility.TIMEZONE_UTC
                        )
                    }
                    it.executePendingBindings()
                }
            }
        } else {
            if (jobCard.vehicle.insurance != null) {
                binding.insuranceDetailView.let { detailsBinding ->
                    if (jobCard.vehicle.insurance?.expiryDate != null && isViewOnly.not()) {
                        if (Calendar.getInstance().time == Utility.getDate(jobCard.vehicle.insurance?.expiryDate) || Calendar.getInstance().time.after(
                                Utility.getDate(jobCard.vehicle.insurance?.expiryDate)
                            )
                        ) {
                            binding.insuranceDetailView.insuranceDateView.error =
                                "Insurance Expired"
                            binding.insuranceDetailView.insuranceDateView.isEnabled = true
                        }
                    }
                    detailsBinding.details = jobCard.vehicle.insurance.apply {
                        this?.expiryDate = Utility.formatDate(
                            this?.expiryDate,
                            Utility.TIMESTAMP,
                            Utility.DATE_FORMAT_7,
                            Utility.TIMEZONE_UTC
                        )
                    }
                    detailsBinding.executePendingBindings()
                    var selectedIndex =
                        companyList.indexOf(companyList.find { it.name == jobCard.vehicle.insurance.company })
                    selectedIndex = if (selectedIndex == -1) 0 else selectedIndex
                    selectedCompany = if (selectedIndex == 0) null else companyList[selectedIndex]
                    binding.insuranceDetailView.insuranceCompanyView.setSelection(selectedIndex)
                }
            }
        }
    }

    override fun displayCity(city: String) {
        binding.insuranceCompanyDetailView.cityView.setText(city)
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.insuranceParentView -> {
                Utility.expandOrCollapseView(
                    binding.insuranceDetailView,
                    object : AnimationCallback {
                        override fun onAnimationEnd(endState: AnimationCallback.Toggle) {
                            if (endState == AnimationCallback.Toggle.UP) {
                                binding.toggleInsuranceView.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_keyboard_arrow_up_black_24dp
                                    )
                                )
                            } else {
                                binding.toggleInsuranceView.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_keyboard_arrow_down_black_24dp
                                    )
                                )
                            }
                        }
                    })
            }

            binding.companyParentView -> {
                Utility.expandOrCollapseView(
                    binding.insuranceCompanyDetailView,
                    object : AnimationCallback {
                        override fun onAnimationEnd(endState: AnimationCallback.Toggle) {
                            if (endState == AnimationCallback.Toggle.UP) {
                                binding.toggleCompanyView.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_keyboard_arrow_up_black_24dp
                                    )
                                )
                            } else {
                                binding.toggleCompanyView.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_keyboard_arrow_down_black_24dp
                                    )
                                )
                            }
                        }
                    })
            }

            binding.surveyorParentView -> {
                Utility.expandOrCollapseView(
                    binding.surveyorDetailView,
                    object : AnimationCallback {
                        override fun onAnimationEnd(endState: AnimationCallback.Toggle) {
                            if (endState == AnimationCallback.Toggle.UP) {
                                binding.toggleSurveyView.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_keyboard_arrow_up_black_24dp
                                    )
                                )
                            } else {
                                binding.toggleSurveyView.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_keyboard_arrow_down_black_24dp
                                    )
                                )
                            }
                        }
                    })
            }

            binding.firParentView -> {
                Utility.expandOrCollapseView(binding.firDetailsView, object : AnimationCallback {
                    override fun onAnimationEnd(endState: AnimationCallback.Toggle) {
                        if (endState == AnimationCallback.Toggle.UP) {
                            binding.toggleFirView.setImageDrawable(
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_keyboard_arrow_up_black_24dp
                                )
                            )
                        } else {
                            binding.toggleFirView.setImageDrawable(
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_keyboard_arrow_down_black_24dp
                                )
                            )
                        }
                    }
                })
            }

            binding.documentParentView -> {
                Utility.expandOrCollapseView(binding.documentView, object : AnimationCallback {
                    override fun onAnimationEnd(endState: AnimationCallback.Toggle) {
                        if (endState == AnimationCallback.Toggle.UP) {
                            binding.docToggleView.setImageDrawable(
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_keyboard_arrow_up_black_24dp
                                )
                            )
                        } else {
                            binding.docToggleView.setImageDrawable(
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_keyboard_arrow_down_black_24dp
                                )
                            )
                        }
                    }
                })
            }

            binding.uploadView -> checkOrAskPermission()
            binding.scanView -> startActivity(
                ClickPictureActivity.getIntent(
                    requireContext(),
                    jobCardID!!,
                    isViewOnly,
                    ClickPictureActivity.ACTION_ACCIDENTAL
                )
            )

            binding.insuranceDetailView.insuranceDateView -> insuranceDatePicker.show()
            binding.firDetailsView.firDateView -> firDatePicker.show()
            binding.insuranceCompanyDetailView.insuranceCompanyAssistView -> {
                if (selectedCompany == null) {
                    Toast.makeText(context, "Please select Insurance company", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    startActivityForResult(
                        InsuranceAddressSelectionActivity.getIntent(
                            requireContext(),
                            selectedCompany!!
                        ), REQUEST_CODE_COMPANY_ADDRESS
                    )
                }
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
        when (activity?.currentFocus) {
            binding.insuranceCompanyDetailView.pinCodeView -> {
                if (checkIfNetworkAvailable() && Utility.isPinCodeValid(s.toString()))
                    presenter.getCityByPincode(s.toString().toInt())
                if (binding.insuranceCompanyDetailView.pinCodeView.text!!.isEmpty() || !Utility.isPinCodeValid(
                        s.toString()
                    )
                ) {
                    binding.insuranceCompanyDetailView.cityView.text!!.clear()
                }
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun showProgressIndicator() {
        (activity as ScreenContainerActivity).showProgressBar()
    }

    override fun dismissProgressIndicator() {
        (activity as ScreenContainerActivity).dismissProgressBar()
    }

    override fun showGenericError(errorMsg: String) {
        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
    }

    @Subscribe
    fun onNextBtnEvent(btn: ActionButtonClickEvent) {
        if (status != JobCard.STATUS_INITIATED && status != JobCard.STATUS_IN_PROGRESS) {
            moveToNextScreen()
        } else {
            if (checkGst(binding.insuranceCompanyDetailView.gstView.text.toString()) &&
                (((binding.insuranceCompanyDetailView.pinCodeView.text!!.isNotEmpty() && Utility.isPinCodeValid(
                    binding.insuranceCompanyDetailView.pinCodeView.text.toString()
                ) && binding.insuranceCompanyDetailView.cityView.text!!.isNotEmpty()) || binding.insuranceCompanyDetailView.pinCodeView.text!!.isEmpty())) &&
                ((binding.surveyorDetailView.numberView.text!!.isNotEmpty() && Utility.isMobileNumberValid(
                    binding.surveyorDetailView.numberView.text.toString()
                ) || binding.surveyorDetailView.numberView.text!!.isEmpty()))
            ) {
                Accidental().let {
                    val insurance = Insurance()
                    insurance.cashless = binding.insuranceDetailView.cashlessView.isChecked
                    insurance.claim = binding.claimView.isChecked
                    insurance.claimNumber =
                        fetchTextOrNull(binding.insuranceDetailView.claimNumberView)
                    insurance.policyNumber =
                        fetchTextOrNull(binding.insuranceDetailView.policyNumberView)
                    insurance.expiryDate =
                        if (binding.insuranceDetailView.insuranceDateView.text.toString()
                                .isNotEmpty()
                        ) {
                            Utility.formatDate(
                                binding.insuranceDetailView.insuranceDateView.text.toString(),
                                Utility.DATE_FORMAT_7,
                                Utility.DATE_FORMAT_5,
                                Utility.TIMEZONE_UTC
                            )
                        } else {
                            null
                        }
                    it.insurance = insurance
                    val company = Company()
                    company.gstNumber = fetchTextOrNull(binding.insuranceCompanyDetailView.gstView)
                    company.name = selectedCompany?.name
                    val address = Address()
//                    address.location = fetchTextOrNull(localityView)
                    if (binding.insuranceCompanyDetailView.pinCodeView.text.toString().trim()
                            .isNotEmpty()
                    ) {
                        address.pincode =
                            binding.insuranceCompanyDetailView.pinCodeView.text.toString().toInt()
                    }
                    address.street = fetchTextOrNull(binding.insuranceCompanyDetailView.streetView)
                    address.ownerType = null
                    company.address = address
                    it.company = company
                    val surveyor = Surveyor()
                    surveyor.name = fetchTextOrNull(binding.surveyorDetailView.nameView)
                    surveyor.code = fetchTextOrNull(binding.surveyorDetailView.codeView)
                    val mobile = fetchTextOrNull(binding.surveyorDetailView.numberView)
                    surveyor.mobile =
                        if (mobile == null) null else Utility.getServerAcceptableContactNumber(
                            fetchTextOrNull(binding.surveyorDetailView.numberView)
                        )
                    it.surveyor = surveyor
                    val fir = Fir()
                    fir.policeStation = fetchTextOrNull(binding.firDetailsView.stationNameView)
                    fir.date =
                        if (binding.firDetailsView.firDateView.text.toString().isNotEmpty()) {
                            Utility.formatDate(
                                binding.firDetailsView.firDateView.text.toString(),
                                Utility.DATE_FORMAT_7,
                                Utility.DATE_FORMAT_5,
                                Utility.TIMEZONE_UTC
                            )
                        } else {
                            null
                        }
                    fir.number = fetchTextOrNull(binding.firDetailsView.firNumberView)
                    it.fir = fir
                    presenter.saveData(jobCardID!!, it)
                }
            } else {
                dismissProgressIndicator()
                if (!checkGst(binding.insuranceCompanyDetailView.gstView.text.toString())) {
                    binding.insuranceCompanyDetailView.gstView.error = "Invalid GST"
                }
                if (binding.surveyorDetailView.numberView.text!!.isNotEmpty() && !Utility.isMobileNumberValid(
                        binding.surveyorDetailView.numberView.text.toString()
                    )
                ) {
                    binding.surveyorDetailView.numberView.error = "Invalid Number"
                }
                if ((binding.insuranceCompanyDetailView.pinCodeView.text!!.isNotEmpty() && !Utility.isPinCodeValid(
                        binding.insuranceCompanyDetailView.pinCodeView.text.toString()
                    )) || (binding.insuranceCompanyDetailView.pinCodeView.text!!.isNotEmpty() && binding.insuranceCompanyDetailView.cityView.text!!.isEmpty())
                ) {
                    binding.insuranceCompanyDetailView.pinCodeView.error = "Invalid PinCode"
                }
            }
        }
    }

    private fun checkGst(gst: String): Boolean {
        return when {
            gst.isEmpty() -> true
            gst.isNotEmpty() && Utility.isValidGst(gst) -> true
            else -> false
        }
    }

    private fun getMime(context: Context, contentUri: Uri): String? {
        return context.contentResolver.getType(contentUri)
    }

    private fun fetchTextOrNull(view: View): String? {
        return if (view is EditText) {
            if (view.text.toString().isEmpty()) {
                null
            } else {
                view.text.toString()
            }
        } else {
            null
        }
    }

    private fun checkOrAskPermission() {
        val isPermissionGranted =
            PermissionUtil.checkPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)) {
            if (!isPermissionGranted) {
                PermissionUtil.requestPermissions(
                    activity as BaseActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    R.string.permission_save_file
                ) { _, _, grantResults ->
                    if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                        showFileChooser()
                    } else {
                        activity?.finish()
                    }
                }
            } else {
                showFileChooser()
            }
        } else {

            showFileChooser()
        }
    }

    private fun displayCompanyAddress(companyDetails: InsuranceCompanyDetails) {
        binding.insuranceCompanyDetailView.streetView.setText(companyDetails.street)
        binding.insuranceCompanyDetailView.pinCodeView.setText(companyDetails.pincode.toString())
        binding.insuranceCompanyDetailView.cityView.setText(companyDetails.city)
        binding.insuranceCompanyDetailView.gstView.setText(companyDetails.gstNumber)
    }

    private fun clearCompanyAddress() {
        binding.insuranceCompanyDetailView.streetView.text!!.clear()
        binding.insuranceCompanyDetailView.pinCodeView.text!!.clear()
        binding.insuranceCompanyDetailView.cityView.text!!.clear()
        binding.insuranceCompanyDetailView.gstView.text!!.clear()
    }

    private fun showFileChooser() {
        /* FilePickerBuilder.instance.setMaxCount(1)
                 .setActivityTheme(R.style.LibAppTheme)
                 .enableDocSupport(false)
                 .addFileSupport("PDF", arrayOf(".pdf"))
                 .addFileSupport("Images", arrayOf(".jpg", ".jpeg", ".png"))
                 .pickFile(this)*/

        var data = Intent(Intent.ACTION_OPEN_DOCUMENT)
        data.addCategory(Intent.CATEGORY_OPENABLE)
        data.type = "*/*"
        val mimeTypes = arrayOf("aplication/pdf", "image/*")
        data.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        data = Intent.createChooser(data, "Choose a file")
        activityResultLauncher.launch(data)
    }

    private fun initComponent() {
        ((requireActivity().application) as DearOApplication)
            .repositoryComponent
            .COMPONENT(AccidentalPresenterModule(this))
            .inject(this)
    }

    companion object {
        const val ID = "ID"
        const val STATUS = "STATUS"
        const val ARG_VIEW_ONLY = "is_view_only"
        private const val REQUEST_CODE_COMPANY_ADDRESS = 1201

        @JvmStatic
        fun newInstance(jobCardId: String, status: String, isViewOnly: Boolean) =
            AccidentalFragment().apply {
                val args = Bundle()
                args.putString(STATUS, status)
                args.putString(ID, jobCardId)
                args.putBoolean(ARG_VIEW_ONLY, isViewOnly)
                arguments = args
            }
    }
}
