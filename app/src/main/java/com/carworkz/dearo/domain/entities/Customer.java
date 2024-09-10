package com.carworkz.dearo.domain.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by kush on 17/8/17.
 */

public class Customer implements Parcelable {

    @Json(name = "id")
    private String id;
    @Json(name = "name")
    private String name;
    @Json(name = "mobile")
    private String mobile;
    @Json(name = "isRegisteredDealer")
    private Boolean isRegisteredDealer = false;
    @Json(name = "alternateMobile")
    private String alternateMobile;
    @Json(name = "email")
    private String email;
    @Json(name = "remarks")
    private String remarks;
    @Json(name = "birthDate")
    private String birthDate;
    @Json(name = "anniversary")
    private String anniversary;
    @Json(name = "createdOn")
    private String createdOn;
    @Json(name = "updatedOn")
    private String updatedOn;
    @Json(name = "addresses")
    private List<Address> address;
    @Json(name = "full")
    private List<String> full;
    @Json(name = "gstNumber")
    private String gst;
    @Json(name = "isEditable")
    private Boolean isEditable = false;
    @Json(name = "customerGroupId")
    private String customerGroupId ;

    public static final Creator<Customer> CREATOR = new Creator<Customer>() {
        @Override
        public Customer createFromParcel(Parcel in) {
            return new Customer(in);
        }

        @Override
        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };

    public Customer() {

    }

    protected Customer(Parcel in) {
        id = in.readString();
        name = in.readString();
        mobile = in.readString();
        byte tmpIsRegisteredDealer = in.readByte();
        isRegisteredDealer = tmpIsRegisteredDealer == 0 ? null : tmpIsRegisteredDealer == 1;
        alternateMobile = in.readString();
        email = in.readString();
        remarks = in.readString();
        birthDate = in.readString();
        anniversary = in.readString();
        createdOn = in.readString();
        updatedOn = in.readString();
        address = in.createTypedArrayList(Address.CREATOR);
        full = in.createStringArrayList();
        gst = in.readString();
        byte tmpIsEditable = in.readByte();
        isEditable = tmpIsEditable == 0 ? null : tmpIsEditable == 1;
        customerGroupId = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Boolean getRegisteredDealer() {
        return isRegisteredDealer;
    }

    public void setRegisteredDealer(Boolean registeredDealer) {
        isRegisteredDealer = registeredDealer;
    }

    public String getAlternateMobile() {
        return alternateMobile;
    }

    public void setAlternateMobile(String alternateMobile) {
        this.alternateMobile = alternateMobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getAnniversary() {
        return anniversary;
    }

    public void setAnniversary(String anniversary) {
        this.anniversary = anniversary;
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

    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }

    public List<String> getFull() {
        return full;
    }

    public void setFull(List<String> full) {
        this.full = full;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public Boolean isEditable() {
        return isEditable;
    }

    public void isEditable(Boolean isEditable) {
        this.isEditable = isEditable;
    }

    public String getCustomerGroupId() {
        return customerGroupId;
    }

    public void setCustomerGroupId(String customerGroupId) {
        this.customerGroupId = customerGroupId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(mobile);
        dest.writeByte((byte) (isRegisteredDealer == null ? 0 : isRegisteredDealer ? 1 : 2));
        dest.writeString(alternateMobile);
        dest.writeString(email);
        dest.writeString(remarks);
        dest.writeString(birthDate);
        dest.writeString(anniversary);
        dest.writeString(createdOn);
        dest.writeString(updatedOn);
        dest.writeTypedList(address);
        dest.writeStringList(full);
        dest.writeString(gst);
        dest.writeByte((byte) (isEditable == null ? 0 : isEditable ? 1 : 2));
        dest.writeString(customerGroupId);
    }

}