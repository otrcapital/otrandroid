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

import com.mobile.otrcapitalllc.Helpers.PermissionHelper;
import com.mobile.otrcapitalllc.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUp extends BaseActivity {
    @OnClick(R.id.signUpButton)
    public void signUpButton(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.url_sign_up)));
        startActivity(intent);
    }

    @OnClick(R.id.callBtn)
    public void callBtn(View view) {
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
                                    getPermissionsHelper().requestPhonePermissions(SignUp.this);
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

    @OnClick(R.id.emailBtn)
    public void emailBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.office_email_address));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_us_email_subject));

        startActivity(Intent.createChooser(intent, getString(R.string.send_email)));
    }

    @OnClick(R.id.fbImgBtn)
    public void fbImgBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.url_facebook)));
        startActivity(intent);
    }

    @OnClick(R.id.googleplusImgBtn)
    public void googleplusImgBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.url_plus)));
        startActivity(intent);
    }

    @OnClick(R.id.twitterImgBtn)
    public void twitterImgBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.url_twitter)));
        startActivity(intent);
    }

    @OnClick(R.id.instagramImgBtn)
    public void instagramImgBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.url_instagram)));
        startActivity(intent);
    }

    @OnClick(R.id.linkedinImgBtn)
    public void linkedinImgBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.url_linkedin)));
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
    }


}
