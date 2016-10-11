package com.mobile.otrcapitalllc.Helpers;

import android.util.Log;

import com.mobile.otrcapitalllc.BuildConfig;

public class LogHelper {

    public static final String TAG_LOG = "OTRCapital";

    public static void logDebug(String message) {
        logDebug(TAG_LOG, message);
    }

    public static void logDebug(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void logError(String message) {
        logError(TAG_LOG, message);
    }

    public static void logError(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message);
        }
    }
}
