package app.bizita.colorrush.ui.ui.home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.karan_brahmaxatriya.kbnetwork.KBNetworkCheck;
import com.karan_brahmaxatriya.kbnetwork.OnChangeConnectivityListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import app.bizita.colorrush.R;
import app.bizita.colorrush.api.RetrofitHelper;
import app.bizita.colorrush.model.dashboard.GameConfig;
import app.bizita.colorrush.model.game_start.GameStartModel;
import app.bizita.colorrush.model.login.LoginModel;
import app.bizita.colorrush.model.transcation_history.History;
import app.bizita.colorrush.model.transcation_history.TranscationHistroyModel;
import app.bizita.colorrush.model.withdrawal.WithdrawalRequestModel;
import app.bizita.colorrush.ui.DashboardActivity;
import app.bizita.colorrush.ui.LoginActivity;
import app.bizita.colorrush.ui.RegisterActivity;
import app.bizita.colorrush.ui.StartActivity;
import app.bizita.colorrush.ui.ui.gallery.GalleryFragment;
import app.bizita.colorrush.utils.BaseClass;
import app.bizita.colorrush.utils.PrefUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private ProgressBar progress_bar;
    private View parent_view;
    LoginModel loginModel;
    String token;
    RecyclerView rvHistory;
    MyCustomAdapter myCustomAdapter;
    TextView tvCurrentBalance;
    ArrayList<History> historyArrayList = new ArrayList<>();
    ImageButton ibWithdrawMoney;
    SweetAlertDialog sweetAlertDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        tvCurrentBalance = root.findViewById(R.id.tvCurrentBalance);
        rvHistory = root.findViewById(R.id.rvHistory);
        parent_view = root.findViewById(android.R.id.content);
        ibWithdrawMoney = root.findViewById(R.id.ibWithdrawMoney);
        progress_bar = root.findViewById(R.id.progress_bar);
        loginModel = PrefUtils.getUser(getContext());
        token = loginModel.getData().getToken();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvHistory.setLayoutManager(mLayoutManager);
        rvHistory.setHasFixedSize(true);
        rvHistory.setItemAnimator(new DefaultItemAnimator());
        HomeCateogry();
        sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE);

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
        ibWithdrawMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWithDrawDialog();
            }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            KBNetworkCheck.setConnectivityListener(new OnChangeConnectivityListener() {
                @Override
                public void onChanged(boolean status) {
                    Log.d("TAG", "onChanged: main = " + status);
//                            Toast.makeText(SplashActivity.this, "" + status, Toast.LENGTH_SHORT).show();
                    if (status == true) {
                        getActivity().finish();
                        startActivity(getActivity().getIntent());
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

    private void HomeCateogry() {
//        if (!isNetworkAvailable()) {
//            return;
//        }
        progress_bar.setVisibility(View.VISIBLE);
        Call<TranscationHistroyModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).TranscationHistroyModel("Bearer " + token);
        marqueCall.enqueue(new Callback<TranscationHistroyModel>() {
            @Override
            public void onResponse(@NonNull Call<TranscationHistroyModel> call, @NonNull Response<TranscationHistroyModel> response) {
                progress_bar.setVisibility(View.GONE);
                TranscationHistroyModel object = response.body();
                if (response.isSuccessful()) {
                    tvCurrentBalance.setText("" + object.getData().getCurrentBalance());
                    historyArrayList = object.getData().getHistory();
                    myCustomAdapter = new MyCustomAdapter(historyArrayList);
                    rvHistory.setAdapter(myCustomAdapter);


                } else {

                }
            }

            @Override
            public void onFailure(@NonNull Call<TranscationHistroyModel> call, @NonNull Throwable t) {

                progress_bar.setVisibility(View.GONE);
                t.printStackTrace();
                Log.e("Category_Response", t.getMessage() + "");
            }
        });


    }

    public static String formatDateToString(Date date, String format,
                                            String timeZone) {
        // null check
        if (date == null) return null;
        // create SimpleDateFormat object with input format
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        // default system timezone if passed null or empty
        if (timeZone == null || "".equalsIgnoreCase(timeZone.trim())) {
            timeZone = Calendar.getInstance().getTimeZone().getID();
        }
        // set timezone to SimpleDateFormat
        sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
        // return Date in required format with timezone as String
        return sdf.format(date);
    }

    public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.MyViewHolder> {

        private ArrayList<History> moviesList;

        public MyCustomAdapter(ArrayList<History> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getActivity())
                    .inflate(R.layout.item_history, parent, false);

            return new MyViewHolder(itemView);
        }

        public void clear() {
            int size = this.moviesList.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    this.moviesList.remove(0);
                }

                this.notifyItemRangeRemoved(0, size);
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {


            final History datum = moviesList.get(position);


            if (datum.getGameId() != null) {
                if (datum.getGameId() != 0) {
                    holder.tvReceived.setText("Game ID : " + datum.getGameId() + "");
                } else {

                }
            } else {
                if (datum.getPaymentMode() != null) {
                    holder.tvReceived.setText("" + datum.getPaymentMode());
                } else {
                    holder.tvReceived.setText("Withdrawal Request");
                }

            }

            if (datum.getStatus().matches("Won") || datum.getStatus().matches("Paid")) {
                holder.cardViewinLoss.setBackgroundColor(getResources().getColor(R.color.pink_A200));
                if (datum.getBidpoint() == null) {
                    holder.tvAmt.setText("Credited Points : " + datum.getAmount() + "");
                } else {
                    holder.tvAmt.setText("Bid Points : " + datum.getBidpoint() + "");

                }
                if (datum.getGameId() != null) {
                    if (datum.getGameId() != 0) {
                        holder.tvBalance.setText("Win Points : " + " + " + datum.getWinloss() + "");
                    } else {
                        holder.tvBalance.setVisibility(View.GONE);
                    }
                }

            } else {
                holder.cardViewinLoss.setBackgroundColor(getResources().getColor(R.color.light_green_A400));
                if (datum.getBidpoint() == null) {
                    holder.tvAmt.setText("Debited Points : " + datum.getAmount() + "");
                    holder.cardViewinLoss.setBackgroundColor(getResources().getColor(R.color.yellow_50));
                    holder.tvAmt.setTextColor(getResources().getColor(R.color.black));
                    holder.tvBalance.setTextColor(getResources().getColor(R.color.black));
                    holder.tvDate.setTextColor(getResources().getColor(R.color.black));
                    holder.tvReceived.setTextColor(getResources().getColor(R.color.black));

                } else {
                    holder.tvAmt.setText("Bid Points : " + datum.getBidpoint() + "");
                    holder.tvBalance.setText("Loss Points : " + " - " + datum.getWinloss() + "");


                }

            }

            holder.tvDate.setText(datum.getDatetime() + "");
            if (datum.getCreatedAt() != null) {
//                holder.tvDate.setText(datum.getCreatedAt() + "");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    Instant instant = Instant.parse(datum.getCreatedAt());
                    Date date = Date.from(instant);

//                holder.tv_homework_dt.setText(formatDateToString(date, "dd MMM yyyy hh:mm:ss a", null));

                }

            }


        }

        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tvReceived, tvDate, tvAmt, tvBalance;
            CardView cardViewinLoss;

            public MyViewHolder(View view) {
                super(view);

                tvReceived = view.findViewById(R.id.gameId);
                tvAmt = view.findViewById(R.id.tvAmt);
                tvBalance = view.findViewById(R.id.tvBalance);
                tvDate = view.findViewById(R.id.tvDate);
                cardViewinLoss = view.findViewById(R.id.cardViewinLoss);


            }

        }

    }

    private void showWithDrawDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_withdraw_money);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        TextInputEditText et_amt, et_remark;
        et_amt = dialog.findViewById(R.id.et_amt);
        et_remark = dialog.findViewById(R.id.et_remark);
        parent_view = dialog.findViewById(android.R.id.content);
        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ((Button) dialog.findViewById(R.id.bt_request)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email_id = et_amt.getText().toString().trim();
                final String pass_word = et_remark.getText().toString().trim();


                if (email_id.isEmpty()) {
                    Snackbar.make(parent_view, "Amount Required", Snackbar.LENGTH_SHORT).show();
                    et_amt.requestFocus();
                    return;
                } else if (pass_word.isEmpty()) {
                    Snackbar.make(parent_view, "Remark Required", Snackbar.LENGTH_SHORT).show();
                    et_remark.requestFocus();
                    return;
                } else {
                    progress_bar.setVisibility(View.VISIBLE);
//                    fab.setAlpha(0f);
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("requested_amount", email_id + "");

                    Call<WithdrawalRequestModel> loginModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).money_withdrawal("Bearer " + token, hashMap);
                    loginModelCall.enqueue(new Callback<WithdrawalRequestModel>() {

                        @Override
                        public void onResponse(@NonNull Call<WithdrawalRequestModel> call, @NonNull Response<WithdrawalRequestModel> response) {
                            WithdrawalRequestModel object = response.body();


                            if (response.isSuccessful()) {
                                Log.e("TAG", "Login_Response : " + new Gson().toJson(response.body()));
//                    labelNotification.setText(object.getResultMarque() + "");
                                progress_bar.setVisibility(View.GONE);

//                                Snackbar.make(parent_view, object.get("message") + "", Snackbar.LENGTH_SHORT).show();
                                et_amt.setText("");
                                et_remark.setText("");
//

                            } else {
                                progress_bar.setVisibility(View.GONE);
//                                fab.setAlpha(1f);
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    Snackbar.make(parent_view, jObjError.getJSONArray("errors").toString().replace("[", "").replace("]", "") + "", Snackbar.LENGTH_SHORT).show();
                                    Snackbar.make(parent_view, jObjError.getString("message") + "", Snackbar.LENGTH_SHORT).show();
//                            if (!jObjError.getString("message").isEmpty()){
//
//                            }
//                            else {
//
//                            }
                                } catch (Exception e) {
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<WithdrawalRequestModel> call, @NonNull Throwable t) {
                            progress_bar.setVisibility(View.GONE);

                            t.printStackTrace();
                            Log.e("Login_Response", t.getMessage() + "");
                        }
                    });
                }
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

    public boolean isNetworkAvailable() {
        return BaseClass.isNetworkAvailable(getContext());
    }
}