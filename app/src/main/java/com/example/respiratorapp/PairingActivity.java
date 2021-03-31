package com.example.respiratorapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.view.Window;
import android.view.WindowManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.List;
import java.util.UUID;

/**
 * Represents the BLE pairing process. Attempts to pair to the MC.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class PairingActivity extends Activity {

    BleService svc;
    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //makes this activity full-screen (removes notification bar)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_pairing);

        Intent intent = new Intent (this, BleService.class);

        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        Log.i("PAIRING", "4");


        /**
        Intent intent = new Intent(PairingActivity.this, PairedActivity.class);
        startActivity(intent);
         */


    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("PAIRING", "Ble Service discovered.");
            BleService.BleServiceBinder binder = (BleService.BleServiceBinder) service;
            svc = binder.getService();
            svc.setActivity(activity);
            svc.setContext(getApplicationContext());
            Intent intent = new Intent (activity, BleService.class);
            startService(intent);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("PAIRING", "Service unbounded from this activity.");
        }
    };

}
