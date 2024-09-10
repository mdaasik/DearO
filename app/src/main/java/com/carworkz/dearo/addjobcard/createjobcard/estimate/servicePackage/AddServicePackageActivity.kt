package com.carworkz.dearo.addjobcard.createjobcard.estimate.servicePackage

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.appointments.createappointment.servicePackages.ServicePackageAdapter
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityAddServicePackageBinding
import com.carworkz.dearo.databinding.ActivitySearchPartNumberBinding
import com.carworkz.dearo.domain.entities.Category
import com.carworkz.dearo.domain.entities.ServicePackage
import com.carworkz.dearo.interactionprovider.ToolBarInteractionProvider
import com.carworkz.dearo.screencontainer.SingleTextActionScreenContainer
/*import kotlinx.android.synthetic.main.activity_add_service_package.*
import kotlinx.android.synthetic.main.base_layout.*
import kotlinx.android.synthetic.main.fragment_service_package.view.**/
import javax.inject.Inject

class AddServicePackageActivity : ScreenContainerActivity(), ToolBarInteractionProvider, ServicePackageContract.View, AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityAddServicePackageBinding
    @Inject
    lateinit var presenter: ServicePackagePresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    private var adapter: ServicePackageAdapter? = null
    private var categoryList = mutableListOf<String>()
    private var packageList: List<ServicePackage>? = null
    private lateinit var jobCardId: String // can be invoiceId or jobcardId
    private lateinit var invoiceId: String // can be invoiceId or jobcardId
    private var isInvoice: Boolean = false
    private var existingIds = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (intent.extras?.getString(ARG_JOBCARD_ID) != null) {
            jobCardId = intent.extras!!.getString(ARG_JOBCARD_ID).toString()
            invoiceId = intent.extras!!.getString(ARG_INVOICE_ID).toString()
            isInvoice = intent.extras!!.getBoolean(ARG_IS_FROM_PROFROMA)
        } else
            throw IllegalStateException("Invoice/Jobcard id is mandatory")

        existingIds = intent.extras!!.getStringArrayList(ARG_SERVICE_PKG_EXISTING_ID) as ArrayList<String>
        super.onCreate(savedInstanceState)
        (application as DearOApplication)
                .repositoryComponent
                .COMPONENT(ServicePackagePresenterModule(this))
                .injects(this)
        screenTracker.sendScreenEvent(this, ScreenTracker.SCREEN_ADD_PACKAGES_JC, this::class.java.simpleName)
        categoryList.add(0, getString(R.string.all))
        binding.serviceLayout.filterSpinner.onItemSelectedListener = this
        presenter.getPackages(jobCardId)
        binding.serviceLayout.rvServicePackages.addItemDecoration(DividerItemDecoration(this, LinearLayout.HORIZONTAL))
        binding.serviceLayout.rvServicePackages.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        binding.saveView.setOnClickListener {
            if (adapter?.getSelection()?.isNotEmpty() == true)
                presenter.updatePackages(jobCardId, invoiceId, adapter?.getSelection()?.map { it.packageId!! }
                        ?: mutableListOf(), isInvoice)
            else
                moveToNextScreen(mutableListOf())
        }
    }

    override fun displayPackages(packages: List<ServicePackage>?) {
        if (packages.isNullOrEmpty().not()) {
            binding.serviceLayout.noPackageView.visibility = View.GONE
            binding.serviceLayout.rvServicePackages.visibility = View.VISIBLE
            packageList = packages
            adapter = ServicePackageAdapter(packageList!!)
            binding.serviceLayout.rvServicePackages.adapter = adapter
            adapter?.selectExisting(existingIds)
        } else {
            binding.serviceLayout.noPackageView.visibility = View.VISIBLE
            binding.serviceLayout.rvServicePackages.visibility = View.GONE
        }
    }

    override fun displayFilter(filter: Category?) {
        categoryList = categoryList.subList(0, 1)
        filter?.value?.forEach {
            categoryList.add(it)
        }
        binding.serviceLayout.filterSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categoryList)
    }

    override fun displayPackageError() {
        val alert=AlertDialog.Builder(this)
        alert.setTitle(getString(R.string.sorry))
        alert.setMessage(getString(R.string.no_package))
        alert.setPositiveButton("OK") { _: DialogInterface, _: Int ->
            finish()
        }
        alert.show()
    }

    override fun displayFilteredPackages(filterType: String) {
        if (packageList == null) {
            binding.serviceLayout.noPackageView.visibility = View.VISIBLE
            binding.serviceLayout.rvServicePackages.visibility = View.GONE
            return
        }
        if (filterType == getString(R.string.all)) {
            adapter?.applyFilter(packageList!!)
        } else {
            val list = packageList!!.filter { it.category == filterType }
            if (list.isNotEmpty())
                adapter?.applyFilter(list)
            else {
                binding.serviceLayout.noPackageView.visibility = View.VISIBLE
                binding.serviceLayout.rvServicePackages.visibility = View.GONE
            }
        }
    }

    override fun moveToNextScreen(packageList: List<ServicePackage>) {
        setResult(Activity.RESULT_OK, Intent().apply {
            if (adapter?.getSelection()?.isNotEmpty() == true)
                putExtra(ARG_PACKAGE_LIST, packageList as ArrayList<ServicePackage>)
        })
        finish()
    }

    override fun showProgressIndicator() {
        super.showProgressBar()
    }

    override fun dismissProgressIndicator() {
        super.dismissProgressBar()
    }

    override fun showGenericError(errorMsg: String) {
        toast(errorMsg)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent) {
            binding.serviceLayout.filterSpinner -> {
                presenter.filterPackages(categoryList[position])
            }
        }
    }

    override fun createScreenContainer(): ScreenContainer = SingleTextActionScreenContainer(this)
    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityAddServicePackageBinding.inflate(layoutInflater)
        return binding
    }


    override fun getProgressView(): View = binding.baseLayout.pbMain

    override fun getToolBarTitle(): String = "Add Service Packages"

    override fun getActionBtnTitle(): String = ""

    override fun onActionBtnClick() = Unit

    companion object {
        const val ARG_JOBCARD_ID = "jobcard_id"
        const val ARG_INVOICE_ID = "invoice_id"
        const val ARG_SERVICE_PKG_EXISTING_ID = "selected"
        const val ARG_IS_FROM_PROFROMA = "is_invoice"
        const val ARG_PACKAGE_LIST = "PackageList"

        fun getIntent(context: Context, jobCardId: String, invoiceId: String, isFromProforma: Boolean, existingIds: ArrayList<String>): Intent {
            return Intent(context, AddServicePackageActivity::class.java).apply {
                putExtra(ARG_JOBCARD_ID, jobCardId)
                putExtra(ARG_INVOICE_ID, invoiceId)
                putExtra(ARG_IS_FROM_PROFROMA, isFromProforma)
                putStringArrayListExtra(ARG_SERVICE_PKG_EXISTING_ID, existingIds)
            }
        }
    }
}
