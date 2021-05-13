package com.tony.android.bluetoothlegatt;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();

//    public static String UUID_DEVICE_SERVICE = "0000180d-0000-1000-8000-00805f9b34fb";

//    public static String UUID_DEVICE_SEC = "00002a37-0000-1000-8000-00805f9b34fb";
//    public static String UUID_DEVICE_ID = "00002a38-0000-1000-8000-00805f9b34fb";
//    public static String UUID_DEVICE_ON =  "00002a39-0000-1000-8000-00805f9b34fb";
//    public static String UUID_DEVICE_SLEEP = "00002a3A-0000-1000-8000-00805f9b34fb";

    public static String HEART_SERVICE = "0000180d-0000-1000-8000-00805f9b34fb";
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";


    public static String UUID_GATT_SERVICE = "facade00-8ed3-4bdf-8a39-a01bebede295";
    public static String UUID_SEC_CHAR = "facade01-8ed3-4bdf-8a39-a01bebede295";
    public static String UUID_ID_CHAR = "facade02-8ed3-4bdf-8a39-a01bebede295";
    public static String UUID_ON_CHAR =  "facade03-8ed3-4bdf-8a39-a01bebede295";
    public static String UUID_SLEEP_CHAR = "facade04-8ed3-4bdf-8a39-a01bebede295";

    public static String SEC_CHAR_NAME = "SEC Characteristic";
    public static String ID_CHAR_NAME = "ID Characteristic";
    public static String ON_CHAR_NAME = "ON Characteristic";
    public static String SLEEP_CHAR_NAME = "ID Characteristic";



    static {
        // Sample Services.
        attributes.put("00001800-0000-1000-8000-00805f9b34fb", "Test service");

        attributes.put(UUID_GATT_SERVICE, "Gatt Service");
        attributes.put(UUID_SEC_CHAR, SEC_CHAR_NAME);
        attributes.put(UUID_ID_CHAR, ID_CHAR_NAME);
        attributes.put(UUID_ON_CHAR, ON_CHAR_NAME);
        attributes.put(UUID_SLEEP_CHAR, SLEEP_CHAR_NAME);

        // Sample Services.
        attributes.put(HEART_SERVICE, "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }

    /*
    * Search sec, id
    * */
    public static boolean lookup(String uuid){
         if(uuid.equals(HEART_RATE_MEASUREMENT)
            || uuid.equals("00002a29-0000-1000-8000-00805f9b34fb")
            || uuid.equals(UUID_SEC_CHAR)
            || uuid.equals(UUID_ID_CHAR)){
             return true;
         }else{
             return false;
         }
    }
}
