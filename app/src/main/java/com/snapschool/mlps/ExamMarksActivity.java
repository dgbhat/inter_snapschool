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

import Adapters.ExamMarksListAdapter;
import Adapters.SelectionListAdapter;
import DataModels.ClassInfo;
import DataModels.ExamInfo;
import DataModels.MarkInfo;
import DataModels.SubjectInfo;

public class ExamMarksActivity extends ActionBarActivity implements ServerManager.ServerResponseHandler {

    private SelectionListAdapter examListAdapter;
    private List<String> examNames;
    private SelectionListAdapter classListAdapter;
    private List<String> classNames;
    private SelectionListAdapter subjectListAdapter;
    private List<String> subjectNames;
    private String authKey;
    private String loginType;
    private ServerManager serverManager;
    private List<ExamInfo> examList;
    private List<ClassInfo> classList;
    private List<SubjectInfo> subjectList;
    private int selectedExam;
    private int selectedClass;
    private int selectedSubject;
    private List<MarkInfo> markList;
    private ExamMarksListAdapter examMarksListAdapter;

    private final int GET_EXAM_LIST_REQUEST = 1000;
    private final int GET_CLASS_LIST_REQUEST = 1001;
    private final int GET_SUBJECT_OF_CLASS_REQUEST = 1002;
    private final int GET_MARKS_REQUEST = 1003;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.snapschool.mlps.R.layout.activity_exam_marks);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initValues();
    }

    // initialize values and settings
    public void initValues() {
        serverManager = new ServerManager(this);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("mlpsPrefs", 0);
        authKey = sharedPreferences.getString("AUTHKEY", "");
        loginType = sharedPreferences.getString("LOGINTYPE", "");
        setExamListSpinner();
        setClassListSpinner();
        setSubjectListSpinner();
        markList = new ArrayList<>();
        examMarksListAdapter = new ExamMarksListAdapter(this, markList);
        ListView examMarksListView = (ListView)findViewById(com.snapschool.mlps.R.id.exam_marks_listView);
        examMarksListView.setAdapter(examMarksListAdapter);
        progressDialog = new ProgressDialog(this);
    }

    // configuring the exam list spinner
    public void setExamListSpinner() {
        examNames = new ArrayList<>();
        examNames.add("Exams");
        examListAdapter = new SelectionListAdapter(this, examNames);
        Spinner examListSpinner = (Spinner)findViewById(com.snapschool.mlps.R.id.exam_list_spinner);
        examListSpinner.setAdapter(examListAdapter);
        examListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedExam = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        examList = new ArrayList<>();
        selectedExam = 0;
        serverManager.getExamList(authKey, loginType, GET_EXAM_LIST_REQUEST);
    }

    // configuring the class list spinner
    public void setClassListSpinner() {
        classNames = new ArrayList<>();
        classNames.add("Classes");
        classListAdapter = new SelectionListAdapter(this, classNames);
        Spinner classListSpinner = (Spinner)findViewById(com.snapschool.mlps.R.id.class_list_spinner);
        classListSpinner.setAdapter(classListAdapter);
        classListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedClass = position;
                if (selectedClass > 0) {
                    serverManager.getSubjectList(authKey, loginType, classList.get(selectedClass - 1).getClassId(), GET_SUBJECT_OF_CLASS_REQUEST);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        classList = new ArrayList<>();
        selectedClass = 0;
        serverManager.getClassList(authKey, loginType, GET_CLASS_LIST_REQUEST);
    }

    // configuring the subject list spinner
    public void setSubjectListSpinner() {
        subjectList = new ArrayList<>();
        subjectNames = new ArrayList<>();
        subjectNames.add("Subjects");
        subjectListAdapter = new SelectionListAdapter(this, subjectNames);
        Spinner subjectListSpinner = (Spinner)findViewById(com.snapschool.mlps.R.id.subject_list_spinner);
        subjectListSpinner.setAdapter(subjectListAdapter);
        subjectListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSubject = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // shows exam marks information fetched from server
    public void viewButtonAction(View view) {
        if (selectedExam>0 && selectedClass>0 && selectedSubject>0) {
            progressDialog.show();
            serverManager.getMarks(authKey, loginType, examList.get(selectedExam - 1).getExamId(), classList.get(selectedClass - 1).getClassId(), subjectList.get(selectedSubject - 1).getSubjectId(), GET_MARKS_REQUEST);
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
            if (requestTag == GET_EXAM_LIST_REQUEST) {
                JSONArray jsonArray = new JSONArray(response);
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String examId = jsonObject.optString("exam_id");
                    String examName = jsonObject.optString("name");
                    examNames.add(examName);
                    examList.add(new ExamInfo(examId, examName));
                }
                examListAdapter.notifyDataSetChanged();
            }
            else if (requestTag == GET_CLASS_LIST_REQUEST) {
                JSONArray jsonArray = new JSONArray(response);
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String classId = jsonObject.optString("class_id");
                    String className = jsonObject.optString("name");
                    classNames.add(className);
                    classList.add(new ClassInfo(classId, className));
                }
                classListAdapter.notifyDataSetChanged();
            }
            else if (requestTag == GET_SUBJECT_OF_CLASS_REQUEST) {
                JSONArray jsonArray = new JSONArray(response);
                subjectList.clear();
                subjectNames.clear();
                subjectNames.add("Subjects");
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String subjectId = jsonObject.optString("subject_id");
                    String subjectName = jsonObject.optString("name");
                    subjectList.add(new SubjectInfo(subjectId, subjectName, ""));
                    subjectNames.add(subjectName);
                }
                subjectListAdapter.notifyDataSetChanged();
            }
            else if (requestTag == GET_MARKS_REQUEST) {
                JSONArray jsonArray = new JSONArray(response);
                markList.clear();
                markList.add(new MarkInfo("Roll", "Name", "Marks"));
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String roll = jsonObject.optString("student_roll");
                    String name = jsonObject.optString("student_name");
                    String marks = jsonObject.optString("mark_obtained");
                    markList.add(new MarkInfo(roll, name, marks));
                }
                examMarksListAdapter.notifyDataSetChanged();
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
        String message = "";
        if (requestTag == GET_EXAM_LIST_REQUEST)
            message = "Failed to load exam list";
        else if (requestTag == GET_CLASS_LIST_REQUEST)
            message = "Failed to load class list";
        else if (requestTag == GET_SUBJECT_OF_CLASS_REQUEST)
            message = "Failed to load subject list of the class";
        else if (requestTag == GET_MARKS_REQUEST)
            message = "Failed to load exam marks";
        showAlert(message);
    }

    @Override
    public void imageDownloaded(Bitmap image, int requestTag) {

    }
}
