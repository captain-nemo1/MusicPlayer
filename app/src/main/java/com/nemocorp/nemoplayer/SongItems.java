package com.nemocorp.nemoplayer;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.core.content.res.ResourcesCompat;

import java.io.IOException;

public class SongItems {
    String song;
    String song_name;
    String song_artist;
    Long song_dur;
    Bitmap artwork;
    String album;
    Long album_id;
    public SongItems(String song, String song_name, String song_artist, Long song_dur, String album, Long album_id){
        this.song=song;
        this.song_name=song_name;
        this.song_artist=song_artist;
        this.song_dur=song_dur;
        this.album=album;
        this.album_id=album_id;
    }

    public void set_Artwork()
    {
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                Uri sArtworkUri = Uri
                        .parse("content://media/external/audio/albumart");
                Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, album_id);
                try {
                    artwork = MediaStore.Images.Media.getBitmap(MainActivity.con_main.getContentResolver(), albumArtUri);
                    artwork = Bitmap.createScaledBitmap(artwork, 60, 60, true);
                } catch (IOException e) {
                    artwork=MainActivity.drawableToBitmap(ResourcesCompat.getDrawable(MainActivity.main.getResources(),R.drawable.ic_music_note_black_24dp,null));
                }
            }
        });
        t.start();
    }
    public Bitmap get_Artwork()
    {
        return artwork;
    }

    public long get_Album_id(){
        return album_id;
    }
    public String get_Song()
    {
        return song;
    }
    public String getSong_Name()
    {
        return song_name;
    }public Long getSong_Dur()
    {
        return song_dur;
    }public String getSong_artist()
    {
        return song_artist;
    }
    public String get_Album(){
        return  album;
    }

}
