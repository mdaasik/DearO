package com.carworkz.dearo.customerapproval

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.LoggingFacade
import com.carworkz.dearo.R
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityAmcDetailsBinding
import com.carworkz.dearo.databinding.ActivityCustomerApprovalBinding
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.events.ApproveEvent
import com.carworkz.dearo.extensions.alert
import com.carworkz.dearo.interactionprovider.DefaultInteractionProvider
import com.carworkz.dearo.interactionprovider.ToolBarImgInteractionProvider
import com.carworkz.dearo.morecta.MoreCtaListDialogFragment
import com.carworkz.dearo.outwarding.OutwardingProcessActivity
import com.carworkz.dearo.outwarding.helper.OutwardItem
import com.carworkz.dearo.outwarding.helper.OutwardStep
import com.carworkz.dearo.outwarding.helper.OutwardStepMapper
import com.carworkz.dearo.screencontainer.ActionImgScreenContainer
/*import kotlinx.android.synthetic.main.activity_customer_approval.*
import kotlinx.android.synthetic.main.base_layout.**/
import javax.inject.Inject

const val ARG_JOB_CARD_ID = "arg_jobcard_id"
const val ARG_JOB_CARD_DISPLAY_ID = "arg_jobcard_display_id"

class CustomerApprovalActivity : ScreenContainerActivity(), DefaultInteractionProvider,
    CustomerApprovalContract.View,
    ToolBarImgInteractionProvider, View.OnClickListener, CustomerApprovalAdapter.CallBackListener {
    private lateinit var binding: ActivityCustomerApprovalBinding
    private var estimatorList = arrayListOf<OutwardStep>()

    @Inject
    lateinit var presenter: CustomerApprovalPresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    private var unsavedData: Boolean = false
    private lateinit var jobCardId: String
    private lateinit var jobCardDisplayId: String
    private var isViewOnly: Boolean = false
    private lateinit var adapter: CustomerApprovalAdapter
    private lateinit var screenContainer: ActionImgScreenContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        initComponent()
        getExtras()
        super.onCreate(savedInstanceState)
        binding.btnSave.setOnClickListener(this)
        //fetch jobcard
        presenter.getEstimation(jobCardId)
    }

    override fun onFetchJobCard(obj: JobCard) {
        val estimate = obj.costEstimate
        val packages = estimate.packages
        if (isViewOnly) binding.btnSave.visibility = View.GONE
        estimatorList = OutwardStepMapper.costEstimateToOutwardStep(estimate, packages, true)
        binding.estimatorRecyclerview.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        adapter = CustomerApprovalAdapter(estimatorList, true, isViewOnly, this)
        binding.estimatorRecyclerview.adapter = adapter
    }

    override fun onSaveSuccess() {
        toast("Saved Successfully !")
        finish()
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
        binding = ActivityCustomerApprovalBinding.inflate(layoutInflater)
        return binding
    }


    override fun getProgressView(): View = binding.baseLayout.pbMain
    override fun getNavigationImage(): Int {
        return R.drawable.ic_clear_white_24dp
    }

    override fun onActionBtnClick() {

    }

    override fun onSecondaryActionBtnClick() {

    }

    override fun getActionBarImage(): Int = 0

    override fun getSecondaryActionBarImage(): Int = 0

    override fun getToolBarTitle(): String = jobCardDisplayId

    private fun initComponent() {
        (application as DearOApplication)
            .repositoryComponent
            .COMPONENT(CustomerApprovalModule(this))
            .inject(this)
    }

    private fun getExtras() {
        jobCardId = intent.extras!!.getString(ARG_JOB_CARD_ID)!!
        jobCardDisplayId = intent.extras!!.getString(ARG_JOB_CARD_DISPLAY_ID)!!
        isViewOnly = intent.extras!!.getBoolean(OutwardingProcessActivity.ARG_IS_VIEW_ONLY)
//        setTitle(jobCardDisplayId)
    }

    companion object {
        fun getIntent(
            context: Context,
            jobCardId: String,
            jobCardDisplayId: String,
            isViewOnly: Boolean
        ): Intent {
            return Intent(context, CustomerApprovalActivity::class.java).apply {
                putExtra(ARG_JOB_CARD_ID, jobCardId)
                putExtra(ARG_JOB_CARD_DISPLAY_ID, jobCardDisplayId)
                putExtra(OutwardingProcessActivity.ARG_IS_VIEW_ONLY, isViewOnly)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            promptUserIfDirtyOrExit()
        }
        return true
    }

    override fun dataChanged(isChanged: Boolean) {
        unsavedData = isChanged
    }

    private fun promptUserIfDirtyOrExit() {
        if (unsavedData) {
            alert(getString(R.string.alert_dirty_message), getString(R.string.alert_dirty_title)) {
                positiveButton(getString(R.string.alert_dirty_btn_save)) {
                    //save the data
                    saveCustomerApprovals()
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

    fun saveCustomerApprovals() {
        adapter.let { nonNullAdapter ->
            val costEstimate = OutwardStepMapper.outwardStepToCostEstimate(
                nonNullAdapter.items.asSequence().filter { it is OutwardItem }
                    .map { it as OutwardItem }.toList()
            )
            presenter.saveEstimation(jobCardId, costEstimate)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventsManager.post(
            ApproveEvent(
                ApproveEvent.ESTIMATE_REFRESH, jobCardId
            )
        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_save -> {
                saveCustomerApprovals()
            }
        }
    }

}
