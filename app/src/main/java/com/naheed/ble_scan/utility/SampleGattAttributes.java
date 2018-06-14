package com.naheed.ble_scan.utility;

import java.util.HashMap;

/**
 * Created by Naheed on 6/13/2018.
 */
public class SampleGattAttributes {

    private static HashMap<String, String> attributes = new HashMap();

    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }

    static {
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
    }


}
