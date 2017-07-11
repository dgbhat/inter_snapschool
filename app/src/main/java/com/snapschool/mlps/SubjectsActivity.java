package com.snapschool.mlps;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import Adapters.SelectionListAdapter;
import Adapters.SubjectListAdapter;
import DataModels.ClassInfo;
import DataModels.SubjectInfo;

public class SubjectsActivity extends ActionBarActivity implements ServerManager.ServerResponseHandler {

    private ServerManager serverManager;
    private String loginType;
    private String authKey;
    private List<ClassInfo> classList;
    private List<SubjectInfo> subjectList;
    private String selectedClassId;
    private SubjectListAdapter adapter;

    private final int GET_CLASS_LIST_REQUEST = 1000;
    private final int GET_SUBJECT_LIST_REQUEST = 1001;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.snapschool.mlps.R.layout.activity_subjects);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initValues();
    }

    // initialize values and settings
    public void initValues() {
        serverManager = new ServerManager(this);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("mlpsPrefs", 0);
        loginType = sharedPreferences.getString("LOGINTYPE", "");
        authKey = sharedPreferences.getString("AUTHKEY", "");
        progressDialog = new ProgressDialog(this);
        classList = new ArrayList<>();
        Spinner classSpinner = (Spinner)findViewById(com.snapschool.mlps.R.id.classList_spinner);
        classSpinner.setVisibility(View.INVISIBLE);
        if (loginType.equals("student")) {
            String classId = sharedPreferences.getString("CLASSID", "");
            progressDialog.show();
            serverManager.getSubjectList(authKey, loginType, classId, GET_SUBJECT_LIST_REQUEST);
        }
        else {
            classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    getSubjectList(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            serverManager.getClassList(authKey, loginType, GET_CLASS_LIST_REQUEST);
        }
        selectedClassId = "";
        subjectList = new ArrayList<>();
        adapter = new SubjectListAdapter(this, subjectList);
        ListView subjectListView = (ListView)findViewById(com.snapschool.mlps.R.id.subjects_listView);
        subjectListView.setAdapter(adapter);
    }

    // shows subject list fetched from server
    public void getSubjectList(int clickedIndex) {
        if (clickedIndex == 0) {
            subjectList.clear();
            adapter.notifyDataSetChanged();
        }
        else {
            String newSelectedClassId = classList.get(clickedIndex-1).getClassId();
            if (!newSelectedClassId.equals(selectedClassId)) {
                selectedClassId = newSelectedClassId;
                progressDialog.show();
                serverManager.getSubjectList(authKey, loginType, selectedClassId, GET_SUBJECT_LIST_REQUEST);
            }
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
            if (requestTag == GET_CLASS_LIST_REQUEST) {
                JSONArray jsonArray = new JSONArray(response);
                List<String> classNames = new ArrayList<>();
                classNames.add("Select class");
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String classId = jsonObject.optString("class_id");
                    String className = jsonObject.optString("name");
                    classNames.add(className);
                    classList.add(new ClassInfo(classId, className));
                }
                SelectionListAdapter selectionListAdapter = new SelectionListAdapter(this, classNames);
                Spinner classSpinner = (Spinner)findViewById(com.snapschool.mlps.R.id.classList_spinner);
                classSpinner.setVisibility(View.VISIBLE);
                classSpinner.setAdapter(selectionListAdapter);
            }
            else if (requestTag == GET_SUBJECT_LIST_REQUEST) {
                JSONArray jsonArray = new JSONArray(response);
                subjectList.clear();
                subjectList.add(new SubjectInfo("", "Subject", "Teacher"));
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String subjectName = jsonObject.optString("name");
                    String teacherName = jsonObject.optString("teacher_name");
                    subjectList.add(new SubjectInfo("", subjectName, teacherName));
                }
                adapter.notifyDataSetChanged();
            }
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
        String message;
        if (requestTag == GET_CLASS_LIST_REQUEST)
            message = "Failed to load class list";
        else
            message = "Failed to load subject list";
        showAlert(message);
    }

    @Override
    public void imageDownloaded(Bitmap image, int requestTag) {

    }
}
