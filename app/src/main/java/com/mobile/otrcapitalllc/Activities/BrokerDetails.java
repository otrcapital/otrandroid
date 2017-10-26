package com.mobile.otrcapitalllc.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobile.otrcapitalllc.Helpers.ActivityTags;
import com.mobile.otrcapitalllc.Helpers.CrashlyticsHelper;
import com.mobile.otrcapitalllc.Helpers.Extras;
import com.mobile.otrcapitalllc.Helpers.LogHelper;
import com.mobile.otrcapitalllc.Helpers.PermissionHelper;
import com.mobile.otrcapitalllc.Helpers.PreferenceManager;
import com.mobile.otrcapitalllc.Helpers.RestClient;
import com.mobile.otrcapitalllc.Models.CustomerViewModel;
import com.mobile.otrcapitalllc.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BrokerDetails extends BaseActivity {

    @Bind(R.id.brokerNameTV)
    TextView brokerNameTV;
    @Bind(R.id.mcTV)
    TextView mcTV;
    @Bind(R.id.factorableTV)
    TextView factorableTV;
    @Bind(R.id.dotNumberTV)
    TextView dotNumberTV;
    @Bind(R.id.locationTV)
    TextView locationTV;
    @Bind(R.id.phoneNumberTV)
    TextView phoneNumberTV;
    @Bind(R.id.detailsGroup)
    LinearLayout detailsGroup;
    @Bind(R.id.verifyUserGroup)
    LinearLayout verifyUserGroup;
    @Bind(R.id.factorLoadButton)
    Button factorLoadButton;
    @Bind(R.id.advanceLoadButton)
    Button advanceLoadButton;
    @Bind(R.id.networkErrorTV)
    TextView networkErrorTV;

    private String brokerName, pKey;

    @OnClick(R.id.factorLoadButton)
    public void factorLoad(View view) {
        if (factorLoadButton.getText().toString().equals(getString(R.string.retry))) {
            networkErrorTV.setVisibility(View.INVISIBLE);
            progressIndicatorVisiblity(View.VISIBLE);
            brokerDetails();
        } else if (factorLoadButton.getText().toString().equals(getString(R.string.call_office))) {

            final Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(getString(R.string.office_tel_number)));

            if (PermissionHelper.hasPermission(this, Manifest.permission.CALL_PHONE)) {
                startActivity(intent);
            } else {
                getPermissionsHelper().checkPhonePermissions(new PermissionHelper.RequestResultListener() {

                    @Override
                    public void onShouldShowPermissionRationale() {
                        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), R.string.permission_phone_rationale, Snackbar.LENGTH_INDEFINITE)
                                .setAction(android.R.string.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        getPermissionsHelper().requestPhonePermissions(BrokerDetails.this);
                                    }
                                }).show();
                    }

                    @Override
                    public void onRequestResult(boolean granted) {
                        if (granted) {
                            startActivity(intent);
                        } else {
                            Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), R.string.permissions_not_granted, Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                    }
                }, this);
            }
        } else {
            factorAdvanceLoad(ActivityTags.TAG_FACTOR_LOAD);
        }
    }

    @OnClick(R.id.advanceLoadButton)
    public void advanceLoad(View view) {
        factorAdvanceLoad(ActivityTags.TAG_FACTOR_ADVANCE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broker_details);
        ButterKnife.bind(this);

        progressIndicatorVisiblity(View.VISIBLE);
        networkErrorTV.setVisibility(View.INVISIBLE);

        Bundle extras = getIntent().getExtras();
        Bundle bundle = extras.getBundle(Extras.EXTRA_DATA);

        if (bundle == null) return;

        brokerName = bundle.getString(ActivityTags.TAG_BROKER_NAME);
        pKey = bundle.getString(ActivityTags.TAG_PKEY);

        if (pKey != null && !pKey.isEmpty()) {
            brokerDetails();
        }
    }

    private void brokerDetails() {
        final String userEmail = PreferenceManager.with(this).getUserEmail();
        final String userPassword = PreferenceManager.with(this).getUserPassword();
        final String userCredentials = PreferenceManager.with(this).getUserCredentials();

        RestClient restClient = new RestClient(this, userCredentials);
        restClient.getApiService().BrokerCheck(userEmail, userPassword, this.pKey, new Callback<CustomerViewModel>() {
            @Override
            public void success(CustomerViewModel cvm, Response response) {
                brokerNameTV.setText(cvm.Name);
                mcTV.setText(cvm.McNumber);
                dotNumberTV.setText(cvm.DotNumber);
                factorLoadButton.setText(getString(R.string.title_activity_factor_a_load));
                locationTV.setText(cvm.City + ", " + cvm.State);
                phoneNumberTV.setText(cvm.Phone);
                progressIndicatorVisiblity(View.INVISIBLE);

                LogHelper.logDebug("Broker details fetched from server");

                String checkResult = cvm.CreditCheckResult;
                if (cvm.CreditCheckResult == null || "".equals(cvm.CreditCheckResult)) {
                    checkResult = getString(R.string.call_office);
                }

                factorableTV.setText(checkResult);

                if (getString(R.string.call_office).equals(checkResult)) {
                    factorableTV.setTextColor(getResources().getColor(R.color.red));
                    factorLoadButton.setText(getString(R.string.call_office));
                    advanceLoadButton.setVisibility(View.INVISIBLE);
                } else if (getString(R.string.approved).equals(checkResult)) {
                    factorableTV.setTextColor(getResources().getColor(R.color.green));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                CrashlyticsHelper.logException(error);
                verifyUserGroup.setVisibility(View.INVISIBLE);
                networkErrorTV.setVisibility(View.VISIBLE);
                networkErrorTV.setText(getString(R.string.alert_message_no_connection));
                factorLoadButton.setText(getString(R.string.retry));
                factorLoadButton.setVisibility(View.VISIBLE);

                LogHelper.logError(error.toString());
            }
        });

    }

    void factorAdvanceLoad(String activityType) {
        Intent intent = new Intent(BrokerDetails.this, FactorAdvanceLoad.class);
        intent.putExtra(ActivityTags.TAG_ACTIVITY_TYPE, activityType);
        intent.putExtra(ActivityTags.TAG_BROKER_NAME, brokerName);
        startActivity(intent);
    }

    private void progressIndicatorVisiblity(final int visibility) {
        if (visibility == View.INVISIBLE) {
            verifyUserGroup.setVisibility(View.INVISIBLE);
            detailsGroup.setVisibility(View.VISIBLE);
            factorLoadButton.setVisibility(View.VISIBLE);
            advanceLoadButton.setVisibility(View.VISIBLE);
        } else {
            verifyUserGroup.setVisibility(View.VISIBLE);
            detailsGroup.setVisibility(View.INVISIBLE);
            factorLoadButton.setVisibility(View.INVISIBLE);
            advanceLoadButton.setVisibility(View.INVISIBLE);
        }
    }
}
