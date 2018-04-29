package com.example.kotak.shaban_new_1;

/**
 * Created by kotak on 10/04/2018.
 */

public class Group {

    private String description;
    private String transcriptURL;
    private String id;
    private String createdAt;
    private String updatedAt;
    private String courseId;

    public Group(String description, String transcriptURL, String id, String createdAt, String updatedAt, String courseId) {

        this.description = description;
        this.transcriptURL = transcriptURL;
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.courseId = courseId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTranscriptURL() {
        return transcriptURL;
    }

    public void setTranscriptURL(String transcriptURL) {
        this.transcriptURL = transcriptURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
