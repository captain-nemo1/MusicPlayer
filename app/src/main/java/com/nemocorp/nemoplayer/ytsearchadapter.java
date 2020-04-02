package com.nemocorp.nemoplayer;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class ytsearchadapter extends ArrayAdapter {
    private List<String> title;
    private Activity context;
    private List<String> dur;
    private List<String> channel;


    public ytsearchadapter(Activity context, List<String> title,List<String> dur,List<String> channel) {
        super(context, R.layout.row, title);
        this.context = context;
        this.title = title;
        this.dur=dur;
        this.channel=channel;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView == null)
            row = inflater.inflate(R.layout.row3, null, true);
        TextView textView1 = row.findViewById(R.id.textView3);
        TextView textView2 = row.findViewById(R.id.textView4);
        TextView textView3 = row.findViewById(R.id.textView5);
        textView1.setText(title.get(position));
        textView2.setText(channel.get(position));
        textView3.setText(dur.get(position));
        Button b1=row.findViewById(R.id.button2);
        Button b2=row.findViewById(R.id.button5);
        b1.setTag(position);
        b2.setTag(position);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos=(Integer)view.getTag();
                Ytsearch.download_links(pos);
                Ytsearch.download(getContext());
                Log.d("values","lllll"+pos);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos=(Integer)view.getTag();
                Ytsearch.download_links(pos);
                Ytsearch.stream(getContext());
                Log.d("values","lllll"+pos);
            }
        });


        return row;

    }
}
