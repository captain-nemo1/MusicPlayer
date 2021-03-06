package com.nemocorp.nemoplayer;

import android.app.Activity;
import android.content.ContentUris;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.List;

//This is for Playlist.
public class myAdapter extends ArrayAdapter {
    private List<String> title;
    private List<String> artist;
    private Activity context;

    public myAdapter(Activity context, List<String> title, List<String> artist) {
        super(context, R.layout.row2, title);
        this.context = context;
        this.title = title;
        this.artist = artist;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView == null)
            row = inflater.inflate(R.layout.row2, null, true);
        TextView textView1 = (TextView) row.findViewById(R.id.textView1);
        TextView textView2 = (TextView) row.findViewById(R.id.textView2);
        SimpleDraweeView img= row.findViewById(R.id.image);
        textView1.setText(title.get(position));
        textView2.setText(artist.get(position));
        int pos=MainActivity.song_name.indexOf(title.get(position)); //need to get the original position of the songs being added
       /* Glide.with(MainActivity.main).load(MainActivity.songInfo.get(pos).get_Artwork()).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                img.setBackground(resource);
            }
            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                img.setBackground(MainActivity.main.getResources().getDrawable(R.drawable.ic_music_note_black_24dp));
                super.onLoadFailed(errorDrawable);
            }
        }); */
        Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, MainActivity.songInfo.get(pos).get_Album_id());
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(albumArtUri)
                .setResizeOptions(new ResizeOptions(50, 50))
                .build();
        img.setController(
                Fresco.newDraweeControllerBuilder()
                        .setOldController(img.getController())
                        .setImageRequest(request)
                        .build());
       return row;
        }
}