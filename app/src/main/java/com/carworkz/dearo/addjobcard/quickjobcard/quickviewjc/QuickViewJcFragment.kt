package com.carworkz.dearo.addjobcard.quickjobcard.quickviewjc

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.jobs.JobSearchEvent
import com.carworkz.dearo.base.BaseFragment
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.databinding.FragmentDashboardBinding
import com.carworkz.dearo.databinding.LayoutShortRecommendedJobsInProgressBinding
import com.carworkz.dearo.domain.entities.Jobs
import com.carworkz.dearo.domain.entities.RecommendedJob
import com.carworkz.dearo.extensions.toast
import com.carworkz.dearo.search.SearchActivity
/*import kotlinx.android.synthetic.main.layout_short_recommended_jobs_in_progress.**/
import org.greenrobot.eventbus.Subscribe

private const val ARG_IS_VIEW_ONLY = "arg_is_view_only"
private const val ARG_JOBS = "arg_jobs"
private const val ARG_IS_ADD_JOB = "arg_is_add_job"
private const val ARG_VEHICLE_TYPE = "arg_vehicle_type"

class QuickViewJcFragment : BaseFragment(), EventsManager.EventSubscriber {
    private lateinit var binding: LayoutShortRecommendedJobsInProgressBinding
    private lateinit var jobs: Jobs
    private var isViewOnly = false
    private var vehicleType: String? = null
    private var isAddJob = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            jobs = it.getParcelable(ARG_JOBS)!!
            isAddJob = it.getBoolean(ARG_IS_ADD_JOB)
            isViewOnly = it.getBoolean(ARG_IS_VIEW_ONLY)
            vehicleType = it.getString(ARG_VEHICLE_TYPE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutShortRecommendedJobsInProgressBinding.inflate(inflater, container, false)
        return binding.root
 /*       return inflater.inflate(R.layout.layout_short_recommended_jobs_in_progress, container, false)*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (jobs.regularService != null && (jobs.regularService.parts?.isEmpty() == false || jobs.regularService.labours?.isEmpty() == false)) {
            binding.groupRegularJob.visibility = View.VISIBLE
            binding.regularJobsContainer.removeAllViews()
            jobs.regularService.parts.forEach { part ->
                val regularServiceParentView = View.inflate(context, R.layout.row_short_additional_jobs, null)
                val isDoneView = regularServiceParentView.findViewById<ImageView>(R.id.isDoneView)
                val titleView = regularServiceParentView.findViewById<TextView>(R.id.additionalJobTitleView)
                if (!isViewOnly) {
                    isDoneView.setOnClickListener { v ->
                        part.isComplete = part.isComplete?.not()
                        setDoneView(v, part.isComplete ?: false)
                    }
                }
                titleView.text = part.text
                setDoneView(isDoneView, part.isComplete ?: false)
                binding.regularJobsContainer.addView(regularServiceParentView)
            }
            jobs.regularService.labours.forEach { labour ->
                val regularServiceParentView = View.inflate(context, R.layout.row_short_additional_jobs, null)
                val isDoneView = regularServiceParentView.findViewById<ImageView>(R.id.isDoneView)
                val titleView = regularServiceParentView.findViewById<TextView>(R.id.additionalJobTitleView)
                if (!isViewOnly) {
                    isDoneView.setOnClickListener { v ->
                        labour.isComplete = labour.isComplete?.not()
                        setDoneView(v, labour.isComplete ?: false)
                    }
                }
                titleView.text = labour.text
                setDoneView(isDoneView, labour.isComplete ?: false)
                binding.additionalJobsContainer.addView(regularServiceParentView)
            }
        } else {
            binding.groupRegularJob.visibility = View.GONE
        }
        if (jobs.recommendedJobs != null && jobs.recommendedJobs.isNotEmpty()) {
            binding.recommendedGroup.visibility = View.VISIBLE
            binding.recommendedJobsContainer.removeAllViews()
            jobs.recommendedJobs.forEach { recommendedJob ->
                val recommendedJobsView = View.inflate(context, R.layout.row_short_additional_jobs, null)
                val isDoneView = recommendedJobsView.findViewById<ImageView>(R.id.isDoneView)
                val titleView = recommendedJobsView.findViewById<TextView>(R.id.additionalJobTitleView)
                if (!isViewOnly) {
                    isDoneView.setOnClickListener { v ->
                        recommendedJob.isComplete = recommendedJob.isComplete.not()
                        setDoneView(v, recommendedJob.isComplete)
                    }
                }
                titleView.text = recommendedJob.text
                setDoneView(isDoneView, recommendedJob.isComplete)
                binding.recommendedJobsContainer.addView(recommendedJobsView)
            }
        } else {
            binding.recommendedGroup.visibility = View.GONE
        }
        inflateAdditionalJobs()
        if (jobs.demandedJobs?.isNotEmpty() == true) {
            binding. groupDemandedJobs.visibility = View.VISIBLE
            binding.demandedJobsContainer.removeAllViews()
            jobs.demandedJobs?.forEach { demandedJob ->
                val demandedJobsParentView = View.inflate(context, R.layout.row_short_additional_jobs, null)
                val isDoneView = demandedJobsParentView.findViewById<ImageView>(R.id.isDoneView)
                val titleView = demandedJobsParentView.findViewById<TextView>(R.id.additionalJobTitleView)
                val deleteView = demandedJobsParentView.findViewById<ImageView>(R.id.additionalJobDeleteView)
                if (!isViewOnly) {
                    isDoneView.setOnClickListener { v ->
                        demandedJob.isComplete = demandedJob.isComplete.not()
                        setDoneView(v, demandedJob.isComplete)
                    }
                }
                titleView.text = demandedJob.text
                deleteView.visibility = View.GONE
                setDoneView(isDoneView, demandedJob.isComplete)
                binding.demandedJobsContainer.addView(demandedJobsParentView)
            }
        } else {
            binding.groupDemandedJobs.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        EventsManager.register(this)
        if (isAddJob && isViewOnly.not()) {
            startJobSearchActivity()
            // isAddJob should be true only once while opening fragment for the first time.
            isAddJob = false
        }
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
                var containsDuplicateJobs = false
                selectedJobs?.forEach { recommendedJob ->
                    if (jobs.additionalJobs?.find {
                                (recommendedJob.id != null && it.id == recommendedJob.id) || it.text == recommendedJob.text
                            } == null &&
                            jobs.demandedJobs.find { it.id == recommendedJob.id || it.text == recommendedJob.text } == null &&
                            jobs.recommendedJobs.find { it.id == recommendedJob.id || it.text == recommendedJob.text } == null
                    ) {
                        if (jobs.additionalJobs == null) {
                            jobs.additionalJobs = mutableListOf<RecommendedJob>()
                        }
                        recommendedJob.isComplete = true
                        jobs.additionalJobs?.add(recommendedJob)
                        inflateAdditionalJobs()
                    } else {
                        containsDuplicateJobs = true
                    }
                }

                if (containsDuplicateJobs) toast(R.string.job_screen_duplicate_items)
            }
        }
    }

    fun getJobs(): Jobs = jobs

    @Subscribe
    fun onJobSelectedEvent(jobEvent: JobSearchEvent) {
        if (jobs.additionalJobs?.find {
                    (jobEvent.job.id != null && it.id == jobEvent.job.id) || it.text == jobEvent.job.text
                } == null &&
                jobs.demandedJobs.find { it.id == jobEvent.job.id || it.text == jobEvent.job.text } == null &&
                jobs.recommendedJobs.find { it.id == jobEvent.job.id || it.text == jobEvent.job.text } == null
        ) {
            if (jobs.additionalJobs == null) {
                jobs.additionalJobs = mutableListOf<RecommendedJob>()
            }
            val newJob = jobEvent.job
            newJob.isComplete = true
            jobs.additionalJobs?.add(newJob)
            inflateAdditionalJobs()
            binding.additionalJobsContainer.requestFocus()
        } else {
            toast("Job already added")
        }
    }

    private fun inflateAdditionalJobs() {
        if (isViewOnly) {
            binding.btnAddJob.visibility = View.GONE
        } else {
            binding.btnAddJob.visibility = View.VISIBLE
            binding.btnAddJob.setOnClickListener {
                startJobSearchActivity()
            }
        }

        if (isViewOnly.not() || (jobs.additionalJobs?.isNotEmpty() == true && isViewOnly)) {
            binding.groupAdditionalJob.visibility = View.VISIBLE
            binding.additionalJobsContainer.removeAllViews()
            jobs.additionalJobs?.forEach { additionalJob ->
                val view = View.inflate(context, R.layout.row_short_additional_jobs, null)
                val isDoneView = view.findViewById<ImageView>(R.id.isDoneView)
                val titleView = view.findViewById<TextView>(R.id.additionalJobTitleView)
                val deleteView = view.findViewById<ImageView>(R.id.additionalJobDeleteView)
                if (isViewOnly) {
                    deleteView.visibility = View.GONE
                } else {
                    deleteView.visibility = View.VISIBLE
                    isDoneView.setOnClickListener { v ->
                        additionalJob.isComplete = additionalJob.isComplete.not()
                        setDoneView(v, additionalJob.isComplete)
                    }
                    deleteView.setOnClickListener {
                        jobs.additionalJobs.remove(additionalJob)
                        binding.additionalJobsContainer.removeView(view)
                    }
                }
                titleView.text = additionalJob.text
                setDoneView(isDoneView, additionalJob.isComplete)
                binding.additionalJobsContainer.addView(view)
            }
        } else {
            binding.groupAdditionalJob.visibility = View.GONE
        }
    }

    private fun setDoneView(isDoneView: View, isDone: Boolean) {
        (isDoneView as? ImageView)?.setImageDrawable(if (isDone) AppCompatResources.getDrawable(requireContext(),R.drawable.ic_outline_check_circle_outline_green_24px) else AppCompatResources.getDrawable(requireContext(),R.drawable.ic_radio_button_unchecked_black_24dp))
    }

    private fun startJobSearchActivity() {
        startActivityForResult(SearchActivity.getIntent(requireContext(), SearchActivity.ARG_JOB_SEARCH, vehicleType), REQUEST_CODE_JOBS_SELECT)
    }

    companion object {

        private const val REQUEST_CODE_JOBS_SELECT = 100

        @JvmStatic
        fun newInstance(isViewOnly: Boolean, jobs: Jobs, vehicleType: String?, isAddJob: Boolean) =
                QuickViewJcFragment().apply {
                    arguments = Bundle().apply {
                        putBoolean(ARG_IS_VIEW_ONLY, isViewOnly)
                        putString(ARG_VEHICLE_TYPE, vehicleType)
                        putParcelable(ARG_JOBS, jobs)
                        putBoolean(ARG_IS_ADD_JOB, isAddJob)
                    }
                }
    }
}
