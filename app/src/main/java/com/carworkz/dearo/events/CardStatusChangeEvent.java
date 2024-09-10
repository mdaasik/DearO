package com.carworkz.dearo.events;

/**
 * Created by ambab on 13/10/17.
 */

public final  class CardStatusChangeEvent {

    /*This event is used to update app to new status. Note: should not be used to make network calls*/

    public static final String CARD_TYPE_INVOICE = "card_invoice";
    public static final String CARD_TYPE_JOB_CARD = "card_job_card";
    public static final String CARD_TYPE_APPOINTMENT = "card_appointment";
    public static final String CARD_TYPE_OTC = "card_otc";

    public static final String CARD_STATUS_JOB_CARD_INITAITED = "jc_initiate";
    public static final String CARD_STATUS_JOB_CARD_IN_PROGRESS = "jc_in_progress";
    public static final String CARD_STATUS_JOB_CARD_COMPLETED = "jc_completed";
    public static final String CARD_STATUS_JOB_CARD_CLOSED = "jc_closed";

    public static final String CARD_STATUS_INVOICED= "invoiced";
    public static final String CARD_STATUS_PROFORMA = "invoice_proforma";
    public static final String CARD_STATUS_PAID = "invoice_paid";
    public static final String CARD_STATUS_CANCELLED = "invoice_cancelled";

    public static final String CARD_STATUS_OTC_INVOICED = "otc_invoiced";
    public static final String CARD_STATUS_OTC_PROFORMA = "otc_proforma";
    public static final String CARD_STATUS_OTC_CANCELLED = "otc_cancelled";

    public static final String CARD_STATUS_APPOINTMENT_REQUESTED = "appointment_requested";
    public static final String CARD_STATUS_APPOINTMENT_PAST = "appointment_past";
    public static final String CARD_STATUS_APPOINTMENT_TODAY = "appointment_today";
    public static final String CARD_STATUS_APPOINTMENT_UPCOMING = "appointment_upcoming";
    public static final String CARD_STATUS_APPOINTMENT_CANCELLED = "appointment_cancelled";

    private final String cardType;
    private final String cardStatus;

    public CardStatusChangeEvent(String cardType, String cardStatus) {
        this.cardType = cardType;
        this.cardStatus = cardStatus;
    }

    public String getCardType() {
        return cardType;
    }

    public String getCardStatus() {
        return cardStatus;
    }
}
