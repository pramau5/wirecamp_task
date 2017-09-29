package com.example.wirecamp.activity;

import android.os.Build;

/**
 * Created by Pramod on 27-09-2017.
 */
public class Deploy {

    //private static final String _SIMULATOR_SERVER_URL = "http://192.168.1.14:8080";
    //private static final String _PHONE_SERVER_URL = "http://192.168.1.14:8080";
    private static final String _SIMULATOR_SERVER_URL = "http://api.openweathermap.org";
    private static final String _PHONE_SERVER_URL = "http://api.openweathermap.org";

    public static String getServerUrl() {
        Boolean isEmulator = Build.HARDWARE.contains("goldfish");
        if (isEmulator) {
            return _SIMULATOR_SERVER_URL;
        }
        return _PHONE_SERVER_URL;
    }
}