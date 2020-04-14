package com.nemocorp.nemoplayer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static com.nemocorp.nemoplayer.MainActivity.bit;
import static com.nemocorp.nemoplayer.MainActivity.bitmapArray;
import static com.nemocorp.nemoplayer.MainActivity.temp4;


public class myAdapter extends ArrayAdapter {
    private List<String> title;
    private List<String> artist;
    private List<String> id;
    private Activity context;

    public myAdapter(Activity context, List<String> title, List<String> artist, List<String> id) {
        super(context, R.layout.row2, title);
        this.context = context;
        this.title = title;
        this.artist = artist;
        this.id=id;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if(temp4.size()>0)position= temp4.get(position);
        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView == null)
            row = inflater.inflate(R.layout.row2, null, true);
        TextView textView1 = (TextView) row.findViewById(R.id.textView1);
        TextView textView2 = (TextView) row.findViewById(R.id.textView2);
        ImageView imageview=row.findViewById(R.id.image);
        textView1.setText(title.get(position));
        textView2.setText(artist.get(position));
        if(bit.isAlive())
        {
        MediaMetadataRetriever m= new MediaMetadataRetriever();
        try {
            m.setDataSource(id.get(position));
            byte[] a = m.getEmbeddedPicture();
            Bitmap c = BitmapFactory.decodeByteArray(a, 0, a.length);
            imageview.setImageBitmap(c);
        } catch (Exception e) {
            imageview.setImageResource(R.drawable.ic_music_note_black_24dp);
        }}
        else
        {
            Bitmap c=bitmapArray.get(position);
            if(c!=null)
                imageview.setImageBitmap(c);
            else
                imageview.setImageResource(R.drawable.ic_music_note_black_24dp);
        }
        return row;
        }
}