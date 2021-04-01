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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class RiskAssessmentActivity extends AppCompatActivity {

    private ImageView export;
    private ImageView home;
    private TextView riskText;
    private TextView meansText;
    private static final int NUM_MEASUREMENTS = 100;
    private TestResults RiskAssessment;
    private BleService svc;
    private String Information;
    private Activity activity = this;
    private RespiratoryUser user;
    private UserService userService;


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
        bindService(intent, userServiceConnection, Context.BIND_AUTO_CREATE);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void initListeners() {
        export = (ImageView) findViewById(R.id.export_res);
        home = (ImageView) findViewById(R.id.home);
        riskText = (TextView) findViewById(R.id.textViewRiskAssessment);
        meansText = (TextView) findViewById(R.id.textViewThisMeans);

        Information = (RiskAssessment.getBo2Risk().toString() + "\n" + RiskAssessment.getHrRisk() + "\n" + RiskAssessment.getRrRisk());
        riskText.setText( RiskAssessment.getOverallRisk().toString() );
        meansText.setText( Information );

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
            svc = binder.getService();
            svc.notifyHR(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
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
        int [][] bo2Meas, hrMeas, rrMeas;

        // retrieve measurement data from BleService.
        hrMeas = svc.getHRMeasurement();
        rrMeas = svc.getRRMeasurement();
        bo2Meas = svc.getBO2Measurement();

        TestResults test = null;
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
            userService = binder.getService();
            user = userService.getActiveUser();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void calculateHrRisk(int[][] meas) {
        for (int i = 0; i < NUM_MEASUREMENTS; i++) {

        }
    }
    private void calculateRrRisk(int[][] meas) {
        for (int i = 0; i < NUM_MEASUREMENTS; i++) {


        }
    }
    private void calculateB02Risk(int[][] meas) {
        for (int i = 0; i < NUM_MEASUREMENTS; i++) {

            if (meas[i][1] <= 89) {
                // abnormal
            } else if (meas[i][1] >= 95) {
                // normal
            }
        }
    }
}