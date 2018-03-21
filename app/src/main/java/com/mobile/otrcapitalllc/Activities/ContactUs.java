package com.mobile.otrcapitalllc.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mobile.otrcapitalllc.Helpers.PermissionHelper;
import com.mobile.otrcapitalllc.Helpers.RemoteConfigManager;
import com.mobile.otrcapitalllc.R;

import butterknife.BindView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("MissingPermission")
public class ContactUs extends BaseActivity {

    @BindView(R.id.callTV)
    TextView callTV;

    @BindView(R.id.faxTV)
    TextView faxTV;

    @BindView(R.id.emailTV)
    TextView emailTV;

    @BindView(R.id.mailTV)
    TextView mailTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        callTV.setText(RemoteConfigManager.getContactPhoneNumber());
        faxTV.setText(RemoteConfigManager.getContactFaxNumber());
        emailTV.setText(RemoteConfigManager.getContactEmail());

        String address = RemoteConfigManager.getContactAddress();
        address = address.replace("\\n", System.getProperty("line.separator"));
        mailTV.setText(address);
    }

    @OnClick(R.id.callArrowBtn)
    public void callArrowBtn(View view) {
        final Intent intent = new Intent(Intent.ACTION_CALL);
        String phoneNumber = RemoteConfigManager.getFormattedContactPhoneNumber();
        intent.setData(Uri.parse(phoneNumber));

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
                                    getPermissionsHelper().requestPhonePermissions(ContactUs.this);
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
    }

    @OnClick(R.id.callTV)
    public void callTV(View view) {
        final Intent intent = new Intent(Intent.ACTION_CALL);
        String phoneNumber = RemoteConfigManager.getFormattedContactPhoneNumber();
        intent.setData(Uri.parse(phoneNumber));

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
                                    getPermissionsHelper().requestPhonePermissions(ContactUs.this);
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
    }

    @OnClick(R.id.emailArrowBtn)
    public void emailArrowBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {RemoteConfigManager.getContactEmail()});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_us_email_subject));

        startActivity(Intent.createChooser(intent, getString(R.string.send_email)));
    }

    @OnClick(R.id.emailTV)
    public void emailTV(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {RemoteConfigManager.getContactEmail()});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_us_email_subject));

        startActivity(Intent.createChooser(intent, getString(R.string.send_email)));
    }

    @OnClick(R.id.fbImgBtn)
    public void fbImgBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(RemoteConfigManager.getFacebookURL()));
        startActivity(intent);
    }

    @OnClick(R.id.googleplusImgBtn)
    public void googleplusImgBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(RemoteConfigManager.getGooglePlusURL()));
        startActivity(intent);
    }

    @OnClick(R.id.twitterImgBtn)
    public void twitterImgBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(RemoteConfigManager.getTwitterURL()));
        startActivity(intent);
    }

    @OnClick(R.id.instagramImgBtn)
    public void instagramImgBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(RemoteConfigManager.getInstagramURL()));
        startActivity(intent);
    }

    @OnClick(R.id.linkedinImgBtn)
    public void linkedinImgBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(RemoteConfigManager.getLinkedinURL()));
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
