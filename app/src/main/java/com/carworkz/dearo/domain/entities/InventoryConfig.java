package com.carworkz.dearo.domain.entities;

import com.squareup.moshi.Json;

/**
 * Created by ambab on 1/12/17.
 */

public class InventoryConfig {

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Json(name = "enabled")
    private boolean enabled;

    @Json(name = "mode")
    private String mode;
}
