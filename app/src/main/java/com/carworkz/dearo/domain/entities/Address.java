
package com.carworkz.dearo.domain.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

import javax.annotation.Nullable;

public class Address implements Parcelable {

    @Nullable
    @Json(name = "id")
    private String id;
    @Nullable
    @Json(name = "street")
    private String street;
    @Nullable
    @Json(name = "location")
    private String location;
    @Json(name = "city")
    private String city;
    @Nullable
    @Json(name = "pincode")
    private Integer pincode;
    @Nullable
    @Json(name = "ownerId")
    private String ownerId;
    @Nullable
    @Json(name = "ownerType")
    private String ownerType = "Customer";//Always Customer
    @Json(name = "state")
    private String state;

    public Address() {
    }


    Address(Parcel in) {
        id = in.readString();
        street = in.readString();
        location = in.readString();
        city = in.readString();
        state = in.readString();
        if (in.readByte() == 0) {
            pincode = null;
        } else {
            pincode = in.readInt();
        }
        ownerId = in.readString();
        ownerType = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(street);
        dest.writeString(location);
        dest.writeString(city);
        dest.writeString(state);
        if (pincode == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(pincode);
        }
        dest.writeString(ownerId);
        dest.writeString(ownerType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    @Nullable
    public String getId() {
        return id;
    }

    public void setId(@Nullable String id) {
        this.id = id;
    }

    @Nullable
    public String getStreet() {
        return street;
    }

    public void setStreet(@Nullable String street) {
        this.street = street;
    }

    @Nullable
    public String getLocation() {
        return location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setLocation(@Nullable String location) {
        this.location = location;
    }

    @Nullable
    public Integer getPincode() {
        return pincode;
    }

    public void setPincode(@Nullable Integer pincode) {
        this.pincode = pincode;
    }

    @Nullable
    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(@Nullable String ownerId) {
        this.ownerId = ownerId;
    }

    @Nullable
    public String getOwnerType() {
        return ownerType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setOwnerType(@Nullable String ownerType) {
        this.ownerType = ownerType;
    }

}
