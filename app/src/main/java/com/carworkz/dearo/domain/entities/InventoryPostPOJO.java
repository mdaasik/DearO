package com.carworkz.dearo.domain.entities;

import com.carworkz.dearo.utils.Constants;
import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by ambab on 21/8/17.
 */

@SuppressWarnings("CanBeFinal")
public class InventoryPostPOJO {
    @Json(name = Constants.ApiConstants.KEY_JOB_CARD_INVENTORY_SERVICE_TYPE)
    private String serviceType;

    @Json(name = Constants.ApiConstants.KEY_JOB_CARD_INVENTORY_REMARKS)
    private String remarks;

    @Json(name = Constants.ApiConstants.KEY_JOB_CARD_INVENTORY_FUEL_READING)
    private float fuelReading;

    @Json(name = Constants.ApiConstants.KEY_JOB_CARD_INVENTORY_KMS_READING)
    private int kmsReading;

    @Json(name = Constants.ApiConstants.KEY_JOB_CARD_INVENTORY_LIST)
    private List<Inventory> inventoryList;

    public InventoryPostPOJO(String serviceType, String remarks, float fuelReading, int kmsReading, List<Inventory> inventoryList) {
        this.serviceType = serviceType;
        this.remarks = remarks;
        this.fuelReading = fuelReading;
        this.kmsReading = kmsReading;
        this.inventoryList = inventoryList;
    }
}
