package com.mobile.otrcapitalllc.Services;

import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class FirebaseInstanceIdentifierService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseInstanceIdentifierService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        sendRegistrationToServer(refreshedToken);
    }


    private void sendRegistrationToServer(String token) {}
}
