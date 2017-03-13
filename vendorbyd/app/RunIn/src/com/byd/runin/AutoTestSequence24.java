
package com.byd.runin;

import java.util.ArrayList;

import com.byd.runin._2d._2dActivity;
import com.byd.runin._3d._3dActivity;
import com.byd.runin.audio.AudioActivity;
import com.byd.runin.battery.BatteryActivity;
import com.byd.runin.camera.CameraActivity;
import com.byd.runin.cpu.CpuActivity;
import com.byd.runin.emmc.EmmcActivity;
import com.byd.runin.lcd.LcdActivity;
import com.byd.runin.memory.MemoryActivity;
import com.byd.runin.reboot.RebootActivity;
import com.byd.runin.s3.S3Activity;
import com.byd.runin.video.VideoActivity;
import com.byd.runin.front_camera.FrontCameraActivity;
    ///A:yushengqiang for Spark 20140604

public class AutoTestSequence24
{
    public static int cpu_time = 480 * 60 * 1000;
    public static int emmc_time = 120 * 60 * 1000;
    public static int memory_time = 120 * 60 * 1000;
    public static int video_time = 180 * 60 * 1000;
    public static int lcd_time = 120 * 60 * 1000;
    public static int _2d_time = 180 * 60 * 1000;
    public static int _3d_time = 240 * 60 * 1000;
    public static int camera_time = 10 * 1000;
    public static int front_camera_time = 10 * 1000;
    public static long getTime()
    {
        return cpu_time + emmc_time + memory_time + video_time + lcd_time +
            _2d_time + _3d_time + camera_time + front_camera_time;
    }
    public static ArrayList < TestEntry > getTestSequence()
    {
        ArrayList < TestEntry > result = new ArrayList < TestEntry > ();

        TestEntry entry = new TestEntry();
        entry.cls = CpuActivity.class;
        entry.title = CpuActivity.TITLE;
        entry.time = cpu_time;
        result.add(entry);

        entry = new TestEntry();
        entry.cls = EmmcActivity.class;
        entry.title = EmmcActivity.TITLE;
        entry.time = emmc_time;
        result.add(entry);

        entry = new TestEntry();
        entry.cls = MemoryActivity.class;
        entry.title = MemoryActivity.TITLE;
        entry.time = memory_time;
        result.add(entry);

        //        entry = new TestEntry();
        //        entry.cls = AudioActivity.class;
        //        entry.title = AudioActivity.TITLE;
        //        entry.time = 144 * 60 * 1000;
        //        result.add(entry);

        entry = new TestEntry();
        entry.cls = VideoActivity.class;
        entry.title = VideoActivity.TITLE;
        entry.time = video_time;
        result.add(entry);

        entry = new TestEntry();
        entry.cls = LcdActivity.class;
        entry.title = LcdActivity.TITLE;
        entry.time = lcd_time;
        result.add(entry);

        entry = new TestEntry();
        entry.cls = _2dActivity.class;
        entry.title = _2dActivity.TITLE;
        entry.time = _2d_time;
        result.add(entry);

        entry = new TestEntry();
        entry.cls = _3dActivity.class;
        entry.title = _3dActivity.TITLE;
        entry.time = _3d_time;
        result.add(entry);

        entry = new TestEntry();
        entry.cls = CameraActivity.class;
        entry.title = CameraActivity.TITLE;
        entry.time = camera_time;
        entry.cameratimes = 100;
        result.add(entry);

        entry = new TestEntry();
        entry.cls = FrontCameraActivity.class;
        entry.title = FrontCameraActivity.TITLE;
        entry.time = front_camera_time;
        entry.cameratimes = 100;
        result.add(entry);

        //        entry = new TestEntry();
        //        entry.cls = BatteryActivity.class;
        //        entry.title = BatteryActivity.TITLE;
        //        entry.time = 10 * 60 * 1000;
        //        result.add(entry);

        entry = new TestEntry();
        entry.cls = S3Activity.class;
        entry.title = S3Activity.TITLE;
        entry.time = 50;
            //s3 use counts to test,may use time to test have some problem.
        result.add(entry);

        entry = new TestEntry();
        entry.cls = RebootActivity.class;
        entry.title = RebootActivity.TITLE;
        entry.time = 50;
        result.add(entry);

        return result;
    }
}
