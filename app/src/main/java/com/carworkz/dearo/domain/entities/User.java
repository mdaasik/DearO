
package com.carworkz.dearo.domain.entities;

import com.squareup.moshi.Json;

import java.util.List;

import javax.annotation.Nullable;

public class User {

    @Json(name = "id")
    private String id;
    @Json(name = "name")
    private String name;
    @Json(name = "mobile")
    private String mobile;
    @Json(name = "email")
    private String email;
    @Json(name = "defaultWorkshop")
    private String defaultWorkshop;

    @Json(name = "clusterId")
    private String clusterId;
    @Json(name = "where")
    private String status;
    @Json(name = "createdOn")
    private String createdOn;

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    @Json(name = "updatedOn")
    private String updatedOn;
    @Json(name = "workshops")
    private List<Workshop> workshops = null;
    @Json(name = "token")
    private Token token;
    @Json(name = "gst")
    private boolean isGstEnabled;
    @Json(name = "hsn")
    private boolean isHsnEnabled;
    @Json(name = "inventory")
    private InventoryConfig inventoryConfig;
    @Json(name = "jobCard")
    private JCConfig jcConfig;
    @Json(name = "miscLabour")
    private MiscLabour miscLabour;
    @Json(name = "appointment")
    private AppointmentConfig appointment;
    @Json(name = "partFinder")
    private PartFinder partFinder;
    @Json(name = "accidental")
    private AccidentalConfig accidentalConfig;
    @Json(name = "clusterLogo")
    private Logo clusterLogo;
    @Json(name = "amc")
    private AMCConfig amc;
    @Json(name = "otc")
    private OtcConfig otcConfig;
    @Nullable
    @Json(name = "servicePackage")
    private ServicePackageConfig servicePackageConfig;
    @Json(name = "notification")
    private NotificationConfig notification;
    @Nullable
    @Json(name="customerSource")
    private CustomerSourceConfig customerSource;
    @Json(name = "branding")
    private Branding branding;
    @Json(name = "vehicleType")
    private VehicleType vehicleType;
    @Json(name = "access")
    private ServiceAdviserAccessConfig accessConfig;
    @Json(name = "doorstep")
    private DoorStep doorStep;
    @Json(name = "invoice")
    private InvoiceConfig invoiceConfig;
    @Json(name = "validation")
    private ValidationConfig validationConfig;
    @Json(name = "customerGroup")
    private CustomerGroup customerGroup;
    @Json(name = "roles")
    private List<Role> role;
    @Json(name = "allowFOC")
    private FOCConfig allowFOC;



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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDefaultWorkshop() {
        return defaultWorkshop;
    }

    public void setDefaultWorkshop(String defaultWorkshop) {
        this.defaultWorkshop = defaultWorkshop;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public List<Workshop> getWorkshops() {
        return workshops;
    }

    public void setWorkshops(List<Workshop> workshops) {
        this.workshops = workshops;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public boolean isGstEnabled() {
        return isGstEnabled;
    }

    public void setGstEnabled(boolean gstEnabled) {
        isGstEnabled = gstEnabled;
    }

    public JCConfig getJCConfig() {
        return jcConfig;
    }

    public void setJCConfig(JCConfig jcConfig) {
        this.jcConfig = jcConfig;
    }

    public InventoryConfig getInventoryConfig() {
        return inventoryConfig;
    }

    public void setInventoryConfig(InventoryConfig inventoryConfig) {
        this.inventoryConfig = inventoryConfig;
    }

    public MiscLabour getMiscLabour() {
        return miscLabour;
    }

    public void setMiscLabour(MiscLabour miscLabour) {
        this.miscLabour = miscLabour;
    }

    public JCConfig getJcConfig() {
        return jcConfig;
    }

    public void setJcConfig(JCConfig jcConfig) {
        this.jcConfig = jcConfig;
    }

    public AppointmentConfig getAppointmentConfig() {
        return appointment;
    }

    public void setAppointmentConfig(AppointmentConfig appointment) {
        this.appointment = appointment;
    }

    public boolean isHsnEnabled() {
        return isHsnEnabled;
    }

    public void setHsnEnabled(boolean hsnEnabled) {
        isHsnEnabled = hsnEnabled;
    }

    public PartFinder getPartFinder() {
        return partFinder;
    }

    public void setPartFinder(PartFinder partFinder) {
        this.partFinder = partFinder;
    }

    public AppointmentConfig getAppointment() {
        return appointment;
    }

    public void setAppointment(AppointmentConfig appointment) {
        this.appointment = appointment;
    }

    public AccidentalConfig getAccidentalConfig() {
        return accidentalConfig;
    }

    public void setAccidentalConfig(AccidentalConfig accidentalConfig) {
        this.accidentalConfig = accidentalConfig;
    }

    public AMCConfig getAmc() {
        return amc;
    }

    public void setAmc(AMCConfig amc) {
        this.amc = amc;
    }

    @Nullable
    public ServicePackageConfig getServicePackageConfig() {
        return servicePackageConfig;
    }

    public void setServicePackageConfig(@Nullable ServicePackageConfig servicePackageConfig) {
        this.servicePackageConfig = servicePackageConfig;
    }

    public OtcConfig getOtcConfig() {
        return otcConfig;
    }

    public void setOtcConfig(OtcConfig otcConfig) {
        this.otcConfig = otcConfig;
    }

    public NotificationConfig getNotification() {
        return notification;
    }

    public void setNotification(NotificationConfig notification) {
        this.notification = notification;
    }

    @Nullable
    public CustomerSourceConfig getCustomerSource() {
        return customerSource;
    }

    public void setCustomerSource(@Nullable CustomerSourceConfig customerSource) {
        this.customerSource = customerSource;
    }

    public Logo getClusterLogo() {
        return clusterLogo;
    }

    public void setClusterLogo(Logo clusterLogo) {
        this.clusterLogo = clusterLogo;
    }

    public Branding getBranding() {
        return branding;
    }

    public void setBranding(Branding branding) {
        this.branding = branding;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public ServiceAdviserAccessConfig getAccessConfig() {
        return accessConfig;
    }

    public void setAccessConfig(ServiceAdviserAccessConfig accessConfig) {
        this.accessConfig = accessConfig;
    }

    public DoorStep getDoorStep() {
        return doorStep;
    }

    public void setDoorStep(DoorStep doorStep) {
        this.doorStep = doorStep;
    }

    public InvoiceConfig getInvoiceConfig() {
        return invoiceConfig;
    }

    public void setInvoiceConfig(InvoiceConfig invoiceConfig) {
        this.invoiceConfig = invoiceConfig;
    }

    public ValidationConfig getValidationConfig() {
        return validationConfig;
    }

    public void setValidationConfig(ValidationConfig validationConfig) {
        this.validationConfig = validationConfig;
    }

    public CustomerGroup getCustomerGroup() {
        return customerGroup;
    }

    public void setCustomerGroup(CustomerGroup customerGroup) {
        this.customerGroup = customerGroup;
    }

    public List<Role> getRole() {
        return role;
    }

    public void setRole(List<Role> role) {
        this.role = role;
    }

    public FOCConfig isAllowFOC() {
        return allowFOC;
    }

    public void setAllowFOC(FOCConfig allowFOC) {
        this.allowFOC = allowFOC;
    }
}
