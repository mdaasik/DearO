package com.carworkz.dearo.domain.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

import java.util.List;

import javax.annotation.Nullable;

public class Verbatim implements Parcelable {

    @Json(name = "id")
    private String id;
    @Json(name = "jobCardId")
    private String jobCardId;
    @Json(name = "workshopId")
    private String workshopId;
    @Json(name = "customerId")
    private String customerId;
    @Json(name = "vehicleId")
    private String vehicleId;
    @Json(name = "where")
    private String status;
    @Json(name = "verbatim")
    private List<String> verbatim = null;
    @Json(name = "technicianName")
    private String technicianName;
    @Json(name = "serviceAdviserId")
    private String serviceAdviserId;
    @Json(name = "bayId")
    private String bayId;
    @Nullable
    @Json(name = "source")
    private String source;
    @Nullable
    @Json(name = "sourceType")
    private String sourceType;
    @Json(name = "createdOn")
    private String createdOn;
    @Json(name = "updatedOn")
    private String updatedOn;


    public static final Creator<Verbatim> CREATOR = new Creator<Verbatim>() {
        @Override
        public Verbatim createFromParcel(Parcel in) {
            return new Verbatim(in);
        }

        @Override
        public Verbatim[] newArray(int size) {
            return new Verbatim[size];
        }
    };

    public Verbatim() {
    }

    protected Verbatim(Parcel in) {
        id = in.readString();
        jobCardId = in.readString();
        workshopId = in.readString();
        customerId = in.readString();
        vehicleId = in.readString();
        status = in.readString();
        verbatim = in.createStringArrayList();
        technicianName = in.readString();
        serviceAdviserId = in.readString();
        bayId = in.readString();
        source = in.readString();
        sourceType = in.readString();
        createdOn = in.readString();
        updatedOn = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJobCardId() {
        return jobCardId;
    }

    public void setJobCardId(String jobCardId) {
        this.jobCardId = jobCardId;
    }

    public String getWorkshopId() {
        return workshopId;
    }

    public void setWorkshopId(String workshopId) {
        this.workshopId = workshopId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getVerbatim() {
        return verbatim;
    }

    public void setVerbatim(List<String> verbatim) {
        this.verbatim = verbatim;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
    }

    public String getServiceAdviserId() {
        return serviceAdviserId;
    }

    public void setServiceAdviserId(String serviceAdviserId) {
        this.serviceAdviserId = serviceAdviserId;
    }

    public String getBayId() {
        return bayId;
    }

    public void setBayId(String bayId) {
        this.bayId = bayId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Nullable
    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(@Nullable String sourceType) {
        this.sourceType = sourceType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(jobCardId);
        dest.writeString(workshopId);
        dest.writeString(customerId);
        dest.writeString(vehicleId);
        dest.writeString(status);
        dest.writeStringList(verbatim);
        dest.writeString(technicianName);
        dest.writeString(serviceAdviserId);
        dest.writeString(bayId);
        dest.writeString(source);
        dest.writeString(sourceType);
        dest.writeString(createdOn);
        dest.writeString(updatedOn);
    }
}