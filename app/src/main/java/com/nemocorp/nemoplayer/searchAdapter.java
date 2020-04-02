package com.nemocorp.nemoplayer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class searchAdapter extends ArrayAdapter {

    private List<String> title;
    private List<String> artist;
    private List<String> id;
    private Activity context;


    public searchAdapter(Activity context, List<String> title, List<String> artist, List<String> id) {
        super(context, R.layout.row, title);
        this.context = context;
        this.title = title;
        this.artist = artist;
        this.id=id;    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView == null)
            row = inflater.inflate(R.layout.row, null, true);
        TextView textView1 = (TextView) row.findViewById(R.id.textView1);
        TextView textView2 = (TextView) row.findViewById(R.id.textView2);
        textView1.setText(title.get(position));
        textView2.setText(artist.get(position));
        return row;
    }
}
