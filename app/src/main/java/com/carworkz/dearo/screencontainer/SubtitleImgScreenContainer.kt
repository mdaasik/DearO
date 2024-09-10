package com.carworkz.dearo.screencontainer


import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import com.carworkz.dearo.R
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.interactionprovider.SubtitleImgInteractionProvider

class SubtitleImgScreenContainer(private val interactionProvider: SubtitleImgInteractionProvider) : ScreenContainer {

    private lateinit var toolbar: Toolbar

    override fun getToolbar(): Toolbar = toolbar

    override fun bind(activity: AppCompatActivity): ViewGroup {
        activity.setContentView(R.layout.base_layout)
        val toolbarContainer = activity.findViewById<ViewGroup>(R.id.toolbar_container)
        activity.layoutInflater.inflate(R.layout.tool_w_subtitle, toolbarContainer, true)
        toolbar = activity.findViewById(R.id.toolbar_action)
        val container = activity.findViewById<ViewGroup>(R.id.fl_container)
        initToolBar(activity)
        return container
    }

    private fun initToolBar(activity: AppCompatActivity) {
        activity.setSupportActionBar(toolbar)
        val actionBar = activity.supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.title = interactionProvider.toolBarTitle
            if (interactionProvider.getNavigationImage() != 0) {
                toolbar.navigationIcon = AppCompatResources.getDrawable(activity, interactionProvider.getNavigationImage())
            }
        }
        val actionView = activity.findViewById<ImageView>(R.id.iv_toolbar_action_one)
        val secondaryActionView = activity.findViewById<ImageView>(R.id.iv_toolbar_action_two)
        val parentLayoutView = activity.findViewById<LinearLayout>(R.id.ll_parent_toolbar_click_primary)
        val secondaryLayoutView = activity.findViewById<LinearLayout>(R.id.ll_parent_toolbar_click_secondary)
        val titleView = activity.findViewById<TextView>(R.id.titleToolbar)
        val subTitleView = activity.findViewById<TextView>(R.id.subTitleToolbar)

        titleView.text = interactionProvider.toolBarTitle
        subTitleView.text = interactionProvider.getToolbarSubTitle()

        if (interactionProvider.getActionBarImage() != 0) {
            actionView.setImageDrawable(AppCompatResources.getDrawable(activity, interactionProvider.getActionBarImage()))
            parentLayoutView.setOnClickListener { interactionProvider.onActionBtnClick() }
        } else {
            actionView.setImageDrawable(null)
            parentLayoutView.setOnClickListener(null)
        }

        if (interactionProvider.getSecondaryActionBarImage() != 0) {
            secondaryActionView.setImageDrawable(AppCompatResources.getDrawable(activity, interactionProvider.getSecondaryActionBarImage()))
            secondaryLayoutView.visibility = View.VISIBLE
            secondaryLayoutView.setOnClickListener { interactionProvider.onSecondaryActionBtnClick() }
        } else {
            secondaryActionView.setImageDrawable(null)
            secondaryLayoutView.visibility = View.GONE
            secondaryLayoutView.setOnClickListener(null)
        }

        if (interactionProvider.getTertiaryActionBarImage() != 0) {
            val tertiaryActionView = activity.findViewById<ImageView>(R.id.iv_toolbar_action_three)
            val tertiaryParentLayoutView = activity.findViewById<LinearLayout>(R.id.ll_parent_toolbar_click_tertiary)
            tertiaryActionView.setImageDrawable(AppCompatResources.getDrawable(activity, interactionProvider.getTertiaryActionBarImage()))
            tertiaryParentLayoutView.visibility = View.VISIBLE
            tertiaryParentLayoutView.setOnClickListener { interactionProvider.onTertiaryActionBtnClick() }
        }
    }

    fun refreshToolBar(activity: AppCompatActivity) {
        initToolBar(activity)
    }
}
