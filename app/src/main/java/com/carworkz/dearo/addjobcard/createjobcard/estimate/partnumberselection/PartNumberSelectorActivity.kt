package com.carworkz.dearo.addjobcard.createjobcard.estimate.partnumberselection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.estimate.partnumberselection.searchpartnumber.SearchPartNumberActivity
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityDigitalSignatureBinding
import com.carworkz.dearo.databinding.ActivityPartNumberSelectorBinding
import com.carworkz.dearo.domain.entities.PartNumber
import com.carworkz.dearo.interactionprovider.ToolBarImgInteractionProvider
import com.carworkz.dearo.screencontainer.ActionImgScreenContainer
/*import kotlinx.android.synthetic.main.activity_part_number_selector.*
import kotlinx.android.synthetic.main.base_layout.**/

class PartNumberSelectorActivity : ScreenContainerActivity(), ToolBarImgInteractionProvider {
    private lateinit var binding: ActivityPartNumberSelectorBinding
    private var partNumberList: List<PartNumber> = ArrayList()
    private lateinit var partName: String
    private lateinit var jobCardId: String
    private var partId: String? = null
    private var vehicleType: String? = null
    private var isSearchEnabled:Boolean=true

    override fun onCreate(savedInstanceState: Bundle?) {
        partNumberList = intent.extras?.getParcelableArrayList<PartNumber>(PartNumberSelectionAdapter.ARG_PART_NUMBER) as List<PartNumber>
        partName = intent.extras?.getString(ARG_PART_NAME).toString()
        jobCardId = intent.extras?.getString(ARG_JOB_CARD_ID).toString()
        partId = intent.extras?.getString(ARG_PART_ID)
        vehicleType = intent.extras?.getString(ARG_VEHICLE_TYPE)
        isSearchEnabled = intent.extras?.getBoolean(ARG_IS_SEARCH_ENABLED) == true
        super.onCreate(savedInstanceState)
        binding.partNumberView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.partNumberView.adapter = PartNumberSelectionAdapter(partNumberList, true)
    }

    override fun createScreenContainer(): ScreenContainer = ActionImgScreenContainer(this)
    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityPartNumberSelectorBinding.inflate(layoutInflater)
        return binding
    }


    override fun getProgressView(): View = binding.pbMain

    override fun getToolBarTitle(): String = partName

    override fun getNavigationImage(): Int = R.drawable.ic_close_black_24dp

    override fun onSecondaryActionBtnClick() = Unit

    override fun getActionBarImage(): Int = if(isSearchEnabled)R.drawable.ic_search_black_24dp else 0//

    override fun getSecondaryActionBarImage(): Int = 0

    override fun onActionBtnClick() {
        val intent = SearchPartNumberActivity.getIntent(this, partName, jobCardId, null, vehicleType)
        intent.flags = Intent.FLAG_ACTIVITY_FORWARD_RESULT
        startActivity(intent)
        finish()
    }

    companion object {
        const val ARG_PART_NAME = "arg_part_name"
        const val ARG_PART_ID = "arg_part_id"
        const val ARG_JOB_CARD_ID = "arg_job_card_id"
        const val ARG_VEHICLE_TYPE = "arg_vehicle_type"
        const val ARG_IS_SEARCH_ENABLED = "arg_search_enabled"

        fun getIntent(context: Context, jobCardId: String, partId: String?, partName: String, partNumber: ArrayList<PartNumber>, vehicleType: String?,isSearchEnabled:Boolean): Intent {
            return Intent(context, PartNumberSelectorActivity::class.java).apply {
                putExtra(PartNumberSelectionAdapter.ARG_PART_NUMBER, partNumber)
                putExtra(ARG_PART_NAME, partName)
                putExtra(ARG_PART_ID, partId)
                putExtra(ARG_JOB_CARD_ID, jobCardId)
                putExtra(ARG_VEHICLE_TYPE, vehicleType)
                putExtra(ARG_IS_SEARCH_ENABLED, isSearchEnabled)
            }
        }
    }
}
