package com.nemocorp.nemoplayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    static ListView list;
    PowerManager.WakeLock wakeLock;
    static Button b1, b4, b5;
    static SeekBar s;
    static int current;
    final String CHANNEL_ID = "101";
    static RelativeLayout r;
    static boolean loop = false;
    static LinearLayout l1;
    static int image = R.drawable.pause;
    static int repeat22= R.drawable.repeat;
    static boolean flag = false;
    static int download_finished=0;
    Intent i;
    static boolean shuffle = false;
    static MediaPlayer mediaPlayer;
    static List<String> songs = new ArrayList<String>();
    static NotificationManagerCompat notificationManager;
    static List<String> song_name = new ArrayList<>();
    static List<String> song_id = new ArrayList<>();
    static List<String> song_artist = new ArrayList<>();
    static List<String> song_album = new ArrayList<>();
    ArrayList<String> temp;
    ArrayList<String> temp2;
    ArrayList<String> temp3;
   static ArrayList<Integer> temp4=new ArrayList<>();;
    static ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();
    static List<Long> song_dur = new ArrayList<>();
    static String stream_name;
    static String stream_channel;
    static Bitmap stream_thumnail;
    static Thread t = null;
    static Thread bit=null;
    static Intent service;
    static NotificationCompat.Builder builder;
    AudioManager audioManager;
    static boolean wantsmusic;
    static int k1 = 0;
    static  BroadcastReceiver onComplete=null;
    int first = 0;
    static BottomNavigationView bv;
    Intent e;
    AlertDialog.Builder dialog;
    MediaMetadataRetriever metaRetriver;
    AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {

                case AudioManager.AUDIOFOCUS_GAIN:
                    if (mediaPlayer != null && wantsmusic == true) {
                        Pause1();
                        image=R.drawable.pause;
                        wantsmusic=true;
                        startService(service);
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    Pause1();
                    image=R.drawable.ic_play_circle_filled_black_24dp;
                    startService(service);
                    wantsmusic = true;
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if (wantsmusic == false)
                        break;
                    flag = true;
                    Pause1();
                    image=R.drawable.ic_play_circle_filled_black_24dp;
                    startService(service);
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
        final Toolbar yourToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(yourToolbar);
        createNotificationChannel();
        wantsmusic=true;
        dialog=new AlertDialog.Builder(this);
        list = findViewById(R.id.list);
        list.setLongClickable(true);
        t2 = findViewById(R.id.title);
        t3 = findViewById(R.id.starttime);
        t4 = findViewById(R.id.endtime);
        b1 = findViewById(R.id.play);
        b4 = findViewById(R.id.repeat);
        b5 = findViewById(R.id.shuffle);
        b1.setVisibility(View.INVISIBLE);
        t2.setVisibility(View.INVISIBLE);
        bv=findViewById(R.id.bottom_view);
        s = findViewById(R.id.seekBar);
        s.setEnabled(false);
        r = findViewById(R.id.r2);
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 101);
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 102);
        getsong(getApplicationContext());
        getart();
        service = new Intent(getApplicationContext(), StickyService.class);
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire();
        t = new Thread();
        i = new Intent(MainActivity.this, musicpage.class);
        l1 = findViewById(R.id.layout);
        r=findViewById(R.id.r2);
        mediaPlayer = new MediaPlayer();
       // audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        createList();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                streaming=false;
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {

                }
                if(temp4.size()>0)
                {
                    position= temp4.get(position);
                }
                String songname = songs.get(position);
                stopifSongPlaying();
                Log.d("values", songs.get(position));
                current = position;
                s.setEnabled(true);
                media(songname);
                first = 1;
                check();
            }
        });
       list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
               dialog.setMessage(song_name.get(i))
                       .setTitle("Delete")
                       .setCancelable(true)
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                               try {
                                   File del = new File(songs.get(i));
                                   boolean f=del.delete();
                                   if(f)
                                        Toast.makeText(getApplicationContext(), "Deleted " + song_name.get(i)+"Restart App to reflect change", Toast.LENGTH_SHORT).show();
                                   else
                                       Toast.makeText(getApplicationContext(), "Can't Delete files in SD-Card", Toast.LENGTH_SHORT).show();
                                   MediaScannerConnection.scanFile(getApplicationContext(),new String[]{songs.get(i)},new String[]{"mp3/*"},null);//scan audio files so deleted files are reflected
                               } catch (Exception ex) {
                                   ex.printStackTrace();
                                   Log.d("values",e+"");
                               }
                           }
                       })
                       .setNegativeButton("No", new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                               dialog.cancel();
                           }
                       });
               if(temp4.size()<=0||temp4.size()==song_name.size()){//not delete when search is being used
               AlertDialog alert = dialog.create();
               alert.show();}
               return true;
           }
       });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                t.interrupt();
                if (!streaming) {
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
                        musicpage.t4.setText(song_artist.get(current));
                        MediaMetadataRetriever m = new MediaMetadataRetriever();
                        try {
                            m.setDataSource(songs.get(MainActivity.current));
                            byte[] a = m.getEmbeddedPicture();
                            Bitmap c = BitmapFactory.decodeByteArray(a, 0, a.length);
                            musicpage.i1.setImageBitmap(c);
                        } catch (Exception e) {
                            musicpage.i1.setImageResource(R.drawable.ic_music_note_black_24dp);
                        }
                    }
                }
            }});

        r.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {
                if (!streaming) {
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
            }
            public void onSwipeBottom() {
                if (!streaming) {
                    if (first != 0) {
                        int k = mediaPlayer.getCurrentPosition();
                        i.putExtra("prog", String.valueOf(k));
                        String s = songs.get(current);
                        i.putExtra("art", s);
                        startActivity(i);
                        overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
                    }
                }
            }
            public void onSwipeRight() {
                if (!streaming) {
                    if (shuffle == true) {
                        Random r = new Random();
                        current = r.nextInt(songs.size() - 1);
                    }
                    next1();
                }
            }
            public void onSwipeLeft() {
                if (!streaming) {
                    if (shuffle == true) {
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
                switch(menuItem.getItemId())
                {
                    case R.id.action_downloader:
                        Intent i=new Intent(getApplicationContext(), Ytsearch.class);
                        startActivity(i);
                        break;
                    case R.id.action_player:
                        if (!streaming) {
                        if (first != 0) {
                            Intent i2 = new Intent(getApplicationContext(), musicpage.class);
                            i2.putExtra("name", song_name.get(current));
                            int k = mediaPlayer.getCurrentPosition();
                            i2.putExtra("prog", String.valueOf(k));
                            String s = songs.get(current);
                            i2.putExtra("art", s);
                            startActivity(i2);
                            break;
                        }
                };
            } return true;
        }});
    }
    static public void downloadfinished(Context ctx){
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
                                Toast.makeText(context,"Close and Open App to get the downloaded song in list", Toast.LENGTH_LONG).show();
                                try {
                                    MediaScannerConnection.scanFile(context,new String[]{String.valueOf(Environment.getExternalStorageDirectory())},new String[]{"audio/*"},null);//updateMediaStore
                                    Log.d("values","updated");
                                    getsong(context );
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    Log.d("values", ""+ex);
                                }
                            }
                        }
                    }
                    cursor.close();
                }
                download_finished=0;
                if(download_finished==0)
                    unregister(ctx);
            }
        };
        ctx.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
    public static void unregister(Context ctx)
    {
        ctx.unregisterReceiver(MainActivity.onComplete);
    }

     public void createList()
    {
        myAdapter madapter=new myAdapter(this,song_name, song_artist,songs);
        madapter.notifyDataSetChanged();
        list.setAdapter(madapter);
    }
  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.menu, menu);
      MenuItem search=menu.findItem(R.id.action_search);
      SearchView sv= (SearchView)MenuItemCompat.getActionView(search);
      EditText txtSearch=(sv.findViewById(androidx.appcompat.R.id.search_src_text));
      ImageView searchclose=sv.findViewById(androidx.appcompat.R.id.search_close_btn);
      searchclose.setImageResource(R.drawable.close);
      txtSearch.setTextColor(Color.WHITE);
      txtSearch.setHint("Search by Title Or Artist");
      txtSearch.setHintTextColor(Color.WHITE);
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
              if(temp4.size()<0 || temp4.size()==songs.size())
            { myAdapter myAdapter=new myAdapter(MainActivity.this,song_name,song_artist,songs );
                myAdapter.notifyDataSetChanged();
                list.setAdapter(myAdapter);}
              else
              {searchAdapter madapter=new searchAdapter(MainActivity.this,temp, temp2,temp3);
                  madapter.notifyDataSetChanged();
                  list.setAdapter(madapter);}
              return false;
          }
      });
      return super.onCreateOptionsMenu(menu);
    }

    public void getart()
    {
        bit=new Thread(){
            @Override
            public void run()
            {
                Bitmap c=null;
                for(int i=0;i<songs.size();i++)
                {
                    MediaMetadataRetriever m=new MediaMetadataRetriever();

                    try{
                        m.setDataSource(songs.get(i));
                        byte[] art = m.getEmbeddedPicture();
                        c = BitmapFactory.decodeByteArray(art, 0, art.length);
                        bitmapArray.add(c);}
                    catch (Exception e){bitmapArray.add(null);}
                }
            }
        };
       bit.start();
    }
    @Override
    public void onResume()
    {
       // if(download_finished==1)
         //   downloadfinished(this);
        if(streaming==true)
            requestAudio();
        fileopen();
        super.onResume();

    }
    public void fileopen() {
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
            Log.d("values", String.valueOf(e));
        }
    }

    static public void check() {
        if (MainActivity.flag == true)
            b1.setBackgroundResource(R.drawable.pause);
        else
            b1.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
    }
    public void requestAudio() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        AudioAttributes mAudioAttributes =
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
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
        t3.setText("0:00");
        requestAudio();
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
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
            startService(service);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MEDIAPLAYER ERROR", String.valueOf(e));
        }
        details();
        createThread();
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
            if (shuffle == true) {
                Random r = new Random();
                current = r.nextInt(songs.size() - 1);
            }
            String prevsong = songs.get(current);
            stopifSongPlaying();
            media(prevsong);
            startService(service);
        } else
            return;
    }
    public void previous(View view) {
        if (!streaming) {
            previous1();
        }
    }
    public void next1() {
        if (!streaming) {
            if (flag == true) {
                if (current == songs.size() - 1)
                    current = 0;
                else
                    current = current + 1;
                if (shuffle == true) {
                    Random r = new Random();
                    current = r.nextInt(songs.size() - 1);
                }
                String nextsong = songs.get(current);
                stopifSongPlaying();
                media(nextsong);
                startService(service);
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
        if(streaming)//if streaming them always will repeat
        {
            loop = true;
            mediaPlayer.setLooping(loop);
            b4.setBackgroundResource(R.drawable.donot);
            repeat22 = R.drawable.donot;
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
                PackageManager packageManager = this.getPackageManager();
                Intent intent = packageManager.getLaunchIntentForPackage(this.getPackageName());
                ComponentName componentName = intent.getComponent();
                Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                this.startActivity(mainIntent);
                Runtime.getRuntime().exit(0);
            } else {
                Toast.makeText(MainActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
  static public void getsong(Context context) {

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
                MediaStore.Audio.Media.ALBUM
        };
        Cursor songCursor = contentResolver.query(songUri, projection, selection, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER+" ASC");
        if (songCursor != null && songCursor.moveToFirst()) {
            do {
                String data = songCursor.getString(3);
                String title=songCursor.getString(2);
                String id = songCursor.getString(0);
                String artist=songCursor.getString(1);
                String album=songCursor.getString(6);
                long dur=songCursor.getLong(5);
                int k = Integer.parseInt(songCursor.getString(5));
                if (k > 60000) {
                    songs.add(data);
                    song_name.add(title);
                    song_id.add(id);
                    song_artist.add(artist);
                    song_dur.add(dur);
                    song_album.add(album);
                }
            } while (songCursor.moveToNext());
        }
        songCursor.close();
    }

    @Override
    public void onDestroy() {
        wantsmusic=false;
        super.onDestroy();
        wakeLock.release();
        mediaPlayer.stop();
        flag = false;
        mediaPlayer.reset();
        audioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
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