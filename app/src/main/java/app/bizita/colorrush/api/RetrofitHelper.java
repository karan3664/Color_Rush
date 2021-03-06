package app.bizita.colorrush.api;


import android.content.Context;

import androidx.annotation.NonNull;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import app.bizita.colorrush.model.already_bid.AlreadyBidModel;
import app.bizita.colorrush.model.bank.add_new_bank.AddBankDetailsModel;
import app.bizita.colorrush.model.bank.all_bank_list.AllBankListsModel;
import app.bizita.colorrush.model.bank.get_single_bank_details.GetbankdetailModel;
import app.bizita.colorrush.model.bank.remove_bank.RemoveBankModel;
import app.bizita.colorrush.model.bank.set_primary_bank.SetPrimaryAccountModel;
import app.bizita.colorrush.model.bank.update_bank.UpdateBankImageModel;
import app.bizita.colorrush.model.bank.update_single_bank_details.UpdatebankdetailModel;
import app.bizita.colorrush.model.buy_money.BuyMoneyModel;
import app.bizita.colorrush.model.dashboard.DashboardModel;
import app.bizita.colorrush.model.game_start.GameStartModel;
import app.bizita.colorrush.model.login.LoginModel;
import app.bizita.colorrush.model.particular_bid.CancelBidsModel;
import app.bizita.colorrush.model.particular_bid.ParticularBidsModel;
import app.bizita.colorrush.model.profile.ProfileModel;
import app.bizita.colorrush.model.rebid.ReBidModel;
import app.bizita.colorrush.model.results.ResultsModel;
import app.bizita.colorrush.model.transcation_history.TranscationHistroyModel;
import app.bizita.colorrush.model.update_profile.UpdateProfileModel;
import app.bizita.colorrush.model.withdrawal.CurrentWalletAndWithdrawalModel;
import app.bizita.colorrush.model.withdrawal.WithdrawalRequestModel;
import app.bizita.colorrush.model.withdrawal.cancel.CancelWithdrawalModel;
import app.bizita.colorrush.model.withdrawal.list.WithdrawalListModel;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;


/**
 * Created by Karan Brahmaxatriya on 20-Sept-18.
 */
public class RetrofitHelper {
    public static OkHttpClient okHttpClient;
    public static Retrofit retrofit, retrofitMatchScore;
    public static CookieManager cookieManager;
    private Context mContext;

    public RetrofitHelper(Context context) {
        mContext = context;
    }

    public static OkHttpClient getOkHttpClientInstance() {
        if (okHttpClient != null) {
            return okHttpClient;
        }


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(12000, TimeUnit.SECONDS);
        httpClient.readTimeout(2000, TimeUnit.SECONDS);
        httpClient.writeTimeout(2000, TimeUnit.SECONDS);
//        httpClient.addInterceptor(new NetworkConnectionInterceptor(BaseClass.getContext()));
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
               /* if (!Connectivity.isConnected(BaseClass.getContext())) {
                    throw new NoConnectivityException();
                }*/
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder(); // Add Device Detail

                Request request = requestBuilder.build();
                Response response = chain.proceed(request);
                String requestedHost = request.url().host();
                assert response.networkResponse() != null;
                String responseHost = response.networkResponse().request().url().host();
                if (!requestedHost.equalsIgnoreCase(responseHost)) {
                    throw new NoConnectivityException();
                }


                return response;
            }
        });
        httpClient.addInterceptor(logging);

        return httpClient.build();
    }

    public static <S> S createService(Class<S> serviceClass) {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .serializeNulls()
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConstants.CURRENT_REST_URL)
                    .client(getOkHttpClientInstance())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

        }
        return retrofit.create(serviceClass);
    }


    public interface Service {
        /*---------------------------------------GET METHOD------------------------------------------------------*/

        @GET("dashboard")
        Call<DashboardModel> DashboardModel(@Header("Authorization") String token);

        @GET("transaction_history")
        Call<TranscationHistroyModel> TranscationHistroyModel(@Header("Authorization") String token);

        @GET("withdrawal_points")
        Call<CurrentWalletAndWithdrawalModel> CurrentWalletAndWithdrawalModel(@Header("Authorization") String token);

        @FormUrlEncoded
        @POST("login")
        Call<LoginModel> LoginModel(@FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @POST("google")
        Call<LoginModel> google(@FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @POST("facebook")
        Call<LoginModel> facebook(@FieldMap HashMap<String, String> hashMap);

            @FormUrlEncoded
            @POST("register")
            Call<LoginModel> RegisterModel(@FieldMap HashMap<String, String> hashMap);

        @POST("submit_otp")
        Call<JsonObject> submit_otp(@Header("Authorization") String token);

        @GET("get_profile")
        Call<ProfileModel> ProfileModel(@Header("Authorization") String token);

        @Multipart
        @POST("update_profile")
        Call<UpdateProfileModel> UpdateProfileModel(@Header("Authorization") String authorization,
                                                    @PartMap Map<String, RequestBody> map);

        @Multipart
        @POST("update_pan_image")
        Call<UpdateProfileModel> update_pan_image(@Header("Authorization") String authorization,
                                                  @PartMap Map<String, RequestBody> map);

        @Multipart
        @POST("update_aadhar_image")
        Call<UpdateProfileModel> update_aadhar_image(@Header("Authorization") String authorization,
                                                     @PartMap Map<String, RequestBody> map);

        @Multipart
        @POST("update_profile_image")
        Call<UpdateProfileModel> update_profile_image(@Header("Authorization") String authorization,
                                                      @PartMap Map<String, RequestBody> map);

        @FormUrlEncoded
        @POST("game_start")
        Call<GameStartModel> GameStartModel(@Header("Authorization") String token, @FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @POST("change_password")
        Call<JsonObject> ChangePassword(@Header("Authorization") String token, @FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @POST("buy_money")
        Call<BuyMoneyModel> BuyMoneyModel(@Header("Authorization") String token, @FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @POST("money_withdrawal")
        Call<WithdrawalRequestModel> money_withdrawal(@Header("Authorization") String token, @FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @POST("bank_lists")
        Call<AllBankListsModel> AllBankListsModel(@Header("Authorization") String token, @FieldMap HashMap<String, String> hashMap);


        @FormUrlEncoded
        @POST("remove_bank_detail")
        Call<RemoveBankModel> RemoveBankModel(@Header("Authorization") String token, @FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @POST("set_primary_bank")
        Call<SetPrimaryAccountModel> SetPrimaryAccountModel(@Header("Authorization") String token, @FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @POST("update_bank_image")
        Call<UpdateBankImageModel> UpdateBankImageModel(@Header("Authorization") String token, @FieldMap HashMap<String, String> hashMap);


        @FormUrlEncoded
        @POST("get_bank_details")
        Call<GetbankdetailModel> GetbankdetailModel(@Header("Authorization") String token, @FieldMap HashMap<String, String> hashMap);

        @Multipart
        @POST("update_bank_details")
        Call<UpdatebankdetailModel> UpdatebankdetailModel(@Header("Authorization") String authorization,
                                                          @PartMap Map<String, RequestBody> map);

        @Multipart
        @POST("add_bank_detail")
        Call<AddBankDetailsModel> AddBankDetailsModel(@Header("Authorization") String authorization,
                                                      @PartMap Map<String, RequestBody> map);


        @FormUrlEncoded
        @POST("current_withdrawal_list")
        Call<WithdrawalListModel> WithdrawalListModel(@Header("Authorization") String token, @FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @POST("cancel_withdrawal_request")
        Call<CancelWithdrawalModel> CancelWithdrawalModel(@Header("Authorization") String token, @FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @POST("all_bids")
        Call<AlreadyBidModel> AlreadyBidModel(@Header("Authorization") String token, @FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @POST("particular_bids")
        Call<ParticularBidsModel> ParticularBidsModel(@Header("Authorization") String token, @FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @POST("cancel_bid")
        Call<CancelBidsModel> CancelBidsModel(@Header("Authorization") String token, @FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @POST("result")
        Call<ResultsModel> ResultsModel(@Header("Authorization") String token, @FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @POST("update_bid")
        Call<ReBidModel> ReBidModel(@Header("Authorization") String token, @FieldMap HashMap<String, String> hashMap);


    }


    public static ArrayList<KeyValueModel> getKeyValueInputData(LinkedHashMap<String, String> hm) {
        ArrayList<KeyValueModel> modelList = new ArrayList<>();
        for (String key : hm.keySet()) {
            KeyValueModel obj = new KeyValueModel();
            obj.setKey(key);
            obj.setValue(hm.get(key));
            modelList.add(obj);
        }
        return modelList;
    }


}