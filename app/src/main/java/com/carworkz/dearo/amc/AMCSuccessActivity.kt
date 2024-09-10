package com.carworkz.dearo.amc

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.amc.amcdetails.AmcBenefitsAdapter
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityAmcSuccessBinding
import com.carworkz.dearo.domain.entities.AMC
import com.carworkz.dearo.domain.entities.AMCPackage
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.interactionprovider.ToolBarImgInteractionProvider
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.screencontainer.ActionImgScreenContainer
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.activity_amc_success.*
import kotlinx.android.synthetic.main.base_layout.**/
import javax.inject.Inject


class AMCSuccessActivity : ScreenContainerActivity(), ToolBarImgInteractionProvider,
    ScreenContainerActivity.OptionItemHandler, AMCContract.View {
    private lateinit var pkg: AMC
    private lateinit var binding: ActivityAmcSuccessBinding

    @Inject
    lateinit var pdfMediator: PdfMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        pkg = intent.getParcelableExtra<AMC>(AMC_PACKAGE) as AMC
        super.onCreate(savedInstanceState)
        initialize()
        setData()
    }

    fun initialize() {
        createComponent()

        binding.viewDetails.setOnClickListener {
            pdfMediator.startAmcInvoicePdf(this, pkg.pdf)
        }

    }

    private fun createComponent() {
        (application as DearOApplication)
            .repositoryComponent
            .COMPONENT(AMCPresenterModule(this))
            .inject(this)
    }

    private fun setData() {
        val chipText = " <b>${pkg.amcName}</b>"

        binding.amcAmount.text = Utility.convertToCurrency(pkg.amount)

        binding.amcCode.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(chipText, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(chipText)
        }

        val timeRemaining = "Expires on <b>${
            Utility.formatDate(
                pkg.expiryDate,
                Utility.TIMESTAMP,
                Utility.DATE_FORMAT_4,
                Utility.TIMEZONE_UTC
            )
        }</b>"

        binding.amcExpiry.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(timeRemaining, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(timeRemaining)
        }

//        subtotal
//        tax
//        discount
        binding.total.text = Utility.convertToCurrency(pkg.amount)
//        val itemsAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pkg.benefits)
        binding.benefitList.adapter = AmcBenefitsAdapter(pkg.benefits)
    }//setData

    override fun createScreenContainer(): ScreenContainer {
        return ActionImgScreenContainer(this)
    }

    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityAmcSuccessBinding.inflate(layoutInflater)
        return binding
    }

    override fun getOptionHandler(): OptionItemHandler {
        return this
    }

    override fun getProgressView(): View = binding.baseLayout.pbMain

    companion object {
        const val AMC_PACKAGE = "amc-package"

        fun getIntent(context: Context, obj: AMC): Intent {
            return Intent(context, AMCSuccessActivity::class.java)
                .putExtra(AMC_PACKAGE, obj)
        }
    }

    override fun getNavigationImage(): Int = R.drawable.ic_close_black_24dp

    override fun onActionBtnClick() {
    }

    override fun onBackPressed() {
        //start amc listing and close this activity
        finish()
    }

    override fun onSecondaryActionBtnClick() {
    }

    override fun getActionBarImage(): Int = 0

    override fun getSecondaryActionBarImage(): Int = 0

    override fun getToolBarTitle(): String = pkg.amcNumber

    override fun onOptionsSelected(item: MenuItem?): Boolean {
        //start amc listing and close this activity
        finish()
        return false
    }

    override fun updateAmcPackages(packages: List<AMCPackage>) {

    }

    override fun errorToast(error: String) {
        toast(error)
    }

    override fun showSuccess(obj: AMC) {

    }

    override fun moveToNextScreen(obj: JobCard) {

    }

    override fun showProgressIndicator() {

    }

    override fun dismissProgressIndicator() {

    }

    override fun showGenericError(errorMsg: String) {
        showGenericError(errorMsg)
    }
}
