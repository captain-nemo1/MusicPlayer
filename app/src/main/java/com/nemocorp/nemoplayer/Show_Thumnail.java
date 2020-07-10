package com.nemocorp.nemoplayer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Show_Thumnail extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show__thumnail);
        recyclerView=findViewById(R.id.my_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        /*if(musicpage.draw.size()>0) {
            ThumnailAdapter customAdapter= new ThumnailAdapter(musicpage.draw,Show_Thumnail.this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(customAdapter);
        }
        else
            finish();*/
    }
    @Override
    public void onDestroy()
    {
        musicpage.draw=null;
        super.onDestroy();
    }

}