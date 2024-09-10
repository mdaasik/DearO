
package com.carworkz.dearo.domain.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

import java.util.List;

public class InspectionItem implements Parcelable {

    @Json(name = "id")
    private String id;
    @Json(name = "text")
    private String text;
    @Json(name = "parent")
    private String parent;
    @Json(name = "position")
    private Integer position;
    @Json(name = "condition")
    private String condition;
    public static final Creator<InspectionItem> CREATOR = new Creator<InspectionItem>() {
        @Override
        public InspectionItem createFromParcel(Parcel in) {
            return new InspectionItem(in);
        }

        @Override
        public InspectionItem[] newArray(int size) {
            return new InspectionItem[size];
        }
    };
    @Json(name = "jobs")
    private InspectionJobs inspectionJobs;
    @Json(name = "suggestedJobs")
    private List<String> suggestedJobs;

    public InspectionItem() {
    }

    protected InspectionItem(Parcel in) {
        id = in.readString();
        text = in.readString();
        parent = in.readString();
        if (in.readByte() == 0) {
            position = null;
        } else {
            position = in.readInt();
        }
        condition = in.readString();
        suggestedJobs = in.createStringArrayList();
        inspectionJobs = in.readParcelable(InspectionJobs.class.getClassLoader());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public InspectionJobs getInspectionJobs() {
        return inspectionJobs;
    }

    public void setInspectionJobs(InspectionJobs inspectionJobs) {
        this.inspectionJobs = inspectionJobs;
    }


    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public List<String> getSuggestedJobs() {
        return suggestedJobs;
    }

    public void setSuggestedJobs(List<String> suggestedJobs) {
        this.suggestedJobs = suggestedJobs;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(text);
        dest.writeString(parent);
        if (position == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(position);
        }
        dest.writeString(condition);
        dest.writeStringList(suggestedJobs);
        dest.writeParcelable(inspectionJobs, flags);
    }
}
