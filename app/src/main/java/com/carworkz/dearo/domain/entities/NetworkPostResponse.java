package com.carworkz.dearo.domain.entities;

import com.squareup.moshi.Json;

/**
 * Created by ambab on 19/8/17.
 */

public class
NetworkPostResponse {

    @Json(name = "where")
    private String status;
    @Json(name = "message")
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
