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

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;

/**
 * @brief Represents the respiratory rate test screen of our application
 */
public class Test3Activity extends AppCompatActivity {

    /**
     * Buttons
     */
    private ImageView next;
    private ImageView retry;

    /**
     * Graph
     */
    private GraphView respiratoryRateGraph;

    BleService svc;
    Activity activity = this;
    private LineGraphSeries<DataPoint> series;
    public static final long SAMPLE_TIME = 10000;
    private static final int NUM_MEASUREMENTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //makes this activity full-screen (removes notification bar)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_test3);

        initListeners();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void collectMeasurements() throws InterruptedException {
        respiratoryRateGraph = (GraphView) findViewById(R.id.breath_graph);
        Intent intent = new Intent(this, BleService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        series = new LineGraphSeries<>();

        long startTime = Calendar.getInstance().getTimeInMillis();
        int i = 0;
        int[][] rrMeasurements = new int[NUM_MEASUREMENTS][2];

        long endTime;
        do {
            Thread.sleep(100 );

            if (i == NUM_MEASUREMENTS) {
                break;
            }
            rrMeasurements[i][1] = svc.getHrVal();
            i++;
            endTime = Calendar.getInstance().getTimeInMillis();
            double x = (int) (endTime - startTime);
            rrMeasurements[i][0] = (int) x;
            double y = rrMeasurements[i][1];
            series.appendData( new DataPoint( x, y ),true,NUM_MEASUREMENTS );
        } while (endTime - startTime < SAMPLE_TIME);
        svc.setRRMeasurements( rrMeasurements );
    }

    protected void initListeners() {
        next = (ImageView) findViewById(R.id.next_btn);
        retry = (ImageView) findViewById(R.id.retry_btn);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Test3Activity.this, RiskAssessmentActivity.class);
                startActivity(intent);
            }
        });
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Test3Activity.this, Test3Activity.class);
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
            try {
                collectMeasurements();
            } catch (InterruptedException e) {
                Log.i("FORM","error");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("PAIRED", "Service unbounded from this activity.");
        }
    };
}