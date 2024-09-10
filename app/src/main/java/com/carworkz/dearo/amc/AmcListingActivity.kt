package com.carworkz.dearo.amc

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.amc.amcdetails.AmcDetailsActivity
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityAmcListingBinding
import com.carworkz.dearo.databinding.ActivityAmcSuccessBinding
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.interactionprovider.ToolBarImgInteractionProvider
import com.carworkz.dearo.screencontainer.DefaultScreenContainer
/*import kotlinx.android.synthetic.main.activity_amc_listing.*
import kotlinx.android.synthetic.main.base_layout.**/
import javax.inject.Inject

class AmcListingActivity : ScreenContainerActivity(), ToolBarImgInteractionProvider, AMCContract.View, AmcPackageListingAdapter.AmcCallbackListener, AmcSellBottomSheet.PurchaseListener {
    private lateinit var binding: ActivityAmcListingBinding
    lateinit var vehicle: Vehicle
    lateinit var customer: Customer

    @Inject
    lateinit var screenTracker: ScreenTracker

    @Inject
    lateinit var presenter: AMCPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        vehicle = intent.getParcelableExtra(VEHICLE)!!
        customer = intent.getParcelableExtra(CUSTOMER)!!
        super.onCreate(savedInstanceState)
        initialize()
        createComponent()
    }

    override fun onResume() {
        super.onResume()
        presenter.suggestAmcPackages(vehicle.id!!)
    }

    private fun initialize() {

    }

    private fun createComponent() {
        (application as DearOApplication)
                .repositoryComponent
                .COMPONENT(AMCPresenterModule(this))
                .inject(this)
    }

    override fun createScreenContainer(): ScreenContainer {
        return DefaultScreenContainer(this)
    }

    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityAmcListingBinding.inflate(layoutInflater)
        return binding
    }

    override fun getProgressView(): View = binding.baseLayout.pbMain

    override fun getNavigationImage(): Int = R.drawable.ic_arrow_back_white_24dp

    override fun onActionBtnClick() {
        toast("Show filter")
    }

    override fun onSecondaryActionBtnClick() {
    }

    override fun getActionBarImage(): Int = R.drawable.ic_filter_list_white_24dp

    override fun getSecondaryActionBarImage(): Int = 0

    override fun getToolBarTitle(): String = "AMC"

    companion object {
        const val VEHICLE = "amc-vehicle"
        const val CUSTOMER = "amc-customer"

        fun getIntent(context: Context, vehicle: Vehicle, customer: Customer): Intent {
            return Intent(context, AmcListingActivity::class.java)
                    .putExtra(VEHICLE, vehicle)
                    .putExtra(CUSTOMER, customer)
        }
    }

    override fun updateAmcPackages(packages: List<AMCPackage>)
    {
        if(packages.isNullOrEmpty())
        {
            binding.tvNoCardsFound.visibility=View.VISIBLE
        }
        else
            binding.amcRecyclerView.adapter = AmcPackageListingAdapter(this, packages, this)
    }

    override fun errorToast(error: String) {
        toast(error)
    }

    override fun showSuccess(obj: AMC) {
        obj.vehicle = vehicle
        obj.customer = customer
        startActivity(AMCSuccessActivity.getIntent(this, obj))
        finish()
    }

    override fun moveToNextScreen(obj: JobCard) {

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

    override fun onPackageSelected(pkg: AMCPackage)
    {
        //on click of sell button this method will get called
        //open AMC details activity
        val amcDetailsIntent= AmcDetailsActivity.getIntent(this,pkg,vehicle,customer)
        startActivity(amcDetailsIntent)
        finish()

//        val modalBottom = AmcSellBottomSheet.newInstance(pkg, vehicle, customer)
//        modalBottom.setPurchaseListener(this)
//        modalBottom.show(supportFragmentManager, "some")
    }

    override fun onPurchaseClicked(amcPackage: AMCPackage) {
        val amcDetails = AMCPurchase()
        amcDetails.vehicleId = vehicle.id
        amcDetails.customerId = customer.id
        amcDetails.customerAddressId = customer.address.last().id!!
        amcDetails.packageId = amcPackage.packageId
        amcDetails.amcId = amcPackage.id
        presenter.purchaseAmc(amcDetails)
    }
}
