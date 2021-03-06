package app.bizita.colorrush.ui.ui.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.karan_brahmaxatriya.kbnetwork.KBNetworkCheck;
import com.karan_brahmaxatriya.kbnetwork.OnChangeConnectivityListener;

import org.json.JSONObject;


import java.util.HashMap;

import app.bizita.colorrush.R;
import app.bizita.colorrush.api.BuildConstants;
import app.bizita.colorrush.api.RetrofitHelper;
import app.bizita.colorrush.model.login.LoginModel;
import app.bizita.colorrush.model.profile.ProfileModel;
import app.bizita.colorrush.ui.DashboardActivity;
import app.bizita.colorrush.ui.LoginActivity;
import app.bizita.colorrush.ui.ui.cancel_withdrawalamt.CancelWithdrawalAmtFragment;
import app.bizita.colorrush.utils.PrefUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordActivity extends AppCompatActivity {
    TextInputEditText edOldPassword, edNewPassword;
    LoginModel loginModel;
    String token;
    //    private View parent_view;
    private FloatingActionButton fab;
    private ProgressBar progress_bar;
    SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_password);
        edOldPassword = findViewById(R.id.edOldPassword);
        edNewPassword = findViewById(R.id.edOldPassword);

        loginModel = PrefUtils.getUser(this);
        token = loginModel.getData().getToken();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Change Password");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        parent_view = findViewById(android.R.id.content);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        sweetAlertDialog = new SweetAlertDialog(PasswordActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);

        KBNetworkCheck.setConnectivityListener(new OnChangeConnectivityListener() {
            @Override
            public void onChanged(boolean status) {
                Log.d("TAG", "onChanged: main = " + status);
//                            Toast.makeText(SplashActivity.this, "" + status, Toast.LENGTH_SHORT).show();
                if (status == false) {
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
                } else {
                    sweetAlertDialog.dismiss();
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePassword();
            }
        });
    }

    public void ChangePassword() {
        String pas = edOldPassword.getText().toString();
        String Npas = edNewPassword.getText().toString();
        if (pas.isEmpty()) {
            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(PasswordActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
            sweetAlertDialoga.setTitleText("Old Password is required...");
            sweetAlertDialoga.setConfirmText("Okay")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            // reuse previous dialog instance
                            sDialog.dismissWithAnimation();


                        }
                    })
                    .show();
            // Hide after some seconds

            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (sweetAlertDialoga.isShowing()) {
                        sweetAlertDialoga.dismissWithAnimation();


                    }
                }
            };

            sweetAlertDialoga.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    handler.removeCallbacks(runnable);
                }
            });

            handler.postDelayed(runnable, 1000);

            edOldPassword.requestFocus();
        } else if (Npas.isEmpty()) {

            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(PasswordActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
            sweetAlertDialoga.setTitleText("New Password is required...");
            sweetAlertDialoga.setConfirmText("Okay")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            // reuse previous dialog instance
                            sDialog.dismissWithAnimation();


                        }
                    })
                    .show();
            // Hide after some seconds

            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (sweetAlertDialoga.isShowing()) {
                        sweetAlertDialoga.dismissWithAnimation();


                    }
                }
            };

            sweetAlertDialoga.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    handler.removeCallbacks(runnable);
                }
            });

            handler.postDelayed(runnable, 1000);
            edNewPassword.requestFocus();
        } else {
            progress_bar.setVisibility(View.VISIBLE);
            fab.setAlpha(0f);
            HashMap hashMap = new HashMap();
            hashMap.put("password", edNewPassword.getText().toString());
            Call<JsonObject> loginModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).ChangePassword("Bearer " + token, hashMap);
            loginModelCall.enqueue(new Callback<JsonObject>() {

                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    JsonObject object = response.body();


                    if (response.isSuccessful()) {
                        Log.e("TAG", "Login_Response : " + new Gson().toJson(response.body()));

                        progress_bar.setVisibility(View.GONE);
                        fab.setAlpha(1f);

                        SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(PasswordActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                        sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
                        sweetAlertDialoga.setTitleText(object.get("message") + "");
                        sweetAlertDialoga.setConfirmText("Okay")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        // reuse previous dialog instance
                                        sDialog.dismissWithAnimation();


                                    }
                                })
                                .show();
                        // Hide after some seconds

                        final Handler handler = new Handler();
                        final Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (sweetAlertDialoga.isShowing()) {
                                    sweetAlertDialoga.dismissWithAnimation();


                                }
                            }
                        };

                        sweetAlertDialoga.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                handler.removeCallbacks(runnable);
                            }
                        });

                        handler.postDelayed(runnable, 1000);
                        PrefUtils.clearCurrentUser(PasswordActivity.this);
                        Intent i = new Intent(PasswordActivity.this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();


                    } else {
                        progress_bar.setVisibility(View.GONE);
                        fab.setAlpha(1f);
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(PasswordActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
                            sweetAlertDialoga.setTitleText(jObjError.getJSONArray("errors").toString().replace("[", "").replace("]", "") + "");
                            sweetAlertDialoga.setConfirmText("Okay")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            // reuse previous dialog instance
                                            sDialog.dismissWithAnimation();


                                        }
                                    })
                                    .show();
                            // Hide after some seconds

                            final Handler handler = new Handler();
                            final Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    if (sweetAlertDialoga.isShowing()) {
                                        sweetAlertDialoga.dismissWithAnimation();


                                    }
                                }
                            };

                            sweetAlertDialoga.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    handler.removeCallbacks(runnable);
                                }
                            });

                            handler.postDelayed(runnable, 1000);
                        } catch (Exception e) {
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    progress_bar.setVisibility(View.GONE);
                    fab.setAlpha(1f);
                    t.printStackTrace();
                    Log.e("Login_Response", t.getMessage() + "");
                }
            });
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
}