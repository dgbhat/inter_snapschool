package com.snapschool.mlps;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Adapters.AttendanceListAdapter;
import Adapters.SelectionListAdapter;
import DataModels.AttendanceInfo;
import DataModels.ClassInfo;

public class AttendanceActivity extends ActionBarActivity implements ServerManager.ServerResponseHandler {

    private SelectionListAdapter classListAdapter;
    private List<String> classNames;
    private ServerManager serverManager;
    private String authKey;
    private String loginType;
    private String[] monthNames;
    private int selectedDay;
    private int selectedMonth;
    private int selectedYear;
    private int selectedClass;
    private List<ClassInfo> classList;
    private List<AttendanceInfo> attendanceList;
    private AttendanceListAdapter attendanceListAdapter;
    private ProgressDialog progressDialog;

    private final int GET_CLASS_LIST_REQUEST = 1000;
    private final int GET_ATTENDANCE_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.snapschool.mlps.R.layout.activity_attendance);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initValues();
    }

    // initialize values and settings
    public void initValues() {
        monthNames = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        selectedDay = -1;
        selectedMonth = -1;
        selectedYear = -1;
        selectedClass = -1;
        serverManager = new ServerManager(this);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("mlpsPrefs", 0);
        authKey = sharedPreferences.getString("AUTHKEY", "");
        loginType = sharedPreferences.getString("LOGINTYPE", "");
        attendanceList = new ArrayList<>();
        attendanceListAdapter = new AttendanceListAdapter(this, attendanceList);
        ListView attendanceListView = (ListView)findViewById(com.snapschool.mlps.R.id.attendance_listView);
        attendanceListView.setAdapter(attendanceListAdapter);
        setUpClassPicker();
        progressDialog = new ProgressDialog(this);
    }

    // configuring the class spinner
    public void setUpClassPicker() {
        classNames = new ArrayList<>();
        classNames.add("Select class");
        classListAdapter = new SelectionListAdapter(this, classNames);
        Spinner classSpinner = (Spinner)findViewById(com.snapschool.mlps.R.id.attendanceClassSpinner);
        classSpinner.setAdapter(classListAdapter);
        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedClass = position - 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        serverManager.getClassList(authKey, loginType, GET_CLASS_LIST_REQUEST);
    }

    // shows an date picker on press
    public void datePickerButtonAction(View view) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String dateString = monthNames[monthOfYear] + " " + String.valueOf(dayOfMonth) + ", " + String.valueOf(year);
                Button dateButton = (Button)findViewById(com.snapschool.mlps.R.id.datePicker_button);
                dateButton.setText(dateString);
                selectedDay = dayOfMonth;
                selectedMonth = monthOfYear+1;
                selectedYear = year;
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    // views attendance information fetched from server
    public void viewButtonAction(View view) {
        attendanceList.clear();
        attendanceListAdapter.notifyDataSetChanged();
        if (selectedDay != -1 && selectedMonth != -1 && selectedYear != -1 && selectedClass != -1) {
            progressDialog.show();
            serverManager.getAttendance(authKey, loginType, selectedDay, selectedMonth, selectedYear, classList.get(selectedClass).getClassId(), GET_ATTENDANCE_REQUEST);
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
                classList = new ArrayList<>();
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String classId = jsonObject.optString("class_id");
                    String className = jsonObject.optString("name");
                    classNames.add(className);
                    classList.add(new ClassInfo(classId, className));
                }
                classListAdapter.notifyDataSetChanged();
            }
            else if (requestTag == GET_ATTENDANCE_REQUEST) {
                attendanceList.clear();
                attendanceList.add(new AttendanceInfo("Roll", "Name", "Status"));
                JSONArray jsonArray = new JSONArray(response);
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String roll = jsonObject.optString("roll");
                    String name = jsonObject.optString("name");
                    String status = jsonObject.optString("status");
                    attendanceList.add(new AttendanceInfo(roll, name, status));
                }
                attendanceListAdapter.notifyDataSetChanged();
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
        String message = "";
        if (requestTag == GET_CLASS_LIST_REQUEST)
            message = "Failed to load class list";
        else if (requestTag == GET_ATTENDANCE_REQUEST)
            message = "Failed to load attendance information";
        showAlert(message);
    }

    @Override
    public void imageDownloaded(Bitmap image, int requestTag) {

    }
}
