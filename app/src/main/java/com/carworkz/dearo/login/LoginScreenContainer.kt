package com.carworkz.dearo.login

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.carworkz.dearo.R
import com.carworkz.dearo.base.ScreenContainer

/**
 * Created by Farhan on 9/8/17.
 */
class LoginScreenContainer : ScreenContainer {

    private lateinit var toolBar: Toolbar

    override fun bind(activity: AppCompatActivity): ViewGroup {
        activity.setContentView(R.layout.base_login_layout)
        toolBar = activity.findViewById(R.id.toolbar) as Toolbar
        toolBar.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.transparent))
        val container = activity.findViewById(R.id.fl_login_container) as ViewGroup
        initToolbar(activity)
        return container
    }

    override fun getToolbar(): Toolbar {
        return toolBar
    }

    private fun initToolbar(activity: AppCompatActivity) {
        activity.setSupportActionBar(toolBar)
        val actionBar = activity.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowTitleEnabled(false)
    }
}