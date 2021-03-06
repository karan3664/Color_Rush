package app.bizita.colorrush.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.karan_brahmaxatriya.kbnetwork.KBNetworkCheck;
import com.karan_brahmaxatriya.kbnetwork.OnChangeConnectivityListener;

import org.json.JSONObject;


import java.util.HashMap;

import app.bizita.colorrush.R;
import app.bizita.colorrush.api.RetrofitHelper;
import app.bizita.colorrush.model.login.LoginModel;
import app.bizita.colorrush.model.register.RegisterModel;
import app.bizita.colorrush.utils.BaseClass;
import app.bizita.colorrush.utils.PrefUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText et_password, et_email, et_username, et_cpassword, et_mobile;
    private ProgressBar progress_bar;
    private FloatingActionButton fab;
//    private View parent_view;
    LinearLayout sign_up_for_account;
    TextView textTerms;
    CheckBox cbTerms;
    SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_register);
        et_mobile = findViewById(R.id.et_mobile);
        et_email = findViewById(R.id.et_email);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_cpassword = findViewById(R.id.et_cpassword);
//        parent_view = findViewById(android.R.id.content);
        textTerms = findViewById(R.id.textTerms);
        cbTerms = findViewById(R.id.cbTerms);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        sweetAlertDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchAction();

            }
        });
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
//        textTerms.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showTermServicesDialog();
//            }
//        });
    }

    public boolean isNetworkAvailable() {
        return BaseClass.isNetworkAvailable(RegisterActivity.this);
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

    private void searchAction() {
        if (!isNetworkAvailable()) {
            return;
        }

        final String name = et_username.getText().toString().trim();
        final String email_id = et_email.getText().toString().trim();
        final String mobile = et_mobile.getText().toString().trim();

        final String pass_word = et_password.getText().toString().trim();
        final String cpass_word = et_cpassword.getText().toString().trim();


        if (name.isEmpty()) {

            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
            sweetAlertDialoga.setTitleText( "Name Required");
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
            et_username.requestFocus();
            return;
        }/* else if (email_id.isEmpty()) {

            Snackbar.make(parent_view, "Email Required", Snackbar.LENGTH_SHORT).show();
            et_email.requestFocus();
            return;
        }*/ else if (mobile.isEmpty()) {

            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
            sweetAlertDialoga.setTitleText( "Mobile Number Required");
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
            et_mobile.requestFocus();
            return;
        } /*else if (pass_word.isEmpty()) {
            Snackbar.make(parent_view, "Password Required", Snackbar.LENGTH_SHORT).show();
            et_password.requestFocus();
            return;
        } else if (!cpass_word.matches(pass_word)) {
            Snackbar.make(parent_view, "Confirm Password Not Matched", Snackbar.LENGTH_SHORT).show();
            et_cpassword.requestFocus();
            return;
        } */ else {
            progress_bar.setVisibility(View.VISIBLE);
            fab.setAlpha(0f);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("name", name + "");
            hashMap.put("mobile", mobile + "");
            hashMap.put("email", email_id + "");
//            hashMap.put("password", pass_word + "");
//            hashMap.put("password_confirmation", cpass_word + "");

            Call<LoginModel> loginModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).RegisterModel(hashMap);
            loginModelCall.enqueue(new Callback<LoginModel>() {

                @Override
                public void onResponse(@NonNull Call<LoginModel> call, @NonNull Response<LoginModel> response) {
                    LoginModel object = response.body();

                    if (response.isSuccessful()) {
                        Log.e("TAG", "Login_Response : " + new Gson().toJson(response.body()));
//                    labelNotification.setText(object.getResultMarque() + "");

                        progress_bar.setVisibility(View.GONE);
                        fab.setAlpha(1f);

                        Intent i = new Intent(RegisterActivity.this, OtpVerifyActivity.class);
                        i.putExtra("otp", object.getData().getUser().getOtp() + "");
                        i.putExtra("token", object.getData().getToken() + "");
                        i.putExtra("phone", object.getData().getUser().getMobile() + "");
                        startActivity(i);

                    } else {
                        progress_bar.setVisibility(View.GONE);
                        fab.setAlpha(1f);
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
                            sweetAlertDialoga.setTitleText( jObjError.getJSONArray("error").toString().replace("[", "").replace("]", "") +"");
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

                            handler.postDelayed(runnable, 1000);                        } catch (Exception e) {
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<LoginModel> call, @NonNull Throwable t) {
                    progress_bar.setVisibility(View.GONE);
                    fab.setAlpha(1f);
                    t.printStackTrace();
                    Log.e("Login_Response", t.getMessage() + "");
                }
            });
        }
    }
}