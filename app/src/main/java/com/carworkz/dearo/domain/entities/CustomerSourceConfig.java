package com.carworkz.dearo.domain.entities;

import com.squareup.moshi.Json;

import java.util.List;

public class CustomerSourceConfig {

    @Json(name ="enabled")
    private Boolean enabled;

    @Json(name ="options")
    private List<String> options;

//    public Boolean getEnabled() {
//        return enabled;
//    }
//
//    public void setEnabled(Boolean enabled) {
//        this.enabled = enabled;
//    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
