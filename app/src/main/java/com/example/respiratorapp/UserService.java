package com.example.respiratorapp;


import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * @class Stores the user of this session.
 */
public class UserService extends Service {

    private static final String LOGGER_INFO = "USER_SERVICE";
    /**
     * The user for this session.
     */
    private RespiratoryUser user;

    /**
     * The binder to be given to clients.
     */
    private UserServiceBinder binder = new UserServiceBinder();

    public void deregisterUser() {
        user = null;
    }

    /**
     * Class used for the client Binder.
     */
    public class UserServiceBinder extends Binder {
        UserService getService() {
            // return instance of BleServiceBinder so clients can call public methods
            return UserService.this;
        }
    }
    @Override
    public void onCreate() {
        Log.i(LOGGER_INFO, "CREATED");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOGGER_INFO, "STARTED");
       return START_STICKY;
    }

    /**
     * Registers a specified user to be this session's user.
     * @param usr The user.
     */
    public void registerUser(RespiratoryUser usr) { user = usr; }

    /**
     * Gets this session's user.
     * @return The user.
     */
    public RespiratoryUser getActiveUser() { return user; }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return binder; }
}
