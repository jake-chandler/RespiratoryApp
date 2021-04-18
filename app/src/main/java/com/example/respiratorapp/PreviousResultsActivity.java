package com.example.respiratorapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;

public class PreviousResultsActivity extends AppCompatActivity {
    private Spinner previousTests;
    String information = "";
    String riskText = "";
    TestResults testResults;
    private ImageView submitButton, homeButton;
    private TextView risk, means;
    private Activity activity = this;
    private RespiratoryUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("PREVIOUS", "Reached.");
        //makes this activity full-screen (removes notification bar)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_results);


        Intent userServiceIntent = new Intent(activity, UserService.class);
        bindService(userServiceIntent, userServiceConnection, Context.BIND_AUTO_CREATE);




    }

    private void initListeners() {
        previousTests = (Spinner) findViewById(R.id.previous_tests);
        submitButton = (ImageView) findViewById(R.id.export_res);
        homeButton = (ImageView) findViewById(R.id.home);
        risk = (TextView) findViewById(R.id.RiskAssessmenttextView);
        means = (TextView) findViewById(R.id.ThisMeanstextView);
        means.setMovementMethod(new ScrollingMovementMethod());
        risk.setText("");
        means.setText("");

        String[] info = new String[user.getTestResultsList().size()];
        int count = 0;
        for (String test : user.getTestResultsList()) {
            info[count] = test;
            count++;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, info);
        previousTests.setAdapter(adapter);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PreviousResultsActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                try {
                    Log.i("PREVIOUS", previousTests.getSelectedItem().toString());
                    Log.i("PREVIOUS", TestResults.retrieveTestResults(getApplicationContext(), previousTests.getSelectedItem().toString()));
                    testResults = new TestResults(TestResults.retrieveTestResults(getApplicationContext(), previousTests.getSelectedItem().toString()));
                    Log.i("PREVIOUS", testResults.toString());

                    new Thread(() -> {
                        buildInfo();
                        means.setText(information);
                        risk.setText(riskText);
                    }).start();

                } catch (FileNotFoundException e) {
                    Log.e("PREVIOUS", "Test result file not found.");
                }

            }
        });



    }

    private void buildInfo() {
        if (testResults.getHrRisk()== TestResults.HR_RiskAssessment.HIGH){
            Log.i("PREVIOUS", "HIGH");
            information += "Your heart rate is not in the normal range for your age." + ". This is HIGH risk for your age.\n\n";
            information += "Please consult a physician. \n\n";
            riskText = "HIGH";
        }
        if (testResults.getBo2Risk() == TestResults.B02_RiskAssessment.HIGH) {
            Log.i("PREVIOUS", "HIGH");
            information += "Your breathing frequencies are not in the normal range." + ". This is HIGH risk for your age.\n\n";
            information += "Please consult a physician. \n\n";
            riskText = "HIGH";
        }
        if (testResults.getRrRisk() == TestResults.RR_RiskAssessment.HIGH) {
            Log.i("PREVIOUS", "HIGH");
            information += "Your blood oxygen concentration is not in the normal range." + ". This is HIGH risk for your age.\n\n";
            information += "Please consult a physician. \n\n";
            riskText = "HIGH";
        }
        if (risk.equals("")) {
            riskText = "LOW";
        }
    }

    private ServiceConnection userServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            UserService.UserServiceBinder binder = (UserService.UserServiceBinder) service;

            // retrieve this session's user.
            UserService userService = binder.getService();
            user = userService.getActiveUser();
            Log.i("PREVIOUS", user.getTestResultsList().toString());

            initListeners();


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
