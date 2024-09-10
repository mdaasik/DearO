package com.carworkz.dearo.domain.entities;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by kush on 19/8/17.
 */

public class CustomerVehicleSearch {

    private int historyCount;
    private int otherServiceHistoryCount;

    @Json(name = "customer")
    private Customer customer;
    @Json(name = "vehicle")
    private Vehicle vehicle;
    @Json(name = "decision")
    private Decision decision;

    @Json(name = "vehicleAmc")
    private List<AMC> amc;

    @Json(name = "history")
    private List<History> history;

    @Json(name = "customerVehicleId")
    private String customerVehicleId;
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    public List<History> getHistory() {
        return history;
    }

    public void setHistory(List<History> history) {
        this.history = history;
    }
    public int getHistoryCount() {
        return historyCount;
    }

    public void setHistoryCount(int historyCount) {
        this.historyCount = historyCount;
    }

    public int getOtherServiceHistoryCount() {
        return otherServiceHistoryCount;
    }

    public void setOtherServiceHistoryCount(int otherServiceHistoryCount) {
        this.otherServiceHistoryCount = otherServiceHistoryCount;
    }

    public String getCustomerVehicleId() {
        return customerVehicleId;
    }

    public void setCustomerVehicleId(String customerVehicleId) {
        this.customerVehicleId = customerVehicleId;
    }

    public List<AMC> getAmc() {
        return amc;
    }

    public void setAmc(List<AMC> amc) {
        this.amc = amc;
    }
}
