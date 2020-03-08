package com.nemocorp.nemoplayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    static TextView t2;
    static TextView t3;
    TextView t4;
    public static final String PLAY_ACTION = "PLAY_ACTION";
    public static final String PREV_ACTION = "PREV_ACTION";
    public static final String NEXT_ACTION = "NEXT_ACTION";
    public static final String CANCEL_ACTION = "CANCEL_ACTION";
    ListView list;
    PowerManager.WakeLock wakeLock;
    static Button b1, b4, b5;
    static SeekBar s;
    static int current;
    final String CHANNEL_ID = "101";
    RelativeLayout r;
    static String song_art;
    static boolean loop = false;
    LinearLayout l1;
    static int image = R.drawable.pause;
    static boolean flag = false;
    Intent i;
    static boolean shuffle = false;
    static MediaPlayer mediaPlayer;
    static List<String> songs = new ArrayList<String>();
    static NotificationManagerCompat notificationManager;
    static List<String> song_name = new ArrayList<>();
    static List<String> song_id = new ArrayList<>();
    static List<String> song_artist = new ArrayList<>();
    static ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();
    static Thread t = null;
    static Thread th2 = null;
    static Intent service;
    static NotificationCompat.Builder builder;
    AudioManager audioManager;
    static boolean wantsmusic = true;
    static int k1 = 0;
    int first = 0;
    Intent e;
    MediaMetadataRetriever metaRetriver;
    AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {

                case AudioManager.AUDIOFOCUS_GAIN:
                    if (mediaPlayer != null && wantsmusic == true) {
                        Pause1();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    Log.d("Workingloss", "Audio");
                    Pause1();
                    wantsmusic = true;
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    Log.d("WorkingTera", "Audio");
                    if (wantsmusic == false)
                        break;
                    flag = true;
                    Pause1();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                    mediaPlayer.setVolume(0.1f, 0.1f);
                    break;
            }
        }

    };

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        list = findViewById(R.id.list);
        t2 = findViewById(R.id.title);
        t3 = findViewById(R.id.starttime);
        t4 = findViewById(R.id.endtime);
        b1 = findViewById(R.id.play);
        b4 = findViewById(R.id.repeat);
        b5 = findViewById(R.id.shuffle);
        b1.setVisibility(View.INVISIBLE);
        t2.setVisibility(View.INVISIBLE);
        s = findViewById(R.id.seekBar);
        r = findViewById(R.id.r2);
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 101);
        getsong();
        sort();
        settingart();
        service = new Intent(getApplicationContext(), StickyService.class);
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire();
        t = new Thread();
        i = new Intent(MainActivity.this, musicpage.class);
        l1 = findViewById(R.id.layout);
        mediaPlayer = new MediaPlayer();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        ArrayAdapter<String> ar = new ArrayAdapter<>(this, R.layout.row, song_name);
        list.setAdapter(ar);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songname = songs.get(position);
                stopifSongPlaying();
                current = position;
                media(songname);
                first = 1;
                check();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                t.interrupt();
                if (shuffle == true) {
                    Random r = new Random();
                    current = r.nextInt(songs.size() - 1);
                }
                next1();
                if (k1 == 1) {
                    int min = (mediaPlayer.getDuration() / 1000) / 60;
                    int sec = (mediaPlayer.getDuration() / 1000) % 60;
                    if (sec >= 10)
                        musicpage.t3.setText(min + ":" + sec);
                    else
                        musicpage.t3.setText(min + ":0" + sec);
                    musicpage.t2.setText("0:00");
                    musicpage.seek();
                    musicpage.t1.setText(song_name.get(current));
                    musicpage.i1.setImageBitmap(bitmapArray.get(current));
                }
            }
        });
        r.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {
                if (first != 0) {
                    i.putExtra("name", song_name.get(current));
                    int k = mediaPlayer.getCurrentPosition();
                    i.putExtra("prog", String.valueOf(k));
                    String s = songs.get(current);
                    i.putExtra("art", s);
                    startActivity(i);
                    overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
                }
            }
            public void onSwipeBottom() {
                if (first != 0) {
                    i.putExtra("name", song_name.get(current));
                    int k = mediaPlayer.getCurrentPosition();
                    i.putExtra("prog", String.valueOf(k));
                    String s = songs.get(current);
                    i.putExtra("art", s);
                    startActivity(i);
                    overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
                }
            }
            public void onSwipeRight() {
                next1();
            }

            public void onSwipeLeft() {
                previous1();
            }
        });
    }
    public void sort()
    {
        for(int i=0;i<songs.size();i++)
        {
            for(int j=0;j<songs.size()-i-1;j++)
            {
                if(song_name.get(j).compareTo(song_name.get(j+1))>0)
                {
                    String temp=song_name.get(j);
                    song_name.set(j,song_name.get(j+1));
                    song_name.set(j+1,temp);
                    temp=songs.get(j);
                    songs.set(j,songs.get(j+1));
                    songs.set(j+1,temp);
                }
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        fileopen();
    }
    public void fileopen() {
        String name="";
        e = getIntent();
        Uri p1 = e.getData();
        String l = String.valueOf(p1);
        if (!l.equals("null")) {
            Cursor cursor = getContentResolver().query(p1, null, null, null, null);
            if (cursor != null && cursor.getCount() != 0) {
                cursor.moveToFirst();
                name = cursor.getString(2);
            }
            if (cursor != null) {
                cursor.close();
            }
            int k = 0;
            for (int i = 0; i < songs.size(); i++) {
                if (name.equalsIgnoreCase(songs.get(i))) {
                    k = i;
                    break;
                }
            }
            current = k;
            media(songs.get(current));
            first = 1;
            check();
            e.setData(null);
        }
    }
    public void Top(View view) {
        if (first != 0) {
            i.putExtra("name", song_name.get(current));
            int k = mediaPlayer.getCurrentPosition();
            i.putExtra("prog", String.valueOf(k));
            String s = songs.get(current);
            i.putExtra("art", s);
            startActivity(i);
            overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
        }
    }

    public void settingart() {
        th2 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < songs.size(); i++) {
                    metaRetriver = new MediaMetadataRetriever();
                    metaRetriver.setDataSource(songs.get(i));
                    try {
                        String s = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                        song_art = s;
                    } catch (Exception e) {
                        song_art = "Unknown";
                    }
                    song_artist.add(song_art);
                    try {
                        byte[] art2 = metaRetriver.getEmbeddedPicture();
                        bitmapArray.add(BitmapFactory.decodeByteArray(art2, 0, art2.length));
                    } catch (Exception e) {
                        bitmapArray.add(null);
                    }
                }
            }
        };
        th2.start();
    }

    public void check() {
        if (MainActivity.flag == true)
            b1.setBackgroundResource(R.drawable.pause);
        else
            b1.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
    }
    public void requestAudio() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.d("AudioFocus", "Audio focus received");
        } else {
            Log.d("AudioFocus", "Audio focus NOT received");
        }
    }
    static public void shuffleon1() {
        if (shuffle == true) {
            shuffle = false;
            b5.setBackgroundResource(R.drawable.shuffle1);

        } else {
            shuffle = true;
            b5.setBackgroundResource(R.drawable.shuffle2);
        }

    }
    public void shuffleon(View view) {
        shuffleon1();
    }
    public void stopifSongPlaying() {
        if (flag == true) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                flag = false;
                mediaPlayer.reset();
                s.setProgress(0);
                t3.setText("0:00");
            }
            if (t.isAlive())
                t.interrupt();
        }
    }
    public void Pause(View view) {
        if (flag == true) {
            wantsmusic = false;
        } else
            wantsmusic = true;
        Pause1();
        startService(service);
    }
    static public void Pause1() {
        if (flag == true) {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
                flag = false;
                Log.d("Working1", "Audio");
                b1.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
                image = R.drawable.ic_play_circle_filled_black_24dp;
            }
        } else {
            if (mediaPlayer != null) {
                mediaPlayer.start();
                flag = true;
                b1.setBackgroundResource(R.drawable.pause);
                image = R.drawable.pause;
            }
        }
    }
    public void media(String path) {
        b1.setVisibility(View.VISIBLE);
        Uri mp3 = Uri.parse(path);
        t3.setText("0:00");
        requestAudio();
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getApplicationContext(), mp3);
            mediaPlayer.prepare();
            mediaPlayer.start();
            int min = (mediaPlayer.getDuration() / 1000) / 60;
            int sec = (mediaPlayer.getDuration() / 1000) % 60;
            if (sec < 10)
                t4.setText(min + ":0" + sec);
            else
                t4.setText(min + ":" + sec);
            flag = true;
            startService(service);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MEDIAPLAYER ERROR", String.valueOf(e));
        }
        createThread();
    }
    static public void createThread() {
        String path = songs.get(current);
        t2.setText(song_name.get(current));
        t2.setVisibility(View.VISIBLE);
        t2.setSelected(true);
        if (t.isAlive())
            t.interrupt();
        t = new Thread() {
            @Override
            public void run() {
                int currentPosition = 0;
                int total = mediaPlayer.getDuration();
                s.setMax(total);
                while (mediaPlayer != null && currentPosition < total) {
                    try {
                        Thread.sleep(1000);
                        currentPosition = mediaPlayer.getCurrentPosition();
                    } catch (InterruptedException e) {
                        return;
                    }
                    s.setProgress(currentPosition);
                    int min = (currentPosition / 1000) / 60;
                    int sec = (currentPosition / 1000) % 60;
                    if (sec < 10)
                        t3.setText(min + ":0" + sec);
                    else
                        t3.setText(min + ":" + sec);
                }
            }

        };
        t.start();
        s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromuser) {
                if (mediaPlayer != null && fromuser)
                    mediaPlayer.seekTo(i);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
    public void previous1() {
        if (flag == true) {
            if (current == 0) {
                current = 0;
            } else {
                current = current - 1;
            }
            String prevsong = songs.get(current);
            stopifSongPlaying();
            media(prevsong);
            startService(service);
        } else
            return;
    }
    public void previous(View view) {
        previous1();
    }
    public void next1() {
        if (flag == true) {
            if (current == songs.size() - 1)
                current = 0;
            else
                current = current + 1;
            String nextsong = songs.get(current);
            stopifSongPlaying();
            media(nextsong);
            startService(service);
        } else
            return;
    }
    public void next(View view) {
        next1();
    }
    static public void repeat1() {
        if (loop == false) {
            loop = true;
            mediaPlayer.setLooping(loop);
            b4.setBackgroundResource(R.drawable.donot);
        } else {
            loop = false;
            mediaPlayer.setLooping(loop);
            b4.setBackgroundResource(R.drawable.repeat);
        }
    }
    static public void repeat(View view) {
        repeat1();
    }
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this,
                        "Storage Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(MainActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void getsong() {

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        ContentResolver contentResolver = this.getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };
        Cursor songCursor = contentResolver.query(songUri, projection, selection, null, MediaStore.Audio.Media.DISPLAY_NAME + " ASC");
        if (songCursor != null && songCursor.moveToFirst()) {
            do {
                String s = songCursor.getString(3);
                String k1 = songCursor.getString(0);
                int k = Integer.parseInt(songCursor.getString(5));
                if (k > 60000) {
                    songs.add(s);
                    song_name.add(s.substring(s.lastIndexOf("/") + 1, s.lastIndexOf('.')));
                    song_id.add(k1);
                }
            } while (songCursor.moveToNext());
        }
        songCursor.close();
        for(int i=0;i<songs.size();i++)
        {
            if(Character.isDigit(song_name.get(i).charAt(0)))
            {
                String c=song_name.get(i).substring(2,song_name.get(i).length()).trim();
                song_name.set(i,c);
            }
        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        wakeLock.release();
        mediaPlayer.stop();
        flag = false;
        mediaPlayer.reset();
        stopService(service);
        songs.clear();
        song_name.clear();
        notificationManager.cancelAll();
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}