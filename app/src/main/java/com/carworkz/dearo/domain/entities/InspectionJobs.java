
package com.carworkz.dearo.domain.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InspectionJobs implements Parcelable {


    @Nullable
    @Json(name = "poor")
    private List<String> poor;

    @Nullable
    @Json(name = "average")
    private List<String> average;

    @Nullable
    @Json(name = "good")
    private List<String> good;

    public static final Creator<InspectionJobs> CREATOR = new Creator<InspectionJobs>() {
        @Override
        public InspectionJobs createFromParcel(Parcel in) {
            return new InspectionJobs(in);
        }

        @Override
        public InspectionJobs[] newArray(int size) {
            return new InspectionJobs[size];
        }
    };

    private InspectionJobs(Parcel in) {
        poor = in.createStringArrayList();
        average = in.createStringArrayList();
        good = in.createStringArrayList();
    }

    public List<String> getAverage() {
        return average;
    }

    public void setAverage(List<String> average) {
        this.average = average;
    }

    public List<String> getPoor() {
        return poor;
    }

    public void setPoor(List<String> poor) {
        this.poor = poor;
    }

    public List<String> getGood() {
        return good;
    }

    public void setGood(List<String> good) {
        this.good = good;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(poor);
        dest.writeStringList(average);
        dest.writeStringList(good);
    }
}
