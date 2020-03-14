package com.nemocorp.nemoplayer;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.commit451.youtubeextractor.Stream;
import com.commit451.youtubeextractor.YouTubeExtraction;
import com.commit451.youtubeextractor.YouTubeExtractor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class Ytsearch extends AppCompatActivity {
    EditText search;
    ListView result;
    Thread t=null;
    Button b1;
    Thread t1=null;
    Thread t2=null;
    Thread t3=null;
    String filetitle;
    static List<String> title=new ArrayList<>();
    static List<String> id=new ArrayList<>();
    static List<String> dur=new ArrayList<>();

    String s;
    List<Stream> videoStreams;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ytsearch);
        b1=findViewById(R.id.button4);
        search=findViewById(R.id.search);
        result=findViewById(R.id.result);



    }
    public void result(View view){
        title.clear();
        id.clear();
        b1.setBackgroundColor(Color.GRAY);
        s=String.valueOf(search.getText());
        scrap();

      result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                result.setDrawSelectorOnTop(false);
                Toast.makeText(getApplicationContext(), "Freeze Time depends on your internet connection", Toast.LENGTH_SHORT).show();
                t2=new Thread(){
                    @Override
                    public void run()
                    {
                        final YouTubeExtractor extractor1 = new YouTubeExtractor.Builder().build();
                        String s=id.get(i);
                        s=s.substring(s.indexOf('=')+1, s.length());
                        final Disposable y = extractor1.extract(s)
                                .subscribe(new Consumer<YouTubeExtraction>() {
                                    @Override
                                    public void accept(final YouTubeExtraction youTubeExtraction) throws Exception {
                                        Log.d("Tube", youTubeExtraction + "Done");
                                        videoStreams = youTubeExtraction.getStreams();
                                    }
                                });
                    }
                };
                t2.start();
                try {
                    t2.join();
                    Log.d("values", String.valueOf(videoStreams.get(videoStreams.size()-4)));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String url= String.valueOf(videoStreams.get(videoStreams.size()-4));
                url=url.substring(url.indexOf('h'),url.lastIndexOf(','));
                DownloadManager downloadmanager = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(url);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setDescription("Downloading");
                request.setTitle(title.get(i)+".m4a");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                filetitle=title.get(i)+".m4a";
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filetitle);
                downloadmanager.enqueue(request);
            }
        });

       BroadcastReceiver onComplete=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context,"Close and Open App to get the downloaded song in list", Toast.LENGTH_SHORT).show();
            }
        };

        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }
    public void click(View view)
    {
        String path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+"juicy.mp4";
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
                  doc = Jsoup.connect("https://www.youtube.com/results?search_query="+s).get();
              } catch (IOException e) {
                  e.printStackTrace();
              }
              Elements Headlines = doc.select("h3");
              for (Element headline : Headlines) {
                      String k=String.valueOf(headline);
                      try {
                          k = k.substring(k.lastIndexOf(' '), k.lastIndexOf('.'));
                          dur.add(k);
                          title.add(headline.getElementsByTag("a").attr("title"));
                          id.add(headline.getElementsByTag("a").attr("href"));
                      } catch (Exception e) {
                          e.printStackTrace();
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
        ytsearchadapter yt= new ytsearchadapter(this, title,dur);
        result.setAdapter(yt);

    }

    @Override
    public void onBackPressed()
    {
        title.clear();
        id.clear();
        super.onBackPressed();
    }
}
