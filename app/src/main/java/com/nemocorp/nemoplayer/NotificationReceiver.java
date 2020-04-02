package com.nemocorp.nemoplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.util.Random;

import static com.nemocorp.nemoplayer.MainActivity.NEXT_ACTION;
import static com.nemocorp.nemoplayer.MainActivity.PLAY_ACTION;
import static com.nemocorp.nemoplayer.MainActivity.PREV_ACTION;
import static com.nemocorp.nemoplayer.MainActivity.REPEAT_ACTION;
import static com.nemocorp.nemoplayer.MainActivity.b4;
import static com.nemocorp.nemoplayer.MainActivity.bitmapArray;
import static com.nemocorp.nemoplayer.MainActivity.current;
import static com.nemocorp.nemoplayer.MainActivity.flag;
import static com.nemocorp.nemoplayer.MainActivity.image;
import static com.nemocorp.nemoplayer.MainActivity.k1;
import static com.nemocorp.nemoplayer.MainActivity.loop;
import static com.nemocorp.nemoplayer.MainActivity.mediaPlayer;
import static com.nemocorp.nemoplayer.MainActivity.notificationManager;
import static com.nemocorp.nemoplayer.MainActivity.repeat22;
import static com.nemocorp.nemoplayer.MainActivity.shuffle;
import static com.nemocorp.nemoplayer.MainActivity.song_name;
import static com.nemocorp.nemoplayer.MainActivity.songs;
import static com.nemocorp.nemoplayer.musicpage.seek;


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
            if(k1==1)
            {
                musicpage.check();
            }
            Intent service=new Intent(context, StickyService.class);
            context.startService(service);
        } else if (PREV_ACTION.equals(action)) {
            if(!Ytsearch.streaming) {
                if (flag != false) {
                    if (current == 0)
                        current = 0;
                    else
                        current = current - 1;
                    if (shuffle == true) {
                        Random r = new Random();
                        current = r.nextInt(songs.size() - 1);
                    }
                    if (mediaPlayer != null)
                        mediaPlayer.reset();
                    try {
                        mediaPlayer.setDataSource(songs.get(current));
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        if (loop == true)
                            mediaPlayer.setLooping(true);
                        MainActivity.details();
                        MainActivity.createThread();
                        Intent service = new Intent(context, StickyService.class);
                        context.startService(service);
                        //notification(context);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (k1 == 1) {
                        changephotoactivity2();
                    }
                }
            }
            } else if(NEXT_ACTION.equals(action))
            {
                if(!Ytsearch.streaming) {
                if(flag!=false) {
                    if (current == songs.size() - 1)
                        current =0;
                    else
                        current=current+1;
                    if (shuffle == true) {
                        Random r = new Random();
                        current = r.nextInt(songs.size() - 1);
                    }
                    if(mediaPlayer!=null)
                        mediaPlayer.reset();
                    try {
                        mediaPlayer.setDataSource(songs.get(current));
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        if(loop==true)
                            mediaPlayer.setLooping(true);
                        MainActivity.details();
                        MainActivity.createThread();
                        Intent service=new Intent(context, StickyService.class);
                        context.startService(service);
                        //notification(context);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(k1==1)
                    {changephotoactivity2();}
                    }
            }
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
        int min=(mediaPlayer.getDuration()/1000)/60;
        int sec=(mediaPlayer.getDuration()/1000)%60;
        if(sec>=10)
            musicpage.t3.setText(min+":"+sec);
        else
            musicpage.t3.setText(min+":0"+sec);
        musicpage.t2.setText("0:00");
        seek();
        musicpage.t1.setText(song_name.get(current));
        String k=String.valueOf(bitmapArray.get(current));
        if(k!="null")
            musicpage.i1.setImageBitmap(bitmapArray.get(current));
        else
        {
            musicpage.i1.setImageResource(R.drawable.ic_music_note_black_24dp);
        }
    }
}


