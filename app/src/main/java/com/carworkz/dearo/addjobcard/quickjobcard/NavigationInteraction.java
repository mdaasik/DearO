package com.carworkz.dearo.addjobcard.quickjobcard;

public interface NavigationInteraction {

    String SCREEN_VOICE = "screen_voice";
    String SCREEN_INVENTORY = "screen_inventory";
    String SCREEN_INSPECTION = "screen_inspection";
    String SCREEN_DAMAGES = "screen_damages";
    String SCREEN_INSURANCE = "screen_insurance";
    String SCREEN_ESTIMATE = "screen_estimate";
    String SCREEN_PROFORMA = "screen_proforma";
    String SCREEN_PDF = "screen_pdf";
    String SCREEN_VIEW_JC = "screen_view_jc";
    String SCREEN_NONE = "screen_nothing";
    String SCREEN_EXIT = "screen_exit";

    void onStoryClick(String screenToStart);
}
