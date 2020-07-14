package com.nemocorp.nemoplayer.adapter;

import android.content.ContentUris;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.nemocorp.nemoplayer.MainActivity;
import com.nemocorp.nemoplayer.R;
//Main Song list Adapter
public class MainList extends RecyclerView.Adapter<MainList.MyViewHolder> {

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        View view = li.inflate(R.layout.row2, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SimpleDraweeView  img_more = holder.img_more;
        TextView t1 = holder.t1;
        TextView t2 = holder.t2;
        if(MainActivity.temp4.size()>0){
            try {
                int p = MainActivity.temp4.get(position);
            //    set_Glide(p,img_more);
                Uri sArtworkUri = Uri
                        .parse("content://media/external/audio/albumart");
                Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, MainActivity.songInfo.get(p).get_Album_id());
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(albumArtUri)
                        .setResizeOptions(new ResizeOptions(50, 50))
                        .build();
                img_more.setController(
                        Fresco.newDraweeControllerBuilder()
                                .setOldController(img_more.getController())
                                .setImageRequest(request)
                                .build());
                t1.setText(MainActivity.songInfo.get(p).getSong_Name());
                t2.setText(MainActivity.songInfo.get(p).getSong_artist());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, MainActivity.songInfo.get(position).get_Album_id());
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(albumArtUri)
                    .setResizeOptions(new ResizeOptions(50, 50))
                    .build();
                img_more.setController(
                        Fresco.newDraweeControllerBuilder()
                                .setOldController(img_more.getController())
                                .setImageRequest(request)
                                .build());
          //  set_Glide(position,img_more);
            t1.setText(MainActivity.songInfo.get(position).getSong_Name());
            t2.setText(MainActivity.songInfo.get(position).getSong_artist());
        }
    }
/*  public void set_Glide(int pos, ImageView img_more)
    {
        Glide.with(MainActivity.main).load(MainActivity.songInfo.get(pos).get_Artwork()).into(new SimpleTarget<Drawable>() {
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
    }*/


    @Override
    public int getItemCount()
    {
        if(MainActivity.temp4.size()>0)
            return MainActivity.temp4.size();
        return MainActivity.songInfo.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        SimpleDraweeView img_more;
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
