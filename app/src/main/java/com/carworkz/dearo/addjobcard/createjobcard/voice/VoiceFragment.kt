package com.carworkz.dearo.addjobcard.createjobcard.voice

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.ActionButtonClickEvent
import com.carworkz.dearo.addjobcard.createjobcard.ICreateJobCardInteraction
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.base.BaseFragment
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.FragmentNewVoiceBinding
import com.carworkz.dearo.databinding.ItemAutoSuggestVoiceBinding
import com.carworkz.dearo.domain.entities.CustomerConcern
import com.carworkz.dearo.domain.entities.CustomerSource
import com.carworkz.dearo.domain.entities.CustomerSourceType
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.domain.entities.Verbatim
import com.carworkz.dearo.domain.entities.WorkshopAdviser
import com.carworkz.dearo.domain.entities.WorkshopBay
import com.carworkz.dearo.domain.entities.WorkshopResource
import com.carworkz.dearo.domain.entities.WorkshopTechnician
import com.carworkz.dearo.extensions.toast
import com.carworkz.dearo.utils.Utility
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import javax.inject.Inject

class VoiceFragment : BaseFragment(), EventsManager.EventSubscriber, VoiceContract.View,
    AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, View.OnFocusChangeListener,
    TextWatcher {
    private lateinit var binding: FragmentNewVoiceBinding
    private var isViewOnly = false
    private lateinit var jobCardId: String
    private lateinit var concernsViews: MutableList<View>
    private var serviceAdviserList = mutableListOf<WorkshopAdviser>()
    private var technicianList = mutableListOf<WorkshopTechnician>()
    private var workshopBayList = mutableListOf<WorkshopBay>()
    private var interaction: ICreateJobCardInteraction? = null
    private var serviceAdviserId: String? = null
    private var bayId: String? = null
    private var status: String? = null
    private var source: String? = null
    private var sourceType: String? = null
    private var appointmentId: String? = null
    private val sourceTypeList = mutableListOf<CustomerSourceType>()
    private val sourceList = mutableListOf<CustomerSource>()
    private var isSourceTypeSelectedByUser = false

    @Inject
    lateinit var presenter: VoicePresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    /*private val suggestionRunnable = Runnable {
        val query = concernsViews.map { it.writeConcernView }.find { it.isFocused }?.text.toString()
        presenter.getVerbatimSuggestions(query)
    }*/
    private val suggestionRunnable = Runnable {
        val focusedView = concernsViews.map { view ->
            val bindingEdit = ItemAutoSuggestVoiceBinding.bind(view)
            bindingEdit.writeConcernView
        }.find { it.isFocused }

        val query = focusedView?.text.toString()
        presenter.getVerbatimSuggestions(query)
    }


    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            isViewOnly = requireArguments().getBoolean(ARG_IS_VIEW_ONLY)
            jobCardId = requireArguments().getString(ARG_JOB_CARD_ID)!!
            status = requireArguments().getString(ARG_STATUS)
            appointmentId = requireArguments().getString(ARG_APPOINTMENT_ID)
        }

        ((requireActivity().application) as DearOApplication).repositoryComponent.COMPONENT(
                VoicePresenterModule(this)
            ).inject(this)
        screenTracker.sendScreenEvent(
            activity,
            if (isViewOnly) ScreenTracker.SCREEN_VIEW_VOICE else ScreenTracker.SCREEN_VOICE,
            this.javaClass.name
        )
        concernsViews = mutableListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewVoiceBinding.inflate(inflater, container, false)
        return binding.root/* return inflater.inflate(R.layout.fragment_new_voice, container, false)*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inflate the layout for this fragment
        if (isViewOnly) {
            binding.voiceConcerns.addConcernView.visibility = View.GONE
        }
        if (status != JobCard.STATUS_INITIATED && status != JobCard.STATUS_IN_PROGRESS) {
            false.apply {
                binding.serviceAdviserView.isEnabled = this
                binding.serviceAdviserView.isFocusable = this
                binding.mechanicView.isEnabled = this
                binding.mechanicView.isFocusable = this
                binding.bayNumberView.isEnabled = this
                binding.bayNumberView.isFocusable = this
                binding.customerSourceView.isEnabled = this
                binding.customerSourceView.isFocusable = this
            }
        } else {
            createSelectItem()
        }
        binding.serviceAdviserView.adapter = CustomSpinnerAdapter(
            requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, serviceAdviserList
        )
        binding.bayNumberView.adapter = CustomSpinnerAdapter(
            requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, workshopBayList
        )

        //before setting up source, need to fetch appointment to get source
        presenter.getSourceTypes()

        binding.serviceAdviserView.onItemSelectedListener = this
        binding.bayNumberView.onItemSelectedListener = this
        binding.mechanicView.onItemClickListener = this
        binding.mechanicView.onFocusChangeListener = this
        binding.voiceConcerns.addConcernView.setOnClickListener { createNewEditText("") }
        if (checkIfNetworkAvailable()) {
            presenter.getVerbatimForJobCard(jobCardId)
        } else {
            toast(R.string.NO_INTERNET)
        }

        view.clearFocus()
    }

    private fun createSelectItem() {
        val selectBay = WorkshopBay()
        selectBay.name = "Select Work Bay"
        selectBay.id = ""
        workshopBayList.add(0, selectBay)
        val selectAdviser = WorkshopAdviser()
        selectAdviser.name = "Select Service Adviser"
        selectBay.id = ""
        serviceAdviserList.add(0, selectAdviser)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ICreateJobCardInteraction) {
            interaction = context
        } else throw IllegalStateException("Activity must implement ICreateJobCardInteraction interface ")
    }

    override fun onResume() {
        super.onResume()
        EventsManager.register(this)
    }

    override fun onStop() {
        super.onStop()
        EventsManager.unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
        interaction = null
    }

    override fun displayVoice(list: List<String>?) {
        if (list == null || list.isEmpty()) {
            createNewEditText("")
        } else {
            list.forEach {
                createNewEditText(it)
            }
        }
    }

    /* override fun displayVoiceSuggestions(concerns: List<CustomerConcern>) {
         val focusedView = concernsViews.map { it.writeConcernView }.find { it.isFocused }
         focusedView?.setAdapter(
             ArrayAdapter<String>(
                 requireContext(),
                 android.R.layout.simple_spinner_dropdown_item,
                 concerns.map { it.name })
         )
         focusedView?.showDropDown()
     }*/

    override fun displayVoiceSuggestions(concerns: List<CustomerConcern>) {
        val focusedView = concernsViews.map { view ->
            val bindingEdit = ItemAutoSuggestVoiceBinding.bind(view)
            bindingEdit.writeConcernView
        }.find { it.isFocused }

        focusedView?.let { autoCompleteTextView ->
            val adapter = ArrayAdapter<String>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                concerns.map { it.name })
            autoCompleteTextView.setAdapter(adapter)
            autoCompleteTextView.showDropDown()
        }
    }


    override fun displayResource(
        workshopResource: WorkshopResource,
        serviceAdviserId: String?,
        bayId: String?,
        technicianId: String?,
        source: String?,
        sourceType: String?
    ) {
        serviceAdviserList.addAll(workshopResource.workshopAdviser)
        workshopBayList.addAll(workshopResource.workshopBay)
        technicianList.addAll(workshopResource.workshopTechnician)
        binding.mechanicView.setAdapter(
            ArrayAdapter<String>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                workshopResource.workshopTechnician.map { it.name })
        )
        (binding.serviceAdviserView.adapter as CustomSpinnerAdapter).notifyDataSetChanged()
        (binding.bayNumberView.adapter as CustomSpinnerAdapter).notifyDataSetChanged()
        binding.mechanicView.setText(
            workshopResource.workshopTechnician.find { technicianId == it.id }?.name ?: ""
        )
        val serviceIndex =
            serviceAdviserList.indexOf(serviceAdviserList.find { it.id == serviceAdviserId } ?: 0)
        binding.serviceAdviserView.setSelection(serviceIndex)
        val bayIndex = workshopBayList.indexOf(workshopBayList.find { it.id == bayId } ?: 0)
        binding.bayNumberView.setSelection(bayIndex)
        if (source != null) {
            val sourceIndex = sourceList.indexOf(sourceList.find { it.source == source })
            if (sourceIndex != -1) {
                binding.customerSourceView.setSelection(sourceIndex)
            }
            this.source = source
            this.sourceType = sourceType
        }
    }

    /*    override fun displaySource(source : String?) {
            if (SharedPrefHelper.getCustomerSource().isNotEmpty() && SharedPrefHelper.getCustomerSource()[0].isNotEmpty()) {
                sourceParent.visibility = View.VISIBLE
                sourceList.add(0, "Select Source")
                sourceList.addAll(SharedPrefHelper.getCustomerSource())
                customerSourceView.adapter = ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, sourceList)
                customerSourceView.onItemSelectedListener = this
                //in case of GoBumper lead set 'Go Bumper Customers' selected
                if(source?.equals(Appointment.SOURCE.GO_BUMPER, true) == true)
                {
                    if(sourceList.contains("Go Bumper Customers"))
                    {
                        //set selected
                        customerSourceView.setSelection(sourceList.indexOf("Go Bumper Customers"))
                    }
                }
            } else {
                sourceParent.visibility = View.GONE
            }
        }*/

    override fun displaySources(sources: List<CustomerSource>) {
        if (sources.isNotEmpty()) {
            binding.sourceParent.visibility = View.VISIBLE
            sourceList.removeAll(mutableListOf())
            sourceList.add(0, CustomerSource(null, null, null, "Select Source", null, null, null))

            sourceList.addAll(sources)

            binding.customerSourceView.adapter = ArrayAdapter(
                requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, sourceList
            )
            binding.customerSourceView.onItemSelectedListener = this
            //in case of GoBumper lead set 'Go Bumper Customers' selected
            if (source.isNullOrBlank().not()) {
                val selectedSource =
                    sourceList.find { obj -> obj.source?.contains(source!!, true) == true }


                if (selectedSource != null) {
                    binding.customerSourceView.setSelection(sourceList.indexOf(selectedSource))
                }

//                if(source?.equals(Appointment.SOURCE.GO_BUMPER, true) == true)

//                val sourceT= sourceList.find {obj -> obj.source?.contains("Go Bumper Customers",true) == true }
//
//                if (sourceT != null) {
//                    if(sourceT.source?.contains("Go Bumper Customers") == true) {
//                        //set selected
//                        customerSourceView.setSelection(sourceList.indexOf(sourceT))
//                    }
//                }
            }
        } else {
            binding.sourceTypeParent.visibility = View.GONE
        }
    }

    override fun displaySourceTypes(sourceTypes: List<CustomerSourceType>) {
        //If sourceType is not null or empty
        // then check with list and keep it selected

        if (sourceTypes.isNotEmpty()) {

            binding.sourceTypeParent.visibility = View.VISIBLE
            sourceTypeList.removeAll(mutableListOf())
            sourceTypeList.add(0, CustomerSourceType(null, null, null, "Select Source", null, null))

            sourceTypeList.addAll(sourceTypes)

            binding.customerSourceTypeView.adapter = ArrayAdapter(
                requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, sourceTypeList
            )
            binding.customerSourceTypeView.onItemSelectedListener = this

            if (sourceType.isNullOrBlank().not()) {

                val sourceTypeSelected = sourceTypeList.find { obj ->
                    obj.sourceType?.contains(
                        sourceType!!, true
                    ) == true
                }

                if (sourceTypeSelected != null) {
                    binding.customerSourceTypeView.setSelection(
                        sourceTypeList.indexOf(
                            sourceTypeSelected
                        )
                    )
                }
            }
        } else {
            //here we have to show error instead of hiding field
            binding.sourceTypeParent.visibility = View.GONE
        }
    }

    override fun moveToNextScreen() {
        interaction?.onJobSuccess()
    }

    @Subscribe
    fun onNextBtnEvent(btn: ActionButtonClickEvent) {
        if (status != JobCard.STATUS_INITIATED && status != JobCard.STATUS_IN_PROGRESS) {
            interaction?.onJobSuccess()
            return
        }

        val list = ArrayList<String>()
        concernsViews.indices.mapTo(list) { concernsViews[it].findViewById<TextView>(R.id.writeConcernView).text.toString() }

        // In case of adapterList empty add dummy blank to satisfy api validation.
        if (list.isEmpty()) {
            list.add("")
        }
        val verbatim = Verbatim()
        verbatim.verbatim = list
        verbatim.serviceAdviserId = serviceAdviserId
        verbatim.technicianName = binding.mechanicView.text.toString()
        verbatim.source = source
        verbatim.sourceType = sourceType

        if (bayId == "") {
            verbatim.bayId = null
        } else {
            verbatim.bayId = bayId
        }

        presenter.saveVoice(jobCardId, verbatim)
        Utility.hideSoftKeyboard(requireActivity())
    }


    /* @Subscribe
     fun onNextBtnEvent(btn: ActionButtonClickEvent) {
         if (status != JobCard.STATUS_INITIATED && status != JobCard.STATUS_IN_PROGRESS) {
             interaction?.onJobSuccess()
             return
         }

         val list = ArrayList<String>()
         concernsViews.indices.mapTo(list) { concernsViews[it].writeConcernView.text.toString() }

         *//*In case of adapterList empty add dummy blank to satisfy api validation.*//*
        if (list.isEmpty()) {
            list.add("")
        }
        val verbatim = Verbatim()
        verbatim.verbatim = list
        verbatim.serviceAdviserId = serviceAdviserId
        verbatim.technicianName = binding.mechanicView.text.toString()
        verbatim.source = source
        verbatim.sourceType = sourceType

        if (bayId == "") {
            verbatim.bayId = null
        } else {
            verbatim.bayId = bayId
        }

        presenter.saveVoice(jobCardId, verbatim)
        Utility.hideSoftKeyboard(activity)
    }*/

    override fun showProgressIndicator() {
        (activity as ScreenContainerActivity).showProgressBar()
    }

    override fun dismissProgressIndicator() {
        (activity as ScreenContainerActivity).dismissProgressBar()
    }

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg)
        interaction?.onJobFailure()
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (activity?.isFinishing == false && v == binding.mechanicView && hasFocus) binding.mechanicView.showDropDown()
    }

    private fun createNewEditText(voiceText: String) {
        val bindingEdit = ItemAutoSuggestVoiceBinding.inflate(
            LayoutInflater.from(activity), binding.voiceConcerns.concernsContainerView, false
        )
        val view = bindingEdit.root

        if (!isViewOnly) {
            bindingEdit.writeConcernView.setRawInputType(InputType.TYPE_CLASS_TEXT)
            bindingEdit.writeConcernView.onFocusChangeListener =
                View.OnFocusChangeListener { concernView, hasFocus ->
                    concernView as AutoCompleteTextView
                    if (hasFocus) {
                        bindingEdit.deleteConcernView.visibility = View.VISIBLE
                        concernView.addTextChangedListener(this)
                        concernView.onItemClickListener =
                            AdapterView.OnItemClickListener { _, _, _, _ ->
                                concernView.clearFocus()
                                Utility.hideSoftKeyboard(activity)
                            }
                    } else {
                        bindingEdit.deleteConcernView.visibility = View.GONE
                        concernView.removeTextChangedListener(this)
                        concernView.onItemClickListener = null
                    }
                }
            bindingEdit.deleteConcernView.setOnClickListener {
                val nextIndex = findNextConcernView(concernsViews.indexOf(view))
                concernsViews.remove(view)
                binding.voiceConcerns.concernsContainerView.removeView(view)
                if (nextIndex != -1) concernsViews[nextIndex].requestFocus()
            }
            bindingEdit.writeConcernView.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    createNewEditText("")
                    true
                } else {
                    false
                }
            }
        } else {
            bindingEdit.writeConcernView.isEnabled = false
            bindingEdit.deleteConcernView.visibility = View.GONE
        }
        bindingEdit.writeConcernView.setText(voiceText)

        bindingEdit.writeConcernView.setOnClickListener {
            Timber.d("hey")
        }
        concernsViews.add(view)
        binding.voiceConcerns.concernsContainerView.addView(view)
        bindingEdit.writeConcernView.requestFocus()
    }


    /* private fun createNewEditText(voiceText: String) {
         val view = LayoutInflater.from(activity)
             .inflate(R.layout.item_auto_suggest_voice, view?.parent as ViewGroup, false)
         if (!isViewOnly) {
             view.writeConcernView.setRawInputType(InputType.TYPE_CLASS_TEXT)
             view.writeConcernView.onFocusChangeListener =
                 View.OnFocusChangeListener { concernView, hasFocus ->
                     concernView as AutoCompleteTextView
                     if (hasFocus) {
                         view.deleteConcernView.visibility = View.VISIBLE
                         concernView.addTextChangedListener(this)
                         concernView.onItemClickListener =
                             AdapterView.OnItemClickListener { parent, view, position, id ->
                                 concernView.clearFocus()
                                 Utility.hideSoftKeyboard(activity)
                             }
                     } else {
                         view.deleteConcernView.visibility = View.GONE
                         concernView.removeTextChangedListener(this)
                         concernView.onItemClickListener = null
                     }
                 }
             view.deleteConcernView.setOnClickListener {
                 val nextIndex = findNextConcernView(concernsViews.indexOf(view))
                 concernsViews.remove(view)
                 concernsContainerView.removeView(view)
                 if (nextIndex != -1)
                     concernsViews[nextIndex].requestFocus()
             }
             view.writeConcernView.setOnEditorActionListener { v, actionId, event ->
                 if (actionId == EditorInfo.IME_ACTION_NEXT) {
                     createNewEditText("")
                     true
                 } else {
                     false
                 }
             }
         } else {
             view.writeConcernView.isEnabled = false
             view.deleteConcernView.visibility = View.GONE
         }
         view.writeConcernView.setText(voiceText)
         concernsViews.add(view)
         concernsContainerView.addView(view)
         view.writeConcernView.requestFocus()
     }*/


    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent) {
            binding.serviceAdviserView -> {
                serviceAdviserId = serviceAdviserList[position].id
            }

            binding.bayNumberView -> {
                bayId = workshopBayList[position].id
            }

            binding.customerSourceTypeView -> {
                if (position > 0) {
                    sourceType = sourceTypeList[position].sourceType
                    sourceList.clear()
                    if (isSourceTypeSelectedByUser) {
                        source = ""
                    }
                    isSourceTypeSelectedByUser = true
                    binding.customerSourceView.adapter = ArrayAdapter(
                        requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, sourceList
                    )
                    presenter.getSources(sourceTypeList[position].id!!)
                }
            }

            binding.customerSourceView -> {
                source = if (position > 0) {
                    sourceList[position].source
                } else {
                    null
                }
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent) {
            binding.mechanicView -> {
                binding.mechanicView.setText(technicianList[position].name)
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
        handler.removeCallbacks(suggestionRunnable)
        handler.postDelayed(suggestionRunnable, 200)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    private fun findNextConcernView(currentIndex: Int): Int {
        if (currentIndex + 1 < concernsViews.size) return currentIndex
        if (currentIndex - 1 >= 0) return currentIndex - 1
        return -1
    }

    companion object {

        private const val ARG_IS_VIEW_ONLY = "arg_is_view_only"
        private const val ARG_JOB_CARD_ID = "arg_job_card_id"
        private const val ARG_STATUS = "arg_status"
        private const val ARG_APPOINTMENT_ID = "arg_aptmnt_id"

        @JvmStatic
        fun newInstance(
            isViewOnly: Boolean, jobCardId: String, status: String, source: String?
        ): VoiceFragment {
            val fragment = VoiceFragment()
            val args = Bundle()
            args.putString(ARG_STATUS, status)
            args.putBoolean(ARG_IS_VIEW_ONLY, isViewOnly)
            args.putString(ARG_JOB_CARD_ID, jobCardId)
            args.putString(ARG_APPOINTMENT_ID, source)
            fragment.arguments = args
            return fragment
        }
    }
}