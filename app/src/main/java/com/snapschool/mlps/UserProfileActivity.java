package com.snapschool.mlps;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import Adapters.UserProfileAdapter;

public class UserProfileActivity extends ActionBarActivity implements ServerManager.ServerResponseHandler{

    private String userType;
    private List<String> captions;
    private List<String> userInformations;
    private UserProfileAdapter adapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.snapschool.mlps.R.layout.activity_user_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initValues();
    }

    // initialize values and settings
    public void initValues() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        int userImageSize = outMetrics.heightPixels/8;
        ImageView userImageView = (ImageView)findViewById(com.snapschool.mlps.R.id.user_imageView);
        userImageView.getLayoutParams().height = userImageSize;
        userImageView.getLayoutParams().width = userImageSize;
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        String userName = intent.getStringExtra("userName");
        String imageUrl = intent.getStringExtra("imageUrl");
        userType = intent.getStringExtra("userType");
        TextView userNameText = (TextView)findViewById(com.snapschool.mlps.R.id.username_textView);
        userNameText.setText(userName);
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("mlpsPrefs", 0);
        String authKey = preferences.getString("AUTHKEY", "");
        String loginType = preferences.getString("LOGINTYPE", "");
        captions = new ArrayList<>();
        userInformations = new ArrayList<>();
        ServerManager serverManager = new ServerManager(this);
        progressDialog = new ProgressDialog(this);
        adapter = new UserProfileAdapter(this, captions, userInformations);
        ListView userProfileListView = (ListView)findViewById(com.snapschool.mlps.R.id.profile_listView);
        userProfileListView.setAdapter(adapter);
        progressDialog.show();
        serverManager.getUserProfile(authKey, loginType, userId, userType, 0);
        serverManager.downloadImage(imageUrl, userImageSize, 0);
    }

    // shows an alert
    void showAlert(String alertMessage) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(alertMessage);
        alertDialogBuilder.setPositiveButton("Ok", null);
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    // handles click on phone number and email address
    public void profileTextClicked(View view) {
        if (view.getTag().equals("Phone")) {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            String phoneNo;
            if (userType.equals("Teachers"))
                phoneNo = userInformations.get(4);
            else
                phoneNo = userInformations.get(1);
            callIntent.setData(Uri.parse("tel:"+phoneNo));
            startActivity(callIntent);
        }
        else if (view.getTag().equals("Email")) {
            Intent i = new Intent(Intent.ACTION_SEND);
            String emailAddress;
            if (userType.equals("Teachers"))
                emailAddress = userInformations.get(5);
            else
                emailAddress = userInformations.get(2);
            i.putExtra(android.content.Intent.EXTRA_EMAIL, emailAddress);
            startActivity(Intent.createChooser(i, "Send email"));
        }
    }

    // server response methods
    @Override
    public void requestFinished(String response, int requestTag) {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
        boolean jsonError = false;
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (userType.equals("Teachers")) {
                captions.add("Name");
                captions.add("Birthday");
                captions.add("Gender");
                captions.add("Address");
                captions.add("Phone");
                captions.add("Email");
                userInformations.add(jsonObject.optString("name"));
                userInformations.add(jsonObject.optString("birthday"));
                userInformations.add(jsonObject.optString("sex"));
                userInformations.add(jsonObject.optString("address"));
                userInformations.add(jsonObject.optString("phone"));
                userInformations.add(jsonObject.optString("email"));
            }
            else {
                captions.add("Name");
                captions.add("Phone");
                captions.add("Email");
                captions.add("Address");
                captions.add("Profession");
                userInformations.add(jsonObject.optString("name"));
                userInformations.add(jsonObject.optString("phone"));
                userInformations.add(jsonObject.optString("email"));
                userInformations.add(jsonObject.optString("address"));
                userInformations.add(jsonObject.optString("profession"));
            }
            adapter.notifyDataSetChanged();
        }
        catch (JSONException e) {
            jsonError = true;
        }
        if (jsonError) {
            showAlert("An error occurred");
        }
    }

    @Override
    public void requestFailed(String errorMessage, int requestTag) {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
        showAlert("Could not load profile information");
    }

    @Override
    public void imageDownloaded(Bitmap image, int requestTag) {
        ImageView userImageView = (ImageView)findViewById(com.snapschool.mlps.R.id.user_imageView);
        userImageView.setImageBitmap(image);
    }
}
