package com.snapschool.mlps;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.snapschool.mlps.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyProfileActivity extends ActionBarActivity implements ServerManager.ServerResponseHandler{
    private ServerManager serverManager;
    private String authKey;
    private String loginType;
    private String userId;
    private String name;
    private String email;
    private String imageUrl;
    private int profileImageSize;

    private final int GET_MY_PROFILE_REQUEST = 1000;
    private final int UPDATE_PROFILE_REQUEST = 1001;
    private final int UPDATE_PASSWORD_REQUEST = 1002;
    private ProgressDialog progressDialog;
    private ImageView profileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.snapschool.mlps.R.layout.activity_my_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initValues();
    }

    // initialize values and settings
    public void initValues() {
        serverManager = new ServerManager(this);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("mlpsPrefs", 0);
        authKey = sharedPreferences.getString("AUTHKEY", "");
        loginType = sharedPreferences.getString("LOGINTYPE", "");
        userId = sharedPreferences.getString("USERID", "");
        name = "";
        email = "";
        imageUrl = "";
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        serverManager.getMyProfile(authKey, loginType, userId, GET_MY_PROFILE_REQUEST);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        profileImageSize = displayMetrics.heightPixels/6;
        profileImageView = (ImageView)findViewById(com.snapschool.mlps.R.id.my_profile_imageView);
        profileImageView.getLayoutParams().height = profileImageSize;
        profileImageView.getLayoutParams().width = profileImageSize;
    }

    // profile update action
    public void profileUpdateButtonAction(View view) {
        EditText nameText = (EditText)findViewById(R.id.name_editText);
        EditText emailText = (EditText)findViewById(R.id.email_editText);
        String updatedName = nameText.getText().toString().trim();
        String updatedEmail = emailText.getText().toString().trim();
        if (updatedName.isEmpty()) {
            updatedName = name;
        }
        if (updatedEmail.isEmpty()) {
            updatedEmail = email;
        }
        progressDialog.show();
        serverManager.updateProfile(authKey, loginType, userId, updatedName, updatedEmail, UPDATE_PROFILE_REQUEST);
    }

    // password update action
    public void passwordUpdateButtonAction(View view) {
        EditText currentPasswordText = (EditText)findViewById(com.snapschool.mlps.R.id.current_password_editText);
        String currentPassword = currentPasswordText.getText().toString().trim();
        if (currentPassword.isEmpty()) {
            currentPasswordText.setError("Enter current password");
            return;
        }
        EditText newPasswordText = (EditText)findViewById(com.snapschool.mlps.R.id.new_password_editText);
        String newPassword = newPasswordText.getText().toString().trim();
        if (newPassword.isEmpty()) {
            newPasswordText.setError("Enter new password");
            return;
        }
        EditText confirmNewPasswordText = (EditText)findViewById(com.snapschool.mlps.R.id.confirm_new__password_editText);
        String confirmNewPassword = confirmNewPasswordText.getText().toString().trim();
        if (confirmNewPassword.isEmpty()) {
            confirmNewPasswordText.setError("Re type new password");
            return;
        }
        if (!newPassword.equals(confirmNewPassword)) {
            confirmNewPasswordText.setError("Re type new password correctly");
            return;
        }
        progressDialog.show();
        serverManager.updatePassword(authKey, loginType, userId, newPassword, currentPassword, UPDATE_PASSWORD_REQUEST);
    }

    // shows an alert
    public void showAlert(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
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
            if (requestTag == GET_MY_PROFILE_REQUEST) {
                JSONArray jsonArray = new JSONArray(response);
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    name = jsonObject.optString("name");
                    email = jsonObject.optString("email");
                    imageUrl = jsonObject.optString("image_url");
                    EditText nameText = (EditText)findViewById(com.snapschool.mlps.R.id.name_editText);
                    nameText.setText(name);
                    EditText emailText = (EditText)findViewById(com.snapschool.mlps.R.id.email_editText);
                    emailText.setText(email);
                    serverManager.downloadImage(imageUrl, profileImageSize, 0);
                }
            }
            else if (requestTag == UPDATE_PROFILE_REQUEST) {
                JSONObject jsonObject = new JSONObject(response);
                String updateStatus = jsonObject.optString("update_status");
                String updateMessage;
                if (updateStatus.equals("success"))
                    updateMessage = "Profile updated successfully";
                else
                    updateMessage = "Profile update failed";
                showAlert(updateMessage);
            }
            else if (requestTag == UPDATE_PASSWORD_REQUEST) {
                JSONObject jsonObject = new JSONObject(response);
                String updateStatus = jsonObject.optString("update_status");
                String updateMessage;
                if (updateStatus.equals("success"))
                    updateMessage = "Password updated successfully";
                else
                    updateMessage = "Password update failed";
                showAlert(updateMessage);
            }
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
        String message = "";
        if (requestTag == GET_MY_PROFILE_REQUEST)
            message = "Could not profile information";
        else if (requestTag == UPDATE_PROFILE_REQUEST)
            message = "Error in updating";
        showAlert(message);
    }

    @Override
    public void imageDownloaded(Bitmap image, int requestTag) {
        profileImageView.setImageBitmap(image);
    }
}
