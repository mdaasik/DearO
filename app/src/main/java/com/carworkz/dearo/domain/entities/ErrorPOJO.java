
package com.carworkz.dearo.domain.entities;

import com.squareup.moshi.Json;

public class ErrorPOJO{

    @Json(name = "error")
    private Error error;

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

}
