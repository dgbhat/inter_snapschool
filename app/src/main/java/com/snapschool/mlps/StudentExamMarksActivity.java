package com.snapschool.mlps;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ExpandableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Adapters.StudentMarkAdapter;
import DataModels.StudentMarkInfo;

public class StudentExamMarksActivity extends ActionBarActivity implements ServerManager.ServerResponseHandler {

    private String authKey;
    private String loginType;
    private ServerManager serverManager;
    private List<String> examNames;
    private HashMap<String, String> exams;
    private HashMap<String, List<StudentMarkInfo>> marks;
    private StudentMarkAdapter markAdapter;
    private String studentId;

    private final int GET_EXAM_LIST_REQUEST = 1000;
    private final int MARKS_INFORMATION_REQUEST = 1001;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.snapschool.mlps.R.layout.activity_student_exam_marks);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initValues();
    }

    // initialize values and settings
    public void initValues() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("mlpsPrefs", 0);
        authKey = sharedPreferences.getString("AUTHKEY", "");
        loginType = sharedPreferences.getString("LOGINTYPE", "");
        if (loginType.equals("student"))
            studentId = sharedPreferences.getString("USERID", "");
        else if (loginType.equals("parent")) {
            Intent intent = getIntent();
            studentId = intent.getStringExtra("childId");
        }
        serverManager = new ServerManager(this);
        examNames = new ArrayList<>();
        marks = new HashMap<>();
        ExpandableListView studentMarkListView = (ExpandableListView)findViewById(com.snapschool.mlps.R.id.student_exam_marks_listView);
        markAdapter = new StudentMarkAdapter(this, examNames, marks);
        studentMarkListView.setAdapter(markAdapter);
        serverManager.getExamList(authKey, loginType, GET_EXAM_LIST_REQUEST);
        progressDialog = new ProgressDialog(this);
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
            if (requestTag == GET_EXAM_LIST_REQUEST) {
                JSONArray jsonArray = new JSONArray(response);
                exams = new HashMap<>();
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String examId = jsonObject.optString("exam_id");
                    String examName = jsonObject.optString("name");
                    examNames.add(examName);
                    exams.put(examId, examName);
                    marks.put(examNames.get(i), new ArrayList<StudentMarkInfo>());
                    if (!progressDialog.isShowing())
                        progressDialog.show();
                    serverManager.getStudentMarksOfExam(authKey, loginType, studentId, examId, MARKS_INFORMATION_REQUEST);
                }
                markAdapter.notifyDataSetChanged();
            }
            else if (requestTag == MARKS_INFORMATION_REQUEST) {
                JSONObject jsonObject = new JSONObject(response);
                String examId = jsonObject.optString("exam_id");
                JSONArray jsonArray = jsonObject.getJSONArray("marks");
                List<StudentMarkInfo> marksOfExam = new ArrayList<>();
                marksOfExam.add(new StudentMarkInfo("Subject", "Mark", "Grade"));
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject marksJsonObj = jsonArray.getJSONObject(i);
                    String subject = marksJsonObj.optString("subject");
                    String mark = marksJsonObj.optString("mark_obtained");
                    String grade = marksJsonObj.optString("grade");
                    if (grade.equals("null"))
                        grade = "";
                    marksOfExam.add(new StudentMarkInfo(subject, mark, grade));
                }
                marks.put(exams.get(examId), marksOfExam);
                markAdapter.notifyDataSetChanged();
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
        String alertMessage = "";
        if (requestTag == GET_EXAM_LIST_REQUEST)
            alertMessage = "Could not load exam list";
        else if (requestTag == MARKS_INFORMATION_REQUEST)
            alertMessage = "Could not load exam marks information";
        showAlert(alertMessage);
    }

    @Override
    public void imageDownloaded(Bitmap image, int requestTag) {

    }
}
