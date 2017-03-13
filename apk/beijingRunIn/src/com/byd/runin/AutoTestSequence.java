
package com.byd.runin;

import com.byd.runin._2d._2dActivity;
import com.byd.runin._3d._3dActivity;
import com.byd.runin.camera.CameraActivity;
import com.byd.runin.camera.SwitchCameraActivity;
import com.byd.runin.cpu.CpuActivity;
import com.byd.runin.emmc.EmmcActivity;
import com.byd.runin.front_camera.FrontCameraActivity;
import com.byd.runin.lcd.LcdActivity;
import com.byd.runin.memory.MemoryActivity;
import com.byd.runin.mic.MicActivity;
import com.byd.runin.reboot.RebootActivity;
import com.byd.runin.receiver.ReceiverActivity;
import com.byd.runin.s3.S3Activity;
import com.byd.runin.speaker.SpeakerActivity;
import com.byd.runin.vibrator.VibratorActivity;
import com.byd.runin.video.VideoActivity;

import java.util.ArrayList;

public class AutoTestSequence
{

    private static final long TIME_UNIT = 60 * 1000;
    public static ArrayList < TestEntry > getTestSequence(ArrayList <
        TestListItem > tliList)
    {
        ArrayList < TestEntry > result = new ArrayList < TestEntry > ();
        TestEntry entry = null;
        int index = 0;

        boolean mode = true;

        if( !tliList.get(0).testMode.equals(RuninTestMainActivity.FINAL)){

            if ( tliList.get(0).testMode.equals(RuninTestMainActivity.PVT) ){
                mode = false;
                //cpu test
                TestListItem test_cpu = tliList.get(0);
                entry = new TestEntry();
                entry.cls = CpuActivity.class;
                entry.title = CpuActivity.TITLE;
                entry.time = Long.parseLong(test_cpu.testItemValue) * TIME_UNIT;
                result.add(entry);

                //2d test
                TestListItem test_2d = tliList.get(1);
                entry = new TestEntry();
                entry.cls = _2dActivity.class;
                entry.title = _2dActivity.TITLE;
                entry.time = Long.parseLong(test_2d.testItemValue) * TIME_UNIT;
                result.add(entry);

                //3d test
                TestListItem test_3d = tliList.get(2);
                entry = new TestEntry();
                entry.cls = _3dActivity.class;
                entry.title = _3dActivity.TITLE;
                entry.time = Long.parseLong(test_3d.testItemValue) * TIME_UNIT;
                result.add(entry);

                //Memory Test
                TestListItem test_memory = tliList.get(3);
                entry = new TestEntry();
                entry.cls = MemoryActivity.class;
                entry.title = MemoryActivity.TITLE;
                entry.time = Long.parseLong(test_memory.testItemValue) * TIME_UNIT;
                result.add(entry);

                //EMMC Test
                TestListItem test_emcc = tliList.get(4);
                entry = new TestEntry();
                entry.cls = EmmcActivity.class;
                entry.title = EmmcActivity.TITLE;
                entry.time = Long.parseLong(test_emcc.testItemValue) * TIME_UNIT;
                result.add(entry);
                index = 5;
            }
            //Sleep Test
            TestListItem test_s3 = tliList.get(index);
            entry = new TestEntry();
            entry.cls = S3Activity.class;
            entry.title = S3Activity.TITLE;
            entry.time = Long.parseLong(test_s3.testItemValue);
            entry.done_times = 0;
            result.add(entry);

            //LCD Test
            TestListItem test_lcd = tliList.get(index+1);
            entry = new TestEntry();
            entry.cls = LcdActivity.class;
            entry.title = LcdActivity.TITLE;
            entry.ssMode = mode;
            entry.time = Long.parseLong(test_lcd.testItemValue) * TIME_UNIT;
            result.add(entry);


            /*
            //Rec Test
            TestListItem test_rec = tliList.get(index+3);
            entry = new TestEntry();
            entry.cls = ReceiverActivity.class;
            entry.title = ReceiverActivity.TITLE;
            entry.time = Integer.parseInt(test_rec.testItemValue) * TIME_UNIT;
            result.add(entry);

            //vibrator
            TestListItem test_vibrator = tliList.get(index+4);
            entry = new TestEntry();
            entry.cls = VibratorActivity.class;
            entry.title = VibratorActivity.TITLE;
            entry.time = Integer.parseInt(test_vibrator.testItemValue) * TIME_UNIT;
            result.add(entry);*/

            //Video test
            TestListItem test_video = tliList.get(index+2);
            entry = new TestEntry();
            entry.cls = VideoActivity.class;
            entry.title = VideoActivity.TITLE;
            entry.strMode = "";
            entry.time = Long.parseLong(test_video.testItemValue) * TIME_UNIT;
            result.add(entry);
            /*
            //Speaker test
            TestListItem test_speaker = tliList.get(index+5);
            entry = new TestEntry();
            entry.cls = SpeakerActivity.class;
            entry.title = SpeakerActivity.TITLE;
            entry.time = Integer.parseInt(test_speaker.testItemValue) * TIME_UNIT;
            result.add(entry);*/

            //mic test
            /*
            TestListItem test_mic = tliList.get(index+6);
            entry = new TestEntry();
            entry.cls = MicActivity.class;
            entry.title = MicActivity.TITLE;
            entry.time = Integer.parseInt(test_mic.testItemValue) * TIME_UNIT;
            result.add(entry);*/

            entry = new TestEntry();
            TestListItem test_camera = tliList.get(index+3);
            entry.cls = CameraActivity.class;
            entry.title = CameraActivity.TITLE;
            entry.time = Long.parseLong(test_camera.testItemValue)* TIME_UNIT;
            result.add(entry);

            entry = new TestEntry();
            TestListItem test_switchCamera = tliList.get(index+4);
            entry.cls = SwitchCameraActivity.class;
            entry.title = SwitchCameraActivity.TITLE;
            entry.time = Long.parseLong(test_switchCamera.testItemValue);
            result.add(entry);

            TestListItem test_reboot = tliList.get(index+5);
            entry = new TestEntry();
            entry.cls = RebootActivity.class;
            entry.title = RebootActivity.TITLE;
            entry.time = Long.parseLong(test_reboot.testItemValue);
            result.add(entry);
        }else{
            //cpu test
            TestListItem test_cpu = tliList.get(0);
            entry = new TestEntry();
            entry.cls = CpuActivity.class;
            entry.title = CpuActivity.TITLE;
            entry.time = Long.parseLong(test_cpu.testItemValue) * TIME_UNIT;
            result.add(entry);

            //Memory Test
            TestListItem test_memory = tliList.get(1);
            entry = new TestEntry();
            entry.cls = MemoryActivity.class;
            entry.title = MemoryActivity.TITLE;
            entry.time = Long.parseLong(test_memory.testItemValue) * TIME_UNIT;
            result.add(entry);

            //EMMC Test
            TestListItem test_emcc = tliList.get(2);
            entry = new TestEntry();
            entry.cls = EmmcActivity.class;
            entry.title = EmmcActivity.TITLE;
            entry.time = Long.parseLong(test_emcc.testItemValue) * TIME_UNIT;
            result.add(entry);
            index = 5;

            //Speaker test
            TestListItem test_speaker = tliList.get(3);
            entry = new TestEntry();
            entry.cls = SpeakerActivity.class;
            entry.title = SpeakerActivity.TITLE;
            entry.time = Integer.parseInt(test_speaker.testItemValue) * TIME_UNIT;
            result.add(entry);

            //2d test
            TestListItem test_2d = tliList.get(4);
            entry = new TestEntry();
            entry.cls = _2dActivity.class;
            entry.title = _2dActivity.TITLE;
            entry.time = Long.parseLong(test_2d.testItemValue) * TIME_UNIT;
            result.add(entry);

            //3d test
            TestListItem test_3d = tliList.get(5);
            entry = new TestEntry();
            entry.cls = _3dActivity.class;
            entry.title = _3dActivity.TITLE;
            entry.time = Long.parseLong(test_3d.testItemValue) * TIME_UNIT;
            result.add(entry);

            //Video test
            TestListItem test_video = tliList.get(6);
            entry = new TestEntry();
            entry.cls = VideoActivity.class;
            entry.title = VideoActivity.TITLE;
            entry.strMode = RuninTestMainActivity.FINAL;
            entry.time = Long.parseLong(test_video.testItemValue) * TIME_UNIT;
            result.add(entry);

            //Sleep Test
            TestListItem test_s3 = tliList.get(7);
            entry = new TestEntry();
            entry.cls = S3Activity.class;
            entry.title = S3Activity.TITLE;
            entry.time = Long.parseLong(test_s3.testItemValue);
            entry.done_times = 0;
            result.add(entry);

            entry = new TestEntry();
            TestListItem test_switchCamera = tliList.get(8);
            entry.cls = SwitchCameraActivity.class;
            entry.title = SwitchCameraActivity.TITLE;
            entry.time = Long.parseLong(test_switchCamera.testItemValue);
            result.add(entry);

            TestListItem test_reboot = tliList.get(9);
            entry = new TestEntry();
            entry.cls = RebootActivity.class;
            entry.title = RebootActivity.TITLE;
            entry.time = Long.parseLong(test_reboot.testItemValue);
            result.add(entry);
        }

        return result;
    }
}
