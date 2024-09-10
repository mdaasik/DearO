package com.carworkz.dearo.screencontainer

import android.graphics.Color
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.carworkz.dearo.R
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.interactionprovider.ToolBarInteractionProvider

class FullScreenContainer(private val interactionProvider: ToolBarInteractionProvider) : ScreenContainer {

    private var toolbar: Toolbar? = null

    override fun bind(activity: AppCompatActivity): ViewGroup {
        activity.setContentView(R.layout.base_layout)
        val toolbarContainer = activity.findViewById(R.id.toolbar_container) as ViewGroup
        activity.layoutInflater.inflate(R.layout.toolbar_transparent, toolbarContainer, true)
        toolbar = activity.findViewById(R.id.toolbar_action) as Toolbar
        toolbar!!.setTitleTextColor(Color.WHITE)
        val container = activity.findViewById(R.id.fl_container) as ViewGroup
        initToolBar(activity)
        return container
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }

    private fun initToolBar(activity: AppCompatActivity) {
        activity.setSupportActionBar(toolbar)
        val actionBar = activity.supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(true)
            actionBar.title = interactionProvider.toolBarTitle
            toolbar!!.navigationIcon = AppCompatResources.getDrawable(activity, R.drawable.ic_close_black_24dp)
        }
    }

    fun refreshToolBar(activity: AppCompatActivity) {
        initToolBar(activity)
    }
}
