package com.example.kotak.shaban_new_1;

import java.io.Serializable;

/**
 * Created by Daren Liu on 11/11/2016.
 */

public class Video implements Serializable {

    private Lecture lecture;
    private String title;
    private String url;
    private String id;

    public Video(Lecture lecture, String title, String url, String id){
        this.lecture = lecture;
        this.title = title;
        this.url = url;
        this.id = id;
    }

    public Lecture getLecture() {
        return lecture;
    }

    public void setLecture(Lecture lecture) {
        this.lecture = lecture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public String getShortenedUrl() {
        String[] temp = url.split("/");
        return temp[temp.length - 1];
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
