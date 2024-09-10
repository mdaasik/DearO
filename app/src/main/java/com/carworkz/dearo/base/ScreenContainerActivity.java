package com.carworkz.dearo.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewbinding.ViewBinding;

import com.carworkz.dearo.R;

public abstract class ScreenContainerActivity extends BaseActivity {

    protected abstract ScreenContainer createScreenContainer();

    protected abstract ViewBinding getViewBinding(LayoutInflater inflater, ViewGroup container, boolean attachToParent);

    protected OptionItemHandler getOptionHandler() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScreenContainer screenContainer = createScreenContainer();
        if (screenContainer != null) {
            ViewGroup container = screenContainer.bind(this);
            ViewBinding viewBinding = getViewBinding(getLayoutInflater(), container, true);

            View viewToAdd = viewBinding.getRoot();
            if (viewToAdd.getParent() != null) {
                ((ViewGroup) viewToAdd.getParent()).removeView(viewToAdd);
            }
            container.addView(viewToAdd);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getOptionHandler() != null) {
                return getOptionHandler().onOptionsSelected(item);
            } else {
                finish();
            }
        }
        return true;
    }

    public interface OptionItemHandler {
        boolean onOptionsSelected(MenuItem item);
    }
}

/*public abstract class ScreenContainerActivity extends BaseActivity {

    private ViewBinding viewBinding;

    protected abstract ScreenContainer createScreenContainer();

    protected abstract ViewBinding getViewBinding(LayoutInflater inflater, ViewGroup container, boolean attachToParent);

    protected OptionItemHandler getOptionHandler() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the screen container
        ScreenContainer screenContainer = createScreenContainer();
        if (screenContainer != null) {
            ViewGroup container = screenContainer.bind(this);

            // Get the ViewBinding but do not attach it to the parent immediately
            viewBinding = getViewBinding(getLayoutInflater(), container, false);

            // Remove the view from any existing parent before adding
            View view = viewBinding.getRoot();
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }

            // Manually add the view to the container or set it as the content view
            container.addView(view);
            setContentView(view);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getOptionHandler() != null) {
                return getOptionHandler().onOptionsSelected(item);
            } else {
                finish();
            }
        }
        return true;
    }

    public interface OptionItemHandler {
        boolean onOptionsSelected(MenuItem item);
    }
}*/


/*
public abstract class ScreenContainerActivity extends BaseActivity {

    private ViewBinding viewBinding;

    protected abstract ScreenContainer createScreenContainer();

    protected abstract ViewBinding getViewBinding(LayoutInflater inflater, ViewGroup container, boolean attachToParent);

    protected OptionItemHandler getOptionHandler() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenContainer screenContainer = createScreenContainer();
        if (screenContainer != null) {
            ViewGroup container = screenContainer.bind(this);
            viewBinding = getViewBinding(getLayoutInflater(), container, true);
            setContentView(viewBinding.getRoot());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getOptionHandler() != null) {
                return getOptionHandler().onOptionsSelected(item);
            } else {
                finish();
            }
        }
        return true;
    }

    public interface OptionItemHandler {
        boolean onOptionsSelected(MenuItem item);
    }
}
*/
