package com.carworkz.dearo.partfinder

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.database.MatrixCursor
import android.os.Bundle
import android.os.Handler
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.cursoradapter.widget.CursorAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.PriceChangeListener
import com.carworkz.dearo.R
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityPartSearchBinding
import com.carworkz.dearo.domain.entities.PartNumber
import com.carworkz.dearo.domain.entities.Vehicle
import com.carworkz.dearo.extensions.find
import com.carworkz.dearo.extensions.image
import com.carworkz.dearo.outwarding.SwipeCallback
import com.carworkz.dearo.screencontainer.SearchScreenContainer
import com.carworkz.dearo.utils.AnimationCallback
import com.carworkz.dearo.utils.Utility
import timber.log.Timber
import javax.inject.Inject

class PartFinderActivity : ScreenContainerActivity(), PartFinderContract.View,
    SearchView.OnQueryTextListener, SearchView.OnSuggestionListener, PriceChangeListener {
    private lateinit var binding: ActivityPartSearchBinding
    private lateinit var searchView: SearchView
    private lateinit var suggestionAdapter: CursorAdapter
    lateinit var vehicle: Vehicle
    private var suggestionList: MutableList<PartNumber> = mutableListOf()
    private val newColumns = arrayOf(
        BaseColumns._ID,
        SUGGEST_COLUMN_PART_NAME,
        SUGGEST_COLUMN_PART_DESCRIPTION,
        SUGGEST_COLUMN_PART_NUMBER,
        SUGGEST_COLUMN_PART_INSTOCK,
        SUGGEST_COLUMN_PART_RATE,
        SUGGEST_COLUMN_IS_CUSTOM,
        SUGGEST_COLUMN_PART_REMARK
    )
    private val handler = Handler()
    private var queryRunnable: Runnable? = null
    private var total: Double = 0.0

    private var selectedPartsList = arrayListOf<PartNumber>()
    private lateinit var selectedPartRecyclerView: RecyclerView
    private lateinit var selectedPartAdapter: PartFinderAdapter
    private val screenContainer = SearchScreenContainer()

    @Inject
    lateinit var presenter: PartFinderPresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        vehicle = intent.extras!!.getParcelable(VEHICLE)!!
        super.onCreate(savedInstanceState)

        initComponents()
        screenTracker.sendScreenEvent(this, ScreenTracker.PART_FINDER, this.javaClass.simpleName)
        addVehicleDetails()
        selectedPartRecyclerView = findViewById(R.id.rv_part_finder)
        selectedPartAdapter = PartFinderAdapter(this, selectedPartsList, this)
        selectedPartRecyclerView.adapter = selectedPartAdapter
        screenContainer.toolbar.title = "Add Part"
        binding.partFinder.amountView.text = Utility.convertToCurrency(total)
        setSwipeForRecyclerView()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            setResult(Activity.RESULT_OK, Intent().putExtra(VEHICLE, vehicle))
            finish()
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_OK, Intent().putExtra(VEHICLE, vehicle))
        finish()
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the options menu from XML
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_search, menu)

        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu?.findItem(R.id.searchView)?.actionView as SearchView
        searchView.queryHint = "Search Parts"
        val searchEditTextId = R.id.search_src_text
        val searchEditText: AutoCompleteTextView = searchView.find(searchEditTextId)
        val dropDownAnchor: View = searchView.find(searchEditText.dropDownAnchor)

        dropDownAnchor?.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            // screen width
            val screenWidthPixel = this.resources.displayMetrics.widthPixels
            searchEditText.dropDownWidth = screenWidthPixel
        }

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setIconifiedByDefault(true) // Iconify the widget; expand it by default
        searchView.setOnQueryTextListener(this)
        searchView.setOnSuggestionListener(this)
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            Timber.d("farhan is great ")
            if (hasFocus.not()) {
                clearSuggestions()
            }
        }
        suggestionAdapter = SearchSuggestionAdapter(this, null)
        searchView.suggestionsAdapter = suggestionAdapter

        return true
    }

   /* override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // voice search
        if (Intent.ACTION_SEARCH == intent?.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            if (query != null) {
                searchView.setQuery(query, false)
            }
        }
        Timber.d("on new intent ${intent?.dataString}")
    }*/

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (suggestionList.isEmpty()) {
            toast("No Result found")
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        Timber.d(newText)
        if (newText?.isNotEmpty() == true) {
            if (queryRunnable != null) {
                handler.removeCallbacks(queryRunnable!!)
            }
            queryRunnable = Runnable {
                presenter.searchPart(
                    newText,
                    vehicle.makeSlug,
                    vehicle.modelSlug,
                    vehicle.variantCode,
                    vehicle.fuelType,
                    vehicle.vehicleType
                )
            }
            handler.postDelayed(queryRunnable!!, 300)
        } else {
            clearSuggestions()
        }

        return false
    }

    override fun onSuggestionSelect(position: Int): Boolean {
        return false
    }

    override fun onSuggestionClick(position: Int): Boolean {
        selectedPartsList.add(suggestionList[position])
        selectedPartAdapter.notifyItemInserted(selectedPartsList.size)
        onPriceUpdate("", -1)
        return false
    }

    override fun onPriceUpdate(itemType: String, position: Int) {
        total = selectedPartsList.sumOf { it.sellingPrice.toDouble() }
        binding.partFinder.amountView.text = Utility.convertToCurrency(total)
    }

    override fun onVendorSelectionClick(itemType: String, position: Int) {
        TODO("Not yet implemented")
    }

    override fun createScreenContainer(): ScreenContainer = screenContainer
    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityPartSearchBinding.inflate(layoutInflater)
        return binding
    }

    override fun getProgressView(): View = ProgressBar(this)

    override fun onSearchResultReceived(partNumbers: List<PartNumber>) {
        suggestionList = partNumbers.toMutableList()
        val cursor = MatrixCursor(newColumns)
        partNumbers.forEachIndexed { index, partNumber ->
            cursor.addRow(
                arrayOf(
                    Integer.toString(index),
                    partNumber.partName,
                    partNumber.description,
                    partNumber.brandName + " - " + partNumber.partNumber,
                    false,
                    if (partNumber.sellingPrice > 0.0f) Utility.convertToCurrency(partNumber.sellingPrice.toDouble()) else "\u20B9 -",
                    PART_IS_CUSTOM_FALSE,
                    partNumber.remark
                )
            )
        }
        suggestionAdapter.changeCursor(cursor)
        suggestionAdapter.notifyDataSetChanged()
    }

    override fun showProgressIndicator() {
        showProgressBar()
    }

    override fun dismissProgressIndicator() {
        dismissProgressIndicator()
    }

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg)
    }

    private fun setSwipeForRecyclerView() {

        val swipeHelper = object : SwipeCallback(0, ItemTouchHelper.LEFT, this) {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val swipedPosition = viewHolder.adapterPosition
                val adapter = selectedPartRecyclerView.adapter as PartFinderAdapter
                adapter.pendingRemoval(swipedPosition)
            }

            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val position = viewHolder.adapterPosition
                val adapter = selectedPartRecyclerView.adapter as PartFinderAdapter
                return if (viewHolder !is PartFinderAdapter.PartViewHolder || adapter.isPendingRemoval(
                        position
                    )
                ) {
                    0
                } else super.getSwipeDirs(recyclerView, viewHolder)
            }
        }

        val mItemTouchHelper = ItemTouchHelper(swipeHelper)
        mItemTouchHelper.attachToRecyclerView(selectedPartRecyclerView)

        // set swipe label
        swipeHelper.leftSwipeLabel = "Delete"
        // set swipe background-Color
        swipeHelper.leftColorCode = ContextCompat.getColor(this, R.color.red)
    }

    private fun addVehicleDetails() {
        binding.vehicleView.titleView.text =
            StringBuilder().append(vehicle.makeName).append(" - ").append(vehicle.modelName)
                .append(" - ").append(vehicle.variantName)

        binding.vehicleView.descriptionView.text = vehicle.description ?: "Not Selected"
        binding.vehicleView.fuelView.text = vehicle.fuelType
        binding.vehicleView.arrowView.setOnClickListener {
            Utility.expandOrCollapseView(
                binding.vehicleView.collapseView,
                object : AnimationCallback {
                    override fun onAnimationEnd(endState: AnimationCallback.Toggle) {
                        if (endState == AnimationCallback.Toggle.UP) {
                            binding.vehicleView.arrowView.image = ContextCompat.getDrawable(
                                it.context,
                                R.drawable.ic_keyboard_arrow_up_black_24dp
                            )
                        } else {
                            binding.vehicleView.arrowView.image = ContextCompat.getDrawable(
                                it.context,
                                R.drawable.ic_keyboard_arrow_down_black_24dp
                            )
                        }
                    }
                })
        }
    }

    private fun initComponents() {
        (application as DearOApplication).repositoryComponent.COMPONENT(
                PartFinderPresenterModule(
                    this
                )
            ).inject(this)
    }

    private fun clearSuggestions() {
        Timber.d("clearing suggestion list")
        suggestionList.clear()
        if (suggestionAdapter.cursor != null) {
            Timber.d("clearing curosr list")
            suggestionAdapter.cursor.close()
        }
        suggestionAdapter.notifyDataSetChanged()
    }

    companion object {
        const val VEHICLE = "vehicle"
        const val SUGGEST_COLUMN_PART_NAME = "part_name"
        const val SUGGEST_COLUMN_PART_DESCRIPTION = "part_description"
        const val SUGGEST_COLUMN_PART_NUMBER = "part_number"
        const val SUGGEST_COLUMN_PART_REMARK = "part_remark"
        const val SUGGEST_COLUMN_PART_RATE = "part_rate"
        const val SUGGEST_COLUMN_PART_INSTOCK = "part_in_stock"
        const val SUGGEST_COLUMN_IS_CUSTOM = "part_is_custom"
        const val PART_IS_CUSTOM_TRUE = 0
        const val PART_IS_CUSTOM_FALSE = 1
    }
}
