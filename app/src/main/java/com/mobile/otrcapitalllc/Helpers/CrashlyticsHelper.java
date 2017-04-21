package com.mobile.otrcapitalllc.Helpers;

import com.crashlytics.android.Crashlytics;
import com.mobile.otrcapitalllc.Activities.LoginScreen.AgentViewModel;

import io.fabric.sdk.android.Fabric;

public class CrashlyticsHelper {

    public static void setUser(AgentViewModel agentViewModel) {
        if(!Fabric.isInitialized()) return;

        Crashlytics.setUserIdentifier(agentViewModel.ClientId);
        Crashlytics.setUserName(agentViewModel.Login);
    }

    public static void setUserEmail(String email) {
        if(!Fabric.isInitialized()) return;

        Crashlytics.setUserEmail(email);
    }

    public static void logException(Exception ex) {
        if(!Fabric.isInitialized()) return;

        Crashlytics.logException(ex);
    }
}
