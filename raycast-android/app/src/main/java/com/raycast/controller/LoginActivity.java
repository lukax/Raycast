package com.raycast.controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.PlusClient;
import com.raycast.R;
import com.raycast.controller.base.PlusBaseActivity;
import com.raycast.service.AccountService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;


/**
 * A login screen that offers login via email/password and via Google+ sign in.
 * <p/>
 * ************ IMPORTANT SETUP NOTES: ************
 * In order for Google+ sign in to work with your app, you must first go to:
 * https://developers.google.com/+/mobile/android/getting-started#step_1_enable_the_google_api
 * and follow the steps in "Step 1" to create an OAuth 2.0 client for your package.
 */
@EActivity(R.layout.activity_login)
public class LoginActivity extends PlusBaseActivity {
    static final String TOKEN_FILE_NAME = "raycast_token";
    static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;
    static final String CLIENT_ID = "663385753631-negeq0ad0h0ln09jhnjurisacb4r0a19.apps.googleusercontent.com";
    static final String SCOPES_STRING = Scopes.PLUS_LOGIN + " " + Scopes.PLUS_ME;
    static final String SCOPE_AUDIENCE = "audience:server:client_id:" + CLIENT_ID;
    static final String SCOPE_AUTHCODE = "oauth2:server:client_id:" + CLIENT_ID + ":api_scope:" + SCOPES_STRING;

    private boolean handlingException;
    @ViewById(R.id.login_progress) View mProgressView;
    @ViewById(R.id.plus_sign_in_button) SignInButton mPlusSignInButton;
    @ViewById(R.id.plus_sign_out_buttons) View mSignOutButtons;
    @ViewById(R.id.login_form) View mLoginFormView;
    @Bean AccountService accountService;
    @InstanceState String mEmail;

    @AfterViews
    void afterViews(){
        // Initialize the PlusClient connection.
        // Scopes indicate the information about the user your application will be able to access.
        mPlusClient = new PlusClient.Builder(this, this, this).setScopes(Scopes.PLUS_LOGIN, Scopes.PLUS_ME).build();

        if (supportsGooglePlayServices()) {
            // Set a listener to connect the user when the G+ button is clicked.
            mPlusSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn();
                }
            });
        } else {
            // Don't offer G+ sign in if the app's version is too low to support Google Play
            // Services.
            mPlusSignInButton.setVisibility(View.GONE);
            return;
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void onPlusClientSignIn() {
        showProgress(true);
        if(!handlingException){
            mEmail = getPlusClient().getAccountName();
            new DoLoginTask(LoginActivity.this, mEmail, SCOPE_AUTHCODE).execute();
        }
    }

    @Override
    protected void updateConnectButtonState() {
        boolean connected = getPlusClient().isConnected();
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mSignOutButtons = findViewById(R.id.plus_sign_out_buttons);
        //TODO: remove this button completely
        mSignOutButtons.setVisibility(View.GONE/*connected ? View.VISIBLE : View.GONE*/);
        mPlusSignInButton.setVisibility(connected ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onPlusClientRevokeAccess() {
        // TODO: Access t o the user's G+ account has been revoked.  Per the developer terms, delete
        // any stored user data here.
    }

    @Override
    protected void onPlusClientSignOut() {

    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        if(responseCode == 0){
            Toast.makeText(this, "não foi possível fazer login :(", Toast.LENGTH_LONG).show();
        }
        else{
            new DoLoginTask(LoginActivity.this,  mEmail, SCOPE_AUTHCODE).execute();
        }
    }

    /**
     * Check if the device supports Google Play Services.  It's best
     * practice to check first rather than handling this as an error case.
     *
     * @return whether the device supports Google Play Services
     */
    private boolean supportsGooglePlayServices() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) ==
                ConnectionResult.SUCCESS;
    }

    /**
     * This method is a hook for background threads and async tasks that need to
     * provide the user a response UI when an exception occurs.
     */
    public void handleException(final Exception e) {

        // Because this call comes from the AsyncTask, we must ensure that the following
        // code instead executes on the UI thread.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            if (e instanceof GooglePlayServicesAvailabilityException) {
                // The Google Play services APK is old, disabled, or not present.
                // Show a dialog created by Google Play services that allows
                // the user to update the APK
                int statusCode = ((GooglePlayServicesAvailabilityException)e)
                        .getConnectionStatusCode();
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                        LoginActivity.this,
                        REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                dialog.show();
            } else if (e instanceof UserRecoverableAuthException) {
                // Unable to authenticate, such as when the user has not yet granted
                // the app access to the account, but the user can fix this.
                // Forward the user to an activity in Google Play services.
                handlingException = true;
                Intent intent = ((UserRecoverableAuthException)e).getIntent();
                startActivityForResult(intent,
                        REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
            }
            }
        });
    }

    class DoLoginTask extends AsyncTask<Void, Void, Boolean> {
        LoginActivity mActivity;
        String mScope;
        String mEmail;

        DoLoginTask(LoginActivity activity, String name, String scope) {
            this.mActivity = activity;
            this.mScope = scope;
            this.mEmail = name;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean ok = false;
            try {
                if(!accountService.isLoggedIn()){
                    String token = fetchToken();
                    if(token != null){
                        Log.d(getClass().getSimpleName(), "trying to login with token");
                        ok = accountService.login(token);
                        if(!ok){
                            GoogleAuthUtil.invalidateToken(mActivity, token);
                        }
                    }
                }
                else{
                    ok = true;
                }
            } catch (IOException e) {
                // The fetchToken() method handles Google-specific exceptions,
                // so this indicates something went wrong at a higher level.
                // TIP: Check for network connectivity before starting the AsyncTask.
                //TODO
                Log.e(getClass().getSimpleName(), e.getMessage());
            }
            return ok;
        }

        @Override
        protected void onPostExecute(Boolean ok) {
            if(ok){
                FeedActivity_.intent(LoginActivity.this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
                finish();
            }
        }

        protected String fetchToken() throws IOException {
            try {
                return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
            } catch (UserRecoverableAuthException userRecoverableException) {
                // GooglePlayServices.apk is either old, disabled, or not present
                // so we need to show the user some UI in the activity to recover.
                //mActivity.handleException(userRecoverableException);
                mActivity.handleException(userRecoverableException);
            } catch (GoogleAuthException fatalException) {
                // Some other type of unrecoverable exception has occurred.
                // Report and log the error as appropriate for your app.
                //TODO
                Log.e(getClass().getSimpleName(), fatalException.getMessage());
            }
            return null;
        }
    }
}

