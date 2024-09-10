package com.carworkz.dearo.data.local;

import com.carworkz.dearo.BuildConfig;
import com.carworkz.dearo.data.Preferences;
import com.carworkz.dearo.domain.entities.User;
import com.carworkz.dearo.utils.Constants;

import java.sql.SQLOutput;
import java.util.List;


public class SharedPrefHelper {

    private static SharedPrefHelper INSTANCE = null;

    private SharedPrefHelper() {

    }

    public static SharedPrefHelper getInstance() {
        if (INSTANCE == null) {
            synchronized (SharedPrefHelper.class) {
                if (INSTANCE == null)
                    INSTANCE = new SharedPrefHelper();
            }
        }
        return INSTANCE;
    }

    public static void setUser(User user) {
        if (user != null) {

            System.out.println("--------clusterid------"+user.getClusterId());

            Preferences.user().set(Constants.PreferencesKeys.User.USER_ID, user.getId());
            Preferences.user().set(Constants.PreferencesKeys.User.CLUSTER_ID, user.getClusterId());
            Preferences.user().set(Constants.PreferencesKeys.User.USER_NAME, user.getName());
            Preferences.user().set(Constants.PreferencesKeys.User.USER_MOBILE, user.getMobile());
            Preferences.user().set(Constants.PreferencesKeys.User.USER_EMAIL, user.getEmail());
            Preferences.user().set(Constants.PreferencesKeys.User.USER_ACCESS_TOKEN, user.getToken().getId());
            Preferences.user().set(Constants.PreferencesKeys.User.USER_WORKSHOP_DISPLAY_NAME, user.getWorkshops().get(0).getDisplayName());
            Preferences.user().set(Constants.PreferencesKeys.User.USER_WORKSHOP_REGISTERED_NAME, user.getWorkshops().get(0).getRegisteredName());
            Preferences.user().set(Constants.PreferencesKeys.User.USER_WORKSHOP_ID, user.getDefaultWorkshop());
            if (user.getClusterLogo() != null) {
                SharedPrefHelper.setBrandingClusterLogo(user.getClusterLogo().getUrl());
            } else {
                SharedPrefHelper.setBrandingClusterLogo("");
            }

            if ((user.getWorkshops() != null && !user.getWorkshops().isEmpty() && user.getWorkshops().get(0).getLogo() != null)) {
                SharedPrefHelper.setBrandingLogo(user.getWorkshops().get(0).getLogo().getUrl());
            } else {
                SharedPrefHelper.setBrandingLogo("");
            }

            if (user.getBranding() != null) {
                SharedPrefHelper.setBrandingEnabled(user.getBranding().getEnabled());
            } else {
                SharedPrefHelper.setBrandingEnabled(false);
            }

            //save role
            setUserRole(user.getRole().get(0).getName());

            //set app routes
            setRoutes(user.getRole().get(0).getAppRoutes());
        }
    }

    public static void setRoutes(List<String> routes) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_APP_ROUTES, routes);
    }

    public static List<String> getRoutes() {
        return Preferences.user().getStringList(Constants.PreferencesKeys.User.USER_APP_ROUTES);
    }

    public static void setUserRole(String role) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_APP_ROLE, role);
    }

    public static String getUserRole() {
        return Preferences.user().getString(Constants.PreferencesKeys.User.USER_APP_ROLE,"");
    }

    public static String getWorkshopId() {
        return Preferences.user().getString(Constants.PreferencesKeys.User.USER_WORKSHOP_ID, "");
    }

    public static void setIsPartPriceEditable(boolean isPriceEnabled) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_ALLOW_EDIT_PART_PRICE, isPriceEnabled);
    }

    public static Boolean isPartPriceEditable() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_ALLOW_EDIT_PART_PRICE, true);
    }

    public static void setIsJobCardClosureAllowed(boolean isAllowed) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_ALLOW_JOB_CARD_CLOSURE, isAllowed);
    }

    public static Boolean isJobCardClosureAllowed() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_ALLOW_JOB_CARD_CLOSURE, false);
    }

    public static Boolean getIsLabourRateEditable() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_ALLOW_EDIT_LABOUR_RATE, true);
    }

    public static void setIsLabourRateEditable(boolean isRateEnabled) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_ALLOW_EDIT_LABOUR_RATE, isRateEnabled);
    }

    public static Boolean isHsnEnabled() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_HSN_ENABLED, true);
    }

    public static void setHsnEnabled(boolean isHsnEnabled) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_HSN_ENABLED, isHsnEnabled);
    }

    public static Boolean isServiceAdvisorEnabled() {
        //default return is false as per business requirement 09 SEP 21
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_WORKSHOP_SERVICE_ADVISOR_ENABLED, false);
//        return true;
    }

    public static void setServiceAdvisorEnabled(boolean isServiceAdvisorEnabled) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_WORKSHOP_SERVICE_ADVISOR_ENABLED, isServiceAdvisorEnabled);
    }

    public static Boolean isQuickFlow() {
//        return false;
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_JC_FLOW, true);
    }

    public static void setJcFlow(boolean isQuickJobCard) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_JC_FLOW, isQuickJobCard);
    }

    public static Boolean isHsnPartEnabled() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_HSN_PART_ENABLED, true);
    }

    public static void setHsnPartEnabled(boolean isHsnPartEnabled) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_HSN_PART_ENABLED, isHsnPartEnabled);
    }

    public static Boolean getIsHsnLabourEnabled() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_HSN_LABOUR_ENABLED, true);
    }

    public static void setIsHsnLabourEnabled(boolean isHsnLabourEnabled) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_HSN_LABOUR_ENABLED, isHsnLabourEnabled);
    }

    public static void setAllowAddCustomPart(boolean isPartEnabled) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_WORKSHOP_PART_ENABLED, isPartEnabled);
    }

    public static boolean isAddCustomPartEnabled() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_WORKSHOP_PART_ENABLED, true);
    }

    public static void setIsLabourEnabled(boolean isLabourEnabled) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_WORKSHOP_LABOUR_ENABLED, isLabourEnabled);
    }

    public static boolean isCustomLabourEnabled() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_WORKSHOP_LABOUR_ENABLED, true);
    }

    public static boolean isGstEnabled() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_WORKSHOP_GST_ENABLED, true);
    }

    public static void setGstEnabled(boolean isGstEnabled) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_WORKSHOP_GST_ENABLED, isGstEnabled);
    }

    public static void isInventoryEnabled(boolean isInventoryEnabled) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_WORKSHOP_INVENTORY_ENABLED, isInventoryEnabled);
    }

    public static boolean isInventoryEnabled() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_WORKSHOP_INVENTORY_ENABLED, false);
    }

    public static boolean isMiscEnabled() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_ALLOW_EDIT_MISC_RATE, false);
    }

    public static void setMiscEnabled(boolean isMiscEnabled) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_ALLOW_EDIT_MISC_RATE, isMiscEnabled);
    }

    public static String getMiscId() {
        return Preferences.user().getString(Constants.PreferencesKeys.User.USER_MISC_LABOUR_ID, "");
    }

    public static void setMiscId(String Id) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_MISC_LABOUR_ID, Id);
    }

    public static int getMaxMiscPrice() {
        return Preferences.user().getInt(Constants.PreferencesKeys.User.USER_MISC_MAX_PRICE, 500);
    }

    public static void setMaxMiscPrice(int maxPrice) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_MISC_MAX_PRICE, maxPrice);
    }

    public static String getUserAccessToken() {
        return Preferences.user().getString(Constants.PreferencesKeys.User.USER_ACCESS_TOKEN, "");
    }

    public static String getUserId() {
        return Preferences.user().getString(Constants.PreferencesKeys.User.USER_ID, "");
    }
    public static String getClusterId() {
        return Preferences.user().getString(Constants.PreferencesKeys.User.CLUSTER_ID, "");
    }

    public static String getUserName() {
        return Preferences.user().getString(Constants.PreferencesKeys.User.USER_NAME, "");
    }

    public static String getWorkShopName() {
        return Preferences.user().getString(Constants.PreferencesKeys.User.USER_WORKSHOP_DISPLAY_NAME, "");
    }

    public static boolean getForceUpdate() {
        return Preferences.app().getBoolean(Constants.PreferencesKeys.App.APP_FORCE_UPDATE, false);
    }

    public static void setForceUpdate(boolean forceUpdate) {
        Preferences.app().set(Constants.PreferencesKeys.App.APP_FORCE_UPDATE, forceUpdate);
    }

    public static boolean getOptionalUpdate() {
        return Preferences.app().getBoolean(Constants.PreferencesKeys.App.APP_OPTIONAL_UPDATE_AVAILABLE, false);
    }

    public static void setOptionalUpdate(boolean optionalUpdate) {
        Preferences.app().set(Constants.PreferencesKeys.App.APP_OPTIONAL_UPDATE_AVAILABLE, optionalUpdate);
    }

    public static int getVersionCode() {
        return Preferences.app().getInt(Constants.PreferencesKeys.App.APP_VERSION_CODE, 0);
    }

    public static void setVersionCode(int versionName) {
        Preferences.app().set(Constants.PreferencesKeys.App.APP_VERSION_CODE, versionName);
    }

    public static String getForceUpdateText() {
        return Preferences.app().getString(Constants.PreferencesKeys.App.APP_FORCE_UPDATE_TEXT, "Please update app to continue");
    }

    public static void setForceUpdateText(String forceUpdateText) {
        Preferences.app().set(Constants.PreferencesKeys.App.APP_FORCE_UPDATE_TEXT, forceUpdateText);
    }

    public static String getForceUpdateLink() {
        return Preferences.app().getString(Constants.PreferencesKeys.App.APP_FORCE_UPDATE_LINK, "");
    }

    public static void setForceUpdateLink(String forceUpdateLink) {
        Preferences.app().set(Constants.PreferencesKeys.App.APP_FORCE_UPDATE_LINK, forceUpdateLink);
    }

    public static int getMinimumVersion() {
        return Preferences.app().getInt(Constants.PreferencesKeys.App.APP_MINIMUM_VERSION, 0);
    }

    public static void setMinimumVersion(int minVersion) {
        Preferences.app().set(Constants.PreferencesKeys.App.APP_MINIMUM_VERSION, minVersion);
    }

    public static boolean isAppointmentActive() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_APPOINTMENT_ACTIVE, false);
    }

    public static void isAppointmentActive(Boolean isActive) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_APPOINTMENT_ACTIVE, isActive);
    }

    public static void setAppointmentServicePackageActive(Boolean isActive) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_SERVICE_PACKAGE_ACTIVE, isActive);
    }

    public static Boolean isAppointmentServicePackageActive() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_SERVICE_PACKAGE_ACTIVE, false);
    }

    public static void setAppointmentServicePackageMandatory(Boolean isMandatory) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_SERVICE_PACKAGE_MANDATORY, isMandatory);
    }

    public static Boolean isAppointmentServicePackageMandatory() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_SERVICE_PACKAGE_MANDATORY, false);
    }

    public static void isPartFinderEnable(boolean isActive) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_PART_FINDER_ENABLE, isActive);
    }

    public static Boolean isPartFinderEnabled() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_PART_FINDER_ENABLE, false);
    }

    public static boolean isAmcEnabled() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_AMC_ENABLED, true);
    }

    public static void setAmcEnabled(Boolean enabled) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_AMC_ENABLED, enabled);
    }

    public static boolean isAccidentalEnabled() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_ACCIDENTAL_ENABLED, false);
    }

    public static void setAccidentalEnabled(Boolean enabled) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_ACCIDENTAL_ENABLED, enabled);
    }

    public static void logout() {
        Preferences.user().clear();
    }

    public static Boolean isPackagesEnabled() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_SERVICE_PACKAGE_JC, false);
    }

    public static void setPackagesEnabled(Boolean isPackagesEnabled) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_SERVICE_PACKAGE_JC, isPackagesEnabled);
    }

    public static Boolean isOtcEnabled() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_OTC_ENABLED, false);
    }

    public static void setOtcEnabled(boolean enabled) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_OTC_ENABLED, enabled);
    }


    public static Boolean isDoorStepEnabled() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_DOORSTEP_ENABLED, false);
    }

    public static void setDoorStepEnabled(boolean enabled) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_DOORSTEP_ENABLED, enabled);
    }


    public static boolean isNotifyEnabled() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_NOTIFICATION_ENABLED, false);
    }

    public static void setNotifyEnabled(Boolean isNotificationEnabled) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_NOTIFICATION_ENABLED, isNotificationEnabled);
    }

    public static void setSmsEnabled(Boolean isSmsEnabled) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_SMS_ENABLED, isSmsEnabled);
    }

    public static Boolean isNotifyOnCreateJC() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_NOTIFY_CREATE_JC, false);
    }

    public static void setNotifyOnCreateJC(boolean notify) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_NOTIFY_CREATE_JC, notify);
    }

    public static Boolean isNotifyOnCompleteJC() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_NOTIFY_COMPLETE_JC, false);
    }

    public static void setNotifyOnCompleteJC(boolean notify) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_NOTIFY_COMPLETE_JC, notify);
    }

    public static Boolean isNotifyOnCloseJC() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_NOTIFY_CLOSE_JC, false);
    }

    public static void setNotifyOnCloseJC(boolean notify) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_NOTIFY_CLOSE_JC, notify);
    }

    public static Boolean isNotifyOnCreateInvoice() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_NOTIFY_CREATE_INVOICE, false);
    }

    public static void setNotifyOnCreateInvoice(boolean notify) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_NOTIFY_CREATE_INVOICE, notify);
    }

    public static Boolean getDefaultOptionCreateInvoice() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_DEFAULT_NOTIFY_CREATE_INVOICE, false);
    }

    public static void setDefaultOptionCreateInvoice(Boolean checked) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_DEFAULT_NOTIFY_CREATE_INVOICE, checked);
    }

    public static Boolean getDefaultOptionCancelInvoice() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_DEFAULT_NOTIFY_CANCEL_INVOICE, false);
    }

    public static void setDefaultOptionCancelInvoice(Boolean checked) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_DEFAULT_NOTIFY_CANCEL_INVOICE, checked);
    }

    public static Boolean isNotifyOnCancelInvoice() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_NOTIFY_CANCEL_INVOICE, false);
    }

    public static void setNotifyOnCancelInvoice(boolean notify) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_NOTIFY_CANCEL_INVOICE, notify);
    }

    public static Boolean getDefaultOptionCloseJC() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_DEFAULT_NOTIFY_CLOSE_JC, false);
    }

    public static void setDefaultOptionCloseJC(Boolean checked) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_DEFAULT_NOTIFY_CLOSE_JC, checked);
    }

    public static Boolean getDefaultOptionCompleteJC() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_DEFAULT_NOTIFY_COMPLETE_JC, false);

    }

    public static void setDefaultOptionCompleteJC(Boolean checked) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_DEFAULT_NOTIFY_COMPLETE_JC, checked);
    }

    public static Boolean getDefaultOptionCreateJC() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_DEFAULT_NOTIFY_CREATE_JC, false);
    }

    public static void setDefaultOptionCreateJC(Boolean checked) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_DEFAULT_NOTIFY_CREATE_JC, checked);
    }

    public static String getBaseUrl() {
        if (!BuildConfig.DEBUG) {
            throw new IllegalStateException("This method should'nt have been called in non debug builds");
        }
        return Preferences.app().getString(Constants.PreferencesKeys.App.APP_DEBUG_BASE_URL, Constants.ApiConstants.BASE_URL);
    }

    public static void setBaseUrl(String baseUrl) {
        Preferences.app().set(Constants.PreferencesKeys.App.APP_DEBUG_BASE_URL, baseUrl);
    }

    public static void clearBaseUrl() {
        Preferences.app().edit().remove(Constants.PreferencesKeys.App.APP_DEBUG_BASE_URL).clear().commit();
    }

    public static Boolean isBrandingEnabled() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_BRANDING, false);
    }

    public static void setBrandingEnabled(Boolean checked) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_BRANDING, checked);
    }

    public static String getBrandingClusterLogo() {
        return Preferences.user().getString(Constants.PreferencesKeys.User.USER_BRANDING_CLUSTER_LOGO, "");
    }

    @SuppressWarnings("WeakerAccess")
    public static void setBrandingClusterLogo(String url) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_BRANDING_CLUSTER_LOGO, url);
    }

    public static String getBrandingLogo() {
        return Preferences.user().getString(Constants.PreferencesKeys.User.USER_BRANDING_LOGO, "");
    }

    @SuppressWarnings("WeakerAccess")
    public static void setBrandingLogo(String url) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_BRANDING_LOGO, url);
    }

    public static List<String> getCustomerSource() {
        return Preferences.user().getStringList(Constants.PreferencesKeys.User.USER_CUSTOMER_SOURCE);
    }

    public static void setCustomerSource(List<String> customerSource) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_CUSTOMER_SOURCE, customerSource);
    }

    public static List<String> getWorkshopVehicleType() {
        return Preferences.user().getStringList(Constants.PreferencesKeys.User.USER_VEHICLE_TYPE);
    }

    public static void setWorkshopVehicleType(List<String> vehicleTypes) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_VEHICLE_TYPE, vehicleTypes);
    }

    public static String getFcmToken() {
        return Preferences.user().getString(Constants.PreferencesKeys.User.USER_FCM_TOKEN, "");
    }

    public static void setFcmToken(String fcmToken) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_FCM_TOKEN, fcmToken);
    }

    public static boolean isFcmSynced() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_IS_FCM_TOKEN_SYNCED, false);
    }

    public static void setFcmSynced() {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_IS_FCM_TOKEN_SYNCED, true);
    }

    public static String getDeviceId() {
        return Preferences.app().getString(Constants.PreferencesKeys.App.APP_DEVICE_ID, "");
    }

    public static void setDeviceId(String deviceId) {
        Preferences.app().set(Constants.PreferencesKeys.App.APP_DEVICE_ID, deviceId);
    }

    public static Boolean isMrnEnabled() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_MRN_ENABLED, false);
    }

    public static void setMrnEnabled(Boolean isEnabled) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_MRN_ENABLED, isEnabled);
    }

    public static void setNotifyByWhatsApp(boolean notify) {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_NOTIFY_WHATSAPP, notify);
    }

    public static boolean isNotifyWhatsappEnabled() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_NOTIFY_WHATSAPP, false);
    }

    public static void setLabourSurcharge(boolean isSurchargeEnabled){
        Preferences.user().set(Constants.PreferencesKeys.User.USER_LABOUR_SURCHARGE, isSurchargeEnabled);
    }

    public static boolean isLabourSurchargeEnabled(){
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_LABOUR_SURCHARGE, false);
    }

    public static void setLabourReduction(boolean isSurchargeEnabled){
        Preferences.user().set(Constants.PreferencesKeys.User.USER_LABOUR_REDUCTION, isSurchargeEnabled);
    }

    public static boolean isLabourReductionEnabled(){
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_LABOUR_REDUCTION, false);
    }

    public static void setCompositeEnabled(boolean isCompositeEnabled){
        Preferences.user().set(Constants.PreferencesKeys.User.USER_WORKSHOP_COMPOSITE_ENABLED, isCompositeEnabled);
    }

    public static boolean isCompositeEnabled(){
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_WORKSHOP_COMPOSITE_ENABLED, false);
    }

    public static void setInsuranceValidationEnabled(boolean enabled){
        Preferences.user().set(Constants.PreferencesKeys.User.USER_WORKSHOP_INSURANCE_VALIDATION_ENABLED, enabled);
    }

    public static boolean isInsuranceValidationEnabled(){
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_WORKSHOP_INSURANCE_VALIDATION_ENABLED, false);
    }


    public static void setLabourSurchargeMaxAmount(double maxSurchargeAmount){
        Preferences.user().set(Constants.PreferencesKeys.User.USER_LABOUR_SURCHARGE_MAX_AMOUNT, maxSurchargeAmount);
    }

    public static double getLabourSurchargeMaxAmount(){
        return Preferences.user().getDouble(Constants.PreferencesKeys.User.USER_LABOUR_SURCHARGE_MAX_AMOUNT, 0.0);
    }

    public static void allowReasonForDelay(boolean allowReasonForDelay)
    {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_ALLOW_REASON_FOR_DELAY, allowReasonForDelay);
    }

    public static boolean isReasonForDelayAllowed(){
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_ALLOW_REASON_FOR_DELAY,false);
    }

    public static void setOSLWorkOrder(boolean oslWorkOrder)
    {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_ALLOW_OSL_WORK_ORDER, oslWorkOrder);
    }

    public static boolean isOSLWorkOrder() {
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_ALLOW_OSL_WORK_ORDER, false);
    }

    public static void preDeliveryCheckEnabled(boolean allowReasonForDelay)
    {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_ALLOW_PRE_DELIVERY_CHECK, allowReasonForDelay);
    }

    public static boolean isPreDeliveryCheckEnabled(){
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_ALLOW_PRE_DELIVERY_CHECK,false);
    }

    public static void preDeliveryCheckMode(String mode)
    {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_ALLOW_PRE_DELIVERY_CHECK_MODE, mode);
    }

    public static String getPreDeliveryCheckMode(){
        return Preferences.user().getString(Constants.PreferencesKeys.User.USER_ALLOW_PRE_DELIVERY_CHECK_MODE,"");
    }
    public static void customerApproval(boolean approval)
    {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_CUSTOMER_APPROVAL, approval);
    }

    public static boolean getCustomerApproval(){
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_CUSTOMER_APPROVAL,false);
    }
    public static void customerGroup(boolean mode)
    {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_CUSTOMER_GROUP, mode);
    }

    public static boolean getCustomerGroup(){
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_CUSTOMER_GROUP,false);
    }
    public static void setApproval(boolean oslWorkOrder)
    {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_JC_APPROVAL, oslWorkOrder);
    }

    public static boolean getApproval(){
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_JC_APPROVAL,false);
    }

    public static void setFOC(boolean isFoc)
    {
        Preferences.user().set(Constants.PreferencesKeys.User.USER_IS_FOC, isFoc);
    }

    public static boolean getFOC(){
        return Preferences.user().getBoolean(Constants.PreferencesKeys.User.USER_IS_FOC,false);
    }
}
