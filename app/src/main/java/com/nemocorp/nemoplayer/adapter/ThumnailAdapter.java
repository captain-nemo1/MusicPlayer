package com.nemocorp.nemoplayer.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nemocorp.nemoplayer.R;

import java.util.List;

public class ThumnailAdapter extends RecyclerView.Adapter<ThumnailAdapter.MyViewHolder>{
    List<Drawable>d;
    Context context;
    public ThumnailAdapter(List<Drawable>d, Context context){
        this.d=d;
        this.context=context;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        View view = li.inflate(R.layout.row2,parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ImageView img_more=holder.img_more;
    }

    @Override
    public int getItemCount() {
        Log.d("valuesinadapter",d.size()+"  ");
        return d.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img_more;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.img_more=itemView.findViewById(R.id.imageView456);
        }
    }
}
