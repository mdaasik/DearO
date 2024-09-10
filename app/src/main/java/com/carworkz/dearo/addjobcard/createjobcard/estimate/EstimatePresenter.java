package com.carworkz.dearo.addjobcard.createjobcard.estimate;

import android.text.TextUtils;

import com.carworkz.dearo.base.ErrorWrapper;
import com.carworkz.dearo.data.DataSource;
import com.carworkz.dearo.data.DearODataRepository;
import com.carworkz.dearo.data.local.SharedPrefHelper;
import com.carworkz.dearo.domain.entities.JobCard;
import com.carworkz.dearo.domain.entities.NetworkPostResponse;
import com.carworkz.dearo.domain.entities.WhatsAppTemplate;
import com.carworkz.dearo.injection.FragmentScoped;
import com.carworkz.dearo.utils.Constants;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

/**
 * Created by Farhan on 19/8/17.
 */
@FragmentScoped
public class EstimatePresenter implements EstimateContract.Presenter {

    private EstimateContract.View view;
    private final DearODataRepository dearODataRepository;


    @Inject
    EstimatePresenter(EstimateContract.View view, DearODataRepository dearODataRepository) {
        this.view = view;
        this.dearODataRepository = dearODataRepository;
    }

    @Override
    public void start() {

    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void getEstimate(@NotNull String jobCardId) {
        if (view != null) {
            view.showProgressIndicator();
            dearODataRepository.getJobCardById(jobCardId, null, new DataSource.OnResponseCallback<JobCard>() {
                @Override
                public void onSuccess(JobCard obj) {
                    if (view != null) {
                        view.dismissProgressIndicator();
                        view.displayEstimate(obj);
                    }
                }

                @Override
                public void onError(@NotNull ErrorWrapper error) {
                    if (view != null) {
                        view.dismissProgressIndicator();
                        view.showGenericError(error.getErrorMessage());
                    }
                }
            });
        }

    }
    @Override
    public void getEstimateAndCompleteJC(@NotNull String jobCardId)
    {
        if (view != null) {
            view.showProgressIndicator();
            dearODataRepository.getJobCardById(jobCardId, null, new DataSource.OnResponseCallback<JobCard>() {
                @Override
                public void onSuccess(JobCard obj) {
                    if (view != null) {
                        view.dismissProgressIndicator();
                        view.setUpdatedJC(obj);
                    }
                }

                @Override
                public void onError(@NotNull ErrorWrapper error) {
                    if (view != null) {
                        view.dismissProgressIndicator();
                        view.showGenericError(error.getErrorMessage());
                    }
                }
            });
        }
    }


    @Override
    public void saveAndCompleteJobCard(@NotNull final String jobCardId, @NotNull String deliveryDateTime, int minEstimate, int maxEstimate, @NotNull final Boolean notify) {
        if (view == null)
            return;

        view.showProgressIndicator();
        dearODataRepository.saveJobCardEstimate(jobCardId, deliveryDateTime, minEstimate, maxEstimate, null, notify,null, new DataSource.OnResponseCallback<NetworkPostResponse>() {

            @Override
            public void onError(@NotNull ErrorWrapper error) {
                if (view == null)
                    return;
                view.dismissProgressIndicator();
                view.showGenericError(error.getErrorMessage());
            }

            @Override
            public void onSuccess(NetworkPostResponse obj)
            {
                boolean shouldNotify;
                if (SharedPrefHelper.isNotifyWhatsappEnabled()) {
                    shouldNotify = false;
                } else {
                    shouldNotify = notify;
                }
                dearODataRepository.completeJobCard(jobCardId, shouldNotify,null, new DataSource.OnResponseCallback<NetworkPostResponse>() {
                    @Override
                    public void onSuccess(NetworkPostResponse obj) {
                        if (view == null)
                            return;
                        view.dismissProgressIndicator();

                        if (notify && SharedPrefHelper.isNotifyWhatsappEnabled()) {
                            view.showProgressIndicator();
                            dearODataRepository.getWhatsAppTemplate(Constants.ApiConstants.KEY_WHATSAPP_JOB_CARD_COMPLETED, jobCardId, new DataSource.OnResponseCallback<WhatsAppTemplate>() {
                                @Override
                                public void onSuccess(WhatsAppTemplate obj) {
                                    view.dismissProgressIndicator();
                                    view.moveToNextScreen();
                                    view.launchWhatsapp(obj.mobile, obj.text);
                                }

                                @Override
                                public void onError(@NotNull ErrorWrapper error) {
                                    view.dismissProgressIndicator();
                                    view.moveToNextScreen();
                                }
                            });
                        }else{
                            view.moveToNextScreen();
                        }
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
        });
    }

    @Override
    public void saveEstimate(@NotNull final String jobCardId, @NotNull String dateTime, int minEstimate, int maxEstimate, final String status, final boolean notify) {
        if (view == null)
            return;
        boolean shouldNotify;
        if (SharedPrefHelper.isNotifyWhatsappEnabled()) {
            shouldNotify = false;
        } else {
            shouldNotify = notify;
        }

        view.showProgressIndicator();
        dearODataRepository.saveJobCardEstimate(jobCardId, dateTime, minEstimate, maxEstimate, status, shouldNotify,null, new DataSource.OnResponseCallback<NetworkPostResponse>() {
            @Override
            public void onSuccess(NetworkPostResponse obj) {
                if (view == null)
                    return;

                //status not null means jobcard is being created.
                if (status != null){
                    if(notify && SharedPrefHelper.isNotifyWhatsappEnabled()){
                        dearODataRepository.getWhatsAppTemplate(Constants.ApiConstants.KEY_WHATSAPP_JOB_CARD_CREATE_JC, jobCardId, new DataSource.OnResponseCallback<WhatsAppTemplate>() {
                            @Override
                            public void onSuccess(WhatsAppTemplate obj) {
                                if(view == null)
                                    return;

                                view.dismissProgressIndicator();
                                view.launchWhatsapp(obj.mobile,obj.text);
                                view.moveToNextScreen();
                            }

                            @Override
                            public void onError(@NotNull ErrorWrapper error) {
                                if (view == null)
                                    return;

                                view.dismissProgressIndicator();
                                view.moveToNextScreen();

                            }
                        });
                    }else{
                        view.moveToNextScreen();
                    }
                }


                view.dismissProgressIndicator();
            }

            @Override
            public void onError(@NotNull ErrorWrapper error) {
                if (view == null)
                    return;
//                Timber.d("error estimate"+error.getErrorMessages().isEmpty());
                view.dismissProgressIndicator();
                if (error.getErrorMessages() != null) {
                    for (String key : error.getErrorMessages().keySet()) {
                        String finalErrorMessage = TextUtils.join(",", error.getErrorMessages().get(key));
                        switch (key) {
                            case Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_MIN_COST:
                                view.showMinCostError(finalErrorMessage);
                                break;
                            case Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_MAX_COST:
                                view.showMaxCostError(finalErrorMessage);
                                break;
                            case Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_DELIVERY_DATE_TIME:
                                view.showDeliveryDateError(finalErrorMessage);
                                break;
                        }
                    }

                } else {
                    view.showGenericError(error.getErrorMessage());
                }
            }
        });
    }

//    @Override
//    public void updateMissingInfo(@NotNull String jobCardId, @NotNull MissingAccidentalDetails missingAccidentalDetails) {
//        if (view != null) view.showProgressIndicator();
//        dearODataRepository.saveMissingAccidentalDetails(jobCardId, missingAccidentalDetails, new DataSource.OnResponseCallback<JobCard>() {
//            @Override
//            public void onSuccess(JobCard obj) {
//                if (view == null)
//                    return;
//
//                view.dismissProgressIndicator();
//                view.updateJCAndCallEstimate(obj);
//            }
//
//            @Override
//            public void onError(@NotNull ErrorWrapper error) {
//                if (view == null)
//                    return;
//
//                view.dismissProgressIndicator();
//                if (error.getErrorMessages() != null && error.getErrorMessages().size() > 0) {
//                    StringBuilder errorString = new StringBuilder();
//                    for (Map.Entry<String, List<String>> entry : error.getErrorMessages().entrySet()) {
//                        switch (entry.getKey()) {
//                            case Constants.ApiConstants.KEY_INSURANCE_ERROR: {
//                                for (String value : entry.getValue()) {
//                                    if (value.contains(Constants.ApiConstants.KEY_CLAIM_ERROR)) {
//                                        errorString.append("\u2022 ").append(value).append(" \n");
//                                    }
//                                    if (value.contains(Constants.ApiConstants.KEY_INSURANCE_POLICY)) {
//                                        errorString.append("\u2022 ").append(value).append(" \n");
//                                    }
//                                    if (value.contains(Constants.ApiConstants.KEY_INSURANCE_EXPIRY)) {
//                                        errorString.append("\u2022 ").append(value).append(" \n");
//                                    }
//                                }
//                            }
//                            case Constants.ApiConstants.KEY_COMPANY: {
//                                for (String value : entry.getValue()) {
//                                    if (value.contains(Constants.ApiConstants.KEY_INSURANCE_COMPANY)) {
//                                        errorString.append("\u2022 ").append(value).append(" \n");
//                                    }
//
//                                    if (value.contains(Constants.ApiConstants.KEY_PINCODE_ERROR)) {
//                                        errorString.append("\u2022 ").append(value).append(" \n");
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    view.showAccidentalError(errorString.toString());
//                } else {
//                    view.showAccidentalError("Something Went Wrong");
//                }
//            }
//        });
//    }
//
//    @Override
//    public void getCompanyList(final ArrayList<String> errorList) {
//        if (view == null)
//            return;
//        view.showProgressIndicator();
//        dearODataRepository.getCompanyNames(new DataSource.OnResponseCallback<List<InsuranceCompany>>() {
//            @Override
//            public void onSuccess(List<InsuranceCompany> obj) {
//                if (view == null)
//                    return;
//
//                view.dismissProgressIndicator();
//                if (!errorList.isEmpty()) {
//                    view.showAccidentalErrors(errorList, obj);
//                }
//            }
//
//            @Override
//            public void onError(@NotNull ErrorWrapper error) {
//                if (view == null)
//                    return;
//
//                view.dismissProgressIndicator();
//                view.showGenericError(error.getErrorMessage());
//
//            }
//        });
//    }
}
