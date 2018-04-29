package com.example.kotak.shaban_new_1;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by dliu7 on 1/9/2017.
 */

/***********************************************************************************
 * Author: Muhammad Bilal
 * Date: 9/28/16
 * Availability: http://mobilesiri.com/json-parsing-in-android-using-android-studio/
 * Notes: Code for parsing json file asynchronously
 ***********************************************************************************/
public class JsonReader {

    //JSON Node variables for course
    private static final String TAG_COURSE_ID = "id";
    private static final String TAG_COURSE_NAME = "name";
    private static final String TAG_USER_NUMBER = "phone";
    private static final String TAG_USER_FIRST_NAME = "firstName";
    private static final String TAG_USER_LAST_NAME = "lastName";

    //JSON Node variables for lectures
    private static final String TAG_LECTURE = "lecture";
    private static final String TAG_LECTURE_ID = "id";
    private static final String TAG_LECTURE_NAME = "description";
    private static final String TAG_SERIAL_NUMBER = "serial_number";
    private static final String TAG_TRANSCRIPT_URL = "transcript_url";
    private static final String TAG_VIDEOS = "videos";
    private static final String TAG_TITLE = "title";
    private static final String TAG_URL = "url";
    private static final String TAG_COURSES = "course";
    private static final String TAG_CREATED_AT = "createdAt";
    private static final String TAG_UPDATED_AT = "updatedAt";
    private static final String TAG_PARENT = "parent";
    private static final String TAG_ROLE_ID = "roleId";


    public static ArrayList<Document> parseJsonTutorial(String jsonString)
    {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            ArrayList<Document> documentList= new ArrayList<Document>();
            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonobj=jsonArray.getJSONObject(i);
                String document=jsonobj.getString("document");
                //System.out.println(document);
                JSONArray documentsArray=new JSONArray(document);
                for(int j=0;j<documentsArray.length();j++)
                {
                    Document doc=new Document();
                    JSONObject docJson=documentsArray.getJSONObject(j);
                    doc.setCreatedAt(docJson.getString("createdAt"));
                    doc.setId(docJson.getString("id"));
                    doc.setLecture(docJson.getString("lecture"));
                    doc.setTitle(docJson.getString("title"));
                    doc.setUpdatedAt(docJson.getString("updatedAt"));
                    doc.setUrl(docJson.getString("url"));
                    documentList.add(doc);
                    Log.d("id",""+doc.getId());
                }
            }
            Log.d("size",""+documentList.size());
            return documentList;


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    public static ArrayList<Lecture> ParseJSONLecture(String json) {
        if (json != null) {
            try {

                ArrayList<Lecture> lectureList = new ArrayList<Lecture>();
                JSONArray jsonArray = new JSONArray(json);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject c = jsonArray.getJSONObject(i);

                    String lectureid = c.getString(TAG_LECTURE_ID);
                    Log.d("lectureId",lectureid);
                    String lecturename = c.getString(TAG_LECTURE_NAME);
                 //   String serialnumber = c.getString(TAG_SERIAL_NUMBER);
                    String transcript_url = c.getString(TAG_TRANSCRIPT_URL);
                    Lecture lecture = new Lecture(lecturename, lectureid, "1", transcript_url);
                   // JSONArray videosArray = c.getJSONArray(TAG_VIDEOS);
                    ArrayList<Video> videoList = new ArrayList<Video>();
                    ArrayList<Course> courseList = new ArrayList<Course>();
                  /*  for(int j = 0; j < videosArray.length(); j++){
                        JSONObject d = videosArray.getJSONObject(j);
                        String id = d.getString(TAG_LECTURE);
                        String lecture_id = d.getString(TAG_LECTURE_ID);
                        String title = d.getString(TAG_TITLE);
                        String url = "https://shaban.rit.albany.edu" + d.getString(TAG_URL);
                        videoList.add(new Video(lecture, title, url, id));
                    }
                    lecture.setVideo(videoList);
*/
                    JSONObject e = c.getJSONObject("course");
                    String course_name = e.getString(TAG_COURSE_NAME);
                    String course_id = e.getString(TAG_LECTURE_ID);
                    lecture.setCourse(new Course(course_name, course_id));
                    lectureList.add(lecture);
                }
                Log.d("lecturesixe",lectureList.size()+"");
                return lectureList;
            } catch (JSONException e) {
                Log.d("Exception",""+e);
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "No data received from HTTP request");
            return null;
        }
    }

    public static ArrayList<Course> ParseJSONCourse(String json,int flagParent) {

        if (json != null) {
            try {

                ArrayList<Course> courseList = new ArrayList<Course>();
                ArrayList<String> listOfId = new ArrayList<String>();
                JSONArray jsonArray = new JSONArray(json);

                // looping through All Students
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject courseObj = jsonArray.getJSONObject(i);

                    if (courseObj.has(TAG_PARENT)&& flagParent==0){
                        /*Log.d("parent:", courseObj.getString(TAG_PARENT));
                        if (courseObj.getString(TAG_PARENT) != null && courseObj.getString(TAG_PARENT).length() > 0) {
                            course.setParent(ParseJSONCourseParent(courseObj.getString(TAG_PARENT)));
                        }*/
                    }
                    else {
                        Course course = new Course();
                        if (courseObj.has(TAG_PARENT)&& flagParent==1){
                            //adding parent link as course object
                            Log.d("parent:", courseObj.getString(TAG_PARENT));
                            if (courseObj.getString(TAG_PARENT) != null && courseObj.getString(TAG_PARENT).length() > 0) {
                                course.setParent(ParseJSONCourseParent(courseObj.getString(TAG_PARENT)));
                        }
                        }
                        if (courseObj.has(TAG_COURSE_ID))
                            course.setCourseId(courseObj.getString(TAG_COURSE_ID));
                        if (courseObj.has(TAG_COURSE_NAME))
                            course.setCourseName(courseObj.getString(TAG_COURSE_NAME));
                        if (courseObj.has(TAG_CREATED_AT))
                            course.setCreatedAt(courseObj.getString(TAG_CREATED_AT));
                        if (courseObj.has(TAG_LECTURE_NAME))
                            course.setDesc(courseObj.getString(TAG_LECTURE_NAME));
                        //                    course.setLectures(courseObj.getString(TAG_LECTURE));



                        if (courseObj.has(TAG_TRANSCRIPT_URL))
                            course.setTranscriptURL(courseObj.getString(TAG_TRANSCRIPT_URL));
                        if (courseObj.has(TAG_UPDATED_AT))
                            course.setUpdatedAt(courseObj.getString(TAG_UPDATED_AT));
                        courseList.add(course);

                    }
                    /* String courseid = courseObj.getString(TAG_COURSE_ID);
                        String coursename = courseObj.getString(TAG_COURSE_NAME);

                        if(!listOfId.contains(courseid)) {
                            courseList.add(new Course(coursename, courseid));
                            listOfId.add(courseid);
                        }*/
                }
                return courseList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "No data received from HTTP request");
            return null;
        }
    }

    private static Course ParseJSONCourseParent(String string) {
        Course parentCourse=new Course();
        try {
            JSONObject courseObj = new JSONObject(string);
            if (courseObj.has(TAG_COURSE_ID))
                parentCourse.setCourseId(courseObj.getString(TAG_COURSE_ID));
            if (courseObj.has(TAG_COURSE_NAME))
                parentCourse.setCourseName(courseObj.getString(TAG_COURSE_NAME));
            if (courseObj.has(TAG_CREATED_AT))
                parentCourse.setCreatedAt(courseObj.getString(TAG_CREATED_AT));
            if (courseObj.has(TAG_LECTURE_NAME))
                parentCourse.setDesc(courseObj.getString(TAG_LECTURE_NAME));
            if (courseObj.has(TAG_TRANSCRIPT_URL))
                parentCourse.setTranscriptURL(courseObj.getString(TAG_TRANSCRIPT_URL));
            if (courseObj.has(TAG_UPDATED_AT))
                parentCourse.setUpdatedAt(courseObj.getString(TAG_UPDATED_AT));
            return parentCourse;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static User ParseJSONUser(String json) {
        if (json != null) {
            try {
                JSONObject userObject = new JSONObject(json);
                String userNumber = userObject.getString(TAG_USER_NUMBER);
                String firstName = userObject.getString(TAG_USER_FIRST_NAME);
                String lastName = userObject.getString(TAG_USER_LAST_NAME);
                return new User(firstName, lastName, userNumber);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        else {
            Log.e("ServiceHandler", "No data received from HTTP request");
            return null;
        }
    }

    public static ArrayList<Message> ParseJSONMessage(String json, String lectureId) {
        if (json != null) {
            try {

                JSONArray jsonArray = new JSONArray(json);
                ArrayList<Message> messageList = new ArrayList<Message>();

                // looping through All Messages
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject message = jsonArray.getJSONObject(i);
                    JSONObject author = message.getJSONObject("author");
                    if(!message.has("group")) {

                        continue;
                    }
                    JSONObject group =message.getJSONObject("group");
                    Group group1=new Group(group.getString(TAG_LECTURE_NAME),group.getString(TAG_TRANSCRIPT_URL) ,group.getString(TAG_COURSE_ID) ,group.getString(TAG_CREATED_AT)
                            ,group.getString(TAG_UPDATED_AT),group.getString(TAG_COURSES));
                    Author author1=new Author((String) author.getString(TAG_USER_FIRST_NAME), (String)author.getString(TAG_USER_LAST_NAME), (String)author.get(TAG_USER_NUMBER),(String) author.get(TAG_CREATED_AT),(String) author.getString(TAG_UPDATED_AT),(String) author.getString(TAG_ROLE_ID));

                    //JSONObject group = message.getJSONObject("group");
                    String name = author.getString(TAG_USER_FIRST_NAME) + " " + author.getString(TAG_USER_LAST_NAME);
                    String content = message.getString("content");
                    String updatedAt=message.getString(TAG_UPDATED_AT);
                    String createdAt=message.getString(TAG_CREATED_AT);
                    int msgId = Integer.parseInt(message.getString("id"));
                    int id = Integer.parseInt(group.getString("id"));
                    if(id == Integer.parseInt(lectureId)) {
                        Message message1=new Message(name, content, msgId);
                        message1.setCreatedAt(createdAt);
                        message1.setUpdatedAt(updatedAt);
                        message1.setAuthor(author1);
                       message1.setGroup(group1);
                        messageList.add(message1);
                    }
                }
                return messageList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "No data received from HTTP request");
            return null;
        }
    }



    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static String readJsonFromUrl(String url) throws IOException, JSONException {
        Log.d("url",url);
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
           // Log.d("jsonString:",jsonText);
            return jsonText;
        } finally {
            is.close();
        }
    }

    public static String parseJsonTranscript(String jsonStr) {

        if (jsonStr != null) {
            try {
                JSONArray jsonArray = new JSONArray(jsonStr);
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject transcriptObj = jsonArray.getJSONObject(i);
                    if (transcriptObj.has(TAG_TRANSCRIPT_URL))
                    {
                        return  transcriptObj.getString(TAG_TRANSCRIPT_URL);
                    }
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
}
