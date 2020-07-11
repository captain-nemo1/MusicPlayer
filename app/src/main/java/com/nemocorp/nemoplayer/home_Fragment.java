package com.nemocorp.nemoplayer;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class home_Fragment extends Fragment {
    Activity referenceActivity;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parentholder = inflater.inflate(R.layout.home_fragment, container, false);
        MainActivity.list=parentholder.findViewById(R.id.list_recycle);
        MainActivity.list.setLongClickable(true);
        referenceActivity = getActivity();
        if(MainActivity.songs.isEmpty()) {
            MainActivity.songInfo.clear();//to prevent 2-3 times list shown
            new List_Async(referenceActivity.getApplicationContext()).execute("1");
        }
        return parentholder;
    }
}
