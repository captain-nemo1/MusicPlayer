package com.nemocorp.nemoplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.IOException;

import static com.nemocorp.nemoplayer.MainActivity.PREV_ACTION;
import static com.nemocorp.nemoplayer.MainActivity.YES_ACTION;
import static com.nemocorp.nemoplayer.MainActivity.bitmapArray;
import static com.nemocorp.nemoplayer.MainActivity.current;
import static com.nemocorp.nemoplayer.MainActivity.flag;
import static com.nemocorp.nemoplayer.MainActivity.k1;
import static com.nemocorp.nemoplayer.MainActivity.mediaPlayer;
import static com.nemocorp.nemoplayer.MainActivity.notification;
import static com.nemocorp.nemoplayer.MainActivity.song_name;
import static com.nemocorp.nemoplayer.MainActivity.songs;
import static com.nemocorp.nemoplayer.musicpage.seek;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (YES_ACTION.equals(action)) {
            MainActivity.Pause1();
            if(k1==1)
            {
                musicpage.check();
            }

        } else if (PREV_ACTION.equals(action)) {
            if (flag != false) {
                if(current==0)
                    current=0;
                else
                current = current - 1;
                if (mediaPlayer != null)
                    mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(context, Uri.parse(songs.get(current)));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    MainActivity.createThread();
                    notification(context);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(k1==1)
                {changephotoactivity2();}}
            } else
            {
                if(flag!=false) {
                    if (current == songs.size() - 1)
                        current =0;
                    else
                        current=current+1;
                    if(mediaPlayer!=null)
                        mediaPlayer.reset();
                    try {
                        mediaPlayer.setDataSource(context, Uri.parse(songs.get(current)));
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        MainActivity.createThread();
                        notification(context);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(k1==1)
                    {changephotoactivity2();}
            }
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


