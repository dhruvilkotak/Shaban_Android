package com.example.kotak.shaban_new_1;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Daren Liu on 7/23/2016.
 */
public class Course implements Serializable {

    private String courseName;
    private String courseId;
    private ArrayList<Lecture> lectures;
    private Course parent; // might need to change Course object to make linked list
    private String desc;
    private String createdAt;
    private String updatedAt;
    private String transcriptURL;

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public Course getParent() {
        return parent;
    }

    public void setParent(Course parent) {
        this.parent = parent;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTranscriptURL() {
        return transcriptURL;
    }

    public void setTranscriptURL(String transcriptURL) {
        this.transcriptURL = transcriptURL;
    }

    public Course(){

    }

    public Course(String courseId){
        this.courseId = courseId;
    }

    public Course(String courseName, String courseId){
        this.courseName = courseName;
        this.courseId = courseId;
    }

    public ArrayList<Lecture> getLectures(){ return lectures; }

    public void setLectures(ArrayList<Lecture> lectures){ this.lectures = lectures; }

    public void addLecture(Lecture lecture){ this.lectures.add(lecture); }

    public String getCourseName(){
        return courseName;
    }

    public String getCourseId(){
        return courseId;
    }
}
