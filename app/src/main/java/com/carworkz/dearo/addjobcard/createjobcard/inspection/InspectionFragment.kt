package com.carworkz.dearo.addjobcard.createjobcard.inspection

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.ActionButtonClickEvent
import com.carworkz.dearo.addjobcard.createjobcard.ICreateJobCardInteraction
import com.carworkz.dearo.addjobcard.createjobcard.inspection.pojo.InspectionViewPOJOCreator
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.base.BaseFragment
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.domain.entities.InspectionGroup
import com.carworkz.dearo.domain.entities.InspectionItem
import com.carworkz.dearo.domain.entities.InspectionPostPOJO
import com.thoughtbot.expandablerecyclerview.listeners.GroupExpandCollapseListener
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import javax.inject.Inject

class InspectionFragment : BaseFragment(), InspectionContract.View, AdapterView.OnItemSelectedListener, EventsManager.EventSubscriber {

    private var isViewOnly: Boolean = false
    private lateinit var jobCardId: String
    private lateinit var inspectionRV: RecyclerView
    private lateinit var inspectionSpinnerGrp: Spinner
    private lateinit var inspectionGrpList: List<InspectionGroup>

    private var vehicleType: String? = null

    // private var activityRef: Activity? = null

    private lateinit var adapter: InspectionAdapter
    private lateinit var viewOnlyAdapter: InspectionViewOnlyAdapter

    private var interaction: ICreateJobCardInteraction? = null

    private var expandedGroup: ExpandableGroup<*>? = null

    private lateinit var groupId: String
    private var selectedInspectionItemList: List<InspectionItem>? = null

    private lateinit var inspectionItemList: MutableList<InspectionItem>

    @Inject lateinit var presenter: InspectionPresenter
    @Inject
    lateinit var screenTracker: ScreenTracker
    private var isSpinnerInit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            isViewOnly = arguments!!.getBoolean(ARG_IS_VIEW_ONLY)
            jobCardId = arguments!!.getString(ARG_JOB_CARD_ID)!!
            vehicleType = arguments!!.getString(ARG_VEHICLE_TYPE)
        }
        ((requireActivity().application) as DearOApplication).repositoryComponent
                .COMPONENT(InspectionPresenterModule(this, isViewOnly))
                .inject(this)
        screenTracker.sendScreenEvent(activity, if (isViewOnly) ScreenTracker.SCREEN_VIEW_INSPECTION else ScreenTracker.SCREEN_INSPECTION, this.javaClass.name)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_inspection, container, false)
        inspectionRV = view.findViewById(R.id.rv_inspection) as RecyclerView
        inspectionRV.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        initSpinner(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (checkIfNetworkAvailable()) {
            if (!isViewOnly)
                presenter.getInspectionGroups(vehicleType)
            else {
                presenter.getSavedInspection(jobCardId)
            }
        } else {
            dismissProgressIndicator()
            Toast.makeText(activity, "Check Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ICreateJobCardInteraction) {
            interaction = context
        } else
            throw IllegalStateException("Activity must implement ICreateJobCardInteraction interface ")

        // activityRef = context as Activity
    }

    override fun onResume() {
        super.onResume()
        EventsManager.register(this)
    }

    override fun onStop() {
        super.onStop()
        EventsManager.unregister(this)
    }

    override fun onDetach() {
        super.onDetach()
        interaction = null
        presenter.detachView()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (isSpinnerInit) {
            groupId = inspectionGrpList[position].id
            if (checkIfNetworkAvailable()) {
                presenter.getInspectionItemByGroup(groupId)
            } else {
                Toast.makeText(activity, getString(R.string.check_internet), Toast.LENGTH_SHORT).show()
            }
        } else {
            isSpinnerInit = true
        }
    }

    override fun displayInspectionItems(items: List<InspectionItem>) {
        inspectionItemList = items.toMutableList()
        if (selectedInspectionItemList != null) {
            selectedInspectionItemList?.forEach { i ->
                inspectionItemList.filter { it.id == i.id }.map { it.condition = i.condition }
            }
        }
        val list = InspectionViewPOJOCreator.getInspectionViewObj(inspectionItemList)
        adapter = InspectionAdapter(activity, list)
        if (selectedInspectionItemList != null) {
            /*update selected adapterList in adapter*/
            selectedInspectionItemList?.forEach {
                adapter.selectedInspection[it.id] = it.condition
            }
        }
        inspectionRV.adapter = adapter

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
        }
        )
    }

    override fun moveToNextScreen() {
        interaction?.onJobSuccess()
    }

    override fun displaySelectedInspectionItems(selectedGroupId: String?, selectedItems: List<InspectionItem>?) {
        if (selectedGroupId != null) {
            groupId = selectedGroupId
            selectedInspectionItemList = selectedItems
            val pos = inspectionGrpList.indexOf(inspectionGrpList.find { it.id == groupId })
            inspectionSpinnerGrp.setSelection(pos)
            presenter.getInspectionItemByGroup(groupId)
        } else {
            presenter.getInspectionItemByGroup(groupId)
        }
    }

    override fun showProgressIndicator() {
        //  if (activityRef != null)
        (activity as ScreenContainerActivity).showProgressBar()
    }

    override fun dismissProgressIndicator() {
        // if (activityRef != null)
        (activity as ScreenContainerActivity).dismissProgressBar()
    }

    override fun displayInspectionGroups(groups: List<InspectionGroup>) {
        inspectionGrpList = groups
        val groupName = mutableListOf<String>()
        inspectionGrpList.forEach {
            groupName.add(it.name)
        }

        val dataAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, groupName)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        inspectionSpinnerGrp.adapter = dataAdapter

        expandedGroup = null
        groupId = inspectionGrpList[0].id
        presenter.getSavedInspection(jobCardId)
    }

    override fun showGenericError(errorMsg: String) {
        interaction?.onJobFailure()
        displayError(errorMsg)
    }

    override fun displayViewOnly(selectedGroupId: String?, selectedItems: List<InspectionItem>?) {
        inspectionSpinnerGrp.visibility = View.GONE
        viewOnlyAdapter = InspectionViewOnlyAdapter(activity, selectedItems)
        inspectionRV.adapter = viewOnlyAdapter
    }

    @Subscribe
    fun onNextBtnClick(actionBtn: ActionButtonClickEvent) {
        Timber.v("ActionButton Click Event" + actionBtn.hashCode())
        if (isViewOnly) {
            interaction?.onJobSuccess()
            return
        }
        val map = adapter.selectedInspection
        val postInspectionList = ArrayList<InspectionItem>()
        map.forEach { entry ->
            val postInspectionItem = InspectionItem()
            postInspectionItem.id = entry.key
            postInspectionItem.condition = entry.value

            val inspectionItem = inspectionItemList.find { it.id == postInspectionItem.id }

            if (entry.value == "poor") {
                postInspectionItem.suggestedJobs = inspectionItem?.inspectionJobs?.poor
            }

            if (entry.value == "average") {
                postInspectionItem.suggestedJobs = inspectionItem?.inspectionJobs?.average
            }

            if (entry.value == "good") {
                postInspectionItem.suggestedJobs = inspectionItem?.inspectionJobs?.good
            }

            postInspectionList.add(postInspectionItem)
        }

        val postInspectionPOJO = InspectionPostPOJO()
        postInspectionPOJO.inspectionItems = postInspectionList
        postInspectionPOJO.inspectionGroup = groupId
        if (checkIfNetworkAvailable()) {
            presenter.saveInspection(jobCardId, postInspectionPOJO)
        } else {
            dismissProgressIndicator()
            Toast.makeText(activity, "Check Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initSpinner(view: View) {
        inspectionSpinnerGrp = view.findViewById(R.id.spinner_inspection) as Spinner
        inspectionSpinnerGrp.onItemSelectedListener = this
    }

    companion object {

        private const val ARG_IS_VIEW_ONLY = "arg_is_view_only"
        private const val ARG_JOB_CARD_ID = "arg_job_card_id"
        private const val ARG_VEHICLE_TYPE = "arg_vehicle_type"

        @JvmStatic
        fun newInstance(isViewOnly: Boolean, jobCardId: String, vehicleType: String?): InspectionFragment {
            val fragment = InspectionFragment()
            val args = Bundle()
            args.putBoolean(this.ARG_IS_VIEW_ONLY, isViewOnly)
            args.putString(this.ARG_JOB_CARD_ID, jobCardId)
            args.putString(this.ARG_VEHICLE_TYPE, vehicleType)
            fragment.arguments = args
            return fragment
        }
    }
}
