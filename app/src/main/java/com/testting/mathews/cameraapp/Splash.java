package com.testting.mathews.cameraapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Binarizer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

//TODO Add ZXING for QR Code Reader
//TODO ADD TOASTS for error messages
//TODO Ask for permission at start of app

public class Splash extends Activity {
    static final int CAM_REQUEST = 2;
    static final int MY_REQUEST_CODE = 1;
    Button button1;
    Button evaluateButton;
    ImageView imageview1;
    TextView textView;
    static final int multiplePermissionFlag = 1;

    byte byteArray[];
 //   private final QRCodeReader qrCodeReader = new QRCodeReader();
 private final QRCodeReader qrFormatReader = new QRCodeReader();
    String nameOfImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        button1 = (Button) findViewById(R.id.button1);
        evaluateButton = (Button) findViewById(R.id.evaluateButton);
        imageview1 = (ImageView) findViewById(R.id.imageView2);
        textView = (TextView) findViewById(R.id.textView2);

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
        {requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_REQUEST_CODE);} //Need to explicitly ask for permission to write in Android 6.0 +

    }

    @Override
    protected void onResume() {
        super.onResume();
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
                            Log.e("CameraError:","Could not open Camera");
                                }
                            }

                        }
                    }
                });

        evaluateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    String readFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Images2/qrcode.jpg";// +"/Images2/"+ nameOfImage;//

                    //Create a byte stream
                    Bitmap bm = BitmapFactory.decodeFile(readFilePath);
                    //Show read Image
                    imageview1.setImageBitmap(bm);

                   /* ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byteArray = stream.toByteArray();*/
                    readQRCode(bm);
                }
            }
        });
    }

    private void readQRCode(Bitmap bm)
    {
        Result rawResult = null;
        int[] intArray = new int[bm.getWidth()*bm.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        bm.getPixels(intArray, 0, bm.getWidth(), 0, 0, bm.getWidth(), bm.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bm.getWidth(), bm.getHeight(), intArray);

        if (source != null) {
            BinaryBitmap bitmapFile = new BinaryBitmap(new HybridBinarizer(source));
            try {
                rawResult = qrFormatReader.decode(bitmapFile);
            } catch (ReaderException re) {
                // continue
                Log.e("ERROR:", "Could not read");
            } finally {
                qrFormatReader.reset();
            }
        }
        String qrContent;
        if (rawResult!=null){
            qrContent = rawResult.getText();
        }
        else{qrContent = "ReaderError";}

        textView.setText(qrContent);


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
        nameOfImage = "Image_" + myString + ".jpg";
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (multiplePermissionFlag == 1)
        {
            if (checkSelfPermission(Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED)
            {requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_REQUEST_CODE);}
        }
    }
}
