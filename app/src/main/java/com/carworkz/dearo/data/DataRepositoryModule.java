package com.carworkz.dearo.data;

import androidx.annotation.NonNull;
import com.carworkz.dearo.BuildConfig;
import com.carworkz.dearo.UnsafeOkHttpClient;
import com.carworkz.dearo.data.local.LocalDataSource;
import com.carworkz.dearo.data.local.SharedPrefHelper;
import com.carworkz.dearo.data.remote.NetworkDataSource;
import com.carworkz.dearo.data.remote.RetroFitService;
import com.carworkz.dearo.helpers.ErrorWrapperHelper;
import com.carworkz.dearo.injection.ApplicationScoped;
import com.carworkz.dearo.retrofitcustomadapter.GranularErrorsCallAdapterFactory;
import com.carworkz.dearo.utils.Constants;
import com.carworkz.dearo.utils.Utility;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.inject.Named;
import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import timber.log.Timber;

/**
 * Created by Farhan on 27/7/17.
 */

@Module
public class DataRepositoryModule {


    private static final String TAG = "DataRepositoryModule";


    @Provides
    @ApplicationScoped
    @Named("idempotent")
    RetroFitService providesIdempotentRetroFitService() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        String baseUrl = Constants.ApiConstants.BASE_URL;
        if (BuildConfig.DEBUG) {
            loggingInterceptor= new HttpLoggingInterceptor(message ->
                    Timber.tag("DEARO-API").d(message)
            );
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            if (BuildConfig.ENABLE_LOCAL_URL) {
                baseUrl = SharedPrefHelper.getBaseUrl();
                httpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
            }
        }
        httpClient.retryOnConnectionFailure(true);
        httpClient.readTimeout(1, TimeUnit.MINUTES);
        httpClient.connectTimeout(1, TimeUnit.MINUTES);

        httpClient.addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new Interceptor() {
                    @NonNull
                    @Override
                    public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("authorization", SharedPrefHelper.getUserAccessToken())
                                .header("Content-Type", "application/json")
                                .header("AppVersion", BuildConfig.VERSION_CODE + "")
                                .header("DeviceInfo", Utility.getDeviceName())
                                .method(original.method(), original.body())
                                .build();

                        return chain.proceed(request);
                    }
                });
        Timber.d(TAG + " : providesRetroFitService: init with base url " + baseUrl);
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(new GranularErrorsCallAdapterFactory())
                .addConverterFactory(MoshiConverterFactory.create())
                .client(httpClient.build())
                .build()
                .create(RetroFitService.class);

    }


    @Provides
    @ApplicationScoped
    @Named("non_idempotent")
    RetroFitService providesNonIdempotentRetroFitService() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        String baseUrl = Constants.ApiConstants.BASE_URL;
        if (BuildConfig.DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            if (BuildConfig.ENABLE_LOCAL_URL) {
                baseUrl = SharedPrefHelper.getBaseUrl();
                httpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
            }
        }
        httpClient.retryOnConnectionFailure(false);
        httpClient.readTimeout(1, TimeUnit.MINUTES);
        httpClient.connectTimeout(1, TimeUnit.MINUTES);

        httpClient.addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new Interceptor() {
                    @NonNull
                    @Override
                    public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("authorization", SharedPrefHelper.getUserAccessToken())
                                .header("Content-Type", "application/json")
                                .header("AppVersion", BuildConfig.VERSION_CODE + "")
                                .header("DeviceInfo", Utility.getDeviceName())
                                .method(original.method(), original.body())
                                .build();

                        return chain.proceed(request);
                    }
                });
        Timber.d(TAG + " : providesRetroFitService: init with base url " + baseUrl);
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(new GranularErrorsCallAdapterFactory())
                .addConverterFactory(MoshiConverterFactory.create())
                .client(httpClient.build())
                .build()
                .create(RetroFitService.class);

    }


    @Provides
    Moshi providesMoshi() {
        return new Moshi.Builder()
                .build();
    }

    @Provides
    @ApplicationScoped
    @Local
    DataSource providesLocalDataSource(ErrorWrapperHelper errorWrapperHelper) {
        return new LocalDataSource(errorWrapperHelper);
    }


    @ApplicationScoped
    @Provides
    @Remote
    DataSource providesNetworkDataSource(@Named("idempotent") RetroFitService idempotentService, @Named("non_idempotent") RetroFitService nonIdempotentService, ErrorWrapperHelper errorWrapperHelper) {
        return new NetworkDataSource(idempotentService, nonIdempotentService, errorWrapperHelper);
    }


}
