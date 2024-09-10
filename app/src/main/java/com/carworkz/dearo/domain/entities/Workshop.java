
package com.carworkz.dearo.domain.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

public class Workshop implements Parcelable {

    @Json(name = "id")
    private String id;
    @Json(name = "displayName")
    private String displayName;
    @Json(name = "registeredName")
    private String registeredName;
    @Json(name = "address")
    private Address address;
    @Json(name = "mobile")
    private String mobile;
    //    @Json(name = "logo")
//    private String logo;
    @Json(name = "signature")
    private String signature;
    @Json(name = "gstNumber")
    private String gstNumber;
    @Json(name = "panNumber")
    private String panNumber;
    @Json(name = "remarks")
    private String remarks;
    @Json(name = "where")
    private String status;
    @Json(name = "logo")
    private Logo logo;


    protected Workshop(Parcel in) {
        id = in.readString();
        displayName = in.readString();
        registeredName = in.readString();
        address = in.readParcelable(Address.class.getClassLoader());
        mobile = in.readString();
        signature = in.readString();
        gstNumber = in.readString();
        panNumber = in.readString();
        remarks = in.readString();
        status = in.readString();
        logo = in.readParcelable(Logo.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(displayName);
        dest.writeString(registeredName);
        dest.writeParcelable(address, flags);
        dest.writeString(mobile);
        dest.writeString(signature);
        dest.writeString(gstNumber);
        dest.writeString(panNumber);
        dest.writeString(remarks);
        dest.writeString(status);
        dest.writeParcelable(logo, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Workshop> CREATOR = new Creator<Workshop>() {
        @Override
        public Workshop createFromParcel(Parcel in) {
            return new Workshop(in);
        }

        @Override
        public Workshop[] newArray(int size) {
            return new Workshop[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRegisteredName() {
        return registeredName;
    }

    public void setRegisteredName(String registeredName) {
        this.registeredName = registeredName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Logo getLogo() {
        return logo;
    }

    public void setLogo(Logo logo) {
        this.logo = logo;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}
