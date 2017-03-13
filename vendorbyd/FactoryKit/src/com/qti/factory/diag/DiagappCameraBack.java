/*
 * Copyright (c) 2011-2013, Qualcomm Technologies, Inc. All Rights Reserved.
 * Qualcomm Technologies Proprietary and Confidential.
 */
package com.qti.factory.diag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import com.qti.factory.R;
import com.qti.factory.Utils;
//import com.qualcomm.factory.Framework.MainApp;
import com.qti.factory.Values;
public class DiagappCameraBack extends Activity implements SurfaceHolder.Callback {

    private Camera mCamera = null;
    private Button takeButton, passButton, failButton;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    final static String TAG = "CameraBack";
    private boolean useAutoFocus = false;
    private boolean firstTime = false;
    private static Context mContext = null;
    private final String filePath = "/data/ftcamera.jpg";
    private File picture;
    private Bitmap bitmap;
    @Override
    public void finish() {

        stopCamera();
        super.finish();
    }

    void init(Context context) {
        mContext = context;
    //    int index = getIntent().getIntExtra(Values.KEY_SERVICE_INDEX, -1);
		firstTime = true;
		int index = getIntent().getIntExtra(Values.KEY_SERVICE_INDEX, -1);
		if (index > 0)
			useAutoFocus = Utils.getBoolPara(index, "AutoFocus", false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        init(getApplicationContext());
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.camera_back);
        setResult(RESULT_CANCELED);
        /* SurfaceHolder set */
        mSurfaceView = (SurfaceView) findViewById(R.id.mSurfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(DiagappCameraBack.this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        bindView();

    }

    void bindView() {

        takeButton = (Button) findViewById(R.id.take_picture_back);
        passButton = (Button) findViewById(R.id.camera_pass);
        failButton = (Button) findViewById(R.id.camera_fail);
        takeButton.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View arg0) {

                takeButton.setVisibility(View.GONE);
                try {
                    if (mCamera != null) {
                        if (useAutoFocus)
                            mCamera.autoFocus(new AutoFocusCallback());
                        else
                            takePicture();
                    } else {
                        finish();
                    }
                } catch (Exception e) {
                    fail(getString(R.string.autofocus_fail));
                    loge(e);
                }
            }
        });

        passButton.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View arg0) {

                setResult(RESULT_OK);
                finish();
            }
        });
        failButton.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View arg0) {

                setResult(RESULT_CANCELED);
                finish();
            }
        });

    }

    public void surfaceCreated(SurfaceHolder surfaceholder) {

        logd("surfaceCreated");
        int oritationAdjust = 0;
        //oritationAdjust = 180;
        try {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            mCamera.setDisplayOrientation(oritationAdjust);
        } catch (Exception exception) {
            toast(getString(R.string.cameraback_fail_open));
            mCamera = null;
        }

        if (mCamera == null) {
            fail(null);
        } else {
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
            } catch (IOException exception) {
                mCamera.release();
                mCamera = null;
                finish();
            }
        }
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int format, int w, int h) {

        logd("surfaceChanged");
        startCamera();
    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {

        logd("surfaceDestroyed");
        stopCamera();
    }

    private void takePicture() {

        logd("takePicture");
        if (mCamera != null) {
            try {
                mCamera.takePicture(mShutterCallback, rawPictureCallback, jpegCallback);
            } catch (Exception e) {
                loge(e);
                finish();
            }
        } else {
            loge("Camera null");
            finish();
        }
    }

    private ShutterCallback mShutterCallback = new ShutterCallback() {

        public void onShutter() {

            logd("onShutter");
            try {
                takeButton.setVisibility(View.GONE);
                passButton.setVisibility(View.VISIBLE);
                failButton.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                loge(e);
            }
        }
    };

    private PictureCallback rawPictureCallback = new PictureCallback() {

        public void onPictureTaken(byte[] _data, Camera _camera) {
            logd("rawPictureCallback onPictureTaken");
            try {
                takeButton.setVisibility(View.GONE);
                passButton.setVisibility(View.VISIBLE);
                failButton.setVisibility(View.VISIBLE);
		picture = new File(filePath);
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(picture));
                bitmap = BitmapFactory.decodeByteArray(_data, 0, _data.length);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
		        bitmap.recycle();
                bos.flush();
                bos.close();
//stopCamera()   startCamera()
            } catch (Exception e) {
                loge(e);
            }
        }
    };

    private PictureCallback jpegCallback = new PictureCallback() {

        public void onPictureTaken(byte[] _data, Camera _camera) {
            logd("jpegCallback onPictureTaken");
            try {
                takeButton.setVisibility(View.GONE);
                passButton.setVisibility(View.VISIBLE);
                failButton.setVisibility(View.VISIBLE);
                picture = new File(filePath);
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(picture));
                bitmap = BitmapFactory.decodeByteArray(_data, 0, _data.length);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
		        bitmap.recycle();
                bos.flush();
                bos.close();

                mCamera.stopPreview();
                mCamera.startPreview();

            } catch (Exception e) {
                loge(e);
            }
        }
    };

    public final class AutoFocusCallback implements android.hardware.Camera.AutoFocusCallback {

        public void onAutoFocus(boolean focused, Camera camera) {

            if (focused) {
                takePicture();
            } else
                fail(getString(R.string.autofocus_fail));
        }
    };

    private void startCamera() {

        if (mCamera != null) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPictureFormat(PixelFormat.JPEG);
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                parameters.setRotation(CameraInfo.CAMERA_FACING_BACK);
                mCamera.setParameters(parameters);
                mCamera.startPreview();
            } catch (Exception e) {
                loge(e);
            }
        }

    }

    private void stopCamera() {

        if (mCamera != null) {
            try {
                if (mCamera.previewEnabled())
                    mCamera.stopPreview();
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void fail(Object msg) {
        loge(msg);
        toast(msg);
        setResult(RESULT_CANCELED);
        finish();
    }

    void pass() {
        // toast(getString(R.string.test_pass));
        setResult(RESULT_OK);
        finish();

    }

    private void logd(Object s) {

        Thread mThread = Thread.currentThread();
        StackTraceElement[] mStackTrace = mThread.getStackTrace();
        String mMethodName = mStackTrace[3].getMethodName();

        s = "[" + mMethodName + "] " + s;
        Log.d(TAG, s + "");
    }

    private void loge(Object e) {

        if (e == null)
            return;
        Thread mThread = Thread.currentThread();
        StackTraceElement[] mStackTrace = mThread.getStackTrace();
        String mMethodName = mStackTrace[3].getMethodName();
        e = "[" + mMethodName + "] " + e;
        Log.e(TAG, e + "");
    }

    public void toast(Object s) {

        if (s == null)
            return;
        Toast.makeText(this, s + "", Toast.LENGTH_SHORT).show();
    }
}
