package com.team2.android.proctor.model.input;

/**
 * Created by kofikyei on 4/9/16.
 */
public class Attendance {
    private  long userId ;
    private int  courseId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}
