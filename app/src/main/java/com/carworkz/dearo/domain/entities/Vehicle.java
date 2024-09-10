
package com.carworkz.dearo.domain.entities;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.squareup.moshi.Json;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by kush on 19/8/17.
 */

public class Vehicle implements Parcelable {
    public Vehicle() {

    }

    @Nullable
    @Json(name = "id")
    private String id;
    @Nullable
    @Json(name = "vehicleId")
    private String vehicleId;
    @Json(name = "makeSlug")
    private String makeSlug;
    @Json(name = "modelSlug")
    private String modelSlug;
    @Json(name = "fuelType")
    private String fuelType;
    @Json(name = "variantCode")
    private String variantCode;
    @Json(name = "variant")
    private Variant variant;

    @Nonnull
    @Json(name = "registrationNumber")
    private String registrationNumber;

    @Json(name = "insurance")
    private Insurance insurance = new Insurance();
    @Json(name = "amc")
    private AMCDetails amcDetails = new AMCDetails();
    @Json(name = "vinNumber")
    private String vinNumber;
    @Json(name = "engineNumber")
    private String engineNumber;
    @Json(name = "registrationDate")
    private String registrationDate;
    @Json(name = "color")
    private String color;
    @Json(name = "batteryNumber")
    private String batteryNumber;
    @Json(name = "transmissionType")
    private String transmissionType;
    @Json(name = "remarks")
    private String remarks;
    @Nullable
    @Json(name = "customerId")
    private String customerId;
    @Json(name = "createdOn")
    private String createdOn;
    @Json(name = "updatedOn")
    private String updatedOn;
    @Json(name = "make")
    private Make make;
    @Json(name = "model")
    private Model model;
    @Json(name = "engineOilType")
    private String engineOilType;
    @Json(name = "makeName")
    private String makeName="";
    @Json(name = "modelName")
    private String modelName="";
    @Json(name = "variantName")
    private String variantName="";
    @Json(name = "description")
    private String description;
    @Json(name = "serviceDate")
    private String serviceDate;
    @Json(name = "vehicleType")
    private String vehicleType;
    @Json(name = "year")
    private String year;


    protected Vehicle(Parcel in) {
        id = in.readString();
        vehicleId = in.readString();
        makeSlug = in.readString();
        modelSlug = in.readString();
        fuelType = in.readString();
        variantCode = in.readString();
        registrationNumber = in.readString();
        insurance = in.readParcelable(Insurance.class.getClassLoader());
        amcDetails = in.readParcelable(AMCDetails.class.getClassLoader());
        vinNumber = in.readString();
        engineNumber = in.readString();
        registrationDate = in.readString();
        color = in.readString();
        batteryNumber = in.readString();
        transmissionType = in.readString();
        remarks = in.readString();
        customerId = in.readString();
        createdOn = in.readString();
        updatedOn = in.readString();
        make = in.readParcelable(Make.class.getClassLoader());
        model = in.readParcelable(Model.class.getClassLoader());
        variant = in.readParcelable(Variant.class.getClassLoader());
        engineOilType = in.readString();
        makeName = in.readString();
        modelName = in.readString();
        variantName = in.readString();
        description = in.readString();
        serviceDate = in.readString();
        year = in.readString();
    }


    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMakeSlug() {
        return makeSlug;
    }

    public void setMakeSlug(String makeSlug) {
        this.makeSlug = makeSlug;
    }

    public String getModelSlug() {
        return modelSlug;
    }

    public void setModelSlug(String modelSlug) {
        this.modelSlug = modelSlug;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getVariantCode() {
        return variantCode;
    }

    public void setVariantCode(String variantCode) {
        this.variantCode = variantCode;
    }

    public Variant getVariant() {
        return variant;
    }

    public void setVariant(Variant variant) {
        this.variant = variant;
    }


    @NonNull
    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(@Nonnull String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getVinNumber() {
        return vinNumber;
    }

    public void setVinNumber(String vinNumber) {
        this.vinNumber = vinNumber;
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBatteryNumber() {
        return batteryNumber;
    }

    public void setBatteryNumber(String batteryNumber) {
        this.batteryNumber = batteryNumber;
    }

    public String getTransmissionType() {
        return transmissionType;
    }

    public void setTransmissionType(String transmissionType) {
        this.transmissionType = transmissionType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
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

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Make getMake() {
        return make;
    }

    public void setMake(Make make) {
        this.make = make;
    }

    public String getEngineOilType() {
        return engineOilType;
    }

    public void setEngineOilType(String engineOilType) {
        this.engineOilType = engineOilType;
    }

    public String getMakeName() {
        return makeName;
    }

    public void setMakeName(String makeName) {
        this.makeName = makeName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public String getDescription() {
        return description;
    }


    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    public String getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(String serviceDate) {
        this.serviceDate = serviceDate;
    }

    @Nullable
    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(@Nullable String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Insurance getInsurance() {
        return insurance;
    }

    public void setInsurance(Insurance insurance) {
        this.insurance = insurance;
    }

    public AMCDetails getAmcDetails() {
        return amcDetails;
    }

    public void setAmcDetails(AMCDetails amcDetails) {
        this.amcDetails = amcDetails;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Vehicle deepCopy() {
        Vehicle vehicle = new Vehicle();
        vehicle.id = id;
        vehicle.makeSlug = makeSlug;
        vehicle.makeName = makeName;
        vehicle.modelSlug = modelSlug;
        vehicle.modelName = modelName;
        vehicle.registrationNumber = registrationNumber;
        vehicle.fuelType = fuelType;
        vehicle.description = description;
        vehicle.engineOilType = engineOilType;
        vehicle.transmissionType = transmissionType;
        vehicle.customerId = customerId;
        vehicle.color = color;
        vehicle.year = year;
        return vehicle;
    }

    @Override
    public String toString() {
        return "Vehicle{" + "id='" + id + '\'' + ", makeSlug='" + makeSlug + '\'' + ", modelSlug='" + modelSlug + '\'' + ", fuelType='" + fuelType + '\'' + ", variantCode='" + variantCode + '\'' + ", variant=" + variant + ", registrationNumber='" + registrationNumber + '\'' + ", vinNumber='" + vinNumber + '\'' + ", engineNumber='" + engineNumber + '\'' + ", registrationDate='" + registrationDate + '\'' + ", color='" + color + '\'' + ", batteryNumber='" + batteryNumber + '\'' + ", transmissionType='" + transmissionType + '\'' + ", remarks='" + remarks + '\'' + ", customerId='" + customerId + '\'' + ", createdOn='" + createdOn + '\'' + ", updatedOn='" + updatedOn + '\'' + ", make=" + make + ", model=" + model + ", engineOilType='" + engineOilType + '\'' + ", makeName='" + makeName + '\'' + ", modelName='" + modelName + '\'' + ", variantName='" + variantName + '\'' + ", description='" + description + '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(vehicleId);
        dest.writeString(makeSlug);
        dest.writeString(modelSlug);
        dest.writeString(fuelType);
        dest.writeString(variantCode);
        dest.writeString(registrationNumber);
        dest.writeParcelable(insurance, flags);
        dest.writeParcelable(amcDetails, flags);
        dest.writeString(vinNumber);
        dest.writeString(engineNumber);
        dest.writeString(registrationDate);
        dest.writeString(color);
        dest.writeString(batteryNumber);
        dest.writeString(transmissionType);
        dest.writeString(remarks);
        dest.writeString(customerId);
        dest.writeString(createdOn);
        dest.writeString(updatedOn);
        dest.writeParcelable(make, flags);
        dest.writeParcelable(model, flags);
        dest.writeParcelable(variant, flags);
        dest.writeString(engineOilType);
        dest.writeString(makeName);
        dest.writeString(modelName);
        dest.writeString(variantName);
        dest.writeString(description);
        dest.writeString(serviceDate);
        dest.writeString(year);
    }

    public static final Creator<Vehicle> CREATOR = new Creator<Vehicle>() {
        @Override
        public Vehicle createFromParcel(Parcel in) {
            return new Vehicle(in);
        }

        @Override
        public Vehicle[] newArray(int size) {
            return new Vehicle[size];
        }
    };

}
