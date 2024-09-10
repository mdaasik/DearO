package com.carworkz.dearo.domain.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Farhan on 24/8/17.
 */

public class Jobs implements Parcelable {

    @Json(name = "regularService")
    private RegularService regularService;
    @Json(name = "demanded")
    private List<RecommendedJob> demandedJobs = new ArrayList<>(0);
    @Json(name = "recommended")
    private List<RecommendedJob> recommendedJobs = new ArrayList<>(0);
    @Json(name = "additional")
    private List<RecommendedJob> additionalJobs = new ArrayList<>(0);
    @Json(name = "unapproved")
    private List<RecommendedJob> unapprovedJobs = new ArrayList<>(0);
    private List<String> packageIds = new ArrayList<>(0);

    @Json(name = "isCalculator")
    private boolean isCalculator;

    public RegularService getRegularService() {
        return regularService;
    }

    public void setRegularService(RegularService regularService) {
        this.regularService = regularService;
    }

    public List<RecommendedJob> getDemandedJobs() {
        return demandedJobs;
    }

    public void setDemandedJobs(List<RecommendedJob> demandedJobs) {
        this.demandedJobs = demandedJobs;
    }

    public List<RecommendedJob> getRecommendedJobs() {
        return recommendedJobs;
    }

    public void setRecommendedJobs(List<RecommendedJob> recommendedJobs) {
        this.recommendedJobs = recommendedJobs;
    }

    public List<RecommendedJob> getAdditionalJobs() {
        return additionalJobs;
    }

    public void setAdditionalJobs(List<RecommendedJob> additionalJobs) {
        this.additionalJobs = additionalJobs;
    }

    public List<RecommendedJob> getUnapprovedJobs() {
        return unapprovedJobs;
    }

    public void setUnapprovedJobs(List<RecommendedJob> unapprovedJobs) {
        this.unapprovedJobs = unapprovedJobs;
    }

    public boolean isCalculator() {
        return isCalculator;
    }

    public void setCalculator(boolean calculator) {
        isCalculator = calculator;
    }

    public static final Parcelable.Creator<Jobs> CREATOR = new Parcelable.Creator<Jobs>() {
        @Override
        public Jobs createFromParcel(Parcel source) {
            return new Jobs(source);
        }

        @Override
        public Jobs[] newArray(int size) {
            return new Jobs[size];
        }
    };

    public Jobs() {
    }

    protected Jobs(Parcel in) {
        this.regularService = in.readParcelable(RegularService.class.getClassLoader());
        this.demandedJobs = in.createTypedArrayList(RecommendedJob.CREATOR);
        this.recommendedJobs = in.createTypedArrayList(RecommendedJob.CREATOR);
        this.additionalJobs = in.createTypedArrayList(RecommendedJob.CREATOR);
        this.unapprovedJobs = in.createTypedArrayList(RecommendedJob.CREATOR);
        this.packageIds = in.createStringArrayList();
        this.isCalculator = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.regularService, flags);
        dest.writeTypedList(this.demandedJobs);
        dest.writeTypedList(this.recommendedJobs);
        dest.writeTypedList(this.additionalJobs);
        dest.writeTypedList(this.unapprovedJobs);
        dest.writeStringList(this.packageIds);
        dest.writeByte(this.isCalculator ? (byte) 1 : (byte) 0);
    }

    @Override
    public String toString() {
        return "Jobs{" +
                "regularService=" + regularService +
                ", demandedJobs=" + demandedJobs +
                ", recommendedJobs=" + recommendedJobs +
                ", additionalJobs=" + additionalJobs +
                ", unapprovedJobs=" + unapprovedJobs +
                ", packageIds=" + packageIds +
                ", isCalculator=" + isCalculator +
                '}';
    }
}
