/*
 * Copyright (c) 2011-2013, Qualcomm Technologies, Inc. All Rights Reserved.
 * Qualcomm Technologies Proprietary and Confidential.
 */
package com.qti.factory.diag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
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
import com.qti.factory.Values;

public class DiagappCameraFront extends Activity implements SurfaceHolder.Callback {

    private Camera mCamera = null;
    private Button takeButton, passButton, failButton;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    final static String TAG = "DiagappCameraFront";
	private boolean useAutoFocus = false;
	private boolean firstTime = false;
    private static Context mContext = null;
    String i2C_CMD = "i2cdetect -y -r 6 0x42 0x42";
    private static boolean WORKROUND = false;
    private final String filePath = "/data/ftcamerafront.jpg";
    private File picture;
    private Bitmap bitmap;
    @Override
    public void finish() {

        if (!WORKROUND)
            stopCamera();
        super.finish();
    }

	void init() {
		mContext = getApplicationContext();
		firstTime = true;
		int index = getIntent().getIntExtra(Values.KEY_SERVICE_INDEX, -1);
		if (index > 0)
			useAutoFocus = Utils.getBoolPara(index, "AutoFocus", false);
	}
    void exec(final String para) {

        new Thread() {

            public void run() {
                try {
                    logd(para);

                    Process mProcess;
                    String paras[] = para.split(" ");
                    for (int i = 0; i < paras.length; i++)
                        logd(i + ":" + paras[i]);
                    mProcess = Runtime.getRuntime().exec(paras);
                    mProcess.waitFor();

                    InputStream inStream = mProcess.getInputStream();
                    InputStreamReader inReader = new InputStreamReader(inStream);
                    BufferedReader inBuffer = new BufferedReader(inReader);
                    String s;
                    String data = "";
                    while ((s = inBuffer.readLine()) != null) {
                        data += s + "\n";
                    }
                    logd(data);
                    int result = mProcess.exitValue();
                    logd("ExitValue=" + result);
                    Message message = new Message();
                    if (data.contains("--"))
                        message.obj = false;
                    else
                        message.obj = true;
                    message.setTarget(mHandler);
                    message.sendToTarget();

                } catch (Exception e) {
                    logd(e);
                }

            }
        }.start();

    }

    Handler mHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            boolean res = (Boolean) msg.obj;
            if (res)
                pass();
            else
                fail(null);

        };
    };

    void fail(Object msg) {

        loge(msg);
        setResult(RESULT_CANCELED);
        finish();
    }

    void pass() {

        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        init();
        if (WORKROUND) {
            exec(i2C_CMD);
        } else {
            getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setContentView(R.layout.camera_front);

            mSurfaceView = (SurfaceView) findViewById(R.id.mSurfaceView);
            mSurfaceHolder = mSurfaceView.getHolder();
            mSurfaceHolder.addCallback(DiagappCameraFront.this);
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            bindView();
        }

    }

    void bindView() {

        takeButton = (Button) findViewById(R.id.take_picture);
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
                    loge(e.toString());
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
        int oritationAdjust = 0;
   /*     String hwPlatform = SystemProperties.get("ro.hw_platform");
        if (Values.PRODUCT_MSM8X25_SKU5.equals(hwPlatform)) {
            // oritationAdjust = 180;
        }
        // oritationAdjust = 180;*/

        logd("surfaceCreated");
        try {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            mCamera.setDisplayOrientation(oritationAdjust);
        } catch (Exception exception) {
            showToast(getString(R.string.cameraback_fail_open));
            mCamera = null;
        }

        if (mCamera == null) {
            finish();
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
                e.printStackTrace();
                finish();
            }
        } else {
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
//stopCamera()   startCamera() 
            } catch (Exception e) {
                loge(e);
            }
        }
    };

    public final class AutoFocusCallback implements android.hardware.Camera.AutoFocusCallback {

        public void onAutoFocus(boolean focused, Camera camera) {
            
            if (focused) {
                takePicture();
            }
        }
    };

    private void startCamera() {

        if (mCamera != null) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPictureFormat(PixelFormat.JPEG);
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                mCamera.setParameters(parameters);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
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

    private void logd(Object d) {

 //       if (LOG)
            Log.d(TAG, d + "");
    }

    private void loge(Object e) {

//        if (LOG)
            Log.e(TAG, e + "");
    }

    private void showToast(String str) {

        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
