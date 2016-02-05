package com.mobile.otrcapital.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobile.otrcapital.Helpers.ActivityTags;
import com.mobile.otrcapital.Helpers.CustomerViewModel;
import com.mobile.otrcapital.Helpers.RESTAPIs;
import com.mobile.otrcapital.R;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.OkClient;
import retrofit.client.Response;


public class BrokerDetails extends Activity
{
    @Bind(R.id.brokerNameTV) TextView brokerNameTV;
    @Bind(R.id.mcTV) TextView mcTV;
    @Bind(R.id.factorableTV) TextView factorableTV;
    @Bind(R.id.dotNumberTV) TextView dotNumberTV;
    @Bind(R.id.locationTV) TextView locationTV;
    @Bind(R.id.phoneNumberTV) TextView phoneNumberTV;
    @Bind(R.id.detailsGroup) LinearLayout detailsGroup;
    @Bind(R.id.verifyUserGroup) LinearLayout verifyUserGroup;
    @Bind(R.id.factorLoadButton) Button factorLoadButton;
    @Bind(R.id.advanceLoadButton)Button advanceLoadButton;
    @Bind(R.id.networkErrorTV) TextView networkErrorTV;
    private String brokerName, pKey;

    @OnClick(R.id.factorLoadButton)
    public void factorLoad (View view)
    {
        if (factorLoadButton.getText().toString().equals("Retry"))
        {
            networkErrorTV.setVisibility(View.INVISIBLE);
            progressIndicatorVisiblity(View.VISIBLE);
            brokerDetails();
        }
        else if (factorLoadButton.getText().toString().equals("Call Office"))
        {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:770 8820 124"));
            startActivity(intent);
        }
        else
        {
            factorAdvanceLoad(ActivityTags.TAG_FACTOR_LOAD);
        }
    }

    @OnClick(R.id.advanceLoadButton)
    public void advanceLoad(View view)
    {
        factorAdvanceLoad(ActivityTags.TAG_FACTOR_ADVANCE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broker_details);
        ButterKnife.bind(this);

        progressIndicatorVisiblity(View.VISIBLE);
        networkErrorTV.setVisibility(View.INVISIBLE);

        Bundle extras = getIntent().getExtras();
        Bundle bundle =  extras.getBundle("data_extra");
        brokerName = bundle.getString(ActivityTags.TAG_BROKER_NAME);
        pKey = bundle.getString(ActivityTags.TAG_PKEY);

        if (pKey!=null && !pKey.isEmpty())
            brokerDetails();

    }

    private void brokerDetails()
    {
        SharedPreferences prefs = getSharedPreferences(ActivityTags.SHARED_PREFS_TAG, 0);
        final String userEmail = prefs.getString(ActivityTags.PREFS_USER_EMAIL, "");
        final String userPassword = prefs.getString(ActivityTags.PREFS_USER_PASSWORD, "");
        final String userCredentials = prefs.getString(ActivityTags.PREFS_USER_CREDENTIALS, "");
        Log.d(ActivityTags.TAG_LOG, "Password: " + userPassword);
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ActivityTags.API_URL_PROD)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new AndroidLog("RetrofitLog"))
                .setClient(new OkClient(okHttpClient))
                .setRequestInterceptor(new RequestInterceptor()
                {
                    @Override
                    public void intercept(RequestFacade request)
                    {
                        request.addHeader("User-Agent", "Fiddler");
                        request.addHeader("Host", ActivityTags.HOST_NAME);
                        request.addHeader("Authorization", "Basic " + userCredentials)
                        ;
                    }
                })
                .build();
        RESTAPIs methods = restAdapter.create(RESTAPIs.class);
        Callback callback = new Callback()
        {
            @Override
            public void success(Object o, Response response)
            {
                CustomerViewModel cvm = (CustomerViewModel) o;
                brokerNameTV.setText(cvm.Name);
                mcTV.setText(cvm.McNumber);
                dotNumberTV.setText(cvm.DotNumber);
                factorLoadButton.setText("Factor a Load");
                locationTV.setText(cvm.City +", "+ cvm.State);
                phoneNumberTV.setText(cvm.Phone);
                progressIndicatorVisiblity(View.INVISIBLE);
                Log.d(ActivityTags.TAG_LOG, "Broker details fetched from server");

                if (cvm.CreditCheckResult.equals("Call Office"))
                {
                    factorableTV.setText("Call Office");
                    factorableTV.setTextColor(getResources().getColor(R.color.red));
                    factorLoadButton.setText("Call Office");
                    advanceLoadButton.setVisibility(View.INVISIBLE);
                }
                else
                {
                    factorableTV.setText("Yes");
                    factorableTV.setTextColor(getResources().getColor(R.color.green));
                }


            }

            @Override
            public void failure(RetrofitError error)
            {
                verifyUserGroup.setVisibility(View.INVISIBLE);
                networkErrorTV.setVisibility(View.VISIBLE);
                networkErrorTV.setText("Unable to connect to the server at the moment");
                factorLoadButton.setText("Retry");
                factorLoadButton.setVisibility(View.VISIBLE);
                Log.e(ActivityTags.TAG_LOG,error.toString());
            }
        };

        methods.BrokerCheck(userEmail,userPassword,this.pKey, callback);

    }

    void factorAdvanceLoad(String activityType)
    {
        Bundle extras = new Bundle();
        extras.putString(ActivityTags.TAG_ACTIVITY_TYPE, activityType);
        extras.putString(ActivityTags.TAG_BROKER_NAME, brokerName);
        Intent intent = new Intent(BrokerDetails.this, FactorAdvanceLoad.class);
        intent.putExtra("data_extra",extras);
        startActivity(intent);
    }

    private void progressIndicatorVisiblity(final int visibility)
    {
        if (visibility == View.INVISIBLE)
        {
            verifyUserGroup.setVisibility(View.INVISIBLE);
            detailsGroup.setVisibility(View.VISIBLE);
            factorLoadButton.setVisibility(View.VISIBLE);
            advanceLoadButton.setVisibility(View.VISIBLE);
        }
        else
        {
            verifyUserGroup.setVisibility(View.VISIBLE);
            detailsGroup.setVisibility(View.INVISIBLE);
            factorLoadButton.setVisibility(View.INVISIBLE);
            advanceLoadButton.setVisibility(View.INVISIBLE);
        }
    }

    public class ArrayOfCustomerViewModel {
        public List<CustomerViewModel> CVM;
    }


}
