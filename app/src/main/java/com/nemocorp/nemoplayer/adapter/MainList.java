package com.nemocorp.nemoplayer.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.nemocorp.nemoplayer.MainActivity;
import com.nemocorp.nemoplayer.R;
//Main Song list Adapter
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
                Glide.with(MainActivity.main).load(MainActivity.songInfo.get(p).get_Artwork()).into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        img_more.setBackground(resource);
                    }
                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        img_more.setBackground(MainActivity.main.getResources().getDrawable(R.drawable.ic_music_note_black_24dp));
                        super.onLoadFailed(errorDrawable);
                    }
                });
                t1.setText(MainActivity.songInfo.get(p).getSong_Name());
                t2.setText(MainActivity.songInfo.get(p).getSong_artist());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
          //  Glide.with(MainActivity.main).load(MainActivity.songInfo.get(position).get_Artwork()).into(img_more);
            Glide.with(MainActivity.main).load(MainActivity.songInfo.get(position).get_Artwork()).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    img_more.setBackground(resource);
                }
                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    img_more.setBackground(MainActivity.main.getResources().getDrawable(R.drawable.ic_music_note_black_24dp));
                    super.onLoadFailed(errorDrawable);
                }
            });
            t1.setText(MainActivity.songInfo.get(position).getSong_Name());
            t2.setText(MainActivity.songInfo.get(position).getSong_artist());
        }
    }

    @Override
    public int getItemCount()
    {
        if(MainActivity.temp4.size()>0)
            return MainActivity.temp4.size();
        return MainActivity.songInfo.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        ImageView img_more;
        TextView t1;
        TextView t2;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.img_more = itemView.findViewById(R.id.image);
            this.t1 = itemView.findViewById(R.id.textView1);
            this.t2 = itemView.findViewById(R.id.textView2);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            MainActivity.play(getAdapterPosition());
            MainActivity.playlist_play=0;
            MainActivity.remove_streaming_icon();
        }
    }
}
