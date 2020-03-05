    package com.nemocorp.nemoplayer;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.app.ActivityCompat;
    import androidx.core.content.ContextCompat;

    import android.Manifest;
    import android.content.ContentResolver;
    import android.content.Context;
    import android.content.pm.PackageManager;
    import android.database.Cursor;
    import android.media.AudioManager;
    import android.media.MediaPlayer;
    import android.net.Uri;
    import android.os.Bundle;
    import android.provider.MediaStore;
    import android.util.Log;
    import android.view.View;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.Button;
    import android.widget.ListView;
    import android.widget.SeekBar;
    import android.widget.TextView;
    import android.widget.Toast;
    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Random;

    import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;


    public class MainActivity extends AppCompatActivity {
        TextView t1, t2, t3, t4;
        ListView list;
        Button b1, b2, b3, b4,b5;
        SeekBar s;
        int current;
        boolean loop = false;
        static boolean flag = false;
        static boolean shuffle=false;
        static MediaPlayer mediaPlayer;
        private List<String> songs = new ArrayList<String>();
        private List<String> song_name = new ArrayList<>();
        Thread t;
        AudioManager audioManager;
        static  boolean wantsmusic=false;

        AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {

            @Override
            public void onAudioFocusChange(int focusChange) {

                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_GAIN:
                            Log.d("AudioFocus", "AUDIOFOCUS_GAIN");
                            if (mediaPlayer != null && wantsmusic) {
                                Pause1();
                            }
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS:
                            Log.d("AUDIOFOCUS", "AUDIOFOCUS_LOSS");
                            flag = true;
                            wantsmusic=true;
                            Pause1();
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            Log.d("AUDIOFOCUS", "AUDIOFOCUS_LOSS_TRANSIENT");
                            //Loss of audio focus for a short time;
                            flag = true;
                            wantsmusic=true;
                            Pause1();//Pause playing the sound
                            break;
                    }
                }

        };



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            list = findViewById(R.id.list);
            t1 = findViewById(R.id.textView4);
            t2 = findViewById(R.id.title);
            t3 = findViewById(R.id.starttime);
            t4 = findViewById(R.id.endtime);
            b1 = findViewById(R.id.play);
            b1.setVisibility(View.INVISIBLE);
            b2 = findViewById(R.id.previous);
            b3 = findViewById(R.id.next);
            b4 = findViewById(R.id.repeat);
            b5=findViewById(R.id.shuffle);
            s = findViewById(R.id.seekBar);
            t1.setTextColor(getResources().getColor(R.color.turqoise, null));
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 101);
            getsong();
            mediaPlayer = new MediaPlayer();
            audioManager =(AudioManager) getSystemService(Context.AUDIO_SERVICE);
            ArrayAdapter<String> ar = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, song_name);
            list.setAdapter(ar);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String songname = songs.get(position);
                    stopifSongPlaying();

                    media(songname);
                    current = position;
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopifSongPlaying();
                    if(shuffle==true)
                    {
                        Random r=new Random();
                        current=r.nextInt(songs.size()-1);
                    }
                    next1();
                }
            });

        }
        public void requestAudio()
        {
            audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
            int result = audioManager.requestAudioFocus( mOnAudioFocusChangeListener,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                Log.d("AudioFocus", "Audio focus received");
            } else {
                Log.d("AudioFocus", "Audio focus NOT received");
            }
        }




        public void shuffleon(View view)
        {
            if(shuffle==true)
            {
                shuffle=false;
                b5.setBackgroundResource(R.drawable.shuffle1);
            }
            else
            {
                shuffle=true;
                b5.setBackgroundResource(R.drawable.shuffle2);
            }
        }


        public void stopifSongPlaying() {
            if (flag == true) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    flag = false;
                    mediaPlayer.reset();
                    s.setProgress(0);
                }
                t.interrupt();
            }
        }
        public void Pause(View view) {
            Pause1();
            wantsmusic=false;
        }
        public void Pause1()
        {
            if (flag == true) {
                if (mediaPlayer != null) {
                    mediaPlayer.pause();
                    flag = false;
                }
            } else {
                if(mediaPlayer!=null)
                {mediaPlayer.start();
                    flag = true;
                }
            }
        }

        public void media(String path) {
            b1.setVisibility(View.VISIBLE);
            t2.setText(path.substring(path.lastIndexOf("/") + 1, path.length()));
            Uri mp3 = Uri.parse(path);
            t3.setText("0:00");
            requestAudio();
            try {
                mediaPlayer.setDataSource(getApplicationContext(), mp3);
                mediaPlayer.prepare();
                mediaPlayer.start();
                //getSeekBarStatus();
                int min = (mediaPlayer.getDuration() / 1000) / 60;
                int sec = (mediaPlayer.getDuration() / 1000) % 60;
                if (sec < 10)
                    t4.setText(min + ":0" + sec);
                else
                    t4.setText(min + ":" + sec);
                flag = true;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("MEDIAPLAYER ERROR", String.valueOf(e));
            }

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

        public void previous(View view) {
            if (current == 0) {
                current = 0;
            } else {
                current -= 1;
            }
            String prevsong = songs.get(current);
            stopifSongPlaying();
            media(prevsong);

        }

        public void next1() {
            if (current == songs.size() - 1)
                current -= 1;
            current += 1;
            String nextsong = songs.get(current);
            stopifSongPlaying();
            media(nextsong);
        }

        public void next(View view) {
            next1();
        }

        public void repeat(View view) {
            if (loop == false) {
                loop = true;
                mediaPlayer.setLooping(loop);
                b4.setBackgroundResource(R.drawable.donot);
                Toast.makeText(this, "Repeat ON", Toast.LENGTH_SHORT).show();
            } else {
                loop = false;
                mediaPlayer.setLooping(loop);
                b4.setBackgroundResource(R.drawable.repeat);
                Toast.makeText(this, "Repeat OFF", Toast.LENGTH_SHORT).show();

            }

        }

        public void checkPermission(String permission, int requestCode) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                    == PackageManager.PERMISSION_DENIED) {
                // Requesting the permission
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission},                    requestCode);
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
                    Toast.makeText(MainActivity.this,"Storage Permission Denied",Toast.LENGTH_SHORT).show();
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
                    songs.add(s);
                    song_name.add(s.substring(s.lastIndexOf("/") + 1, s.lastIndexOf('.')));
                } while (songCursor.moveToNext());
            }

        }
    @Override
    public void onDestroy()
    {
        mediaPlayer.release();
        mediaPlayer.stop();
        mediaPlayer=null;
        super.onDestroy();
    }
}



