package com.mobile.otrcapitalllc.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.mobile.otrcapitalllc.Helpers.PermissionHelper;


public class BaseActivity extends AppCompatActivity {

    private PermissionHelper mPermissionsHelper;

    protected PermissionHelper getPermissionsHelper() { return mPermissionsHelper; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPermissionsHelper = new PermissionHelper();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean proceeded = mPermissionsHelper.processRequestResult(requestCode, grantResults);
        if (!proceeded) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
