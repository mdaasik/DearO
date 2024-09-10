package com.carworkz.dearo.addjobcard.createjobcard.estimate.partnumberselection.searchpartnumber

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.estimate.partnumberselection.PartNumberSelectionAdapter
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.databinding.ActivityPartNumberSelectorBinding
import com.carworkz.dearo.databinding.ActivitySearchPartNumberBinding
import com.carworkz.dearo.domain.entities.PartNumber
import com.carworkz.dearo.interactionprovider.DefaultInteractionProvider
import com.carworkz.dearo.screencontainer.InverseSearchScreenContainer
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.activity_search_part_number.*
import kotlinx.android.synthetic.main.base_layout.**/
import timber.log.Timber
import javax.inject.Inject

class SearchPartNumberActivity : ScreenContainerActivity(), SearchPartNumberContract.View, SearchView.OnQueryTextListener, DefaultInteractionProvider {
    private lateinit var binding: ActivitySearchPartNumberBinding
    private var searchView: SearchView? = null
    private var searchQuery: String? = null
    private var brandId: String? = null
    private var vehicleType: String? = null
    private var jobCardId: String? = null
    private val partNumbers = arrayListOf<PartNumber>()
    private lateinit var adapter: PartNumberSelectionAdapter
    private val handler = Handler()
    private var selectedFilter = FILTER_OTHER_PARTS

    @Inject
    lateinit var presenter: SearchPartNumberPresenter

    private val queryRunnable = Runnable {
        searchQuery?.let {
                when (selectedFilter) {
                    FILTER_OTHER_PARTS -> presenter.searchPartNumber(it, null, jobCardId, vehicleType,null)
                    FILTER_IN_STOCK_PARTS -> presenter.searchInStockPartNumber(it, jobCardId, brandId, vehicleType)
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initComponent()
        if (intent.extras == null) {
            throw IllegalArgumentException("Part Id or Job Card Id cannot be null")
        }
        searchQuery = intent?.extras?.getString(ARG_SEARCH_QUERY)
        jobCardId = intent?.extras!!.getString(ARG_JOB_CARD_ID)
        vehicleType = intent?.extras!!.getString(ARG_VEHICLE_TYPE)
        adapter = PartNumberSelectionAdapter(partNumbers, true)
        binding.searchResultRecyclerView.adapter = adapter
        initFilters()
        binding.baseLayout.grandParentView.setPadding(Utility.dpToPx(this, 10))
        binding.baseLayout.grandParentView.setBackgroundColor( ContextCompat.getColor(this, R.color.light_grey))
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the options menu from XML

        menuInflater.inflate(R.menu.menu_search, menu)

        // Get the SearchView and set the searchable configuration
        searchView = menu?.findItem(R.id.searchView)?.actionView as SearchView
        searchView?.maxWidth = Integer.MAX_VALUE
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView?.queryHint = "Search By Part Number"
        val searchEditTextId = R.id.search_src_text
        val searchEditText: AutoCompleteTextView? = searchView?.findViewById(searchEditTextId)
        searchEditText?.setPadding(0, 2, 0, 0)
        // Assumes current activity is the searchable activity
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView?.setIconifiedByDefault(false) // Iconify the widget; expand it by default
        searchView?.setOnQueryTextListener(this)
        searchQuery?.let {
            searchView?.setQuery(it, true)
        }
        super.onCreateOptionsMenu(menu)
        return true
    }

    @Suppress("DEPRECATION")
    override fun displaySearchResults(partNumbers: List<PartNumber>) {
        if (partNumbers.isEmpty()) {
            binding.noResultView.visibility = View.VISIBLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.noResultView.text = Html.fromHtml(getString(R.string.no_part_numbers_found), Html.FROM_HTML_MODE_LEGACY)
            } else {
                binding.noResultView.text = Html.fromHtml(getString(R.string.no_part_numbers_found))
            }
            binding.searchResultRecyclerView.visibility = View.GONE
        } else {
            binding.searchResultRecyclerView.visibility = View.VISIBLE
            binding.noResultView.visibility = View.GONE
            this.partNumbers.clear()
            this.partNumbers.addAll(partNumbers)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        callSearch()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        searchQuery = newText
        callSearch()
        return true
    }

  /*  override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // Voice search
        if (Intent.ACTION_SEARCH == intent?.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            if (query != null) {
                searchView?.setQuery(query, false)
            }
        }
        Timber.d("on new intent ${intent?.dataString}")
    }*/
    override fun createScreenContainer(): ScreenContainer = InverseSearchScreenContainer(this)
    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivitySearchPartNumberBinding.inflate(layoutInflater)
        return binding
    }


    override fun getProgressView(): View = binding.baseLayout.pbMain

    override fun getToolBarTitle(): String = "Search By Part Number"

    override fun showProgressIndicator() {
        showProgressBar()
    }

    override fun dismissProgressIndicator() {
        dismissProgressBar()
    }

    override fun showGenericError(errorMsg: String) {
    }

    private fun initComponent() {
        (application as DearOApplication).repositoryComponent
                .COMPONENT(SearchPartNumberPresenterModule(this))
                .inject(this)
    }

    private fun initFilters() {
        val filterTextView = TextView(this)
        filterTextView.text = getString(R.string.search_part_number_label_filter)
        filterTextView.setTextColor( ContextCompat.getColor(this, R.color.dim_gray))

        val inStockPartView = LayoutInflater.from(this).inflate(R.layout.row_filter_part_number, binding.filterParentView, false)
        val inStockTextView = inStockPartView.findViewById<TextView>(R.id.filterSlotView)
        val otherPartView = LayoutInflater.from(this).inflate(R.layout.row_filter_part_number, binding.filterParentView, false)
        val otherPartTextView = otherPartView.findViewById<TextView>(R.id.filterSlotView)
        val inStockPartCheckBoxParent = inStockPartView.findViewById<LinearLayout>(R.id.partFilterParentView)
        val otherPartsCheckBoxParent = otherPartView.findViewById<LinearLayout>(R.id.partFilterParentView)

        inStockTextView.text = getString(R.string.search_part_number_label_filter_in_stock_parts)
        otherPartTextView.text = getString(R.string.search_part_number_label_filter_other_parts)

        binding.filterParentView.addView(filterTextView)

        if (SharedPrefHelper.isInventoryEnabled()) {
            binding.filterParentView.addView(inStockPartView)
            selectedFilter = FILTER_IN_STOCK_PARTS
        }

        binding.filterParentView.addView(otherPartView)

        if (selectedFilter == FILTER_OTHER_PARTS) {
            inStockPartCheckBoxParent.isSelected = false
            otherPartsCheckBoxParent.isSelected = true
            inStockTextView.setTextColor( ContextCompat.getColor(this, R.color.black))
            otherPartTextView.setTextColor( ContextCompat.getColor(this, R.color.white))
        } else {
            inStockPartCheckBoxParent.isSelected = true
            otherPartsCheckBoxParent.isSelected = false
            inStockTextView.setTextColor( ContextCompat.getColor(this, R.color.white))
            otherPartTextView.setTextColor( ContextCompat.getColor(this, R.color.black))
        }

        inStockPartCheckBoxParent.setOnClickListener {
            if (selectedFilter == FILTER_IN_STOCK_PARTS)
                return@setOnClickListener

            selectedFilter = FILTER_IN_STOCK_PARTS
            inStockTextView.setTextColor( ContextCompat.getColor(this, R.color.white))
            otherPartTextView.setTextColor( ContextCompat.getColor(this, R.color.black))
            inStockPartCheckBoxParent.isSelected = true
            otherPartsCheckBoxParent.isSelected = false
            callSearch()
        }
        otherPartsCheckBoxParent.setOnClickListener {
            if (selectedFilter == FILTER_OTHER_PARTS)
                return@setOnClickListener

            selectedFilter = FILTER_OTHER_PARTS
            otherPartTextView.setTextColor( ContextCompat.getColor(this, R.color.white))
            inStockTextView.setTextColor( ContextCompat.getColor(this, R.color.black))
            inStockPartCheckBoxParent.isSelected = false
            otherPartsCheckBoxParent.isSelected = true
            callSearch()
        }
    }

    private fun callSearch() {
        handler.removeCallbacks(queryRunnable)
        handler.postDelayed(queryRunnable, SEARCH_DELAY)
    }

    companion object {
        private const val ARG_SEARCH_QUERY = "arg_search_query"
        private const val ARG_VEHICLE_TYPE = "arg_vehicle_type"
        private const val ARG_JOB_CARD_ID = "arg_job_card_id"
        private const val ARG_BRAND_ID = "arg_brand_id"
        private const val FILTER_OTHER_PARTS = "filter_other_parts"
        private const val FILTER_IN_STOCK_PARTS = "filter_in_stock"
        private const val SEARCH_DELAY = 300L

        fun getIntent(context: Context, initialSearchQuery: String, jobCardId: String?, brandId: String?, vehicleType: String?): Intent {
            return Intent(context, SearchPartNumberActivity::class.java).apply {
                putExtra(ARG_SEARCH_QUERY, initialSearchQuery)
                putExtra(ARG_JOB_CARD_ID, jobCardId)
                putExtra(ARG_BRAND_ID, brandId)
                putExtra(ARG_VEHICLE_TYPE, vehicleType)
            }
        }
    }
}
