package com.carworkz.dearo.addjobcard.createjobcard.inventory;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.carworkz.dearo.DearOApplication;
import com.carworkz.dearo.R;
import com.carworkz.dearo.addjobcard.createjobcard.ActionButtonClickEvent;
import com.carworkz.dearo.addjobcard.createjobcard.ICreateJobCardInteraction;
import com.carworkz.dearo.analytics.ScreenTracker;
import com.carworkz.dearo.base.BaseFragment;
import com.carworkz.dearo.base.EventsManager;
import com.carworkz.dearo.base.ScreenContainerActivity;
import com.carworkz.dearo.domain.entities.Inventory;
import com.carworkz.dearo.utils.Utility;
import com.google.android.material.textfield.TextInputLayout;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;


public class InventoryFragment extends BaseFragment implements InventoryContract.View, EventsManager.EventSubscriber {
    private static final String ARG_TYPE = "arg_type";
    private static final String ARG_JOB_CARD_ID = "arg_job_card_id";
    private static final String SERVICE_TYPE_WALK_IN = "walk-in";
    private static final String SERVICE_TYPE_PICK_UP = "pickup";
    private static final String SERVICE_TYPE_DOORSTEP = "doorstep";
    @Inject
    InventoryPresenter presenter;
    private boolean isViewOnly;
    private RecyclerView inventoryRecyclerView;
    private RadioGroup radioGroup;
    private RadioButton walkInView, pickUpView, doorStepView;
    private EditText remarksView, kmView;
    private SeekBar seekBar;
    private InventoryAdapter adapter;
    private float initial = 180f;
    private ICreateJobCardInteraction interaction;
    @NotNull
    private String jobCardId = "";

    @Inject
    ScreenTracker screenTracker;


    public InventoryFragment() {
        // Required empty public constructor
    }

    public static InventoryFragment newInstance(boolean isViewOnly, String jobCardId) {
        InventoryFragment fragment = new InventoryFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_TYPE, isViewOnly);
        args.putString(ARG_JOB_CARD_ID, jobCardId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isViewOnly = getArguments().getBoolean(ARG_TYPE);
            jobCardId = getArguments().getString(ARG_JOB_CARD_ID, "");
        }
        ((DearOApplication) getActivity().getApplication())
                .getRepositoryComponent()
                .COMPONENT(new InventoryPresenterModule(this))
                .inject(this);

        if (isViewOnly)
            screenTracker.sendScreenEvent(getActivity(), ScreenTracker.SCREEN_VIEW_INVENTORY, getClass().getName());
        else
            screenTracker.sendScreenEvent(getActivity(), ScreenTracker.SCREEN_INVENTORY, getClass().getName());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        container = view.findViewById(R.id.fl_inventory_km_fuel_container);
        getLayoutInflater().inflate(R.layout.layout_inventory_km_fuel_editable, container);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        inventoryRecyclerView = view.findViewById(R.id.rv_inventory);

        remarksView = view.findViewById(R.id.et_inventory_remarks);
        kmView = view.findViewById(R.id.et_inventory_km);
        TextInputLayout kmInputLayout = view.findViewById(R.id.tip_inventory_km);
        //Timber.d(kmInputLayout.getEditText().gettex + "textsize");
        seekBar = view.findViewById(R.id.sb_inventory_fuel);
        radioGroup = view.findViewById(R.id.rg_inventory);
        walkInView = view.findViewById(R.id.rb_inventory_walkin);
        pickUpView = view.findViewById(R.id.rb_inventory_pick_up);
        doorStepView = view.findViewById(R.id.rb_inventory_doorstep);
        final View needle = view.findViewById(R.id.needle);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        inventoryRecyclerView.setLayoutManager(layoutManager);
        if (isViewOnly) {
            kmView.setEnabled(false);
            seekBar.setEnabled(false);
            remarksView.setEnabled(false);
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                radioGroup.getChildAt(i).setEnabled(false);
            }
        }
        final AnimationSet animationSet = new AnimationSet(true);
        animationSet.setInterpolator(new DecelerateInterpolator());
        animationSet.setFillAfter(true);
        animationSet.isFillEnabled();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                needle.setVisibility(View.VISIBLE);
                Timber.d("progress ", progress / 1.8f + "");
                RotateAnimation rotationAnimation = new RotateAnimation(initial, 180f + progress, 1.0f, 1.0f);
                initial = 180f + progress;
                rotationAnimation.setDuration(500);
                rotationAnimation.setFillAfter(true);
                animationSet.addAnimation(rotationAnimation);
                needle.startAnimation(rotationAnimation);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ICreateJobCardInteraction) {
            interaction = (ICreateJobCardInteraction) context;
        } else
            throw new IllegalStateException("Activity must implement ICreateJobCardInteraction interface ");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (checkIfNetworkAvailable()) {
            presenter.getSelectedInventoryList(jobCardId);
        } else {
            dismissProgressIndicator();
            Toast.makeText(getActivity(), "Check Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventsManager.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventsManager.unregister(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        presenter.detachView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        interaction = null;
    }

    @Override
    public void displayInventoryList(@NotNull List<? extends Inventory> inventories) {
        //noinspection unchecked
        List<Inventory> inventoryList = (List<Inventory>) inventories;
        adapter = new InventoryAdapter(inventoryList, getActivity(), isViewOnly);
        inventoryRecyclerView.setAdapter(adapter);
    }


    @Subscribe
    public void onNextBtnEvent(ActionButtonClickEvent event) {
        if (isViewOnly) {
            interaction.onJobSuccess();
            return;
        }
        int kmReading;
        String remarks = "";
        int fuelReading = (int) (seekBar.getProgress() / 1.8f);
        Timber.d("fuel", String.valueOf(fuelReading));

        if (!kmView.getText().toString().isEmpty()) {
            kmReading = Integer.parseInt(kmView.getText().toString());
        } else {
            kmReading = 0;
        }

        if (!remarksView.getText().toString().isEmpty()) {
            remarks = remarksView.getText().toString();
        }
        int checkRadioId = radioGroup.getCheckedRadioButtonId();
        String serviceType = "";
        switch (checkRadioId) {
            case R.id.rb_inventory_walkin:
                serviceType = SERVICE_TYPE_WALK_IN;
                break;
            case R.id.rb_inventory_pick_up:
                serviceType = SERVICE_TYPE_PICK_UP;
                break;
            case R.id.rb_inventory_doorstep:
                serviceType = SERVICE_TYPE_DOORSTEP;
                break;
        }
        // if (seekBar.getProgress() != 0) {
        if (checkIfNetworkAvailable()) {
            presenter.saveJobCardInventory(jobCardId, serviceType, fuelReading, kmReading, adapter.getSelectedInventoryList(), remarks);
            Utility.hideSoftKeyboard(getActivity());
        } else {
            Toast.makeText(getActivity(), "Check Internet Connection", Toast.LENGTH_SHORT).show();
        }
//        } else {
//            interaction.onJobFailure();
//            Toast.makeText(getActivity(), "Fuel Reading required", Toast.LENGTH_LONG).show();
//        }
    }

    @Override
    public void showProgressIndicator() {
        ((ScreenContainerActivity) getActivity()).showProgressBar();
    }

    @Override
    public void dismissProgressIndicator() {
        if (getActivity() != null)
            ((ScreenContainerActivity) getActivity()).dismissProgressBar();
    }

    @Override
    public void showGenericError(@NotNull String errorMsg) {
        displayError(errorMsg);
    }

    @Override
    public void onInventorySaveSuccess() {
        interaction.onJobSuccess();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void displayInventoryItems(@Nullable String serviceType, int fuelReading, int kmsReading, @NonNull String userComment) {

        //1.8 is the factor to adjust seekBar value with fuel needle 0-100.
        Timber.d("Progress", String.valueOf(fuelReading / 1.4f));
        seekBar.setProgress((int) (fuelReading * 1.8f));
        kmView.setText(kmsReading + "");
        remarksView.setText(userComment);
        if (serviceType != null) {
            switch (serviceType) {

                case SERVICE_TYPE_DOORSTEP:
                    doorStepView.setChecked(true);
                    break;
                case SERVICE_TYPE_PICK_UP:
                    pickUpView.setChecked(true);
                    break;
                case SERVICE_TYPE_WALK_IN:
                    walkInView.setChecked(true);
                    break;
            }
        }

    }

    @Override
    public void showServiceTypeError(@NotNull String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showKmsReadingError(@NotNull String message) {
        kmView.setError(message);
    }

    @Override
    public void showInventoryListError(@NotNull String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void showRemarksError(@NotNull String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showFuelReadingError(@NotNull String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
