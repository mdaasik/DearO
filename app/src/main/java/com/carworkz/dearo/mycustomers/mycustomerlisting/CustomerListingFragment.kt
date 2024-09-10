package com.carworkz.dearo.mycustomers.mycustomerlisting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.base.BaseFragment
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.domain.entities.CustomerVehicleDetails
import com.carworkz.dearo.extensions.find
import com.carworkz.dearo.mycustomers.cutomervehiclehistory.CustomerVehicleHistoryActivity
import com.carworkz.dearo.mycustomers.filterappointment.FilterActivity
import com.carworkz.dearo.utils.Utility
import com.google.android.material.floatingactionbutton.FloatingActionButton
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class CustomerListingFragment : BaseFragment(), EventsManager.EventSubscriber, CustomerListingContract.View,
        MyCustomerAdapter.Interaction {

    @Inject
    lateinit var presenter: CustomerListingPresenter
    @Inject
    lateinit var screenTracker: ScreenTracker

    private lateinit var customerView: RecyclerView
    private lateinit var errorView: RelativeLayout
    private lateinit var progressView: ProgressBar
    private lateinit var filterView: FloatingActionButton
    private var filterArray = ArrayList<String>()
    private var startCalendar: Calendar? = null
    private var endCalendar: Calendar? = null
    private var customerList = ArrayList<CustomerVehicleDetails>()
    private lateinit var adapter: MyCustomerAdapter
    private var limit = 9
    private var isLast = false
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as DearOApplication)
                .repositoryComponent
                .COMPONENT(CustomerListingPresenterModule(this))
                .inject(this)
        arguments?.let {
            val startDateString = it.getString(ARG_FILTER_START_DATE)
            val endDateString = it.getString(ARG_FILTER_END_DATE)
            if (startDateString.isNullOrEmpty() || endDateString.isNullOrEmpty()) {
                IllegalArgumentException("Both start & end date should be provided")
                return
            }
            startCalendar = Calendar.getInstance()
            endCalendar = Calendar.getInstance()
            Timber.d("start date string $startDateString")
            Timber.d("start date object ${Utility.getDate(startDateString)}")

            Timber.d("end date string $endDateString")
            Timber.d("end date object ${Utility.getDate(endDateString)}")

            startCalendar!!.time = Utility.getDate(startDateString)
            endCalendar!!.time = Utility.getDate(endDateString)
            filterArray.add("serviceDate")
        }
        screenTracker.sendScreenEvent(activity, ScreenTracker.SCREEN_MY_CUSTOMERS, this.javaClass.name)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_customer_listing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        filterView = view.find(R.id.fab_my_customers)
        errorView = view.find(R.id.rl_error)
        customerView = view.find(R.id.rv_customer)
        customerView.let {
            it.layoutManager = LinearLayoutManager(it.context, RecyclerView.VERTICAL, false)
            it.addItemDecoration(DividerItemDecoration(it.context, LinearLayout.VERTICAL))
        }
        customerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager
                if (dy > 0 && filterView.visibility == View.VISIBLE) {
                    filterView.hide()
                } else if (dy <= 0 && filterView.visibility != View.VISIBLE) {
                    filterView.show()
                }
                val visibleItemCount = layoutManager?.childCount ?: -1
                val totalItemCount = layoutManager?.itemCount ?: -1
                val pastVisibleItems = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    if (!isLast && isLoading.not())
                        getCustomers(false)
                }
            }
        })
        filterView.setOnClickListener {
            Timber.d("Sending Start Date: ${startCalendar?.time} ${startCalendar?.timeInMillis}")
            Timber.d("Sending End Date: ${endCalendar?.time} ${endCalendar?.timeInMillis}")
            startActivityForResult(FilterActivity.getIntent(requireContext(), startCalendar?.timeInMillis, endCalendar?.timeInMillis, filterArray), REQUEST_CODE_FILTER)
        }
        progressView = ProgressBar(context)
        adapter = MyCustomerAdapter(customerList, this)
        customerView.adapter = adapter
        getCustomers(true)
    }

//    override fun onResume() {
//        super.onResume()
//        //getCustomers(true)
//    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_FILTER -> {
                when (resultCode) {
                    FilterActivity.RESULT_CODE_APPLY -> {
                        if (startCalendar == null || endCalendar == null) {
                            startCalendar = Calendar.getInstance()
                            endCalendar = Calendar.getInstance()
                        }
                        startCalendar?.timeInMillis = data!!.extras?.getLong(FilterActivity.START_CALENDER)!!
                        endCalendar?.timeInMillis = data.extras?.getLong(FilterActivity.END_CALENDER)!!
                        Timber.d("Received Start Date: ${startCalendar?.time} + ${startCalendar?.timeInMillis}")
                        Timber.d("Received End Date: ${endCalendar?.time} ${endCalendar?.timeInMillis}")
                        filterArray.clear()
                        data.extras?.getStringArrayList(FilterActivity.FILTER_ARRAY)?.forEach {
                            filterArray.add(it)
                        }
                    }
                    FilterActivity.RESULT_CODE_CLEAR -> {
                        filterArray.clear()
                        startCalendar = null
                        endCalendar = null
                        Timber.d(startCalendar?.time.toString())
                        Timber.d(endCalendar?.time.toString())
                    }
                }
                getCustomers(true)
            }
        }
    }

    override fun startActivity(card: CustomerVehicleDetails) {
        startActivityForResult(CustomerVehicleHistoryActivity.getIntent(requireContext(), card), REQUEST_CODE_2)
    }

    override fun displayCustomers(list: List<CustomerVehicleDetails>, totalItemCount: Int) {
        isLoading = false
        if (totalItemCount == 0) {
            customerView.visibility = View.GONE
            errorView.visibility = View.VISIBLE
        } else {
            customerView.visibility = View.VISIBLE
            errorView.visibility = View.GONE
            val lastIndex = customerList.lastIndex + 1
            customerList.addAll(list)
            isLast = customerList.size == totalItemCount
            adapter.notifyItemRangeInserted(lastIndex, list.size)
        }
    }

    override fun showError() {
        customerView.visibility = View.GONE
        errorView.visibility = View.VISIBLE
    }

    override fun showProgressIndicator() {
        progressView.visibility = View.VISIBLE
    }

    override fun dismissProgressIndicator() {
        progressView.visibility = View.GONE
    }

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg)
    }

    private fun getCustomers(reset: Boolean) {
        isLoading = true
        if (reset) {
            customerList.clear()
            adapter.notifyDataSetChanged()
        }
        if (startCalendar != null || endCalendar != null) {
            presenter.getListing(filterArray, Utility.formatToServerTime(startCalendar!!.time, Utility.DATE_FORMAT_5), Utility.formatToServerTime(endCalendar!!.time, Utility.DATE_FORMAT_5), adapter.itemCount, limit)
        } else {
            presenter.getListing(filterArray, null, null, adapter.itemCount, limit)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(filterStartDate: String? = null, filterEndDate: String? = null) = CustomerListingFragment().apply {
            if ((filterEndDate.isNullOrEmpty() || filterStartDate.isNullOrEmpty()).not()) {
                arguments = Bundle().apply {
                    putString(ARG_FILTER_START_DATE, filterStartDate)
                    putString(ARG_FILTER_END_DATE, filterEndDate)
                }
            } else {
                Timber.i("start/end date is null, not applying any filters")
            }
        }

        const val REQUEST_CODE_FILTER = 1000
        const val REQUEST_CODE_2 = 2000
        const val ARG_FILTER_START_DATE = "arg_filter_start_date"
        const val ARG_FILTER_END_DATE = "arg_filter_end_date"
    }
}
