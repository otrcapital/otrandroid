package com.mobile.otrcapitalllc.Helpers;

import android.content.Context;

import com.mobile.otrcapitalllc.BuildConfig;
import com.mobile.otrcapitalllc.R;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.client.OkClient;

public class RestClient {

    private RESTAPIs mRESTAPIs;
    private String mUserCredentials;
    private Context mContext;

    public RestClient(Context context, String userCredentials) {
        mUserCredentials = userCredentials;
        mContext = context;
        String apiUrl = context.getString(R.string.WebSiteAPI);
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(60, TimeUnit.SECONDS);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(apiUrl)
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
            request.addHeader("Host", mContext.getString(R.string.WebSiteHost));
            if (mUserCredentials != null)
                request.addHeader("Authorization", "Basic " + mUserCredentials);
        }
    };

    public RESTAPIs getApiService() {
        return mRESTAPIs;
    }

    public String getUserCredentials() {
        return mUserCredentials;
    }
}
