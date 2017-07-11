package com.snapschool.mlps;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordActivity extends ActionBarActivity implements ServerManager.ServerResponseHandler {
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.snapschool.mlps.R.layout.activity_forgot_password);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initValues();
    }

    // initialize values and settings
    public void initValues() {
        progressDialog = new ProgressDialog(this);
    }

    // submits email to reset password
    public void submitAction(View view) {
        EditText emailText = (EditText)findViewById(com.snapschool.mlps.R.id.email_editText);
        String email = emailText.getText().toString().trim();
        if (email.isEmpty())
            emailText.setError("Enter email address");
        else {
            ServerManager serverManager = new ServerManager(this);
            progressDialog.show();
            serverManager.resetPassword(email, 1000);
        }
    }

    // shows an alert
    void showAlert(String alertMessage) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(alertMessage);
        alertDialogBuilder.setPositiveButton("Ok", null);
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    // server response methods
    @Override
    public void requestFinished(String response, int requestTag) {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
        boolean jsonError = false;
        try {
            JSONObject jsonObject = new JSONObject(response);
            String status = jsonObject.optString("status");
            String alertMessage;
            if (status.equals("success"))
                alertMessage = "Password has been sent to your email address";
            else
                alertMessage = "An error occurred";
            showAlert(alertMessage);
        }
        catch (JSONException e) {
            jsonError = true;
        }
        if (jsonError)
            showAlert("An error occurred");
    }

    @Override
    public void requestFailed(String errorMessage, int requestTag) {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
        showAlert("An error occurred");
    }

    @Override
    public void imageDownloaded(Bitmap image, int requestTag) {

    }
}
