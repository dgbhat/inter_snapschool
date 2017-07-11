package com.snapschool.mlps;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.*;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import Adapters.ClassRoutineListAdapter;
import Adapters.SelectionListAdapter;
import DataModels.ClassInfo;
import DataModels.ClassRoutineInfo;
import DataModels.SectionInfo;

public class ClassRoutineActivity extends ActionBarActivity implements ServerManager.ServerResponseHandler{

    private int selectedClass;
    private int selectedSection;
    private int selectedDay;
    private List<ClassInfo> classList;
    private List<SectionInfo> sectionList;
    private ServerManager serverManager;
    private String authKey;
    private String loginType;
    private final int GET_CLASS_LIST_REQUEST = 1000;
    private final int GET_CLASS_ROUTINE_REQUEST = 1001;
    private final int GET_STUDENT_PROFILE_REQUEST = 1002;
    private List<ClassRoutineInfo> classesOfDay;
    private ClassRoutineListAdapter classRoutineAdapter;
    private SelectionListAdapter sectionListAdapter;
    private List<String> sectionNames;
    private List<String> sectionIDsOfClass;
    private ProgressDialog progressDialog;
    private List<String> dayNames = Arrays.asList("select day", "sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday");
    private String classID, sectionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.snapschool.mlps.R.layout.activity_class_routine);
        initValues();
    }

    // initialize values and settings
    public void initValues() {
        selectedClass = -1;
        selectedSection = -1;
        selectedDay = -1;

        serverManager = new ServerManager(this);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("mlpsPrefs", 0);
        authKey = sharedPreferences.getString("AUTHKEY", "");
        loginType = sharedPreferences.getString("LOGINTYPE", "");

        if (loginType.equals("student") || loginType.equals("parent")) {
            Spinner classSpinner = (Spinner)findViewById(com.snapschool.mlps.R.id.class_list_spinner);
            classSpinner.setVisibility(View.INVISIBLE);
            Spinner sectionSpinner = (Spinner)findViewById(com.snapschool.mlps.R.id.section_list_spinner);
            sectionSpinner.setVisibility(View.INVISIBLE);
            classID = "";
            sectionID = "";
            Intent intent = getIntent();
            String studentID = intent.getStringExtra("studentId");
            serverManager.getUserProfile(authKey, loginType, studentID, "Students", GET_STUDENT_PROFILE_REQUEST);
        }
        else
            serverManager.getClassList(authKey, loginType, GET_CLASS_LIST_REQUEST);

        SelectionListAdapter daySelectionListAdapter = new SelectionListAdapter(this, dayNames);
        Spinner daySpinner = (Spinner)findViewById(com.snapschool.mlps.R.id.day_list_spinner);
        daySpinner.setAdapter(daySelectionListAdapter);
        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDay = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sectionNames = new ArrayList<>();
        sectionNames.add("select section");
        sectionIDsOfClass = new ArrayList<>();
        sectionListAdapter = new SelectionListAdapter(this, sectionNames);
        Spinner sectionSpinner = (Spinner)findViewById(com.snapschool.mlps.R.id.section_list_spinner);
        sectionSpinner.setAdapter(sectionListAdapter);
        sectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSection = position-1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        classesOfDay = new ArrayList<>();
        classRoutineAdapter = new ClassRoutineListAdapter(this, classesOfDay);
        ListView classRoutineListView = (ListView)findViewById(com.snapschool.mlps.R.id.classRoutine_listView);
        classRoutineListView.setAdapter(classRoutineAdapter);
        progressDialog = new ProgressDialog(this);
    }

    // shows an alert
    void showAlert(String alertMessage) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(alertMessage);
        alertDialogBuilder.setPositiveButton("Ok", null);
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    // shows class routine of the day fetched from server
    public void viewButtonAction(View view) {
        if (loginType.equals("admin") || loginType.equals("teacher")) {
            if (selectedClass == -1)
                showAlert("Select a class");
            else if (selectedSection == -1)
                showAlert("Select a section");
            else if (selectedDay < 1)
                showAlert("Select a day");
            else {
                progressDialog.show();
                serverManager.getClassRoutineOfDay(authKey, loginType, classList.get(selectedClass).getClassId(), sectionIDsOfClass.get(selectedSection), dayNames.get(selectedDay), GET_CLASS_ROUTINE_REQUEST);
            }
        }
        else {
            if (classID.equals("") || sectionID.equals(""))
                showAlert("Student profile information not found");
            else if (selectedDay < 1)
                showAlert("Select a day");
            else {
                progressDialog.show();
                serverManager.getClassRoutineOfDay(authKey, loginType, classID, sectionID, dayNames.get(selectedDay), GET_CLASS_ROUTINE_REQUEST);
            }
        }
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
                classList = new ArrayList<>();
                sectionList = new ArrayList<>();
                List<String> classNames = new ArrayList<>();
                classNames.add("Select class");
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String classId = jsonObject.optString("class_id");
                    String className = jsonObject.optString("name");
                    classList.add(new ClassInfo(classId, className));
                    classNames.add(className);
                    JSONArray sectionArray = jsonObject.getJSONArray("sections");
                    for (int j=0;j<sectionArray.length();j++) {
                        JSONObject sectionObject = sectionArray.getJSONObject(j);
                        String sectionId = sectionObject.optString("section_id");
                        String sectionName = sectionObject.optString("name");
                        String associatedClassId = sectionObject.optString("class_id");
                        sectionList.add(new SectionInfo(sectionId, sectionName, associatedClassId));
                    }
                }
                SelectionListAdapter selectionListAdapter = new SelectionListAdapter(this, classNames);
                Spinner classSpinner = (Spinner)findViewById(com.snapschool.mlps.R.id.class_list_spinner);
                classSpinner.setAdapter(selectionListAdapter);
                classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedClass = position-1;
                        if (selectedClass >= 0) {
                            String selectedClassId = classList.get(selectedClass).getClassId();
                            sectionNames.clear();
                            if (sectionIDsOfClass.size() > 0)
                                sectionIDsOfClass.clear();
                            sectionNames.add("select section");
                            for (int i=0;i<sectionList.size();i++) {
                                if (sectionList.get(i).getClassID().equals(selectedClassId)) {
                                    sectionNames.add(sectionList.get(i).getSectionName());
                                    sectionIDsOfClass.add(sectionList.get(i).getSectionID());
                                }
                            }
                            sectionListAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
            else if (requestTag == GET_CLASS_ROUTINE_REQUEST) {
                JSONArray jsonArray = new JSONArray(response);
                classesOfDay.clear();
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String subjectName = jsonObject.optString("subject");
                    String startHour = jsonObject.optString("time_start");
                    String startMinute = jsonObject.optString("time_start_min");
                    String endHour = jsonObject.optString("time_end");
                    String endMinute = jsonObject.optString("time_end_min");
                    classesOfDay.add(new ClassRoutineInfo(subjectName, startHour, startMinute, endHour, endMinute));
                }
                classRoutineAdapter.notifyDataSetChanged();
            }
            else if (requestTag == GET_STUDENT_PROFILE_REQUEST) {
                JSONArray jsonArray = new JSONArray(response);
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    classID = jsonObject.optString("class");
                    sectionID = jsonObject.optString("section");
                }
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
        else if (requestTag == GET_CLASS_ROUTINE_REQUEST)
            message = "Failed to load class routine";
        else
            message = "Failed to load student profile";
        showAlert(message);
    }

    @Override
    public void imageDownloaded(Bitmap image, int requestTag) {

    }
}
