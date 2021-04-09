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

public class Test3Activity extends AppCompatActivity {

    private static final int NUM_MEASUREMENTS = 100;
    private static final Timestamp SAMPLE_TIME = new Timestamp(10000);
    private static final int UPDATE_TIME = 100;
    private static final String LOGGER_INFO = "Test3Activity";
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

        setContentView(R.layout.activity_test3);

        initListeners();
        Intent intent = new Intent(activity, BleService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void collectMeasurements() throws InterruptedException {

        long startTime = Calendar.getInstance().getTimeInMillis();
        double[][] rrMeasurements = new double[1000][2];
        int i = 0;
        long elapsedTime, endTime;
        Timestamp timeElapsed;
        Log.i("MEASUREMENT_THREAD", "Updating live graph...");
        // take measurements for SAMPLE_TIME milliseconds or until we've achieved the NUM_MEASUREMENTS of measurements.
        // theoretically, it should take SAMPLE_TIME milliseconds in order to reach NUM_MEASUREMENTS measurements.
        do {
            // this is to prevent slight update timing issues that may arise.
            Thread.sleep(UPDATE_TIME);

            if (i == NUM_MEASUREMENTS - 1) {
                break;
            }
            // calculate elapsed time.
            endTime = Calendar.getInstance().getTimeInMillis();
            elapsedTime = endTime - startTime;

            // trim down to one decimal place.
            double x = endTime / 1000.00;
            x = x * Math.pow(10, 1);
            x = Math.floor(x);
            x = x / Math.pow(10, 1);

            //retrieve metric from ble service.
            double y = svc.getRrVal();

            // populate the measurement data structure
            rrMeasurements[i][0] = endTime;
            rrMeasurements[i][1] = y;
            series.appendData(new DataPoint(x, y), true, NUM_MEASUREMENTS);
            timeElapsed = new Timestamp(elapsedTime);
            i++;
        } while (timeElapsed.compareTo(SAMPLE_TIME) < 0);

        // save the measurements with the ble service.
        svc.setRRMeasurements(rrMeasurements);

    }


    protected void initListeners() {
        ImageView next = findViewById(R.id.next_btn);
        ImageView retry = findViewById(R.id.retry_btn);
        next.setOnClickListener(view -> {
            Intent intent = new Intent(Test3Activity.this, RiskAssessmentActivity.class);
            startActivity(intent);
        });
        retry.setOnClickListener(view -> {
            Intent intent = new Intent(Test3Activity.this, Test3Activity.class);
            startActivity(intent);
        });
    }

    private void initGraph() {
        GraphView respiratoryRateGraph = findViewById(R.id.heartrate_graph);


        series = new LineGraphSeries<>();
        respiratoryRateGraph.addSeries(series);

        Viewport viewport = respiratoryRateGraph.getViewport();

        viewport.setYAxisBoundsManual(false);
        viewport.setXAxisBoundsManual(true);

        viewport.setScalable(true);
        viewport.setScrollable(true);
        Log.i(LOGGER_INFO, "Graph initialized.");
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