package com.example.ruiz.karaokay;

/**
 * Created by Ruiz on 10/2/2017.
 */

public class MusicDB {
    private int id;
    private String title , artist, fullpath, lyrics;


    public MusicDB(int musicid , String musictitle , String musicfullpath , String musicartist, String musiclyrics){
        this.id =musicid;
        this.title = musictitle;
        this.fullpath =musicfullpath;
        this.artist = musicartist;
        this.lyrics =musiclyrics;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }


    public void setFullpath(String fullpath) {
        this.fullpath = fullpath;
    }

    public String getFullpath() {
        return fullpath;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getLyrics() {
        return lyrics;
    }
}
