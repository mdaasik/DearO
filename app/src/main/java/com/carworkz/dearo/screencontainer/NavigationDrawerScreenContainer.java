package com.carworkz.dearo.screencontainer;

//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;

import com.carworkz.dearo.databinding.BaseDrawerLayoutBinding;
import com.carworkz.dearo.databinding.ToolbarWithImageButtonBinding;
import com.carworkz.dearo.interactionprovider.TertiaryImgInteractionProvider;
import com.carworkz.dearo.interactionprovider.ToolBarImgInteractionProvider;

/**
 * Created by Farhan on 1/11/17.
 */

/*public class NavigationDrawerScreenContainer extends ActionImgScreenContainer {
    private BaseDrawerLayoutBinding baseDrawerLayoutBinding;
    private final ToolBarImgInteractionProvider interactionProvider;

    public NavigationDrawerScreenContainer(ToolBarImgInteractionProvider interactionProvider) {
        super(interactionProvider);
        this.interactionProvider = interactionProvider;
    }

    @Override
    public ViewGroup bind(AppCompatActivity activity) {

        this.activity = activity;

        // Inflate the base drawer layout
        baseDrawerLayoutBinding = BaseDrawerLayoutBinding.inflate(activity.getLayoutInflater());

        // Inflate and set up the toolbar and content
        toolbarBinding = ToolbarWithImageButtonBinding.inflate(activity.getLayoutInflater(), baseDrawerLayoutBinding.toolbarContainer, true);
        toolbar = toolbarBinding.toolbarAction;

        // Initialize the toolbar
        initToolBar();

        // Set the base drawer layout as the content view
        activity.setContentView(baseDrawerLayoutBinding.getRoot());

        // Return the main content container
        return baseDrawerLayoutBinding.flContainer;
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected void initToolBar() {
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(interactionProvider.getToolBarTitle());
            if (interactionProvider.getNavigationImage() != 0) {
                toolbar.setNavigationIcon(AppCompatResources.getDrawable(activity, interactionProvider.getNavigationImage()));
            }
        }

        // Set up action buttons
        setupActionButtons();
    }

    private void setupActionButtons() {
        ImageView actionView = toolbarBinding.ivToolbarActionOne;
        ImageView secondaryActionView = toolbarBinding.ivToolbarActionTwo;
        LinearLayout parentLayoutView = toolbarBinding.llParentToolbarClickPrimary;
        LinearLayout secondaryLayoutView = toolbarBinding.llParentToolbarClickSecondary;

        if (interactionProvider.getActionBarImage() != 0) {
            actionView.setImageDrawable(AppCompatResources.getDrawable(activity, interactionProvider.getActionBarImage()));
            parentLayoutView.setOnClickListener(view -> interactionProvider.onActionBtnClick());
        } else {
            actionView.setImageDrawable(null);
            parentLayoutView.setOnClickListener(null);
        }

        if (interactionProvider.getSecondaryActionBarImage() != 0) {
            secondaryActionView.setImageDrawable(AppCompatResources.getDrawable(activity, interactionProvider.getSecondaryActionBarImage()));
            secondaryLayoutView.setVisibility(View.VISIBLE);
            secondaryLayoutView.setOnClickListener(view -> interactionProvider.onSecondaryActionBtnClick());
        } else {
            secondaryActionView.setImageDrawable(null);
            secondaryLayoutView.setVisibility(View.GONE);
            secondaryLayoutView.setOnClickListener(null);
        }

        if (interactionProvider instanceof TertiaryImgInteractionProvider && ((TertiaryImgInteractionProvider) interactionProvider).getTertiaryActionBarImage() != 0) {
            ImageView tertiaryActionView = toolbarBinding.ivToolbarActionThree;
            LinearLayout tertiaryParentLayoutView = toolbarBinding.llParentToolbarClickTertiary;
            tertiaryActionView.setImageDrawable(AppCompatResources.getDrawable(activity, ((TertiaryImgInteractionProvider) interactionProvider).getTertiaryActionBarImage()));
            tertiaryParentLayoutView.setVisibility(View.VISIBLE);
            tertiaryParentLayoutView.setOnClickListener(view -> ((TertiaryImgInteractionProvider) interactionProvider).onTertiaryActionBtnClick());
        }
    }
}*/

public class NavigationDrawerScreenContainer extends ActionImgScreenContainer {
    private BaseDrawerLayoutBinding baseDrawerLayoutBinding;

    public NavigationDrawerScreenContainer(ToolBarImgInteractionProvider interactionProvider) {
        super(interactionProvider);
    }

    @Override
    public ViewGroup bind(AppCompatActivity activity) {
        this.activity = activity;

        // Inflate the base drawer layout
        baseDrawerLayoutBinding = BaseDrawerLayoutBinding.inflate(activity.getLayoutInflater());

        // Inflate and set up the toolbar and content
        toolbarBinding = ToolbarWithImageButtonBinding.inflate(activity.getLayoutInflater(), baseDrawerLayoutBinding.toolbarContainer, true);
        toolbar = toolbarBinding.toolbarAction;

        // Initialize the toolbar
        initToolBar();

        // Set the base drawer layout as the content view
        activity.setContentView(baseDrawerLayoutBinding.getRoot());

        // Return the main content container
        return baseDrawerLayoutBinding.flContainer;
    }


    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }
}
