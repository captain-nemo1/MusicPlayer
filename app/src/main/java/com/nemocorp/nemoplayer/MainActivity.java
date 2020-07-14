package com.nemocorp.nemoplayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.flurry.android.FlurryAgent;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nemocorp.nemoplayer.adapter.MainList;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;
import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;
import static com.nemocorp.nemoplayer.StickyService.mediaSession;
import static com.nemocorp.nemoplayer.Ytsearch.streaming;


@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {
    static TextView t2;
    static TextView t3;
    static TextView t4;
    public static final String PLAY_ACTION = "PLAY_ACTION";
    public static final String PREV_ACTION = "PREV_ACTION";
    public static final String NEXT_ACTION = "NEXT_ACTION";
    public static final String CANCEL_ACTION = "CANCEL_ACTION";
    public static final String REPEAT_ACTION = "REPEAT_ACTION";
    static RecyclerView list;
    PowerManager.WakeLock wakeLock;
    static Button b1, b4, b5;
    static SeekBar s;
    static int current;
    final String CHANNEL_ID = "101";
    static RelativeLayout r;
    static boolean loop = false;
    static LinearLayout l1;
    static MenuItem search;
    static boolean perm_granted=false;
    static int image = R.drawable.pause;
    static int repeat22= R.drawable.repeat;
    static boolean flag = false;
    static int download_finished=0;
    Intent i;
    static boolean shuffle = false;
    static MediaPlayer mediaPlayer;
    public static List<String> songs = new ArrayList<String>();
    static NotificationManagerCompat notificationManager;
    public static List<String> song_name = new ArrayList<>();
    static List<String> song_id = new ArrayList<>();
    public static List<String> song_artist = new ArrayList<>();
    static List<String> song_album = new ArrayList<>();
    public static List<SongItems> songInfo=new ArrayList<>();
    ArrayList<String> temp;
    ArrayList<String> temp2;
    ArrayList<String> temp3;
    public static List<String> playlist_songs=new ArrayList<>();
    public static List<String> playlist_song_name=new ArrayList<>();
    public static List<String> playlist_song_artist=new ArrayList<>();

    public static ArrayList<Integer> temp4=new ArrayList<>();;
    static List<Long> song_dur = new ArrayList<>();
    static String stream_name;
    static String stream_channel;
    static Bitmap stream_thumnail;
    static Thread t = null;
    static Thread bit=null;
    static Intent service;
    static NotificationChannel channel;
    static NotificationCompat.Builder builder;
    static AudioManager audioManager;
    static boolean wantsmusic;
    static int k1 = 0;
    static  BroadcastReceiver onComplete=null;
    static int first = 0;
    static BottomNavigationView bv;
    Intent e;
    static MainList adap=new MainList();
    static boolean internet_flag;
    public static Activity main;
    AlertDialog.Builder dialog;
    static Context con_main;
    MediaMetadataRetriever metaRetriver;
    static Document doc=null;
    static Document imagedoc=null;
    static URL imageurl;
    static Thread ch;
    static List_Async ls;
    static boolean byCall=false;
    static TelephonyManager telephonyManager;
    static PhoneStateListener callStateListener;
    SharedPreferences sharedPreferences;
    public static int playlist_play=0;
    FragmentTransaction ftx = null;
    static int playlist_index;
    static String prev_bottom_view=String.valueOf(R.id.home); //for animation select for action_downloader Fragment
    static AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
                        switch (focusChange) {

                case AudioManager.AUDIOFOCUS_GAIN:
                    if (mediaPlayer != null && wantsmusic == true ) {
                        Pause1();
                        image=R.drawable.pause;
                        wantsmusic=false;
                         //update_playback(mediaPlayer.getCurrentPosition());
                        con_main.startService(service);
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    if(mediaPlayer.isPlaying()) {
                        Pause1();
                        image = R.drawable.ic_play_circle_filled_black_24dp;
                        con_main.startService(service);
                        //update_playback(mediaPlayer.getCurrentPosition());
                        wantsmusic = true;
                    }
                        break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    //if (wantsmusic == false)
                      //  break;
                    if(mediaPlayer.isPlaying()) {
                        flag = true;
                        Pause1();
                        image = R.drawable.ic_play_circle_filled_black_24dp;
                        con_main.startService(service);
                        //update_playback(mediaPlayer.getCurrentPosition());
                        wantsmusic = true;
                    }
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
        Log.i("values", "BackPressed");
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 101);
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 102);
        new FlurryAgent.Builder()
                .withLogEnabled(true)
                .build(this, "P48MJ6P4FYR8WJ836BJ8");
        sharedPreferences=getSharedPreferences("NEMO", MODE_PRIVATE);
    }


    public void all_Stuff()
    {
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
            .setDownsampleEnabled(true)
                .setDiskCacheEnabled(true)
                .build();
        Fresco.initialize(this,config);
        final Toolbar yourToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(yourToolbar);
        createNotificationChannel();
        wantsmusic=true;
        dialog=new AlertDialog.Builder(this);
      //  list = findViewById(R.id.list);
        //list.setLongClickable(true);
        main= (Activity) this;
        t2 = findViewById(R.id.title);
        t3 = findViewById(R.id.starttime);
        t4 = findViewById(R.id.endtime);
        t4 = findViewById(R.id.endtime);
        b1 = findViewById(R.id.play);
        b4 = findViewById(R.id.repeat);
        b5 = findViewById(R.id.shuffle);
        con_main=getApplicationContext();
        internet_flag=Check_Internet(this);
        if(internet_flag) {
            scrap_speed();//improves scraping speed in Ytsearch
        }
        b1.setVisibility(View.INVISIBLE);
        t2.setVisibility(View.INVISIBLE);
        bv=findViewById(R.id.bottom_view);
        s = findViewById(R.id.seekBar);
        s.setEnabled(false);
        r = findViewById(R.id.r2);
       // getsong(this);
        mediaSession=new MediaSessionCompat(getApplicationContext(),"NemoCorp");
        appendLog("Start Session");
        service = new Intent(getApplicationContext(), StickyService.class);
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire();
        i = new Intent(MainActivity.this, musicpage.class);
        l1 = findViewById(R.id.layout);
        r=findViewById(R.id.r2);
        t=new Thread();
        mediaPlayer = new MediaPlayer();
        Fragment yt=new Ytsearch();
        Fragment home =new home_Fragment();
        Fragment play=new Playlist();
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .add(R.id.frame_layout,home)
                .add(R.id.frame_layout,play)
                .add(R.id.frame_layout,yt)
                .hide(home)
                .hide(play)
                .hide(yt)
                .commit();
        telephonyManager = (TelephonyManager)getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        callStateListener = new PhoneStateListener();
        stop_call(this);
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                mediaPlayer.reset();
                mediaPlayer.stop();
                return false;
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.i("values", "SONG COMPLETE 1 in MAIN");
                appendLog("SONG COMPLETE 1 in MAIN");

                if(t.isAlive())
                {t.interrupt();Log.i("values", " INTERUPPTING THREAD ON COMPLETE");
                    appendLog(" INTERUPPTING THREAD ON COMPLETE");
                }
                if (!streaming) {
                    if (shuffle == true) {
                        Random r = new Random();
                        current = r.nextInt(songs.size() - 1);
                    }
                    if(playlist_play==1)
                    {
                        from_playlist("next");
                    }
                    try {
                        next1();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    if(loop)//when repeating start thread again
                        t.start();

                    if (k1 == 1 && !streaming) {
                        int min = (int)((song_dur.get(current) / 1000) / 60);
                        int sec = (int) ((song_dur.get(current) / 1000) % 60);
                        if (sec >= 10)
                            musicpage.t3.setText(min + ":" + sec);
                        else
                            musicpage.t3.setText(min + ":0" + sec);
                        musicpage.t2.setText("0:00");
                        musicpage.seek();
                        musicpage.t1.setText(song_name.get(current));
                        String temp5=song_artist.get(current);
                        if(!temp5.equals("<unknown>"))
                            musicpage.t4.setText(song_artist.get(current));
                        else
                            musicpage.t4.setText("");
                     /*   MediaMetadataRetriever m = new MediaMetadataRetriever();
                        try {
                            m.setDataSource(songs.get(MainActivity.current));
                            byte[] a = m.getEmbeddedPicture();
                            Bitmap c = BitmapFactory.decodeByteArray(a, 0, a.length);
                            Drawable d = new BitmapDrawable(getResources(), c);
                            musicpage.i1.setBackground(d);
                            musicpage.background(c);
                        } catch (Exception e) {
                            musicpage.i1.setBackground(getResources().getDrawable(R.drawable.ic_music_note_black_24dp));
                            musicpage.c1.setBackgroundColor(getResources().getColor(R.color.black));
                        }*/
                        musicpage.change_Album_Art();
                        Log.i("values", "SONG COMPLETE STARTING NEXT IN MAIN");
                        appendLog("SONG COMPLETE STARTING NEXT IN MAIN");
                    }
                }
            }});
        r.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {
                if (!streaming) {
                    if (first != 0) {
                        Log.i("values", "SWIPE UP MAIN");
                        appendLog("SWIPE UP MAIN");
                        i.putExtra("name", song_name.get(current));
                        int k = mediaPlayer.getCurrentPosition();
                        i.putExtra("prog", String.valueOf(k));
                        String s = songs.get(current);
                        i.putExtra("art", s);
                        startActivity(i);
                    }
                }
            }
            public void onSwipeBottom() {
                if (!streaming) {
                    if (first != 0) {
                        Log.i("values", "SWIPE DONW MAIN");
                        appendLog("SWIPE DONW MAIN");

                        int k = mediaPlayer.getCurrentPosition();
                        i.putExtra("prog", String.valueOf(k));
                        String s = songs.get(current);
                        i.putExtra("art", s);
                        startActivity(i);
                    }
                }
            }
            public void onSwipeRight() {
                if (!streaming) {
                    if (shuffle == true) {
                        Log.i("values", "SWIPE RIGHT MAIN");
                        appendLog("SWIPE RIGHT MAIN");
                        Random r = new Random();
                        current = r.nextInt(songs.size() - 1);
                    }
                    next1();
                }
            }
            public void onSwipeLeft() {
                if (!streaming) {
                    if (shuffle == true) {
                        Log.i("values", "SWIPE LEFT MAIN");
                        appendLog("SWIPE LEFT MAIN");

                        Random r = new Random();
                        current = r.nextInt(songs.size() - 1);
                    }
                    previous1();
                }
            }
        });
        bv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Log.i("values", "IN BOTTOM BAR MAIN");
                appendLog("IN BOTTOM BAR MAIN");
                switch(menuItem.getItemId())
                {
                    case R.id.action_downloader:
                        Log.i("values", "CLICKED DOWNLOADER");
                        appendLog("CLICKED DOWNLOADER");
                        search.setEnabled(false);
                        try {//Need to add try catch otherwise gives problem when musicpage onBackPressed
                            if (prev_bottom_view.equals(String.valueOf(R.id.home)))
                                fm.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).show(yt).hide(play).hide(home).commit();
                            else
                                fm.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right).show(yt).hide(play).hide(home).commit();
                        } catch (Exception ex) {
                        }
                        prev_bottom_view= String.valueOf(R.id.action_downloader);
                        break;
                    case R.id.action_player:
                            if (first != 0) {
                                Log.i("values", "CLICKED PLAYER");
                                appendLog("CLICKED PLAYER");
                                Intent i2 = new Intent(getApplicationContext(), musicpage.class);
                                startActivity(i2);
                            }
                        break;
                    case R.id.action_playlist:
                        search.setEnabled(false);
                        try {
                            fm.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).show(play).hide(home).hide(yt).commit();
                        } catch (Exception ex) {
                        }//Need to add try catch otherwise gives problem when musicpage onBackPressed
                        prev_bottom_view= String.valueOf(R.id.action_playlist);
                        break;
                    case R.id.home:
                        try {//android.R.anim are system inbuild.
                            fm.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right).show(home).hide(yt).hide(play).commit();
                            search.setEnabled(true);
                        } catch (Exception ex) {
                        }
                        prev_bottom_view= String.valueOf(R.id.home);
                        break;
                } return true;
            }});
        bv.setSelectedItemId(R.id.home);//open homw fragment on start
    }

    public void setting_page(View view)
    {
        startActivity(new Intent(MainActivity.this,Setting.class));
    }
    static public void stop_call(Context context)
    {
         telephonyManager = (TelephonyManager)context.getSystemService(context.TELEPHONY_SERVICE);
         callStateListener = new PhoneStateListener() {
            public void onCallStateChanged(int state, String incomingNumber)
            {
                if(state==TelephonyManager.CALL_STATE_RINGING){
                    if(mediaPlayer.isPlaying()){
                        byCall = true;
                        Intent yesReceive = new Intent(context, NotificationReceiver.class);
                        yesReceive.setAction(PLAY_ACTION);
                        context.sendBroadcast(yesReceive);
                    }
                }

                if(state==TelephonyManager.CALL_STATE_OFFHOOK){
                    if(MainActivity.mediaPlayer.isPlaying()){
                        byCall = true;
                        Intent yesReceive = new Intent(context, NotificationReceiver.class);
                        yesReceive.setAction(PLAY_ACTION);
                        context.sendBroadcast(yesReceive);
                    }
                }

                if(state==TelephonyManager.CALL_STATE_IDLE){
                    if(byCall){
                        byCall = false;
                        try {
                            Thread.sleep(50);// otherwise plays then stops music
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                            appendLog(String.valueOf(ex));
                        }
                        Intent yesReceive = new Intent(context, NotificationReceiver.class);
                        yesReceive.setAction(PLAY_ACTION);
                        context.sendBroadcast(yesReceive);
                    }
                }
            }
        };
    }
    public static void from_playlist(String what)
    {
        int z= what.equals("prev")?-1:1;
        int new_index=playlist_index+z;
        Log.d("prev index", String.valueOf(new_index));
        if(playlist_songs.size()<=new_index)
            new_index=0;
        else if(new_index==-1)
            new_index=playlist_songs.size()-1;
        playlist_index=new_index;
        Log.d("prev playting", new_index+"   "+ playlist_index);
        try {
            String k = (playlist_songs.get(new_index));
            int p = songs.indexOf(k);
            current = p;
        } catch (Exception ex) {//playlist error or playlist empty
            ex.printStackTrace();
            current++;
            playlist_play=0;
        }
    }
    static public void play(int position)
    {
        Log.i("values", "SongLIST ITEM CLICKED");
        appendLog("SongLIST ITEM CLICKED");
        image=R.drawable.pause;
        streaming=false;
        try {
            InputMethodManager imm = (InputMethodManager) main.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(main.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            appendLog(String.valueOf(e));
        }
        if(temp4.size()>0 && search.isEnabled())
        {
            position= temp4.get(position);
        }
        String songname = songs.get(position);
        stopifSongPlaying();
        current = position;
        StickyService.setting_icon();
        s.setEnabled(true);
        media(songname);
        first = 1;
        check();
        Log.d("VALUES", songs.get(current)+" \n "+song_dur.get(current));
    }

   static public void appendLog(String text)
    {
        File logFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)+"/");
        if(!logFile.exists()){
            if(!logFile.mkdir()){
                Log.d("values","CANT CREATE DIRECTORY MUSIC");
            }
        }

        try {
            File path = con_main.getExternalFilesDir("");
            logFile = new File(path, "log.txt");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
                logFile.setReadable(true,false);
                logFile.setWritable(true,false);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                logFile.delete();
            }
        }
        double size=logFile.length()/(1024*1024); //clear content when size more than 2mb
        if(size>2)
        {
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(logFile);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            writer.close();
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            logFile.delete();
            e.printStackTrace();
        }

    }

    static public void scrap_speed()
    {
        appendLog("SCRAP SPEED");
        ch=new Thread(){
            @Override
            public void run()
            {
                try {
                    Connection.Response rep = Jsoup.connect("https://www.bing.com/videos/search?q=")
                            .timeout(500)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36")
                            .referrer("https://www.google.com")
                            .followRedirects(true)
                            .execute();
                    appendLog("RESPONSE CODE "+rep.statusCode()+"   "+rep.statusMessage());
                    MainActivity.doc=rep.parse();
                    rep= Jsoup.connect( "https://www.bing.com/images/search?q=Billie+Eilish+-+all+the+good+girls+go+to+hell+song&qpvt=Billie+Eilish+-+all+the+good+girls+go+to+hell+song&FORM=IARRSM")
                            .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                            .timeout(500)
                            .ignoreContentType(true)
                            .referrer("https://www.google.com")
                            .followRedirects(true)
                            .execute();
                    appendLog("Scrap Main Done");
                } catch (IOException e) {
                    appendLog("Scrap_speed"+String.valueOf(e));
                }
            }
        };
        ch.start();
    }
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.RGB_565);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    static public void downloadfinished(Context ctx){
    appendLog("DOWNLOAD FINISHED");
        onComplete=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
                DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Cursor cursor = manager.query(query);
                if (cursor.moveToFirst()) {
                    if (cursor.getCount() > 0) {
                        int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        Long download_id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,0);
                        if (status == DownloadManager.STATUS_SUCCESSFUL)
                        {
                            Ytsearch.change_format(context);
                            Ytsearch.add_tags(context);

                            try {
                                if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O)
                                {
                                    new SingleMediaScanner(MainActivity.con_main, new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/" + Ytsearch.filetitle));
                                }
                                else
                                {
                                    Uri contentUri = Uri.fromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
                                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                    mediaScanIntent.setData(contentUri);
                                    main.sendBroadcast(mediaScanIntent);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                appendLog(String.valueOf(ex));
                                Log.d("values", ""+ex);
                            }
                        }
                        else
                            Log.d("Download error", String.valueOf(cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON))));
                    }
                }
                cursor.close();
                unregister(ctx);
            }
            download_finished=0;
            Ytsearch.b1.setText("Search");
            Log.i("values", "DOWNLOAD FINISHED ENDED");
            appendLog("DOWNLOAD FINISHED ENDED");
        }
    };
    ctx.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
}



    public static void unregister(Context ctx)
    {
        Log.i("values", "UNREGISTER ONCOMPLETE");
        appendLog("UNREGISTER ONCOMPLETE");

                ctx.unregisterReceiver(MainActivity.onComplete);
    }

    static public void createList(Activity ctx)
    {
        Log.i("values", "CREATING LIST");
        appendLog("CREATING LIST");
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(ctx);
        list.setLayoutManager(layoutManager);
        list.addItemDecoration(new DividerItemDecoration(MainActivity.con_main,DividerItemDecoration.VERTICAL)); //for divider in between
        //animation, code from https://www.journaldev.com/24088/android-recyclerview-layout-animations
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(MainActivity.con_main, R.anim.layout_animation_left_to_right);
        list.setLayoutAnimation(controller);
        list.setHasFixedSize(true);
        list.scheduleLayoutAnimation();//to show animation when come back from searchview
        list.setAdapter(adap);
        SwipeController swipeController = new SwipeController();
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(list);
    }
  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.menu, menu);
      search=menu.findItem(R.id.action_search);
      SearchView sv= (SearchView)MenuItemCompat.getActionView(search);
      EditText txtSearch=(sv.findViewById(R.id.search_src_text));
      ImageView searchclose=sv.findViewById(R.id.search_close_btn);
      searchclose.setImageResource(R.drawable.close);
      txtSearch.setTextColor(Color.GRAY);
      txtSearch.setHint("Search by Title Or Artist");
      txtSearch.setHintTextColor(Color.GRAY);
      search.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
          @Override
          public boolean onMenuItemActionExpand(MenuItem menuItem) {
              return true;
          }

          @Override
          public boolean onMenuItemActionCollapse(MenuItem menuItem) {
              list.setVisibility(View.VISIBLE);
              //adap.notifyDataSetChanged();
              list.setAdapter(adap);
              return true;
          }
      });
      sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
          @Override
          public boolean onQueryTextSubmit(String s) {
              return false;
          }

          @Override
          public boolean onQueryTextChange(String s) {
              temp=new ArrayList<>();
              temp2=new ArrayList<>();
              temp3=new ArrayList<>();
              temp4=new ArrayList<>();
              for(int i=0;i<song_name.size();i++) {
                  String temp1=song_name.get(i);
                  String temp21=song_artist.get(i);
                  if (temp1.toLowerCase().contains(s.toLowerCase())||temp21.toLowerCase().contains(s.toLowerCase())) {
                      temp.add(song_name.get(i));
                      temp2.add(song_artist.get(i));
                      temp3.add(songs.get(i));
                      temp4.add(i);
                  }
              }
              if(temp4.size()<0 || temp4.size()==songs.size()) {
                  list.setVisibility(View.VISIBLE);
                  adap.notifyDataSetChanged();
              }
              else if(temp4.size()==0)
              {
                  list.setVisibility(View.GONE);
              }
              else
              {   MainList madapter=new MainList();
                  madapter.notifyDataSetChanged();
                  list.setAdapter(madapter);
                  list.setVisibility(View.VISIBLE);
              }
              return false;
          }
      });
      return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void onResume()
    {
        Log.i("values", "INSIDE RESUME MAIN");
        appendLog("INSIDE RESUME MAIN");
        if(streaming==true)
            requestAudio();
        fileopen();
       try {//after musicpage ondestroy
           if(prev_bottom_view.equals(String.valueOf(R.id.home)))
            bv.setSelectedItemId(R.id.home);
           else if(prev_bottom_view.equals(String.valueOf(R.id.action_playlist)))
               bv.setSelectedItemId(R.id.action_playlist);
            else
               bv.setSelectedItemId(R.id.action_downloader);
       } catch (Exception ex) {
            ex.printStackTrace();
        }
        super.onResume();
        Log.i("values", "OUT OF RESUME MAIN");
        appendLog("OUT OF RESUME MAIN");
    }
    public void fileopen() {
        Log.i("values", "OPENING FILE MAIN");
        appendLog("OPENING FILE MAIN");
        try {
            String name = "";
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
        } catch (Exception ex) {
            ex.printStackTrace();
            appendLog(String.valueOf(ex));
            Log.d("values", String.valueOf(e));
        }
        Log.i("values", "FILE OPENED  MAIN");
        appendLog("FILE OPENED  MAIN");

    }
    static public void check() { //checks play button icon image
        b1.setBackgroundResource((MainActivity.flag && !streaming)? R.drawable.ic_play_circle_filled_black_24dp:R.drawable.pause );

    }
    static public void requestAudio() {
        audioManager = (AudioManager) con_main.getSystemService(Context.AUDIO_SERVICE);
        AudioAttributes mAudioAttributes =
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioFocusRequest mAudioFocusRequest =
                    new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                            .setAudioAttributes(mAudioAttributes)
                            .setAcceptsDelayedFocusGain(true)
                            .setOnAudioFocusChangeListener(mOnAudioFocusChangeListener)
                            .build();

            int result = audioManager.requestAudioFocus(mAudioFocusRequest);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                Log.d("AudioFocus", "Audio focus received");
            } else {
                Log.d("AudioFocus", "Audio focus NOT received");
            }
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
        if (!streaming) {
            shuffleon1();
        }
    }
    public static void stopifSongPlaying() {
        if (flag == true) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                flag = false;
                mediaPlayer.reset();
                s.setProgress(0);
                t3.setText("0:00");
            }
            if (t.isAlive())
            {t.interrupt(); Log.i("values", "INTERRUPT THREAD IN STOP IF ALREADY PLAING");
                appendLog("INTERRUPT THREAD IN STOP IF ALREADY PLAING");
            }
        }

    }
    public void Pause(View view) {
        if(flag)
            wantsmusic=false;
        Pause1();
      //  else
        //    wantsmusic=true;
        requestAudio();
        startService(service);
    }
    static public void Pause1() {
        if (flag == true) {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
                flag = false;
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
    public static void media(String path) {
        b1.setVisibility(View.VISIBLE);
        t3.setText("0:00");
        requestAudio();
        if(mediaPlayer==null)
            mediaPlayer=new MediaPlayer();
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepareAsync();//just using prepare blocks main thread
        } catch (IOException ex) {
            ex.printStackTrace();
            appendLog("MediaPlayer error"+ ex);
            mediaPlayer.reset();
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    if(loop==true)
                        mediaPlayer.setLooping(loop);
                    int min = (mediaPlayer.getDuration() / 1000) / 60;
                    int sec = (mediaPlayer.getDuration() / 1000) % 60;
                    if (sec < 10)
                        t4.setText(min + ":0" + sec);
                    else
                        t4.setText(min + ":" + sec);
                    flag = true;
                    main.startService(service);
                details();
                createThread();
                }
            });
    }
    static public void details()
    {
        String path = songs.get(current);
        if((!song_artist.get(current).equals("<unknown>"))&&(song_name.get(current).toLowerCase().contains(song_artist.get(current).toLowerCase()))==false)
            t2.setText(song_artist.get(current)+"-"+song_name.get(current));
        else
            t2.setText(song_name.get(current));
        t2.setVisibility(View.VISIBLE);
        t2.setSelected(true);
        int min = (mediaPlayer.getDuration() / 1000) / 60;
        int sec = (mediaPlayer.getDuration() / 1000) % 60;
        if (sec < 10)
            t4.setText(min + ":0" + sec);
        else
            t4.setText(min + ":" + sec);
    }
    static public void createThread() {
        try {
            if (t.isAlive())
            {t.interrupt();Log.i("values", "INTERRUPTING THREAD IN CREATE THREAD");
            appendLog("INTERRUPTING THREAD IN CREATE THREAD");}
        } catch (Exception ex) {
            appendLog(String.valueOf(ex));
        }

        t = new Thread() {
            @Override
            public void run() {
               // StickyService.metadata();
                //StickyService.setting_icon();
                int currentPosition = 0;
                int total = mediaPlayer.getDuration();
                s.setMax(total);
                while (mediaPlayer != null && currentPosition < total) {
                    try {
                        Thread.sleep(1000);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        telephonyManager.listen(callStateListener,PhoneStateListener.LISTEN_CALL_STATE);
                    } catch (InterruptedException e) {
                        appendLog(String.valueOf(e));
                      return;
                    }
                    s.setProgress(currentPosition);
                    int min = (currentPosition / 1000) / 60;
                    int sec = (currentPosition / 1000) % 60;
                    try {
                        main.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (sec < 10)
                                    t3.setText(min + ":0" + sec);
                                else
                                    t3.setText(min + ":" + sec);
                            }
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    if(k1==1)
                    {
                        int finalCurrentPosition = currentPosition;
                        musicpage.s2.setMax(total);
                        musicpage.s2.setProgress(finalCurrentPosition);
                        main.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (sec < 10)
                                    musicpage.t2.setText(min + ":0" + sec);
                                else
                                    musicpage.t2.setText(min + ":" + sec);
                            }
                        });
                    }
                }
            }
        };
        t.start();
        s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromuser) {
                if (mediaPlayer != null && fromuser)
                {mediaPlayer.seekTo(i);}
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    static public void previous1() {
        if (flag == true) {
            if (current == 0) {
                current = 0;
            } else {
                current = current - 1;
            }
            if(playlist_play==1)
                from_playlist("prev");
            if (shuffle == true) {
                Random r = new Random();
                current = r.nextInt(songs.size() - 1);
            }
            StickyService.setting_icon();
            String prevsong = songs.get(current);
            stopifSongPlaying();
            media(prevsong);
            main.startService(service);
        } else
            return;
    }
    public void previous(View view) {
        if (!streaming) {
            previous1();
        }
    }
    static public void next1() {
        if (!streaming) {
            if (flag == true) {
                if (current == songs.size() - 1)
                    current = 0;
                else
                    current = current + 1;
                if(playlist_play==1)
                    from_playlist("next");
                if (shuffle == true) {
                    Random r = new Random();
                    current = r.nextInt(songs.size() - 1);
                }
                StickyService.setting_icon();
                String nextsong = songs.get(current);
                stopifSongPlaying();
                try {
                    media(nextsong);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                main.startService(service);
            } else
                return;
        }
    }
    public void next(View view) {
        next1();
    }
    static public void repeat1() {
        if (loop == false) {
            loop = true;
            mediaPlayer.setLooping(loop);
            b4.setBackgroundResource(R.drawable.donot);
            repeat22 = R.drawable.donot;
        } else {
            loop = false;
            mediaPlayer.setLooping(loop);
            b4.setBackgroundResource(R.drawable.repeat);
            repeat22 = R.drawable.repeat;
        }
    }
     public void repeat(View view) {
        repeat1();
        startService(service);
     }
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        }
        else if(requestCode==101)//if permission already granted
           all_Stuff();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this,
                        "Storage Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
                all_Stuff();
            } else {
                Toast.makeText(MainActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
  static public void getsong(Context context, String ch) {
        clear();
     //   songInfo=new ArrayList<>(); //if not done then causes crash cause index out of bound.
      String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        ContentResolver contentResolver = context.getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID
        };
        int i=0;
      Cursor songCursor = contentResolver.query(songUri, projection, selection, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER+" ASC");
        if (songCursor != null && songCursor.moveToFirst()) {
            do {
                String data = songCursor.getString(3);
                String title=songCursor.getString(2);
                String id = songCursor.getString(0);
                String artist=songCursor.getString(1);
                String album=songCursor.getString(6);
                long dur=songCursor.getLong(5);
                long album_id=songCursor.getLong(7);
                int k = Integer.parseInt(songCursor.getString(5));
                if (k > 60000) {
                    songs.add(data);
                    song_name.add(title);
                    song_id.add(id);
                    song_artist.add(artist);
                    song_dur.add(dur);
                    song_album.add(album);
                    if(ch.equals("0") && !songInfo.get(i).get_Song().equals(data) ) {
                        SongItems ob = new SongItems(data, title, artist, dur, album, album_id);
                       // ob.set_Artwork();
                        songInfo.add(i,ob);
                    }
                    else
                    {
                        SongItems ob = new SongItems(data, title, artist, dur, album, album_id);
                       // ob.set_Artwork();
                        songInfo.add(ob);
                    }
                    i++;
                }
            } while (songCursor.moveToNext());
        }
        songCursor.close();
    }
    static public void clear()
    {
        songs.clear();
        song_name.clear();
        song_artist.clear();
        song_album.clear();
        song_dur.clear();
        song_id.clear();
       // songInfo.clear();
    }


    @Override
    public void onDestroy() {
        Log.i("values", "DESTROYED");
        appendLog("destroyed \n\n\n\n\n");
        wantsmusic=false;
        telephonyManager.listen(callStateListener,PhoneStateListener.LISTEN_NONE);
        super.onDestroy();
        mediaPlayer.stop();
        flag = false;
        main=null;
        b1=null;
        mediaPlayer.reset();
        audioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        stopService(service);
        songs.clear();
        song_name.clear();
        notificationManager.cancelAll();
        wakeLock.release();
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.canShowBadge();

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        return;
    }
    public static boolean Check_Internet(Context context)
    {
        ConnectivityManager conn=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(conn!=null){
            NetworkInfo active=conn.getActiveNetworkInfo();
            if(active!=null)return true;
        }
        return false;
    }
    public static void remove_streaming_icon()
    {
        streaming=false;
        stream_thumnail=null;
    }


}
class List_Async extends AsyncTask<String, String, String> {
    Context ctx;
    List_Async(Context c)
    {
        ctx=c;
    }
    @Override
    protected String doInBackground(String... strings) {
        Log.i("values", "IN LIST ASYNC" +strings[0]);
        MainActivity.getsong(ctx , strings[0]);
        return strings[0];
    }
    @Override
    protected void onPostExecute(String result)
    {
        if(result.equals("0"))
        MainActivity.adap.notifyDataSetChanged();// not go to top when update list
        else {
            Log.i("values", "LEAVING LIST ASYNC");
            MainActivity.createList(MainActivity.main);
        }
    }
}
class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {

    private MediaScannerConnection mMs;
    private File mFile;

    public SingleMediaScanner(Context context, File f) {
        mFile = f;
        mMs = new MediaScannerConnection(context, this);
        mMs.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        mMs.scanFile(mFile.getAbsolutePath(), null);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        MainActivity.ls=new List_Async(MainActivity.con_main);
        MainActivity.ls.execute("0");
        mMs.disconnect();
    }
}
//Code from https://codeburst.io/android-swipe-menu-with-recyclerview-8f28a235ff28?gi=11cc5f0055c9
class SwipeController extends ItemTouchHelper.Callback {
    private boolean swipeBack = false;
    private static final float swipe_needed = 300;
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, LEFT | RIGHT);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    }
    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (swipeBack) {
            swipeBack = false;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }
    @Override
    public void onChildDraw(Canvas c,
                            RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder,
                            float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {

        if (actionState == ACTION_STATE_SWIPE) {
            setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void setTouchListener(Canvas c,
                                  RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  float dX, float dY,
                                  int actionState, boolean isCurrentlyActive) {

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                if(swipeBack)
                {
                    if (dX >swipe_needed) //for playlist
                    {
                        int k;
                        if(MainActivity.temp4.size()>0)
                            k=MainActivity.temp4.get(viewHolder.getAdapterPosition());
                        else
                            k=viewHolder.getAdapterPosition();
                        Log.d("playlist", k+" ");
                        String name=MainActivity.songs.get(k);
                        if(!MainActivity.playlist_songs.contains(name)) {
                            MainActivity.playlist_songs.add(MainActivity.songs.get(k));
                            MainActivity.playlist_song_artist.add(MainActivity.song_artist.get(k));
                            MainActivity.playlist_song_name.add(MainActivity.song_name.get(k));
                            Toast.makeText(MainActivity.main, "Added to Playlist", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(MainActivity.main, "Already in Playlist", Toast.LENGTH_SHORT).show();
                    }
                    else if (dX <-swipe_needed)
                    {
                        int i;
                        if(MainActivity.temp4.size()>0)
                            i=MainActivity.temp4.get(viewHolder.getAdapterPosition());
                        else
                            i=viewHolder.getAdapterPosition();
                        AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.main)
                                .setMessage(MainActivity.song_name.get(i))
                                .setTitle("Delete")
                                .setCancelable(true)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        try {
                                            File del = new File(MainActivity.songs.get(i));
                                            boolean f=del.delete();
                                            if(f)
                                            {
                                                Toast.makeText(MainActivity.con_main, "Deleted " + MainActivity.song_name.get(i), Toast.LENGTH_SHORT).show();
                                                new SingleMediaScanner(MainActivity.con_main, new File(MainActivity.songs.get(i)));
                                                MainActivity.songInfo.remove(i);
                                                MainActivity.adap.notifyItemRemoved(i);
                                                MainActivity.song_name.remove(i);
                                                MainActivity.song_artist.remove(i);
                                                MainActivity.song_album.remove(i);
                                                MainActivity.song_dur.remove(i);
                                                MainActivity.song_id.remove(i);
                                                MainActivity.songs.remove(i);
                                            }
                                            else
                                                Toast.makeText(MainActivity.con_main, "Can't Delete files in SD-Card", Toast.LENGTH_SHORT).show();
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                            Log.d("values",ex+"");
                                        }
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                            AlertDialog alert = dialog.create();
                            alert.show();
                    }
                }

                return false;
            }
        });
    }
}