package com.carworkz.dearo.customerfeedback

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityNewCustomerFeedBackBinding
import com.carworkz.dearo.databinding.LayoutFeedbackThankYouBinding
import com.carworkz.dearo.databinding.LayoutNewCustomerFeedBackBinding
import com.carworkz.dearo.domain.entities.Feedback
import com.carworkz.dearo.interactionprovider.ToolBarInteractionProvider
import com.carworkz.dearo.screencontainer.SingleTextActionScreenContainer
import javax.inject.Inject

class NewCustomerFeedBackActivity : ScreenContainerActivity(), CustomerFeedbackContract.View,
    View.OnClickListener, ToolBarInteractionProvider {

    private lateinit var binding: ActivityNewCustomerFeedBackBinding
    private lateinit var layoutBinding: LayoutNewCustomerFeedBackBinding
    private lateinit var layoutOne: LayoutFeedbackThankYouBinding
    private val feedbackRatings: ArrayList<AppCompatImageView> = arrayListOf()

    @Inject
    lateinit var presenter: CustomerFeedbackPresenter

    private var recommendedScore: Int? = null
    private lateinit var jobcardId: String
    private var feedback: Feedback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as DearOApplication)
            .repositoryComponent
            .COMPONENT(CustomerFeedbackPresenterModule(this))
            .inject(this)

        feedback = intent.getParcelableExtra(ARG_FEEDBACK)
        jobcardId = intent.getStringExtra(ARG_JOBCARD_ID).toString()

        // Inflate the layout and add it to the container view using view binding
        layoutBinding = LayoutNewCustomerFeedBackBinding.inflate(
            layoutInflater,
            binding.feedbackContainerView,
            true
        )
        layoutOne = LayoutFeedbackThankYouBinding.inflate(
            layoutInflater,
            binding.feedbackContainerView,
            true
        )

        with(layoutBinding) {
            (likeView.background as GradientDrawable).setColor(
                ContextCompat.getColor(
                    this@NewCustomerFeedBackActivity,
                    R.color.colorPrimaryLight
                )
            )

            feedbackRatings.apply {
                add(feedback0)
                add(feedback1)
                add(feedback2)
                add(feedback3)
                add(feedback4)
                add(feedback5)
                add(feedback6)
                add(feedback7)
                add(feedback8)
                add(feedback9)
                add(feedback10)
            }

            if (feedback == null) {
                feedbackRatings.forEach { it.setOnClickListener(this@NewCustomerFeedBackActivity) }
                binding.maybeLayerBtn.setOnClickListener(this@NewCustomerFeedBackActivity)
                binding.submitBtn.setOnClickListener(this@NewCustomerFeedBackActivity)
                commentsGroup.visibility = View.VISIBLE
            } else {
                setWorkshopRating(feedback!!.recommendedScore ?: 0)
                serviceQualityBar.rating = feedback!!.serviceQuality
                billingTransparencyBar.rating = feedback!!.billingTransparency
                timelyDeliveryBar.rating = feedback!!.timelyDelivery
                commentsView.setText(feedback!!.comment ?: "")
                serviceQualityBar.isEnabled = false
                billingTransparencyBar.isEnabled = false
                timelyDeliveryBar.isEnabled = false
                commentsView.isEnabled = false
                binding.feedbackActionParentView.visibility = View.GONE
                if (feedback!!.comment.isNullOrEmpty()) {
                    commentsGroup.visibility = View.GONE
                    noCommentsView.visibility = View.VISIBLE
                } else {
                    commentsGroup.visibility = View.VISIBLE
                    noCommentsView.visibility = View.GONE
                    commentLabelView.setText(R.string.customer_feedback_title_comments)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            layoutBinding.feedback0 -> setWorkshopRating(0)
            layoutBinding.feedback1 -> setWorkshopRating(1)
            layoutBinding.feedback2 -> setWorkshopRating(2)
            layoutBinding.feedback3 -> setWorkshopRating(3)
            layoutBinding.feedback4 -> setWorkshopRating(4)
            layoutBinding.feedback5 -> setWorkshopRating(5)
            layoutBinding.feedback6 -> setWorkshopRating(6)
            layoutBinding.feedback7 -> setWorkshopRating(7)
            layoutBinding.feedback8 -> setWorkshopRating(8)
            layoutBinding.feedback9 -> setWorkshopRating(9)
            layoutBinding.feedback10 -> setWorkshopRating(10)
            binding.maybeLayerBtn, layoutOne.dismissBtn -> finish()
            binding.submitBtn -> {
                if (recommendedScore != null &&
                    layoutBinding.serviceQualityBar.rating != 0.0f &&
                    layoutBinding.billingTransparencyBar.rating != 0.0f &&
                    layoutBinding.timelyDeliveryBar.rating != 0.0f
                ) {
                    presenter.saveFeedback(
                        jobcardId,
                        recommendedScore!!,
                        layoutBinding.serviceQualityBar.rating,
                        layoutBinding.billingTransparencyBar.rating,
                        layoutBinding.timelyDeliveryBar.rating,
                        layoutBinding.commentsView.text.toString()
                    )
                } else {
                    if (recommendedScore == null) {
                        toast("Please add recommended score")
                        return
                    }
                    if (layoutBinding.serviceQualityBar.rating == 0.0f) {
                        toast("Please rate service quality")
                        return
                    }
                    if (layoutBinding.billingTransparencyBar.rating == 0.0f) {
                        toast("Please rate billing transparency")
                        return
                    }
                    if (layoutBinding.timelyDeliveryBar.rating == 0.0f) {
                        toast("Please rate timely delivery")
                        return
                    }
                }
            }
        }
    }

    override fun onFeedBackSavedSuccess() {
        binding.feedbackContainerView.removeAllViewsInLayout()
        val thankYouBinding = LayoutFeedbackThankYouBinding.inflate(
            layoutInflater,
            binding.feedbackContainerView,
            true
        )
        (thankYouBinding.heartThankyouView.background as GradientDrawable).setColor(
            ContextCompat.getColor(
                this,
                R.color.colorPrimaryLight
            )
        )
        binding.feedbackActionParentView.visibility = View.GONE
        thankYouBinding.dismissBtn.setOnClickListener(this)
    }

    override fun createScreenContainer(): ScreenContainer = SingleTextActionScreenContainer(this)
    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityNewCustomerFeedBackBinding.inflate(layoutInflater)
        return binding
    }


    override fun getToolBarTitle(): String = "Feedback"

    override fun getActionBtnTitle(): String = ""

    override fun onActionBtnClick() = Unit

    override fun showProgressIndicator() {
        showProgressBar()
    }

    override fun dismissProgressIndicator() {
        dismissProgressBar()
    }

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg)
    }

    private fun setWorkshopRating(position: Int) {
        recommendedScore = position

        for (i in 0 until position + 1) {
            ImageViewCompat.setImageTintList(feedbackRatings[i], null)
        }
        for (i in feedbackRatings.size - 1 downTo position + 1) {
            ImageViewCompat.setImageTintList(
                feedbackRatings[i],
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_grey))
            )
        }
    }

    override fun getProgressView(): View = binding.baseLayout.pbMain

    companion object {
        const val ARG_FEEDBACK = "arg_feedback"
        const val ARG_JOBCARD_ID = "arg_jobcard_id"

        fun getIntent(context: Context, feedback: Feedback?, jobcardId: String): Intent {
            if (jobcardId.isEmpty()) {
                throw IllegalArgumentException("jobcard id cannot be empty")
            }

            return Intent(context, NewCustomerFeedBackActivity::class.java).apply {
                putExtra(ARG_FEEDBACK, feedback)
                putExtra(ARG_JOBCARD_ID, jobcardId)
            }
        }
    }
}
