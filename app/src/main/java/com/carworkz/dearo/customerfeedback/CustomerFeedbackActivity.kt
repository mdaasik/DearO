package com.carworkz.dearo.customerfeedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.R
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityCustomerFeedbackBinding
import com.carworkz.dearo.domain.entities.Feedback
import com.carworkz.dearo.extensions.find
import com.carworkz.dearo.extensions.image
import com.carworkz.dearo.interactionprovider.ToolBarInteractionProvider
import com.carworkz.dearo.screencontainer.SingleTextActionScreenContainer
import java.util.Locale

class CustomerFeedbackActivity : ScreenContainerActivity(), ToolBarInteractionProvider {
    private lateinit var binding: ActivityCustomerFeedbackBinding
    private lateinit var feedback: Feedback
    private lateinit var recommendedRatingView: ImageView
    private lateinit var serviceRatingBar: RatingBar
    private lateinit var billRatingBar: RatingBar
    private lateinit var timeRatingBar: RatingBar
    private lateinit var userCommentView: TextView
    private lateinit var serviceTextView: TextView
    private lateinit var billTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var ratingBarServiceRatingView: View
    private lateinit var ratingBarTimeRatingView: View
    private lateinit var ratingBarBillingRatingView: View

    override fun getToolBarTitle(): String = getString(R.string.rating_title)

    override fun getActionBtnTitle(): String = ""

    override fun onActionBtnClick() {
    }

    override fun createScreenContainer(): ScreenContainer = SingleTextActionScreenContainer(this)
    override fun getViewBinding(
        inflater: LayoutInflater?, container: ViewGroup?, attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityCustomerFeedbackBinding.inflate(layoutInflater)
        return binding
    }


    override fun getProgressView(): View {
        return find(R.id.pb_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        feedback = intent.extras?.getParcelable(ARG_FEEDBACK)!!
        init()
    }

    private fun init() {
        recommendedRatingView = binding.ivRecommendedRating

        ratingBarServiceRatingView = find(R.id.rating_layout_1)
        serviceRatingBar = ratingBarServiceRatingView.find(R.id.ratingBar)
        serviceTextView = ratingBarServiceRatingView.find(R.id.title_tv)

        ratingBarBillingRatingView = find(R.id.rating_layout_2)
        billRatingBar = ratingBarBillingRatingView.find(R.id.ratingBar)
        billTextView = ratingBarBillingRatingView.find(R.id.title_tv)

        ratingBarTimeRatingView = find(R.id.rating_layout_3)
        timeRatingBar = ratingBarTimeRatingView.find(R.id.ratingBar)
        timeTextView = ratingBarTimeRatingView.find(R.id.title_tv)

        userCommentView = find(R.id.tv_user_comment)

        recommendedRatingView.image = when (feedback.recommendedScore) {
            0 -> AppCompatResources.getDrawable(this, R.drawable.ic_asset_0)
            1 -> AppCompatResources.getDrawable(this, R.drawable.ic_asset_1)
            2 -> AppCompatResources.getDrawable(this, R.drawable.ic_asset_2)
            3 -> AppCompatResources.getDrawable(this, R.drawable.ic_asset_3)
            4 -> AppCompatResources.getDrawable(this, R.drawable.ic_asset_4)
            5 -> AppCompatResources.getDrawable(this, R.drawable.ic_asset_5)
            6 -> AppCompatResources.getDrawable(this, R.drawable.ic_asset_6)
            7 -> AppCompatResources.getDrawable(this, R.drawable.ic_asset_7)
            8 -> AppCompatResources.getDrawable(this, R.drawable.ic_asset_8)
            9 -> AppCompatResources.getDrawable(this, R.drawable.ic_asset_9)
            10 -> AppCompatResources.getDrawable(this, R.drawable.ic_asset_10)
            else -> AppCompatResources.getDrawable(this, R.drawable.ic_asset_null)
        }

        serviceRatingBar.rating = feedback.serviceQuality
        serviceTextView.text = getString(R.string.service_quality)

        billRatingBar.rating = feedback.billingTransparency
        billTextView.text = getString(R.string.billing_transparency)

        timeRatingBar.rating = feedback.timelyDelivery
        timeTextView.text = getString(R.string.timely_delivery_quality)

        userCommentView.text = feedback.comment?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
    }

    companion object {
        const val ARG_FEEDBACK = "ARG_FEEDBACK"
    }
}
