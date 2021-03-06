package app.bizita.colorrush.ui.payment_gateway;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.multidex.MultiDex;


import com.karan_brahmaxatriya.kbnetwork.App;
import com.karan_brahmaxatriya.kbnetwork.KBNetworkCheck;

import app.bizita.colorrush.utils.ConnectivityReceiver;


public class BaseApplication extends App {

    AppEnvironment appEnvironment;
    private static BaseApplication mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        appEnvironment = AppEnvironment.SANDBOX;
        mInstance = this;
    }

    public AppEnvironment getAppEnvironment() {
        return appEnvironment;
    }

    public void setAppEnvironment(AppEnvironment appEnvironment) {
        this.appEnvironment = appEnvironment;
    }
    public static synchronized BaseApplication getInstance() {
        return mInstance;
    }
    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
