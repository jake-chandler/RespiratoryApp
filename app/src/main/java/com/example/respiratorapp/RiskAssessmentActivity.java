package com.example.respiratorapp;

import android.app.Activity;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class RiskAssessmentActivity extends AppCompatActivity {

    private static final int NUM_MEASUREMENTS = 100;
    private static final String LOGGER_INFO = "RiskAssessmentActivity";
    private BleService ble_svc;
    private final Activity activity = this;
    private RespiratoryUser user;
    private String info = "";
    ImageView export, home;
    TextView riskText;
    TextView meansText;
    TestResults test;
    int hrAvg, rrAvg, b02Avg;
    TestResults.HR_RiskAssessment hrRisk;
    TestResults.RR_RiskAssessment rrRisk;
    TestResults.B02_RiskAssessment b02Risk;
    double[][] hrMeas, rrMeas, b02Meas;
    TestResults.RiskAssessment riskAssessment;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //makes this activity full-screen (removes notification bar)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        Log.i(LOGGER_INFO, "REACHED");
        Intent userServiceIntent = new Intent(activity, UserService.class);
        bindService(userServiceIntent, userServiceConnection, Context.BIND_AUTO_CREATE);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void initListeners() {
        ImageView export = (ImageView) findViewById(R.id.export_res);
        ImageView home = (ImageView) findViewById(R.id.home);
        riskText = (TextView) findViewById(R.id.textViewRiskAssessment);
        meansText = (TextView) findViewById(R.id.textViewThisMeans);
        meansText.setMovementMethod(new ScrollingMovementMethod());
        if (hrRisk == TestResults.HR_RiskAssessment.HIGH || rrRisk == TestResults.RR_RiskAssessment.HIGH || b02Risk == TestResults.B02_RiskAssessment.HIGH) {
            riskText.setText("HIGH");
            riskAssessment = TestResults.RiskAssessment.HIGH;
        }
        else {
            riskText.setText("LOW");
            riskAssessment = TestResults.RiskAssessment.LOW;
        }
        meansText.setText(info);


        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RiskAssessmentActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RiskAssessmentActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private ServiceConnection connection = new ServiceConnection() {

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void runTest() {

        // retrieve measurement data from BleService.
        hrMeas = ble_svc.getHRMeasurement();
        rrMeas = ble_svc.getRRMeasurement();
        b02Meas = ble_svc.getB02Measurement();
        calculateHrRisk();
        calculateB02Risk();
        calculateRrRisk();

        setContentView(R.layout.activity_riskassessment);
        initListeners();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            test = new TestResults(hrAvg,rrAvg,b02Avg,riskAssessment,hrRisk,rrRisk,b02Risk );
        }
         test.saveTestResults(this);

         user.addTestResult(test.getTestID());

         try {
         user.saveUser(this);
         } catch (IOException e) {
         Log.i("RISK_ASSESSMENT", "Failed to save user.");
         }

    }

    private ServiceConnection userServiceConnection = new ServiceConnection() {
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void calculateHrRisk() {
        int upperThreshold = 0, lowerThreshold = 0;
        double sum = 0;
        for (int i = 0; i < NUM_MEASUREMENTS; i++) {

            double heartRate = hrMeas[i][1];
            sum+=heartRate;
            if (user.getAge() > 40) {
                if (user.getAge() > 60) {
                    if (user.getAge() > 80) {

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
            if (lowerThreshold < heartRate && heartRate < upperThreshold) {
                // normal
                hrRisk = TestResults.HR_RiskAssessment.HIGH;
            } else {
                //abnormal
                hrRisk = TestResults.HR_RiskAssessment.LOW;
            }
        }
        hrAvg = (int) (sum / NUM_MEASUREMENTS);
        info += "Your heart rate is between " + lowerThreshold +" and "+ upperThreshold + ". This is "+ hrRisk.toString() + " risk for your age.\n\n";
        if (hrRisk == TestResults.HR_RiskAssessment.HIGH) {
            info+= "Please consult a physician.\n\n";
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void calculateRrRisk() {
        double sum = 0;
        for (int i = 0; i < NUM_MEASUREMENTS; i++) {
            double resp = rrMeas[i][1];
            sum+=resp;
            if (resp >=0 && resp <= 700) {
                rrRisk = TestResults.RR_RiskAssessment.LOW;
            }
            else {
                rrRisk = TestResults.RR_RiskAssessment.HIGH;
            }
        }
        if (rrRisk == TestResults.RR_RiskAssessment.LOW) {
            info += "Your breathing frequencies are between " + 60 + " and "+600+" hz." +" You are at " + rrRisk.toString() + " respiratory risk.\n\n";
        } else {
            info += "Your breathing frequencies are above " + 600+" hz." + " You are at " + rrRisk.toString() + " respiratory risk.\n\n";
            info += "You may be at risk for COPD or chronic bronchitis. Please consult a physician.\n\n";
        }
        rrAvg = (int) (sum/NUM_MEASUREMENTS);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void calculateB02Risk() {
        double sum = 0;
        for (int i = 0; i < NUM_MEASUREMENTS ; i++){
            double b02 = b02Meas[i][1];
            sum+=b02;
            if (b02 <= 89) {
                b02Risk = TestResults.B02_RiskAssessment.HIGH;
            }
            else if (b02>=95) {
                b02Risk = TestResults.B02_RiskAssessment.LOW;
            }
        }
        if (b02Risk == TestResults.B02_RiskAssessment.LOW) {
            info += "Your blood oxygen is above " + 94+". This is "+ b02Risk.toString() + " risk.\n\n";
        } else {
            info += "Your blood oxygen is less than " + 90+". This is "+ b02Risk.toString() + " risk.\n\n";
            info += "You may be at risk for Hypoxemia. Please consult a physician.\n\n";
        }
        b02Avg = (int) (sum/NUM_MEASUREMENTS);

    }
}