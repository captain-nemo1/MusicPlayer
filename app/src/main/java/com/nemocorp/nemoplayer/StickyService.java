package com.nemocorp.nemoplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.nemocorp.nemoplayer.MainActivity.CANCEL_ACTION;
import static com.nemocorp.nemoplayer.MainActivity.NEXT_ACTION;
import static com.nemocorp.nemoplayer.MainActivity.PLAY_ACTION;
import static com.nemocorp.nemoplayer.MainActivity.PREV_ACTION;
import static com.nemocorp.nemoplayer.MainActivity.REPEAT_ACTION;
import static com.nemocorp.nemoplayer.MainActivity.current;
import static com.nemocorp.nemoplayer.MainActivity.image;
import static com.nemocorp.nemoplayer.MainActivity.mediaPlayer;
import static com.nemocorp.nemoplayer.MainActivity.notificationManager;
import static com.nemocorp.nemoplayer.MainActivity.repeat22;
import static com.nemocorp.nemoplayer.MainActivity.song_album;
import static com.nemocorp.nemoplayer.MainActivity.song_artist;
import static com.nemocorp.nemoplayer.MainActivity.song_name;
import static com.nemocorp.nemoplayer.MainActivity.songs;

public class StickyService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    static MediaMetadata.Builder mediaMetadataBuilder;
    @Override
    public void onCreate()
    {
    super.onCreate();

    }


    public int onStartCommand(Intent i, int flags, int startId )
    {
        mediaMetadataBuilder=new MediaMetadata.Builder();
        mediaMetadataBuilder.putLong(MediaMetadata.METADATA_KEY_DURATION, 0);
        MediaSessionCompat mediaSession=new MediaSessionCompat(getApplicationContext(),"session");
        mediaSession.setMetadata(MediaMetadataCompat.fromMediaMetadata(mediaMetadataBuilder.build()));
        MediaSessionCompat.Token token=mediaSession.getSessionToken();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Bitmap icon;
        MediaMetadataRetriever m = new MediaMetadataRetriever();
        m.setDataSource(songs.get(current));
            try {
                byte[] a = m.getEmbeddedPicture();
                Bitmap c = BitmapFactory.decodeByteArray(a, 0, a.length);
                icon = c;
            } catch (Exception e) {
                icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.noicon);
            }
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
                .setSmallIcon(R.drawable.music)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle(song_name.get(current))
                .setContentText(song_artist.get(current))
                .setSubText(song_album.get(current))
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
                .setOnlyAlertOnce(true)
                .setTimeoutAfter(mediaPlayer.getDuration())
                .setPriority(NotificationCompat.PRIORITY_LOW);

        Notification notification= MainActivity.builder.build();
        notificationManager = NotificationManagerCompat.from(this);
        startForeground(101, notification);
        super.onStartCommand(i,flags,101);
        return START_NOT_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent intent)
    {
        if(notificationManager!=null)
        {notificationManager.cancel(101);
        stopForeground(true);}

    }
}
