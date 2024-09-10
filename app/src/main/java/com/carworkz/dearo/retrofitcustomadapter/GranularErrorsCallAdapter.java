package com.carworkz.dearo.retrofitcustomadapter;

import androidx.annotation.NonNull;
import java.lang.reflect.Type;
import java.util.concurrent.Executor;
import retrofit2.Call;
import retrofit2.CallAdapter;

/**
 * Created by Farhan on 3/8/17.
 */

public class GranularErrorsCallAdapter<R> implements CallAdapter<R, NetworkCall<R>> {
    private final Type responseType;
    private final Executor callbackExecutor;

    GranularErrorsCallAdapter(Type responseType, Executor callbackExecutor) {
        this.responseType = responseType;
        this.callbackExecutor = callbackExecutor;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public NetworkCall<R> adapt(@NonNull Call<R> call) {
        return new NetworkCallAdapter<>(call,callbackExecutor);
    }
}
