package com.mobile.otrcapitalllc.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.otrcapitalllc.Helpers.ActivityTags;
import com.mobile.otrcapitalllc.Helpers.LogHelper;
import com.mobile.otrcapitalllc.Helpers.PreferenceManager;
import com.mobile.otrcapitalllc.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainDashboard extends Activity {

    @Bind(R.id.verifyUserGroup)
    LinearLayout verifyUserGroup;
    @Bind(R.id.verifyUserTV)
    TextView verifyUserTV;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    private String activityType;

    @OnClick(R.id.factorLoadImgBtn)
    public void factorLoadImgBtn(View view) {
        factorAdvanceLoad(ActivityTags.TAG_FACTOR_LOAD);
    }

    @OnClick(R.id.advanceLoadImgBtn)
    public void advanceLoadImgBtn(View view) {
        factorAdvanceLoad(ActivityTags.TAG_FACTOR_ADVANCE);
    }

    @OnClick(R.id.brokerCheckImgBtn)
    public void brokerCheckImgBtn(View view) {
        Intent intent = new Intent(MainDashboard.this, BrokerCheck.class);
        startActivity(intent);

    }

    @OnClick(R.id.historyImgBtn)
    public void historyImgBtn(View view) {
        Intent intent = new Intent(MainDashboard.this, History.class);
        startActivity(intent);
    }

    @OnClick(R.id.signOutImgBtn)
    public void signOutImgBtn(View view) {

        PreferenceManager.with(MainDashboard.this).saveTokenValid(false);
 
        Intent intent = new Intent(MainDashboard.this, LoginScreen.class);
        finish();
        startActivity(intent);

//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage(getString(R.string.alert_logout));
//
//        String positiveText = getString(R.string.btn_confirm);
//        builder.setPositiveButton(positiveText,
//                new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        PreferenceManager.with(MainDashboard.this).saveTokenValid(false);
//
//                        Intent intent = new Intent(MainDashboard.this, LoginScreen.class);
//                        finish();
//                        startActivity(intent);
//                    }
//                });
//
//        String negativeText = getString(android.R.string.cancel);
//        builder.setNegativeButton(negativeText,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                });
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
    }

    @OnClick(R.id.contactUsImgBtn)
    public void contactUsImgBtn(View view) {
        Intent intent = new Intent(MainDashboard.this, ContactUs.class);
        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);
        ButterKnife.bind(this);

        LogHelper.logDebug("Getting customer list from server");

        if (PreferenceManager.with(this).getDbUpdateTimestamp() == 0) {
            Toast.makeText(this, "Setting up database, check notification bar for progress", Toast.LENGTH_LONG).show();
        }

        Intent intent = new Intent(MainDashboard.this, GetBrokers.class);
        startService(intent);
    }

    void factorAdvanceLoad(String activityType) {
        Intent intent = new Intent(MainDashboard.this, FactorAdvanceLoad.class);
        intent.putExtra(ActivityTags.TAG_ACTIVITY_TYPE, activityType);
        startActivity(intent);
    }

}
