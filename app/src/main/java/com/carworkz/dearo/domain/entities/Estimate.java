package com.carworkz.dearo.domain.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.carworkz.dearo.utils.Constants;
import com.squareup.moshi.Json;

/**
 * Created by ambab on 22/8/17.
 */
public class Estimate implements Parcelable {

    @Json(name = Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_DELIVERY_DATE_TIME)
    private String deliveryDateTime;
    @Json(name = Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_MIN_COST)
    private int minCost;
    @Json(name = Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_MAX_COST)
    private int maxCost;

    @Json(name = "status")
    private String status;


    private boolean notifyCustomer;

    public Estimate() {
    }

    protected Estimate(Parcel in) {
        deliveryDateTime = in.readString();
        minCost = in.readInt();
        maxCost = in.readInt();
        status = in.readString();
        notifyCustomer = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(deliveryDateTime);
        dest.writeInt(minCost);
        dest.writeInt(maxCost);
        dest.writeString(status);
        dest.writeByte((byte) (notifyCustomer ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Estimate> CREATOR = new Creator<Estimate>() {
        @Override
        public Estimate createFromParcel(Parcel in) {
            return new Estimate(in);
        }

        @Override
        public Estimate[] newArray(int size) {
            return new Estimate[size];
        }
    };

    public String getDeliveryDateTime() {
        return deliveryDateTime;
    }

    public void setDeliveryDateTime(String deliveryDateTime) {
        this.deliveryDateTime = deliveryDateTime;
    }

    public int getMinCost() {
        return minCost;
    }

    public void setMinCost(int minCost) {
        this.minCost = minCost;
    }

    public int getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(int maxCost) {
        this.maxCost = maxCost;
    }

    public Boolean getNotifyCustomer() {
        return notifyCustomer;
    }

    public void setNotifyCustomer(Boolean notifyCustomer) {
        this.notifyCustomer = notifyCustomer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}