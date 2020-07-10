package com.nemocorp.nemoplayer.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nemocorp.nemoplayer.MainActivity;
import com.nemocorp.nemoplayer.R;

public class MainList extends RecyclerView.Adapter<MainList.MyViewHolder> {

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        View view = li.inflate(R.layout.row2, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ImageView img_more = holder.img_more;
        TextView t1 = holder.t1;
        TextView t2 = holder.t2;
        if(MainActivity.temp4.size()>0){
            try {
                int p = MainActivity.temp4.get(position);
                Glide.with(MainActivity.main).load(MainActivity.songInfo.get(p).get_Artwork()).placeholder(R.drawable.ic_music_note_black_24dp).into(img_more);
                t1.setText(MainActivity.songInfo.get(p).getSong_Name());
                t2.setText(MainActivity.songInfo.get(p).getSong_artist());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            Glide.with(MainActivity.main).load(MainActivity.songInfo.get(position).get_Artwork()).into(img_more);
            t1.setText(MainActivity.songInfo.get(position).getSong_Name());
            t2.setText(MainActivity.songInfo.get(position).getSong_artist());
        }
    }

    @Override
    public int getItemCount() {
        return MainActivity.songInfo.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener, View.OnLongClickListener {
        ImageView img_more;
        TextView t1;
        TextView t2;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.img_more = itemView.findViewById(R.id.image);
            this.t1 = itemView.findViewById(R.id.textView1);
            this.t2 = itemView.findViewById(R.id.textView2);
            t1.setOnClickListener(this);
            t2.setOnClickListener(this);
            img_more.setOnClickListener(this);
            t2.setOnLongClickListener(this);
            t1.setOnLongClickListener(this);
            img_more.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            MainActivity.play(getAdapterPosition());
            MainActivity.playlist_play=0;
        }

        @Override
        public boolean onLongClick(View view) {
          /*  Log.i("values", "SONG ITEM LONG CLICKED");
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
               */
            int k;
            if(MainActivity.temp4.size()>0)
                k=MainActivity.temp4.get(getAdapterPosition());
            else
                k=getAdapterPosition();
            Log.d("playlist", k+" ");
            String name=MainActivity.songs.get(k);
            if(!MainActivity.playlist_songs.contains(name)) {
                MainActivity.playlist_songs.add(MainActivity.songs.get(k));
                MainActivity.playlist_song_artist.add(MainActivity.song_artist.get(k));
                MainActivity.playlist_song_name.add(MainActivity.song_name.get(k));
                Toast.makeText(MainActivity.main, "Added to Playlist", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(MainActivity.main, "Already in Playlist", Toast.LENGTH_SHORT).show();

            return true;
        }
    }
}
