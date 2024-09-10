
package com.carworkz.dearo.domain.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

import java.util.List;

public class RegularService implements Parcelable {

    @Json(name = "pms")
    private int pms;
    @Json(name = "id")
    private String id;
    @Json(name = "variantCode")
    private String variantCode;
    @Json(name = "kms")
    private Integer kms;
    @Json(name = "months")
    private Integer months;
    @Json(name = "createdOn")
    private String createdOn;
    @Json(name = "updatedOn")
    private String updatedOn;
    @Json(name = "isDefault")
    private Boolean isDefault;
    @Json(name = "labours")
    private List<Labour> labours = null;

    @Json(name = "parts")
    private List<Part> parts = null;

    public RegularService() {
    }

    private RegularService(Parcel in) {
        pms = in.readInt();
        id = in.readString();
        variantCode = in.readString();
        if (in.readByte() == 0) {
            kms = null;
        } else {
            kms = in.readInt();
        }
        if (in.readByte() == 0) {
            months = null;
        } else {
            months = in.readInt();
        }
        createdOn = in.readString();
        updatedOn = in.readString();
        byte tmpIsDefault = in.readByte();
        isDefault = tmpIsDefault == 0 ? null : tmpIsDefault == 1;
        labours = in.createTypedArrayList(Labour.CREATOR);
        parts = in.createTypedArrayList(Part.CREATOR);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVariantCode() {
        return variantCode;
    }

    public void setVariantCode(String variantCode) {
        this.variantCode = variantCode;
    }

    public Integer getKms() {
        return kms;
    }

    public void setKms(Integer kms) {
        this.kms = kms;
    }

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
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

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public List<Labour> getLabours() {
        return labours;
    }

    public void setLabours(List<Labour> labours) {
        this.labours = labours;
    }

    public int getPms() {
        return pms;
    }

    public void setPms(int pms) {
        this.pms = pms;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(pms);
        dest.writeString(id);
        dest.writeString(variantCode);
        if (kms == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(kms);
        }
        if (months == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(months);
        }
        dest.writeString(createdOn);
        dest.writeString(updatedOn);
        dest.writeByte((byte) (isDefault == null ? 0 : isDefault ? 1 : 2));
        dest.writeTypedList(labours);
        dest.writeTypedList(parts);
    }

    public static final Creator<RegularService> CREATOR = new Creator<RegularService>() {
        @Override
        public RegularService createFromParcel(Parcel in) {
            return new RegularService(in);
        }

        @Override
        public RegularService[] newArray(int size) {
            return new RegularService[size];
        }
    };

    @Override
    public String toString() {
        return "RegularService{" +
                "pms=" + pms +
                ", id='" + id + '\'' +
                ", variantCode='" + variantCode + '\'' +
                ", kms=" + kms +
                ", months=" + months +
                ", createdOn='" + createdOn + '\'' +
                ", updatedOn='" + updatedOn + '\'' +
                ", isDefault=" + isDefault +
                ", labours=" + labours +
                ", parts=" + parts +
                '}';
    }
}
