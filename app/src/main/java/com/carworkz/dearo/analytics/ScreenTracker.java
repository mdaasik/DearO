package com.carworkz.dearo.analytics;

import android.app.Activity;

public interface ScreenTracker {

    String SCREEN_VOICE = "Voice";
    String SCREEN_VIEW_VOICE = "View Voice";
    String SCREEN_INVENTORY = "Inventory";
    String SCREEN_VIEW_INVENTORY = "View Inventory";
    String SCREEN_INSPECTION = "Inspection";
    String SCREEN_VIEW_INSPECTION = "View Inspection";
    String SCREEN_DAMAGE = "Damage";
    String SCREEN_VIEW_DAMAGE = "View Damage";
    String SCREEN_JOBS = "Jobs";
    String SCREEN_VIEW_JOBS = "View Jobs";
    String SCREEN_ESTIMATE = "Estimate";
    String SCREEN_VIEW_ESTIMATE = "View Estimate";
    String SCREEN_ESTIMATOR = "Estimator";
    String SCREEN_VIEW_ESTIMATOR = "View Estimator";
    String SCREEN_VIEW_JC = "View JobCard";
    String SCREEN_VIEW_ONLY_VIEW_JC = "View Only View JobCard";
    String SCREEN_JC_LISTING_INTIATED = "JC Initiated Listing";
    String SCREEN_JC_LISTING_IN_PROGRESS = "JC In Progress Listing";
    String SCREEN_JC_LISTING_COMPLETED = "JC Completed Listing";
    String SCREEN_JC_LISTING_CLOSED = "JC Closed Listing";
    String SCREEN_INVOICE_LISTING_PROFORMA = "Invoice Proforma Listing";
    String SCREEN_INVOICE_LISTING_INVOICED = "Invoice invoiced Listing";
    String SCREEN_INVOICE_LISTING_PAID = "Invoice Paid Listing";
    String SCREEN_INVOICE_LISTING_CANCELLED = "Invoice Cancelled Listing";
    String SCREEN_OTC_LISTING_PROFORMA = "OTC Proforma Listing";
    String SCREEN_OTC_LISTING_INVOICED = "OTC invoiced Listing";
    String SCREEN_OTC_LISTING_CANCELLED = "OTC invoiced cancelled";
    String SCREEN_APPOINTMENT_LISTING_PAST = "Appointment Past Listing";
    String SCREEN_APPOINTMENT_LISTING_TODAY = "Appointment Today  Listing";
    String SCREEN_APPOINTMENT_LISTING_UPCOMING = "Appointment Upcoming Listing";
    String SCREEN_APPOINTMENT_LISTING_CANCELLED = "Appointment Cancelled Listing";
    String SCREEN_AMC_LISTING_ACTIVE = "AMC Listing Active";
    String SCREEN_AMC_LISTING_EXPIRED = "AMC Listing Expired";
    String SCREEN_LOGIN = "Login";
    String SCREEN_READ_OTP = "ReadOtp";
    String SCREEN_CUSTOMER_CAR_SEARCH = "Customer Car Search";
    String SCREEN_ADD_VEHICLE = "add vehicle";
    String SCREEN_EDIT_VEHICLE = "edit vehicle";
    String SCREEN_VIEW_VEHICLE = "view vehicle";
    String SCREEN_ADD_CUSTOMER = "add customer";
    String SCREEN_EDIT_CUSTOMER = "edit customer";
    String SCREEN_VIEW_CUSTOMER = "view customer";
    String SCREEN_SEARCH_JOBS = "search jobs";
    String SCREEN_SEARCH_JC = "search job cards";
    String SCREEN_SEARCH_INVOICE = "search invoice cards";
    String SCREEN_SEARCH_CUSTOMER = "search My Customer";
    String SCREEN_SEARCH_PARTS = "search parts";
    String SCREEN_SEARCH_LABOUR = "search labour";
    String SCREEN_SEARCH_PART_FINDER = "search part finder";
    String SCREEN_EDIT_PROFORMA = "edit proforma";
    String SCREEN_VIEW_PDF_GATE_PASS = "PDF Gate pass";
    String SCREEN_VIEW_PDF_PROFORMA = "PDF proforma";
    String SCREEN_VIEW_PDF_INVOICE = "PDF invoice";
    String SCREEN_VIEW_PDF_JC = "PDF job card";
    String SCREEN_VIEW_PDF_ESTIMATE = "PDF estimate";
    String SCREEN_ADD_PART = "add part";
    String SCREEN_EDIT_PART = "edit part";
    String SCREEN_ADD_LABOUR = "add labour";
    String SCREEN_EDIT_LABOUR = "edit labour";
    String SCREEN_TERMS_CONDITION = "terms and condition";
    String SCREEN_WRITE_TO_US = "write to us";
    String SCREEN_PRIVACY_POLICY = "privacy policy";
    String SCREEN_ADD_APPMNT_DETAILS = "add appointment details";
    String SCREEN_ADD_APPMNT_SERVICE_PKG = "add appointment service pkg";
    String SCREEN_ADD_APPMNT_TIME_SLOT = "add appointment time slot";
    String SCREEN_MY_CUSTOMERS = "my customers";
    String SCREEN_FILTER = "filter customer";
    String SCREEN_CUSTOMER_CARD_DETAILS = "customer card details";
    String SCREEN_RESCHEDULE = "Reschedule Appointment";
    String SCREEN_REJECT = "Reject Appointment";
    String SCREEN_CANCEL_INVOICE = "Cancel Invoice";
    String SCREEN_WEBPAGE_TERMS_AND_CONDITIONS = "Terms and Conditions";
    String SCREEN_WEBPAGE_PRIVACY_POLICY = "Privacy Policy";
    String SCREEN_PART_FINDER_VEHICLE_DETAILS = "Part Finder Vehicle";
    String PART_FINDER = "Part Finder";
    String SCREEN_PRINT_PDF_ESTIMATE = "Print Estimate PDF";
    String SCREEN_EDIT_SPLIT_INVOICE = "EditSplitInvoice";
    String SCREEN_OTC_CUSTOMER = "Customer Details OTC";
    String SCREEN_OTC_PROFORMA = "OTCProforma";
    String SCREEN_ADD_PACKAGES_JC = "Add Service Packages JC";
    String SCREEN_VIEW_PDF_OTC = "OTC Proforma";


    String CATEGORY_MY_CUSTOMERS = "category_my_customer";

    void sendScreenEvent(Activity activity, String screenName, String className);

}
