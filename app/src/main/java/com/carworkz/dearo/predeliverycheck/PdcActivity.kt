package com.carworkz.dearo.predeliverycheck

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.damage.DamageRecycleAdapter
import com.carworkz.dearo.addjobcard.createjobcard.damage.EditDamageCallback
import com.carworkz.dearo.addjobcard.createjobcard.damage.capturedamage.ClickPictureActivity
import com.carworkz.dearo.base.BaseActivity
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityMainBinding
import com.carworkz.dearo.databinding.ActivityPdcBinding
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.extensions.alert
import com.carworkz.dearo.extensions.find
import com.carworkz.dearo.extensions.toast
import com.carworkz.dearo.interactionprovider.DefaultInteractionProvider
import com.carworkz.dearo.interactionprovider.ToolBarImgInteractionProvider
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.screencontainer.ActionImgScreenContainer
import com.carworkz.dearo.utils.PermissionUtil
import com.carworkz.dearo.utils.Utility
import com.thoughtbot.expandablerecyclerview.listeners.GroupExpandCollapseListener
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
/*import kotlinx.android.synthetic.main.activity_pdc.*
import kotlinx.android.synthetic.main.base_layout.**/
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

const val ARG_JOB_CARD_ID = "arg_jobcard_id"
const val ARG_JOB_CARD_DISPLAY_ID = "arg_jobcard_display_id"

class PdcActivity : ScreenContainerActivity(), DefaultInteractionProvider, PdcContract.View,
    ToolBarImgInteractionProvider, PdcChecklistAdapter.CallBackListener, View.OnClickListener,
    EditDamageCallback {
    private lateinit var binding: ActivityPdcBinding
    @Inject
    lateinit var presenter: PdcPresenter
    @Inject
    lateinit var pdfMediator: PdfMediator
    private lateinit var checklist: List<ChecklistItemPOJO>
    private lateinit var jobCardId: String
    private lateinit var jobCardDisplayId: String
    private lateinit var adapter: PdcChecklistAdapter
    var isPdcCompleted = false
    private lateinit var screenContainer: ActionImgScreenContainer
    private var expandedGroup: ExpandableGroup<*>? = null
    private var unsavedData: Boolean = false
    private lateinit var datePicker: DatePickerDialog
    private lateinit var timePicker: TimePickerDialog

    private var deliveryDateCalendar = Calendar.getInstance()

    private var selectedYear: Int = deliveryDateCalendar.get(Calendar.YEAR)
    private var selectedMonth: Int = deliveryDateCalendar.get(Calendar.MONTH)
    private var selectedDay: Int = deliveryDateCalendar.get(Calendar.DAY_OF_MONTH)
    private var selectedHour: Int = deliveryDateCalendar.get(Calendar.HOUR_OF_DAY)
    private var selectedMinutes: Int = deliveryDateCalendar.get(Calendar.MINUTE)

    override fun onCreate(savedInstanceState: Bundle?) {
        initComponent()
        super.onCreate(savedInstanceState)
        getExtras()
        datePicker = DatePickerDialog(
            this, { _, year, monthOfYear, dayOfMonth ->
                selectedYear = year
                selectedDay = dayOfMonth
                selectedMonth = monthOfYear

                binding.tvEstimateDate.text =
                    Utility.formatDate(Utility.DATE_FORMAT_3, year, monthOfYear, dayOfMonth)
            },
            selectedYear, selectedMonth, selectedDay
        )

        datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
        datePicker.datePicker.maxDate = System.currentTimeMillis() - 1000

        timePicker = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->

                Timber.d("Time", " $selectedHour hours & $selectedMinute minutes")
                this.selectedHour = selectedHour
                this.selectedMinutes = selectedMinute
                  binding.tvEstimateTime.text = Utility.formatTime(selectedHour, selectedMinute)
            },
            selectedHour,
            selectedMinutes,
            false
        )

        binding.tvEstimateDate.text =
            Utility.formatDate(Utility.DATE_FORMAT_3, selectedYear, selectedMonth, selectedDay)
        binding.tvEstimateTime.text = Utility.formatTime(selectedHour, selectedMinutes)
//        ll_estimate_parent_date.setOnClickListener { datePicker.show() }
        binding.llEstimateParentTime.setOnClickListener { timePicker.show() }
        binding.btnCompletePdc.setOnClickListener(this)
        binding.addPdcImage.setOnClickListener(this)
        binding.remarkEt.doOnTextChanged { text, start, before, count ->
            dataChanged(true)
        }
        binding.pdcInspector.doOnTextChanged { text, start, before, count ->
            dataChanged(true)
        }
        binding.startKm.doOnTextChanged { text, start, before, count ->
            dataChanged(true)
        }
        binding.stopKm.doOnTextChanged { text, start, before, count ->
            dataChanged(true)
        }

        //fetch jobcard and check for PDC checklist
        presenter.getEstimation(jobCardId)
    }

    override fun onResume() {
        super.onResume()
        getImages()
    }

    private fun getImages() {
        if (checkIfNetworkAvailable()) {
            binding.pdcDamageView.removeAllViews()
//            presenter.getGalleryData(jobCardId)
            presenter.getPdcData(jobCardId)
        } else {
            toast("No Internet Connection")
        }
    }

    override fun onFetchJobCard(jobCard: JobCard) {


        // check for checklist is available
        // 1. if checklist available show it in list
        expandedGroup = null
        isPdcCompleted = jobCard.isPdcCompleted
        screenContainer.refreshToolBar()
       /* if (isPdcCompleted) {
            //hide complete PDC button
            binding.btnCompletePdc.visibility = View.GONE
            ll_estimate_parent_date.setOnClickListener(null)
            binding.llEstimateParentTime.setOnClickListener(null)
            remarkEt.isEnabled = false
            pdcInspector.isEnabled = false
            startKm.isEnabled = false
            stopKm.isEnabled = false
        }*/

        val isChecklistNotNull = jobCard.preDelivery?.checklist != null
        if (isChecklistNotNull) {
            if(jobCard.preDelivery.inspectionDate!="") {
                deliveryDateCalendar.time = Utility.getDate(jobCard.preDelivery.inspectionDate)
                selectedDay = deliveryDateCalendar.get(Calendar.DAY_OF_MONTH)
                selectedMonth = deliveryDateCalendar.get(Calendar.MONTH)
                selectedYear = deliveryDateCalendar.get(Calendar.YEAR)
                selectedHour = deliveryDateCalendar.get(Calendar.HOUR_OF_DAY)
                selectedMinutes = deliveryDateCalendar.get(Calendar.MINUTE)

                  binding.tvEstimateTime.setText(Utility.formatTime(selectedHour, selectedMinutes))

                binding.tvEstimateDate.setText(
                    Utility.formatDate(
                        jobCard.preDelivery.inspectionDate,
                        Utility.TIMESTAMP,
                        Utility.DATE_FORMAT_4,
                        Utility.TIMEZONE_UTC
                    )
                )
            }


            setRVandData(jobCard.preDelivery?.checklist!!)
            //set data to views
            binding.remarkEt.setText(jobCard.preDelivery.remarks)
            binding.pdcInspector.setText(jobCard.preDelivery.inspectorName)
            binding.startKm.setText(""+jobCard.preDelivery.roadTest.startKms)
            binding.stopKm.setText(""+jobCard.preDelivery.roadTest.endKms)
            unsavedData=false
        } else {
            presenter.getChecklist(jobCardId)
        }
        // 2. if checklist not available fetch for fresh checklist against jobcard id
    }

    override fun onFetchChecklist(list: List<PdcBase>) {
        binding.rvPdcChecklist.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        //create list with pojo which has type
        checklist = getInspectionViewObj(list)
        adapter = PdcChecklistAdapter(this, checklist, this, isPdcCompleted)
        binding.rvPdcChecklist.adapter = adapter

        adapter.setOnGroupExpandCollapseListener(object : GroupExpandCollapseListener {
            override fun onGroupExpanded(group: ExpandableGroup<*>?) {
                if (expandedGroup != null && expandedGroup!! != group) {
                    adapter.toggleGroup(expandedGroup)
                }
                expandedGroup = group
            }

            override fun onGroupCollapsed(group: ExpandableGroup<*>?) {
                if (expandedGroup == group)
                    expandedGroup = null
            }
        })
    }

    fun setRVandData(datList: List<ChecklistItem>) {
        //prepare ChecklistItemPOJO
        checklist = getChecklistGroupBy(datList)
        //final list of ChecklistItemPOJO
        binding.rvPdcChecklist.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        //create list with pojo which has type
        adapter = PdcChecklistAdapter(this, checklist, this, isPdcCompleted)
        binding.rvPdcChecklist.adapter = adapter

        adapter.setOnGroupExpandCollapseListener(object : GroupExpandCollapseListener {
            override fun onGroupExpanded(group: ExpandableGroup<*>?) {
                if (expandedGroup != null && expandedGroup!! != group) {
                    adapter.toggleGroup(expandedGroup)
                }
                expandedGroup = group
            }

            override fun onGroupCollapsed(group: ExpandableGroup<*>?) {
                if (expandedGroup == group)
                    expandedGroup = null
            }
        })
    }

    fun getChecklistGroupBy(datList: List<ChecklistItem>): List<ChecklistItemPOJO> {
        datList.sortedBy { it.group }
        val viewList = ArrayList<ChecklistItemPOJO>()
        val map = datList.groupBy { it.group }
        map.forEach { entry ->
            val list = mutableListOf<ChecklistItem>()
            var listGroup: String? = ""
            entry.value.forEach {
                list.add(ChecklistItem().apply {
                    checked = it.checked
                    comments = it.comments
                    defect = it.defect
                    group = it.group
                    name = it.name
                    listGroup = it.group
                })
            }
            viewList.add(ChecklistItemPOJO(listGroup, list))
        }
        return viewList
    }

    fun getInspectionViewObj(inspectionItems: List<PdcBase>): List<ChecklistItemPOJO> {
        val viewList = ArrayList<ChecklistItemPOJO>()

        inspectionItems.forEach { entry ->
            val list = ArrayList<ChecklistItem>()
            entry.checklist.forEach()
            {
                list.add(ChecklistItem().apply {
                    name = it.name
                    group = entry.group
                })
            }
            viewList.add(ChecklistItemPOJO(entry.group, list))
        }
        return viewList
    }

    override fun onSaveSuccess(print:Boolean) {
        if(print)
        {
            pdfMediator.startJobCardPdcPdf(this, jobCardId, jobCardDisplayId)
        }
        else {
            finish()
        }
    }

    private fun addPdcImage()
    {
        if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            startActivity(ClickPictureActivity.getIntent(this, jobCardId, false, ClickPictureActivity.ACTION_PDC))
        } else {
            PermissionUtil.requestPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.string.permission_save_file) { _, _, grantResults ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(ClickPictureActivity.getIntent(this, jobCardId, false, ClickPictureActivity.ACTION_PDC))
                }
            }
        }
    }

    override fun displayPdcImages(imageList: List<FileObject>) {
        Timber.d("Display Images")
        if (imageList.isEmpty()) {
            binding.pdcDamageView.visibility = View.GONE
        } else {
            binding.pdcDamageView.removeAllViews()
            binding.pdcDamageView.visibility = View.VISIBLE
            inflatePdcImageList(imageList)
        }
    }

    private fun inflatePdcImageList(pdcImageList: List<FileObject>) {
        Timber.d("inflateInspectionList")
        val view = layoutInflater.inflate(R.layout.section_damage_layout, binding.pdcDamageView, false)
        val recyclerView: RecyclerView = view.find(R.id.rv_damage_grid)
        val titleView: TextView = view.find(R.id.tv_title)
        val countView: TextView = view.find(R.id.tv_count)
        val emptyView: TextView = view.find(R.id.tv_empty)
        if (pdcImageList.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
        titleView.text = FileObject.PDC_AND_DAMAGES
        countView.text = String.format(Locale.getDefault(), getString(R.string.damage_template_number_of_photos), pdcImageList.size)
        recyclerView.adapter = DamageRecycleAdapter(pdcImageList as java.util.ArrayList<FileObject>, this, true, FileObject.PDC_AND_DAMAGES)
        binding.pdcDamageView.addView(view)
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

    override fun createScreenContainer(): ScreenContainer {
        screenContainer = ActionImgScreenContainer(this)
        return screenContainer
    }

    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityPdcBinding.inflate(layoutInflater)
        return binding
    }

    override fun getProgressView(): View = ProgressBar(this)
    override fun getNavigationImage(): Int {
        return R.drawable.ic_clear_white_24dp
    }

    override fun onActionBtnClick() {
//        toast("Action button clicked")
        savePdc(false,false)
    }

    override fun onSecondaryActionBtnClick() {
        //save and Print PDC
        savePdc(false,true)
    }

    override fun getActionBarImage(): Int {
        return if (isPdcCompleted) 0 else R.drawable.ic_save_white_24dp
    }

    override fun getSecondaryActionBarImage(): Int =R.drawable.ic_print_white_24dp

    override fun getToolBarTitle(): String = "Pre-Delivery Checklist"

    private fun initComponent() {
        (application as DearOApplication)
            .repositoryComponent
            .COMPONENT(PdcModule(this))
            .inject(this)
    }

    private fun getExtras() {
        jobCardId = intent.extras!!.getString(ARG_JOB_CARD_ID)!!
        jobCardDisplayId = intent.extras!!.getString(ARG_JOB_CARD_DISPLAY_ID)!!
    }

    companion object {
        fun getIntent(context: Context, jobCardId: String,jobCardDisplayId:String): Intent {
            return Intent(context, PdcActivity::class.java).apply {
                putExtra(ARG_JOB_CARD_ID, jobCardId)
                putExtra(ARG_JOB_CARD_DISPLAY_ID, jobCardDisplayId)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item?.itemId == android.R.id.home) {
            promptUserIfDirtyOrExit()
        }
        return true
    }

    private fun promptUserIfDirtyOrExit() {
        if (unsavedData and !isPdcCompleted) {
            alert(getString(R.string.alert_dirty_message), getString(R.string.alert_dirty_title)) {
                positiveButton(getString(R.string.alert_dirty_btn_save)) {
                    //save the data
                    savePdc(false,false)
                }
                negativeButton(getString(R.string.alert_dirty_btn_exit)) {
                    finish()
                }
                neutralPressed(getString(R.string.alert_dirty_btn_cancel)) {
                    it.dismiss()
                }
            }.show()
        } else {
            finish()
        }
    }

    override fun onBackPressed() {
        promptUserIfDirtyOrExit()
    }


    override fun dataChanged(isChanged: Boolean) {
        unsavedData = isChanged
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_complete_pdc -> {
                savePdc(true,false)
            }
            R.id.addPdcImage ->{
                addPdcImage()
            }
        }
    }

    private fun savePdc(isCompleted: Boolean,print:Boolean) {
        Timber.v("ActionButton Click Event")
        val preDeli = PreDelivery()

        //1. create checklist for server first
        val map = adapter.checklist
        val postChecklist = ArrayList<ChecklistItem>()
        map.forEach { entry ->
            entry.items.forEach { checklistItem ->
                postChecklist.add(checklistItem)
            }
        }

        preDeli.checklist = postChecklist
        //2. Remarks
        preDeli.remarks = binding.remarkEt.text.toString()
        //3. Inspector
        preDeli.inspectorName = binding.pdcInspector.text.toString()

        //4. Inspection Date
        deliveryDateCalendar.set(Calendar.YEAR, selectedYear)
        deliveryDateCalendar.set(Calendar.MONTH, selectedMonth)
        deliveryDateCalendar.set(Calendar.DAY_OF_MONTH, selectedDay)
        deliveryDateCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
        deliveryDateCalendar.set(Calendar.MINUTE, selectedMinutes)
        deliveryDateCalendar.set(Calendar.SECOND, 0)

        preDeli.inspectionDate =
            Utility.formatToServerTime(deliveryDateCalendar.time, Utility.DATE_FORMAT_1)
        //5. Start and End KM
        preDeli.roadTest = RoadTest()
        preDeli.roadTest.startKms = if(binding.startKm.text.toString().isNotEmpty()) binding.startKm.text.toString().toInt() else 0
        preDeli.roadTest.endKms = if(binding.stopKm.text.toString().isNotEmpty()) binding.stopKm.text.toString().toInt() else 0
        //6. KM date time
        preDeli.roadTest.datetime =
            Utility.formatToServerTime(deliveryDateCalendar.time, Utility.DATE_FORMAT_1)

        if (checkIfNetworkAvailable()) {
            presenter.saveChecklist(jobCardId, PdcEntity().apply {
                pdcCompleted = isCompleted
                preDelivery = preDeli
            },print)
        } else {
            dismissProgressIndicator()
            Toast.makeText(this, "Check Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onEditCaption(imageId: String, caption: String) {
        if (checkIfNetworkAvailable()) {
            presenter.editCaption(imageId, caption, jobCardId)
        }
    }

    override fun onDeleteImage(fileObject: FileObject) {
        if (checkIfNetworkAvailable()) {
            presenter.deleteDamage(fileObject)
        }
    }

    override fun updateAlert() {
        getImages()
    }
}
