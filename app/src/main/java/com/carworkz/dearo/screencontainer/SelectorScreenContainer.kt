package com.carworkz.dearo.screencontainer

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.databinding.BaseLayoutBinding
import com.carworkz.dearo.databinding.ToolbarWithSelectorBinding
import com.carworkz.dearo.interactionprovider.SelectorInteractionProvider

class SelectorScreenContainer(val interactionProvider: SelectorInteractionProvider) :
    ScreenContainer, View.OnClickListener {

    private lateinit var activity: AppCompatActivity
    private lateinit var toolbar: Toolbar
    private lateinit var baseLayoutBinding: BaseLayoutBinding
    private lateinit var toolbarBinding: ToolbarWithSelectorBinding

    override fun bind(activity: AppCompatActivity): ViewGroup {
        this.activity = activity
        baseLayoutBinding = BaseLayoutBinding.inflate(activity.layoutInflater)
        this.activity.setContentView(baseLayoutBinding.root)
        toolbarBinding = ToolbarWithSelectorBinding.inflate(
            activity.layoutInflater,
            baseLayoutBinding.toolbarContainer,
            true
        )
        toolbar = toolbarBinding.toolbarAction
        val container = baseLayoutBinding.flContainer
        initToolBar()
        return container
    }

    override fun onClick(v: View?) {
        when (v) {
            toolbarBinding.selectorParentView -> {
                interactionProvider.onSelectorClicked()
            }
        }
    }

    override fun getToolbar(): Toolbar = toolbar

    fun initToolBar() {
        activity.setSupportActionBar(toolbar)
        val actionBar = activity.supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
        }
        toolbarBinding.selectorParentView.setOnClickListener(this)
    }

    fun setSelectorText(text: String) {
        toolbarBinding.selectedStateView.text = text
    }
}
