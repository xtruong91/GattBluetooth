package com.tony.android.bluetoothlegatt;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();


    public static String MAC_ADDRESS = "C3:2C:42:29:97:2B";
    public static String GATT_SERVICE = "bd8522d8-30e9-49d4-6c8d-04b58f529834";
    public static String GATT_CHARACTERISTIC = "bd851524-30e9-49d4-6c8d-04b58f529834";




    static {
        // Sample Services.
        attributes.put(GATT_SERVICE, "GATT Service");
        attributes.put(GATT_CHARACTERISTIC, "Sensor Data");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }

    /*
    * Search sec, id
    * */
    public static boolean lookup(String uuid){
         if(uuid.equals(GATT_SERVICE)
            || uuid.equals(GATT_CHARACTERISTIC)){
             return true;
         }else{
             return false;
         }
    }
}
