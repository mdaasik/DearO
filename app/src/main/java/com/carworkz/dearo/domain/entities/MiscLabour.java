package com.carworkz.dearo.domain.entities;

import com.squareup.moshi.Json;

/**
 * Created by Kush Singh Chibber on 12/18/2017.
 */

public class MiscLabour {

    @Json(name = "enabled")
    private Boolean enabled;

    @Json(name = "maxPrice")
    private Integer maxPrice;

    @Json(name = "id")
    private String id;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Status : " + getEnabled() +
                ",Max Price : " + getMaxPrice() +
                ",id : " + getId();
    }
}
