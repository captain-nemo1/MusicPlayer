package com.nemocorp.nemoplayer;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.mp4.Mp4FieldKey;
import org.jaudiotagger.tag.mp4.Mp4Tag;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
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
import static com.nemocorp.nemoplayer.MainActivity.shuffle;
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
    static String prog;
    static String change_name_of="none";
    static String new_name="";
    static ImageView i1;
    static Thread th;
    LinearLayout c1;
    Intent service;
    Bitmap new_thumbnail;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicpage);
        i = getIntent();
        MainActivity.appendLog("MUSICPAGE CREATED");
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
                        {th.interrupt();        MainActivity.appendLog("MUSICPAGE INTERRUPTING ITS THREAD");
                        }
                        MainActivity.appendLog("MUSICPAGE LEAVING");

                        onBackPressed();
                    }
                });
                c1.setOnTouchListener(new OnSwipeTouchListener(musicpage.this) {
                    public void onSwipeBottom() {
                        if(th.isAlive())
                            th.interrupt();
                        MainActivity.appendLog("MUSICPAGE LEAVINg SWIPE BOTTOM");
                        onBackPressed();
                    }
                    public void onSwipeTop()
                    {
                        if(th.isAlive())
                            th.interrupt();
                        MainActivity.appendLog("MUSICPAGE LEAVINg SWIPE up");
                        onBackPressed();
                    }
                    public void onSwipeRight() {
                        MainActivity.next1();
                            MediaMetadataRetriever m= new MediaMetadataRetriever();
                            m.setDataSource(songs.get(MainActivity.current));
                            try {
                                byte[] a = m.getEmbeddedPicture();
                                Bitmap c = BitmapFactory.decodeByteArray(a, 0, a.length);
                                Drawable d = new BitmapDrawable(getResources(), c);
                                i1.setBackground(d);
                                background(c);
                            } catch (Exception e) {
                                i1.setBackground(getResources().getDrawable(R.drawable.ic_music_note_black_24dp));
                                c1.setBackgroundColor(getResources().getColor(R.color.black));
                                t1.setTextColor(getResources().getColor(R.color.white));
                                t4.setTextColor(getResources().getColor(R.color.white));
                            }
                                int min = Math.toIntExact((song_dur.get(current) / 1000) / 60);
                                int sec = Math.toIntExact((song_dur.get(current) / 1000) % 60);
                                if (sec >= 10)
                                    t3.setText(min + ":" + sec);
                                else
                                    t3.setText(min + ":0" + sec);
                                t2.setText("0:00");
                            //    seek();
                                if (!song_artist.get(current).equals("<unknown>")) {
                                    t1.setText(song_name.get(current));
                                    t4.setText(song_artist.get(current));
                                } else {
                                    t1.setText(song_name.get(current));
                                    t4.setText("");
                                }
                    }
                    public void onSwipeLeft() {
                        MainActivity.previous1();
                        MediaMetadataRetriever m = new MediaMetadataRetriever();
                        m.setDataSource(songs.get(MainActivity.current));
                        try {
                            byte[] a = m.getEmbeddedPicture();
                            Bitmap c = BitmapFactory.decodeByteArray(a, 0, a.length);
                            Drawable d = new BitmapDrawable(getResources(), c);
                            i1.setBackground(d);
                            background(c);
                        } catch (Exception e) {
                            i1.setBackground(getResources().getDrawable(R.drawable.ic_music_note_black_24dp));
                            c1.setBackgroundColor(getResources().getColor(R.color.black));
                            t1.setTextColor(getResources().getColor(R.color.white));
                            t4.setTextColor(getResources().getColor(R.color.white));
                        }
                        int min = Math.toIntExact((song_dur.get(current) / 1000) / 60);
                        int sec = Math.toIntExact((song_dur.get(current) / 1000) % 60);
                        if (sec >= 10)
                            t3.setText(min + ":" + sec);
                        else
                            t3.setText(min + ":0" + sec);
                        t2.setText("0:00");
                       // seek();
                            if (!song_artist.get(current).equals("<unknown>")) {
                                t1.setText(song_name.get(current));
                                t4.setText(song_artist.get(current));
                            } else {
                                t1.setText(song_name.get(current));
                                t4.setText("");
                            }
                    }
                });

            }
        },1000);
        k1=1;
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
                Drawable d = new BitmapDrawable(getResources(), c);
                i1.setBackground(d);
                background(c);
            } catch (Exception e) {
                i1.setBackground(getResources().getDrawable(R.drawable.ic_music_note_black_24dp));
                c1.setBackgroundColor(getResources().getColor(R.color.black));
                t1.setTextColor(getResources().getColor(R.color.white));
                t4.setTextColor(getResources().getColor(R.color.white));
            }
        MainActivity.appendLog("MUSICPAGE ON CREATE FINISHED");
            t1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    change_name_of="title";
                    Name_Dialog();
                }
            });
            t4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    change_name_of="artist";
                    Name_Dialog();
                }
            });
            i1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    change_name_of="album";
                    Name_Dialog();
                }
            });

    }

    public Palette createPaletteSync(Bitmap bitmap) {
        Palette p = Palette.from(bitmap).generate();
        return p;
    }

     public void background(Bitmap c)
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
             Log.d("color", String.valueOf(color)+"heloppp");
             int[] colors = {color, Color.LTGRAY};
             GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
             int titlecolor;
             titlecolor =color3.getTitleTextColor();
             c1.setBackground(gd);
             t1.setTextColor(titlecolor);
             t4.setTextColor(titlecolor);
         } catch (Exception e) {
             e.printStackTrace();
         }
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
    public void Name_Dialog() {
        final EditText taskEditText = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Change Name")
                .setMessage("Enter The Name")
                .setView(taskEditText)
                .setCancelable(false)
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new_name= String.valueOf(taskEditText.getText());
                        if(change_name_of=="album" && !new_name.equals(""))
                            Get_Thumnail();
                           // test(null);
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
        if(change_name_of=="title")
            taskEditText.setText(song_name.get(current));
        else if(change_name_of=="artist")
            taskEditText.setText(song_artist.get(current));

        dialog.show();
    }
    public void name_change()
    {
        File song_selected=new File(songs.get(current));
        if(songs.get(current).contains(".m4a")) {
            Log.d("Path", new_name);
            if (!new_name.equals("")) {
                try {
                    AudioFile f = AudioFileIO.read(song_selected);
                    Mp4Tag mp4tag = (Mp4Tag) f.getTag();
                    if (change_name_of.equals("artist"))
                        mp4tag.setField(Mp4FieldKey.ARTIST, new_name);
                    else if (change_name_of.equals("title"))
                        mp4tag.setField(Mp4FieldKey.TITLE, new_name);
                    f.commit();
                    MediaScannerConnection.scanFile(getApplicationContext(), new String[]{songs.get(current)}, new String[]{"mp3/*"}, null);
                    Toast.makeText(getApplicationContext(), "Will Show Changed Name Next Time App is Opened.", Toast.LENGTH_LONG).show();
                } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException | CannotWriteException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error Reading Files", Toast.LENGTH_LONG).show();
                    MainActivity.appendLog("Change Artist or Title Error" + e);
                }
            }
        }
        else
            Toast.makeText(getApplicationContext(), "Can't change tags of formats other than m4a", Toast.LENGTH_LONG).show();
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
    public void change_Album_Art()
    {
        MediaMetadataRetriever m= new MediaMetadataRetriever();
        m.setDataSource(songs.get(MainActivity.current));
        try {
            byte[] a = m.getEmbeddedPicture();
            Bitmap c = BitmapFactory.decodeByteArray(a, 0, a.length);
            Drawable d = new BitmapDrawable(getResources(), c);
            i1.setBackground(d);
            background(c);
        } catch (Exception e) {
            i1.setBackground(getResources().getDrawable(R.drawable.ic_music_note_black_24dp));
            c1.setBackgroundColor(getResources().getColor(R.color.black));
            t1.setTextColor(getResources().getColor(R.color.white));
            t4.setTextColor(getResources().getColor(R.color.white));
        }
    }

    @Override
    public void onResume()
    {
        if(t1.getText()!=song_name.get(current) && t1.getText()!=song_artist.get(current)+"-"+song_name.get(current))
        {
            if((!song_artist.get(current).equals("<unknown>"))&&(song_name.get(current).toLowerCase().contains(song_artist.get(current).toLowerCase()))==false)
                t1.setText(song_artist.get(current)+"-"+song_name.get(current));
            else
                t1.setText(song_name.get(current));
            t4.setText(song_artist.get(current));
            int min = (mediaPlayer.getDuration() / 1000) / 60;
            int sec = (mediaPlayer.getDuration() / 1000) % 60;
           // seek();//for seekbar
            if (sec < 10)
                t3.setText(min + ":0" + sec);
            else
                t3.setText(min + ":" + sec);
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
                                    Log.d("valuesphoto2",temp);
                                }
                        }
                        //Log.d("values",resultUrls.get(0)+"");
                        if(resultUrls.size()>0) {
                            downloadImages(resultUrls.get(0));
                            break;
                        }
                       // else
                         //   Toast.makeText(getApplicationContext(), "Cant Find Album Art",Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }};
        thumbnail.start();
    }
    public void test(View view)//Google
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
    private final Handler uiHandler = new Handler();

    private class JSHtmlInterface {
        @android.webkit.JavascriptInterface
        public void showHTML(String html) {
            final String htmlContent = html;

            uiHandler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            Document doc = Jsoup.parse(htmlContent);
                            Log.d("values", doc.toString());
                            Elements elements = doc.getElementsByClass("eHAdSb");
                            for (Element element : elements) {
                                Log.d("values", element.toString()+" help");
                            }
                        }
                    }
            );
        }
    }
    public void downloadImages(String inSrc) throws IOException {
        MainActivity.imageurl = new URL(inSrc);
        new_thumbnail=BitmapFactory.decodeStream(MainActivity.imageurl.openConnection().getInputStream());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new_thumbnail.compress(Bitmap.CompressFormat.PNG, 90, baos);
        byte[] Byte=baos.toByteArray();
        Bitmap bit = BitmapFactory.decodeByteArray(Byte, 0, Byte.length);
        new_thumbnail=bit;
        Drawable d = new BitmapDrawable(getResources(), new_thumbnail);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                i1.setBackground(d);
                Toast.makeText(getApplicationContext(),"CHANGED PICTURE", Toast.LENGTH_LONG).show();
            }
        });
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
        MainActivity.appendLog("MUSICPAGE BACK PRESSED THREAD STATUS"+th.isAlive()+"\n");
        Log.d("Thread", String.valueOf(th.isAlive()));
        b1=null;
        super.onBackPressed();
    }
    @Override
    public void onDestroy()
    {
        k1=0;
        super.onDestroy();
    }
}

