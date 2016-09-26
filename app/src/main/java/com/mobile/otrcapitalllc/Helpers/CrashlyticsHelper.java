package com.mobile.otrcapitalllc.Helpers;

import com.crashlytics.android.Crashlytics;
import com.mobile.otrcapitalllc.Activities.LoginScreen.AgentViewModel;

public class CrashlyticsHelper {

    public static void setUser(AgentViewModel agentViewModel) {
        Crashlytics.setUserIdentifier(agentViewModel.ClientId);
        Crashlytics.setUserName(agentViewModel.Login);
    }

    public static void setUserEmail(String email) {
        Crashlytics.setUserEmail(email);
    }

    public static void logException(Exception ex) {
        Crashlytics.logException(ex);
    }
}
