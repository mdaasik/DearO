package com.carworkz.dearo.appointments.createappointment.timeSlot

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.ActionButtonClickEvent
import com.carworkz.dearo.addjobcard.createjobcard.voice.CustomSpinnerAdapter
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.appointments.createappointment.CreateAppointmentActivity
import com.carworkz.dearo.appointments.createappointment.ICreateAppointmentInteraction
import com.carworkz.dearo.base.BaseActivity
import com.carworkz.dearo.base.BaseFragment
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.databinding.FragmentTimeSlotBinding
import com.carworkz.dearo.databinding.StockWarningBinding
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.extensions.*
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.fragment_time_slot.*
import kotlinx.android.synthetic.main.stock_warning.view.**/
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class TimeSlotFragment : BaseFragment(),
    TimeSlotContract.View,
    EventsManager.EventSubscriber, CalendarView.OnDateChangeListener, AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentTimeSlotBinding
    @Inject
    lateinit var presenter: TimeSlotPresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    var interaction: ICreateAppointmentInteraction? = null
    private lateinit var adapter: TimeSlotAdapter
    private lateinit var timeSlotView: RecyclerView
    private lateinit var calenderView: CalendarView
    private lateinit var viewMore: RelativeLayout
    private var appointment = Appointment()
    private lateinit var dateView: TextView
    private lateinit var remarkView: TextView
    private lateinit var emptyView: TextView
    private lateinit var selectedDate: Calendar
    private lateinit var selection: String
    private val today = Calendar.getInstance()

    private var serviceAdviserList: MutableList<WorkshopAdviser> = arrayListOf()

    private var serviceAdviserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate called")
        ((requireActivity().application) as DearOApplication)
            .repositoryComponent
            .COMPONENT(TimeSlotPresenterModule(this))
            .inject(this)
        screenTracker.sendScreenEvent(activity, ScreenTracker.SCREEN_ADD_APPMNT_TIME_SLOT, javaClass.name)
        appointment = (context as CreateAppointmentActivity).appointment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimeSlotBinding.inflate(inflater, container, false)
        Timber.d("onCreateView called")
       // timeSlotView = view.find(R.id.rv_timeSlot)
        timeSlotView = binding.rvTimeSlot
     //   calenderView = view.find(R.id.calendarView)
        calenderView = binding.calendarView
       // viewMore = view.find(R.id.relativeLayout)
        viewMore = binding.relativeLayout
     //   dateView = view.find(R.id.tv_date)
        dateView = binding.tvDate
       // remarkView = view.find(R.id.et_remark)
        remarkView = binding.etRemark
       // emptyView = view.find(R.id.tv_emptyListView)
        emptyView = binding.tvEmptyListView
        timeSlotView.layoutManager = GridLayoutManager(activity, 4)
        calenderView.setOnDateChangeListener(this)
        calenderView.minDate = today.timeInMillis
        val futureCalender = Calendar.getInstance()
        futureCalender.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH) + 15)
        calenderView.maxDate = futureCalender.timeInMillis
        @SuppressLint("SetTextI18n")
        dateView.text = "${today.time.toString().substring(0, 11)} ${today.get(Calendar.YEAR)} "
        selectedDate = today
        selection = Utility.formatToServerTime(today.time, Utility.DATE_FORMAT_5)
        displayTimeSlots(appointment.timeSlots?.get(0))
        serviceAdviserId = appointment.serviceAdvisorId
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("onViewCreated called")
        super.onViewCreated(view, savedInstanceState)
        initServiceAdviser()
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume called")
        EventsManager.register(this)
    }

    override fun onStop() {
        super.onStop()
        Timber.d("onStop called")

        EventsManager.unregister(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.d("onAttach called")

        if (context is ICreateAppointmentInteraction) {
            interaction = context
        } else {
            throw IllegalStateException("Activity must implement ICreateJobCardInteraction interface ")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy called")

        presenter.detachView()
    }

    override fun showProgressIndicator() {
        (activity as BaseActivity).showProgressBar()
    }

    override fun dismissProgressIndicator() {
        (activity as BaseActivity).dismissProgressBar()
    }

    override fun displayTimeSlots(timeSlot: TimeSlot?) {
        if (timeSlot?.slots?.isNotEmpty() == true) {
            timeSlotView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            adapter = TimeSlotAdapter(timeSlot.slots)
            timeSlotView.adapter = adapter
        } else {
            timeSlotView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        }
    }

    override fun displayWorkshopAdviser(adviser: List<WorkshopAdviser>) {
        serviceAdviserList.addAll(adviser)
        (binding.serviceAdviserView.adapter as CustomSpinnerAdapter).notifyDataSetChanged()
        val serviceIndex = serviceAdviserList.indexOf(serviceAdviserList.find { it.id == serviceAdviserId }
            ?: 0)
        binding.serviceAdviserView.setSelection(serviceIndex)
    }

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg) { it, _ -> it.dismiss() }
    }

    override fun moveToNextScreen(appointment: Appointment) {
        (context as CreateAppointmentActivity).appointment.let {
            it.appointmentId = appointment.appointmentId
            it.createdOn = appointment.createdOn
            it.date = appointment.date
            it.packages = appointment.packages
        }
        interaction?.onSuccess()
    }

    override fun onSelectedDayChange(view: CalendarView, year: Int, month: Int, dayOfMonth: Int) {
        if (view == calenderView) {
            val cal = Calendar.getInstance()
            cal.set(year, month, dayOfMonth)
            selectedDate = cal
            Timber.d(selectedDate.time.toString())
            @SuppressLint("SetTextI18n")
            dateView.text = "${selectedDate.time.toString().substring(0, 11)} $year "
            selection = "$year-${month + 1}-$dayOfMonth"
            presenter.getTimeSlots(appointment.id!!, selection)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent) {
            binding.serviceAdviserView -> {
                serviceAdviserId = serviceAdviserList[position].id
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) = Unit

    @Subscribe
    fun onNextBtnEvent(btn: ActionButtonClickEvent) {
        Timber.v("ActionButton Click Event" + btn.hashCode())
        if (adapter.getTimeSlot() != null) {
            if(remarkView.text.toString().isBlank()) {
                toast("Remark can't be empty")
                return
            }
            val appointmentPost = AppointmentPost()
            appointmentPost.date = "$selection ${adapter.getTimeSlot()?.time}"
            appointmentPost.remarks = remarkView.text.toString()
            appointmentPost.serviceAdvisorId = serviceAdviserId
            presenter.saveTimeSlot(appointment.id!!, appointmentPost)
        } else {
            toast("Please Select a Appointment Slot!")
        }
    }

    override fun showInventoryAlert(inventory: ArrayList<StockInventory>) {
        val partNameParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            marginStart = 45
        }

        val stockWarning = AlertDialog.Builder(requireActivity())
        stockWarning.setTitle(getString(R.string.stock_warning))

        // Use View Binding for the stock_warning layout
        val bindingNew = StockWarningBinding.inflate(layoutInflater)
        stockWarning.setView(bindingNew.root)

        inventory.forEach { inventoryItem ->

            val partName = TextView(requireActivity()).apply {
                setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                text = "${inventoryItem.brand?.name?.uppercase()} - ${inventoryItem.part?.text?.uppercase()}"
                textSize = 18f
                layoutParams = partNameParams
                setPadding(14, 0, 0, 0)
            }
            bindingNew.containerView.addView(partName)

            val partNumber = TextView(requireActivity()).apply {
                setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                text = inventoryItem.partNumber
                textSize = 14f
                layoutParams = partNameParams.apply { topMargin = 8 }
                setPadding(14, 0, 0, 0)
            }
            bindingNew.containerView.addView(partNumber)

            LinearLayout(requireActivity()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = partNameParams

                val quantityText = TextView(requireActivity()).apply {
                    textSize = 12f
                    setPadding(14, 0, 0, 0)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    text = "${inventoryItem.quantity} ${inventoryItem.uom}"
                }
                addView(quantityText)

                val stockStatusText = TextView(requireActivity()).apply {
                    textSize = 12f
                    setPadding(14, 0, 0, 0)
                    text = if (inventoryItem.quantity > inventoryItem.stock) " Out of Stock!" else " Available"
                    setTextColor(
                        if (inventoryItem.quantity > inventoryItem.stock)
                            ContextCompat.getColor(requireContext(), R.color.red)
                        else
                            ContextCompat.getColor(requireContext(), R.color.forest_green)
                    )
                }
                addView(stockStatusText)
                bindingNew.containerView.addView(this)
            }

            if (inventory.size > 1) {
                View(requireActivity()).apply {
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1)
                    setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.old_lavender))
                    bindingNew.containerView.addView(this)
                }
            }
        }

        TextView(requireActivity()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(30, 0, 30, 0)
            }
            text = getString(R.string.caution)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.check_box_red))
            bindingNew.containerView.addView(this)
        }

        stockWarning.setPositiveButton("YES,CONTINUE") { _, _ ->
            if (adapter.getTimeSlot() != null) {
                val appointmentPost = AppointmentPost().apply {
                    date = "$selection ${adapter.getTimeSlot()?.time}"
                    force = true
                }
                presenter.saveTimeSlot(appointment.id!!, appointmentPost)
            } else {
                toast("Please Select an Appointment Slot!")
            }
        }

        stockWarning.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        val dialog = stockWarning.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        }
        dialog.show()
    }


    private fun initServiceAdviser() {
        if (SharedPrefHelper.isServiceAdvisorEnabled()) {
            binding.serviceAdvisorParentView.visibility = View.VISIBLE
            if (serviceAdviserList.isEmpty()) {
                val selectAdviser = WorkshopAdviser()
                selectAdviser.name = "Select Service Advisor"
                serviceAdviserList.add(0, selectAdviser)
                presenter.getServiceAdviser()
            }
            binding.serviceAdviserView.adapter = CustomSpinnerAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, serviceAdviserList)
            binding.serviceAdviserView.onItemSelectedListener = this
        } else {
            binding.serviceAdvisorParentView.visibility = View.GONE
        }
    }

    companion object {
        fun newInstance(): TimeSlotFragment {
            return TimeSlotFragment()
        }
    }
}



/*
package com.carworkz.dearo.appointments.createappointment.timeSlot

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.ActionButtonClickEvent
import com.carworkz.dearo.addjobcard.createjobcard.voice.CustomSpinnerAdapter
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.appointments.createappointment.CreateAppointmentActivity
import com.carworkz.dearo.appointments.createappointment.ICreateAppointmentInteraction
import com.carworkz.dearo.base.BaseActivity
import com.carworkz.dearo.base.BaseFragment
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.databinding.FragmentAppointmentDetailsBinding
import com.carworkz.dearo.databinding.FragmentTimeSlotBinding
import com.carworkz.dearo.databinding.StockWarningBinding
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.extensions.*
import com.carworkz.dearo.utils.Utility
*/
/*import kotlinx.android.synthetic.main.fragment_time_slot.*
import kotlinx.android.synthetic.main.stock_warning.view.**//*

import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class TimeSlotFragment : BaseFragment(),
        TimeSlotContract.View,
        EventsManager.EventSubscriber, CalendarView.OnDateChangeListener, AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentTimeSlotBinding
    @Inject
    lateinit var presenter: TimeSlotPresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    var interaction: ICreateAppointmentInteraction? = null
    private lateinit var adapter: TimeSlotAdapter
 */
/*   private lateinit var binding.rvTimeSlot: RecyclerView
    private lateinit var binding.calendarView: CalendarView
    private lateinit var viewMore: RelativeLayout*//*

    private var appointment = Appointment()
   */
/* private lateinit var  binding.tvDate: TextView
   *//*
*/
/* private lateinit var remarkView: TextView
    private lateinit var  binding.tvEmptyListView: TextView*//*

    private lateinit var selectedDate: Calendar
    private lateinit var selection: String
    private val today = Calendar.getInstance()

    private var serviceAdviserList: MutableList<WorkshopAdviser> = arrayListOf()

    private var serviceAdviserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate called")
        ((requireActivity().application) as DearOApplication)
                .repositoryComponent
                .COMPONENT(TimeSlotPresenterModule(this))
                .inject(this)
        screenTracker.sendScreenEvent(activity, ScreenTracker.SCREEN_ADD_APPMNT_TIME_SLOT, javaClass.name)
        appointment = (context as CreateAppointmentActivity).appointment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimeSlotBinding.inflate(inflater, container, false)
        return binding.root
       */
/* val view = inflater.inflate(R.layout.fragment_time_slot, container, false)*//*

        Timber.d("onCreateView called")
       */
/* binding.rvTimeSlot = view.find(R.id.rv_timeSlot)
        binding.calendarView = view.find(R.id.calendarView)
        viewMore = view.find(R.id.relativeLayout)
         binding.tvDate = view.find(R.id.tv_date)
        remarkView = view.find(R.id.et_remark)
         binding.tvEmptyListView = view.find(R.id.tv_emptyListView)*//*

        binding.rvTimeSlot.layoutManager = GridLayoutManager(activity, 4)
        binding.calendarView.setOnDateChangeListener(this)
        binding.calendarView.minDate = today.timeInMillis
        val futureCalender = Calendar.getInstance()
        futureCalender.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH) + 15)
        binding.calendarView.maxDate = futureCalender.timeInMillis
        @SuppressLint("SetTextI18n")
        binding.tvDate.text = "${today.time.toString().substring(0, 11)} ${today.get(Calendar.YEAR)} "
        selectedDate = today
        selection = Utility.formatToServerTime(today.time, Utility.DATE_FORMAT_5)
        displayTimeSlots(appointment.timeSlots?.get(0))
        serviceAdviserId = appointment.serviceAdvisorId
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("onViewCreated called")
        super.onViewCreated(view, savedInstanceState)
        initServiceAdviser()
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume called")
        EventsManager.register(this)
    }

    override fun onStop() {
        super.onStop()
        Timber.d("onStop called")

        EventsManager.unregister(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.d("onAttach called")

        if (context is ICreateAppointmentInteraction) {
            interaction = context
        } else {
            throw IllegalStateException("Activity must implement ICreateJobCardInteraction interface ")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy called")

        presenter.detachView()
    }

    override fun showProgressIndicator() {
        (activity as BaseActivity).showProgressBar()
    }

    override fun dismissProgressIndicator() {
        (activity as BaseActivity).dismissProgressBar()
    }

    override fun displayTimeSlots(timeSlot: TimeSlot?) {
        if (timeSlot?.slots?.isNotEmpty() == true) {
            binding.rvTimeSlot.visibility = View.VISIBLE
            binding.tvEmptyListView.visibility = View.GONE
            adapter = TimeSlotAdapter(timeSlot.slots)
            binding.rvTimeSlot.adapter = adapter
        } else {
            binding.rvTimeSlot.visibility = View.GONE
             binding.tvEmptyListView.visibility = View.VISIBLE
        }
    }

    override fun displayWorkshopAdviser(adviser: List<WorkshopAdviser>) {
        serviceAdviserList.addAll(adviser)
        (binding.serviceAdviserView.adapter as CustomSpinnerAdapter).notifyDataSetChanged()
        val serviceIndex = serviceAdviserList.indexOf(serviceAdviserList.find { it.id == serviceAdviserId }
                ?: 0)
        binding.serviceAdviserView.setSelection(serviceIndex)
    }

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg) { it, _ -> it.dismiss() }
    }

    override fun moveToNextScreen(appointment: Appointment) {
        (context as CreateAppointmentActivity).appointment.let {
            it.appointmentId = appointment.appointmentId
            it.createdOn = appointment.createdOn
            it.date = appointment.date
            it.packages = appointment.packages
        }
        interaction?.onSuccess()
    }

    override fun onSelectedDayChange(view: CalendarView, year: Int, month: Int, dayOfMonth: Int) {
        if (view == binding.calendarView) {
            val cal = Calendar.getInstance()
            cal.set(year, month, dayOfMonth)
            selectedDate = cal
            Timber.d(selectedDate.time.toString())
            @SuppressLint("SetTextI18n")
             binding.tvDate.text = "${selectedDate.time.toString().substring(0, 11)} $year "
            selection = "$year-${month + 1}-$dayOfMonth"
            presenter.getTimeSlots(appointment.id!!, selection)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent) {
            binding.serviceAdviserView -> {
                serviceAdviserId = serviceAdviserList[position].id
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) = Unit

    @Subscribe
    fun onNextBtnEvent(btn: ActionButtonClickEvent) {
        Timber.v("ActionButton Click Event" + btn.hashCode())
        if (adapter.getTimeSlot() != null) {
            if(binding.etRemark.text.toString().isBlank()) {
                toast("Remark can't be empty")
                return
            }
            val appointmentPost = AppointmentPost()
            appointmentPost.date = "$selection ${adapter.getTimeSlot()?.time}"
            appointmentPost.remarks = binding.etRemark.text.toString()
            appointmentPost.serviceAdvisorId = serviceAdviserId
            presenter.saveTimeSlot(appointment.id!!, appointmentPost)
        } else {
            toast("Please Select a Appointment Slot!")
        }
    }

    override fun showInventoryAlert(inventory: ArrayList<StockInventory>) {
        val partNameParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        partNameParams.marginStart = 45

        val stockWarning = AlertDialog.Builder(requireContext())
        stockWarning.setTitle(getString(R.string.stock_warning))

        val binding = StockWarningBinding.inflate(LayoutInflater.from(context))
        stockWarning.setView(binding.root)

        inventory.forEach { inventoryItem ->

            val partName = TextView(requireContext())
            partName.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            @SuppressLint("SetTextI18n")
            partName.text = "${inventoryItem.brand?.name?.toUpperCase()} - ${inventoryItem.part?.text?.toUpperCase()}"
            partName.textSize = 18f
            partName.layoutParams = partNameParams
            partName.setPadding(14, 0, 0, 0)
            binding.containerView.addView(partName)

            val partNumber = TextView(requireContext())
            partNumber.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            partNumber.text = inventoryItem.partNumber
            partNumber.textSize = 14f
            partNameParams.run {
                topMargin = 8
                partNumber.layoutParams = this
            }
            partNumber.setPadding(14, 0, 0, 0)
            binding.containerView.addView(partNumber)

            LinearLayout(requireContext()).let { hl ->
                hl.orientation = LinearLayout.HORIZONTAL
                hl.layoutParams = partNameParams
                TextView(requireContext()).let {
                    it.textSize = 12f
                    it.setPadding(14, 0, 0, 0)
                    it.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    @SuppressLint("SetTextI18n")
                    it.text = "${inventoryItem.quantity} ${inventoryItem.uom}"
                    hl.addView(it)
                }
                TextView(requireContext()).let {
                    it.textSize = 12f
                    it.setPadding(14, 0, 0, 0)
                    it.text = if (inventoryItem.quantity > inventoryItem.stock) " Out of Stock!" else " Available"
                    it.setTextColor(
                        if (inventoryItem.quantity > inventoryItem.stock)
                            ContextCompat.getColor(requireContext(), R.color.red)
                        else
                            ContextCompat.getColor(requireContext(), R.color.forest_green)
                    )
                    hl.addView(it)
                }
                binding.containerView.addView(hl)
            }
            if (inventory.size > 1) {
                View(requireContext()).let {
                    it.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1)
                    it.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.old_lavender))
                    binding.containerView.addView(it)
                }
            }
        }

        TextView(requireContext()).let {
            val continueParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            continueParams.margin = 30
            it.layoutParams = continueParams
            it.text = requireContext().getString(R.string.caution)
            it.setTextColor(ContextCompat.getColor(requireContext(), R.color.check_box_red))
            binding.containerView.addView(it)
        }

        stockWarning.setPositiveButton("YES, CONTINUE") { _, _ ->
            if (adapter.getTimeSlot() != null) {
                val appointmentPost = AppointmentPost().apply {
                    date = "$selection ${adapter.getTimeSlot()?.time}"
                    force = true
                }
                presenter.saveTimeSlot(appointment.id!!, appointmentPost)
            } else {
                toast("Please Select an Appointment Slot!")
            }
        }
        stockWarning.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        val dialog = stockWarning.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        }
        dialog.show()
    }


    */
/*   override fun showInventoryAlert(inventory: ArrayList<StockInventory>) {
           val partNameParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
           partNameParams.marginStart = 45
           val stockWarning = AlertDialog.Builder(activity)
           stockWarning.setTitle(getString(R.string.stock_warning))
           val view = View.inflate(context, R.layout.stock_warning, null)
           stockWarning.setView(view)
           inventory.forEach { inventoryItem ->

               val partName = TextView(activity)
               partName.textColor = ContextCompat.getColor(requireContext(), R.color.black)
               @SuppressLint("SetTextI18n")
               partName.text = "${inventoryItem.brand?.name?.toUpperCase()} - ${inventoryItem.part?.text?.toUpperCase()}"
               partName.textSize = 18f
               partName.layoutParams = partNameParams
               partName.setPadding(14, 0, 0, 0)
               view.containerView.addView(partName)

               val partNumber = TextView(activity)
               partNumber.textColor = ContextCompat.getColor(requireContext(), R.color.black)
               partNumber.text = "${inventoryItem.partNumber}"
               partNumber.textSize = 14f
               partNameParams.run {
                   topMargin = 8
                   partNumber.layoutParams = this
               }
               partNumber.setPadding(14, 0, 0, 0)
               view.containerView.addView(partNumber)

               LinearLayout(activity).let { hl ->
                   hl.orientation = LinearLayout.HORIZONTAL
                   hl.layoutParams = partNameParams
                   TextView(activity).let {
                       it.textSize = 12f
                       it.setPadding(14, 0, 0, 0)
                       it.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                       @SuppressLint("SetTextI18n")
                       it.text = inventoryItem.quantity.toString() + " " + inventoryItem.uom
                       hl.addView(it)
                   }
                   TextView(activity).let {
                       it.textSize = 12f
                       it.setPadding(14, 0, 0, 0)
                       it.text = if (inventoryItem.quantity > inventoryItem.stock) " Out of Stock!" else " Available"
                       it.textColor = if (inventoryItem.quantity > inventoryItem.stock) ContextCompat.getColor(requireContext(), R.color.red) else ContextCompat.getColor(requireContext(), R.color.forest_green)
                       hl.addView(it)
                   }
                   view.containerView.addView(hl)
               }
               if (inventory.size > 1)
                   View(activity).let {
                       it.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1)
                       it.backgroundColor = ContextCompat.getColor(requireContext(), R.color.old_lavender)
                       view.containerView.addView(it)
                   }
           }
           TextView(activity).let {
               val continueParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
               continueParams.margin = 30
               it.layoutParams = continueParams
               it.text = requireContext().getString(R.string.caution)
               it.textColor = ContextCompat.getColor(requireContext(), R.color.check_box_red)
               view.containerView.addView(it)
           }
           stockWarning.setPositiveButton("YES,CONTINUE") { _, _ ->
               if (adapter.getTimeSlot() != null) {
                   val appointmentPost = AppointmentPost()
                   appointmentPost.date = "$selection ${adapter.getTimeSlot()?.time}"
                   appointmentPost.force = true
                   presenter.saveTimeSlot(appointment.id!!, appointmentPost)
               } else {
                   toast("Please Select a Appointment Slot!")
               }
           }
           stockWarning.setNegativeButton("Cancel") { dialog, _ ->
               dialog.cancel()
           }
           val dialog = stockWarning.create()
           dialog.setOnShowListener {
               dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
               dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
           }
           dialog.show()
       }*//*


    private fun initServiceAdviser() {
        if (SharedPrefHelper.isServiceAdvisorEnabled()) {
            binding.serviceAdvisorParentView.visibility = View.VISIBLE
            if (serviceAdviserList.isEmpty()) {
                val selectAdviser = WorkshopAdviser()
                selectAdviser.name = "Select Service Advisor"
                serviceAdviserList.add(0, selectAdviser)
                presenter.getServiceAdviser()
            }
            binding.serviceAdviserView.adapter = CustomSpinnerAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, serviceAdviserList)
            binding.serviceAdviserView.onItemSelectedListener = this
        } else {
            binding.serviceAdvisorParentView.visibility = View.GONE
        }
    }

    companion object {
        fun newInstance(): TimeSlotFragment {
            return TimeSlotFragment()
        }
    }
}
*/
