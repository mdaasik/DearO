package com.carworkz.dearo.mycustomers.mycustomerlisting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.R
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityMyCustomersBinding
import com.carworkz.dearo.interactionprovider.DefaultInteractionProvider
import com.carworkz.dearo.screencontainer.DefaultScreenContainer
/*import kotlinx.android.synthetic.main.base_layout.**/

class MyCustomersActivity : ScreenContainerActivity(), DefaultInteractionProvider {
private lateinit var binding:ActivityMyCustomersBinding
    private var filterStartDate: String? = null
    private var filterEndDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.extras != null) {
            filterEndDate = intent.extras?.getString(ARG_END_DATE)
            filterStartDate = intent.extras?.getString(ARG_START_DATE)
        }
        supportFragmentManager.beginTransaction().add(R.id.myCustomerContainerView, CustomerListingFragment.newInstance(filterStartDate, filterEndDate)).commitAllowingStateLoss()
    }

    override fun getProgressView(): View = ProgressBar(this)

    override fun getToolBarTitle(): String = getString(R.string.my_customers_toolbar_title)

    override fun createScreenContainer(): ScreenContainer = DefaultScreenContainer(this)
    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        return binding
    }

    companion object {
        const val ARG_START_DATE = "start_date"
        const val ARG_END_DATE = "end_date"

        @JvmStatic
        fun getIntent(context: Context, startDate: String?, endDate: String?): Intent {
            return Intent(context, MyCustomersActivity::class.java).apply {
                putExtra(ARG_START_DATE, startDate)
                putExtra(ARG_END_DATE, endDate)
            }
        }
    }
}
