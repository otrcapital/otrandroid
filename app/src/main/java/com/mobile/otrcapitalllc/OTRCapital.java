package com.mobile.otrcapitalllc;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            createNotificationChannels();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannels() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String notificationChannelId = getString(R.string.notification_channel_id);
        CharSequence notificationChannelName = getString(R.string.notification_channel_title);
        int notificationImportance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId, notificationChannelName, notificationImportance);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(notificationChannel);
    }


}
