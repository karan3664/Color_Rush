package app.bizita.colorrush.ui.ui.cancel_withdrawalamt;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.karan_brahmaxatriya.kbnetwork.KBNetworkCheck;
import com.karan_brahmaxatriya.kbnetwork.OnChangeConnectivityListener;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import app.bizita.colorrush.R;
import app.bizita.colorrush.api.RetrofitHelper;
import app.bizita.colorrush.model.bank.all_bank_list.AllBankListsModel;
import app.bizita.colorrush.model.bank.all_bank_list.BankList;
import app.bizita.colorrush.model.login.LoginModel;
import app.bizita.colorrush.model.withdrawal.cancel.CancelWithdrawalModel;
import app.bizita.colorrush.model.withdrawal.list.CurrentWithdrawalArray;
import app.bizita.colorrush.model.withdrawal.list.WithdrawalListModel;
import app.bizita.colorrush.ui.ui.profile.AccountDetailsActivity;
import app.bizita.colorrush.ui.ui.profile.EditBankDetailsActivity;
import app.bizita.colorrush.utils.BaseClass;
import app.bizita.colorrush.utils.PrefUtils;
import app.bizita.colorrush.utils.ViewDialog;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CancelWithdrawalAmtFragment extends Fragment {

    LoginModel loginModel;
    protected ViewDialog viewDialog;
    String token;
    private ProgressBar progress_bar, progress_bar1;
    private View parent_view;
    RecyclerView rvAllBankList;
    private CustomAdapter customAdapter;
    ArrayList<CurrentWithdrawalArray> withdrawalListModelArrayList = new ArrayList<>();
    SweetAlertDialog sweetAlertDialog;
    TextView tvCurrentBalance;
    LinearLayout lyt_parent1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_cancel_withdrawal_amt, container, false);
        loginModel = PrefUtils.getUser(getActivity());
        token = loginModel.getData().getToken();
        viewDialog = new ViewDialog(getContext());
        viewDialog.setCancelable(false);

        parent_view = getActivity().findViewById(android.R.id.content);
        progress_bar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        progress_bar1 = (ProgressBar) rootView.findViewById(R.id.progress_bar1);



        rvAllBankList = rootView.findViewById(R.id.rvWiList);
        lyt_parent1 = rootView.findViewById(R.id.lyt_parent);
        tvCurrentBalance = rootView.findViewById(R.id.tvCurrentBalance);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvAllBankList.setLayoutManager(layoutManager);
        rvAllBankList.setHasFixedSize(true);
        rvAllBankList.setNestedScrollingEnabled(false);
        sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE);
        lyt_parent1.setVisibility(View.GONE);
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
                    GetHomeWrk();
//                    finish();
//                    startActivity(getIntent());
                }
            }
        });
        GetHomeWrk();
        return rootView;
    }

    public boolean isNetworkAvailable() {
        return BaseClass.isNetworkAvailable(getContext());
    }

    public void GetHomeWrk() {
//        if (!isNetworkAvailable()) {
//            return;
//        }
        HashMap<String, String> hashMap = new HashMap<>();
        showProgressDialog();
        Call<WithdrawalListModel> schoolModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).WithdrawalListModel("Bearer " + token, hashMap);
        schoolModelCall.enqueue(new Callback<WithdrawalListModel>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<WithdrawalListModel> call, @NonNull Response<WithdrawalListModel> response) {
                WithdrawalListModel object = response.body();
                hideProgressDialog();

                if (response.isSuccessful()) {
                    lyt_parent1.setVisibility(View.VISIBLE);
                    Log.e("TAG", "School_Response : " + new Gson().toJson(response.body()));
                    if (!object.getData().getCurrentWithdrawalArray().isEmpty()) {
                        withdrawalListModelArrayList = object.getData().getCurrentWithdrawalArray();
                        tvCurrentBalance.setText(" " + object.getData().getCurrentBalance() + "");
                        customAdapter = new CustomAdapter(withdrawalListModelArrayList);
                        rvAllBankList.setAdapter(customAdapter);
                    } else {
//                        Snackbar.make(parent_view, "No Data Available", Snackbar.LENGTH_SHORT).show();
                        lyt_parent1.setVisibility(View.GONE);
                        SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                        sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
                        sweetAlertDialoga.setTitleText("No Data Available");
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
                    }


                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Snackbar.make(parent_view, jObjError.getJSONArray("error") + "", Snackbar.LENGTH_SHORT).show();
                        SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                        sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
                        sweetAlertDialoga.setTitleText(jObjError.getJSONArray("error") + "");
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
            public void onFailure(@NonNull Call<WithdrawalListModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                t.printStackTrace();
                Log.e("School_Response", t.getMessage() + "");
                SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
                sweetAlertDialoga.setTitleText(t.getMessage() + "");
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
            }
        });
    }

    protected void hideProgressDialog() {
        viewDialog.dismiss();
    }

    protected void showProgressDialog() {
        viewDialog.show();
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

        private ArrayList<CurrentWithdrawalArray> moviesList;

        public CustomAdapter(ArrayList<CurrentWithdrawalArray> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_withdrawal_amt_lisy, parent, false);

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


            final CurrentWithdrawalArray datum = moviesList.get(position);
//            holder.amt.setText(" " + datum.getBalance() + "");
            holder.requestAmt.setText(" " + datum.getRequestedAmount() + "");
//            holder.transferAmt.setText(" " + datum.getTransferAmount() + "");
            Date d = new Date();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
            String currentDateTimeString = sdf.format(d);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
            Date startDate = null;
            Date endDate = null;
            try {
                startDate = simpleDateFormat.parse(currentDateTimeString);
                endDate = simpleDateFormat.parse(datum.getExpiresOn() + "");


//                        timer.setText(min + ": " + sec); datum.getExpiresOn() +
                assert startDate != null;
                assert endDate != null;
                if (startDate.getTime() > endDate.getTime()) {
                    holder.btnCancel.setEnabled(false);
                    holder.btnCancel.setBackgroundColor(getResources().getColor(R.color.green_A700));
                    holder.btnCancel.setText("Request Submitted");


                }
       /*         long difference = endDate.getTime() - startDate.getTime();
                if (difference < 0) {
                    Date dateMax = null;
                    Date dateMin = null;
                    try {
                        dateMax = simpleDateFormat.parse("24:00:00");
                        dateMin = simpleDateFormat.parse("00:00:00");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    difference = (dateMax.getTime() - startDate.getTime()) + (endDate.getTime() - dateMin.getTime());
                }


                Log.e("END_TIME", difference + "");
                new CountDownTimer(difference, 1000) { // adjust the milli seconds here

                    public void onTick(long millisUntilFinished) {
                        Log.e("END_TIME", millisUntilFinished + "");
                    }

                    public void onFinish() {
                        GetHomeWrk();

                    }
                }.start();
*/
            } catch (ParseException e) {
                e.printStackTrace();
            }


            holder.btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("withdrawal_id", datum.getId() + "");
                    showProgressDialog();
                    Call<CancelWithdrawalModel> schoolModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).CancelWithdrawalModel("Bearer " + token, hashMap);
                    schoolModelCall.enqueue(new Callback<CancelWithdrawalModel>() {

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onResponse(@NonNull Call<CancelWithdrawalModel> call, @NonNull Response<CancelWithdrawalModel> response) {
                            CancelWithdrawalModel object = response.body();

                            hideProgressDialog();
                            if (response.isSuccessful()) {
                                Log.e("TAG", "School_Response : " + new Gson().toJson(response.body()));
                                if (object != null) {
//                                    Snackbar.make(parent_view, object.getMessage() + "", Snackbar.LENGTH_SHORT).show();
                                    SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                                    sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
                                    sweetAlertDialoga.setTitleText(object.getMessage() + "");
                                    sweetAlertDialoga.setConfirmText("Okay")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    // reuse previous dialog instance
                                                    sDialog.dismissWithAnimation();
                                                    GetHomeWrk();

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
                                                GetHomeWrk();
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
                                }


                            } else {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
//                                    Snackbar.make(parent_view, jObjError.getJSONArray("error") + "", Snackbar.LENGTH_SHORT).show();
                                    SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                                    sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
                                    sweetAlertDialoga.setTitleText(jObjError.getJSONArray("error") + "");
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
                        public void onFailure(@NonNull Call<CancelWithdrawalModel> call, @NonNull Throwable t) {
                            hideProgressDialog();
                            t.printStackTrace();
                            Log.e("School_Response", t.getMessage() + "");
                            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
                            sweetAlertDialoga.setTitleText(t.getMessage() + "");
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
                        }
                    });
                }
            });

        }

        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public void refreshEvents(ArrayList<CurrentWithdrawalArray> events) {
            this.moviesList.clear();
            this.moviesList.addAll(events);
            notifyDataSetChanged();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {


            LinearLayout lyt_parent;
            TextView amt, requestAmt, transferAmt;
            Button btnCancel;

            public MyViewHolder(View view) {
                super(view);

//                amt = view.findViewById(R.id.balanceAmt);
                requestAmt = view.findViewById(R.id.requestAmt);
//                transferAmt = view.findViewById(R.id.transferAmt);

                btnCancel = view.findViewById(R.id.btnCancel);
                lyt_parent = view.findViewById(R.id.lyt_parent);


            }

        }

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
                        GetHomeWrk();
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