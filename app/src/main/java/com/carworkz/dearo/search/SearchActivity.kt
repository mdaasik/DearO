package com.carworkz.dearo.search

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout

import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.LoggingFacade
import com.carworkz.dearo.MainActivity.Companion.ARG_AMC_SEARCH
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.customercarsearch.CustomerCarSearchActivity
import com.carworkz.dearo.addjobcard.createjobcard.estimate.partnumberselection.PartNumberSelectionAdapter
import com.carworkz.dearo.addjobcard.createjobcard.jobs.viewjc.ViewJCActivity
import com.carworkz.dearo.amc.amcsolddetails.AmcSoldDetailsActivity
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.appointments.reschedule.RescheduleActivity
import com.carworkz.dearo.base.BaseActivity
import com.carworkz.dearo.base.DialogFactory
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.cardslisting.AMCInteractionProvider
import com.carworkz.dearo.cardslisting.CardListingFragment
import com.carworkz.dearo.cardslisting.CardListingInteractionProvider
import com.carworkz.dearo.cardslisting.adapters.AmcCardListingAdapter
import com.carworkz.dearo.cardslisting.adapters.InvoiceListingAdapter
import com.carworkz.dearo.cardslisting.adapters.JobCardListingAdapter
import com.carworkz.dearo.cardslisting.adapters.OtcListingAdapter
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.databinding.ActivityMainBinding
import com.carworkz.dearo.databinding.ActivitySearchJobsBinding
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.events.CancelEvent
import com.carworkz.dearo.events.ChangeCardStatusEvent
import com.carworkz.dearo.extensions.*
import com.carworkz.dearo.mycustomers.cutomervehiclehistory.CustomerVehicleHistoryActivity
import com.carworkz.dearo.mycustomers.mycustomerlisting.MyCustomerAdapter
import com.carworkz.dearo.outwarding.OutwardingProcessActivity
import com.carworkz.dearo.outwarding.OutwardingProcessActivity.Companion.SCREEN_TYPE_PROFORMA
import com.carworkz.dearo.partpayment.PartPaymentActivity
import com.carworkz.dearo.pdf.Source
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.serviceremainder.NewServiceReminderActivity
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.activity_search_jobs.*
import kotlinx.android.synthetic.main.layout_full_width_button.**/
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import javax.inject.Inject

class SearchActivity : BaseActivity(), SearchContract.View, CardListingInteractionProvider, EventsManager.EventSubscriber, MyCustomerAdapter.Interaction, MultiSelectSearchAdapter.OnItemAddedListener, View.OnClickListener,
    AMCInteractionProvider {
    private lateinit var binding: ActivitySearchJobsBinding
 /*   private lateinit var binding.searchRv: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var binding.etSearch: EditText*/
    private lateinit var invoiceList: List<Invoice>
    private lateinit var amcList: List<AMC>
    private lateinit var jobList: List<JobCard>
    private var list: ArrayList<RecommendedJob>? = null
    private var partList: ArrayList<Part>? = null
    private var labourList: ArrayList<Labour>? = null
    private lateinit var isFrom: String
  /*  private lateinit var binding.llEmptyView: LinearLayout
    private lateinit var binding.tvEmptyListView: TextView*/
    private lateinit var jobCardId: String
    private lateinit var invoiceId: String
   /* private lateinit var binding.pbMain: binding.pbMain*/

    private var vehicleType: String? = null

    private var multiSelectAdapter: MultiSelectSearchAdapter<*>? = null

    @Inject
    lateinit var presenter: SearchPresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    @Inject
    lateinit var pdfMediator: PdfMediator

    private var vehicleAmcId:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchJobsBinding.inflate(layoutInflater)
        setContentView(binding.root)
       /* setContentView(R.layout.activity_search_jobs)
        binding.pbMain = find(R.id.pb_main)*/
        // tes comment
        setupComponent()
        getIntentData()
        setupToolbar()
        initViews()
        EventsManager.register(this)
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
        EventsManager.unregister(this)
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        super.onBackPressed()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.selectedButtonStubView.addToJobView -> {
                multiSelectAdapter?.let { nonNullAdapter ->
                    Intent(Intent.ACTION_ATTACH_DATA).apply {
                        when (isFrom) {
                            ARG_JOB_SEARCH -> {
                                val arrayList = arrayListOf<RecommendedJob>()
                                arrayList.addAll(nonNullAdapter.selectedItems.map { it as RecommendedJob }.toList())
                                putParcelableArrayListExtra(RecommendedJob.TAG, arrayList)
                            }
                            ARG_PART_SEARCH -> {
                                val arrayList = arrayListOf<Part>()
                                arrayList.addAll(nonNullAdapter.selectedItems.map { it as Part }.toList())
                                putParcelableArrayListExtra(Part.TAG, arrayList)
                            }
                            ARG_LABOUR_SEARCH -> {
                                val arrayList = arrayListOf<Labour>()
                                arrayList.addAll(nonNullAdapter.selectedItems.asSequence().map { it as Labour }.toList())
                                putParcelableArrayListExtra(Labour.TAG, arrayList)
                            }
                            else -> {
                                UnsupportedOperationException("addToJobView clicked with unsupported isfrom $isFrom")
                            }
                        }
                        setResult(Activity.RESULT_OK, this)
                        finish()
                    }
                } ?: run {
                    LoggingFacade.log(IllegalStateException("addToJobView clicked when adapter is null, Strange!! isfrom $isFrom"))
                }
            }
        }
    }

    override fun callCreateJobCard(appointment: Appointment) = Unit

    override fun getCardType(): String {
        // when searching appointment,return with exact type of the card idView.e Appointment past
        return ""
    }

    override fun callCloseJobCard(jobCardId: String) {
        Timber.d("Close JC")
        startActivity(NewServiceReminderActivity.getIntent(this, jobCardId, null, NewServiceReminderActivity.ACTION_CLOSE))
    }

    override fun startEditProformaActivity(invoice: Invoice, jobCardId: String?, displayJcId: String?, invoiceId: String?, displayInvoiceId: String?, splitInvoice: Boolean, vehicleType: String?,jcType:String, requestCode: Int) {
        startActivity(OutwardingProcessActivity.getProformaIntent(this, SCREEN_TYPE_PROFORMA, displayJcId, displayInvoiceId, jobCardId, invoiceId, splitInvoice, vehicleType, invoice, jcType))
    }

    override fun callCancelJobCard(jobCardId: String) {
    }

    override fun launchWhatsapp(contactNumber: String, message: String) {
        Utility.sendWhatsApp(this, contactNumber, message)
    }

    override fun callCompleteJobCard(jobCard: JobCard) {
        if (checkIfNetworkAvailable()) {
            DialogFactory.notifyAlertDialog(this, getString(R.string.job_complete), getString(R.string.job_complete_message), SharedPrefHelper.isNotifyOnCompleteJC() && SharedPrefHelper.isNotifyEnabled(), SharedPrefHelper.getDefaultOptionCompleteJC(), true, object : DialogFactory.NotifyButtonListener {
                override fun positiveButton(notify: Boolean?) {
                    presenter.completeJobCard(jobCard.id, notify ?: false)
                }

                override fun neutralButton() {
                    startActivity(ViewJCActivity.getViewJcIntent(this@SearchActivity, jobCard.id, jobCard.jobCardId, false, false, jobCard.vehicleType))
                }
            }).show()
        }
    }

    override fun acceptAppointment(appointment: Appointment) {
        presenter.acceptAppointment(appointment.appointmentId!!)
    }

    override fun updateLeadStatus(appointment: Appointment) {

    }

    override fun rescheduleAppointment(appointmentId: String) {
        startActivity<RescheduleActivity>(RescheduleActivity.ARG_ID to appointmentId, RescheduleActivity.ARG_TYPE to RescheduleActivity.ARG_IS_RESCHEDULE)
    }

    override fun startActivity(card: CustomerVehicleDetails) {
        startActivity(CustomerVehicleHistoryActivity.getIntent(this, card))
    }

    override fun callUpdatePayment(invoiceId: String, displayInvoiceId: String, jobCardId: String) {
        Timber.d("update payment")
        startActivity(PartPaymentActivity.getIntent(this, invoiceId, displayInvoiceId, jobCardId))
    }

    override fun startOtcProformaActivity(invoiceId: String, displayInvoiceId: String, vehicleType: String?) {
    }

    override fun startInvoicePreview(invoice: Invoice, jobCardId: String, source: Source) {
        pdfMediator.startInvoicePreviewPdf(this, invoice, jobCardId, source)
    }

    override fun startOtcInvoicePreview(invoice: Invoice) {
    }

    override fun startJobCardDetailsPreview(jobCardId: String, displayId: String, source: Source) {
        pdfMediator.startJobCardDetailsPdf(this, jobCardId, displayId, source)
    }

    override fun getAmcId(): String? {
        return vehicleAmcId
    }

    override fun getJobCardById(id: String) {

    }

    override fun startProformaPdf(invoice: Invoice, jobCardId: String, source: Source) {
        pdfMediator.startProformaPdf(this, invoice, jobCardId, source)
    }

    override fun refresh() {
        when (isFrom) {
            ARG_JOB_SEARCH -> presenter.searchJobs(binding.etSearch.text.toString(), vehicleType)
            ARG_PART_SEARCH -> presenter.searchParts(binding.etSearch.text.toString(), vehicleType,vehicleAmcId)
            ARG_PART_FINDER_SEARCH -> presenter.searchPartNumber(binding.etSearch.text.toString(), vehicleType)
            ARG_LABOUR_SEARCH -> presenter.searchLabours(binding.etSearch.text.toString(), jobCardId, vehicleType,vehicleAmcId)
            ARG_OFFER_SEARCH -> presenter.searchJobs(binding.etSearch.text.toString(), vehicleType)
            ARG_JOB_CARD_SEARCH -> if (binding.etSearch.text.toString().isNotEmpty()) {
                presenter.searchJobCards(binding.etSearch.text.toString(), ARG_JOBCARD)
            }
            ARG_INVOICE_SEARCH -> if (binding.etSearch.text.toString().isNotEmpty()) {
                presenter.searchInvoiceCards(binding.etSearch.text.toString(), ARG_INVOICES, ARG_INVOICE)
            }
            ARG_OTC_SEARCH -> if (binding.etSearch.text.toString().isNotEmpty()) {
                presenter.searchInvoiceCards(binding.etSearch.text.toString(), ARG_INVOICES, ARG_INVOICE)
            }
            ARG_CUSTOMER_SEARCH -> if (binding.etSearch.text.toString().isNotEmpty()) {
                presenter.searchMyCustomers(binding.etSearch.text.toString())
            }
            ARG_AMC_SEARCH ->if (binding.etSearch.text.toString().isNotEmpty()) {
                presenter.searchAMC(binding.etSearch.text.toString())
            }
        }
    }

    @SuppressLint("StringFormatMatches")
    override fun displayJobs(jobsList: List<RecommendedJob>) {
        this.list?.let {
            it.clear()
            it.addAll(jobsList)
        } ?: run {
            list = jobsList as ArrayList<RecommendedJob>
        }

        if (getQuery().isNotEmpty()) {
            val addItem = RecommendedJob()
            addItem.vehicleType = vehicleType
            addItem.text = getQuery()
            this.list!!.add(0, addItem)
        }
        multiSelectAdapter?.let { adapter ->
            adapter.selectedItems.forEach { selectedJob ->

                if (selectedJob !is RecommendedJob || list!!.find { it.id == selectedJob.id && it.text == selectedJob.text } != null) {
                    return
                }
                list!!.add(selectedJob)
            }
            adapter.notifyDataSetChanged()
        } ?: run {
            binding.searchRv.visibility = VISIBLE
            multiSelectAdapter = MultiSelectSearchAdapter(this, vehicleType, list!!, this)
            binding.searchRv.adapter = multiSelectAdapter
            binding.selectedButtonStubView.selectedJobTextView.text = String.format(getString(R.string.search_screen_selected_items, 0))
        }
    }

    @SuppressLint("StringFormatMatches")
    override fun displayParts(list: List<Part>) {
        this.partList?.let {
            it.clear()
            it.addAll(list)
        } ?: run {
            partList = list as ArrayList<Part>
        }

        if (getQuery().isNotEmpty() && SharedPrefHelper.isAddCustomPartEnabled()) {
            val addItem = Part()
            addItem.vehicleType = vehicleType
            addItem.text = getQuery()
            this.partList!!.add(0, addItem)
        }

        multiSelectAdapter?.let { adapter ->
            adapter.selectedItems.forEach { selectedPart ->
                if (selectedPart !is Part || partList!!.find { it.id == selectedPart.id && it.text == selectedPart.text } != null) {
                    return
                }
                partList!!.add(selectedPart)
            }
            adapter.notifyDataSetChanged()
        } ?: run {
            binding.searchRv.visibility = VISIBLE
            multiSelectAdapter = MultiSelectSearchAdapter(this, vehicleType, partList!!, this)
            binding.searchRv.adapter = multiSelectAdapter
             binding.selectedButtonStubView.selectedJobTextView.text = String.format(getString(R.string.search_screen_selected_items, 0))
        }
    }

    @SuppressLint("StringFormatMatches")
    override fun displayLabours(list: List<Labour>) {
        this.labourList?.let {
            it.clear()
            it.addAll(list)
        } ?: run {
            labourList = list as ArrayList<Labour>
        }

        if (getQuery().isNotEmpty() && SharedPrefHelper.isCustomLabourEnabled()) {
            val addItem = Labour()
            addItem.vehicleType = vehicleType
            addItem.text = getQuery()
            this.labourList!!.add(0, addItem)
        }

        multiSelectAdapter?.let { adapter ->
            adapter.selectedItems.forEach { selectedLabour ->

                if (selectedLabour !is Labour || labourList!!.find { it.id == selectedLabour.id && it.text == selectedLabour.text } != null)
                    return

                labourList!!.add(selectedLabour)
            }

            adapter.notifyDataSetChanged()
        } ?: run {
            binding.searchRv.visibility = VISIBLE
            multiSelectAdapter = MultiSelectSearchAdapter(this, vehicleType, labourList!!, this)
            binding.searchRv.adapter = multiSelectAdapter
             binding.selectedButtonStubView.selectedJobTextView.text = String.format(getString(R.string.search_screen_selected_items, 0))
        }
    }

    override fun displayPartNumber(list: List<PartNumber>) {
        binding.searchRv.visibility = VISIBLE
        binding.searchRv.adapter = PartNumberSelectionAdapter(list, false)
    }

    override fun displayJobCards(list: List<JobCard>) {
        this.jobList = list as ArrayList<JobCard>
        if (list.isNotEmpty()) {
            if (binding.etSearch.text.toString().isNotEmpty()) {
                binding.searchRv.visibility = VISIBLE
                binding.llEmptyView.visibility = GONE
                binding.searchRv.adapter = JobCardListingAdapter(this, list, this)
            }
        } else {
            binding.tvEmptyListView.text = getString(R.string.no_job_card_found)
            binding.llEmptyView.visibility = VISIBLE
            binding.searchRv.visibility = GONE
        }
    }

    override fun displayInvoiceCards(list: List<Invoice>) {
        this.invoiceList = list as ArrayList<Invoice>
        if (list.isNotEmpty()) {
            if (binding.etSearch.text.toString().isNotEmpty()) {
                binding.searchRv.visibility = VISIBLE
                binding.llEmptyView.visibility = GONE
                binding.searchRv.adapter = InvoiceListingAdapter(list, this)
            }
        } else {
            binding.tvEmptyListView.text = getString(R.string.no_invoice_found)
            binding.llEmptyView.visibility = VISIBLE
            binding.searchRv.visibility = GONE
        }
    }

    override fun displayOtcCards(obj: List<Invoice>) {
        invoiceList = obj as ArrayList<Invoice>
        if (obj.isNotEmpty()) {
            if (binding.etSearch.text.toString().isNotEmpty()) {
                binding.searchRv.visibility = VISIBLE
                binding.llEmptyView.visibility = GONE
                binding.searchRv.adapter = OtcListingAdapter(obj, this)
            }
        } else {
            binding.tvEmptyListView.text = getString(R.string.no_cards_found)
            binding.llEmptyView.visibility = VISIBLE
            binding.searchRv.visibility = GONE
        }
    }

    override fun displayAMC(obj: List<AMC>) {
        amcList=obj as ArrayList<AMC>
        if (obj.isNotEmpty()) {
            if (binding.etSearch.text.toString().isNotEmpty()) {
                binding.searchRv.visibility = VISIBLE
                binding.llEmptyView.visibility = GONE
                binding.searchRv.adapter = AmcCardListingAdapter(this, obj.toMutableList(), this)
            }
        } else {
            binding.tvEmptyListView.text = getString(R.string.no_cards_found)
            binding.llEmptyView.visibility = VISIBLE
            binding.searchRv.visibility = GONE
        }
    }

    override fun displayMyCustomers(list: List<CustomerVehicleDetails>) {
        if (list.isNotEmpty()) {
            if (binding.etSearch.text.toString().isNotEmpty()) {
                binding.searchRv.visibility = VISIBLE
                binding.llEmptyView.visibility = GONE
                binding.searchRv.adapter = MyCustomerAdapter(list.toMutableList(), this)
            }
        } else {
            binding.tvEmptyListView.text = getString(R.string.no_cutomer_found)
            binding.llEmptyView.visibility = VISIBLE
            binding.searchRv.visibility = GONE
        }
    }

    @SuppressLint("StringFormatMatches")
    override fun onItemAdded(itemSize: Int) {
         binding.selectedButtonStubView.selectedJobTextView.text = String.format(getString(R.string.search_screen_selected_items, itemSize))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
        return false
    }

    override fun getProgressView(): View {
        return binding.pbMain
    }

    override fun showProgressIndicator() {
        binding.pbMain.visibility = VISIBLE
    }

    override fun dismissProgressIndicator() {
        binding.pbMain.visibility = GONE
    }

    override fun showGenericError(errorMsg: String) = displayError(errorMsg) { _, _ -> finish() }

    fun getQuery(): String = binding.etSearch.text.toString().trim()

    @Subscribe
    fun onJobCardCloseEvent(event: ChangeCardStatusEvent) {
        if (event.cardStatus == JobCard.STATUS_CLOSED)
            callCloseJobCard(event.cardId)
    }

    @Subscribe
    fun cancelEvent(cancelEvent: CancelEvent) {
        when (cancelEvent.type) {
            CancelEvent.AMC->{
                //show dialog
                alert(getString(R.string.cancel_amc_warning), getString(R.string.are_u_sure)) {
                    positiveButton("AGREE") {
                        val intent= Intent(baseContext,RescheduleActivity::class.java)
                        intent.putExtra(RescheduleActivity.ARG_ID,cancelEvent.id)
                        intent.putExtra(RescheduleActivity.ARG_TYPE,RescheduleActivity.ARG_IS_CANCEL_AMC)
                        startActivity(intent)
                    }
                    negativeButton("DISAGREE") { it.dismiss() }
                }.show()
            }

            CancelEvent.INVOICE -> {
                alert(getString(R.string.cancel_invoice_warning), getString(R.string.are_u_sure)) {
                    positiveButton("AGREE") {
                        startActivity<RescheduleActivity>(CardListingFragment.ARG_ID to cancelEvent.id, RescheduleActivity.ARG_TYPE to RescheduleActivity.ARG_IS_CANCEL_INVOICE)
                    }
                    negativeButton("DISAGREE") { it.dismiss() }
                }.show()
            }
            CancelEvent.JOBCARD -> {
                alert(getString(R.string.search_screen_alert_cancel_jc_message), getString(R.string.search_screen_alert_cancel_jc_title)) {
                    positiveButton(getString(R.string.search_screen_alert_positive_button)) {
                        presenter.cancelJobCard(cancelEvent.id)
                    }
                    negativeButton(getString(R.string.search_screen_alert_negative_button)) {
                        Timber.d("Event Jobcard Cancelled dismissing")
                    }
                }.show()
            }
            /*CancelEvent.APPOINTMENT -> {
                presenter.cancelAppointment(cancelEvent.id)
                adapter.removeItemAndRefresh(cancelEvent.id)
                Timber.d("event Appointment Cancelled + $listingType")
            }*/
        }
    }

    private fun setupComponent() {
        (application as DearOApplication)
                .repositoryComponent
                .COMPONENT(SearchPresenterModule(this))
                .inject(this)
    }

    private fun setupToolbar() {
      //  binding.SearchToolbar = find(R.id.Search_toolbar)
        setSupportActionBar(binding.SearchToolbar)
        val actionbar = this.supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
      //  binding.etSearch = binding.SearchToolbar.findViewById(R.id.et_search) as EditText
        binding.etSearch.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_FORCED)
        binding.etSearch.requestFocus()
        when (isFrom) {
            ARG_JOB_SEARCH -> {
                binding.selectedButtonStubView.root.visibility = VISIBLE
                binding.etSearch.hint = "Search Items in Jobs"
                if (checkIfNetworkAvailable())
                    presenter.searchJobs("", vehicleType)
            }
            ARG_PART_SEARCH -> {
                binding.selectedButtonStubView.root.visibility = VISIBLE
                binding.etSearch.hint = "Search Items in Parts"
                if (checkIfNetworkAvailable())
                    presenter.searchParts("", vehicleType,vehicleAmcId)
            }
            ARG_PART_FINDER_SEARCH -> {
                binding.etSearch.hint = "Search Items in Parts"
//                if (checkIfNetworkAvailable())
//                    presenter.searchParts("", jobCardId)
            }
            ARG_LABOUR_SEARCH -> {
                binding.selectedButtonStubView.root.visibility = VISIBLE
                binding.etSearch.hint = "Search Items in Labour"
                if (checkIfNetworkAvailable())
                    presenter.searchLabours("", jobCardId, vehicleType,vehicleAmcId)
            }
            ARG_OFFER_SEARCH -> {
                binding.etSearch.hint = "Search Items in Offers"
                if (checkIfNetworkAvailable())
                    presenter.searchJobs("", vehicleType)
            }
            ARG_AMC_SEARCH -> {
                binding.etSearch.hint="Search Items in AMC/SMC"
                if(checkIfNetworkAvailable())
                {
                    presenter.searchAMC("")
                }
            }
            ARG_JOB_CARD_SEARCH -> binding.etSearch.hint = "Search"
            ARG_INVOICE_SEARCH -> binding.etSearch.hint = "Search"
            ARG_APPOINTMENT_SEARCH -> binding.etSearch.hint = "Search Appointments"
            ARG_CUSTOMER_SEARCH -> binding.etSearch.hint = "Search My Customer"
        }
    }

    private fun initViews() {
       // binding.searchRv = find(R.id.search_rv)
//        binding.searchRv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        //binding.llEmptyView = find(R.id.ll_emptyView)
      //  binding.tvEmptyListView = find(R.id.tv_binding.tvEmptyListView)
        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            if (checkIfNetworkAvailable()) {
                when (isFrom) {
                    ARG_JOB_SEARCH -> presenter.searchJobs(binding.etSearch.text.toString(), vehicleType)
                    ARG_PART_SEARCH -> presenter.searchParts(binding.etSearch.text.toString(), vehicleType,vehicleAmcId )
                    ARG_PART_FINDER_SEARCH -> presenter.searchPartNumber(binding.etSearch.text.toString(), vehicleType)
                    ARG_LABOUR_SEARCH -> presenter.searchLabours(binding.etSearch.text.toString(), jobCardId, vehicleType,vehicleAmcId)
                    ARG_OFFER_SEARCH -> presenter.searchJobs(binding.etSearch.text.toString(), vehicleType)
                    ARG_JOB_CARD_SEARCH -> if (binding.etSearch.text.toString().isNotEmpty()) {
                        presenter.searchJobCards(binding.etSearch.text.toString(), ARG_JOBCARD)
                    }
                    ARG_INVOICE_SEARCH -> if (binding.etSearch.text.toString().isNotEmpty()) {
                        presenter.searchInvoiceCards(binding.etSearch.text.toString(), ARG_INVOICES, ARG_INVOICE)
                    }
                    ARG_CUSTOMER_SEARCH -> if (binding.etSearch.text.toString().isNotEmpty()) {
                        presenter.searchMyCustomers(binding.etSearch.text.toString())
                    }
                    ARG_OTC_SEARCH -> if (binding.etSearch.text.toString().isNotEmpty()) {
                        presenter.searchInvoiceCards(binding.etSearch.text.toString(), ARG_INVOICES, ARG_OTC)
                    }
                    ARG_AMC_SEARCH ->if (binding.etSearch.text.toString().isNotEmpty()) {
                        presenter.searchAMC(binding.etSearch.text.toString())
                    }
                }
            }
        }
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 1500)
                if (s?.toString()?.isNotEmpty() == true) {
                    binding.etSearch.run {
                        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(context, R.drawable.ic_clear_white_24dp), null)
                        setOnTouchListener { _, event ->
                            val drawableRight = 2
                            if (event.rawX >= (binding.etSearch.right - binding.etSearch.compoundDrawables[drawableRight].bounds.width())) {
                                binding.etSearch.text.clear()
                                //  binding.searchRv.visibility = View.GONE
                                // binding.llEmptyView.visibility = View.GONE
                            }
                            false
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        when (isFrom) {
            ARG_JOB_SEARCH -> {
                binding.selectedButtonStubView.addToTitleView.setText(R.string.search_screen_add_to_job)
                binding.selectedButtonStubView.addToJobView.setOnClickListener(this)
            }
            ARG_PART_SEARCH -> {
                binding.selectedButtonStubView.addToTitleView.setText(R.string.search_screen_add_to_part)
                binding.selectedButtonStubView.addToJobView.setOnClickListener(this)
            }
            ARG_LABOUR_SEARCH -> {
                binding.selectedButtonStubView.addToTitleView.setText(R.string.search_screen_add_to_labour)
                binding.selectedButtonStubView.addToJobView.setOnClickListener(this)
            }
        }
    }

    private fun getIntentData() {
        isFrom = intent.extras!!.getString(ARG_TYPE, ARG_JOB_SEARCH)
        vehicleAmcId = intent.extras!!.getString(ARG_VEHICLE_AMC_ID, "")
        vehicleType = intent.extras!!.getString(ARG_VEHICLE_TYPE)
        if (isFrom != ARG_JOB_CARD_SEARCH || isFrom != ARG_INVOICE_SEARCH) {
            invoiceId = intent.extras!!.getString(ARG_INVOICE_ID, "0")
            jobCardId = intent.extras!!.getString(ARG_JOB_ID, "0")
        }
        when (isFrom) {
            ARG_JOB_SEARCH -> screenTracker.sendScreenEvent(this, ScreenTracker.SCREEN_SEARCH_JOBS, this.javaClass.name)
            ARG_PART_SEARCH -> screenTracker.sendScreenEvent(this, ScreenTracker.SCREEN_SEARCH_PARTS, this.javaClass.name)
            ARG_PART_FINDER_SEARCH -> screenTracker.sendScreenEvent(this, ScreenTracker.SCREEN_SEARCH_PART_FINDER, this.javaClass.name)
            ARG_LABOUR_SEARCH -> screenTracker.sendScreenEvent(this, ScreenTracker.SCREEN_SEARCH_LABOUR, this.javaClass.name)
            ARG_JOB_CARD_SEARCH -> screenTracker.sendScreenEvent(this, ScreenTracker.SCREEN_SEARCH_JC, this.javaClass.name)
            ARG_INVOICE_SEARCH -> screenTracker.sendScreenEvent(this, ScreenTracker.SCREEN_SEARCH_INVOICE, this.javaClass.name)
            ARG_OTC_SEARCH -> screenTracker.sendScreenEvent(this, ScreenTracker.SCREEN_SEARCH_INVOICE, this.javaClass.name)
            ARG_CUSTOMER_SEARCH -> screenTracker.sendScreenEvent(this, ScreenTracker.SCREEN_SEARCH_CUSTOMER, this.javaClass.name)
        }
    }

    companion object {
        const val ARG_VEHICLE_AMC_ID = "vehicle-amc-id"
        const val ARG_TYPE = "is_from"
        const val ARG_JOB_SEARCH = "ARG_JOB_SEARCH"
        const val ARG_PART_SEARCH = "ARG_PART_SEARCH"
        const val ARG_PART_FINDER_SEARCH = "ARG_PART_FINDER_SEARCH"
        const val ARG_LABOUR_SEARCH = "ARG_LABOUR_SEARCH"
        const val ARG_OFFER_SEARCH = "ARG_OFFER_SEARCH"
        const val ARG_JOB_CARD_SEARCH = "ARG_JOB_CARD_SEARCH"
        const val ARG_INVOICE_SEARCH = "ARG_INVOICE_SEARCH"
        const val ARG_OTC_SEARCH = "ARG_OTC_SEARCH"
        const val ARG_APPOINTMENT_SEARCH = "ARG_APPOINTMENT_SEARCH"
        const val ARG_INVOICE_ID = "ARG_INVOICE_ID"
        const val ARG_JOB_ID = "ARG_JOB_ID"
        const val ARG_JOBCARD = "JobCards"
        const val ARG_CUSTOMER_SEARCH = "ARG_CUSTOMER_SEARCH"
        const val ARG_INVOICES = "Invoices"
        const val ARG_VEHICLE_TYPE = "arg_vehicle_type"
        const val ARG_AMC_TYPE = "arg_amc_type"

        fun getIntent(context: Context, isFrom: String, vehicleType: String?): Intent {
            return Intent(context, SearchActivity::class.java)
                    .putExtra(ARG_TYPE, isFrom)
                    .putExtra(ARG_VEHICLE_TYPE, vehicleType)
        }

        fun getIntent(context: Context, isFrom: String, jobCardId: String, vehicleType: String?, vehcleAmcId: String?): Intent {
            return Intent(context, SearchActivity::class.java)
                    .putExtra(ARG_TYPE, isFrom)
                    .putExtra(ARG_VEHICLE_AMC_ID, vehcleAmcId)
                    .putExtra(ARG_VEHICLE_TYPE, vehicleType)
                    .putExtra(ARG_JOB_ID, jobCardId)
        }

        const val ARG_OTC = "OTC"
        const val ARG_INVOICE = "JOBCARD" // intentional value change
    }

    override fun initiateJobCard(registrationNumber: String, mobile: String) {
        startActivity(
            CustomerCarSearchActivity.getIntent(
                this,
                registrationNumber,
                mobile,
                null
            )
        )
    }

    override fun startAmcPreview(amc: AMC) {
        startActivity(AmcSoldDetailsActivity.getIntent(this, amc))
    }

    override fun startAMCInvoicePreview(amc: AMC) {
        pdfMediator.startAmcInvoicePdf(this, amc.invoice?.pdf)
    }
}
