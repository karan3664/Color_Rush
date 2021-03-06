package app.bizita.colorrush.ui.ui.slideshow;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.karan_brahmaxatriya.kbnetwork.KBNetworkCheck;
import com.karan_brahmaxatriya.kbnetwork.OnChangeConnectivityListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONObject;

import app.bizita.colorrush.R;
import app.bizita.colorrush.api.BuildConstants;
import app.bizita.colorrush.api.RetrofitHelper;
import app.bizita.colorrush.model.dashboard.DashboardModel;
import app.bizita.colorrush.model.login.LoginModel;
import app.bizita.colorrush.model.profile.ProfileModel;
import app.bizita.colorrush.ui.DashboardActivity;
import app.bizita.colorrush.ui.ui.cancel_withdrawalamt.CancelWithdrawalAmtFragment;
import app.bizita.colorrush.ui.ui.gallery.GalleryFragment;
import app.bizita.colorrush.ui.ui.profile.AboutAppActivity;
import app.bizita.colorrush.ui.ui.profile.AccountDetailsActivity;
import app.bizita.colorrush.ui.ui.profile.FriendsActivity;
import app.bizita.colorrush.ui.ui.profile.NotificationsActivity;
import app.bizita.colorrush.ui.ui.profile.PasswordActivity;
import app.bizita.colorrush.ui.ui.profile.PersonalDetailsActivity;
import app.bizita.colorrush.utils.BaseClass;
import app.bizita.colorrush.utils.PrefUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SlideshowFragment extends Fragment implements View.OnClickListener {

    CircularImageView civ_ProfileImage;
    TextView tv_Username, tvCurrentBalance, tvReceived, tvSent;
    LoginModel loginModel;
    String token;
    private ProgressBar progress_bar;
    LinearLayout lyt_parent, lyt_PersonalDetails, lyt_AccountDetails, lyt_FriendsDetails,
            lyt_PasswordDetails, lyt_NotificationsDetails, lyt_AboutAppDetails;
    //    private View parent_view;
    SweetAlertDialog sweetAlertDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        loginModel = PrefUtils.getUser(getContext());
        token = loginModel.getData().getToken();

        progress_bar = (ProgressBar) root.findViewById(R.id.progress_bar);
        lyt_parent = root.findViewById(R.id.lyt_parent);
        lyt_PersonalDetails = root.findViewById(R.id.lyt_PersonalDetails);
        lyt_AccountDetails = root.findViewById(R.id.lyt_AccountDetails);
        lyt_FriendsDetails = root.findViewById(R.id.lyt_FriendsDetails);
        lyt_PasswordDetails = root.findViewById(R.id.lyt_PasswordDetails);
        lyt_NotificationsDetails = root.findViewById(R.id.lyt_NotificationsDetails);
        lyt_AboutAppDetails = root.findViewById(R.id.lyt_AboutAppDetails);
        civ_ProfileImage = root.findViewById(R.id.civ_ProfileImage);
        tv_Username = root.findViewById(R.id.tv_Username);
        tvCurrentBalance = root.findViewById(R.id.tvCurrentBalance);
        tvReceived = root.findViewById(R.id.tvReceived);
        tvSent = root.findViewById(R.id.tvSent);
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
                    GetProfile();
//                    finish();
//                    startActivity(getIntent());
                }
            }
        });
        lyt_PersonalDetails.setOnClickListener(this);
        lyt_AccountDetails.setOnClickListener(this);
        lyt_FriendsDetails.setOnClickListener(this);
        lyt_PasswordDetails.setOnClickListener(this);
        lyt_NotificationsDetails.setOnClickListener(this);
        lyt_AboutAppDetails.setOnClickListener(this);
        lyt_parent.setVisibility(View.GONE);
        GetProfile();
        return root;
    }

    public void GetProfile() {
//        if (!isNetworkAvailable()) {
//            return;
//        }
        progress_bar.setVisibility(View.VISIBLE);
        Call<ProfileModel> loginModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).ProfileModel("Bearer " + token);
        loginModelCall.enqueue(new Callback<ProfileModel>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ProfileModel> call, @NonNull Response<ProfileModel> response) {
                ProfileModel object = response.body();


                if (response.isSuccessful()) {
                    Log.e("TAG", "Login_Response : " + new Gson().toJson(response.body()));
                    lyt_parent.setVisibility(View.VISIBLE);
                    progress_bar.setVisibility(View.GONE);

                    tv_Username.setText(object.getData().getUser().getName() + "");
                    tvCurrentBalance.setText(object.getData().getUserBalance() + "");
                    tvReceived.setText(object.getData().getMonthBuyed() + " ₹");
                    tvSent.setText(object.getData().getMonthSpent() + " ₹");
                    if (object.getData().getUser().getProfileFile() != null) {
                        Glide.with(getContext())
                                .load(BuildConstants.Main_Image + object.getData().getUser().getProfileFile().toString().replace("public", "storage") + "")
                                .placeholder(R.drawable.ic_user)
//                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                .into(civ_ProfileImage);
                        Log.e("IMAGE", BuildConstants.Main_Image + object.getData().getUser().getProfileFile().toString().replace("public", "storage") + "");

                    }


                } else {
                    progress_bar.setVisibility(View.GONE);
                    lyt_parent.setVisibility(View.GONE);
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
            public void onFailure(@NonNull Call<ProfileModel> call, @NonNull Throwable t) {
                progress_bar.setVisibility(View.GONE);
//                fab.setAlpha(1f);
                t.printStackTrace();

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
                Log.e("Login_Response", t.getMessage() + "");
            }
        });
    }

    public boolean isNetworkAvailable() {
        return BaseClass.isNetworkAvailable(getContext());
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
                        sweetAlertDialog.dismiss();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lyt_PersonalDetails:
                Intent lp = new Intent(getContext(), PersonalDetailsActivity.class);
                startActivity(lp);
                break;
            case R.id.lyt_AccountDetails:
                Intent la = new Intent(getContext(), AccountDetailsActivity.class);
                startActivity(la);
                break;
            case R.id.lyt_FriendsDetails:
                final String appPackageName = getActivity().getPackageName();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out the App at: https://play.google.com/store/apps/details?id=" + appPackageName);
                sendIntent.setType("text/plain");
                getActivity().startActivity(sendIntent);
                break;
            case R.id.lyt_PasswordDetails:
                Intent lpa = new Intent(getContext(), PasswordActivity.class);
                startActivity(lpa);
                break;
            case R.id.lyt_NotificationsDetails:
                Intent ln = new Intent(getContext(), NotificationsActivity.class);
                startActivity(ln);
                break;
            case R.id.lyt_AboutAppDetails:
                Intent lab = new Intent(getContext(), AboutAppActivity.class);
                startActivity(lab);
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        GetProfile();
    }

    @Override
    public void onStart() {
        super.onStart();
        GetProfile();
    }
}