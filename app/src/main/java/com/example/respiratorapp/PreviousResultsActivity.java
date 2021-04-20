package com.example.respiratorapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.StyleSpan;
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

/**
 * Retrieves and displays results from previous tests.
 */
public class PreviousResultsActivity extends AppCompatActivity {
    private Spinner previousTests;
    private ImageView slider1, slider2, slider3, slider4, slider5, slider6, slider7, slider8, slider9, home, submit;
    TestResults testResults;
    private TextView means;
    private final Activity activity = this;
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

        submit = (ImageView) findViewById(R.id.export_res);
        home = (ImageView) findViewById(R.id.home);
        slider1 = (ImageView) findViewById(R.id.slider1);
        slider2 = (ImageView) findViewById(R.id.slider2);
        slider3 = (ImageView) findViewById(R.id.slider3);
        slider4 = (ImageView) findViewById(R.id.slider4);
        slider5 = (ImageView) findViewById(R.id.slider5);
        slider6 = (ImageView) findViewById(R.id.slider6);
        slider7 = (ImageView) findViewById(R.id.slider7);
        slider8 = (ImageView) findViewById(R.id.slider8);
        slider9 = (ImageView) findViewById(R.id.slider9);
        means = (TextView) findViewById(R.id.textView);

        Intent userServiceIntent = new Intent(activity, UserService.class);
        bindService(userServiceIntent, userServiceConnection, Context.BIND_AUTO_CREATE);


    }

    /**
     * Helper function to initialize onClick listeners for elements on the layout.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initListeners() {
        previousTests = (Spinner) findViewById(R.id.spinner);
        means = (TextView) findViewById(R.id.textView);
        means.setMovementMethod(new ScrollingMovementMethod());
        means.setText("");

        String[] info = new String[user.getTestResultsList().size()];
        int count = 0;
        for (String test : user.getTestResultsList()) {
            info[count] = test;
            count++;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, info);
        previousTests.setAdapter(adapter);
        home.setOnClickListener(v -> {
            Intent intent = new Intent(PreviousResultsActivity.this, HomeActivity.class);
            startActivity(intent);
        });
        submit.setOnClickListener(v -> {
            slider1.setVisibility(View.INVISIBLE);
            slider2.setVisibility(View.INVISIBLE);
            slider3.setVisibility(View.INVISIBLE);
            slider4.setVisibility(View.INVISIBLE);
            slider5.setVisibility(View.INVISIBLE);
            slider6.setVisibility(View.INVISIBLE);
            slider7.setVisibility(View.INVISIBLE);
            slider8.setVisibility(View.INVISIBLE);
            slider9.setVisibility(View.INVISIBLE);
            means.setText("");
            try {
                Log.i("PREVIOUS", previousTests.getSelectedItem().toString());
                Log.i("PREVIOUS", TestResults.retrieveTestResults(getApplicationContext(), previousTests.getSelectedItem().toString()));
                // retrieve the desired test result.
                testResults = new TestResults(TestResults.retrieveTestResults(getApplicationContext(), previousTests.getSelectedItem().toString()));
                Log.i("PREVIOUS", testResults.toString());

                buildInfo();

            } catch (FileNotFoundException e) {
                Log.e("PREVIOUS", "Test result file not found.");
            }

        });


    }

    /**
     * Updates the risk assessment slider from selected previous test.
     */
    private void buildInfo() {
        if (testResults.getHrRisk() == TestResults.HR_RiskAssessment.HIGH) {
            Log.i("PREVIOUS", "HIGH");
            slider3.setVisibility(View.VISIBLE);

        } else {
            slider1.setVisibility(View.VISIBLE);
        }
        if (testResults.getBo2Risk() == TestResults.B02_RiskAssessment.HIGH) {
            Log.i("PREVIOUS", "HIGH");
            slider6.setVisibility(View.VISIBLE);

        } else {
            slider4.setVisibility(View.VISIBLE);
        }
        if (testResults.getRrRisk() == TestResults.RR_RiskAssessment.HIGH) {
            Log.i("PREVIOUS", "HIGH");
            slider9.setVisibility(View.VISIBLE);
        } else {
            slider7.setVisibility(View.VISIBLE);
        }

        SpannableString s;
        if (testResults.getHrRisk() == TestResults.HR_RiskAssessment.HIGH ||
                testResults.getRrRisk() == TestResults.RR_RiskAssessment.HIGH ||
                testResults.getBo2Risk() == TestResults.B02_RiskAssessment.HIGH) {
            s = new SpannableString("Please Consult a Physician.");
        } else {
            s = new SpannableString("You're all good!");
        }
        StyleSpan span = new StyleSpan(Typeface.BOLD);
        s.setSpan(span, 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        means.setText(s);
    }

    private ServiceConnection userServiceConnection = new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
