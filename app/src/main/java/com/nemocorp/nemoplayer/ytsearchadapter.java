package com.nemocorp.nemoplayer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ytsearchadapter extends ArrayAdapter {
    private List<String> title;
    private Activity context;
    private List<String> id;


    public ytsearchadapter(Activity context, List<String> title,List<String> id) {
        super(context, R.layout.row, title);
        this.context = context;
        this.title = title;
        this.id=id;
          }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView == null)
            row = inflater.inflate(R.layout.row3, null, true);
        TextView textView1 = row.findViewById(R.id.textView3);
        TextView textView2 = row.findViewById(R.id.textView4);
        textView1.setText(title.get(position));
        textView2.setText(id.get(position));
        return row;
    }
}
