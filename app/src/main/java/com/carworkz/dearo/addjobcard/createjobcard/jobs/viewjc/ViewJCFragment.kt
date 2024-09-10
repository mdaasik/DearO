package com.carworkz.dearo.addjobcard.createjobcard.jobs.viewjc

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.ActionButtonClickEvent
import com.carworkz.dearo.addjobcard.createjobcard.ICreateJobCardInteraction
import com.carworkz.dearo.addjobcard.createjobcard.jobs.JobSearchEvent
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.base.BaseFragment
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.FragmentViewJcBinding
import com.carworkz.dearo.domain.entities.Jobs
import com.carworkz.dearo.domain.entities.RecommendedJob
import com.carworkz.dearo.domain.entities.ServicePackage
import com.carworkz.dearo.extensions.find
import com.carworkz.dearo.extensions.toast
import com.carworkz.dearo.search.SearchActivity
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.fragment_view_jc.**/
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import javax.inject.Inject

class ViewJCFragment : BaseFragment(), ViewJCContract.View, EventsManager.EventSubscriber {
    private lateinit var binding: FragmentViewJcBinding
    @Inject
    lateinit var presenter: ViewJCPresenter
    @Inject
    lateinit var screenTracker: ScreenTracker

    private lateinit var parentView: LinearLayout

    private var isViewOnly: Boolean = false
    private var isAddJob: Boolean = false
    private lateinit var jobCardId: String

    private var vehicleType: String? = null

    private lateinit var jobsObj: Jobs

    private lateinit var additionalJobContainer: LinearLayout
    private lateinit var additionalJobTitle: TextView
    private lateinit var demandedJobContainer: LinearLayout
    private lateinit var demandedJobsTitle: TextView
    private lateinit var regularJobsContainer: LinearLayout
    private lateinit var regularJobsTitle: TextView
    private lateinit var recommendedJobsContainer: LinearLayout
    private lateinit var recommendedJobsTitle: TextView
    private lateinit var remarksParentView: LinearLayout

    private var interaction: ICreateJobCardInteraction? = null
    private val titleViewParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    private val itemViewParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    private val subItemViewParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            arguments?.let {
                isViewOnly = it.getBoolean(ARG_IS_VIEW_ONLY)
                isAddJob = it.getBoolean(ARG_ADD_JOB)
                jobCardId = it.getString(ARG_JOB_CARD_ID)!!
                vehicleType = it.getString(ARG_VEHICLE_TYPE)
            }
        }

        (requireActivity().application as DearOApplication)
                .repositoryComponent
                .COMPONENT(ViewJCPresenterModule(this))
                .inject(this)

        screenTracker.sendScreenEvent(activity, if (isViewOnly) ScreenTracker.SCREEN_VIEW_ONLY_VIEW_JC else ScreenTracker.SCREEN_VIEW_JC, this.javaClass.name)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        binding = FragmentViewJcBinding.inflate(inflater, container, false)
        return binding.root
       /* return inflater.inflate(R.layout.fragment_view_jc, container, false)*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentView = view.findViewById(R.id.ll_view_jc_parent) as LinearLayout
        additionalJobContainer = find(R.id.additionalJobsContainer)
        additionalJobTitle = find(R.id.additionalJobTitle)
        demandedJobContainer = find(R.id.demandedJobsContainer)
        demandedJobsTitle = find(R.id.demandedJobsTitle)
        regularJobsContainer = find(R.id.regularJobsContainer)
        regularJobsTitle = find(R.id.regularJobsTitle)
        recommendedJobsContainer = find(R.id.recommendedJobsContainer)
        recommendedJobsTitle = find(R.id.recommendedJobsTitle)
        remarksParentView = find(R.id.ll_remark_parent)
        titleViewParams.setMargins(0, Utility.dpToPx(activity, 8), 0, 0)
        itemViewParams.setMargins(Utility.dpToPx(activity, 20), Utility.dpToPx(activity, 3), 0, 0)
        itemViewParams.weight = 0.2f
        subItemViewParams.setMargins(Utility.dpToPx(activity, 28), Utility.dpToPx(activity, 3), 0, 0)

        if (checkIfNetworkAvailable()) {
            presenter.getJobsData(jobCardId)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ICreateJobCardInteraction)
            interaction = context
    }

    override fun onResume() {
        super.onResume()
        EventsManager.register(this@ViewJCFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventsManager.unregister(this)
        presenter.detachView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_JOBS_SELECT -> {
                val jobs = data?.getParcelableArrayListExtra<RecommendedJob>(RecommendedJob.TAG)
                var containsDuplicateJobs = false
                jobs?.forEach { recommendedJob ->
                    if (jobsObj.additionalJobs?.find {
                                (recommendedJob.id != null && it.id == recommendedJob.id) || it.text == recommendedJob.text
                            } == null &&
                            jobsObj.demandedJobs.find { it.id == recommendedJob.id || it.text == recommendedJob.text } == null &&
                            jobsObj.recommendedJobs.find { it.id == recommendedJob.id || it.text == recommendedJob.text } == null
                    ) {
                        if (jobsObj.additionalJobs == null) {
                            jobsObj.additionalJobs = mutableListOf<RecommendedJob>()
                        }
                        recommendedJob.isComplete = true
                        jobsObj.additionalJobs?.add(recommendedJob)
                        inflateAdditionalJobs()
                    } else {
                        containsDuplicateJobs = true
                    }
                    if (containsDuplicateJobs) toast(R.string.job_screen_duplicate_items)
                }
            }
        }
    }

    override fun showProgressIndicator() {
        (activity as ScreenContainerActivity).showProgressBar()
    }

    override fun dismissProgressIndicator() {
        (activity as ScreenContainerActivity).dismissProgressBar()
    }

    override fun displayJobsData(obj: Jobs) {
        jobsObj = obj
        if (isViewOnly && jobsObj.additionalJobs.isEmpty() &&
                jobsObj.demandedJobs.isEmpty() &&
                jobsObj.recommendedJobs.isEmpty() &&
                jobsObj.regularService.parts.isEmpty() &&
                jobsObj.regularService.labours.isEmpty()) {
            binding.noJobView.visibility = View.VISIBLE
            binding.parentViewJc.visibility = View.GONE
            return
        }
        if (!isViewOnly || (jobsObj.additionalJobs?.isEmpty() == false && isViewOnly)) {
            binding.additionalJobsView.visibility = View.VISIBLE
            inflateAdditionalJobs()
        } else {
            binding.additionalJobsView.visibility = View.GONE
            additionalJobTitle.visibility = View.GONE
            additionalJobContainer.visibility = View.GONE
        }

        /*When implementing check box logic make commons function for similar layouts*/
        if (jobsObj.demandedJobs?.isEmpty() == false) {
            binding.demandedJobs.visibility = View.VISIBLE
            demandedJobContainer.removeAllViews()
            jobsObj.demandedJobs?.forEach { recommendedJob ->
                val view = LayoutInflater.from(activity).inflate(R.layout.row_jobs, demandedJobContainer, false)
                val imageView = view.find<ImageView>(R.id.cancelView)
                val itemView = view.find<TextView>(R.id.itemName)
                val completeView = view.find<CheckBox>(R.id.cb_view_jc)
                completeView.isChecked = recommendedJob.isComplete
                completeView.isEnabled = isViewOnly.not()
                if (isViewOnly.not()) {
                    completeView.setOnClickListener {
                        recommendedJob.isComplete = completeView.isChecked
                    }
                }
                itemView.text = recommendedJob.text
                imageView.visibility = View.INVISIBLE
                demandedJobContainer.addView(view)
            }
        } else {
            binding.demandedJobs.visibility = View.GONE
            demandedJobsTitle.visibility = View.GONE
            demandedJobContainer.visibility = View.GONE
        }

        if (jobsObj.regularService != null) {
            binding.regularJobs.visibility = View.VISIBLE
            regularJobsContainer.removeAllViews()
            if (jobsObj.regularService.parts.isNotEmpty() || jobsObj.regularService.labours.isNotEmpty()) {

                jobsObj.regularService.labours.forEach { labour ->
                    val view = LayoutInflater.from(activity).inflate(R.layout.row_jobs, regularJobsContainer, false)
                    val imageView = view.find<ImageView>(R.id.cancelView)
                    val itemView = view.find<TextView>(R.id.itemName)
                    itemView.text = labour.text
                    imageView.visibility = View.INVISIBLE
                    val completeView = view.findViewById<CheckBox>(R.id.cb_view_jc)
                    completeView.isChecked = labour.isComplete ?: false
                    completeView.isEnabled = false // by default labour should be disabled
                    regularJobsContainer.addView(view)
                }
                jobsObj.regularService.parts.forEach { part ->
                    val view = LayoutInflater.from(activity).inflate(R.layout.row_jobs, regularJobsContainer, false)
                    val imageView =view.find<ImageView>(R.id.cancelView)
                    val itemView = view.find<TextView>(R.id.itemName)
                    itemView.text = part.text
                    imageView.visibility = View.INVISIBLE
                    val completeView = view.findViewById<CheckBox>(R.id.cb_view_jc)
                    completeView.isChecked = part.isComplete ?: false
                    completeView.isEnabled = isViewOnly.not()
                    if (isViewOnly.not()) {
                        completeView.setOnClickListener {
                            part.isComplete = completeView.isChecked
                        }
                    }
                    regularJobsContainer.addView(view)
                }
            } else {
                binding.regularJobs.visibility = View.VISIBLE
                regularJobsContainer.visibility = View.GONE
                regularJobsTitle.visibility = View.GONE
            }
        }

        if (jobsObj.recommendedJobs?.isNotEmpty() == true) {
            binding.recommendedJobs.visibility = View.VISIBLE
            recommendedJobsContainer.removeAllViews()
            jobsObj.recommendedJobs.forEach { recommendedJob ->
                val view = LayoutInflater.from(activity).inflate(R.layout.row_jobs, recommendedJobsContainer, false)
                val imageView =view.find<ImageView>(R.id.cancelView)
                val itemView = view.find<TextView>(R.id.itemName)
                val completeView = view.findViewById<CheckBox>(R.id.cb_view_jc)
                completeView.isChecked = recommendedJob.isComplete
                completeView.isEnabled = isViewOnly.not()
                if (isViewOnly.not()) {
                    completeView.setOnClickListener {
                        recommendedJob.isComplete = completeView.isChecked
                    }
                }
                itemView.text = recommendedJob.text
                imageView.visibility = View.INVISIBLE
                recommendedJobsContainer.addView(view)
            }
        } else {
            binding.recommendedJobs.visibility = View.VISIBLE
            recommendedJobsContainer.visibility = View.GONE
            recommendedJobsTitle.visibility = View.GONE
        }

        if (isAddJob) {
            startJobSearchActivity()
            isAddJob = false
        }
    }

    @SuppressLint("SetTextI18n")
    override fun displayPackages(packages: List<ServicePackage>) {
        if (packages.isNotEmpty()) {
            val packageLayout = LinearLayout(activity)
            parentView.addView(packageLayout)
            packageLayout.orientation = LinearLayout.VERTICAL
            TextView(activity).let {
                it.text = getString(R.string.service_packages)
                it.textSize = 16f
                it.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                it.setTypeface(null, Typeface.BOLD)
                it.layoutParams = titleViewParams
                packageLayout.addView(it)
            }
            packages.forEach { servicePackage ->
                TextView(activity).let {
                    it.text = servicePackage.name
                    it.setPadding( 4)
                    it.setTextColor( ContextCompat.getColor(requireContext(), R.color.black))
                    it.layoutParams = itemViewParams
                    packageLayout.addView(it)
                }

                servicePackage.parts?.forEach {
                    val itemView = TextView(activity)
                    itemView.layoutParams = subItemViewParams
                    itemView.setPadding( 4)
                    itemView.text = "- " + it.part.text
                    itemView.setTextColor( ContextCompat.getColor(requireContext(), R.color.old_lavender))
                    packageLayout.addView(itemView)
                }
            }
        }
    }

    override fun displayRemarks(remarks: Array<out String>?) {
        binding.remarksLayout.removeAllViews()
        if (remarks?.isEmpty() == true || remarks == null) {
            remarksParentView.visibility = View.GONE
            return
        } else {
            remarksParentView.visibility = View.VISIBLE
        }
        remarks.filter { it.trimEnd().isNotEmpty() }.forEach {
            val textView = TextView(activity)
            textView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            textView.text = it
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            textView.setPadding ( 8)
            binding.remarksLayout.addView(textView)
        }
    }

    override fun moveToNextScreen() {
        interaction?.onJobSuccess()
    }

    override fun showError(message: String?) {
        interaction?.onJobFailure()
    }

    override fun showGenericError(errorMsg: String) {
        interaction?.onJobFailure()
        displayError(errorMsg)
    }

    @Subscribe
    fun onJobSelectedEvent(jobEvent: JobSearchEvent) {
        if (jobsObj.additionalJobs?.find {
                    (jobEvent.job.id != null && it.id == jobEvent.job.id) || it.text == jobEvent.job.text
                } == null &&
                jobsObj.demandedJobs.find { it.id == jobEvent.job.id || it.text == jobEvent.job.text } == null &&
                jobsObj.recommendedJobs.find { it.id == jobEvent.job.id || it.text == jobEvent.job.text } == null
        ) {
            if (jobsObj.additionalJobs == null) {
                jobsObj.additionalJobs = mutableListOf<RecommendedJob>()
            }
            val newJob = jobEvent.job
            newJob.isComplete = true
            jobsObj.additionalJobs?.add(newJob)
            inflateAdditionalJobs()
        } else {
            toast("Job already added")
        }
    }

    @Subscribe
    fun onSaveButtonClick(actionButtonClickEvent: ActionButtonClickEvent) {
        Timber.v("viewjc Click Event" + actionButtonClickEvent.hashCode())
        if (isViewOnly) {
            interaction?.onJobSuccess()
            return
        }
        val jobPost = Jobs()
        jobPost.isCalculator = true
        jobPost.regularService = jobsObj.regularService
        jobPost.additionalJobs = jobsObj.additionalJobs
        jobPost.demandedJobs = jobsObj.demandedJobs
        jobPost.recommendedJobs = jobsObj.recommendedJobs
        presenter.saveJobsData(jobCardId, jobPost)
    }

    private fun startJobSearchActivity() {
        startActivityForResult(SearchActivity.getIntent(requireContext(), SearchActivity.ARG_JOB_SEARCH, vehicleType), REQUEST_CODE_JOBS_SELECT)
    }

    private fun inflateAdditionalJobs() {
        additionalJobContainer.removeAllViews()
        jobsObj.additionalJobs?.forEachIndexed { _, additionalJob ->
//            info { "inside inflate job" + additionalJob.text }
            val view = LayoutInflater.from(activity).inflate(R.layout.row_jobs, additionalJobContainer, false)
            val imageView = view.find<ImageView>(R.id.cancelView)
            val itemView = view.find<TextView>(R.id.itemName)
            itemView.text = additionalJob.text
            val completeView = view.find<CheckBox>(R.id.cb_view_jc)
            completeView.isChecked = additionalJob.isComplete
            completeView.isEnabled = isViewOnly.not()
            additionalJobContainer.addView(view)
            if (!isViewOnly) {
                imageView.setOnClickListener {
                    jobsObj.additionalJobs.remove(additionalJob)
                    additionalJobContainer.removeView(view)
                }
                completeView.setOnClickListener {
                    additionalJob.isComplete = completeView.isChecked
                }
            } else {
                imageView.visibility = View.INVISIBLE
            }
        }
        if (!isViewOnly) {
            val addItemView = TextView(activity)
            addItemView.text = getString(R.string.add_item)
            addItemView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
            addItemView.layoutParams = itemViewParams
            addItemView.maxWidth = 100
            addItemView.setTypeface(null, Typeface.BOLD)
            addItemView.setOnClickListener {
                startJobSearchActivity()
            }
            additionalJobContainer.addView(addItemView)
        }
    }

    companion object {

        private const val ARG_IS_VIEW_ONLY = "is_view_only"
        private const val ARG_ADD_JOB = "is_add_job"
        private const val ARG_JOB_CARD_ID = "job_card_id"
        private const val ARG_VEHICLE_TYPE = "arg_vehicle_type"
        private const val REQUEST_CODE_JOBS_SELECT = 100

        @JvmStatic
        fun newInstance(isViewOnly: Boolean, isAddJob: Boolean, jobCardId: String, vehicleType: String?): ViewJCFragment {
            val fragment = ViewJCFragment()
            val args = Bundle()
            args.putBoolean(ARG_IS_VIEW_ONLY, isViewOnly)
            args.putBoolean(ARG_ADD_JOB, isAddJob)
            args.putString(ARG_JOB_CARD_ID, jobCardId)
            args.putString(ARG_VEHICLE_TYPE, vehicleType)
            fragment.arguments = args
            return fragment
        }
    }
}
