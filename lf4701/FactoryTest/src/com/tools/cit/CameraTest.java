
package com.tools.cit;

import java.io.IOException;
import java.util.List;

import com.tools.util.FTLog;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.view.View.OnTouchListener;

/*Begin add by liugang for W101HM 20130806*/
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.AutoFocusCallback;
import android.view.MotionEvent;
import android.widget.Button;
import android.hardware.Camera.ShutterCallback;
import android.os.Handler;
import android.os.SystemProperties;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.TextView;
import java.util.List;
import java.util.Collections;
import android.view.WindowManager;
/*End add*/

public class CameraTest extends TestModule implements OnTouchListener{
    public static final String CAMERA = "RearCamera";
    public static int DFLT_WIDTH = 720;
    public static int DFLT_HEIGHT = 960;

    Camera mCamera;
    CameraPreview mCameraPreview;
    LinearLayout cameraLayout;
    int cameraCount = 0;

    private String strCaptureFilePath = "/data/camera_snap.jpg";
    private Button btnPass;
    private TextView tv;
    /*Add by liugang for W101HM 20130906*/
    private static boolean isPreview = false;
    /*End add*/
    private String config;

    @Override
    public String getModuleName() {
        return CAMERA;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.camera, R.drawable.camera);
        cameraLayout = (LinearLayout) findViewById(R.id.camera_preview);
        tv=(TextView)findViewById(R.id.tv);

        cameraLayout.setOnTouchListener(this);

        mCameraPreview = new CameraPreview(this);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(DFLT_WIDTH, DFLT_HEIGHT);
        // lp.setMargins(2, 2, 2, 2);
        mCameraPreview.setLayoutParams(lp);

        btnPass = (Button)findViewById(R.id.btn_pass);
        btnPass.setEnabled(false);
        // cameraLayout.addView(mCameraPreview );
    }

    @Override
    protected void onResume() {
        FTLog.d(this, "onResume()");
        super.onResume();

        // TODO surfaceCreated won't be called when press HANGUP key!
        cameraLayout.removeAllViews();
        cameraLayout.addView(mCameraPreview);

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras(); // get cameras number
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo); // get camera info
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                try {
                    mCamera = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        // mCamera = Camera.open();
        if (mCamera != null) {
         config=SystemProperties.get("ro.build.lcd_size");
         if(config.equals("7")){
                mCamera.setDisplayOrientation(90);
         }else{
         mCamera.setDisplayOrientation(90);
        }
            mCameraPreview.setCamera(mCamera);
            setCameraParameters();
        } else {
            // open camera failed.
            setTestResult(FAIL);
        }
    }

    @Override
    protected void onPause() {
        FTLog.d(this, "onPause()");
        if (mCamera != null) {
            mCameraPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
        super.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
        isPreview = false;
    }
    /*Begin add by liugang for W101HM 20130806*/
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                try{
                    mCamera.getParameters().setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    mCamera.autoFocus(autofocusCallback);
                } catch (Exception e) {
                    e.printStackTrace(); 
                }
                getResolution();
                btnPass.setEnabled(true);
                break;
            default:
                ;
        }
        return true;
    }

    private AutoFocusCallback autofocusCallback = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            /*Add by liugang for W101HM 20130906*/
            if(!isPreview){
                mCamera.takePicture(shutterCallback, null, jpegCallback);
                isPreview = true;
            } else {
                camera.startPreview();
                isPreview = false;
            }
            /*End add*/
        }
    };

    private ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
            // Shutter has closed
        }
    };

    private PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] _data, Camera _camera) {

            final Bitmap bm = BitmapFactory.decodeByteArray(_data, 0, _data.length);

            final File myCaptureFile = new File(strCaptureFilePath);
            try {

                Runnable save = new Runnable(){
                    public void run(){
                        try {
                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(
                                    myCaptureFile));

                            bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);

                            bm.recycle();

                            bos.flush();

                            bos.close();
                        } catch (Exception e) {
                        }
                    }
                };

                Thread t = new Thread(save);
                t.start();

                _camera.stopPreview();

                //_camera.startPreview();

                if(getResolution()){
                    btnPass.setEnabled(true);
                }   
                isPreview = true;
            } catch (Exception e) {
                Log.e("CameraTest", e.getMessage());
            }

        }
    };
     private  boolean  getResolution()
     {
        boolean  result = false;
        int wid = 0;
        int hei = 0;
        long  w=0,h=0;
        if(mCamera == null){

            return false;
        }       
        Camera.Parameters mParameters = mCamera.getParameters();
        List<Size> support = mParameters.getSupportedPictureSizes();
        if(support != null){
            for (Size size: support){
                if(size.width > wid){
                    wid = size.width;
                    hei = size.height;
                 }
            }
         }
        //Bitmap bmp = BitmapFactory.decodeFile(strCaptureFilePath);
        //w=bmp.getWidth();
        //h=bmp.getHeight();
        //if(wid == w && hei == h){
            tv.setText(getResources().getString(R.string.picture_resolution)+"  "+wid +"*" +hei);
            return true;
       // }
        //return false  ;
     }
   

    private Size getOptimalPreviewSize(List<Size> sizes, double targetRatio) {
        final double ASPECT_TOLERANCE = 0.05;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        Display display = getWindowManager().getDefaultDisplay();
        int targetHeight = Math.min(display.getHeight(), display.getWidth());

        if (targetHeight <= 0) {
             // We don't know the size of SurefaceView, use screen height
            WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
            targetHeight = windowManager.getDefaultDisplay().getHeight();
        }

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            //Log.d(TAG, "No preview size match the aspect ratio");
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
    //Log.d(TAG, String.format(
      //             "Optimal preview size is %sx%s",
        //           optimalSize.width, optimalSize.height));
        return optimalSize;
    }
    private void setCameraParameters() {
        Camera.Parameters mParameters = mCamera.getParameters();
        List<Integer> frameRates = mParameters.getSupportedPreviewFrameRates();
        if (frameRates != null) {
            Integer max = Collections.max(frameRates);
            mParameters.setPreviewFrameRate(max);
        }

            // Set picture size.
        List<Size> supported = mParameters.getSupportedPictureSizes();
        if(supported != null) {
            int wid = 0;
            int hei = 0;
            for (Size size: supported) {
                if(size.width > wid) {
                    wid = size.width;
                    hei = size.height;
                }
            }
            mParameters.setPictureSize(wid,hei);
        }

         // Set the preview frame aspect ratio according to the picture size.

        Size size = mParameters.getPictureSize();

        // Set a preview size that is closest to the viewfinder height and has
         // the right aspect ratio.
        List<Size> sizes = mParameters.getSupportedPreviewSizes();
        Size optimalSize = getOptimalPreviewSize(
                       sizes, (double) size.width / size.height);
        if (optimalSize != null) {
            mParameters.setPreviewSize(optimalSize.width, optimalSize.height);
        }

        mCamera.setParameters(mParameters);
       // mCamera.setDisplayOrientation(180);
     }
}



class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder mHolder;
    Size mPreviewSize;
    List<Size> mSupportedPreviewSizes;
    Camera mCamera;

    CameraPreview(Context context) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera) {
         mCamera = camera;

        if (mCamera != null) {
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            for (Size size : mSupportedPreviewSizes)
                FTLog.i(this, "Support: " + size.width + "x" + size.height);
            requestLayout();
                List<Size> supported = mCamera.getParameters().getSupportedPictureSizes();
                if(supported != null) {
                    int wid = 0;
                    int hei = 0;
                    for (Size size: supported) {
                        if(size.width > wid) {
                            wid = size.width;
                            hei = size.height;
                        }
                    }
                    mCamera.getParameters().setPictureSize(wid,hei);
                }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        FTLog.i(this, "Surface Created!");
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
                Camera.Parameters parameters = mCamera.getParameters();
                Camera.Size size = getOptimalPreviewSize(mCamera.getParameters()
                        .getSupportedPreviewSizes(), CameraTest.DFLT_HEIGHT, CameraTest.DFLT_WIDTH/*
                                                                                                   * CAUTION
                                                                                                   * !
                                                                                                   */);
                FTLog.i(this, "Optimal: " + size.width + ":" + size.height);
                // parameters.setPreviewSize(size.width, size.height);
                parameters.setPreviewSize(640, 480);
                // parameters.set("orientation", "landscape");
                // parameters.set("rotation", 90);
                // parameters.setRotation(90);
                // requestLayout();
                mCamera.setParameters(parameters);
            }
        } catch (IOException exception) {
            FTLog.e(this, "IOException caused by setPreviewDisplay()");
            exception.printStackTrace();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mCamera == null)
            return;

        // setCameraDisplayOrientation((Activity)getContext(), mCamera);
        mCamera.startPreview();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera == null)
            return;
        mCamera.stopPreview();
    }

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        double targetRatio = (double) w / h;
        // FTLog.i(this, targetRatio+"");
        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        for (Size size : sizes) {
            double diff = Math.abs((double) size.width / size.height - targetRatio);
            if (diff < minDiff) {
                optimalSize = size;
                minDiff = diff;
            }
        }
        return optimalSize;
    }

    private static void setCameraDisplayOrientation(Activity activity,
            android.hardware.Camera camera) {
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result = (90 - degrees + 90) % 360;
        camera.setDisplayOrientation(90);
    }
}
