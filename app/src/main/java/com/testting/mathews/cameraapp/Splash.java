package com.testting.mathews.cameraapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

//TODO Add ZXING for QR Code Reader
//TODO ADD TOASTS for error messages
//TODO Ask for permission at start of app
public class Splash extends Activity {
    static final int CAM_REQUEST = 1;
    static final int MY_REQUEST_CODE = 1;
    Button button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        button1 = (Button) findViewById(R.id.button1);
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_REQUEST_CODE); //Need to explicitly ask for permission to write in Android 6.0 +


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        File fileName = getFile();
                        Uri photoURI = FileProvider.getUriForFile(Splash.this,
                                BuildConfig.APPLICATION_ID + ".provider",
                                fileName);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        try {
                            startActivityForResult(cameraIntent, CAM_REQUEST);
                        } catch (Exception ex) {
                            if (hasPermissionInManifest(Splash.this, "android.permission.CAMERA")) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA},
                                        MY_REQUEST_CODE);
                                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    startActivityForResult(cameraIntent, CAM_REQUEST);
                                }
                            }

                        }
                    }
                }
            }

        });
    }

    private File getFile() {

        File folder = new File(Environment.getExternalStorageDirectory(),
                "Images2/");
        if (!folder.exists()) {
            if (!folder.mkdir()) {
                Log.e("TravellerLoog :: ", "Problem creating Image folder");
            }
        }
        Date myDate = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-yyyy-hh-mm-ss");
        String myString = dateFormatter.format(myDate);
        String nameOfImage = "Image_" + myString + ".jpg";
        File imageName = new File(folder, nameOfImage);

        return imageName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("End", "Finished");
    }

    public boolean hasPermissionInManifest(Context context, String permissionName) {
        final String packageName = context.getPackageName();
        try {
            final PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermissions = packageInfo.requestedPermissions;
            if (declaredPermissions != null && declaredPermissions.length > 0) {
                for (String p : declaredPermissions) {
                    if (p.equals(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("Error", "Permission Name not found");
        }
        return false;
    }


}
