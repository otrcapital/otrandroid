package com.mobile.otrcapitalllc.Helpers;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.mobile.otrcapitalllc.R;

public class RemoteConfigManager {

    public static void initDefaults() {
        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .build();

        firebaseRemoteConfig.setConfigSettings(configSettings);
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
    }

    public static void fetchDefaults(final Activity activity) {
//        long cacheExpiration = 3600;
//        FirebaseRemoteConfig.getInstance().fetch(cacheExpiration)
//                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
//
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            FirebaseRemoteConfig.getInstance().activateFetched();
//                        }
//                    }});
    }


    public static String getContactPhoneNumber() {
        return FirebaseRemoteConfig.getInstance().getString("tel_contact");
    }

    public static String getFormattedContactPhoneNumber() {
        String number = RemoteConfigManager.getContactPhoneNumber();

        number = number.replace("-", "");
        number = number.replace(")", "");
        number = number.replace("(", "");
        return "tel:" + number;
    }

    public static String getContactFaxNumber() {
        return FirebaseRemoteConfig.getInstance().getString("tel_fax");
    }

    public static String getContactEmail() {
        return FirebaseRemoteConfig.getInstance().getString("contact_email");
    }

    public static String getContactAddress() {
        return FirebaseRemoteConfig.getInstance().getString("contact_address");
    }

    //region URLs

    public static String getSignUpURL() {
        return FirebaseRemoteConfig.getInstance().getString("url_sign_up");
    }

    public static String getFacebookURL() {
        return FirebaseRemoteConfig.getInstance().getString("url_facebook");
    }

    public static String getGooglePlusURL() {
        return FirebaseRemoteConfig.getInstance().getString("url_google_plus");
    }

    public static String getTwitterURL() {
        return FirebaseRemoteConfig.getInstance().getString("url_twitter");
    }

    public static String getInstagramURL() {
        return FirebaseRemoteConfig.getInstance().getString("url_instagram");
    }

    public static String getLinkedinURL() {
        return FirebaseRemoteConfig.getInstance().getString("url_linkedin");
    }

    //endregion
}