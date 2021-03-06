package app.bizita.colorrush.ui.ui.win_screen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import app.bizita.colorrush.R;
import app.bizita.colorrush.api.RetrofitHelper;
import app.bizita.colorrush.model.dashboard.DashboardModel;
import app.bizita.colorrush.model.dashboard.GameConfig;
import app.bizita.colorrush.model.game_start.GameStartModel;
import app.bizita.colorrush.model.login.LoginModel;
import app.bizita.colorrush.ui.ui.gallery.GalleryFragment;
import app.bizita.colorrush.utils.BaseClass;
import app.bizita.colorrush.utils.PrefUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WinScreenActivity extends AppCompatActivity {

    RecyclerView rvColor;
    ArrayList<GameConfig> gameConfigArrayList = new ArrayList<>();
    private CustomAdapter customAdapter;
    LoginModel loginModel;
    String token, status;
    ImageView tv1, tv2, tv3, tv4, tv5, tv6, iv_1, iv_2, iv_3, iv_4, iv_5, iv_6;
    DisplayMetrics displaymetrics;
    ImageView image1, image2, image3, image4, image5, image6;
    ImageView image11, image22, image33, image44, image55, image66;
    ImageView image111, image222, image333, image444, image555, image666;
    ImageView image1111, image2222, image3333, image4444, image5555, image6666;
    ImageView image11111, image22222, image33333, image44444, image55555, image66666;
    ImageView image111111, image222222, image333333, image444444, image555555, image666666;
    LinearLayout lyt_parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win_scree);
        loginModel = PrefUtils.getUser(WinScreenActivity.this);
        token = loginModel.getData().getToken();
        lyt_parent = findViewById(R.id.lyt_parent);
        rvColor = findViewById(R.id.rvColor);
        rvColor.setLayoutManager(new GridLayoutManager(WinScreenActivity.this, 6));
//        rvColor.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(getActivity(), 8), true));
        rvColor.setHasFixedSize(true);
        rvColor.setNestedScrollingEnabled(false);
        tv1 = (ImageView) findViewById(R.id.tv);
        tv2 = (ImageView) findViewById(R.id.tv2);
        tv3 = (ImageView) findViewById(R.id.tv3);

        tv4 = (ImageView) findViewById(R.id.tv4);
        tv5 = (ImageView) findViewById(R.id.tv5);
        tv6 = (ImageView) findViewById(R.id.tv6);

        iv_1 = (ImageView) findViewById(R.id.iv_1);
        iv_2 = (ImageView) findViewById(R.id.iv_2);
        iv_3 = (ImageView) findViewById(R.id.iv_3);

        iv_4 = (ImageView) findViewById(R.id.iv_4);
        iv_5 = (ImageView) findViewById(R.id.iv_4);
        iv_6 = (ImageView) findViewById(R.id.iv_6);


        displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
       /* image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);
        image5 = findViewById(R.id.image5);
        image6 = findViewById(R.id.image6);

        image11 = findViewById(R.id.image11);
        image22 = findViewById(R.id.image22);
        image33 = findViewById(R.id.image33);
        image44 = findViewById(R.id.image44);
        image55 = findViewById(R.id.image55);
        image66 = findViewById(R.id.image66);

        image111 = findViewById(R.id.image111);
        image222 = findViewById(R.id.image222);
        image333 = findViewById(R.id.image333);
        image444 = findViewById(R.id.image444);
        image555 = findViewById(R.id.image555);
        image666 = findViewById(R.id.image666);

        image1111 = findViewById(R.id.image1111);
        image2222 = findViewById(R.id.image2222);
        image3333 = findViewById(R.id.image3333);
        image4444 = findViewById(R.id.image4444);
        image5555 = findViewById(R.id.image5555);
        image6666 = findViewById(R.id.image6666);

        image11111 = findViewById(R.id.image11111);
        image22222 = findViewById(R.id.image22222);
        image33333 = findViewById(R.id.image33333);
        image44444 = findViewById(R.id.image44444);
        image55555 = findViewById(R.id.image55555);
        image66666 = findViewById(R.id.image66666);

        image111111 = findViewById(R.id.image111111);
        image222222 = findViewById(R.id.image222222);
        image333333 = findViewById(R.id.image333333);
        image444444 = findViewById(R.id.image444444);
        image555555 = findViewById(R.id.image555555);
        image666666 = findViewById(R.id.image666666);
*/
        GetColor();

    }

    public boolean isNetworkAvailable() {
        return BaseClass.isNetworkAvailable(WinScreenActivity.this);
    }

    public void GetColor() {
        if (!isNetworkAvailable()) {
            return;
        }

        Call<DashboardModel> loginModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).DashboardModel("Bearer " + token);
        loginModelCall.enqueue(new Callback<DashboardModel>() {

            @Override
            public void onResponse(@NonNull Call<DashboardModel> call, @NonNull Response<DashboardModel> response) {
                DashboardModel object = response.body();


                if (response.isSuccessful()) {
                    Log.e("TAG", "Login_Response : " + new Gson().toJson(response.body()));


                    gameConfigArrayList = object.getData().getGameConfig();

               /*     if (object.getData().getGameConfig().get(0).getStatus().equalsIgnoreCase("Win")) {

                        ObjectAnimator moveX2 = ObjectAnimator.ofFloat(tv1, "x", 600);
                        ObjectAnimator moveY2 = ObjectAnimator.ofFloat(tv1, "y", 600);
                        AnimatorSet as2 = new AnimatorSet();
                        as2.playTogether(moveX2, moveY2);
                        as2.setDuration(5000);
                        as2.start();
                        iv_1.setBackgroundColor(Color.parseColor(object.getData().getGameConfig().get(0).getColorName() + ""));

                    } else {
                        tv1.setVisibility(View.GONE);
                        iv_1.setVisibility(View.GONE);
                    }

                    if (object.getData().getGameConfig().get(1).getStatus().equalsIgnoreCase("Win")) {
                        ObjectAnimator moveX2 = ObjectAnimator.ofFloat(tv2, "x", 100);
                        ObjectAnimator moveY2 = ObjectAnimator.ofFloat(tv2, "y", 100);
                        AnimatorSet as2 = new AnimatorSet();
                        as2.playTogether(moveX2, moveY2);
                        as2.setDuration(5000);
                        as2.start();
                        iv_2.setBackgroundColor(Color.parseColor(object.getData().getGameConfig().get(1).getColorName() + ""));

                    } else {
                        tv2.setVisibility(View.GONE);
                        iv_2.setVisibility(View.GONE);

                    }

                    if (object.getData().getGameConfig().get(2).getStatus().equalsIgnoreCase("Win")) {
                        ObjectAnimator moveX2 = ObjectAnimator.ofFloat(tv3, "x", 200);
                        ObjectAnimator moveY2 = ObjectAnimator.ofFloat(tv3, "y", 200);
                        AnimatorSet as2 = new AnimatorSet();
                        as2.playTogether(moveX2, moveY2);
                        as2.setDuration(5000);
                        as2.start();
                        iv_3.setBackgroundColor(Color.parseColor(object.getData().getGameConfig().get(2).getColorName() + ""));

                    } else {
                        tv3.setVisibility(View.GONE);
                        iv_3.setVisibility(View.GONE);

                    }
                    if (object.getData().getGameConfig().get(3).getStatus().equalsIgnoreCase("Win")) {
                        ObjectAnimator moveX2 = ObjectAnimator.ofFloat(tv4, "x", image444.getX());
                        ObjectAnimator moveY2 = ObjectAnimator.ofFloat(tv4, "y", 300);
                        AnimatorSet as2 = new AnimatorSet();
                        as2.playTogether(moveX2, moveY2);
                        as2.setDuration(5000);
                        as2.start();
                        iv_4.setBackgroundColor(Color.parseColor(object.getData().getGameConfig().get(3).getColorName() + ""));

                    } else {
                        tv4.setVisibility(View.GONE);
                        iv_4.setVisibility(View.GONE);

                    }
                    if (object.getData().getGameConfig().get(4).getStatus().equalsIgnoreCase("Win")) {
                        ObjectAnimator moveX2 = ObjectAnimator.ofFloat(tv5, "x", image1.getX());
                        ObjectAnimator moveY2 = ObjectAnimator.ofFloat(tv5, "y", 100);
                        AnimatorSet as2 = new AnimatorSet();
                        as2.playTogether(moveX2, moveY2);
                        as2.setDuration(5000);
                        as2.start();
                        iv_5.setBackgroundColor(Color.parseColor(object.getData().getGameConfig().get(4).getColorName() + ""));

                    } else {
                        tv5.setVisibility(View.GONE);
                        iv_5.setVisibility(View.GONE);

                    }
                    if (object.getData().getGameConfig().get(5).getStatus().equalsIgnoreCase("Win")) {
                        ObjectAnimator moveX2 = ObjectAnimator.ofFloat(tv6, "x", image6666.getX());
                        ObjectAnimator moveY2 = ObjectAnimator.ofFloat(tv6, "y", 400);
                        AnimatorSet as2 = new AnimatorSet();
                        as2.playTogether(moveX2, moveY2);
                        as2.setDuration(5000);
                        as2.start();
                        iv_6.setBackgroundColor(Color.parseColor(object.getData().getGameConfig().get(5).getColorName() + ""));

                    } else {
                        tv6.setVisibility(View.GONE);
                        iv_6.setVisibility(View.GONE);

                    }*/


                    customAdapter = new CustomAdapter(gameConfigArrayList);
                    Collections.shuffle(gameConfigArrayList);
                    rvColor.setAdapter(customAdapter);


//

                } else {

//                    fab.setAlpha(1f);
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());

                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<DashboardModel> call, @NonNull Throwable t) {

//                fab.setAlpha(1f);
                t.printStackTrace();
                Log.e("Login_Response", t.getMessage() + "");
            }
        });
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

        private ArrayList<GameConfig> moviesList;
        MotionEvent event;

        public CustomAdapter(ArrayList<GameConfig> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(WinScreenActivity.this)
                    .inflate(R.layout.grid, parent, false);

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


            final GameConfig datum = moviesList.get(position);
            holder.image2.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));


            if (datum.getStatus().matches("Win")) {
//                ObjectAnimator moveX2 = ObjectAnimator.ofFloat(tv5, "x",0, 300);
//                ObjectAnimator moveY2 = ObjectAnimator.ofFloat(tv5, "y", holder.image2.getX());
//                AnimatorSet as2 = new AnimatorSet();
//                as2.playTogether(moveX2);
//                as2.setDuration(5000);
//                as2.start();
                int[] location = new int[2];
//                image1.getLocationOnScreen(location);
                holder.image2.getLocationOnScreen(location);
//                image3.getLocationOnScreen(location);
//                image4.getLocationOnScreen(location);
//                image5.getLocationOnScreen(location);
//                image6.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];
                Random random = new Random();
                int xa = random.nextInt(700) + 100;
                Log.e("Random", xa + "");
                //Do something after 100ms

                if (datum.getColorName().matches("#ff0000")) {
                    ObjectAnimator moveX = ObjectAnimator.ofFloat(tv5, "x", 0);
                    ObjectAnimator moveY = ObjectAnimator.ofFloat(tv5, "y", 0);
                    AnimatorSet as = new AnimatorSet();
                    as.playTogether(moveX, moveY);
                    as.setDuration(5000);
                    as.start();
                }
                if (datum.getColorName().matches("#ffffff")) {
                    ObjectAnimator moveXa = ObjectAnimator.ofFloat(tv1, "x", xa);
                    ObjectAnimator moveYa = ObjectAnimator.ofFloat(tv1, "y", xa);
                    AnimatorSet aas = new AnimatorSet();
                    aas.playTogether(moveXa, moveYa);
                    aas.setDuration(8000);
                    aas.start();
                }

                if (datum.getColorName().matches("#008000")) {
                    ObjectAnimator moveXe = ObjectAnimator.ofFloat(tv3, "x", xa);
                    ObjectAnimator moveYe = ObjectAnimator.ofFloat(tv3, "y", xa);
                    AnimatorSet ase = new AnimatorSet();
                    ase.playTogether(moveXe, moveYe);
                    ase.setDuration(12000);
                    ase.start();
                }
            }
        /*
            holder.image222.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));
            holder.image3.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));
            holder.image4.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));
            holder.image5555.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));

            holder.image6.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));

            holder.image11.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));
            holder.image22.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));
            holder.image33.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));
            holder.image44.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));
            holder.image55.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));
            holder.image66.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));

            holder.image111.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));

            holder.image222222.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));
            holder.image333.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));
            holder.image444.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));
            holder.image555555.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));

            holder.image5.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));
            holder.image666.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));

            holder.image1111.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));
            holder.image2222.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));
            holder.image3333.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));
            holder.image4444.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));
            holder.image555.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));
            holder.image6666.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));

            holder.image11111.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));
            holder.image22222.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));
            holder.image33333.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));
            holder.image44444.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));
            holder.image66666.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));

            holder.image111111.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));
            holder.image2.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));
            holder.image55555.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));

            holder.image333333.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));
            holder.image444444.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));
            holder.image666666.setBackgroundColor(Color.parseColor(datum.getColorName() + ""));

*/


        }

        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView image1, image2, image3, image4, image5, image6;
            ImageView image11, image22, image33, image44, image55, image66;
            ImageView image111, image222, image333, image444, image555, image666;
            ImageView image1111, image2222, image3333, image4444, image5555, image6666;
            ImageView image11111, image22222, image33333, image44444, image55555, image66666;
            ImageView image111111, image222222, image333333, image444444, image555555, image666666;

            public MyViewHolder(View view) {
                super(view);

//                image1 = view.findViewById(R.id.image1);
                image2 = view.findViewById(R.id.image2);

//                image3 = view.findViewById(R.id.image3);
//                image4 = view.findViewById(R.id.image4);
//                image5 = view.findViewById(R.id.image5);
//                image6 = view.findViewById(R.id.image6);
//
//                image11 = view.findViewById(R.id.image11);
//                image22 = view.findViewById(R.id.image22);
//                image33 = view.findViewById(R.id.image33);
//                image44 = view.findViewById(R.id.image44);
//                image55 = view.findViewById(R.id.image55);
//                image66 = view.findViewById(R.id.image66);
//
//                image111 = view.findViewById(R.id.image111);
//                image222 = view.findViewById(R.id.image222);
//                image333 = view.findViewById(R.id.image333);
//                image444 = view.findViewById(R.id.image444);
//                image555 = view.findViewById(R.id.image555);
//                image666 = view.findViewById(R.id.image666);
//
//                image1111 = view.findViewById(R.id.image1111);
//                image2222 = view.findViewById(R.id.image2222);
//                image3333 = view.findViewById(R.id.image3333);
//                image4444 = view.findViewById(R.id.image4444);
//                image5555 = view.findViewById(R.id.image5555);
//                image6666 = view.findViewById(R.id.image6666);
//
//                image11111 = view.findViewById(R.id.image11111);
//                image22222 = view.findViewById(R.id.image22222);
//                image33333 = view.findViewById(R.id.image33333);
//                image44444 = view.findViewById(R.id.image44444);
//                image55555 = view.findViewById(R.id.image55555);
//                image66666 = view.findViewById(R.id.image66666);
//
//                image111111 = view.findViewById(R.id.image111111);
//                image222222 = view.findViewById(R.id.image222222);
//                image333333 = view.findViewById(R.id.image333333);
//                image444444 = view.findViewById(R.id.image444444);
//                image555555 = view.findViewById(R.id.image555555);
//                image666666 = view.findViewById(R.id.image666666);
            }

        }

    }

    public static Point getLocationOnScreen(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new Point(location[0], location[1]);
    }
}