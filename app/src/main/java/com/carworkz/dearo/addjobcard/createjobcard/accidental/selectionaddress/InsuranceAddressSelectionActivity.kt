package com.carworkz.dearo.addjobcard.createjobcard.accidental.selectionaddress

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityInsuranceAddressSelectionBinding
import com.carworkz.dearo.domain.entities.InsuranceCompany
import com.carworkz.dearo.domain.entities.InsuranceCompanyDetails
import com.carworkz.dearo.domain.entities.State
import com.carworkz.dearo.interactionprovider.SelectorInteractionProvider
import com.carworkz.dearo.screencontainer.SelectorScreenContainer
import com.carworkz.dearo.searchabledialog.SearchableDialog
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.activity_insurance_address_selection.*
import kotlinx.android.synthetic.main.base_layout.**/
import javax.inject.Inject

class InsuranceAddressSelectionActivity : ScreenContainerActivity(), InsuranceAddressSelectionContract.View, SelectorInteractionProvider {
    private lateinit var binding: ActivityInsuranceAddressSelectionBinding
    @Inject
    lateinit var presenter: InsuranceAddressSelectionPresenter

    private lateinit var company: InsuranceCompany

    private lateinit var selectedState: State

    private lateinit var states: List<State>

    private var screenContainer = SelectorScreenContainer(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initComponent()
        company = intent.getParcelableExtra(ARG_INSURANCE_COMPANY_SLUG)!!
        presenter.getInsuranceStates()
    }

    override fun displayAddress(address: List<InsuranceCompanyDetails>) {
        Utility.setVisibility(address.isEmpty(), binding.noAddressesFoundView)
        Utility.setVisibility(address.isNotEmpty(), binding.insuranceAddressListParentView)
        binding.insuranceAddressSelectionRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.insuranceAddressSelectionRecyclerView.adapter = InsuranceAddressSelectionAdapter(this, address)
        binding.insuranceAddressSelectionRecyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
    }

    override fun onSelectorClicked() {
        val dialog = SearchableDialog.Builder(this, states)
        dialog.setTitle("Select State")
        dialog.setItemSelectedListener(object : SearchableDialog.OnSearchItemSelectedListener<State> {
            override fun onItemSelectedItem(item: State?) {
                toast("${item?.state} selected")
                selectedState = item!!
                screenContainer.setSelectorText(selectedState.state!!)
                getInsuranceAddressesForState()
            }

            override fun onCancel() {
            }
        })
        dialog.setSelectedItem(selectedState)
        dialog.show()
    }

    override fun onStatesFetched(states: List<State>) {
        this.states = states
        selectedState = this.states.find { it.selected } ?: this.states.first()
        screenContainer.setSelectorText(selectedState.state!!)
        getInsuranceAddressesForState()
    }

    override fun showProgressIndicator() {
        showProgressBar()
    }

    override fun dismissProgressIndicator() {
        dismissProgressBar()
    }

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg) { _, _ -> finish() }
    }

    //    override fun createScreenContainer(): ScreenContainer = InverseSearchScreenContainer(this)
    override fun createScreenContainer(): ScreenContainer = screenContainer
    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityInsuranceAddressSelectionBinding.inflate(layoutInflater)
        return binding
    }


    override fun getProgressView(): View = binding.baseLayout.pbMain

    private fun initComponent() {
        (application as DearOApplication)
                .repositoryComponent
                .COMPONENT(InsuranceAddressSelectionPresenterModule(this))
                .inject(this)
    }

    private fun getInsuranceAddressesForState() {
        company.stateCode = selectedState.stateCode
        presenter.getInsuranceAddress(company)
    }

    fun setResult(address: InsuranceCompanyDetails) {
        val resultIntent = Intent()
        resultIntent.putExtra(TAG, address)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    companion object {

        private const val ARG_INSURANCE_COMPANY_SLUG = "arg_company_slug"
        const val TAG = "tag_insurance_address"
        fun getIntent(context: Context, insurance: InsuranceCompany): Intent {
            return Intent(context, InsuranceAddressSelectionActivity::class.java).apply {
                putExtra(ARG_INSURANCE_COMPANY_SLUG, insurance)
            }
        }
    }
}
