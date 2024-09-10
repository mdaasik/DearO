package com.carworkz.dearo.screencontainer;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import com.carworkz.dearo.R;
import com.carworkz.dearo.base.ScreenContainer;
import com.carworkz.dearo.databinding.BaseLayoutBinding;
import com.carworkz.dearo.databinding.ToolbarWithImageButtonBinding;
import com.carworkz.dearo.interactionprovider.TertiaryImgInteractionProvider;
import com.carworkz.dearo.interactionprovider.ToolBarImgInteractionProvider;

/**
 * Created by Farhan on 25/10/17.
 */

public class ActionImgScreenContainer implements ScreenContainer {

    protected Toolbar toolbar;
    private final ToolBarImgInteractionProvider interactionProvider;

    protected AppCompatActivity activity;

    protected ToolbarWithImageButtonBinding toolbarBinding;
    private BaseLayoutBinding baseLayoutBinding;


    public ActionImgScreenContainer(ToolBarImgInteractionProvider interactionProvider) {
        this.interactionProvider = interactionProvider;
    }

    @Override
    public ViewGroup bind(AppCompatActivity activity) {
        this.activity = activity;
        baseLayoutBinding  = BaseLayoutBinding.inflate(activity.getLayoutInflater());
        activity.setContentView(baseLayoutBinding.getRoot());
      //  this.activity.setContentView(R.layout.base_layout);


       // ViewGroup toolbarContainer = this.activity.findViewById(R.id.toolbar_container);

        toolbarBinding = ToolbarWithImageButtonBinding.inflate(activity.getLayoutInflater(), baseLayoutBinding.toolbarContainer, true);
        toolbar = toolbarBinding.toolbarAction;

        ViewGroup container = baseLayoutBinding.flContainer;

        /*this.activity.getLayoutInflater().inflate(R.layout.toolbar_with_image_button, toolbarContainer, true);
        toolbar = this.activity.findViewById(R.id.toolbar_action);
        ViewGroup container = this.activity.findViewById(R.id.fl_container);*/
        initToolBar();
        return container;
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }


    void initToolBar() {
        activity.setSupportActionBar(toolbar);
        final ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(interactionProvider.getToolBarTitle());
            if (interactionProvider.getNavigationImage() != 0) {
                toolbar.setNavigationIcon(AppCompatResources.getDrawable(activity, interactionProvider.getNavigationImage()));
            }
        }

        ImageView actionView = toolbarBinding.ivToolbarActionOne;
        ImageView secondaryActionView = toolbarBinding.ivToolbarActionTwo;
        LinearLayout parentLayoutView = toolbarBinding.llParentToolbarClickPrimary;
        LinearLayout secondaryLayoutView = toolbarBinding.llParentToolbarClickSecondary;
       /* ImageView actionView =  activity.findViewById(R.id.iv_toolbar_action_one);
        ImageView secondaryActionView =  activity.findViewById(R.id.iv_toolbar_action_two);
        LinearLayout parentLayoutView =  activity.findViewById(R.id.ll_parent_toolbar_click_primary);
        LinearLayout secondaryLayoutView = activity.findViewById(R.id.ll_parent_toolbar_click_secondary);*/

        if (interactionProvider.getActionBarImage() != 0) {
            actionView.setImageDrawable(AppCompatResources.getDrawable(activity, interactionProvider.getActionBarImage()));
            parentLayoutView.setOnClickListener(view -> interactionProvider.onActionBtnClick());
        }else{
            actionView.setImageDrawable(null);
            parentLayoutView.setOnClickListener(null);
        }

        if (interactionProvider.getSecondaryActionBarImage() != 0) {
            secondaryActionView.setImageDrawable(AppCompatResources.getDrawable(activity, interactionProvider.getSecondaryActionBarImage()));
            secondaryLayoutView.setVisibility(View.VISIBLE);
            secondaryLayoutView.setOnClickListener(view -> interactionProvider.onSecondaryActionBtnClick());
        }else{
            secondaryActionView.setImageDrawable(null);
            secondaryLayoutView.setVisibility(View.GONE);
            secondaryLayoutView.setOnClickListener(null);
        }

        if (interactionProvider instanceof TertiaryImgInteractionProvider && ((TertiaryImgInteractionProvider) interactionProvider).getTertiaryActionBarImage() != 0) {
            /*ImageView tertiaryActionView = activity.findViewById(R.id.iv_toolbar_action_three);
            LinearLayout tertiaryParentLayoutView = activity.findViewById(R.id.ll_parent_toolbar_click_tertiary);*/
            ImageView tertiaryActionView = toolbarBinding.ivToolbarActionThree;
            LinearLayout tertiaryParentLayoutView = toolbarBinding.llParentToolbarClickTertiary;
            tertiaryActionView.setImageDrawable(AppCompatResources.getDrawable(activity, ((TertiaryImgInteractionProvider) interactionProvider).getTertiaryActionBarImage()));
            tertiaryParentLayoutView.setVisibility(View.VISIBLE);
            tertiaryParentLayoutView.setOnClickListener(view -> ((TertiaryImgInteractionProvider) interactionProvider).onTertiaryActionBtnClick());
        }
    }

    public void refreshToolBar() {
        initToolBar();
    }

}
