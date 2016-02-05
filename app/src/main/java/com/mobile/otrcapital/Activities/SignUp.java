package com.mobile.otrcapital.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.mobile.otrcapital.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUp extends Activity
{
    @OnClick (R.id.signUpButton) public void signUpButton (View view)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://otrcapital.com/apply-now/"));
        startActivity(intent);
    }

    @OnClick (R.id.callBtn) public void callBtn (View view)
    {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:770 8820 124"));
        startActivity(intent);
    }

    @OnClick (R.id.emailBtn) public void emailBtn (View view)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, "info@otrcapital.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Information");

        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    @OnClick (R.id.fbImgBtn)  public void fbImgBtn (View view)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.facebook.com/pages/OTR-Capital/473947932696034"));
        startActivity(intent);
    }
    @OnClick (R.id.googleplusImgBtn)  public void googleplusImgBtn (View view)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://plus.google.com/112871732199319272036/about?hl=en"));
        startActivity(intent);
    }
    @OnClick (R.id.twitterImgBtn)  public void twitterImgBtn (View view)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://twitter.com/otrcapitalllc"));
        startActivity(intent);
    }
    @OnClick (R.id.instagramImgBtn)  public void instagramImgBtn (View view)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://instagram.com/otrcapital/"));
        startActivity(intent);
    }
    @OnClick (R.id.linkedinImgBtn)  public void linkedinImgBtn (View view)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.linkedin.com/company/otr-capital"));
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
    }


}
