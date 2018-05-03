package com.example.ruiz.karaokay;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ruiz on 10/21/2017.
 */

public class CustomListRecoder extends BaseAdapter {
    Recordings record;
    private Activity activity;
    private LayoutInflater layoutInflater;
    List<Recordings> lstRecord;
    ImageView btnrecord;
    Recordings md;
    public  static  boolean isClick;
    public CustomListRecoder(Activity a , List<Recordings> m){
        this.activity = a;
        this.lstRecord = m;
    }
    @Override
    public int getCount() {
        return lstRecord.size();
    }

    @Override
    public Object getItem(int i) {
        return lstRecord.get(i);
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

        if (view == null) view = layoutInflater.inflate(R.layout.lst_record, null);

        md = lstRecord.get(pos);
        final Recordings m = lstRecord.get(pos);


        TextView title = view.findViewById(R.id.lblsongrecord);

        title.setText(md.getFilepath());
        return view;
    }

}
