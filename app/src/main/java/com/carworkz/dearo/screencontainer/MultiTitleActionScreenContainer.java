package com.carworkz.dearo.screencontainer;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import com.carworkz.dearo.R;
import com.carworkz.dearo.base.ScreenContainer;
import com.carworkz.dearo.interactionprovider.MultiTitleInteractionProvider;

/**
 * Created by Farhan on 9/8/17.
 */

public class MultiTitleActionScreenContainer implements ScreenContainer {

    private Toolbar toolbar;
    private final MultiTitleInteractionProvider interactionProvider;
    private TextView titleOne;
    private TextView titleTwo;

    public MultiTitleActionScreenContainer(MultiTitleInteractionProvider interactionProvider) {
        this.interactionProvider = interactionProvider;
    }

    @Override
    public ViewGroup bind(AppCompatActivity activity) {
        activity.setContentView(R.layout.base_layout);
        ViewGroup toolbarContainer = activity.findViewById(R.id.toolbar_container);
        activity.getLayoutInflater().inflate(R.layout.toolbar_with_multitext_title, toolbarContainer,true);
        toolbar = activity.findViewById(R.id.toolbar_action);
        titleOne = toolbar.findViewById(R.id.tv_title1);
        titleTwo = toolbar.findViewById(R.id.tv_title2);
        ViewGroup container = activity.findViewById(R.id.fl_container);
        initToolBar(activity);
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
            titleOne.setText(interactionProvider.getToolBarTitleOne());
            titleTwo.setText(interactionProvider.getToolBarTitleTwo());
            toolbar.setNavigationIcon(AppCompatResources.getDrawable(activity,R.drawable.ic_close_black_24dp));
        }
        TextView actionView = activity.findViewById(R.id.tv_toolbar_action);
        RelativeLayout parentActionView = activity.findViewById(R.id.ll_parent_toolbar_click);
        actionView.setText(interactionProvider.getActionBtnTitle());
        parentActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interactionProvider.onActionBtnClick();
            }
        });
    }


    public void refreshToolBar(AppCompatActivity activity){
        initToolBar(activity);
    }


}
