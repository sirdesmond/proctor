package com.team2.android.proctor.ui;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.team2.android.proctor.R;
import com.team2.android.proctor.model.constants.Constants;
import com.team2.android.proctor.model.input.User;
import com.team2.android.proctor.model.output.Course;
import com.team2.android.proctor.util.AlarmReceiver;
import com.team2.android.proctor.util.CourseAdapter;
import com.team2.android.proctor.util.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class StudentFragment extends BackHandledFragment
        //implements CompoundButton.OnCheckedChangeListener
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String TAG = "prof";
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "PROCTOR_PREF_KEY";
    private static final String NOTIFY_STATUS = "NOTIFY_STATUS";


    public ArrayList<Course> courses = new ArrayList<Course>();
    ArrayList<PendingIntent> intentarray = new ArrayList<PendingIntent>();
    Proctor proctor;


    View fragmentView;
    Bundle bundle;
    OnCourseSelectedListener mCallback;
    User user;
    ListView courselist;
    CourseAdapter courseAdapter;
    ArrayList<Calendar> calTimes = new ArrayList<>();
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    int alarm_req = 0;

  /*  @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
       if (isChecked) {
            final int alarm_cnt = alarm_req;
            // switchStatus.setText("Switch is currently ON");
            try {
                editor.putBoolean(NOTIFY_STATUS, true);
                editor.commit();
                Log.d("Alarm", "starting alarm...");
                alarms(alarm_cnt);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            Log.d("Alarm","not checked");
            cancelAlarm();
            editor.putBoolean(NOTIFY_STATUS, false);
            editor.commit();
        }
    }*/

    public interface OnCourseSelectedListener {
        public void onCourseSelected(User user, Course course);
    }

    public StudentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfessorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StudentFragment newInstance(String param1, String param2) {
        StudentFragment fragment = new StudentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnCourseSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + "must implement OnCourseSelectedListener");
        }
    }

    @Override
    public String getTagText() {
        return TAG;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        proctor = (Proctor) getActivity().getApplicationContext();
        bundle = getArguments();

        try {
            alarms(alarm_req);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        LayoutInflater mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        fragmentView = mInflater.inflate(R.layout.fragment_student, null, false);
        courselist = (ListView) fragmentView.findViewById(R.id.courselist);

        user = (User) bundle.getSerializable("user");
        Log.d("Student", "user id: " + user.getUserId());
        long studentIDint = user.getUserId();
        String studentID = String.valueOf(studentIDint);
        GetStudentCourseData getStudentCourseData = new GetStudentCourseData(studentID);
        getStudentCourseData.execute((Void) null);

        JSONArray jsonCourses = null;
        try {
            jsonCourses = getStudentCourseData.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        int size;

        size = jsonCourses.length();


        String[] courseNames = new String[size];
        Time[] stimestamps = new Time[size];
        Time[] etimestamps = new Time[size];
        long[] startDurations = new long[size];
        long[] endDurations = new long[size];
        String[] days = new String[size];
        int[] courseIds = new int[size];

        try {
            for (int i = 0; i < size; i++) {
                JSONObject object = jsonCourses.getJSONObject(i);
                courseNames[i] = object.getString("courseName");
                stimestamps[i] = Time.valueOf(object.getString("courseStartTime"));
                etimestamps[i] = Time.valueOf(object.getString("courseEndTime"));
                startDurations[i] = Long.parseLong(object.getString("startDuration").toString());
                endDurations[i] =Long.parseLong(object.getString("endDuration").toString());
                days[i] = object.getString("days");
                courseIds[i] = object.getInt("course_id");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Course coursearray[] = new Course[size];
        for (int i = 0; i < size; i++) {
            coursearray[i] = new Course(courseNames[i],startDurations[i],endDurations[i],
                    stimestamps[i], etimestamps[i], days[i],courseIds[i]);
            courses.add(coursearray[i]);
        }


        for (int n = 0; n < size; n++) {
            alarm_req = alarm_req + days[n].length();
        }

  /*      Switch notifySwitch = (Switch) fragmentView.findViewById(R.id.notify_alarm);
        pref = getActivity().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        boolean notify_status = pref.getBoolean(NOTIFY_STATUS, false);

        //set the switch to ON
        if (notify_status)
            notifySwitch.setChecked(true);
        else
            notifySwitch.setChecked(false);

        Log.d("Alarm", "switch? " + notifySwitch.getId());
      //  notifySwitch.setOnCheckedChangeListener(this);
        notifySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
Log.d("","clicked on toggle");
                if (isChecked) {
                    // switchStatus.setText("Switch is currently ON");
                    try {
                        Log.d("","clicked on on");
                        editor.putBoolean(NOTIFY_STATUS, true);
                        editor.commit();
                        alarms(alarm_req);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.d("","clicked on off");
                    cancelAlarm();
                    editor.putBoolean(NOTIFY_STATUS, false);
                    editor.commit();
                }

            }
        });
*/


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_student, container, false);
        courselist = (ListView) fragmentView.findViewById(R.id.courselist);


        courseAdapter = new CourseAdapter(getActivity(), android.R.layout.simple_list_item_1, courses);
        courselist.setAdapter(courseAdapter);

        courselist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //start attendance activity with selected course details and user type
                Course course = courses.get(position);

                mCallback.onCourseSelected(user, course);
            }
        });



        Switch notifySwitch = (Switch) fragmentView.findViewById(R.id.notify_alarm);
        pref = getActivity().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        boolean notify_status = pref.getBoolean(NOTIFY_STATUS, false);

        //set the switch to ON
        if (notify_status)
            notifySwitch.setChecked(true);
        else
            notifySwitch.setChecked(false);

        Log.d("Alarm", "switch? " + notifySwitch.getId());
        //  notifySwitch.setOnCheckedChangeListener(this);
        notifySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                Log.d("", "clicked on toggle");
                if (isChecked) {
                    // switchStatus.setText("Switch is currently ON");
                    try {
                        Log.d("", "clicked on on");
                        //Toast.makeText(getActivity().getApplicationContext(), "Test  clicked on swtch!!!", Toast.LENGTH_LONG).show();

                        editor.putBoolean(NOTIFY_STATUS, true);
                        editor.commit();
                        alarms(alarm_req);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.d("", "clicked on off");
//                    Toast.makeText(getActivity().getApplicationContext(), "Test  clicked on swtch!!!", Toast.LENGTH_LONG).show();

                    cancelAlarm();
                    editor.putBoolean(NOTIFY_STATUS, false);
                    editor.commit();
                }

            }
        });


        return fragmentView;
    }


    private void alarms(int alarm_req) throws ParseException {

        int days_send[] = new int[alarm_req];
        String[] courseNames_send = new String[alarm_req];
        int alarm_count = 0;
        int size = courses.size();
// getting days of  courses and c
        for (int m = 0; m < size; m++) {
            String time = courses.get(m).getCourseStartTime().toString();
            try {
                for (int k = 0; k < courses.get(m).getDays().length(); k++) {
                    System.out.println(courses.get(m).getDays().charAt(k));
                    switch (courses.get(m).getDays().charAt(k)) {
                        case 'M':
                            System.out.println("is M equi int 2");
                            days_send[alarm_count] = 2;
                            courseNames_send[alarm_count] = courses.get(m).getCourseName();
                            alarm_count++;
                            break;
                        case 'T':
                            System.out.println("is T equi int 3");
                            days_send[alarm_count] = 3;
                            courseNames_send[alarm_count] = courses.get(m).getCourseName();
                            alarm_count++;
                            break;
                        case 'W':
                            System.out.println("is W equi int 4");
                            days_send[alarm_count] = 4;
                            courseNames_send[alarm_count] = courses.get(m).getCourseName();
                            alarm_count++;
                            break;
                        case 'R':
                            System.out.println("is R equi int 5");
                            days_send[alarm_count] = 5;
                            courseNames_send[alarm_count] = courses.get(m).getCourseName();
                            alarm_count++;
                            break;
                        case 'F':
                            System.out.println("is F equi int 6");
                            days_send[alarm_count] = 6;
                            courseNames_send[alarm_count] = courses.get(m).getCourseName();
                            alarm_count++;
                            break;
                        case 'S':
                            System.out.println("is S equi int 7");
                            days_send[alarm_count] = 7;
                            courseNames_send[alarm_count] = courses.get(m).getCourseName();
                            alarm_count++;
                            break;
                        default:
                            break;

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        //
        int index = 0;
        for (int r = 0; r < courses.size(); r++) {
            String time = courses.get(r).getCourseStartTime().toString();
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            try {
                Log.d("Student","in here:"+ courses.size());
                Calendar dBCourseTime = Calendar.getInstance();
                dBCourseTime.setTimeInMillis(System.currentTimeMillis());
                Date dt = formatter.parse(time);
                dBCourseTime.setTime(dt);
                int hour = dBCourseTime.get(Calendar.HOUR_OF_DAY);
                int minute = dBCourseTime.get(Calendar.MINUTE);
                int second = dBCourseTime.get(Calendar.SECOND);
                for (int s = 0; s < courses.get(r).getDays().length(); s++) {
                    Calendar alarmTime = Calendar.getInstance();
                    alarmTime.setTimeInMillis(System.currentTimeMillis());
                    alarmTime.set(Calendar.DAY_OF_WEEK, days_send[index]);
                    alarmTime.set(Calendar.HOUR_OF_DAY, hour);
                    alarmTime.set(Calendar.MINUTE, minute);
                    alarmTime.set(Calendar.SECOND, 0);
                    alarmTime.set(Calendar.MILLISECOND, 0);
                    if (alarmTime.getTimeInMillis() < System.currentTimeMillis()) {
                        alarmTime.add(Calendar.DATE, 7);
                    }
                    calTimes.add(alarmTime);
                    index++;
                }
            } catch (Exception ex) {

            }

        }

        PendingIntent pendingIntent;

        Intent alarmIntent = new Intent(getActivity(), AlarmReceiver.class);

        for (int i = 0; i < alarm_req; i++) {
            alarmIntent.putExtra("reqcode", (i * 1) + 37);
            alarmIntent.putExtra("course", courseNames_send[i]);
            if (System.currentTimeMillis() < calTimes.get(i).getTimeInMillis() - 5 * 60 * 1000) {
                pendingIntent = PendingIntent.getBroadcast(getActivity(), (i * 1) + 37, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, (calTimes.get(i).getTimeInMillis()) - 5 * 60 * 1000, pendingIntent);
                intentarray.add(pendingIntent);
            }
        }
    }


    void cancelAlarm() {
        Intent intentstop = new Intent(getActivity(), AlarmReceiver.class);
        int size = intentarray.size();
        for (int i = 0; i < size; i++) {
            Intent alarmIntent = new Intent(getActivity(), AlarmReceiver.class);
            AlarmManager alarmManagerstop = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
            alarmManagerstop.cancel(intentarray.get(i));
            intentarray.get(i).cancel();
        }

        AlarmReceiver alarmReceiver = new AlarmReceiver();
        alarmReceiver.stopAlarm(getActivity(), intentarray);
        intentarray.clear();
        calTimes.clear();
    }


    class GetStudentCourseData extends AsyncTask<Void, Void, JSONArray> {
        private final String id;

        GetStudentCourseData(String id) {
            this.id = id;
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            JSONArray returnJsonVal = null;

            List<NameValuePair> params1 = new ArrayList<NameValuePair>();

            params1.add(new BasicNameValuePair("id", id));

            returnJsonVal = JSONParser.makeHttpRequestArray(Constants.STUDENT_COURSES_URL, "POST", params1);

            return returnJsonVal;

        }

    }


    public void myclick(View v)
    {
        Log.d("","on myclick success");
    }
}