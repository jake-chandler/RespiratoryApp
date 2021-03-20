package com.example.respiratorapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

/**
 * Represents the BLE pairing process. Attempts to pair to the MC.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class PairingActivity extends Activity {

    /**
     * Request code for enabling bluetooth services.
     *
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
     * Scanned devices.
     */
    private final BluetoothDevice[] devices = new BluetoothDevice[MAX_DEVICES];

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
        handler = new Handler();

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
     *
     */
    private void postGattConnection() {


        // send GATT connection to

    }

    /**
     * @brief Helper method used to connect to MC via BLE
     */
    private void pair() {
        Log.i("PAIRING","Beginning connection process...");
        deviceListIterator = 0;

        if (bleManager != null) {
            bleAdapter = bleManager.getAdapter();
        } else {
            Log.e("PAIRING", "Error. This device does not support BLE.");
        }
        if (bleAdapter != null && !bleAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        } else {
            Log.e("PAIRING", "Error. Please enable Bluetooth on this device.");
        }

        bleScanner = bleAdapter.getBluetoothLeScanner();

        scan();

        boolean devicePaired;
        if (deviceFound) {

            // attempt to connect to GATT server.
            try {
                bleGatt = bleDevice.connectGatt(this, false, gattCallback);
                devicePaired = true;
                Log.i("PAIRING", "Connected to GATT Server successfully.");
            } catch (Exception e) {
                devicePaired = false;
                Log.e("PAIRING", "Failure to connect to GATT Server.",e);
            }

            if (devicePaired) {
                postGattConnection();
                changeScreens();
            } else {
                Log.e("PAIRING", "Failure to connect to GATT Server.");
            }
        } else {
            // Device not found.
            Log.i("PAIRING", "UGA Sensor BLE device was not found.");
        }
    }

    /**
     * Changes the activity.
     */
    private void changeScreens() {
        Intent intent = new Intent(this, PairedActivity.class);
        startActivity(intent);
    }

    /**
     * Scans for the BLE device for 10 seconds.
     */
    private void scan() {
        Log.i("PAIRING", "Now scanning for BLE devices...");
        if (bleScanner != null) {
            if (!scanning) {

                // Stops scanning after a pre-defined scan period.
                handler.postDelayed(() -> {
                    scanning = false;
                    bleScanner.stopScan(scanCallback);
                }, SCAN_TIME);

                scanning = true;
                bleScanner.startScan(scanCallback);

            } else {
                scanning = false;
                bleScanner.stopScan(scanCallback);
            }
        } else {
            Log.e("PAIRING", "Error with BLE scanner.");
        }
    }

    /**
     * Callback function that searches for the MC device upon a scan result.
     */
    private final ScanCallback scanCallback =
            new ScanCallback() {

                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    Log.i("PAIRING", "A new BLE device has been scanned.");
                    devices[deviceListIterator] = result.getDevice();
                    deviceListIterator++;

                    for (short i = 0; i < deviceListIterator; i++) {
                        if (devices[i].getName().equals(DEVICE_NAME)) {
                            bleDevice = devices[i];
                            deviceFound = true;
                            Log.i("PAIRING", "The UGA HR, RR, AND B02 sensor device has been found");
                        }
                    }

                }
            };
    private final BluetoothGattCallback gattCallback =
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
