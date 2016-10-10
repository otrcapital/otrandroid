package com.mobile.otrcapitalllc.Helpers;

import android.os.Environment;

/**
 * Created by Jawad_2 on 7/20/2015.
 */
public final class ActivityTags
{
    public enum TAKE_PHOTO_TYPE {
        CAMERA,
        GALLERY
    }

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
    public static final String HOST_NAME_PROD = "customer.otrcapital.com";
    public static final String HOST_NAME_STAGE = "stgportal.otrcapital.com";
    public static final String API_URL_PROD = "http://customer.otrcapital.com/api";
    public static final String API_URL_STAGE = "http://stgportal.otrcapital.com/api";
    public static final String API_URL_MOCK = "http://192.168.13.15/FactorHawkPortal/api";
    public static final String LOGIN_TOKEN = "login_token";
    public static final String TAG_PHOTO_TYPE = "photo_type";
    public static final String TAG_PAYMENT_OPTION = "payment_option";
    public static final String TAG_CELL_NUMBER = "cell_number";

    private ActivityTags() {
    }


}
