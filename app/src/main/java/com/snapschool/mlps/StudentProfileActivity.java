package com.snapschool.mlps;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.*;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Adapters.StudentMarkAdapter;
import Adapters.StudentProfileAdapter;
import DataModels.StudentMarkInfo;

public class StudentProfileActivity extends ActionBarActivity implements ServerManager.ServerResponseHandler{

    private final int PROFILE_INFORMATION_REQUEST = 1000;
    private final int MARKS_INFORMATION_REQUEST = 1001;
    private final int GET_EXAMLIST_REQUEST = 1002;
    private List<String> profileCaptions;
    private List<String> profileDetails;
    private StudentProfileAdapter profileAdapter;
    private StudentMarkAdapter markAdapter;
    private ListView profileListView;
    private ExpandableListView marksListView;
    private List<String> examNames;
    private HashMap<String , String> exams;
    private HashMap<String, List<StudentMarkInfo>> marks;
    private ServerManager serverManager;
    private String studentId;
    private String authKey;
    private String loginType;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.snapschool.mlps.R.layout.activity_student_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initValues();
    }

    // initialize values and settings
    public void initValues() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        int userImageSize = outMetrics.heightPixels/8;
        ImageView studentImageView = (ImageView)findViewById(com.snapschool.mlps.R.id.student_imageView);
        studentImageView.getLayoutParams().height = userImageSize;
        studentImageView.getLayoutParams().width = userImageSize;
        Intent intent = getIntent();
        String studentName = intent.getStringExtra("studentName");
        String imageUrl = intent.getStringExtra("imageUrl");
        studentId = intent.getStringExtra("userId");
        String userType = intent.getStringExtra("userType");
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("mlpsPrefs", 0);
        authKey = preferences.getString("AUTHKEY", "");
        loginType = preferences.getString("LOGINTYPE", "");
        TextView studentNameText = (TextView)findViewById(com.snapschool.mlps.R.id.studentName_textView);
        studentNameText.setText(studentName);
        profileCaptions = new ArrayList<>();
        profileDetails = new ArrayList<>();
        profileAdapter = new StudentProfileAdapter(this, profileCaptions, profileDetails);
        profileListView = (ListView)findViewById(com.snapschool.mlps.R.id.student_profile_listView);
        profileListView.setAdapter(profileAdapter);
        profileListView.setVisibility(View.VISIBLE);
        progressDialog = new ProgressDialog(this);
        serverManager = new ServerManager(this);
        progressDialog.show();
        serverManager.getUserProfile(authKey, loginType, studentId, userType, PROFILE_INFORMATION_REQUEST);
        examNames = new ArrayList<>();
        marks = new HashMap<>();
        marksListView = (ExpandableListView)findViewById(com.snapschool.mlps.R.id.student_marks_listView);
        marksListView.setVisibility(View.INVISIBLE);
        markAdapter = new StudentMarkAdapter(this, examNames, marks);
        marksListView.setAdapter(markAdapter);
        marksListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });
        serverManager.getExamList(authKey, loginType, GET_EXAMLIST_REQUEST);
        serverManager.downloadImage(imageUrl, userImageSize, 0);
    }

    // shows student profile information
    public void profileButtonAction(View view) {
        Button profile = (Button)findViewById(com.snapschool.mlps.R.id.profile_button);
        Button marks = (Button)findViewById(com.snapschool.mlps.R.id.marks_button);
        profile.setBackgroundColor(Color.rgb(69, 187, 247));
        profile.setTextColor(Color.WHITE);
        marks.setBackgroundColor(Color.WHITE);
        marks.setTextColor(Color.rgb(69, 187, 247));
        profileListView.setVisibility(View.VISIBLE);
        marksListView.setVisibility(View.INVISIBLE);
    }

    // shows student marks information
    public void marksButtonClicked(View view) {
        Button profile = (Button)findViewById(com.snapschool.mlps.R.id.profile_button);
        Button marks = (Button)findViewById(com.snapschool.mlps.R.id.marks_button);
        marks.setBackgroundColor(Color.rgb(69, 187, 247));
        marks.setTextColor(Color.WHITE);
        profile.setBackgroundColor(Color.WHITE);
        profile.setTextColor(Color.rgb(69, 187, 247));
        profileListView.setVisibility(View.INVISIBLE);
        marksListView.setVisibility(View.VISIBLE);
    }

    // handles click on phone number and email address
    public void profileTextClicked(View view) {
        if (view.getTag().equals("Phone")) {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:"+profileDetails.get(4)));
            startActivity(callIntent);
        }
        else if (view.getTag().equals("Email")) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.putExtra(android.content.Intent.EXTRA_EMAIL, profileDetails.get(5));
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
            if (requestTag == PROFILE_INFORMATION_REQUEST) {
                JSONArray jsonArray = new JSONArray(response);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                profileDetails.add(jsonObject.optString("name"));
                profileDetails.add(jsonObject.optString("birthday"));
                profileDetails.add(jsonObject.optString("gender"));
                profileDetails.add(jsonObject.optString("address"));
                profileDetails.add(jsonObject.optString("phone"));
                profileDetails.add(jsonObject.optString("email"));
                profileDetails.add(jsonObject.optString("class"));
                profileDetails.add(jsonObject.optString("roll"));
                profileDetails.add(jsonObject.optString("parent_name"));
                profileCaptions.add("Name");
                profileCaptions.add("Birthday");
                profileCaptions.add("Gender");
                profileCaptions.add("Address");
                profileCaptions.add("Phone");
                profileCaptions.add("Email");
                profileCaptions.add("Class");
                profileCaptions.add("Roll");
                profileCaptions.add("Parent");
                profileAdapter.notifyDataSetChanged();
            }
            else if (requestTag == GET_EXAMLIST_REQUEST) {
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
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("An error occurred");
            alertDialogBuilder.setPositiveButton("Ok", null);
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
    }

    @Override
    public void requestFailed(String errorMessage, int requestTag) {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
        String error = "";
        if (requestTag == PROFILE_INFORMATION_REQUEST)
            error = "Could not load profile information";
        else if (requestTag == GET_EXAMLIST_REQUEST)
            error = "Could not load exam list";
        else if (requestTag == MARKS_INFORMATION_REQUEST)
            error = "Could not load marks of exam";
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(error);
        alertDialogBuilder.setPositiveButton("Ok", null);
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void imageDownloaded(Bitmap image, int requestTag) {
        ImageView studentImageView = (ImageView)findViewById(com.snapschool.mlps.R.id.student_imageView);
        studentImageView.setImageBitmap(image);
    }
}
