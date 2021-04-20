package com.example.respiratorapp;

import android.bluetooth.BluetoothGattDescriptor;
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

/**
 * Represents a successful pair to the BLE MC.
 */
public class PairedActivity extends AppCompatActivity {

    BleService svc;

    public PairedActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //makes this activity full-screen (removes notification bar)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_paired);

        Intent intent = new Intent (this, BleService.class);

        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        initListeners();
    }

    protected void initListeners() {
        ImageView homeButton = (ImageView) findViewById(R.id.home);
        homeButton.setOnClickListener(view -> {
            Intent intent = new Intent(PairedActivity.this, HomeActivity.class);
            startActivity(intent);
        });
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

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("PAIRED", "Service unbounded from this activity.");
        }
    };
}
