package com.mobile.otrcapital.Helpers;

import android.os.Environment;

/**
 * Created by Jawad_2 on 7/20/2015.
 */
public final class ActivityTags
{
    public static final String TAG_LOG = "OTR";
    public static final String TAG_FACTOR_LOAD = "Factor a Load";
    public static final String TAG_FACTOR_ADVANCE = "Fuel Advance";
    public static final String TAG_ACTIVITY_TYPE = "activity_type";
    public static final String EXT_STORAGE_DIR = Environment.getExternalStorageDirectory().toString() + "/OTR/docs/";
    public static final String TEMP_STORAGE_DIR = Environment.getExternalStorageDirectory().toString() + "/OTR/temp/";
    public static final String TAG_BROKER_NAME = "Broker_Name";
    public static final String TAG_LOAD_NUMBER = "Load_Number";
    public static final String TAG_INVOICE_AMOUNT = "Invoice_Amount";
    public static final String TAG_MC_NUMBER = "MC_Number";
    public static final String TAG_PKEY = "PKey";
    public static final String TAG_ADV_REQ_AMOUNT = "Advance_request_amount";
    public static final String TAG_DATE = "Date";
    public static final String TAG_FIRST_RUN = "First_Run";
    public static final String TAG_DOWNLOAD_STATUS = "Download_Status";
    public static final String API_URL = "http://customer.otrcapital.com/api";
    public static final String HOST_NAME = "customer.otrcapital.com";
    public static final String API_URL_PROD = "http://customer.otrcapital.com/api";
    public static final String LOGIN_TOKEN = "login_token";
    //shared prefs tags
    public static final String SHARED_PREFS_TAG = "OTR_Prefs";
    public static final String PREFS_TOKEN_VALID = "token_valid";
    public static final String PREFS_USER_EMAIL = "user_email";
    public static final String PREFS_USER_PASSWORD = "user_password";
    public static final String PREFS_USER_CREDENTIALS = "user_credentials";
    public static final String PREFS_NOT_FIRST_RUN = "first_run";
    //file upload tags
    public static final int FILE_LOAD_NUMBER = 0;   //po number
    public static final int FILE_INVOICE_AMOUNT = 1;
    public static final int FILE_MC_NUMBER = 2;
    public static final int FILE_DOCUMENT_TYPES = 3;
    public static final int FILE_PKEY = 4;
    public static final int FILE_STATUS = 5;
    public static final int FILE_TIMESTAMP = 6;
    public static final int FILE_FACTOR_TYPE = 7;
    public static final int FILE_ADV_REQ_AMOUNT = 8;

    private ActivityTags() {
    }


}
