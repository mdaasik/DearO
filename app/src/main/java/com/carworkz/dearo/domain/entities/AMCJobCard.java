
package com.carworkz.dearo.domain.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.carworkz.dearo.amc.amcsolddetails.AmcServiceRedemptionAdapter;
import com.squareup.moshi.Json;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class AMCJobCard implements Parcelable {

    public static final String STATUS_INITIATED = "INITIATED";
    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CLOSED = "CLOSED";
    public static final String STATUS_CANCELLED = "CANCELLED";


    public static final String TYPE_ACCIDENTAL = "ACCIDENTAL";
    public static final String TYPE_PERIODIC = "PERIODIC";
    public static final String TYPE_MINOR = "MINOR";
    public static final String TYPE_MAJOR = "MAJOR";
    public static final String TYPE_AMC_SMC = "AMC/SMC";


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
    @Json(name = "status")
    private String status;
    @Json(name = "type")
    private String type = TYPE_PERIODIC;
    @Json(name = "verbatim")
    private List<String> verbatim = new ArrayList<>();
    @Json(name = "technicianId")
    private String technicianId;
    @Json(name = "serviceAdviserId")
    private String serviceAdviserId;
    @Json(name = "bayId")
    private String bayId;
    @Json(name = "serviceType")
    private String serviceType;


    @Json(name = "createdOn")
    private String createdOn;
    @Json(name = "fuelReading")
    private Integer fuelReading;
    @Json(name = "kmsReading")
    private Integer kmsReading;
    @Json(name = "updatedOn")
    private String updatedOn;
    @Json(name = "step")
    private String step = "VERBATIM";
    @Json(name = "stepLabel")
    private String stepLabel;
    @Json(name = "userComment")
    private String userComment;
    @Json(name = "inspectionGroup")
    private String inspectionGroup;
    @Json(name = "completionDate")
    private String completionDate;
    @Json(name = "source")
    private String source;

    @Json(name = "inventory")
    private List<Inventory> inventory = null;
    @Json(name = "inspection")
    private List<InspectionItem> inspection = null;
    @Json(name = "vehicle")
    private Vehicle vehicle;
    @Json(name = "customer")
    private Customer customer;
    @Json(name = "workshop")
    private Workshop workshop;
    @Json(name = "estimate")
    private Estimate estimate;
    @Json(name = "invoice")
    private Invoice invoice;
    @Json(name = "customerAddress")
    private Address address;
    @Json(name = "feedback")
    private Feedback feedback;
    @Json(name = "packages")
    private List<ServicePackage> packages = new ArrayList<>(0);
    @Json(name = "costEstimate")
    private CostEstimate costEstimate;
    @Nullable
    @Json(name = "mrnEstimate")
    private MrnEstimate mrn;
    @Nullable
    @Json(name = "remarks")
    private List<Remark> remarks= null;;
    @Json(name = "workshopResource")
    private WorkshopResource workshopResource;
    @Json(name = "customerSignature")
    private Signature customerSignature;
    @Json(name = "accidental")
    private Accidental accidental;
    @Json(name = "vehicleAmcId")
    private String vehicleAmcId;
    @Json(name = "redemptions")
    private List<Redemption> redemptions=null;

    public AMCJobCard() {

    }

    public static final Creator<AMCJobCard> CREATOR = new Creator<AMCJobCard>() {
        @Override
        public AMCJobCard createFromParcel(Parcel in) {
            return new AMCJobCard(in);
        }

        @Override
        public AMCJobCard[] newArray(int size) {
            return new AMCJobCard[size];
        }
    };
    @Json(name = "vehicleType")
    private String vehicleType;

    protected AMCJobCard(Parcel in) {
        id = in.readString();
        jobCardId = in.readString();
        workshopId = in.readString();
        customerId = in.readString();
        vehicleId = in.readString();
        status = in.readString();
        type = in.readString();
        verbatim = in.createStringArrayList();
        technicianId = in.readString();
        serviceAdviserId = in.readString();
        bayId = in.readString();
        serviceType = in.readString();
        createdOn = in.readString();
        if (in.readByte() == 0) {
            fuelReading = null;
        } else {
            fuelReading = in.readInt();
        }
        if (in.readByte() == 0) {
            kmsReading = null;
        } else {
            kmsReading = in.readInt();
        }
        updatedOn = in.readString();
        step = in.readString();
        stepLabel = in.readString();
        userComment = in.readString();
        inspectionGroup = in.readString();
        completionDate = in.readString();
        source = in.readString();
        inventory = in.createTypedArrayList(Inventory.CREATOR);
        inspection = in.createTypedArrayList(InspectionItem.CREATOR);
        vehicle = in.readParcelable(Vehicle.class.getClassLoader());
        customer = in.readParcelable(Customer.class.getClassLoader());
        workshop = in.readParcelable(Workshop.class.getClassLoader());
        estimate = in.readParcelable(Estimate.class.getClassLoader());
        invoice = in.readParcelable(Invoice.class.getClassLoader());
        address = in.readParcelable(Address.class.getClassLoader());
        feedback = in.readParcelable(Feedback.class.getClassLoader());
        packages = in.createTypedArrayList(ServicePackage.CREATOR);
        costEstimate = in.readParcelable(CostEstimate.class.getClassLoader());
        mrn = in.readParcelable(MrnEstimate.class.getClassLoader());
        remarks = in.createTypedArrayList(Remark.CREATOR);
        customerSignature = in.readParcelable(Signature.class.getClassLoader());
        accidental = in.readParcelable(Accidental.class.getClassLoader());
        vehicleType = in.readString();
        vehicleAmcId = in.readString();
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
        dest.writeString(type);
        dest.writeStringList(verbatim);
        dest.writeString(technicianId);
        dest.writeString(serviceAdviserId);
        dest.writeString(bayId);
        dest.writeString(serviceType);
        dest.writeString(createdOn);
        if (fuelReading == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(fuelReading);
        }
        if (kmsReading == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(kmsReading);
        }
        dest.writeString(updatedOn);
        dest.writeString(step);
        dest.writeString(stepLabel);
        dest.writeString(userComment);
        dest.writeString(inspectionGroup);
        dest.writeString(completionDate);
        dest.writeString(source);
        dest.writeTypedList(inventory);
        dest.writeTypedList(inspection);
        dest.writeParcelable(vehicle, flags);
        dest.writeParcelable(customer, flags);
        dest.writeParcelable(workshop, flags);
        dest.writeParcelable(estimate, flags);
        dest.writeParcelable(invoice, flags);
        dest.writeParcelable(address, flags);
        dest.writeParcelable(feedback, flags);
        dest.writeTypedList(packages);
        dest.writeParcelable(costEstimate, flags);
        dest.writeParcelable(mrn, flags);
        dest.writeTypedList(remarks);
        dest.writeParcelable(customerSignature, flags);
        dest.writeParcelable(accidental, flags);
        dest.writeString(vehicleType);
        dest.writeString(vehicleAmcId);
    }


    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
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

    public Accidental getAccidental() {
        return accidental;
    }

    public void setAccidental(Accidental accidental) {
        this.accidental = accidental;
    }

    public List<String> getVerbatim() {
        return verbatim;
    }

    public void setVerbatim(List<String> verbatim) {
        this.verbatim = verbatim;
    }

    public String getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(String technicianId) {
        this.technicianId = technicianId;
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

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public Integer getFuelReading() {
        return fuelReading;
    }

    public void setFuelReading(Integer fuelReading) {
        this.fuelReading = fuelReading;
    }

    public Integer getKmsReading() {
        return kmsReading;
    }

    public void setKmsReading(Integer kmsReading) {
        this.kmsReading = kmsReading;
    }

    public List<Inventory> getInventory() {
        return inventory;
    }

    public void setInventory(List<Inventory> inventory) {
        this.inventory = inventory;
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

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Workshop getWorkshop() {
        return workshop;
    }

    public void setWorkshop(Workshop workshop) {
        this.workshop = workshop;
    }

    public Estimate getEstimate() {
        return estimate;
    }

    public void setEstimate(Estimate estimate) {
        this.estimate = estimate;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getUserComment() {
        return userComment;
    }


    public String getInspectionGroup() {
        return inspectionGroup;
    }

    public void setInspectionGroup(String inspectionGroup) {
        this.inspectionGroup = inspectionGroup;
    }

    public List<InspectionItem> getInspection() {
        return inspection;
    }

    public void setInspection(List<InspectionItem> inspection) {
        this.inspection = inspection;
    }

    public String getStepLabel() {
        return stepLabel;
    }

    public void setStepLabel(String stepLabel) {
        this.stepLabel = stepLabel;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public List<ServicePackage> getPackages() {
        return packages;
    }

    public void setPackages(List<ServicePackage> packages) {
        this.packages = packages;
    }

    public CostEstimate getCostEstimate() {
        return costEstimate;
    }

    public void setCostEstimate(CostEstimate costEstimate) {
        this.costEstimate = costEstimate;
    }

    public String getVehicleAmcId() {
        return vehicleAmcId;
    }

    public void setVehicleAmcId(String vehicleAmcId) {
        this.vehicleAmcId = vehicleAmcId;
    }

    @Nullable
    public List<Remark> getRemarks() {
        return remarks;
    }

    public void setRemarks(@Nullable List<Remark> remarks) {
        this.remarks = remarks;
    }

    public WorkshopResource getWorkshopResource() {
        return workshopResource;
    }

    public void setWorkshopResource(WorkshopResource workshopResource) {
        this.workshopResource = workshopResource;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Signature getCustomerSignature() {
        return customerSignature;
    }

    public void setCustomerSignature(Signature customerSignature) {
        this.customerSignature = customerSignature;
    }

    public void setUserComment(String userComment) {
        if (userComment == null)
            this.userComment = "";
        else {
            this.userComment = userComment;

        }
    }

    @Nullable
    public MrnEstimate getMrn() {
        return mrn;
    }

    public void setMrn(@Nullable MrnEstimate mrn) {
        this.mrn = mrn;
    }

    @Override
    public String toString() {
        return "JobCard{" +
                "id='" + id + '\'' +
                ", jobCardId='" + jobCardId + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                ", status='" + status + '\'' +
                ", verbatim=" + verbatim +
                ", fuelReading=" + fuelReading +
                ", kmsReading=" + kmsReading +
                ", inventory=" + inventory +
                ", inspection=" + inspection +
                ", estimate=" + estimate +
                ", costEstimate=" + costEstimate +
                '}';
    }
    public List<Redemption> getRedemptions() {
        return redemptions;
    }

    public void setRedemptions(List<Redemption> redemptions) {
        this.redemptions = redemptions;
    }
}
