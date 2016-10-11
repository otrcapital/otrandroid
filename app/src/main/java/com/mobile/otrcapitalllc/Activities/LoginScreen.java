package com.mobile.otrcapitalllc.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobile.otrcapitalllc.BuildConfig;
import com.mobile.otrcapitalllc.Helpers.CrashlyticsHelper;
import com.mobile.otrcapitalllc.Helpers.EventTracker;
import com.mobile.otrcapitalllc.Helpers.LogHelper;
import com.mobile.otrcapitalllc.Helpers.PreferenceManager;
import com.mobile.otrcapitalllc.Helpers.RestClient;
import com.mobile.otrcapitalllc.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginScreen extends Activity {

    @Bind(R.id.loginEmailET)
    EditText loginEmailET;
    @Bind(R.id.loginPasswordET)
    EditText loginPasswordET;
    @Bind(R.id.loginGroup)
    LinearLayout loginGroup;
    @Bind(R.id.socialMediaGroup)
    LinearLayout socialMediaGroup;
    @Bind(R.id.verifyUserGroup)
    LinearLayout verifyUserGroup;
    @Bind(R.id.signUpImgBtn)
    ImageButton signUpImgBtn;
    @Bind(R.id.contactUsImgBtn)
    ImageButton contactUsImgBtn;
    @Bind(R.id.loginResultTV)
    TextView loginResultTV;
    @Bind(R.id.testBtn)
    Button mButton;

    @OnClick(R.id.testBtn)
    public void testLogin(View view) {
        final String credentials = "MobileOTRCapital@otrcapital.com" + ":" + "Portal123";
        final String passwordEncoded = Base64.encodeToString("Portal123".getBytes(), Base64.NO_WRAP);
        final String credentialsEncoded = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        verifyAgent("MobileOTRCapital@otrcapital.com", passwordEncoded, credentialsEncoded);
    }

    @OnClick(R.id.loginButton)
    public void loginButton(View view) {
        final String userEmail = loginEmailET.getText().toString();
        final String password = loginPasswordET.getText().toString();
        final String credentials = userEmail + ":" + password;
        final String passwordEncoded = Base64.encodeToString(password.getBytes(), Base64.NO_WRAP);
        final String credentialsEncoded = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        verifyAgent(userEmail, passwordEncoded, credentialsEncoded);
    }

    @OnClick(R.id.signUpImgBtn)
    public void signUp(View view) {
        Intent intent = new Intent(LoginScreen.this, SignUp.class);
        startActivity(intent);
    }

    @OnClick(R.id.contactUsImgBtn)
    public void contactUs(View view) {
        Intent intent = new Intent(LoginScreen.this, ContactUs.class);
        startActivity(intent);
    }

    @OnClick(R.id.fbImgBtn)
    public void fbImgBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.facebook.com/pages/OTR-Capital/473947932696034"));
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
        setContentView(R.layout.activity_login_screen);
        ButterKnife.bind(this);

        if (!BuildConfig.DEBUG) {
            mButton.setVisibility(View.GONE);
        }

        loginEmailET.getBackground().setColorFilter(getResources().getColor(R.color.blue_light), PorterDuff.Mode.SRC_ATOP);
        loginPasswordET.getBackground().setColorFilter(getResources().getColor(R.color.blue_light), PorterDuff.Mode.SRC_ATOP);

        ProgressIndicatorVisiblity(View.VISIBLE);
        verifyUserGroup.setVisibility(View.INVISIBLE);

        if (!isNetworkAvailable()) {
            //if network is not available then show Dialogue to quite app
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("NETWORK NOT AVAILABLE").setCancelable(false).setPositiveButton("Quit", new
                DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            //if network is available then continue
            if (PreferenceManager.with(this).getFirstRun()) {
                PreferenceManager.with(this).setFirstRun();

                ProgressIndicatorVisiblity(View.INVISIBLE);

                loginResultTV.setText("New user detected, please login");
            } else {
                //check if there if there is a valid login token present in sharedPreferenes
                boolean isTokenValid = PreferenceManager.with(this).getTokenValid();
                if (!isTokenValid) {
                    //if there is no token present then ask the
                    // user to login with email and password
                    ProgressIndicatorVisiblity(View.INVISIBLE);
                    loginResultTV.setText("Please login");
                } else {
                    //if there is a valid token present, then use the stored email and password to login
                    ProgressIndicatorVisiblity(View.VISIBLE);
                    verifyAgent(PreferenceManager.with(this).getUserEmail(), PreferenceManager.with(this).getUserPassword(),
                        PreferenceManager.with(this).getUserCredentials());
                }
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * Method to verify the login info of the agent to sign in to the app
     */
    private void verifyAgent(final String user_email, final String user_password, final String user_credentials) {
        ProgressIndicatorVisiblity(View.VISIBLE);
        CrashlyticsHelper.setUserEmail(user_email);
        EventTracker.trackUserLoginAtempt(user_email);

        RestClient mRestClient = new RestClient(user_credentials);
        mRestClient.getApiService().GetClientInfo(user_email, user_password, new Callback<AgentViewModel>() {
            @Override
            public void success(AgentViewModel agentViewModel, Response response) {
                EventTracker.trackUserLogin(user_email, true);
                ProgressIndicatorVisiblity(View.INVISIBLE);

                // IsValidUser - is user has an access to service
                if (agentViewModel.IsValidUser) {
                    CrashlyticsHelper.setUser(agentViewModel);

                    PreferenceManager.with(LoginScreen.this).saveUserData(user_email, user_password, user_credentials, true);

                    Intent intent = new Intent(LoginScreen.this, MainDashboard.class);
                    finish();
                    startActivity(intent);
                } else {
                    loginResultTV.setText("You don't have an access to this service");
                    loginResultTV.setTextColor(getResources().getColor(R.color.red));

                    PreferenceManager.with(LoginScreen.this).saveTokenValid(false);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                EventTracker.trackUserLogin(user_email, false);
                CrashlyticsHelper.logException(error);
                if (error.getResponse().getStatus() == 401) {
                    loginResultTV.setText("Incorrect username/password");
                } else {
                    loginResultTV.setText("Server unavailable, please try again later");
                }
                loginResultTV.setTextColor(getResources().getColor(R.color.red));
                ProgressIndicatorVisiblity(View.INVISIBLE);
                LogHelper.logError(error.toString());
            }
        });
    }

    private void ProgressIndicatorVisiblity(final int visibility) {
        if (visibility == View.INVISIBLE) {
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

    public class AgentViewModel {
        public String ClientId;
        public String Login;
        public String Password;
        public boolean IsValidUser;
    }

}
