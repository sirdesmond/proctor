package com.team2.android.proctor.model.output;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by Arvind on 4/5/2016.
 */
public class Course implements Serializable{

    private String      courseName;
    private long   startDuration;
    private long   endDuration;
    private Time        courseStartTime;
    private Time        courseEndTime;
    private String      days;
    private int         course_id;


    public Course(String courseName, long startDuration, long endDuration, Time courseStartTime, Time courseEndTime, String days,int course_id) {
        this.courseName = courseName;
        this.startDuration = startDuration;
        this.endDuration = endDuration;
        this.courseStartTime = courseStartTime;
        this.courseEndTime = courseEndTime;
        this.days = days;
        this.course_id = course_id;
    }

    public  Course(int id,String courseName, Time startDuration, Time endDuration,String days){
        this.course_id = id;
        this.courseName = courseName;
        this.courseStartTime = startDuration;
        this.courseEndTime = endDuration;
        this.days = days;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public long getStartDuration() {
        return startDuration;
    }

    public void setStartDuration(long startDuration) {
        this.startDuration = startDuration;
    }

    public long getEndDuration() {
        return endDuration;
    }

    public void setEndDuration(long endDuration) {
        this.endDuration = endDuration;
    }

    public Time getCourseStartTime() {
        return courseStartTime;
    }

    public void setCourseStartTime(Time courseStartTime) {
        this.courseStartTime = courseStartTime;
    }

    public Time getCourseEndTime() {
        return courseEndTime;
    }

    public void setCourseEndTime(Time courseEndTime) {
        this.courseEndTime = courseEndTime;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }


    public int getCourse_id() {
        return course_id;
    }

    public void setCourse_id(int course_id) {
        this.course_id = course_id;
    }

    public String getformatedStartTime(){
        return String.valueOf(courseStartTime.getHours())+":"+String.valueOf(courseStartTime.getMinutes());
    }
    public String getformatedEndTime(){
        return String.valueOf(courseEndTime.getHours())+":"+String.valueOf(courseEndTime.getMinutes());
    }
}
