package app.bizita.colorrush.ui.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.karan_brahmaxatriya.kbnetwork.KBNetworkCheck;
import com.karan_brahmaxatriya.kbnetwork.OnChangeConnectivityListener;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;

import app.bizita.colorrush.R;
import app.bizita.colorrush.api.RetrofitHelper;
import app.bizita.colorrush.model.buy_money.BuyMoneyModel;
import app.bizita.colorrush.model.login.LoginModel;
import app.bizita.colorrush.ui.DashboardActivity;
import app.bizita.colorrush.ui.LoginActivity;
import app.bizita.colorrush.ui.StartActivity;
import app.bizita.colorrush.ui.payment_gateway.AppEnvironment;
import app.bizita.colorrush.ui.payment_gateway.AppPreference;
import app.bizita.colorrush.ui.payment_gateway.BaseApplication;
import app.bizita.colorrush.utils.BaseClass;
import app.bizita.colorrush.utils.PrefUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyMoneyActivity extends AppCompatActivity {
    TextInputEditText et_password, et_email;
    private ProgressBar progress_bar;
    private FloatingActionButton fab;
//    private View parent_view;
    LoginModel loginModel;
    String token;
    private PayUmoneySdkInitializer.PaymentParam mPaymentParams;
    private AppPreference mAppPreference;
    private boolean isDisableExitConfirmation = false;
    private SharedPreferences settings;
    public static final String TAG = "MainActivity : ";
    String txnId;
    SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_buy_money);

        loginModel = PrefUtils.getUser(BuyMoneyActivity.this);
        token = loginModel.getData().getToken();
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_password.setText("Add points");
        sweetAlertDialog = new SweetAlertDialog(BuyMoneyActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);

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
//                    finish();
//                    startActivity(getIntent());
                }
            }
        });
//        parent_view = findViewById(android.R.id.content);
        mAppPreference = new AppPreference();
        settings = getSharedPreferences("settings", MODE_PRIVATE);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                searchAction();
            }
        });
        if (settings.getBoolean("is_prod_env", true)) {
            ((BaseApplication) getApplication()).setAppEnvironment(AppEnvironment.PRODUCTION);
//            radio_btn_production.setChecked(true);
        } else {
            ((BaseApplication) getApplication()).setAppEnvironment(AppEnvironment.SANDBOX);
//            radio_btn_sandbox.setChecked(true);
        }

//        selectSandBoxEnv();

        setupCitrusConfigs();
    }

//    public boolean isNetworkAvailable() {
//        return BaseClass.isNetworkAvailable(BuyMoneyActivity.this);
//    }

    private void searchAction() {
//        if (!isNetworkAvailable()) {
//            return;
//        }

        final String email_id = et_email.getText().toString().trim();
        final String pass_word = et_password.getText().toString().trim();


        if (email_id.isEmpty()) {

            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(BuyMoneyActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
            sweetAlertDialoga.setTitleText("Points Required");
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
            return;
        } else if (pass_word.isEmpty()) {

            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(BuyMoneyActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
            sweetAlertDialoga.setTitleText("Remark Required");
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
            et_password.requestFocus();
            return;
        } else {
//            progress_bar.setVisibility(View.VISIBLE);
//            fab.setAlpha(0f);

            double amt = Double.parseDouble(et_email.getText().toString());
            launchPayUMoneyFlow(amt, loginModel.getData().getUser().getMobile() + "", loginModel.getData().getUser().getName() + "", loginModel.getData().getUser().getEmail() + "");

        }
    }

    private void launchPayUMoneyFlow(double amount, String contact, String name, String email_id) {

        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();

        //Use this to set your custom text on result screen button
//        payUmoneyConfig.setDoneButtonText(((EditText) findViewById(R.id.status_page_et)).getText().toString());

        //Use this to set your custom title for the activity
//        payUmoneyConfig.setPayUmoneyActivityTitle(((EditText) findViewById(R.id.activity_title_et)).getText().toString());

        payUmoneyConfig.disableExitConfirmation(isDisableExitConfirmation);

        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();


        txnId = System.currentTimeMillis() + "";
        //String txnId = "TXNID720431525261327973";
        String phone = contact;
        String productName = name;
        String firstName = name;
        String email = email_id;
        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        String udf6 = "";
        String udf7 = "";
        String udf8 = "";
        String udf9 = "";
        String udf10 = "";

        AppEnvironment appEnvironment = ((BaseApplication) getApplication()).getAppEnvironment();
        builder.setAmount(String.valueOf(amount))
                .setTxnId(txnId)
                .setPhone(phone)
                .setProductName(productName)
                .setFirstName(firstName)
                .setEmail(email)
                .setsUrl(appEnvironment.surl())
                .setfUrl(appEnvironment.furl())
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setUdf6(udf6)
                .setUdf7(udf7)
                .setUdf8(udf8)
                .setUdf9(udf9)
                .setUdf10(udf10)
                .setIsDebug(appEnvironment.debug())
                .setKey(appEnvironment.merchant_Key())
                .setMerchantId(appEnvironment.merchant_ID());

        try {
            mPaymentParams = builder.build();

            /*
             * Hash should always be generated from your server side.
             * */
            //    generateHashFromServer(mPaymentParams);

            /*            *//**
             * Do not use below code when going live
             * Below code is provided to generate hash from sdk.
             * It is recommended to generate hash from server side only.
             * */
            mPaymentParams = calculateServerSideHashAndInitiatePayment1(mPaymentParams);

            if (AppPreference.selectedTheme != -1) {
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, BuyMoneyActivity.this, AppPreference.selectedTheme, mAppPreference.isOverrideResultScreen());
            } else {
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, BuyMoneyActivity.this, R.style.AppTheme_default, mAppPreference.isOverrideResultScreen());
            }

        } catch (Exception e) {
            // some exception occurred
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//            payNowButton.setEnabled(true);
        }
    }


    /**
     * Thus function calculates the hash for transaction
     *
     * @param paymentParam payment params of transaction
     * @return payment params along with calculated merchant hash
     */
    private PayUmoneySdkInitializer.PaymentParam calculateServerSideHashAndInitiatePayment1(final PayUmoneySdkInitializer.PaymentParam paymentParam) {

        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> params = paymentParam.getParams();
        stringBuilder.append(params.get(PayUmoneyConstants.KEY) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.TXNID) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.AMOUNT) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.PRODUCT_INFO) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.FIRSTNAME) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.EMAIL) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF1) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF2) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF3) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF4) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF5) + "||||||");

        AppEnvironment appEnvironment = ((BaseApplication) getApplication()).getAppEnvironment();
        stringBuilder.append(appEnvironment.salt());

        String hash = hashCal(stringBuilder.toString());
        paymentParam.setMerchantHash(hash);

        return paymentParam;
    }

    /**
     * This method generates hash from server.
     *
     * @param paymentParam payments params used for hash generation
     */
    public void generateHashFromServer(PayUmoneySdkInitializer.PaymentParam paymentParam) {
        //nextButton.setEnabled(false); // lets not allow the user to click the button again and again.

        HashMap<String, String> params = paymentParam.getParams();

        // lets create the post params
        StringBuffer postParamsBuffer = new StringBuffer();
        postParamsBuffer.append(concatParams(PayUmoneyConstants.KEY, params.get(PayUmoneyConstants.KEY)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.AMOUNT, params.get(PayUmoneyConstants.AMOUNT)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.TXNID, params.get(PayUmoneyConstants.TXNID)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.EMAIL, params.get(PayUmoneyConstants.EMAIL)));
        postParamsBuffer.append(concatParams("productinfo", params.get(PayUmoneyConstants.PRODUCT_INFO)));
        postParamsBuffer.append(concatParams("firstname", params.get(PayUmoneyConstants.FIRSTNAME)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF1, params.get(PayUmoneyConstants.UDF1)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF2, params.get(PayUmoneyConstants.UDF2)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF3, params.get(PayUmoneyConstants.UDF3)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF4, params.get(PayUmoneyConstants.UDF4)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF5, params.get(PayUmoneyConstants.UDF5)));

        String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();

        // lets make an api call
        GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
        getHashesFromServerTask.execute(postParams);
    }


    protected String concatParams(String key, String value) {
        return key + "=" + value + "&";
    }

    /**
     * This AsyncTask generates hash from server.
     */
    private class GetHashesFromServerTask extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(BuyMoneyActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... postParams) {

            String merchantHash = "";
            try {
                //TODO Below url is just for testing purpose, merchant needs to replace this with their server side hash generation url
                URL url = new URL("https://payu.herokuapp.com/get_hash");

                String postParam = postParams[0];

                byte[] postParamsByte = postParam.getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postParamsByte);

                InputStream responseInputStream = conn.getInputStream();
                StringBuffer responseStringBuffer = new StringBuffer();
                byte[] byteContainer = new byte[1024];
                for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                    responseStringBuffer.append(new String(byteContainer, 0, i));
                }

                JSONObject response = new JSONObject(responseStringBuffer.toString());

                Iterator<String> payuHashIterator = response.keys();
                while (payuHashIterator.hasNext()) {
                    String key = payuHashIterator.next();
                    switch (key) {
                        /**
                         * This hash is mandatory and needs to be generated from merchant's server side
                         *
                         */
                        case "payment_hash":
                            merchantHash = response.getString(key);
                            break;
                        default:
                            break;
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return merchantHash;
        }

        @Override
        protected void onPostExecute(String merchantHash) {
            super.onPostExecute(merchantHash);

            progressDialog.dismiss();


            if (merchantHash.isEmpty() || merchantHash.equals("")) {
                Toast.makeText(BuyMoneyActivity.this, "Could not generate hash", Toast.LENGTH_SHORT).show();
            } else {
                mPaymentParams.setMerchantHash(merchantHash);

                if (AppPreference.selectedTheme != -1) {
                    PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, BuyMoneyActivity.this, AppPreference.selectedTheme, mAppPreference.isOverrideResultScreen());
                } else {
                    PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, BuyMoneyActivity.this, R.style.AppTheme_default, mAppPreference.isOverrideResultScreen());
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            KBNetworkCheck.setConnectivityListener(new OnChangeConnectivityListener() {
                @Override
                public void onChanged(boolean status) {
                    Log.d("TAG", "onChanged: main = " + status);
//                            Toast.makeText(SplashActivity.this, "" + status, Toast.LENGTH_SHORT).show();
                    if (status == true) {
                        sweetAlertDialog.dismiss();
                        finish();
                        startActivity(getIntent());
                    } else {
//                        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(BuyMoneyActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
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
        // Result Code is -1 send from Payumoney activity
        Log.d("MainActivity", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data !=
                null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager
                    .INTENT_EXTRA_TRANSACTION_RESPONSE);

            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

            // Check which object is non-null
            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                String payuResponse = transactionResponse.getPayuResponse();

                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();
                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    //Success Transaction
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("transaction_id", txnId + "");
                    hashMap.put("payment_mode", "Card Payment");
                    hashMap.put("amount", et_email.getText().toString() + "");
                    hashMap.put("status", "Paid");

                    Call<BuyMoneyModel> loginModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).BuyMoneyModel("Bearer " + token, hashMap);
                    loginModelCall.enqueue(new Callback<BuyMoneyModel>() {

                        @Override
                        public void onResponse(@NonNull Call<BuyMoneyModel> call, @NonNull Response<BuyMoneyModel> response) {
                            BuyMoneyModel object = response.body();

                            progress_bar.setVisibility(View.GONE);
                            fab.setAlpha(1f);
                            if (response.isSuccessful()) {
                                Log.e("TAG", "Login_Response : " + new Gson().toJson(response.body()));
//                    labelNotification.setText(object.getResultMarque() + "");
                                progress_bar.setVisibility(View.GONE);
                                fab.setAlpha(1f);

                                SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(BuyMoneyActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                                sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
                                sweetAlertDialoga.setTitleText( "Payment Successfull");
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
                                et_email.setText("");
                                et_password.setText("");

                            } else {
                                progress_bar.setVisibility(View.GONE);
                                fab.setAlpha(1f);
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(BuyMoneyActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
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
                        public void onFailure(@NonNull Call<BuyMoneyModel> call, @NonNull Throwable t) {
                            progress_bar.setVisibility(View.GONE);
                            fab.setAlpha(1f);
                            t.printStackTrace();
                            Log.e("Login_Response", t.getMessage() + "");
                        }
                    });
                } else {
                    //Failure Transaction
                    new SweetAlertDialog(BuyMoneyActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                            .setTitleText("Sorry Order Failed")
                            .setContentText("Please try again...")
                            .setConfirmText("Okay")
                            .setCustomImage(R.drawable.bitcoint)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    // reuse previous dialog instance
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                }

                // Response from Payumoney


              /*  new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("Payu's Data : " + payuResponse + "\n\n\n Merchant's Data: " + merchantResponse)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();*/

            } else if (resultModel != null && resultModel.getError() != null) {
                Log.d(TAG, "Error response : " + resultModel.getError().getTransactionResponse());
            } else {
                Log.d(TAG, "Both objects are null!");
            }
        }
    }

    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }

    private void setupCitrusConfigs() {
        AppEnvironment appEnvironment = ((BaseApplication) getApplication()).getAppEnvironment();
        if (appEnvironment == AppEnvironment.PRODUCTION) {
//            Toast.makeText(MainActivity.this, "Environment Set to Production", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(MainActivity.this, "Environment Set to SandBox", Toast.LENGTH_SHORT).show();
        }
    }


}