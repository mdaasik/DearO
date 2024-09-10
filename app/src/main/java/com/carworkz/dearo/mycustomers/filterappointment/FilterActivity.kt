package com.carworkz.dearo.mycustomers.filterappointment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityCustomerVehicleHistoryBinding
import com.carworkz.dearo.databinding.ActivityFilterBinding
import com.carworkz.dearo.domain.entities.FilterCustomer
import com.carworkz.dearo.domain.entities.FilterMap
import com.carworkz.dearo.extensions.*
import com.carworkz.dearo.interactionprovider.ToolBarInteractionProvider
import com.carworkz.dearo.screencontainer.SingleTextActionScreenContainer
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.base_layout.**/
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class FilterActivity : ScreenContainerActivity(), ToolBarInteractionProvider, View.OnTouchListener,
    View.OnClickListener, DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityFilterBinding

    @Inject
    lateinit var screenTracker: ScreenTracker

    private lateinit var applyBtn: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var filterView: Spinner
    private lateinit var startDateView: EditText
    private lateinit var endDateView: EditText
    private lateinit var dateView: LinearLayout
    private var selectedFilter = ArrayList<String>()
    private var filterMap = ArrayList<FilterMap>()
    private lateinit var startDatePicker: DatePickerDialog
    private lateinit var endDatePicker: DatePickerDialog
    private var startDateCalendar = Calendar.getInstance()
    private var endDateCalendar = Calendar.getInstance()
    private lateinit var dateSetType: String

    private val todayDate = Calendar.getInstance()
    private var init = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as DearOApplication).repositoryComponent.COMPONENT(this)
        screenTracker.sendScreenEvent(this, ScreenTracker.SCREEN_FILTER, this.javaClass.name)
        Timber.d("Found Start Calender : ${intent.extras?.getLong(START_CALENDER)}")
        Timber.d("Found End Calender : ${intent.extras?.getLong(END_CALENDER)}")
        selectedFilter.addAll(intent.extras?.getStringArrayList(FILTER_ARRAY)!!)
        progressBar = find(R.id.pb_main)
        applyBtn = find(R.id.applyBtn)
        filterView = find(R.id.filterSpinner)
        startDateView = find(R.id.etStartDateView)
        endDateView = find(R.id.etEndDate)
        dateView = find(R.id.ll_date)
        startDateCalendar.timeInMillis =
            intent.extras?.getLong(START_CALENDER, Calendar.getInstance().timeInMillis)!!
        endDateCalendar.timeInMillis =
            intent.extras?.getLong(END_CALENDER, Calendar.getInstance().timeInMillis)!!
        startDateView.setText(
            String.format(
                getString(R.string.filter_screen_date_template),
                startDateCalendar.get(Calendar.YEAR),
                startDateCalendar.get(Calendar.MONTH) + 1,
                startDateCalendar.get(Calendar.DAY_OF_MONTH)
            )
        )
        endDateView.setText(
            String.format(
                getString(R.string.filter_screen_date_template),
                endDateCalendar.get(Calendar.YEAR),
                endDateCalendar.get(Calendar.MONTH) + 1,
                endDateCalendar.get(Calendar.DAY_OF_MONTH)
            )
        )
        startDateView.setOnTouchListener(this)
        endDateView.setOnTouchListener(this)
        startDatePicker = DatePickerDialog(
            this,
            this,
            startDateCalendar.get(Calendar.YEAR),
            startDateCalendar.get(Calendar.MONTH),
            startDateCalendar.get(Calendar.DAY_OF_MONTH)
        )
        endDatePicker = DatePickerDialog(
            this,
            this,
            endDateCalendar.get(Calendar.YEAR),
            endDateCalendar.get(Calendar.MONTH),
            endDateCalendar.get(Calendar.DAY_OF_MONTH)
        )
        startDatePicker.datePicker.maxDate = todayDate.timeInMillis
        endDatePicker.datePicker.maxDate = todayDate.timeInMillis
        applyBtn.setOnClickListener(this)
        filterMap.add(FilterMap("select", "All"))
        filterMap.add(FilterMap(FilterCustomer.SERVICE_DATE, "Service Reminders"))
        filterMap.add(FilterMap(FilterCustomer.IN_PROGRESS, "Job Card Creation Date"))
        filterMap.add(FilterMap(FilterCustomer.COMPLETION_DATE, "Job Card Completion Date"))
        filterMap.add(FilterMap(FilterCustomer.INVOICE_DATE, "Invoiced Date"))
        if (selectedFilter.isEmpty()) {
            selectedFilter.add(filterMap[0].key)
        }
        filterView.onItemSelectedListener = this
        filterView.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            filterMap.map { it.value })
        filterView.setSelection(filterMap.indices.find { filterMap[it].key == selectedFilter[0] }
            ?: 0)
        Utility.disableStartKeyboard(this)
    }

    @SuppressLint("StringFormatMatches")
    override fun onDateSet(datePicker: DatePicker?, year: Int, month: Int, day: Int) {
        when (dateSetType) {
            END_CALENDER_DATE -> {
                endDateCalendar.set(year, month, day)
                Timber.d("Setting End Date")
                endDateView.setText(
                    String.format(
                        getString(
                            R.string.filter_screen_date_template,
                            year,
                            month + 1,
                            day
                        )
                    )
                )
            }

            START_CALENDER_DATE -> {
                startDateCalendar.set(year, month, day)
                Timber.d("Setting Start Date")
                startDateView.setText(
                    String.format(
                        getString(
                            R.string.filter_screen_date_template,
                            year,
                            month + 1,
                            day
                        )
                    )
                )
            }
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            applyBtn -> {
                if (selectedFilter.isNotEmpty() && (endDateCalendar.time == startDateCalendar.time || endDateCalendar.after(
                        startDateCalendar
                    ))
                ) {
                    if (filterView.selectedItemPosition != 0) {
                        val intent = Intent()
                        Timber.d("Intent Dates : ${startDateCalendar.time} - ${endDateCalendar.time}")
                        intent.putExtra(START_CALENDER, startDateCalendar.timeInMillis)
                        intent.putExtra(END_CALENDER, endDateCalendar.timeInMillis)
                        intent.putStringArrayListExtra(FILTER_ARRAY, selectedFilter)
                        setResult(RESULT_CODE_APPLY, intent)
                    } else {
                        setResult(RESULT_CODE_CLEAR)
                    }
                    finish()
                } else {
                    if (selectedFilter.isEmpty()) {
                        toast("Select A Filter")
                    } else {
                        toast("End Date can not be earlier than Start Date.")
                    }
                }
            }
        }
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        when (view) {
            startDateView -> {
                startDatePicker.show()
                dateSetType = START_CALENDER_DATE
            }

            endDateView -> {
                endDatePicker.show()
                dateSetType = END_CALENDER_DATE
            }

            else -> view?.performClick()
        }
        return true
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, p3: Long) {
        if (init) {
            when (parent) {
                filterView -> {
                    if (position > 0) {
                        dateView.visibility = View.VISIBLE
                        selectedFilter.clear()
                        if (filterMap.find { it.value == (view as TextView).text.toString() }!!.key == FilterCustomer.SERVICE_DATE) {
                            endDatePicker.datePicker.maxDate = twoYearsAheadInMillis()
                            startDatePicker.datePicker.maxDate = twoYearsAheadInMillis()
                            endDatePicker.datePicker.minDate = 0
                            startDatePicker.datePicker.minDate = 0
                        } else {
                            endDatePicker.datePicker.maxDate = todayDate.timeInMillis
                            startDatePicker.datePicker.maxDate = todayDate.timeInMillis
                            endDatePicker.datePicker.minDate = twoYearsAgoInMillis()
                            startDatePicker.datePicker.minDate = twoYearsAgoInMillis()
                            val cal = Calendar.getInstance()
                            startDatePicker.datePicker.updateDate(
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            )
                            endDatePicker.datePicker.updateDate(
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            )
                            startDateView.setText(
                                Utility.formatToServerTime(
                                    cal.time,
                                    Utility.DATE_FORMAT_5
                                )
                            )
                            endDateView.setText(
                                Utility.formatToServerTime(
                                    cal.time,
                                    Utility.DATE_FORMAT_5
                                )
                            )
                            startDateCalendar = Calendar.getInstance()
                            endDateCalendar = Calendar.getInstance()
                        }
                        selectedFilter.add(filterMap.find { it.value == (view as TextView).text.toString() }!!.key)
                    } else {
                        dateView.visibility = View.GONE
                    }
                }
            }
        } else {
            when (position) {
                0 -> {
                    // all
                    dateView.visibility = View.GONE
                }

                1 -> {
                    // service date
                    endDatePicker.datePicker.maxDate = twoYearsAheadInMillis()
                    startDatePicker.datePicker.maxDate = twoYearsAheadInMillis()
                    endDatePicker.datePicker.minDate = 0
                    startDatePicker.datePicker.minDate = 0
                }
            }
            init = true
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun createScreenContainer(): ScreenContainer {
        return SingleTextActionScreenContainer(this)
    }

    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityFilterBinding.inflate(layoutInflater)
        return binding
    }


    override fun getProgressView() = progressBar

    override fun getToolBarTitle() = "Filter"

    override fun getActionBtnTitle() = "CLEAR ALL"

    override fun onActionBtnClick() {
        setResult(RESULT_CODE_CLEAR)
        finish()
    }

    override fun onBackPressed() {
        setResult(RESULT_CODE_CLEAR)
        finish()
    }

    private fun twoYearsAheadInMillis(): Long {
        val yearCal = Calendar.getInstance()
        yearCal.set(
            yearCal.get(Calendar.YEAR) + 2,
            yearCal.get(Calendar.MONTH),
            yearCal.get(Calendar.DAY_OF_MONTH)
        )
        return yearCal.timeInMillis
    }

    private fun twoYearsAgoInMillis(): Long {
        val yearCal = Calendar.getInstance()
        yearCal.set(
            yearCal.get(Calendar.YEAR) - 2,
            yearCal.get(Calendar.MONTH),
            yearCal.get(Calendar.DAY_OF_MONTH)
        )
        return yearCal.timeInMillis
    }

    companion object {
        const val RESULT_CODE_APPLY = 1001
        const val RESULT_CODE_CLEAR = 1002
        const val FILTER_ARRAY = "FilterArray"
        const val END_CALENDER = "end_cal"
        const val END_CALENDER_DATE = "end_cal_date"
        const val START_CALENDER = "start_cal"
        const val START_CALENDER_DATE = "start_cal_date"

        fun getIntent(
            context: Context,
            startCalendar: Long?,
            endCalendar: Long?,
            filterArray: ArrayList<String>
        ): Intent {
            Timber.d("Intent Start Calender : $startCalendar")
            Timber.d("Intent End Calender : $endCalendar")
            val intent = Intent(context, FilterActivity::class.java)
            intent.putExtra(FILTER_ARRAY, filterArray)
            intent.putExtra(START_CALENDER, startCalendar)
            intent.putExtra(END_CALENDER, endCalendar)
            return intent
        }
    }
}
