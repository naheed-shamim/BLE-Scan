package com.naheed.ble_scan.ble;

import java.util.HashMap;

/**
 * Created by Naheed on 6/13/2018.
 */
public class SampleGattAttributes {

    private static HashMap<String, String> attributes = new HashMap();


    // UUIDs for Service
    public static final String UUID_GENERIC_ACCESS = "00001800-0000-1000-8000-00805f9b34fb";
    public static final String UUID_Alert_Notification_Service = "00001811-0000-1000-8000-00805f9b34fb";
    public static final String UUID_Automation_IO = "00001815-0000-1000-8000-00805f9b34fb";

    public static final String UUID_BATTERY_SERVICE = "0000180f-0000-1000-8000-00805f9b34fb";

    // UUIDs for characteristics
    public static final String UUID_BATTERY_LEVEL_CHARACTERISTIC = "00002a19-0000-1000-8000-00805f9b34fb";


    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    public static String lookup(String uuid, String defaultName) {

        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }

    static {
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put(UUID_BATTERY_LEVEL_CHARACTERISTIC, "Battery Level");
        attributes.put(UUID_BATTERY_SERVICE, "Battery Service");
        attributes.put(UUID_GENERIC_ACCESS, "Generic Access Service");
        attributes.put(UUID_Alert_Notification_Service, "Alert Notification Service");
        attributes.put(UUID_Automation_IO, "AutomationIO Service");


    }

}
