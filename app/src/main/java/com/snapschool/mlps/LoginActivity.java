package com.snapschool.mlps;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends ActionBarActivity implements ServerManager.ServerResponseHandler {

    final private int GET_SYSTEM_INFO_REQUEST = 1000;
    final private int LOGIN_REQUEST = 1001;
    private ServerManager serverManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.snapschool.mlps.R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("mlpsPrefs", 0);
        if (preferences.getString("USERID", null) != null) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.snapschool.mlps.R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.snapschool.mlps.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initValues();
    }

    // initialize values and settings
    public void initValues() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        int logoSize = outMetrics.heightPixels/6;
        ImageView logoView = (ImageView)findViewById(com.snapschool.mlps.R.id.logo_imageView);
        logoView.getLayoutParams().height = logoSize;
        logoView.getLayoutParams().width = logoSize;
        EditText email = (EditText)findViewById(com.snapschool.mlps.R.id.email_editText);
        email.setText("");
        EditText password = (EditText)findViewById(com.snapschool.mlps.R.id.password_editText);
        password.setText("");
        serverManager = new ServerManager(this);
        serverManager.getSystemInfo(GET_SYSTEM_INFO_REQUEST);
        progressDialog = new ProgressDialog(this);
    }

    // login action
    public void loginAction(View view) {
        EditText emailText = (EditText)findViewById(com.snapschool.mlps.R.id.email_editText);
        String email = emailText.getText().toString().trim();
        if (email.length() == 0) {
            emailText.setError("Enter email address");
            return;
        }
        EditText passwordText = (EditText)findViewById(com.snapschool.mlps.R.id.password_editText);
        String password = passwordText.getText().toString().trim();
        if (password.length() == 0) {
            passwordText.setError("Enter password");
            return;
        }
        progressDialog.show();
        serverManager.login(email, password, LOGIN_REQUEST);
    }

    // forgot password button action
    public void forgotPasswordAction(View view) {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    // server response methods
    @Override
    public void requestFinished(String response, int requestTag) {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
        try {
            JSONObject responseJson = new JSONObject(response);
            if (requestTag == GET_SYSTEM_INFO_REQUEST) {
                TextView schoolName = (TextView)findViewById(com.snapschool.mlps.R.id.app_name_textView);
                schoolName.setText(responseJson.optString("system_name"));
            }
            if (requestTag == LOGIN_REQUEST) {
                if (responseJson.optString("status").equals("success")) {
                    String loginType = responseJson.optString("login_type");
                    String userId = responseJson.optString("login_user_id");
                    String userName = responseJson.optString("name");
                    String authKey = responseJson.optString("authentication_key");
                    SharedPreferences preferences = getApplicationContext().getSharedPreferences("mlpsPrefs", 0);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("LOGINTYPE", loginType);
                    editor.putString("USERID", userId);
                    editor.putString("USERNAME", userName);
                    editor.putString("AUTHKEY", authKey);
                    if (loginType.equals("student")) {
                        editor.putString("CLASSID", responseJson.optString("class_id"));
                    }
                    editor.apply();
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                }
                else
                    showAlert("Login failed");
            }
        }
        catch (JSONException e) {
            //do nothing
        }
    }

    @Override
    public void requestFailed(String errorMessage, int requestTag) {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
        if (requestTag == LOGIN_REQUEST)
            showAlert("Login failed");
    }

    @Override
    public void imageDownloaded(Bitmap image, int requestTag) {

    }

    // shows an alert
    public void showAlert(String message) {
        AlertDialog alert = (new AlertDialog.Builder(this)).setMessage(message).setPositiveButton("Ok", null).create();
        alert.show();
    }
}
