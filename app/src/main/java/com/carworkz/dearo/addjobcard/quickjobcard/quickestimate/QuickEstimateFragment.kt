package com.carworkz.dearo.addjobcard.quickjobcard.quickestimate

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.carworkz.dearo.R
import com.carworkz.dearo.base.BaseFragment
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.databinding.FragmentDashboardBinding
import com.carworkz.dearo.databinding.FragmentQuickEstimateBinding
import com.carworkz.dearo.domain.entities.Estimate
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.exceptions.ValidationInputException
import com.carworkz.dearo.extensions.toast
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.layout_short_cost_estimate.*
import kotlinx.android.synthetic.main.layout_short_estimate_delivery.**/
import timber.log.Timber
import java.util.*

private const val ARG_IS_VIEW_ONLY = "arg_is_view_only"
private const val ARG_JOBCARD = "arg_jobcard"

class QuickEstimateFragment : BaseFragment(), EventsManager.EventSubscriber {
    private lateinit var binding: FragmentQuickEstimateBinding
    private var isViewOnly: Boolean = false
    private lateinit var jobCard: JobCard

    private lateinit var datePicker: DatePickerDialog
    private lateinit var timePicker: TimePickerDialog

    private var calendar = Calendar.getInstance()

    private var selectedYear: Int? = null
    private var selectedMonth: Int? = null
    private var selectedDay: Int? = null
    private var selectedHour: Int = calendar.get(Calendar.HOUR_OF_DAY) + 1
    private var selectedMinutes: Int = calendar.get(Calendar.MINUTE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isViewOnly = it.getBoolean(ARG_IS_VIEW_ONLY)
            jobCard = it.getParcelable(ARG_JOBCARD)!!
        }

        datePicker = DatePickerDialog(
            requireActivity(), { _, year, monthOfYear, dayOfMonth ->
            selectedYear = year
            selectedDay = dayOfMonth
            selectedMonth = monthOfYear
                binding.shortDelivery.dateTextView.text = Utility.formatDate(Utility.DATE_FORMAT_9, year, monthOfYear, dayOfMonth)
            if (binding.shortDelivery.timeTextView.text.isEmpty()) {
                binding.shortDelivery.timeTextView.text = Utility.formatTime(selectedHour, selectedMinutes)
            }
        }, selectedYear ?: calendar.get(Calendar.YEAR), selectedMonth
                ?: calendar.get(Calendar.MONTH), selectedDay ?: calendar.get(Calendar.DAY_OF_MONTH))

        datePicker.datePicker.minDate = System.currentTimeMillis() - 1000

        timePicker = TimePickerDialog(activity, TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
            Timber.d("Time", " $selectedHour hours & $selectedMinute minutes")
            this.selectedHour = selectedHour
            this.selectedMinutes = selectedMinute
            binding.shortDelivery.timeTextView.text = Utility.formatTime(selectedHour, selectedMinute)
        }, selectedHour, selectedMinutes, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuickEstimateBinding.inflate(inflater, container, false)
        return binding.root
       /* return inflater.inflate(R.layout.fragment_quick_estimate, container, false)*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isViewOnly) {
            binding.shortEstimate.maxEstimateView.isEnabled = false
            binding.shortEstimate.minEstimateView.isEnabled = false
        } else {
            binding.shortDelivery.dateTextView.setOnClickListener { datePicker.show() }
            binding.shortDelivery.timeTextView.setOnClickListener { timePicker.show() }
        }
        jobCard.estimate?.deliveryDateTime?.let { deliveryDateTime ->
            calendar.time = Utility.dateToCalender(deliveryDateTime, Utility.TIMESTAMP).time
            selectedDay = calendar.get(Calendar.DAY_OF_MONTH)
            selectedMonth = calendar.get(Calendar.MONTH)
            selectedYear = calendar.get(Calendar.YEAR)
            selectedHour = calendar.get(Calendar.HOUR_OF_DAY)
            selectedMinutes = calendar.get(Calendar.MINUTE)
            binding.shortDelivery.dateTextView.text = Utility.formatDate(Utility.DATE_FORMAT_9, selectedYear!!, selectedMonth!!, selectedDay!!)
            binding.shortDelivery.timeTextView.text = Utility.formatTime(selectedHour, selectedMinutes)
        }

        if (jobCard.estimate?.maxCost ?: 0 > 0) {
            binding.shortEstimate.minEstimateView.setText(jobCard.estimate?.maxCost?.toString() ?: "")
        }

        if (jobCard.estimate?.minCost ?: 0 > 0)
            binding.shortEstimate.minEstimateView.setText(jobCard.estimate?.minCost?.toString() ?: "")
    }

    @Throws
    fun getEstimate(validateCostEstimate: Boolean): Estimate {
        binding.shortDelivery.dateTextView.error = null
        val estimate = Estimate()
        var datetime: String? = null
        selectedYear?.let {
            calendar.set(Calendar.YEAR, selectedYear!!)
            calendar.set(Calendar.MONTH, selectedMonth!!)
            calendar.set(Calendar.DAY_OF_MONTH, selectedDay!!)
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
            calendar.set(Calendar.MINUTE, selectedMinutes)
            calendar.set(Calendar.SECOND, 0)
            val tempCal = Calendar.getInstance()
            if (validateCostEstimate && calendar.time < tempCal.time) {
                context?.toast("Delivery time cannot be less than current time")
                throw ValidationInputException()
            }
            datetime = Utility.formatToServerTime(calendar.time, Utility.DATE_FORMAT_1)
        }

        val minEstimate =  binding.shortEstimate.minEstimateView.text.toString()
        val maxEstimate =  binding.shortEstimate.minEstimateView.text.toString()
        if (validateCostEstimate || (minEstimate.isNotEmpty() || maxEstimate.isNotEmpty())) {
            if (datetime != null && ((minEstimate.isNotEmpty() && maxEstimate.isEmpty()) || (minEstimate.isNotEmpty() && maxEstimate.isNotEmpty() && (maxEstimate.toInt() > 0 && minEstimate.toInt() <= maxEstimate.toInt())))) {
                estimate.deliveryDateTime = datetime
                estimate.minCost = minEstimate.toInt()
                estimate.maxCost = if (maxEstimate.isEmpty()) 0 else maxEstimate.toInt()
            } else {
                if (minEstimate.isEmpty()) {
                    binding.shortEstimate.minEstimateView.error = "Please enter minimum estimate"
                    binding.shortEstimate.minEstimateView.requestFocus()
                } else {
                    if (datetime == null)
                        binding.shortDelivery.dateTextView.error = "Please select estimated delivery date"
                    else
                        toast("Minimum estimate cannot be greater than Maximum estimate")
                }
                throw ValidationInputException()
            }
        } else {
            estimate.deliveryDateTime = datetime
            estimate.minCost = if (minEstimate.isEmpty()) 0 else minEstimate.toInt()
            estimate.maxCost = if (maxEstimate.isEmpty()) 0 else maxEstimate.toInt()
        }

        return estimate
    }

    companion object {

        @JvmStatic
        fun newInstance(isViewOnly: Boolean, jobCard: JobCard) =
                QuickEstimateFragment().apply {
                    arguments = Bundle().apply {
                        putBoolean(ARG_IS_VIEW_ONLY, isViewOnly)
                        putParcelable(ARG_JOBCARD, jobCard)
                    }
                }
    }
}
