package com.carworkz.dearo.login.readotp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewbinding.ViewBinding;

import com.carworkz.dearo.BuildConfig;
import com.carworkz.dearo.DearOApplication;
import com.carworkz.dearo.LoggingFacade;
import com.carworkz.dearo.MainActivity;
import com.carworkz.dearo.OTPSmsReceiver;
import com.carworkz.dearo.R;
import com.carworkz.dearo.analytics.ScreenTracker;
import com.carworkz.dearo.base.ScreenContainer;
import com.carworkz.dearo.base.ScreenContainerActivity;
import com.carworkz.dearo.cronjobs.JobHelper;
import com.carworkz.dearo.data.local.SharedPrefHelper;
import com.carworkz.dearo.databinding.ActivityReadOtpBinding;
import com.carworkz.dearo.login.LoginScreenContainer;
import com.carworkz.dearo.utils.Utility;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import timber.log.Timber;

public class ReadOtpActivity extends ScreenContainerActivity implements ReadOtpContract.View {

    public static final String ARG_MOBILE_NUMBER = "arg_mobile_number";
    private static final String TAG = "ReadOtpActivity";
    private static final String TEMPLATE_FORMAT_RESEND_OTP = "Resend OTP in %d";
    ActivityReadOtpBinding binding;
    @Inject
    ReadOtpPresenter readOtpPresenter;
    @Inject
    ScreenTracker screenTracker;
    private EditText otpView;
    private String contactNumber;
    private final OTPSmsReceiver otpReceiver = new OTPSmsReceiver(new OTPSmsReceiver.SmsCallback() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onSmsReceived(int otp) {
            otpView.setText(otp + "");
            if (checkIfNetworkAvailable()) {
                readOtpPresenter.verifyOtp(otp, Utility.getServerAcceptableContactNumber(contactNumber));
            }
        }

        @Override
        public void onTimeOut() {
            Timber.d("sms retriever timeout");

        }
    });
    private ProgressBar progressBar;
    private Button submitBtn;
    private String text;
    private final CountDownTimer countDownTimer = new CountDownTimer(TimeUnit.SECONDS.toMillis(30), TimeUnit.SECONDS.toMillis(1)) {

        @Override
        public void onTick(long millisUntilFinished) {
            resendOtp.setText(String.format(Locale.getDefault(), TEMPLATE_FORMAT_RESEND_OTP, TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)));
            Timber.d("tick" + millisUntilFinished);
        }

        @Override
        public void onFinish() {
            resendOtp.setText(R.string.resend_otp);
            Timber.d("tick finished");
            resendOtp.setOnClickListener(view -> {
                if (checkIfNetworkAvailable()) {
                    readOtpPresenter.resendOtp(Utility.getServerAcceptableContactNumber(contactNumber));
                    resendOtp.setOnClickListener(null);
                    countDownTimer.start();
                }
            });

        }
    };
    private TextView readOtpErrorView, resendOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras() != null) {
            contactNumber = getIntent().getStringExtra(ARG_MOBILE_NUMBER).trim();
            if (!Utility.isMobileNumberValid(contactNumber)) {
                throw new IllegalArgumentException("Contact number not valid");
            }
        } else {
            if (BuildConfig.DEBUG) throw new IllegalArgumentException("pass contact mobile");
        }
        initComponent();
        initView();
        screenTracker.sendScreenEvent(this, ScreenTracker.SCREEN_READ_OTP, this.getClass().getName());
        Task retrieverTask = SmsRetriever.getClient(this).startSmsRetriever();
        retrieverTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {


                Timber.d("starting sms retriever successful");


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // For Android 13 and higher
                    registerReceiver(otpReceiver, new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION), Context.RECEIVER_EXPORTED);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // For Android 8.0 to Android 12
                    registerReceiver(otpReceiver, new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION), Context.RECEIVER_NOT_EXPORTED);
                } else {
                    // For older versions
                    registerReceiver(otpReceiver, new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION));
                }

                //     registerReceiver(otpReceiver, new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION));
            } else {
                Timber.d("starting sms retriever failed doing nothing");

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        readOtpPresenter.detachView();

        //case where registration fails because on unknown sms retriever failure
        try {
            if (otpReceiver != null) {
                unregisterReceiver(otpReceiver);
            }
            //  unregisterReceiver(otpReceiver);
        } catch (IllegalArgumentException e) {
            LoggingFacade.log(e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onOtpMatchSuccess() {
        LoggingFacade.log("Username", SharedPrefHelper.getUserName());
        LoggingFacade.log("WorkshopName", SharedPrefHelper.getWorkShopName());
        LoggingFacade.log("accessToken", SharedPrefHelper.getUserAccessToken());
        readOtpPresenter.getInitData();
        readOtpPresenter.checkForceUpdate("dearoapp", "android", BuildConfig.VERSION_CODE);
        JobHelper.scheduleAllMandatoryJobs(this);
        Intent startHomeActivityIntent = new Intent(ReadOtpActivity.this, MainActivity.class);
        startActivity(startHomeActivityIntent);
        countDownTimer.cancel();
        finishAffinity();
    }

    @Override
    public void showOtpDidNotMatchError(@NotNull String message) {
        readOtpErrorView.setVisibility(View.VISIBLE);
        readOtpErrorView.setText(message);
    }

    @Override
    public void showProgressIndicator() {
        showProgressBar();
    }

    @Override
    public void dismissProgressIndicator() {
        dismissProgressBar();
    }

    @Override
    public void showGenericError(@NotNull String errorMsg) {

        displayError(errorMsg);

    }

    @Override
    public void showOtpSentMessage() {
        Toast.makeText(this, "OTP sent", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected ScreenContainer createScreenContainer() {
        return new LoginScreenContainer();
    }

    @Override
    protected ViewBinding getViewBinding(LayoutInflater inflater, ViewGroup container, boolean attachToParent) {
        binding = ActivityReadOtpBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected View getProgressView() {
        return progressBar;
    }

    private void initView() {
        otpView = binding.etReadotp;
        progressBar = binding.pbMain;
        resendOtp = binding.tvReadOtpResend;
        readOtpErrorView = binding.tvReadotpError;
        TextView mobileNumberView = binding.tvReadotpMobile;
        mobileNumberView.setText(contactNumber);
        submitBtn = binding.submit;
        otpView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Timber.d(TAG, "onTextChanged: " + editable.toString());
                text = editable.toString();
                if (text.length() == 6) {
                    submitBtn.setVisibility(View.VISIBLE);
                    submitBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (text.length() == 6) {
                                int otp = Integer.parseInt(otpView.getText().toString());
                                if (checkIfNetworkAvailable()) {
                                    readOtpPresenter.verifyOtp(otp, Utility.getServerAcceptableContactNumber(contactNumber));
                                }
                            }

                        }
                    });
                } else {
                    submitBtn.setVisibility(View.GONE);
                }
            }
        });
        countDownTimer.start();

    }

    private void initComponent() {
        ((DearOApplication) getApplication()).getRepositoryComponent().COMPONENT(new ReadOtpPresenterModule(this)).inject(this);
    }


}
