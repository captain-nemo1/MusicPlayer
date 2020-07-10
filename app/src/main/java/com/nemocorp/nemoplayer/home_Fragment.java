package com.nemocorp.nemoplayer;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class home_Fragment extends Fragment {
    Activity referenceActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parentholder = inflater.inflate(R.layout.home_fragment, container, false);
        MainActivity.list=parentholder.findViewById(R.id.list_recycle);
        MainActivity.list.setLongClickable(true);
        referenceActivity = getActivity();
        if(MainActivity.songs.isEmpty()) {
            new List_Async(referenceActivity.getApplicationContext()).execute("1");
        }
      //  MainActivity.createList(referenceActivity);
        /*MainActivity.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.play(position);
                MainActivity.playlist_play=0;
            }
        });
        MainActivity.list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                /*Log.i("values", "SONG ITEM LONG CLICKED");
                appendLog("SONG ITEM LONG CLICKED");
                dialog.setMessage(song_name.get(i))
                        .setTitle("Delete")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    File del = new File(songs.get(i));
                                    boolean f=del.delete();
                                    if(f)
                                    {    Toast.makeText(getApplicationContext(), "Deleted " + song_name.get(i)+"Restart App to reflect change", Toast.LENGTH_SHORT).show();
                                        ls=new List_Async(getApplicationContext());
                                        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{songs.get(i)}, new String[]{"mp3/*"}, new MediaScannerConnection.OnScanCompletedListener() {
                                            @Override
                                            public void onScanCompleted(String s, Uri uri) {
                                                ls.execute(song_name.get(i));
                                            }
                                        });
                                    }
                                    else
                                        Toast.makeText(getApplicationContext(), "Can't Delete files in SD-Card", Toast.LENGTH_SHORT).show();
                                    MediaScannerConnection.scanFile(getApplicationContext(),new String[]{songs.get(i)},new String[]{"mp3/*"},null);//scan audio files so deleted files are reflected
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    appendLog(String.valueOf(ex));
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

                int k;
                if(MainActivity.temp4.size()>0)
                    k=MainActivity.temp4.get(i);
                else
                    k=i;
                Log.d("playlist", k+" ");
                MainActivity.playlist_songs.add(MainActivity.songs.get(k));
                MainActivity.playlist_song_artist.add(MainActivity.song_artist.get(k));
                MainActivity.playlist_song_name.add(MainActivity.song_name.get(k));
                return true;
            }
        });*/
        return parentholder;
    }
}
