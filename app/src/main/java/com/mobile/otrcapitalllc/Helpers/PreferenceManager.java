package com.mobile.otrcapitalllc.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private static final String SHARED_PREFS_TAG = "OTR_Prefs";
    private static final String PREFS_TOKEN_VALID = "token_valid";
    private static final String PREFS_USER_EMAIL = "user_email";
    private static final String PREFS_USER_PASSWORD = "user_password";
    private static final String PREFS_USER_CREDENTIALS = "user_credentials";
    private static final String PREFS_NOT_FIRST_RUN = "first_run";
    private static final String PREFS_DB_TIMESTAMP = "db_timestamp";

    private SharedPreferences mSharedPreferences;

    private static volatile PreferenceManager instance;

    public static PreferenceManager with(Context context) {
        PreferenceManager localInstance = instance;
        if (localInstance == null) {
            synchronized (RestClient.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new PreferenceManager(context);
                }
            }
        }
        return localInstance;
    }

    private PreferenceManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(SHARED_PREFS_TAG, Context.MODE_PRIVATE);
    }

    public void saveDbUpdateTimestamp(long timestamp) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(PREFS_DB_TIMESTAMP, timestamp);
        editor.apply();
    }

    public long getDbUpdateTimestamp() {
        return mSharedPreferences.getLong(PREFS_DB_TIMESTAMP, 0);
    }

    public void saveStringWithKey(String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getStringWithKey(String key) {
        return mSharedPreferences.getString(key, null);
    }

    public void saveUserData(String email, String password, String credentials, boolean isValidToken) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PREFS_USER_EMAIL, email);
        editor.putString(PREFS_USER_PASSWORD, password);
        editor.putBoolean(PREFS_TOKEN_VALID, isValidToken);
        editor.putString(PREFS_USER_CREDENTIALS, credentials);
        editor.apply();
    }

    public void saveTokenValid(boolean isValidToken) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(PREFS_TOKEN_VALID, isValidToken);
        editor.apply();
    }

    public void setFirstRun() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(PREFS_NOT_FIRST_RUN, false);
        editor.apply();
    }

    public String getUserEmail() {
        return mSharedPreferences.getString(PREFS_USER_EMAIL, null);
    }

    public String getUserPassword() {
        return mSharedPreferences.getString(PREFS_USER_PASSWORD, null);
    }

    public String getUserCredentials() {
        return mSharedPreferences.getString(PREFS_USER_CREDENTIALS, null);
    }

    public boolean getTokenValid() {
        return mSharedPreferences.getBoolean(PREFS_TOKEN_VALID, false);
    }

    public boolean getFirstRun() {
        return mSharedPreferences.getBoolean(PREFS_NOT_FIRST_RUN, true);
    }
}
