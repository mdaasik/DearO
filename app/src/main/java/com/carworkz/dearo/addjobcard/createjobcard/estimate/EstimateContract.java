package com.carworkz.dearo.addjobcard.createjobcard.estimate;

import com.carworkz.dearo.base.BasePresenter;
import com.carworkz.dearo.base.BaseView;
import com.carworkz.dearo.domain.entities.JobCard;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Farhan on 19/8/17.
 */

public interface EstimateContract {

    interface View extends BaseView<Presenter>{

        void moveToNextScreen();

        void showDeliveryDateError(String message);

        void showMinCostError(String message);

        void showMaxCostError(String message);

        void displayEstimate(JobCard jobCard);

        void setUpdatedJC(JobCard jobCard);

//        void showAccidentalErrors(ArrayList<String> errorList, @Nullable List<InsuranceCompany> companyList);
//
//        void updateJCAndCallEstimate(JobCard obj);

        void showAccidentalError(String error);

        void launchWhatsapp(String contactNumber, String message);
    }

    interface Presenter extends BasePresenter{

        void getEstimate(@NotNull String jobCardId);

        void getEstimateAndCompleteJC(@NotNull String jobCardId);

        void saveAndCompleteJobCard(@NotNull String jobCardId, @NotNull String dateTime, int minEstimate, int maxEstimate, @NotNull Boolean notify);

        void saveEstimate(@NotNull String jobCardId, @NotNull String dateTime, int minEstimate, int maxEstimate, @Nullable String status, boolean notify);

       // void updateMissingInfo(@NotNull String jobCardId, @NotNull MissingAccidentalDetails missingAccidentalDetails);

        //void getCompanyList(ArrayList<String> errorList);
    }
}
