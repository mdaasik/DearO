
package com.carworkz.dearo.domain.entities;

import java.util.List;
import java.util.Map;

import com.squareup.moshi.Json;

public class Messages {

    @Json(name = "error")
    private List<String> error = null;

    private Map<String,List<String>> errorMessages;


    public List<String> getError() {
        return error;
    }

    public void setError(List<String> error) {
        this.error = error;
    }

    public Map<String, List<String>> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(Map<String, List<String>> errorMessages) {
        this.errorMessages = errorMessages;
    }
}
