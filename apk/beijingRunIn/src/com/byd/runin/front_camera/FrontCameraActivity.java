package com.byd.runin.front_camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class FrontCameraActivity extends TestActivity
{
    public static final String KEY_SHARED_PREF = "key_front_camera_test";
    public static final String TITLE = "FrontCamera Test";
    public static final String ACTION_FRONT_CAMERA_ON = "com.byd.runin.front_camera.CAMERA_ON";
    public static final String TAG = "FrontCameraActivity";
    private static final String CAMERA_TEST_FAIL_INFO = "frontCamera test fail: ";
    private static final String STORAGE_PICTURE_PATH = "/data/data/com.byd.runin/picture/";
    private static final int CAMERA_RESOLUTION_PARAME_MSG_NEXT = 1;
    private static final int MSG_DELAY_TIME = 4000;
    private Preview mPreview;
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
            mCamera = Camera.open(FindFrontCamera());

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

        private int FindFrontCamera()
        {
            int cameraCount = 0;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            cameraCount = Camera.getNumberOfCameras();
            for (int camIdx = 0; camIdx < cameraCount; camIdx++)
            {
                Camera.getCameraInfo(camIdx, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
                {
                    return camIdx;
                }
            }
            return 0;
        }

        public void surfaceDestroyed(SurfaceHolder holder)
        {
            mHandler.removeMessages(CAMERA_RESOLUTION_PARAME_MSG_NEXT);
            stopPreview();
            closeCamera();
            deletePictureFile();
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
                    mCamera = Camera.open(FindFrontCamera());
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
                    mCamera.takePicture(null, null, jpegCallback);
                }
                catch (Throwable e)
                {
                    closeCamera();
                    Log.d(TAG, e.getMessage());
                    dealwithError(mStatusText, CAMERA_TEST_FAIL_INFO + e.getMessage());
                }
            }

            mHandler.sendEmptyMessageDelayed(CAMERA_RESOLUTION_PARAME_MSG_NEXT,
                MSG_DELAY_TIME);
        }

        private void takePicture()
        {
            if (deletePictureFile())
            {
                Log.d(TAG, "delete picture success");
                mCamera.takePicture(null, null, jpegCallback);
                mHandler.sendEmptyMessageDelayed
                    (CAMERA_RESOLUTION_PARAME_MSG_NEXT, MSG_DELAY_TIME);
            }

        }

        private Handler mHandler = new Handler()
        {
            @Override
	   public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case CAMERA_RESOLUTION_PARAME_MSG_NEXT:
                        takePicture();
                        break;

                    default:
                        super.handleMessage(msg);
                }
            }
        };

        private boolean deletePictureFile()
        {
            File dir = new File(STORAGE_PICTURE_PATH);
            boolean bFlag = true;
            if (dir.isDirectory())
            {
                String[]children = dir.list();
                for (int i = 0; i < children.length; ++i)
                {
                    File file = new File(dir, children[i]);
                    if (!file.delete())
                    {
                        bFlag = false;
                        break;
                    }
                }
            }

            return bFlag;

        }

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

        private Size getOptimalPreviewSize(List < Size > sizes, double targetRatio)
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

        private PictureCallback jpegCallback = new PictureCallback()
        {

            public void onPictureTaken(byte[]_data, Camera _camera)
            {
                // TODO Handle JPEG image data
                Bitmap bm = BitmapFactory.decodeByteArray(_data, 0,
                    _data.length);
                File myCaptureFile = new File(STORAGE_PICTURE_PATH + System.currentTimeMillis() + ".jpg");

                File parentDir = myCaptureFile.getParentFile();
                if (!parentDir.exists())
                {
                    if (parentDir.mkdirs())
                    {
                        Log.d(TAG, "create captureFile sucess!");
                    }
                }

                if (!myCaptureFile.exists())
                {
                    try
                    {
                        myCaptureFile.createNewFile();
                    }
                    catch (IOException e)
                    {
                        // TODO Auto-generated catch block
                        Log.d(TAG, e.getMessage());
                        mCamera.stopPreview();
                        mCamera.release();
                        mCamera = null;
                        dealwithError(mStatusText, CAMERA_TEST_FAIL_INFO + e.getMessage());
                    }
                }

                try
                {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
                    bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);

                    bos.flush();
                    bos.close();

                    mCamera.stopPreview();
                    mCamera.startPreview();

                }
                catch (Exception e)
                {
                    Log.d(TAG, e.getMessage());
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                    dealwithError(mStatusText, CAMERA_TEST_FAIL_INFO + e.getMessage());
                }
            }
        };

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
