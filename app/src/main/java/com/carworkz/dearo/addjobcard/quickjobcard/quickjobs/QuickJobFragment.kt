package com.carworkz.dearo.addjobcard.quickjobcard.quickjobs

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.jobs.JobSearchEvent
import com.carworkz.dearo.base.BaseFragment
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.databinding.FragmentDashboardBinding
import com.carworkz.dearo.databinding.LayoutShortDemandedRepairsBinding
import com.carworkz.dearo.domain.entities.JobAndVerbatim
import com.carworkz.dearo.domain.entities.Jobs
import com.carworkz.dearo.domain.entities.RecommendedJob
import com.carworkz.dearo.domain.entities.RegularService
import com.carworkz.dearo.extensions.*
import com.carworkz.dearo.search.SearchActivity
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.item_toggle_km_container.*
import kotlinx.android.synthetic.main.layout_short_demanded_repairs.*
import kotlinx.android.synthetic.main.layout_short_recommend_jobs.**/
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber

private const val ARG_JOBS = "arg_jobs"
private const val ARG_JOB_VERBATIM = "arg_job_verbatim"
private const val ARG_VEHICLE_TYPE = "arg_vehicle_type"

class QuickJobFragment : BaseFragment(), EventsManager.EventSubscriber {

    private lateinit var binding: LayoutShortDemandedRepairsBinding
    private var vehicleType: String? = null
    private var jobs: Jobs? = null
    private lateinit var jobAndVerbatim: JobAndVerbatim

    private var quickRegServiceAdapter: QuickRegServiceAdapter? = null

    private var currentRegService: RegularService? = null
    private var currentRegIndex: Int = 0

    private var selectedDemandedRepairs: MutableList<RecommendedJob> = ArrayList()
    private var selectedRecommededJobs: MutableList<RecommendedJob> = ArrayList()
    private var unApprovedJobs: MutableList<RecommendedJob> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("on onCreate")

        arguments?.let {
            jobs = it.getParcelable(ARG_JOBS)
            jobAndVerbatim = it.getParcelable(ARG_JOB_VERBATIM)!!
            vehicleType = it.getString(ARG_VEHICLE_TYPE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("on onCreateView")
        binding = LayoutShortDemandedRepairsBinding.inflate(inflater, container, false)
        return binding.root
  /*      return inflater.inflate(R.layout.layout_short_demanded_repairs, container, false)*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.d("on viewcreated")
        if (jobAndVerbatim.regularServices.isEmpty()) {
            binding.regularServiceGroup.visibility = View.GONE
        } else {
            binding.regularServiceGroup.visibility = View.VISIBLE

            binding.include2.pmsPrev.setOnClickListener {
                if (currentRegIndex > 0) {
                    currentRegIndex--
                    currentRegService = jobAndVerbatim.regularServices?.get(currentRegIndex)
                    binding.include2.pmsView.text = currentRegService?.kms.toString() + " KMS"
                    setCurrentRegularServices()
                }
            }
            binding.include2.pmsNext.setOnClickListener {
                if (currentRegIndex < (jobAndVerbatim.regularServices?.size ?: 1) - 1) {
//                    Timber.d("current index before add $currentRegIndex list size " + ((jobAndVerbatim.regularServices?.size
//                            ?: 1) - 2))
                    currentRegIndex++
//                    Timber.d("current index after add $currentRegIndex list size " + ((jobAndVerbatim.regularServices?.size
//                            ?: 1) - 2))
                    currentRegService = jobAndVerbatim.regularServices?.get(currentRegIndex)
                    binding.include2.pmsView.text = currentRegService?.kms.toString() + " KMS"
                    setCurrentRegularServices()
                }
            }
           binding.regularServiceCheckbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    binding.servicingGroup.visibility = View.VISIBLE
                    currentRegService = jobAndVerbatim.regularServices?.find { it.isDefault == true }
                            ?: jobAndVerbatim.regularServices?.get(0)
                    currentRegIndex = Utility.findIndexFromList(currentRegService, jobAndVerbatim.regularServices)
                    binding.include2.pmsView.text = currentRegService?.kms.toString() + " KMS"
                    setCurrentRegularServices()
                } else {
                    binding.servicingGroup.visibility = View.GONE
                    binding.regularServiceJobListView.adapter = null
                    currentRegService = null
                    binding.include2.pmsView.text = ""
                }
            }
            val index = Utility.findIndexFromList(jobAndVerbatim.regularServices?.find { it.kms == jobs?.regularService?.pms }, jobAndVerbatim.regularServices)
            if (index != -1) {
                Timber.d("farhan hey wah")
                binding.regularServiceCheckbox.isChecked = true
                currentRegIndex = index
                currentRegService = jobAndVerbatim.regularServices?.get(currentRegIndex)
                binding.include2.pmsView.text = currentRegService?.kms.toString() + " KMS"
                // setCurrentRegularServices()
                jobs?.let {
                    quickRegServiceAdapter?.selectedPartsList?.clear()
                    quickRegServiceAdapter?.selectedLabourList?.clear()
                    quickRegServiceAdapter?.selectedLabourList?.addAll(it.regularService.labours)
                    quickRegServiceAdapter?.selectedPartsList?.addAll(it.regularService.parts)
                    quickRegServiceAdapter?.notifyDataSetChanged()
                }
            }
        }
        selectedRecommededJobs = jobs?.recommendedJobs ?: arrayListOf()
        unApprovedJobs = jobs?.unapprovedJobs ?: arrayListOf()
        // selectedDemandedRepairs = jobs?.demandedJobs ?: arrayListOf()
        // setCurrentRegularServices()
        inflateRecommendedJobs()
        inflateDemandedJobs()

        binding.btnAddJob.setOnClickListener {
            startActivityForResult(SearchActivity.getIntent(requireContext(), SearchActivity.ARG_JOB_SEARCH, vehicleType), REQUEST_CODE_JOBS_SELECT)
        }
    }

    override fun onResume() {
        super.onResume()
        EventsManager.register(this)
        Timber.d("on resume")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("on destory")
        EventsManager.unregister(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_JOBS_SELECT -> {
                val jobs = data?.getParcelableArrayListExtra<RecommendedJob>(RecommendedJob.TAG)
                jobs?.forEach { selectedJob ->
                    var isPresent = false
                    jobAndVerbatim.recommendedJobs?.forEach innerLoop@{ recommendedJob ->
                        if (recommendedJob.id == selectedJob.id || recommendedJob.text == selectedJob.text) {
                            isPresent = true
                            if (recommendedJob.isSelected)
                                toast(getString(R.string.quick_jc_already_selected))
                            else {
                                toast(getString(R.string.quick_jc_select_from_recommended))
                            }
                            return@innerLoop
                        }
                    }
                    if (!isPresent && selectedDemandedRepairs.find { it.text == selectedJob.text } == null) {
                        selectedDemandedRepairs.add(selectedJob)
                        selectedJob.text?.let { createNewTextView(it) }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun getJobs(): Jobs {
        val jobs = Jobs()
        val selectedRegularService = RegularService()
        if (currentRegService != null) {
            selectedRegularService.labours = quickRegServiceAdapter?.selectedLabourList?.toList()
            selectedRegularService.parts = quickRegServiceAdapter?.selectedPartsList?.toList()
            selectedRegularService.pms = currentRegService?.kms ?: 0
        }
        jobs.regularService = selectedRegularService
        jobs.recommendedJobs = jobAndVerbatim.recommendedJobs?.filter { it.isRecommended.not() || it.isSelected }
                ?: arrayListOf<RecommendedJob>()
        jobs.demandedJobs = selectedDemandedRepairs
        Timber.d("on getjobs")
        return jobs
    }

    @Subscribe
    fun onJobSelected(jobEvent: JobSearchEvent) {
        var isPresent = false
        jobAndVerbatim.recommendedJobs?.forEach {
            if (it.id == jobEvent.job.id || it.text == jobEvent.job.text) {
                isPresent = true
                if (it.isSelected)
                    toast(getString(R.string.quick_jc_already_selected))
                else {
                    toast(getString(R.string.quick_jc_select_from_recommended))
                }
                return@forEach
            }
        }
        if (isPresent) return
        if (!isPresent && selectedDemandedRepairs.find { it.text == jobEvent.job.text } == null) {
            selectedDemandedRepairs.add(jobEvent.job)
            jobEvent.job.text?.let { createNewTextView(it) }
        } else {
            toast("${jobEvent.job.text} has already been added")
        }
    }

    fun updatePms(kms: Int) {
        if (binding.regularServiceCheckbox.isChecked && jobAndVerbatim.regularServices.isNotEmpty()) {
            val index = findNearestKms(kms, jobAndVerbatim.regularServices.asSequence().map { it.kms }.sorted().toList())
            Timber.d("farhan on kms changed index is $index")

            if (index != -1) {
                currentRegIndex = index
                currentRegService = jobAndVerbatim.regularServices?.get(currentRegIndex)
                binding.include2.pmsView.text = """${currentRegService?.kms} KMS"""
                setCurrentRegularServices()
//                jobs?.let {
//                    quickRegServiceAdapter?.selectedLabourList = it.regularService.labours
//                    quickRegServiceAdapter?.selectedPartsList = it.regularService.parts
//                    quickRegServiceAdapter?.notifyDataSetChanged()
//                }
            }
        }
    }

    private fun findNearestKms(kms: Int, kmsList: List<Int>): Int {
        var distance = Math.abs(kmsList[0] - kms)
        var index = 0
        for (c in 1 until kmsList.size) {
            val cdistance = Math.abs(kmsList[c] - kms)
            if (cdistance < distance) {
                index = c
                distance = cdistance
            }
        }
        return index
    }

    private fun inflateRecommendedJobs() {
        Timber.d("on inflaterecommeded")

        if (jobAndVerbatim.recommendedJobs == null || jobAndVerbatim.recommendedJobs.isEmpty()) {
            binding.recommendedGroup.visibility = View.GONE
        } else {
            binding.recommendedGroup.visibility = View.VISIBLE
            jobAndVerbatim.recommendedJobs.forEach { recommendedJob ->
                selectedRecommededJobs.forEach { selectedJob ->
                    if (recommendedJob.id == selectedJob.id) {
                        recommendedJob.isSelected = true
                        recommendedJob.price = selectedJob.price
                    }
                }
                if (unApprovedJobs.find { unApprovedJob ->
                            unApprovedJob.id == recommendedJob.id && !unApprovedJob.isRecommended
                        } != null) {
                    recommendedJob.isRecommended = false
                }

                val view = View.inflate(context, R.layout.row_short_recommended_job, null)
                val jobCheckBox: CheckBox = view.findViewById(R.id.checkBox)
                val deleteView: ImageView = view.findViewById(R.id.deleteView)
                jobCheckBox.text = recommendedJob.text
                jobCheckBox.isChecked = recommendedJob.isSelected
                if (recommendedJob.isRecommended) {
                    setApproved(jobCheckBox, deleteView, recommendedJob)
                } else {
                    setUnApproved(jobCheckBox, deleteView, recommendedJob)
                }
                deleteView.visibility = View.VISIBLE
                deleteView.setOnClickListener {
                    if (recommendedJob.isRecommended) {
                        setUnApproved(jobCheckBox, deleteView, recommendedJob)
                    } else {
                        setApproved(jobCheckBox, deleteView, recommendedJob)
                    }
                }
                jobCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        val job = selectedDemandedRepairs.find { it.text == jobCheckBox.text }
                        if (job != null) {
                            jobCheckBox.isChecked = false
                            requireContext().toast("Job already added")
                        } else {
                            recommendedJob.isSelected = true
                        }
                        recommendedJob.isSelected = true
                        selectedRecommededJobs.add(recommendedJob)
                    } else {
                        recommendedJob.isSelected = false
                        selectedRecommededJobs.remove(recommendedJob)
                    }
                }
                binding.include3.recommendedContainerView.addView(view)
            }
        }
    }

    private fun inflateDemandedJobs() {
        Timber.d("on infaltedemanded")

        if (jobs?.demandedJobs?.isNotEmpty() == true) {
            jobs?.demandedJobs?.forEach {
                selectedDemandedRepairs.add(it)
                it.text?.let { it1 -> createNewTextView(it1) }
            }
        }
    }

    private fun setApproved(jobCheckBox: CheckBox, deleteView: ImageView, job: RecommendedJob) {
        jobCheckBox.textColor = ContextCompat.getColor(requireContext(), R.color.black)
        jobCheckBox.paintFlags = jobCheckBox.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        jobCheckBox.isEnabled = true
        // jobCheckBox.backgroundColor = ContextCompat.getColor(context, android.R.color.transparent)
        deleteView.image = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete_black_24dp)
        job.isRecommended = true
    }

    private fun setUnApproved(jobCheckBox: CheckBox, deleteView: ImageView, job: RecommendedJob) {
        jobCheckBox.textColor = ContextCompat.getColor(requireContext(), R.color.old_lavender)
        jobCheckBox.paintFlags = jobCheckBox.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        // jobCheckBox.backgroundColor = ContextCompat.getColor(context, R.color.light_grey)
        jobCheckBox.isEnabled = false
        jobCheckBox.isChecked = false
        job.isRecommended = false
        job.isSelected = false
        deleteView.image = ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_black_24dp)
    }

    private fun createNewTextView(text: String) {
        binding.demandedRepairsContainer.addView(TextView(context).apply {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                @Suppress("DEPRECATION")
                setTextAppearance(activity, R.style.DearOTextAppearance_Body_Regular_B1)
            } else {
                setTextAppearance(R.style.DearOTextAppearance_Body_Regular_B1)
            }
            val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.setMargins(10, 10, 10, 10)
            params.gravity = Gravity.END
            layoutParams = params
            setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(context, R.drawable.ic_clear_grey_20dp), null)
            setOnTouchListener(View.OnTouchListener { _, event ->
                //  val DRAWABLE_BOTTOM = 3
                val drawableRight = 2
                // val DRAWABLE_LEFT = 0
                // val DRAWABLE_TOP = 1
                if (event.rawX >= (right - compoundDrawables[drawableRight].bounds.width())) {
                    binding.demandedRepairsContainer.removeView(this)
                    selectedDemandedRepairs.remove(selectedDemandedRepairs.find { it.text == text })
                    return@OnTouchListener true
                }
                false
            })
            setText(text)
        })
        Utility.hideSoftKeyboard(activity)
    }

    private fun setCurrentRegularServices() {
        if (currentRegService == null)
            return
        Timber.d("on currentregualreservice")

        val partsAndLabour = ArrayList<Any>()
        partsAndLabour.addAll(currentRegService!!.parts)
        partsAndLabour.addAll(currentRegService!!.labours)
        quickRegServiceAdapter = QuickRegServiceAdapter(activity as Context, partsAndLabour)

        /*Initially all items should be selected*/
        quickRegServiceAdapter?.selectedPartsList?.addAll(currentRegService!!.parts)
        quickRegServiceAdapter?.selectedLabourList?.addAll(currentRegService!!.labours)
        binding.regularServiceJobListView.adapter = quickRegServiceAdapter
    }

    companion object {

        private const val REQUEST_CODE_JOBS_SELECT = 100

        @JvmStatic
        fun newInstance(jobs: Jobs?, jobAndVerbatim: JobAndVerbatim, vehicleType: String?) =
                QuickJobFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_JOBS, jobs)
                        putParcelable(ARG_JOB_VERBATIM, jobAndVerbatim)
                        putString(ARG_VEHICLE_TYPE, vehicleType)
                    }
                }
    }
}
