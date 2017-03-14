package com.example.oldmanlauncher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.provider.Settings;
import android.R.integer;
import android.R.string;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class MainActivity extends Activity implements OnGestureListener {
    List<ResolveInfo> resolveInfo;
    List<View> drawableList;
    private ViewFlipper flipper = null;
    private GestureDetector detector;
    int cellcount_x = 2;
    int cellcount_y = 3;
    int cellcount_screen = cellcount_x * cellcount_y;
    int count_screen = 1;
    int now_screen = 0;
    int count_allcell = 0;
    int current_screen = 0;
    private PackageManager packagemanager;
    private static int bg[] = { Color.GRAY, Color.YELLOW, Color.BLUE,
            Color.GREEN, Color.DKGRAY };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //old man don't need SCREEN_ORIENTATION_LANDSCAPE
        if(this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
         }
        int flag = Settings.System.getInt(this.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
        Settings.System.putInt(this.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, flag == 1?0:1);
        
       
        packagemanager = MainActivity.this.getPackageManager();
        resolveInfo = allPackageList();
        drawableList = createShortcut();
        count_allcell = drawableList.size();
        count_screen = count_allcell % cellcount_screen == 0 ? count_allcell
                / cellcount_screen : count_allcell / cellcount_screen + 1;
        setContentView(R.layout.activity_main);
        flipper = (ViewFlipper) this.findViewById(R.id.ViewFlipper01);
        // flipper[1] = (ViewFlipper) this.findViewById(R.id.ViewFlipper02);
        // flipper[2] = (ViewFlipper) this.findViewById(R.id.ViewFlipper03);
        // flipper[3] = (ViewFlipper) this.findViewById(R.id.ViewFlipper04);
        setView(now_screen-1);
        UpdateBottom();
        detector = new GestureDetector(this, this);
        
       

       
    }

    @SuppressLint("NewApi")
    private List<View> createShortcut() {
        List<View> drawables = new ArrayList<View>();
        for (int i = 0; i < resolveInfo.size(); i++) {
            LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.listphone, null);
            TextView tv = (TextView) layout.findViewById(R.id.name);
            final ResolveInfo resolve = resolveInfo.get(i);
            tv.setText(resolve.loadLabel(packagemanager).toString());
            ImageView imageView = (ImageView) layout
                    .findViewById(R.id.phonepic);
            // imageView.sr
            imageView.setBackground(resolve.loadIcon(packagemanager));
            layout.setBackgroundColor(bg[i % 5]);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    String classname = resolve.activityInfo.name ;                    
                    Intent intent = new Intent();
                    intent.setClassName(resolve.activityInfo.packageName, classname);
                    // intent.setComponent(component)
                    startActivity(intent);
                }
            });
            drawables.add(layout);
        }
        return drawables;
    }

    public List<ResolveInfo> allPackageList() {
        final PackageManager packageManager = this.getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // mainIntent.setPackage(packageName);

        final List<ResolveInfo> apps = packageManager.queryIntentActivities(
                mainIntent, 0);
        return apps != null ? apps : new ArrayList<ResolveInfo>();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY) {
        Log.i("qianyan", "right to left" + String.valueOf(cellcount_screen + 1)
                + " " + String.valueOf(e1.getX() - e2.getX()));
        if (e1.getX() - e2.getX() > 50) {
            Log.i("qianyan","qianyan 1");
            now_screen++;
            current_screen++;
            if (current_screen > 0 && current_screen < count_screen) {
                setView(now_screen-1);
                this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));                 
               // this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_right)); 
                this.flipper.showNext();
                // Toast.makeText(getApplicationContext(), "qdsa", 1000).show();
                UpdateBottom();
                return true;
            } else {
                now_screen = count_screen -1;
                current_screen = current_screen -1;
            }           
        } else if (e1.getX() - e2.getX() < -50) {
            Log.i("qianyan","qianyan 2");
            //now_screen--;
            current_screen--;
            Log.i("qianyan","right to left" + String.valueOf(cellcount_screen - 1));
            if (current_screen >= 0 && current_screen < count_screen) {
                //setView(now_screen+1);
                this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_left));                 
               // this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_left)); 
                this.flipper.showPrevious();
                UpdateBottom();
                return true;
            } else {
                //now_screen = 0;
                current_screen = 0;
            }            
        }
        return false;

    }

    @Override
    public void onLongPress(MotionEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
            float arg3) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    @SuppressWarnings("null")
    private void setView(int pre) {
        if (now_screen >= 0 && now_screen < count_screen) {
            // if (flipper.getChildCount() > 1) {
               // flipper.removeViewAt(pre);
                //flipper.removeAllViews();
           // }
            ViewGroup screen_layout = new celllayout(this);
            //screen_layout.removeAllViews();
            for (int cellcount = 0; cellcount < cellcount_screen && now_screen * cellcount_screen + cellcount < count_allcell; cellcount++) {
                screen_layout.addView(drawableList.get(now_screen * cellcount_screen + cellcount));
            }
            flipper.addView(screen_layout, flipper.getChildCount());
            
        }
    }

    private void UpdateBottom() {
        // TODO Auto-generated method stub
        LinearLayout bottomLayout = (LinearLayout) findViewById(R.id.bottom);
        bottomLayout.removeAllViews();
        for(int i=0;i<count_screen;i++) {
            if(i != current_screen) {    
                ImageView imageView = new ImageView(this);
                imageView.setBackgroundResource(R.drawable.bottom);
                
                LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layout.setMargins(10, 0, 0, 0);
                imageView.setLayoutParams(layout);

                imageView.setPadding(10, 10, 10, 10);
                bottomLayout.addView(imageView);
            }
            else {      
                ImageView imageView = new ImageView(this);
                imageView.setBackgroundResource(R.drawable.bottom1);
                
                LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layout.setMargins(10, 0, 0, 0);
                imageView.setLayoutParams(layout);
                
                imageView.setPadding(10, 10, 10, 10);
                bottomLayout.addView(imageView);
            }
           
        }
    }

}
