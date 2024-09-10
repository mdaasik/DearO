package com.carworkz.dearo.events;

/**
 * Created by ambab on 29/11/17.
 */

public final class ChangeCardStatusEvent {

    private final String cardId;
    private String cardStatus;
    private String paymentType;
    private String method;
    private String amount;


    public ChangeCardStatusEvent(String cardId, String cardStatus) {
        this.cardId = cardId;
        this.cardStatus = cardStatus;
    }

    public ChangeCardStatusEvent(String cardId, String paymentType, String method, String amount) {
        this.cardId = cardId;
        this.paymentType = paymentType;
        this.method = method;
        this.amount = amount;
    }

    public String getCardId() {
        return cardId;
    }

    public String getCardStatus() {
        return cardStatus;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public String getMethod() {
        return method;
    }

    public String getAmount() {
        return amount;
    }

}
