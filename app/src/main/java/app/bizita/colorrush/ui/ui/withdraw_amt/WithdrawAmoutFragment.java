package app.bizita.colorrush.ui.ui.withdraw_amt;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.karan_brahmaxatriya.kbnetwork.KBNetworkCheck;
import com.karan_brahmaxatriya.kbnetwork.OnChangeConnectivityListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import app.bizita.colorrush.R;
import app.bizita.colorrush.api.RetrofitHelper;
import app.bizita.colorrush.model.bank.all_bank_list.AllBankListsModel;
import app.bizita.colorrush.model.bank.all_bank_list.BankList;
import app.bizita.colorrush.model.login.LoginModel;
import app.bizita.colorrush.model.profile.ProfileModel;
import app.bizita.colorrush.model.withdrawal.CurrentWalletAndWithdrawalModel;
import app.bizita.colorrush.model.withdrawal.WithdrawalRequestModel;
import app.bizita.colorrush.ui.ui.cancel_withdrawalamt.CancelWithdrawalAmtFragment;
import app.bizita.colorrush.ui.ui.profile.AccountDetailsActivity;
import app.bizita.colorrush.ui.ui.profile.AddNewBankAccountActivity;
import app.bizita.colorrush.ui.ui.profile.EditBankDetailsActivity;
import app.bizita.colorrush.utils.BaseClass;
import app.bizita.colorrush.utils.PrefUtils;
import app.bizita.colorrush.utils.ViewDialog;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WithdrawAmoutFragment extends Fragment {
    LoginModel loginModel;
    String token;
    private ProgressBar progress_bar, progress_bar1;
    //    private View parent_view;
    private FloatingActionButton fab;
    NestedScrollView nested_content;
    EditText et_amt;
    TextView tvBalance, tvWithdrawal;
    int orginal = 0;
    String bank_id = "";
    RecyclerView rvAllBankList;
    private CustomAdapter customAdapter;
    ArrayList<BankList> bankListArrayList = new ArrayList<>();
    protected ViewDialog viewDialog;
    Button ed_50, ed_100, ed_200;
    Button btnAddNew;
    SweetAlertDialog sweetAlertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_withdraw_amout, container, false);
        loginModel = PrefUtils.getUser(getContext());
        token = loginModel.getData().getToken();
        viewDialog = new ViewDialog(getContext());
        viewDialog.setCancelable(false);

//        parent_view = getActivity().findViewById(android.R.id.content);
        progress_bar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        progress_bar1 = (ProgressBar) rootView.findViewById(R.id.progress_bar1);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

        et_amt = rootView.findViewById(R.id.edamt);
        tvBalance = rootView.findViewById(R.id.tvBalance);
        tvWithdrawal = rootView.findViewById(R.id.tvWithdrawal);
        ed_50 = rootView.findViewById(R.id.ed_50);
        ed_100 = rootView.findViewById(R.id.ed_100);
        ed_200 = rootView.findViewById(R.id.ed_200);
        btnAddNew = rootView.findViewById(R.id.btnAddNew);
        TextView txt = rootView.findViewById(R.id.marquesText);
        txt.setSelected(true);
        nested_content = rootView.findViewById(R.id.nested_content);
        nested_content.setVisibility(View.GONE);
        rvAllBankList = rootView.findViewById(R.id.rvBankList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvAllBankList.setLayoutManager(layoutManager);
        rvAllBankList.setHasFixedSize(true);
        rvAllBankList.setNestedScrollingEnabled(false);

        GetHomeWrk();
        GetProfile();
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

                    GetHomeWrk();
                    GetProfile();
//                    finish();
//                    startActivity(getIntent());
                }
            }
        });
        ed_50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_amt.setText("1000");
            }
        });
        ed_200.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_amt.setText("5000");
            }
        });
        ed_100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_amt.setText("2000");
            }
        });
        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AddNewBankAccountActivity.class);
                startActivityForResult(i, 1);
            }
        });
        return rootView;
    }

    public boolean isNetworkAvailable() {
        return BaseClass.isNetworkAvailable(getContext());
    }

    protected void hideProgressDialog() {
        viewDialog.dismiss();
    }

    protected void showProgressDialog() {
        viewDialog.show();
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
        if (requestCode == 1) {
            GetHomeWrk();
            GetProfile();
        }
    }

    public void GetProfile() {
//        if (!isNetworkAvailable()) {
//            return;
//        }
        progress_bar1.setVisibility(View.VISIBLE);
        Call<CurrentWalletAndWithdrawalModel> loginModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).CurrentWalletAndWithdrawalModel("Bearer " + token);
        loginModelCall.enqueue(new Callback<CurrentWalletAndWithdrawalModel>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<CurrentWalletAndWithdrawalModel> call, @NonNull Response<CurrentWalletAndWithdrawalModel> response) {
                CurrentWalletAndWithdrawalModel object = response.body();


                if (response.isSuccessful()) {
                    Log.e("TAG", "Login_Response : " + new Gson().toJson(response.body()));

                    progress_bar1.setVisibility(View.GONE);
                    nested_content.setVisibility(View.VISIBLE);
                    tvWithdrawal.setText(object.getData().getWithdrawalPoint() + "");
                    tvBalance.setText(object.getData().getCurrent() + "");

                } else {
                    progress_bar1.setVisibility(View.GONE);
                    nested_content.setVisibility(View.GONE);
//                    fab.setAlpha(1f);
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE);
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
            public void onFailure(@NonNull Call<CurrentWalletAndWithdrawalModel> call, @NonNull Throwable t) {
                progress_bar1.setVisibility(View.GONE);
                nested_content.setVisibility(View.GONE);
//                fab.setAlpha(1f);
                t.printStackTrace();
                Log.e("Login_Response", t.getMessage() + "");
            }
        });
    }

    public void GetHomeWrk() {
//        if (!isNetworkAvailable()) {
//            return;
//        }
        HashMap<String, String> hashMap = new HashMap<>();
        showProgressDialog();
        Call<AllBankListsModel> schoolModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).AllBankListsModel("Bearer " + token, hashMap);
        schoolModelCall.enqueue(new Callback<AllBankListsModel>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<AllBankListsModel> call, @NonNull Response<AllBankListsModel> response) {
                AllBankListsModel object = response.body();
                hideProgressDialog();

                if (response.isSuccessful()) {
                    Log.e("TAG", "School_Response : " + new Gson().toJson(response.body()));
                    if (!object.getData().getBankList().isEmpty()) {
                        bankListArrayList = object.getData().getBankList();
                        customAdapter = new CustomAdapter(bankListArrayList);
                        rvAllBankList.setAdapter(customAdapter);
                    } else {

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
                        SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE);
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
            public void onFailure(@NonNull Call<AllBankListsModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                t.printStackTrace();
                Log.e("School_Response", t.getMessage() + "");
            }
        });
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

        private ArrayList<BankList> moviesList;
        private int row_index = -1;

        public CustomAdapter(ArrayList<BankList> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_bank_list_for_withdrawal, parent, false);

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


            final BankList datum = moviesList.get(position);
            holder.radioBank.setChecked(row_index == position);
            String lastFourDigits = ""; //substring containing last 4 characters.
            if (datum.getBankAccountNo().length() > 4) {
                lastFourDigits = datum.getBankAccountNo().substring(datum.getBankAccountNo().length() - 4);
            } else {
                lastFourDigits = datum.getBankAccountNo();
            }
            holder.radioBank.setText(datum.getBankName() + " - " + lastFourDigits);

            if (datum.getPrimary() == 1) {
//                holder.radioGroupBank.clearCheck();
//                holder.radioBank.setChecked(true);
                bank_id = datum.getId() + "";
            } else {

//                holder.radioBank.setChecked(false);
            }

//            Toast.makeText(MainActivity.this, radioSexButton.getText(), Toast.LENGTH_SHORT).show();
            holder.radioBank.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
//                        holder.radioBank.setChecked(false);

                        bank_id = datum.getId() + "";
                    } else {
//                        holder.radioBank.setChecked(true);
                    }
                }
            });

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final String amt = et_amt.getText().toString().trim();


                    if (amt.isEmpty()) {
                        SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE);
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
                        et_amt.requestFocus();
                        return;
                    }
                    if (bank_id.matches("")) {

                        SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                        sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
                        sweetAlertDialoga.setTitleText("Select Bank");
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
                        return;
                    } else /*if (amt.length() >= orginal) {
                        SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                        sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
                        sweetAlertDialoga.setTitleText("Withdrawal Point Not More Than Available Points");
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
                    }*/ {
                        progress_bar.setVisibility(View.VISIBLE);
//                    fab.setAlpha(0f);
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("requested_amount", amt + "");
                        hashMap.put("bank_id", bank_id + "");

                        Call<WithdrawalRequestModel> loginModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).money_withdrawal("Bearer " + token, hashMap);
                        loginModelCall.enqueue(new Callback<WithdrawalRequestModel>() {

                            @Override
                            public void onResponse(@NonNull Call<WithdrawalRequestModel> call, @NonNull Response<WithdrawalRequestModel> response) {
                                WithdrawalRequestModel object = response.body();
                                progress_bar.setVisibility(View.GONE);
                                if (response.code() == 401) {
                                    SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                                    sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
                                    sweetAlertDialoga.setTitleText("Withdrawal request failed due to insufficient amount in wallet !!");
                                    sweetAlertDialoga.setConfirmText("Okay")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    // reuse previous dialog instance
                                                    sDialog.dismissWithAnimation();
                                                    et_amt.setText("");
                                                    holder.radioBank.setChecked(false);

                                                    GetProfile();
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
                                                et_amt.setText("");
                                                holder.radioBank.setChecked(false);

                                                GetProfile();
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
                                } else if (response.isSuccessful()) {
                                    SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                                    sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
                                    sweetAlertDialoga.setTitleText(object.getMessage() + "");
                                    sweetAlertDialoga.setConfirmText("Okay")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    // reuse previous dialog instance
                                                    sDialog.dismissWithAnimation();
                                                    et_amt.setText("");
                                                    holder.radioBank.setChecked(false);
                                                    replaceFragment(new CancelWithdrawalAmtFragment());
                                                    GetProfile();
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
                                                et_amt.setText("");
                                                holder.radioBank.setChecked(false);
                                                replaceFragment(new CancelWithdrawalAmtFragment());
                                                GetProfile();
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


                                } else {
                                    progress_bar.setVisibility(View.GONE);
//                                fab.setAlpha(1f);
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE);
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
                            public void onFailure(@NonNull Call<WithdrawalRequestModel> call, @NonNull Throwable t) {
                                progress_bar.setVisibility(View.GONE);

                                t.printStackTrace();
                                Log.e("Login_Response", t.getMessage() + "");
                            }
                        });
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {


            LinearLayout lyt_parent;
            RadioButton radioBank;
            RadioGroup radioGroupBank;


            public MyViewHolder(View view) {
                super(view);

                radioBank = view.findViewById(R.id.radioBank);
//                radioGroupBank = view.findViewById(R.id.radioGroupBank);
//                int selectedId = radioGroupBank.getCheckedRadioButtonId();
//                radioBank =  view.findViewById(selectedId);
                radioBank.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        row_index = getAdapterPosition();
                        notifyDataSetChanged();


                    }
                });
            }

        }

    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}