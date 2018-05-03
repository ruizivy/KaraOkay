package com.example.ruiz.karaokay;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static com.example.ruiz.karaokay.CustomListAdapter.isClick;
import static com.example.ruiz.karaokay.MainActivity.mArtist;
import static com.example.ruiz.karaokay.MainActivity.mFullpath;
import static com.example.ruiz.karaokay.MainActivity.mTitle;
import static com.example.ruiz.karaokay.MainActivity.mlyrics;

public class NowPlaying extends AppCompatActivity {

    private static String fileName;
    TextView lyric , lblStart , lblEnd, lbltitle , lblartist , lblstatus;
    String maxDuration , minDuration;
    ToggleButton btnplay;
    SeekBar skbar;
    public MediaPlayer mediaplayer;
    public static android.os.Handler seekhandler = new android.os.Handler();
    public static Uri soundUri;
    private MediaRecorder recorder;
    private AudioManager audioManager;
    File myDir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        mediaplayer = new MediaPlayer();
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(afChangeListener , AudioManager.STREAM_MUSIC , AudioManager.AUDIOFOCUS_GAIN);

        lblStart = (TextView)findViewById(R.id.txtStart);
        lblEnd = (TextView)findViewById(R.id.txtEnd);
        lyric = (TextView)findViewById(R.id.txtLyrics);
        lblartist = (TextView)findViewById(R.id.txtArtist);
        lbltitle = (TextView)findViewById(R.id.txtSongTitle);
        lbltitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        lbltitle.setSelected(true);
        lbltitle.setSingleLine(true);

        lblstatus = (TextView)findViewById(R.id.txtstatus);
        lblstatus.setVisibility(View.INVISIBLE);


        skbar = (SeekBar)findViewById(R.id.skduration);
        skbar.setOnTouchListener(new OnTouchListener());
        btnplay = (ToggleButton)findViewById(R.id.btnplaypuase);
        btnplay.setEnabled(true);

        soundUri = Uri.parse("android.resource://com.example.ruiz.karaokay/" + mFullpath );

        initPlayPause();
        Play();
        loadDataFromAsset(mlyrics);
        lbltitle.setText(mTitle);
        lblartist.setText("Artist :" + mArtist);
        if(isClick == true){
            startRecording();
            lblstatus.setText("STATUS : RECORDING STARTED");
            lblstatus.setVisibility(View.VISIBLE);
            btnplay.setEnabled(false);
        }
    }
    public void Play(){
        if(mediaplayer != null && mediaplayer.isPlaying()){
            mediaplayer.stop();
        }
        mediaplayer = new MediaPlayer();
        try
        {
            mediaplayer = MediaPlayer.create(this, getResources().getIdentifier(mFullpath , "raw" , getPackageName()));
            skbar.setMax(mediaplayer.getDuration());
            mediaplayer.start();
            btnplay.setChecked(true);
            maxDuration = (getTimeString(mediaplayer.getDuration()));
            lblEnd.setText(getTimeString(mediaplayer.getDuration()));
            seekUpdate();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void loadDataFromAsset(String txtfile) {
        try {
            InputStream is = getAssets().open(txtfile);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            lyric.setText(new String(buffer));
        }
        catch (IOException ex) {
            return;
        }
    }
    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int i) {
            if(i == AudioManager.AUDIOFOCUS_LOSS){
                audioManager.abandonAudioFocus(afChangeListener);
                if(mediaplayer.isPlaying()){
                  //  stopPlaying();
                }
            }
        }
    };

    private void startRecording(){
        fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)+ "/MyRecordings/" + mFullpath + ".3gp";
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        recorder.setOutputFile(fileName);
        try{
            recorder.prepare();
        }
        catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Could not start media Recorder", Toast.LENGTH_SHORT).show();
        }
        recorder.start();
    }

    private  void stopRecording(){
        if(null != recorder){
            recorder.stop();
            recorder.release();
            recorder = null;

        }
    }
    private String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();

        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        buf
                .append(String.format("%02d", hours))
                .append(":")
                .append(String.format("%02d", minutes))
                .append(":")

                .append(String.format("%02d", seconds));

        return buf.toString();
    }
    private void seekUpdate(){
        skbar.setProgress(mediaplayer.getCurrentPosition());
        lblStart.setText(getTimeString(mediaplayer.getCurrentPosition()));
        minDuration = getTimeString(mediaplayer.getCurrentPosition());
        seekhandler.postDelayed(run , 1000);
    }
    Runnable run = new Runnable() {
        @Override
        public void run() {
            if(recorder != null){
                if(minDuration == maxDuration){
                    stopRecording();
                    Toast.makeText(getApplicationContext() , "Recorder Stop" , Toast.LENGTH_SHORT).show();
                }
            }
            seekUpdate();
        }
    };
    public  class OnTouchListener implements View.OnTouchListener{
        @Override
        public boolean onTouch(View view, MotionEvent Event) {
            seekChange(view);
            return false;
        }
    }
    private void seekChange(View v){

        if(mediaplayer != null && mediaplayer.isPlaying()){
            SeekBar sb =  (SeekBar)v;
            mediaplayer.seekTo(sb.getProgress());
        }
    }
    private void initPlayPause(){
        btnplay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(mediaplayer != null){
                    if(isChecked) {
                        mediaplayer.start();
                        btnplay.setBackgroundResource(R.drawable.play);
                    }
                    else {
                        mediaplayer.pause();
                        btnplay.setBackgroundResource(R.drawable.pause);
                    }
                }
            }
        });
    }
    private void stopPlaying(){

        if(null != mediaplayer){
            if(mediaplayer.isPlaying()){
                mediaplayer.stop();
            }
            mediaplayer.release();
            mediaplayer = null;
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopRecording();
        if(null != mediaplayer) {
            if (mediaplayer.isPlaying()) {
                mediaplayer.stop();
            }
        }
        if(null != recorder){
            recorder.stop();
            Toast.makeText(NowPlaying.this, "Recording Save!" ,Toast.LENGTH_SHORT).show();
        }
        isClick = false;
    }
}
