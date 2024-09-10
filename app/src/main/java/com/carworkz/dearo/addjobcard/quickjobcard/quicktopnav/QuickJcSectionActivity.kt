package com.carworkz.dearo.addjobcard.quickjobcard.quicktopnav

import android.app.Activity
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
import com.carworkz.dearo.addjobcard.createjobcard.damage.DamageFragment
import com.carworkz.dearo.addjobcard.createjobcard.inspection.InspectionFragment
import com.carworkz.dearo.addjobcard.createjobcard.inventory.InventoryFragment
import com.carworkz.dearo.addjobcard.createjobcard.voice.VoiceFragment
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityMainBinding
import com.carworkz.dearo.databinding.ActivityQuickJcSectionBinding
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.interactionprovider.ToolBarImgInteractionProvider
import com.carworkz.dearo.screencontainer.ActionImgScreenContainer

/*import kotlinx.android.synthetic.main.base_layout.**/

class QuickJcSectionActivity : ScreenContainerActivity(), ToolBarImgInteractionProvider,
    ICreateJobCardInteraction {
    private lateinit var binding: ActivityQuickJcSectionBinding
    private lateinit var jobCard: JobCard
    private lateinit var screenType: String
    private var isViewOnly: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        getIntentData()
        super.onCreate(savedInstanceState)
        when (screenType) {

            SCREEN_SHOPFLOOR -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.sectionContainerView,
                    VoiceFragment.newInstance(
                        isViewOnly,
                        jobCard.id,
                        jobCard.status,
                        jobCard.appointmentId
                    )
                ).commitAllowingStateLoss()
            }

            SCREEN_INVENTORY -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.sectionContainerView,
                    InventoryFragment.newInstance(isViewOnly, jobCard.id)
                ).commitAllowingStateLoss()
            }

            SCREEN_INSPECTION -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.sectionContainerView,
                    InspectionFragment.newInstance(
                        (jobCard.status == JobCard.STATUS_INITIATED || jobCard.status == JobCard.STATUS_IN_PROGRESS).not(),
                        jobCard.id,
                        jobCard.vehicleType
                    )
                ).commitAllowingStateLoss()
            }

            SCREEN_INSURANCE -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.sectionContainerView,
                    AccidentalFragment.newInstance(jobCard.id, jobCard.status, isViewOnly)
                ).commitAllowingStateLoss()
            }

            SCREEN_DAMAGES -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.sectionContainerView,
                    DamageFragment.newInstance(jobCard.status, isViewOnly, jobCard.id)
                ).commitAllowingStateLoss()
            }

            else -> {
                IllegalStateException("Unknown Section Initiated")
            }
        }
    }

    override fun getProgressView(): View {
        TODO("Not yet implemented")
    }

    override fun getNavigationImage(): Int = R.drawable.ic_arrow_back_white_24dp

    override fun onActionBtnClick() {
        showProgressBar()
        EventsManager.post(ActionButtonClickEvent())
    }

    override fun onSecondaryActionBtnClick() = Unit

    override fun getActionBarImage(): Int {
        return when (screenType) {

            SCREEN_SHOPFLOOR -> {
                if (jobCard.status == JobCard.STATUS_INITIATED || jobCard.status == JobCard.STATUS_IN_PROGRESS) {
                    R.drawable.ic_save_white_24dp
                } else {
                    0
                }
            }

            SCREEN_INVENTORY -> {
                if (!isViewOnly) R.drawable.ic_save_white_24dp else 0
            }

            SCREEN_INSPECTION -> {
                if (jobCard.status == JobCard.STATUS_INITIATED || jobCard.status == JobCard.STATUS_IN_PROGRESS) {
                    R.drawable.ic_save_white_24dp
                } else {
                    0
                }
            }

            SCREEN_INSURANCE -> {
                if (!isViewOnly) R.drawable.ic_save_white_24dp else 0
            }

            SCREEN_DAMAGES -> {
                0
            }

            else -> {
                if (!isViewOnly) R.drawable.ic_save_white_24dp else 0
            }
        }
    }

    override fun getSecondaryActionBarImage(): Int = 0

    override fun getToolBarTitle(): String {
        return when (screenType) {
            SCREEN_SHOPFLOOR -> getString(R.string.quick_jc_nav_item_shopfloor)

            SCREEN_INVENTORY -> getString(R.string.quick_jc_nav_item_inventory)

            SCREEN_INSPECTION -> getString(R.string.quick_jc_nav_item_inspection)

            SCREEN_INSURANCE -> getString(R.string.quick_jc_nav_item_insurance)

            SCREEN_DAMAGES -> getString(R.string.quick_jc_nav_item_damages)

            else -> {
                IllegalStateException("Unknown section initiated")
                return ""
            }
        }
    }

    override fun createScreenContainer(): ScreenContainer = ActionImgScreenContainer(this)
    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityQuickJcSectionBinding.inflate(layoutInflater)
       return binding
    }


    /* override fun getProgressView(): View = pb_main*/

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

    private fun getIntentData() {
        if (intent.extras == null)
            IllegalArgumentException("Intent data cannot be empty")
        jobCard = intent.extras?.getParcelable(ARG_JOBCARD)!!
        screenType = intent.extras!!.getString(ARG_SCREEN_TYPE).toString()
        isViewOnly = intent.extras!!.getBoolean(ARG_IS_VIEWONLY)
    }

    companion object {

        const val ARG_SCREEN_TYPE = "arg_screen_type"
        const val ARG_JOBCARD = "arg_jobcard"
        const val ARG_IS_VIEWONLY = "arg_is_viewonly"
        const val SCREEN_SHOPFLOOR = "screen_shopfloor"
        const val SCREEN_INVENTORY = "screen_inventory"
        const val SCREEN_INSPECTION = "screen_inspection"
        const val SCREEN_DAMAGES = "screen_damages"
        const val SCREEN_INSURANCE = "screen_insurance"

        fun getIntent(
            context: Context,
            isViewOnly: Boolean,
            screenType: String,
            jobCard: JobCard
        ): Intent {
            return Intent(context, QuickJcSectionActivity::class.java).apply {
                putExtra(ARG_SCREEN_TYPE, screenType)
                putExtra(ARG_JOBCARD, jobCard)
                putExtra(ARG_IS_VIEWONLY, isViewOnly)
            }
        }
    }
}
