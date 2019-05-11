package com.grabid.models;

/**
 * Created by Lenovo on 2/10/2018.
 */

public class PreviewField {
    public static String DeliveryName, DeliverySubType, DeliverySubSubType, suiotableVehicles;
    public static String PickUpLiftingEquipmentAvIds, DropOffLiftingEquipmentAvIds, PickUpLiftingEquipmentNeededIds, DropOffLiftingEquipmentNeddedIds;

    public static String getDeliveryName() {
        return DeliveryName;
    }

    public static String getSuiotableVehicles() {
        return suiotableVehicles;
    }

    public static String getPickUpLiftingEquipmentAvIds() {
        return PickUpLiftingEquipmentAvIds;
    }

    public static void setPickUpLiftingEquipmentAvIds(String pickUpLiftingEquipmentAvIds) {
        PickUpLiftingEquipmentAvIds = pickUpLiftingEquipmentAvIds;
    }

    public static String getDropOffLiftingEquipmentAvIds() {
        return DropOffLiftingEquipmentAvIds;
    }

    public static void setDropOffLiftingEquipmentAvIds(String dropOffLiftingEquipmentAvIds) {
        DropOffLiftingEquipmentAvIds = dropOffLiftingEquipmentAvIds;
    }

    public static String getPickUpLiftingEquipmentNeededIds() {
        return PickUpLiftingEquipmentNeededIds;
    }

    public static void setPickUpLiftingEquipmentNeededIds(String pickUpLiftingEquipmentNeededIds) {
        PickUpLiftingEquipmentNeededIds = pickUpLiftingEquipmentNeededIds;
    }

    public static String getDropOffLiftingEquipmentNeddedIds() {
        return DropOffLiftingEquipmentNeddedIds;
    }

    public static void setDropOffLiftingEquipmentNeddedIds(String dropOffLiftingEquipmentNeddedIds) {
        DropOffLiftingEquipmentNeddedIds = dropOffLiftingEquipmentNeddedIds;
    }

    public static void setSuiotableVehicles(String suiotableVehicles) {
        PreviewField.suiotableVehicles = suiotableVehicles;

    }

    public static void setDeliveryName(String deliveryName) {

        DeliveryName = deliveryName;
    }

    public static String getDeliverySubType() {
        return DeliverySubType;
    }

    public static void setDeliverySubType(String deliverySubType) {
        DeliverySubType = deliverySubType;
    }

    public static String getDeliverySubSubType() {
        return DeliverySubSubType;
    }

    public static void setDeliverySubSubType(String deliverySubSubType) {
        DeliverySubSubType = deliverySubSubType;
    }
}
