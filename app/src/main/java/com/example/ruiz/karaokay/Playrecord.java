package com.example.ruiz.karaokay;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import static com.example.ruiz.karaokay.MainActivity.filepath;

public class Playrecord extends Activity {

    TextView txtrecordsong;
    Button btn_close;
    public static MediaPlayer mplayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playrecord);
        txtrecordsong = (TextView)findViewById(R.id.lblsongrecord);
        txtrecordsong.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txtrecordsong.setSelected(true);
        txtrecordsong.setSingleLine(true);
        txtrecordsong.setText(filepath);
        btn_close = (Button)findViewById(R.id.btnclose);
        btn_close.setOnClickListener(new ButtonEvent());
        startPlaying(filepath);
    }
    private void startPlaying(String filename){

        if(mplayer != null && mplayer.isPlaying()){
            mplayer.stop();
            mplayer.release();
        }
        mplayer = new MediaPlayer();
        try{
            mplayer.setDataSource(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)+ "/MyRecordings/" + filename);
            mplayer.prepare();
            mplayer.start();

        }catch (IOException e){
            Toast.makeText(getApplicationContext() , "Could not start media Player" , Toast.LENGTH_SHORT).show();
        }
    }

    private class ButtonEvent implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(null != mplayer){
                if(mplayer.isPlaying()){
                    mplayer.stop();
                    Intent intent = new Intent();
                    setResult(1 , intent);
                    finish();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(null != mplayer){
            if(mplayer.isPlaying()){
                mplayer.stop();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mplayer.stop();
        }
}
