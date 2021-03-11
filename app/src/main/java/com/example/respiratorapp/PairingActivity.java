package com.example.respiratorapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import java.util.List;

/**
 * Represents the BLE pairing process. Attempts to pair to the MC.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class PairingActivity extends Activity {

    /**
     * Request code for enabling bluetooth services.
     * @note check for consistency in the return method to verify successful enabling.
     */
    private static final int REQUEST_ENABLE_BT = 1;

    /**
     * The name of the device to pair with.
     */
    private static final String DEVICE_NAME = "uga_respiratory_mc";

    /**
     * Time to scan for device in milliseconds.
     */
    private static final long SCAN_TIME = 10000;

    /**
     * Maximum number of devices to scan for.
     */
    private static final short MAX_DEVICES = 20;

    /**
     * BLE Manager for adapter retrieval.
     */
    private final BluetoothManager bleManager = getSystemService(BluetoothManager.class);

    /**
     * BLE Adapter for determining if bluetooth is enabled.
     */
    private BluetoothAdapter bleAdapter = null;
    private BluetoothLeScanner bleScanner;

    private Handler handler;

    /**
     * GATT server connection to be exported to the 'TestingActivity' class.
     */
    private BluetoothGatt bleGatt;

    /**
     * True if the device is scanning, false otherwise.
     */
    private boolean scanning;

    /**
     * True if specified device is found, false otherwise.
     */
    private boolean deviceFound = false;

    /**
     * True upon successful GATT connection, false otherwise.
     */
    private boolean devicePaired = false;

    /**
     * Scanned devices.
     */
    private BluetoothDevice[] devices = new BluetoothDevice[MAX_DEVICES];

    /**
     * The desired device to connect to.
     */
    private BluetoothDevice bleDevice;

    /**
     * Used to iterate through the list of devices.
     */
    private short deviceListIterator;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //makes this activity full-screen (removes notification bar)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_pairing);

        // pair to the device
        pair();

    }

    /**
     * Sends GATT connection to 'TestingActivity' class.
     * @return true on success, false otherwise.
     */
    private boolean postGattConnection() {


        // send GATT connection to

        return true;
    }

    /**
     * @brief Helper method used to connect to MC via BLE
     *
     * @return false on pairing fail, true on pairing success
     */
    private boolean pair() {
        deviceListIterator = 0;

        if (bleManager != null) {
            bleAdapter = bleManager.getAdapter();
        }
        if (bleAdapter != null && !bleAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return false;
        }

        scan();

        if (deviceFound) {

            // attempt to connect to GATT server.
            try {

                bleGatt = bleDevice.connectGatt(this, false, gattCallback);
                devicePaired = true;
            } catch (Exception e) {
                devicePaired = false;
                System.out.println(e.getMessage());
            }

            if (devicePaired) {
                postGattConnection();
                changeScreens();
            } else {
                // Unsuccessful pair. TODO
            }
        } else {
            // Device not found.
            devicePaired = false;
        }
        return true;
    }

    /**
     * Changes the activity.
     */
    private void changeScreens() {
        Intent intent = new Intent(this, PairedActivity.class);
        startActivity(intent);
    }

    /**
     * Scans for the BLE devices for 10 seconds.
     */
    private void scan() {
        if (bleScanner != null) {
            if (!scanning) {

                // Stops scanning after a pre-defined scan period.
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scanning = false;
                        bleScanner.stopScan(scanCallback);
                    }
                }, SCAN_TIME);

                scanning = true;
                bleScanner.startScan(scanCallback);

            } else {
                scanning = false;
                bleScanner.stopScan(scanCallback);
            }
        }
    }

    /**
     * Callback function that searches for the MC device upon a scan result.
     */
    private ScanCallback scanCallback =
            new ScanCallback() {

                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    devices[deviceListIterator] = result.getDevice();
                    deviceListIterator++;

                    for (short i = 0; i < deviceListIterator; i++) {
                        if (devices[i].getName().equals(DEVICE_NAME)) {
                            bleDevice = devices[i];
                            deviceFound = true;
                        }
                    }

                }
            };
    private BluetoothGattCallback gattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);

                }
            };


}
