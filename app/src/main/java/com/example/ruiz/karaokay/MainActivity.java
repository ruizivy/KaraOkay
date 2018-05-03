package com.example.ruiz.karaokay;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.example.ruiz.karaokay.Playrecord.mplayer;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public CustomListAdapter adapter;
    ListView lstMusic;
    TextView txttitle , txtartistname , txtduration;
    ImageView btn_records;
    public static AudioManager audiomanager;
    public MusicDB music;
    public Recordings recordings;
    public CustomListRecoder recordadapter;
    public static  List<Recordings> songlist  = new ArrayList<Recordings>();
    public static  List<MusicDB> musicselected = new ArrayList<MusicDB>();
    public static MediaPlayer mediaplayer;
    public static  int mID;
    public static  String mFullpath , mlyrics , mTitle , mArtist , filepath;
    private AudioManager audioManager;
    boolean isRecording = false;
    String path;

    DialogInterface.OnClickListener dialoglistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mediaplayer = new MediaPlayer();
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(afChangeListener , AudioManager.STREAM_MUSIC , AudioManager.AUDIOFOCUS_GAIN);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        txttitle = (TextView)findViewById(R.id.lblsongname);
        txtartistname = (TextView)findViewById(R.id.lblartistname);
        adapter = new CustomListAdapter(this , musicselected);
        lstMusic = (ListView)findViewById(R.id.lstsong);
        lstMusic.setAdapter(adapter);
        lstMusic.setOnItemLongClickListener(new LongClickListener());
        lstMusic.setOnItemClickListener(new ListItemClick());
        btn_records = (ImageView)findViewById(R.id.btnrecord);

        MusicSong();
        readFile();

        dialoglistener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i)
                {
                    case DialogInterface.BUTTON_POSITIVE :
                        String filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)+ "/MyRecordings/" + path;
                        File delete = new File(filepath);
                        deleteAudioRecord(delete);
                        getRecordings();
                        Toast.makeText(MainActivity.this, "Recording deleted successfully", Toast.LENGTH_SHORT).show();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //Toast.makeText(MainActivity.this, "Item was not added", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

    }
    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int i) {
            if(i == AudioManager.AUDIOFOCUS_LOSS){
                audioManager.abandonAudioFocus(afChangeListener);
                if(mediaplayer.isPlaying()){
                    stopPlaying();
                }
            }
        }
    };
    private void stopPlaying(){
        if(null != mediaplayer){
            if(mediaplayer.isPlaying()){
                mediaplayer.stop();
            }
            mediaplayer.release();
            mediaplayer = null;
        }
    }
    private static String ConvertToProperCase(String name)
    {
        String retVal = "";
        List<String> tokens = RemoveExtraSpaces(name.split("_"));

        for(int i = 0; i < tokens.size(); i++) {
            String firstChar = String.valueOf(tokens.get(i).charAt(0)).toUpperCase();
            String theRestChar = tokens.get(i).substring(1).toLowerCase();
            retVal += firstChar + theRestChar + " ";
        }
        return retVal.substring(0,retVal.length() - 1);
    }
    private static List<String> RemoveExtraSpaces(String [] arr) {
        List<String> contents = new ArrayList<String>();

        for(String s : arr) {
            if (!s.equals(""))
                contents.add(s);
        }
        return contents;
    }
    public void MusicSong(){
        musicselected.clear();
        Field[] fields = R.raw.class.getFields();
        for(int count=0; count < fields.length; count++){

            int musicid  = count;
            String musictitle = ConvertToProperCase(fields[count].getName().toString());
            String[] title  = musictitle.split("By");
            String titlename = title[0];
            String titleartist = title[1];
            Bundle bundle = getIntent().getExtras();
            String choice = fields[count].getName();
//            String subpath = "R.raw."+ choic;
//            Uri myUri = Uri.parse("android.resource://com.example.ruiz.karaokay/" + subpath);
           // int path = Integer.valueOf(R.raw.most_girls_by_hailee_steinfeild);
//            mediaplayer = MediaPlayer.create(this, getResources().getIdentifier(choice , "raw" , getPackageName()));
//            mediaplayer.start();
            musicselected.add(new MusicDB(musicid ,titlename, choice , titleartist , choice + ".txt"));
            lstMusic.setAdapter(adapter);
        }
    }
    public void getRecordings(){

        songlist.clear();
        recordadapter = new CustomListRecoder(this , songlist);
        lstMusic.setAdapter(recordadapter);
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/MyRecordings");
        File list[] = directory.listFiles();

        for( int i= 0; i < list.length; i++)
        {
                songlist.add(new Recordings(list[i].getName()));
               // Toast.makeText(this, list[i].getName() , Toast.LENGTH_SHORT).show();
               lstMusic.setAdapter(recordadapter);
        }
    }

    private void readFile(){
        try {
        File myDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/MyRecordings");
        myDir.mkdirs();
        //File file = new File(myDir , fileName)
            FileInputStream fin = new FileInputStream((myDir));
            InputStreamReader inputStream = new InputStreamReader(fin);
            BufferedReader bufferedReader = new BufferedReader(inputStream);
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            fin.close();
            inputStream.close();
            Toast.makeText(this , "Data Retrieved : " +  stringBuilder.toString() , Toast.LENGTH_SHORT).show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
    public class ListItemClick implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            mID = musicselected.get(i).getId();
            if(isRecording == false) {
                mFullpath = musicselected.get(i).getFullpath();
                mlyrics = musicselected.get(i).getLyrics();
                mTitle = musicselected.get(i).getTitle();
                mArtist = musicselected.get(i).getArtist();

                int id = i;
                Intent intent = new Intent(getApplicationContext(), NowPlaying.class);
                intent.putExtra("get_id", id);
                intent.putExtra("fullpath", mFullpath);
                startActivityForResult(intent, 1);
            }else if(isRecording == true){
                filepath = songlist.get(i).getFilepath();
                Toast.makeText(MainActivity.this, "Recording Playing", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this , Playrecord.class);
                startActivityForResult(intent , 1);
            }
        }
    }
    private class LongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(isRecording == true){
                path = songlist.get(i).getFilepath();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Do you want to delete?").setPositiveButton("Yes", dialoglistener).setNegativeButton("No", dialoglistener).show();
            }
            return false;
        }
    }
    public static void deleteAudioRecord(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteAudioRecord(child);
        fileOrDirectory.delete();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        stopPlaying();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_recordings) {
            getRecordings();
            isRecording = true;
        } else if (id == R.id.nav_songs) {
            MusicSong();
            isRecording = false;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == 1){
                mplayer.stop();
            }
        }
    }


}
