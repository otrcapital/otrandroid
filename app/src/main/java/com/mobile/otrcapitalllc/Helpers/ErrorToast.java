package com.mobile.otrcapitalllc.Helpers;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

public class ErrorToast {

    private static Toast mToast;

    public static void show(Context context, @StringRes int text, int duration) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(context, text, duration);
        mToast.show();
    }
}
