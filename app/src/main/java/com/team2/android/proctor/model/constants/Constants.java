package com.team2.android.proctor.model.constants;

/**
 * Created by ashekar on 3/26/2016.
 */
public class Constants {
    public static final int ENDPOINT_PORT = 8091 ;

    //http://ec2-54-172-114-213.compute-1.amazonaws.com
    public static final String BASE_URL = "http://ec2-54-172-114-213.compute-1.amazonaws.com:"+ENDPOINT_PORT+"/" ;


    // this is for the login page end point
    public static final String LOGIN_ENDPOINT_URL = BASE_URL+"api/student/login/" ;
    public static final String STUDENT_COURSES_URL = BASE_URL+"api/student/courses/" ;
    public static final String PROFESSOR_COURSES_URL = BASE_URL+"api/professor/courses/" ;
    public static final String TAKE_ATTENDANCE_URL = BASE_URL+"api/student/attendance/" ;
    public static final String VIEW_ATTENDANCE_URL = BASE_URL+"api/professor/students/" ;
    public static final String ATTENDANCE_COUNT_URL = BASE_URL+"api/student/attendancecount/" ;

}
