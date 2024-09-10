package com.carworkz.dearo.login.addmobilenumber;

import androidx.annotation.NonNull;
import com.carworkz.dearo.base.ErrorWrapper;
import com.carworkz.dearo.data.DataSource;
import com.carworkz.dearo.data.DearODataRepository;
import com.carworkz.dearo.domain.entities.NetworkPostResponse;
import com.carworkz.dearo.injection.ActivityScoped;
import com.carworkz.dearo.utils.Constants;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by Farhan on 31/7/17.
 */
@ActivityScoped
public class LoginPresenter implements LoginContract.Presenter {

    private static final String TAG = "LoginPresenter";
    private LoginContract.View view;

    private final DearODataRepository dearODataRepository;

    @Inject
    LoginPresenter(LoginContract.View view, DearODataRepository dearODataRepository) {
        this.view = view;
        this.dearODataRepository = dearODataRepository;
    }


    @Override
    public void initOtp(@NonNull String mobileNo) {
        view.toggleLoginButton(false);
        view.showProgressIndicator();
        dearODataRepository.sendOtp(mobileNo, new DataSource.OnResponseCallback<NetworkPostResponse>() {
            @Override
            public void onSuccess(NetworkPostResponse obj) {
                Timber.d(TAG, "onSuccess: ");
                if (view == null)
                    return;
                view.dismissProgressIndicator();
                view.moveToNextScreen();
                view.toggleLoginButton(true);
            }

            @Override
            public void onError(@NotNull ErrorWrapper error) {
                Timber.d(TAG, "onError: ");
                if (view == null)
                    return;

                view.dismissProgressIndicator();

                if (error.getErrorCode().equals(Constants.ErrorConstants.LOGIN_MOBILE_NOT_EXISTS)) {
                    view.moveToContactUsScreen(error.getHeader().getLeadId());
                } else {
                    view.showGenericError(error.getErrorMessage());
                }
                view.toggleLoginButton(true);
            }
        });

    }

    @Override
    public void logout() {
        dearODataRepository.logoutUser(new DataSource.OnResponseCallback<NetworkPostResponse>() {
            @Override
            public void onSuccess(@Nullable NetworkPostResponse obj) {

            }

            @Override
            public void onError(@NotNull ErrorWrapper error) {

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
}
