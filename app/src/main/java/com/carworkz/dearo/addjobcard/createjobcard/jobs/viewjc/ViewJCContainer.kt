package com.carworkz.dearo.addjobcard.createjobcard.jobs.viewjc

import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.carworkz.dearo.R
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.interactionprovider.ToolBarInteractionProvider

/**
 * Created by kush on 19/9/17.
 */

class ViewJCContainer(private val interactionProvider: ToolBarInteractionProvider) : ScreenContainer {

    private lateinit var toolbar: Toolbar

    override fun bind(activity: AppCompatActivity?): ViewGroup {
        activity?.setContentView(R.layout.base_layout)
        val toolbarContainer = activity?.findViewById(R.id.toolbar_container) as ViewGroup
        activity.layoutInflater.inflate(R.layout.toolbar_with_text_button, toolbarContainer, true)
        toolbar = activity.findViewById(R.id.toolbar_action) as Toolbar
        val container = activity.findViewById(R.id.fl_container) as ViewGroup
        initToolBar(activity)
        return container
    }

    private fun initToolBar(activity: AppCompatActivity) {
        activity.setSupportActionBar(toolbar)
        val actionBar = activity.supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(true)
            actionBar.title = interactionProvider.toolBarTitle
            toolbar.navigationIcon = AppCompatResources.getDrawable(activity, R.drawable.ic_close_black_24dp)
        }
        val actionView = activity.findViewById(R.id.tv_toolbar_action) as TextView
        val parentActionView = activity.findViewById(R.id.ll_parent_toolbar_click) as LinearLayout
        actionView.text = interactionProvider.getActionBtnTitle()
        parentActionView.setOnClickListener { interactionProvider.onActionBtnClick() }
    }

    override fun getToolbar(): Toolbar {
        return toolbar
    }
}
