package com.example.pjtfacerec;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.resolutionselector.AspectRatioStrategy;
import androidx.camera.core.resolutionselector.ResolutionSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.pjtfacerec.api.AuthResponse;
import com.example.pjtfacerec.api.AuthRetrofitClient;
import com.example.pjtfacerec.api.SharedPreferencesService;
import com.example.pjtfacerec.api.SpringApiRetrofitClient;
import com.example.pjtfacerec.api.LoginRequestBody;
import com.example.pjtfacerec.api.SpringApiService;
import com.example.pjtfacerec.api.SpringAuthService;
import com.example.pjtfacerec.api.SpringResponse;
import com.example.pjtfacerec.databinding.ActivityMainBinding;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final int MULTIPLE_PERMISSION_ID = 14;
    private List<String> multiplePermissionNameList;
    private ImageCapture imageCapture;
    private ProcessCameraProvider cameraProvider;
    private SharedPreferencesService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.service = new SharedPreferencesService(MainActivity.this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Prepare permissions list
        multiplePermissionNameList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            multiplePermissionNameList.add(android.Manifest.permission.CAMERA);
        } else {
            multiplePermissionNameList.add(android.Manifest.permission.CAMERA);
            multiplePermissionNameList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            multiplePermissionNameList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (checkMultiplePermission()) {
            startCamera();
        }

        assert binding.captureIB != null;
        binding.captureIB.setOnClickListener(v -> {
            takePhoto();
        });
        Log.wtf("Logging in", "Logging in");
        makeLoginRequest();
        Log.wtf("Logging in", "Called login request");
    }

    private boolean checkMultiplePermission() {
        List<String> listPermissionNeeded = new ArrayList<>();
        for (String permission : multiplePermissionNameList) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(permission);
            }
        }
        if (!listPermissionNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    listPermissionNeeded.toArray(new String[0]),
                    MULTIPLE_PERMISSION_ID
            );
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MULTIPLE_PERMISSION_ID) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                startCamera();
            } else {
                boolean someDenied = false;
                for (String permission : permissions) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
                            && ContextCompat.checkSelfPermission(this, permission)
                            == PackageManager.PERMISSION_DENIED) {
                        someDenied = true;
                        break;
                    }
                }
                if (someDenied) {
                    Utils.appSettingOpen(this);
                } else {
                    Utils.warningPermissionDialog(this, (dialog, which) -> {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            checkMultiplePermission();
                        }
                    });
                }
            }
        }
    }

    private void startCamera() {
        ProcessCameraProvider.getInstance(this)
            .addListener(() -> {
                try {
                    cameraProvider = ProcessCameraProvider.getInstance(this).get();
                    bindCameraUserCases();
                } catch (Exception e) {
                    Log.e("Error starting Camera", Objects.requireNonNull(e.getMessage()));
                }
            }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUserCases() {
        assert binding.previewView != null;
        int rotation = binding.previewView.getDisplay().getRotation();

        int aspectRatio = AspectRatio.RATIO_16_9;
        ResolutionSelector resolutionSelector = new ResolutionSelector.Builder()
                .setAspectRatioStrategy(
                        new AspectRatioStrategy(
                                aspectRatio,
                                AspectRatioStrategy.FALLBACK_RULE_AUTO
                        )
                )
                .build();

        Preview preview = new Preview.Builder()
                .setResolutionSelector(resolutionSelector)
                .setTargetRotation(rotation)
                .build();
        preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setResolutionSelector(resolutionSelector)
                .setTargetRotation(rotation)
                .build();

        int lensFacing = CameraSelector.LENS_FACING_FRONT;
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build();

        OrientationEventListener orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                int targetRotation;
                if (orientation >= 45 && orientation < 135) {
                    targetRotation = Surface.ROTATION_270;
                } else if (orientation < 225) {
                    targetRotation = Surface.ROTATION_180;
                } else if (orientation < 315) {
                    targetRotation = Surface.ROTATION_90;
                } else {
                    targetRotation = Surface.ROTATION_0;
                }
                imageCapture.setTargetRotation(targetRotation);
            }
        };
        orientationEventListener.enable();

        try {
            cameraProvider.unbindAll();
            androidx.camera.core.Camera camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
            );
        } catch (Exception e) {
            Log.e("Error binding Camera", Objects.requireNonNull(e.getMessage()));
        }
    }

    private void takePhoto() {
        if (this.service.getToken() == null) {
            Toast.makeText(MainActivity.this, "This machine has not been authenticated", Toast.LENGTH_LONG).show();
            return;
        }

        File imageFolder = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Images"
        );

        if (!imageFolder.exists()) {
            imageFolder.mkdirs();
        }

        String fileName = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(System.currentTimeMillis()) + ".jpg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Images");

        ImageCapture.OutputFileOptions outputOptions;
        outputOptions = new ImageCapture.OutputFileOptions.Builder(
                getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
        ).setMetadata(new ImageCapture.Metadata())
                .build();

        imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        String msg = "Photo Capture Succeeded: " + outputFileResults.getSavedUri();
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();

                        File imageFile = new File(getFilePathFromUri(Objects.requireNonNull(outputFileResults.getSavedUri())));
                        uploadImage(imageFile);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("Error capturing Image", Objects.requireNonNull(exception.getMessage()));
                    }
                }
        );
    }

    private void uploadImage(File imageFile) {
        // Create RequestBody for image file
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("image/*"), imageFile);

        // Convert RequestBody to MultipartBody.Part
        MultipartBody.Part body = MultipartBody.Part.createFormData("facePhoto", imageFile.getName(), requestBody);

        // Set up Retrofit client and make the API call
        Retrofit retrofit = SpringApiRetrofitClient.getRetrofitInstance(MainActivity.this);
        SpringApiService service = retrofit.create(SpringApiService.class);

        Call<SpringResponse> call = service.identify(body);
        call.enqueue(new Callback<SpringResponse>() {
            @Override
            public void onResponse(@NonNull Call<SpringResponse> call, @NonNull Response<SpringResponse> response) {
                if (response.isSuccessful()) {
                    // Handle success (e.g., display the response data)
                    SpringResponse resp = response.body();

                    if (resp == null) {
                        Toast.makeText(MainActivity.this, "Process failed. Please report this to the security staff", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String label = String.format("Welcome %s", resp.getFullName());
                    int color = ContextCompat.getColor(MainActivity.this, R.color.success);

                    Log.wtf("PERSON IIIIIIS", label);

                    changeViewContent(color, label, false);
                    reverse();
                    return;
                }

                String message = "Face could not be identified";
                int code = response.code();

                if (code == 404)
                    message = "You don't have a ticket for this match";

                int color = Color.RED;
                Log.e("BODY", String.valueOf(response.body()));
                Log.e("CODE", String.valueOf(response.code()));

                changeViewContent(color, message, false);
                reverse();
            }

            @Override
            public void onFailure(@NonNull Call<SpringResponse> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "An unexpected error occurred", Toast.LENGTH_LONG).show();
                Log.e("API Request Failure", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private void changeViewContent(int color, String label, boolean reverse) {
        assert binding.materialCardView != null;
        binding.materialCardView.setStrokeColor(color);
        assert binding.captureIB != null;
        binding.captureIB.setImageTintList(ColorStateList.valueOf(color));
        assert binding.banner != null;
        binding.banner.setTextColor(color);
        if (reverse) {
            binding.banner.setText(R.string.welcome);
            return;
        }
        binding.banner.setText(label);
    }
    private void reverse() {
        new Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    public void run() {
                        changeViewContent(Color.WHITE, null, true);
                    }
                },
                6000);
    }

    private String getFilePathFromUri(Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};

        try (Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                filePath = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            Log.e("Error fetching Image URI", Objects.requireNonNull(e.getMessage()));
        }
        return filePath;
    }

    private void makeLoginRequest() {
        LoginRequestBody body = new LoginRequestBody();
        Retrofit client = AuthRetrofitClient.getInstance();
        SpringAuthService service = client.create(SpringAuthService.class);

        Call<AuthResponse> call = service.login(body);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    AuthResponse body = response.body();

                    if (body == null) {
                        Log.e("Auth Failure", "Failed to authenticate client");
                        return;
                    }

                    MainActivity.this.service.setToken(body.getJwt());
                    Log.d("Authentication Success", body.getJwt());
                    return;
                }
                Log.e("Auth Failure", "Failed to authenticate client");
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Unable to authenticate client", Toast.LENGTH_LONG).show();
                Log.e("Auth Failure", "Failed to authenticate client");
            }
        });
    }
}
