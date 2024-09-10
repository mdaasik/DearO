package com.carworkz.dearo.screencontainer

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.carworkz.dearo.R
import com.carworkz.dearo.base.ScreenContainer

open class SearchScreenContainer : ScreenContainer {

    private lateinit var toolbar: Toolbar

    override fun bind(activity: AppCompatActivity): ViewGroup {
        activity.setContentView(R.layout.base_layout)
        val toolbarContainer = activity.findViewById(R.id.toolbar_container) as ViewGroup
        activity.layoutInflater.inflate(R.layout.toolbar, toolbarContainer, true)
        toolbar = activity.findViewById(R.id.toolbar_action) as Toolbar
        val container = activity.findViewById(R.id.fl_container) as ViewGroup
        initToolBar(activity)
        return container
    }

    override fun getToolbar(): Toolbar = toolbar

    open fun initToolBar(activity: AppCompatActivity) {
        activity.setSupportActionBar(toolbar)
        val actionBar = activity.supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(true)
            actionBar.title = ""
        }
    }
}