package com.mobile.otrcapital.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobile.otrcapital.BuildConfig;
import com.mobile.otrcapital.Helpers.ActivityTags;
import com.mobile.otrcapital.Helpers.RESTAPIs;
import com.mobile.otrcapital.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;

public class LoginScreen extends Activity
{

    @Bind (R.id.loginEmailET) EditText loginEmailET;
    @Bind (R.id.loginPasswordET) EditText loginPasswordET;
    @Bind (R.id.loginGroup) LinearLayout loginGroup;
    @Bind (R.id.socialMediaGroup) LinearLayout socialMediaGroup;
    @Bind (R.id.verifyUserGroup) LinearLayout verifyUserGroup;
    @Bind (R.id.signUpImgBtn) ImageButton signUpImgBtn;
    @Bind (R.id.contactUsImgBtn) ImageButton contactUsImgBtn;
    @Bind (R.id.loginResultTV) TextView loginResultTV;
    @Bind(R.id.testBtn) Button mButton;
    private String date = "";
    private boolean isFirstRun;

    @OnClick(R.id.testBtn) public void testLogin (View view)
    {
        final String credentials = "MobileOTRCapital@otrcapital.com"+":"+"Portal123";
        final String passwordEncoded = Base64.encodeToString("Portal123".getBytes(), Base64.NO_WRAP);
        final String credentialsEncoded = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        verifyAgent("MobileOTRCapital@otrcapital.com", passwordEncoded,credentialsEncoded);
    }

    @OnClick(R.id.loginButton) public void loginButton (View view)
    {
        final String userEmail = loginEmailET.getText().toString();
        final String password = loginPasswordET.getText().toString();
        final String credentials = userEmail+":"+password;
        final String passwordEncoded = Base64.encodeToString(password.getBytes(), Base64.NO_WRAP);
        final String credentialsEncoded = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        verifyAgent(userEmail, passwordEncoded, credentialsEncoded);
    }

    @OnClick (R.id.signUpImgBtn) public void signUp (View view)
    {
        Intent intent = new Intent(LoginScreen.this, SignUp.class);
        startActivity(intent);
    }

    @OnClick (R.id.contactUsImgBtn) public void contactUs (View view)
    {
        Intent intent = new Intent(LoginScreen.this, ContactUs.class);
        startActivity(intent);
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
        setContentView(R.layout.activity_login_screen);
        ButterKnife.bind(this);

        if (!BuildConfig.DEBUG) {
            mButton.setVisibility(View.GONE);
        }

        loginEmailET.getBackground().setColorFilter(getResources().getColor(R.color.blue_light), PorterDuff.Mode.SRC_ATOP);
        loginPasswordET.getBackground().setColorFilter(getResources().getColor(R.color.blue_light), PorterDuff.Mode.SRC_ATOP);

        ProgressIndicatorVisiblity(View.VISIBLE);
        verifyUserGroup.setVisibility(View.INVISIBLE);

            if (!isNetworkAvailable())  //if network is not available then show Dialogue to quite app
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder
                        .setMessage("NETWORK NOT AVAILABLE")
                        .setCancelable(false)
                        .setPositiveButton("Quit", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                finish();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }

            else    //if network is available then continue
            {
                SharedPreferences prefs = getSharedPreferences(ActivityTags.SHARED_PREFS_TAG, 0);

                if (!prefs.getBoolean(ActivityTags.PREFS_NOT_FIRST_RUN, false))
                {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(ActivityTags.PREFS_NOT_FIRST_RUN, true);
                    editor.commit();

                    ProgressIndicatorVisiblity(View.INVISIBLE);

                    loginResultTV.setText("New user detected, please login");
                    date = "1975/01/01";
                    isFirstRun = true;
                }

                else
                {
                    date = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
                    isFirstRun = false;
                    //check if there if there is a valid login token present in sharedPreferenes
                    if (!prefs.getBoolean(ActivityTags.PREFS_TOKEN_VALID, false))   //if there is no token present then ask the user to login with email and password
                    {
                        ProgressIndicatorVisiblity(View.INVISIBLE);
                        loginResultTV.setText("Please login");
                    }

                    else    //if there is a valid token present, then use the stored email and password to login
                    {
                        ProgressIndicatorVisiblity(View.VISIBLE);
                       // Intent intent = new Intent(LoginScreen.this, MainDashboard.class);
                       // finish();
                        //startActivity(intent);
                        verifyAgent(prefs.getString(ActivityTags.PREFS_USER_EMAIL, ""),
                                prefs.getString(ActivityTags.PREFS_USER_PASSWORD, ""),
                                prefs.getString(ActivityTags.PREFS_USER_CREDENTIALS,""));
                    }
                }
            }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

/**
 *Method to verify the login info of the agent to sign in to the app
 *
 * */
 private void verifyAgent(final String user_email, final String user_password, final String user_credentials) {
     ProgressIndicatorVisiblity(View.VISIBLE);

     RestAdapter restAdapter = new RestAdapter.Builder()
             .setEndpoint(ActivityTags.API_URL_PROD)
             .setLogLevel(RestAdapter.LogLevel.FULL)
             .setLog(new AndroidLog("RetrofitLog"))
             .setRequestInterceptor(new RequestInterceptor() {
                 @Override
                 public void intercept(RequestFacade request) {
                     request.addHeader("User-Agent", "Fiddler");
                     request.addHeader("Host", ActivityTags.HOST_NAME);
                     request.addHeader("Authorization", "Basic " + user_credentials);
                 }
             })
             .build();
     RESTAPIs methods = restAdapter.create(RESTAPIs.class);

     Callback callback = new Callback() {
         @Override
         public void success(Object o, Response response) {
             AgentViewModel avm = (AgentViewModel) o;

             SharedPreferences prefs = getSharedPreferences(ActivityTags.SHARED_PREFS_TAG, 0);
             SharedPreferences.Editor editor = prefs.edit();

             //check if the provided login info was valid

             //if the callback is valid, then save the user email and password, set the token to valid and launch the main dashboard
             if (avm.IsValidUser) {
                 verifyUserGroup.setVisibility(View.INVISIBLE);

                 editor.putBoolean(ActivityTags.PREFS_TOKEN_VALID, true);
                 editor.putString(ActivityTags.PREFS_USER_EMAIL, user_email);
                 editor.putString(ActivityTags.PREFS_USER_PASSWORD, user_password);
                 editor.putString(ActivityTags.PREFS_USER_CREDENTIALS, user_credentials);
                 editor.commit();

                 //Intent serviceIntent = new Intent(LoginScreen.this, GetBrokers.class);
                 //serviceIntent.putExtra(ActivityTags.TAG_DATE,date);
                 //startService(serviceIntent);
                 //Log.d(ActivityTags.TAG_LOG,"Service intent requested from login screen, date: " + date);

                 Intent intent = new Intent(LoginScreen.this, MainDashboard.class);
                 intent.putExtra(ActivityTags.TAG_DATE, date);
                 intent.putExtra(ActivityTags.TAG_FIRST_RUN, isFirstRun);
                 finish();
                 startActivity(intent);

             }

             //if the user info is not valid, then set the token to invalid, and ask the user to login again
             else {
                 loginResultTV.setText("Incorrect username/password");
                 loginResultTV.setTextColor(getResources().getColor(R.color.red));
                 ProgressIndicatorVisiblity(View.INVISIBLE);
                 editor.putBoolean(ActivityTags.PREFS_TOKEN_VALID, false);
                 editor.commit();
             }

         }

         @Override
         public void failure(RetrofitError error) {
             loginResultTV.setText("Server unavailable, please try again later");
             loginResultTV.setTextColor(getResources().getColor(R.color.red));
             ProgressIndicatorVisiblity(View.INVISIBLE);
             Log.e(ActivityTags.TAG_LOG, error.toString());

         }
     };

     methods.GetClientInfo(user_email, user_password, callback);
 }

    private void ProgressIndicatorVisiblity(final int visibility)
    {
        if (visibility == View.INVISIBLE)
        {
            verifyUserGroup.setVisibility(View.INVISIBLE);
            loginGroup.setVisibility(View.VISIBLE);
            socialMediaGroup.setVisibility(View.VISIBLE);
            signUpImgBtn.setVisibility(View.VISIBLE);
            contactUsImgBtn.setVisibility(View.VISIBLE);
        } else {
            verifyUserGroup.setVisibility(View.VISIBLE);
            loginGroup.setVisibility(View.INVISIBLE);
            socialMediaGroup.setVisibility(View.INVISIBLE);
            signUpImgBtn.setVisibility(View.INVISIBLE);
            contactUsImgBtn.setVisibility(View.INVISIBLE);
        }
    }

    public class AgentViewModel
    {
        public String ClientId;
        public String Login;
        public String Password;
        public boolean IsValidUser;
    }



}
