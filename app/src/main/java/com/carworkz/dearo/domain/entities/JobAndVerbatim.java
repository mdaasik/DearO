
package com.carworkz.dearo.domain.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class JobAndVerbatim implements Parcelable {

    @Nullable
    @Json(name = "verbatim")
    private List<Object> verbatim = null;
    @Nullable
    @Json(name = "recommended")
    private List<RecommendedJob> recommendedJobs;
    @Nullable
    @Json(name = "demanded")
    private List<RecommendedJob> demandedJobs;
    @Nullable
    @Json(name = "additional")
    private List<RecommendedJob> additionalJobs;

    @Json(name = "regularService")
    private List<RegularService> regularServices;

    @Json(name = "packages")
    private List<ServicePackage> packagesList;

    @Json(name = "pms")
    private int pms;

    @Json(name = "remarks")
    private List<Remark> remarks;

    public static final Creator<JobAndVerbatim> CREATOR = new Creator<JobAndVerbatim>() {
        @Override
        public JobAndVerbatim createFromParcel(Parcel in) {
            return new JobAndVerbatim(in);
        }

        @Override
        public JobAndVerbatim[] newArray(int size) {
            return new JobAndVerbatim[size];
        }
    };

    protected JobAndVerbatim(Parcel in) {
        recommendedJobs = in.createTypedArrayList(RecommendedJob.CREATOR);
        demandedJobs = in.createTypedArrayList(RecommendedJob.CREATOR);
        additionalJobs = in.createTypedArrayList(RecommendedJob.CREATOR);
        regularServices = in.createTypedArrayList(RegularService.CREATOR);
        packagesList = in.createTypedArrayList(ServicePackage.CREATOR);
        pms = in.readInt();
        remarks = in.createTypedArrayList(Remark.CREATOR);
    }

    public List<Object> getVerbatim() {
        return verbatim;
    }

    public void setVerbatim(List<Object> verbatim) {
        this.verbatim = verbatim;
    }

    public List<RecommendedJob> getRecommendedJobs() {
        return recommendedJobs;
    }

    public void setRecommendedJobs(List<RecommendedJob> recommendedJobs) {
        this.recommendedJobs = recommendedJobs;
    }

    public List<RegularService> getRegularServices() {
        return regularServices;
    }

    public void setRegularServices(List<RegularService> regularServices) {
        this.regularServices = regularServices;
    }


    public List<ServicePackage> getPackagesList() {
        return packagesList;
    }

    public void setPackagesList(List<ServicePackage> packagesList) {
        this.packagesList = packagesList;
    }

    @Nullable
    public List<RecommendedJob> getDemandedJobs() {
        return demandedJobs;
    }

    public void setDemandedJobs(@Nullable List<RecommendedJob> demandedJobs) {
        this.demandedJobs = demandedJobs;
    }

    public int getPms() {
        return pms;
    }

    public void setPms(int pms) {
        this.pms = pms;
    }

    @Nullable
    public List<RecommendedJob> getAdditionalJobs() {
        return additionalJobs;
    }

    public void setAdditionalJobs(@Nullable List<RecommendedJob> additionalJobs) {
        this.additionalJobs = additionalJobs;
    }

    public List<Remark> getRemarks() {
        return remarks;
    }

    public void setRemarks(List<Remark> remarks) {
        this.remarks = remarks;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(recommendedJobs);
        dest.writeTypedList(demandedJobs);
        dest.writeTypedList(additionalJobs);
        dest.writeTypedList(regularServices);
        dest.writeTypedList(packagesList);
        dest.writeInt(pms);
        dest.writeTypedList(remarks);
    }
}
