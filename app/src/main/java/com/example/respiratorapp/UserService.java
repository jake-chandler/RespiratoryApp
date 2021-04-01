package com.example.respiratorapp;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * @class Stores the user of this session.
 */
public class UserService extends Service {

    /**
     * The user for this session.
     */
    private RespiratoryUser user;

    /**
     * The binder to be given to clients.
     */
    private UserServiceBinder binder;

    /**
     * Class used for the client Binder.
     */
    public class UserServiceBinder extends Binder {
        UserService getService() {
            // return instance of BleServiceBinder so clients can call public methods
            return UserService.this;
        }
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
