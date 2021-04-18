package com.example.respiratorapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * @brief Represents the heart rate test screen of our application
 */
public class TestActivity extends AppCompatActivity {

    private static final String LOGGER_INFO = "TestActivity";
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
        // Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage("Please put on the Heart Rate Sensor");
        builder.setNegativeButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                initListeners();

                Intent intent = new Intent(activity, BleService.class);
                bindService(intent, connection, Context.BIND_AUTO_CREATE);
            }
        });
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();

        //makes this activity full-screen (removes notification bar)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_test);



    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void collectMeasurements() throws InterruptedException {

        long startTime = Calendar.getInstance().getTimeInMillis();
        double[][] hrMeasurements = new double[1000][2];
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
            double x = elapsedTime / 1000.00;
            x = x * Math.pow(10, 1);
            x = Math.floor(x);
            x = x / Math.pow(10, 1);

            //retrieve metric from ble service.
            double y = svc.getHrVal();

            // populate the measurement data structure
            hrMeasurements[i][0] = endTime;
            hrMeasurements[i][1] = y;
            series.appendData(new DataPoint(x, y), true, NUM_MEASUREMENTS);
            timeElapsed = new Timestamp(elapsedTime);
            i++;
        } while (timeElapsed.compareTo(SAMPLE_TIME) < 0);

        // save the measurements with the ble service.
        svc.setHRMeasurement(hrMeasurements);
        // disable HR notifications
        svc.notifyHR(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);

    }

    protected void initListeners() {
        Log.i(LOGGER_INFO, "Initializing button listeners.");
        ImageView next = findViewById(R.id.next_btn);
        ImageView retry = findViewById(R.id.retry_btn);

        next.setOnClickListener(view -> {
            Intent intent = new Intent(TestActivity.this, Test2Activity.class);
            startActivity(intent);
        });

        retry.setOnClickListener(view -> {
            Intent intent = new Intent(TestActivity.this, TestActivity.class);
            startActivity(intent);
        });
        Log.i(LOGGER_INFO, "Button Listeners successfully identified");
    }

    private void initGraph() {
        Log.i(LOGGER_INFO, "Initializing graph.");
        GraphView heartGraph = findViewById(R.id.heartrate_graph);

        series = new LineGraphSeries<>();
        heartGraph.addSeries(series);
        GridLabelRenderer label = heartGraph.getGridLabelRenderer();
        label.setHorizontalAxisTitle("Time (s)");
        label.setVerticalAxisTitle("Heart Rate (bpm)");


        Viewport viewport = heartGraph.getViewport();
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
            Log.i(LOGGER_INFO, "Ble Service discovered.");
            BleService.BleServiceBinder binder = (BleService.BleServiceBinder) service;
            svc = binder.getService();
            Log.i(LOGGER_INFO, "Notifying service...");
            svc.notifyHR(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);


            // initialize graph.
            initGraph();

            // update graph with data points in the background.
            new Thread(() -> {
                try {
                    Log.i(LOGGER_INFO, "Collecting measurements.");
                    collectMeasurements();
                    Log.i(LOGGER_INFO, "Measurements collected.");
                } catch (InterruptedException e) {
                    Log.e(LOGGER_INFO, "Error collecting measurements.");
                }
            }).start();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("PAIRED", "Service unbounded from this activity.");
        }
    };
}