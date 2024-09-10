
package com.carworkz.dearo.domain.entities;

import com.squareup.moshi.Json;

public class Token {

    @Json(name = "id")
    private String id;
    @Json(name = "ttl")
    private Integer ttl;
    @Json(name = "created")
    private String created;
    @Json(name = "userId")
    private String userId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
