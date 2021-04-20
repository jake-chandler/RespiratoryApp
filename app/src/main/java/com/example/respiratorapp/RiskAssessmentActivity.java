package com.example.respiratorapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

/**
 * Retrieves saved biometric samples from the BleService computes the risk assessment,
 * and displays the results to the user.
 */
public class RiskAssessmentActivity extends AppCompatActivity {

    /**
     * Logging and BLE related variables.
     */
    private static final int NUM_MEASUREMENTS = 100;
    private static final String LOGGER_INFO = "RISK";
    private BleService ble_svc;
    private final Activity activity = this;
    private RespiratoryUser user;

    /**
     * Various items on the screen.
     */
    private ImageView export, home ,slider1, slider2,
            slider3, slider4, slider5, slider6, slider7,slider8,slider9;
    private TextView meansText;

    /**
     * Test Result information.
     */
    private TestResults test;
    private int hrAvg, rrAvg, b02Avg;
    private TestResults.HR_RiskAssessment hrRisk;
    private TestResults.RR_RiskAssessment rrRisk;
    private TestResults.B02_RiskAssessment b02Risk;
    private double[][] hrMeas, rrMeas, b02Meas;
    private TestResults.RiskAssessment riskAssessment;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //makes this activity full-screen (removes notification bar)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_riskassessment);
        findSliders();
        Intent userServiceIntent = new Intent(activity, UserService.class);
        bindService(userServiceIntent, userServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * @brief Helper method used to initialize elements on layout.
     */
    private void findSliders() {
        export = (ImageView) findViewById(R.id.export_res);
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
        meansText = (TextView) findViewById(R.id.textView);
    }

    /**
     * @brief Helper method used to update the layout after the test has been completed.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void displayResults() {

        if (hrRisk == TestResults.HR_RiskAssessment.HIGH || rrRisk == TestResults.RR_RiskAssessment.HIGH || b02Risk == TestResults.B02_RiskAssessment.HIGH) {
            riskAssessment = TestResults.RiskAssessment.HIGH;
            SpannableString s = new SpannableString("Please Consult a Physician.");
            StyleSpan span = new StyleSpan(Typeface.BOLD);
            s.setSpan(span, 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            meansText.setText(s);
        }
        else {
            riskAssessment = TestResults.RiskAssessment.LOW;
            SpannableString s = new SpannableString("You're all good!");
            StyleSpan span = new StyleSpan(Typeface.BOLD);
            s.setSpan(span, 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            meansText.setText(s);
        }

        export.setOnClickListener(view -> {
            Intent intent = new Intent(RiskAssessmentActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        home.setOnClickListener(view -> {
            Intent intent = new Intent(RiskAssessmentActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }

    /**
     * BleService connection callback functions.
     */
    private final ServiceConnection connection = new ServiceConnection() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("PAIRED", "Ble Service discovered.");
            BleService.BleServiceBinder binder = (BleService.BleServiceBinder) service;
            ble_svc = binder.getService();
            Log.i("PAIRED", "Notifying service...");
            runTest();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("PAIRED", "Service unbounded from this activity.");
        }
    };

    /**
     * Runs algorithms designed by the BioE team.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void runTest() {
        Log.i(LOGGER_INFO, "Running tests.");
        // retrieve measurement data from BleService.
        hrMeas = ble_svc.getHRMeasurement();
        rrMeas = ble_svc.getRRMeasurement();
        b02Meas = ble_svc.getB02Measurement();
        calculateHrRisk();
        calculateB02Risk();
        calculateRrRisk();

        // display results to user.
        displayResults();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            test = new TestResults(hrAvg,rrAvg,b02Avg,riskAssessment,hrRisk,rrRisk,b02Risk );
        }
         test.saveTestResults(this);

         user.addTestResult(test.getTestID());

         try {
         user.saveUser(this);
         } catch (IOException e) {
         Log.i(LOGGER_INFO, "Failed to save user.");
         }

        Log.i(LOGGER_INFO,"Test complete and results successfully saved.");
    }

    /**
     * UserService callback functions.
     */
    private final ServiceConnection userServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            UserService.UserServiceBinder binder = (UserService.UserServiceBinder) service;

            // retrieve this session's user.
            UserService userService = binder.getService();
            user = userService.getActiveUser();

            Intent intent = new Intent(activity, BleService.class);
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * Implementation of Heart Rate Risk algorithm.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void calculateHrRisk() {
        int upperThreshold = 0, lowerThreshold = 0;
        double sum = 0;
        for (int i = 0; i < NUM_MEASUREMENTS; i++) {
            double heartRate = hrMeas[i][1];
            sum+=heartRate;
            // classify heart rate threshold based on age.
            if (user.getAge() > 40) {
                if (user.getAge() > 60) {
                    if (user.getAge() > 80) {
                        lowerThreshold = 45;
                        upperThreshold = 95;
                    } else {
                        upperThreshold = 100;
                        lowerThreshold = 60;
                    }
                } else {
                    upperThreshold = 70;
                    lowerThreshold = 50;
                }

            } else {
                upperThreshold = 100;
                lowerThreshold = 60;
            }
            // determine risk
            if (lowerThreshold < heartRate && heartRate < upperThreshold) {
                // normal
                hrRisk = TestResults.HR_RiskAssessment.LOW;
            } else {
                //abnormal
                hrRisk = TestResults.HR_RiskAssessment.HIGH;
            }
        }
        // compute average.
        hrAvg = (int) (sum / NUM_MEASUREMENTS);
        if (hrRisk == TestResults.HR_RiskAssessment.HIGH) {
            slider3.setVisibility(View.VISIBLE);
        } else {
            slider1.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Implementation of Respiratory Rate Risk algorithm.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void calculateRrRisk() {
        double sum = 0;
        int consecutiveZeros = 0;
        boolean continuous = true;
        for (int i = 0; i < NUM_MEASUREMENTS; i++) {
            double resp = rrMeas[i][1];
            sum+=resp;
            if (resp <=0){
                consecutiveZeros++;
            }
            else if (resp >=0 && resp <= 700) {
                rrRisk = TestResults.RR_RiskAssessment.LOW;
            }
            else {
                rrRisk = TestResults.RR_RiskAssessment.HIGH;
            }

            // classify as continuous or discontinuous
            // ten consecutive zero's implies that there were minimal readings for at least 100 ms
            if (consecutiveZeros == 10) {
                continuous = false;
                consecutiveZeros = 0;
            }
        }
        if (rrRisk == TestResults.RR_RiskAssessment.LOW) {
           slider7.setVisibility(View.VISIBLE);
        } else {
            slider9.setVisibility(View.VISIBLE);
        }
        rrAvg = (int) (sum/NUM_MEASUREMENTS);
    }

    /**
     * Implementation of Blood Oxygen Risk algorithm.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void calculateB02Risk() {
        double sum = 0;
        for (int i = 0; i < NUM_MEASUREMENTS ; i++){
            double b02 = b02Meas[i][1];
            sum+=b02;
            if (b02 <= 89) {
                // high risk.
                b02Risk = TestResults.B02_RiskAssessment.HIGH;
            }
            else if (b02>=95) {
                // low risk.
                b02Risk = TestResults.B02_RiskAssessment.LOW;
            }
        }
        if (b02Risk == TestResults.B02_RiskAssessment.LOW) {
           slider4.setVisibility(View.VISIBLE);
        } else {
            slider6.setVisibility(View.VISIBLE);
        }
        b02Avg = (int) (sum/NUM_MEASUREMENTS);

    }
}