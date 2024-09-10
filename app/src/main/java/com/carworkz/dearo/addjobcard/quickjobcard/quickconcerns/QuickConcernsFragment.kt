package com.carworkz.dearo.addjobcard.quickjobcard.quickconcerns

import android.net.Uri
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
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.base.BaseFragment
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.databinding.FragmentDashboardBinding
import com.carworkz.dearo.databinding.ItemAutoSuggestVoiceBinding
import com.carworkz.dearo.databinding.LayoutVoiceConcernsBinding
import com.carworkz.dearo.domain.entities.CustomerConcern
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.item_auto_suggest_voice.view.*
import kotlinx.android.synthetic.main.layout_voice_concerns.**/
import java.util.*
import javax.inject.Inject

private const val ARG_VIEW_ONLY = "arg_is_view_only"
private const val ARG_VERBATIM = "arg_verbatim"

class QuickConcernsFragment : BaseFragment(), QuickConcernsContract.View,
    EventsManager.EventSubscriber, TextWatcher {
    private lateinit var binding: LayoutVoiceConcernsBinding
    private var isViewOnly: Boolean = false
    private lateinit var verbatim: List<String>
    private var concernsViews: MutableList<View> = mutableListOf()

    @Inject
    lateinit var presenter: QuickConcernPresenter

    private val suggestionRunnable = Runnable {
        val focusedView = concernsViews.map { view ->
            val bindingEdit = ItemAutoSuggestVoiceBinding.bind(view)
            bindingEdit.writeConcernView
        }.find { it.isFocused }

        val query = focusedView?.text.toString()
        presenter.getSuggestions(query)
    }
    /*
    private val suggestionRunnable = Runnable {
        val query = concernsViews.map { it.writeConcernView }.find { it.isFocused }?.text.toString()
        presenter.getSuggestions(query)
    }*/

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isViewOnly = it.getBoolean(ARG_VIEW_ONLY)
            verbatim = it.getStringArrayList(ARG_VERBATIM)!!
        }

        ((requireActivity().application) as DearOApplication)
            .repositoryComponent
            .COMPONENT(QuickConcernPresenterModule(this))
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutVoiceConcernsBinding.inflate(inflater, container, false)
        return binding.root
        // Inflate the layout for this fragment
        /*  return inflater.inflate(R.layout.layout_voice_concerns, container, false)*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isViewOnly) {
            binding.addConcernView.visibility = View.GONE
            if (verbatim.isEmpty()) {
                binding.noConcernAddedView.visibility = View.VISIBLE
                binding.concernsContainerView.visibility = View.GONE
            }
        }

        displayVoice(verbatim)
        binding.addConcernView.setOnClickListener { createNewEditText("") }
    }

    override fun afterTextChanged(s: Editable?) {
        handler.removeCallbacks(suggestionRunnable)
        handler.postDelayed(suggestionRunnable, 200)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    /*override fun displaySuggestions(suggestions: List<CustomerConcern>) {
        val focusedView = concernsViews.map { it.writeConcernView }.find { it.isFocused }
        focusedView?.setAdapter(ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_dropdown_item, suggestions.map { it.name }))
        focusedView?.showDropDown()
    }*/

    override fun displaySuggestions(suggestions: List<CustomerConcern>) {
        val focusedView = concernsViews.map { view ->
            val bindingEdit = ItemAutoSuggestVoiceBinding.bind(view)
            bindingEdit.writeConcernView
        }.find { it.isFocused }

        focusedView?.let {
            val adapter = ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                suggestions.map { it.name })
            it.setAdapter(adapter)
            it.showDropDown()
        }
    }


    override fun showProgressIndicator() {
    }

    override fun dismissProgressIndicator() {
    }

    override fun showGenericError(errorMsg: String) {
    }

    /*fun getConcerns(): List<String> {
        val list = ArrayList<String>()
        concernsViews.indices.mapTo(list) { concernsViews[it].writeConcernView.text.toString() }
        return list
    }*/

    fun getConcerns(): List<String> {
        val list = ArrayList<String>()
        concernsViews.mapTo(list) { view ->
            val bindingEdit = ItemAutoSuggestVoiceBinding.bind(view)
            bindingEdit.writeConcernView.text.toString()
        }
        return list
    }


    private fun displayVoice(list: List<String>?) {
        if (list == null || list.isEmpty()) {
            createNewEditText("")
        } else {
            list.forEach {
                createNewEditText(it)
            }
        }
    }

    private fun createNewEditText(voiceText: String) {
        val bindingEdit = ItemAutoSuggestVoiceBinding.inflate(
            LayoutInflater.from(activity),
            binding.concernsContainerView,
            false
        )
        val view = bindingEdit.root

        if (!isViewOnly) {
            bindingEdit.writeConcernView.setRawInputType(InputType.TYPE_CLASS_TEXT)
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
            }.also { bindingEdit.writeConcernView.onFocusChangeListener = it }
            bindingEdit.deleteConcernView.setOnClickListener {
                val nextIndex = findNextConcernView(concernsViews.indexOf(view))
                concernsViews.remove(view)
                binding.concernsContainerView.removeView(view)
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
        concernsViews.add(view)
        binding.concernsContainerView.addView(view)
        bindingEdit.writeConcernView.requestFocus()
    }


    private fun findNextConcernView(currentIndex: Int): Int {
        if (currentIndex + 1 < concernsViews.size) return currentIndex
        if (currentIndex - 1 >= 0) return currentIndex - 1
        return -1
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        @JvmStatic
        fun newInstance(isViewOnly: Boolean, verbatim: List<String>) =
            QuickConcernsFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_VIEW_ONLY, isViewOnly)
                    putStringArrayList(ARG_VERBATIM, verbatim as ArrayList<String>?)
                }
            }
    }
}
