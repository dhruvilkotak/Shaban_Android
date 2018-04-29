package com.example.kotak.shaban_new_1;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class Lecture implements Serializable {

    private String lectureDescription;
    private String lectureId;
    private String serialNumber;
    private Course course;
    private ArrayList<Video> videos;

    private ArrayList<String> transcript_url = new ArrayList<String>();

    public Lecture(String lectureDescription, String lectureId){
        this.lectureDescription = lectureDescription;
        this.lectureId = lectureId;
    }

    public Lecture(String lectureDescription, String lectureId, String serialNumber, String transcript_url){
        this.lectureDescription = lectureDescription;
        this.lectureId = lectureId;
        this.serialNumber = serialNumber;
        this.transcript_url.add(transcript_url);
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public ArrayList<Video> getVideo(){ return videos; }

    public ArrayList<String> getVideoTitles(){
        ArrayList<String> titles = new ArrayList<String>();

        for(Video vid: videos){
            titles.add(vid.getTitle());
            Log.d("vidTitles:",vid.getTitle());
        }

        return titles;
    }

    public ArrayList<String> getTranscriptUrl() {
        return transcript_url;
    }

    public ArrayList<String> getShortenedUrl(){
        ArrayList<String> arr = new ArrayList<String>();
        for(String url : transcript_url){
            Log.d("lecturl",url);
            arr.add(url.split("/")[2]);
            Log.d("shorturl:",url.split("/")[2]);
        }
        return arr;
    }

    public void setVideo(ArrayList<Video> videos){ this.videos = videos; }

    public void addVideo(Video video){ this.videos.add(video); }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getLectureId() {
        return lectureId;
    }

    public String getLectureDescription() {
        return lectureDescription;
    }
}
