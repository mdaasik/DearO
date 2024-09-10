package com.carworkz.dearo.addjobcard.createjobcard;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.viewbinding.ViewBinding;

import com.carworkz.dearo.R;
import com.carworkz.dearo.addjobcard.createjobcard.accidental.AccidentalFragment;
import com.carworkz.dearo.addjobcard.createjobcard.damage.DamageFragment;
import com.carworkz.dearo.addjobcard.createjobcard.estimate.EstimateFragment;
import com.carworkz.dearo.addjobcard.createjobcard.inspection.InspectionFragment;
import com.carworkz.dearo.addjobcard.createjobcard.inventory.InventoryFragment;
import com.carworkz.dearo.addjobcard.createjobcard.jobs.JobsFragment;
import com.carworkz.dearo.addjobcard.createjobcard.jobs.viewjc.ViewJCFragment;
import com.carworkz.dearo.addjobcard.createjobcard.voice.VoiceFragment;
import com.carworkz.dearo.base.EventsManager;
import com.carworkz.dearo.base.ScreenContainer;
import com.carworkz.dearo.base.ScreenContainerActivity;
import com.carworkz.dearo.databinding.ActivityCreateJobCardBinding;
import com.carworkz.dearo.domain.entities.JobCard;
import com.carworkz.dearo.interactionprovider.ToolBarInteractionProvider;
import com.carworkz.dearo.screencontainer.SingleTextActionScreenContainer;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import timber.log.Timber;

public class CreateJobCardActivity extends ScreenContainerActivity implements ToolBarInteractionProvider, ICreateJobCardInteraction {

    public static final String VEHICLE_AMC_ID = "vehicle-amc-id";
    public static final String ARG_JOB_CARD = "job_card";
    public static final String ARG_INIT = "init";
    public static final String ARG_IS_VIEW_ONLY = "is_view_only";
    public static final String ARG_IS_ADD_JOB = "is_add_job";
    public static final String ARG_VEHICLE_ID = "Vehicle";
    public static final String ARG_ISHISTORY = "is_history";
    public static final int RESULT_CODE = 1001;
    //    private static final String TAG = "CreateJobCardActivity";
    private static final String POS_ACCIDENTAL = "Insurance";
    private static final String POS_VOICE = "Voice";
    private static final String POS_DAMAGE = "Damage";
    private static final String POS_INVENTORY = "Inventory";
    private static final String POS_INSPECTION = "Inspection";
    private static final String POS_JOB = "Jobs";
    private static final String POS_ESTIMATE = "Estimate";


    private static final String STEP_ACCIDENTAL = "ACCIDENTAL";
    private static final String STEP_VOICE = "VERBATIM";
    private static final String STEP_DAMAGE = "DAMAGES";
    private static final String STEP_INVENTORY = "INVENTORY";
    private static final String STEP_INSPECTION = "INSPECTION";
    private static final String STEP_JOB = "JOB";
    private static final String STEP_ESTIMATE = "ESTIMATE";
    private static final int DEFAULT = 0;
    ActivityCreateJobCardBinding binding;
    // private String[] stepPos = {POS_VOICE, POS_DAMAGE, POS_INVENTORY, POS_INSPECTION, POS_JOB, POS_ESTIMATE};
    private TabLayout tabLayout;
    private ProgressBar progressBar;
    private SingleTextActionScreenContainer screenContainer;
    private JobCard jobCard;
    private String jobCardId, vehicleRegNo;
    private boolean isViewOnly, isAddJob;
    private String vehicleAmcId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            jobCard = bundle.getParcelable(ARG_JOB_CARD);
            vehicleRegNo = bundle.getString(ARG_VEHICLE_ID);
            isViewOnly = bundle.getBoolean(ARG_IS_VIEW_ONLY);
            isAddJob = bundle.getBoolean(ARG_IS_ADD_JOB);
            vehicleAmcId = bundle.getString(VEHICLE_AMC_ID);
            if (jobCard != null) {
                jobCardId = jobCard.getId();
            } else {
                throw new IllegalArgumentException("JobCard is null");
            }
            //   jobCardId = jobCard.getId();
        } else {
            throw new IllegalArgumentException("Please pass job card Number & vehicle Number in intent");
        }
        super.onCreate(savedInstanceState);
        tabLayout = binding.tabLayoutCreateJobCard;
        progressBar = binding.pbMain;
        initTabs();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!isViewOnly || jobCard.getStatus().equals(JobCard.STATUS_INITIATED)
                || jobCard.getStatus().equals(JobCard.STATUS_IN_PROGRESS)) {
            showExitDialog();
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            if (!isViewOnly || jobCard.getStatus().equals(JobCard.STATUS_INITIATED) || jobCard.getStatus().equals(JobCard.STATUS_IN_PROGRESS)) {
                showExitDialog();
            } else {
                finish();
            }
        return true;
    }

    @Override
    protected ScreenContainer createScreenContainer() {
        screenContainer = new SingleTextActionScreenContainer(this);
        return screenContainer;
    }

    @Override
    protected ViewBinding getViewBinding(LayoutInflater inflater, ViewGroup container, boolean attachToParent) {
        binding = ActivityCreateJobCardBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected View getProgressView() {
        return progressBar;
    }

    @Override
    public void onJobFailure() {
        dismissProgressBar();
    }

    @Override
    public void onJobSuccess() {
        dismissProgressBar();
        if (tabLayout.getSelectedTabPosition() < tabLayout.getTabCount()) {
            toggleTabStatus(tabLayout.getSelectedTabPosition());
            delegateTabSelect(tabLayout.getSelectedTabPosition() + 1);
        }
    }

    @Override
    public void onJobVerify() {
        if (tabLayout.getSelectedTabPosition() < tabLayout.getTabCount()) {
            toggleTabStatus(tabLayout.getSelectedTabPosition());
            delegateTabSelect(findIndexByTag(STEP_JOB));
        }
    }

    @Override
    public void onActionBtnClick() {
        if (tabLayout.getSelectedTabPosition() < tabLayout.getTabCount() && checkIfNetworkAvailable()) {
            showProgressBar();
            EventsManager.post(new ActionButtonClickEvent());
        }
    }

    @NonNull
    @Override
    public String getToolBarTitle() {
        return jobCard.getJobCardId() + "-" + vehicleRegNo;
    }

    @NonNull
    @Override
    public String getActionBtnTitle() {
        if (tabLayout != null) {
            int i = tabLayout.getSelectedTabPosition();//                case 0:
//                    if (jobCard.getStatus().equals(JobCard.STATUS_COMPLETED) || jobCard.getStatus().equals(JobCard.STATUS_CLOSED))
//                        return "NEXT";
//                    else
//                        return "SAVE";
            if (i == findIndexByTag(STEP_JOB)) {
                if (jobCard.getStatus().equals(JobCard.STATUS_COMPLETED) || jobCard.getStatus().equals(JobCard.STATUS_CLOSED))
                    return "NEXT";
                else return "SAVE";
            } else if (i == findIndexByTag(STEP_ESTIMATE)) {
                if (jobCard.getStatus().equals(JobCard.STATUS_IN_PROGRESS) || jobCard.getStatus().equals(JobCard.STATUS_INITIATED))
                    return "SAVE";
                else return "";
            } else {
                if (isViewOnly) return "NEXT";
                else return "SAVE";
            }
        }
        if (isViewOnly) return "NEXT";
        else return "SAVE";
    }

    public void showProgressBar() {
        super.showProgressBar();
    }

    public void dismissProgressBar() {
        super.dismissProgressBar();
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to Exit?").setMessage("Make sure you have saved the information before exiting the Job Card").setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }).setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss()).show();
    }

    private void initTabs() {
        if (jobCard.getType().equals(JobCard.TYPE_ACCIDENTAL)) {
            tabLayout.addTab(tabLayout.newTab().setText(POS_ACCIDENTAL).setTag(STEP_ACCIDENTAL));
        }
        tabLayout.addTab(tabLayout.newTab().setText(POS_VOICE).setTag(STEP_VOICE));
        tabLayout.addTab(tabLayout.newTab().setText(POS_INVENTORY).setTag(STEP_INVENTORY));
        tabLayout.addTab(tabLayout.newTab().setText(POS_DAMAGE).setTag(STEP_DAMAGE));
        tabLayout.addTab(tabLayout.newTab().setText(POS_INSPECTION).setTag(STEP_INSPECTION));
        tabLayout.addTab(tabLayout.newTab().setText(POS_JOB).setTag(STEP_JOB));
        tabLayout.addTab(tabLayout.newTab().setText(POS_ESTIMATE).setTag(STEP_ESTIMATE));


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (!isFinishing() && tab.getText() != null) {
                    inflateJobStepFragment(tab.getText().toString());
                    toggleNextButton();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        switch (jobCard.getStatus()) {
            /*check in progress status first*/
            case JobCard.STATUS_IN_PROGRESS:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        delegateTabSelect(findIndexByTag(STEP_JOB));
                    }
                }, 100);
                toggleTabStatus(findIndexByTag(STEP_JOB) + 1);
                //Handler to Show Visual Selection Of Tab
                //inflateJobStepFragment(POS_JOB);
                break;
            case JobCard.STATUS_INITIATED:
                switch (jobCard.getStep()) {
                    case STEP_ACCIDENTAL:
                        delegateTabSelect(findIndexByTag(STEP_ACCIDENTAL));
                        toggleTabStatus(findIndexByTag(STEP_ACCIDENTAL));
                        inflateJobStepFragment("");
                        break;
                    case STEP_VOICE:
                        /*since default jobcard step is verbatim and accidental is config based(can't be made default)*/
                        if (jobCard.getType().equals(JobCard.TYPE_ACCIDENTAL)) {
                            delegateTabSelect(findIndexByTag(STEP_VOICE));
                            toggleTabStatus(findIndexByTag(STEP_VOICE));
                        } else {
                            delegateTabSelect(findIndexByTag(STEP_VOICE));
                            toggleTabStatus(findIndexByTag(STEP_VOICE));
                        }
                        /*empty to add fragment instead of replace*/
                        inflateJobStepFragment("");
                        break;
                    case STEP_INVENTORY:
                        delegateTabSelect(findIndexByTag(STEP_INVENTORY));
                        toggleTabStatus(findIndexByTag(STEP_INVENTORY));
                        inflateJobStepFragment(POS_INVENTORY);
                        break;
                    case STEP_DAMAGE:
                        delegateTabSelect(findIndexByTag(STEP_DAMAGE));
                        toggleTabStatus(findIndexByTag(STEP_DAMAGE));
                        inflateJobStepFragment(POS_DAMAGE);
                        break;
                    case STEP_INSPECTION:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                delegateTabSelect(findIndexByTag(STEP_INSPECTION));
                            }
                        }, 100);
                        toggleTabStatus(findIndexByTag(STEP_INSPECTION));
                        inflateJobStepFragment(POS_INSPECTION);
                        break;
                    case STEP_JOB:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                delegateTabSelect(findIndexByTag(STEP_JOB));
                            }
                        }, 100);
                        toggleTabStatus(findIndexByTag(STEP_JOB));
                        inflateJobStepFragment(POS_JOB);
                        break;
                    case STEP_ESTIMATE:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                delegateTabSelect(findIndexByTag(STEP_ESTIMATE));
                            }
                        }, 100);
                        toggleTabStatus(findIndexByTag(STEP_ESTIMATE));
                        inflateJobStepFragment(POS_ESTIMATE);
                        break;
                    default:
                        delegateTabSelect(DEFAULT);
                        toggleTabStatus(DEFAULT);
                        if (jobCard.getType().equals(JobCard.TYPE_ACCIDENTAL)) {
                            inflateJobStepFragment(POS_ACCIDENTAL);
                        } else {
                            inflateJobStepFragment(POS_VOICE);
                        }
                        break;
                }
                break;
            default:
                delegateTabSelect(DEFAULT);
                toggleTabStatus(DEFAULT);
                if (jobCard.getType().equals(JobCard.TYPE_ACCIDENTAL)) {
                    inflateJobStepFragment(POS_ACCIDENTAL);
                } else {
                    inflateJobStepFragment(POS_VOICE);
                }
                break;
        }
    }

    private int findIndexByTag(String tag) {
        Timber.d("Searching : " + tag);
        int index = -1;
        for (int x = 0; x < tabLayout.getTabCount(); x++) {
            TabLayout.Tab tab = tabLayout.getTabAt(x);
            if (tab != null && tab.getTag() != null && tab.getTag().toString().equals(tag)) {
                index = x;
                break;
            }
        }
        return index;
    }

    private void toggleTabStatus(int lastActiveTabPos) {
        LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        if (isViewOnly) {
            for (int j = 0; j < tabLayout.getChildCount(); j++) {
                // final int finalJ = j + 1;
                tabStrip.getChildAt(j).setOnTouchListener(new View.OnTouchListener() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
//                        v.performClick();
                        return false;
                    }
                });
            }
        } else {
            for (int i = tabStrip.getChildCount() - 1; i > lastActiveTabPos; i--) {
                tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
//                        v.performClick();
                        Toast.makeText(CreateJobCardActivity.this, R.string.completeStep_Save, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
            }
            for (int j = 0; j <= lastActiveTabPos; j++) {
                // final int finalJ = j + 1;
                tabStrip.getChildAt(j).setOnTouchListener(new View.OnTouchListener() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
//                        v.performClick();
                        return false;
                    }
                });
            }
        }

        //currentTab = lastActiveTabPos;
    }

    private void inflateJobStepFragment(String step) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        tabLayout.setSmoothScrollingEnabled(true);
        switch (step) {
            case POS_ACCIDENTAL:
                AccidentalFragment accidentalFragment = AccidentalFragment.newInstance(jobCardId, jobCard.getStatus(), isViewOnly);
                fragmentManager.beginTransaction().replace(R.id.fl_create_job_container, accidentalFragment).commitNowAllowingStateLoss();
                break;
            case POS_VOICE:
                VoiceFragment voiceFragment = VoiceFragment.newInstance(isViewOnly, jobCardId, jobCard.getStatus(), jobCard.getAppointmentId());
                fragmentManager.beginTransaction().replace(R.id.fl_create_job_container, voiceFragment).commitNowAllowingStateLoss();
                break;
            case POS_INVENTORY:
                InventoryFragment inventoryFragment = InventoryFragment.newInstance(isViewOnly, jobCardId);
                fragmentManager.beginTransaction().replace(R.id.fl_create_job_container, inventoryFragment).commitNowAllowingStateLoss();
                break;
            case POS_DAMAGE:
                DamageFragment damageFragment = DamageFragment.newInstance(jobCard.getStatus(), isViewOnly, jobCardId);
                fragmentManager.beginTransaction().replace(R.id.fl_create_job_container, damageFragment).commitNowAllowingStateLoss();
                break;
            case POS_INSPECTION:
                InspectionFragment inspectionFragment = InspectionFragment.newInstance(!(jobCard.getStatus().equals(JobCard.STATUS_INITIATED) || jobCard.getStatus().equals(JobCard.STATUS_IN_PROGRESS)), jobCardId, jobCard.getVehicleType());
                fragmentManager.beginTransaction().replace(R.id.fl_create_job_container, inspectionFragment).commitNowAllowingStateLoss();
                break;
            case POS_JOB:
                if (Objects.equals(jobCard.getStatus(), JobCard.STATUS_INITIATED)) {
                    JobsFragment jobsFragment = JobsFragment.newInstance(isViewOnly, jobCardId, jobCard.getVehicleType());
                    fragmentManager.beginTransaction().replace(R.id.fl_create_job_container, jobsFragment).commitNowAllowingStateLoss();
                } else {
                    ViewJCFragment viewJCFragment = ViewJCFragment.newInstance(isViewOnly && !jobCard.getStatus().equals(JobCard.STATUS_IN_PROGRESS), isAddJob, jobCardId, jobCard.getVehicleType());
                    fragmentManager.beginTransaction().replace(R.id.fl_create_job_container, viewJCFragment).commitNowAllowingStateLoss();
                    // isAddJob should be true only once while opening fragment for the first time.
                    isAddJob = false;
                }
                break;
            case POS_ESTIMATE:
                boolean viewState = isViewOnly && jobCard.getStatus().equals(JobCard.STATUS_COMPLETED) || jobCard.getStatus().equals(JobCard.STATUS_CLOSED) || jobCard.getStatus().equals(JobCard.STATUS_CANCELLED);
                EstimateFragment estimateFragment = EstimateFragment.newInstance(viewState, jobCardId, vehicleAmcId);
                fragmentManager.beginTransaction().replace(R.id.fl_create_job_container, estimateFragment).commitNowAllowingStateLoss();
                tabLayout.setScrollX(600);
                break;
            default:
                if (jobCard.getType().equals(JobCard.TYPE_ACCIDENTAL)) {
                    AccidentalFragment initAccidental = AccidentalFragment.newInstance(jobCardId, jobCard.getStatus(), isViewOnly);
                    fragmentManager.beginTransaction().add(R.id.fl_create_job_container, initAccidental).commitNowAllowingStateLoss();
                } else {
                    VoiceFragment initVoiceFragment = VoiceFragment.newInstance(isViewOnly, jobCardId, jobCard.getStatus(), jobCard.getAppointmentId());
                    fragmentManager.beginTransaction().add(R.id.fl_create_job_container, initVoiceFragment).commitNowAllowingStateLoss();
                }
                break;
        }
    }

    private void toggleNextButton() {
        // if (tabLayout.getSelectedTabPosition() == tabLayout.getTabCount() - 1)
        screenContainer.refreshToolBar();
    }

    private void delegateTabSelect(int position) {
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        if (tab != null) {
            tab.select();
        }
    }
}
