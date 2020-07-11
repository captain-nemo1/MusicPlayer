package com.nemocorp.nemoplayer;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Playlist extends Fragment {
    ListView list_play;
    Activity referenceActivity;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parentholder;
        parentholder=inflater.inflate(R.layout.playlist,container,false);
        referenceActivity=getActivity();
        list_play=parentholder.findViewById(R.id.playlist_list);
        return parentholder;
    }
    @Override
    public void onResume()
    {
        myAdapter playlit = new myAdapter(referenceActivity, MainActivity.playlist_song_name, MainActivity.playlist_song_artist);
        list_play.setAdapter(playlit);
            list_play.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    MainActivity.playlist_song_artist.remove(i);
                    MainActivity.playlist_song_name.remove(i);
                    MainActivity.playlist_songs.remove(i);
                    playlit.notifyDataSetChanged();
                    return false;
                }
            });
        list_play.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String k=MainActivity.playlist_songs.get(i);
                int p=MainActivity.songs.indexOf(k);
                MainActivity.play(p);
                MainActivity.playlist_index=i;
                MainActivity.playlist_play=1;
            }
        });
        super.onResume();
    }
}
