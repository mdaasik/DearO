package com.carworkz.dearo.domain.entities;

import com.squareup.moshi.Json;

public class Decision {

    @Json(name = "customer")
    private String customer;
    @Json(name = "vehicle")
    private String vehicle;

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }


}
