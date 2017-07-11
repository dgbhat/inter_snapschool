package com.snapschool.mlps;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import Adapters.UserListAdapter;
import DataModels.UserInfo;

public class UserListActivity extends ActionBarActivity implements ServerManager.ServerResponseHandler {

    private String userType;
    private String classId;
    private List<UserInfo> userList;
    private List<UserInfo> showingUserList;
    private UserListAdapter adapter;
    private ServerManager serverManager;
    private final int IMAGE_REQUEST_BASE_TAG = 1000000;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.snapschool.mlps.R.layout.activity_user_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initValues();
    }

    // initialize values and settings
    public void initValues() {
        Intent intent = getIntent();
        userType = intent.getStringExtra("usertype");
        classId = intent.getStringExtra("classid");
        String className = intent.getStringExtra("classname");
        TextView userTypeText = (TextView)findViewById(com.snapschool.mlps.R.id.userType_textView);
        TextView classNameText = (TextView)findViewById(com.snapschool.mlps.R.id.className_textView);
        userTypeText.setText(userType);
        if (userType.equals("Students"))
            classNameText.setText(className);
        else
            classNameText.setText("");
        SearchView userSearchView = (SearchView)findViewById(com.snapschool.mlps.R.id.user_searchView);
        userSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUserList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchUserList(newText);
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("mlpsPrefs", 0);
        String authKey = preferences.getString("AUTHKEY", "");
        String loginType = preferences.getString("LOGINTYPE", "");
        progressDialog = new ProgressDialog(this);
        serverManager = new ServerManager(this);
        userList = new ArrayList<>();
        showingUserList = new ArrayList<>();
        ListView userListView = (ListView)findViewById(com.snapschool.mlps.R.id.users_listView);
        adapter = new UserListAdapter(this, showingUserList);
        userListView.setAdapter(adapter);
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showUserDetails(position);
            }
        });
        progressDialog.show();
        serverManager.getUserList(authKey, loginType, userType, classId, 0);
    }

    // serches an user in the list
    public void searchUserList(String searchQeury) {
        showingUserList.clear();
        for (int i=0;i<userList.size();i++) {
            if (userList.get(i).getUserName().toLowerCase().contains(searchQeury.toLowerCase())) {
                showingUserList.add(userList.get(i));
            }
        }
        adapter.notifyDataSetChanged();
    }

    // send image download request to server
    public void sendImageDownloadRequests() {
        for (int i=0;i<showingUserList.size();i++) {
            serverManager.downloadImage(showingUserList.get(i).getImageURL(), 60, IMAGE_REQUEST_BASE_TAG+i);
        }
    }

    // shows user details
    public void showUserDetails(int clickedPosition) {
        if (userType.equals("Students")) {
            Intent intent = new Intent(this, StudentProfileActivity.class);
            intent.putExtra("userId", userList.get(clickedPosition).getUserId());
            intent.putExtra("userType", userType);
            intent.putExtra("studentName", userList.get(clickedPosition).getUserName());
            intent.putExtra("imageUrl", userList.get(clickedPosition).getImageURL());
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, UserProfileActivity.class);
            intent.putExtra("userId", userList.get(clickedPosition).getUserId());
            intent.putExtra("userType", userType);
            intent.putExtra("userName", userList.get(clickedPosition).getUserName());
            intent.putExtra("imageUrl", userList.get(clickedPosition).getImageURL());
            startActivity(intent);
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
            JSONArray jsonArray = new JSONArray(response);
            String idKey = "";
            if (userType.equals("Students"))
                idKey = "student_id";
            if (userType.equals("Teachers"))
                idKey = "teacher_id";
            if (userType.equals("Parents"))
                idKey = "parent_id";
            for (int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String userId = jsonObject.optString(idKey);
                String userName = jsonObject.optString("name");
                String imageUrl = jsonObject.optString("image_url");
                userList.add(new UserInfo(userId, userName, imageUrl));
                showingUserList.add(new UserInfo(userId, userName, imageUrl));
            }
            adapter.notifyDataSetChanged();
            sendImageDownloadRequests();
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
        String alertMessage = "";
        switch (userType) {
            case "Students":
                alertMessage = "Could not load student list";
                break;
            case "Teachers":
                alertMessage = "Could not load teacher list";
                break;
            case "Parents":
                alertMessage = "Could not load parent list";
                break;
        }
        showAlert(alertMessage);
    }

    @Override
    public void imageDownloaded(Bitmap image, int requestTag) {
        int rowPosition = requestTag - IMAGE_REQUEST_BASE_TAG;
        showingUserList.get(rowPosition).setImage(image);
        adapter.notifyDataSetChanged();
    }
}
