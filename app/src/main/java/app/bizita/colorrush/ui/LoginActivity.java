package app.bizita.colorrush.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.daimajia.easing.linear.Linear;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import app.bizita.colorrush.MainActivity;
import app.bizita.colorrush.R;
import app.bizita.colorrush.SplashActivity;
import app.bizita.colorrush.api.RetrofitHelper;
import app.bizita.colorrush.model.error.ErrorsModel;
import app.bizita.colorrush.model.login.LoginModel;
import app.bizita.colorrush.utils.BaseClass;
import app.bizita.colorrush.utils.NoConnectivityException;
import app.bizita.colorrush.utils.PrefUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.karan_brahmaxatriya.kbnetwork.KBNetworkCheck;
import com.karan_brahmaxatriya.kbnetwork.OnChangeConnectivityListener;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText et_password, et_email;
    private ProgressBar progress_bar;
    private FloatingActionButton fab;
    //    private View parent_view;
    LinearLayout sign_up_for_account;
    LoginModel loginModel;
    //a constant for detecting the login intent result
    private static final int RC_SIGN_IN = 234;

    //Tag for the logs optional
    private static final String TAG = "simplifiedcoding";

    //creating a GoogleSignInClient object
    GoogleSignInClient mGoogleSignInClient;

    //And also a Firebase Auth object
    FirebaseAuth mAuth;
    LoginButton loginButton;
    CallbackManager callbackManager;
    AppCompatButton btn_sign_in, loginfb_button;
    SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(LoginActivity.this);
        setContentView(R.layout.activity_login);

        loginModel = PrefUtils.getUser(LoginActivity.this);
        try {
            if (loginModel.getStatus().matches("success")) {
                Intent ia = new Intent(LoginActivity.this, StartActivity.class);
                startActivity(ia);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
//        parent_view = findViewById(android.R.id.content);/
        sign_up_for_account = findViewById(R.id.sign_up_for_account);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        loginButton = findViewById(R.id.login_button);
        loginfb_button = findViewById(R.id.loginfb_button);
        btn_sign_in = findViewById(R.id.btn_sign_in);
        callbackManager = CallbackManager.Factory.create();

       /* ((View) findViewById(R.id.sign_up_for_account)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(parent_view, "Sign up for an account", Snackbar.LENGTH_SHORT).show();
            }
        });
*/
        mAuth = FirebaseAuth.getInstance();

        //Then we need a GoogleSignInOptions object
        //And we need to build it as below
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //Then we will get the GoogleSignInClient object from GoogleSignIn class
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        boolean loggedOut = AccessToken.getCurrentAccessToken() == null;

        if (!loggedOut) {
//            Picasso.with(this).load(Profile.getCurrentProfile().getProfilePictureUri(200, 200)).into(imageView);
//            Log.d("TAG", "Username is: " + Profile.getCurrentProfile().getName());
//
//            //Using Graph API

        }
        sweetAlertDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);

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
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                //loginResult.getAccessToken();
                //loginResult.getRecentlyDeniedPermissions()
                //loginResult.getRecentlyGrantedPermissions()
//                Toast.makeText(LoginActivity.this, loginResult.getAccessToken()+"", Toast.LENGTH_SHORT).show();
                boolean loggedIn = AccessToken.getCurrentAccessToken() == null;
                Log.d("API123", loggedIn + " ??");
                getUserProfile(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                searchAction();
            }
        });
        sign_up_for_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkAvailable()) {
                    return;
                }

                showTermServicesDialog();
            }
        });
        loginfb_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkAvailable()) {
                    return;
                }

                final Dialog dialog = new Dialog(LoginActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                dialog.setContentView(R.layout.dialog_term_of_services);
                dialog.setCancelable(true);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                WebView webviewAboutUs = dialog.findViewById(R.id.webview_pp_us);
                ProgressBar progress_bar1 = dialog.findViewById(R.id.progress_bar);
                webviewAboutUs.setWebViewClient(new WebViewClient() {
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        progress_bar1.setVisibility(View.VISIBLE);
                    }


                    @Override
                    public void onPageFinished(WebView view, String url) {
                        progress_bar1.setVisibility(View.GONE);
                        String webUrl = webviewAboutUs.getUrl();

                    }

                });
                webviewAboutUs.getSettings().setJavaScriptEnabled(true);

                webviewAboutUs.loadUrl("http://13.127.177.177/api/privacy");

                ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                ((Button) dialog.findViewById(R.id.bt_accept)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loginButton.performClick();
                        dialog.dismiss();
                    }
                });

                ((Button) dialog.findViewById(R.id.bt_decline)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                dialog.show();
                dialog.getWindow().setAttributes(lp);


            }
        });
        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkAvailable()) {
                    return;
                }

                final Dialog dialog = new Dialog(LoginActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                dialog.setContentView(R.layout.dialog_term_of_services);
                dialog.setCancelable(true);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                WebView webviewAboutUs = dialog.findViewById(R.id.webview_pp_us);
                ProgressBar progress_bar1 = dialog.findViewById(R.id.progress_bar);
                webviewAboutUs.setWebViewClient(new WebViewClient() {
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        progress_bar1.setVisibility(View.VISIBLE);
                    }


                    @Override
                    public void onPageFinished(WebView view, String url) {
                        progress_bar1.setVisibility(View.GONE);
                        String webUrl = webviewAboutUs.getUrl();

                    }

                });
                webviewAboutUs.getSettings().setJavaScriptEnabled(true);
                webviewAboutUs.loadUrl("http://13.127.177.177/api/privacy");

                ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                ((Button) dialog.findViewById(R.id.bt_accept)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signIn();
                        dialog.dismiss();
                    }
                });

                ((Button) dialog.findViewById(R.id.bt_decline)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                dialog.show();
                dialog.getWindow().setAttributes(lp);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //if the user is already signed in
        //we will close this activity
        //and take the user to profile activity
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, StartActivity.class));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);


        //if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN) {

            //Getting the GoogleSignIn Task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //authenticating with firebase
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
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

    private void getUserProfile(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("TAG", object.toString());
                        try {
                            String first_name = object.getString("first_name");
                            String last_name = object.getString("last_name");
                            String email = object.getString("email");
                            String id = object.getString("id");
                            String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";

//                            txtUsername.setText("First Name: " + first_name + "\nLast Name: " + last_name);
//                            txtEmail.setText(email);
//                            Picasso.with(MainActivity.this).load(image_url).into(imageView);
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("name", first_name + "");
                            hashMap.put("email", email + "");
                            hashMap.put("facebook_id", id + "");


                            progress_bar.setVisibility(View.VISIBLE);
                            fab.setAlpha(0f);
                            Call<LoginModel> loginModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).facebook(hashMap);
                            loginModelCall.enqueue(new Callback<LoginModel>() {

                                @Override
                                public void onResponse(@NonNull Call<LoginModel> call, @NonNull Response<LoginModel> response) {
                                    LoginModel object = response.body();
//                                    hideProgressDialog();
                                    progress_bar.setVisibility(View.GONE);
                                    fab.setAlpha(1f);
                                    if (response.isSuccessful()) {
                                        Log.e("TAG", "Login_Response : " + new Gson().toJson(response.body()));
//                    labelNotification.setText(object.getResultMarque() + "");
                                        PrefUtils.setUser(object, LoginActivity.this);
                                        Intent loginIntent = new Intent(LoginActivity.this, StartActivity.class);
                                        startActivity(loginIntent);
                                        finish();

                                    } else {
                                        try {
                                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                                            Toast.makeText(LoginActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                                        } catch (Exception e) {
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<LoginModel> call, @NonNull Throwable t) {
//                                    hideProgressDialog();
                                    progress_bar.setVisibility(View.GONE);
                                    fab.setAlpha(1f);
                                    t.printStackTrace();
                                    Log.e("Login_Response", t.getMessage() + "");
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //Now using firebase we are signing in the user here
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("name", user.getDisplayName() + "");
                                hashMap.put("email", user.getEmail() + "");
                                hashMap.put("google_id", user.getUid() + "");

                                progress_bar.setVisibility(View.VISIBLE);
                                fab.setAlpha(0f);
//                                showProgressDialog();
                                Call<LoginModel> loginModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).google(hashMap);
                                loginModelCall.enqueue(new Callback<LoginModel>() {

                                    @Override
                                    public void onResponse(@NonNull Call<LoginModel> call, @NonNull Response<LoginModel> response) {
                                        LoginModel object = response.body();
//                                        hideProgressDialog();
                                        progress_bar.setVisibility(View.GONE);
                                        fab.setAlpha(1f);
                                        if (response.isSuccessful()) {
                                            Log.e("TAG", "Login_Response : " + new Gson().toJson(response.body()));
//                    labelNotification.setText(object.getResultMarque() + "");
                                            PrefUtils.setUser(object, LoginActivity.this);
                                            Intent loginIntent = new Intent(LoginActivity.this, StartActivity.class);
                                            startActivity(loginIntent);
                                            finish();

                                        } else {
                                            try {
                                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                                Toast.makeText(LoginActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                                            } catch (Exception e) {
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<LoginModel> call, @NonNull Throwable t) {
//                                        hideProgressDialog();
                                        progress_bar.setVisibility(View.GONE);
                                        fab.setAlpha(1f);
                                        t.printStackTrace();
                                        Log.e("Login_Response", t.getMessage() + "");
                                    }
                                });
                            }
                            // Toast.makeText(LoginActivity.this, "User Signed In", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }


    //this method is called on click
    private void signIn() {
        //getting the google signin intent
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        //starting the activity for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public boolean isNetworkAvailable() {
        return BaseClass.isNetworkAvailable(LoginActivity.this);
    }

    private void showTermServicesDialog() {
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_term_of_services);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        WebView webviewAboutUs = dialog.findViewById(R.id.webview_pp_us);
        ProgressBar progress_bar1 = dialog.findViewById(R.id.progress_bar);
        webviewAboutUs.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progress_bar1.setVisibility(View.VISIBLE);
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                progress_bar1.setVisibility(View.GONE);
                String webUrl = webviewAboutUs.getUrl();

            }

        });
        webviewAboutUs.getSettings().setJavaScriptEnabled(true);
        webviewAboutUs.loadUrl("http://13.127.177.177/api/privacy");

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ((Button) dialog.findViewById(R.id.bt_accept)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
                dialog.dismiss();
            }
        });

        ((Button) dialog.findViewById(R.id.bt_decline)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void searchAction() {
        if (!isNetworkAvailable()) {
            return;
        }
        final String regexStr = "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[6789]\\d{9}$";
        String input = et_email.getText().toString();
        if (input.contains("@")) {
            et_email.requestFocus();
//            Snackbar.make(parent_view,"Enter Valid Email", Snackbar.LENGTH_SHORT).show();
            progress_bar.setVisibility(View.VISIBLE);
            fab.setAlpha(0f);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("field", input + "");
//            hashMap.put("password", pass_word + "");

            Call<LoginModel> loginModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).LoginModel(hashMap);
            loginModelCall.enqueue(new Callback<LoginModel>() {

                @Override
                public void onResponse(@NonNull Call<LoginModel> call, @NonNull Response<LoginModel> response) {
                    LoginModel object = response.body();


                    if (response.code() == 422) {
                        progress_bar.setVisibility(View.GONE);
                        fab.setAlpha(1f);
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
                            sweetAlertDialoga.setTitleText(jObjError.getString("message") + "");
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (response.isSuccessful()) {
                        Log.e("TAG", "Login_Response : " + new Gson().toJson(response.body()));
//                    labelNotification.setText(object.getResultMarque() + "");
                        progress_bar.setVisibility(View.GONE);
                        fab.setAlpha(1f);

                        PrefUtils.setUser(object, LoginActivity.this);
                        Intent i = new Intent(LoginActivity.this, StartActivity.class);
//                        i.putExtra("phone", et_email.getText().toString() + "");
                        startActivity(i);
                        finish();


//

                    } else {
                        progress_bar.setVisibility(View.GONE);
                        fab.setAlpha(1f);
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
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
                public void onFailure(@NonNull Call<LoginModel> call, @NonNull Throwable t) {
                    progress_bar.setVisibility(View.GONE);
                    fab.setAlpha(1f);
                    t.printStackTrace();
                    Log.e("Login_Response", t.getMessage() + "");

                }
            });
//            return  android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches();
        } else if (input.matches(regexStr)) {
//            Snackbar.make(parent_view,"Enter Valid Phone", Snackbar.LENGTH_SHORT).show();
//            return android.util.Patterns.PHONE.matcher(input).matches();
            progress_bar.setVisibility(View.VISIBLE);
            fab.setAlpha(0f);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("field", input + "");
//            hashMap.put("password", pass_word + "");

            Call<LoginModel> loginModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).LoginModel(hashMap);
            loginModelCall.enqueue(new Callback<LoginModel>() {

                @Override
                public void onResponse(@NonNull Call<LoginModel> call, @NonNull Response<LoginModel> response) {
                    LoginModel object = response.body();


                    if (response.code() == 422) {
                        progress_bar.setVisibility(View.GONE);
                        fab.setAlpha(1f);
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());


                            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
                            sweetAlertDialoga.setTitleText(jObjError.getString("message") + "");
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (response.isSuccessful()) {
                        Log.e("TAG", "Login_Response : " + new Gson().toJson(response.body()));
//                    labelNotification.setText(object.getResultMarque() + "");
                        progress_bar.setVisibility(View.GONE);
                        fab.setAlpha(1f);

                        PrefUtils.setUser(object, LoginActivity.this);
                        Intent i = new Intent(LoginActivity.this, OtpVerifyActivity.class);
//                        i.putExtra("phone", et_email.getText().toString() + "");
                        i.putExtra("otp", object.getData().getUser().getOtp() + "");
                        i.putExtra("token", object.getData().getToken() + "");
                        i.putExtra("phone", object.getData().getUser().getMobile() + "");
                        startActivity(i);
                        finish();


//

                    } else {
                        progress_bar.setVisibility(View.GONE);
                        fab.setAlpha(1f);
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
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
                public void onFailure(@NonNull Call<LoginModel> call, @NonNull Throwable t) {
                    progress_bar.setVisibility(View.GONE);
                    fab.setAlpha(1f);
                    t.printStackTrace();
                    Log.e("Login_Response", t.getMessage() + "");

                }
            });
        } else if (input.isEmpty()) {

            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
            sweetAlertDialoga.setTitleText("Phone No. Required");
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
            et_email.requestFocus();
        } else if (!input.matches(regexStr)) {

//            return android.util.Patterns.PHONE.matcher(input).matches();

            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
            sweetAlertDialoga.setTitleText("Enter Valid Phone");
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
            et_email.requestFocus();
        }


    }

    private boolean checkValidation() {
        String input = et_email.getText().toString();
        if (input.contains("@")) {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches();
        } else {
            return android.util.Patterns.PHONE.matcher(input).matches();
        }
    }


}