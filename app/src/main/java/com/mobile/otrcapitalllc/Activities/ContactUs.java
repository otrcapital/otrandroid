package com.mobile.otrcapitalllc.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.mobile.otrcapitalllc.R;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class ContactUs extends Activity {

    @OnClick(R.id.callArrowBtn)
    public void callArrowBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(getString(R.string.office_tel_number)));
        startActivity(intent);
    }

    @OnClick(R.id.callTV)
    public void callTV(View view) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(getString(R.string.office_tel_number)));
        startActivity(intent);
    }

    @OnClick(R.id.emailArrowBtn)
    public void emailArrowBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.office_email_address));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_us_email_subject));

        startActivity(Intent.createChooser(intent, getString(R.string.send_email)));
    }

    @OnClick(R.id.emailTV)
    public void emailTV(View view) {
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
        intent.setData(Uri.parse("https://plus.google.com/112871732199319272036/about?hl=en"));
        startActivity(intent);
    }

    @OnClick(R.id.twitterImgBtn)
    public void twitterImgBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://twitter.com/otrcapitalllc"));
        startActivity(intent);
    }

    @OnClick(R.id.instagramImgBtn)
    public void instagramImgBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://instagram.com/otrcapital/"));
        startActivity(intent);
    }

    @OnClick(R.id.linkedinImgBtn)
    public void linkedinImgBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.linkedin.com/company/otr-capital"));
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        ButterKnife.bind(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
        }
        return super.onMenuItemSelected(featureId, item);
    }
}
