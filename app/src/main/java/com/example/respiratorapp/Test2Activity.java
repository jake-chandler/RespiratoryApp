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

import java.util.Calendar;

public class Test2Activity extends AppCompatActivity {

    private static final int NUM_MEASUREMENTS = 100;
    private static final int UPDATE_TIME = 100;
    private ImageView next;
    private ImageView retry;
    private GraphView BO2Graph;
    BleService svc;
    Activity activity = this;
    private LineGraphSeries<DataPoint> series;
    public static final long SAMPLE_TIME = 10000;

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
        int i = 0;
        int[][] bo2Measurements = new int[NUM_MEASUREMENTS][2];

        long endTime;
        do {
            if (i == NUM_MEASUREMENTS) { break; }
            // sleep this thread UPDATE_TIME milliseconds. to ensure retrieval of latest measurement from the sensor device.
            Thread.sleep(UPDATE_TIME );
            // get the metric from the Ble Service
            bo2Measurements[i][1] = svc.getHrVal();
            endTime = Calendar.getInstance().getTimeInMillis();
            double time = (int) (endTime);
            double b02 = bo2Measurements[i][1];
            bo2Measurements[i][0] = (int) time;

            // plot metric to graph.
            series.appendData( new DataPoint( time, b02),true,NUM_MEASUREMENTS );
            i++;
        } while (endTime - startTime < SAMPLE_TIME);
        svc.setBO2Measurements( bo2Measurements );
    }

    protected void initListeners() {
        next = (ImageView) findViewById(R.id.next_btn);
        retry = (ImageView) findViewById(R.id.retry_btn);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Test2Activity.this, Test3Activity.class);
                startActivity(intent);
            }
        });
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Test2Activity.this, Test2Activity.class);
                startActivity(intent);
            }
        });
    }

    private void initGraph() {
        BO2Graph = (GraphView) findViewById(R.id.heartrate_graph);
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



    private ServiceConnection connection = new ServiceConnection() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("PAIRED", "Ble Service discovered.");
            // initialize graph.
            initGraph();

            // update graph with data points in the background.
            new Thread(() -> {
                try {
                    collectMeasurements();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("PAIRED", "Service unbounded from this activity.");
        }
    };
}