package com.carworkz.dearo.domain.entities;

/*Created by ambab on 26/10/17.*/

import com.squareup.moshi.Json;

public class AppUpdate {

    @Json(name = "id")
    private String id;
    @Json(name = "type")
    private String type;
    @Json(name = "platform")
    private String platform;
    @Json(name = "versionName")
    private String versionName;
    @Json(name = "versionCode")
    private Integer versionCode;
    @Json(name = "minVersionCode")
    private Integer minVersionCode;
    @Json(name = "link")
    private String link;
    @Json(name = "forceText")
    private String forceText;
    @Json(name = "flexibleText")
    private String flexibleText;
    @Json(name = "createdOn")
    private String createdOn;
    @Json(name = "updatedOn")
    private String updatedOn;
    @Json(name = "forceUpdate")
    private Boolean forceUpdate;
    @Json(name = "updateAvailable")
    private Boolean updateAvailable;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Integer getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }

    public Integer getMinVersionCode() {
        return minVersionCode;
    }

    public void setMinVersionCode(Integer minVersionCode) {
        this.minVersionCode = minVersionCode;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getForceText() {
        return forceText;
    }

    public void setForceText(String forceText) {
        this.forceText = forceText;
    }

    public String getFlexibleText() {
        return flexibleText;
    }

    public void setFlexibleText(String flexibleText) {
        this.flexibleText = flexibleText;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Boolean getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(Boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public Boolean getUpdateAvailable() {
        return updateAvailable;
    }

    public void setUpdateAvailable(Boolean updateAvailable) {
        this.updateAvailable = updateAvailable;
    }

}