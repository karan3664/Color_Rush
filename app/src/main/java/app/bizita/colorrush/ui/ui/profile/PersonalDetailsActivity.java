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
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.karan_brahmaxatriya.kbnetwork.KBNetworkCheck;
import com.karan_brahmaxatriya.kbnetwork.OnChangeConnectivityListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import app.bizita.colorrush.R;
import app.bizita.colorrush.api.BuildConstants;
import app.bizita.colorrush.api.RetrofitHelper;
import app.bizita.colorrush.model.login.LoginModel;
import app.bizita.colorrush.model.profile.ProfileModel;
import app.bizita.colorrush.model.update_profile.UpdateProfileModel;
import app.bizita.colorrush.ui.DashboardActivity;
import app.bizita.colorrush.ui.ui.cancel_withdrawalamt.CancelWithdrawalAmtFragment;
import app.bizita.colorrush.utils.PrefUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission_group.CAMERA;
import static android.os.Build.VERSION_CODES.M;

public class PersonalDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputEditText edName, edEmail, edMobile, edAadharNo, edPanNo;
    LoginModel loginModel;
    String token;
    CircularImageView civ_ProfileImage;
    private ProgressBar progress_bar, progress_bar1;
    NestedScrollView nested_content;
    //    private View parent_view;
    ImageView imageViewDrivingLicenseDummy, imageViewDrivingLicense, imageViewVehiclePhotoDummy, imageViewAadharPhoto;
    private FloatingActionButton fab;


    File filePath = null;
    String PDFfileName = "";
    File fileImage1 = null;
    File fileImage2 = null;
    File fileImage3 = null;
    public static final int PERMISSION_REQUEST_CODE = 1111;
    private static final int REQUEST = 1337;
    private static final String IMAGE_DIRECTORY = "/LuxaHaxa";
    private static final int BUFFER_SIZE = 1024 * 2;
    public static int CAMERA_PIC_REQUEST = 0;
    private int GALLERY1 = 1, GALLERY2 = 2, GALLERY3 = 3, CAMERAs4 = 4, CAMERAs5 = 5, CAMERAs6 = 6;
    private int PICK_PDF_REQUEST = 3;
    private File photoFile4 = null;
    private File photoFile5 = null;
    private File photoFile6 = null;
    SweetAlertDialog sweetAlertDialog;

    public static String getFilePathFromURI(Context context, Uri contentUri) {
        //copy file and send new file path
        String fileName = getFileName(contentUri);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = new File(wallpaperDirectory + File.separator + fileName);
            // create folder if not exists

            copy(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }

    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            copystream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int copystream(InputStream input, OutputStream output) throws Exception, IOException {
        byte[] buffer = new byte[BUFFER_SIZE];

        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        BufferedOutputStream out = new BufferedOutputStream(output, BUFFER_SIZE);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                Log.e(e.getMessage(), String.valueOf(e));
            }
            try {
                in.close();
            } catch (IOException e) {
                Log.e(e.getMessage(), String.valueOf(e));
            }
        }
        return count;
    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(PersonalDetailsActivity.this)
                .withPermissions(

                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
//                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(PersonalDetailsActivity.this, "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showPictureDialog1() {
        final CharSequence[] options = {"From Camera", "From Gallery", "Cancel"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(PersonalDetailsActivity.this);
        builder.setTitle("Please choose an Image");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("From Camera")) {
                    if (Build.VERSION.SDK_INT >= M) {
                        if (checkCameraPermission())
                            cameraIntent4();
                        else
                            requestPermission();
                    } else
                        cameraIntent4();
                } else if (options[item].equals("From Gallery")) {
                    if (Build.VERSION.SDK_INT >= M) {
                        galleryIntent1();
                    } else
                        galleryIntent1();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.create().show();
    }

    private void showPictureDialog2() {
        final CharSequence[] options = {"From Camera", "From Gallery", "Cancel"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(PersonalDetailsActivity.this);
        builder.setTitle("Please choose an Image");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("From Camera")) {
                    if (Build.VERSION.SDK_INT >= M) {
                        if (checkCameraPermission())
                            cameraIntent5();
                        else
                            requestPermission();
                    } else
                        cameraIntent5();
                } else if (options[item].equals("From Gallery")) {
                    if (Build.VERSION.SDK_INT >= M) {
                        galleryIntent2();
                    } else
                        galleryIntent2();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.create().show();
    }

    private void showPictureDialog3() {
        final CharSequence[] options = {"From Camera", "From Gallery", "Cancel"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(PersonalDetailsActivity.this);
        builder.setTitle("Please choose an Image");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("From Camera")) {
                    if (Build.VERSION.SDK_INT >= M) {
                        if (checkCameraPermission())
                            cameraIntent6();
                        else
                            requestPermission();
                    } else
                        cameraIntent6();
                } else if (options[item].equals("From Gallery")) {
                    if (Build.VERSION.SDK_INT >= M) {
                        galleryIntent3();
                    } else
                        galleryIntent3();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.create().show();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(PersonalDetailsActivity.this, new String[]{CAMERA}, PERMISSION_REQUEST_CODE);
    }

    public void galleryIntent1() {
        Intent intent = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY1);
    }

    public void galleryIntent2() {
        Intent intent = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY2);
    }

    public void galleryIntent3() {
        Intent intent = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY3);
    }

    private File createImageFile1() throws IOException {
        // Create an image file name
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                "image",  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
//        currentPhotoPath = image.getAbsolutePath();
        fileImage1 = new File(image.getAbsolutePath());
        return image;
    }

    private File createImageFile2() throws IOException {
        // Create an image file name
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                "image",  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
//        currentPhotoPath = image.getAbsolutePath();
        fileImage2 = new File(image.getAbsolutePath());
        return image;
    }

    private File createImageFile3() throws IOException {
        // Create an image file name
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                "image",  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
//        currentPhotoPath = image.getAbsolutePath();
        fileImage3 = new File(image.getAbsolutePath());
        return image;
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void cameraIntent4() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(Objects.requireNonNull(PersonalDetailsActivity.this).getPackageManager()) != null) {
            try {
                photoFile4 = createImageFile1();
            } catch (IOException ex) {
            }
            if (photoFile4 != null) {
//                Uri photoURI = FileProvider.getUriForFile(Objects.requireNonNull(getActivity()),
//                        BuildConfig.APPLICATION_ID + ".provider", photoFile);
                Uri photoURI = FileProvider.getUriForFile(PersonalDetailsActivity.this, "app.luxahaxa.fileprovider", photoFile4);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERAs4);
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void cameraIntent5() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(Objects.requireNonNull(PersonalDetailsActivity.this).getPackageManager()) != null) {
            try {
                photoFile5 = createImageFile2();
            } catch (IOException ex) {
            }
            if (photoFile5 != null) {
//                Uri photoURI = FileProvider.getUriForFile(Objects.requireNonNull(getActivity()),
//                        BuildConfig.APPLICATION_ID + ".provider", photoFile);
                Uri photoURI = FileProvider.getUriForFile(PersonalDetailsActivity.this, "app.luxahaxa.fileprovider", photoFile5);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERAs5);
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void cameraIntent6() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(Objects.requireNonNull(PersonalDetailsActivity.this).getPackageManager()) != null) {
            try {
                photoFile6 = createImageFile3();
            } catch (IOException ex) {
            }
            if (photoFile6 != null) {
//                Uri photoURI = FileProvider.getUriForFile(Objects.requireNonNull(getActivity()),
//                        BuildConfig.APPLICATION_ID + ".provider", photoFile);
                Uri photoURI = FileProvider.getUriForFile(PersonalDetailsActivity.this, "app.luxahaxa.fileprovider", photoFile6);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERAs6);
            }
        }
    }

    private boolean checkCameraPermission() {
        int result1 = ContextCompat.checkSelfPermission(PersonalDetailsActivity.this, Manifest.permission.CAMERA);
        return result1 == PackageManager.PERMISSION_GRANTED;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("result", "" + resultCode);
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
        if (resultCode == RESULT_CANCELED) {
//            Log.d("what","cancle");
            return;
        }
        if (requestCode == GALLERY1) {

            if (data != null) {
                Uri galleryURI = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), galleryURI);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (null != bitmap) {

                    civ_ProfileImage.setImageBitmap(bitmap);
//                final File userImageFile = getUserImageFile(bitmap);
                    fileImage1 = getUserImageFile(bitmap);
                    UploadBook1();
               /* if (null != userImageFile) {
                    fileImage = userImageFile;
                    imageUri = Uri.fromFile(userImageFile);
                    //call presetner of manager for api it always required file
                    // new ProfilePresenter(context, this).callImageUploadApi(userImageFile);
//                    ((DashboardActivity) getActivity()).alertDialog("Click ok to upload image", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            uploadFile("pic", userImageFile);
//                        }
//                    });
                }*/
                }


//                mediaPath = cursor.getString(columnIndex);


//                videoImage.start();


            }

        } else if (requestCode == CAMERAs4) {
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile4.getAbsolutePath());
            if (null != bitmap) {

                civ_ProfileImage.setImageBitmap(bitmap);
//                final File userImageFile = getUserImageFile(bitmap);
                fileImage1 = getUserImageFile(bitmap);
                UploadBook1();
               /* if (null != userImageFile) {
                    fileImage = userImageFile;
                    imageUri = Uri.fromFile(userImageFile);
                    //call presetner of manager for api it always required file
                    // new ProfilePresenter(context, this).callImageUploadApi(userImageFile);
//                    ((DashboardActivity) getActivity()).alertDialog("Click ok to upload image", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            uploadFile("pic", userImageFile);
//                        }
//                    });
                }*/
            }

        }
        if (requestCode == GALLERY2) {

            if (data != null) {
                Uri galleryURI = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), galleryURI);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (null != bitmap) {

                    imageViewDrivingLicense.setImageBitmap(bitmap);
//                final File userImageFile = getUserImageFile(bitmap);
                    fileImage2 = getUserImageFile(bitmap);
                    UploadBook2();
               /* if (null != userImageFile) {
                    fileImage = userImageFile;
                    imageUri = Uri.fromFile(userImageFile);
                    //call presetner of manager for api it always required file
                    // new ProfilePresenter(context, this).callImageUploadApi(userImageFile);
//                    ((DashboardActivity) getActivity()).alertDialog("Click ok to upload image", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            uploadFile("pic", userImageFile);
//                        }
//                    });
                }*/
                }


//                mediaPath = cursor.getString(columnIndex);


//                videoImage.start();


            }

        } else if (requestCode == CAMERAs5) {
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile4.getAbsolutePath());
            if (null != bitmap) {

                imageViewDrivingLicense.setImageBitmap(bitmap);
//                final File userImageFile = getUserImageFile(bitmap);
                fileImage2 = getUserImageFile(bitmap);
                UploadBook2();
               /* if (null != userImageFile) {
                    fileImage = userImageFile;
                    imageUri = Uri.fromFile(userImageFile);
                    //call presetner of manager for api it always required file
                    // new ProfilePresenter(context, this).callImageUploadApi(userImageFile);
//                    ((DashboardActivity) getActivity()).alertDialog("Click ok to upload image", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            uploadFile("pic", userImageFile);
//                        }
//                    });
                }*/
            }

        }
        if (requestCode == GALLERY3) {

            if (data != null) {
                Uri galleryURI = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), galleryURI);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (null != bitmap) {

                    imageViewAadharPhoto.setImageBitmap(bitmap);
//                final File userImageFile = getUserImageFile(bitmap);
                    fileImage3 = getUserImageFile(bitmap);
                    UploadBook3();
               /* if (null != userImageFile) {
                    fileImage = userImageFile;
                    imageUri = Uri.fromFile(userImageFile);
                    //call presetner of manager for api it always required file
                    // new ProfilePresenter(context, this).callImageUploadApi(userImageFile);
//                    ((DashboardActivity) getActivity()).alertDialog("Click ok to upload image", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            uploadFile("pic", userImageFile);
//                        }
//                    });
                }*/
                }


//                mediaPath = cursor.getString(columnIndex);


//                videoImage.start();


            }

        } else if (requestCode == CAMERAs6) {
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile4.getAbsolutePath());
            if (null != bitmap) {

                imageViewAadharPhoto.setImageBitmap(bitmap);
//                final File userImageFile = getUserImageFile(bitmap);
                fileImage3 = getUserImageFile(bitmap);
                UploadBook3();
               /* if (null != userImageFile) {
                    fileImage = userImageFile;
                    imageUri = Uri.fromFile(userImageFile);
                    //call presetner of manager for api it always required file
                    // new ProfilePresenter(context, this).callImageUploadApi(userImageFile);
//                    ((DashboardActivity) getActivity()).alertDialog("Click ok to upload image", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            uploadFile("pic", userImageFile);
//                        }
//                    });
                }*/
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_personal_details);
        loginModel = PrefUtils.getUser(this);
        token = loginModel.getData().getToken();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Personal Details");
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
        nested_content = findViewById(R.id.nested_content);

        civ_ProfileImage = findViewById(R.id.civ_ProfileImage);
        imageViewDrivingLicenseDummy = findViewById(R.id.imageViewDrivingLicenseDummy);
        imageViewDrivingLicense = findViewById(R.id.imageViewDrivingLicense);
        imageViewVehiclePhotoDummy = findViewById(R.id.imageViewVehiclePhotoDummy);
        imageViewAadharPhoto = findViewById(R.id.imageViewAadharPhoto);

        imageViewDrivingLicenseDummy.setOnClickListener(this);
        imageViewVehiclePhotoDummy.setOnClickListener(this);
        civ_ProfileImage.setOnClickListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        edName = findViewById(R.id.edName);
        edEmail = findViewById(R.id.edEmail);
        edMobile = findViewById(R.id.edMobile);
        edAadharNo = findViewById(R.id.edAadharNo);
        edPanNo = findViewById(R.id.edPanNo);
        sweetAlertDialog = new SweetAlertDialog(PersonalDetailsActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);

        nested_content.setVisibility(View.GONE);
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
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                UpdateProfile();
            }
        });
        GetProfile();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.btn_register:
//                RegisterCall();
//                break;


            case R.id.imageViewDrivingLicenseDummy:
                showPictureDialog2();
                break;
            case R.id.imageViewVehiclePhotoDummy:
                showPictureDialog3();
                break;
            case R.id.civ_ProfileImage:
                showPictureDialog1();
                break;
        }
    }

    public void GetProfile() {
        progress_bar1.setVisibility(View.VISIBLE);
        Call<ProfileModel> loginModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).ProfileModel("Bearer " + token);
        loginModelCall.enqueue(new Callback<ProfileModel>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ProfileModel> call, @NonNull Response<ProfileModel> response) {
                ProfileModel object = response.body();


                if (response.isSuccessful()) {
                    Log.e("TAG", "Login_Response : " + new Gson().toJson(response.body()));
                    nested_content.setVisibility(View.VISIBLE);
                    progress_bar1.setVisibility(View.GONE);


                    if (object.getData().getUser().getName() != null) {
                        edName.setText(object.getData().getUser().getName() + "");
                    }
                    if (object.getData().getUser().getEmail() != null) {
                        edEmail.setText(object.getData().getUser().getEmail() + "");
                    }
                    if (object.getData().getUser().getMobile() != null) {
                        edMobile.setText(object.getData().getUser().getMobile() + "");
                    }
                    if (object.getData().getUser().getPanNumber() != null) {
                        edPanNo.setText(object.getData().getUser().getPanNumber() + "");
                    }
                    if (object.getData().getUser().getAadharNumber() != null) {
                        edAadharNo.setText(object.getData().getUser().getAadharNumber() + "");
                    }
                    if (object.getData().getUser().getProfileFile() != null) {
                        Glide.with(PersonalDetailsActivity.this)
                                .load(BuildConstants.Main_Image + object.getData().getUser().getProfileFile().toString().replace("public", "storage") + "")
                                .placeholder(R.drawable.ic_user)
//                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                .into(civ_ProfileImage);
                    }
                    if (object.getData().getUser().getPanFile() != null) {
                        Glide.with(PersonalDetailsActivity.this)
                                .load(BuildConstants.Main_Image + object.getData().getUser().getPanFile().toString().replace("public", "storage") + "")
//                                .placeholder(R.drawable.photo_male_2)
//                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                .into(imageViewDrivingLicense);
                    }
                    if (object.getData().getUser().getAadharFile() != null) {
                        Glide.with(PersonalDetailsActivity.this)
                                .load(BuildConstants.Main_Image + object.getData().getUser().getAadharFile().toString().replace("public", "storage") + "")
//                                .placeholder(R.drawable.photo_male_2)
//                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                .into(imageViewAadharPhoto);
                    }


                } else {
                    progress_bar1.setVisibility(View.GONE);
                    nested_content.setVisibility(View.GONE);
//                    fab.setAlpha(1f);
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(PersonalDetailsActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
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
                progress_bar1.setVisibility(View.GONE);
//                fab.setAlpha(1f);
                t.printStackTrace();
                Log.e("Login_Response", t.getMessage() + "");
            }
        });
    }


    public void UpdateProfile() {


        final String fname = edName.getText().toString().trim();
        final String mobile = edMobile.getText().toString().trim();
        final String email = edEmail.getText().toString().trim();
        final String adhar = edAadharNo.getText().toString().trim();
        final String pan = edPanNo.getText().toString().trim();


        if (fname.isEmpty()) {
            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(PersonalDetailsActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
            sweetAlertDialoga.setTitleText("Name Required");
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
            edName.requestFocus();
            return;
        } else if (mobile.isEmpty()) {
            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(PersonalDetailsActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
            sweetAlertDialoga.setTitleText("Mobile Required");
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
            edMobile.requestFocus();
            return;
        } else if (email.isEmpty()) {

            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(PersonalDetailsActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
            sweetAlertDialoga.setTitleText("Email Required");
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
            edEmail.requestFocus();
            return;
        } else if (adhar.isEmpty()) {

            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(PersonalDetailsActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
            sweetAlertDialoga.setTitleText("Aadhar Required");
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
            edAadharNo.requestFocus();
            return;
        } else if (pan.isEmpty()) {

            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(PersonalDetailsActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
            sweetAlertDialoga.setTitleText("Pan Required");
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
            edPanNo.requestFocus();
            return;
        } else {
            progress_bar.setVisibility(View.VISIBLE);
            fab.setAlpha(0f);


            Map<String, RequestBody> hashMap = new HashMap<>();


            RequestBody name1 = RequestBody.create(MediaType.parse("text/plain"), fname + "");
            RequestBody phone11 = RequestBody.create(MediaType.parse("text/plain"), mobile + "");
            RequestBody email1 = RequestBody.create(MediaType.parse("text/plain"), email + "");
            RequestBody adhar1 = RequestBody.create(MediaType.parse("text/plain"), adhar + "");
            RequestBody pan1 = RequestBody.create(MediaType.parse("text/plain"), pan + "");
            RequestBody bank_name = RequestBody.create(MediaType.parse("text/plain"), "");
            RequestBody bank_account_number = RequestBody.create(MediaType.parse("text/plain"), "");
            RequestBody bank_branch_name = RequestBody.create(MediaType.parse("text/plain"), "");
            RequestBody bank_ifsc_code = RequestBody.create(MediaType.parse("text/plain"), "");


            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            RequestBody attachmentEmpty2 = RequestBody.create(MediaType.parse("text/plain"), "");
            RequestBody attachmentEmpty3 = RequestBody.create(MediaType.parse("text/plain"), "");


            hashMap.put("name", name1);
            hashMap.put("mobile", phone11);
            hashMap.put("phone", phone11);
            hashMap.put("bank_name", bank_name);
            hashMap.put("bank_account_number", bank_account_number);
            hashMap.put("bank_branch_name", bank_branch_name);
            hashMap.put("bank_ifsc_code", bank_ifsc_code);
            hashMap.put("aadhar_number", adhar1);
            hashMap.put("pan_number", pan1);
            hashMap.put("pan_file", attachmentEmpty);
            hashMap.put("aadhar_file", attachmentEmpty2);
            hashMap.put("profile_file", attachmentEmpty3);


            Log.e("Params", hashMap + "");
//            Log.e("Params1", aadharImageFile.getAbsolutePath() + "");
//            Log.e("Params2", fileImage.getAbsoluteFile() + "");
            Call<UpdateProfileModel> registerModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).UpdateProfileModel("Bearer " + token, hashMap);
            registerModelCall.enqueue(new Callback<UpdateProfileModel>() {

                @Override
                public void onResponse(@NonNull Call<UpdateProfileModel> call, @NonNull Response<UpdateProfileModel> response) {
                    UpdateProfileModel object = response.body();
                    if (response.isSuccessful()) {
                        progress_bar.setVisibility(View.GONE);
                        fab.setAlpha(1f);
                        Log.e("TAG", "Register_Response : " + new Gson().toJson(response.body()));
                        SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(PersonalDetailsActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                        sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
                        sweetAlertDialoga.setTitleText(object.getMessage() + "");
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
                        GetProfile();
                    } else {
                        progress_bar.setVisibility(View.GONE);
                        fab.setAlpha(1f);
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());

                            SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(PersonalDetailsActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                            sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
                            sweetAlertDialoga.setTitleText(jObjError.getString("errors") + "");
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
                public void onFailure(@NonNull Call<UpdateProfileModel> call, @NonNull Throwable t) {
                    progress_bar.setVisibility(View.GONE);
                    fab.setAlpha(1f);
                    t.printStackTrace();
                    Log.e("Register_Response", t.getMessage() + "");
                }
            });
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

    public void UploadBook1() {


        Map<String, RequestBody> hashMap = new HashMap<>();

        RequestBody thumbnailimage = null;

        try {
//                assert file != null;
//                assert fileImage != null;
            thumbnailimage = RequestBody.create(MediaType.parse("*/*"), fileImage1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Parsing any Media type file


        RequestBody pdf = RequestBody.create(MediaType.parse("text/plain"), "");


        if (fileImage1 != null) {
            hashMap.put("profile_file\"; filename=\"" + fileImage1.getName() + "\"", thumbnailimage);
        } else {
            hashMap.put("profile_file", pdf);
        }

        Log.e("Params", hashMap + "");
//            Log.e("Params1", aadharImageFile.getAbsolutePath() + "");
//            Log.e("Params2", fileImage.getAbsoluteFile() + "");
        Call<UpdateProfileModel> registerModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).update_profile_image("Bearer " + token, hashMap);
        registerModelCall.enqueue(new Callback<UpdateProfileModel>() {

            @Override
            public void onResponse(@NonNull Call<UpdateProfileModel> call, @NonNull Response<UpdateProfileModel> response) {
                UpdateProfileModel object = response.body();
                if (response.isSuccessful()) {
                    progress_bar.setVisibility(View.GONE);
                    fab.setAlpha(1f);
                    Log.e("TAG", "Register_Response : " + new Gson().toJson(response.body()));
//                    Snackbar.make(parent_view, object.getMessage() + "", Snackbar.LENGTH_SHORT).show();
//                    GetProfile();
                } else {
                    progress_bar.setVisibility(View.GONE);
                    fab.setAlpha(1f);
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(PersonalDetailsActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                        sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
                        sweetAlertDialoga.setTitleText(jObjError.getString("errors") + "");
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
            public void onFailure(@NonNull Call<UpdateProfileModel> call, @NonNull Throwable t) {
                progress_bar.setVisibility(View.GONE);
                fab.setAlpha(1f);
                t.printStackTrace();
                Log.e("Register_Response", t.getMessage() + "");
            }
        });


    }

    public void UploadBook2() {


        Map<String, RequestBody> hashMap = new HashMap<>();

        RequestBody thumbnailimage = null;

        try {
//                assert file != null;
//                assert fileImage != null;
            thumbnailimage = RequestBody.create(MediaType.parse("*/*"), fileImage2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Parsing any Media type file


        RequestBody pdf = RequestBody.create(MediaType.parse("text/plain"), "");


        if (fileImage2 != null) {
            hashMap.put("pan_file\"; filename=\"" + fileImage2.getName() + "\"", thumbnailimage);
        } else {
            hashMap.put("pan_file", pdf);
        }

        Log.e("Params", hashMap + "");
//            Log.e("Params1", aadharImageFile.getAbsolutePath() + "");
//            Log.e("Params2", fileImage.getAbsoluteFile() + "");
        Call<UpdateProfileModel> registerModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).update_pan_image("Bearer " + token, hashMap);
        registerModelCall.enqueue(new Callback<UpdateProfileModel>() {

            @Override
            public void onResponse(@NonNull Call<UpdateProfileModel> call, @NonNull Response<UpdateProfileModel> response) {
                UpdateProfileModel object = response.body();
                if (response.isSuccessful()) {
                    progress_bar.setVisibility(View.GONE);
                    fab.setAlpha(1f);
                    Log.e("TAG", "Register_Response : " + new Gson().toJson(response.body()));
//                    Snackbar.make(parent_view, object.getMessage() + "", Snackbar.LENGTH_SHORT).show();
//                    GetProfile();
                } else {
                    progress_bar.setVisibility(View.GONE);
                    fab.setAlpha(1f);
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(PersonalDetailsActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                        sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
                        sweetAlertDialoga.setTitleText(jObjError.getString("errors") + "");
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
//                                Toast.makeText(EditProfileActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<UpdateProfileModel> call, @NonNull Throwable t) {
                progress_bar.setVisibility(View.GONE);
                fab.setAlpha(1f);
                t.printStackTrace();
                Log.e("Register_Response", t.getMessage() + "");
            }
        });


    }

    public void UploadBook3() {


        Map<String, RequestBody> hashMap = new HashMap<>();

        RequestBody thumbnailimage = null;

        try {
//                assert file != null;
//                assert fileImage != null;
            thumbnailimage = RequestBody.create(MediaType.parse("*/*"), fileImage3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Parsing any Media type file


        RequestBody pdf = RequestBody.create(MediaType.parse("text/plain"), "");


        if (fileImage3 != null) {
            hashMap.put("aadhar_file\"; filename=\"" + fileImage3.getName() + "\"", thumbnailimage);
        } else {
            hashMap.put("aadhar_file", pdf);
        }

        Log.e("Params", hashMap + "");
//            Log.e("Params1", aadharImageFile.getAbsolutePath() + "");
//            Log.e("Params2", fileImage.getAbsoluteFile() + "");
        Call<UpdateProfileModel> registerModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).update_aadhar_image("Bearer " + token, hashMap);
        registerModelCall.enqueue(new Callback<UpdateProfileModel>() {

            @Override
            public void onResponse(@NonNull Call<UpdateProfileModel> call, @NonNull Response<UpdateProfileModel> response) {
                UpdateProfileModel object = response.body();
                if (response.isSuccessful()) {
                    progress_bar.setVisibility(View.GONE);
                    fab.setAlpha(1f);
                    Log.e("TAG", "Register_Response : " + new Gson().toJson(response.body()));
//                    Snackbar.make(parent_view, object.getMessage() + "", Snackbar.LENGTH_SHORT).show();
//                    GetProfile();
                } else {
                    progress_bar.setVisibility(View.GONE);
                    fab.setAlpha(1f);
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        SweetAlertDialog sweetAlertDialoga = new SweetAlertDialog(PersonalDetailsActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                        sweetAlertDialoga.setCustomImage(R.drawable.bitcoint);
                        sweetAlertDialoga.setTitleText(jObjError.getString("errors") + "");
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
//                                Toast.makeText(EditProfileActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<UpdateProfileModel> call, @NonNull Throwable t) {
                progress_bar.setVisibility(View.GONE);
                fab.setAlpha(1f);
                t.printStackTrace();
                Log.e("Register_Response", t.getMessage() + "");
            }
        });


    }
}