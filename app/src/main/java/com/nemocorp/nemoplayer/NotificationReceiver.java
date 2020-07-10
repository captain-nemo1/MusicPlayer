package com.nemocorp.nemoplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.nemocorp.nemoplayer.MainActivity.NEXT_ACTION;
import static com.nemocorp.nemoplayer.MainActivity.PLAY_ACTION;
import static com.nemocorp.nemoplayer.MainActivity.PREV_ACTION;
import static com.nemocorp.nemoplayer.MainActivity.REPEAT_ACTION;
import static com.nemocorp.nemoplayer.MainActivity.b4;
import static com.nemocorp.nemoplayer.MainActivity.flag;
import static com.nemocorp.nemoplayer.MainActivity.image;
import static com.nemocorp.nemoplayer.MainActivity.k1;
import static com.nemocorp.nemoplayer.MainActivity.loop;
import static com.nemocorp.nemoplayer.MainActivity.mediaPlayer;
import static com.nemocorp.nemoplayer.MainActivity.notificationManager;
import static com.nemocorp.nemoplayer.MainActivity.repeat22;


public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (PLAY_ACTION.equals(action)) {
            if(flag==true)
            {MainActivity.wantsmusic=false; image=R.drawable.ic_play_circle_filled_black_24dp;}
            else
            {MainActivity.wantsmusic=true; image=R.drawable.pause;}
            MainActivity.Pause1();
            MainActivity.requestAudio();
            if(k1==1)
            {
                musicpage.check();
            }
            Intent service=new Intent(context, StickyService.class);
            context.startService(service);
        } else if (PREV_ACTION.equals(action)) {
                    MainActivity.previous1();
            if(k1==1)
            {changephotoactivity2();}
                }
             else if(NEXT_ACTION.equals(action))
            {
                    MainActivity.next1();
                if(k1==1)
                {changephotoactivity2();}


                }

        else if(REPEAT_ACTION.equals(action))
        {
            if(loop==false)
            {
                loop=true;
                mediaPlayer.setLooping(loop);
                repeat22=R.drawable.donot;
                b4.setBackgroundResource(repeat22);
            }
            else
            {
                loop=false;
                mediaPlayer.setLooping(loop);
                repeat22=R.drawable.repeat;
                b4.setBackgroundResource(repeat22);

            }
            Intent service=new Intent(context, StickyService.class);
            context.startService(service);
        }
        else {
            notificationManager.cancel(101);

            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
    public void changephotoactivity2()
    {
        int temp_durr= Math.toIntExact(MainActivity.song_dur.get(MainActivity.current));
        int min=(temp_durr/1000)/60;
        int sec=(temp_durr/1000)%60;
        if(sec>=10)
            musicpage.t3.setText(min+":"+sec);
        else
            musicpage.t3.setText(min+":0"+sec);
        musicpage.t2.setText("0:00");
        //MainActivity.seek();
        musicpage.t1.setText(MainActivity.song_name.get(MainActivity.current));
        musicpage.t4.setText(MainActivity.song_artist.get(MainActivity.current));
        musicpage.change_Album_Art();
    }
}


