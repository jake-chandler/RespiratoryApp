package com.example.respiratorapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

public class Test2Activity extends AppCompatActivity {

    private static final int NUM_MEASUREMENTS = 100;
    private static final Timestamp SAMPLE_TIME = new Timestamp(10000);
    private static final int UPDATE_TIME = 100;
    BleService svc;
    Activity activity = this;
    private LineGraphSeries<DataPoint> series;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //makes this activity full-screen (removes notification bar)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_test2);

        initListeners();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void collectMeasurements() throws InterruptedException {
        long startTime = Calendar.getInstance().getTimeInMillis();
        List<DataPoint> b02Measurements = null;
        Timestamp timeElapsed;
        long endTime;
        Log.i("MEASUREMENT_THREAD", "Updating live graph...");
        do {
            if (b02Measurements.size() == NUM_MEASUREMENTS) { break; }
            // sleep this thread UPDATE_TIME milliseconds. to ensure retrieval of latest measurement from the sensor device.
            Thread.sleep(UPDATE_TIME);
            endTime = Calendar.getInstance().getTimeInMillis();

            // get the metric from the Ble Service
            double time = endTime;
            double b02 =    svc.getHrVal();;

            // plot metric to graph.
            series.appendData( new DataPoint( time, b02),true,NUM_MEASUREMENTS );
            b02Measurements.add(new DataPoint(time, b02));
            timeElapsed = new Timestamp((endTime - startTime));
        } while (timeElapsed.compareTo(SAMPLE_TIME) < 0);
        // save measurements with the BLE service.
        if (b02Measurements != null) {
            svc.setB02Measurements(b02Measurements);
        }
    }

    protected void initListeners() {
        ImageView next = findViewById(R.id.next_btn);
        ImageView retry = findViewById(R.id.retry_btn);
        next.setOnClickListener(view -> {
            Intent intent = new Intent(Test2Activity.this, Test3Activity.class);
            startActivity(intent);
        });
        retry.setOnClickListener(view -> {
            Intent intent = new Intent(Test2Activity.this, Test2Activity.class);
            startActivity(intent);
        });
    }

    private void initGraph() {
        GraphView BO2Graph = findViewById(R.id.heartrate_graph);
        Intent intent = new Intent(activity, BleService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        series = new LineGraphSeries<>();
        BO2Graph.addSeries(series);

        Viewport viewport = BO2Graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(100);
        viewport.setScrollable(true);

    }



    private final ServiceConnection connection = new ServiceConnection() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("PAIRED", "Ble Service discovered.");
            // initialize graph.
            initGraph();

            // update graph with data points in the background.
            new Thread(() -> {
                try {
                    Log.i("MEASUREMENT_THREAD", "Collecting measurements.");
                    collectMeasurements();
                    Log.i("MEASUREMENT_THREAD", "Collected measurements!");
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