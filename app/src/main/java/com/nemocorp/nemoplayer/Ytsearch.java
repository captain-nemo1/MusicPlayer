package com.nemocorp.nemoplayer;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.commit451.youtubeextractor.Stream;
import com.commit451.youtubeextractor.YouTubeExtraction;
import com.commit451.youtubeextractor.YouTubeExtractor;
import com.netcompss.loader.LoadJNI;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.mp4.Mp4FieldKey;
import org.jaudiotagger.tag.mp4.Mp4Tag;
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

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.nemocorp.nemoplayer.MainActivity.loop;
import static com.nemocorp.nemoplayer.MainActivity.mediaPlayer;
import static com.nemocorp.nemoplayer.MainActivity.repeat1;
import static com.nemocorp.nemoplayer.MainActivity.t3;
import static com.nemocorp.nemoplayer.MainActivity.t4;


public class Ytsearch extends AppCompatActivity {
    EditText search;
    static ListView result;
    Thread t = null;
    Button b1;
    Thread t1 = null;
    static Thread t2 = null;
    static  int k;
    static String url;
    static String filetitle;
    static String name;
    static String author;
    static boolean streaming=false;
    static List<String> channel = new ArrayList<>();
    static List<String> title = new ArrayList<>();
    static List<String> id = new ArrayList<>();
    static List<String> dur = new ArrayList<>();
    static Bitmap thumnail;
    static byte[] Byte;
    String s;
    static List<Stream> videoStreams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ytsearch);
        b1 = findViewById(R.id.button4);
        search = findViewById(R.id.search);
        result = findViewById(R.id.result);
    }

    public void result(View view) {
        title.clear();
        id.clear();
        channel.clear();
        dur.clear();
        s = String.valueOf(search.getText());
        scrap();
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
        }
    }
    static public void download_links(int i)
    {
                k= i;
                author = channel.get(k);
                t2 = new Thread() {
                    @Override
                    public void run() {
                        final YouTubeExtractor extractor1 = new YouTubeExtractor.Builder().build();
                        String id_value = id.get(k);
                        id_value = id_value.substring(id_value.indexOf('=') + 1, id_value.length());
                        final Disposable y = extractor1.extract(id_value)
                                .subscribe(new Consumer<YouTubeExtraction>() {
                                    @Override
                                    public void accept(final YouTubeExtraction youTubeExtraction) throws Exception {
                                        Log.d("Tube", youTubeExtraction + "Done");
                                        videoStreams = youTubeExtraction.getStreams();
                                    }
                                });
                        String thumb_url = "https://i4.ytimg.com/vi/" + id_value + "/hqdefault.jpg";
                        try {
                            URL url = new URL(thumb_url);
                            thumnail = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            Log.d("values", thumnail + " \n " + thumb_url);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("values", "Failed Bitmap" + e);
                        }
                    }
                };
                t2.start();
                try {
                    t2.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }
                url = String.valueOf(videoStreams.get(videoStreams.size() - 4));
                Log.d("values", url);
                url = url.substring(url.indexOf('h'), url.lastIndexOf(','));
                convert_byte();
                Bitmap bit=BitmapFactory.decodeByteArray(Byte,0,Byte.length);
                MainActivity.stream_thumnail=bit;
         //   }
       // });
    }
    static public void stream(Context context){
       if(mediaPlayer!=null)
            mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
            if(loop==true)
                mediaPlayer.setLooping(true);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        MainActivity.t2.setVisibility(View.VISIBLE);
        MainActivity.t2.setText(title.get(k));
        MainActivity.stream_name=title.get(k);
        MainActivity.stream_channel=channel.get(k);
        int min=(mediaPlayer.getDuration()/1000)/60;
        int sec=(mediaPlayer.getDuration()/1000)%60;
        if(sec>=10)
            t4.setText(min+":"+sec);
        else
            t4.setText(min+":0"+sec);
        t3.setText("0:00");
        MainActivity.createThread();
        MainActivity.s.setEnabled(true);
        MainActivity.b1.setVisibility(View.VISIBLE);
        MainActivity.flag=true;
        MainActivity.check();
        streaming=true;
        repeat1();

        context.startService(MainActivity.service);
    }

    static public void download(Context context)
    {
        DownloadManager downloadmanager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDescription("Downloading");
        request.setTitle(title.get(k) + ".m4a");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        filetitle = title.get(k) + ".m4a";
        name = title.get(k);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filetitle);
        downloadmanager.enqueue(request);
        MainActivity.download_finished = 1;
        MainActivity.downloadfinished(context);
    }
    static public void change_format(Context context)
    {
        LoadJNI vk=new LoadJNI();
        try {
            String workFolder = context.getFilesDir().getAbsolutePath();
            String workFolder1 = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/"+filetitle));
            String s3= String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC))+"/"+name+".m4a";
            String s4= String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC))+"/"+name+"12.mp3";
            String[] complexCommand = {"ffmpeg","-i",workFolder1,"-vn","-c:a","copy",s3};
            vk.run(complexCommand ,workFolder,context);
            File f=new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/"+filetitle)));
            boolean t =f.delete();
            MediaScannerConnection.scanFile(context,new String[]{workFolder1},new String[]{"mp3/*"},null);//scan audio files so deleted files are reflected
            Log.i("values", "ffmpeg4android finished successfully");
        } catch (Throwable e) {
            Log.e("values", "vk run exception."+ e);
        }
    }
    public static void convert_byte()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumnail.compress(Bitmap.CompressFormat.PNG, 90, baos);
        Byte=baos.toByteArray();
        Log.d("values", "converted to byte array"+String.valueOf(Byte));
    }
    static public void add_tags(Context context) {
        File e1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC + "/"+name+".m4a");
        Log.d("values", String.valueOf(e1));
        try {
            AudioFile f = AudioFileIO.read(e1);
            Mp4Tag mp4tag = (Mp4Tag) f.getTag();
            mp4tag.setField(Mp4FieldKey.ARTIST, author);
            mp4tag.setField(Mp4FieldKey.TITLE,name);
                try{
                  //  convert_byte();
                    mp4tag.deleteField(Mp4FieldKey.ARTWORK);
                    Log.d("values", String.valueOf(mp4tag.get(Mp4FieldKey.ARTWORK)));
                    mp4tag.addField(mp4tag.createArtworkField(Byte));
                    Log.d("values", "ARTWORK ADDED");
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log.d("values", "cant set bitmap"+e);
                }
            String log_path=context.getFilesDir().getAbsoluteFile() + "/vk.log";
            File f1= new File(log_path);
            f1.delete();
            MediaScannerConnection.scanFile(context,new String[]{e1.getPath()},new String[]{"mp3/*"},null);//scan audio files so deleted files are reflected

            try {
                f.commit();
                Log.d("values","tagdone");
            } catch (CannotWriteException e) {
                e.printStackTrace();
            }
            Log.d("values", String.valueOf(mp4tag));
        } catch (CannotReadException ex) {
            ex.printStackTrace();
            Log.d("values", String.valueOf(ex));
        } catch (IOException ex) {
            ex.printStackTrace();
            Log.d("values", String.valueOf(ex));
        } catch (TagException ex) {
            ex.printStackTrace();
            Log.d("values", String.valueOf(ex));
        } catch (ReadOnlyFileException ex) {
            ex.printStackTrace();
            Log.d("values", String.valueOf(ex));
        } catch (InvalidAudioFrameException ex) {
            ex.printStackTrace();
            Log.d("values", String.valueOf(ex));
        }
    }
    public void scrap()
    {
        t1=new Thread()
        {
          @Override
          public void run()
          {
              Document doc = null;
              try {
                  doc = Jsoup.connect("https://www.youtube.com/results?search_query="+s).timeout(200000).get();
              } catch (IOException e) {
                  Log.d("values", String.valueOf(e));
                  e.printStackTrace();
              }
              Elements check=doc.getElementsByClass("yt-lockup");
              for(Element ch:check) {
                  try {
                      String k = String.valueOf(ch.getElementsByTag("h3"));
                      k = k.substring(k.lastIndexOf(' '), k.lastIndexOf('.'));
                      dur.add(k);
                      title.add(ch.getElementsByTag("a").attr("title"));
                      channel.add(ch.getElementsByClass("yt-uix-sessionlink       spf-link ").text());
                      id.add(ch.getElementsByTag("a").attr("href"));
                  } catch (Exception ex) {
                      ex.printStackTrace();
                  }
              }
          }
        };
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ytsearchadapter yt= new ytsearchadapter(this, title,dur,channel);
        yt.notifyDataSetChanged();
        result.setAdapter(yt);
    }

    @Override
    public void onBackPressed()
    {
        title.clear();
        id.clear();
        channel.clear();
        dur.clear();
        super.onBackPressed();
    }
}
