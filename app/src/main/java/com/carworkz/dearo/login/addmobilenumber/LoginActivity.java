package com.carworkz.dearo.login.addmobilenumber;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.viewbinding.ViewBinding;

import com.carworkz.dearo.BuildConfig;
import com.carworkz.dearo.DearOApplication;
import com.carworkz.dearo.MainActivity;
import com.carworkz.dearo.R;
import com.carworkz.dearo.analytics.ScreenTracker;
import com.carworkz.dearo.base.ScreenContainer;
import com.carworkz.dearo.base.ScreenContainerActivity;
import com.carworkz.dearo.data.local.SharedPrefHelper;
import com.carworkz.dearo.databinding.ActivityLoginBinding;
import com.carworkz.dearo.login.WebPageActivity;
import com.carworkz.dearo.login.lead.LeadActivity;
import com.carworkz.dearo.login.readotp.ReadOtpActivity;
import com.carworkz.dearo.utils.Utility;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.api.GoogleApiClient;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import timber.log.Timber;

public class LoginActivity extends ScreenContainerActivity implements LoginContract.View {

    private static final String TAG = "LoginActivity";
    private static final String EXTRA_POPUP_MESSAGE = "arg_popup_message";
    private static final int REQUEST_CODE = 1000;
    private static final int REQUEST_RESOLVE_HINT = 100;
    ActivityLoginBinding binding;
    @Inject
    LoginPresenter presenter;

    @Inject
    ScreenTracker screenTracker;

    private LoginComponent loginComponent;

    private EditText mobileNumberView;
    private String contactNo;
    private ProgressBar progressBar;
    private Button nextButtonView;

    public static Intent getStartIntent(Context context, String popUpMessage) {
        Intent intent = getStartIntent(context, true);
        intent.putExtra(EXTRA_POPUP_MESSAGE, popUpMessage);
        return intent;
    }

    public static Intent getStartIntent(Context context, boolean clearPreviousActivities) {
        Intent intent = new Intent(context, LoginActivity.class);
        if (clearPreviousActivities) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!SharedPrefHelper.getUserAccessToken().isEmpty()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        createComponent();
        initViews();
        screenTracker.sendScreenEvent(this, ScreenTracker.SCREEN_LOGIN, this.getClass().getName());
        //requestHint();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginComponent = null;
        presenter.detachView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    mobileNumberView.getText().clear();
                }

            }

//            case REQUEST_RESOLVE_HINT: {
//                if (resultCode == RESULT_OK) {
//                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
//                    Timber.d("number "+credential.getId());
//                }
//
//            }
        }

    }

    @Override
    public void toggleLoginButton(boolean isEnabled) {
        nextButtonView.setEnabled(isEnabled);
    }

    @Override
    protected ScreenContainer createScreenContainer() {
        return null;
    }

    @Override
    protected ViewBinding getViewBinding(LayoutInflater inflater, ViewGroup container, boolean attachToParent) {
        return null;
    }


    @Override
    protected View getProgressView() {
        return progressBar;
    }

    @Override
    public void moveToNextScreen() {
        Intent startReadOtpIntent = new Intent(this, ReadOtpActivity.class);
        startReadOtpIntent.putExtra(ReadOtpActivity.ARG_MOBILE_NUMBER, contactNo);
        startActivity(startReadOtpIntent);
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
    public void showPhoneNumberNotValidError() {
        mobileNumberView.setError(getString(R.string.error_mobile_number_not_valid));
    }

    @Override
    public void moveToContactUsScreen(@NotNull String leadId) {
        startActivityForResult(LeadActivity.getIntent(this, leadId, mobileNumberView.getText().toString()), REQUEST_CODE);
    }

    private void createComponent() {
        loginComponent = ((DearOApplication) getApplication()).getRepositoryComponent().COMPONENT(new LoginPresenterModule(this));
        loginComponent.inject(this);
    }

    private void initViews() {
        progressBar = binding.pbLogin;
        TextView versionView = binding.tvLoginVersion;
        String versionName = "Version " + BuildConfig.VERSION_NAME;
        versionView.setText(versionName);
        TextView termsAndConditionView = binding.tAndC;
        TextView policyView = binding.policy;
        termsAndConditionView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), WebPageActivity.class);
            intent.putExtra(WebPageActivity.TYPE, WebPageActivity.TERMS_AND_CONDITION);
            startActivity(intent);
        });
        policyView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), WebPageActivity.class);
            intent.putExtra(WebPageActivity.TYPE, WebPageActivity.PRIVACY_POLICY);
            startActivity(intent);
        });
        mobileNumberView = binding.etLoginMobileNo;
        nextButtonView = binding.btnLoginNext;
        nextButtonView.setOnClickListener(view -> {
            Timber.d(TAG, "onClick: ");
            contactNo = mobileNumberView.getText().toString();
            if (checkIfNetworkAvailable()) {
                if (Utility.isMobileNumberValid(mobileNumberView.getText().toString()) && !mobileNumberView.getText().toString().isEmpty()) {
                    presenter.initOtp(Utility.getServerAcceptableContactNumber(contactNo));
                } else {
                    if (mobileNumberView.getText().toString().isEmpty()) {
                        mobileNumberView.setError("Phone mobile is required");
                    } else if (!Utility.isMobileNumberValid(mobileNumberView.getText().toString())) {
                        mobileNumberView.setError("Invalid Mobile Number");
                    }
                }
            }
        });
    }

    private void requestHint() {
        HintRequest hintRequest = new HintRequest.Builder().setPhoneNumberIdentifierSupported(true).build();


        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(new GoogleApiClient.Builder(this).build(), hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(), REQUEST_RESOLVE_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

}
