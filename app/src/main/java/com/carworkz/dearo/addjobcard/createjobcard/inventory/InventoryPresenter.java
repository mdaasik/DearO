package com.carworkz.dearo.addjobcard.createjobcard.inventory;

import android.text.TextUtils;

import com.carworkz.dearo.base.ErrorWrapper;
import com.carworkz.dearo.data.DataSource;
import com.carworkz.dearo.data.DearODataRepository;
import com.carworkz.dearo.domain.entities.Inventory;
import com.carworkz.dearo.domain.entities.JobCard;
import com.carworkz.dearo.domain.entities.NetworkPostResponse;
import com.carworkz.dearo.injection.FragmentScoped;
import com.carworkz.dearo.utils.Constants;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by Farhan on 18/8/17.
 */
@FragmentScoped
public class InventoryPresenter implements InventoryContract.Presenter {
    private static final String TAG = "InventoryPresenter";
    private InventoryContract.View view;
    private final DearODataRepository dataRespository;

    @Inject
    public InventoryPresenter(InventoryContract.View view, DearODataRepository dataRespository) {
        this.view = view;
        this.dataRespository = dataRespository;
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void start() {

    }

    @Override
    public void getInventoryList(@Nullable String vehicleType) {
        view.showProgressIndicator();
        dataRespository.getInventory(vehicleType, new DataSource.OnResponseCallback<List<Inventory>>() {
            @Override
            public void onSuccess(List<Inventory> obj) {
                if (view == null)
                    return;
                Timber.d(TAG, "onSuccess:  " + obj.size());
                view.dismissProgressIndicator();
                view.displayInventoryList(obj);
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

    @Override
    public void saveJobCardInventory(@NotNull String jobCardId, @NotNull String serviceType, int fuelReading, int kmsReading, @NotNull List<? extends Inventory> inventory, @Nullable String remarks) {

        dataRespository.saveJobCardInventory(jobCardId, serviceType, fuelReading, kmsReading, inventory, remarks, new DataSource.OnResponseCallback<NetworkPostResponse>() {
            @Override
            public void onSuccess(NetworkPostResponse obj) {
                if (view == null)
                    return;
                view.onInventorySaveSuccess();
            }

            @Override
            public void onError(@NotNull ErrorWrapper error) {
                view.dismissProgressIndicator();
                if (error.getErrorMessages() != null) {
                    for (String key : error.getErrorMessages().keySet()) {
                        List<String> messages = error.getErrorMessages().get(key);
                        String finalErrorMsg = TextUtils.join(",", messages);
                        switch (key) {
                            case Constants.ApiConstants.KEY_JOB_CARD_INVENTORY_SERVICE_TYPE:
                                view.showServiceTypeError(finalErrorMsg);
                                break;
                            case Constants.ApiConstants.KEY_JOB_CARD_INVENTORY_KMS_READING:
                                view.showKmsReadingError(finalErrorMsg);
                                break;
                            case Constants.ApiConstants.KEY_JOB_CARD_INVENTORY_LIST:
                                view.showInventoryListError(finalErrorMsg);
                                break;
                            case Constants.ApiConstants.KEY_JOB_CARD_INVENTORY_REMARKS:
                                view.showRemarksError(finalErrorMsg);
                                break;
                            case Constants.ApiConstants.KEY_JOB_CARD_INVENTORY_FUEL_READING:
                                view.showFuelReadingError(finalErrorMsg);
                                break;
                            default:
                                view.showGenericError(error.getErrorMessage());
                        }
                    }
                } else {
                    view.showGenericError(error.getErrorMessage());
                }
            }
        });

    }

    @Override
    public void getSelectedInventoryList(@NotNull String jobCardId) {
        view.showProgressIndicator();
        dataRespository.getJobCardById(jobCardId, null, new DataSource.OnResponseCallback<JobCard>() {
            @Override
            public void onSuccess(JobCard obj) {
                if (view == null)
                    return;
                view.dismissProgressIndicator();
                if (obj.getInventory() != null) {
                    view.displayInventoryList(obj.getInventory());
                } else {
                    getInventoryList(obj.getVehicleType());
                }
                int fuelReading = 0, kmsReading = 0;
                if (obj.getFuelReading() != null) {
                    fuelReading = obj.getFuelReading();
                }

                if (obj.getKmsReading() != null) {
                    kmsReading = obj.getKmsReading();
                }
                view.displayInventoryItems(obj.getServiceType(), fuelReading, kmsReading, obj.getUserComment());
            }

            @Override
            public void onError(@NotNull ErrorWrapper error) {
                if (view == null)
                    return;
                view.dismissProgressIndicator();
                view.showInventoryListError(error.getErrorMessage());
            }
        });
    }
}
