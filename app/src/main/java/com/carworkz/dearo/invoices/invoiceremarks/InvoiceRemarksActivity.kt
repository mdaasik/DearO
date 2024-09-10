package com.carworkz.dearo.invoices.invoiceremarks

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityInvoiceRemarksBinding
import com.carworkz.dearo.databinding.ActivityNewAddPartBinding
import com.carworkz.dearo.databinding.RemarkEditLayoutBinding
import com.carworkz.dearo.domain.entities.InvoiceRemarks
import com.carworkz.dearo.domain.entities.RecommendedJob
import com.carworkz.dearo.domain.entities.Remark
import com.carworkz.dearo.interactionprovider.ToolBarImgInteractionProvider
import com.carworkz.dearo.screencontainer.ActionImgScreenContainer
/*import kotlinx.android.synthetic.main.base_layout.*
import kotlinx.android.synthetic.main.fragment_voice.*
import kotlinx.android.synthetic.main.remark_edit_layout.view.**/
import timber.log.Timber
import javax.inject.Inject

class InvoiceRemarksActivity : ScreenContainerActivity(), ToolBarImgInteractionProvider, InvoiceRemarksContract.View, View.OnClickListener
{
    private lateinit var binding: ActivityInvoiceRemarksBinding
    private var remarks = ArrayList<Remark>()
    private var unApprovedJobs = mutableListOf<RecommendedJob>()
    private var editTextList = mutableListOf<View>()
    private var editJobList = mutableListOf<View>()
    private lateinit var jobCardId: String

    @Inject
    lateinit var presenter: InvoiceRemarksPresenter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        jobCardId = intent.extras?.getString(ID)!!
        super.onCreate(savedInstanceState)
        (application as DearOApplication)
                .repositoryComponent
                .COMPONENT(InvoiceRemarksPresenterModule(this))
                .inject(this)
        if (checkIfNetworkAvailable())
        {
            presenter.getRemarks(jobCardId)
        }
    }

    override fun onDestroy()
    {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onClick(v: View?)
    {
        when (v)
        {
            binding.addItemView ->
            {
                createNewRemarkText(Remark())
            }
        }
    }

    private fun createNewRemarkText(remark: Remark) {
        remarks.add(remark)

        // Inflate the remark_edit_layout using View Binding
        val bindingRemark = RemarkEditLayoutBinding.inflate(layoutInflater, null, false)

        // Setting up the editTextList and clearView functionality
        bindingRemark.clearView.setOnClickListener {
            editTextList.remove(bindingRemark.root)
            binding.addedItemView.removeView(bindingRemark.root)
        }

        bindingRemark.editView.setText(remark.remark)
        bindingRemark.switchView.isOn = remark.type == "red"

        // Add the view to the editTextList and addedItemView
        editTextList.add(bindingRemark.root)
        binding.addedItemView.addView(bindingRemark.root)

        // Request focus for the editView
        bindingRemark.editView.requestFocus()
    }


    /* private fun createNewRemarkText(remark: Remark)
    {
        remarks.add(remark)
        val view = View.inflate(this, R.layout.remark_edit_layout, null)
      *//*  view.editView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                view.clearView.visibility = View.VISIBLE
            else
                view.clearView.visibility = View.GONE
        }*//*
        view.clearView.setOnClickListener {
            editTextList.remove(view)
            addedItemView.removeView(view)
        }
        view.editView.setText(remark.remark)
        view.switchView.isOn=remark.type.equals("red")
        editTextList.add(view)
        addedItemView.addView(view)
        view.editView.requestFocus()
    }
*/

    @SuppressLint("InflateParams")
    private fun createNewJobText(s: RecommendedJob) {
        unApprovedJobs.add(s)

        // Inflate the remark_edit_layout using View Binding
        val bindingRemark = RemarkEditLayoutBinding.inflate(LayoutInflater.from(this))

        // Setting up the editJobList and clearView functionality
        bindingRemark.editView.tag = s
        bindingRemark.clearView.visibility = View.VISIBLE
        bindingRemark.clearView.setOnClickListener {
            editJobList.remove(bindingRemark.root)
            binding.addedItemView.removeView(bindingRemark.root)
        }

        bindingRemark.editView.setText(s.text)
        bindingRemark.editView.isEnabled = false
        bindingRemark.editView.isFocusable = false
        bindingRemark.switchView.visibility = View.INVISIBLE

        // Add the view to the editJobList and addedItemView
        editJobList.add(bindingRemark.root)
        binding.addedItemView.addView(bindingRemark.root)

        // Request focus for the editView
        bindingRemark.editView.requestFocus()
    }

    /*  @SuppressLint("InflateParams")
      private fun createNewJobText(s: RecommendedJob)
      {
          unApprovedJobs.add(s)
          val view = LayoutInflater.from(this).inflate(R.layout.remark_edit_layout, null)
          view.editView.tag = s
          view.clearView.visibility = View.VISIBLE
          view.clearView.setOnClickListener {
              editJobList.remove(view)
              addedItemView.removeView(view)
          }
          view.editView.setText(s.text)
          view.editView.isEnabled = false
          view.editView.isFocusable = false
          view.switchView.visibility=View.INVISIBLE
          editJobList.add(view)
          addedItemView.addView(view)
          view.editView.requestFocus()
      }*/

    override fun displayRemarksAndJobs(remarks: List<Remark>, unApprovedJobs: List<RecommendedJob>?)
    {
        remarks.forEach {
            createNewRemarkText(it)
        }
        unApprovedJobs?.forEach {
            createNewJobText(it)
        }

        if (remarks.isEmpty() && unApprovedJobs?.isEmpty() == true)
        {
            createNewRemarkText(Remark())
        }
    }

    override fun createScreenContainer(): ScreenContainer = ActionImgScreenContainer(this)
    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityInvoiceRemarksBinding.inflate(layoutInflater)
        return binding
    }

    override fun getProgressView(): View = binding.pbMain

    override fun getToolBarTitle(): String = "Invoice Remarks"

    override fun getNavigationImage(): Int = R.drawable.ic_clear_white_24dp

    override fun onSecondaryActionBtnClick() = Timber.d("No Action")

    override fun getActionBarImage(): Int = R.drawable.ic_save_white_24dp

    override fun getSecondaryActionBarImage(): Int = 0

    override fun onActionBtnClick() {
        val data = InvoiceRemarks()

        val remarkList = ArrayList<Remark>()
        for (view in editTextList) {
            // Assuming each view is bound using RemarkEditLayoutBinding
            val binding = RemarkEditLayoutBinding.bind(view)

            if (binding.editView.text.isNotEmpty()) {
                val remark = Remark()
                remark.remark = binding.editView.text.toString()
                remark.type = if (binding.switchView.isOn) "red" else "yellow"
                remarkList.add(remark)
            }
        }
        data.remarks = remarkList

        val deleteList = ArrayList<String>()
        unApprovedJobs.forEach { job ->
            if (editJobList.map {
                    val binding = RemarkEditLayoutBinding.bind(it)
                    binding.editView.tag as RecommendedJob
                }.find { job.id == it.id } == null) {
                job.id?.let { deleteList.add(it) }
            }
        }
        data.jobIds = deleteList
        presenter.saveInvoiceRemarks(jobCardId, data)
    }


    /*  override fun onActionBtnClick()
      {
          val data = InvoiceRemarks()

          val remarkList=ArrayList<Remark>()
          for (view in editTextList)
          {
              if(view.editView.text.isNotEmpty())
              {
                  val remark=Remark()
                  remark.remark=view.editView.text.toString()
                  remark.type=if(view.switchView.isOn)"red" else "yellow"
                  remarkList.add(remark)
              }
          }
          data.remarks=remarkList

          val deleteList = ArrayList<String>()
          unApprovedJobs.forEach { job ->
              if (editJobList.map { it.editView.tag as RecommendedJob }.find { job.id == it.id } == null)
              {
                  job.id?.let { deleteList.add(it) }
              }
          }
          data.jobIds = deleteList
          presenter.saveInvoiceRemarks(jobCardId, data)
      }*/

    override fun moveToNextScreen()
    {
        finish()
    }

    override fun showProgressIndicator()
    {
        super.showProgressBar()
    }

    override fun dismissProgressIndicator()
    {
        super.dismissProgressBar()
    }

    override fun showGenericError(errorMsg: String)
    {
        displayError(errorMsg)
    }

    companion object
    {
        const val ID = "id"
        fun getIntent(context: Context, id: String): Intent
        {
            return Intent(context, InvoiceRemarksActivity::class.java).putExtra(ID, id)
        }
    }
}
