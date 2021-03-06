package app.bizita.colorrush.ui.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import com.karan_brahmaxatriya.kbnetwork.KBNetworkCheck;
import com.karan_brahmaxatriya.kbnetwork.OnChangeConnectivityListener;

import org.json.JSONObject;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import app.bizita.colorrush.R;
import app.bizita.colorrush.api.RetrofitHelper;
import app.bizita.colorrush.model.bank.add_new_bank.AddBankDetailsModel;
import app.bizita.colorrush.model.bank.update_single_bank_details.UpdatebankdetailModel;
import app.bizita.colorrush.model.login.LoginModel;
import app.bizita.colorrush.ui.DashboardActivity;
import app.bizita.colorrush.ui.ui.cancel_withdrawalamt.CancelWithdrawalAmtFragment;
import app.bizita.colorrush.utils.BaseClass;
import app.bizita.colorrush.utils.PrefUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission_group.CAMERA;
import static android.os.Build.VERSION_CODES.M;

public class AddNewBankAccountActivity extends AppCompatActivity {
    LoginModel loginModel;
    String token;
    private ProgressBar progress_bar, progress_bar1;
//    private View parent_view;
    private FloatingActionButton fab;
    NestedScrollView nested_content;
    TextInputEditText edbank_name, edbank_account_number, edbank_re_account_number, edbank_branch_name, edbank_ifsc_code, edAccountHolderName;
    ImageView imageViewCancelCheque, imageViewVehiclePhotoDummy;
    public static final int PERMISSION_REQUEST_CODE = 1111;
    private static final int REQUEST = 1337;
    private static final int IMAGE_REQUEST = 1;
    public static int SELECT_FROM_GALLERY = 2;
    public static int CAMERA_PIC_REQUEST = 0;
    private File photoFile = null;
    File profileImage = null;
    SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_add_new_bank_account);
        loginModel = PrefUtils.getUser(this);
        token = loginModel.getData().getToken();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add New Bank Details");
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
        fab = (FloatingActionButton) findViewById(R.id.fab);
        edbank_name = findViewById(R.id.edbank_name);
        edbank_account_number = findViewById(R.id.edbank_account_number);
        edbank_re_account_number = findViewById(R.id.edbank_re_account_number);
        edbank_branch_name = findViewById(R.id.edbank_branch_name);
        edbank_ifsc_code = findViewById(R.id.edbank_ifsc_code);
        nested_content = findViewById(R.id.nested_content);
        edAccountHolderName = findViewById(R.id.edAccountHolderName);
        imageViewCancelCheque = findViewById(R.id.imageViewCancelCheque);
        imageViewVehiclePhotoDummy = findViewById(R.id.imageViewVehiclePhotoDummy);
//        nested_content.setVisibility(View.GONE);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        progress_bar1 = (ProgressBar) findViewById(R.id.progress_bar1);
        sweetAlertDialog = new SweetAlertDialog(AddNewBankAccountActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);

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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                UpdateProfile();
            }
        });
        imageViewVehiclePhotoDummy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                selectImageProfile();
            }
        });
    }

    public void UpdateProfile() {


        final String fname = edbank_name.getText().toString().trim();
        final String mobile = edbank_account_number.getText().toString().trim();
        final String rebank = edbank_re_account_number.getText().toString().trim();
        final String email = edbank_branch_name.getText().toString().trim();
        final String adhar = edbank_ifsc_code.getText().toString().trim();
        final String holdername = edAccountHolderName.getText().toString().trim();


        if (fname.isEmpty()) {
//            Snackbar.make(parent_view, "Bank Name Required", Snackbar.LENGTH_SHORT).show();
            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(AddNewBankAccountActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
            sweetAlertDialoga.setTitleText( "Bank Name Required");
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
            edbank_name.requestFocus();
            return;
        }
        if (holdername.isEmpty()) {

            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(AddNewBankAccountActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
            sweetAlertDialoga.setTitleText( "Account Holder Name Required");
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
            edAccountHolderName.requestFocus();
            return;
        }
        else if (mobile.isEmpty()) {

            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(AddNewBankAccountActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
            sweetAlertDialoga.setTitleText( "Bank Account Number Required");
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
            edbank_account_number.requestFocus();
            return;
        }
        else if (rebank.isEmpty()) {

            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(AddNewBankAccountActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
            sweetAlertDialoga.setTitleText( "Re Enter Bank Account Number Required");
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
            edbank_account_number.requestFocus();
            return;
        }
        else if (!rebank.matches(mobile)) {

            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(AddNewBankAccountActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
            sweetAlertDialoga.setTitleText( "Bank Account Number Not Match");
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
            edbank_account_number.requestFocus();
            return;
        }
        else if (email.isEmpty()) {

            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(AddNewBankAccountActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
            sweetAlertDialoga.setTitleText( "Bank Branch Name Required");
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
            edbank_branch_name.requestFocus();
            return;
        } else if (adhar.isEmpty()) {

            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(AddNewBankAccountActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
            sweetAlertDialoga.setTitleText( "Bank IFSC Code Required");
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
            edbank_ifsc_code.requestFocus();
            return;
        } else {
            progress_bar.setVisibility(View.VISIBLE);
            fab.setAlpha(0f);

            Map<String, RequestBody> hashMap = new HashMap<>();


            RequestBody bank_name = RequestBody.create(MediaType.parse("text/plain"), fname + "");
            RequestBody bank_account_number = RequestBody.create(MediaType.parse("text/plain"), mobile + "");
            RequestBody bank_branch_name = RequestBody.create(MediaType.parse("text/plain"), email + "");
            RequestBody bank_ifsc_code = RequestBody.create(MediaType.parse("text/plain"), adhar + "");
            RequestBody account_holder_name = RequestBody.create(MediaType.parse("text/plain"), holdername + "");
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");


            hashMap.put("account_holder_name", account_holder_name);
            hashMap.put("bank_name", bank_name);
            hashMap.put("bank_account_no", bank_account_number);
            hashMap.put("bank_branch", bank_branch_name);
            hashMap.put("bank_ifsc", bank_ifsc_code);


            RequestBody thumbnailimage3 = null;
            try {
//                assert file2 != null;
//                assert fileImage2 != null;
                thumbnailimage3 = RequestBody.create(MediaType.parse("*/*"), profileImage);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (profileImage != null) {
                hashMap.put("cancel_image\";  filename=\"" + profileImage.getName() + "\"", thumbnailimage3);
            } else {

                hashMap.put("cancel_image", attachmentEmpty);
            }


            Log.e("Params", hashMap + "");

            Call<AddBankDetailsModel> registerModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).AddBankDetailsModel("Bearer " + token, hashMap);
            registerModelCall.enqueue(new Callback<AddBankDetailsModel>() {

                @Override
                public void onResponse(@NonNull Call<AddBankDetailsModel> call, @NonNull Response<AddBankDetailsModel> response) {
                    AddBankDetailsModel object = response.body();
                    if (response.isSuccessful()) {
                        progress_bar.setVisibility(View.GONE);
                        fab.setAlpha(1f);
                        Log.e("TAG", "Register_Response : " + new Gson().toJson(response.body()));
//                        Snackbar.make(parent_view, object.getMessage() + "", Snackbar.LENGTH_SHORT).show();

                        SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(AddNewBankAccountActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                        sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
                        sweetAlertDialoga.setTitleText(object.getMessage() + "");
                        sweetAlertDialoga.setConfirmText("Okay")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        // reuse previous dialog instance
                                        sDialog.dismissWithAnimation();
                                        edbank_name.setText("");
                                        edbank_account_number.setText("");
                                        edbank_branch_name.setText("");
                                        edbank_ifsc_code.setText("");
                                        edAccountHolderName.setText("");
                                        profileImage = null;
                                        imageViewCancelCheque.setImageDrawable(null);
                                        finish();

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
                                    edbank_name.setText("");
                                    edbank_account_number.setText("");
                                    edbank_branch_name.setText("");
                                    edbank_ifsc_code.setText("");
                                    edAccountHolderName.setText("");
                                    profileImage = null;
                                    imageViewCancelCheque.setImageDrawable(null);
                                    finish();

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
                        fab.setAlpha(1f);
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
//                            Snackbar.make(parent_view, jObjError.getString("errors") + "", Snackbar.LENGTH_SHORT).show();
                            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(AddNewBankAccountActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
                            sweetAlertDialoga.setTitleText(jObjError.getString("errors") + "");
                            sweetAlertDialoga.setConfirmText("Okay")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            // reuse previous dialog instance
                                            sDialog.dismissWithAnimation();

                                            finish();

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
                                        finish();

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
//                                Toast.makeText(EditProfileActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AddBankDetailsModel> call, @NonNull Throwable t) {
                    progress_bar.setVisibility(View.GONE);
                    fab.setAlpha(1f);
                    t.printStackTrace();
//                    Snackbar.make(parent_view, t.getMessage() + "", Snackbar.LENGTH_SHORT).show();
                    SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(AddNewBankAccountActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                    sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
                    sweetAlertDialoga.setTitleText(t.getMessage() + "");
                    sweetAlertDialoga.setConfirmText("Okay")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    // reuse previous dialog instance
                                    sDialog.dismissWithAnimation();

                                    finish();

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
                                finish();

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
                    Log.e("Register_Response", t.getMessage() + "");
                }
            });
        }


    }

    public boolean isNetworkAvailable() {
        return BaseClass.isNetworkAvailable(AddNewBankAccountActivity.this);
    }

    private void selectImageProfile() {
        final CharSequence[] options = {"From Camera", "From Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewBankAccountActivity.this);
        builder.setTitle("Please choose an Image");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("From Camera")) {
                    if (Build.VERSION.SDK_INT >= M) {
                        if (checkCameraPermission())
                            cameraIntentProfile();
                        else
                            requestPermission();
                    } else
                        cameraIntentProfile();
                } else if (options[item].equals("From Gallery")) {
                    if (Build.VERSION.SDK_INT >= M) {
                        if (checkGalleryPermission())
                            galleryIntentProfile();
                        else
                            requestGalleryPermission();
                    } else
                        galleryIntentProfile();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.create().show();
    }

    private void galleryIntentProfile() {
        Intent intent = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_FROM_GALLERY);
    }

    private void cameraIntentProfile() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), "app.luxahaxa.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_PIC_REQUEST);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                "image",  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
//        currentPhotoPath = image.getAbsolutePath();
        profileImage = new File(image.getAbsolutePath());
        return image;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(AddNewBankAccountActivity.this, new String[]{CAMERA}, PERMISSION_REQUEST_CODE);
    }

    private void requestGalleryPermission() {
        ActivityCompat.requestPermissions(AddNewBankAccountActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST);
    }

    private boolean checkCameraPermission() {
        int result1 = ContextCompat.checkSelfPermission(AddNewBankAccountActivity.this, Manifest.permission.CAMERA);
        return result1 == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkGalleryPermission() {
        int result2 = ContextCompat.checkSelfPermission(AddNewBankAccountActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result2 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        if (requestCode == CAMERA_PIC_REQUEST && photoFile != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            if (null != bitmap) {
                imageViewCancelCheque.setImageBitmap(bitmap);
                profileImage = getUserImageFile(bitmap);

            }

        } else if (requestCode == SELECT_FROM_GALLERY && resultCode == Activity.RESULT_OK && null != data) {
            Uri galleryURI = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), galleryURI);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (null != bitmap) {
                imageViewCancelCheque.setImageBitmap(bitmap);
                profileImage = getUserImageFile(bitmap);

            }
        }

    }

    private File getUserImageFile(Bitmap bitmap) {
        try {
            File f = new File(getCacheDir(), ".jpg");
            f.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return f;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}