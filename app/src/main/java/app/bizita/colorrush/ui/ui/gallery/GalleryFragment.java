package app.bizita.colorrush.ui.ui.gallery;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.karan_brahmaxatriya.kbnetwork.KBNetworkCheck;
import com.karan_brahmaxatriya.kbnetwork.OnChangeConnectivityListener;

import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import app.bizita.colorrush.R;
import app.bizita.colorrush.api.RetrofitHelper;
import app.bizita.colorrush.model.already_bid.AlreadyBidModel;
import app.bizita.colorrush.model.already_bid.Bid;
import app.bizita.colorrush.model.dashboard.DashboardModel;
import app.bizita.colorrush.model.dashboard.GameConfig;
import app.bizita.colorrush.model.game_start.GameStartModel;
import app.bizita.colorrush.model.login.LoginModel;
import app.bizita.colorrush.model.particular_bid.CancelBidsModel;
import app.bizita.colorrush.model.particular_bid.ParticularBidsModel;
import app.bizita.colorrush.model.rebid.ReBidModel;
import app.bizita.colorrush.model.results.BidsArray;
import app.bizita.colorrush.model.results.ResultsModel;
import app.bizita.colorrush.ui.DashboardActivity;
import app.bizita.colorrush.ui.LoginActivity;
import app.bizita.colorrush.ui.ui.BuyMoneyActivity;
import app.bizita.colorrush.ui.ui.cancel_withdrawalamt.CancelWithdrawalAmtFragment;
import app.bizita.colorrush.utils.BaseClass;
import app.bizita.colorrush.utils.PrefUtils;
import app.bizita.colorrush.utils.SpacingItemDecoration;
import app.bizita.colorrush.utils.Tools;
import app.bizita.colorrush.utils.ViewDialog;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GalleryFragment extends Fragment {
    Activity activity;
    Dialog dialogCancelBid;
    NestedScrollView nested_content;
    RecyclerView rvColor;
    protected ViewDialog viewDialog;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;
    private CustomAdapter customAdapter;
    private ProgressBar progress_bar;
    //    private View parent_view;
    LoginModel loginModel;
    String token;
    TextView timer, tvBalance, marques;
    ArrayList<GameConfig> gameConfigArrayList = new ArrayList<>();
    DashboardModel dashboardModel = new DashboardModel();
    ArrayList<Bid> bidArrayList = new ArrayList<>();
    public int counter;
    @TargetApi(Build.VERSION_CODES.O)
    CountDownTimer countDownTimer;
    CountDownTimer countDownTimerNewGmae;
    CountDownTimer countDownTimerResult;
    ArrayList<app.bizita.colorrush.model.particular_bid.Bid> particular_bidArrayList = new ArrayList<>();
    Handler handler = new Handler();
    Runnable runnable;
    ArrayList<BidsArray> bidsArrayArrayList = new ArrayList<>();
    String returning_user, game_id = "", dateStop = "";
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    EditText et_hours;
    Handler mHandler = new Handler();
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    SharedPreferences mpref;
    SharedPreferences.Editor mEditor;
    String bid_1 = "", bid_2 = "", bid_3 = "", bid_4 = "", bid_5 = "", bid_6 = "";
    SchoolCustomAdapter schoolCustomAdapter;
    ResultsCustomAdapter resultsCustomAdapter;
    int posi = 0;
    Date d1 = new Date();
    Date d2 = new Date();
    Date d3 = new Date();
    Date d4 = new Date();
    private boolean isShown = false;
    private boolean isShownS = false;
    Dialog firstDialog;

    public static final String TRACER = "tracer";
    SweetAlertDialog sweetAlertDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        loginModel = PrefUtils.getUser(getContext());
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //Then we will get the GoogleSignInClient object from GoogleSignIn class
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        // Passing each menu ID as a set of Ids because each
        token = loginModel.getData().getToken();
        rvColor = root.findViewById(R.id.rvColor);
        viewDialog = new ViewDialog(activity);
        viewDialog.setCancelable(false);
//        parent_view = getActivity().findViewById(android.R.id.content);
        progress_bar = (ProgressBar) root.findViewById(R.id.progress_bar);
        rvColor.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvColor.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(getActivity(), 8), true));
        rvColor.setHasFixedSize(true);
        rvColor.setNestedScrollingEnabled(false);
        mpref = PreferenceManager.getDefaultSharedPreferences(getContext());
        mEditor = mpref.edit();
        timer = root.findViewById(R.id.timer);
        tvBalance = root.findViewById(R.id.tvBalance);
        tvBalance.setSelected(true);
        marques = root.findViewById(R.id.marques);
        marques.setSelected(true);
        nested_content = root.findViewById(R.id.nested_content);
        bottom_sheet = root.findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);
        nested_content.setVisibility(View.GONE);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (countDownTimerResult != null) {
            countDownTimerResult.cancel();
        }
        if (countDownTimerNewGmae != null) {
            countDownTimerNewGmae.cancel();
        }


        returning_user = "";
        sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE);
        KBNetworkCheck.setConnectivityListener(new OnChangeConnectivityListener() {
            @Override
            public void onChanged(boolean status) {
                Log.d("TAG", "onChanged: main = " + status);


//                            Toast.makeText(SplashActivity.this, "" + status, Toast.LENGTH_SHORT).show();
                if (status == true) {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    if (countDownTimerResult != null) {
                        countDownTimerResult.cancel();
                    }
                    GetColor();
                    sweetAlertDialog.dismiss();
                } else {
                    nested_content.setVisibility(View.GONE);
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    if (countDownTimerResult != null) {
                        countDownTimerResult.cancel();
                    }
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
//                    finish();
//                    startActivity(getIntent());
                }
            }
        });
        GetColor();
        Log.e("Start", "timer_create");
        return root;
    }

    @Override
    public void onStart() {
        Log.e("Start", "timer_start");
      /*  if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (countDownTimerNewGmae != null) {
            countDownTimerNewGmae.cancel();
        }
//*/
        super.onStart();

    }

    @Override
    public void onPause() {
        Log.e("Start", "timer_pause");


        super.onPause();
    }

    @Override
    public void onResume() {
        Log.e("Start", "timer_resume");
//        if (countDownTimerNewGmae != null) {
//            countDownTimerNewGmae.cancel();
//        }

        super.onResume();
    }


    @Override
    public void onStop() {
        Log.e("Start", "timer_stop");
//        if (countDownTimer != null) {
//            countDownTimer.cancel();
//        }
//        if (countDownTimerNewGmae != null) {
//            countDownTimerNewGmae.cancel();
//        }

        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.e("Start", "timer_destroy");
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (countDownTimerNewGmae != null) {
            countDownTimerNewGmae.cancel();
        }
        super.onDestroy();
    }


    public void GetColor() {
//        gameConfigArrayList.clear();
//        if (!isNetworkAvailable()) {
//            return;
//        }


        progress_bar.setVisibility(View.VISIBLE);
        Call<DashboardModel> loginModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).DashboardModel("Bearer " + token);
        loginModelCall.enqueue(new Callback<DashboardModel>() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<DashboardModel> call, @NonNull Response<DashboardModel> response) {
                DashboardModel object = response.body();


                if (response.isSuccessful()) {
                    nested_content.setVisibility(View.VISIBLE);

                    if (object.getData() != null) {
//                        if (countDownTimer != null) {
//                            countDownTimer.cancel();
//                        }
                        if (countDownTimerNewGmae != null) {
                            countDownTimerNewGmae.cancel();
                        }
                        returning_user = "";
                        returning_user = object.getData().getReturningUser() + "";
                        Animation anim = new AlphaAnimation(0.0f, 1.0f);
                        anim.setDuration(50); //You can manage the blinking time with this parameter
                        anim.setStartOffset(20);
                        anim.setRepeatMode(Animation.REVERSE);
                        anim.setRepeatCount(Animation.INFINITE);
                        if (returning_user.matches("0")) {
                            marques.setVisibility(View.GONE);
                        } else {
                            marques.setVisibility(View.VISIBLE);
//                            marques.startAnimation(anim);
                        }
                        Log.e("returning_user", returning_user + "");
                        progress_bar.setVisibility(View.GONE);

                        gameConfigArrayList = object.getData().getGameConfig();

                        game_id = object.getData().getFutureGames().get(0).getGameId() + "";
                        Date d = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String currentDateTimeString = sdf.format(d);

                        String dateStart = currentDateTimeString;
                        String dateStop = object.getData().getFutureGames().get(0).getEndTime() + "";
                        String pres = object.getData().getPresentGameStart() + "";
                        String futgameSt = object.getData().getFutureGames().get(1).getStartTime() + "";

                        //HH converts hour in 24 hours format (0-23), day calculation
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


                        try {
                            d1 = format.parse(dateStart);
                            d2 = format.parse(dateStop);
                            d3 = format.parse(pres);
                            d4 = format.parse(futgameSt);
                            assert d1 != null;
                            assert d3 != null;
                            if (d3.getTime() <= d1.getTime()) {
//                                Toast.makeText(activity, "Old Game" + "-- " + d3.getTime(), Toast.LENGTH_SHORT).show();
                                //in milliseconds
                                if (!gameConfigArrayList.isEmpty()) {
                                    customAdapter = new CustomAdapter(gameConfigArrayList);
                                    rvColor.setAdapter(customAdapter);

                                }

                                assert d2 != null;
                                long diff = d2.getTime() - d1.getTime();

                                long diffSeconds = diff / 1000 % 60;
                                long diffMinutes = diff / (60 * 1000) % 60;
                                long diffHours = diff / (60 * 60 * 1000) % 24;
//                            long diffDays = diff / (24 * 60 * 60 * 1000);

//                            System.out.print(diffDays + " days, ");
                                System.out.print(diffHours + " hours, ");
                                System.out.print(diffMinutes + " minutes, ");
                                System.out.print(diffSeconds + " seconds.");
                                Log.e("Timer", diffHours + "--" + diffMinutes + "--" + diffSeconds + "");
//                                Log.e("returning_user", returning_user + "");

                                tvBalance.setText(object.getData().getCurrentBalance() + "");
                                if (object.getData().getCurrentBalance().matches("0")) {
                                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(activity, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                                    sweetAlertDialog.setTitleText("Please Add Points In Wallet");
                                    sweetAlertDialog.setConfirmText("Okay")
                                            .setCustomImage(R.drawable.bitcoint)
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    // reuse previous dialog instance
                                                    sDialog.dismissWithAnimation();
                                                    Intent i = new Intent(getContext(), BuyMoneyActivity.class);
                                                    getActivity().startActivity(i);
                                                    getActivity().finish();

                                                }
                                            })
                                            .show();
                                }

                                countDownTimer = new CountDownTimer(diff, 1000) {
                                    public void onTick(long millisUntilFinished) {
//                                    ring.start();
                                        timer.setText("" + String.format("%d:%02d",
                                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                                        //here you can have your logic to smTextField.setText("seconds remaining: " + millisUntilFinished / 1000);et text to edittext

                                    }

                                    public void onFinish() {
                                        if (countDownTimer != null) {
//                                            countDownTimer.cancel();

                                        }
                                        try {
                                            dialogCancelBid.dismiss();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        bidsArrayArrayList.clear();
                                        Result(game_id);

//                                        gameConfigArrayList.clear();


                                    }

                                };
                                countDownTimer.start();


                            } else {
//                                Toast.makeText(activity, "New Game" + "-- " + d1.getTime(), Toast.LENGTH_SHORT).show();
                                //in milliseconds
                                long diffs = d3.getTime() - d1.getTime();

                                long diffSeconds = diffs / 1000 % 60;
                                long diffMinutes = diffs / (60 * 1000) % 60;
                                long diffHours = diffs / (60 * 60 * 1000) % 24;
//                            long diffDays = diff / (24 * 60 * 60 * 1000);

                                final Dialog newDialog1g = new Dialog(activity);
                                newDialog1g.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                newDialog1g.setContentView(R.layout.floating_tutorial);
                                newDialog1g.setCancelable(false);
                                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                lp.copyFrom(newDialog1g.getWindow().getAttributes());
                                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                lp.height = WindowManager.LayoutParams.MATCH_PARENT;


                                newDialog1g.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                TextView timesa = newDialog1g.findViewById(R.id.time);
                                TextView resultin = newDialog1g.findViewById(R.id.resultin);
                                resultin.setText("New Game \nStarts in ");

                                countDownTimerNewGmae = new CountDownTimer(diffs, 1000) { // adjust the milli seconds here

                                    public void onTick(long millisUntilFinished) {
                                        timesa.setText("" + String.format("%d:%02d",
                                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) + " sec ");
                                    }

                                    public void onFinish() {
                                        if (countDownTimerNewGmae != null) {
                                            countDownTimerNewGmae.cancel();

                                        }
                                        newDialog1g.dismiss();
//                                        //gameConfigArrayList.clear();
                                        GetColor();
//                                    GetColorTime();


                                    }
                                };
                                countDownTimerNewGmae.start();
                                try {
                                    newDialog1g.show();
                                    newDialog1g.getWindow().setAttributes(lp);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else {
                        SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
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
                                                    PrefUtils.clearCurrentUser(getContext());
                                                    Intent i = new Intent(getContext(), LoginActivity.class);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(i);
                                                    getActivity().finish();

                                                }
                                            }).executeAsync();
                                        } else if (mAuth.getCurrentUser() != null) {
                                            mAuth.signOut();
                                            mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    PrefUtils.clearCurrentUser(getContext());
                                                    Intent i = new Intent(getContext(), LoginActivity.class);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(i);
                                                    getActivity().finish();
                                                }
                                            });
                                        } else {
                                            PrefUtils.clearCurrentUser(getContext());
                                            Intent i = new Intent(getContext(), LoginActivity.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(i);
                                            getActivity().finish();
                                        }

                                        sDialog.dismissWithAnimation();
                                        sDialog.setCanceledOnTouchOutside(false);

                                    }
                                })
                                .show();

                    }


//

                } else {
                    progress_bar.setVisibility(View.GONE);
//                    fab.setAlpha(1f);
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
//                        Snackbar.make(parent_view, jObjError.getJSONArray("errors").toString().replace("[", "").replace("]", "") + "", Snackbar.LENGTH_SHORT).show();
//                        Snackbar.make(parent_view, jObjError.getString("message") + "", Snackbar.LENGTH_SHORT).show();

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
            public void onFailure(@NonNull Call<DashboardModel> call, @NonNull Throwable t) {
                progress_bar.setVisibility(View.GONE);
//                fab.setAlpha(1f);
                t.printStackTrace();
                Log.e("Login_Response", t.getMessage() + "");
            }
        });
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
                        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(activity, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    public class SchoolCustomAdapter extends RecyclerView.Adapter<SchoolCustomAdapter.MyViewHolder> {

        private ArrayList<app.bizita.colorrush.model.particular_bid.Bid> moviesList;


        public SchoolCustomAdapter(ArrayList<app.bizita.colorrush.model.particular_bid.Bid> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getActivity())
                    .inflate(R.layout.item_school, parent, false);

            return new MyViewHolder(itemView);
        }


        @SuppressLint("SetTextI18n")
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {


            final app.bizita.colorrush.model.particular_bid.Bid datum = moviesList.get(position);
            holder.tvSchool.setText(" " + datum.getAmount() + "");
            holder.ivCloseBid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, String> hashMasp = new HashMap<>();
                    hashMasp.put("bid_id", datum.getId() + "");

                    showProgressDialog();
                    Call<CancelBidsModel> schoolModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).CancelBidsModel("Bearer " + token, hashMasp);
                    schoolModelCall.enqueue(new Callback<CancelBidsModel>() {

                        @Override
                        public void onResponse(@NonNull Call<CancelBidsModel> call, @NonNull Response<CancelBidsModel> response) {
                            CancelBidsModel object = response.body();
                            hideProgressDialog();
                            if (response.isSuccessful()) {
                                Log.e("TAG", "School_Response : " + new Gson().toJson(response.body()));

//                                Snackbar.make(parent_view, object.getMessage() + "", Snackbar.LENGTH_SHORT).show();
                                SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                                sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
                                sweetAlertDialoga.setTitleText(object.getMessage() + "");
                                sweetAlertDialoga.setConfirmText("Okay")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                // reuse previous dialog instance
                                                sDialog.dismissWithAnimation();

                                                dialogCancelBid.dismiss();

                                                //gameConfigArrayList.clear();
                                                if (countDownTimer != null) {
                                                    countDownTimer.cancel();
                                                }
                                                GetColor();
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
                                            dialogCancelBid.dismiss();

                                            //gameConfigArrayList.clear();
                                            if (countDownTimer != null) {
                                                countDownTimer.cancel();
                                            }
                                            GetColor();
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


//                                customAdapter.notifyItemChanged(posi);
//                                GetColorBalance();
                            } else {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());


                                } catch (Exception e) {
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<CancelBidsModel> call, @NonNull Throwable t) {
                            hideProgressDialog();
                            t.printStackTrace();
                            Log.e("School_Response", t.getMessage() + "");
                        }
                    });
                }
            });

        }


        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {


            TextView tvSchool;
            ImageView ivCloseBid;

            public MyViewHolder(View view) {
                super(view);


                tvSchool = view.findViewById(R.id.tvSchool);
                ivCloseBid = view.findViewById(R.id.ivCloseBid);


            }

        }


    }

    public class ResultsCustomAdapter extends RecyclerView.Adapter<ResultsCustomAdapter.MyViewHolder> {

        private ArrayList<BidsArray> moviesList;


        public ResultsCustomAdapter(ArrayList<BidsArray> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(activity)
                    .inflate(R.layout.item_result_list, parent, false);

            return new MyViewHolder(itemView);
        }


        @SuppressLint("SetTextI18n")
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {


            final BidsArray datum = moviesList.get(position);
            holder.bidAmt.setText(" " + datum.getBidAmount());

            if (datum.getStarCount() == 0) {
                holder.star.setVisibility(View.GONE);
            } else {
                holder.star.setVisibility(View.VISIBLE);
                holder.star.setRating(datum.getStarCount());
            }
            if (datum.getCurrentStatus().matches("Win")) {
                holder.ivWin.setVisibility(View.GONE);
                holder.winAmt.setVisibility(View.VISIBLE);
                holder.ivLoss.setVisibility(View.VISIBLE);
                holder.lossAmt.setVisibility(View.GONE);
//                holder.ivLoss.setVisibility(View.GONE);
//                holder.lossAmt.setVisibility(View.GONE);
                holder.winAmt.setText(" " + datum.getAmount());
//                holder.ivLoss.setBackgroundResource(R.drawable.close);
            } else if (datum.getCurrentStatus().matches("Not_bid")) {
                holder.ivWin.setVisibility(View.VISIBLE);
                holder.ivLoss.setVisibility(View.VISIBLE);
                holder.lossAmt.setVisibility(View.GONE);
                holder.winAmt.setVisibility(View.GONE);
//                holder.ivWin.setBackgroundResource(R.drawable.close);
            } else {
                holder.ivWin.setVisibility(View.VISIBLE);
                holder.winAmt.setVisibility(View.GONE);
                holder.ivLoss.setVisibility(View.GONE);
                holder.lossAmt.setVisibility(View.VISIBLE);

//                holder.ivWin.setVisibility(View.GONE);
//                holder.winAmt.setVisibility(View.GONE);


                holder.lossAmt.setText(" " + datum.getAmount());
            }

//            holder.cBalance.setText(" " + datum.getCurrentBalance());
            holder.ivColor.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));

        }


        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {


            TextView bidAmt, winAmt, lossAmt, cBalance;
            ImageView ivColor, ivLoss, ivWin;
            RatingBar star;

            public MyViewHolder(View view) {
                super(view);


                bidAmt = view.findViewById(R.id.bidAmt);
                winAmt = view.findViewById(R.id.winAmt);
                ivLoss = view.findViewById(R.id.ivLoss);
                ivWin = view.findViewById(R.id.ivWin);
                ivColor = view.findViewById(R.id.ivColor);
                lossAmt = view.findViewById(R.id.lossAmt);
                star = view.findViewById(R.id.star);


            }

        }


    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

        private ArrayList<GameConfig> moviesList;

        public CustomAdapter(ArrayList<GameConfig> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getActivity())
                    .inflate(R.layout.item_color, parent, false);

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

        @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {


            final GameConfig datum = moviesList.get(position);

            holder.cardView.setCardBackgroundColor(Color.parseColor(datum.getColorName() + ""));
            holder.btnCancelBid.setVisibility(View.GONE);
            holder.btnReBid.setVisibility(View.GONE);
            holder.bidAmt.setVisibility(View.GONE);
            holder.lyt_bid.setVisibility(View.GONE);
            holder.bd.setVisibility(View.GONE);

            bidArrayList.clear();
//            if (!isNetworkAvailable()) {
//                return;
//            }
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("game_id", game_id + "");

            Call<AlreadyBidModel> loginModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).AlreadyBidModel("Bearer " + token, hashMap);
            loginModelCall.enqueue(new Callback<AlreadyBidModel>() {

                @Override
                public void onResponse(@NonNull Call<AlreadyBidModel> call, @NonNull Response<AlreadyBidModel> response) {
                    AlreadyBidModel object = response.body();


                    if (response.isSuccessful()) {

                        Log.e("TAG", "Login_Response : " + new Gson().toJson(response.body()));
                        bidArrayList = object.getData().getBids();
                        if (!bidArrayList.get(position).getBids().isEmpty()) {
                            returning_user = "1";
                            holder.lyt_parent.setBackground(getResources().getDrawable(R.drawable.btn_rect_teal_outline));
                            holder.btnCancelBid.setVisibility(View.VISIBLE);
                            holder.btnReBid.setVisibility(View.VISIBLE);
                            holder.bidAmt.setVisibility(View.VISIBLE);
                            holder.lyt_bid.setVisibility(View.VISIBLE);
                            holder.bd.setVisibility(View.VISIBLE);
                            holder.bidAmt.setText(bidArrayList.get(position).getBids().get(0).getAmount() + "");
                            holder.btnReBid.setOnClickListener(new View.OnClickListener() {
                                @SuppressLint("NonConstantResourceId")
                                @Override
                                public void onClick(View view) {
                                    posi = position;
                                    if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                                        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                    }

                                    final View views = getLayoutInflater().inflate(R.layout.sheet_filter, null);

                                    mBottomSheetDialog = new BottomSheetDialog(getContext());
                                    mBottomSheetDialog.setContentView(views);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                                    }
                                    final String[] amt = new String[1];
                                    Button ed_50, ed_100, ed_200, ed_300, ed_400, ed_500, ed_1000,
                                            ed_1500, ed_2000, ed_2500, ed_3000, ed_3500, ed_4000, ed_4500, ed_5000;

                                    EditText edBidAmount = views.findViewById(R.id.edBidAmount);
                                    TextView bidText = views.findViewById(R.id.bidText);
                                    edBidAmount.setText("" + object.getData().getBids().get(position).getBids().get(0).getAmount());
                                    bidText.setText("Update Bidding Points");

                                    ed_50 = views.findViewById(R.id.ed_50);
                                    ed_100 = views.findViewById(R.id.ed_100);
                                    ed_200 = views.findViewById(R.id.ed_200);
                                    ed_300 = views.findViewById(R.id.ed_300);
                                    ed_400 = views.findViewById(R.id.ed_400);
                                    ed_500 = views.findViewById(R.id.ed_500);
                                    ed_1000 = views.findViewById(R.id.ed_1000);
                                    ed_1500 = views.findViewById(R.id.ed_1500);
                                    ed_2000 = views.findViewById(R.id.ed_2000);
                                    ed_2500 = views.findViewById(R.id.ed_2500);
                                    ed_3000 = views.findViewById(R.id.ed_3000);
                                    ed_3500 = views.findViewById(R.id.ed_3500);
                                    ed_4000 = views.findViewById(R.id.ed_4000);
                                    ed_4500 = views.findViewById(R.id.ed_4500);
                                    ed_5000 = views.findViewById(R.id.ed_5000);

                                    ed_50.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_50.isSelected()) {

                                                ed_50.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_50.setTextColor(Color.WHITE);
                                            }
                                            ed_50.setSelected(!ed_50.isSelected());

                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);

                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);
                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);


                                            edBidAmount.setText("50");
//                            ed_100.setSelected(!ed_100.isSelected());
                            /*if (view instanceof Button) {
                                Button b = (Button) view;

                            }*/
                                        }
                                    });
                                    ed_100.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_100.isSelected()) {

                                                ed_100.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_100.setTextColor(Color.WHITE);
                                            }
                                            ed_100.setSelected(!ed_100.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);
                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);
                                            edBidAmount.setText("100");

                                        }
                                    });
                                    ed_200.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_200.isSelected()) {

                                                ed_200.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_200.setTextColor(Color.WHITE);
                                            }
                                            ed_200.setSelected(!ed_200.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);
                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);
                                            edBidAmount.setText("200");

                                        }
                                    });
                                    ed_300.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_300.isSelected()) {

                                                ed_300.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_300.setTextColor(Color.WHITE);
                                            }
                                            ed_300.setSelected(!ed_300.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);
                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);
                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);
                                            edBidAmount.setText("300");

                                        }
                                    });
                                    ed_400.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_400.isSelected()) {

                                                ed_400.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_400.setTextColor(Color.WHITE);
                                            }
                                            ed_400.setSelected(!ed_400.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);
                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);
                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);
                                            edBidAmount.setText("400");

                                        }
                                    });
                                    ed_500.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_500.isSelected()) {

                                                ed_500.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_500.setTextColor(Color.WHITE);
                                            }
                                            ed_500.setSelected(!ed_500.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);
                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);
                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);
                                            edBidAmount.setText("500");

                                        }
                                    });
                                    ed_1000.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_1000.isSelected()) {

                                                ed_1000.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_1000.setTextColor(Color.WHITE);
                                            }
                                            ed_1000.setSelected(!ed_1000.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);
                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);
                                            edBidAmount.setText("1000");

                                        }
                                    });
                                    ed_1500.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_1500.isSelected()) {

                                                ed_1500.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_1500.setTextColor(Color.WHITE);
                                            }
                                            ed_1500.setSelected(!ed_1500.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);
                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);

                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);
                                            edBidAmount.setText("1500");

                                        }
                                    });
                                    ed_2000.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_2000.isSelected()) {

                                                ed_2000.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_2000.setTextColor(Color.WHITE);
                                            }
                                            ed_2000.setSelected(!ed_2000.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);
                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);

                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);
                                            edBidAmount.setText("2000");

                                        }
                                    });
                                    ed_2500.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_2500.isSelected()) {

                                                ed_2500.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_2500.setTextColor(Color.WHITE);
                                            }
                                            ed_2500.setSelected(!ed_2500.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);
                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);

                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);
                                            edBidAmount.setText("2500");

                                        }
                                    });
                                    ed_3000.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_3000.isSelected()) {

                                                ed_3000.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_3000.setTextColor(Color.WHITE);
                                            }
                                            ed_3000.setSelected(!ed_3000.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);
                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);

                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);
                                            edBidAmount.setText("3000");

                                        }
                                    });
                                    ed_3500.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_3500.isSelected()) {

                                                ed_3500.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_3500.setTextColor(Color.WHITE);
                                            }
                                            ed_3500.setSelected(!ed_3500.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);
                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);

                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);
                                            edBidAmount.setText("3500");

                                        }
                                    });
                                    ed_4000.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_4000.isSelected()) {

                                                ed_4000.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_4000.setTextColor(Color.WHITE);
                                            }
                                            ed_4000.setSelected(!ed_4000.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);
                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);

                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);
                                            edBidAmount.setText("4000");

                                        }
                                    });
                                    ed_4500.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_4500.isSelected()) {

                                                ed_4500.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_4500.setTextColor(Color.WHITE);
                                            }
                                            ed_4500.setSelected(!ed_4500.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);
                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);

                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);
                                            edBidAmount.setText("4500");

                                        }
                                    });
                                    ed_5000.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_5000.isSelected()) {

                                                ed_5000.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_5000.setTextColor(Color.WHITE);
                                            }
                                            ed_5000.setSelected(!ed_5000.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);
                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);

                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            edBidAmount.setText("5000");

                                        }
                                    });


                                    Button btn_placebid = views.findViewById(R.id.btn_placebid);

                                    btn_placebid.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {


                                            if (edBidAmount.getText().toString().matches("00")) {
                                                Toast.makeText(getContext(), "Select Bid Points", Toast.LENGTH_SHORT).show();
//                                                Snackbar.make(parent_view, "Select Bid Points", Snackbar.LENGTH_SHORT).show();

                                            } else {
                                                Log.e("BID_AMT===>", edBidAmount.getText().toString().replace("", ""));


                                                progress_bar.setVisibility(View.VISIBLE);
                                                HashMap<String, String> hashMap = new HashMap<>();

                                                hashMap.put("bid_id", object.getData().getBids().get(position).getBids().get(0).getId() + "");
                                                hashMap.put("new_amount", edBidAmount.getText().toString().replace("", "") + "");


                                                Call<ReBidModel> loginModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).ReBidModel("Bearer " + token, hashMap);
                                                loginModelCall.enqueue(new Callback<ReBidModel>() {

                                                    @Override
                                                    public void onResponse(@NonNull Call<ReBidModel> call, @NonNull Response<ReBidModel> response) {
                                                        ReBidModel object = response.body();


                                                        if (response.isSuccessful()) {


                                                            Log.e("TAG", "Login_Response : " + new Gson().toJson(response.body()));
//                    labelNotification.setText(object.getResultMarque() + "");
                                                            if (object.getMessage().matches("No Money in the Wallet")) {
                                                                SweetAlertDialog sweetAlertDialogNomo = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
                                                                sweetAlertDialogNomo.setTitleText("Wrong Bidding");
                                                                sweetAlertDialogNomo.setConfirmText("Okay")
                                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                            @Override
                                                                            public void onClick(SweetAlertDialog sDialog) {
                                                                                // reuse previous dialog instance
                                                                                sDialog.dismissWithAnimation();
//                                                                holder.lyt_parent.setBackground(getResources().getDrawable(R.drawable.btn_rect_teal_outline));
                                                                                try {
                                                                                    mBottomSheetDialog.dismiss();
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }

                                                                            }
                                                                        })
                                                                        .show();


                                                            } else {
                                                              /*  new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                                        .setTitleText(object.getMessage() + "")
                                                                        .setContentText("")
                                                                        .setConfirmText("Okay")
                                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                            @Override
                                                                            public void onClick(SweetAlertDialog sDialog) {
                                                                                // reuse previous dialog instance
                                                                                sDialog.dismissWithAnimation();
                                                                                holder.lyt_parent.setBackground(getResources().getDrawable(R.drawable.btn_rect_teal_outline));
                                                                                holder.btnCancelBid.setVisibility(View.VISIBLE);
                                                                                holder.btnReBid.setVisibility(View.VISIBLE);
                                                                                holder.bidAmt.setVisibility(View.VISIBLE);
                                                                                holder.lyt_bid.setVisibility(View.VISIBLE);
                                                                                holder.bd.setVisibility(View.VISIBLE);
                                                                                holder.bidAmt.setText(edBidAmount.getText().toString().replace("", "") + "");
                                                                                gameConfigArrayList.clear();
                                                                                 countDownTimer.cancel();
                                                                                GetColor();
                                                                               */
                                                                /* GetColorBalance();
                                                                                customAdapter.notifyItemChanged(posi);
                                                                                returning_user = "1";*/
                                                                /*

                                                                                try {
                                                                                    mBottomSheetDialog.dismiss();
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }

                                                                            }
                                                                        })
                                                                        .show();*/
                                                                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
                                                                sweetAlertDialog.setTitleText(object.getMessage() + "");
                                                                sweetAlertDialog.setConfirmText("Okay")
                                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                            @Override
                                                                            public void onClick(SweetAlertDialog sDialog) {
                                                                                // reuse previous dialog instance
                                                                                sDialog.dismissWithAnimation();
                                                                                holder.lyt_parent.setBackground(getResources().getDrawable(R.drawable.btn_rect_teal_outline));
                                                                                holder.btnCancelBid.setVisibility(View.VISIBLE);
                                                                                holder.btnReBid.setVisibility(View.VISIBLE);
                                                                                holder.bidAmt.setVisibility(View.VISIBLE);
                                                                                holder.lyt_bid.setVisibility(View.VISIBLE);
                                                                                holder.bd.setVisibility(View.VISIBLE);
                                                                                holder.bidAmt.setText(edBidAmount.getText().toString().replace("", "") + "");
                                                                                //gameConfigArrayList.clear();
                                                                                if (countDownTimer != null) {
                                                                                    countDownTimer.cancel();
                                                                                }
                                                                                GetColor();

                                                                                try {
                                                                                    mBottomSheetDialog.dismiss();
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }

                                                                            }
                                                                        })
                                                                        .show();
                                                                // Hide after some seconds

                                                                final Handler handler = new Handler();
                                                                final Runnable runnable = new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        if (sweetAlertDialog.isShowing()) {
                                                                            sweetAlertDialog.dismissWithAnimation();
                                                                            try {
                                                                                mBottomSheetDialog.dismiss();
                                                                            } catch (Exception e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                            holder.lyt_parent.setBackground(getResources().getDrawable(R.drawable.btn_rect_teal_outline));
                                                                            holder.btnCancelBid.setVisibility(View.VISIBLE);
                                                                            holder.btnReBid.setVisibility(View.VISIBLE);
                                                                            holder.bidAmt.setVisibility(View.VISIBLE);
                                                                            holder.lyt_bid.setVisibility(View.VISIBLE);
                                                                            holder.bd.setVisibility(View.VISIBLE);
                                                                            holder.bidAmt.setText(edBidAmount.getText().toString().replace("", "") + "");
                                                                            //gameConfigArrayList.clear();
                                                                            if (countDownTimer != null) {
                                                                                countDownTimer.cancel();
                                                                            }
                                                                            GetColor();
                                                                        }
                                                                    }
                                                                };

                                                                sweetAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                                    @Override
                                                                    public void onDismiss(DialogInterface dialog) {
                                                                        handler.removeCallbacks(runnable);
                                                                    }
                                                                });

                                                                handler.postDelayed(runnable, 1000);

                                                            }
                                                            progress_bar.setVisibility(View.GONE);

//

                                                        } else {
                                                            progress_bar.setVisibility(View.GONE);
//                                            fab.setAlpha(1f);
                                                            try {
                                                                JSONObject jObjError = new JSONObject(response.errorBody().string());
//                                                                Snackbar.make(parent_view, jObjError.getJSONArray("errors").toString().replace("[", "").replace("]", "") + "", Snackbar.LENGTH_SHORT).show();
//                                                                Snackbar.make(parent_view, jObjError.getString("message") + "", Snackbar.LENGTH_SHORT).show();
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
                                                    public void onFailure(@NonNull Call<ReBidModel> call, @NonNull Throwable t) {
                                                        progress_bar.setVisibility(View.GONE);

                                                        t.printStackTrace();
                                                        Log.e("Login_Response", t.getMessage() + "");
                                                    }
                                                });
                                            }

                                        }
                                    });


                                    mBottomSheetDialog.show();
                                    mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            mBottomSheetDialog = null;
                                        }
                                    });
                                }
                            });
                            holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                                    sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
                                    sweetAlertDialoga.setTitleText("You Can Place New Bid Only One Time On Same Color");
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

                        } else {

                            holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
                                @SuppressLint("NonConstantResourceId")
                                @Override
                                public void onClick(View view) {

                                    if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                                        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                    }

                                    final View views = getLayoutInflater().inflate(R.layout.sheet_filter, null);

                                    mBottomSheetDialog = new BottomSheetDialog(getContext());
                                    mBottomSheetDialog.setContentView(views);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                                    }
                                    final String[] amt = new String[1];
                                    Button ed_50, ed_100, ed_200, ed_300, ed_400, ed_500, ed_1000,
                                            ed_1500, ed_2000, ed_2500, ed_3000, ed_3500, ed_4000, ed_4500, ed_5000;

                                    EditText edBidAmount = views.findViewById(R.id.edBidAmount);


                                    ed_50 = views.findViewById(R.id.ed_50);
                                    ed_100 = views.findViewById(R.id.ed_100);
                                    ed_200 = views.findViewById(R.id.ed_200);
                                    ed_300 = views.findViewById(R.id.ed_300);
                                    ed_400 = views.findViewById(R.id.ed_400);
                                    ed_500 = views.findViewById(R.id.ed_500);
                                    ed_1000 = views.findViewById(R.id.ed_1000);
                                    ed_1500 = views.findViewById(R.id.ed_1500);
                                    ed_2000 = views.findViewById(R.id.ed_2000);
                                    ed_2500 = views.findViewById(R.id.ed_2500);
                                    ed_3000 = views.findViewById(R.id.ed_3000);
                                    ed_3500 = views.findViewById(R.id.ed_3500);
                                    ed_4000 = views.findViewById(R.id.ed_4000);
                                    ed_4500 = views.findViewById(R.id.ed_4500);
                                    ed_5000 = views.findViewById(R.id.ed_5000);

                                    ed_50.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_50.isSelected()) {

                                                ed_50.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_50.setTextColor(Color.WHITE);
                                            }
                                            ed_50.setSelected(!ed_50.isSelected());

                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);

                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);
                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);


                                            edBidAmount.setText("50");
//                            ed_100.setSelected(!ed_100.isSelected());
                            /*if (view instanceof Button) {
                                Button b = (Button) view;

                            }*/
                                        }
                                    });
                                    ed_100.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_100.isSelected()) {

                                                ed_100.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_100.setTextColor(Color.WHITE);
                                            }
                                            ed_100.setSelected(!ed_100.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);
                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);
                                            edBidAmount.setText("100");

                                        }
                                    });
                                    ed_200.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_200.isSelected()) {

                                                ed_200.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_200.setTextColor(Color.WHITE);
                                            }
                                            ed_200.setSelected(!ed_200.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);
                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);
                                            edBidAmount.setText("200");

                                        }
                                    });
                                    ed_300.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_300.isSelected()) {

                                                ed_300.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_300.setTextColor(Color.WHITE);
                                            }
                                            ed_300.setSelected(!ed_300.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);
                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);
                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);
                                            edBidAmount.setText("300");

                                        }
                                    });
                                    ed_400.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_400.isSelected()) {

                                                ed_400.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_400.setTextColor(Color.WHITE);
                                            }
                                            ed_400.setSelected(!ed_400.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);
                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);
                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);
                                            edBidAmount.setText("400");

                                        }
                                    });
                                    ed_500.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_500.isSelected()) {

                                                ed_500.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_500.setTextColor(Color.WHITE);
                                            }
                                            ed_500.setSelected(!ed_500.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);
                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);
                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);
                                            edBidAmount.setText("500");

                                        }
                                    });
                                    ed_1000.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_1000.isSelected()) {

                                                ed_1000.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_1000.setTextColor(Color.WHITE);
                                            }
                                            ed_1000.setSelected(!ed_1000.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);
                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);
                                            edBidAmount.setText("1000");

                                        }
                                    });
                                    ed_1500.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_1500.isSelected()) {

                                                ed_1500.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_1500.setTextColor(Color.WHITE);
                                            }
                                            ed_1500.setSelected(!ed_1500.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);
                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);

                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);
                                            edBidAmount.setText("1500");

                                        }
                                    });
                                    ed_2000.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_2000.isSelected()) {

                                                ed_2000.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_2000.setTextColor(Color.WHITE);
                                            }
                                            ed_2000.setSelected(!ed_2000.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);
                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);

                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);
                                            edBidAmount.setText("2000");

                                        }
                                    });
                                    ed_2500.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_2500.isSelected()) {

                                                ed_2500.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_2500.setTextColor(Color.WHITE);
                                            }
                                            ed_2500.setSelected(!ed_2500.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);
                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);

                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);
                                            edBidAmount.setText("2500");

                                        }
                                    });
                                    ed_3000.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_3000.isSelected()) {

                                                ed_3000.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_3000.setTextColor(Color.WHITE);
                                            }
                                            ed_3000.setSelected(!ed_3000.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);
                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);

                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);
                                            edBidAmount.setText("3000");

                                        }
                                    });
                                    ed_3500.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_3500.isSelected()) {

                                                ed_3500.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_3500.setTextColor(Color.WHITE);
                                            }
                                            ed_3500.setSelected(!ed_3500.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);
                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);

                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);
                                            edBidAmount.setText("3500");

                                        }
                                    });
                                    ed_4000.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_4000.isSelected()) {

                                                ed_4000.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_4000.setTextColor(Color.WHITE);
                                            }
                                            ed_4000.setSelected(!ed_4000.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);
                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);

                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);
                                            edBidAmount.setText("4000");

                                        }
                                    });
                                    ed_4500.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_4500.isSelected()) {

                                                ed_4500.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_4500.setTextColor(Color.WHITE);
                                            }
                                            ed_4500.setSelected(!ed_4500.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);
                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);

                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_5000.setSelected(false);
                                            ed_5000.setTextColor(Color.BLACK);
                                            edBidAmount.setText("4500");

                                        }
                                    });
                                    ed_5000.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ed_5000.isSelected()) {

                                                ed_5000.setTextColor(getResources().getColor(R.color.grey_40));
                                            } else {
                                                ed_5000.setTextColor(Color.WHITE);
                                            }
                                            ed_5000.setSelected(!ed_5000.isSelected());
                                            ed_50.setSelected(false);
                                            ed_50.setTextColor(Color.BLACK);
                                            ed_100.setSelected(false);
                                            ed_100.setTextColor(Color.BLACK);
                                            ed_200.setSelected(false);
                                            ed_200.setTextColor(Color.BLACK);
                                            ed_300.setSelected(false);
                                            ed_300.setTextColor(Color.BLACK);
                                            ed_400.setSelected(false);
                                            ed_400.setTextColor(Color.BLACK);
                                            ed_500.setSelected(false);
                                            ed_500.setTextColor(Color.BLACK);
                                            ed_1000.setSelected(false);
                                            ed_1000.setTextColor(Color.BLACK);

                                            ed_1500.setSelected(false);
                                            ed_1500.setTextColor(Color.BLACK);
                                            ed_2000.setSelected(false);
                                            ed_2000.setTextColor(Color.BLACK);
                                            ed_2500.setSelected(false);
                                            ed_2500.setTextColor(Color.BLACK);
                                            ed_3000.setSelected(false);
                                            ed_3000.setTextColor(Color.BLACK);
                                            ed_3500.setSelected(false);
                                            ed_3500.setTextColor(Color.BLACK);
                                            ed_4000.setSelected(false);
                                            ed_4000.setTextColor(Color.BLACK);
                                            ed_4500.setSelected(false);
                                            ed_4500.setTextColor(Color.BLACK);
                                            edBidAmount.setText("5000");

                                        }
                                    });


                                    Button btn_placebid = views.findViewById(R.id.btn_placebid);

                                    btn_placebid.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (edBidAmount.getText().toString().matches("00")) {
                                                Toast.makeText(getContext(), "Select Bid Points", Toast.LENGTH_SHORT).show();
//                                                Snackbar.make(parent_view, "Select Bid Points", Snackbar.LENGTH_SHORT).show();

                                            } else {
                                                Log.e("BID_AMT===>", edBidAmount.getText().toString().replace("", ""));


                                                progress_bar.setVisibility(View.VISIBLE);
                                                HashMap<String, String> hashMap = new HashMap<>();
                                                hashMap.put("game_id", game_id + "");
                                                hashMap.put("color_id", datum.getColorId() + "");
                                                hashMap.put("amount", edBidAmount.getText().toString().replace("", "") + "");


                                                Call<GameStartModel> loginModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).GameStartModel("Bearer " + token, hashMap);
                                                loginModelCall.enqueue(new Callback<GameStartModel>() {

                                                    @Override
                                                    public void onResponse(@NonNull Call<GameStartModel> call, @NonNull Response<GameStartModel> response) {
                                                        GameStartModel object = response.body();


                                                        if (response.isSuccessful()) {


                                                            Log.e("TAG", "Login_Response : " + new Gson().toJson(response.body()));
//                    labelNotification.setText(object.getResultMarque() + "");
                                                            if (object.getMessage().matches("No Money in the Wallet")) {
                                                                SweetAlertDialog sweetAlertDialogNomo = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
                                                                sweetAlertDialogNomo.setTitleText("Wrong Bidding");
                                                                sweetAlertDialogNomo.setConfirmText("Okay")
                                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                            @Override
                                                                            public void onClick(SweetAlertDialog sDialog) {
                                                                                // reuse previous dialog instance
                                                                                sDialog.dismissWithAnimation();
//                                                                holder.lyt_parent.setBackground(getResources().getDrawable(R.drawable.btn_rect_teal_outline));
                                                                                try {
                                                                                    mBottomSheetDialog.dismiss();
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }

                                                                            }
                                                                        })
                                                                        .show();
                                                                final Handler handler = new Handler();
                                                                final Runnable runnable = new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        if (sweetAlertDialogNomo.isShowing()) {
                                                                            sweetAlertDialogNomo.dismissWithAnimation();
                                                                            try {
                                                                                mBottomSheetDialog.dismiss();
                                                                            } catch (Exception e) {
                                                                                e.printStackTrace();
                                                                            }

                                                                            GetColor();
                                                                        }
                                                                    }
                                                                };

                                                                sweetAlertDialogNomo.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                                    @Override
                                                                    public void onDismiss(DialogInterface dialog) {
                                                                        handler.removeCallbacks(runnable);
                                                                    }
                                                                });

                                                                handler.postDelayed(runnable, 1000);


                                                            } else {
                                                                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
                                                                sweetAlertDialog.setTitleText(object.getMessage() + "");
                                                                sweetAlertDialog.setConfirmText("Okay")
                                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                            @Override
                                                                            public void onClick(SweetAlertDialog sDialog) {
                                                                                // reuse previous dialog instance
                                                                                sDialog.dismissWithAnimation();
                                                                                holder.lyt_parent.setBackground(getResources().getDrawable(R.drawable.btn_rect_teal_outline));
                                                                                holder.btnCancelBid.setVisibility(View.VISIBLE);
                                                                                holder.btnReBid.setVisibility(View.VISIBLE);
                                                                                holder.bidAmt.setVisibility(View.VISIBLE);
                                                                                holder.lyt_bid.setVisibility(View.VISIBLE);
                                                                                holder.bd.setVisibility(View.VISIBLE);
                                                                                holder.bidAmt.setText(edBidAmount.getText().toString().replace("", "") + "");
                                                                                //gameConfigArrayList.clear();
                                                                                if (countDownTimer != null) {
                                                                                    countDownTimer.cancel();
                                                                                }
                                                                                GetColor();

                                                                                try {
                                                                                    mBottomSheetDialog.dismiss();
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }

                                                                            }
                                                                        })
                                                                        .show();
                                                                // Hide after some seconds

                                                                final Handler handler = new Handler();
                                                                final Runnable runnable = new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        if (sweetAlertDialog.isShowing()) {
                                                                            sweetAlertDialog.dismissWithAnimation();
                                                                            try {
                                                                                mBottomSheetDialog.dismiss();
                                                                            } catch (Exception e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                            holder.lyt_parent.setBackground(getResources().getDrawable(R.drawable.btn_rect_teal_outline));
                                                                            holder.btnCancelBid.setVisibility(View.VISIBLE);
                                                                            holder.btnReBid.setVisibility(View.VISIBLE);
                                                                            holder.bidAmt.setVisibility(View.VISIBLE);
                                                                            holder.lyt_bid.setVisibility(View.VISIBLE);
                                                                            holder.bd.setVisibility(View.VISIBLE);
                                                                            holder.bidAmt.setText(edBidAmount.getText().toString().replace("", "") + "");
                                                                            //gameConfigArrayList.clear()
                                                                            // if;
                                                                            if (countDownTimer != null) {
                                                                                countDownTimer.cancel();
                                                                            }

                                                                            GetColor();
                                                                        }
                                                                    }
                                                                };

                                                                sweetAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                                    @Override
                                                                    public void onDismiss(DialogInterface dialog) {
                                                                        handler.removeCallbacks(runnable);
                                                                    }
                                                                });

                                                                handler.postDelayed(runnable, 1000);
                                                            }
                                                            progress_bar.setVisibility(View.GONE);

//

                                                        } else {
                                                            progress_bar.setVisibility(View.GONE);
//                                            fab.setAlpha(1f);
                                                            try {
                                                                JSONObject jObjError = new JSONObject(response.errorBody().string());
//                                                                Snackbar.make(parent_view, jObjError.getJSONArray("errors").toString().replace("[", "").replace("]", "") + "", Snackbar.LENGTH_SHORT).show();
//                                                                Snackbar.make(parent_view, jObjError.getString("message") + "", Snackbar.LENGTH_SHORT).show();
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
                                                    public void onFailure(@NonNull Call<GameStartModel> call, @NonNull Throwable t) {
                                                        progress_bar.setVisibility(View.GONE);

                                                        t.printStackTrace();
                                                        Log.e("Login_Response", t.getMessage() + "");
                                                    }
                                                });
                                            }

                                        }
                                    });


                                    mBottomSheetDialog.show();
                                    mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            mBottomSheetDialog = null;
                                        }
                                    });
                                }
                            });

                        }


                    } else {

//                    fab.setAlpha(1f);
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
//                            Snackbar.make(parent_view, jObjError.getJSONArray("errors").toString().replace("[", "").replace("]", "") + "", Snackbar.LENGTH_SHORT).show();
//                            Snackbar.make(parent_view, jObjError.getString("message") + "", Snackbar.LENGTH_SHORT).show();
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
                public void onFailure(@NonNull Call<AlreadyBidModel> call, @NonNull Throwable t) {
                    progress_bar.setVisibility(View.GONE);
//                fab.setAlpha(1f);
                    t.printStackTrace();
                    Log.e("Login_Response", t.getMessage() + "");
                }
            });

            holder.btnCancelBid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    posi = position;
                    SchoolCall(datum.getColorId() + "");
//                    Toast.makeText(getActivity(), datum.getColorId() + "", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialogCancelBid = new Dialog(activity);
                            dialogCancelBid.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                            dialogCancelBid.setContentView(R.layout.dialog_school);
                            dialogCancelBid.setCancelable(true);

                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            lp.copyFrom(dialogCancelBid.getWindow().getAttributes());
                            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            TextInputEditText et_amt, et_remark;

                            RecyclerView rvTimeSlot;
                            rvTimeSlot = dialogCancelBid.findViewById(R.id.rvTimeSlot);
                            rvTimeSlot.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//        recyclerViewPackagedFood.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(getActivity(), 8), true));
                            rvTimeSlot.setHasFixedSize(true);
                            rvTimeSlot.setNestedScrollingEnabled(false);
                            schoolCustomAdapter = new SchoolCustomAdapter(particular_bidArrayList);
                            rvTimeSlot.setAdapter(schoolCustomAdapter);


                            dialogCancelBid.show();
                            dialogCancelBid.getWindow().setAttributes(lp);
                        }
                    }, 500);

                }
            });

//            if (bidArrayList.get(position).getBids().isEmpty()) {
//
//
//            }else {
//                holder.lyt_parent.setClickable(false);
//            }

        }

        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            CardView cardView;
            LinearLayout lyt_parent, lyt_bid;
            Button btnCancelBid, btnReBid;
            TextView bidAmt, bd;


            public MyViewHolder(View view) {
                super(view);

                cardView = view.findViewById(R.id.cardView);
                btnCancelBid = view.findViewById(R.id.btnCancelBid);
                bidAmt = view.findViewById(R.id.bidAmt);
                bd = view.findViewById(R.id.bd);
                btnReBid = view.findViewById(R.id.btnReBid);
                lyt_parent = view.findViewById(R.id.lyt_parent);
                lyt_bid = view.findViewById(R.id.lyt_bid);
            }

        }

    }

    public void SchoolCall(String ColorId) {

        HashMap<String, String> hashMasp = new HashMap<>();
        hashMasp.put("game_id", game_id + "");
        hashMasp.put("color_id", ColorId + "");
        showProgressDialog();
        Call<ParticularBidsModel> schoolModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).ParticularBidsModel("Bearer " + token, hashMasp);
        schoolModelCall.enqueue(new Callback<ParticularBidsModel>() {

            @Override
            public void onResponse(@NonNull Call<ParticularBidsModel> call, @NonNull Response<ParticularBidsModel> response) {
                ParticularBidsModel object = response.body();
                hideProgressDialog();
                if (response.isSuccessful()) {
                    Log.e("TAG", "School_Response : " + new Gson().toJson(response.body()));

                    particular_bidArrayList = object.getData().getBids();


                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ParticularBidsModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                t.printStackTrace();
                Log.e("School_Response", t.getMessage() + "");
            }
        });
    }

    public void Result(String game_id) {
//        handler.removeCallbacks(runnable);
        bidsArrayArrayList.clear();
        HashMap<String, String> hashMasp = new HashMap<>();
        hashMasp.put("game_id", game_id + "");

//        showProgressDialog();
        Call<ResultsModel> schoolModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).ResultsModel("Bearer " + token, hashMasp);
        schoolModelCall.enqueue(new Callback<ResultsModel>() {

            @Override
            public void onResponse(@NonNull Call<ResultsModel> call, @NonNull Response<ResultsModel> response) {
                ResultsModel object = response.body();
                hideProgressDialog();
                if (response.isSuccessful()) {
//                    Toast.makeText(activity, "Result", Toast.LENGTH_SHORT).show();
                    Log.e("TAG", "Result");


                    if (object != null) {
                        bidsArrayArrayList = object.getData().getBidsArray();
                        if (returning_user.matches("0")) {
//                                            gameConfigArrayList.clear();
                            nested_content.setVisibility(View.GONE);
                            Log.e("returning_userAfter", returning_user + "");
                            final Dialog newDialog = new Dialog(activity);
                            newDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            newDialog.setContentView(R.layout.floating_tutorial);
                            newDialog.setCancelable(false);
                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            lp.copyFrom(newDialog.getWindow().getAttributes());
                            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                            lp.height = WindowManager.LayoutParams.MATCH_PARENT;


//
                            newDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
                            TextView timesa = newDialog.findViewById(R.id.time);
                            TextView resultin = newDialog.findViewById(R.id.resultin);
                            resultin.setText("New Game \nStarts in");
                            long difsf = d4.getTime() - d2.getTime();

                            new CountDownTimer(difsf, 1000) { // adjust the milli seconds here

                                public void onTick(long millisUntilFinished) {
                                    timesa.setText("" + String.format("%d:%02d",
                                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) + " sec ");
                                }

                                public void onFinish() {
                                    //gameConfigArrayList.clear();
                                    GetColor();
//                                    GetColorTime();
                                    try {
                                        newDialog.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                }
                            }.start();

                            newDialog.getWindow().setAttributes(lp);
                            try {
                                newDialog.show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
//                                            gameConfigArrayList.clear();
                            nested_content.setVisibility(View.GONE);
                            firstDialog = new Dialog(activity);

                            firstDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            firstDialog.setContentView(R.layout.floating_tutorial);
                            firstDialog.setCancelable(false);
                            WindowManager.LayoutParams lpA = new WindowManager.LayoutParams();
                            lpA.copyFrom(firstDialog.getWindow().getAttributes());
                            lpA.width = WindowManager.LayoutParams.MATCH_PARENT;
                            lpA.height = WindowManager.LayoutParams.MATCH_PARENT;

//                            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                            firstDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                            dialog.getWindow().setFormat(PixelFormat.TRANSPARENT);
                            TextView times = firstDialog.findViewById(R.id.time);

                            countDownTimerResult = new CountDownTimer(20150, 1000) { // adjust the milli seconds here

                                public void onTick(long millisUntilFinished) {
                                    times.setText("" + String.format("%d:%02d",
                                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) + " sec ");
                                }

                                @SuppressLint("SetTextI18n")
                                public void onFinish() {


                                    firstDialog.dismiss();


                                    final Dialog dialog = new Dialog(activity);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                                    dialog.setContentView(R.layout.dialog_result_animation);
                                    dialog.setCancelable(false);

                                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                    lp.copyFrom(dialog.getWindow().getAttributes());
                                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
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
//                                    webviewAboutUs.requestFocus();
//                                    webviewAboutUs.getSettings().setLightTouchEnabled(true);
//                                    webviewAboutUs.getSettings().setJavaScriptEnabled(true);
//                                    webviewAboutUs.getSettings().setGeolocationEnabled(true);
//                                    webviewAboutUs.setSoundEffectsEnabled(true);
//                                    webviewAboutUs.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
//                                    webviewAboutUs.getSettings().setUseWideViewPort(true);
                                    webviewAboutUs.getSettings().setLoadWithOverviewMode(true);
                                    webviewAboutUs.getSettings().setUseWideViewPort(true);

                                    webviewAboutUs.loadUrl("http://13.127.177.177/api/result_board");
//                                    webviewAboutUs.getSettings().setUseWideViewPort(true);
//                                    webviewAboutUs.getSettings().setLoadWithOverviewMode(true);
                                    new CountDownTimer(25150, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {

                                        }

                                        @Override
                                        public void onFinish() {
                                            dialog.dismiss();
                                            Dialog secondDialog = new Dialog(activity);
                                            secondDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            secondDialog.setContentView(R.layout.dialog_results_screen);
                                            secondDialog.setCancelable(false);
                                            WindowManager.LayoutParams lpog = new WindowManager.LayoutParams();
                                            lpog.copyFrom(secondDialog.getWindow().getAttributes());
                                            lpog.width = WindowManager.LayoutParams.MATCH_PARENT;
                                            lpog.height = WindowManager.LayoutParams.MATCH_PARENT;
                                            TextView tvWin = secondDialog.findViewById(R.id.tvWin);
                                            TextView tvLoss = secondDialog.findViewById(R.id.tvLoss);
                                            TextView tvBalance = secondDialog.findViewById(R.id.tvCurrentBalance);

//                                    dial_og.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            RecyclerView rvResults;
                                            rvResults = secondDialog.findViewById(R.id.rvResults);
                                            rvResults.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                            rvResults.setHasFixedSize(true);
                                            rvResults.setNestedScrollingEnabled(false);

                                            resultsCustomAdapter = new ResultsCustomAdapter(bidsArrayArrayList);
                                            rvResults.setAdapter(resultsCustomAdapter);
//                                            countDownTimerResult.cancel();
                                            tvWin.setText(object.getData().getWinAmount() + "");
                                            tvLoss.setText(object.getData().getLossAmount() + "");
                                            tvBalance.setText("Current Points : " + object.getData().getCurrentBalance() + "");
                                            CountDownTimer cas = new CountDownTimer(25150, 1000) { // adjust the milli seconds here

                                                public void onTick(long millisUntilFinished) {

                                                }

                                                public void onFinish() {
                                                    countDownTimerResult.cancel();
                                                    secondDialog.dismiss();
                                                    final Dialog thirdDialog = new Dialog(activity);
                                                    thirdDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                    thirdDialog.setContentView(R.layout.floating_tutorial);
                                                    thirdDialog.setCancelable(false);
                                                    WindowManager.LayoutParams lpw = new WindowManager.LayoutParams();
                                                    lpw.copyFrom(thirdDialog.getWindow().getAttributes());
                                                    lpw.width = WindowManager.LayoutParams.MATCH_PARENT;
                                                    lpw.height = WindowManager.LayoutParams.MATCH_PARENT;


//
                                                    thirdDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
                                                    TextView timessas = thirdDialog.findViewById(R.id.time);
                                                    TextView resultinfdf = thirdDialog.findViewById(R.id.resultin);
                                                    resultinfdf.setText("New Game \nStarts in");

                                                    CountDownTimer ca = new CountDownTimer(10150, 1000) { // adjust the milli seconds here

                                                        public void onTick(long millisUntilFinished) {
                                                            timessas.setText("" + String.format("%d:%02d",
                                                                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                                                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) + " sec ");
                                                        }

                                                        public void onFinish() {
//                                                            returning_user = "0";
//                                                            marques.setVisibility(View.GONE);
                                                            try {
//                                                                dialogCancelBid.dismiss();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                            thirdDialog.dismiss();
                                                            //gameConfigArrayList.clear();
                                                            if (countDownTimer != null) {
                                                                countDownTimer.cancel();
                                                            }

                                                            GetColor();

//                                                    GetColorTime();


                                                        }
                                                    };
                                                    ca.start();
                                                    try {
                                                        thirdDialog.show();
                                                        thirdDialog.getWindow().setAttributes(lpw);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }


                                                }
                                            };
                                            cas.start();
                                            try {
                                                secondDialog.show();
                                                secondDialog.getWindow().setAttributes(lpog);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }.start();

                                    dialog.show();
                                    dialog.getWindow().setAttributes(lp);


                                }
                            };
                            countDownTimerResult.start();


                            try {

                                firstDialog.show();
                                firstDialog.getWindow().setAttributes(lpA);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    }


                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultsModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                t.printStackTrace();
                Log.e("School_Response", t.getMessage() + "");
            }
        });
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
}