package com.carworkz.dearo.screencontainer;

import android.view.ViewGroup;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.carworkz.dearo.R;
import com.carworkz.dearo.base.ScreenContainer;
import com.carworkz.dearo.interactionprovider.DefaultInteractionProvider;

public class DefaultScreenContainer implements ScreenContainer {

    private Toolbar toolbar;
    private DefaultInteractionProvider interactionProvider;

    public DefaultScreenContainer(DefaultInteractionProvider interactionProvider) {
        this.interactionProvider = interactionProvider;
    }


    @Override
    public ViewGroup bind(AppCompatActivity activity) {
        activity.setContentView(R.layout.base_layout);
        ViewGroup toolbarContainer = activity.findViewById(R.id.toolbar_container);
        activity.getLayoutInflater().inflate(R.layout.toolbar, toolbarContainer, true);
        toolbar = activity.findViewById(R.id.toolbar_action);
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
            actionBar.setTitle(interactionProvider.getToolBarTitle());
//            toolbar.setNavigationIcon(VectorDrawableCompat.create(activity.getResources(),R.drawable.ic_close_black_24dp,null));
        }

    }
}
