package com.carworkz.dearo.cardslisting

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R
import com.carworkz.dearo.extensions.find
import com.carworkz.dearo.utils.Utility

/**
 * Created by farhan on 03/01/18.
 */
abstract class CardListingBaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val chipView = view.findViewById(R.id.tv_job_listing_status) as TextView
    val idView = view.findViewById(R.id.tv_job_listing_jcno) as TextView
    val titleView = view.findViewById(R.id.tv_job_listing_reg_no) as TextView
    val makeModelView = view.findViewById(R.id.tv_job_listing_make_model) as TextView
    val nameView = view.findViewById(R.id.tv_job_listing_name) as TextView
    val timeRemainingView = view.findViewById(R.id.tv_job_listing_time_remain) as TextView
    val moneyEstimateView = view.findViewById(R.id.tv_job_listing_amount) as TextView
    val primaryCtaView = view.findViewById(R.id.tv_primary_cta_one) as TextView
    val primaryCtaImageView = view.findViewById(R.id.iv_primary_cta_one) as ImageView
    val primaryCtaParent = view.findViewById(R.id.ll_cta_primary_parent) as LinearLayout
    val secondaryCtaParent = view.findViewById(R.id.ll_cta_secondary_parent) as LinearLayout
    val secondaryCtaView = view.findViewById(R.id.tv_secondary_cta) as TextView
    val secondaryCtaImageView = view.findViewById(R.id.iv_secondary_cta) as ImageView
    val callView = view.findViewById(R.id.iv_call_cta) as ImageView
    val moreActionView = view.findViewById(R.id.iv_more_cta) as ImageView
    val timeImageView = view.findViewById(R.id.iv_time) as ImageView
    val moneyImageView = view.findViewById(R.id.iv_money) as ImageView
    var parentView = view.findViewById(R.id.ll_parent_new_job_listing) as LinearLayout
    val ctaParentView = itemView.findViewById(R.id.ll_cta_parent) as LinearLayout
    val dividerView = itemView.find<View>(R.id.view4)
    val isCarpmScanned = itemView.find<View>(R.id.tv_scanned_by_carpm) as TextView

    init {
        callView.setOnClickListener { v -> Utility.makeCall(v.context, v.tag as String) }
    }
}