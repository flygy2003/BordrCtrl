package com.example.neekondev.quickscan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

import android.content.res.TypedArray;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    SurfaceView cameraView;
    TextView barcodeInfo;
    Intent toSettings;

    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;

    FirebaseDatabase database;
    DatabaseReference myRef;

    private static final int CAMERA_PERMISSION_CAMERA = 0x000000;
    Camera cam;
    Camera.Parameters p;

    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setRequestedOrientation(ActivityInfo
                .SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        if (ContextCompat.checkSelfPermission(MainActivity
                .this,
                Manifest
                        .permission
                        .CAMERA)
                != PackageManager
                .PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity
                    .this,
                    Manifest
                            .permission
                            .CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity
                        .this,
                        new String[]{Manifest.permission
                                .CAMERA},
                        CAMERA_PERMISSION_CAMERA);

                // CAMERA_PERMISSION_CAMERA is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        ImageButton reDO = (ImageButton)findViewById(R.id.reDO);
        reDO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reloader = new Intent(MainActivity
                        .this, MainActivity
                        .class);
                startActivity(reloader);
            }
        });

        cameraView = (SurfaceView) findViewById(R
                .id
                .camera_view);
        barcodeInfo = (TextView) findViewById(R
                .id
                .code_info);

        MainActivity
                .this
                .getPackageManager()
                .hasSystemFeature
                        (PackageManager
                                .FEATURE_CAMERA_FLASH);

        barcodeDetector =
                new BarcodeDetector.Builder(getApplicationContext())
                        .setBarcodeFormats(Barcode.ALL_FORMATS)
                        .build();

        cameraSource = new CameraSource
                .Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(25.0f)
                .setRequestedPreviewSize(2560, 1800)
                .setAutoFocusEnabled(true)
                .build();


        cameraView.getHolder().addCallback(new SurfaceHolder
                .Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        //      TODO: CONSIDER CALLING
                        //ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.


                        return;
                    }
                    cameraSource.start(cameraView
                            .getHolder());
                } catch (IOException ie) {
                    Log.e("CAMERA SOURCE", ie
                            .getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder,
                                       int format,
                                       int width,
                                       int height){}

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>()
        {
            @Override
            public void release() {}

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections)
            {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                database = FirebaseDatabase
                                .getInstance();
                myRef = database
                        .getReference()
                        .child(barcodes.valueAt(0).displayValue)
                        .child("timestamps");
                if (barcodes
                        .size() != 0) {
                    // Cooc Cac Lol and your girl Vivica does too!
                    barcodeInfo
                            .post(new Runnable() {// Use the post method of the TextView
                        public void run() {
                            barcodeInfo.setText(barcodes
                                    .valueAt(0)
                                    .displayValue
                            );
                            // Lock your laptop you idiot <3

                            myRef.child(getTime()).setValue(index);
                            index++;
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            toSettings = new Intent(getApplicationContext(), settings.class);
            startActivity(toSettings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //###########################################
    //###################################################################
    /*###########################################
    public void FLASH_ON()
    {
        cam = Camera.open();
        p = cam.getParameters();
        p.setFlashMode(Camera
                .Parameters
                .FLASH_MODE_TORCH);
        cam.setParameters(p);
        cam.startPreview();
    }

    public void FLASH_OFF()
    {
        cam.stopPreview();
        cam.release();
    }

    public void setupButtons() {
        ImageButton flash = (ImageButton) findViewById(R.id.flash);
        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        position = position + 1;
                        if (((position % 2) == 0) && (position == 0)) {
                            //even and equals zero
                            //Turn Light OFF
                            FLASH_OFF();

                        }
                        if (((position % 2) == 0) && (position != 0)) {
                            //even & more than zero
                            //Turn Light OFF
                            FLASH_OFF();

                        } else {
                            //All odd numbers
                            //Turn Light ON
                            FLASH_ON();
                        }
                    }
                };
                Thread mThread = new Thread(r);
                mThread.start();
            }
        });
    }
    //###################################################################*/
    public String getTime()
    {
        String downToSeconds = DateFormat
                .getDateTimeInstance()
                .format(
                        new Date());
        return downToSeconds;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0]
                        == PackageManager
                        .PERMISSION_GRANTED) {

                    Intent startMain = new Intent(MainActivity
                            .this, MainActivity
                            .class);
                    startActivity(startMain);

                } else {
                    if (ContextCompat.checkSelfPermission(MainActivity
                                    .this,
                            Manifest
                                    .permission
                                    .CAMERA)
                            != PackageManager
                            .PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity
                                        .this,
                                Manifest
                                        .permission
                                        .CAMERA)) {

                            // Show an explanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.

                        } else {

                            // No explanation needed, we can request the permission.

                            ActivityCompat.requestPermissions(MainActivity
                                            .this,
                                    new String[]{Manifest.permission
                                            .CAMERA},
                                    CAMERA_PERMISSION_CAMERA);

                            // CAMERA_PERMISSION_CAMERA is an
                            // app-defined int constant. The callback method gets the
                            // result of the request.
                        }
                    }
                }
                return;
            }
        }
    }
}

/* Error Outputs:

12-24 21:49:51.919 13092-13092/? E/AndroidRuntime: FATAL EXCEPTION: main
                                                   Process: com.example.neekondev.quickscan, PID: 13092
                                                   java.lang.RuntimeException: Unknown camera error
                                                       at android.hardware.Camera.<init>(Camera.java:524)
                                                       at android.hardware.Camera.open(Camera.java:361)
                                                       at com.google.android.gms.vision.CameraSource.zzBZ(Unknown Source)
                                                       at com.google.android.gms.vision.CameraSource.start(Unknown Source)
                                                       at com.example.neekondev.quickscan.activities.MainActivity$1.surfaceCreated(MainActivity.java:52)
                                                       at android.view.SurfaceView.updateWindow(SurfaceView.java:597)
                                                       at android.view.SurfaceView$3.onPreDraw(SurfaceView.java:179)
                                                       at android.view.ViewTreeObserver.dispatchOnPreDraw(ViewTreeObserver.java:944)
                                                       at android.view.ViewRootImpl.performTraversals(ViewRootImpl.java:2494)
                                                       at android.view.ViewRootImpl.doTraversal(ViewRootImpl.java:1544)
                                                       at android.view.ViewRootImpl$TraversalRunnable.run(ViewRootImpl.java:6528)
                                                       at android.view.Choreographer$CallbackRecord.run(Choreographer.java:858)
                                                       at android.view.Choreographer.doCallbacks(Choreographer.java:670)
                                                       at android.view.Choreographer.doFrame(Choreographer.java:606)
                                                       at android.view.Choreographer$FrameDisplayEventReceiver.run(Choreographer.java:844)
                                                       at android.os.Handler.handleCallback(Handler.java:745)
                                                       at android.os.Handler.dispatchMessage(Handler.java:95)
                                                       at android.os.Looper.loop(Looper.java:171)
                                                       at android.app.ActivityThread.main(ActivityThread.java:5454)
                                                       at java.lang.reflect.Method.invoke(Native Method)
                                                       at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:726)
                                                       at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:616)



12-24 21:49:51.919 13092-13092/? E/AndroidRuntime: FATAL EXCEPTION: main
                                                   Process: com.example.neekondev.quickscan, PID: 13092
                                                   java.lang.RuntimeException: Unknown camera error
                                                       at android.hardware.Camera.<init>(Camera.java:524)
                                                       at android.hardware.Camera.open(Camera.java:361)
                                                       at com.google.android.gms.vision.CameraSource.zzBZ(Unknown Source)
                                                       at com.google.android.gms.vision.CameraSource.start(Unknown Source)
                                                       at com.example.neekondev.quickscan.activities.MainActivity$1.surfaceCreated(MainActivity.java:52)
                                                       at android.view.SurfaceView.updateWindow(SurfaceView.java:597)
                                                       at android.view.SurfaceView$3.onPreDraw(SurfaceView.java:179)
                                                       at android.view.ViewTreeObserver.dispatchOnPreDraw(ViewTreeObserver.java:944)
                                                       at android.view.ViewRootImpl.performTraversals(ViewRootImpl.java:2494)
                                                       at android.view.ViewRootImpl.doTraversal(ViewRootImpl.java:1544)
                                                       at android.view.ViewRootImpl$TraversalRunnable.run(ViewRootImpl.java:6528)
                                                       at android.view.Choreographer$CallbackRecord.run(Choreographer.java:858)
                                                       at android.view.Choreographer.doCallbacks(Choreographer.java:670)
                                                       at android.view.Choreographer.doFrame(Choreographer.java:606)
                                                       at android.view.Choreographer$FrameDisplayEventReceiver.run(Choreographer.java:844)
                                                       at android.os.Handler.handleCallback(Handler.java:745)
                                                       at android.os.Handler.dispatchMessage(Handler.java:95)
                                                       at android.os.Looper.loop(Looper.java:171)
                                                       at android.app.ActivityThread.main(ActivityThread.java:5454)
                                                       at java.lang.reflect.Method.invoke(Native Method)
                                                       at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:726)
                                                       at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:616)
*/
