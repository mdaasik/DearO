package com.carworkz.dearo.domain.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

import java.util.List;

public class PartNumber implements Parcelable {

    @Json(name = "brandId")
    private String brandId;
    @Json(name = "brandName")
    private String brandName;
    @Json(name = "description")
    private String description;
    @Json(name = "id")
    private String id;
    @Json(name = "partNumber")
    private String partNumber;
    @Json(name = "quantity")
    private Float quantity;
    @Json(name = "sellingPrice")
    private Float sellingPrice;
    @Json(name = "volume")
    private Float volume;
    @Json(name = "purchasePrice")
    private Float purchasePrice;
    @Json(name = "partName")
    private String partName;
    @Json(name = "stock")
    private float stock;
    @Json(name = "category")
    private String category;
    @Json(name = "unitPrice")
    private Float unitPrice;
    @Json(name = "unit")
    private String unit;
    @Json(name = "assemblyGroup")
    private List<String> assemblyGroup;
    @Json(name = "isOEM")
    private Boolean isOEM;
    @Json(name = "type")
    private String type;
    @Json(name = "parts")
    private List<Part> parts;
    @Json(name = "remark")
    private String remark;
    @Json(name = "vehicleType")
    private String vehicleType;
    @Json(name = "tax")
    private Tax tax;

    protected PartNumber(Parcel in) {
        brandId = in.readString();
        brandName = in.readString();
        description = in.readString();
        id = in.readString();
        partNumber = in.readString();
        if (in.readByte() == 0) {
            quantity = null;
        } else {
            quantity = in.readFloat();
        }
        if (in.readByte() == 0) {
            sellingPrice = null;
        } else {
            sellingPrice = in.readFloat();
        }
        if (in.readByte() == 0) {
            volume = null;
        } else {
            volume = in.readFloat();
        }
        if (in.readByte() == 0) {
            purchasePrice = null;
        } else {
            purchasePrice = in.readFloat();
        }
        partName = in.readString();
        stock = in.readFloat();
        category = in.readString();
        if (in.readByte() == 0) {
            unitPrice = null;
        } else {
            unitPrice = in.readFloat();
        }
        unit = in.readString();
        assemblyGroup = in.createStringArrayList();
        byte tmpIsOEM = in.readByte();
        isOEM = tmpIsOEM == 0 ? null : tmpIsOEM == 1;
        type = in.readString();
        parts = in.createTypedArrayList(Part.CREATOR);
        remark = in.readString();
        vehicleType = in.readString();
        tax = in.readParcelable(Tax.class.getClassLoader());
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public Float getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Float sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public Float getVolume() {
        return volume;
    }

    public void setVolume(Float volume) {
        this.volume = volume;
    }

    public Float getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Float purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public float getStock() {
        return stock;
    }

    public void setStock(float stock) {
        this.stock = stock;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Float getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Float unitPrice) {
        this.unitPrice = unitPrice;
    }

    public List<String> getAssemblyGroup() {
        return assemblyGroup;
    }

    public void setAssemblyGroup(List<String> assemblyGroup) {
        this.assemblyGroup = assemblyGroup;
    }

    public Boolean getOEM() {
        return isOEM;
    }

    public void setOEM(Boolean OEM) {
        isOEM = OEM;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Tax getTax() {
        return tax;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public static final Creator<PartNumber> CREATOR = new Creator<PartNumber>() {
        @Override
        public PartNumber createFromParcel(Parcel in) {
            return new PartNumber(in);
        }

        @Override
        public PartNumber[] newArray(int size) {
            return new PartNumber[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(brandId);
        dest.writeString(brandName);
        dest.writeString(description);
        dest.writeString(id);
        dest.writeString(partNumber);
        if (quantity == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(quantity);
        }
        if (sellingPrice == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(sellingPrice);
        }
        if (volume == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(volume);
        }
        if (purchasePrice == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(purchasePrice);
        }
        dest.writeString(partName);
        dest.writeFloat(stock);
        dest.writeString(category);
        if (unitPrice == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(unitPrice);
        }
        dest.writeString(unit);
        dest.writeStringList(assemblyGroup);
        dest.writeByte((byte) (isOEM == null ? 0 : isOEM ? 1 : 2));
        dest.writeString(type);
        dest.writeTypedList(parts);
        dest.writeString(remark);
        dest.writeString(vehicleType);
        dest.writeParcelable(tax,0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "PartNumber{" +
                "brandId='" + brandId + '\'' +
                ", brandName='" + brandName + '\'' +
                ", description='" + description + '\'' +
                ", id='" + id + '\'' +
                ", partNumber='" + partNumber + '\'' +
                ", quantity=" + quantity +
                ", sellingPrice=" + sellingPrice +
                ", volume=" + volume +
                ", purchasePrice=" + purchasePrice +
                ", partName='" + partName + '\'' +
                ", stock=" + stock +
                ", category='" + category + '\'' +
                ", unitPrice=" + unitPrice +
                ", unit='" + unit + '\'' +
                ", assemblyGroup=" + assemblyGroup +
                ", isOEM=" + isOEM +
                ", type='" + type + '\'' +
                ", parts=" + parts +
                ", remark='" + remark + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", tax=" + tax +
                '}';
    }
}
