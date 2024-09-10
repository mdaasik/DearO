package com.carworkz.dearo.addjobcard.createjobcard.inspection.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Farhan on 23/8/17.
 */

@SuppressWarnings("CanBeFinal")
public class InspectionSubItemPOJO implements Parcelable {
    public static final Creator<InspectionSubItemPOJO> CREATOR = new Creator<InspectionSubItemPOJO>() {
        @Override
        public InspectionSubItemPOJO createFromParcel(Parcel in) {
            return new InspectionSubItemPOJO(in);
        }

        @Override
        public InspectionSubItemPOJO[] newArray(int size) {
            return new InspectionSubItemPOJO[size];
        }
    };
    private String id;
    private String text;
    private String condition;

    protected InspectionSubItemPOJO(Parcel in) {
        this.id = in.readString();
        this.text = in.readString();
        this.condition = in.readString();
    }

    public InspectionSubItemPOJO(String id, String text, String condition) {
        this.id = id;
        this.text = text;
        this.condition = condition;
    }
    public InspectionSubItemPOJO(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.condition);
        parcel.writeString(this.text);
    }

    @Override
    public String toString() {
        return "InspectionSubItemPOJO{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", condition='" + condition + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InspectionSubItemPOJO that = (InspectionSubItemPOJO) o;

        return id.equals(that.id) && text.equals(that.text);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + text.hashCode();
        return result;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
