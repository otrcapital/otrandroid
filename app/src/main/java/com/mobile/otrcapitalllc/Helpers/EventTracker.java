package com.mobile.otrcapitalllc.Helpers;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.LoginEvent;

public class EventTracker {

    public static void trackUserLogin(String email, boolean success) {
        Answers.getInstance().logLogin(new LoginEvent()
            .putMethod("GetClientInfo")
            .putSuccess(success)
            .putCustomAttribute("User's email", email));
    }
}
