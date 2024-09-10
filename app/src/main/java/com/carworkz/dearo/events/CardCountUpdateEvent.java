package com.carworkz.dearo.events;

/**
 * Created by farhan on 05/01/18.
 */

public class CardCountUpdateEvent {


    public static final String CARD_STATUS_AMC_ACTIVE = "amc_active";
    public static final String CARD_STATUS_AMC_EXPIRED = "amc_expired";

    public static final String CARD_STATUS_JOB_CARD_INITAITED = "jc_initiate";
    public static final String CARD_STATUS_JOB_CARD_IN_PROGRESS = "jc_in_progress";
    public static final String CARD_STATUS_JOB_CARD_COMPLETED = "jc_completed";
    public static final String CARD_STATUS_JOB_CARD_CLOSED = "jc_closed";

    public static final String CARD_STATUS_INVOICED= "invoiced";
    public static final String CARD_STATUS_PROFORMA = "invoice_proforma";
    public static final String CARD_STATUS_PAID = "invoice_paid";
    public static final String CARD_STATUS_INVOICE_CANCELLED = "invoice_cancelled";

    public static final String CARD_STATUS_OTC_INVOICED = "otc_invoiced";
    public static final String CARD_STATUS_OTC_PROFORMA = "otc_proforma";
    public static final String CARD_STATUS_OTC_CANCELLED = "otc_cancelled";

    public static final String CARD_STATUS_APPOINTMENT_PAST = "appointment_past";
    public static final String CARD_STATUS_APPOINTMENT_TODAY = "appointment_today";
    public static final String CARD_STATUS_APPOINTMENT_UPCOMING = "appointment_upcoming";
    public static final String CARD_STATUS_APPOINTMENT_REQUESTED = "appointment_requested";
    public static final String CARD_STATUS_APPOINTMENT_CANCELLED = "appointment_cancelled";

    private final String cardType;
    private final int count;

    public CardCountUpdateEvent(String cardType, int count) {
        this.cardType = cardType;
        this.count = count;
    }

    public String getCardType() {
        return cardType;
    }

    public int getCardCount() {
        return count;
    }
}
