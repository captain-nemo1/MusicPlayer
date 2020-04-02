package com.nemocorp.nemoplayer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import java.io.IOException;
import java.util.Random;

import static com.nemocorp.nemoplayer.MainActivity.Pause1;
import static com.nemocorp.nemoplayer.MainActivity.current;
import static com.nemocorp.nemoplayer.MainActivity.flag;
import static com.nemocorp.nemoplayer.MainActivity.image;
import static com.nemocorp.nemoplayer.MainActivity.k1;
import static com.nemocorp.nemoplayer.MainActivity.loop;
import static com.nemocorp.nemoplayer.MainActivity.mediaPlayer;
import static com.nemocorp.nemoplayer.MainActivity.shuffle;
import static com.nemocorp.nemoplayer.MainActivity.song_artist;
import static com.nemocorp.nemoplayer.MainActivity.song_name;
import static com.nemocorp.nemoplayer.MainActivity.songs;


public class musicpage extends AppCompatActivity {

    static TextView t1,t2,t3,t4;
    static SeekBar s2;
    static Button b1,b3,b4;
    ImageButton b2;
    Intent i;
    static String prog;
    static ImageView i1;
    static Thread th;
    LinearLayout c1;
    Intent service;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicpage);
        i = getIntent();
        t1 = findViewById(R.id.t1);
        t1.setSelected(true);
        t4=findViewById(R.id.t2);
        b1 = findViewById(R.id.button);
        b2=findViewById(R.id.button3);
        b3=findViewById(R.id.repeat);
        b4=findViewById(R.id.shuf);
        t2=findViewById(R.id.start);
        t3=findViewById(R.id.end);
        s2=findViewById(R.id.seekBar2);
        c1=findViewById(R.id.lay);
        i1=findViewById(R.id.image);
        th=new Thread();
        if(!song_artist.get(current).equals("<unknown>"))
        {
            t1.setText(song_name.get(current));
            t4.setText(song_artist.get(current));
        }
        else
        { t1.setText(song_name.get(current));
          t4.setText("");
        }
        check();
        service=new Intent(this, StickyService.class);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(th.isAlive())
                            th.interrupt();
                        onBackPressed();
                    }
                });
                c1.setOnTouchListener(new OnSwipeTouchListener(musicpage.this) {
                    public void onSwipeBottom() {
                        if(th.isAlive())
                            th.interrupt();
                        onBackPressed();
                    }
                    public void onSwipeTop()
                    {
                        if(th.isAlive())
                            th.interrupt();
                        onBackPressed();
                    }
                    public void onSwipeRight() {
                        if(flag!=false) {
                        if (current == songs.size() - 1)
                            current =0;
                        else
                        current=current+1;
                            if (shuffle == true) {
                                Random r = new Random();
                                current = r.nextInt(songs.size() - 1);
                            }
                            Log.d("thiss111", String.valueOf(current));
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
                            startService(service);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        int min=(mediaPlayer.getDuration()/1000)/60;
                        int sec=(mediaPlayer.getDuration()/1000)%60;
                        if(sec>=10)
                            t3.setText(min+":"+sec);
                        else
                            t3.setText(min+":0"+sec);
                        t2.setText("0:00");
                        seek();
                            if(!song_artist.get(current).equals("<unknown>")) {
                                t1.setText(song_name.get(current));
                                t4.setText(song_artist.get(current));
                            }
                            else {
                                t1.setText(song_name.get(current));
                                t4.setText("");
                            }
                        MediaMetadataRetriever m= new MediaMetadataRetriever();
                        m.setDataSource(songs.get(MainActivity.current));
                        try {
                            byte[] a = m.getEmbeddedPicture();
                            Bitmap c = BitmapFactory.decodeByteArray(a, 0, a.length);
                            i1.setImageBitmap(c);
                            background(c);
                        } catch (Exception e) {
                            i1.setImageResource(R.drawable.ic_music_note_black_24dp);
                            c1.setBackgroundColor(getResources().getColor(R.color.black));
                            b2.setBackgroundColor(getResources().getColor(R.color.black));
                            t1.setTextColor(getResources().getColor(R.color.white));
                            t4.setTextColor(getResources().getColor(R.color.white));

                        }
                        }
                    }

                    public void onSwipeLeft()
                    {
                        if(flag!=false) {

                            if(current!=0)
                                current=current-1;
                            if (shuffle == true) {
                                Random r = new Random();
                                current = r.nextInt(songs.size() - 1);
                            }

                            if(mediaPlayer!=null)
                                mediaPlayer.reset();
                            Log.d("thiss", String.valueOf(current));
                            try {
                                mediaPlayer.setDataSource(songs.get(current));
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                                if(loop==true)
                                    mediaPlayer.setLooping(true);
                                MainActivity.details();
                                MainActivity.createThread();
                                startService(service);
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                            int min=(mediaPlayer.getDuration()/1000)/60;
                            int sec=(mediaPlayer.getDuration()/1000)%60;
                            if(sec>=10)
                                t3.setText(min+":"+sec);
                            else
                                t3.setText(min+":0"+sec);
                            t2.setText("0:00");
                            seek();
                            if(!song_artist.get(current).equals("<unknown>")) {
                                t1.setText(song_name.get(current));
                                t4.setText(song_artist.get(current));
                            }
                            else
                            { t1.setText(song_name.get(current));
                                t4.setText("");
                            }
                            MediaMetadataRetriever m= new MediaMetadataRetriever();
                            m.setDataSource(songs.get(MainActivity.current));
                            try {
                                byte[] a = m.getEmbeddedPicture();
                                Bitmap c = BitmapFactory.decodeByteArray(a, 0, a.length);
                                i1.setImageBitmap(c);
                                background(c);
                            } catch (Exception e) {
                                i1.setImageResource(R.drawable.ic_music_note_black_24dp);
                                c1.setBackgroundColor(getResources().getColor(R.color.black));
                                b2.setBackgroundColor(getResources().getColor(R.color.black));
                                t1.setTextColor(getResources().getColor(R.color.white));
                                t4.setTextColor(getResources().getColor(R.color.white));
                            }
                        }
                    }
                });

            }
        },1000);
        k1=1;
        s2.setMax(mediaPlayer.getDuration());
        prog=i.getStringExtra("prog");
        int current=Integer.valueOf(prog);
        int min = (current / 1000) / 60;
        int sec = (current/ 1000) % 60;
        if (sec<10)
            t2.setText(min+":0"+sec);
        else
            t2.setText(min+":"+sec);
        seek();
        int total= mediaPlayer.getDuration();
        min = (total / 1000) / 60;
        sec = (total / 1000) % 60;
        if (sec<10)
            t3.setText(min+":0"+sec);
        else
        t3.setText(min+":"+sec);
        MediaMetadataRetriever m= new MediaMetadataRetriever();
        m.setDataSource(songs.get(MainActivity.current));
            try {
                byte[] a = m.getEmbeddedPicture();
                Bitmap c = BitmapFactory.decodeByteArray(a, 0, a.length);
                i1.setImageBitmap(c);
                background(c);
            } catch (Exception e) {
                i1.setImageResource(R.drawable.ic_music_note_black_24dp);
                c1.setBackgroundColor(getResources().getColor(R.color.black));
                b2.setBackgroundColor(getResources().getColor(R.color.black));
                t1.setTextColor(getResources().getColor(R.color.white));
                t4.setTextColor(getResources().getColor(R.color.white));
                MainActivity.r.setBackgroundColor(getResources().getColor(R.color.black));

            }
        }

    public Palette createPaletteSync(Bitmap bitmap) {
        Palette p = Palette.from(bitmap).generate();
        return p;
    }

     public void background(Bitmap c)
     {
         Palette p = createPaletteSync(c);
         Palette.Swatch vibrantSwatch = p.getDominantSwatch();
         int backgroundColor;
         int titlecolor;
           backgroundColor = vibrantSwatch.getRgb();
           titlecolor=vibrantSwatch.getBodyTextColor();
             c1.setBackgroundColor(backgroundColor);
             b2.setBackgroundColor(backgroundColor);
             t1.setTextColor(titlecolor);
             t4.setTextColor(titlecolor);
     }

    public void shuffle(View View)
    {
        MainActivity.shuffleon1();
        if(shuffle==true)
            b4.setBackgroundResource(R.drawable.shuffle2);
        else
            b4.setBackgroundResource(R.drawable.shuffle1);

    }
    public void repeat(View view){
        MainActivity.repeat1();
        if (loop == true)
            b3.setBackgroundResource(R.drawable.donot);
        else
            b3.setBackgroundResource(R.drawable.repeat);
    }
   static public void seek()
    {
        if(th.isAlive())
            th.interrupt();
            s2.setProgress(Integer.parseInt(prog));
            th = new Thread() {
                @Override
                public void run() {
                    int current = Integer.parseInt(prog);
                    int total = mediaPlayer.getDuration();
                    while (mediaPlayer != null && current < total) {
                        try {
                            Thread.sleep(1000);
                            current = mediaPlayer.getCurrentPosition();
                            s2.setMax(mediaPlayer.getDuration());
                        } catch (InterruptedException e) {
                            return;
                        }
                        s2.setProgress(current);

                        int min = (current / 1000) / 60;
                        int sec = (current / 1000) % 60;
                        if (sec < 10)
                            t2.setText(min + ":0" + sec);
                        else
                            t2.setText(min + ":" + sec);
                    }
                }
            };
            th.start();
            s2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

    static public void check() {
        if (flag == true)
            b1.setBackgroundResource(R.drawable.pause);
        else
            b1.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
        if (loop == true) {
            b3.setBackgroundResource(R.drawable.donot);
        } else {
            b3.setBackgroundResource(R.drawable.repeat);
        }
        if(shuffle==true)
            b4.setBackgroundResource(R.drawable.shuffle2);
        else
            b4.setBackgroundResource(R.drawable.shuffle1);

    }


    public void onClick(View view)
    {
        Pause1();
        if (flag == true) {
                this.b1.setBackgroundResource(R.drawable.pause);
                image=R.drawable.pause;
            }
         else {
                this.b1.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
                image=R.drawable.ic_play_circle_filled_black_24dp;
        }
        startService(service);
    }
    @Override
    public void onPause()
    {
        k1=0;
        super.onPause();
    }
    @Override
    public void onStop()
    {
        k1=0;
        super.onStop();
    }

    @Override
    public void onBackPressed()
    {
        if(flag==false)
        {
            MainActivity.b1.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
        }
        else
        {
            MainActivity.b1.setBackgroundResource(R.drawable.pause);
        }
        k1=0;
        th.interrupt();
        Log.d("Thread", String.valueOf(th.isAlive()));
        super.onBackPressed();
    }
}

