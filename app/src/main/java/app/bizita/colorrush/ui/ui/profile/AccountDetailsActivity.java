package app.bizita.colorrush.ui.ui.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.karan_brahmaxatriya.kbnetwork.KBNetworkCheck;
import com.karan_brahmaxatriya.kbnetwork.OnChangeConnectivityListener;

import org.json.JSONObject;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import app.bizita.colorrush.R;
import app.bizita.colorrush.api.BuildConstants;
import app.bizita.colorrush.api.RetrofitHelper;
import app.bizita.colorrush.model.bank.all_bank_list.AllBankListsModel;
import app.bizita.colorrush.model.bank.all_bank_list.BankList;
import app.bizita.colorrush.model.login.LoginModel;
import app.bizita.colorrush.model.profile.ProfileModel;
import app.bizita.colorrush.model.update_profile.UpdateProfileModel;
import app.bizita.colorrush.ui.DashboardActivity;
import app.bizita.colorrush.ui.ui.cancel_withdrawalamt.CancelWithdrawalAmtFragment;
import app.bizita.colorrush.utils.BaseClass;
import app.bizita.colorrush.utils.PrefUtils;
import app.bizita.colorrush.utils.ViewDialog;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountDetailsActivity extends AppCompatActivity {
    LoginModel loginModel;
    String token;
    private ProgressBar progress_bar, progress_bar1;
//    private View parent_view;
    RecyclerView rvAllBankList;
    private CustomAdapter customAdapter;
    ArrayList<BankList> bankListArrayList = new ArrayList<>();
    Button btnAddNew;
    protected ViewDialog viewDialog;
    SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_account_details);
        loginModel = PrefUtils.getUser(this);
        token = loginModel.getData().getToken();
        viewDialog = new ViewDialog(this);
        viewDialog.setCancelable(false);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("All Bank Account");
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
        progress_bar1 = (ProgressBar) findViewById(R.id.progress_bar1);
        btnAddNew = findViewById(R.id.btnAddNew);


        rvAllBankList = findViewById(R.id.rvAllBankList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvAllBankList.setLayoutManager(layoutManager);
        rvAllBankList.setHasFixedSize(true);
        rvAllBankList.setNestedScrollingEnabled(false);

        GetHomeWrk();
        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AccountDetailsActivity.this, AddNewBankAccountActivity.class);
                startActivity(i);
            }
        });
        sweetAlertDialog = new SweetAlertDialog(AccountDetailsActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);

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
                }
            }
        });
    }

    public boolean isNetworkAvailable() {
        return BaseClass.isNetworkAvailable(AccountDetailsActivity.this);
    }

    protected void hideProgressDialog() {
        viewDialog.dismiss();
    }

    protected void showProgressDialog() {
        viewDialog.show();
    }


    public void GetHomeWrk() {

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
//                        Snackbar.make(parent_view, "No Data Available", Snackbar.LENGTH_SHORT).show();
                        SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(AccountDetailsActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
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
//                        Snackbar.make(parent_view, jObjError.getJSONArray("error") + "", Snackbar.LENGTH_SHORT).show();
                        SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(AccountDetailsActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
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
            public void onFailure(@NonNull Call<AllBankListsModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                t.printStackTrace();
                Log.e("School_Response", t.getMessage() + "");
            }
        });
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

        private ArrayList<BankList> moviesList;

        public CustomAdapter(ArrayList<BankList> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(AccountDetailsActivity.this)
                    .inflate(R.layout.item_all_bank_list, parent, false);

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
            if (datum.getPrimary() == 1) {
                holder.primaryAccount.setVisibility(View.VISIBLE);
            }

            holder.bankName.setText(datum.getBankName() + "");
            holder.accountHolderName.setText(datum.getAccountHolderName() + "");
            holder.editBankDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(AccountDetailsActivity.this, EditBankDetailsActivity.class);
                    i.putExtra("bank_id", datum.getId() + "");
                    startActivity(i);
                }
            });


        }

        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {


            LinearLayout lyt_parent;
            TextView primaryAccount, bankName, accountHolderName;
            ImageView editBankDetails;

            public MyViewHolder(View view) {
                super(view);

                primaryAccount = view.findViewById(R.id.primaryAccount);
                bankName = view.findViewById(R.id.bankName);
                accountHolderName = view.findViewById(R.id.accountHolderName);
                lyt_parent = view.findViewById(R.id.lyt_parent);
                editBankDetails = view.findViewById(R.id.editBankDetails);

            }

        }

    }

    @Override
    public void onResume() {
        super.onResume();

        GetHomeWrk();
    }

    @Override
    public void onStart() {
        super.onStart();
        GetHomeWrk();
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