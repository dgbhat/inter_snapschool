package com.snapschool.mlps;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.*;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Adapters.StudentAccountingListAdapter;
import DataModels.AccountingInfo;

public class StudentAccountingActivity extends ActionBarActivity implements ServerManager.ServerResponseHandler{
    private List<AccountingInfo> studentAccountingInfoList;
    private StudentAccountingListAdapter studentAccountingListAdapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.snapschool.mlps.R.layout.activity_student_accounting);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initValues();
    }

    // initialize values and settings
    public void initValues() {
        studentAccountingInfoList = new ArrayList<>();
        studentAccountingListAdapter = new StudentAccountingListAdapter(this, studentAccountingInfoList);
        ListView studentAccountingListView = (ListView)findViewById(com.snapschool.mlps.R.id.student_accounting_listView);
        studentAccountingListView.setAdapter(studentAccountingListAdapter);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("mlpsPrefs", 0);
        String loginType = sharedPreferences.getString("LOGINTYPE", "");
        String authKey = sharedPreferences.getString("AUTHKEY", "");
        String studentId = "";
        if (loginType.equals("student"))
            studentId = sharedPreferences.getString("USERID", "");
        else if (loginType.equals("parent")) {
            Intent intent = getIntent();
            studentId = intent.getStringExtra("childId");
        }
        progressDialog = new ProgressDialog(this);
        ServerManager serverManager = new ServerManager(this);
        progressDialog.show();
        serverManager.getStudentAccountingInfo(authKey, loginType, studentId, 0);
    }

    // shows an alert
    public void showAlert(String alertMessage) {
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
            studentAccountingInfoList.add(new AccountingInfo("Title", "Amount", "Due", "Date"));
            for (int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String title = jsonObject.optString("title");
                String amount = jsonObject.optString("amount");
                String dueAmount = jsonObject.optString("due");
                String timestamp = jsonObject.optString("creation_timestamp");
                studentAccountingInfoList.add(new AccountingInfo(title, amount, dueAmount, timestamp));
            }
            studentAccountingListAdapter.notifyDataSetChanged();
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
        showAlert("Could not load accounting information");
    }

    @Override
    public void imageDownloaded(Bitmap image, int requestTag) {

    }
}
