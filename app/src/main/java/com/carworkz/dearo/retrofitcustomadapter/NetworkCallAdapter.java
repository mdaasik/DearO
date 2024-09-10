package com.carworkz.dearo.retrofitcustomadapter;

import com.carworkz.dearo.LoggingFacade;
import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.DeviceBandwidthSampler;

import java.io.IOException;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by Farhan on 3/8/17.
 */

public class NetworkCallAdapter<T> implements NetworkCall<T> {
    private static final String TAG = "NetworkCallAdapter";
    private final Call<T> call;
    private final Executor callbackExecutor;

    NetworkCallAdapter(Call<T> call, Executor callbackExecutor) {
        this.call = call;
        this.callbackExecutor = callbackExecutor;
    }


    @Override
    public void cancel() {
        call.cancel();
    }

    @Override
    public void enqueue(final NetworkCallBack<T> callback) {
        DeviceBandwidthSampler.getInstance().startSampling();
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(final Call<T> call, final Response<T> response) {
                DeviceBandwidthSampler.getInstance().stopSampling();
                callbackExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        switch (ConnectionClassManager.getInstance().getCurrentBandwidthQuality()) {
                            case POOR:
                                Timber.d("Connection quality poor, : in kb "+ConnectionClassManager.getInstance().getDownloadKBitsPerSecond());
                                break;
                            case MODERATE:
                                Timber.d("Connection quality moderate, : in kb "+ConnectionClassManager.getInstance().getDownloadKBitsPerSecond());
                                break;
                            case GOOD:
                                Timber.d("Connection quality good, : in kb "+ConnectionClassManager.getInstance().getDownloadKBitsPerSecond());
                                break;
                            case EXCELLENT:
                                Timber.d("Connection quality excellent, : in kb "+ConnectionClassManager.getInstance().getDownloadKBitsPerSecond());
                                break;
                            case UNKNOWN:
                                Timber.d("Connection quality unknown, : in kb "+ConnectionClassManager.getInstance().getDownloadKBitsPerSecond());
                                break;
                            default:
                                Timber.d("connection quality no clue, : in kb "+ConnectionClassManager.getInstance().getDownloadKBitsPerSecond());
                        }

                        int code = response.code();
                        if (code >= 200 && code < 300) {
                            callback.success(response);
                        } else if (code == 401) {
                            callback.unauthenticated(response);
                        } else if (code >= 400 && code < 500) {
                            callback.clientError(response);
                        } else if (code >= 500 && code < 600) {
                            callback.serverError(response);
                        } else {
                            if (call.isCanceled())
                                callback.unexpectedError(new InterruptedException("request was cancel"));
                            else {
//                                LoggingFacade.log("Username", SharedPrefHelper.getUserName());
//                                LoggingFacade.log("WorkshopName", SharedPrefHelper.getWorkShopName());
//                                LoggingFacade.log("accessToken", SharedPrefHelper.getUserAccessToken());
                                LoggingFacade.log("url", call.request().url().toString());
                                LoggingFacade.log("method", call.request().method());
                                LoggingFacade.log(new RuntimeException("Unexpected response " + response));
                                callback.unexpectedError(new RuntimeException("Unexpected response " + response));
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailure(final Call<T> call, final Throwable t) {
                DeviceBandwidthSampler.getInstance().stopSampling();
                callbackExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (t instanceof IOException) {
                            callback.networkError((IOException) t);
                            switch (ConnectionClassManager.getInstance().getCurrentBandwidthQuality()) {
                                case POOR:
                                    LoggingFacade.log("Conn quality", "Poor KBPS: " + ConnectionClassManager.getInstance().getDownloadKBitsPerSecond());
                                    break;
                                case MODERATE:
                                    LoggingFacade.log("Conn quality", "Moderate KBPS: " + ConnectionClassManager.getInstance().getDownloadKBitsPerSecond());
                                    break;
                                case GOOD:
                                    LoggingFacade.log("Conn quality", "Good KBPS: " + ConnectionClassManager.getInstance().getDownloadKBitsPerSecond());
                                    break;
                                case EXCELLENT:
                                    LoggingFacade.log("Conn quality", "Excellent KBPS: " + ConnectionClassManager.getInstance().getDownloadKBitsPerSecond());
                                    break;
                            }
                        } else {
                            callback.unexpectedError(t);
                        }
//                        LoggingFacade.log("Username", SharedPrefHelper.getUserName());
//                        LoggingFacade.log("WorkshopName", SharedPrefHelper.getWorkShopName());
//                        LoggingFacade.log("accessToken", SharedPrefHelper.getUserAccessToken());
                        LoggingFacade.log("url", call.request().url().toString());
                        LoggingFacade.log("method", call.request().method());
//                        LoggingFacade.log(t);
                    }
                });
            }
        });
    }

    @Override
    public NetworkCall<T> clone() {
        return new NetworkCallAdapter<>(call.clone(), callbackExecutor);
    }

}
