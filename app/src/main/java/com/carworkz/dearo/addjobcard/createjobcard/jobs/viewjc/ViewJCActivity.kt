package com.carworkz.dearo.addjobcard.createjobcard.jobs.viewjc

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.ActionButtonClickEvent
import com.carworkz.dearo.addjobcard.createjobcard.ICreateJobCardInteraction
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityViewjcBinding
import com.carworkz.dearo.interactionprovider.ToolBarInteractionProvider

/**
 * Created by kush on 19/9/17.
 */
class ViewJCActivity : ScreenContainerActivity(), ToolBarInteractionProvider, ICreateJobCardInteraction {
    private lateinit var binding: ActivityViewjcBinding
          //  private lateinit var progressView: ProgressBar

    private lateinit var jobCardId: String

    private var isViewOnly: Boolean = false

    private var isAddJob: Boolean = false

    private lateinit var displayJobCardId: String

    private var vehicleType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        intent.extras?.let {
            jobCardId = it.getString(ARG_JOB_CARD_ID)!!
            displayJobCardId = it.getString(ARG_DISPLAY_JOB_CARD_ID)!!
            vehicleType = it.getString(ARG_VEHICLE_TYPE)
            isViewOnly = it.getBoolean(ARG_IS_VIEW_ONLY)
            isAddJob = it.getBoolean(ARG_IS_ADD_JOB)
        }
        super.onCreate(savedInstanceState)
       // progressView = findViewById(R.id.pb_main)
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().add(R.id.ll_view_jc_container, ViewJCFragment.newInstance(isViewOnly, isAddJob, jobCardId, vehicleType)).commitAllowingStateLoss()
    }

    override fun createScreenContainer(): ScreenContainer? {
        return ViewJCContainer(this)
    }

    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityViewjcBinding.inflate(layoutInflater)
        return binding
    }


    override fun getToolBarTitle(): String {
        return "$displayJobCardId Jobs"
    }

    override fun getProgressView(): View? {
        return progressView
    }

    override fun getActionBtnTitle(): String {
        return if (isViewOnly.not()) "SAVE" else ""
    }

    override fun onActionBtnClick() {
        showProgressBar()
        EventsManager.post(ActionButtonClickEvent())
    }

    override fun onJobSuccess() {
        dismissProgressBar()
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onJobFailure() {
        dismissProgressBar()
    }

    override fun onJobVerify() {
    }

    companion object {
        const val ARG_JOB_CARD_ID = "job_card_id"
        const val ARG_DISPLAY_JOB_CARD_ID = "display_job_card_id"
        const val ARG_IS_VIEW_ONLY = "arg_is_view_only"
        const val ARG_IS_ADD_JOB = "arg_is_add_job"
        const val ARG_VEHICLE_TYPE = "arg_vehicle_type"

        fun getViewJcIntent(context: Context, jobCardId: String, displayJobCardId: String, isViewOnly: Boolean, isAddJob: Boolean, vehicleType: String?): Intent {
            return (Intent(context, ViewJCActivity::class.java)
                    .putExtra(ARG_JOB_CARD_ID, jobCardId)
                    .putExtra(ARG_DISPLAY_JOB_CARD_ID, displayJobCardId))
                    .putExtra(ARG_IS_VIEW_ONLY, isViewOnly)
                    .putExtra(ARG_IS_ADD_JOB, isAddJob)
                    .putExtra(ARG_VEHICLE_TYPE, vehicleType)
        }
    }
}