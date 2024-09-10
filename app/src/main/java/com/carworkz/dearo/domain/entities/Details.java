
package com.carworkz.dearo.domain.entities;

import com.squareup.moshi.Json;

public class Details {

    @Json(name = "messages")
    private Messages messages = new Messages();

    public Messages getMessages() {
        return messages;
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }

}
