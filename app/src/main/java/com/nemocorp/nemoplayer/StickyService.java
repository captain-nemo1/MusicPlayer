package com.nemocorp.nemoplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.session.MediaButtonReceiver;

import static com.nemocorp.nemoplayer.MainActivity.CANCEL_ACTION;
import static com.nemocorp.nemoplayer.MainActivity.NEXT_ACTION;
import static com.nemocorp.nemoplayer.MainActivity.PLAY_ACTION;
import static com.nemocorp.nemoplayer.MainActivity.PREV_ACTION;
import static com.nemocorp.nemoplayer.MainActivity.REPEAT_ACTION;
import static com.nemocorp.nemoplayer.MainActivity.builder;
import static com.nemocorp.nemoplayer.MainActivity.con_main;
import static com.nemocorp.nemoplayer.MainActivity.current;
import static com.nemocorp.nemoplayer.MainActivity.image;
import static com.nemocorp.nemoplayer.MainActivity.k1;
import static com.nemocorp.nemoplayer.MainActivity.mediaPlayer;
import static com.nemocorp.nemoplayer.MainActivity.notificationManager;
import static com.nemocorp.nemoplayer.MainActivity.repeat22;
import static com.nemocorp.nemoplayer.MainActivity.song_artist;
import static com.nemocorp.nemoplayer.MainActivity.song_name;
import static com.nemocorp.nemoplayer.MainActivity.songs;

public class StickyService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    static MediaMetadata.Builder mediaMetadataBuilder=new MediaMetadata.Builder();
    static MediaSessionCompat mediaSession;
    static Bitmap icon;
    public static PlaybackStateCompat state;
    @Override
    public void onCreate()
    {
        state = new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, 0, 0, 0)
                .build();
        mediaSession.setPlaybackState(null);
        mediaSession.setActive(true);
        super.onCreate();
        if(mediaPlayer!=null)
            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        headphone_unplug();
    }
    private BroadcastReceiver NoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {  //need to do for mediaplayer2 for now
            if( mediaPlayer!= null && mediaPlayer.isPlaying() ) {
                MainActivity.wantsmusic=true; image=R.drawable.pause;
                MainActivity.Pause1();
                if(k1==1)
                {
                    musicpage.check();
                }
                Intent service=new Intent(context, StickyService.class);
                context.startService(service);
            }
        }
    };
    public void headphone_unplug()
    {
        IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(NoisyReceiver, filter);
    }
    public static void metadata()
    {
        try {
            if(!Ytsearch.streaming) {
                mediaMetadataBuilder.putString(MediaMetadata.METADATA_KEY_TITLE, song_name.get(current));//new
                mediaMetadataBuilder.putString(MediaMetadata.METADATA_KEY_ARTIST, song_artist.get(current));//new
                mediaMetadataBuilder.putLong(MediaMetadata.METADATA_KEY_DURATION, 0);
            }
            else
            {
                mediaMetadataBuilder.putString(MediaMetadata.METADATA_KEY_TITLE,MainActivity.stream_name);
                mediaMetadataBuilder.putString(MediaMetadata.METADATA_KEY_ARTIST, MainActivity.stream_channel);//new
                mediaMetadataBuilder.putLong(MediaMetadata.METADATA_KEY_DURATION, 0);
            }
            mediaMetadataBuilder.putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, icon);
            mediaSession.setMetadata(MediaMetadataCompat.fromMediaMetadata(mediaMetadataBuilder.build()));//not ne
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void setting_icon()
    {
        if(!Ytsearch.streaming) {
            MediaMetadataRetriever m = new MediaMetadataRetriever();
            m.setDataSource(songs.get(current));
            try {
                byte[] a = m.getEmbeddedPicture();
                Bitmap c = BitmapFactory.decodeByteArray(a, 0, a.length);
                icon=c;
            } catch (Exception e) {
                icon = BitmapFactory.decodeResource(con_main.getResources(), R.drawable.album);
            }
        }
        else if(Ytsearch.streaming) {
            icon = MainActivity.stream_thumnail;
            if(MainActivity.k1==1)
            {
                musicpage.song_name_change();
                musicpage.change_tags();
                musicpage.change_Album_Art();
            }
        }
    }

    public int onStartCommand(Intent i, int flags, int startId )
    {
        metadata();
        setting_icon();
        MediaButtonReceiver.handleIntent(mediaSession, i);//new
        MediaSessionCompat.Token token=mediaSession.getSessionToken();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Intent yesReceive2 = new Intent(this, NotificationReceiver.class);
        yesReceive2.setAction(PREV_ACTION);
        PendingIntent pendingIntentYes2 = PendingIntent.getBroadcast(this, 12345, yesReceive2, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent yesReceive = new Intent(this, NotificationReceiver.class);
        yesReceive.setAction(PLAY_ACTION);
        PendingIntent pendingIntentYes = PendingIntent.getBroadcast(this, 12345, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent yesReceive3 = new Intent(this, NotificationReceiver.class);
        yesReceive3.setAction(NEXT_ACTION);
        PendingIntent pendingIntentYes3 = PendingIntent.getBroadcast(this, 12345, yesReceive3, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent yesReceive4 = new Intent(this, NotificationReceiver.class);
        yesReceive4.setAction(CANCEL_ACTION);
        PendingIntent pendingIntentYes4 = PendingIntent.getBroadcast(this, 12345, yesReceive4, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent yesReceive5 = new Intent(this, NotificationReceiver.class);
        yesReceive5.setAction(REPEAT_ACTION);
        PendingIntent pendingIntentYes5 = PendingIntent.getBroadcast(this, 12345, yesReceive5, PendingIntent.FLAG_UPDATE_CURRENT);

        MainActivity.builder = new NotificationCompat.Builder(this, "101")
                .setSmallIcon(R.drawable.ic_music_note_black_24dp)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setLargeIcon(icon)
                .setColorized(true)
                .setColor(Color.BLACK)
                .addAction(repeat22, "Repeat", pendingIntentYes5)
                .addAction(R.drawable.previous, "Prev", pendingIntentYes2)
                .addAction(image, "Play/Pause", pendingIntentYes)
                .addAction(R.drawable.ic_skip_next_black_24dp, "Next", pendingIntentYes3)
                .addAction(R.drawable.cancel, "Cancel", pendingIntentYes4)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(1, 2, 3).setMediaSession(token))
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true);
        if(!Ytsearch.streaming)
        {
            builder.setContentTitle(song_name.get(current))
                    .setContentText(song_artist.get(current))
                    .setTicker(song_name.get(current));
        }
        else
        {builder.setContentTitle(MainActivity.stream_name);
            builder.setContentText(MainActivity.stream_channel);
            builder.setTicker(MainActivity.stream_name);
        }
        Notification notification= MainActivity.builder.build();
        notificationManager = NotificationManagerCompat.from(this);
        startForeground(101, notification);
        super.onStartCommand(i,flags,101);
        return START_NOT_STICKY ;
    }

    @Override
    public void onTaskRemoved(Intent intent)
    {
        if(notificationManager!=null)
        {notificationManager.cancel(101);
            unregisterReceiver(NoisyReceiver);
            stopForeground(true);}
        icon=null;
        try {
            unregisterReceiver(NoisyReceiver);
        } catch (java.lang.IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}


