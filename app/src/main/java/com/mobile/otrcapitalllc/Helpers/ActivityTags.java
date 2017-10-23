package com.mobile.otrcapitalllc.Helpers;

import android.os.Environment;

public final class ActivityTags {
    public enum TAKE_PHOTO_TYPE {
        CAMERA,
        GALLERY
    }

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

    public static final String TAG_PHOTO_TYPE = "photo_type";
    public static final String TAG_PAYMENT_OPTION = "payment_option";
    public static final String TAG_CELL_NUMBER = "cell_number";

    private ActivityTags() {
    }
}

