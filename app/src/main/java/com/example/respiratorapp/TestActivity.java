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
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

/**
 * @brief Represents the heart rate test screen of our application
 */
public class TestActivity extends AppCompatActivity {

    private static final int NUM_MEASUREMENTS = 100;
    private static final Timestamp SAMPLE_TIME = new Timestamp(10000);
    public static final long UPDATE_TIME = 100;
    BleService svc;
    Activity activity = this;
    private LineGraphSeries<DataPoint> series;
    /**
     * Buttons
     */
    private ImageView next;
    private ImageView retry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //makes this activity full-screen (removes notification bar)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_test);

        initListeners();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void collectMeasurements() throws InterruptedException {

        long startTime = Calendar.getInstance().getTimeInMillis();
        List<DataPoint> hrMeasurements = null;
        Timestamp timeElapsed;
        long endTime;
        do {
            // sleep every 100 ms to stay updated with the MC characteristic.
            Thread.sleep(UPDATE_TIME);

            if (hrMeasurements.size() == NUM_MEASUREMENTS - 1) {
                break;
            }
            endTime = Calendar.getInstance().getTimeInMillis();
            double x = endTime;
            double y = svc.getHrVal();
            hrMeasurements.add(new DataPoint(x, y));
            series.appendData(new DataPoint(x, y), true, NUM_MEASUREMENTS);
            timeElapsed = new Timestamp((endTime - startTime));
        } while (timeElapsed.compareTo(SAMPLE_TIME) < 0);
        if (hrMeasurements != null) {
            svc.setHRMeasurement(hrMeasurements);
        }
    }

    protected void initListeners() {
        ImageView next = findViewById(R.id.next_btn);
        ImageView retry = findViewById(R.id.retry_btn);

        next.setOnClickListener(view -> {
            Intent intent = new Intent(TestActivity.this, Test2Activity.class);
            startActivity(intent);
            //setContentView(R.layout.activity_test2);
        });

        retry.setOnClickListener(view -> {
            Intent intent = new Intent(TestActivity.this, TestActivity.class);
            startActivity(intent);
        });
    }
    private void initGraph() {
        GraphView heartGraph = findViewById(R.id.heartrate_graph);
        Intent intent = new Intent(activity, BleService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        series = new LineGraphSeries<>();
        heartGraph.addSeries(series);

        Viewport viewport = heartGraph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(200);
        viewport.setScalable(true);
    }

    private final ServiceConnection connection = new ServiceConnection() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("PAIRED", "Ble Service discovered.");
            BleService.BleServiceBinder binder = (BleService.BleServiceBinder) service;
            svc = binder.getService();
            svc.notifyHR(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            Log.i("PAIRED", "Notifying service...");

            // initialize graph.
            initGraph();

            // update graph with data points in the background.
            new Thread(() -> {
                try {
                    Log.i("MEASUREMENT_THREAD", "Collecting measurements...");
                    collectMeasurements();
                } catch (InterruptedException e) {
                    Log.e("MEASUREMENT_THREAD", e.toString());
                }
            }).start();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("PAIRED", "Service unbounded from this activity.");
        }
    };
}