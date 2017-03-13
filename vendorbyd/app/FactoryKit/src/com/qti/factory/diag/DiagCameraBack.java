/*
 * Copyright (c) 2011-2013, Qualcomm Technologies, Inc. All Rights Reserved.
 * Qualcomm Technologies Proprietary and Confidential.
 */

package com.qti.factory.diag;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.KeyEvent;
import android.view.View;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.qti.factory.Framework.MainApp;
import com.qti.factory.R;
import com.qti.factory.Utils;
import com.qti.factory.Values;


public class DiagCameraBack extends Activity implements SurfaceHolder.Callback {

    private Camera mCamera = null;
    private Button takeButton, passButton, failButton;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private String resultString = Utils.RESULT_FAIL;
    final static String TAG = "DiagCameraBack";
    private boolean useAutoFocus = true;
    private static Context mContext = null;
    private final String filePath = "/storage/sdcard0/MTF.jpg";
    private File picture;
    private Bitmap bitmap;

    @Override
    public void finish() {

        stopCamera();
        Utils.writeCurMessage(TAG, resultString);
        logd(resultString);
        super.finish();
    }

    void init(Context context) {
        mContext = context;
        int index = getIntent().getIntExtra(Values.KEY_SERVICE_INDEX, -1);
        //useAutoFocus = Utilities.getBoolPara(index, "AutoFocus", false);
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
        mSurfaceHolder.addCallback(DiagCameraBack.this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        bindView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_0:
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
            break;
            default:
            break;
        }
        return true;
    }

    void bindView() {
        takeButton = (Button) findViewById(R.id.take_picture_back);
        takeButton.setVisibility(View.GONE);
    }

    public void surfaceCreated(SurfaceHolder surfaceholder) {

        logd("surfaceCreated");
        int oritationAdjust = 0;
        //int oritationAdjust = 180;
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

        }
    };

    private PictureCallback rawPictureCallback = new PictureCallback() {

        public void onPictureTaken(byte[] _data, Camera _camera) {
            logd("rawPictureCallback onPictureTaken");

        }
    };

    private PictureCallback jpegCallback = new PictureCallback() {

        public void onPictureTaken(byte[] _data, Camera _camera) {
            logd("jpegCallback onPictureTaken");

            picture = new File(filePath);
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(picture));
                bitmap = BitmapFactory.decodeByteArray(_data, 0, _data.length);
                Log.e(TAG,"_data.length="+_data.length);
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

//            if (focused) {
                takePicture();
//            } else
//                fail(getString(R.string.autofocus_fail));
        }
    };

    private void startCamera() {

        if (mCamera != null) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPictureFormat(PixelFormat.JPEG);
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                parameters.setRotation(90);

                Size optimalPreSize = getOptimalPreviewSize(parameters);
                if (optimalPreSize != null) {
                    parameters.setPreviewSize(optimalPreSize.width,optimalPreSize.height);
                    logd("Set Preview size is: " +optimalPreSize.width + "X" +  optimalPreSize.height);
                }

                Size maxPictureSize = getMaxPictureSize(parameters);
                if(maxPictureSize !=null) {
                    logd("Set Picture size is: " +maxPictureSize.width + " X " +  maxPictureSize.height);
                    parameters.setPictureSize(maxPictureSize.width, maxPictureSize.height);
                }

                //parameters.setPictureSize(4160, 3120);
                mCamera.setParameters(parameters);
                mCamera.startPreview();
            } catch (Exception e) {
                loge(e);
            }
        }

    }

//Begin shen.zhiyong@byd.com 20150430 add for set preview and picture size dynamic
/**
*   get preview optimal size
**/
    protected Size getOptimalPreviewSize(Camera.Parameters mParameters) {
        final double ASPECT_TOLERANCE = 0.05;

    List<Size> sizes = mParameters.getSupportedPreviewSizes();
    for(Size size : sizes) {
        logd("Preview size: " + size.width + "X" + size.height);
    }

        if (sizes == null) {
            return null;
        }

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        // Because of bugs of overlay and layout, we sometimes will try to
        // layout the viewfinder in the portrait orientation and thus get the
        // wrong size of mSurfaceView. When we change the preview size, the
        // new overlay will be created before the old one closed, which causes
        // an exception. For now, just get the screen size

        Display display = getWindowManager().getDefaultDisplay();
        int targetHeight = Math.min(display.getHeight(), display.getWidth());
        int targetWidht = Math.max(display.getHeight(), display.getWidth());
        ;

        if (targetHeight <= 0) {
            // We don't know the size of SurefaceView, use screen height
            WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            targetHeight = windowManager.getDefaultDisplay().getHeight();
            targetWidht = windowManager.getDefaultDisplay().getWidth();
        }

        // Try to find an size match window size
        for (Size size : sizes) {
            if (size.width == targetWidht && size.height == targetHeight) {
                optimalSize = size;
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                    logd("Optimal Size:" + size.width + "&" + size.height);
                }
            }
        }
        return optimalSize;
    }

/**
* Get max size of take picture
**/
    protected Size getMaxPictureSize( Camera.Parameters mParameters ) {

        List<Size> sizes = mParameters.getSupportedPictureSizes();
        long getPicSize = 0L;
        long maxPicSize = 0L;
        int sizeMaxIndex = 0;

        for(int i =0; i< sizes.size(); i++) {
            getPicSize = sizes.get(i).width * sizes.get(i).height;
            logd("Picture Size : " + sizes.get(i).width + " X " + sizes.get(i).height);
            if(getPicSize > maxPicSize) {
                maxPicSize = getPicSize;
                getPicSize =0L;
                sizeMaxIndex = i;
                logd("Max Picture Index: " + sizeMaxIndex);
            }

        }

        return sizes.get(sizeMaxIndex);
    }
//End shen.zhiyong@byd.com 20150430

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
        resultString = Utils.RESULT_FAIL;
        finish();
    }

    void pass() {
        // toast(getString(R.string.test_pass));
        setResult(RESULT_OK);
        resultString = Utils.RESULT_PASS;
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
