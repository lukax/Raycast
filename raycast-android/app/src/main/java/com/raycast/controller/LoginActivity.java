package com.raycast.controller;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.Scopes;
import com.raycast.R;
import com.raycast.service.AccountService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;

@EActivity(R.layout.activity_login)
public class LoginActivity extends Activity {
    public static final int REQUEST_ACCOUNT_CODE = 1001;
    public static final int RESULT_CANCELLED_CODE = 0;
    @ViewById(R.id.user_username) EditText usernameView;
    @ViewById(R.id.user_password) EditText passwordView;
    @Bean AccountService accountService;

    String[] avail_accounts;
    ArrayAdapter<String> adapter;
    SharedPreferences pref;

    @AfterViews
    void afterViews() {
        checkIfAlreadyLoggedIn();
        /////////////////////////
        avail_accounts = getAccountNames();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, avail_accounts);
        pref = getSharedPreferences("AppPref", MODE_PRIVATE);
    }

    @Click(R.id.plus_sign_in_button)
    void plusSignInButtonClick() {
        if (avail_accounts.length != 0) {
            final Dialog accountDialog;
            accountDialog = new Dialog(LoginActivity.this);
            accountDialog.setContentView(R.layout.dialog_accounts);
            accountDialog.setTitle("Select Google Account");
            ListView accountList = (ListView) accountDialog.findViewById(R.id.account_list);
            accountList.setAdapter(adapter);
            accountList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    SharedPreferences.Editor edit = pref.edit();
                    //Storing Data using SharedPreferences
                    edit.putString("Email", avail_accounts[position]);
                    edit.commit();
                    new PlusLoginTask(LoginActivity.this, avail_accounts[position], accountService).execute();
                    accountDialog.cancel();
                }
            });
            accountDialog.show();
        } else {
            Toast.makeText(getApplicationContext(), "No accounts found, Add a Account and Continue.", Toast.LENGTH_SHORT).show();
        }
    }

    @Click(R.id.raycast_login)
    void raycastLoginButtonClick(){
        new RaycastLoginTask(this, accountService).execute();
    }

    private String[] getAccountNames() {
        AccountManager mAccountManager = AccountManager.get(this);
        Account[] accounts = mAccountManager.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        String[] names = new String[accounts.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = accounts[i].name;
        }
        return names;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ACCOUNT_CODE && resultCode != RESULT_CANCELLED_CODE){
            new PlusLoginTask(LoginActivity.this, pref.getString("Email", ""), accountService).execute();
        }
    }

    @Background
    void checkIfAlreadyLoggedIn(){
        if(accountService.isLoggedIn()){
            onLoginSuccess();
        }
    }

    @UiThread
    void onLoginSuccess(){
        FeedActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
        finish();
    }

    @UiThread
    void handleException(Exception ex){
        if(ex instanceof GoogleAuthException){
            startActivityForResult(((UserRecoverableAuthException) ex).getIntent(), 1001);
        }
        else{
            Toast.makeText(this, "Could not login :(", Toast.LENGTH_SHORT).show();
        }
    }
}


class PlusLoginTask extends AsyncTask<Void, Void, Boolean> {
    static final String CLIENT_ID = "663385753631-negeq0ad0h0ln09jhnjurisacb4r0a19.apps.googleusercontent.com";
    static final String SCOPES_STRING = Scopes.PLUS_LOGIN + " " + Scopes.PLUS_ME;
    //static final String SCOPE_AUDIENCE = "audience:server:client_id:" + CLIENT_ID;
    static final String SCOPE_AUTHCODE = "oauth2:server:client_id:" + CLIENT_ID + ":api_scope:" + SCOPES_STRING;
    LoginActivity mActivity;
    SharedPreferences pref;
    AccountService accountService;
    String mEmail;
    ProgressDialog pDialog;

    PlusLoginTask(LoginActivity activity, String mEmail, AccountService accountService) {
        this.mActivity = activity;
        this.mEmail = mEmail;
        this.accountService = accountService;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(mActivity);
        pDialog.setMessage("Authenticating....");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean ok = false;
        if(!accountService.isLoggedIn()){
            String token = fetchToken();
            if(token != null){
                Log.d(getClass().getSimpleName(), "trying to login with token");
                ok = accountService.login(token);
                if(!ok){
                    GoogleAuthUtil.invalidateToken(mActivity, token);
                    Log.e("Token", "Server access denied: " + token);
                    mActivity.handleException(null);
                }else{
                    Log.i("Token", "Access Token retrieved:" + token);
                }
            }
        }
        else{
            ok = true;
        }
        return ok;
    }

    @Override
    protected void onPostExecute(Boolean ok) {
        pDialog.dismiss();
        if(ok){
            mActivity.onLoginSuccess();
        }
    }

    protected String fetchToken() {
        try {
            return GoogleAuthUtil.getToken(mActivity, mEmail, SCOPE_AUTHCODE);
        } catch (IOException e) {
            // The fetchToken() method handles Google-specific exceptions,
            // so this indicates something went wrong at a higher level.
            // TIP: Check for network connectivity before starting the AsyncTask.
            //TODO
            Log.e("AuthException", e.getMessage());
        } catch (UserRecoverableAuthException e) {
            // GooglePlayServices.apk is either old, disabled, or not present
            // so we need to show the user some UI in the activity to recover.
            mActivity.handleException(e);
            Log.e("AuthException", e.toString());
        } catch (GoogleAuthException e) {
            // Some other type of unrecoverable exception has occurred.
            // Report and log the error as appropriate for your app.
            Log.e("AuthException", e.getMessage());
        }
        return null;
    }
}

class RaycastLoginTask extends AsyncTask<Void, Void, Boolean> {
    AccountService accountService;
    LoginActivity mActivity;

    RaycastLoginTask(LoginActivity mActivity, AccountService accountService){
        this.accountService = accountService;
        this.mActivity = mActivity;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean ok = accountService.login(mActivity.usernameView.getText().toString(), mActivity.passwordView.getText().toString());
        return ok;
    }

    @Override
    protected void onPostExecute(Boolean ok) {
        if(ok){
            mActivity.onLoginSuccess();
        }
        else{
            mActivity.handleException(null);
        }
    }
}