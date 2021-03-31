package com.example.respiratorapp;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Calendar;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

/**
 * Represents a background service that manages Bluetooth Low Energy connection
 * with the UGA Sensor device. Provides an interface for retrieving BLE characteristic(s) values of
 * the offered BLE services.
 *
 * @note This service should be started in the PairingActivity class
 *       and terminated in the TestingActivity class.
 */
public class BleService extends Service {
    /**
     * Request code for enabling bluetooth services.
     */
    private static final int REQUEST_ENABLE_BT = 1;

    /**
     * UUIDs for HR, RR, and B02 characteristics
     */
    private static final String UUID_HR = "00002a37-0000-1000-8000-00805f9b34fb";
    private static final String UUID_RR = "00003b57-0000-1000-8000-00805f9b34fb";


    /**
     * The name of the device to pair with.
     */
    private static final String DEVICE_NAME = "UGA HR RR and B02 Sensor";

    /**
     * Logging context.
     */
    private static final String LOGGER_INFO = "BLE_SERVICE";

    /**
     * Time to scan for device in milliseconds.
     */
    private static final long SCAN_TIME = 5000;

    /**
     * Time to sample measurement values.
     */
    private static final long SAMPLE_TIME = 10000;

    /**
     * The number of measurements to collect.
     */
    private static final int NUM_MEASUREMENTS = 100;

    /**
     * BLE Manager for adapter retrieval.
     */
    private BluetoothManager bleManager;

    /**
     * BLE Adapter for determining if bluetooth is enabled.
     */
    private BluetoothAdapter bleAdapter;
    private BluetoothLeScanner bleScanner;
    private BluetoothGattDescriptor rrDescriptor;
    private BluetoothGattDescriptor hrDescriptor;

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
     * The context this service is started in.
     */
    Context context;

    /**
     * The activity this service is started in.
     */
    Activity activity;

    /**
     * Binder given to clients
     */
    private final IBinder bleBinder = new BleServiceBinder();

    /**
     * The UGA Sensor device to connect to.
     */
    private BluetoothDevice bleDevice;

    /**
     * Characteristics to be read
     */
    private int hrVal, rrVal;

    private int [][] rrMeasurements, hrMeasurements = new int [NUM_MEASUREMENTS][2];

    public void setHRMeasurement( int[][] arr) {
        this.hrMeasurements = arr;
    }

    public int[][] getHRMeasurement(){
        return this.hrMeasurements;
    }

    /**
     * Class used for the client Binder.
     */
    public class BleServiceBinder extends Binder {
        BleService getService() {
            // return instance of BleServiceBinder so clients can call public methods
            return BleService.this;
        }
    }

    /**
     * Used to set the context of the activity this Service is started in.
     * @param context The context the service is started in
     * @note context is expected to be the PairingActivity context.
     */
    public void setContext(Context context) {
        this.context = context;
    }
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public int getHrVal(){
        return this.hrVal;
    }

    public int getRrVal(){
        return this.rrVal;
    }


    /**
     * Notifies the BLE Device to update for HR measurements.
     *
     * @param val BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE to notify, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
     *            to turn off notifications.
     */
    public void notifyHR(byte[] val) {
        if (val == BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE) {
            bleGatt.setCharacteristicNotification(hrDescriptor.getCharacteristic(), true);
        }
        else if (val == BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE) {
            bleGatt.setCharacteristicNotification(hrDescriptor.getCharacteristic(), false);
        }
        hrDescriptor.setValue(val);
        bleGatt.writeDescriptor(hrDescriptor);
        Log.i(LOGGER_INFO, "Changing heart rate notifications.");
    }

    /**
     * Notifies the BLE Device to update for RR measurements.
     *
     * @param val BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE to notify, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
     *            to turn off notifications.
     */
    public void notifyRR(byte[] val) {
        if (val == BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE) {
            bleGatt.setCharacteristicNotification(rrDescriptor.getCharacteristic(), true);
        }
        else if (val == BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE) {
            bleGatt.setCharacteristicNotification(rrDescriptor.getCharacteristic(), false);
        }
        rrDescriptor.setValue(val);
        bleGatt.writeDescriptor(rrDescriptor);
        Log.i(LOGGER_INFO, "Changing respiratory rate notifications.");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return bleBinder;
    }

    @Override
    public void onCreate() {
        Log.i(LOGGER_INFO, "Beginning connection process...");
        handler = new Handler();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bleManager = activity.getSystemService(BluetoothManager.class);

        if (bleManager != null) {
            bleAdapter = bleManager.getAdapter();
        } else {
            Log.e(LOGGER_INFO, "Error. This device does not support BLE.");
        }
        if (bleAdapter != null && !bleAdapter.isEnabled()) {
            Log.i(LOGGER_INFO, "Requesting to enable Bluetooth...");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Log.i(LOGGER_INFO, "Bluetooth already enabled.");
        }
        bleScanner = bleAdapter.getBluetoothLeScanner();
        scan(context);
        return START_STICKY;
    }

    /**
     * Helper method used to handle scanning results.
     *
     * @param context The context of the activity that started the service (PairingActivity)
     */
    private void handleResults(Context context) {
        boolean devicePaired = false;
        if (deviceFound) {

            // attempt to connect to GATT server.
            try {
                bleGatt = bleDevice.connectGatt(context, false, gattCallback);
                if (bleGatt.connect()) {
                    Log.i(LOGGER_INFO, "Connected to GATT Server successfully.");
                    devicePaired = true;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            boolean state = bleGatt.discoverServices();
                            Log.i(LOGGER_INFO, "State Service Discovered: " + state);
                        }
                    }, 3000);

                }
            } catch (Exception e) {
                devicePaired = false;
                Log.e(LOGGER_INFO, "Failure to connect to GATT Server.", e);
            }

            if (devicePaired) {

            } else {
                Log.e(LOGGER_INFO, "Failure to connect to GATT Server.");
            }
        } else {
            // Device not found.
            Log.i(LOGGER_INFO, "UGA Sensor BLE device was not found.");
        }
    }

    /**
     * Helper method that scans for the BLE device for a predefined period.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scan(Context context) {
        Log.i(LOGGER_INFO, "Now scanning for BLE devices...");
        if (bleScanner != null) {
            if (!scanning) {
                // Stops scanning after a pre-defined scan period.
                handler.postDelayed(() -> {
                    scanning = false;
                    Log.i(LOGGER_INFO, "Terminating the scanning procedure.");
                    bleScanner.stopScan(scanCallback);
                    handleResults(context);
                }, SCAN_TIME);

                scanning = true;
                bleScanner.startScan(scanCallback);
            }
        } else {
            Log.e(LOGGER_INFO, "Error with BLE scanner.");
        }
    }


    /**
     * Helper method used to obtain the BluetoothGattDescriptor for for HR, RR, and B02 characteristics from the offered services
     * @param services The services offered by the UGA sensor device.
     */
    private void findServices(List<BluetoothGattService> services) {
        for (BluetoothGattService service : services) {
            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic : characteristics) {
                if (characteristic.getUuid().toString().equals(UUID_HR)) {
                    BluetoothGattDescriptor descriptor =
                            characteristic.getDescriptor(characteristic.getDescriptors().get(0).getUuid());
                    hrDescriptor = descriptor;
                }
                if (characteristic.getUuid().toString().equals(UUID_RR)) {
                    bleGatt.setCharacteristicNotification(characteristic, true);
                    BluetoothGattDescriptor descriptor =
                            characteristic.getDescriptor(characteristic.getDescriptors().get(0).getUuid());
                    rrDescriptor = descriptor;
                }
            }
        }

    }

    /**
     * Callbacks for Ble GATT events.
     */
    private final BluetoothGattCallback gattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);
                    Log.i(LOGGER_INFO, "Services discovered.");
                    findServices(bleGatt.getServices());

                    Intent intent = new Intent(activity, PairedActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    super.onCharacteristicChanged(gatt, characteristic);
                    if (characteristic.getUuid().toString().equals(UUID_HR)) {
                        // Update heart rate value.
                        hrVal = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1);
                    }
                    if (characteristic.getUuid().toString().equals(UUID_RR)) {
                        // Update respiratory rate value.
                        rrVal = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1);
                    }
                }
            };

    /**
     * Callbacks for android BluetoothManager scanning events.
     */
    private final ScanCallback scanCallback = new ScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            if (!(result.getDevice().getName() == null) && result.getDevice().getName().equals(DEVICE_NAME)) {
                bleDevice = result.getDevice();
                deviceFound = true;
                Log.i(LOGGER_INFO, "Device " + result.getDevice().getName() + " has been found.");
            }

        }
    };

}
