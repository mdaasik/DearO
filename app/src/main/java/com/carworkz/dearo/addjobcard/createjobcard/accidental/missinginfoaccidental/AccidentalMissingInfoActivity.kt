package com.carworkz.dearo.addjobcard.createjobcard.accidental.missinginfoaccidental

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.ActionButtonClickEvent
import com.carworkz.dearo.addjobcard.createjobcard.ICreateJobCardInteraction
import com.carworkz.dearo.addjobcard.createjobcard.accidental.AccidentalFragment
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityAccidentalMissingInfoBinding
import com.carworkz.dearo.interactionprovider.ToolBarImgInteractionProvider
import com.carworkz.dearo.screencontainer.ActionImgScreenContainer
/*import kotlinx.android.synthetic.main.base_layout.**/
class AccidentalMissingInfoActivity : ScreenContainerActivity(), ICreateJobCardInteraction, ToolBarImgInteractionProvider {
    private lateinit var binding: ActivityAccidentalMissingInfoBinding
    private lateinit var status: String
    private lateinit var jobCardId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        status = intent.extras?.getString(ARG_STATUS).toString() // "IN_PROGRESS"
        jobCardId = intent.extras?.getString(ARG_JOBCARD_ID).toString() // "1561615505136610"
        val accidentalFragment = AccidentalFragment.newInstance(jobCardId, status, false)
        supportFragmentManager.beginTransaction().replace(R.id.accidentalMissingParentView, accidentalFragment).commitNowAllowingStateLoss()
    }

    override fun onJobSuccess() {
        dismissProgressBar()
        toast("Saved")
        finish()
    }

    override fun onJobFailure() {
        dismissProgressBar()
        toast("Fail to save")
    }

    override fun onJobVerify() {
        dismissProgressBar()
    }

    override fun createScreenContainer(): ScreenContainer = ActionImgScreenContainer(this)
    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityAccidentalMissingInfoBinding.inflate(layoutInflater)
        return binding
    }


    override fun getProgressView(): View = binding.baseLayout.pbMain

    override fun getNavigationImage(): Int = R.drawable.ic_arrow_back_white_24dp

    override fun onActionBtnClick() = Unit

    override fun onSecondaryActionBtnClick() {
        showProgressBar()
        EventsManager.post(ActionButtonClickEvent())
    }

    override fun getActionBarImage(): Int = 0

    override fun getSecondaryActionBarImage(): Int = R.drawable.ic_save_white_24dp

    override fun getToolBarTitle(): String = "Add Missing Information"

    companion object {
        const val ARG_JOBCARD_ID = "ID"
        const val ARG_STATUS = "STATUS"

        @JvmStatic
        fun getIntent(context: Context, jobCardId: String, status: String) = Intent(context, AccidentalMissingInfoActivity::class.java).apply {
            putExtra(ARG_STATUS, status)
            putExtra(ARG_JOBCARD_ID, jobCardId)
        }
    }
}
