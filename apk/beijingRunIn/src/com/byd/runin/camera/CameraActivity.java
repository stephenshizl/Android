package com.byd.runin.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;

import com.byd.runin.TestActivity;
import com.byd.runin.TestLog.Log;
import com.byd.runin.mic.MicTest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class CameraActivity extends TestActivity
{

    public static final String TAG = "CameraActivity";
    public static final String KEY_SHARED_PREF = "key_camera_test";
    public static final String TITLE = "Camera Test";

    private static final int CAMERA_RESOLUTION_PARAME_MSG_NEXT = 1;
    private static final int CAMERA_ANTIBANDING_PARAME_MSG = 2;
    private static final int CAMERA_FLASHMODE_PARAME_MSG = 3;
    private static final int CAMERA_SCENEMODE_PARAME_MSG = 4;
    private static final int CAMERA_WHITEBALANCE_PARAME_MSG = 5;
    private static final int CAMERA_FOUCUSMODE_PARAME_MSG = 6;

    private static final String STORAGE_PICTURE_PATH = "/data/data/com.byd.runin/picture/";
    private static final String CAMERA_TEST_FAIL_INFO = "camera test fail: ";

    private static final int PICTURE_DEFAULT_WIDTH = 1024;
    private static final int PICTURE_DEFAULT_HEIGHT = 768;
    private static final int MSG_DELAY_TIME = 5000;

    public static final String ACTION_CAMERA_ON = "com.byd.runin.camera.CAMERA_ON";

    private Preview mPreview;

    private int takePictureCount = 0;

    private MicTest mic = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mSharedPrefKey = KEY_SHARED_PREF;
        mTitle = TITLE;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        mPreview = new Preview(this);
        setContentView(mPreview);

        mic = new MicTest(this);
        mic.startRecord();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        mic.release();
    }

    class Preview extends SurfaceView implements SurfaceHolder.Callback
    {
        SurfaceHolder mHolder;
        Camera mCamera;

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
            mCamera = Camera.open();
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

        public void surfaceDestroyed(SurfaceHolder holder)
        {
            // Surface will be destroyed when we return, so stop the preview.
            // Because the CameraDevice object is not a shared resource, it's very
            // important to release it when the activity is paused.
            mHandler.removeMessages(CAMERA_RESOLUTION_PARAME_MSG_NEXT);
            mHandler.removeMessages(CAMERA_ANTIBANDING_PARAME_MSG);
            mHandler.removeMessages(CAMERA_FLASHMODE_PARAME_MSG);
            mHandler.removeMessages(CAMERA_SCENEMODE_PARAME_MSG);
            mHandler.removeMessages(CAMERA_WHITEBALANCE_PARAME_MSG);
            mHandler.removeMessages(CAMERA_FOUCUSMODE_PARAME_MSG);
            mCamera.stopPreview();
            mCamera.release();
            deletePictureFile();
            mCamera = null;
        }

        private Size getOptimalPreviewSize(List < Size > sizes, int w, int h)
        {
            final double ASPECT_TOLERANCE = 0.001;
            double targetRatio = (double)w / h;
            if (sizes == null)
                return null;

            Size optimalSize = null;
            double minDiff = Double.MAX_VALUE;

            int targetHeight = h;

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

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
        {
            // Now that the size is known, set up the camera parameters and begin
            // the preview.
            Camera.Parameters parameters = mCamera.getParameters();
            List < Size > sizes = parameters.getSupportedPreviewSizes();
            Size optimalSize = getOptimalPreviewSize(sizes, w, h);
            parameters.setPreviewSize(optimalSize.width, optimalSize.height);

            resolutionMaxPictureSizeMode(parameters);
        }


        /**
         * The maximum resolution of the mobile phone camera support
         * and Random access a resolution of the photo
         */
        //resolution
        private void resolutionMaxPictureSizeMode(Camera.Parameters parameter)
        {
            List < Size > pictureSize = parameter.getSupportedPictureSizes();
            int maxWidth = 0;
            int maxHeight = 0;
            int i = 0;
            for (Size size: pictureSize)
            {
                Log.d(TAG, "size[" + i++ + "] : w = " + size.width + "   h = " + size.height);
                if (size.width > maxWidth)
                {
                    maxWidth = size.width;
                }

                if (size.height > maxHeight)
                {
                    maxHeight = size.height;
                }

            }
            parameter.setPictureSize(maxWidth, maxHeight);
            mCamera.setParameters(parameter);

            mCamera.startPreview();

            mCamera.takePicture(null, null, jpegCallback);

            Message msg = new Message();
            msg.what = CAMERA_RESOLUTION_PARAME_MSG_NEXT;
            msg.obj = parameter;
            mHandler.sendMessageDelayed(msg, MSG_DELAY_TIME);

        }

        private void resolutionRadomPictureSizeMode(Camera.Parameters parameter)
        {
            List < Size > pictureSize = parameter.getSupportedPictureSizes();
            Random r = new Random();
            int length = pictureSize.size();
            int index = Math.abs((r.nextInt() % length));

            Size size = pictureSize.get(index);
            parameter.setPictureSize(size.width, size.height);
            mCamera.setParameters(parameter);
            //mCamera.startPreview();
            mCamera.takePicture(null, null, jpegCallback);

            Message msg = new Message();
            msg.what = CAMERA_ANTIBANDING_PARAME_MSG;
            msg.obj = parameter;
            msg.arg1 = 0;
            mHandler.sendMessageDelayed(msg, MSG_DELAY_TIME);
        }

        //antibanding
        private void antiBandMode(Camera.Parameters parameter, int index)
        {
            takePictureCount++;
            parameter.setPictureSize(PICTURE_DEFAULT_WIDTH,
                PICTURE_DEFAULT_HEIGHT);
            List < String > antibanding = parameter.getSupportedAntibanding();

            String antibandIndex = antibanding.get(index);

            parameter.setAntibanding(antibandIndex);

            mCamera.setParameters(parameter);
            mCamera.takePicture(null, null, jpegCallback);

            Message msg = new Message();
            if (1 == takePictureCount)
            {
                msg.what = CAMERA_ANTIBANDING_PARAME_MSG;
                msg.obj = parameter;
                if (antibanding.size() > 1)
                {
                    msg.arg1 = 1;
                }
                else
                {
                    msg.arg1 = 0;
                }
            }
            else if (2 == takePictureCount)
            {
                parameter.setAntibanding(Camera.Parameters.ANTIBANDING_AUTO);
                takePictureCount = 0;
                msg.what = CAMERA_FLASHMODE_PARAME_MSG;
                msg.obj = parameter;
                msg.arg1 = 0;
            }

            mHandler.sendMessageDelayed(msg, MSG_DELAY_TIME);

        }

        //flashmode
        private void flashMode(Camera.Parameters parameter, int index)
        {
            takePictureCount++;
            parameter.setPictureSize(PICTURE_DEFAULT_WIDTH,
                PICTURE_DEFAULT_HEIGHT);
            List < String > flashMode = parameter.getSupportedFlashModes();

            String flashModeStr = flashMode.get(index);

            if (0 == index && !flashModeStr.equals(Camera.Parameters.FLASH_MODE_ON))
                parameter.setFlashMode(Camera.Parameters.FLASH_MODE_ON);

            mCamera.setParameters(parameter);
            mCamera.takePicture(null, null, jpegCallback);

            Message msg = new Message();
            if (1 == takePictureCount)
            {
                msg.what = CAMERA_FLASHMODE_PARAME_MSG;
                msg.obj = parameter;
                if (flashMode.size() > 1)
                {
                    msg.arg1 = 1;
                }
                else
                {
                    msg.arg1 = 0;
                }
            }
            else if (2 == takePictureCount)
            {
                parameter.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                takePictureCount = 0;
                msg.what = CAMERA_SCENEMODE_PARAME_MSG;
                msg.obj = parameter;
                msg.arg1 = 0;
            }

            mHandler.sendMessageDelayed(msg, MSG_DELAY_TIME);

        }

        //sceneMode
        private void sceneMode(Camera.Parameters parameter, int index)
        {
            takePictureCount++;
            parameter.setPictureSize(PICTURE_DEFAULT_WIDTH,
                PICTURE_DEFAULT_HEIGHT);
            List < String > sceneMode = parameter.getSupportedSceneModes();

            String sceneModeStr = sceneMode.get(index);

            parameter.setSceneMode(sceneModeStr);

            mCamera.setParameters(parameter);
            mCamera.takePicture(null, null, jpegCallback);

            Message msg = new Message();
            if (1 == takePictureCount)
            {
                msg.what = CAMERA_SCENEMODE_PARAME_MSG;
                msg.obj = parameter;
                if (sceneMode.size() > 1)
                {
                    msg.arg1 = 1;
                }
                else
                {
                    msg.arg1 = 0;
                }
            }
            else if (2 == takePictureCount)
            {
                parameter.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
                takePictureCount = 0;
                msg.what = CAMERA_WHITEBALANCE_PARAME_MSG;
                msg.obj = parameter;
                msg.arg1 = 0;
            }

            mHandler.sendMessageDelayed(msg, MSG_DELAY_TIME);

        }

        //whitebalanceMode
        private void whitebalanceMode(Camera.Parameters parameter, int index)
        {
            takePictureCount++;
            parameter.setPictureSize(PICTURE_DEFAULT_WIDTH,
                PICTURE_DEFAULT_HEIGHT);
            List < String > whitebalanceMode =
                parameter.getSupportedWhiteBalance();

            String sceneModeStr = whitebalanceMode.get(index);

            parameter.setWhiteBalance(sceneModeStr);

            mCamera.setParameters(parameter);

            mCamera.takePicture(null, null, jpegCallback);

            Message msg = new Message();
            if (1 == takePictureCount)
            {
                msg.what = CAMERA_WHITEBALANCE_PARAME_MSG;
                msg.obj = parameter;
                if (whitebalanceMode.size() > 1)
                {
                    msg.arg1 = 1;
                }
                else
                {
                    msg.arg1 = 0;
                }
            }
            else if (2 == takePictureCount)
            {
                parameter.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
                takePictureCount = 0;
                msg.what = CAMERA_FOUCUSMODE_PARAME_MSG;
                msg.obj = parameter;
                msg.arg1 = 0;
            }

            mHandler.sendMessageDelayed(msg, MSG_DELAY_TIME);

        }

        //foucusMode
        private void foucusMode(Camera.Parameters parameter, int index)
        {
            takePictureCount++;

            parameter.setPictureSize(PICTURE_DEFAULT_WIDTH,
                PICTURE_DEFAULT_HEIGHT);
            List < String > focusMode = parameter.getSupportedFocusModes();

            String focusModeStr = focusMode.get(index);

            parameter.setFocusMode(focusModeStr);

            mCamera.setParameters(parameter);
            mCamera.takePicture(null, null, jpegCallback);

            Message msg = new Message();
            if (1 == takePictureCount)
            {
                msg.what = CAMERA_FOUCUSMODE_PARAME_MSG;
                msg.obj = parameter;
                if (focusMode.size() > 1)
                {
                    msg.arg1 = 1;
                }
                else
                {
                    msg.arg1 = 0;
                }
                mHandler.sendMessageDelayed(msg, MSG_DELAY_TIME);
            }
            else if (2 == takePictureCount)
            {
                parameter.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                Log.d(TAG, "finish one times");
                takePictureCount = 0;

                if (deletePictureFile())
                {
                    Log.d(TAG, "start next times");
                    resolutionMaxPictureSizeMode(parameter);
                }
            }

        }

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

        private Handler mHandler = new Handler()
        {
            Camera.Parameters parameters = null;
            @Override
	   public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case CAMERA_RESOLUTION_PARAME_MSG_NEXT:
                        parameters = (Camera.Parameters)msg.obj;
                        resolutionRadomPictureSizeMode(parameters);
                        break;
                    case CAMERA_ANTIBANDING_PARAME_MSG:
                        parameters = (Camera.Parameters)msg.obj;
                        antiBandMode(parameters, msg.arg1);
                        break;
                    case CAMERA_FLASHMODE_PARAME_MSG:
                        parameters = (Camera.Parameters)msg.obj;
                        flashMode(parameters, msg.arg1);
                        break;
                    case CAMERA_SCENEMODE_PARAME_MSG:
                        parameters = (Camera.Parameters)msg.obj;
                        sceneMode(parameters, msg.arg1);
                        break;
                    case CAMERA_WHITEBALANCE_PARAME_MSG:
                        parameters = (Camera.Parameters)msg.obj;
                        whitebalanceMode(parameters, msg.arg1);
                        break;
                    case CAMERA_FOUCUSMODE_PARAME_MSG:
                        parameters = (Camera.Parameters)msg.obj;
                        foucusMode(parameters, msg.arg1);
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        };

        private PictureCallback jpegCallback = new PictureCallback()
        {

            public void onPictureTaken(byte[]_data, Camera _camera)
            {
                // TODO Handle JPEG image data
                Bitmap bm = BitmapFactory.decodeByteArray(_data, 0,
                    _data.length);
                File myCaptureFile = new File(STORAGE_PICTURE_PATH +
                    System.currentTimeMillis() + ".jpg");

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
                        dealwithError(mStatusText, CAMERA_TEST_FAIL_INFO +
                            e.getMessage());
                    }
                }

                try
                {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
                    bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);

                    bos.flush();
                    bos.close();

                    resetCamera();

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

        private void resetCamera()
        {
            mCamera.stopPreview();
            mCamera.startPreview();
        }

    }
}
