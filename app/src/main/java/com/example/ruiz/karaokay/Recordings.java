package com.example.ruiz.karaokay;

/**
 * Created by Ruiz on 10/20/2017.
 */

public class Recordings {
    private String filepath;

    public Recordings(String f){
        this.filepath = f;
    }
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getFilepath() {
        return filepath;
    }
}
