package app.bizita.colorrush.utils;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;

import app.bizita.colorrush.R;

public class BaseClass extends Application {
    private static BaseClass instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

    }

    public static Context getContext() {
        return instance;
    }

    public static boolean isNetworkAvailable(Context context) {
        boolean toReturn;
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        toReturn = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        if (!toReturn) {
            showNetworkDialog(context);
        }
        return toReturn;
    }


    static void showNetworkDialog(final Context context) {
        new AlertDialog.Builder(context, R.style.AlertDialogStyle)
                .setTitle(R.string.app_name)
                .setMessage(R.string.no_internet)
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton(R.string.connect, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent settingIntent = new Intent(Settings.ACTION_SETTINGS);
                        settingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(settingIntent);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        getContext().finish();
                        dialog.dismiss();
                    }
                }).show();
    }

}
