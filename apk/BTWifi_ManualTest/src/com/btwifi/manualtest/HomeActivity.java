package com.btwifi.manualtest;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import android.os.SystemProperties;
public class HomeActivity extends FragmentActivity implements
        ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    public static String TAG = "BtWifi test";
    SectionsPagerAdapter mSectionsPagerAdapter;
    protected static int pagesNum = 2;
    public static int BT_TAB = 0;
    public static int WIFI_TAB = 1;
    public static String[] wifiLabels = new String[]{""};
    //public static String[] btLabels = new String[]{"TX Test", "RX Test", "LE TX", "LE RX"};
    public static String[] btLabels = new String[]{"BLE TestMode"};
    public static String[] wifiLabels_activity = new String[]{""};
    //public static String[] btLabels_activity = new String[]{"TxtestActivity", "RxtestActivity", "LetxActivity", "LerxActivity"};
    public static String[] btLabels_activity = new String[]{"BletestmodeActivity"};
    public static int nowPosition;
    public static String openBtCommand = "echo 1 > /data/test1";
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        actionBar.setSelectedNavigationItem(position);
                    }
                });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab()
                    .setText(mSectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab,
            FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        int tabPos = tab.getPosition();
        if(tabPos == 0) {
            openBt();
            SystemProperties.set("persist.service.daemon.enable", "1");
        } else {
            openWifi();
            SystemProperties.set("persist.service.daemon.enable", "1");
        }
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab,
            FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab,
            FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            nowPosition = position;
            Fragment fragment = new DummySectionFragment();            
            Bundle args = new Bundle();
            args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            // Show number total pages.
            return pagesNum;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_bt).toUpperCase(l);
                case 1:
                    return getString(R.string.title_wifi).toUpperCase(l);
                    //add more menu here
                /*case 2:
                    return getString(R.string.title_section3).toUpperCase(l);*/
            }
            return null;
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";
        public int pos = 0;
        public DummySectionFragment() {
            pos = nowPosition;
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home_dummy,
                    container, false);
            ListView list = (ListView) rootView
                    .findViewById(R.id.list);
            if(pos == BT_TAB) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, btLabels);
                list.setAdapter(adapter);
                list.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                            int index, long arg3) {
                        jumptoActivity(0, index);                       
                    }
                });
                
            }else if (pos == WIFI_TAB) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, wifiLabels);
                list.setAdapter(adapter);
                list.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                            int index, long arg3) {
                        jumptoActivity(1, index);                       
                    }
                  
                });
                
            }
            return rootView;
        }

        private void jumptoActivity(int flag, int index) {
            Intent intent = new Intent();
            if(flag == BT_TAB) {
                intent.setClassName("com.btwifi.manualtest", 
                        "com.btwifi.manualtest" + "." + btLabels_activity[index]);
                //intent.putExtra("index", index);
                startActivity(intent);
            } else if(flag == WIFI_TAB) {
                intent.setClassName("com.btwifi.manualtest", 
                        "com.btwifi.manualtest" + "." + wifiLabels_activity[index]);
                startActivity(intent);                 
            }
            
        }

 
    }

        private void openBt() {
        Log.i(TAG, "openBT ....");
        ShellExecute cmdexe = new ShellExecute();
        String re = "";
        //String ARGS = "btconfig /dev/smd3 reset && btconfig /dev/smd3 rawcmd 0x06, 0x03 && btconfig /dev/smd3 rawcmd 0x03, 0x05, 0x02, 0x00, 0x02 && btconfig /dev/smd3 rawcmd 0x03, 0x1A, 0x03 && btconfig /dev/smd3 rawcmd 0x03, 0x20, 0x00 && btconfig /dev/smd3 rawcmd 0x03, 0x22, 0x00";
        String ARGS = "echo 19 > /data/citflag";
        try {
            re = cmdexe.execute(ARGS,"/");
        }catch(IOException e) {
        }
        Log.i(TAG, "cmd result:" + re);

    }
    private void openWifi() {
        Log.i(TAG, "openwifi ....");
        ShellExecute cmdexe = new ShellExecute();
        String re = "";
        String ARGS = "echo 20 > /data/citflag && echo /system/xbin/enterwifi > /data/btwifi";
        try {
            re = cmdexe.execute(ARGS,"/");
        }catch(IOException e) {
        }
        Log.i(TAG, "cmd result:" + re);
        
    }
    private class ShellExecute {
        public String execute (String command, String directory) throws IOException{
            String result = "";
            Log.i("qianyan", "cmd ShellExecute start");
            List<String> cmds = new ArrayList<String>();
            cmds.add("sh");
            cmds.add("-c");
            cmds.add(command);
            try {
                ProcessBuilder builder = new ProcessBuilder(cmds);
                if(directory != null) {
                    builder.directory(new File(directory));
                }
                Log.i("qianyan", "cmd ShellExecute cmds=" + cmds);
                builder.redirectErrorStream(true);
                Process process = builder.start();

                //get result
                InputStream is = process.getInputStream();
                byte[] buffer = new byte[1024];
                while(is.read(buffer) != -1) {
                    result = result + new String(buffer);
                }
                is.close();
            
            }catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        String re = "";
        SystemProperties.set("persist.service.daemon.enable", "0");
        ShellExecute cmdexe = new ShellExecute();
        String ARGS = "echo 0 > /data/citflag && rm /data/btwifi";
        try {
            re = cmdexe.execute(ARGS,"/");
        }catch(IOException e) {
        }
    }
}
