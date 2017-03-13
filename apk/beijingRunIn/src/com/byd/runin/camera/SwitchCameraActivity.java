package com.byd.runin.camera;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.byd.runin.TestActivity;
import com.byd.runin.TestLog.Log;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class SwitchCameraActivity extends TestActivity
{
    public static final String KEY_SHARED_PREF = "key_switch_camera_test";
    public static final String TITLE = "SwitchCamera Test";
    public static final String TAG = "SwitchCamera";
    private static final String CAMERA_TEST_FAIL_INFO =
        "SwitchCamera test fail: ";
    private static final int SWITCH_CAMERA_MSG_NEXT = 1;
    private static final int FINISH_SWITCH_CAMERA = 2;
    private static final int MSG_DELAY_TIME = 4000;
    private static final int MSG_FINISH_DELAY_TIME = 10;
    private Preview mPreview;
    private int mCameraIndex = 0;
    private int switchCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mSharedPrefKey = KEY_SHARED_PREF;
        mTitle = TITLE;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        mPreview = new Preview(this);
        setContentView(mPreview);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    class Preview extends SurfaceView implements SurfaceHolder.Callback
    {
        SurfaceHolder mHolder;
        Camera mCamera;
        boolean isView = false;

        Preview(Context context)
        {
            super(context);
            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            mHolder.addCallback(this);

        }

        public void surfaceCreated(SurfaceHolder holder)
        {
            // The Surface has been created, acquire the camera and tell it where
            // to draw.
            mCamera = Camera.open(FindBackCamera());

            try
            {
                if (mCamera != null)
                {
                    mCamera.setPreviewDisplay(holder);
                    mCamera.setDisplayOrientation(90);
                }

            }
            catch (IOException e)
            {
                mCamera.release();
                mCamera = null;
                Log.d(TAG, e.getMessage());
                dealwithError(mStatusText, CAMERA_TEST_FAIL_INFO + e.getMessage());
                // TODO: add more exception handling logic here
            }
        }

        private int FindBackCamera()
        {
            int cameraCount = 0;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            cameraCount = Camera.getNumberOfCameras();
            for (int camIdx = 0; camIdx < cameraCount; camIdx++)
            {
                Camera.getCameraInfo(camIdx, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
                {
                    mCameraIndex = camIdx;
                    return camIdx;
                }
            }
            return 0;
        }

        public void surfaceDestroyed(SurfaceHolder holder)
        {
            mHandler.removeMessages(SWITCH_CAMERA_MSG_NEXT);
            mHandler.removeMessages(FINISH_SWITCH_CAMERA);
            stopPreview();
            closeCamera();
            mCamera = null;
            mHolder = null;
            mPreview = null;
        }

        private void stopPreview()
        {
            if (mCamera != null)
            {
                mCamera.stopPreview();
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
        {
            // Now that the size is known, set up the camera parameters and begin
            // the preview.
            startPreview();
        }

        public void startPreview()
        {
            if (mCamera == null)
            {
                try
                {
                    mCamera = Camera.open(FindBackCamera());
                }
                catch (Throwable e)
                {
                    Log.d(TAG, e.getMessage());
                    dealwithError(mStatusText, CAMERA_TEST_FAIL_INFO + e.getMessage());
                }
            }
            if (mCamera != null)
            {
                setCameraParameters();
                setPreviewDisplay(mHolder);
                try
                {
                    mCamera.startPreview();
                }
                catch (Throwable e)
                {
                    closeCamera();
                    Log.d(TAG, e.getMessage());
                    dealwithError(mStatusText, CAMERA_TEST_FAIL_INFO + e.getMessage());
                }
            }

            mHandler.sendEmptyMessageDelayed(SWITCH_CAMERA_MSG_NEXT,MSG_DELAY_TIME);
        }

        private void switchCamera()
        {
            mCameraIndex = ((mCameraIndex + 1) % 2);
            if ((mCameraIndex + 1) % 2 == 0)
            {
                ++switchCount;
            }

            if (switchCount >= mTime)
            {
                mHandler.sendEmptyMessageDelayed(FINISH_SWITCH_CAMERA,MSG_FINISH_DELAY_TIME);
                return ;
            }

            mCamera.stopPreview(); //The original camera stopped Preview
            mCamera.release(); //release
            mCamera = null;

            mCamera = Camera.open(mCameraIndex);
            if (mCamera != null)
            {
                mCamera.setDisplayOrientation(90);
                setCameraParameters();
                setPreviewDisplay(mHolder);

                try
                {
                    mCamera.startPreview();
                }
                catch (Throwable e)
                {
                    closeCamera();
                    Log.d(TAG, "open camera fail!");
                    dealwithError(mStatusText, CAMERA_TEST_FAIL_INFO + e.getMessage());
                }
            }

            mHandler.sendEmptyMessageDelayed(SWITCH_CAMERA_MSG_NEXT,MSG_DELAY_TIME);
        }

        private Handler mHandler = new Handler()
        {
            @Override public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case SWITCH_CAMERA_MSG_NEXT:
                        switchCamera();
                        break;

                    case FINISH_SWITCH_CAMERA:
                        stopPreview();
                        closeCamera();
                        finish();
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        };

        private void setPreviewDisplay(SurfaceHolder holder)
        {
            try
            {
                mCamera.setPreviewDisplay(holder);
            }
            catch (IOException e)
            {
                closeCamera();
                // TODO Auto-generated catch block
                Log.d(TAG, e.getMessage());
                dealwithError(mStatusText, CAMERA_TEST_FAIL_INFO + e.getMessage());

            }
        }

        private void setCameraParameters()
        {
            Camera.Parameters mParameters = mCamera.getParameters();
            List < Integer > frameRates =
                mParameters.getSupportedPreviewFrameRates();
            if (frameRates != null)
            {
                Integer max = Collections.max(frameRates);
                mParameters.setPreviewFrameRate(max);
            }

            // Set picture size.
            List < Size > supported = mParameters.getSupportedPictureSizes();
            if (supported != null)
            {
                int wid = 0;
                int hei = 0;
                for (Size size: supported)
                {
                    if (size.width > wid)
                    {
                        wid = size.width;
                        hei = size.height;
                    }
                }
                mParameters.setPictureSize(wid, hei);
            }

            // Set the preview frame aspect ratio according to the picture size.
            Size size = mParameters.getPictureSize();

            // Set a preview size that is closest to the viewfinder height and has
            // the right aspect ratio.
            List < Size > sizes = mParameters.getSupportedPreviewSizes();
            Size optimalSize = getOptimalPreviewSize(sizes, (double)size.width / size.height);
            if (optimalSize != null)
            {
                mParameters.setPreviewSize(optimalSize.width,
                    optimalSize.height);
            }
            mCamera.setParameters(mParameters);
        }

        private Size getOptimalPreviewSize(List < Size > sizes, double
            targetRatio)
        {
            final double ASPECT_TOLERANCE = 0.05;
            if (sizes == null)
                return null;

            Size optimalSize = null;
            double minDiff = Double.MAX_VALUE;

            Display display = getWindowManager().getDefaultDisplay();
            int targetHeight = Math.min(display.getHeight(), display.getWidth());

            if (targetHeight <= 0)
            {
                // We don't know the size of SurefaceView, use screen height
                WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
                targetHeight = windowManager.getDefaultDisplay().getHeight();
            }

            // Try to find an size match aspect ratio and size
            for (Size size: sizes)
            {
                double ratio = (double)size.width / size.height;
                if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                    continue;
                if (Math.abs(size.height - targetHeight) < minDiff)
                {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }

            // Cannot find the one match the aspect ratio, ignore the requirement
            if (optimalSize == null)
            {
                minDiff = Double.MAX_VALUE;
                for (Size size: sizes)
                {
                    if (Math.abs(size.height - targetHeight) < minDiff)
                    {
                        optimalSize = size;
                        minDiff = Math.abs(size.height - targetHeight);
                    }
                }
            }
            return optimalSize;
        }

        private void closeCamera()
        {
            if (mCamera != null)
            {
                mCamera.release();
                mCamera = null;
            }
        }
    }
}
