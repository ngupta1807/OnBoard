package com.grabid.models;

/**
 * Created by graycell on 28/8/17.
 */

public class DeliveryUpdate {
    public static String DeliveryId;

    public static String getDeliveryId() {
        return DeliveryId;
    }

    public static void setDeliveryId(String deliveryId) {
        DeliveryId = deliveryId;
    }

    public static boolean IsFirstUpdate;

    public static boolean isFirstUpdate() {
        return IsFirstUpdate;
    }

    public static void setIsFirstUpdate(boolean isFirstUpdate) {
        IsFirstUpdate = isFirstUpdate;
    }
}
