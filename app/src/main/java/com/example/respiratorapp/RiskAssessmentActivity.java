package com.example.respiratorapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.jjoe64.graphview.series.DataPoint;

import java.io.IOException;
import java.util.List;

public class RiskAssessmentActivity extends AppCompatActivity {

    private static final int NUM_MEASUREMENTS = 100;
    private BleService bleService;
    private final Activity activity = this;
    private RespiratoryUser user;
    ImageView export, home;
    TextView riskText;
    TextView meansText;
    TestResults test;
    TestResults.HR_RiskAssessment hrRisk;
    TestResults.RR_RiskAssessment rrRisk;
    TestResults.B02_RiskAssessment b02Risk;
    TestResults.RiskAssessment riskAssessment;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //makes this activity full-screen (removes notification bar)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_riskassessment);

        initListeners();

        Intent intent = new Intent(activity, BleService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        Intent userServiceIntent = new Intent(activity, UserService.class);
        bindService(userServiceIntent, userServiceConnection, Context.BIND_AUTO_CREATE);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void initListeners() {
        ImageView export = (ImageView) findViewById(R.id.export_res);
        ImageView home = (ImageView) findViewById(R.id.home);
        TextView riskText = (TextView) findViewById(R.id.textViewRiskAssessment);
        TextView meansText = (TextView) findViewById(R.id.textViewThisMeans);


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
            bleService = binder.getService();
            bleService.notifyHR(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
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
        List<DataPoint> hrMeas, rrMeas, b02Meas;

        // retrieve measurement data from BleService.
        hrMeas = bleService.getHRMeasurement();
        rrMeas = bleService.getRRMeasurement();
        b02Meas = bleService.getB02Measurement();
        
        test.saveTestResults(this);
        user.addTestResult(test.getTestID());
        try {
            user.saveUser(this);
        } catch (IOException e) {
            Log.i("RISK_ASSESSMENT", "Failed to save user.");
        }
        String information = rrRisk + " " + hrRisk + " " + b02Risk + " ";
        riskText.setText( information );
        meansText.setText(information);
    }
    private ServiceConnection userServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            UserService.UserServiceBinder binder = (UserService.UserServiceBinder) service;

            // retrieve this session's user.
            UserService userService = binder.getService();
            user = userService.getActiveUser();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void calculateHrRisk() {
        for (DataPoint measurement : bleService.getHRMeasurement()) {
 
            double heartRate = measurement.getY();
            // TODO: Set heart rate threshold values based on age.
            int upperThreshold = 0, lowerThreshold = 0;
            if (user.getAge() > 60) {
                if (user.getAge() > 70) {
                    
                }
            }
            if (lowerThreshold <heartRate && heartRate < upperThreshold) {
                // normal
                hrRisk = TestResults.HR_RiskAssessment.LOW;
            } else {
                
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void calculateRrRisk() {
        for (DataPoint measurement : bleService.getRRMeasurement()) {
            double time = measurement.getX();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void calculateB02Risk() {
        for (DataPoint measurement : bleService.getB02Measurement()) {
            
        }
    }
}