package com.carworkz.dearo.events;

import javax.annotation.Nullable;

import timber.log.Timber;

public class AppointmentReassignEvent {

    private String appointmentId;

    @Nullable
    private String selectedServiceAdvisorId;

    public AppointmentReassignEvent(String appointmentId, @Nullable String selectedServiceAdvisorId) {
        this.appointmentId = appointmentId;
        this.selectedServiceAdvisorId = selectedServiceAdvisorId;
        Timber.d("Setting appointment id " + appointmentId);
        Timber.d("Setting advisor id " + selectedServiceAdvisorId);
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    @Nullable
    public String getSelectedServiceAdvisorId() {
        return selectedServiceAdvisorId;
    }
}
