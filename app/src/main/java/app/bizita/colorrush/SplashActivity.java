package app.bizita.colorrush;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.karan_brahmaxatriya.kbnetwork.KBNetworkCheck;
import com.karan_brahmaxatriya.kbnetwork.OnChangeConnectivityListener;


import app.bizita.colorrush.ui.LoginActivity;
import app.bizita.colorrush.ui.payment_gateway.BaseApplication;

import app.bizita.colorrush.utils.ConnectivityReceiver;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_PHONE_STATE;


public class SplashActivity extends AppCompatActivity {
    Animation slide_up, rotate;
    TextView textColorRush;
    ImageView imageSplash;
    private static final int PERMISSION_REQUEST_CODE = 200;
    //    AlertDialog.Builder builder;
    private View parent_view;
    SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_splash);
        parent_view = findViewById(android.R.id.content);
        imageSplash = findViewById(R.id.imageSplash);
        textColorRush = findViewById(R.id.textColorRush);
//        builder = new AlertDialog.Builder(this);
        slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);
        rotate = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.rotate_left);
        // Slide Up
        imageSplash.startAnimation(slide_up);
        textColorRush.startAnimation(rotate);
        sweetAlertDialog = new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);

        try {
            if (Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME) == 1) {
                // Enabled

                if (!checkPermission()) {
//            openGPSSettings();

                    requestPermission();

//                    return;
//
                } else {
                    KBNetworkCheck.setConnectivityListener(new OnChangeConnectivityListener() {
                        @Override
                        public void onChanged(boolean status) {
                            Log.d("TAG", "onChanged: main = " + status);
//                            Toast.makeText(SplashActivity.this, "" + status, Toast.LENGTH_SHORT).show();
                            if (status == true) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        sweetAlertDialog.dismiss();
                                        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                                        startActivity(i);
                                        finish();


                                    }
                                }, 2000);
                            } else {
                                sweetAlertDialog.setTitleText("No Internet Connection");
                                sweetAlertDialog.setCanceledOnTouchOutside(false);
                                sweetAlertDialog.setContentText("");
                                sweetAlertDialog.setConfirmText("Okay")
                                        .setCustomImage(R.drawable.bitcoint)
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                // reuse previous dialog instance
                                                sDialog.dismissWithAnimation();
                                                Intent i = new Intent(Settings.ACTION_SETTINGS);
                                                startActivityForResult(i, 0);
//                                startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
//                                finish();


                                            }
                                        })
                                        .show();
                            }
                        }
                    });
//                    checkConnection();
//

                }
            } else {
                //Uncomment the below code to Set the message and title from the strings.xml file


                //Setting message manually and performing action on button click
         /*       builder.setMessage("We have detected that the Automatic Time and Timezone settings are not enabled on your phone. For security reasons and proper functioning of the app please enable these settings.")
                        .setCancelable(false)
                        .setPositiveButton("OPEN SETTINGS", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
//                                finish();
                                startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
                                finish();

                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();

                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Time Setting Not Automatic");
                alert.show();*/

                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                sweetAlertDialog.setTitleText("Please Set Your Time With Network Provided Time Zone");
                sweetAlertDialog.setContentText("And Try Again...");
                sweetAlertDialog.setConfirmText("Okay")
                        .setCustomImage(R.drawable.bitcoint)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                // reuse previous dialog instance
                                sDialog.dismissWithAnimation();
                                Intent i = new Intent(Settings.ACTION_DATE_SETTINGS);
                                startActivityForResult(i, 0);
//                                startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
//                                finish();


                            }
                        })
                        .show();

                // Disabed

            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            KBNetworkCheck.setConnectivityListener(new OnChangeConnectivityListener() {
                @Override
                public void onChanged(boolean status) {
                    Log.d("TAG", "onChanged: main = " + status);
//                            Toast.makeText(SplashActivity.this, "" + status, Toast.LENGTH_SHORT).show();
                    if (status == true) {
                        finish();
                        startActivity(getIntent());
                    } else {
                        sweetAlertDialog.setTitleText("No Internet Connection");
                        sweetAlertDialog.setCanceledOnTouchOutside(false);
                        sweetAlertDialog.setContentText("");
                        sweetAlertDialog.setConfirmText("Okay")
                                .setCustomImage(R.drawable.bitcoint)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        // reuse previous dialog instance
                                        sDialog.dismissWithAnimation();
                                        Intent i = new Intent(Settings.ACTION_SETTINGS);
                                        startActivityForResult(i, 0);
//                                startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
//                                finish();


                                    }
                                })
                                .show();
                    }
                }
            });

        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
//        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);

        return result == PackageManager.PERMISSION_GRANTED &&
//                result1 == PackageManager.PERMISSION_GRANTED &&
                result2 == PackageManager.PERMISSION_GRANTED &&
                result3 == PackageManager.PERMISSION_GRANTED &&
                result4 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION,
                READ_PHONE_STATE, CAMERA, CALL_PHONE
        }, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean telepone = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean camera = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean call = grantResults[3] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && telepone && camera) {
                        startActivity(new Intent(this, this.getClass()));
//                        updateUI(location);
                        finish();
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    } else {

                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to all the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, READ_PHONE_STATE, CAMERA, CALL_PHONE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(SplashActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


}