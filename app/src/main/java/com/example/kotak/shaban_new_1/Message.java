package com.example.kotak.shaban_new_1;

import java.io.Serializable;

/**
 * Created by Daren Liu on 7/24/2016.
 */
public class Message implements Serializable {

    private String username;
    private Course course;
    private Lecture lecture;
    private String content;
    private int id;
    private int courseId;
    private String createdAt;
    private String updatedAt;
    private Author author;
    private Group group;
    private String messageStatus="";

    public String getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
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

    public Message(String username, String content){
        this.username = username;
        this.content = content;
    }

    public Message(String username, String content, int id){
        this.username = username;
        this.id = id;
        this.content = content;
    }

    public Message(String username, Lecture lecture, String content, int id, int courseId){
        this.username = username;
        this.lecture = lecture;
        this.content = content;
        this.id = id;
        this.courseId = courseId;
    }

    public String getUsername(){return username; }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setLectureId(int courseId) {
        this.courseId = courseId;
    }

    public Lecture getLecture() {
        return lecture;
    }

    public void setLecture(Lecture lecture) {
        this.lecture = lecture;
    }
}
