package com.mobile.otrcapitalllc;

import android.app.Application;
import android.os.StrictMode;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class OTRCapital extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //@formatter:off
        final Fabric fabric = new Fabric.Builder(this)
            .kits(new Crashlytics())
            .debuggable(BuildConfig.DEBUG)
            .build();
        //@formatter:on

        if (!BuildConfig.DEBUG) {
            Fabric.with(fabric);
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }
}
