package com.carworkz.dearo.mrn

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityLeadBinding
import com.carworkz.dearo.databinding.ActivityMrnBinding
import com.carworkz.dearo.domain.entities.Part
import com.carworkz.dearo.interactionprovider.DefaultInteractionProvider
import com.carworkz.dearo.screencontainer.DefaultScreenContainer
/*import kotlinx.android.synthetic.main.activity_mrn.*
import kotlinx.android.synthetic.main.base_layout.**/
import javax.inject.Inject

class MrnActivity : ScreenContainerActivity(), MrnContract.View, DefaultInteractionProvider {
    private lateinit var binding: ActivityMrnBinding
    @Inject
    lateinit var presenter: MrnPresenter

    private lateinit var jobCardId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as DearOApplication)
                .repositoryComponent
                .COMPONENT(MrnPresenterModule(this))
                .inject(this)

        jobCardId = intent.extras?.getString(ARG_JOB_CARD_ID)!!
        presenter.getMrnParts(jobCardId)
    }

    override fun displayMrn(parts: List<Part>?) {
        if (parts.isNullOrEmpty()) {
            binding.mrnRecyclerView.visibility = View.GONE
            binding.noIssuedPartsView.visibility = View.VISIBLE
        } else {
            binding.noIssuedPartsView.visibility = View.GONE
            binding.mrnRecyclerView.visibility = View.VISIBLE
            val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.mrnRecyclerView.layoutManager = layoutManager
            binding.mrnRecyclerView.adapter = MrnAdapter(this, parts)
            val dividerItemDecoration = DividerItemDecoration(binding.mrnRecyclerView.context,
                    layoutManager.orientation)
            binding.mrnRecyclerView.addItemDecoration(dividerItemDecoration)
        }
    }

    override fun showProgressIndicator() {
        showProgressBar()
    }

    override fun dismissProgressIndicator() {
        dismissProgressBar()
    }

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg)
    }

    override fun createScreenContainer(): ScreenContainer = DefaultScreenContainer(this)
    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityMrnBinding.inflate(layoutInflater)
        return binding
    }


    override fun getProgressView(): View = binding.pbMain

    override fun getToolBarTitle(): String = "Parts Issued"

    companion object {

        private const val ARG_JOB_CARD_ID = "arg_jc_id"

        fun getIntent(context: Context, jobCardId: String): Intent {
            return Intent(context, MrnActivity::class.java).apply {
                putExtra(ARG_JOB_CARD_ID, jobCardId)
            }
        }
    }
}
