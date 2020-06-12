package com.nemocorp.nemoplayer;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.kotvertolet.youtubejextractor.JExtractorCallback;
import com.github.kotvertolet.youtubejextractor.YoutubeJExtractor;
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException;
import com.github.kotvertolet.youtubejextractor.models.AdaptiveAudioStream;
import com.github.kotvertolet.youtubejextractor.models.youtube.videoData.YoutubeVideoData;
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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.nemocorp.nemoplayer.MainActivity.appendLog;
import static com.nemocorp.nemoplayer.MainActivity.loop;
import static com.nemocorp.nemoplayer.MainActivity.mediaPlayer;
import static com.nemocorp.nemoplayer.MainActivity.repeat1;
import static com.nemocorp.nemoplayer.MainActivity.t3;
import static com.nemocorp.nemoplayer.MainActivity.t4;


public class Ytsearch extends AppCompatActivity {
    EditText search;
    static ListView result;
    static Thread t = null;
    static Button b1;
    static int k=-1;
    static String url;
    static String filetitle;
    static String name;
    static String author;
    static ScrapAsync r;
    static boolean streaming=false;
    static List<String> channel = new ArrayList<>();
    static List<String> title = new ArrayList<>();
    static List<String> id = new ArrayList<>();
    static List<String> dur = new ArrayList<>();
    static Bitmap thumnail;
    static byte[] Byte;
    String s;
    static Context con;
    static boolean downloading=false;
    static boolean prepare_stream=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ytsearch);
        b1 = findViewById(R.id.button4);
        search = findViewById(R.id.search);
        result = findViewById(R.id.result);
        con=getApplicationContext();
        MainActivity.internet_flag=MainActivity.Check_Internet(this);
        if(MainActivity.internet_flag)b1.setClickable(true);
        else b1.setClickable(false);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() { //on press enter search
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    result(null);
                    return true;
                }
                return false;
            }
        });

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
            if (imm != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            appendLog(String.valueOf(e));
        }
    }
    static public void download_links(int i,String ch)
    {
        if (!downloading&&!prepare_stream)
        {
            k = i;
            author = channel.get(k);
            r=new ScrapAsync(null);
            r.execute(ch);
                if(ch.equals("1"))downloading=true;
                else if(ch.equals("2"))prepare_stream=true;
        }
        else
        {
            if(downloading==true)
                Toast.makeText(con,"Preparing for Download Right now, Please Wait",Toast.LENGTH_LONG).show();
            else if(prepare_stream==true)
                Toast.makeText(con,"Preparing for Streaming Right now, Please Wait",Toast.LENGTH_LONG).show();
        }
    }
    static public void stream(Context context){
        Log.d("values","IN stream");
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        try {
            mediaPlayer.setDataSource(url.substring(0,url.length()-1));
            mediaPlayer.prepareAsync();
            if(loop==true)
                mediaPlayer.setLooping(true);
        }
        catch (IOException e) {
            e.printStackTrace();
            appendLog(String.valueOf(e));
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                mediaPlayer.start();
                prepare_stream=false;
                int min=(mediaPlayer.getDuration()/1000)/60;
                int sec=(mediaPlayer.getDuration()/1000)%60;
                if(sec>=10)
                    t4.setText(min+":"+sec);
                else
                    t4.setText(min+":0"+sec);
                MainActivity.createThread();
                t3.setText("0:00");
                Ytsearch.b1.setText("Search");
                context.startService(MainActivity.service);
            }
        });
        Ytsearch.b1.setText("Preparing Wait a few more seconds");
        MainActivity.t2.setVisibility(View.VISIBLE);
        MainActivity.t2.setText(title.get(k));
        MainActivity.stream_name=title.get(k);
        MainActivity.stream_channel=channel.get(k);
        MainActivity.s.setEnabled(true);
        MainActivity.b1.setVisibility(View.VISIBLE);
        MainActivity.flag=true;
        MainActivity.b1.setBackgroundResource(R.drawable.pause);
        //MainActivity.check();
        streaming=true;
        loop=false;
        repeat1();
    }

    static public void download(Context context)
    { DownloadManager downloadmanager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url.substring(0,url.length()-1));
        Log.d("URL",url);
      DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDescription("Downloading");
        if(title.get(k).contains("/")) {
            String t=title.get(k).replaceAll("/"," or ");
            title.set(k,t);
        }
        else if(title.get(k).contains("#")){
            String t=title.get(k).replaceAll("#","");
            title.set(k,t);
        }
        if(title.get(k).contains(":")) {
           String t=title.get(k).replaceAll(":", "-");
           title.set(k,t);
        }
        filetitle = title.get(k) + ".m4a";
        name = title.get(k);
        request.setTitle(title.get(k) + ".m4a");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filetitle);
        downloadmanager.enqueue(request);
        downloading=false;
        MainActivity.download_finished = 1;
        Ytsearch.b1.setText("Downloading");
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
            appendLog(String.valueOf(e));

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
                    appendLog(String.valueOf(e));

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
                appendLog(String.valueOf(e));
            }
            Log.d("values", String.valueOf(mp4tag));
        } catch (CannotReadException ex) {
            ex.printStackTrace();
            appendLog(String.valueOf(ex));

            Log.d("values", String.valueOf(ex));
        } catch (IOException ex) {
            ex.printStackTrace();
            appendLog(String.valueOf(ex));
            Log.d("values", String.valueOf(ex));
        } catch (TagException ex) {
            ex.printStackTrace();
            appendLog(String.valueOf(ex));
            Log.d("values", String.valueOf(ex));
        } catch (ReadOnlyFileException ex) {
            ex.printStackTrace();
            appendLog(String.valueOf(ex));
            Log.d("values", String.valueOf(ex));
        } catch (InvalidAudioFrameException ex) {
            ex.printStackTrace();
            appendLog(String.valueOf(ex));
            Log.d("values", String.valueOf(ex));
        }
    }
    public void scrap()
    {
        clear();
        b1.setClickable(false);
        ScrapAsync runner=new ScrapAsync(this);
        String[] v=new String[2];
        v[0]="0";
        v[1]=s;
        runner.execute(v);
    }
    public void clear()
    {
        title.clear();
        id.clear();
        channel.clear();
        dur.clear();
    }
    @Override
    public void onResume()
    {
        MainActivity.appendLog("YTSEARCH RESUME STARTED");
        MainActivity.internet_flag=MainActivity.Check_Internet(this);
        if(MainActivity.internet_flag){
            MainActivity.scrap_speed();b1.setClickable(true);}
        else b1.setClickable(false);
        super.onResume();
        MainActivity.appendLog("YTSEARCH RESUME ENDED");

    }

    @Override
    public void onDestroy()
    {
        clear();
        result.setAdapter(null);
        MainActivity.appendLog("YTSEARCH DESTROYED\n");
        MainActivity.t.interrupt();
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer=null;
        b1=null;
        super.onDestroy();
    }
    static public void getThumbnail(String id_value) {
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                String thumb_url =id_value;
                try {
                    URL url = new URL(thumb_url);
                    Ytsearch.thumnail = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    Log.d("values", Ytsearch.thumnail + " \n " + thumb_url);
                } catch (IOException e) {
                    e.printStackTrace();
                    appendLog(String.valueOf(e));
                    Log.d("values", "Failed Bitmap" + e);
                }
                Ytsearch.convert_byte();
                Bitmap bit = BitmapFactory.decodeByteArray(Ytsearch.Byte, 0, Ytsearch.Byte.length);
                MainActivity.stream_thumnail = bit;
            }
        });
        t.start();
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
        MainActivity.appendLog("YTSEARCH MOVING BACK TO MAINACTIVITY");
        startActivity(new Intent(this,MainActivity.class));
    }

}
class ScrapAsync extends AsyncTask<String, String, String> {
    Activity ctx;
    String id_value;
    String thumbnail_link;
    ScrapAsync(Activity c)
    {
        ctx=c;
    }
    @Override
    protected String doInBackground(String... strings) {
        String choice=strings[0];
        MainActivity.appendLog("YTSEARCH ENTERED ASYNC SCRAP");
        publishProgress(choice);
        if(choice.equals("0")) {
             MainActivity.doc = null;
            String f = strings[1];
            try {
                MainActivity.doc = Jsoup.connect("https://www.youtube.com/results?search_query=" + f)
                        .timeout(200000)//200000
                        .userAgent("Mozilla")
                        .ignoreContentType(true)
                        .get();
                appendLog("YTSEARCH SCRAp Done");
            } catch (IOException e) {
                Log.d("values", String.valueOf(e));
                appendLog("YTSEARCH SCRAP FAIL"+String.valueOf(e));
                e.printStackTrace();
            }
            Elements check = MainActivity.doc.getElementsByClass("yt-lockup");
            for (Element ch : check) {
                try {
                    String k = String.valueOf(ch.getElementsByTag("h3"));
                    k = k.substring(k.lastIndexOf(' '), k.lastIndexOf("</span"));
                    char chare=k.charAt(k.length()-1);
                    if(!Character.isDigit(chare)){//to replace . or any other character in the end
                        k=k.substring(0,k.length()-1);
                    }
                    String name=ch.getElementsByTag("a").attr("title");
                    Ytsearch.dur.add(k);
                    Ytsearch.title.add(ch.getElementsByTag("a").attr("title"));
                    Ytsearch.channel.add(ch.getElementsByClass("yt-uix-sessionlink" +
                            "       spf-link ").text());
                    Ytsearch.id.add(ch.getElementsByTag("a").attr("href"));
                } catch (Exception ex) {
                    Log.d("Values","Failed");
                    ex.printStackTrace();
                    appendLog(String.valueOf(ex));

                }
            }
        }
        else
        {
            id_value = Ytsearch.id.get(Ytsearch.k);
            id_value = id_value.substring(id_value.indexOf('=') + 1);
            try{
                String youtubeLink =Ytsearch.id.get(Ytsearch.k);
                youtubeLink=youtubeLink.substring(youtubeLink.indexOf("=")+1);
                YoutubeJExtractor youtubeJExtractor = new YoutubeJExtractor();
                youtubeJExtractor.extract(youtubeLink, new JExtractorCallback() {
                    @Override
                    public void onSuccess(YoutubeVideoData videoData) {
                        List<AdaptiveAudioStream> audioStreams = videoData.getStreamingData().getAdaptiveAudioStreams();
                        String js= String.valueOf(audioStreams.get(0));
                        Log.d("LINK", videoData.getStreamingData()+"");
                        String k= String.valueOf(videoData.getVideoDetails().getThumbnail());
                        String[] urls=k.split("ThumbnailsItem");
                        k=urls[urls.length-1];//gettting the highest resolution thumbnail available
                        k=k.substring(k.indexOf("https"),k.lastIndexOf(",")-1);
                        thumbnail_link=k;
                        Ytsearch.url=js.substring(js.indexOf("https"),js.lastIndexOf(","));
                        Log.d("URL", Ytsearch.url);
                    }
                    @Override
                    public void onNetworkException(YoutubeRequestException e) {
                    }
                    @Override
                    public void onError(Exception exception) {
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                appendLog(String.valueOf(e));
                choice="5";
            }
            }
        Log.d("values", "THREAD DONE"+choice);
        return choice;
    }
    @Override
    protected void onPostExecute(String result) {
        if(!result.equals("5")){
        if (result.equals("0")) {
            Ytsearch.b1.setClickable(true);
            Ytsearch.b1.setText("Search");
            ytsearchadapter yt = new ytsearchadapter(ctx, Ytsearch.title, Ytsearch.dur, Ytsearch.channel);
            yt.notifyDataSetChanged();
            Ytsearch.result.setAdapter(yt);
        }
        else if(result.equals("1"))
        {
            Ytsearch.download(Ytsearch.con);
            Ytsearch.getThumbnail(thumbnail_link);
        }
        else
        {
            Ytsearch.stream(Ytsearch.con);
            Ytsearch.getThumbnail(thumbnail_link);
        }
        MainActivity.appendLog("YTSEARCH Exiting ASYNC SCRAP");

    }}

    @Override
    protected void onPreExecute() {
       Ytsearch.b1.setText("Searching");
    }

    @Override
    protected void onProgressUpdate(String... text) {
        String u=text[0];
        if(u.equals("0"))
        {
            Ytsearch.b1.setText("Searching");
        }
        else if(u.equals("1"))
        {
            Ytsearch.b1.setText("Preparing for Download");
        }
        else
            Ytsearch.b1.setText("Preparing For Streaming");
    }
    }
