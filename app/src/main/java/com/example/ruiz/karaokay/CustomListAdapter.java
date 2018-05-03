package com.example.ruiz.karaokay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.R.attr.id;
import static com.example.ruiz.karaokay.MainActivity.mArtist;
import static com.example.ruiz.karaokay.MainActivity.mFullpath;
import static com.example.ruiz.karaokay.MainActivity.mTitle;
import static com.example.ruiz.karaokay.MainActivity.mlyrics;

/**
 * Created by Ruiz on 10/2/2017.
 */

public class CustomListAdapter extends BaseAdapter {

    MusicDB musicDB;
    private Activity activity;
    private LayoutInflater layoutInflater;
    List<MusicDB> lstMusicDB;
    ImageView btnrecord;
    MusicDB md;
    public  static  boolean isClick;
    public CustomListAdapter(Activity a , List<MusicDB> m){
        this.activity = a;
        this.lstMusicDB = m;
    }

    @Override
    public int getCount() {
        return lstMusicDB.size();
    }

    @Override
    public Object getItem(int i) {
        return lstMusicDB.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int pos, View view, ViewGroup viewGroup) {
        if(layoutInflater == null){
            layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (view == null) view = layoutInflater.inflate(R.layout.list_item, null);

         md =lstMusicDB.get(pos);
        final MusicDB m = lstMusicDB.get(pos);


        TextView title = view.findViewById(R.id.lblsongname);
        TextView artist = view.findViewById(R.id.lblartistname);
        btnrecord = (ImageView)view.findViewById(R.id.btnrecord);
        btnrecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isClick = true;
                mFullpath = m.getFullpath();
                mlyrics = m.getLyrics();
                mTitle = m.getTitle();
                mArtist = m.getArtist();
                Intent intent = new Intent(activity.getApplicationContext(), NowPlaying.class);
                intent.putExtra("get_id", id);
                intent.putExtra("fullpath" , mFullpath);
                activity.startActivityForResult(intent , 1);
                Toast.makeText(activity.getApplicationContext() , "Recording Started!!!!", Toast.LENGTH_SHORT).show();
            }
        });
        title.setText(md.getTitle());
        artist.setText(md.getArtist());

        return view;
    }

}

