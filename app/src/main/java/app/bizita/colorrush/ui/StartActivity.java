package app.bizita.colorrush.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.karan_brahmaxatriya.kbnetwork.KBNetworkCheck;
import com.karan_brahmaxatriya.kbnetwork.OnChangeConnectivityListener;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import app.bizita.colorrush.R;
import app.bizita.colorrush.api.RetrofitHelper;
import app.bizita.colorrush.model.dashboard.DashboardModel;
import app.bizita.colorrush.model.login.LoginModel;
import app.bizita.colorrush.ui.ui.BuyMoneyActivity;
import app.bizita.colorrush.ui.ui.gallery.GalleryFragment;
import app.bizita.colorrush.utils.PrefUtils;
import app.bizita.colorrush.utils.ViewDialog;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartActivity extends AppCompatActivity {

    Button playBtn, creditsBtn, quitBtn;
    View viewBg;
    private ProgressBar progress_bar;
//    private View parent_view;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    LoginModel loginModel;
    String token;
    SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_start);
        loginModel = PrefUtils.getUser(this);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //Then we will get the GoogleSignInClient object from GoogleSignIn class
        mGoogleSignInClient = GoogleSignIn.getClient(StartActivity.this, gso);
        // Passing each menu ID as a set of Ids because each

//        parent_view = findViewById(android.R.id.content);
//        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        playBtn = findViewById(R.id.playBtn);
        creditsBtn = findViewById(R.id.creditsBtn);
        quitBtn = findViewById(R.id.quitBtn);
        viewBg = findViewById(R.id.viewBg);
         sweetAlertDialog = new SweetAlertDialog(StartActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);

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
                }
                else {
                    sweetAlertDialog.dismiss();
                }
            }
        });
        GradientDrawable rainbow = new GradientDrawable(GradientDrawable.Orientation.BL_TR,
                new int[]{Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED});
        viewBg.setBackground(rainbow);
        if (loginModel != null) {
            token = loginModel.getData().getToken();


            GetColor();
        } else {
            SweetAlertDialog pDialog = new SweetAlertDialog(StartActivity.this, SweetAlertDialog.SUCCESS_TYPE);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setTitleText("Please Login Again")
                    .setContentText("Your Session is Destroy...")
                    .setConfirmText("Okay")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            // reuse previous dialog instance
                            if (AccessToken.getCurrentAccessToken() != null) {
                                new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                                        .Callback() {
                                    @Override
                                    public void onCompleted(GraphResponse graphResponse) {

                                        LoginManager.getInstance().logOut();
                                        PrefUtils.clearCurrentUser(StartActivity.this);
                                        Intent i = new Intent(StartActivity.this, LoginActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        finish();

                                    }
                                }).executeAsync();
                            } else if (mAuth.getCurrentUser() != null) {
                                mAuth.signOut();
                                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        PrefUtils.clearCurrentUser(StartActivity.this);
                                        Intent i = new Intent(StartActivity.this, LoginActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        finish();
                                    }
                                });
                            } else {
                                PrefUtils.clearCurrentUser(StartActivity.this);
                                Intent i = new Intent(StartActivity.this, LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
                            }

                            sDialog.dismissWithAnimation();
                            sDialog.setCanceledOnTouchOutside(false);

                        }
                    })
                    .show();

        }

    }

    public void GetColor() {

//        progress_bar.setVisibility(View.VISIBLE);
        Call<DashboardModel> loginModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).DashboardModel("Bearer " + token);
        loginModelCall.enqueue(new Callback<DashboardModel>() {

            @Override
            public void onResponse(@NonNull Call<DashboardModel> call, @NonNull Response<DashboardModel> response) {
                DashboardModel object = response.body();


                if (response.isSuccessful()) {


                    if (object.getData() != null) {
                        Log.e("TAG", "Login_Response : " + new Gson().toJson(response.body()));
//                    labelNotification.setText(object.getResultMarque() + "");
//                        progress_bar.setVisibility(View.GONE);
//                    fab.setAlpha(1f);
//                    Snackbar.make(parent_view, "Login Successfull...", Snackbar.LENGTH_SHORT).show();
                        playBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(StartActivity.this, DashboardActivity.class);
                                startActivity(i);

                            }
                        });
                        creditsBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(StartActivity.this, BuyMoneyActivity.class);
                                startActivity(i);
                            }
                        });
                        quitBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new SweetAlertDialog(StartActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Are you sure?")
                                        .setContentText("You want to Quit ?")
                                        .setConfirmText("Yes")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                // reuse previous dialog instance
                                                if (AccessToken.getCurrentAccessToken() != null) {
                                                    new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                                                            .Callback() {
                                                        @Override
                                                        public void onCompleted(GraphResponse graphResponse) {

                                                            LoginManager.getInstance().logOut();
                                                            PrefUtils.clearCurrentUser(StartActivity.this);
                                                            Intent i = new Intent(StartActivity.this, LoginActivity.class);
                                                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(i);
                                                            finish();

                                                        }
                                                    }).executeAsync();
                                                } else if (mAuth.getCurrentUser() != null) {
                                                    mAuth.signOut();
                                                    mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            PrefUtils.clearCurrentUser(StartActivity.this);
                                                            Intent i = new Intent(StartActivity.this, LoginActivity.class);
                                                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(i);
                                                            finish();
                                                        }
                                                    });
                                                } else {
                                                    PrefUtils.clearCurrentUser(StartActivity.this);
                                                    Intent i = new Intent(StartActivity.this, LoginActivity.class);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(i);
                                                    finish();
                                                }
                                            }
                                        })
                                        .setCancelButton("No", null)
                                        .show();
                            }
                        });

                    } else {
                        new SweetAlertDialog(StartActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Please Login Again")
                                .setContentText("Your Session is Destroy...")
                                .setConfirmText("Okay")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        // reuse previous dialog instance
                                        if (AccessToken.getCurrentAccessToken() != null) {
                                            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                                                    .Callback() {
                                                @Override
                                                public void onCompleted(GraphResponse graphResponse) {

                                                    LoginManager.getInstance().logOut();
                                                    PrefUtils.clearCurrentUser(StartActivity.this);
                                                    Intent i = new Intent(StartActivity.this, LoginActivity.class);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(i);
                                                    finish();

                                                }
                                            }).executeAsync();
                                        } else if (mAuth.getCurrentUser() != null) {
                                            mAuth.signOut();
                                            mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    PrefUtils.clearCurrentUser(StartActivity.this);
                                                    Intent i = new Intent(StartActivity.this, LoginActivity.class);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(i);
                                                    finish();
                                                }
                                            });
                                        } else {
                                            PrefUtils.clearCurrentUser(StartActivity.this);
                                            Intent i = new Intent(StartActivity.this, LoginActivity.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(i);
                                            finish();
                                        }

                                        sDialog.dismissWithAnimation();
                                        sDialog.setCanceledOnTouchOutside(false);

                                    }
                                })
                                .show();

                    }


//

                } else {
//                    progress_bar.setVisibility(View.GONE);
//                    fab.setAlpha(1f);
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(StartActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
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
            public void onFailure(@NonNull Call<DashboardModel> call, @NonNull Throwable t) {
//                progress_bar.setVisibility(View.GONE);
//                fab.setAlpha(1f);
                t.printStackTrace();
                Log.e("Login_Response", t.getMessage() + "");
            }
        });
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