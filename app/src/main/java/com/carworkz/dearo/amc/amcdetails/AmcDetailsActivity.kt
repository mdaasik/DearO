package com.carworkz.dearo.amc.amcdetails

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.CreateJobCardActivity
import com.carworkz.dearo.amc.AMCPurchase
import com.carworkz.dearo.amc.AMCSuccessActivity
import com.carworkz.dearo.amc.amcsolddetails.AmcDetailsContract
import com.carworkz.dearo.amc.amcsolddetails.AmcDetailsPresenter
import com.carworkz.dearo.amc.amcsolddetails.AmcDetailsPresenterModule
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityAmcDetailsBinding
import com.carworkz.dearo.databinding.ActivityAmcListingBinding
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.interactionprovider.ToolBarInteractionProvider
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.screencontainer.SingleTextActionScreenContainer
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.activity_amc_details.**/
import javax.inject.Inject


class AmcDetailsActivity : ScreenContainerActivity(),
        ToolBarInteractionProvider,
        AmcDetailsContract.View,
        View.OnClickListener {
    private lateinit var binding: ActivityAmcDetailsBinding
    @Inject
    lateinit var presenter: AmcDetailsPresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    @Inject
    lateinit var pdfMediator: PdfMediator

    private lateinit var amcPkg: AMCPackage
    private lateinit var vehicle: Vehicle
    private lateinit var customer: Customer
    val amcDetails = AMCPurchase()
    private var delay = 0L

    private var enableDiscount = false
    private val discountTypes = arrayListOf("in (\u20B9)", "in (%)")
    val handler = Handler(Looper.getMainLooper())
    var discountRunnable: Runnable? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        amcPkg = intent.extras?.get(ARG_AMC) as AMCPackage
        customer = intent.extras?.get(ARG_CUSTOMER) as Customer
        vehicle = intent.extras?.get(ARG_VEHICLE) as Vehicle

        initComponent()
        super.onCreate(savedInstanceState)

        binding.next.setOnClickListener(this)
        initDiscount()
        setData()
    }

    private fun setData() {
        ViewCompat.setNestedScrollingEnabled(binding.amcBenefits, false);
        ViewCompat.setNestedScrollingEnabled(binding.termsAndCondition, false);
        binding.totalAmount.text = Utility.convertToCurrency(amcPkg.price)
        binding.amcAmount.text = Utility.convertToCurrency(amcPkg.price)

        //AMC Benefits
        binding.amcBenefits.adapter = AmcBenefitsAdapter(amcPkg.benefits)

        //Service Instruction

        //Terms and condition
        binding.termsAndCondition.adapter = AmcTermsAdapter(amcPkg.terms)

        binding. discountView.setText("0.0")
    }


    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == CreateJobCardActivity.RESULT_CODE && requestCode == REQUEST_CODE) {
            delay = 1000L
        }
    }

    private val listener: OnItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            (parent.getChildAt(0) as TextView).setTextColor(Color.WHITE)
            binding.discountView.apply {
                setText("0.0")
                selectAll()
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    private fun initDiscount() {
        amcDetails.discount?.mode = Discount.MODE_PRICE
        binding.discountListView.onItemSelectedListener = listener
        binding.discountListView.adapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, discountTypes)

        binding.discountView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (discountRunnable!=null) {
                    handler.removeCallbacks(discountRunnable!!)
                }
                discountRunnable = Runnable {

                    if (binding.discountListView.selectedItem.toString() == discountTypes[0]) {
                        //it means price
                        amcDetails.discount?.mode = Discount.MODE_PRICE

                    } else {
                        amcDetails.discount?.mode = Discount.MODE_PERCENTAGE

                    }

                    if (binding.discountView.text.toString().isEmpty()) {
                        binding.discountView.apply {
                            setText("0.0")
                            selectAll()
                        }
                    }

                    if (Utility.isValidDecimal(binding.discountView.text.toString()) &&
                            ((amcDetails.discount?.mode == Discount.MODE_PRICE && binding.discountView.text.toString().toDouble() <= amcPkg.price) || (amcDetails.discount?.mode == Discount.MODE_PERCENTAGE && binding.discountView.text.toString().toDouble() <= 100))) {
                        binding.discountView.error = null
                        amcDetails.discount.amount = binding.discountView.text.toString().toDouble()

                        calculateDiscount()

                    } else if (Utility.isValidDecimal(binding.discountView.text.toString())) {
                        binding.discountView.error = "Discount can't be more than price"

                    }
                }
                handler.postDelayed(discountRunnable!!, PRICE_CHANGE_INTERVAL)
            }
        })
    }

    private fun calculateDiscount() {

        //here we have to calculate as per 'includeTax' flag
        if(amcPkg.includeTax) {
            val withoutTax = amcPkg.price / 1.18

            val itemDiscountAmount =
                if (amcDetails.discount?.amount == 0.0) 0.0 else (if (amcDetails.discount?.mode == Discount.MODE_PRICE) amcDetails.discount.amount else (withoutTax * amcDetails.discount?.amount!! / 100))
            val itemAmountAfterDiscount = withoutTax - itemDiscountAmount

            val withTax =
                if (amcDetails.discount.amount == 0.0) amcPkg.price else itemAmountAfterDiscount + (itemAmountAfterDiscount * 18 / 100)
            binding.totalAmount.text = Utility.convertToCurrency(withTax)
        }
        else
        {
            val itemDiscountAmount =
                if (amcDetails.discount?.amount == 0.0) 0.0 else (if (amcDetails.discount?.mode == Discount.MODE_PRICE) amcDetails.discount.amount else (amcPkg.price * amcDetails.discount?.amount!! / 100))
            val itemAmountAfterDiscount = amcPkg.price - itemDiscountAmount
            binding.totalAmount.text = Utility.convertToCurrency(itemAmountAfterDiscount)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.next -> {

                //don't forget to add discount node here
                if (binding.discountListView.selectedItem.toString() == discountTypes[0]) {
                    //it means price
                    amcDetails.discount?.mode = Discount.MODE_PRICE

                } else {
                    amcDetails.discount?.mode = Discount.MODE_PERCENTAGE
                }

                //Validate first

                amcDetails.vehicleId = vehicle.id
                amcDetails.customerId = customer.id
                amcDetails.customerAddressId = customer.address.last().id!!
                amcDetails.packageId = amcPkg.packageId
                amcDetails.amcId = amcPkg.id

                if (isDiscountValid(amcDetails.discount?.mode)) {
                    presenter.purchaseAmc(amcDetails)
                } else {
                    showGenericError("Enter valid discount")
                }
            }
        }
    }

    private fun isDiscountValid(discountMode: String?): Boolean {
        return Utility.isValidDecimal(binding.discountView.text.toString()) &&
                ((discountMode == Discount.MODE_PRICE && binding.discountView.text.toString().toDouble() <= amcPkg.price) || (discountMode == Discount.MODE_PERCENTAGE && binding.discountView.text.toString().toDouble() <= 100))
    }

    override fun createScreenContainer(): ScreenContainer {
        return SingleTextActionScreenContainer(this)
    }

    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityAmcDetailsBinding.inflate(layoutInflater)
        return binding
    }

    override fun getToolBarTitle(): String = amcPkg.name

    override fun getActionBtnTitle(): String = ""

    override fun onActionBtnClick() = Unit


    override fun getProgressView(): View = ProgressBar(this)

    override fun showProgressIndicator() = super.showProgressBar()

    override fun dismissProgressIndicator() = super.dismissProgressBar()

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun showDetails(amcDetails: SoldAMCDetails) {

    }


    override fun moveToNextScreen(obj: JobCard) {

    }

    override fun serviceDateError() {

    }

    override fun showSuccess(obj: AMC) {
        obj.vehicle = vehicle
        obj.customer = customer
        startActivity(AMCSuccessActivity.getIntent(this, obj))
        finish()
    }

    private fun initComponent() {
        (application as DearOApplication)
                .repositoryComponent
                .COMPONENT(AmcDetailsPresenterModule(this))
                .inject(this)
    }

    companion object {
        const val PRICE_CHANGE_INTERVAL = 1500L // 1.5 sec
        const val ARG_AMC = "AMC"
        const val ARG_CUSTOMER = "CUSTOMER"
        const val ARG_VEHICLE = "VEHICLE"
        const val REQUEST_CODE = 1000

        fun getIntent(context: Context, amcPkg: AMCPackage, vehicle: Vehicle, customer: Customer): Intent {
            return Intent(context, AmcDetailsActivity::class.java).apply {
                putExtra(ARG_AMC, amcPkg)
                putExtra(ARG_CUSTOMER, customer)
                putExtra(ARG_VEHICLE, vehicle)
            }
        }

    }
}
