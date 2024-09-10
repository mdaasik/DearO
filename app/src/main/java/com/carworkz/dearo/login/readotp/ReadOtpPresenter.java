package com.carworkz.dearo.login.readotp;

import androidx.annotation.NonNull;
import com.carworkz.dearo.base.ErrorWrapper;
import com.carworkz.dearo.data.DataSource;
import com.carworkz.dearo.data.DearODataRepository;
import com.carworkz.dearo.domain.entities.HSN;
import com.carworkz.dearo.domain.entities.NetworkPostResponse;
import com.carworkz.dearo.domain.entities.State;
import com.carworkz.dearo.domain.entities.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by Farhan on 2/8/17.
 */

public class ReadOtpPresenter implements ReadOtpContract.Presenter {
    private static final String TAG = "ReadOtpPresenter";
    private ReadOtpContract.View view;
    private final DearODataRepository repository;

    @Inject
    ReadOtpPresenter(ReadOtpContract.View view, DearODataRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void verifyOtp(int otp, @NonNull String mobileNumber) {
        view.showProgressIndicator();
        repository.loginUser(mobileNumber, otp, new DataSource.OnResponseCallback<User>() {
            @Override
            public void onSuccess(User obj) {
                if (view == null)
                    return;

                view.dismissProgressIndicator();
                view.onOtpMatchSuccess();
            }

            @Override
            public void onError(@NotNull ErrorWrapper error) {
                if (view == null)
                    return;

                view.dismissProgressIndicator();
                view.showOtpDidNotMatchError(error.getErrorMessage());
            }
        });
    }

    @Override
    public void start() {

    }

    @Override
    public void detachView() {
        view = null;

    }

    @Override
    public void resendOtp(@NotNull String mobileNo) {
        view.showProgressIndicator();
        repository.sendOtp(mobileNo, new DataSource.OnResponseCallback<NetworkPostResponse>() {
            @Override
            public void onSuccess(NetworkPostResponse obj) {
                if (view == null)
                    return;

                view.dismissProgressIndicator();
                view.showOtpSentMessage();
            }

            @Override
            public void onError(@NotNull ErrorWrapper error) {
                if (view == null)
                    return;

                view.dismissProgressIndicator();
                view.showGenericError(error.getErrorMessage());
            }
        });
    }

    private void getHsn() {
        repository.getHSN(new DataSource.OnResponseCallback<List<HSN>>() {
            @Override
            public void onSuccess(List<HSN> obj) {
                Timber.d("Fetched HSN");
            }

            @Override
            public void onError(@NotNull ErrorWrapper error) {

            }
        });
    }

    private void getUserConfig() {
        repository.getUserConfig(new DataSource.OnResponseCallback<User>() {
            @Override
            public void onError(@NotNull ErrorWrapper error) {
                Timber.e("gst Status " +error.getErrorMessage());

            }

            @Override
            public void onSuccess(User obj) {
                Timber.d("ReadOtp gst "+obj.getStatus());
            }
        });
    }

    private void getStates(){
        repository.getStates(new DataSource.OnResponseCallback<List<State>>() {
            @Override
            public void onError(@NotNull ErrorWrapper error) {
                Timber.e("gst Status " +error.getErrorMessage());

            }

            @Override
            public void onSuccess(List<State> obj) {
                Timber.d("ReadOtp states "+obj.size());
            }
        });
    }

    @Override
    public void checkForceUpdate(@NotNull String appName, @NotNull String platform, int versionCode) {
        repository.checkForceUpdate(appName, platform, versionCode, null);
    }

    @Override
    public void getInitData() {
        getUserConfig();
        getHsn();
        getStates();
    }
}
