package com.nemocorp.nemoplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.palette.graphics.Palette;


import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.material.snackbar.Snackbar;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.AndroidArtwork;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.mp4.Mp4FieldKey;
import org.jaudiotagger.tag.mp4.Mp4Tag;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static com.nemocorp.nemoplayer.MainActivity.Pause1;
import static com.nemocorp.nemoplayer.MainActivity.appendLog;
import static com.nemocorp.nemoplayer.MainActivity.current;
import static com.nemocorp.nemoplayer.MainActivity.flag;
import static com.nemocorp.nemoplayer.MainActivity.image;
import static com.nemocorp.nemoplayer.MainActivity.k1;
import static com.nemocorp.nemoplayer.MainActivity.loop;
import static com.nemocorp.nemoplayer.MainActivity.mediaPlayer;
import static com.nemocorp.nemoplayer.MainActivity.song_artist;
import static com.nemocorp.nemoplayer.MainActivity.song_dur;
import static com.nemocorp.nemoplayer.MainActivity.song_name;
import static com.nemocorp.nemoplayer.MainActivity.songs;


public class musicpage extends AppCompatActivity {

    static TextView t1,t2,t3,t4;
    static SeekBar s2;
    static Button b1,b3,b4;
    Button b2;
    Intent i;
    static int thumnail_no=0;
    static List<Drawable> draw;
    static String change_name_of="none";
    static String new_name="";
    static SimpleDraweeView i1;
    static LinearLayout c1;
    static List<String> resultUrls;
    Intent service;
    static Context ct_main;
    static Activity music;
    static Thread thumbnail2;
    static GradientDrawable old=null;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicpage);
        i = getIntent();
        MainActivity.appendLog("MUSICPAGE CREATED");
        t1 = findViewById(R.id.t1);
        t1.setSelected(true);
        draw=new ArrayList<>();
        ct_main=getApplicationContext();
        music=musicpage.this;
        t4=findViewById(R.id.t2);
        b1 = findViewById(R.id.button);
        b2=findViewById(R.id.button3);
        change_name_of="none";
        new_name="";
        b3=findViewById(R.id.repeat);
        b4=findViewById(R.id.shuf);
        t2=findViewById(R.id.start);
        t3=findViewById(R.id.end);
        s2=findViewById(R.id.seekBar2);
        c1=findViewById(R.id.lay);
        i1=findViewById(R.id.image);
        song_name_change();
        check();
        service=new Intent(this, StickyService.class);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity.appendLog("MUSICPAGE LEAVING");
                        onBackPressed();
                    }
                });
                c1.setOnTouchListener(new OnSwipeTouchListener(musicpage.this) {
                    public void onSwipeBottom() {
                        MainActivity.appendLog("MUSICPAGE LEAVINg SWIPE BOTTOM");
                        onBackPressed();
                    }
                    public void onSwipeTop()
                    {
                        MainActivity.appendLog("MUSICPAGE LEAVINg SWIPE up");
                        onBackPressed();
                    }
                    public void onSwipeRight() {
                        if(!Ytsearch.streaming) {
                            MainActivity.next1();
                            change_Album_Art();
                            int min = Math.toIntExact((song_dur.get(current) / 1000) / 60);
                            int sec = Math.toIntExact((song_dur.get(current) / 1000) % 60);
                            if (sec >= 10)
                                t3.setText(min + ":" + sec);
                            else
                                t3.setText(min + ":0" + sec);
                            t2.setText("0:00");
                            song_name_change();
                        }
                    }
                    public void onSwipeLeft() {
                        if (!Ytsearch.streaming) {
                            MainActivity.previous1();
                            change_Album_Art();
                            int min = Math.toIntExact((song_dur.get(current) / 1000) / 60);
                            int sec = Math.toIntExact((song_dur.get(current) / 1000) % 60);
                            if (sec >= 10)
                                t3.setText(min + ":" + sec);
                            else
                                t3.setText(min + ":0" + sec);
                            t2.setText("0:00");
                            song_name_change();
                        }
                    }
                });
            }
        },500);
        k1=1;
        set_timer();
       change_Album_Art();
        MainActivity.appendLog("MUSICPAGE ON CREATE FINISHED");
        change_tags();

    }
    public static void set_timer()
    {
        musicpage.music.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int current= mediaPlayer.getCurrentPosition();
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
            }
        });
    }

    public static void change_tags()
    {
        if(!Ytsearch.streaming) {
            t1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    change_name_of = "title";
                    Name_Dialog();
                }
            });
            t4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    change_name_of = "artist";
                    Name_Dialog();
                }
            });
            i1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    change_name_of = "album";
                    Name_Dialog();
                }
            });
        }
        else
        {
            t1.setClickable(false);
            t4.setClickable(false);
            i1.setClickable(false);
        }
    }
    public static void song_name_change(){
        if(!Ytsearch.streaming) {
            thumnail_no=0;// cause new thumnail for new song
            if (!song_artist.get(current).equals("<unknown>")) {
                t1.setText(song_name.get(current));
                t4.setText(song_artist.get(current));
            } else {
                t1.setText(song_name.get(current));
                t4.setText("");
            }
        }
        else
        {
            music.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    t1.setText(MainActivity.stream_name);
                    t4.setText(MainActivity.stream_channel);
                }
            });

        }
    }

    public static Palette createPaletteSync(Bitmap bitmap) {
        Palette p = Palette.from(bitmap).generate();
        return p;
    }

     static public void background(Bitmap c)
     {
         try {
             Palette p = createPaletteSync(c);
             Palette.Swatch color3=p.getLightVibrantSwatch();
             int color = 0;
             try {
                 color=color3.getRgb();
             } catch (NullPointerException e) {
                 List<Palette.Swatch> pp=p.getSwatches();
                 for(int k=0;k<pp.size()-1;k++)
                 {
                     color3=pp.get(k);
                     if(color3!=null)
                     {
                         color=color3.getRgb();
                         break;
                     }
                 }
             }
             int[] colors = {color, Color.LTGRAY};
             GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
             int titlecolor;
             titlecolor =color3.getTitleTextColor();
             if(musicpage.old!=null)
             {
                 Drawable backgrounds[] = new Drawable[2];
                 backgrounds[0]=old;
                 backgrounds[1]=gd;
                 TransitionDrawable crossfader = new TransitionDrawable(backgrounds);
                 c1.setBackground(crossfader);
                 old=gd;
                 crossfader.startTransition(250);
             }
             else {
                 c1.setBackground(gd);
                 old=gd;
             }
             t1.setTextColor(titlecolor);
             t4.setTextColor(titlecolor);
         } catch (Exception e) {
             e.printStackTrace();
         }
     }

    public void shuffle(View View)
    {
        MainActivity.shuffleon1();
        if(MainActivity.shuffle==true)
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
    public static void Name_Dialog() {
        final EditText taskEditText = new EditText(musicpage.music);
        String title;
        if(change_name_of=="title") {
            taskEditText.setText(song_name.get(current));
            title="Change Song Name";
        }
        else if(change_name_of=="artist") {
            taskEditText.setText(song_artist.get(current));
            title="Change Artist Name";
        }
        else {
            String name=song_name.get(current);
            String art=song_artist.get(current);
            if(name.contains(art))
                taskEditText.setText(name);
            else
                taskEditText.setText(name+" "+art);
            title="Search For Album";
        }
        AlertDialog dialog = new AlertDialog.Builder(musicpage.music)
                .setTitle(title)
                .setMessage("Enter The Name")
                .setView(taskEditText)
                .setCancelable(false)
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new_name= String.valueOf(taskEditText.getText());
                        if(change_name_of=="album" && !new_name.equals(""))
                            test2();
                        else if(!new_name.equals(song_name.get(current)) && !new_name.equals(song_artist.get(current)))
                            name_change();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                })
                .create();
               dialog.show();
    }
    public static void name_change()
    {
        File song_selected=new File(songs.get(current));
        if(songs.get(current).contains(".m4a")) {
            Log.d("Path", new_name);
            if (!new_name.equals("")) {
                try {
                    AudioFile f = AudioFileIO.read(song_selected);
                    Mp4Tag mp4tag = (Mp4Tag) f.getTag();
                    if (change_name_of.equals("artist")) {
                        mp4tag.setField(Mp4FieldKey.ARTIST, new_name);
                        song_artist.set(current,new_name);
                        SongItems ob=MainActivity.songInfo.get(current);
                        ob.Set_Artist(new_name);
                        MainActivity.songInfo.set(current,ob);
                        musicpage.t4.setText(new_name);
                    }
                    else if (change_name_of.equals("title")) {
                        mp4tag.setField(Mp4FieldKey.TITLE, new_name);
                        song_name.set(current,new_name);
                        SongItems ob=MainActivity.songInfo.get(current);
                        ob.Set_Title(new_name);
                        musicpage.t1.setText(new_name);
                        MainActivity.songInfo.set(current,ob);
                    }
                    f.commit();
                    MainActivity.adap.notifyItemChanged(current);
                    MediaScannerConnection.scanFile(musicpage.ct_main, new String[]{songs.get(current)}, new String[]{"mp3/*"}, null);
                    Toast.makeText(musicpage.ct_main, "Done", Toast.LENGTH_LONG).show();
                } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException | CannotWriteException e) {
                    e.printStackTrace();
                    Toast.makeText(musicpage.ct_main, "Error Reading Files", Toast.LENGTH_LONG).show();
                    MainActivity.appendLog("Change Artist or Title Error" + e);
                }
            }
        }
        else if(songs.get(current).contains("mp3")){
            try {
                AudioFile f = AudioFileIO.read(song_selected);
                Tag tag = f.getTag();
                if (change_name_of.equals("artist")) {
                    tag.setField(FieldKey.ARTIST, new_name);
                    SongItems ob=MainActivity.songInfo.get(current);
                    ob.Set_Artist(new_name);
                    MainActivity.songInfo.set(current,ob);
                    musicpage.t4.setText(new_name);
                }
                else if (change_name_of.equals("title")) {
                    tag.setField(FieldKey.TITLE, new_name);
                    SongItems ob=MainActivity.songInfo.get(current);
                    ob.Set_Title(new_name);
                    musicpage.t1.setText(new_name);
                    MainActivity.songInfo.set(current,ob);
                }
                AudioFileIO.write(f);
                MainActivity.adap.notifyItemChanged(current);
                MediaScannerConnection.scanFile(musicpage.ct_main, new String[]{songs.get(current)}, new String[]{"mp3/*"}, null);
                Toast.makeText(musicpage.ct_main, "Done", Toast.LENGTH_LONG).show();
            } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException | CannotWriteException e) {
                e.printStackTrace();
                Log.d("NAME MPR3", e+" ");
            }
        }
        else
            Toast.makeText(musicpage.ct_main, "Can't change tags of formats other than m4a", Toast.LENGTH_LONG).show();
    }
   static public void seek()
    {
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
    static public void change_Album_Art()
    {
       /* MediaMetadataRetriever m= new MediaMetadataRetriever();
        m.setDataSource(songs.get(MainActivity.current));
        if(!Ytsearch.streaming) {
            try {
                byte[] a = m.getEmbeddedPicture();
                Bitmap c = BitmapFactory.decodeByteArray(a, 0, a.length);
                Drawable d = new BitmapDrawable(music.getResources(), c);
                i1.setBackground(d);
                background(c);
            } catch (Exception e) {
                i1.setBackground(music.getResources().getDrawable(R.drawable.ic_music_note_black_24dp));
                if(musicpage.old!=null) {//animate color to black if previous color existed
                    Drawable backgrounds[] = new Drawable[2];
                    backgrounds[0] = old;
                    ColorDrawable temp = new ColorDrawable(music.getResources().getColor(R.color.black));
                    backgrounds[1] = temp;
                    TransitionDrawable crossfader = new TransitionDrawable(backgrounds);
                    c1.setBackground(crossfader);
                    crossfader.startTransition(250);
                }
                else { //set directly to black if first time opened
                    c1.setBackgroundColor(music.getResources().getColor(R.color.black));
                }
                int[] colors = {Color.BLACK, Color.BLACK};
                GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
                old = gd;
                t1.setTextColor(music.getResources().getColor(R.color.white));
                t4.setTextColor(music.getResources().getColor(R.color.white));
            }
        }
        else
        {
            i1.setBackground(new BitmapDrawable(MainActivity.stream_thumnail));
            background(MainActivity.stream_thumnail);
        }*/
       if(!Ytsearch.streaming)
       {
           Uri sArtworkUri = Uri
                   .parse("content://media/external/audio/albumart");
           Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, MainActivity.songInfo.get(current).album_id);
           ImagePipeline imagePipeline = Fresco.getImagePipeline();
           ImageRequest imageRequest = ImageRequestBuilder
                   .newBuilderWithSource(albumArtUri)
                   .build();

           DataSource<CloseableReference<CloseableImage>> dataSource =
                   imagePipeline.fetchDecodedImage(imageRequest, MainActivity.con_main);

           try {
               dataSource.subscribe(new BaseBitmapDataSubscriber() {
                   @Override
                   public void onNewResultImpl(@Nullable Bitmap bitmap) {
                       Drawable d=new BitmapDrawable(MainActivity.con_main.getResources(),bitmap);
                       MainActivity.main.runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               i1.setBackground(d);
                           }
                       });
                       background(bitmap);
                   }
                   @Override
                   public void onFailureImpl(DataSource dataSource) {
                       MainActivity.main.runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               i1.setBackground(music.getResources().getDrawable(R.drawable.ic_music_note_black_24dp));
                           }
                       });
                       if(musicpage.old!=null) {//animate color to black if previous color existed
                           Drawable backgrounds[] = new Drawable[2];
                           backgrounds[0] = old;
                           ColorDrawable temp = new ColorDrawable(music.getResources().getColor(R.color.black));
                           backgrounds[1] = temp;
                           TransitionDrawable crossfader = new TransitionDrawable(backgrounds);
                           c1.setBackground(crossfader);
                           crossfader.startTransition(250);
                       }
                       else { //set directly to black if first time opened
                           c1.setBackgroundColor(music.getResources().getColor(R.color.black));
                       }
                       int[] colors = {Color.BLACK, Color.BLACK};
                       GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
                       old = gd;
                       t1.setTextColor(music.getResources().getColor(R.color.white));
                       t4.setTextColor(music.getResources().getColor(R.color.white));
                   }
               }, CallerThreadExecutor.getInstance());
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
       else
       {
         i1.setBackground(new BitmapDrawable(MainActivity.con_main.getResources(),MainActivity.stream_thumnail));
         background(MainActivity.stream_thumnail);
       }
    }

    @Override
    public void onResume()
    {
        if(t1.getText()!=song_name.get(current) && t1.getText()!=song_artist.get(current)+"-"+song_name.get(current) && !Ytsearch.streaming)
        {
            if((!song_artist.get(current).equals("<unknown>"))&&(song_name.get(current).toLowerCase().contains(song_artist.get(current).toLowerCase()))==false)
                t1.setText(song_artist.get(current)+"-"+song_name.get(current)); //MainActivity t1
            else
                t1.setText(song_name.get(current));
            t4.setText(song_artist.get(current));
            set_timer();
            change_Album_Art();
        }
        super.onResume();
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
        if(MainActivity.shuffle==true)
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
    public void Get_Thumnail() {//Bing
        List<String> resultUrls = new ArrayList<String>();
        Thread thumbnail=new Thread(){
            @Override
            public void run() {
                Log.d("values", "doingalbum");
                new_name += " album art";
                new_name=new_name.replaceAll(" ","+");
                String url = "https://www.bing.com/images/search?q="+new_name+"&qs=MM&form=QBIR&sp=1&ghc=1&pq="+new_name;
                for (int i = 0; i < 3; i++) {//trying 3 times
                    try {
                        Document imagedoc = Jsoup.connect(url)
                                .userAgent("Mozilla")
                                .timeout(200000)
                                .get();
                        appendLog(imagedoc+"");
                        Elements elements = imagedoc.getElementsByTag("img");
                        Log.d("valuesphoto",elements.toString()+url);
                        for (Element element : elements) {
                            String s=element.toString();
                            if(s.contains("height"))
                                {
                                    Log.d("values2",s);
                                    String temp=s.substring(s.indexOf("https"),s.lastIndexOf("1.1")+3);
                                    resultUrls.add(temp);
                                }
                        }
                        if(resultUrls.size()>0) {
                            for(int i1=0; i1<resultUrls.size();i1++)
                                downloadImages(resultUrls.get(i1));
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }};
        thumbnail.start();
    }
    public void test()//Google
    {
        List<String> resultUrls = new ArrayList<String>();
        Log.d("valuephoto", "testing");

        new_name=new_name.trim().replaceAll(" ","%20");
        Thread thumbnail2=new Thread(){
            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    try {
                        Log.d("valuephoto", "testing222");
                        String url="https://www.google.com/search?q="+new_name+"%20album%20cover&tbm=isch&tbs=isz%3Al";
                        Connection.Response res = Jsoup.connect(url)
                                .followRedirects(false)
                                .timeout(0)
                                .header("User-Agent", "Mozilla/5.0")
                                .execute();
                        String location = res.header("Location");

                        res = Jsoup.connect(url)
                                .timeout(0)
                                .data("is_check", "1")
                                .header("User-Agent", "Mozilla/5.0")
                                .header("Referer", location)
                                .execute();
                        Document doc = res.parse();
                        Elements img=doc.getElementsByClass("t0fcAb");
                        for(Element e:img)
                        {
                            Log.d("valuephoto", e.attr("src")+"\n"+url);
                            resultUrls.add(e.attr("src"));
                        }
                        if(resultUrls.size()>0) {
                            downloadImages(resultUrls.get(0));
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }};
        thumbnail2.start();

    }
    public static void test2() { //bing full size
        resultUrls = new ArrayList<String>();
        new_name+=" song";
        try {
            new_name=URLEncoder.encode(new_name,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        thumbnail2 = new Thread() {
            @Override
            public void run() {
                    for (int i = 0; i < 3; i++) {
                        try {
                            Log.d("valuephoto", "testing222");
                            String url = "https://www.bing.com/images/search?q=" + new_name + "&qpvt=" + new_name + "&FORM=IARRSM";
                            Connection.Response res = Jsoup.connect(url)
                                    .followRedirects(false)
                                    .timeout(0)
                                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                                    .execute();
                            String location = res.header("Location");

                            res = Jsoup.connect(url)
                                    .timeout(0)
                                    .data("is_check", "1")
                                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                                    .header("Referer", location)
                                    .execute();
                            Document doc = res.parse();
                            Elements img = doc.getElementsByClass("iusc");
                            for (Element e : img) {
                                try {
                                    JSONObject js = new JSONObject(e.attr("m"));
                                    resultUrls.add(js.optString("turl"));
                                } catch (JSONException  | NullPointerException ex) {
                                    ex.printStackTrace();
                                    break;
                                }
                            }
                            if (resultUrls.size() > 0) {
                                if (thumnail_no == 3)
                                    thumnail_no = 0;
                                downloadImages(resultUrls.get(thumnail_no));
                                break;
                            }
                        } catch (IOException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
            }
        };
        thumbnail2.start();
    }
   static public void downloadImages(String inSrc) throws IOException {
     /*MainActivity.main.runOnUiThread(new Runnable() {
         @Override
         public void run() {

           /*  Glide.with(music).load(inSrc).into(new SimpleTarget<Drawable>() {
                 @Override
                 public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                     i1.setBackground(resource);
                     background(MainActivity.drawableToBitmap(resource));
                     thumnail_no++;
                     String path=MainActivity.songInfo.get(current).get_Song();
                     if(path.contains("emulated/0/")) {  // in internal storage and m4a
                         Snackbar.make(musicpage.music.findViewById(android.R.id.content), "Set This Image as Album Art?", Snackbar.LENGTH_LONG)
                                 .setAction("Yes", new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         File e1 = new File(path);
                                         Bitmap temp = MainActivity.drawableToBitmap(resource);
                                         try {
                                             AudioFile f = AudioFileIO.read(e1);
                                             if(path.contains("m4a")) {
                                                 Mp4Tag mp4tag = (Mp4Tag) f.getTag();
                                                 mp4tag.deleteField(Mp4FieldKey.ARTWORK);
                                                 mp4tag.addField(mp4tag.createArtworkField(Ytsearch.convert_byte(temp)));
                                                 mp4tag.setField(FieldKey.ALBUM, String.valueOf(mp4tag.get(Mp4FieldKey.ALBUM)));
                                             }
                                             else if(path.contains("mp3"))
                                             {
                                                 Tag tag=f.getTag();
                                                 Artwork artwork = new AndroidArtwork();
                                                 artwork.setBinaryData(Ytsearch.convert_byte(temp));
                                                 if (tag.getFirstArtwork() != null) {
                                                     tag.deleteArtworkField();
                                                     tag.setField(artwork);
                                                 } else {
                                                     tag.addField(artwork);
                                                 }
                                                 if(tag.getFirst(FieldKey.ALBUM)==null)
                                                     tag.addField(FieldKey.ALBUM,MainActivity.song_id.get(current));
                                             }
                                             f.commit();
                                             MediaScannerConnection.scanFile(musicpage.ct_main, new String[]{songs.get(current)}, new String[]{"mp3/*"}, new MediaScannerConnection.OnScanCompletedListener() {
                                                 @Override
                                                 public void onScanCompleted(String path, Uri uri) {
                                                     ImagePipeline imagePipeline = Fresco.getImagePipeline();
                                                     Uri sArtworkUri = Uri
                                                             .parse("content://media/external/audio/albumart");
                                                     Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, MainActivity.songInfo.get(current).album_id);
                                                     imagePipeline.evictFromCache(albumArtUri);
                                                     // imagePipeline.clearCaches();//need to remove old image in cache
                                                     StickyService.setting_icon();
                                                 }
                                             });
                                         } catch (IOException | CannotReadException | ReadOnlyFileException | TagException | InvalidAudioFrameException | CannotWriteException e) {
                                             e.printStackTrace();
                                         }
                                     }
                                 }).show();
                        }
                     }
             });
         }});*/
       fresco_Download_Artwork(Uri.parse(inSrc));

   }
    public static void fresco_Download_Artwork(Uri artwork)
    {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();

        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(artwork)
                .build();

        DataSource<CloseableReference<CloseableImage>> dataSource =
                imagePipeline.fetchDecodedImage(imageRequest, MainActivity.con_main);

        try {
            dataSource.subscribe(new BaseBitmapDataSubscriber() {
                @Override
                public void onNewResultImpl(@Nullable Bitmap bitmap) {
                    Drawable bp=new BitmapDrawable(MainActivity.con_main.getResources(),bitmap);
                    MainActivity.main.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            i1.setBackground(bp);

                        }
                    });
                    background(bitmap);
                    thumnail_no++;
                    String path=MainActivity.songInfo.get(current).get_Song();
                    if(path.contains("emulated/0/")) {  // in internal storage and m4a
                        Snackbar.make(musicpage.music.findViewById(android.R.id.content), "Set This Image as Album Art?", Snackbar.LENGTH_LONG)
                                .setAction("Yes", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        File e1 = new File(path);
                                        try {
                                            AudioFile f = AudioFileIO.read(e1);
                                            if(path.contains("m4a")) {
                                                Mp4Tag mp4tag = (Mp4Tag) f.getTag();
                                                mp4tag.deleteField(Mp4FieldKey.ARTWORK);
                                                mp4tag.addField(mp4tag.createArtworkField(Ytsearch.convert_byte(bitmap)));
                                                mp4tag.setField(FieldKey.ALBUM, String.valueOf(mp4tag.get(Mp4FieldKey.ALBUM)));
                                            }
                                            else if(path.contains("mp3"))
                                            {
                                                Tag tag=f.getTag();
                                                Artwork artwork = new AndroidArtwork();
                                                artwork.setBinaryData(Ytsearch.convert_byte(bitmap));
                                                if (tag.getFirstArtwork() != null) {
                                                    tag.deleteArtworkField();
                                                    tag.setField(artwork);
                                                } else {
                                                    tag.addField(artwork);
                                                }
                                                if(tag.getFirst(FieldKey.ALBUM)==null)
                                                    tag.addField(FieldKey.ALBUM,MainActivity.song_id.get(current));
                                            }
                                            f.commit();
                                            MediaScannerConnection.scanFile(musicpage.ct_main, new String[]{songs.get(current)}, new String[]{"mp3/*"}, new MediaScannerConnection.OnScanCompletedListener() {
                                                @Override
                                                public void onScanCompleted(String path, Uri uri) {
                                                    ImagePipeline imagePipeline = Fresco.getImagePipeline();
                                                    Uri sArtworkUri = Uri
                                                            .parse("content://media/external/audio/albumart");
                                                    Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, MainActivity.songInfo.get(current).album_id);
                                                    imagePipeline.evictFromCache(albumArtUri);
                                                    StickyService.setting_icon();
                                                }
                                            });
                                        } catch (IOException | CannotReadException | ReadOnlyFileException | TagException | InvalidAudioFrameException | CannotWriteException e) {
                                            e.printStackTrace();
                                            Toast.makeText(MainActivity.con_main,"Error"+e,Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).show();
                    }
                }
                @Override
                public void onFailureImpl(DataSource dataSource) {

                }
            }, CallerThreadExecutor.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void stop_Download_Thread()
    {
        try {
            if(thumbnail2.isAlive())
                thumbnail2.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed()
    {
        if(!flag)
            MainActivity.b1.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
        else
            MainActivity.b1.setBackgroundResource(R.drawable.pause);
        k1=0;
        b1=null;
        MainActivity.bv.setSelectedItemId(Integer.parseInt(MainActivity.prev_bottom_view));
        super.onBackPressed();
    }
    @Override
    public void onDestroy()
    {
        k1=0;
        t1=t2=t3=t4=null;
        s2=null;
        b1=b3=b4=null;
        music=null;
        draw=null;
        change_name_of=null;
        new_name=null;
        i1=null;
        stop_Download_Thread();
        resultUrls=null;
         ct_main=null;
         music=null;
        super.onDestroy();
    }
}


