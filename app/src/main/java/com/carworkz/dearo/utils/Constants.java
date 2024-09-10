package com.carworkz.dearo.utils;

import android.os.Build;

import com.carworkz.dearo.BuildConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface Constants {

    String PLATFORM = "ANDROID";
    String DEVICE_MODEL = Build.MODEL;
    String DEVICE_VERSION = Build.VERSION.RELEASE;

    interface PreferencesKeys {

        interface User {
            String USER_ID = "user_id";
            String CLUSTER_ID = "cluster_id";
            String USER_NAME = "username";
            String USER_MOBILE = "user_mobile";
            String USER_EMAIL = "user_email";
            String USER_ACCESS_TOKEN = "user_access_token";
            String USER_FCM_TOKEN = "user_fcm_token";
            String USER_IS_FCM_TOKEN_SYNCED = "user_is_fcm_token_synced";
            String USER_WORKSHOP_ID = "user_work_shop_id";
            String USER_WORKSHOP_GST_ENABLED = "user_work_shop_gst_enabled";
            String USER_WORKSHOP_INVENTORY_ENABLED = "user_work_shop_inventory_enabled";
            String USER_WORKSHOP_DISPLAY_NAME = "user_work_shop_display_name";
            String USER_WORKSHOP_REGISTERED_NAME = "user_work_shop_registered_name";
            String USER_WORKSHOP_PART_ENABLED = "user_work_shop_part_enabled";
            String USER_WORKSHOP_LABOUR_ENABLED = "user_work_shop_labour_enabled";
            String USER_WORKSHOP_SERVICE_ADVISOR_ENABLED = "user_work_shop_service_advisor_enabled";
            String USER_WORKSHOP_COMPOSITE_ENABLED = "user_work_shop_composite_enabled";
            String USER_WORKSHOP_INSURANCE_VALIDATION_ENABLED = "user_work_shop_insurance_validation_enabled";
            String USER_ALLOW_EDIT_PART_PRICE = "user_allow_edit_part_price";
            String USER_ALLOW_JOB_CARD_CLOSURE = "user_allow_job_card_closure";
            String USER_ALLOW_EDIT_LABOUR_RATE = "user_allow_edit_labour_rate";
            String USER_ALLOW_EDIT_MISC_RATE = "user_allow_edit_misc_rate";
            String USER_MISC_MAX_PRICE = "user_misc_max_price";
            String USER_MISC_LABOUR_ID = "user_misc_labour_id";
            String USER_APPOINTMENT_ACTIVE = "appointment_active";
            String USER_HSN_ENABLED = "user_hsn_enabled";
            String USER_HSN_PART_ENABLED = "user_hsn_part_enabled";
            String USER_HSN_LABOUR_ENABLED = "user_hsn_labour_enabled";
            String USER_SERVICE_PACKAGE_ACTIVE = "user_is_service_package_enabled";
            String USER_SERVICE_PACKAGE_MANDATORY = "user_is_service_package_mandatory";
            String USER_PART_FINDER_ENABLE = "user_is_part_finder_enable";
            String USER_AMC_ENABLED = "user_amc_enabled";
            String USER_ACCIDENTAL_ENABLED = "user_accidental_enabled";
            String USER_SERVICE_PACKAGE_JC = "user_service_package_jc";
            String USER_OTC_ENABLED = "user_otc_enable";
            String USER_DOORSTEP_ENABLED = "user_doorstep_enable";
            String USER_NOTIFICATION_ENABLED = "user_notification_enabled";
            String USER_SMS_ENABLED = "user_sms_enabled";
            String USER_NOTIFY_CREATE_JC = "user_notify_create_jc";
            String USER_NOTIFY_COMPLETE_JC = "user_notify_complete_jc";
            String USER_NOTIFY_CLOSE_JC = "user_notify_close_jc";
            String USER_NOTIFY_CANCEL_INVOICE = "user_notify_cancel_invoice";
            String USER_NOTIFY_CREATE_INVOICE = "user_notify_create_jc";
            String USER_DEFAULT_NOTIFY_CREATE_JC = "user_def_create_jc";
            String USER_DEFAULT_NOTIFY_COMPLETE_JC = "user_def_complete_jc";
            String USER_DEFAULT_NOTIFY_CLOSE_JC = "user_def_close_jc";
            String USER_DEFAULT_NOTIFY_CREATE_INVOICE = "user_def_create_invoice";
            String USER_DEFAULT_NOTIFY_CANCEL_INVOICE = "user_def_cancel_invoice";
            String USER_CUSTOMER_SOURCE = "user_customer_source";
            String USER_BRANDING = "user_branding";
            String USER_BRANDING_CLUSTER_LOGO = "user_branding_cluster_logo";
            String USER_BRANDING_LOGO = "user_branding_logo";
            String USER_VEHICLE_TYPE = "user_vehicle_types";
            String USER_JC_FLOW = "user_jc_flow";
            String USER_MRN_ENABLED = "user_mrn_enabled";
            String USER_NOTIFY_WHATSAPP = "user_notify_whatsapp";
            String USER_LABOUR_SURCHARGE= "user_labour_surcharge";
            String USER_LABOUR_REDUCTION= "user_labour_reduction";
            String USER_LABOUR_SURCHARGE_MAX_AMOUNT= "user_labour_surcharge_max_amount";
            String USER_ALLOW_REASON_FOR_DELAY= "user_allow_reason_for_delay";
            String USER_ALLOW_PRE_DELIVERY_CHECK= "user_allow_pre_delivery_check";
            String USER_ALLOW_PRE_DELIVERY_CHECK_MODE= "user_allow_pre_delivery_check_mode";
            String USER_CUSTOMER_APPROVAL= "user_customer_approval";
            String USER_CUSTOMER_GROUP= "user_customer_group";
            String USER_ALLOW_OSL_WORK_ORDER= "user_allow_osl_work_order";
            String USER_JC_APPROVAL= "user_jc_approval";
            String USER_APP_ROUTES= "appRoutes";
            String USER_APP_ROLE= "userAppRole";
            String USER_IS_FOC= "isFOC";
        }

        interface App {
            String APP_FORCE_UPDATE = "force_update";
            String APP_FORCE_UPDATE_TEXT = "force_update_text";
            String APP_FORCE_UPDATE_LINK = "force_update_link";
            String APP_OPTIONAL_UPDATE_AVAILABLE = "updateAvailable";
            String APP_MINIMUM_VERSION = "minVersionCode";
            String APP_VERSION_CODE = "versionCode";
            String APP_DEBUG_BASE_URL = "debug_base_url";
            String APP_DEVICE_ID = "device_id";
        }
    }

    interface DatabaseConstants {

        String COLUMN_FILE_JOB_CARD_ID = "jobCardID";
        String COLUMN_FILE_PRIMARY_KEY = "pid";
        String COLUMN_FILE_URL = "url";
        String COLUMN_FILE_IS_UPLOADED = "isUploaded";
        String COLUMN_FILE_IS_DELETED = "isDeleted";
        String COLUMN_FILE_FILE_ID = "id";
        String COLUMN_FILE_NAME = "name";
        String COLUMN_FILE_META_CATEGORY = "meta.category";
        String COLUMN_FILE_CAPTION = "caption";
        String COLUMN_FILE_ORIGINAL_NAME = "originalName";
        String COLUMN_FILE_MIME = "mime";
        String COLUMN_FILE_TYPE = "type";
        String COLUMN_CUSTOM_SCOPE = "customScope";
        String COLUMN_FILE_PATH = "path";
        String COLUMN_FILE_UPLOADABLE_ID = "uploadableId";
        String COLUMN_FILE_UPLOADABLE_TYPE = "uploadableType";
        String COLUMN_WHATSAPP_TEMPLATE_ID = "id";
    }

    interface ErrorConstants {

        String LOGIN_MOBILE_NOT_EXISTS = "UNAUTHORIZED";
        String INVALID_ACCESS_TOKEN = "INVALID_TOKEN";

        int NETWORK_CANCEL_REQ_CODE = 133;
        int NETWORK_ERROR_STATUS_CODE = 502;
        String NETWORK_ERROR_STATUS = "BAD GATEWAY";
        String DEFAULT_ERROR_MESSAGE = "Something Went Wrong!";
        String DEFAULT_ERROR_CODE = "INTERNAL ERROR";
    }

    interface ApiConstants {

        String BASE_URL = BuildConfig.BASE_URL; //BuildConfig.BASE_URL;
        String BASE_URL_API = "api/"; //BuildConfig.BASE_URL;
        String URL_BASE_INVOICES = ApiConstants.BASE_URL_API + "Invoices/";
        String BASE_URL_WEB = BuildConfig.BASE_WEB_URL;

        String FOUND = "found";
        String NOT_FOUND = "not-found";
        String MISMATCH = "doesnot-match";
        String URL_CREATE_PROFORMA_ESTIMATE = URL_BASE_INVOICES + "{id}/createEstimate";
        String URL_BASE_JOB_CARD = ApiConstants.BASE_URL_API + "JobCards/";
        String URL_APPOINTMENT = ApiConstants.BASE_URL_API + "Appointments/";
        String URL_WORKSHOP = ApiConstants.BASE_URL_API + "Workshops/";
        String URL_CUSTOMER_VEHICLES = ApiConstants.URL_WORKSHOP + "customerVehicles";
        String URL_CUSTOMER_VEHICLE_HISTORY = ApiConstants.URL_WORKSHOP + "customerVehicleDetails/{id}";
        String URL_WORKSHOP_RESOURCES = ApiConstants.URL_WORKSHOP + "getResource";
        String URL_JOB_CARD = ApiConstants.URL_BASE_JOB_CARD + "{id}/";
        String URL_CANCEL_JOBCARD = ApiConstants.URL_JOB_CARD + "cancel";
        String URL_JOB_FROM_JOB_CARD = ApiConstants.URL_BASE_JOB_CARD + "getDetails/{id}/";
        String URL_GET_DETAILS_INVOICE = ApiConstants.URL_BASE_INVOICES + "getDetails/{id}/";

        String URL_DASHBOARD = URL_WORKSHOP + "dashboard";
        String URL_SIGNATURE = URL_JOB_CARD + "uploadCustomerSignature";

        String URL_SAVE_LEAD = BASE_URL_API + "DearoLeads/{id}";

        String URL_INSPECTION_GROUP = ApiConstants.BASE_URL_API + "InspectionGroups/";
        String URL_INSPECTION_BY_GROUP_ID = URL_INSPECTION_GROUP + "{id}/inspections";

        //Customer Sources
//        api/SourceTypes
        String URL_CUSTOMER_SOURCE_TYPES = BASE_URL_API + "SourceTypes";
        String URL_CUSTOMER_SOURCE_BY_ID = BASE_URL_API + "SourceTypes/{id}/getSource";



        String URL_LOGIN = ApiConstants.BASE_URL_API + "WsUsers/login/";
        String URL_LOGOUT = ApiConstants.BASE_URL_API + "WsUsers/logout/";
        String URL_DAMAGE_IMAGE = ApiConstants.BASE_URL_API + "JobCards/{id}/uploadDamageImage/";
        String URl_UPLOAD_DOC = ApiConstants.URL_JOB_CARD + "uploadAccidentalDocument";
        String URL_DELETE_DOC = ApiConstants.URL_JOB_CARD + "deleteAccidentalDocument";
        String URL_GET_DOC = ApiConstants.URL_JOB_CARD + "documents";
        String URL_GET_PACKAGES = ApiConstants.URL_JOB_CARD + "servicePackages";
        String URL_UPDATE_JOBCARD_PACKAGES = ApiConstants.URL_JOB_CARD + "updateServicePackages";
        String URL_GET_INVOICE_BY_ID = URL_BASE_INVOICES + "{id}/";
        String URL_DAMAGE_DATA = ApiConstants.BASE_URL_API + "JobCards/{id}/damages";
        String URL_PDC_DATA = ApiConstants.BASE_URL_API + "JobCards/{id}/pdc";
        String URL_DELETE_IMAGE = ApiConstants.BASE_URL_API + "JobCards/{id}/damages/{fk}";
        String URL_UPDATE_CAPTION = ApiConstants.BASE_URL_API + "JobCards/{id}/damages/{fk}";
        String URL_SEND_OTP = ApiConstants.BASE_URL_API + "WsUsers/generateOtp/";
        String URL_MAKE = ApiConstants.BASE_URL_API + "Workshops/{id}/getMakes";
        String URL_MODEL = ApiConstants.BASE_URL_API + "Workshops/{id}/getModels";
        String URL_VARIANT = ApiConstants.BASE_URL_API + "Workshops/{id}/getVariants";
        String URL_ADD_VEHICLE = ApiConstants.BASE_URL_API + "Vehicles";
        String URL_UPDATE_VEHICLE = ApiConstants.BASE_URL_API + "Vehicles/{id}";
        String URL_UPDATE_VEHICLE_VARIANT = ApiConstants.BASE_URL_API + "Vehicles/{id}/updateVariant";
        String URL_INSURANCE_COMPANY = ApiConstants.BASE_URL_API + "InsuranceCompanies/getListing";
        String URL_INSURANCE_COMPANY_ADDRESSES = ApiConstants.BASE_URL_API + "InsuranceCompanies/getAddress";
        String URL_GET_INVENTORY = ApiConstants.BASE_URL_API + "Inventories";
        String URL_ACCIDENTAL = ApiConstants.URL_JOB_CARD + "saveAccidentalDetails";
        String URL_MISSING_ACCIDENTAL = ApiConstants.URL_JOB_CARD + "updateAccidentalDetails";
        String URL_ADD_JOB_CARD_VOICE = ApiConstants.URL_JOB_CARD + "updateVerbatim/";
        String URL_ADD_JOB_CARD_INVENTORY = ApiConstants.URL_JOB_CARD + "updateInventory/";
        String URL_ADD_JOB_CARD_INSPECTION = ApiConstants.URL_JOB_CARD + "updateInspection/";
        String URL_ADD_JOB_CARD_ESTIMATE = ApiConstants.URL_JOB_CARD + "updateEstimate/";
        String URL_JOB_CARD_GET_RECOMMENDED_JOBS = ApiConstants.URL_JOB_CARD + "getVerbatimAndJobs/";
        String URL_JOB_CARD_SAVE_RECOMMENDED_JOBS = ApiConstants.URL_JOB_CARD + "updateJobs/";
        String URL_JOB_CARD_COMPLETE = ApiConstants.URL_JOB_CARD + "completeJobCard/";
        String URL_JOB_CARD_CLOSE = ApiConstants.URL_JOB_CARD + "close/";
        String URL_SAVE_REMARK = ApiConstants.URL_JOB_CARD + "addRemarks";
        String URL_GET_REMARK = ApiConstants.URL_JOB_CARD + "getRemarks";
        String URL_SAVE_QUICK_JOBCARD = ApiConstants.URL_JOB_CARD + "flexibleFlow";
        String URL_GET_JOBCARD_PDC_CHECKLIST = ApiConstants.URL_JOB_CARD + "checklist";
        String URL_POST_JOBCARD_PDC_CHECKLIST = ApiConstants.URL_JOB_CARD + "preDelivery";

        String URL_ADD_CUSTOMER = ApiConstants.BASE_URL_API + "Customers/";
        String URL_ADD_CUSTOMER_AND_ADDRESS = ApiConstants.BASE_URL_API + "Customers/saveDetails";
        String URL_GET_CUSTOMER_BY_ID = ApiConstants.BASE_URL_API + "Customers/{id}/getDetail";
        String URL_UPDATE_CUSTOMER = ApiConstants.BASE_URL_API + "Customers/{id}/";
        String URL_GET_CUSTOMER_SUGGEST = ApiConstants.BASE_URL_API + "Customers/getConcerns";
        String URL_PINCODE = ApiConstants.BASE_URL_API + "Pincodes/{id}/";
        String URL_ADD_ADDRESS = ApiConstants.BASE_URL_API + "Addresses/";
        String URL_UPDATE_ADDRESS = ApiConstants.BASE_URL_API + "Addresses/{id}";
        String URL_SEARCH = ApiConstants.BASE_URL_API + "JobCards/create-search";
        String URL_NEW_SEARCH = ApiConstants.BASE_URL_API + "JobCards/appointment-search";
        String URL_CARD_SEARCH = ApiConstants.BASE_URL_API + "{path}/suggest";
        String URL_JOB_SEARCH = ApiConstants.BASE_URL_API + "Jobs/suggest";
        String URL_PART_SEARCH = ApiConstants.BASE_URL_API + "Parts/suggest";
        String URL_LABOUR_SEARCH = ApiConstants.BASE_URL_API + "Labours/suggest";


        String URL_COST_ESTIMATE = ApiConstants.BASE_URL_API + "JobCards/{id}/costEstimate";

        String URL_SEARCH_IN_STOCK_PART_NUMBER = ApiConstants.BASE_URL_API + "PurchaseInvoices/suggest";


        //Invoices
        String URL_INVOICES_UPDATE_PAYMENT = ApiConstants.URL_BASE_INVOICES + "{id}/updatePayment";
        String URL_INVOICES_UPDATE_PROFORMA = ApiConstants.URL_BASE_INVOICES + "{id}/updateProforma";
        String URL_CANCEL_INVOICE = ApiConstants.URL_BASE_INVOICES + "{id}/cancel";
        String URL_UPDATE_INVOICE_PACKAGES = ApiConstants.URL_GET_INVOICE_BY_ID + "updateServicePackages";
        String URL_UPDATE_THIRD_PARTY = ApiConstants.URL_GET_INVOICE_BY_ID + "updateThirdParty";

        String URL_GET_RECEIPT_DETAILS = URL_BASE_INVOICES + "getReceiptDetails/{id}";
        String URL_GET_RECEIPT_PDF = URL_BASE_INVOICES + "getReceiptPdf/{id}";


        //Parts

        String URL_ADD_PART = URL_BASE_INVOICES + "{id}/addPart";
        String URL_DELETE_PART = URL_BASE_INVOICES + "{id}/deletePart";
        String URL_PART_NUMBER_SUGGEST = BASE_URL_API + "PartNumbers/suggest";
        String URL_SEARCH_PART_NUMBER_SUGGEST = BASE_URL_API + "PartNumbers/partFinder";
        String URL_BRAND_NAME_SUGGEST = BASE_URL_API + "Brands/suggest";

        //Labour

        String URL_ADD_LABOUR = URL_BASE_INVOICES + "{id}/addLabour";
        String URL_DELETE_LABOUR = URL_BASE_INVOICES + "{id}/deleteLabour";

        //pdf
        String URL_CREATE_PROFORMA = URL_BASE_INVOICES + "{id}/createProforma";
        String URL_CREATE_INVOICE = URL_BASE_INVOICES + "{id}/createInvoice";
        String URL_GET_INVOICE_PDFS = URL_BASE_INVOICES + "{id}/getInvoicePdf";
        String URL_JC_PREVIEW = URL_BASE_JOB_CARD + "generatePdf/{id}";
        String URL_INVOICE_PREVIEW = URL_BASE_INVOICES + "{id}/pdf";
        String URL_GET_JOBCARD_ESTIMATE = URL_BASE_JOB_CARD + "{id}/createEstimatePdf";
        String URL_GET_JOBCARD_PDC_PRINT = URL_BASE_JOB_CARD + "generatePreDeliveryPdf/{id}";
        String URL_INSURANCE_PDF = URL_BASE_INVOICES + "{id}/insurancePdf";
        String URL_CUSTOMER_PDF = URL_BASE_INVOICES + "{id}/customerPdf";
        String URL_IGST_CUSTOMER_PDF = URL_BASE_INVOICES + "{id}/igstCustomerPdf";

        String URL_VEHICLE_AMC_PDF = BASE_URL_API + "VehicleAmcs/{id}/generatePdf";
        String URL_VEHICLE_AMC_CANCEL = BASE_URL_API + "VehicleAmcs/{id}/cancel";
        String URL_GET_OTHER_SYS_HISTORY = URL_BASE_JOB_CARD+ "vehicleHistory";


        String URL_GST_STATUS = BASE_URL_API + "Workshops/getWorkshopConfig/";
        String URL_FORCE_UPDATE = BASE_URL_API + "AppUpdates/checkUpdates/";

        String URL_GET_STATES = BASE_URL_API + "/StateCities/getStates";

        String URL_HSN_MASTER = BASE_URL_API + "HsnMasters/";

        String URL_GATE_PASS = URL_JOB_CARD + "generateGatePass/";

        String URL_TERMS_AND_CONDITION = BASE_URL + "terms/";
        String URL_PRIVACY_POLICY = BASE_URL + "policy/";

        String URL_SAVE_SEVICE_DATE = BASE_URL_API + "Vehicles/{id}/setReminder";

        //Appointments
        String URL_GET_APPOINTMENTS = URL_APPOINTMENT + "getListing";
        String URL_GET_APPOINTMENTS_BY_ID = URL_APPOINTMENT + "getDetails/{id}";
        String URL_APPOINTMENT_DETAILS = URL_APPOINTMENT + "initialize/";
        String URL_APPOINTMENT_PACKAGES = URL_APPOINTMENT + "{id}/addServicePackages/";
        String URL_APPOINTMENT_SAVE_TIME_SLOT = URL_APPOINTMENT + "{id}/updateDateTime/";
        String URL_APPOINTMENT_GET_TIME_SLOT = URL_APPOINTMENT + "{id}/getTimeSlots/";
        String URL_CANCEL_APPOINTMENTS = URL_APPOINTMENT + "{id}/cancel/";
        String URL_APPOINTMENT_UPSERTDETAILS = URL_APPOINTMENT + "{id}/upsertDetails/";
        String URL_APPOINTMENT_RESCHEDULE = URL_APPOINTMENT + "{id}/reschedule";
        String URL_APPOINTMENT_DECLINE = URL_APPOINTMENT + "{id}/decline";
        String URL_APPOINTMENT_ACCEPT = URL_APPOINTMENT + "{id}/confirm";
        String URL_APPOINTMENT_REASSIGN = URL_APPOINTMENT + "{id}/reassign";
        String URL_APPOINTMENT_STATUS_UPDATE = URL_APPOINTMENT + "updateAppointment";

        String URL_ADD_OTC_PROFORMA = URL_BASE_INVOICES + "addOtcProforma";

        String URL_SAVE_FEEDBACK = ApiConstants.URL_JOB_CARD + "customerFeedback";

        String URL_GET_SERVICE_ADVISORS = ApiConstants.URL_WORKSHOP + "{id}/getServiceAdvisors";

        String URL_GET_WORKSHOP_CONFIG = BASE_URL_API + "Workshops/getWorkshopConfig/";


        String URL_DEVICE_REGISTRAR = BASE_URL_API + "FcmNotifications/upsertWithWhere";


        String URL_WHATSAPP_TEMPLATE = URL_WORKSHOP + "getWhatsappMessage";

        //AMC API
        String URL_GET_AMC_LIST=BASE_URL_API+"VehicleAmcs/getDetails";
        String URL_SUGGEST_AMC_LIST=BASE_URL_API+"VehicleAmcs/suggest";
        String URL_PURCHASE_AMC=BASE_URL_API+"VehicleAmcs/saveDetails";
        String URL_GET_AMC_REDEMPTION=BASE_URL_API+"VehicleAmcs/{id}/getRedemptionDetails";

        //Vendor API
        String URL_GET_VENDOR_LIST=BASE_URL_API+"Workshops/getVendorsList";

        //Carpm API
        String REPORT_CAR_PM = "api/Carpms/getReportData";
        String CLEAR_CAR_PM = "api/Carpms/updateCarpmStatus";

        /*Api response Keys*/

        String KEY_JOB_CARD_INVENTORY_REMARKS = "userComment";
        String KEY_JOB_CARD_INVENTORY_SERVICE_TYPE = "serviceType";
        String KEY_JOB_CARD_INVENTORY_FUEL_READING = "fuelReading";
        String KEY_JOB_CARD_INVENTORY_KMS_READING = "kmsReading";
        String KEY_JOB_CARD_INVENTORY_LIST = "inventory";


        String KEY_JOB_CARD_ESTIMATE_DELIVERY_DATE_TIME = "deliveryDateTime";
        String KEY_JOB_CARD_ESTIMATE_MIN_COST = "minCost";
        String KEY_JOB_CARD_ESTIMATE_MAX_COST = "maxCost";
        String KEY_JOB_CARD_ESTIMATE_CLAIM_NUMBER = "claimNumber";
        String KEY_JOB_CARD_ESTIMATE_EXIPRY_DATE = "expiryDate";
        String KEY_JOB_CARD_ESTIMATE_COMPANY_NAME = "companyName";
        String KEY_JOB_CARD_ESTIMATE_POLICY_NUMBER = "policyNumber";
        String KEY_JOB_CARD_ESTIMATE_PINCODE = "pincode";

        String KEY_CCSEARCH_MOBILE = "mobile";

        String KEY_CUSTOMER_EMAIL = "email";
        String KEY_INCOMPLETE = "incomplete";

        String KEY_PART_PARTNUMBER = "partNumber";
        String KEY_PART_BRANDNAME = "brand";

        String KEY_VARIANTCODE = "variantCode";
        String KEY_NAME = "name";
        String KEY_PINCODE = "pincode";

        String KEY_PACKAGE_ID = "packageIds";


        String KEY_CUSTOMER_MOBILE = "mobile";
        String KEY_INSURANCE_ERROR = "insurance";
        String KEY_PINCODE_ERROR = "Pincode";
        String KEY_CLAIM_ERROR = "claim no.";
        String KEY_INSURANCE_POLICY = "insurance policy no";
        String KEY_INSURANCE_EXPIRY = "insurance expiry date.";
        String KEY_EXPIRY_ERROR = "expiry date";
        String KEY_INSURANCE_COMPANY = "insurance company";
        String KEY_COMPANY = "company";

        /*Whatsapp*/
        String KEY_WHATSAPP_JOB_CARD_COMPLETED = "completeJobCard";
        String KEY_WHATSAPP_JOB_CARD_CLOSE = "closeJobCard";
        String KEY_WHATSAPP_JOB_CARD_CREATE_INVOICE = "createInvoice";
        String KEY_WHATSAPP_JOB_CARD_CREATE_JC = "createJobCard";


    }

    interface BusinessConstants{

        List<String> ACCIDENTAL_DOCUMENT_TYPE_LIST = Collections.unmodifiableList(new ArrayList<String>() {{
            add("Insurance Copy");
            add("Driving Licence Copy");
            add("RC Copy");
            add("FIR Copy");
            add("Pan Card");
            add("Claim Form");
            add("Affidavit");
        }});


        String[] STATE_LIST = {"AP", "AR", "AS", "BR", "CG", "GA", "GJ", "HR",
                "HP", "JK", "JH", "KA", "KL", "MP", "MH", "MN", "ML", "MZ", "NL",
                "OD","OR", "PB", "RJ", "SK", "TN", "TS", "TR", "UA", "UK", "UP", "WB",
                "AN", "CH", "DN", "DD", "DL", "LD", "PY", "XX"};

        List<String> UNITS_LIST = new ArrayList<String>() {{
            add("UNT");
            add("L");
            add("BT");
            add("BOX");
            add("G");
            add("KG");
            add("KLR");
            add("ML");
            add("PAC");
            add("PCS");
        }};

        List<String> REASONS_FOR_DELAY_LIST= new ArrayList<String>(){
            {
                add("Select Reason");
                add("Delay due to Equipment Failure");
                add("Vehicle ready on time, delay in updation in system");
                add("Delay due to Customer Approval");
                add("Delay due to Parts Unavailability");
                add("Delay in Parts Delivery from Hub");
                add("Delay due to Corporate Approval");
                add("Mechanic/ Bay Unavailability");
                add("Delay due to MRP Updation");
                add("Delay due to Technical Issues");
                add("Delay due to Insurance Company Surveyor");
                add("Delay due to Power Failure");
                add("Delay due to Insurance Approval");
                add("Network Connectivity Issues");
                add("Delay due to Hardware Failure");
                add("Others");
            }
        };
    }

    interface AppMenuConstant{
        String MENU_HOME = "Home";
        String MENU_JOB_CARDS = "Job Cards";
        String MENU_REPAIR_BILLS = "Repair Bills";
        String MENU_APPOINTMENTS = "Appointments";
        String MENU_AMC = "AMC";
        String MENU_COUNTER_SALE = "Counter Sales";
        String MENU_MY_CUSTOMERS = "My Customers";
        String MENU_PART_FINDER_BETA = "Part Finder(Beta)";
        String MENU_SPARES = "Spares";
        String MENU_MY_PROFILE = "My Profile";
        String MENU_TERMS_AND_CONDITIONS = "Terms and Conditions";
        String MENU_PRIVACY_POLICY = "Privacy Policy";
        String MENU_CONTACT_US = "Contact Us";
        String MENU_LOGOUT = "Logout";
    }
}
