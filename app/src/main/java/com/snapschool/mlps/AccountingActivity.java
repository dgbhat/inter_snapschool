package com.snapschool.mlps;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import Adapters.AccountingListAdapter;
import Adapters.SelectionListAdapter;
import DataModels.AccountingInfo;

public class AccountingActivity extends ActionBarActivity implements ServerManager.ServerResponseHandler{
    private String[] months = {"Select month", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private List<String> years;
    private int selectedMonth;
    private int selectedYear;
    private ServerManager serverManager;
    private String authKey;
    private String loginType;
    private List<AccountingInfo> accountingInfoList;
    private AccountingListAdapter accountingListAdapter;
    private ProgressDialog progressDialog;
    private final int INCOME_INFO_REQUEST = 1000;
    private final int EXPENSE_INFO_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.snapschool.mlps.R.layout.activity_accounting);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initValues();
    }

    // initialize values and settings
    public void initValues() {
        setMonthSpinner();
        setYearSpinner();
        serverManager = new ServerManager(this);
        SharedPreferences sharedPreferences = getSharedPreferences("mlpsPrefs", 0);
        authKey = sharedPreferences.getString("AUTHKEY", "");
        loginType = sharedPreferences.getString("LOGINTYPE", "");
        accountingInfoList = new ArrayList<>();
        accountingListAdapter = new AccountingListAdapter(this, accountingInfoList);
        ListView accountingListView = (ListView)findViewById(com.snapschool.mlps.R.id.accounting_listView);
        accountingListView.setAdapter(accountingListAdapter);
        progressDialog = new ProgressDialog(this);
    }

    // configuring month spinner
    private void setMonthSpinner() {
        selectedMonth = 0;
        ArrayList<String> monthList = new ArrayList<>(Arrays.asList(months));
        SelectionListAdapter monthSpinnerAdapter = new SelectionListAdapter(this, monthList);
        Spinner monthSpinner = (Spinner)findViewById(com.snapschool.mlps.R.id.accounting_month_spinner);
        monthSpinner.setAdapter(monthSpinnerAdapter);
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMonth = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // configuring year spinner
    private void setYearSpinner() {
        selectedYear = 0;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        years = new ArrayList<>();
        years.add("Select year");
        years.add(String.valueOf(year-2));
        years.add(String.valueOf(year-1));
        years.add(String.valueOf(year));
        SelectionListAdapter yearSpinnerAdapter = new SelectionListAdapter(this, years);
        Spinner yearSpinner = (Spinner)findViewById(com.snapschool.mlps.R.id.accounting_year_spinner);
        yearSpinner.setAdapter(yearSpinnerAdapter);
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // on press shows income information of the selected month
    public void incomeButtonAction(View view) {
        Button incomeButton = (Button)findViewById(com.snapschool.mlps.R.id.income_button);
        incomeButton.setBackgroundColor(Color.rgb(69, 187, 247));
        incomeButton.setTextColor(Color.WHITE);
        Button expenseButton = (Button)findViewById(com.snapschool.mlps.R.id.expense_button);
        expenseButton.setBackgroundColor(Color.WHITE);
        expenseButton.setTextColor(Color.rgb(69, 187, 247));
        if (selectedMonth>0 && selectedYear>0) {
            progressDialog.show();
            serverManager.getAccountingInfo(authKey, loginType, selectedMonth, years.get(selectedYear), "income", INCOME_INFO_REQUEST);
        }
    }

    // on press shows expense information of selected month
    public void expenseButtonAction(View view) {
        Button expenseButton = (Button)findViewById(com.snapschool.mlps.R.id.expense_button);
        expenseButton.setBackgroundColor(Color.rgb(69, 187, 247));
        expenseButton.setTextColor(Color.WHITE);
        Button incomeButton = (Button)findViewById(com.snapschool.mlps.R.id.income_button);
        incomeButton.setBackgroundColor(Color.WHITE);
        incomeButton.setTextColor(Color.rgb(69, 187, 247));
        if (selectedMonth>0 && selectedYear>0) {
            progressDialog.show();
            serverManager.getAccountingInfo(authKey, loginType, selectedMonth, years.get(selectedYear), "expense", EXPENSE_INFO_REQUEST);
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
            accountingInfoList.clear();
            accountingInfoList.add(new AccountingInfo("Title", "Amount", "", "Date"));
            for (int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String title = jsonObject.optString("title");
                String amount = jsonObject.optString("amount");
                String timeStamp = jsonObject.optString("timestamp");
                accountingInfoList.add(new AccountingInfo(title, amount, "", timeStamp));
            }
            accountingListAdapter.notifyDataSetChanged();
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
        if (requestTag == INCOME_INFO_REQUEST)
            alertMessage = "Could not load income information";
        else if (requestTag == EXPENSE_INFO_REQUEST)
            alertMessage = "Could not load expense information";
        showAlert(alertMessage);
    }

    @Override
    public void imageDownloaded(Bitmap image, int requestTag) {

    }
}
