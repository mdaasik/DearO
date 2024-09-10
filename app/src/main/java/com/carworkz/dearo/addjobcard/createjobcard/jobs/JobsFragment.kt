package com.carworkz.dearo.addjobcard.createjobcard.jobs

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.ActionButtonClickEvent
import com.carworkz.dearo.addjobcard.createjobcard.ICreateJobCardInteraction
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.base.BaseActivity
import com.carworkz.dearo.base.BaseFragment
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.databinding.FragmentJobsBinding
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.extensions.find
import com.carworkz.dearo.extensions.padding
import com.carworkz.dearo.extensions.textColor
import com.carworkz.dearo.extensions.toast
import com.carworkz.dearo.search.SearchActivity
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.fragment_view_jc.**/
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.text.*

class JobsFragment : BaseFragment(), JobContract.View, SelectedJobListProvider, EventsManager.EventSubscriber {
    private lateinit var binding: FragmentJobsBinding
    private var isViewOnly: Boolean = false
    private lateinit var jobCardId: String

    private var vehicleType: String? = null

    private lateinit var pmsNext: ImageView
    private lateinit var pmsPrev: ImageView
    private lateinit var pmsView: TextView
    private lateinit var pmsParentView: LinearLayout

    private lateinit var voiceView: TextView
    private lateinit var voiceParentView: CardView

    private lateinit var demandedContainerView: LinearLayout
    private lateinit var addItemView: TextView

    private lateinit var regServiceParentView: LinearLayout
    private lateinit var regServiceCbView: CheckBox
    private lateinit var regServiceRV: RecyclerView

    private lateinit var recommendedParentView: CardView
    private lateinit var recommendedRV: RecyclerView

    private var regServiceList: List<RegularService>? = null

    private var recommendedJobsAdapter: RecommendedJobAdapter? = null
    private var demandedJobsAdapter: DemandedRepairAdapter? = null

    private var selectedDemandedRepairs: MutableList<RecommendedJob> = ArrayList()

    private var currentRegService: RegularService? = null
    private var currentRegIndex: Int = 0

    private var interaction: ICreateJobCardInteraction? = null

    private lateinit var servicePackageView: LinearLayout
    private lateinit var remarksParentView: CardView
    private lateinit var serviceParentView: LinearLayout
    private lateinit var titleParams: LinearLayout.LayoutParams

    @Inject
    lateinit var presenter: JobsPresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            isViewOnly = requireArguments().getBoolean(ARG_IS_VIEW_ONLY)
            jobCardId = requireArguments().getString(ARG_JOB_CARD_ID)!!
            vehicleType = requireArguments().getString(ARG_VEHICLE_TYPE)
        }

        (activity?.application as DearOApplication)
                .repositoryComponent
                .COMPONENT(JobPresenterModule(this))
                .inject(this)

        screenTracker.sendScreenEvent(activity, if (isViewOnly) ScreenTracker.SCREEN_VIEW_JOBS else ScreenTracker.SCREEN_JOBS, this.javaClass.name)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentJobsBinding.inflate(inflater, container, false)
        return binding.root
        /*return inflater.inflate(R.layout.fragment_jobs, container, false)*/
    }

    @SuppressLint("StringFormatMatches")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // pmsPrev = find(R.id.tv_minus_pms) as ImageView
        pmsPrev = binding.tvMinusPms
      //  pmsNext = find(R.id.tv_plus_pms) as ImageView
        pmsNext = binding.tvPlusPms
     //   pmsView = find(R.id.tv_pms) as TextView
        pmsView =binding.tvPms
       // pmsParentView = find(R.id.ll_pms_parent) as LinearLayout
        pmsParentView = binding.llPmsParent
       // remarksParentView = find(R.id.ll_remark_parent)
        remarksParentView = binding.llRemarkParent

       // voiceParentView = find(R.id.cd_job_voice) as CardView
        voiceParentView = binding.cdJobVoice
       // voiceView = find(R.id.tv_job_voice) as TextView
        voiceView =binding.tvJobVoice

       // addItemView = find(R.id.tv_job_add_item) as TextView
        addItemView = binding.tvJobAddItem
       // demandedContainerView = find(R.id.ll_job_container) as LinearLayout
        demandedContainerView = binding.llJobContainer

       // regServiceParentView = find(R.id.ll_reg_service_parent) as LinearLayout
        regServiceParentView = binding.llRegServiceParent
       // regServiceCbView = find(R.id.cb_regular_service) as CheckBox
        regServiceCbView = binding.cbRegularService
       // regServiceRV = find(R.id.rv_regular_service) as RecyclerView
        regServiceRV = binding.rvRegularService
        regServiceRV.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

      //  serviceParentView = find(R.id.serviceParent)
        serviceParentView = binding.serviceParent
      //  servicePackageView = find(R.id.ll_service_packages)
        servicePackageView = binding.llServicePackages
        titleParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f)
        titleParams.setMargins(8, 8, 8, 8)
      //  recommendedParentView = find(R.id.cd_recommend_parent) as CardView
        recommendedParentView = binding.cdRecommendParent
       // recommendedRV = find(R.id.rv_job_recommended) as RecyclerView
        recommendedRV = binding.rvJobRecommended
        recommendedRV.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        if (checkIfNetworkAvailable()) {
            presenter.getJobsAndVerbatim(jobCardId)
            regServiceCbView.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    regServiceRV.visibility = View.VISIBLE
                    pmsParentView.visibility = View.VISIBLE
                    currentRegService = regServiceList?.find { it.isDefault == true } ?: regServiceList?.get(0)
                    currentRegIndex = Utility.findIndexFromList(currentRegService, regServiceList)
                    pmsView.text = String.format(Locale.getDefault(), getString(R.string.job_screen_template_kms_read, currentRegService?.kms))
                    setDemandedRepairsList()
                } else {
                    pmsView.text = ""
                    regServiceRV.visibility = View.GONE
                    pmsParentView.visibility = View.GONE
                    regServiceRV.adapter = null
                    currentRegService = null
                }
            }
        }

        addItemView.setOnClickListener {
            startActivityForResult(SearchActivity.getIntent(requireContext(), SearchActivity.ARG_JOB_SEARCH, vehicleType), REQUEST_CODE_JOBS_SELECT)
//            startActivityForResult(Intent(activity, SearchActivity::class.java).apply {
//                putExtra(SearchActivity.ARG_TYPE, SearchActivity.ARG_JOB_SEARCH)
//            }, 20)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ICreateJobCardInteraction) {
            interaction = context
        } else
            throw IllegalStateException("Activity must implement ICreateJobCardInteraction interface ")
    }

    override fun onResume() {
        super.onResume()
        EventsManager.register(this)
    }

    override fun onDetach() {
        super.onDetach()
        presenter.detachView()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventsManager.unregister(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_JOBS_SELECT -> {
                val selectedJobs = data?.getParcelableArrayListExtra<RecommendedJob>(RecommendedJob.TAG)
                selectedJobs?.forEach { recommendedJob ->
                    var isPresent = false
                    recommendedJobsAdapter?.let { nonNullAdapter ->
                        val existingList = nonNullAdapter.recommendedList
                        existingList.forEach { existingJob ->
                            if (existingJob.id == recommendedJob.id || existingJob.text == recommendedJob.text) {
                                isPresent = true
                                toast(getString(R.string.already_selected))
                            }
                        }
                    }

                    if (!isPresent && selectedDemandedRepairs.find { it.text == recommendedJob.text } == null) {
                        selectedDemandedRepairs.add(recommendedJob)
                        recommendedJob.text?.let { createNewTextView(it) }
                    }
                }
            }
        }
    }

    @SuppressLint("StringFormatMatches")
    override fun displayJobsData(jobAndVerbatim: JobAndVerbatim) {
        if (jobAndVerbatim.verbatim == null || jobAndVerbatim.verbatim.isEmpty())
            voiceParentView.visibility = View.GONE
        else {
            voiceParentView.visibility = View.VISIBLE
            val voice = jobAndVerbatim.verbatim?.joinToString(separator = ", ")
            voiceView.text = voice
        }

        if (jobAndVerbatim.regularServices.isNotEmpty()) {
            regServiceList = jobAndVerbatim.regularServices

            pmsPrev.setOnClickListener {
                if (currentRegIndex > 0) {
                    currentRegIndex--
                    currentRegService = regServiceList?.get(currentRegIndex)
                    pmsView.text = String.format(Locale.getDefault(), getString(R.string.job_screen_template_kms_read, currentRegService?.kms))
                    setDemandedRepairsList()
                }
            }

            pmsNext.setOnClickListener {
                if (currentRegIndex < (regServiceList?.size ?: 1) - 1) {
                    Timber.d("current index before add $currentRegIndex list size " + ((regServiceList?.size
                            ?: 1) - 2))
                    currentRegIndex++
                    Timber.d("current index after add $currentRegIndex list size " + ((regServiceList?.size
                            ?: 1) - 2))
                    currentRegService = regServiceList?.get(currentRegIndex)
                    pmsView.text = String.format(Locale.getDefault(), getString(R.string.job_screen_template_kms_read, currentRegService?.kms))
                    setDemandedRepairsList()
                }
            }
        } else {
            regServiceParentView.visibility = View.GONE
        }

        if (jobAndVerbatim.recommendedJobs == null || jobAndVerbatim.recommendedJobs.isEmpty()) {
            recommendedParentView.visibility = View.GONE
        } else {
            recommendedParentView.visibility = View.VISIBLE
            recommendedJobsAdapter = RecommendedJobAdapter(activity as Context, jobAndVerbatim.recommendedJobs as ArrayList, this)
            recommendedRV.adapter = recommendedJobsAdapter
        }

        if (jobAndVerbatim.packagesList.isNotEmpty()) {
            jobAndVerbatim.packagesList.forEach {
                inflatePackageLayout(it)
            }
        } else {
            serviceParentView.visibility = View.GONE
        }

        if (checkIfNetworkAvailable()) {
            presenter.getSavedJobs(jobCardId)
        } else {
            toast(R.string.NO_INTERNET)
        }
    }

    private fun inflatePackageLayout(servicePackage: ServicePackage?) {
        val view = layoutInflater.inflate(R.layout.row_packages_jobs, servicePackageView, false)
        val nameView = view.find<TextView>(R.id.tv_package_name)
        val priceView = view.find<TextView>(R.id.tv_package_price)
        val packagePartLayout = view.find<LinearLayout>(R.id.ll_package_parts)

        nameView.text = servicePackage?.name
        var price = 0.0
        if (servicePackage?.rates?.isNotEmpty() == true) {
            servicePackage.rates?.forEach { price += it.offerPrice.amount }
        } else {
            price += servicePackage?.amount ?: 0.0
        }
        priceView.text = Utility.convertToCurrency(price)
        servicePackage?.parts?.forEach { part ->
            TextView(packagePartLayout.context).let {
                it.padding = 6
                it.textColor = ContextCompat.getColor(packagePartLayout.context, R.color.black)
                it.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                it.text = part.part.text
                packagePartLayout.addView(it)
            }
        }
        servicePackageView.addView(view)
    }

    override fun displayRemarks(remarks: List<Remark>?)
    {
        binding.remarksLayout.removeAllViews()

        val redRemarkList = remarks?.filter { it.type == "red" }?.map(Remark::remark)
        val yellowRemarkList = remarks?.filter { it.type == "yellow" }?.map(Remark::remark)

//        val remarkLinearLayout = LinearLayout(holder.itemView.context)
//        remarkLinearLayout.orientation = LinearLayout.VERTICAL

        val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )
        //Red Remark
        if (redRemarkList!=null && redRemarkList.isNotEmpty())
        {
            val redRemarkLinearLayout = LinearLayout(binding.remarksLayout.context)
            redRemarkLinearLayout.orientation = LinearLayout.HORIZONTAL
            val redCircle = TextView(binding.remarksLayout.context)
            redCircle.text = "\u2B24"
            redCircle.setTextColor(binding.remarksLayout.context.resources.getColor(R.color.switch_red))
            redRemarkLinearLayout.addView(redCircle)
            val redRemarkTextView = TextView(binding.remarksLayout.context)
            val redRemarkText = "<b>RED REMARKS: </b>" + redRemarkList?.joinToString(",")
            redRemarkTextView.text = Html.fromHtml(redRemarkText)
            params.setMargins(20, 0, 0, 0)
            redRemarkTextView.layoutParams = params
            redRemarkLinearLayout.addView(redRemarkTextView)
            binding.remarksLayout.addView(redRemarkLinearLayout)
        }

        //Yellow Remark
        if(yellowRemarkList!=null && yellowRemarkList.isNotEmpty())
        {
            val yellowRemarkLinearLayout = LinearLayout(binding.remarksLayout.context)
            yellowRemarkLinearLayout.orientation = LinearLayout.HORIZONTAL
            val yellowCircle = TextView(binding.remarksLayout.context)
            yellowCircle.text = "\u2B24"
            yellowCircle.setTextColor(binding.remarksLayout.context.resources.getColor(R.color.switch_yellow))
            yellowRemarkLinearLayout.addView(yellowCircle)
            val yellowRemarkTextView = TextView(binding.remarksLayout.context)
            val yellowRemarkText = "<b>YELLOW REMARKS: </b>" + yellowRemarkList?.joinToString(",")
            yellowRemarkTextView.text = Html.fromHtml(yellowRemarkText)
            yellowRemarkTextView.layoutParams = params
            yellowRemarkLinearLayout.addView(yellowRemarkTextView)
            binding.remarksLayout.addView(yellowRemarkLinearLayout)
        }

       /* if (remarks?.isEmpty() == true || remarks == null) {
            remarksParentView.visibility = View.GONE
            return
        } else {
            remarksParentView.visibility = View.VISIBLE
        }
        remarks.filter { it.trimEnd().isNotEmpty() }.forEach {
            val textView = TextView(activity)
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            textView.layoutParams = layoutParams
            textView.text = it
            textView.textColor = ContextCompat.getColor(activity as Context, R.color.black)
            textView.padding = 8
            remarksLayout.addView(textView)
        }*/
    }

    @SuppressLint("StringFormatMatches")
    override fun displaySavedJobs(jobs: Jobs) {
        val index = Utility.findIndexFromList(regServiceList?.find { it.kms == jobs.regularService?.pms }, regServiceList)
        if (index != -1) {
            regServiceCbView.isChecked = true
            currentRegIndex = index
            currentRegService = regServiceList?.get(currentRegIndex)
            pmsView.text = String.format(Locale.getDefault(), getString(R.string.job_screen_template_kms_read, currentRegService?.kms))
            setDemandedRepairsList()
            demandedJobsAdapter?.selectedLabourList = jobs.regularService.labours
            demandedJobsAdapter?.selectedPartsList = jobs.regularService.parts
            demandedJobsAdapter?.notifyDataSetChanged()
        }

        recommendedJobsAdapter?.recommendedList?.forEach { job ->
            jobs.recommendedJobs.forEach {
                if (job.id == it.id) {
                    job.isSelected = true
                    job.price = it.price
                }
            }

            if (jobs.unapprovedJobs.find { it.id == job.id && !it.isRecommended } != null) {
                job.isRecommended = false
            }
        }
        recommendedJobsAdapter?.notifyDataSetChanged()

        if (jobs.demandedJobs.isNotEmpty()) {
            selectedDemandedRepairs = jobs.demandedJobs
            selectedDemandedRepairs.forEach {
                it.text?.let { it1 -> createNewTextView(it1) }
            }
        }
    }

    override fun getRecommendedJobList(): List<RecommendedJob>? {
        return recommendedJobsAdapter?.getList()
    }

    override fun getDemandedJobList(): List<RecommendedJob>? {
        return selectedDemandedRepairs
    }

    override fun showProgressIndicator() {
        (activity as BaseActivity).showProgressBar()
    }

    override fun dismissProgressIndicator() {
        (activity as BaseActivity).dismissProgressBar()
    }

    override fun showGenericError(errorMsg: String) {
        interaction?.onJobFailure()
        displayError(errorMsg)
    }

    override fun showError(message: String?) {
        interaction?.onJobFailure()
    }

    override fun moveToNextScreen() {
        interaction?.onJobSuccess()
    }

    @Subscribe
    fun onJobSelected(jobEvent: JobSearchEvent) {
        var isPresent = false
        if (recommendedJobsAdapter != null) {
            val list = recommendedJobsAdapter?.recommendedList
            list?.forEach {
                if (it.id == jobEvent.job.id || it.text == jobEvent.job.text) {
                    isPresent = true
                    toast(getString(R.string.already_selected))
                }
            }
        }

        if (!isPresent && selectedDemandedRepairs.find { it.text == jobEvent.job.text } == null) {
            selectedDemandedRepairs.add(jobEvent.job)
            jobEvent.job.text?.let { createNewTextView(it) }
        } else {
            toast("${jobEvent.job.text} has already been added")
        }
    }

    @Subscribe
    fun onNextBtnClick(obj: ActionButtonClickEvent) {
        if (isViewOnly) {
            interaction?.onJobSuccess()
            return
        }
        val jobPost = Jobs()
        jobPost.demandedJobs = selectedDemandedRepairs
        if (recommendedJobsAdapter != null) {
            jobPost.recommendedJobs = recommendedJobsAdapter?.getList()
        } else {
            jobPost.recommendedJobs = ArrayList<RecommendedJob>()
        }
        val selectedRegularService = RegularService()
        if (currentRegService != null) {
            selectedRegularService.labours = demandedJobsAdapter?.selectedLabourList?.toList()
            selectedRegularService.parts = demandedJobsAdapter?.selectedPartsList?.toList()
            selectedRegularService.pms = currentRegService?.kms ?: 0
        }
        jobPost.regularService = selectedRegularService
        presenter.saveJobsData(jobCardId, jobPost)
    }

    private fun createNewTextView(text: String) {

        val textView = TextView(activity)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(10, 10, 10, 10)
        params.gravity = Gravity.END
        textView.run {
            layoutParams = params
            setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(context, R.drawable.ic_clear_grey_20dp), null)
            setOnTouchListener(View.OnTouchListener { _, event ->
                //  val DRAWABLE_BOTTOM = 3
                val drawableRight = 2
                // val DRAWABLE_LEFT = 0jobRecommend
                // val DRAWABLE_TOP = 1
                if (event.rawX >= (textView.right - textView.compoundDrawables[drawableRight].bounds.width())) {
                    demandedContainerView.removeView(textView)
                    selectedDemandedRepairs.remove(selectedDemandedRepairs.find { it.text == text })
                    return@OnTouchListener true
                }
                false
            })
            setText(text)
        }
        demandedContainerView.addView(textView)
    }

    private fun setDemandedRepairsList() {
        if (currentRegService == null)
            return
        val partsAndLabour = ArrayList<Any>()
        partsAndLabour.addAll(currentRegService!!.parts)
        partsAndLabour.addAll(currentRegService!!.labours)
        demandedJobsAdapter = DemandedRepairAdapter(activity as Context, partsAndLabour)
        regServiceRV.adapter = demandedJobsAdapter

        /*Initially all items should be selected*/
        demandedJobsAdapter?.selectedPartsList = currentRegService!!.parts
        demandedJobsAdapter?.selectedLabourList = currentRegService!!.labours
    }

    companion object {

        private const val ARG_IS_VIEW_ONLY = "is_view_only"
        private const val ARG_JOB_CARD_ID = "job_card_id"
        private const val ARG_VEHICLE_TYPE = "arg_vehicle_type"
        private const val REQUEST_CODE_JOBS_SELECT = 100

        @JvmStatic
        fun newInstance(isViewOnly: Boolean, jobCardId: String, vehicleType: String?): JobsFragment {
            val fragment = JobsFragment()
            val args = Bundle()
            args.putBoolean(ARG_IS_VIEW_ONLY, isViewOnly)
            args.putString(ARG_JOB_CARD_ID, jobCardId)
            args.putString(ARG_VEHICLE_TYPE, vehicleType)
            fragment.arguments = args
            return fragment
        }
    }
}
