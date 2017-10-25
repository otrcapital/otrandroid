package com.mobile.otrcapitalllc.Helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionHelper {

    public static final int PERMISSION_REQUEST_ACCESS_STORAGE = 3;
    public static final int PERMISSION_REQUEST_ACCESS_CAMERA = 4;

    private RequestResultListener mListener;

    public static boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void checkStoragePermissions(RequestResultListener listener, Activity activity) {
        this.mListener = listener;
        if (!hasPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if (listener != null) {
                    listener.onShouldShowPermissionRationale();
                }
            } else {
                requestStoragePermissions(activity);
            }
        }
    }

    public void checkCameraPermissions(RequestResultListener listener, Activity activity) {
        this.mListener = listener;
        if (!hasPermission(activity, Manifest.permission.CAMERA)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                if (listener != null) {
                    listener.onShouldShowPermissionRationale();
                }
            } else {
                requestCameraPermissions(activity);
            }
        }
    }

    public void requestStoragePermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_ACCESS_STORAGE);
        }
    }

    public void requestCameraPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_ACCESS_CAMERA);
    }

    public boolean processRequestResult(int requestCode, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_ACCESS_STORAGE || requestCode == PERMISSION_REQUEST_ACCESS_CAMERA) {

            boolean permissionGranted = grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (mListener != null) {
                mListener.onRequestResult(permissionGranted);
            }
            return true;
        }
        return false;
    }

    public interface RequestResultListener {
        void onShouldShowPermissionRationale();

        void onRequestResult(boolean granted);
    }
}
