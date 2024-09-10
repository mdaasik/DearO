package com.carworkz.dearo.othersyshistory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.ViewCompat
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.R
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityOtherSysHistoryBinding
import com.carworkz.dearo.databinding.ActivityOtherSysHistoryDetailsBinding
import com.carworkz.dearo.domain.entities.History
import com.carworkz.dearo.interactionprovider.ToolBarImgInteractionProvider
import com.carworkz.dearo.outwarding.helper.OutwardSection
import com.carworkz.dearo.outwarding.helper.OutwardStep
import com.carworkz.dearo.outwarding.helper.OutwardStepMapper
import com.carworkz.dearo.outwarding.helper.OutwardStepMapper.Companion.TITLE_LABOURS
import com.carworkz.dearo.outwarding.helper.OutwardStepMapper.Companion.TITLE_PARTS
import com.carworkz.dearo.outwarding.helper.OutwardSummary
import com.carworkz.dearo.screencontainer.NavigationDrawerScreenContainer
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.activity_other_sys_history_details.*
import kotlinx.android.synthetic.main.header_invoice.**/

class OtherSysHistoryDetailsActivity : ScreenContainerActivity(),
        ToolBarImgInteractionProvider {
    private lateinit var binding: ActivityOtherSysHistoryDetailsBinding
    private lateinit var screenContainer: NavigationDrawerScreenContainer
    private lateinit var history: History
    private var estimatorList = arrayListOf<OutwardStep>()
    var regNumber: String? = null
    var carType: String? = null
    var custNmae: String? = null


    override fun createScreenContainer(): ScreenContainer {
        screenContainer = NavigationDrawerScreenContainer(this)
        return screenContainer
    }

    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityOtherSysHistoryDetailsBinding.inflate(layoutInflater)
        return binding
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        history = intent?.extras?.getParcelable(DETAILS)!!
        regNumber = intent.getStringExtra(REG_NUMBER)
        carType = intent.getStringExtra(CAR_TYPE)
        custNmae = intent.getStringExtra(CUST_NAME)
        super.onCreate(savedInstanceState)
        setData()
        Utility.hideSoftKeyboard(this)
    }

    private fun setData() {
        binding.layOutHeader.invoiceNumber.text = history.jobCardId
        binding.layOutHeader.invoiceDate.text = Utility.formatDate(history.invoice?.date, Utility.TIMESTAMP, Utility.DATE_FORMAT_6, Utility.TIMEZONE_UTC)
        binding.layOutHeader.registrationNumber.text = regNumber
        binding.layOutHeader.carNameType.text = carType
        binding.layOutHeader.customerName.text = custNmae


        var labourTotal = 0.0
        var partsTotal = 0.0
        val servicePackageTotal = 0.0
        history.invoice?.parts?.forEach {
            partsTotal += it.amount
        }
        estimatorList.add(OutwardSection("$TITLE_PARTS(${history.invoice?.parts?.size ?: 0})", partsTotal))
        history.invoice?.parts?.forEach {
            estimatorList.add(OutwardStepMapper.partToOutwardStep(it))
        }

        history.invoice?.labours?.forEach {
            labourTotal += it.amount!!
        }
        estimatorList.add(OutwardSection("$TITLE_LABOURS(${history.invoice?.labours?.size ?: 0})", labourTotal))
        history.invoice?.labours?.forEach {
            estimatorList.add(OutwardStepMapper.labourToOutwardStep(it))
        }

        estimatorList.add(OutwardSection("Summary", null))
        estimatorList.add(OutwardSummary("Summary", history.invoice?.summary?.totalAmountBeforeTax, history.invoice?.summary?.totalTaxAmount, history.invoice?.summary?.totalDiscount, history.invoice?.summary?.totalAmountAfterTax))

        ViewCompat.setNestedScrollingEnabled(binding.estimatorRecyclerview, false);
        binding.estimatorRecyclerview.adapter = HistoryDetailsProcessAdapter(estimatorList)
    }

    override fun getProgressView(): View = ProgressBar(this)

    override fun getNavigationImage(): Int = R.drawable.ic_close_black_24dp

    override fun onActionBtnClick() {
    }

    override fun onSecondaryActionBtnClick() {

    }
    override fun getActionBarImage(): Int = 0

    override fun getSecondaryActionBarImage(): Int = 0


    override fun getToolBarTitle(): String = history.invoice?.invoiceId!!

    companion object {
        const val DETAILS = "DETAILS"
        const val REG_NUMBER = "reg_number"
        const val CAR_TYPE = "car_type"
        const val CUST_NAME = "cust_name"

        fun getIntent(context: Context, history: History, reg_number: String, car_type: String, cust_name: String): Intent {
            return Intent(context, OtherSysHistoryDetailsActivity::class.java).apply {
                putExtra(DETAILS, history)
                putExtra(REG_NUMBER, reg_number)
                putExtra(CAR_TYPE, car_type)
                putExtra(CUST_NAME, cust_name)
            }
        }
    }
}
