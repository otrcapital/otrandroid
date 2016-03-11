package com.mobile.otrcapitalllc.Helpers;

import com.mobile.otrcapitalllc.BuildConfig;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.client.OkClient;

public class RestClient {

    private RESTAPIs mRESTAPIs;
    private String mUserCredentials;
    private String mHostName;

    public static volatile RestClient instance;

    public static RestClient getInstance(String userCredentials) {
        RestClient localInstance = instance;
        // check if credintials not equal
        if (localInstance != null && localInstance.getUserCredentials() != null
                && !localInstance.getUserCredentials().equals(userCredentials)) {
            localInstance = null;
        }
        if (localInstance == null) {
            synchronized (RestClient.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new RestClient(userCredentials);
                }
            }
        }
        return localInstance;
    }

    public RestClient(String userCredentials) {
        String endpointUrl = ActivityTags.API_URL_PROD;
        mHostName = ActivityTags.HOST_NAME_PROD;
        mUserCredentials = userCredentials;

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(endpointUrl)
                .setLog(new AndroidLog("RetrofitLog"))
                .setClient(new OkClient(okHttpClient))
                .setRequestInterceptor(requestInterceptor)
                .build();

        if (BuildConfig.DEBUG) {
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        } else {
            restAdapter.setLogLevel(RestAdapter.LogLevel.NONE);
        }
        mRESTAPIs = restAdapter.create(RESTAPIs.class);
    }

    RequestInterceptor requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("User-Agent", "Fiddler");
            request.addHeader("Host", mHostName);
            if (mUserCredentials != null) request.addHeader("Authorization", "Basic " + mUserCredentials);
        }
    };

    public RESTAPIs getApiService() {
        return mRESTAPIs;
    }

    public String getUserCredentials() {
        return mUserCredentials;
    }

    public void clear() {
        instance = null;
    }
}
