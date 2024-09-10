package com.carworkz.dearo.othersyshistory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityCustomerDetailsOtcBinding
import com.carworkz.dearo.databinding.ActivityOtherSysHistoryBinding
import com.carworkz.dearo.domain.entities.CustomerVehicleDetails
import com.carworkz.dearo.domain.entities.History
import com.carworkz.dearo.interactionprovider.ToolBarImgInteractionProvider
import com.carworkz.dearo.mycustomers.cutomervehiclehistory.CustomerVehicleHistoryActivity
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.screencontainer.NavigationDrawerScreenContainer
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.activity_other_sys_history.**/
import javax.inject.Inject

class OtherSysHistoryActivity : ScreenContainerActivity(),
        ToolBarImgInteractionProvider,
        OtherHistoryContract.View,
        OtherHistoryInteraction,
        View.OnClickListener {
    private lateinit var binding: ActivityOtherSysHistoryBinding
    private lateinit var customerVehicleDetails: CustomerVehicleDetails
    private lateinit var screenContainer: NavigationDrawerScreenContainer
    private var delay = 0L

    @Inject
    lateinit var presenter: OtherHistoryPresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    @Inject
    lateinit var pdfMediator: PdfMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        customerVehicleDetails = intent.extras?.getParcelable(CustomerVehicleHistoryActivity.ARG_CARD)!!
        super.onCreate(savedInstanceState)
        binding.registrationNumber.text = customerVehicleDetails.vehicle.registrationNumber
        binding.registrationName.text = "${customerVehicleDetails.vehicle.makeName} - ${customerVehicleDetails.vehicle.modelName} - ${customerVehicleDetails.vehicle.fuelType}"
        initComponent()
        binding.historyView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.historyView.setItemViewCacheSize(20)
        binding.historyView.isDrawingCacheEnabled = true
        binding.historyView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        screenTracker.sendScreenEvent(this, ScreenTracker.SCREEN_CUSTOMER_CARD_DETAILS, this.javaClass.name)

    }
    override fun onResume() {
        super.onResume()
        Handler().postDelayed({ presenter.getOtherSysHistory(customerVehicleDetails.vehicle.registrationNumber, customerVehicleDetails.customer.mobile) }, delay)
    }

    override fun createScreenContainer(): ScreenContainer {
        screenContainer = NavigationDrawerScreenContainer(this)
        return screenContainer
    }

    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityOtherSysHistoryBinding.inflate(layoutInflater)
        return binding
    }


    override fun getProgressView(): View = ProgressBar(this)

    override fun getNavigationImage(): Int = R.drawable.ic_arrow_back_white_24dp

    override fun onActionBtnClick()
    {
        Utility.makeCall(this, customerVehicleDetails.customer.mobile.toString())
    }

    override fun onSecondaryActionBtnClick() {
        toast("onSecondaryActionBtnClick")
    }

    override fun getActionBarImage(): Int = R.drawable.ic_call_white_24dp

    override fun getSecondaryActionBarImage(): Int = 0

    override fun getToolBarTitle(): String = customerVehicleDetails.customer.name

    override fun displayHistory(obj: CustomerVehicleDetails?)
    {
        if (obj?.history != null) {
            Utility.setVisibility(true,  binding.historyView)
            binding.historyView.adapter = OtherHistoryAdapter(obj.history!!, customerVehicleDetails.vehicle.registrationNumber, this)
        } else {
            toast("There is no old invoices")
        }
    }

    override fun showProgressIndicator() = super.showProgressBar()

    override fun dismissProgressIndicator() = super.dismissProgressBar()

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg)
    }

    override fun onClick(p0: View?) {

    }

    private fun initComponent() {
        (application as DearOApplication)
                .repositoryComponent
                .COMPONENT(OtherHistoryPresenterModule(this))
                .inject(this)
    }

    companion object {
        const val ARG_CARD = "CARD"
        const val ARG_HISTORY = true
        const val REQUEST_CODE = 1001

        fun getIntent(context: Context, card: CustomerVehicleDetails): Intent {
            return Intent(context, OtherSysHistoryActivity::class.java).apply {
                putExtra(ARG_CARD, card)
            }
        }
    }
    override fun showDetails(details: History) {
        val carType="${customerVehicleDetails.vehicle.makeName} - ${customerVehicleDetails.vehicle.modelName} - ${customerVehicleDetails.vehicle.fuelType}"
        val intent = OtherSysHistoryDetailsActivity.getIntent(this, details,customerVehicleDetails.vehicle.registrationNumber,carType,customerVehicleDetails.customer.name)
        startActivity(intent)
    }
}