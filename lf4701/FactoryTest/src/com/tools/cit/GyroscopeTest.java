
package com.tools.cit;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GyroscopeTest extends TestModule implements SensorEventListener {
    private static String MODULE_NAME = "Gyroscope";
    private SensorManager mSensorManager;
    private Sensor mGyro;

    private GLSurfaceView mGLSurfaceView;
    private MyRenderer mRenderer;
    private LinearLayout mLinearLayout;

    private TextView txtGyroX;
    private TextView txtGyroY;
    private TextView txtGyroZ;
     //add by qianyan LF4701Q_P000062 auto pass by Standard
    private float pre_x = 0;
    private float pre_y = 0;
    private float pre_z = 0;
    private boolean firstCome = true;
    private boolean autoPass[] = {false, false, false};
    //end add by qianyan

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.gyroscope, R.drawable.gyroscope);
        txtGyroX = (TextView) findViewById(R.id.txt_gyro_x);
        txtGyroY = (TextView) findViewById(R.id.txt_gyro_y);
        txtGyroZ = (TextView) findViewById(R.id.txt_gyro_z);
        mLinearLayout = (LinearLayout) findViewById(R.id.glsurface);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (mGyro == null) {
            txtGyroX.setText(R.string.na);
            txtGyroY.setText(R.string.na);
            txtGyroZ.setText(R.string.na);
            setTestResult(FAIL);// sensor wasn't detected
        }
        else{
            Log.i("sensorgyross","Found gyroscope sensor: " + mGyro.getName());
            // Create our Preview view and set it as the content of our
            // Activity
            mRenderer = new MyRenderer();
            mGLSurfaceView = new GLSurfaceView(this);
            mGLSurfaceView.setRenderer(mRenderer);
            mLinearLayout.addView(mGLSurfaceView);
            //setContentView(mGLSurfaceView);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mGyro!=null){
            mRenderer.stop();
            mGLSurfaceView.onPause();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGyro != null)
        {
            mSensorManager.registerListener(this, mGyro, SensorManager.SENSOR_DELAY_NORMAL);
            mRenderer.start();
            mGLSurfaceView.onResume();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (mGyro != null)
            mSensorManager.unregisterListener(this);
            super.onStop();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        txtGyroX.setText(event.values[0] + " radians/s^2");
        txtGyroY.setText(event.values[1] + " radians/s^2");
        txtGyroZ.setText(event.values[2] + " radians/s^2");
        //add by qianyan LF4701Q_P000060 auto pass by Standard
        /*setTestResult(PASS);
        Button btnPass = (Button)findViewById(R.id.btn_pass);
        btnPass.setEnabled(true);*/  
        //findViewById(R.id.btn_pass).setEnabled(true);               
        if(autoPass[0] && autoPass[1] && autoPass[2]){
            setTestResult(PASS);
            findViewById(R.id.btn_pass).setEnabled(true);
        }

        if(firstCome) {
            pre_x = event.values[0];
            pre_y = event.values[1];
            pre_z = event.values[2];
            firstCome = false;
        }
        else {
            if(pre_x - event.values[0] >=3 || pre_x - event.values[0] <= -3) {
                autoPass[0] = true;
            }
            if(pre_y - event.values[1] >=3 || pre_y - event.values[1] <= -3) {
                autoPass[1] = true;
            }
            if(pre_z - event.values[2] >=3 || pre_z - event.values[2] <= -3) {
                autoPass[2] = true;
            }
                
        }
        // end add by qianyan       
        // FTLog.i(this,"accuracy: "+event.accuracy+", value: "+event.values[0]);
    }

    @Override
    public String getModuleName() {
        return MODULE_NAME;
    }

    class MyRenderer implements GLSurfaceView.Renderer, SensorEventListener {
        private Cube mCube;
        private Sensor mRotationVectorSensor;
        private final float[] mRotationMatrix = new float[16];

        public MyRenderer() {
            // find the rotation-vector sensor
            mRotationVectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

            mCube = new Cube();
            // initialize the rotation matrix to identity
            mRotationMatrix[ 0] = 1;
            mRotationMatrix[ 4] = 1;
            mRotationMatrix[ 8] = 1;
            mRotationMatrix[12] = 1;
        }

        public void start() {
            // enable our sensor when the activity is resumed, ask for
            // 10 ms updates.
            if(mRotationVectorSensor != null) {
                mSensorManager.registerListener(this, mRotationVectorSensor, 30000);
            }
        }

        public void stop() {
            // make sure to turn our sensor off when the activity is paused
            mSensorManager.unregisterListener(this);
        }

        @SuppressLint("NewApi")
        public void onSensorChanged(SensorEvent event) {
            // we received a sensor event. it is a good practice to check
            // that we received the proper event
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                // convert the rotation-vector to a 4x4 matrix. the matrix
                // is interpreted by Open GL as the inverse of the
                // rotation-vector, which is what we want.
                float rotate[] = {(float)(event.values[0]/30), (float)(event.values[1]/30), (float)(event.values[2]/30)};
                SensorManager.getRotationMatrixFromVector(
                        //mRotationMatrix , event.values);
                        mRotationMatrix , rotate);
            }
        }

        public void onDrawFrame(GL10 gl) {
            // clear screen
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

            // set-up modelview matrix
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
            //gl.glTranslatef(0, 0, -3.0f);
            gl.glTranslatef(0, 0, -3.0f);
            gl.glMultMatrixf(mRotationMatrix, 0);

            // draw our object
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

            mCube.draw(gl);
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            // set view-port
            gl.glViewport(0, 0, width, height);
            // set projection matrix
            float ratio = (float) width / height;
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            // dither is enabled by default, we don't need it
            gl.glDisable(GL10.GL_DITHER);
            // clear screen in white
            //gl.glClearColor(0,0,0,0);
            gl.glClearColor(1,1,1,1);
            //gl.glClearColor(0.5f, 0.5f, 0.5f, arg3);
        }

        class Cube {
            // initialize our cube
            private FloatBuffer mVertexBuffer;
            private FloatBuffer mColorBuffer;
            private ByteBuffer  mIndexBuffer;

            public Cube() {
                final float vertices[] = {
                        -1, -1, -1,      1, -1, -1,
                         1,  1, -1,     -1,  1, -1,
                        -1, -1,  1,      1, -1,  1,
                         1,  1,  1,     -1,  1,  1,
                };

                final float colors[] = {
                        0,  0,  0,  1,  1,  0,  0,  1,
                        1,  1,  0,  1,  0,  1,  0,  1,
                        0,  0,  1,  1,  1,  0,  1,  1,
                        1,  1,  1,  1,  0,  1,  1,  1,
                };

                final byte indices[] = {
                        0, 4, 5,    0, 5, 1,
                        1, 5, 6,    1, 6, 2,
                        2, 6, 7,    2, 7, 3,
                        3, 7, 4,    3, 4, 0,
                        4, 7, 6,    4, 6, 5,
                        3, 0, 1,    3, 1, 2
                };

                ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
                vbb.order(ByteOrder.nativeOrder());
                mVertexBuffer = vbb.asFloatBuffer();
                mVertexBuffer.put(vertices);
                mVertexBuffer.position(0);

                ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
                cbb.order(ByteOrder.nativeOrder());
                mColorBuffer = cbb.asFloatBuffer();
                mColorBuffer.put(colors);
                mColorBuffer.position(0);

                mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
                mIndexBuffer.put(indices);
                mIndexBuffer.position(0);
            }

            public void draw(GL10 gl) {
                gl.glEnable(GL10.GL_CULL_FACE);
                gl.glFrontFace(GL10.GL_CW);
                gl.glShadeModel(GL10.GL_SMOOTH);
                gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
                gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
                gl.glDrawElements(GL10.GL_TRIANGLES, 36, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

}
