package com.carworkz.dearo.screencontainer;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import com.carworkz.dearo.R;
import com.carworkz.dearo.base.ScreenContainer;
import com.carworkz.dearo.databinding.BaseLayoutBinding;
import com.carworkz.dearo.databinding.ToolbarWithTextButtonBinding;
import com.carworkz.dearo.interactionprovider.ToolBarInteractionProvider;

/**
 * Created by Farhan on 9/8/17.
 */

public class SingleTextActionScreenContainer implements ScreenContainer {

    private Toolbar toolbar;
    private final ToolBarInteractionProvider interactionProvider;
    private AppCompatActivity activity;

    // View binding objects for the layouts
    private BaseLayoutBinding baseLayoutBinding;
    private ToolbarWithTextButtonBinding toolbarBinding;

    public SingleTextActionScreenContainer(ToolBarInteractionProvider interactionProvider) {
        this.interactionProvider = interactionProvider;
    }

    @Override
    public ViewGroup bind(AppCompatActivity activity) {
        this.activity = activity;

        // Inflate the base layout using View Binding
        baseLayoutBinding = BaseLayoutBinding.inflate(activity.getLayoutInflater());
        activity.setContentView(baseLayoutBinding.getRoot());

        // Inflate and bind the toolbar layout
        toolbarBinding = ToolbarWithTextButtonBinding.inflate(activity.getLayoutInflater(), baseLayoutBinding.toolbarContainer, true);
        toolbar = toolbarBinding.toolbarAction;

        // Initialize the toolbar
        initToolBar(this.activity);

        // Return the main content container
        return baseLayoutBinding.flContainer;
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    private void initToolBar(AppCompatActivity activity) {
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(interactionProvider.getToolBarTitle());
            toolbar.setNavigationIcon(AppCompatResources.getDrawable(activity, R.drawable.ic_close_black_24dp));
        }

        // Set the action button text and click listener
        toolbarBinding.tvToolbarAction.setText(interactionProvider.getActionBtnTitle());
        toolbarBinding.llParentToolbarClick.setOnClickListener(view -> interactionProvider.onActionBtnClick());
    }

    public void refreshToolBar() {
        initToolBar(activity);
    }
}

/*
public class SingleTextActionScreenContainer implements ScreenContainer {

    private Toolbar toolbar;
    private final ToolBarInteractionProvider interactionProvider;

    private AppCompatActivity activity;

    public SingleTextActionScreenContainer(ToolBarInteractionProvider interactionProvider) {
        this.interactionProvider = interactionProvider;
    }

    @Override
    public ViewGroup bind(AppCompatActivity activity) {
        this.activity = activity;
        this.activity.setContentView(R.layout.base_layout);
        ViewGroup toolbarContainer = activity.findViewById(R.id.toolbar_container);
        activity.getLayoutInflater().inflate(R.layout.toolbar_with_text_button, toolbarContainer,true);
        toolbar = activity.findViewById(R.id.toolbar_action);
        ViewGroup container = activity.findViewById(R.id.fl_container);
        initToolBar(this.activity);
        return container;
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    private void initToolBar(AppCompatActivity activity) {
        activity.setSupportActionBar(toolbar);
        final ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(interactionProvider.getToolBarTitle());
            toolbar.setNavigationIcon(AppCompatResources.getDrawable(activity,R.drawable.ic_close_black_24dp));
        }
        TextView actionView = activity.findViewById(R.id.tv_toolbar_action);
        LinearLayout parentActionView = activity.findViewById(R.id.ll_parent_toolbar_click);
        actionView.setText(interactionProvider.getActionBtnTitle());
        parentActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interactionProvider.onActionBtnClick();
            }
        });
    }


    public void refreshToolBar() {
        initToolBar(activity);
    }


}
*/
