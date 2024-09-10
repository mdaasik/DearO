package com.carworkz.dearo.invoices.addItem.labour

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.content.res.AppCompatResources
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.customviews.toggle.interfaces.OnToggledListener
import com.carworkz.dearo.customviews.toggle.model.ToggleableView
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.databinding.ActivityAddItemBinding
import com.carworkz.dearo.databinding.ActivityAmcDetailsBinding
import com.carworkz.dearo.domain.entities.Discount
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.domain.entities.Labour
import com.carworkz.dearo.extensions.*
import com.carworkz.dearo.interactionprovider.ToolBarImgInteractionProvider
import com.carworkz.dearo.outwarding.helper.OutwardItem
import com.carworkz.dearo.screencontainer.ActionImgScreenContainer
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.activity_add_item.*
import kotlinx.android.synthetic.main.activity_add_item.focSwitch
import kotlinx.android.synthetic.main.base_layout.*
import kotlinx.android.synthetic.main.layout_add_part_new.*
import kotlinx.android.synthetic.main.row_labour_proforma.view.**/
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kush on 13/9/17.
 */
class AddLabourActivity : ScreenContainerActivity(),
        AddLabourContract.View,
        ToolBarImgInteractionProvider,
        TextWatcher, View.OnFocusChangeListener, OnToggledListener {
    private lateinit var binding: ActivityAddItemBinding
    @Inject
    lateinit var presenter: AddLabourPresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    private var labour = Labour()
    private var canDelete = false
    private var canReduceDiscount = false
    private var enableDiscount = true

    // View Components
    private lateinit var invoiceId: String
    private lateinit var jobCardType: String
    private var taskHandler = Handler()
    private lateinit var amtRunnable: Runnable
    private lateinit var rateRunnable: Runnable
    private var discAmt: Double = 0.0
    private var finalAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        getIntentData()
        super.onCreate(savedInstanceState)
        createComponent()
        screenTracker.sendScreenEvent(this, if (canDelete) ScreenTracker.SCREEN_EDIT_LABOUR else ScreenTracker.SCREEN_ADD_LABOUR, this.javaClass.name)
        createRunnable()
        init()
    }

    override fun onDestroy()
    {
        super.onDestroy()
        presenter.detachView()
    }

    override fun createScreenContainer(): ScreenContainer
    {
        return ActionImgScreenContainer(this)
    }

    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        return binding
    }

    override fun getProgressView(): View
    {
        return binding.baseLayout.pbMain
    }

    override fun showProgressIndicator()
    {
        super.showProgressBar()
    }

    override fun dismissProgressIndicator()
    {
        super.dismissProgressBar()
    }

    override fun moveToNextScreen(isDeleted: Boolean)
    {
        finish()
    }

    override fun onLabourSavedSuccess(labour: Labour)
    {
        setResult(Activity.RESULT_OK, Intent().putExtra(Labour.TAG, labour))
        finish()
    }

    override fun onLabourDeleted()
    {
        setResult(Activity.RESULT_CANCELED, Intent().putExtra(Labour.TAG, labour))
        finish()
    }

    override fun setupTotal()
    {
        labour.finalRate = if (Utility.isValidDecimal(binding.finalRateView.text.toString()))
        {
            binding.finalRateView.text.toString().toDouble()
        }
        else
        {
            0.0
        }

        if (labour.rate == 0.0)
        {
            labour.rate = 0.0 + labour.finalRate
        }
        binding.rateView.setText(labour.rate.toString())
        if (labour.rate != 0.0 && labour.rate != labour.finalRate)
        {
            if (labour.finalRate > labour.rate)
            {
                //surcharge
                labour.surcharge = labour.finalRate - labour.rate
                labour.reduction = 0.0
                binding.surchargeView.setText(labour.surcharge.toString())
                binding.reductionView.setText("0.0")
            }
            else
            {
                //reduction
                labour.reduction = labour.rate - labour.finalRate
                labour.surcharge = 0.0
                binding.reductionView.setText(labour.reduction.toString())
                binding.surchargeView.setText("0.0")
            }
        }
        else
        {
            binding.reductionView.setText("0.0")
            binding.surchargeView.setText("0.0")
            labour.reduction = 0.0
            labour.surcharge = 0.0
        }

        val itemSubTotal = if (labour.reduction > 0.0)
        {
            (labour.rate - labour.reduction) * labour.quantity
        }
        else
        {
            (labour.rate + labour.surcharge) * labour.quantity
        }

        val tax = if (SharedPrefHelper.isGstEnabled()) ((labour.tax.cgst + labour.tax.sgst).toFloat() / 100) else 0.0f

//        val itemSubTotal = labour.rate * labour.quantity
        val itemDiscountAmount = if (discAmt == 0.0) 0.0 else (if (labour.discount.mode == Discount.MODE_PRICE) discAmt else (itemSubTotal * (discAmt / 100)))
        val itemAmountAfterDiscount = itemSubTotal - itemDiscountAmount
        val itemTaxAmount = if (SharedPrefHelper.isGstEnabled() && tax > 0.0f) (itemAmountAfterDiscount * tax) else 0.0
        labour.amount = (itemAmountAfterDiscount + itemTaxAmount)
        binding.totalView.text = Utility.round(labour.amount!!, 1).toString()
        binding.finalRateView.isEnabled = ((SharedPrefHelper.getIsLabourRateEditable().not() && SharedPrefHelper.isLabourSurchargeEnabled().not() && labour.finalRate > 0.0)).not()
    }

    override fun onSecondaryActionBtnClick()
    {
        if (validate(SharedPrefHelper.isGstEnabled()))
        {
            saveLabour()
        }
        else
        {
            if (binding.finalRateView.text.toString().isEmpty())
            {
                binding.finalRateView.error = "Rate Cannot be Blank"
                binding.finalRateView.requestFocus()
            }

            if (!Utility.isValidDecimal(binding.amountView.text.toString()))
            {
                binding.amountView.error = "Invalid Discount"
            }

            if (binding.itemNameView.text.toString().isEmpty())
            {
                binding.itemNameView.error = "Name Cannot be Blank"
                binding.itemNameView.requestFocus()
            }

            if (binding.sgstLabourView.text.toString().isEmpty())
            {
                binding.sgstLabourView.error = "Name Cannot be Blank"
                binding.sgstLabourView.requestFocus()
            }

            if (binding.cgstLabourView.text.toString().isEmpty())
            {
                binding.cgstLabourView.error = "Name Cannot be Blank"
                binding.cgstLabourView.requestFocus()
            }

            if (binding.sacView.text.toString().isEmpty())
            {
                binding.sacView.error = "SAC Cannot be Blank"
                binding.sacView.requestFocus()
            }

            if (binding.totalView.text.toString().toFloat() < 0f)
            {
                toast(getString(R.string.total_warning))
            }
        }
    }

    private fun validate(isGst: Boolean): Boolean
    {
        if (isGst)
        {
            return when
            {
                binding.finalRateView.text.toString().isEmpty()                                                -> false
                binding.sacView.text.toString().isEmpty()                                                      -> false
                !Utility.isValidDecimal(binding.amountView.text.toString())                                    -> binding.amountView.text.toString().isEmpty()
                (binding.sgstLabourView.text.toString().isEmpty() && binding.cgstLabourView.text.toString().isEmpty()) -> false
                binding.totalView.text.toString().toFloat() < 0f                                               -> false
                else                                                                                   -> true
            }
        }
        else
        {
            return when
            {
                binding.finalRateView.text.toString().isEmpty()             -> false
                binding.totalView.text.toString().toFloat() < 0f            -> false
                !Utility.isValidDecimal(binding.amountView.text.toString()) -> binding.amountView.text.toString().isEmpty()
                else                                                -> true
            }
        }
    }

    override fun onActionBtnClick()
    {
        if (checkIfNetworkAvailable())
        {
            alert(getString(R.string.delete_warning), getString(R.string.delete_warning_title)) {
                yesButton {
                    presenter.deleteLabour(invoiceId, labour.uid!!)
                }

                noButton {
                    // ok Cool XD
                }
            }.show()
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean)
    {
        when (v)
        {
            binding.amountView ->
            {
                if (hasFocus)
                {
                    if (binding.amountView.text.toString().isNotEmpty() && binding.amountView.text.toString() == "0.0")
                        binding.amountView.text.clear()
                }
            }
            binding.rateView   ->
            {
                if (hasFocus)
                {
                    if (binding.rateView.text.toString().isNotEmpty() && binding.rateView.text.toString() == "0.0")
                        binding.rateView.text.clear()
                }
            }
        }
    }

    override fun afterTextChanged(s: Editable?)
    {
        when (s?.hashCode())
        {
            binding.amountView.text.hashCode()     ->
            {
                if (s.toString() == labour.amount.toString())
                    return
                taskHandler.removeCallbacksAndMessages(amtRunnable)
                taskHandler.postDelayed(amtRunnable, DELAY)
            }

            binding.finalRateView.text.hashCode()  ->
            {
                taskHandler.removeCallbacksAndMessages(rateRunnable)
                taskHandler.postDelayed(rateRunnable, DELAY)
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
    {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
    {
    }

    override fun getSecondaryActionBarImage(): Int
    {
        return R.drawable.ic_save_black_24dp
    }

    override fun getActionBarImage(): Int
    {
        return if (canDelete)
        {
            R.drawable.ic_delete_black_24dp
        }
        else
        {
            0
        }
    }

    override fun getNavigationImage(): Int
    {
        return R.drawable.ic_clear_white_24dp
    }

    override fun getToolBarTitle(): String
    {
        return if (canDelete)
        {
            getString(R.string.edit_item)
        }
        else
        {
            getString(R.string.add_item_title)
        }
    }

    override fun showGenericError(errorMsg: String)
    {
        displayError(errorMsg)
    }

    private fun createRunnable()
    {
        amtRunnable = Runnable {
            when
            {
                labour.discount.mode == MODE_PERCENTAGE && Utility.isValidDecimal(binding.amountView.text.toString()) ->
                {
                    if (binding.amountView.text.toString().toFloat() <= 100f && binding.amountView.text.toString().isNotEmpty())
                    {
                        if (canReduceDiscount)
                        {
                           /* if (amountView.text.toString().toDouble() < discAmt)
                            {
                                if (amountView.text.toString().toDouble() < labour.discount.amount)
                                {
                                    toast("Discount cannot be less than ${discAmt}%")
                                }
                                else
                                {
                                    discAmt = amountView.text.toString().toDouble()
                                    setupTotal()
                                }
                            }
                            else
                            {
                                discAmt = amountView.text.toString().toDouble()
                                setupTotal()
                            }*/
                            discAmt = binding.amountView.text.toString().toDouble()
                            setupTotal()
                        }
                        else
                        {
                            discAmt = binding.amountView.text.toString().toDouble()
                            setupTotal()
                        }
                    }
                    else
                    {
                        toast("Discount cannot be more than 100%")
                        binding.amountView.text.clear()
                    }
                }

                labour.discount.mode == MODE_PRICE && Utility.isValidDecimal(binding.amountView.text.toString())      ->
                {
                    if (canReduceDiscount)
                    {
                        if (binding.amountView.text.toString().toDouble() < discAmt)
                        {
                            if (binding.amountView.text.toString().toDouble() < labour.discount.amount)
                            {
                                toast("Discount cannot be less than ${discAmt}")
                            }
                            else
                            {
                                discAmt = binding.amountView.text.toString().toDouble()
                                setupTotal()
                            }
                        }
                        else
                        {
                            discAmt = binding.amountView.text.toString().toDouble()
                            setupTotal()
                        }
                    }
                    else
                    {
                        discAmt = binding.amountView.text.toString().toDouble()
                        setupTotal()
                    }
                }

                else                                                                                          ->
                {
                    if (binding.finalRateView.text.toString().isNotEmpty())
                        toast("Invalid Text Input")
                    discAmt = 0.0
                    setupTotal()
                }
            }
        }

        rateRunnable = Runnable {
            val s = binding.finalRateView.text.toString()

            if (s.isNotEmpty() == true && Utility.isValidDecimal(s))
            {
                val finalRate = Utility.round(s.toDouble(), 1)

                if (labour.finalRate != finalRate)
                {
                    labour.finalRate = finalRate
                    labour.price = finalRate
                    if ((labour.discount.mode == Discount.MODE_PERCENTAGE && discAmt > 100) || finalRate < discAmt)
                    {
                        binding.finalRateView.error = binding.finalRateView.context.getString(R.string.discount_gt_price)
                    }
                    else if (labour.rate > 0 && finalRate < 1.0)
                    {
                        binding.finalRateView.error = "Final Rate can not be empty or 0"
                    }
                    else
                    {
                        if ((jobCardType != JobCard.TYPE_ACCIDENTAL))
                        {
                            //if all 3 config are false then final rate and rate should be same
                            if (SharedPrefHelper.getIsLabourRateEditable().not() && SharedPrefHelper.isLabourSurchargeEnabled().not() && SharedPrefHelper.isLabourReductionEnabled().not())
                            {
                                binding.finalRateView.error = null
                                labour.finalRate = finalRate
                                labour.rate = finalRate + 0.0
                                binding.finalRateView.isEnabled = false
                                setupTotal()
                            }
                            //if surcharge is true then final rate and rate should be same
                            else if (SharedPrefHelper.getIsLabourRateEditable().not() && SharedPrefHelper.isLabourSurchargeEnabled() && SharedPrefHelper.isLabourReductionEnabled().not())
                            {
                                labour.finalRate = finalRate
                                if (labour.rate == 0.0)
                                {
                                    labour.rate = finalRate + 0.0
                                }
                                if (labour.rate != 0.0 && finalRate < labour.rate)
                                {
                                    binding.finalRateView.error = "Final rate should be greater than or equal to " + labour.rate
                                }
                                else
                                {
                                    binding.finalRateView.error = null
                                    setupTotal()
                                }
                            }
                            //if labour rate editable is true then rate will not change but it should calculate properly there should not be any restriction in case of accidental JC
                            else if (SharedPrefHelper.getIsLabourRateEditable() && SharedPrefHelper.isLabourSurchargeEnabled().not() && SharedPrefHelper.isLabourReductionEnabled().not())
                            {
                                binding.finalRateView.error = null
                                labour.finalRate = finalRate
                                labour.rate = finalRate + 0.0
                                setupTotal()
                            }
                            //if rate is 0 then update it to final rate and calculate surcharge and
                            else if (SharedPrefHelper.getIsLabourRateEditable().not() && SharedPrefHelper.isLabourSurchargeEnabled() && SharedPrefHelper.isLabourReductionEnabled())
                            {
                                binding.finalRateView.error = null
                                labour.finalRate = finalRate
                                if (labour.rate == 0.0)
                                {
                                    labour.rate = finalRate
                                }
                                setupTotal()
                            }
                            else if (finalRate < labour.rate)
                            {
                                binding.finalRateView.error = "Final rate should be greater than or equal to " + labour.rate
                                labour.finalRate = finalRate
                            }
                        }
                        else
                        {
                            if (SharedPrefHelper.getIsLabourRateEditable() && SharedPrefHelper.isLabourSurchargeEnabled().not() && SharedPrefHelper.isLabourReductionEnabled().not())
                            {
                                binding.finalRateView.error = null
                                labour.finalRate = finalRate
                                labour.rate = finalRate + 0.0
                                setupTotal()
                            }
                            else
                            {
                                binding.finalRateView.error = null
                                labour.finalRate = finalRate
                                setupTotal()
                            }
                        }
                    }
                }
            }
            else
            {
                if (s.isEmpty() && labour.rate > 0)
                {
                    binding.finalRateView.error = "Final Rate can not be empty or 0"
                }
                else if (s.isNotEmpty() && !Utility.isValidDecimal(s))
                {
                    binding.finalRateView.error = "Invalid Price"
                }
            }
        }
    }

    private fun getIntentData()
    {
        canDelete = intent.extras!!.getBoolean(ARG_IS_DELETABLE, false)
        canReduceDiscount = intent.extras!!.getBoolean(ARG_CAN_REDUCE_DISCOUNT, false)
        labour = intent.extras!!.getParcelable(ARG_LABOUR)!!
        invoiceId = intent.extras!!.getString(ARG_INVOICE_ID)!!
        jobCardType = intent.extras!!.getString(ARG_JC_TYPE)!!
        enableDiscount = intent.extras!!.getBoolean(ARG_ENABLE_DISCOUNT)
        discAmt = labour.discount.amount
    }

    private fun createComponent()
    {
        (application as DearOApplication)
                .repositoryComponent
                .COMPONENT(AddLabourPresenterModule(this))
                .inject(this)
    }

    private fun saveLabour()
    {
        if (canReduceDiscount)
        {
//            if (discAmt < labour.discount.amount)
//            {
//                toast("Discount cannot be less than ${labour.discount.amount}%")
//                return
//            }
            labour.discount.amount = discAmt
        }
        else
        {
            labour.discount.amount = discAmt
        }
        labour.tax.sac = binding.sacView.text.toString()
        val errorMsg = isValidEstimateData(labour)
        if (errorMsg.isEmpty())
        {
            if (checkIfNetworkAvailable())
            {
                labour.price=0.0
                presenter.saveLabour(invoiceId, labour)
            }
        }
        else
        {
            showGenericError(errorMsg)
        }
    }

    private fun isValidEstimateData(labour: Labour): String
    {
        var msg = ""
        if ((labour.discount.mode == Discount.MODE_PERCENTAGE && labour.discount.amount > 100) || (labour.discount.mode == Discount.MODE_PRICE && labour.finalRate < labour.discount.amount))
        {
            msg = getString(R.string.discount_gt_rate)
        }
        else
        {
            if ((jobCardType != JobCard.TYPE_ACCIDENTAL) && labour.finalRate < labour.rate)
            {
                msg = "Final rate should be greater than or equal to Rate"
            }
            else if (labour.rate > 0 && labour.finalRate < 1)
            {
                msg = "Final Rate must be greater than or equal to Rate"
            }
            //here another check is important in case of accidental JC
            //if insurance claim is checked then only user can do reduction
        }
        return msg
    }

    @SuppressLint("SetTextI18n")
    private fun init()
    {
        //Conditional visibility
        //For Periodic/Major/Minor/AMC-SMC JC do not show "Reduction" field on Estimate and Proforma screen
        if (jobCardType != JobCard.TYPE_ACCIDENTAL)
        {
            binding.reductionLayout.visibility = View.GONE
        }

        //if surcharge config is disabled hide the field
        if (SharedPrefHelper.isLabourSurchargeEnabled().not())
        {
            binding.surchargeLayout.visibility = View.GONE
        }
        //if reduction config is disabled hide the field
        if (SharedPrefHelper.isLabourReductionEnabled().not())
        {
            binding.reductionLayout.visibility = View.GONE
        }

        binding.labourContainer.visibility = View.VISIBLE
        binding.itemImageView.image = AppCompatResources.getDrawable(this, R.drawable.ic_labour_add_white)
        binding.categoryView.text = getString(R.string.labour)
        binding.itemNameView.text = labour.text
        Timber.d(labour.toString())

        if (SharedPrefHelper.isGstEnabled())
        {
            binding.sacView.setText("998729")
            labour.tax.sac = "998729"
            binding.sacView.isFocusable = false
            binding.sacView.isEnabled = false

            //If the workshop is composite enabled then agst and cgst will be 3% each
            //check if workshop is composit enabled
            if (SharedPrefHelper.isCompositeEnabled())
            {
                binding.sgstLabourView.setText("3")
                binding.cgstLabourView.setText("3")
                labour.tax.sgst = 3.0
                labour.tax.cgst = 3.0
            }
            else
            {
                binding.sgstLabourView.setText("9")
                binding.cgstLabourView.setText("9")
                labour.tax.sgst = 9.0
                labour.tax.cgst = 9.0
            }

            binding.sgstLabourView.isEnabled = false
            binding.sgstLabourView.isFocusable = false
            binding.cgstLabourView.isEnabled = false
            binding.cgstLabourView.isFocusable = false
        }
        else
        {
            binding.sgstLabourView.setText("0")
            binding.cgstLabourView.setText("0")
            binding.sacView.setText("N/A")
            labour.tax.sgst = 0.0
            labour.tax.cgst = 0.0
            binding.sgstLabourView.isEnabled = false
            binding.sgstLabourView.isFocusable = false
            binding.cgstLabourView.isEnabled = false
            binding.cgstLabourView.isFocusable = false
            binding.sacView.isEnabled = false
        }

        if (labour.rate != 0.0)
        {
            binding.rateView.setText(labour.rate.toString())
//            rateView.isEnabled = SharedPrefHelper.getIsLabourRateEditable()
        }

        binding.finalRateView.setText(labour.finalRate.toString())
        /* if (SharedPrefHelper.getMiscId() == labour.id)
         {
             rateView.isEnabled = SharedPrefHelper.isMiscEnabled()
         }*/

        //final rate should not be editable if is disable from config
        binding.finalRateView.isEnabled = ((SharedPrefHelper.getIsLabourRateEditable().not() && SharedPrefHelper.isLabourSurchargeEnabled().not() && labour.finalRate > 0.0)).not()
        //LABOUR RATE REDUCTION
        binding.surchargeView.isEnabled = false
        binding.surchargeView.isFocusable = false
        binding.reductionView.isEnabled = false
        binding.reductionView.isFocusable = false
        binding.rateView.isEnabled = false
        binding.rateView.isFocusable = false

        initDiscount()
        setupTotal()
//        sgstLabourView.addTextChangedListener(this)
        binding.finalRateView.addTextChangedListener(this)
//        cgstLabourView.addTextChangedListener(this)
//        rateView.onFocusChangeListener = this

        if(SharedPrefHelper.getFOC())
        {
            binding.focSwitch.setOnToggledListener(this)
            binding.focSwitch.isEnabled = true
            binding.focSwitch.visibility=View.VISIBLE
        }
        binding.focSwitch.isOn=labour.isFOC
    }

    private fun initDiscount()
    {
        if (enableDiscount)
        {
            if (labour.discount.mode == MODE_PERCENTAGE)
            {
                binding.percentageView.isChecked = true
                binding.amountView.setText(discAmt.toString())
                binding.amountView.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(4))
                if (canReduceDiscount)
                {
                    binding.priceView.isEnabled = false
                }
            }
            if (labour.discount.mode == MODE_PRICE)
            {
                binding.priceView.isChecked = true
                binding.amountView.setText(discAmt.toString())
                binding.amountView.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(7))
                if (canReduceDiscount)
                {
                    binding.percentageView.isEnabled = false
                }
            }
            binding.discountRadio.setOnCheckedChangeListener { _, checkedId ->
                when (find<RadioButton>(checkedId))
                {
                    binding.percentageView ->
                    {
                        labour.discount.mode = MODE_PERCENTAGE
                        val length = 5
                        binding.amountView.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(length))
                    }

                    binding.priceView      ->
                    {
                        labour.discount.mode = MODE_PRICE
                        val length = 7
                        binding.amountView.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(length))
                    }
                }
                binding.amountView.text.clear()
                discAmt = labour.discount.amount
                setupTotal()
            }

            binding.amountView.addTextChangedListener(this)
            binding.amountView.onFocusChangeListener = this
        }
        else
        {
            binding.llDiscount.visibility = View.GONE
        }
    }

    companion object
    {
        const val ARG_CAN_REDUCE_DISCOUNT = "ARG_CAN_REDUCE_DISCOUNT"
        const val ARG_LABOUR = "ARG_LABOUR"
        const val ARG_IS_DELETABLE = "IS_DELETEABLE"
        const val ARG_ENABLE_DISCOUNT = "ENABLE_DISCOUNT"
        const val ARG_INVOICE_ID = "INVOICE_ID"
        const val ARG_JC_TYPE = "JC_TYPE"

        private const val MODE_PRICE = "PRICE"
        private const val MODE_PERCENTAGE = "PERCENTAGE"
        private const val DELAY = 800L

        fun getIntent(context: Context, enableDiscount: Boolean, isDeletable: Boolean, invoiceId: String, labour: Labour, isDescountReducable: Boolean, jcType: String): Intent
        {
            return Intent(context, AddLabourActivity::class.java).apply {
                putExtra(ARG_CAN_REDUCE_DISCOUNT, isDescountReducable)
                putExtra(ARG_IS_DELETABLE, isDeletable)
                putExtra(ARG_INVOICE_ID, invoiceId)
                putExtra(ARG_LABOUR, labour)
                putExtra(ARG_ENABLE_DISCOUNT, !enableDiscount)
                putExtra(ARG_JC_TYPE, jcType)
            }
        }
    }

    override fun onSwitched(toggleableView: ToggleableView?, isOn: Boolean) {
        labour.isFOC=isOn
    }
}